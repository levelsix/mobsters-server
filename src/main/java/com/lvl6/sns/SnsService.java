package com.lvl6.sns;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;

@Component
public class SnsService {
	private static final Logger log = LoggerFactory.getLogger(SnsService.class);

	protected AmazonSNSClient snsClient;

	protected String topicArn = "arn:aws:sns:us-west-2:194330317589:mobsters-game-alerts";

	@PostConstruct
	public void setup() {
		snsClient = new AmazonSNSClient();
		snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
	}

	public void sendNotification(String message) {
		try {
			PublishRequest publishRequest = new PublishRequest(topicArn,
					message);
			snsClient.publish(publishRequest);
		} catch (Throwable e) {
			log.error("Error sending sns notification", e);
		}
	}

}
