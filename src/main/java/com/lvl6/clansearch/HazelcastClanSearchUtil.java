package com.lvl6.clansearch;

import java.sql.Timestamp;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class HazelcastClanSearchUtil {
	
	private static final Logger log = LoggerFactory
			.getLogger(HazelcastClanSearchUtil.class);
	
	@Resource(name = "dailyHelpsMap")
	IMap<Timestamp, Integer> dailyHelpsMap;
	
	public IMap<Timestamp, Integer> getDailyHelpsMap() {
		return dailyHelpsMap;
	}

	public void setDailyHelpsMap(
			IMap<Timestamp, Integer> dailyHelpsMap) {
		this.dailyHelpsMap = dailyHelpsMap;
	}

	@Autowired
	protected HazelcastInstance hazelcastInstance;
	
	public HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}
	
	
	
	@Autowired
	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
		
	}
	
}
