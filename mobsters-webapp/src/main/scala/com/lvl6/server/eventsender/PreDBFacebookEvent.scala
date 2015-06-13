package com.lvl6.server.eventsender

import com.lvl6.events.ResponseEvent
import com.google.protobuf.GeneratedMessage

case class PreDBFacebookEvent(fbid:String, event:ResponseEvent[_ <: GeneratedMessage])