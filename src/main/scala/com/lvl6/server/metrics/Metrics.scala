package com.lvl6.server.metrics

import com.lvl6.spring.AppContext
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.MetricRegistry._

object Metrics {
  
  var reg:MetricRegistry = null
  def registry = {
   if(reg == null) reg = AppContext.getApplicationContext.getBean(classOf[MetricRegistry])
   reg
  }
  
  def timed[T](timerName:String)(body: => T):T={
    val timer = registry.timer(name(this.getClass.getSimpleName, timerName));
    val context = timer.time();
    try {
        body
    } finally {
        context.stop();
    }
  }
}