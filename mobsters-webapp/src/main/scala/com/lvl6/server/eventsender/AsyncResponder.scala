package com.lvl6.server.eventsender

import com.lvl6.websockets.ClientConnections
import com.typesafe.scalalogging.slf4j.LazyLogging

object AsyncResponder extends LazyLogging {
  def sendResponses(responses:ToClientEvents) {
    val connection = ClientConnections.getConnection(responses.sourceConnectionId)
    connection match{
      case Some(c)=> c.sendResponses(responses)
      case None=> logger.error(s"Error sending responses. No connection found: $responses")
    }
  }
}