package com.lvl6.loadtesting;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageChannel;

import com.lvl6.proto.InfoProto.MinimumUserProto;

public class UserQuestTask implements Runnable{
	
	@Resource(name="outboundFakeClientMessageChannel")
	MessageChannel outboundEvents;
	
	
	public MessageChannel getOutboundEvents() {
		return outboundEvents;
	}



	public void setOutboundEvents(MessageChannel outboundEvents) {
		this.outboundEvents = outboundEvents;
	}

	
	protected Integer iterations = 1;
	
	
	

	public Integer getIterations() {
		return iterations;
	}



	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}


	@Autowired
	LoadTestEventGenerator lteg;
	
	
	protected Integer userId;


	public Integer getUserId() {
		return userId;
	}



	public void setUserId(Integer userId) {
		this.userId = userId;
	}



	@Override
	public void run() {
		MinimumUserProto.Builder user = lteg.minimumUserProto(userId);
		for(Integer i = 0; i< iterations; i++) {
			outboundEvents.send(lteg.userQuestDetails(user));
		}

	}

}
