package com.lvl6.server.dynamodb.tables

import org.springframework.stereotype.Component
import java.util.ArrayList
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import java.util.concurrent.TimeUnit
import java.util.UUID


case class CachedClientResponse(request_uuid:String, date:Long, eventType:Int, event:Array[Byte], response_uuid:String=UUID.randomUUID.toString)


@Component
class CachedClientResponses extends TableDefinition {
  
  val dateColumn = "saved_date"
  val eventColumn = "event"
  val eventTypeColumn = "eventType"
  def hashKeyName = "request_uuid"
  def rangeKeyName:Option[String] = Some("response_uuid")
  def tableName = "CachedClientResponses"
  def attributes = List(
      AttributeDef(hashKeyName, ScalarAttributeType.S),
      AttributeDef(rangeKeyName.get, ScalarAttributeType.S)/*,
      AttributeDef(eventColumn, ScalarAttributeType.B),
      AttributeDef(eventTypeColumn, ScalarAttributeType.N),
      AttributeDef(dateColumn, ScalarAttributeType.N)*/)
 
  override def itemTTL = Some(TTLDef(360l, TimeUnit.SECONDS, dateColumn))
}