package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemMiniJobRequestEvent;
import com.lvl6.events.response.RedeemMiniJobResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MiniJob;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto.Builder;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto.RedeemMiniJobStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;


@Component
public class RedeemMiniJobController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  @Autowired
  protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

  @Autowired
  protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;

  @Autowired
  protected UpdateUtil updateUtil;
  
  public RedeemMiniJobController() {
    numAllocatedThreads = 4;
  }


  @Override
  public RequestEvent createRequestEvent() {
    return new RedeemMiniJobRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_REDEEM_MINI_JOB_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RedeemMiniJobRequestProto reqProto = ((RedeemMiniJobRequestEvent)event).getRedeemMiniJobRequestProto();
    log.info(String.format("reqProto=%s", reqProto));

    MinimumUserProtoWithMaxResources senderResourcesProto =
        reqProto.getSender();
    int maxCash = senderResourcesProto.getMaxCash();
    int maxOil = senderResourcesProto.getMaxOil();
    MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();

    String userId = senderProto.getUserUuid();
    Date now = new Date(reqProto.getClientTime());
    Timestamp clientTime = new Timestamp(reqProto.getClientTime());
    String userMiniJobId = reqProto.getUserMiniJobUuid();

    List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();


    RedeemMiniJobResponseProto.Builder resBuilder = RedeemMiniJobResponseProto.newBuilder();
    resBuilder.setSender(senderResourcesProto);
    resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);

    UUID userUuid = null;
    UUID userMiniJobUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(userId);
      userMiniJobUuid = UUID.fromString(userMiniJobId);

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s, userMiniJobId=%s",
          userId, userMiniJobId), e);
      invalidUuids = true;
    }

    //UUID checks
    if (invalidUuids) {
      resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);
      RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setRedeemMiniJobResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }

    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      //retrieve whatever is necessary from the db
      //TODO: consider only retrieving user if the request is valid
      User user = getUserRetrieveUtils()
          .getUserById(senderProto.getUserUuid());
      List<MiniJobForUser> mjfuList = new ArrayList<MiniJobForUser>();

      Map<String, Integer> userMonsterIdToExpectedHealth =
          new HashMap<String, Integer>();


      boolean legit = checkLegit(resBuilder, userId, user,
          userMiniJobId, mjfuList, umchpList,
          userMonsterIdToExpectedHealth);

      boolean success = false;
      Map<String, Integer> currencyChange = new HashMap<String, Integer>();
      Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
      if (legit) {
        MiniJobForUser mjfu = mjfuList.get(0);
        success = writeChangesToDB(resBuilder, userId, user,
            userMiniJobId, mjfu, now, clientTime, maxCash, maxOil,
            userMonsterIdToExpectedHealth, currencyChange,
            previousCurrency);
      }

      if (success) {
        resBuilder.setStatus(RedeemMiniJobStatus.SUCCESS);
      }

      RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setRedeemMiniJobResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

      if (success) {
        //null PvpLeagueFromUser means will pull from hazelcast instead
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods
            .createUpdateClientUserResponseEventAndUpdateLeaderboard(
                user, null, null);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);

        //TODO: track the MiniJobForUser history
        writeToUserCurrencyHistory(user, userMiniJobId,
            currencyChange, clientTime, previousCurrency);
      }

    } catch (Exception e) {
      log.error("exception in RedeemMiniJobController processEvent", e);
      //don't let the client hang
      try {
        resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);
        RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(userId);
        resEvent.setTag(event.getTag());
        resEvent.setRedeemMiniJobResponseProto(resBuilder.build());
        server.writeEvent(resEvent);
      } catch (Exception e2) {
        log.error("exception2 in RedeemMiniJobController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());      
    }
  }

  //userMonsterIdToExpectedHealth  may be modified
  private boolean checkLegit(Builder resBuilder, String userId, User user,
      String userMiniJobId, List<MiniJobForUser> mjfuList,
      List<UserMonsterCurrentHealthProto> umchpList,
      Map<String, Integer> userMonsterIdToExpectedHealth) {

    Collection<String> userMiniJobIds = Collections.singleton(userMiniJobId);
    Map<String, MiniJobForUser> idToUserMiniJob =
        getMiniJobForUserRetrieveUtil()
        .getSpecificOrAllIdToMiniJobForUser(
            userId, userMiniJobIds);

    if (idToUserMiniJob.isEmpty() || umchpList.isEmpty()) {
      log.error("no UserMiniJob exists with id=" + userMiniJobId +
          "or invalid userMonsterIds (monsters need to be damaged). " +
          " userMonsters=" + umchpList);
      resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
      return false;
    }

    MiniJobForUser mjfu = idToUserMiniJob.get(userMiniJobId);
    if (null == mjfu.getTimeCompleted()) {
      //sanity check
      log.error("MiniJobForUser incomplete: " + mjfu);
      return false;
    }

    //sanity check
    int miniJobId = mjfu.getMiniJobId();
    MiniJob mj = MiniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
    if (null == mj) {
      log.error("no MiniJob exists with id=" + miniJobId +
          "\t invalid MiniJobForUser=" + mjfu);
      resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
      return false;
    }

    List<String> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(
        umchpList, userMonsterIdToExpectedHealth);

    Map<String, MonsterForUser> mfuIdsToUserMonsters = 
        getMonsterForUserRetrieveUtils()
        .getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

    //keep only valid userMonsterIds another sanity check
    if (userMonsterIds.size() != mfuIdsToUserMonsters.size()) {
      log.warn("some userMonsterIds client sent are invalid." +
          " Keeping valid ones. userMonsterIds=" + userMonsterIds +
          " mfuIdsToUserMonsters=" + mfuIdsToUserMonsters);

      //since client sent some invalid monsters, keep only the valid
      //mappings from userMonsterId to health
      Set<String> existing = mfuIdsToUserMonsters.keySet();
      userMonsterIdToExpectedHealth.keySet().retainAll(existing);
    }

    if (userMonsterIds.isEmpty()) {
      log.error("no valid user monster ids sent by client");
      return false;
    }


    mjfuList.add(mjfu);
    return true;
  }


  private boolean writeChangesToDB(Builder resBuilder, String userId,
      User user, String userMiniJobId, MiniJobForUser mjfu, Date now,
      Timestamp clientTime, int maxCash, int maxOil,
      Map<String, Integer> userMonsterIdToExpectedHealth,
      Map<String, Integer> currencyChange,
      Map<String, Integer> previousCurrency) {
    int miniJobId = mjfu.getMiniJobId();
    MiniJob mj = MiniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);

    int prevGems = user.getGems();
    int prevCash = user.getCash();
    int prevOil = user.getOil();

    //update user currency
    int gemsChange = mj.getGemReward();
    int cashChange = mj.getCashReward();
    int oilChange = mj.getOilReward();
    int monsterIdReward = mj.getMonsterIdReward();
    int itemIdReward = mj.getItemIdReward();
    int itemRewardQuantity = mj.getItemRewardQuantity();

    if (!updateUser(user, gemsChange, cashChange, maxCash, oilChange, maxOil)) {
      log.error(String.format(
    	  "could not decrement user gems by %s, cash by %s, and oil by %s",
          gemsChange, cashChange, oilChange));
      return false;
    } else {
      if (0 != gemsChange) {
        currencyChange.put(MiscMethods.gems, gemsChange);
        previousCurrency.put(MiscMethods.gems, prevGems);
      }
      if (0 != cashChange) {
        currencyChange.put(MiscMethods.cash, cashChange);
        previousCurrency.put(MiscMethods.cash, prevCash);
      }
      if (0 != oilChange) {
        currencyChange.put(MiscMethods.oil, oilChange);
        previousCurrency.put(MiscMethods.oil, prevOil);
      }
    }

    //give the user the monster if he got one
    if (0 != monsterIdReward) {
      StringBuilder mfusopB = new StringBuilder();
      mfusopB.append(ControllerConstants.MFUSOP__MINI_JOB);
      mfusopB.append(" ");
      mfusopB.append(miniJobId);
      String mfusop = mfusopB.toString();
      Map<Integer, Integer> monsterIdToNumPieces =
          new HashMap<Integer, Integer>();
      monsterIdToNumPieces.put(monsterIdReward, 1);

      log.info(String.format(
    	  "rewarding user with {monsterId->amount}: %s",
    	  monsterIdToNumPieces));
      List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils.
          updateUserMonsters(userId, monsterIdToNumPieces, null,
              mfusop, now);
      FullUserMonsterProto fump = newOrUpdated.get(0);
      resBuilder.setFump(fump);
    }
    
    if (0 != itemIdReward && 0 != itemRewardQuantity) {
    	int numUpdated = updateUtil.updateItemForUser(
    		userId, itemIdReward, itemRewardQuantity);
    	String preface = "rewarding user with more items."; 
    	log.info(String.format(
    		"%s itemId=%s, \t amount=%s, numUpdated=%s",
    		preface, itemIdReward, itemRewardQuantity, numUpdated));
    }

    //delete the user mini job
    int numDeleted = DeleteUtils.get().deleteMiniJobForUser(userMiniJobId);
    log.info("userMiniJob numDeleted=" + numDeleted);


    log.info("updating user's monsters' healths");
    int numUpdated = updateUtil
        .updateUserMonstersHealth(userMonsterIdToExpectedHealth);
    log.info("numUpdated=" + numUpdated);

    //number updated is based on INSERT ... ON DUPLICATE KEY UPDATE
    //so returns 2 if one row was updated, 1 if inserted
    if (numUpdated > 2 * userMonsterIdToExpectedHealth.size()) {
      log.warn("unexpected error: more than user monsters were" +
          " updated. actual numUpdated=" + numUpdated +
          "expected: userMonsterIdToExpectedHealth=" +
          userMonsterIdToExpectedHealth);
    }

    return true;
  }


  private boolean updateUser(User u, int gemsChange, int cashChange,
      int maxCash, int oilChange, int maxOil) {
    //capping how much the user can gain of a certain resource
    int curCash = Math.min(u.getCash(), maxCash); //in case user's cash is more than maxCash
    int maxCashUserCanGain = maxCash - curCash; //this is the max cash the user can gain
    cashChange = Math.min(maxCashUserCanGain, cashChange);

    int curOil = Math.min(u.getOil(), maxOil); //in case user's oil is more than maxOil
    int maxOilUserCanGain = maxOil - curOil;
    oilChange = Math.min(maxOilUserCanGain, oilChange);

    if (0 == cashChange || 0 == oilChange || 0 == gemsChange) {
      log.info("after caping rewards to max, user gets no resources");
      return true;
    }

    int numChange = u.updateRelativeCashAndOilAndGems(cashChange,
        oilChange, gemsChange);

    if (numChange <= 0) {
      log.error(String.format(
          "could not update user gems, cash, and oil. gemChange=%s, cash=%s, oil=%s, user=%s",
          gemsChange, cashChange, oilChange, u));
      return false;
    }
    return true;
  }


  private void writeToUserCurrencyHistory(User aUser, String userMiniJobId,
      Map<String, Integer> currencyChange, Timestamp curTime,
      Map<String, Integer> previousCurrency) {
    String userId = aUser.getId();
    String reason = ControllerConstants.UCHRFC__SPED_UP_COMPLETE_MINI_JOB;
    StringBuilder detailsSb = new StringBuilder();
    detailsSb.append("userMiniJobId=");
    detailsSb.append(userMiniJobId);

    Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> detailsMap = new HashMap<String, String>();
    String gems = MiscMethods.gems;

    currentCurrency.put(gems, aUser.getGems());
    reasonsForChanges.put(gems, reason);
    detailsMap.put(gems, detailsSb.toString());

    MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
        previousCurrency, currentCurrency, reasonsForChanges, detailsMap);

  }

  public Locker getLocker() {
    return locker;
  }
  public void setLocker(Locker locker) {
    this.locker = locker;
  }

  public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
    return monsterForUserRetrieveUtils;
  }

  public void setMonsterForUserRetrieveUtils(
      MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
    this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
  }


  public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
    return miniJobForUserRetrieveUtil;
  }


  public void setMiniJobForUserRetrieveUtil(
      MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
    this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
  }


  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }


  public UpdateUtil getUpdateUtil()
  {
	  return updateUtil;
  }


  public void setUpdateUtil( UpdateUtil updateUtil )
  {
	  this.updateUtil = updateUtil;
  }

}
