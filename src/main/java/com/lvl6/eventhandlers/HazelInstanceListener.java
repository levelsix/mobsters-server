package com.lvl6.eventhandlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstance;

public class HazelInstanceListener implements DistributedObjectListener, InitializingBean {

	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected HazelcastInstance hazel;

	@Autowired
	protected Map<String, DistributedObject> instances;
	
	
	
	public HazelcastInstance getHazel() {
		return hazel;
	}

	public void setHazel(HazelcastInstance hazel) {
		this.hazel = hazel;
	}

	public Map<String, DistributedObject> getInstances() {
		return instances;
	}

	public void setInstances(Map<String, DistributedObject> instances) {
		this.instances = instances;
	}

	/*
	 worked in hazelcast 2.6	 
	@Override
	public void instanceCreated(InstanceEvent instanceEvent) {
		log.info("Created hazel instance: {} type: {}", instanceEvent.getInstance().getId(), instanceEvent.getInstance().getInstanceType().name());
		if(instanceEvent != null) {
			getInstances().put(instanceEvent.getInstance().getId().toString(), instanceEvent.getInstance());
		}
	}

	@Override
	public void instanceDestroyed(InstanceEvent instanceEvent) {
		log.info("Destroyed hazel instance: {} type: {}", instanceEvent.getInstance().getId(), instanceEvent.getEventType().name() );
		if(instanceEvent != null && getInstances().containsKey(instanceEvent.getInstance().getId().toString())) {
			getInstances().remove(instanceEvent.getInstance().getId().toString());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		getHazel().addInstanceListener(this);
	}
	 */
	
	@Override
	public void distributedObjectCreated(DistributedObjectEvent instanceEvent) {
		log.info("Created hazel instance: {} type: {}", instanceEvent.getDistributedObject().getName(),  instanceEvent.getEventType().name());
		if(instanceEvent != null) {
			getInstances().put(instanceEvent.getDistributedObject().getName(), instanceEvent.getDistributedObject());
		}
	}

	@Override
	public void distributedObjectDestroyed(DistributedObjectEvent instanceEvent) {
		log.info("Destroyed hazel instance: {} type: {}", instanceEvent.getDistributedObject().getName(), instanceEvent.getEventType().name() );
		if(instanceEvent != null && getInstances().containsKey(instanceEvent.getDistributedObject().getName())) {
			getInstances().remove(instanceEvent.getDistributedObject().getName());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		getHazel().addDistributedObjectListener(this);
	}

}
