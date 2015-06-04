package com.lvl6.server.eventsender

import com.lvl6.events.ResponseEvent
import java.util.ArrayList
import com.google.protobuf.GeneratedMessage
import scala.beans.BeanProperty

case class ToClientEvents(
    normalResponseEvents:java.util.List[ResponseEvent[_ <: GeneratedMessage]]= new ArrayList[ResponseEvent[_ <: GeneratedMessage]](),
    globalChatResponseEvents:java.util.List[ResponseEvent[_ <: GeneratedMessage]]= new ArrayList[ResponseEvent[_ <: GeneratedMessage]](),
    preDBResponseEvents:java.util.List[PreDBResponseEvent]= new ArrayList[PreDBResponseEvent](),
    preDBFacebookEvents:java.util.List[PreDBFacebookEvent]= new ArrayList[PreDBFacebookEvent](),
    clanResponseEvents:java.util.List[ClanResponseEvent]= new ArrayList[ClanResponseEvent](),
    apnsResponseEvents:java.util.List[ResponseEvent[_ <: GeneratedMessage]]= new ArrayList[ResponseEvent[_ <: GeneratedMessage]]()) {
  
  @BeanProperty
  var clanChanged = false
  @BeanProperty
  var newClanId = "";
  @BeanProperty
  var userId = ""
}

