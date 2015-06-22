package com.lvl6.server.controller

import com.lvl6.events.RequestEvent
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest
import com.lvl6.server.eventsender.ToClientEvents
import com.lvl6.misc.MiscMethods
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.Resource
import com.codahale.metrics.MetricRegistry
import com.lvl6.server.metrics.Metrics._
import com.typesafe.scalalogging.slf4j.LazyLogging
import com.lvl6.util.MDCUtil

abstract class EventController {
  
  @Autowired var miscMethods:MiscMethods = null
  @Resource(name="metrics") var registry:MetricRegistry = null
  
  def createRequestEvent:RequestEvent
  def getEventType():EventProtocolRequest
  def processRequestEvent(event:RequestEvent, responses:ToClientEvents)
  var isAsync = false
  
  val getMetricName:String = "controllers."+getClass.getSimpleName+".processEvent"
  
  def processEvent(event:RequestEvent, responses:ToClientEvents):Option[ToClientEvents]={
    timed(getMetricName){
      try{
        MDCUtil.setMDCProperties("", event.getPlayerId, "")
        //logger.info(s"Received event: ${event.getClass.getSimpleName}")
        processRequestEvent(event, responses)
        if(!isAsync)
          Some(responses)
        else
          None
      }catch{
        case t:Throwable => {
          //logger.error("Error processing request event: $event", t)
          None
        }
      }
    }
  }
}