package com.lvl6.websockets

import com.typesafe.scalalogging.slf4j.LazyLogging
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint
import com.lvl6.eventhandlers.GameEventHandler
import com.lvl6.util.ParsedEvent
import scala.concurrent.Future

@ServerEndpoint(value = "/game/client")
class ClientConnection extends GameEventHandler with LazyLogging{
  
  var userId:Option[String] = None 
  var udid:Option[String] = None
  var clanId:Option[String] = None
  
  @OnOpen
  def open(session:Session)={
    
  }
  
  @OnClose
  def close= {
    
  }
  
  @OnMessage
  def message(message:Array[Byte])={
    
  }
  
  @OnError
  def error(t:Throwable)={
    
  }
  
  
  @Override
  def updatePlayerToServerMaps(parsedEvent:ParsedEvent)={
    
  }
  
}