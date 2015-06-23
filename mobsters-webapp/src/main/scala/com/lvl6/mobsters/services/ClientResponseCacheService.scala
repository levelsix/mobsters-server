package com.lvl6.mobsters.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.lvl6.server.dynamodb.Converter._
import com.lvl6.server.dynamodb.DynamoDBService
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import com.lvl6.server.dynamodb.tables.CachedClientResponses
import com.lvl6.server.dynamodb.tables.TableDefinition
import com.typesafe.scalalogging.slf4j.LazyLogging

@Component
class ClientResponseCacheService extends LazyLogging{
  @Autowired var cachedClientResponses:CachedClientResponses = null
  @Autowired var dynamoService:DynamoDBService = null
  
  def isResponseCached(request_uuid:String):Boolean={
    logger.info("Checking for cached responses")
    val table = dynamoService.getTable(cachedClientResponses)
    val items = table.query(cachedClientResponses.hashKeyName, request_uuid)
    logger.info(s"Found ${items.getTotalCount} cached responses for request: $request_uuid")
    items.getTotalCount > 0
  }
  
  def cacheResponse(response:CachedClientResponse)={
    val pio = dynamoService.putItem(cachedClientResponses, response)
    logger.info(s"Dynamo - caching client response consumed capacity: ${pio.getPutItemResult.getConsumedCapacity}")
  }
  
  def getCachedResponses(request_uuid:String):Option[Vector[CachedClientResponse]]={
    val items = dynamoService.getTable(cachedClientResponses).query(cachedClientResponses.hashKeyName, request_uuid)
    var cachedResponses:Vector[CachedClientResponse] = Vector()
    val it = items.iterator()
    while(it.hasNext()){
      val item = it.next()
      cachedResponses = cachedResponses :+ CachedClientResponse(
        item.getString(cachedClientResponses.hashKeyName),
        item.getLong(cachedClientResponses.dateColumn),
        item.getInt(cachedClientResponses.eventTypeColumn),
        item.getString(cachedClientResponses.eventColumn)
      )
    }
    if(!cachedResponses.isEmpty){
      logger.info(s"Found ${cachedResponses.size} cached responses")
      Some(cachedResponses)
    }else{
      None
    }
  }
  
}