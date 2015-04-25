package com.lvl6.hazelcast

import java.util.concurrent.TimeUnit
import com.hazelcast.core.HazelcastInstance
import com.lvl6.spring.AppContext
import com.hazelcast.core.IMap
import java.util.Date
import com.typesafe.scalalogging.slf4j.LazyLogging

object HazelcastUtil extends LazyLogging {
  
  
  
  def withLock[T](lockName:String, timeout:Long=200l, timeUnit:TimeUnit=TimeUnit.MILLISECONDS, logTimeoutAsError:Boolean = true)( f: => T):Option[T]={
    var gotLock = false
    try{
     gotLock = locksMap.tryLock(lockName, timeout, timeUnit)
     if(gotLock){
       return Some(f)
     }else{
       if(logTimeoutAsError) logger.error(s"Failed to get lock $lockName before timeout of $timeout $timeUnit")
       else logger.trace(s"Failed to get lock $lockName before timeout of $timeout $timeUnit")
     }
    }catch{
      case t:Throwable => throw t
    }finally{
      if(gotLock) locksMap.forceUnlock(lockName)
    }
    None
  }
  
  
  
  private var _hazelcast:HazelcastInstance  = null
  private def hazelcast:HazelcastInstance = {
    if(_hazelcast == null){
      _hazelcast = AppContext.getApplicationContext.getBean(classOf[HazelcastInstance])
    }
    return _hazelcast;
  }
  
  
  private var _locksMap:IMap[String, Date] = null
  private def locksMap:IMap[String, Date]={
    if(_locksMap == null){
      _locksMap = hazelcast.getMap("hazelcastUtilLocksMap")
    }
    _locksMap
  }
  
}