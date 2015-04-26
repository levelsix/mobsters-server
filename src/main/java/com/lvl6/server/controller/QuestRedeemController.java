//package com.lvl6.server.controller;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.events.RequestEvent;
//import com.lvl6.events.request.QuestRedeemRequestEvent;
//import com.lvl6.events.response.QuestRedeemResponseEvent;
//import com.lvl6.events.response.UpdateClientUserResponseEvent;
//import com.lvl6.info.Quest;
//import com.lvl6.info.QuestForUser;
//import com.lvl6.info.User;
//import com.lvl6.misc.MiscMethods;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.proto.EventQuestProto.QuestRedeemRequestProto;
//import com.lvl6.proto.EventQuestProto.QuestRedeemResponseProto;
//import com.lvl6.proto.EventQuestProto.QuestRedeemResponseProto.Builder;
//import com.lvl6.proto.EventQuestProto.QuestRedeemResponseProto.QuestRedeemStatus;
//import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
//import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
//import com.lvl6.server.Locker;
//import com.lvl6.server.controller.utils.MonsterStuffUtils;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.RetrieveUtils;
//import com.lvl6.utils.utilmethods.QuestUtils;
//import com.lvl6.utils.utilmethods.UpdateUtils;
//
//  @Component @DependsOn("gameServer") public class QuestRedeemController extends EventController {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  @Autowired
//  protected Locker locker;
//
//  public QuestRedeemController() {
//    numAllocatedThreads = 4;
//  }
//
//  @Override
//  public RequestEvent createRequestEvent() {
//    return new QuestRedeemRequestEvent();
//  }
//
//  @Override
//  public EventProtocolRequest getEventType() {
//    return EventProtocolRequest.C_QUEST_REDEEM_EVENT;
//  }
//
//  @Override
//  protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
//    QuestRedeemRequestProto reqProto = ((QuestRedeemRequestEvent)event).getQuestRedeemRequestProto();
//
//    MinimumUserProtoWithMaxResources senderResourcesProto = reqProto.getSender();
//    MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
//    int userId = senderProto.getUserUuid();
//    int questId = reqProto.getQuestId();
//    Date currentDate = new Date();
//    Timestamp now = new Timestamp(currentDate.getTime());
//    int maxCash = senderResourcesProto.getMaxCash();
//    int maxOil = senderResourcesProto.getMaxOil();
//    
//    QuestRedeemResponseProto.Builder resBuilder = QuestRedeemResponseProto.newBuilder();
//    resBuilder.setSender(senderResourcesProto);
//    resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
//    resBuilder.setQuestId(questId);
//
//    getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
//    try {
//    	//retrieve whatever is necessary from the db
//    	
//      QuestForUser userQuest = RetrieveUtils.questForUserRetrieveUtils().getSpecificUnredeemedUserQuest(userId, questId);
//      Quest quest = QuestRetrieveUtils.getQuestForQuestId(questId);
//      boolean legitRedeem = checkLegitRedeem(resBuilder, userQuest, quest);
//
//      if (legitRedeem) {
//      	
//      	//calculate the available quests for this user
//      	setAvailableQuests(userId, questId, resBuilder);
//        
//        //give user the monster reward, if any, and send this to the client
//      	legitRedeem = awardMonsterReward(resBuilder, userId, quest, questId, currentDate);
//      }
//      
//      QuestRedeemResponseEvent resEvent = new QuestRedeemResponseEvent(senderProto.getUserUuid());
//      resEvent.setTag(event.getTag());
//      resEvent.setQuestRedeemResponseProto(resBuilder.build());  
//      server.writeEvent(resEvent);
//
//      if (legitRedeem) {
//        User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());
//        
//        Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
//        Map<String, Integer> currencyChange = new HashMap<String, Integer>();
//        writeChangesToDB(userQuest, quest, user, senderProto, maxCash, maxOil,
//        		previousCurrency, currencyChange);
//        //null PvpLeagueFromUser means will pull from hazelcast instead
//        UpdateClientUserResponseEvent resEventUpdate = MiscMethods
//        		.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null);
//        resEventUpdate.setTag(event.getTag());
//        server.writeEvent(resEventUpdate);
//        
//        writeToUserCurrencyHistory(user, userId, questId, currencyChange,
//        		previousCurrency, now);
//      }
//    } catch (Exception e) {
//      log.error("exception in QuestRedeem processEvent", e);
//      //don't let the client hang
//      try {
//    	  resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
//    	  QuestRedeemResponseEvent resEvent = new QuestRedeemResponseEvent(userId);
//    	  resEvent.setTag(event.getTag());
//    	  resEvent.setQuestRedeemResponseProto(resBuilder.build());
//    	  server.writeEvent(resEvent);
//      } catch (Exception e2) {
//    	  log.error("exception2 in QuestRedeem processEvent", e);
//      }
//    } finally {
//      getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());      
//    }
//  }
//
//
//  private boolean checkLegitRedeem(Builder resBuilder, QuestForUser userQuest, Quest quest) {
//    if (userQuest == null || userQuest.isRedeemed()) {
//      resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
//      log.error("user quest is null or redeemed already. userQuest=" + userQuest);
//      return false;
//    }
//    if (!userQuest.isComplete()) {
//      resBuilder.setStatus(QuestRedeemStatus.FAIL_NOT_COMPLETE);
//      log.error("user quest is not complete");
//      return false;
//    }
//    resBuilder.setStatus(QuestRedeemStatus.SUCCESS);
//    return true;  
//  }
//
//  private void setAvailableQuests(int userId, int questId, Builder resBuilder) {
//  	List<QuestForUser> inProgressAndRedeemedQuestForUsers = RetrieveUtils
//  			.questForUserRetrieveUtils().getUserQuestsForUser(userId);
//    List<Integer> inProgressQuestIds = new ArrayList<Integer>();
//    List<Integer> redeemedQuestIds = new ArrayList<Integer>();
//    
//    if (inProgressAndRedeemedQuestForUsers != null) {
//    	//group things into redeemed and unredeemed
//      for (QuestForUser uq : inProgressAndRedeemedQuestForUsers) {
//        if (uq.isRedeemed() || uq.getQuestId() == questId) {
//          redeemedQuestIds.add(uq.getQuestId());
//        } else {
//          inProgressQuestIds.add(uq.getQuestId());  
//        }
//      }
//      List<Integer> availableQuestIds = QuestUtils.getAvailableQuestsForUser(redeemedQuestIds, inProgressQuestIds);
//      
//      //from the available quests, create protos out of the quests that had
//      //the quest user just redeemed as a prerequisite
//      Map<Integer, Quest> questIdsToQuests = QuestRetrieveUtils.getQuestIdsToQuests();
//      for (Integer availableQuestId : availableQuestIds) {
//        Quest q = questIdsToQuests.get(availableQuestId);
//        if (q.getQuestsRequiredForThis().contains(questId)) {
//          resBuilder.addNewlyAvailableQuests(CreateInfoProtoUtils.createFullQuestProtoFromQuest(q));
//        }
//      }
//    }
//  }
//  
//  private boolean awardMonsterReward(Builder resBuilder, int userId,
//  		Quest quest, int questId, Date combineStartDate) {
//  	boolean legitRedeem = true;
//  	
//  	int monsterIdReward = quest.getMonsterIdReward();
//    if (monsterIdReward > 0) {
//    	//WHEN GIVING USER A MONSTER, CALL MonsterStuffUtils.updateUserMonsters(...)
//    	Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
//    	monsterIdToNumPieces.put(monsterIdReward, 1);
//    	
//    	String mfusop = ControllerConstants.MFUSOP__QUEST + questId;
//    	List<FullUserMonsterProto> reward = MonsterStuffUtils
//    			.updateUserMonsters(userId, monsterIdToNumPieces, null,
//    				mfusop, combineStartDate);
//    	
//      if (reward.isEmpty()) {
//        resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
//        log.error("problem with giving user 1 monster after completing the quest, monsterId=" 
//            + monsterIdReward + ", quest= " + quest);
//        legitRedeem = false;
//      } else {
//      	FullUserMonsterProto fump = reward.get(0);
//        resBuilder.setFump(fump);
//      }
//    }
//    
//    return legitRedeem;
//  }
//
//  private void writeChangesToDB(QuestForUser userQuest, Quest quest, User user,
//  		MinimumUserProto senderProto, int maxCash, int maxOil,
//  		Map<String, Integer> previousCurrency, Map<String, Integer> money) {
//    if (!UpdateUtils.get().updateRedeemQuestForUser(userQuest.getUserId(), userQuest.getQuestId())) {
//      log.error("problem with marking user quest as redeemed. questId=" + userQuest.getQuestId());
//    }
//    
//    previousCurrency.put(MiscMethods.gems, user.getGems());
//    previousCurrency.put(MiscMethods.cash, user.getCash());
//    previousCurrency.put(MiscMethods.oil, user.getOil());
//
//    int cashGain = Math.max(0, quest.getCashReward());
//    int oilGain = Math.max(0, quest.getOilReward());
//    int gemsGained = Math.max(0, quest.getGemReward());
//    int expGained = Math.max(0,  quest.getExpReward());
//    
//    int curCash = Math.min(user.getCash(), maxCash); //in case user's cash is more than maxCash
//  	int maxCashUserCanGain = maxCash - curCash; //this is the max cash the user can gain
//  	cashGain = Math.min(maxCashUserCanGain, cashGain);
//  	
//  	int curOil = Math.max(user.getOil(), maxOil);
//  	int maxOilUserCanGain = maxOil - curOil;
//  	oilGain = Math.min(maxOilUserCanGain, oilGain);
//    
//  	if (0 == gemsGained && 0 == cashGain && 0 == expGained && 0 == oilGain) {
//  		log.info("user does not get any gems, cash, or exp from redeeming quest=" + quest +
//  				" because user is maxed out on resources, and quest doesn't given exp nor gems.");
//  		return;
//  	}
//  	
//    if (!user.updateRelativeGemsCashOilExperienceNaive(gemsGained, cashGain, oilGain,
//    		expGained)) {
//      log.error("problem with giving user " + gemsGained + " diamonds, " + cashGain
//          + " cash, " + expGained + " exp, " + oilGain + " oilGain");
//    } else {
//      //things worked
//      if (0 != gemsGained) {
//        money.put(MiscMethods.gems, gemsGained);
//      }
//      if (0 != cashGain) {
//        money.put(MiscMethods.cash, cashGain);
//      }
//      if (0 != oilGain) {
//    	  money.put(MiscMethods.oil, oilGain);
//      }
//    }
//  }
//
//  public void writeToUserCurrencyHistory(User aUser, int userId, int questId,
//		  Map<String, Integer> currencyChange, Map<String, Integer> previousCurrency,
//		  Timestamp curTime) {
//	  
//	  Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
//	  Map<String, String> reasonsForChanges = new HashMap<String, String>();
//	  Map<String, String> detailsMap = new HashMap<String, String>();
//	  String gems = MiscMethods.gems;
//	  String cash = MiscMethods.cash;
//	  String oil = MiscMethods.oil;
//	  
//	  String reason = ControllerConstants.UCHRFC__QUEST_REDEEM;
//	  StringBuilder detailsSb = new StringBuilder();
//	  detailsSb.append("quest redeemed=");
//	  detailsSb.append(questId);
//	  String details = detailsSb.toString();
//
//	  currentCurrency.put(gems, aUser.getGems());
//	  currentCurrency.put(cash, aUser.getCash());
//	  currentCurrency.put(oil, aUser.getOil());
//	  reasonsForChanges.put(gems, reason);
//	  reasonsForChanges.put(cash, reason);
//	  reasonsForChanges.put(oil, reason);
//	  detailsMap.put(gems, details);
//	  detailsMap.put(cash, details);
//	  detailsMap.put(oil, details);
//
//	  MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
//			  previousCurrency, currentCurrency, reasonsForChanges, detailsMap);
//  }
//
//  public Locker getLocker() {
//	  return locker;
//  }
//
//  public void setLocker(Locker locker) {
//	  this.locker = locker;
//  }
//
//}
