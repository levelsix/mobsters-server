package com.lvl6.websockets

import com.typesafe.scalalogging.slf4j.LazyLogging
import java.util.concurrent.ConcurrentHashMap
import java.util.Timer
import java.util.TimerTask
import scala.collection.JavaConversions._

object ClientConnections extends LazyLogging{
  
  protected val connectionsByUserId = new ConcurrentHashMap[String, ClientConnection]()
  protected val connectionsByUdid = new ConcurrentHashMap[String, ClientConnection]()
  protected val connectionsByConnectionId = new ConcurrentHashMap[String, ClientConnection]()
  
  
  def addConnection(connection:ClientConnection)={
    connection.userId match{
      case Some(userId) => connectionsByUserId.put(userId, connection)
      case None =>
    }
    connection.udid match{
      case Some(udid) => connectionsByUdid.put(udid, connection)
      case None =>
    }
    connectionsByConnectionId.put(connection.connectionId, connection)
  }
  
  def removeConnection(connection:ClientConnection)={
    connection.userId match{
      case Some(userId) => connectionsByUserId.remove(userId)
      case None =>
    }
    connection.udid match{
      case Some(udid) => connectionsByUdid.remove(udid)
      case None =>
    }
    connectionsByConnectionId.remove(connection.connectionId)
  }
  
  def getConnection(userIdOrUdidOrConnectionId:String):Option[ClientConnection]= {
    var cc = connectionsByUserId.get(userIdOrUdidOrConnectionId)
    if(cc != null) {
      return Some(cc)
    }
    cc = connectionsByUdid.get(userIdOrUdidOrConnectionId)
    if(cc != null) {
      return Some(cc)
    }
    cc = connectionsByConnectionId.get(userIdOrUdidOrConnectionId)
    if(cc != null) {
      return Some(cc)
    }
    return None
  }
  
  def pingConnections ={
    logger.info(s"Pinging ${connectionsByConnectionId.size()} connections")
    connectionsByConnectionId.elements().foreach{ connection =>
      connection.sendPing    
    }
  }
  
}