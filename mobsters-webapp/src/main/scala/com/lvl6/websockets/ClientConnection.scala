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
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import com.lvl6.events.ResponseEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import com.lvl6.server.events.ClanChangeServerEvent
import com.lvl6.server.eventsender.RoutingKeys

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
  @Resource(name="gamemessagesWS")
  var gameExchange:DirectExchange = null
  @Resource(name="chatmessagesWS")
  var chatExchange:TopicExchange = null
  @Resource(name = "serverMessagesTemplate")
  var serverMessagesTemplate:RabbitTemplate = null
  
  
  
  @OnOpen
  def open(session:Session)={
    logger.info("Opening connection")
    lastMessageReceived = new DateTime()
    this.session = Some(session)
    ClientConnections.addConnection(this)
    wireBeans
  }
  
  @OnClose
  def close(session:Session, reason:CloseReason)= {
    logger.info(s"Closing connection: $this reason: $reason")
    ClientConnections.removeConnection(this)
    removeRabbitListeners
  }
  
  @OnMessage
  def message(message:Array[Byte])={
    lastMessageReceived = new DateTime()
    logger.info(s"Received message on $this")
    processEvent(message)
  }
  
  
  @OnError
  def error(t:Throwable)={
    logger.info(s"Error in ClientConnection. Closing connection.", t)
    closeConnection
  }
  
  def closeConnection= {
    session match{
      case Some(sess)=> {
        try {
          sess.close() 
          session = None
        }catch{
          case t:Throwable => logger.error("Error closing connection", t)
        }
      }
      case None =>
    }
    ClientConnections.removeConnection(this)    
  }
  
  def newToClientEvents(eventUuid:String, playerId:Option[String]):ToClientEvents= {
    new ToClientEvents(connectionId, eventUuid, playerId)
  }
  
  override def sendResponses(responses:ToClientEvents)={
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
    if(!responses.changeClansMap.isEmpty) {
      sendClanChangeServerEvent(responses.changeClansMap)
    }
    //Normal responses can be a response to the requesting player or to another player
    responses.normalResponseEvents.foreach{ revent =>
      logger.info(s"Sending normal response: $revent")
      val plyrId = revent.asInstanceOf[NormalResponseEvent[_ <: GeneratedMessage]].getPlayerId
      sendToPlayer(plyrId, responses.requestUuid, revent)
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
      logger.info("Sending apnsResponse event")
      val player = playersOnlineService.getConnectedPlayer(revent.asInstanceOf[NormalResponseEvent[_ <: GeneratedMessage]].getPlayerId())
      player match{
        case Some(p)=> sendToPlayer(p.getPlayerId, responses.requestUuid, revent)
        case None => apnsWriter.handleEvent(revent)
      }
    }
    cacheResponses(responses)
  }
  
  protected def sendToPlayer(plyrId:String, requestUuid:String, revent:ResponseEvent[_ <: GeneratedMessage])={
    logger.info(s"Sending message to player: $plyrId requestUuid: $requestUuid message: $revent")
    val bytes = EventParser.getResponseBytes(requestUuid, revent)
    //If it's the requester this should be their connection
    if(userId.get.equals(plyrId)) {
      sendToThisSocket(bytes)
    }else {
      val sentLocal = onLocalConnection(plyrId){ lc:ClientConnection =>
        lc.sendToThisSocket(bytes)
      }
      if(!sentLocal) {
        logger.info(s"No connection found on this server for playerId: plyrId  sending to amqp")
        eventWriter.sendToSinglePlayer(plyrId, bytes)
      }
    }
  }
  
  
  def sendCachedResponse(cachedResponse:CachedClientResponse)={
    sendToThisSocket(cachedResponse.event)
  }
  
  
  def sendClanChangeServerEvent(changeClanMap:java.util.Map[String, String])={
    val ccm = changeClanMap.filter{ case(usrId, clnId) =>
      //If user is connected to this server then filter it so it doesn't get sent to other servers
      !onLocalConnection(usrId){ lc:ClientConnection =>
        lc.changeClan(clnId)  
      }
    }
    val ev = new ClanChangeServerEvent()
    ev.setUserIdToNewClanIdMap(ccm)
    serverMessagesTemplate.convertAndSend(RoutingKeys.clanChangeRoutingKey, ev)
  }
  
  protected def onLocalConnection(userId:String)(doOnLC:ClientConnection => Unit):Boolean={
    ClientConnections.getConnection(userId) match{
      case Some(lc)=> {
        doOnLC(lc)
        true
      }
      case None=> {
        false
      }
    }      
  }
  
  
  override def updatePlayerToServerMaps(parsedEvent:ParsedEvent)={
    playersOnlineService.updatePlayerToServerMaps(parsedEvent.event)
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
  
  
  override def handleMaintenanceMode(parsedEvent:ParsedEvent)={
    val re = parsedEvent.event
    val playerId = re.getPlayerId
    if(playerId != null && !playerId.isEmpty) {
      val user = userRetrieveUtils.getUserById(playerId)
      if(user != null && !user.isAdmin){
        sendToThisSocket(messagingUtil.getMaintanenceModeMessage(appMode.getMessageForUsers, playerId, parsedEvent.eventProto.getEventUuid))
      }
    }else {
      if(re.isInstanceOf[PreDatabaseRequestEvent] ){
        val udid = re.asInstanceOf[PreDatabaseRequestEvent].getUdid
        sendToThisSocket(messagingUtil.getMaintanenceModeMessageUdid(appMode.getMessageForUsers, udid, parsedEvent.eventProto.getEventUuid))
      }
    }
  } 
  
  def sendPing= {
    session match{
      case Some(sess)=>{
        sess.isOpen() match{
          case true =>  {
            //logger.info(s"Sending message to this socket: $this")
            synchronized{
              try {
              val buff = ByteBuffer.allocate(8).putLong(System.currentTimeMillis())
              buff.flip
              sess.getBasicRemote.sendPing(buff)
              }catch{
                case t:Throwable => {
                  //logger.error()
                  closeConnection
                }
              }
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
            logger.info(s"Sending message to this socket: $this")
        	  val buff = ByteBuffer.allocate(bytes.length).put(bytes)
            buff.flip()
            synchronized{
              try {
              sess.getBasicRemote.sendBinary(buff)
              }catch{
                case t:Throwable =>     {
                  logger.error(s"Error sending ${bytes.size} bytes. Closing connection", t)
                  closeConnection
                }            
              }
              //logger.info(s"Message sent: $buff")
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
    logger.info(s"Received message from amqp on socket: $this")
    try {
      sendToThisSocket(msg.getBody)
    }catch{
      case t:Throwable => logger.error("Error sending amqp message to socket", t)
    }
  }
  
  
  
  def changeClan(newClanId:String)={
    removeRabbitListener(clanIdListener)
    clanIdListener = None
    if(newClanId != null && !newClanId.isEmpty()) {
      clanId = Some(newClanId)
 		  setupRabbitListeners
    }else {
      clanId = None
    }
  }
  
  def setupRabbitListeners= {
    ClientConnections.addConnection(this)
    synchronized {
      globalChatListener match{
        case Some(gcl) => //already setup
        case None => {
          logger.info(s"Setting up global chat listener for $this")
          globalChatListener = Some(setupRabbitListener(chatExchange, globalchatRoutingKey))
        }
      }
      userIdListener match{
        case Some(uidl) => //already setup
        case None => {
          userId match {
            case Some(uid) => {
              logger.info(s"Setting up userId listener for $this")
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
              logger.info(s"Setting up clanId listener for $this")
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