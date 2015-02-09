package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PurchaseNormStructureRequestEvent;
import com.lvl6.events.response.PurchaseNormStructureResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.Structure;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto.PurchaseNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component @DependsOn("gameServer") public class PurchaseNormStructureController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  @Autowired
  protected InsertUtil insertUtils;

  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;

  public PurchaseNormStructureController() {
    numAllocatedThreads = 3;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new PurchaseNormStructureRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_PURCHASE_NORM_STRUCTURE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    PurchaseNormStructureRequestProto reqProto = ((PurchaseNormStructureRequestEvent)event).getPurchaseNormStructureRequestProto();
    log.info( "reqProto={}", reqProto );

    //get stuff client sent
    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    int structId = reqProto.getStructId();
    CoordinatePair cp = new CoordinatePair(reqProto.getStructCoordinates().getX(), reqProto.getStructCoordinates().getY());
    Timestamp timeOfPurchase = new Timestamp(reqProto.getTimeOfPurchase());
    //positive value, need to convert to negative when updating user
    int gemsSpent = reqProto.getGemsSpent();
    //positive means refund, negative means charge user
    int resourceChange = reqProto.getResourceChange();
    ResourceType resourceType = reqProto.getResourceType();

    //things to send to client
    PurchaseNormStructureResponseProto.Builder resBuilder = PurchaseNormStructureResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);

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
      resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);
      PurchaseNormStructureResponseEvent resEvent = new PurchaseNormStructureResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setPurchaseNormStructureResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }

    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      //get things from the db
      User user = getUserRetrieveUtils().getUserById(senderProto.getUserUuid());
      log.info("user={}", user);
      Structure struct = StructureRetrieveUtils.getStructForStructId(structId);

      //currency history purposes
      int previousGems = 0;
      int previousOil = 0;
      int previousCash = 0;
      String uStructId = null;

      boolean legitPurchaseNorm = checkLegitPurchaseNorm(resBuilder, struct, user,
          timeOfPurchase, gemsSpent, resourceChange, resourceType);

      boolean success = false;
      List<String> uStructIdList = new ArrayList<String>();
      Map<String, Integer> money = new HashMap<String, Integer>();
      if (legitPurchaseNorm) {
        previousGems = user.getGems();
        previousOil = user.getOil();
        previousCash = user.getCash();
        success = writeChangesToDB(user, structId, cp, timeOfPurchase, gemsSpent,
            resourceChange, resourceType, uStructIdList, money);
      }

      if (success) {
        resBuilder.setStatus(PurchaseNormStructureStatus.SUCCESS);
        uStructId = uStructIdList.get(0);
        resBuilder.setUserStructUuid(uStructId);
      }

      PurchaseNormStructureResponseEvent resEvent = new PurchaseNormStructureResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setPurchaseNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

      if (success) {
        //null PvpLeagueFromUser means will pull from hazelcast instead
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods
            .createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, null);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);

        writeToUserCurrencyHistory(user, structId, uStructId, timeOfPurchase,
            money, previousGems, previousOil, previousCash);
      }
    } catch (Exception e) {
      log.error("exception in PurchaseNormStructure processEvent", e);
      //don't let the client hang
      try {
        resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);
        PurchaseNormStructureResponseEvent resEvent = new PurchaseNormStructureResponseEvent(userId);
        resEvent.setTag(event.getTag());
        resEvent.setPurchaseNormStructureResponseProto(resBuilder.build());
        server.writeEvent(resEvent);
      } catch (Exception e2) {
        log.error("exception2 in PurchaseNormStructure processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());      
    }
  }


  private boolean checkLegitPurchaseNorm(Builder resBuilder, Structure prospective,
      User user, Timestamp timeOfPurchase, int gemsSpent, int resourceChange,
      ResourceType resourceType) {
    if (user == null || prospective == null || timeOfPurchase == null) {
      log.error(
    	  "parameter passed in is null. user={}\t struct={}\t timeOfPurchase={}",
    	  new Object[] {user, prospective, timeOfPurchase} );
      return false;
    }
    ResourceType structResourceType = ResourceType.valueOf(prospective.getBuildResourceType());
    if (resourceType != structResourceType) {
      log.error(
    	  "unexpected resource type. actual={}, expected={}, structure={}",
    	  new Object[] { resourceType, structResourceType, prospective } );
      return false;
    }

    //check if user has enough resources to build it
    int userGems = user.getGems();
    //check if gems are spent
    if (gemsSpent > 0) {
      if (userGems < gemsSpent) {
        //doesn't have enough gems
        log.error(
        	"user has {} gems; trying to spend {} and {} {} to buy structure={}",
        	new Object[] { userGems, gemsSpent, resourceChange,
        		resourceType, prospective } );
        resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_GEMS);
        return false;
      } else {
        //has enough gems
        return true;
      }

    }

    //since negative resourceChange means charge, then negative of that is
    //the cost. If resourceChange is positive, meaning refund, user will always
    //have more than a negative amount
    int requiredResourceAmount = -1 * resourceChange;
    if (resourceType == ResourceType.CASH) {
      int userResource = user.getCash();
      if (userResource < requiredResourceAmount) {
        log.error(
        	"not enough cash. cash={}, cost={}, structure={}",
        	new Object[] { userResource, requiredResourceAmount, prospective } );
        resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_CASH);
        return false;
      }
    } else if (resourceType == ResourceType.OIL) {
      int userResource = user.getOil();
      if (userResource < requiredResourceAmount) {
        log.error(
        	"not enough oil. oil={}, cost={}, structure={}",
        	new Object[] { userResource, requiredResourceAmount, prospective } );
        resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_OIL);
        return false;
      }
    } else {
      log.error(
    	  "unknown resource type: {}, structure={}",
    	  resourceType, prospective);
      return false;
    }

    return true;
  }

  //uStructId will store the newly created user structure
  private boolean writeChangesToDB(User user, int structId, CoordinatePair cp,
      Timestamp purchaseTime, int gemsSpent, int resourceChange, ResourceType resourceType,
      List<String> uStructId, Map<String, Integer> money) {
    String userId = user.getId();
    Timestamp lastRetrievedTime = null;
    boolean isComplete = false;

    String userStructId = insertUtils.insertUserStruct(userId, structId, cp, purchaseTime,
        lastRetrievedTime, isComplete);
    if (userStructId == null) {
      log.error(String.format(
    	  "problem with giving struct %s at %s on %s",
    	  structId, purchaseTime, cp));
      return false;
    }

    //TAKE AWAY THE CORRECT RESOURCE
    int gemChange = -1 * gemsSpent;
    int cashChange = 0;
    int oilChange = 0;

    if (resourceType == ResourceType.CASH) {
      cashChange = resourceChange;
    } else if (resourceType == ResourceType.OIL) {
      oilChange = resourceChange;
    }

    if (0 == gemChange && 0 == cashChange && 0 == oilChange) {
      log.error(String.format(
    	  "gemChange=%s, cashChange=%s, oilChange=%s. Not purchasing norm struct.",
    	  gemChange, cashChange, oilChange));
      return false;
    }

    int num = user.updateRelativeCashAndOilAndGems(cashChange, oilChange, gemChange);
    if (1 != num) {
      log.error(String.format(
    	  "can't update user currency. gemChange=%s, cashChange=%s, numRowsUpdated=%s",
    	  gemChange, cashChange, num));
      return false;
    } else {//things went ok
      if (0 != gemChange) {
        money.put(MiscMethods.gems, gemChange);
      }
      if (0 != cashChange) {
        money.put(MiscMethods.cash, cashChange);
      }
      if (0 != oilChange) {
        money.put(MiscMethods.oil, oilChange);
      }
    }

    uStructId.add(userStructId);
    return true;
  }

  private void writeToUserCurrencyHistory(User u, int structId, String uStructId,
      Timestamp date, Map<String, Integer> money, int previousGems, int previousOil,
      int previousCash) {
    String userId = u.getId();
    Map<String, Integer> previousCurencyMap = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> details = new HashMap<String, String>();
    String gems = MiscMethods.gems;
    String cash = MiscMethods.cash;
    String oil = MiscMethods.oil;
    String reasonForChange = ControllerConstants.UCHRFC__PURCHASE_NORM_STRUCT;
    StringBuilder detailSb = new StringBuilder();
    detailSb.append("structId=");
    detailSb.append(structId);
    detailSb.append(" uStructId=");
    detailSb.append(uStructId);
    String detail =  detailSb.toString();

    previousCurencyMap.put(gems, previousGems);
    previousCurencyMap.put(cash, previousCash);
    previousCurencyMap.put(oil, previousOil);
    currentCurrencyMap.put(gems, u.getGems());
    currentCurrencyMap.put(cash, u.getCash());
    currentCurrencyMap.put(oil, u.getOil());
    reasonsForChanges.put(gems, reasonForChange);
    reasonsForChanges.put(cash, reasonForChange);
    reasonsForChanges.put(oil, reasonForChange);
    details.put(gems, detail);
    details.put(cash, detail);
    details.put(oil, detail);

    MiscMethods.writeToUserCurrencyOneUser(userId, date, money,
        previousCurencyMap, currentCurrencyMap, reasonsForChanges,
        details);

  }

  public Locker getLocker() {
    return locker;
  }

  public void setLocker(Locker locker) {
    this.locker = locker;
  }

  public void setInsertUtils(InsertUtil insertUtils) {
    this.insertUtils = insertUtils;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }
}
