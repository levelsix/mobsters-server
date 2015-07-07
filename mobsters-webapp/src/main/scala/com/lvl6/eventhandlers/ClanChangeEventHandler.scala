package com.lvl6.eventhandlers

import scala.collection.JavaConversions._
import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.amqp.support.converter.MessageConverter
import com.lvl6.server.events.ClanChangeServerEvent
import com.lvl6.websockets.ClientConnections
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * @author kelly
 */
class ClanChangeEventHandler extends MessageListener with LazyLogging {

  @Autowired
  var converter: MessageConverter = null

  def onMessage(msg: Message) = {
    val clanChangeEvent = converter.fromMessage(msg).asInstanceOf[ClanChangeServerEvent]
    logger.info("Received clan change event")
    clanChangeEvent.getUserIdToNewClanIdMap.foreach {
      case (userId, clanId) =>
        ClientConnections.getConnection(userId) match {
          case Some(cc) => cc.changeClan(clanId)
          case None     => logger.info("User $userId not connected to this machine")
        }
    }
  }
}