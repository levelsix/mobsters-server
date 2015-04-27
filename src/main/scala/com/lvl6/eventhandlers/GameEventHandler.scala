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


trait GameEventHandler extends LazyLogging  {
  
  @Autowired var parser:EventParser = null
  @Autowired var appMode:ApplicationMode = null
  @Autowired var messagingUtil:MessagingUtil = null
  @Autowired var userRetrieveUtils:UserRetrieveUtils2 = null
  @Autowired var playersOnlineService:PlayersOnlineService = null
  @Autowired var responseCacheService:ClientResponseCacheService = null

  
  def processEvent(eventBytes:Array[Byte])={
    try{
      val parsedEvent = parser.parseEvent(eventBytes)
      if(appMode.isMaintenanceMode()){
        handleMaintenanceMode(parsedEvent)    
      }else{
        updatePlayerToServerMaps(parsedEvent)
        if(responseCacheService.isResponseCached(parsedEvent.eventProto.getEventUuid)){
          
        }else{
          parsedEvent.eventController.processEvent(parsedEvent.event) match{
            case Some(events)=>{
              sendResponses(events)
              cacheResponses(events)
            }
            case None =>
          }
        }
      }
    }catch{
      case t:Throwable => logger.error("Error processing amqp message", t)
    }
  }
  
  
  def sendResponses(responses:ToClientEvents)={
    
  }
  
  def cacheResponses(responses:ToClientEvents)={
    
  }
    
  
  
  def handleMaintenanceMode(parsedEvent:ParsedEvent)={
    val re = parsedEvent.event
    val playerId = re.getPlayerId
    val user = userRetrieveUtils.getUserById(playerId)
    if(!user.isAdmin){
      if(re.isInstanceOf[PreDatabaseRequestEvent] ){
        val udid = re.asInstanceOf[PreDatabaseRequestEvent].getUdid
        messagingUtil.sendMaintanenceModeMessageUdid(appMode.getMessageForUsers, udid)
      }else{
        messagingUtil.sendMaintanenceModeMessage(appMode.getMessageForUsers, playerId)
      }
    }
  } 
  
  def updatePlayerToServerMaps(parsedEvent:ParsedEvent)={
    Future{
      playersOnlineService.updatePlayerToServerMaps(parsedEvent.event)
    }
  }
  
}