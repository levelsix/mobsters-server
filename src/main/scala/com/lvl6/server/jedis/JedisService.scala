package com.lvl6.server.jedis

import redis.clients.jedis.JedisPool
import redis.clients.jedis.Jedis
import com.lvl6.spring.AppContext

object JedisService {
  var jedisPool:JedisPool = null;
  
  protected def getJedisPool={
    if(jedisPool == null){
      jedisPool = AppContext.getApplicationContext.getBean(classOf[JedisPool])
    }
    jedisPool
  }
  
  def setJedisPool(pool:JedisPool)={
    jedisPool = pool;
  }
  
  def jedis( jedisOp:Jedis => Unit)={
    val jedis = getJedisPool.getResource
    try{
      jedisOp(jedis)
    }finally{
      jedis.close()
    }
  }
}