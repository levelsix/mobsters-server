package com.lvl6.eventhandlers

import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.core.Message

class AmqpGameEventHandler extends GameEventHandler with MessageListener{
  def onMessage(msg:Message)={
    processEvent(msg.getBody)
  }
}