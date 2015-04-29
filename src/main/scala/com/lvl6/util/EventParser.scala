package com.lvl6.util

import java.nio.ByteOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.google.protobuf.GeneratedMessage
import com.lvl6.events.RequestEvent
import com.lvl6.events.ResponseEvent
import com.lvl6.proto.ProtocolsProto.EventProto
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest
import com.lvl6.server.controller.EventController
import com.typesafe.scalalogging.slf4j.LazyLogging
import java.nio.ByteBuffer

case class ParsedEvent(eventProto:EventProto, event:RequestEvent, eventType:EventProtocolRequest, eventController:EventController){
  def requestEventBytes =  eventProto.getEventBytes.toByteArray()
}


@Component
class EventParser extends LazyLogging{
  
  @Autowired var eventControllers:List[EventController] = null
  
  def parseEvents(eventBytes:Array[Byte], byteOrder:ByteOrder=ByteOrder.LITTLE_ENDIAN):Vector[ParsedEvent]={
    //parse first event from batch
    var events:Vector[ParsedEvent] = Vector()
    val sizeOfFirstEvent = ByteBuffer.allocate(Integer.BYTES).order(byteOrder).put(eventBytes.slice(0, Integer.BYTES)).getInt
    var startOfNextEvent = Integer.BYTES + sizeOfFirstEvent 
    events = events :+ parseSingleEvent(eventBytes.slice(Integer.BYTES, startOfNextEvent))
    if(startOfNextEvent < eventBytes.size){
      //batch has more events so recurse
      events = events ++ parseEvents(eventBytes.splitAt(startOfNextEvent)._2)
    }
    events
  }
  
  
  protected def parseSingleEvent(eventBytes:Array[Byte]):ParsedEvent={
    val eventProto:EventProto = EventProto.parseFrom(eventBytes.slice(4, eventBytes.length))
    val eventType = EventProtocolRequest.valueOf(eventProto.getEventType)
    val eventController = getEventController(eventType)
    eventController match{
      case Some(ec) => {
        val requestEvent = ec.createRequestEvent
        requestEvent.setTag(eventProto.getTagNum)
        requestEvent.read(ByteBuffer.wrap(eventProto.getEventBytes.toByteArray()))
        ParsedEvent(eventProto, requestEvent, eventType, ec)        
      }
      case None => throw new RuntimeException("Error parsing event.. no event controller for type $eventType")
    }
  }
  
  protected var eventControllerMap:Map[EventProtocolRequest, EventController] = null
  
  def getEventController(eventType:EventProtocolRequest):Option[EventController]={
    if(eventControllerMap == null || eventControllerMap.size < eventControllers.size){
      if(eventControllers != null && !eventControllers.isEmpty){
        eventControllerMap = Map()
        eventControllers.foreach{ ec => 
            eventControllerMap += ec.getEventType -> ec
        }
      }else{
        logger.error("eventControllers list is null or empty")
        return None
      }
    }
    eventControllerMap.get(eventType)
  }
  
  def getResponseBytes[T <: GeneratedMessage](event:ResponseEvent[T]):Array[Byte]={
    //create event proto
    val ep = EventProto.newBuilder()
    ep.setEventType(event.getEventType.getNumber)
    ep.setTagNum(event.getTag)
    ep.setEventBytes(event.getByteString())
    val bytes = ep.build().toByteArray()
    ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN).putInt(bytes.length).array() ++ bytes
  }
  
  
  
}