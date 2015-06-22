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
import javax.annotation.Resource
import com.lvl6.events.response.StartupResponseEvent
import com.lvl6.server.eventsender.PreDBResponseEvent

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
  var facebookIdListener:Option[ClientRabbitListener] = None

  
  
  //beans
  @Autowired
  var rabbitConnectionFactory:ConnectionFactory = null
  @Autowired
  var amqpAdmin:AmqpAdmin = null
  @Resource(name="gamemessages")
  var gameExchange:DirectExchange = null
  @Resource(name="chatmessages")
  var chatExchange:TopicExchange = null
  
  
  
  @OnOpen
  def open(session:Session)={
    logger.debug("Opening connection")
    lastMessageReceived = new DateTime()
    this.session = Some(session)
    ClientConnections.addConnection(this)
    wireBeans
  }
  
  @OnClose
  def close(session:Session, reason:CloseReason)= {
    logger.debug(s"Closing connection: $this reason: $reason")
    ClientConnections.removeConnection(this)
    removeRabbitListeners
  }
  
  @OnMessage
  def message(message:Array[Byte])={
    lastMessageReceived = new DateTime()
    logger.debug(s"Received message on $this")
    processEvent(message)
  }
  
  
  @OnError
  def error(t:Throwable)={
    logger.debug(s"Error in ClientConnection", t)
  }
  
  def newToClientEvents(eventUuid:String, playerId:Option[String]):ToClientEvents= {
    new ToClientEvents(connectionId, eventUuid, playerId)
  }
  
  override def sendResponses(responses:ToClientEvents)={
    logger.info(s"abcd");
    if(userId.isEmpty) {
      if(!responses.userId.isEmpty) {
        userId = Some(responses.userId)
        setupRabbitListeners
      }else {
        responses.playerId match{
          case Some(plyrId) =>{
            userId = responses.playerId
            setupRabbitListeners    
          }
          case None => logger.error(s"UserId is not set and responses didn't contain a userId. It should be in the startup request responses. $this")
        }
      }
    }
    if(responses.clanChanged) {
      changeClan(responses.newClanId)
    }
    //Normal responses can be a response to the requesting player or to another player
    responses.normalResponseEvents.foreach{ revent =>
      logger.info(s"Sending normal response: $revent")
      val plyrId = revent.asInstanceOf[NormalResponseEvent[_ <: GeneratedMessage]].getPlayerId
      val bytes = EventParser.getResponseBytes(responses.requestUuid, revent)
      //If it's the requester this should be their connection
      if(userId.get.equals(plyrId)) {
        sendToThisSocket(bytes)
      }else {
        ClientConnections.getConnection(plyrId) match{
          //If it's not the requester then the player might have a socket on this server
          case Some(lc)=> lc.sendToThisSocket(bytes)
     		  //If they are not on this server send to amqp
          case None=> {
            logger.info(s"No connection found on this server for playerId: plyrId  sending to amqp")
            eventWriter.sendToSinglePlayer(plyrId, bytes)
          }
        }
      }
    }
    responses.preDBResponseEvents.foreach{ revent =>
      logger.info(s"Sending preDbResponse event: $revent")
      sendToThisSocket(EventParser.getResponseBytes(responses.requestUuid, revent.event))
    }
    responses.preDBFacebookEvents.foreach{ revent =>
      logger.info(s"Sending preDbFacebookResponse event: $revent")
      val bytes = EventParser.getResponseBytes(responses.requestUuid, revent.event)
      eventWriter.sendToSinglePlayer(revent.event.asInstanceOf[NormalResponseEvent[_ <: GeneratedMessage]].getPlayerId(), bytes)  
    }
    responses.clanResponseEvents.foreach{ revent =>
      logger.info(s"Sending clanResponse event to amqp: $revent")
      eventWriter.sendToClan(revent.clanId, EventParser.getResponseBytes(responses.requestUuid, revent.event))  
    }
    responses.globalChatResponseEvents.foreach{ revent =>
      logger.info("Sending globalChatResponse event to amqp")
      eventWriter.sendGlobalChat(EventParser.getResponseBytes(responses.requestUuid, revent))
    }
    responses.apnsResponseEvents.foreach{ revent =>
      logger.debug("Sending apnsResponse event")
      apnsWriter.handleEvent(revent)
    }
    cacheResponses(responses)
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
  
  def sendPing= {
    session match{
      case Some(sess)=>{
        sess.isOpen() match{
          case true =>  {
            //logger.info(s"Sending message to this socket: $this")
            synchronized{
              val buff = ByteBuffer.allocate(8).putLong(System.currentTimeMillis())
              buff.flip
              sess.getBasicRemote.sendPing(buff)
            }
          }
          case false => logger.warn(s"Cannot send ping. Socket is closed. $this")
        }
      }
      case None=>{
        logger.warn(s"Cannot send ping. There is no session: $this")
      }
    }
  }
  
  def sendToThisSocket(bytes:Array[Byte]) = {
    session match{
      case Some(sess)=>{
        sess.isOpen() match{
          case true =>  {
            logger.debug(s"Sending message to this socket: $this")
        	  val buff = ByteBuffer.allocate(bytes.length).put(bytes)
            buff.flip()
            synchronized{
              sess.getBasicRemote.sendBinary(buff)
              //logger.debug(s"Message sent: $buff")
            }
          }
          case false => logger.warn(s"Cannot send message. Socket is closed. $this")
        }
      }
      case None=>{
        logger.warn(s"Cannot send message. There is no session: $this")
      }
    }
  }
  
  def sendToThisSocket(bytes:String) = {
    session match{
      case Some(sess)=>{
        sess.isOpen() match{
          case true =>  {
            logger.info(s"Sending message to this socket: $this")
            //val buff = ByteBuffer.allocate(bytes.length).put(bytes)
            sess.synchronized{
              sess.getBasicRemote.sendText(bytes)
              //logger.info(s"Message sent: $bytes")
            }
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
    logger.debug(s"Received message from amqp on socket: $this")
    try {
      sendToThisSocket(msg.getBody)
    }catch{
      case t:Throwable => logger.error("Error sending amqp message to socket", t)
    }
  }
  
  
  
  def changeClan(newClanId:String)={
    removeRabbitListener(clanIdListener)
    clanIdListener = None
    clanId = Some(newClanId)
    setupRabbitListeners
  }
  
  def setupRabbitListeners= {
    ClientConnections.addConnection(this)
    synchronized {
      globalChatListener match{
        case Some(gcl) => //already setup
        case None => {
          logger.debug(s"Setting up global chat listener for $this")
          globalChatListener = Some(setupRabbitListener(chatExchange, globalchatRoutingKey))
        }
      }
      userIdListener match{
        case Some(uidl) => //already setup
        case None => {
          userId match {
            case Some(uid) => {
              logger.debug(s"Setting up userId listener for $this")
              userIdListener = Some(setupRabbitListener(gameExchange, toUserRoutingKey(uid)))
            }
            case None =>
          }
        }
      }
      clanIdListener match{
        case Some(cidl)=> //already setup
        case None =>{
          clanId match{
            case Some(cid)=> {
              logger.debug(s"Setting up clanId listener for $this")
              clanIdListener = Some(setupRabbitListener(gameExchange, clanRoutingKey(cid)))
            }
            case None=>
          }
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
    container.start();
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
    	  listener.listener.stop()
        //listener.listener.removeQueues(listener.queue)
    	  amqpAdmin.removeBinding(listener.binding)
    	  amqpAdmin.deleteQueue(listener.queue.getName)
    	  listener.listener.destroy()
      }
      case None =>
    }
  }
  
  def wireBeans= {
    val factory = AppContext.get.getAutowireCapableBeanFactory();
    factory.autowireBean(this);
    factory.initializeBean(this, this.connectionId );
    //rabbitConnectionFactory = AppContext.get.getBean(classOf[ConnectionFactory])
    //amqpAdmin = AppContext.get.getBean(classOf[AmqpAdmin])
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