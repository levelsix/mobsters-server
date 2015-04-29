package com.lvl6.test

import org.junit.Test
import com.lvl6.proto.EventStartupProto.StartupRequestProto
import java.util.UUID
import com.lvl6.proto.EventStartupProto.StartupRequestProtoOrBuilder
import com.lvl6.util.EventParser
import com.lvl6.proto.ProtocolsProto.EventProto
import com.lvl6.proto.ProtocolsProto
import com.typesafe.scalalogging.slf4j.LazyLogging
import com.lvl6.util.EventParser._


class TestEventParser extends LazyLogging {
  
  @Test
  def testEventProtoParser={
    val events = getSomeEventBytes ++ getSomeEventBytes ++ getSomeEventBytes ++ getSomeEventBytes
    val protos = parseEventProtos(events)
    logger.info(s"Parsed event protos: $protos")
  }
  
  
  def getSomeEventBytes:Array[Byte]={
    val sr = StartupRequestProto
    .newBuilder()
    .setUdid(UUID.randomUUID().toString)
    .setApsalarId(UUID.randomUUID().toString)
    .setFbId(UUID.randomUUID.toString)
    .setMacAddress(UUID.randomUUID.toString)
    .build()
    val ep = EventProto.newBuilder()
    .setEventType(ProtocolsProto.EventProtocolRequest.C_STARTUP_EVENT.getNumber)
    .setEventBytes(sr.toByteString())
    .build()
    val ba = ep.toByteArray()
    intToBytes(ba.size)++ba
  }
  
}