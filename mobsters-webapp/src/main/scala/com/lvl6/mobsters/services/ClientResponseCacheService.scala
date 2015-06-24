package com.lvl6.mobsters.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport
import com.lvl6.server.dynamodb.Converter._
import com.lvl6.server.dynamodb.DynamoDBService
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import com.lvl6.server.dynamodb.tables.CachedClientResponses
import com.lvl6.server.dynamodb.tables.TableDefinition
import com.typesafe.scalalogging.slf4j.LazyLogging
import java.util.ArrayList
import scala.collection.JavaConversions._

@Component
class ClientResponseCacheService extends LazyLogging{
  @Autowired var cachedClientResponses:CachedClientResponses = null
  @Autowired var dynamoService:DynamoDBService = null
  
  
  def cacheResponse(response:CachedClientResponse)={
    val pio = dynamoService.putItem(cachedClientResponses, response)
    logger.info(s"Dynamo - caching client response consumed capacity: ${pio.getPutItemResult.getConsumedCapacity} response: $response")
  }
  
  def getCachedResponses(request_uuid:String):Option[java.util.List[CachedClientResponse]]={
    logger.info(s"Getting cached responses for request: $request_uuid")
    val items = dynamoService.getTable(cachedClientResponses).query(cachedClientResponses.hashKeyName, request_uuid)
    var cachedResponses:java.util.List[CachedClientResponse] = items.iterator.toList.map{ item =>
      CachedClientResponse(
        item.getString(cachedClientResponses.hashKeyName),
        item.getLong(cachedClientResponses.dateColumn),
        item.getInt(cachedClientResponses.eventTypeColumn),
        item.getBinary(cachedClientResponses.eventColumn)
      )
      //logger.info(s"Found item: $item")
    }
    logger.info(s"Found ${cachedResponses.size} cached responses")
    if(!cachedResponses.isEmpty){
      logger.info(s"Found ${cachedResponses.size} cached responses")
      Some(cachedResponses)
    }else{
      logger.info(s"Didn't find any cached responses for $request_uuid")
      None
    }
  }
  
}