package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IList;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PurchaseBoosterPackRequestEvent;
import com.lvl6.events.response.PurchaseBoosterPackResponseEvent;
import com.lvl6.events.response.ReceivedRareBoosterPurchaseResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackRequestProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.Builder;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.PurchaseBoosterPackStatus;
import com.lvl6.proto.EventBoosterPackProto.ReceivedRareBoosterPurchaseResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserBoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.EventWriter;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class PurchaseBoosterPackController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public static int BOOSTER_PURCHASES_MAX_SIZE = 50;

  @Resource
  protected EventWriter eventWriter;

  public EventWriter getEventWriter() {
    return eventWriter;
  }

  public void setEventWriter(EventWriter eventWriter) {
    this.eventWriter = eventWriter;
  }

  
	@Resource(name = "goodEquipsRecievedFromBoosterPacks")
	protected IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks;
	public IList<RareBoosterPurchaseProto> getGoodEquipsRecievedFromBoosterPacks() {
		return goodEquipsRecievedFromBoosterPacks;
	}

	public void setGoodEquipsRecievedFromBoosterPacks(
			IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks) {
		this.goodEquipsRecievedFromBoosterPacks = goodEquipsRecievedFromBoosterPacks;
	}
  
  
  
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
    int boosterPackId = reqProto.getBoosterPackId();
    Date now = new Date(reqProto.getClientTime());
    Timestamp nowTimestamp = new Timestamp(now.getTime());

    PurchaseBoosterPackResponseProto.Builder resBuilder = PurchaseBoosterPackResponseProto.newBuilder();
    resBuilder.setSender(senderProto);


    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      int userId = senderProto.getUserId();
      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      int previousGems = 0;
      BoosterPack aPack = BoosterPackRetrieveUtils.getBoosterPackForBoosterPackId(boosterPackId);
      int gemPrice = aPack.getGemPrice();
      Map<Integer, BoosterItem> boosterItemIdsToBoosterItems = BoosterItemRetrieveUtils.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);

      //values to send to client
      List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
      
      boolean legit = checkLegitPurchase(resBuilder, user, userId, now,
          aPack, boosterPackId, gemPrice, boosterItemIdsToBoosterItems);

      boolean successful = false;
      if (legit) {
        //check if user has bought up all the booster items in the booster pack
        int numBoosterItemsUserWants = 1;
        List<BoosterItem> items = determineBoosterItemsUserReceives(
        		numBoosterItemsUserWants, boosterItemIdsToBoosterItems);
        
        previousGems = user.getGems();
        successful = writeChangesToDB(resBuilder, user, boosterPackId,
        		itemsUserReceives, gemPrice, now);
      }
      
