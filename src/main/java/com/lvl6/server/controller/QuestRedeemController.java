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
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.QuestUtils;

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

    boolean legitRedeem = false;
    QuestForUser userQuest = null;
    Quest quest = null;

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      userQuest = RetrieveUtils.questForUserRetrieveUtils().getSpecificUnredeemedUserQuest(userId, questId);
      quest = QuestRetrieveUtils.getQuestForQuestId(questId);
      legitRedeem = checkLegitRedeem(resBuilder, userQuest, quest);

      List<QuestForUser> inProgressAndRedeemedQuestForUsers = null;
      if (legitRedeem) {
      	
      	//calculate the available quests for this user
        inProgressAndRedeemedQuestForUsers = RetrieveUtils.questForUserRetrieveUtils().getUserQuestsForUser(userId);
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
        
        
        
        int monsterIdReward = quest.getMonsterIdReward();
//        if (monsterIdReward > 0) {
//          int userEquipId = InsertUtils.get().insertUserEquip(userQuest.getUserId(), monsterIdReward,
//              ControllerConstants.DEFAULT_USER_EQUIP_LEVEL, ControllerConstants.DEFAULT_USER_EQUIP_ENHANCEMENT_PERCENT,
//              now, ControllerConstants.UER__QUEST_REDEEM);
//          if (userEquipId < 0) {
//            resBuilder.setStatus(QuestRedeemStatus.OTHER_FAIL);
//            log.error("problem with giving user 1 reward equip after completing the quest, equipId=" 
//                + quest.getEquipIdGained() + ", quest= " + quest);
//            legitRedeem = false;
//          } else {
//            resBuilder.setEquipRewardFromQuest(CreateInfoProtoUtils.createFullUserEquipProtoFromUserEquip(
//                new UserEquip(userEquipId, userQuest.getUserId(), quest.getEquipIdGained(), 
//                    ControllerConstants.DEFAULT_USER_EQUIP_LEVEL, ControllerConstants.DEFAULT_USER_EQUIP_ENHANCEMENT_PERCENT,
//                    ControllerConstants.DEFAULT_USER_EQUIP_DURABILITY)));
//          }
//        }
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
    if (legitRedeem && quest != null && userQuest != null) {
      clearQuestForUserData(quest, userQuest);
    }

  }

  private void clearQuestForUserData(Quest quest, QuestForUser userQuest) {
//    if (quest.getTasksRequired() != null && quest.getTasksRequired().size() > 0) {
//      if (!DeleteUtils.get().deleteQuestForUserInfoInTaskProgressAndCompletedTasks(userQuest.getUserId(), userQuest.getQuestId(), quest.getTasksRequired().size())) {
//        log.error("problem with deleting user quest info in user quest task tables. questid=" + userQuest.getQuestId() 
//            + ", num tasks it has is " + quest.getTasksRequired().size());
//      }
//    }
//    List<Integer> defeatTypeJobs = null;
//    defeatTypeJobs = quest.getDefeatGoodGuysJobsRequired();
//    if (defeatTypeJobs != null && defeatTypeJobs.size() > 0) {
//      if (!DeleteUtils.get().deleteQuestForUserInfoInDefeatTypeJobProgressAndCompletedDefeatTypeJobs(userQuest.getUserId(), userQuest.getQuestId(), defeatTypeJobs.size())) {
//        log.error("problem with deleting user quest info for defeat type job tables. questid=" + userQuest.getQuestId() 
//            + ", num defeat type jobs it has is " + defeatTypeJobs.size());
//      }
//    }    
  }

  private void writeChangesToDB(QuestForUser userQuest, Quest quest, User user, MinimumUserProto senderProto,
      Map<String, Integer> money) {
//    if (!UpdateUtils.get().updateRedeemQuestForUser(userQuest.getUserId(), userQuest.getQuestId())) {
//      log.error("problem with marking user quest as redeemed. questId=" + userQuest.getQuestId());
//    }
//
//    int coinsGained = Math.max(0, quest.getCoinsGained());
//    int diamondsGained = Math.max(0, quest.getDiamondsGained());
//    int expGained = Math.max(0,  quest.getExpGained());
//    if (!user.updateRelativeDiamondsCoinsExperienceNaive(diamondsGained, coinsGained, expGained)) {
//      log.error("problem with giving user " + diamondsGained + " diamonds, " + coinsGained
//          + " coins, " + expGained + " exp");
//    } else {
//      //things worked
//      if (0 != diamondsGained) {
//        money.put(MiscMethods.gold, diamondsGained);
//      }
//      if (0 != coinsGained) {
//        money.put(MiscMethods.silver, coinsGained);
//      }
//    }
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

//  private boolean checkIfUserGetsKiipReward() {
//    if (Math.random() < ControllerConstants.CHANCE_TO_GET_KIIP_ON_QUEST_REDEEM) return true;
//    return false;
//  }

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
