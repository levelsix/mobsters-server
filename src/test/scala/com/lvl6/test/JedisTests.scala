package com.lvl6.test

import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.junit.Test
import com.lvl6.server.jedis.JedisService._
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import redis.clients.jedis.JedisPool
import org.springframework.beans.factory.annotation.Autowired
import org.junit.BeforeClass
import com.lvl6.server.jedis.JedisService
import org.junit.Before

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(Array("/spring-redis.xml"))
class JedisTests extends LazyLogging{

  @Autowired
  var pool:JedisPool = null;
  
  @Before
  def setupClass={
    JedisService.setJedisPool(pool)
  }
  
  @Test
  def testJedisBasic={
    jedis{ jedis =>
      jedis.incr("test_key")
      assert(jedis.get("test_key").toInt > 0)
    }
  }
}