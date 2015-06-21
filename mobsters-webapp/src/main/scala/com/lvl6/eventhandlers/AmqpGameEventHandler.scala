package com.lvl6.eventhandlers

import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.core.Message
import com.lvl6.server.eventsender.ToClientEvents

class AmqpGameEventHandler extends GameEventHandler with MessageListener{
  def onMessage(msg:Message)={
    processEvent(msg.getBody)
  }
  
  def newToClientEvents(eventUuid:String, playerId:Option[String]):ToClientEvents={
    new ToClientEvents("amqp", eventUuid, playerId)
  }
}