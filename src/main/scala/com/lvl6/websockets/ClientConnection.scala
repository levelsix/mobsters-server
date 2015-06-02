package com.lvl6.websockets

import scala.collection.JavaConversions._
import scala.concurrent.Future
import org.joda.time.DateTime
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import com.lvl6.eventhandlers.GameEventHandler
import com.lvl6.server.eventsender.RoutingKeys._
import com.lvl6.server.eventsender.ToClientEvents
import com.lvl6.spring.AppContext
import com.lvl6.util.EventParser
import com.lvl6.util.ParsedEvent
import com.lvl6.events.NormalResponseEvent
import com.typesafe.scalalogging.slf4j.LazyLogging
import javax.websocket.CloseReason
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint
import com.google.protobuf.GeneratedMessage
import com.lvl6.events.PreDatabaseRequestEvent
import com.lvl6.server.eventsender.PreDBFacebookEvent
import com.lvl6.events.request.StartupRequestEvent
import java.util.UUID
import java.nio.ByteBuffer
import org.springframework.beans.factory.annotation.Autowired
import scala.beans.BeanProperty

@ServerEndpoint(value = "/client/connection")
class ClientConnection extends GameEventHandler with LazyLogging with MessageListener{
  
  //user info
  var userId:Option[String] = None 
  var udid:Option[String] = None
  var clanId:Option[String] = None
  var facebookId:Option[String] = None
  
  //socket info
  var session:Option[Session] = None
  var lastMessageReceived:DateTime = new DateTime()
  var connectionId:String = UUID.randomUUID.toString
  
  //rabbit listeners
  var globalChatListener:Option[ClientRabbitListener] = None
  var clanIdListener:Option[ClientRabbitListener] = None
  var userIdListener:Option[ClientRabbitListener] = None

  
  
  //beans
  @Autowired
  var rabbitConnectionFactory:ConnectionFactory = null
  @Autowired
  var amqpAdmin:AmqpAdmin = null
  @Autowired
  var gameExchange:DirectExchange = null
  @Autowired
  var chatExchange:TopicExchange = null
  
  
  
  @OnOpen
  def open(session:Session)={
    logger.info("Opening connection")
    lastMessageReceived = new DateTime()
    this.session = Some(session)
  }
  
  @OnClose
  def close(session:Session, reason:CloseReason)= {
    logger.info(s"Closing connection: $this")
    removeRabbitListeners
    ClientConnections.removeConnection(this)
  }
  
  @OnMessage
  def message(message:Array[Byte])={
    lastMessageReceived = new DateTime()
    logger.info(s"Received message on $this")
    processEvent(message)
  }
  
/*  
  @OnMessage
  def message(message:String)={
    lastMessageReceived = new DateTime()
    logger.info(s"Received message: $message")
    session.get.getBasicRemote.sendText(s"You sent: $message")
  }*/
  
  
  
  @OnError
  def error(t:Throwable)={
    logger.error(s"Error in ClientConnection", t)
  }
  
  
  override def sendResponses(uuid:String, playerId:String, responses:ToClientEvents)={
    if(userId.isEmpty) {
      if(!responses.userId.isEmpty) {
        userId = Some(responses.userId)
        setupRabbitListeners
      }else {
        logger.error(s"UserId is not set and responses didn't contain a userId. It should be in the startup request responses. $this")
      }
    }
    if(responses.clanChanged) {
      changeClan(responses.newClanId)
    }
    //Normal responses can be a response to the requesting player or to another player
    responses.normalResponseEvents.foreach{ revent =>
      val plyrId = revent.asInstanceOf[NormalResponseEvent[_ <: GeneratedMessage]].getPlayerId
      val bytes = EventParser.getResponseBytes(uuid, revent)
      //If it's the requester this should be their connection
      if(this.userId.get.equals(plyrId)) {
        sendToThisSocket(bytes)
      }else {
        ClientConnections.getConnection(plyrId) match{
          //If it's not the requester then the player might have a socket on this server
          case Some(lc)=> lc.sendToThisSocket(bytes)
     		  //If they are not on this server send to amqp
          case None=> eventWriter.sendToSinglePlayer(plyrId, bytes)
        }
      }
    }
    responses.preDBResponseEvents.foreach{ revent =>
      sendToThisSocket(EventParser.getResponseBytes(uuid, revent.event))
    }
    responses.preDBFacebookEvents.foreach{ revent =>
      sendToThisSocket(EventParser.getResponseBytes(uuid, revent.event))  
    }
    responses.clanResponseEvents.foreach{ revent =>
      eventWriter.sendToClan(revent.clanId, EventParser.getResponseBytes(uuid, revent.event))  
    }
    responses.globalChatResponseEvents.foreach{ revent =>
      eventWriter.sendGlobalChat(EventParser.getResponseBytes(uuid, revent))
    }
    responses.apnsResponseEvents.foreach{ revent =>
      apnsWriter.handleEvent(revent)
    }
  }
  
