package com.lvl6.eventhandlers

import scala.beans.BeanProperty
import scala.collection.JavaConversions.asScalaBuffer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.lvl6.events.PreDatabaseRequestEvent
import com.lvl6.events.response.ForceLogoutResponseEvent
import com.lvl6.events.response.StartupResponseEvent
import com.lvl6.mobsters.services.ClientResponseCacheService
import com.lvl6.mobsters.services.PlayersOnlineService
import com.lvl6.retrieveutils.UserRetrieveUtils2
import com.lvl6.server.APNSWriter
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import com.lvl6.server.events.ApplicationMode
import com.lvl6.server.eventsender.EventWriter
import com.lvl6.server.eventsender.ToClientEvents
import com.lvl6.util.EventParser
import com.lvl6.util.ParsedEvent
import com.lvl6.utils.MessagingUtil
import com.typesafe.scalalogging.slf4j.LazyLogging




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
  
  def processEvent(eventBytes:Array[Byte]):Unit={
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
          var isCached = false
          if(responseCachingEnabled) {
            responseCacheService.getCachedResponses(eventUuid) match {
              case Some(responses) => {
            	  logger.info(s"Event $eventUuid was already cached.. sending cached responses")
                responses.foreach{ cr => sendCachedResponse(cr)}
                isCached = true
              }
              case None => logger.info("Cached responses was empty")
            }
          }
          if(!isCached) {
            parsedEvent.eventController.processEvent(parsedEvent.event, toClientEvents) match{
              case Some(events)=>{
                sendResponses(events)
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
  
  def sendCachedResponse(cachedResponse:CachedClientResponse)
  
  
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
    cacheResponses(responses)
  }
  
  def cacheResponses(responses:ToClientEvents)={
    try {
      if(responseCachingEnabled){
        logger.info(s"Caching ${responses.normalResponseEvents.size} responses for request ${responses.requestUuid}")
        responses.normalResponseEvents.foreach{ response =>
          if(!response.isInstanceOf[StartupResponseEvent] && !response.isInstanceOf[ForceLogoutResponseEvent]) {
            responseCacheService.cacheResponse(new CachedClientResponse(responses.requestUuid, System.currentTimeMillis(), response.getEventType.getNumber, EventParser.getResponseBytes(responses.requestUuid, response)))
          }
        }
        responses.preDBResponseEvents.foreach{ response =>
          if(!response.event.isInstanceOf[StartupResponseEvent] && !response.event.isInstanceOf[ForceLogoutResponseEvent]) {
            responseCacheService.cacheResponse(new CachedClientResponse(responses.requestUuid, System.currentTimeMillis(), response.event.getEventType.getNumber, EventParser.getResponseBytes(responses.requestUuid, response.event)))
          }
        }
      }
    }catch{
      case t:Throwable => logger.error("Error caching responses", t)
    }
  }
    
  def newToClientEvents(eventUuid:String, playerId:Option[String]):ToClientEvents
  
  
  def handleMaintenanceMode(parsedEvent:ParsedEvent)={
    val re = parsedEvent.event
    val eventUuid = parsedEvent.eventProto.getEventUuid
    val playerId = re.getPlayerId
    if(playerId != null && !playerId.isEmpty) {
      val user = userRetrieveUtils.getUserById(playerId)
      if(user != null && !user.isAdmin){
        eventWriter.sendToSinglePlayer(eventUuid, messagingUtil.getMaintanenceModeMessage(appMode.getMessageForUsers, playerId, eventUuid))
      }
    }else {
      if(re.isInstanceOf[PreDatabaseRequestEvent] ){
        val udid = re.asInstanceOf[PreDatabaseRequestEvent].getUdid
        eventWriter.sendPreDBResponseEvent(eventUuid, messagingUtil.getMaintanenceModeMessageUdid(appMode.getMessageForUsers, udid, parsedEvent.eventProto.getEventUuid))
      }
    }
  } 
  
  def updatePlayerToServerMaps(parsedEvent:ParsedEvent):Unit={
    //Future{
      playersOnlineService.updatePlayerToServerMaps(parsedEvent.event)
    //}
  }
  
}