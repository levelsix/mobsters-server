package com.lvl6.websockets

import com.typesafe.scalalogging.slf4j.LazyLogging
import java.util.concurrent.ConcurrentHashMap

object ClientConnections extends LazyLogging{
  protected val connectionsByUserId = new ConcurrentHashMap[String, ClientConnection]()
  protected val connectionsByUdid = new ConcurrentHashMap[String, ClientConnection]()
  
  def addConnection(connection:ClientConnection)={
    connection.userId match{
      case Some(userId) => connectionsByUserId.put(userId, connection)
      case None =>
    }
    connection.udid match{
      case Some(udid) => connectionsByUdid.put(udid, connection)
      case None =>
    }
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
  }
  
  def getConnection(userIdOrUdid:String):Option[ClientConnection]= {
    var cc = connectionsByUserId.get(userIdOrUdid)
    if(cc != null) {
      return Some(cc)
    }
    cc = connectionsByUdid.get(userIdOrUdid)
    if(cc != null) {
      return Some(cc)
    }
    return None
  }
  
}