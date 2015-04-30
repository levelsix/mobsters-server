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
import scala.collection.JavaConversions._

case class ParsedEventProto(eventProto:EventProto, eventType:EventProtocolRequest){
  def requestEventBytes =  eventProto.getEventBytes.toByteArray()
}

case class ParsedEvent(eventProto:EventProto, eventType:EventProtocolRequest, event:RequestEvent,  eventController:EventController){
  def requestEventBytes =  eventProto.getEventBytes.toByteArray()
}


object EventParser extends LazyLogging{
  
  implicit def intToBytes(int:Int):Array[Byte]={
    EventParser.buffer().putInt(int).array()
  }
 
  implicit def bytestoInt(bytes:Array[Byte]):Int={
    EventParser.buffer().put(bytes).position(0).asInstanceOf[ByteBuffer].getInt
  }
   
  
  def buffer(size:Int=Integer.BYTES, byteOrder:ByteOrder=ByteOrder.LITTLE_ENDIAN):ByteBuffer={
    ByteBuffer.allocate(Integer.BYTES)//.order(ByteOrder.LITTLE_ENDIAN)
  }
  
  def parseEventProtos(eventBytes:Array[Byte], byteOrder:ByteOrder=ByteOrder.LITTLE_ENDIAN):Vector[ParsedEventProto]={
    //parse first event from batch
    logger.info(s"Parsing event from bytes: ${eventBytes.size}")
    var events:Vector[ParsedEventProto] = Vector()
    val sizeOfFirstEvent:Int = eventBytes.slice(0, Integer.BYTES)
    var startOfNextEvent = Integer.BYTES + sizeOfFirstEvent 
    events = events :+ parseSingleEvent(eventBytes.slice(Integer.BYTES, startOfNextEvent))
    if(startOfNextEvent < eventBytes.size){
      //batch has more events so recurse
      logger.info("more bytes... recursing")
      events = events ++ EventParser.parseEventProtos(eventBytes.splitAt(startOfNextEvent)._2)
    }
    events
  }
  
  
  
  protected def parseSingleEvent(eventBytes:Array[Byte]):ParsedEventProto={
    val eventProto:EventProto = EventProto.parseFrom(eventBytes)
    val eventType = EventProtocolRequest.valueOf(eventProto.getEventType)
    ParsedEventProto(eventProto, eventType)        
  }
  
  def getResponseBytes[T <: GeneratedMessage](uuid:String, event:ResponseEvent[T]):Array[Byte]={
    //create event proto
    val ep = EventProto.newBuilder()
    ep.setEventType(event.getEventType.getNumber)
    ep.setTagNum(event.getTag)
    ep.setEventUuid(uuid)
    ep.setEventBytes(event.getByteString())
    val bytes = ep.build().toByteArray()
    intToBytes(bytes.length) ++ bytes
  }
  
}

@Component
class EventParser extends LazyLogging{
  
  @Autowired var eventControllers:java.util.List[EventController] = null
  
  def parseEvents(eventBytes:Array[Byte], byteOrder:ByteOrder=ByteOrder.LITTLE_ENDIAN):Vector[ParsedEvent]={
    val parsedProtos = EventParser.parseEventProtos(eventBytes)
    parsedProtos map parseEvent
  }
  
  
  protected def parseEvent(parsedEventProto:ParsedEventProto):ParsedEvent={
    val eventProto = parsedEventProto.eventProto
    val eventType = EventProtocolRequest.valueOf(eventProto.getEventType)
    val eventController = getEventController(eventType)
    eventController match{
      case Some(ec) => {
        val requestEvent = ec.createRequestEvent
        requestEvent.setTag(eventProto.getTagNum)
        requestEvent.read(ByteBuffer.wrap(eventProto.getEventBytes.toByteArray()))
        ParsedEvent(eventProto, eventType, requestEvent, ec)        
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
  

  
  
  
}