  override def updatePlayerToServerMaps(parsedEvent:ParsedEvent)={
    val event = parsedEvent.event
    if(event.isInstanceOf[StartupRequestEvent] ){
      //set facebookId and udid
      val sevent = event.asInstanceOf[StartupRequestEvent]
      udid = Some(sevent.getUdid)
      val fbid = sevent.getStartupRequestProto.getFbId
      if(fbid != null && !fbid.isEmpty()) {
        facebookId = Some(fbid)
      }
      setupRabbitListeners
    }
  }
  
  def sendToThisSocket(bytes:Array[Byte]) = {
    session match{
      case Some(sess)=>{
        sess.isOpen() match{
          case true =>  {
        	  val buff = ByteBuffer.allocate(bytes.length).put(bytes)
            sess.synchronized{sess.getBasicRemote.sendBinary(buff)}
          }
          case false => logger.warn(s"Cannot send message. Socket is closed. $this")
        }
      }
      case None=>{
        logger.warn(s"Cannot send message. There is no session: $this")
      }
    }
  }
  
  //rabbit listener
  def onMessage(msg:Message) = {
    sendToThisSocket(msg.getBody)
  }
  
  
  
  def changeClan(newClanId:String)={
    removeRabbitListener(clanIdListener)
    clanIdListener = None
    clanId = Some(newClanId)
    setupRabbitListeners
  }
  
  def setupRabbitListeners= {
    globalChatListener match{
      case Some(gcl) => //already setup
      case None => {
        globalChatListener = Some(setupRabbitListener(chatExchange, globalchatRoutingKey))
      }
    }
    userIdListener match{
      case Some(uidl) => //already setup
      case None => {
        userId match {
          case Some(uid) => userIdListener = Some(setupRabbitListener(gameExchange, toUserRoutingKey(uid)))
          case None =>
        }
      }
    }
    clanIdListener match{
      case Some(cidl)=> //already setup
      case None =>{
        clanId match{
          case Some(cid)=> clanIdListener = Some(setupRabbitListener(gameExchange, clanRoutingKey(cid)))
          case None=>
        }
      }
    }
  }
  
  def setupRabbitListener(exchange:Exchange, routingKey:String):ClientRabbitListener= {
    val queue = amqpAdmin.declareQueue();
    val binding:Binding = BindingBuilder.bind(queue).to(exchange).`with`(routingKey).noargs
    val container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(rabbitConnectionFactory);
    container.setQueues(queue)
    container.setMessageListener(this);
    amqpAdmin.declareQueue(queue)
    amqpAdmin.declareBinding(binding)
    ClientRabbitListener(routingKey, queue, binding, container)
  }
  
  def removeRabbitListeners= {
  	removeRabbitListener(globalChatListener)
    globalChatListener = None
    removeRabbitListener(userIdListener)
    userIdListener = None
    removeRabbitListener(clanIdListener)
    clanIdListener = None
  }
  
  def removeRabbitListener(listener:Option[ClientRabbitListener])={
    listener match{
      case Some(listener)=>{
    	  amqpAdmin.removeBinding(listener.binding)
    	  amqpAdmin.deleteQueue(listener.queue.getName)
      }
      case None =>
    }
  }
  
  def wireBeans= {
    val factory = AppContext.getApplicationContext.getAutowireCapableBeanFactory();
    factory.autowireBean(this);
    factory.initializeBean(this, this.connectionId );
    //rabbitConnectionFactory = AppContext.getApplicationContext.getBean(classOf[ConnectionFactory])
    //amqpAdmin = AppContext.getApplicationContext.getBean(classOf[AmqpAdmin])
  }
  
  
  override def toString:String= {
    return "ClientConnection[userId="+
    userId+
    " udid="+
    udid+
    " clanId="+
    clanId+
    " lastMessageReceived="+
    lastMessageReceived+
    " connectionId"+
    connectionId+
    "]"
  }
  
  
  
}