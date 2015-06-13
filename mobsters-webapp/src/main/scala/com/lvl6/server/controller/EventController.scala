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

abstract class EventController{
  
  @Autowired var miscMethods:MiscMethods = null
  @Resource(name="metrics") var registry:MetricRegistry = null
  
  def createRequestEvent:RequestEvent
  def getEventType():EventProtocolRequest
  def processRequestEvent(event:RequestEvent, responses:ToClientEvents)
  
  val getMetricName:String = "controllers."+getClass.getSimpleName+".processEvent"
  
  def processEvent(event:RequestEvent):Option[ToClientEvents]={
    timed(getMetricName){
      try{
        miscMethods.setMDCProperties("", event.getPlayerId, "")
        //logger.info(s"Received event: ${event.getClass.getSimpleName}")
        val responses = new ToClientEvents()
        processRequestEvent(event, responses)
        Some(responses)
      }catch{
        case t:Throwable => {
          //logger.error("Error processing request event: $event", t)
          None
        }
      }
    }
  }
}