package com.lvl6.server.dynamodb.tables

import org.springframework.stereotype.Component
import java.util.ArrayList
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import java.util.concurrent.TimeUnit


case class CachedClientResponse(uuid:String, date:Long, eventType:Int, event:Array[Byte])


//@Component
class CachedClientResponses extends TableDefinition {
  
  val dateColumn = "date"
  val eventColumn = "event"
  val eventTypeColumn = "eventType"
  def hashKeyName = "uuid"
  def rangeKeyName:Option[String] = None
  def tableName = "CachedClientResponses"
  def attributes = List(
      AttributeDef(hashKeyName, ScalarAttributeType.S), 
      AttributeDef(eventColumn, ScalarAttributeType.B),
      AttributeDef(eventTypeColumn, ScalarAttributeType.N),
      AttributeDef(dateColumn, ScalarAttributeType.N))
 
  override def itemTTL = Some(TTLDef(180l, TimeUnit.SECONDS, dateColumn))
}