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


trait GameEventHandler extends LazyLogging  {
  
  @Autowired var parser:EventParser = null
  @Autowired var appMode:ApplicationMode = null
  @Autowired var messagingUtil:MessagingUtil = null
  @Autowired var userRetrieveUtils:UserRetrieveUtils2 = null
  @Autowired var playersOnlineService:PlayersOnlineService = null
  @Autowired var responseCacheService:ClientResponseCacheService = null
  @Autowired var eventWriter:EventWriter = null
  @Autowired var apnsWriter:APNSWriter = null
  
  @BeanProperty
  var responseCachingEnabled = false
  
  def processEvent(eventBytes:Array[Byte])={
    try{
      val parsedEvents = parser.parseEvents(eventBytes)
      parsedEvents.foreach{ parsedEvent => 
        if(appMode.isMaintenanceMode()){
          handleMaintenanceMode(parsedEvent)                
        }else{
          logger.info(s"Processing event: $parsedEvent")
          updatePlayerToServerMaps(parsedEvent)
          val eventUuid = parsedEvent.eventProto.getEventUuid
          val playerId = parsedEvent.event.getPlayerId
          if(responseCachingEnabled) {
            if(responseCacheService.isResponseCached(eventUuid)) {
              logger.info(s"Event $eventUuid was already cached.. sending cached responses")
              val cachedResponses = responseCacheService.getCachedResponses(eventUuid)
              val toClientEvents = new ToClientEvents()
              cachedResponses match {
                case Some(responses) => responses.foreach{ cr => eventWriter.sendToSinglePlayer(playerId, cr.event)}
                case None =>
              }
            }
          }else{
            parsedEvent.eventController.processEvent(parsedEvent.event) match{
              case Some(events)=>{
                sendResponses(eventUuid, playerId, events)
                cacheResponses(eventUuid, playerId, events)
              }
              case None => logger.error("No events returned from parseEvent")
            }
          }
        }
      }
    }catch{
      case t:Throwable => logger.error("Error processing message", t)
    }
  }
  
  
  def sendResponses(uuid:String, playerId:String, responses:ToClientEvents)={
    responses.normalResponseEvents.foreach{ revent =>
        eventWriter.sendToSinglePlayer(playerId, EventParser.getResponseBytes(uuid, revent))
    }
    responses.preDBResponseEvents.foreach{ revent =>
      eventWriter.sendPreDBResponseEvent(revent.udid, EventParser.getResponseBytes(uuid, revent.event))
    }
    responses.preDBFacebookEvents.foreach{ revent =>
      eventWriter.sendPreDBFacebookEvent(revent.fbid, EventParser.getResponseBytes(uuid, revent.event))  
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
  
  def cacheResponses(request_uuid:String, playerId:String, responses:ToClientEvents)={
    if(responseCachingEnabled){
      responses.normalResponseEvents.foreach{ response =>
        responseCacheService.cacheResponse(new CachedClientResponse(request_uuid, System.currentTimeMillis(), response.getEventType.getNumber, EventParser.getResponseBytes(request_uuid, response)))
      }
    }
  }
    
  
  
  def handleMaintenanceMode(parsedEvent:ParsedEvent)={
    val re = parsedEvent.event
    val playerId = re.getPlayerId
    val user = userRetrieveUtils.getUserById(playerId)
    if(!user.isAdmin){
      if(re.isInstanceOf[PreDatabaseRequestEvent] ){
        val udid = re.asInstanceOf[PreDatabaseRequestEvent].getUdid
        //messagingUtil.sendMaintanenceModeMessageUdid(appMode.getMessageForUsers, udid)
      }else{
        //messagingUtil.sendMaintanenceModeMessage(appMode.getMessageForUsers, playerId)
      }
    }
  } 
  
  def updatePlayerToServerMaps(parsedEvent:ParsedEvent):Unit={
    Future{
      playersOnlineService.updatePlayerToServerMaps(parsedEvent.event)
    }
  }
  
}