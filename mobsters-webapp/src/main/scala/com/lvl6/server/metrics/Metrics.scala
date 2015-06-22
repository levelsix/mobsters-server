package com.lvl6.server.metrics

import com.lvl6.spring.AppContext
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.MetricRegistry._
import org.springframework.stereotype.Component

object Metrics {
  
  var reg:MetricRegistry = null
  def setRegistry(registree:MetricRegistry)={
    reg = registree
  }
  def registry = {
   //if(reg == null) reg = AppContext.get.getBean("metrics").asInstanceOf[MetricRegistry]
   reg
  }
  
  def timed[T](timerName:String)(body: => T):T={
    val timer = registry.timer(timerName);
    val context = timer.time();
    try {
        body
    } finally {
        context.stop();
    }
  }
}