//      if (successful) {
//        UserBoosterPackProto aUserBoosterPackProto = 
//            CreateInfoProtoUtils.createUserBoosterPackProto(boosterPackId, userId, newBoosterItemIdsToNumCollected);
//        
//        resBuilder.addAllMonsterForUsers(fullMonsterForUserProtos);
//        resBuilder.setUserBoosterPack(aUserBoosterPackProto);
//      }
      
      PurchaseBoosterPackResponseProto resProto = resBuilder.build();
      PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setPurchaseBoosterPackResponseProto(resProto);
      server.writeEvent(resEvent);
      
      if (successful) {
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        
        int numBought = itemsUserReceives.size();
        
        //multiple scenarios where user can "buy" booster pack, but,
        //so far, the other ones aren't counted towards the daily limit
        boolean excludeFromLimitCheck = false;
//        
//        MiscMethods.writeToUserBoosterPackHistoryOneUser(userId, boosterPackId, numBought, 
//            nowTimestamp, itemsUserReceives, excludeFromLimitCheck, userEquipIds);
//        writeToUserCurrencyHistory(user, boosterPackId, nowTimestamp,
//            gemChange, previousSilver, previousGems);
//        
        sendBoosterPurchaseMessage(user, aPack, itemsUserReceives);
      }
    } catch (Exception e) {
      log.error("exception in PurchaseBoosterPackController processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName()); 
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
  
  private boolean checkLegitPurchase(Builder resBuilder, User aUser, int userId, 
      Date now, BoosterPack aPack, int boosterPackId, int gemPrice,
      Map<Integer, BoosterItem> idsToBoosterItems) {
    
    if (null == aUser || null == aPack || null == idsToBoosterItems ||
    		idsToBoosterItems.isEmpty()) {
      resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
      log.error("no user for id: " + userId + ", or no BoosterPack for id: " +
      boosterPackId + ", or no booster items for BoosterPack id. items=" +
      		idsToBoosterItems);
      return false;
    }

    int userGems = aUser.getGems();
    //check if user can afford to buy however many more user wants to buy
    if (userGems < gemPrice) {
    	resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_INSUFFICIENT_GEMS);
      return false; //resBuilder status set in called function 
    }
    
    resBuilder.setStatus(PurchaseBoosterPackStatus.SUCCESS);
    return true;
  }
  
  private int getNumEquipsPurchasedToday(int userId, int boosterPackId, 
      DateTime startOfDayInLA) {
    //get the time at the start of the day in UTC
    DateTimeZone utcTZ = DateTimeZone.UTC;
    DateTime startOfDayInLAInUtc = startOfDayInLA.withZone(utcTZ);
    Timestamp startTime = new Timestamp(startOfDayInLAInUtc.toDate().getTime());

    int numPurchased = UserBoosterPackRetrieveUtils
        .getNumPacksPurchasedAfterDateForUserAndPackId(userId, boosterPackId, startTime);
    
    return numPurchased;
  }
  
  //Returns all the booster items the user purchased and whether or not the use reset the chesst.
  //If the user buys out deck start over from a fresh deck 
  //(boosterItemIdsToNumCollected is changed to reflect none have been collected).
  //Also, keep track of which items were purchased before and/or after the reset (via collectedBeforeReset)
  private void getAllBoosterItemsForUser(Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems, 
      int numBoosterItemsUserWants, User aUser, BoosterPack aPack, List<BoosterItem> returnValue) {
    int boosterPackId = aPack.getId();
    
    //set the booster item(s) the user will receieve  
    List<BoosterItem> itemUserReceives = determineBoosterItemsUserReceives(
    		numBoosterItemsUserWants, allBoosterItemIdsToBoosterItems);
    returnValue.addAll(itemUserReceives);
  }
  
  
  //no arguments are modified
  private List<BoosterItem> determineBoosterItemsUserReceives(int amountUserWantsToPurchase,
      Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems) {
    //return value
    List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
    
    Random rand = new Random();
    Collection<BoosterItem> items = allBoosterItemIdsToBoosterItems.values();
    List<BoosterItem> itemsList = new ArrayList<BoosterItem>(items);
    int size = itemsList.size();
    float sumOfProbabilities = sumProbabilities(allBoosterItemIdsToBoosterItems.values());    
    
    //selecting items at random with replacement
    for(int purchaseN = 0; purchaseN < amountUserWantsToPurchase; purchaseN++) {
    	float normalizedProbabilitySoFar = 0f;
      float randFloat = rand.nextFloat();
      
      //for each item, nrmalize 
      for(int i = 0; i < size; i++) {
        BoosterItem item = itemsList.get(i);
        //normalize probability
        normalizedProbabilitySoFar += item.getChanceToAppear() / sumOfProbabilities;
        
        if(randFloat < normalizedProbabilitySoFar) {
          //we have a winner! current boosterItem is what the user gets
          itemsUserReceives.add(item);
          
          break;
        }
      }
    }
    
    return itemsUserReceives;
  }
  
  private float sumProbabilities(Collection<BoosterItem> boosterItems) {
  	float sumOfProbabilities = 0.0f;
  	for (BoosterItem bi : boosterItems) {
  		sumOfProbabilities += bi.getChanceToAppear();
  	}
  	return sumOfProbabilities;
  }
  
  private boolean writeChangesToDB(Builder resBuilder, User user, int bPackId,
      List<BoosterItem> itemsUserReceives, int gemPrice, Date now) {
  	
    //update user, user_monsters
  	int userId = user.getId();
  	//user received the correct number of equips
  	int currencyChange = -1 * gemPrice; //should be negative
  	
  	
  	//update user's money
  	if (!user.updateRelativeDiamondsNaive(currencyChange)) {
  		log.error("could not change user's money. gemPrice=" + gemPrice);
  		return false;
  	}
  	
  	//assuming things work while updating user monsters
    //sop = source of pieces
    Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
    String mfusop = createUpdateUserMonsterArguments(bPackId, itemsUserReceives,
    		monsterIdToNumPieces);
    List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils.
  			updateUserMonsters(userId, monsterIdToNumPieces, mfusop, now);

    if (null == newOrUpdated || newOrUpdated.isEmpty()) {
      resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
      return false;
    }
    
    
    return true;
  }
  
  //monsterIdsToNumPieces will be populated
  private String createUpdateUserMonsterArguments(int boosterPackId,
  		List<BoosterItem> boosterItems, Map<Integer, Integer> monsterIdsToNumPieces) {
  	StringBuffer sb = new StringBuffer();
  	sb.append(ControllerConstants.MFUSOP__BOOSTER_PACK);
  	sb.append(" ");
  	sb.append(boosterPackId);
  	sb.append(" boosterItemIds ");
  	
  	List<Integer> boosterItemIds = new ArrayList<Integer>();
  	for (BoosterItem item : boosterItems) {
  		Integer id = item.getId();
  		Integer numPieces = item.getNumPieces();
  		
  		monsterIdsToNumPieces.put(id, numPieces);
  		boosterItemIds.add(id);
  	}
  	
  	String boosterItemIdsStr = StringUtils.csvList(boosterItemIds);
  	sb.append(boosterItemIdsStr);
  	return sb.toString();
  }

  
  private List<FullUserMonsterProto> createFullUserMonsterProtos(List<Long> userEquipIds, 
      int userId, List<BoosterItem> boosterItems) {
    List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();
    
//    for(int i = 0; i < boosterItems.size(); i++) {
//      long ueId = userEquipIds.get(i);
//      BoosterItem bi = boosterItems.get(i);
//      int equipId = bi.getEquipId();
//      int level = ControllerConstants.DEFAULT_USER_EQUIP_LEVEL;
//      int enhancement = ControllerConstants.DEFAULT_USER_EQUIP_ENHANCEMENT_PERCENT; 
//      int durability = ControllerConstants.DEFAULT_USER_EQUIP_DURABILITY;
//      MonsterForUser ue = new MonsterForUser(ueId, userId, equipId, level, enhancement,
//      		durability);
//      
//      FullUserMonsterProto fuep = CreateInfoProtoUtils.createFullUserMonsterProtoFromMonsterForUser(ue);
//      protos.add(fuep);
//    }
    
    return protos;
  }
  
  private void writeToUserCurrencyHistory(User aUser, int packId, Timestamp date, Map<String, Integer> money,
      int previousSilver, int previousGold) {
//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String gold = MiscMethods.gold;
//    String silver = MiscMethods.silver;
//    String reasonForChange = ControllerConstants.UCHRFC__PURHCASED_BOOSTER_PACK + packId;
//    
//    previousGoldSilver.put(gold, previousGold);
//    previousGoldSilver.put(silver, previousSilver);
//    reasonsForChanges.put(gold, reasonForChange);
//    reasonsForChanges.put(silver, reasonForChange);
//    MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, date, money, previousGoldSilver, reasonsForChanges);
  }
  
}
