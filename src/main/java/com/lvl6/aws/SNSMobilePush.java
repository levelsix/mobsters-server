package com.lvl6.aws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.lvl6.aws.MessageGenerator.Platform;

public class SNSMobilePush implements SNSService {

	private static final Logger		log					= LoggerFactory.getLogger(SNSMobilePush.class);

	private AmazonSNSClientWrapper	snsClientWrapper;

	//gcm
	private String					gcmServerAPIKey		= "";
	private String					gcmApplicationName	= "";
	private String					gcmRegistrationId	= "";

	//apns
	// This should be in pem format with \n at the end of each line.
	private String					apnsCertificate		= "";
	// This should be in pem format with \n at the end of each line.
	private String					apnsPrivateKey		= "";
	private String					apnsApplicationName	= "";
	private boolean					isSandBox			= true;

	
	
	
	public SNSMobilePush() {
		AmazonSNS snsClient = new AmazonSNSClient();
		snsClient.setEndpoint("https://sns.us-west-2.amazonaws.com");
		this.snsClientWrapper = new AmazonSNSClientWrapper(snsClient);
	}

	public static final Map<Platform, Map<String, MessageAttributeValue>>	attributesMap	= new HashMap<Platform, Map<String, MessageAttributeValue>>();
	static {
		attributesMap.put(Platform.GCM, null);
		attributesMap.put(Platform.APNS, null);
		attributesMap.put(Platform.APNS_SANDBOX, null);
	}

	/*
	 * try {
	 * 
	 * } catch (AmazonServiceException ase) { System.out.println(
	 * "Caught an AmazonServiceException, which means your request made it " +
	 * "to Amazon SNS, but was rejected with an error response for some reason."
	 * ); System.out.println("Error Message:    " + ase.getMessage());
	 * System.out.println("HTTP Status Code: " + ase.getStatusCode());
	 * System.out.println("AWS Error Code:   " + ase.getErrorCode());
	 * System.out.println("Error Type:       " + ase.getErrorType());
	 * System.out.println("Request ID:       " + ase.getRequestId()); } catch
	 * (AmazonClientException ace) { System.out.println(
	 * "Caught an AmazonClientException, which means the client encountered " +
	 * "a serious internal problem while trying to communicate with SNS, such as not "
	 * + "being able to access the network.");
	 * System.out.println("Error Message: " + ace.getMessage()); }
	 */

	public void androidAppNotification() {
		// TODO: Please fill in following values for your application. You can
		// also change the notification payload as per your preferences using
		// the method
		// com.amazonaws.sns.samples.tools.SampleMessageGenerator.getSampleAndroidMessage()

		snsClientWrapper.demoNotification(Platform.GCM, "", gcmServerAPIKey, gcmRegistrationId, gcmApplicationName, attributesMap);
	}

	@Override
	public void sendAppleNotification(String message, String token) {

		try {
			snsClientWrapper.demoNotification(Platform.APNS, apnsCertificate, apnsPrivateKey, token, apnsApplicationName, attributesMap);
		} catch (AmazonServiceException ase) {
			StringBuffer sb = new StringBuffer();
			sb.append("Caught an AmazonServiceException, which means your request made it to Amazon SNS, but was rejected with an error response for some reason.");
			sb.append("Error Message:    " + ase.getMessage());
			sb.append("HTTP Status Code: " + ase.getStatusCode());
			sb.append("AWS Error Code:   " + ase.getErrorCode());
			sb.append("Error Type:       " + ase.getErrorType());
			sb.append("Request ID:       " + ase.getRequestId());
			log.error(sb.toString(), ase);
		} catch (AmazonClientException ace) {
			StringBuffer sb = new StringBuffer();
			sb.append("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with SNS, such as not being able to access the network.");
			sb.append("Error Message: " + ace.getMessage());
			log.error(sb.toString(), ace);
		}

	}

	@Override
	public void sendAppleNotifications(String message, List<String> tokens) {
		for (String token : tokens) {
			sendAppleNotification(message, token);
		}
	}

	public String getApnsCertificate() {
		return apnsCertificate;
	}

	public void setApnsCertificate(String apnsCertificate) {
		this.apnsCertificate = apnsCertificate;
	}

	public String getApnsPrivateKey() {
		return apnsPrivateKey;
	}

	public void setApnsPrivateKey(String apnsPrivateKey) {
		this.apnsPrivateKey = apnsPrivateKey;
	}

	public String getApnsApplicationName() {
		return apnsApplicationName;
	}

	public void setApnsApplicationName(String apnsApplicationName) {
		this.apnsApplicationName = apnsApplicationName;
	}

	public String getGcmServerAPIKey() {
		return gcmServerAPIKey;
	}

	public void setGcmServerAPIKey(String gcmServerAPIKey) {
		this.gcmServerAPIKey = gcmServerAPIKey;
	}

	public String getGcmApplicationName() {
		return gcmApplicationName;
	}

	public void setGcmApplicationName(String gcmApplicationName) {
		this.gcmApplicationName = gcmApplicationName;
	}

	public String getGcmRegistrationId() {
		return gcmRegistrationId;
	}

	public void setGcmRegistrationId(String gcmRegistrationId) {
		this.gcmRegistrationId = gcmRegistrationId;
	}

	public boolean isSandBox() {
		return isSandBox;
	}

	public void setIsSandBox(boolean isSandBox) {
		this.isSandBox = isSandBox;
	}
}
