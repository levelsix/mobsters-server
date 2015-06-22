package com.lvl6.eventhandlers

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.springframework.amqp.core.MessageListener
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest
import com.lvl6.events.RequestEvent
import org.springframework.amqp.core.Message
import com.lvl6.util.EventParser
import org.springframework.beans.factory.annotation.Autowired
import com.lvl6.server.ApplicationMode
import com.lvl6.util.ParsedEvent
import com.lvl6.retrieveutils.UserRetrieveUtils2
import com.lvl6.events.PreDatabaseRequestEvent
import com.lvl6.utils.MessagingUtil
import scala.concurrent.Future
import com.lvl6.server.concurrent.FutureThreadPool.ec
import com.lvl6.mobsters.services.PlayersOnlineService
import com.lvl6.mobsters.services.ClientResponseCacheService
import com.lvl6.server.eventsender.ToClientEvents
import com.lvl6.server.eventsender.EventWriter
import com.google.protobuf.GeneratedMessage
import com.lvl6.events.ResponseEvent
import scala.collection.JavaConversions._
import com.lvl6.events.BroadcastResponseEvent
import com.lvl6.server.APNSWriter
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import java.util.Date
import scala.beans.BeanProperty
import org.springframework.beans.factory.annotation.Value


trait GameEventHandler extends LazyLogging  {
  
  @Autowired var parser:EventParser = null
  @Autowired var appMode:ApplicationMode = null
  @Autowired var messagingUtil:MessagingUtil = null
  @Autowired var userRetrieveUtils:UserRetrieveUtils2 = null
  @Autowired var playersOnlineService:PlayersOnlineService = null
  @Autowired var responseCacheService:ClientResponseCacheService = null
  @Autowired var eventWriter:EventWriter = null
  @Autowired var apnsWriter:APNSWriter = null
  
  @Value("${dynamodb.response.caching.enabled}")
  @BeanProperty
  var responseCachingEnabled = false
  
  def processEvent(eventBytes:Array[Byte])={
    try{
      val parsedEvents = parser.parseEvents(eventBytes)
      parsedEvents.foreach{ parsedEvent => 
        if(appMode.isMaintenanceMode()){
          handleMaintenanceMode(parsedEvent)                
        }else{
          logger.debug(s"Processing event: $parsedEvent")
          updatePlayerToServerMaps(parsedEvent)
          val eventUuid = parsedEvent.eventProto.getEventUuid
          val plyrId = parsedEvent.event.getPlayerId
          var playerId:Option[String] = None
          if(plyrId != null && !plyrId.isEmpty) {
            playerId = Some(plyrId)
          }
          val toClientEvents = newToClientEvents(eventUuid, playerId)
          if(responseCachingEnabled) {
            if(responseCacheService.isResponseCached(eventUuid)) {
              logger.info(s"Event $eventUuid was already cached.. sending cached responses")
              val cachedResponses = responseCacheService.getCachedResponses(eventUuid)
              
              cachedResponses match {
                case Some(responses) => responses.foreach{ cr => eventWriter.sendToSinglePlayer(plyrId, cr.event)}
                case None => logger.info("Cached responses was empty")
              }
            }
          }else{
            parsedEvent.eventController.processEvent(parsedEvent.event, toClientEvents) match{
              case Some(events)=>{
                sendResponses(events)
                cacheResponses(events)
              }
              case None => //logger.error("No events returned from parseEvent")
            }
          }
        }
      }
    }catch{
      case t:Throwable => logger.error("Error processing message", t)
    }
  }
  
  
  def sendResponses(responses:ToClientEvents)={
    responses.normalResponseEvents.foreach{ revent =>
      responses.playerId match{
        case Some(plyrId)=> eventWriter.sendToSinglePlayer(plyrId, EventParser.getResponseBytes(responses.requestUuid, revent))
        case None => logger.error("Error sending normal responses. No playerId to send to.")
      }
    }
    responses.preDBResponseEvents.foreach{ revent =>
      eventWriter.sendPreDBResponseEvent(revent.udid, EventParser.getResponseBytes(responses.requestUuid, revent.event))
    }
    responses.preDBFacebookEvents.foreach{ revent =>
      eventWriter.sendPreDBFacebookEvent(revent.fbid, EventParser.getResponseBytes(responses.requestUuid, revent.event))  
    }
    responses.clanResponseEvents.foreach{ revent =>
      eventWriter.sendToClan(revent.clanId, EventParser.getResponseBytes(responses.requestUuid, revent.event))  
    }
    responses.globalChatResponseEvents.foreach{ revent =>
      eventWriter.sendGlobalChat(EventParser.getResponseBytes(responses.requestUuid, revent))
    }
    responses.apnsResponseEvents.foreach{ revent =>
      apnsWriter.handleEvent(revent)
    }
  }
  
  def cacheResponses(responses:ToClientEvents)={
    if(responseCachingEnabled){
      responses.normalResponseEvents.foreach{ response =>
        responseCacheService.cacheResponse(new CachedClientResponse(responses.requestUuid, System.currentTimeMillis(), response.getEventType.getNumber, EventParser.getResponseBytes(responses.requestUuid, response)))
      }
    }
  }
    
  def newToClientEvents(eventUuid:String, playerId:Option[String]):ToClientEvents
  
  
  def handleMaintenanceMode(parsedEvent:ParsedEvent)={
    val re = parsedEvent.event
    val playerId = re.getPlayerId
    if(playerId != null && !playerId.isEmpty) {
      val user = userRetrieveUtils.getUserById(playerId)
      if(user != null && !user.isAdmin){
        messagingUtil.sendMaintanenceModeMessage(appMode.getMessageForUsers, playerId, parsedEvent.eventProto.getEventUuid)
      }
    }else {
      if(re.isInstanceOf[PreDatabaseRequestEvent] ){
        val udid = re.asInstanceOf[PreDatabaseRequestEvent].getUdid
        messagingUtil.sendMaintanenceModeMessageUdid(appMode.getMessageForUsers, udid, parsedEvent.eventProto.getEventUuid)
      }
    }
  } 
  
  def updatePlayerToServerMaps(parsedEvent:ParsedEvent):Unit={
    Future{
      playersOnlineService.updatePlayerToServerMaps(parsedEvent.event)
    }
  }
  
}