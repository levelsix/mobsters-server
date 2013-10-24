package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QuestRedeemRequestEvent;
import com.lvl6.events.response.QuestRedeemResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventQuestProto.QuestRedeemRequestProto;
import com.lvl6.proto.EventQuestProto.QuestRedeemResponseProto;
import com.lvl6.proto.EventQuestProto.QuestRedeemResponseProto.Builder;
import com.lvl6.proto.EventQuestProto.QuestRedeemResponseProto.QuestRedeemStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.QuestUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class QuestRedeemController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public QuestRedeemController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new QuestRedeemRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_QUEST_REDEEM_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    QuestRedeemRequestProto reqProto = ((QuestRedeemRequestEvent)event).getQuestRedeemRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int questId = reqProto.getQuestId();

    Timestamp now = new Timestamp((new Date()).getTime());
    
    QuestRedeemResponseProto.Builder resBuilder = QuestRedeemResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      QuestForUser userQuest = RetrieveUtils.questForUserRetrieveUtils().getSpecificUnredeemedUserQuest(userId, questId);
      Quest quest = QuestRetrieveUtils.getQuestForQuestId(questId);
      boolean legitRedeem = checkLegitRedeem(resBuilder, userQuest, quest);

      if (legitRedeem) {
      	
      	//calculate the available quests for this user
      	setAvailableQuests(userId, questId, resBuilder);
        
        //give user the monster reward, if any, and send this to the client
      	legitRedeem = awardMonsterReward(userId, quest, resBuilder);
      }
      
      QuestRedeemResponseEvent resEvent = new QuestRedeemResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setQuestRedeemResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

      if (legitRedeem) {
        User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
        int previousSilver = user.getCoins();
        int previousGold = user.getDiamonds();
        
        Map<String, Integer> money = new HashMap<String, Integer>();
        writeChangesToDB(userQuest, quest, user, senderProto, money);
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, money, previousSilver, previousGold, now);
      }
    } catch (Exception e) {
      log.error("exception in QuestRedeem processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
    	  QuestRedeemResponseEvent resEvent = new QuestRedeemResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setQuestRedeemResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in BeginDungeonController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }


  private boolean checkLegitRedeem(Builder resBuilder, QuestForUser userQuest, Quest quest) {
    if (userQuest == null || userQuest.isRedeemed()) {
      resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
      log.error("user quest is null or redeemed already. userQuest=" + userQuest);
      return false;
    }
    if (!userQuest.isComplete()) {
      resBuilder.setStatus(QuestRedeemStatus.FAIL_NOT_COMPLETE);
      log.error("user quest is not complete");
      return false;
    }
    resBuilder.setStatus(QuestRedeemStatus.SUCCESS);
    return true;  
  }

  private void setAvailableQuests(int userId, int questId, Builder resBuilder) {
  	List<QuestForUser> inProgressAndRedeemedQuestForUsers = RetrieveUtils
  			.questForUserRetrieveUtils().getUserQuestsForUser(userId);
    List<Integer> inProgressQuestIds = new ArrayList<Integer>();
    List<Integer> redeemedQuestIds = new ArrayList<Integer>();
    
    if (inProgressAndRedeemedQuestForUsers != null) {
      for (QuestForUser uq : inProgressAndRedeemedQuestForUsers) {
        if (uq.isRedeemed() || uq.getQuestId() == questId) {
          redeemedQuestIds.add(uq.getQuestId());
        } else {
          inProgressQuestIds.add(uq.getQuestId());  
        }
      }
      Map<Integer, Quest> questIdsToQuests = QuestRetrieveUtils.getQuestIdsToQuests();
      List<Integer> availableQuestIds = QuestUtils.getAvailableQuestsForUser(redeemedQuestIds, inProgressQuestIds);
      for (Integer availableQuestId : availableQuestIds) {
        Quest q = questIdsToQuests.get(availableQuestId);
        if (q.getQuestsRequiredForThis().contains(questId)) {
          resBuilder.addNewlyAvailableQuests(CreateInfoProtoUtils.createFullQuestProtoFromQuest(q));
        }
      }
    }
  }
  
  private boolean awardMonsterReward(int userId, Quest quest, Builder resBuilder) {
  	boolean legitRedeem = true;
  	
  	int monsterIdReward = quest.getMonsterIdReward();
    if (monsterIdReward > 0) {
    	//WHEN GIVING USER A MONSTER, CALL MonsterStuffUtils.updateUserMonsters(...)
    	Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
    	monsterIdToNumPieces.put(monsterIdReward, 1);
    	
    	List<FullUserMonsterProto> reward = MonsterStuffUtils
    			.updateUserMonsters(userId, monsterIdToNumPieces);
    	
      if (reward.isEmpty()) {
        resBuilder.setStatus(QuestRedeemStatus.FAIL_OTHER);
        log.error("problem with giving user 1 monster after completing the quest, monsterId=" 
            + monsterIdReward + ", quest= " + quest);
        legitRedeem = false;
      } else {
      	FullUserMonsterProto fump = reward.get(0);
        resBuilder.setFump(fump);
      }
    }
    
    return legitRedeem;
  }

  private void writeChangesToDB(QuestForUser userQuest, Quest quest, User user, MinimumUserProto senderProto,
      Map<String, Integer> money) {
    if (!UpdateUtils.get().updateRedeemQuestForUser(userQuest.getUserId(), userQuest.getQuestId())) {
      log.error("problem with marking user quest as redeemed. questId=" + userQuest.getQuestId());
    }

    int coinsGained = Math.max(0, quest.getCoinReward());
    int diamondsGained = Math.max(0, quest.getDiamondReward());
    int expGained = Math.max(0,  quest.getExpReward());
    if (!user.updateRelativeDiamondsCoinsExperienceNaive(diamondsGained, coinsGained, expGained)) {
      log.error("problem with giving user " + diamondsGained + " diamonds, " + coinsGained
          + " coins, " + expGained + " exp");
    } else {
      //things worked
      if (0 != diamondsGained) {
        money.put(MiscMethods.gold, diamondsGained);
      }
      if (0 != coinsGained) {
        money.put(MiscMethods.silver, coinsGained);
      }
    }
  }

  public void writeToUserCurrencyHistory(User aUser, Map<String, Integer> money,
      int previousSilver, int previousGold, Timestamp date) {

    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    String gold = MiscMethods.gold;
    String silver = MiscMethods.silver;
    String reasonForChange = ControllerConstants.UCHRFC__QUEST_REDEEM;

    previousGoldSilver.put(gold, previousGold);
    previousGoldSilver.put(silver, previousSilver);
    reasonsForChanges.put(gold, reasonForChange);
    reasonsForChanges.put(silver, reasonForChange);
    
    MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, date, money,
        previousGoldSilver, reasonsForChanges);
  }

}
