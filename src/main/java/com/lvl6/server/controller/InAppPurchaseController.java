package com.lvl6.server.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.InAppPurchaseRequestEvent;
import com.lvl6.events.response.InAppPurchaseResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.InAppPurchaseAction;
import com.lvl6.utils.utilmethods.InsertUtil;

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
  protected InsertUtil insertUtil;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;
  
  @Autowired
  protected IAPHistoryRetrieveUtils iapHistoryRetrieveUtils;

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

    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    String receipt = reqProto.getReceipt();

    InAppPurchaseResponseProto.Builder resBuilder = InAppPurchaseResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setReceipt(reqProto.getReceipt());

    UUID userUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(userId);

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s",
          userId), e);
      invalidUuids = true;
    }

    //UUID checks
    if (invalidUuids) {
      resBuilder.setStatus(InAppPurchaseStatus.FAIL);
      InAppPurchaseResponseEvent resEvent = new InAppPurchaseResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setInAppPurchaseResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }

    // Lock this player's ID
    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
    	/*
      User user = getUserRetrieveUtils().getUserById(userId);
      
      JSONObject response;
      JSONObject jsonReceipt = new JSONObject();
      jsonReceipt.put(IAPValues.RECEIPT_DATA, receipt);
      log.info("Processing purchase: " + jsonReceipt.toString(4));
      // Send data
      URL url = new URL(PRODUCTION_URL);

      log.info("Sending purchase request to: " + url.toString());

      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(jsonReceipt.toString());
      wr.flush();

      // Get the response
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String responseString = "";
      String line;
      while ((line = rd.readLine()) != null) {
        responseString += line;
      }
      log.info("Response: " + responseString);

      response = new JSONObject(responseString);

      if (response.getInt(IAPValues.STATUS) == 21007 || response.getInt(IAPValues.STATUS) == 21008) {
        wr.close();
        rd.close();
        url = new URL(SANDBOX_URL);
        conn = url.openConnection();
        conn.setDoOutput(true);
        wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(jsonReceipt.toString());
        wr.flush();
        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        responseString = "";
        while ((line = rd.readLine()) != null) {
          responseString += line;
        }
        response = new JSONObject(responseString);
      }

      JSONObject receiptFromApple = null;
      if (response.getInt(IAPValues.STATUS) == 0) {
        receiptFromApple = response.getJSONObject(IAPValues.RECEIPT);
        if (!getIapHistoryRetrieveUtils().checkIfDuplicateTransaction(Long.parseLong(receiptFromApple
            .getString(IAPValues.TRANSACTION_ID)))) {
          try {
            String packageName = receiptFromApple.getString(IAPValues.PRODUCT_ID);
            int diamondChange = IAPValues.getDiamondsForPackageName(packageName);
            int coinChange = IAPValues.getCoinsForPackageName(packageName);
            double realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);
            boolean isBeginnerSale = IAPValues.packageIsBeginnerSale(packageName);

            Map<String, Integer> previousCurrency =
            		new HashMap<String, Integer>();
            Map<String, Integer> currencyChangeMap =
            		new HashMap<String, Integer>();
            if (diamondChange > 0) {
            	previousCurrency.put(MiscMethods.gems, user.getGems());
            	
            	resBuilder.setDiamondsGained(diamondChange);
            	user.updateRelativeDiamondsBeginnerSale(diamondChange, isBeginnerSale);
            	currencyChangeMap.put(MiscMethods.gems, diamondChange);
            } else {
            	previousCurrency.put(MiscMethods.cash, user.getCash());
            	
            	resBuilder.setCoinsGained(coinChange);
            	user.updateRelativeCoinsBeginnerSale(coinChange, isBeginnerSale);
            	currencyChangeMap.put(MiscMethods.cash, coinChange);
            }

            if (!insertUtils.insertIAPHistoryElem(receiptFromApple,
            		diamondChange, coinChange, user, realLifeCashCost)) {
              log.error("problem with logging in-app purchase history for receipt:"
                  + receiptFromApple.toString(4) + " and user " + user);
            }
            resBuilder.setStatus(InAppPurchaseStatus.SUCCESS);
            resBuilder.setPackageName(receiptFromApple.getString(IAPValues.PRODUCT_ID));

            resBuilder.setPackagePrice(realLifeCashCost);
            log.info("successful in-app purchase from user " + user.getId() + " for package "
                + receiptFromApple.getString(IAPValues.PRODUCT_ID));

            Timestamp date = new Timestamp((new Date()).getTime());
            writeToUserCurrencyHistory(user, packageName, date,
            		currencyChangeMap, previousCurrency);
          } catch (Exception e) {
            log.error("problem with in app purchase flow", e);
          }
        } else {
          resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
          log.error("duplicate receipt from user " + user);
        }
      } else {
        log.error("problem with in-app purchase that client sent, with receipt " + receipt);
      }

      wr.close();
      rd.close();

      if (!resBuilder.hasStatus()) {
        resBuilder.setStatus(InAppPurchaseStatus.FAIL);
      }*/

      InAppPurchaseAction iapa = new InAppPurchaseAction(userId, receipt,
    		  userRetrieveUtils, insertUtil);
      iapa.execute(resBuilder);
      
      InAppPurchaseResponseProto resProto = resBuilder.build();

      InAppPurchaseResponseEvent resEvent = new InAppPurchaseResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setInAppPurchaseResponseProto(resProto);
      server.writeEvent(resEvent);

      if (InAppPurchaseStatus.SUCCESS.equals(resBuilder.getStatus())) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
    	  UpdateClientUserResponseEvent resEventUpdate = MiscMethods
    			  .createUpdateClientUserResponseEventAndUpdateLeaderboard(iapa.getUser(), null, null);
    	  resEventUpdate.setTag(event.getTag());
    	  server.writeEvent(resEventUpdate);
      }

      
    } catch (Exception e) {
      log.error("exception in InAppPurchaseController processEvent", e);
      //don't let the client hang
      try {
        resBuilder.setStatus(InAppPurchaseStatus.FAIL);
        InAppPurchaseResponseEvent resEvent = new InAppPurchaseResponseEvent(userId);
        resEvent.setTag(event.getTag());
        resEvent.setInAppPurchaseResponseProto(resBuilder.build());
        server.writeEvent(resEvent);
      } catch (Exception e2) {
        log.error("exception2 in InAppPurchaseController processEvent", e);
      }
    } finally {
      // Unlock this player
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
    }
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

  private static String sha1(String input) throws NoSuchAlgorithmException {
    MessageDigest mDigest = MessageDigest.getInstance("SHA1");
    byte[] result = mDigest.digest(input.getBytes());
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < result.length; i++) {
      sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();
  }

  private void writeToUserCurrencyHistory(User aUser, String packageName,
		  Timestamp date, Map<String, Integer> currencyChangeMap,
		  Map<String, Integer> previousCurrency) {
	  
	  String userId = aUser.getId();
	  Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
	  Map<String, String> changeReasonsMap = new HashMap<String, String>();
	  Map<String, String> detailsMap = new HashMap<String, String>();
	  String gems = MiscMethods.gems;
	  String cash = MiscMethods.cash;
	  String reasonForChange = ControllerConstants.UCHRFC__IN_APP_PURCHASE;
	  
	  currentCurrencyMap.put(gems, aUser.getGems());
	  currentCurrencyMap.put(cash, aUser.getCash());
	  changeReasonsMap.put(gems, reasonForChange);
	  changeReasonsMap.put(cash, reasonForChange);
	  detailsMap.put(gems, packageName);
	  detailsMap.put(cash, packageName);
	  
	  MiscMethods.writeToUserCurrencyOneUser(userId, date, currencyChangeMap,
			  previousCurrency, currentCurrencyMap, changeReasonsMap,
			  detailsMap);
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public void setInsertUtils(InsertUtil insertUtil) {
    this.insertUtil = insertUtil;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }

  public IAPHistoryRetrieveUtils getIapHistoryRetrieveUtils() {
    return iapHistoryRetrieveUtils;
  }

  public void setIapHistoryRetrieveUtils(
      IAPHistoryRetrieveUtils iapHistoryRetrieveUtils) {
    this.iapHistoryRetrieveUtils = iapHistoryRetrieveUtils;
  }
  
}
