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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IList;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PurchaseBoosterPackRequestEvent;
import com.lvl6.events.response.PurchaseBoosterPackResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.Monster;
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
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.EventWriter;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
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

    //values to send to client
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

      List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
      int gemReward = 0;
      
      boolean legit = checkLegitPurchase(resBuilder, user, userId, now,
          aPack, boosterPackId, gemPrice, boosterItemIdsToBoosterItems);

      boolean successful = false;
      if (legit) {
      	previousGems = user.getGems();

      	int numBoosterItemsUserWants = 1;
        itemsUserReceives = determineBoosterItemsUserReceives(
        		numBoosterItemsUserWants, boosterItemIdsToBoosterItems);
        
        gemReward = determineGemReward(itemsUserReceives);
        //set the FullUserMonsterProtos (in resBuilder) to send to the client
        successful = writeChangesToDB(resBuilder, user, boosterPackId,
        		itemsUserReceives, gemPrice, now, gemReward);
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
      PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setPurchaseBoosterPackResponseProto(resProto);
      server.writeEvent(resEvent);
      
      if (successful) {
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, boosterPackId, nowTimestamp,
            gemPrice, previousGems, itemsUserReceives, gemReward);
        
        //just assume user can only buy one booster pack at a time
        writeToBoosterPackPurchaseHistory(userId, boosterPackId, itemsUserReceives,
        		resBuilder.getUpdatedOrNewList(), nowTimestamp);
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
  
  //no arguments are modified
  private List<BoosterItem> determineBoosterItemsUserReceives(int amountUserWantsToPurchase,
      Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems) {
    //return value
    List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
    
    Collection<BoosterItem> items = allBoosterItemIdsToBoosterItems.values();
    List<BoosterItem> itemsList = new ArrayList<BoosterItem>(items);
    float sumOfProbabilities = sumProbabilities(allBoosterItemIdsToBoosterItems.values());    
    
    //selecting items at random with replacement
    for(int purchaseN = 0; purchaseN < amountUserWantsToPurchase; purchaseN++) {
    	BoosterItem bi = selectBoosterItem(itemsList, sumOfProbabilities);
    	if (null == bi) {
    		continue;
    	}
    	itemsUserReceives.add(bi);
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
  
  private BoosterItem selectBoosterItem(List<BoosterItem> itemsList,
  		float sumOfProbabilities) {
  	Random rand = new Random();
  	float normalizedProbabilitySoFar = 0f;
    float randFloat = rand.nextFloat();
    
    int size = itemsList.size();
    //for each item, normalize before seeing if it is selected
    for(int i = 0; i < size; i++) {
      BoosterItem item = itemsList.get(i);
      //normalize probability
      normalizedProbabilitySoFar += item.getChanceToAppear() / sumOfProbabilities;
      
      if(randFloat < normalizedProbabilitySoFar) {
        //we have a winner! current boosterItem is what the user gets
        return item;
      }
    }

    log.error("maybe no boosterItems exist. boosterItems=" + itemsList);
    return null;
  }

  private int determineGemReward(List<BoosterItem> boosterItems) {
  	int gemReward = 0;
  	for (BoosterItem bi : boosterItems) {
  		gemReward += bi.getGemReward();
  	}
  	
  	return gemReward;
  }
  
  private boolean writeChangesToDB(Builder resBuilder, User user, int bPackId,
      List<BoosterItem> itemsUserReceives, int gemPrice, Date now, int gemReward) {
  	
    //update user, user_monsters
  	int userId = user.getId();
  	int currencyChange = -1 * gemPrice; //should be negative
  	currencyChange += gemReward;
  	
  	//update user's money
  	if (!user.updateRelativeDiamondsNaive(currencyChange)) {
  		log.error("could not change user's money. gemPrice=" + gemPrice + "\t gemReward=" +
  				gemReward + "\t change=" + currencyChange);
  		return false;
  	}
  	
  	log.info("SPENT MONEY ON BOOSTER PACK: " + bPackId + "\t gemPrice=" + gemPrice +
  			"\t gemReward=" + gemReward + "\t itemsUserReceives=" + itemsUserReceives);
  	
    Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
    List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
    //sop = source of pieces
    String mfusop = createUpdateUserMonsterArguments(userId, bPackId,
    		itemsUserReceives, monsterIdToNumPieces, completeUserMonsters, now);
    
    //this is if the user bought a complete monster, STORE TO DB THE NEW MONSTERS
    if (!completeUserMonsters.isEmpty()) {
    	List<Long> monsterForUserIds = InsertUtils.get()
    			.insertIntoMonsterForUserReturnIds(userId, completeUserMonsters, mfusop);
    	List<FullUserMonsterProto> newOrUpdated = createFullUserMonsterProtos(
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
    			updateUserMonsters(userId, monsterIdToNumPieces, mfusop, now);
    	
    	log.info("YIIIIPEEEEE!. BOUGHT INCOMPLETE MONSTER(S)! monster(s)= newOrUpdated" +
    			newOrUpdated + "\t bpackId=" + bPackId);
    	//set the builder that will be sent to the client
    	resBuilder.addAllUpdatedOrNew(newOrUpdated);
    }

    if (monsterIdToNumPieces.isEmpty() && completeUserMonsters.isEmpty()) {
      resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
      return false;
    }
    
    return true;
  }
  
  //monsterIdsToNumPieces or completeUserMonsters will be populated
  private String createUpdateUserMonsterArguments(int userId, int boosterPackId,
  		List<BoosterItem> boosterItems, Map<Integer, Integer> monsterIdsToNumPieces,
  		List<MonsterForUser> completeUserMonsters, Date now) {
  	StringBuffer sb = new StringBuffer();
  	sb.append(ControllerConstants.MFUSOP__BOOSTER_PACK);
  	sb.append(" ");
  	sb.append(boosterPackId);
  	sb.append(" boosterItemIds ");
  	
  	List<Integer> boosterItemIds = new ArrayList<Integer>();
  	for (BoosterItem item : boosterItems) {
  		Integer id = item.getId();
  		Integer monsterId = item.getMonsterId();
  		Integer numPieces = item.getNumPieces();
  		
  		if (item.isComplete()) {
  			//create a "complete" user monster
  			boolean isComplete = true;
  			Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(monsterId);
  			MonsterForUser newUserMonster = MonsterStuffUtils.createNewUserMonster(
  					userId, numPieces, monzter, now, isComplete);
  			
  			//return this monster in the argument list completeUserMonsters, so caller
  			//can use it
  			completeUserMonsters.add(newUserMonster);
  			
  		} else {
  			monsterIdsToNumPieces.put(monsterId, numPieces);
  			boosterItemIds.add(id);
  		}
  	}
  	
  	String boosterItemIdsStr = StringUtils.csvList(boosterItemIds);
  	sb.append(boosterItemIdsStr);
  	return sb.toString();
  }

  
  
  private List<FullUserMonsterProto> createFullUserMonsterProtos(
  		List<Long> userMonsterIds, List<MonsterForUser> mfuList) {
    List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();
    
    for(int i = 0; i < userMonsterIds.size(); i++) {
      long mfuId = userMonsterIds.get(i);
      MonsterForUser mfu = mfuList.get(i);
      mfu.setId(mfuId);
      FullUserMonsterProto fump = CreateInfoProtoUtils
      		.createFullUserMonsterProtoFromUserMonster(mfu);
      protos.add(fump);
    }
    
    return protos;
  }
  
  private void writeToUserCurrencyHistory(User aUser, int packId, Timestamp date,
  		int gemPrice, int previousGems, List<BoosterItem> items, int gemReward) {
  	List<Integer> itemIds = new ArrayList<Integer>();
  	for (BoosterItem item : items) {
  		int id = item.getId();
  		itemIds.add(id);
  	}
  	
  	StringBuffer detailSb = new StringBuffer();
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
    Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> details = new HashMap<String, String>();
    
    int change = (-1 * gemPrice) + gemReward;
    money.put(gems, change);
    previousGemsCash.put(gems, previousGems);
    reasonsForChanges.put(gems, reasonForChange);
    details.put(gems, detailSb.toString());
    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, money,
    		previousGemsCash, reasonsForChanges, details);
  }
  
  private void writeToBoosterPackPurchaseHistory(int userId, int boosterPackId,
  		List<BoosterItem> itemsUserReceives, List<FullUserMonsterProto> fumpList,
  		Timestamp timeOfPurchase) {
  	//just assuming there is one Booster Item
  	if (itemsUserReceives.isEmpty()) {
  		return;
  	}
  	BoosterItem bi = itemsUserReceives.get(0);

  	List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(fumpList); 
  	
  	int num = InsertUtils.get().insertIntoBoosterPackPurchaseHistory(userId,
  			boosterPackId, timeOfPurchase, bi, userMonsterIds);
  	
  	log.info("wrote to booster pack history!!!! \t numInserted=" + num +
  			"\t boosterItem=" + itemsUserReceives);
  }
  
}
