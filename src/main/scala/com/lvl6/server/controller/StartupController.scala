package com.lvl6.server.controller

import com.lvl6.events.RequestEvent
import com.lvl6.events.request.StartupRequestEvent
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest
import org.springframework.stereotype.Component
import com.lvl6.mobsters.services.StartupService
import org.springframework.beans.factory.annotation.Autowired
import com.lvl6.server.eventsender.ToClientEvents

@Component
class StartupController extends EventController{
  
  
  @Autowired var startupService:StartupService = null
  
  
  def processRequestEvent(event:RequestEvent, responses:ToClientEvents)={
    startupService.startup(event, responses)
  }
  
  def createRequestEvent:RequestEvent= {
    new StartupRequestEvent()
  }
  
  def getEventType:EventProtocolRequest = EventProtocolRequest.C_STARTUP_EVENT
  
  
}