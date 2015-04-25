package com.lvl6.mobsters.services

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import com.hazelcast.core.IMap
import javax.annotation.Resource
import com.lvl6.utils.ConnectedPlayer
import com.lvl6.events.RequestEvent
import java.util.Date
import java.util.concurrent.TimeUnit
import com.lvl6.events.PreDatabaseRequestEvent
import com.lvl6.util.Util._
import com.typesafe.scalalogging.slf4j.LazyLogging

object PlayersOnlineService{
  def DEFAULT_TTL:Int = 9
}

@Component
class PlayersOnlineService extends LazyLogging{
  @Resource(name = "playersByPlayerId") var playersByPlayerId:IMap[String, ConnectedPlayer] = null
  @Resource(name = "playersPreDatabaseByUDID") var playersPreDatabaseByUDID:IMap[String, ConnectedPlayer] = null
  
  def updatePlayerToServerMaps(event:RequestEvent)={
    val cplayer = getConnectedPlayer(event.getPlayerId)
    cplayer match {
      case Some(cp) =>{
        cp.setLastMessageSentToServer(new Date())
        cp.setIp_connection_id("amqp")
        cp.setServerHostName("amqp")
        playersByPlayerId.put(event.getPlayerId, cp, PlayersOnlineService.DEFAULT_TTL, TimeUnit.MINUTES)
      }
      case None => addNewConnection(event)
    }
  }
  
  def addNewConnection(event:RequestEvent)={
    val cp = new ConnectedPlayer()
    cp.setIp_connection_id("amqp")
    cp.setServerHostName("amqp")
    if(event.isInstanceOf[PreDatabaseRequestEvent]){
      val pdb = event.asInstanceOf[PreDatabaseRequestEvent]
      cp.setUdid(pdb.getUdid)
      playersPreDatabaseByUDID.put(pdb.getUdid, cp, PlayersOnlineService.DEFAULT_TTL, TimeUnit.MINUTES)
    }else{
      notNullOrEmpty(event.getPlayerId) match {
        case Some(playerId)=> {
          cp.setPlayerId(playerId)
          playersByPlayerId.put(playerId, cp, PlayersOnlineService.DEFAULT_TTL, TimeUnit.MINUTES)
        }
        case None => logger.error(s"Player id not set for request event: $event")
      }      
    }
  }
  
  def getConnectedPlayer(playerId:String):Option[ConnectedPlayer]={
    val cp = playersByPlayerId.get(playerId)
    if(cp != null) Some(cp) else  None
  }
  
  def getConnectedPlayerPreDB(udid:String):Option[ConnectedPlayer]={
    val cp = playersPreDatabaseByUDID.get(udid)
    if(cp != null) Some(cp) else  None
  }
  
    
}