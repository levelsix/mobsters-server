package com.lvl6.server;

import java.util.List;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;

public class SNSService {
	
	protected AmazonSNSAsync sns = new AmazonSNSAsyncClient();
	
	
	public void sendApns(String message, String token) {
		
	}
	
	public void sendApns(String message, List<String> tokens) {
		
	}
	
}
