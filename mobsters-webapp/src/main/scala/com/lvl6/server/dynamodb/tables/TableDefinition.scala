package com.lvl6.server.dynamodb.tables

import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import java.util.ArrayList
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.amazonaws.services.dynamodbv2.model.KeyType
import java.util.concurrent.TimeUnit

case class AttributeDef(name:String, attributeType:ScalarAttributeType)
case class TTLDef(ttl:Long, unit:TimeUnit, ttlColumnName:String)

trait TableDefinition {
  
  def hashKeyName:String
  def rangeKeyName:Option[String]
  def tableName:String
  def attributes:List[AttributeDef]
  def itemTTL:Option[TTLDef] = None
  
  
  def keySchema={
    val sch = new ArrayList[KeySchemaElement]()
    sch.add(new KeySchemaElement().withAttributeName(hashKeyName).withKeyType(KeyType.HASH))
    rangeKeyName match {
      case Some(name) => sch.add(new KeySchemaElement().withAttributeName(name).withKeyType(KeyType.RANGE))
      case None =>
    }
    sch
  }
  
  def attributeDefinitions:java.util.List[AttributeDefinition]={
    val list = new ArrayList[AttributeDefinition]()
    attributes.foreach{ attribute => 
      list.add(new AttributeDefinition().withAttributeName(attribute.name).withAttributeType(attribute.attributeType))
    }
    list
  }
  
  def defaultProvisionedThroughput:Option[ProvisionedThroughput] = None
}