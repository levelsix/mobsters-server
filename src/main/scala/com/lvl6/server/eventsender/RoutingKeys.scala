package com.lvl6.server.eventsender

object RoutingKeys {
  def preDBFaceBookRoutingKey(facebookId:String) = "client_facebookid_"+facebookId
  def preDBRoutingKey(udid:String) = "client_udid_"+udid
  def toUserRoutingKey(playerId:String) = "client_userid_"+playerId
  def clanRoutingKey(clanId:String) = "clan_"+clanId
  val globalchatRoutingKey = "chat_global"
  
}