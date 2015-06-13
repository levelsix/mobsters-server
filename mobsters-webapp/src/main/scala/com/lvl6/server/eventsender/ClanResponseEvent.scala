package com.lvl6.server.eventsender

import com.lvl6.events.ResponseEvent
import com.google.protobuf.GeneratedMessage

case class ClanResponseEvent(event:ResponseEvent[_ <: GeneratedMessage], clanId:String, sendApns:Boolean=false)
