package com.lvl6.test

import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.Test
import com.hazelcast.client.impl.client.ClientResponse
import com.lvl6.server.dynamodb.tables.CachedClientResponse
import com.lvl6.mobsters.services.ClientResponseCacheService
import org.springframework.beans.factory.annotation.Autowired
import scala.beans.BeanProperty
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.junit.Assert
import scala.collection.JavaConversions._

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(Array("classpath*:dynamo-test-context.xml"))
class TestResponseCaching extends LazyLogging{

  @Autowired
  @BeanProperty
  var crService: ClientResponseCacheService = null
  val rid = "Test"

  @Test
  def testSaveAndLoad = {
    val cr = CachedClientResponse(rid, System.currentTimeMillis, 2013, Array())
    crService.cacheResponse(cr)
    //Thread.sleep(3000)
    var found = 0
    crService.getCachedResponses(rid) match {
      case Some(responses) => {
        found = responses.size
        logger.info(s"Event $rid was already cached..")
        responses.foreach(r => logger.info(s"Found: $r"))
      }
      case None => logger.info("Cached responses was empty")
    }
    Assert.assertTrue(found > 0)
  }
}