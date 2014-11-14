package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IList;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PurchaseBoosterPackRequestEvent;
import com.lvl6.events.response.PurchaseBoosterPackResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackRequestProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.Builder;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.PurchaseBoosterPackStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class PurchaseBoosterPackController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public static int BOOSTER_PURCHASES_MAX_SIZE = 50;

  @Autowired
  protected Locker locker;

  @Autowired
  protected TimeUtils timeUtils;

  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;
  
  @Resource(name = "goodEquipsRecievedFromBoosterPacks")
  protected IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks;

  
  
  public PurchaseBoosterPackController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new PurchaseBoosterPackRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_PURCHASE_BOOSTER_PACK_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    PurchaseBoosterPackRequestProto reqProto = ((PurchaseBoosterPackRequestEvent)event).getPurchaseBoosterPackRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    int boosterPackId = reqProto.getBoosterPackId();
    Date now = new Date(reqProto.getClientTime());
    Timestamp nowTimestamp = new Timestamp(now.getTime());
    
    boolean freeBoosterPack = reqProto.getDailyFreeBoosterPack();

    //values to send to client
    PurchaseBoosterPackResponseProto.Builder resBuilder = PurchaseBoosterPackResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);

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
      resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
      PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setPurchaseBoosterPackResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }


    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      User user = getUserRetrieveUtils().getUserById(userId);
      int previousGems = 0;
      BoosterPack aPack = BoosterPackRetrieveUtils.getBoosterPackForBoosterPackId(boosterPackId);
      int gemPrice = aPack.getGemPrice();
      Map<Integer, BoosterItem> boosterItemIdsToBoosterItems = BoosterItemRetrieveUtils.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);

      List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
      int gemReward = 0;
      
      boolean legit = checkLegitPurchase(resBuilder, user, userId, now,
          aPack, boosterPackId, gemPrice, freeBoosterPack,
          boosterItemIdsToBoosterItems);

      boolean successful = false;
      if (legit) {
      	previousGems = user.getGems();

      	int numBoosterItemsUserWants = 1;
      	log.info("determining the booster items the user receives.");
        itemsUserReceives = MiscMethods.determineBoosterItemsUserReceives(
        		numBoosterItemsUserWants, boosterItemIdsToBoosterItems);
        
        legit = MiscMethods.checkIfMonstersExist(itemsUserReceives);
      }
        
      if (legit) {
        gemReward = MiscMethods.determineGemReward(itemsUserReceives);
        //set the FullUserMonsterProtos (in resBuilder) to send to the client
        successful = writeChangesToDB(resBuilder, user, boosterPackId,
        		itemsUserReceives, gemPrice, now, gemReward, freeBoosterPack);
      }
      
      if (successful) {
      	//assume user only purchases 1 item. NEED TO LET CLIENT KNOW THE PRIZE
      	if (null != itemsUserReceives && !itemsUserReceives.isEmpty()) {
      		BoosterItem bi = itemsUserReceives.get(0);
      		BoosterItemProto bip = CreateInfoProtoUtils.createBoosterItemProto(bi);
      		resBuilder.setPrize(bip);
      	}
      }
      
      PurchaseBoosterPackResponseProto resProto = resBuilder.build();
      PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setPurchaseBoosterPackResponseProto(resProto);
      server.writeEvent(resEvent);
      
      if (successful) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods
        		.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, boosterPackId, nowTimestamp,
            gemPrice, previousGems, itemsUserReceives, gemReward, freeBoosterPack);
        
        //just assume user can only buy one booster pack at a time
        writeToBoosterPackPurchaseHistory(userId, boosterPackId, itemsUserReceives,
        		resBuilder.getUpdatedOrNewList(), nowTimestamp);
        sendBoosterPurchaseMessage(user, aPack, itemsUserReceives);
      }
    } catch (Exception e) {
    	log.error("exception in PurchaseBoosterPackController processEvent", e);
    	// don't let the client hang
    	try {
    		resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
    		PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(senderProto.getUserUuid());
        resEvent.setTag(event.getTag());
        resEvent.setPurchaseBoosterPackResponseProto(resBuilder.build());
        server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in SellUserMonsterController processEvent", e);
    	}
    } finally {
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName()); 
    }
  }

  private void sendBoosterPurchaseMessage(User user, BoosterPack aPack, List<BoosterItem> itemsUserReceives) {
//    Map<Integer, Monster> equipMap = MonsterRetrieveUtils.getMonsterIdsToMonster();
//    Date d = new Date();
//    for (BoosterItem bi : itemsUserReceives) {
//      Monster eq = equipMap.get(bi.getEquipId());
//      if (eq.getRarity().compareTo(Rarity.SUPERRARE) >= 0) {
//        RareBoosterPurchaseProto r = CreateInfoProtoUtils.createRareBoosterPurchaseProto(aPack, user, eq, d);
//        
//        goodEquipsRecievedFromBoosterPacks.add(0, r);
//        //remove older messages
//        try {
//          while(goodEquipsRecievedFromBoosterPacks.size() > BOOSTER_PURCHASES_MAX_SIZE) {
//            goodEquipsRecievedFromBoosterPacks.remove(BOOSTER_PURCHASES_MAX_SIZE);
//          }
//        } catch(Exception e) {
//          log.error("Error adding rare booster purchase.", e);
//        }
//        
//        ReceivedRareBoosterPurchaseResponseProto p = ReceivedRareBoosterPurchaseResponseProto.newBuilder().setRareBoosterPurchase(r).build();
//        ReceivedRareBoosterPurchaseResponseEvent e = new ReceivedRareBoosterPurchaseResponseEvent(user.getId());
//        e.setReceivedRareBoosterPurchaseResponseProto(p);
//        eventWriter.processGlobalChatResponseEvent(e);
//      }
//    }
  }
  
  private boolean checkLegitPurchase(Builder resBuilder, User aUser, String userId, 
      Date now, BoosterPack aPack, int boosterPackId, int gemPrice,
      boolean freeBoosterPack, Map<Integer, BoosterItem> idsToBoosterItems) {
    
    if (null == aUser || null == aPack || null == idsToBoosterItems ||
    		idsToBoosterItems.isEmpty()) {
      resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
      log.error(String.format(
    	  "no user for id=%s, or no BoosterPack for id=%s, or no booster items=%s",
      		userId, boosterPackId, idsToBoosterItems));
      return false;
    }

    int userGems = aUser.getGems();
    //check if user can afford to buy however many more user wants to buy
    if (!freeBoosterPack) {
    	if (userGems < gemPrice) {
    		resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_INSUFFICIENT_GEMS);
    		return false; //resBuilder status set in called function 
    	}
    } else {

    	Date lastFreeDate = aUser.getLastFreeBoosterPackTime();
    	if (null != lastFreeDate) {
    		if (!timeUtils.isFirstEarlierThanSecond(lastFreeDate, now)) {
    			// lastFreeDate is not earlier than now
    			log.error(String.format(
    				"client incorrectly says time now=%s is before lastFreeBoosterPackDate=%s",
    				now, lastFreeDate));
    			return false;
    			
    		} else if ( Math.abs( timeUtils.numDaysDifference(lastFreeDate,now) ) == 0) {
    			// lastFreeDate is earlier than now but
    			// lastFreeDate is on same day as now 
    			
    			log.error(String.format(
    				"client already received free booster pack today. lastFreeBoosterPackDate=%s, now=%s",
    				lastFreeDate, now));
    			return false;
    		}
    		
    	}
    }
    resBuilder.setStatus(PurchaseBoosterPackStatus.SUCCESS);
    return true;
  }
  
