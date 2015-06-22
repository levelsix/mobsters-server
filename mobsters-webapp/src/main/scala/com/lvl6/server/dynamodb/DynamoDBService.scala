package com.lvl6.server.dynamodb

import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.lvl6.server.dynamodb.tables.TableDefinition
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.model.TableDescription
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.beans.factory.annotation.Autowired
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.LazyLogging
import com.lvl6.server.dynamodb.tables.TTLDef
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome
import com.amazonaws.services.dynamodbv2.document.Item
import com.lvl6.server.dynamodb.Converter._
import scala.beans.BeanProperty
import org.springframework.beans.factory.annotation.Value
import com.amazonaws.auth.BasicAWSCredentials


@Component
class DynamoDBService extends LazyLogging {
  
  
  protected var client:AmazonDynamoDBClient = null
  protected var dynamoDB:DynamoDB = null

  @BeanProperty
  @Value("${dynamodb.table.prefix}")
  var tablePrefix:String = ""
  
  @BeanProperty
  @Value("${dynamodb.isLocal}")
  var isLocal = false//for testing dynamo locally
  
  
  
  def provisionedThroughput:ProvisionedThroughput = {
    new ProvisionedThroughput().withReadCapacityUnits(10l).withWriteCapacityUnits(10l)
  }
  
  @Autowired
  var tableDefinitions:java.util.Set[TableDefinition] = null
  
  def tableName(tableDef:TableDefinition):String= tablePrefix+tableDef.tableName
  
  def checkTableExists(tableDef:TableDefinition):Option[TableDescription]={
    try{
      Some(client.describeTable(tableName(tableDef)).getTable)
    }catch{
      case t:ResourceNotFoundException => None
      case t:Throwable => throw t
    }
  }
  
  def createTableFromDefinition(tableDef:TableDefinition):Table={
    logger.info(s"Creating table ${tableName(tableDef)}")
    val request:CreateTableRequest = new CreateTableRequest()
      .withTableName(tableName(tableDef))
      .withKeySchema(tableDef.keySchema)
      .withAttributeDefinitions(tableDef.attributeDefinitions);
    tableDef.defaultProvisionedThroughput match {
      case Some(pt) => request.withProvisionedThroughput(pt)
      case None =>  request.withProvisionedThroughput(provisionedThroughput)
    }
    val table = dynamoDB.createTable(request)
    table.waitForActive()
    table
  }

  def getTable(tableDef:TableDefinition):Table={
    dynamoDB.getTable(tableName(tableDef))
  }
  
  def createTables={
    tableDefinitions.foreach{ tableDef =>
      try{
        checkTableExists(tableDef) match{
          case Some(td) => logger.info(s"Table ${tableName(tableDef)} already exists. Current capacity read: ${td.getProvisionedThroughput.getReadCapacityUnits} write: ${td.getProvisionedThroughput.getWriteCapacityUnits}")
          case None => createTableFromDefinition(tableDef)
        }        
      } catch{
        case t:Throwable => logger.error("Error creating dynamo table", t)
      } 
    }
  }
  
  def deleteItem(tableDef:TableDefinition, id:String):DeleteItemOutcome={
    logger.info(s"Deleting item with ${tableDef.hashKeyName}: $id from table: ${tableName(tableDef)}")
    getTable(tableDef).deleteItem(tableDef.hashKeyName, id)
  }
  
  val ttlFilterKey = ":ttlTime"
  //@Scheduled(fixedDelay=300000l)
  def checkTTL={
     tableDefinitions.foreach(checkTTLForTable)   
  }
  
  def checkTTLForTable(tableDef:TableDefinition)={
    tableDef.itemTTL match{
      case Some(ttlDef) => {
        val tableN = tableName(tableDef)
        val atts = getTTLScanFilter(ttlDef)
        var lastEvaluatedKey:Map[String, AttributeValue] = null
        do{
          val scanRequest = new ScanRequest()
          .withTableName(tableN)
          .withLimit(25)
          .withFilterExpression(ttlDef.ttlColumnName+" < "+ttlFilterKey)
          .withProjectionExpression(tableDef.hashKeyName)
          .withExpressionAttributeValues(atts)
          val result = client.scan(scanRequest)
          result.getItems.foreach{ item => 
            val av = item.get(tableDef.hashKeyName)
            logger.info(s"Item with key: ${av.getS} in table: $tableN is expired... deleting")  
          }
        }while(lastEvaluatedKey != null)
      }
      case None =>
    }
  }
  
  def getTTLScanFilter(ttlDef:TTLDef):java.util.Map[String, AttributeValue]={
    val atts = new java.util.HashMap[String, AttributeValue]();
    atts.put(ttlFilterKey, new AttributeValue().withN(""+ttlDef.unit.toMillis(ttlDef.ttl)))
    atts
  }
  
  def putItem(tableDef:TableDefinition, caseClass:AnyRef):PutItemOutcome={
    val item:Item = new Item();
    caseClassToMap(caseClass).foreach{ case (key, value) =>
      if(key.equals(tableDef.hashKeyName))
        item.withPrimaryKey(key, value)
      else
        item.`with`(key, value)
    }
    getTable(tableDef).putItem(item)
  }
  
  @PostConstruct
  def setup={
    if(isLocal) {
    	client = new AmazonDynamoDBClient(new BasicAWSCredentials("Fake", "Fake"))
      client.setEndpoint("http://localhost:8000")
    }else {
       client = new AmazonDynamoDBClient()
    }
    dynamoDB = new DynamoDB(client)
  }
  
}