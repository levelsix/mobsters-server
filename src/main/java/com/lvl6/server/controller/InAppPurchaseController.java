package com.lvl6.server.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.InAppPurchaseRequestEvent;
import com.lvl6.events.response.InAppPurchaseResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.InAppPurchaseAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class InAppPurchaseController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String PRODUCTION_URL = "https://buy.itunes.apple.com/verifyReceipt";

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected IAPHistoryRetrieveUtils iapHistoryRetrieveUtil;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2;

	@Autowired
	protected StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils;
	
	@Autowired
	protected BoosterItemRetrieveUtils boosterItemRetrieveUtils;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	

	public InAppPurchaseController() {
		numAllocatedThreads = 2;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new InAppPurchaseRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_IN_APP_PURCHASE_EVENT;
	}

	/*
	 * db stuff done before sending event to eventwriter/client because the
	 * client's not waiting on it immediately anyways
	 */
	// @SuppressWarnings("deprecation")
	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		InAppPurchaseRequestProto reqProto = ((InAppPurchaseRequestEvent) event)
				.getInAppPurchaseRequestProto();

		log.info("reqProto: {}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String receipt = reqProto.getReceipt();

		InAppPurchaseResponseProto.Builder resBuilder = InAppPurchaseResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setReceipt(reqProto.getReceipt());

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(InAppPurchaseStatus.FAIL);
			InAppPurchaseResponseEvent resEvent = new InAppPurchaseResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setInAppPurchaseResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		// Lock this player's ID
		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = userRetrieveUtil.getUserById(userId);

			JSONObject response;
			JSONObject jsonReceipt = new JSONObject();
			jsonReceipt.put(IAPValues.RECEIPT_DATA, receipt);
			log.info("Processing purchase: {}", jsonReceipt.toString(4));
			// Send data
			URL url = new URL(PRODUCTION_URL);

			log.info("Sending purchase request to: {}", url.toString());

			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(jsonReceipt.toString());
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String responseString = "";
			String line;
			while ((line = rd.readLine()) != null) {
				responseString += line;
			}
			log.info("Response: {}", responseString);

			response = new JSONObject(responseString);

			if (response.getInt(IAPValues.STATUS) == 21007
					|| response.getInt(IAPValues.STATUS) == 21008) {
				wr.close();
				rd.close();
				url = new URL(SANDBOX_URL);
				conn = url.openConnection();
				conn.setDoOutput(true);
				wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(jsonReceipt.toString());
				wr.flush();
				rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				responseString = "";
				while ((line = rd.readLine()) != null) {
					responseString += line;
				}
				response = new JSONObject(responseString);
			}

			JSONObject receiptFromApple = null;
			if (response.getInt(IAPValues.STATUS) == 0) {
				receiptFromApple = response.getJSONObject(IAPValues.RECEIPT);
				writeChangesToDb(userId, resBuilder, user, receiptFromApple);
			} else {
				log.error(
						"problem with in-app purchase that client sent, with receipt {}",
						receipt);
			}

			wr.close();
			rd.close();

			if (!resBuilder.hasStatus()) {
				resBuilder.setStatus(InAppPurchaseStatus.FAIL);
			}

			InAppPurchaseResponseProto resProto = resBuilder.build();

			InAppPurchaseResponseEvent resEvent = new InAppPurchaseResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setInAppPurchaseResponseProto(resProto);
			server.writeEvent(resEvent);

			/*if (Globals.KABAM_ENABLED()) {
			if (receiptFromApple != null && resBuilder.getStatus() == InAppPurchaseStatus.SUCCESS) {
			JSONObject logJson = getKabamJsonLogObject(reqProto, resBuilder, receiptFromApple);
			List<NameValuePair> queryParams = getKabamQueryParams(receipt, user, logJson);
			doKabamPost(queryParams, 0);
			}
			}*/

			//null PvpLeagueFromUser means will pull from hazelcast instead
			UpdateClientUserResponseEvent resEventUpdate = miscMethods
					.createUpdateClientUserResponseEventAndUpdateLeaderboard(
							user, null, null);
			resEventUpdate.setTag(event.getTag());
			server.writeEvent(resEventUpdate);

			//      //in case user has a mentor, check if user completed mentor's quest
			//      if (null != receiptFromApple && resBuilder.getStatus() == InAppPurchaseStatus.SUCCESS) {
			//        MenteeQuestType type = MenteeQuestType.BOUGHT_A_PACKAGE;
			//        MiscMethods.sendMenteeFinishedQuests(senderProto, type, server);
			//      }
		} catch (Exception e) {
			log.error("exception in InAppPurchaseController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(InAppPurchaseStatus.FAIL);
				InAppPurchaseResponseEvent resEvent = new InAppPurchaseResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setInAppPurchaseResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in InAppPurchaseController processEvent",
						e);
			}
		} finally {
			// Unlock this player
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private void writeChangesToDb(String userId,
			InAppPurchaseResponseProto.Builder resBuilder, User user,
			JSONObject receiptFromApple) {
		try {
			String packageName = receiptFromApple
					.getString(IAPValues.PRODUCT_ID);
			double realLifeCashCost = IAPValues
					.getCashSpentForPackageName(packageName);

			Date now = new Date();
			InAppPurchaseAction iapa = new InAppPurchaseAction(userId, user,
					receiptFromApple, packageName, now, iapHistoryRetrieveUtil,
					itemForUserRetrieveUtil, structureForUserRetrieveUtils2,
					boosterItemRetrieveUtils, monsterStuffUtils, 
					structureMoneyTreeRetrieveUtils, insertUtil, updateUtil, 
					createInfoProtoUtils, miscMethods);

			iapa.execute(resBuilder);

			if (resBuilder.getStatus().equals(InAppPurchaseStatus.SUCCESS)) {
				resBuilder.setPackageName(packageName);
				resBuilder.setPackagePrice(realLifeCashCost);

				log.info(
						"successful in-app purchase from user {} for package {}",
						userId, packageName);

				Timestamp date = new Timestamp(now.getTime());
				writeToUserCurrencyHistory(userId, date, iapa);
			}
		} catch (Exception e) {
			log.error("problem with in app purchase flow", e);
		}
	}

	private StructureMoneyTree getStructureMoneyTreeForIAPProductId(
			String iapProductId) {
		Map<Integer, StructureMoneyTree> structIdsToMoneyTrees = structureMoneyTreeRetrieveUtils
				.getStructIdsToMoneyTrees();
		log.info(structIdsToMoneyTrees.toString());

		for (Integer i : structIdsToMoneyTrees.keySet()) {
			StructureMoneyTree smt = structIdsToMoneyTrees.get(i);
			if (smt.getIapProductId().equals(iapProductId)) {
				return smt;
			}
		}
		return null;
	}

	/*private void doKabamPost(List<NameValuePair> queryParams, int numTries) {
	log.info("Posting to Kabam");
	String host = Globals.IS_SANDBOX() ? KabamProperties.SANDBOX_PAYMENT_URL : KabamProperties.PRODUCTION_PAYMENT_URL;
	HttpClient client = new DefaultHttpClient();
	HttpPost post = new HttpPost(host);
	try {
	  log.info ("Sending post query: " + queryParams);
	  post.setEntity(new UrlEncodedFormEntity(queryParams, Consts.UTF_8));
	  HttpResponse response = client.execute(post);
	  BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	  String responseString = "";
	  String line;
	  while ((line = rd.readLine()) != null) {
	    responseString += line;
	  }
	  log.info("Received response: " + responseString);

	  JSONObject jsonResponse = new JSONObject(responseString);
	  if (!jsonResponse.getBoolean("success")) {
	    log.error("Failed to log kabam payment with errorcode: "+jsonResponse.getInt("errorcode")+ " and errormessage: "+jsonResponse.getString("errormessage"));
	    if (numTries < 10) {
	      doKabamPost(queryParams, numTries+1);
	    } else {
	      log.error("Giving up..");
	    }
	  }
	} catch (Exception e) {
	  log.error("Error doing Kabam post", e);
	}
	}

	private List<NameValuePair> getKabamQueryParams(String receipt, User user, JSONObject logJson)throws NoSuchAlgorithmException {
	log.info("Generating Post parameters");
	int gameid = Globals.IS_SANDBOX() ? KabamProperties.SANDBOX_CLIENT_ID : KabamProperties.PRODUCTION_CLIENT_ID;
	String secret = Globals.IS_SANDBOX() ? KabamProperties.SANDBOX_SECRET : KabamProperties.PRODUCTION_SECRET;
	long time = new Date().getTime() / 1000;
	List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
	queryParams.add(new BasicNameValuePair("gameid", ""+gameid));
	queryParams.add(new BasicNameValuePair("log", logJson.toString()));
	queryParams.add(new BasicNameValuePair("mobileid", user.getUdid()));
	queryParams.add(new BasicNameValuePair("receipt", receipt));
	queryParams.add(new BasicNameValuePair("timestamp", "" + time));
	queryParams.add(new BasicNameValuePair("userid", "" + user.getKabamNaid()));
	String str = "";
	for (NameValuePair key : queryParams) {
	  str += key.getName() + key.getValue();
	}
	str += secret;
	queryParams.add(new BasicNameValuePair("sig", sha1(str)));
	return queryParams;
	}

	private JSONObject getKabamJsonLogObject(InAppPurchaseRequestProto reqProto,
	  InAppPurchaseResponseProto.Builder resBuilder, JSONObject receiptFromApple) throws JSONException {
	Map<String, Object> logParams = new TreeMap<String, Object>();
	logParams.put("serverid", "1");
	logParams.put("localcents", reqProto.getLocalcents());
	logParams.put("localcurrency", reqProto.getLocalcurrency());
	logParams.put("igc", resBuilder.hasDiamondsGained() ? resBuilder.getDiamondsGained() : resBuilder.getCoinsGained());
	logParams.put("igctype", resBuilder.hasDiamondsGained() ? "gold" : "silver");
	logParams.put("transactionid", receiptFromApple.get(IAPValues.TRANSACTION_ID));
	logParams.put("platform", "itunes");
	logParams.put("locale", reqProto.getLocale());
	logParams.put("lang", "en");
	logParams.put("ipaddr", reqProto.getIpaddr());
	JSONObject logJson = new JSONObject(logParams);
	return logJson;
	}*/

	//	private static String sha1(String input) throws NoSuchAlgorithmException {
	//		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	//		byte[] result = mDigest.digest(input.getBytes());
	//		StringBuilder sb = new StringBuilder();
	//		for (int i = 0; i < result.length; i++) {
	//			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	//		}
	//
	//		return sb.toString();
	//	}

	private void writeToUserCurrencyHistory(String userId, Timestamp date,
			InAppPurchaseAction iapa) {

		miscMethods.writeToUserCurrencyOneUser(userId, date,
				iapa.getCurrencyDeltas(), iapa.getPreviousCurrencies(),
				iapa.getCurrentCurrencies(), iapa.getReasons(),
				iapa.getDetails());
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public IAPHistoryRetrieveUtils getIapHistoryRetrieveUtil() {
		return iapHistoryRetrieveUtil;
	}

	public void setIapHistoryRetrieveUtil(
			IAPHistoryRetrieveUtils iapHistoryRetrieveUtil) {
		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

}
