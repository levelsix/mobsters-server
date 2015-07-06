package com.lvl6.server.eventsender

import com.lvl6.events.ResponseEvent
import java.util.ArrayList
import com.google.protobuf.GeneratedMessage
import scala.beans.BeanProperty

case class ToClientEvents(
    sourceConnectionId:String,
    requestUuid:String,
    playerId:Option[String],
    normalResponseEvents:java.util.List[ResponseEvent[_ <: GeneratedMessage]]= new ArrayList[ResponseEvent[_ <: GeneratedMessage]](),
    globalChatResponseEvents:java.util.List[ResponseEvent[_ <: GeneratedMessage]]= new ArrayList[ResponseEvent[_ <: GeneratedMessage]](),
    preDBResponseEvents:java.util.List[PreDBResponseEvent]= new ArrayList[PreDBResponseEvent](),
    preDBFacebookEvents:java.util.List[PreDBFacebookEvent]= new ArrayList[PreDBFacebookEvent](),
    clanResponseEvents:java.util.List[ClanResponseEvent]= new ArrayList[ClanResponseEvent](),
    apnsResponseEvents:java.util.List[ResponseEvent[_ <: GeneratedMessage]]= new ArrayList[ResponseEvent[_ <: GeneratedMessage]]()) {
 

  @BeanProperty
  var changeClansMap = new java.util.HashMap[String, String]()//userId, newClanId
  @BeanProperty
  var userId = ""
}

