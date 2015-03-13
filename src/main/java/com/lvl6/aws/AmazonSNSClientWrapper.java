package com.lvl6.aws;

/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class AmazonSNSClientWrapper {

	/*	
		private static final Logger log = LoggerFactory.getLogger(AmazonSNSClientWrapper.class);
		
		private final AmazonSNS snsClient;

		public AmazonSNSClientWrapper(AmazonSNS client) {
			this.snsClient = client;
		}

		private CreatePlatformApplicationResult createPlatformApplication(
				String applicationName, 
				Platform platform, 
				String principal,
				String credential) {
			CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("PlatformPrincipal", principal);
			attributes.put("PlatformCredential", credential);
			platformApplicationRequest.setAttributes(attributes);
			platformApplicationRequest.setName(applicationName);
			platformApplicationRequest.setPlatform(platform.name());
			return snsClient.createPlatformApplication(platformApplicationRequest);
		}

		private CreatePlatformEndpointResult createPlatformEndpoint(
				Platform platform, 
				String customData, 
				String platformToken,
				String applicationArn) {
			CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
			platformEndpointRequest.setCustomUserData(customData);
			String token = platformToken;
			String userId = null;
			if (platform == Platform.BAIDU) {
				String[] tokenBits = platformToken.split("\\|");
				token = tokenBits[0];
				userId = tokenBits[1];
				Map<String, String> endpointAttributes = new HashMap<String, String>();
				endpointAttributes.put("UserId", userId);
				endpointAttributes.put("ChannelId", token);
				platformEndpointRequest.setAttributes(endpointAttributes);
			}
			platformEndpointRequest.setToken(token);
			platformEndpointRequest.setPlatformApplicationArn(applicationArn);
			return snsClient.createPlatformEndpoint(platformEndpointRequest);
		}

		private void deletePlatformApplication(String applicationArn) {
			DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
			request.setPlatformApplicationArn(applicationArn);
			snsClient.deletePlatformApplication(request);
		}

		private PublishResult publish(
				String endpointArn, 
				Platform platform,
				Map<Platform, 
				Map<String, 
				MessageAttributeValue>> attributesMap) {
			PublishRequest publishRequest = new PublishRequest();
			Map<String, MessageAttributeValue> notificationAttributes = getValidNotificationAttributes(attributesMap
					.get(platform));
			if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
				publishRequest.setMessageAttributes(notificationAttributes);
			}
			publishRequest.setMessageStructure("json");
			// If the message attributes are not set in the requisite method,
			// notification is sent with default attributes
			String message = getPlatformMessage(platform);
			Map<String, String> messageMap = new HashMap<String, String>();
			messageMap.put(platform.name(), message);
			message = MessageGenerator.jsonify(messageMap);
			// For direct publish to mobile end points, topicArn is not relevant.
			publishRequest.setTargetArn(endpointArn);

			// Display the message that will be sent to the endpoint/
			log.info("{Message Body: " + message + "}");
			StringBuilder builder = new StringBuilder();
			builder.append("{Message Attributes: ");
			for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes
					.entrySet()) {
				builder.append("(\"" + entry.getKey() + "\": \""
						+ entry.getValue().getStringValue() + "\"),");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append("}");
			log.info(builder.toString());

			publishRequest.setMessage(message);
			return snsClient.publish(publishRequest);
		}

		public void demoNotification(
				Platform platform, 
				String principal,
				String credential, 
				String platformToken, 
				String applicationName,
				Map<Platform, 
				Map<String, MessageAttributeValue>> attrsMap) {
			// Create Platform Application. This corresponds to an app on a
			// platform.
			CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
					applicationName, platform, principal, credential);
			log.info(platformApplicationResult.toString());

			// The Platform Application Arn can be used to uniquely identify the
			// Platform Application.
			String platformApplicationArn = platformApplicationResult
					.getPlatformApplicationArn();

			// Create an Endpoint. This corresponds to an app on a device.
			CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(
					platform,
					"CustomData - Useful to store endpoint specific data",
					platformToken, 
					platformApplicationArn);
			log.info(platformEndpointResult.toString());

			// Publish a push notification to an Endpoint.
			PublishResult publishResult = publish(
					platformEndpointResult.getEndpointArn(), 
					platform, 
					attrsMap);
			log.info("Published! \n{MessageId="
					+ publishResult.getMessageId() + "}");
			// Delete the Platform Application since we will no longer be using it.
			deletePlatformApplication(platformApplicationArn);
		}

		private String getPlatformMessage(Platform platform) {
			switch (platform) {
			case APNS:
				return MessageGenerator.getAppleMessage();
			case APNS_SANDBOX:
				return MessageGenerator.getAppleMessage();
			case GCM:
				return MessageGenerator.getAndroidMessage();
			case ADM:
				return MessageGenerator.getKindleMessage();
			case BAIDU:
				return MessageGenerator.getBaiduMessage();
			case WNS:
				return MessageGenerator.getWNSMessage();
			case MPNS:
				return MessageGenerator.getMPNSMessage();
			default:
				throw new IllegalArgumentException("Platform not supported : "
						+ platform.name());
			}
		}

		public static Map<String, MessageAttributeValue> getValidNotificationAttributes(
				Map<String, MessageAttributeValue> notificationAttributes) {
			Map<String, MessageAttributeValue> validAttributes = new HashMap<String, MessageAttributeValue>();

			if (notificationAttributes == null) return validAttributes;

			for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes
					.entrySet()) {
				if (!StringUtils.isBlank(entry.getValue().getStringValue())) {
					validAttributes.put(entry.getKey(), entry.getValue());
				}
			}
			return validAttributes;
		}*/
}