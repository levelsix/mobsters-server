package com.lvl6.aws;

import java.util.List;

public interface SNSService {
	
	
	
	public void sendAppleNotification(String message, String token);
	
	public void sendAppleNotifications(String message, List<String> tokens);
	
}
