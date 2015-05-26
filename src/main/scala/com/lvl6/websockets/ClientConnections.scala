package com.lvl6.websockets

import com.typesafe.scalalogging.slf4j.LazyLogging
import java.util.concurrent.ConcurrentHashMap

object ClientConnections extends LazyLogging{
  protected val socketsByUserId = new ConcurrentHashMap[String, ClientConnection]()
  protected val socketsByUdid = new ConcurrentHashMap[String, ClientConnection]()
  
  
  
}