//  private int getNumEquipsPurchasedToday(int userId, int boosterPackId, 
//      DateTime startOfDayInLA) {
//    //get the time at the start of the day in UTC
//    DateTimeZone utcTZ = DateTimeZone.UTC;
//    DateTime startOfDayInLAInUtc = startOfDayInLA.withZone(utcTZ);
//    Timestamp startTime = new Timestamp(startOfDayInLAInUtc.toDate().getTime());
//
//    int numPurchased = UserBoosterPackRetrieveUtils
//        .getNumPacksPurchasedAfterDateForUserAndPackId(userId, boosterPackId, startTime);
//    
//    return numPurchased;
//  }
  
  private boolean writeChangesToDB(Builder resBuilder, User user, int bPackId,
      List<BoosterItem> itemsUserReceives, int gemPrice, Date now, int gemReward,
      boolean freeBoosterPack) {
  	
    //update user, user_monsters
    String userId = user.getId();
  	int currencyChange = -1 * gemPrice; //should be negative
  	if (freeBoosterPack) {
  		currencyChange = 0;
  	}
  	currencyChange += gemReward;
  	
  	//update user's money
  	if (!freeBoosterPack) {
  		if (!user.updateRelativeGemsNaive(currencyChange)) {
  			log.error("could not change user's money. gemPrice=" + gemPrice + "\t gemReward=" +
  				gemReward + "\t change=" + currencyChange);
  			return false;
  		}
  	} else {
  		if (!user.updateFreeBoosterPack(currencyChange, now)) {
  			log.error("could not change user's money and freeBoosterPackTime. gemPrice=" + gemPrice + "\t gemReward=" +
  				gemReward + "\t change=" + currencyChange);
  			return false;
  		}
  	}
  	
  	log.info(String.format(
  		"SPENT MONEY(?) ON BOOSTER PACK: free=%s, bPackId=%s, gemPrice=%s, gemReward=%s, itemsUserReceives=%s",
  			freeBoosterPack, bPackId, gemPrice, gemReward, itemsUserReceives));
  	
    Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
    List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
    //sop = source of pieces
    String mfusop = MiscMethods.createUpdateUserMonsterArguments(userId, bPackId,
    		itemsUserReceives, monsterIdToNumPieces, completeUserMonsters, now);
    
    log.info("!!!!!!!!!mfusop=" + mfusop);
    
    //this is if the user bought a complete monster, STORE TO DB THE NEW MONSTERS
    if (!completeUserMonsters.isEmpty()) {
    	List<String> monsterForUserIds = InsertUtils.get()
    			.insertIntoMonsterForUserReturnIds(userId, completeUserMonsters, mfusop, now);
    	List<FullUserMonsterProto> newOrUpdated = MiscMethods. 
    		createFullUserMonsterProtos(
    			monsterForUserIds, completeUserMonsters);
    	
    	log.info("YIIIIPEEEEE!. BOUGHT COMPLETE MONSTER(S)! monster(s)= newOrUpdated" +
    			newOrUpdated + "\t bpackId=" + bPackId);
    	//set the builder that will be sent to the client
    	resBuilder.addAllUpdatedOrNew(newOrUpdated);
    }
    
    //this is if the user did not buy a complete monster, UPDATE DB
    if (!monsterIdToNumPieces.isEmpty()) {
    	//assume things just work while updating user monsters
    	List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils.
    			updateUserMonsters(userId, monsterIdToNumPieces, null,
    				mfusop, now);
    	
    	log.info("YIIIIPEEEEE!. BOUGHT INCOMPLETE MONSTER(S)! monster(s)= newOrUpdated" +
    			newOrUpdated + "\t bpackId=" + bPackId);
    	//set the builder that will be sent to the client
    	resBuilder.addAllUpdatedOrNew(newOrUpdated);
    }

//    if (monsterIdToNumPieces.isEmpty() && completeUserMonsters.isEmpty() &&
//    		gemReward <= 0) {
//    	log.warn("user didn't get any monsters or gems...boosterItemsUserReceives="
//    		+ itemsUserReceives);
//      resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
//      return false;
//    }
    
    return true;
  }
  
  

  
  private void writeToUserCurrencyHistory(User aUser, int packId, Timestamp date,
  		int gemPrice, int previousGems, List<BoosterItem> items, int gemReward,
  		boolean freeBoosterPack) {
	  
	  if (freeBoosterPack) {
		  return;
	  }
	  String userId = aUser.getId();
  	List<Integer> itemIds = new ArrayList<Integer>();
  	for (BoosterItem item : items) {
  		int id = item.getId();
  		itemIds.add(id);
  	}
  	
  	StringBuilder detailSb = new StringBuilder();
  	if (null != items && !items.isEmpty()) {
  		detailSb.append(" bItemIds=");
  		String itemIdsCsv = StringUtils.csvList(itemIds);
  		detailSb.append(itemIdsCsv);
  	}
  	if (gemReward > 0) {
  		detailSb.append(" gemPrice=");
  		detailSb.append(gemPrice);
  		detailSb.append(" gemReward=");
  		detailSb.append(gemReward);
  	}
  	String gems = MiscMethods.gems;
  	String reasonForChange = ControllerConstants.UCHRFC__PURHCASED_BOOSTER_PACK;
  	
  	Map<String, Integer> money = new HashMap<String, Integer>();
  	Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
  	Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> details = new HashMap<String, String>();
    
    int change = (-1 * gemPrice) + gemReward;
    money.put(gems, change);
    previousCurrencies.put(gems, previousGems);
    currentCurrencies.put(gems, aUser.getGems());
    reasonsForChanges.put(gems, reasonForChange);
    details.put(gems, detailSb.toString());
    
    log.info("DETAILS=" + detailSb.toString());
    MiscMethods.writeToUserCurrencyOneUser(userId, date, money, previousCurrencies,
    		currentCurrencies, reasonsForChanges, details);
  }
  
  private void writeToBoosterPackPurchaseHistory(String userId, int boosterPackId,
  		List<BoosterItem> itemsUserReceives, List<FullUserMonsterProto> fumpList,
  		Timestamp timeOfPurchase) {
  	//just assuming there is one Booster Item
  	if (itemsUserReceives.isEmpty()) {
  		return;
  	}
  	BoosterItem bi = itemsUserReceives.get(0);

  	List<String> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(fumpList); 
  	
  	int num = InsertUtils.get().insertIntoBoosterPackPurchaseHistory(userId,
  			boosterPackId, timeOfPurchase, bi, userMonsterIds);
  	
  	log.info("wrote to booster pack history!!!! \t numInserted=" + num +
  			"\t boosterItem=" + itemsUserReceives);
  }
  
  public IList<RareBoosterPurchaseProto> getGoodEquipsRecievedFromBoosterPacks() {
	  return goodEquipsRecievedFromBoosterPacks;
  }
  
  public void setGoodEquipsRecievedFromBoosterPacks(
		  IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks) {
	  this.goodEquipsRecievedFromBoosterPacks = goodEquipsRecievedFromBoosterPacks;
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public TimeUtils getTimeUtils()
  {
	  return timeUtils;
  }

  public void setTimeUtils( TimeUtils timeUtils )
  {
	  this.timeUtils = timeUtils;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }

}
