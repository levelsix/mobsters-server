package com.lvl6.server.eventsender

object EventsUtil {
  def getToClientEvents(connectionId:String, eventUuid:String, playerId:Option[String]):ToClientEvents={
    new ToClientEvents(connectionId, eventUuid, playerId)
  }
  
  def getToClientEventsForUnitTest():ToClientEvents={
    new ToClientEvents("", "", None)
  }
}