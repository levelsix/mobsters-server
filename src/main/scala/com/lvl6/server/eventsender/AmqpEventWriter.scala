package com.lvl6.server.eventsender

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import javax.annotation.Resource
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component


@Component
class AmqpEventWriter extends EventWriter with LazyLogging{
  
  
  @Resource(name="chatMessagesTemplate") var chatTemplate:RabbitTemplate = null
  @Resource(name="clientMessagesTemplate") var clientTemplate:RabbitTemplate = null
  
  def preDBFaceBookRoutingKey(facebookId:String) = "client_facebookid_"+facebookId
  def preDBRoutingKey(udid:String) = "client_udid_"+udid
  def toUserRoutingKey(playerId:String) = "client_userid_"+playerId
  def clanRoutingKey(clanId:String) = "clan_"+clanId
  val globalchatRoutingKey = "chat_global"
  
  
  def sendPreDBFacebookEvent(facebookId:String, event:Array[Byte])={
    sendMessageToPlayer(event, messageProperties, preDBFaceBookRoutingKey(facebookId))
  }
  def sendPreDBResponseEvent(udid:String, event:Array[Byte])={
    sendMessageToPlayer(event, messageProperties, preDBRoutingKey(udid))
  }
  def sendToSinglePlayer(playerId:String, event:Array[Byte])={
    sendMessageToPlayer(event, messageProperties, toUserRoutingKey(playerId))
  }
  def sendToMultiplePlayers(playerIds:List[String], event:Array[Byte])={
    playerIds.foreach{ sendToSinglePlayer(_, event) }
  }
  def sendToClan(clanId:String, event:Array[Byte])={
    clientTemplate.send(clanRoutingKey(clanId), new Message(event, messageProperties))
  }
  def sendGlobalChat(event:Array[Byte])={
    chatTemplate.send(globalchatRoutingKey, new Message(event, messageProperties))
  }
  
  
  protected def sendMessageToPlayer(event:Array[Byte], msgProps:MessageProperties, routingKey:String)={
    clientTemplate.send(routingKey, new Message(event, msgProps))
  }
  
  val messageProperties:MessageProperties = {
    val msgProps = new MessageProperties()
    msgProps.setExpiration("120000")
    msgProps
  }
}