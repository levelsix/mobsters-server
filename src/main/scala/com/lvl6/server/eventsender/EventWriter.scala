package com.lvl6.server.eventsender

import com.lvl6.events.ResponseEvent
import com.lvl6.events.NormalResponseEvent
import com.google.protobuf.GeneratedMessage
import java.util.ArrayList

case class PreDBResponseEvent(udid:String, event:ResponseEvent[GeneratedMessage])
case class PreDBFacebookEvent(fbid:String, event:ResponseEvent[GeneratedMessage])
case class PreClanResponseEvent(clanId:String, event:ResponseEvent[GeneratedMessage])

case class ToClientEvents(
    normalResponseEvents:java.util.List[ResponseEvent[GeneratedMessage]]= new ArrayList[ResponseEvent[GeneratedMessage]](),
    globalChatResponseEvents:java.util.List[ResponseEvent[GeneratedMessage]]= new ArrayList[ResponseEvent[GeneratedMessage]](),
    preDBResponseEvents:java.util.List[PreDBResponseEvent]= new ArrayList[PreDBResponseEvent](),
    preDBFacebookEvents:java.util.List[PreDBFacebookEvent]= new ArrayList[PreDBFacebookEvent](),
    clanResponseEvents:java.util.List[PreClanResponseEvent]= new ArrayList[PreClanResponseEvent]())


trait EventWriter {
  
}