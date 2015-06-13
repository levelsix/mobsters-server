package com.lvl6.server.eventsender

import com.lvl6.events.ResponseEvent
import com.lvl6.events.NormalResponseEvent
import com.google.protobuf.GeneratedMessage
import java.util.ArrayList
import com.typesafe.scalalogging.slf4j.LazyLogging





trait EventWriter {
  def sendPreDBFacebookEvent(facebookId:String, event:Array[Byte])
  def sendPreDBResponseEvent(udid:String, event:Array[Byte])
  def sendToSinglePlayer(playerId:String, event:Array[Byte])
  def sendToMultiplePlayers(playerIds:List[String], event:Array[Byte])
  def sendToClan(clanId:String, event:Array[Byte])
  def sendGlobalChat(event:Array[Byte])
}


