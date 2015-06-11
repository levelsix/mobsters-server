package com.lvl6.test.controller.unittests;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class MiscTest {

	 public static void main(final String[] args) throws Exception
	    {
	        final HazelcastInstance hz = Hazelcast.newHazelcastInstance();
	        final IMap<Object, Object> map = hz.getMap("foo");

	        map.put("key", "value", 10, TimeUnit.SECONDS);

	        final StopWatch sw = new StopWatch();
	        sw.start();

	        while (true)
	        {
	            final Object value = map.get("key");
	            if (value == null)
	            {
	            	if(map.containsKey("key")) {
	            		System.out.println("still contains key");
	            	}
	                System.out.println("Value has been flushed");
	                System.exit(0);
	            }

	            System.out.printf("%dms value in cache: %s%n", sw.getTime(), value);
	            Thread.sleep(100L);
	        }
	    }
	
	
	
	
	
}
