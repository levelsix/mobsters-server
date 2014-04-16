package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AddMonsterToBattleTeamRequestEvent;
import com.lvl6.events.response.AddMonsterToBattleTeamResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamRequestProto;
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamResponseProto;
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamResponseProto.AddMonsterToBattleTeamStatus;
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class AddMonsterToBattleTeamController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  public AddMonsterToBattleTeamController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new AddMonsterToBattleTeamRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_ADD_MONSTER_TO_BATTLE_TEAM_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    AddMonsterToBattleTeamRequestProto reqProto = ((AddMonsterToBattleTeamRequestEvent)event).getAddMonsterToBattleTeamRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int teamSlotNum = reqProto.getTeamSlotNum();
    long userMonsterId = reqProto.getUserMonsterId();

    //set some values to send to the client (the response proto)
    AddMonsterToBattleTeamResponseProto.Builder resBuilder = AddMonsterToBattleTeamResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(AddMonsterToBattleTeamStatus.FAIL_OTHER); //default

    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {

    	//make sure it exists
    	Map<Long, MonsterForUser> idsToMonsters = RetrieveUtils.monsterForUserRetrieveUtils()
    			.getSpecificOrAllUserMonstersForUser(userId, null);
//    	//get the ones that aren't in enhancing or healing
//    	Map<Long, MonsterEnhancingForUser> inEnhancing =
//    			MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
//    	Map<Long, MonsterHealingForUser> inHealing =
//    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
    	Map<Long, MonsterEnhancingForUser> inEnhancing =
    			new HashMap<Long, MonsterEnhancingForUser>();
    	Map<Long, MonsterHealingForUser> inHealing =
    			new HashMap<Long, MonsterHealingForUser>(); 
    	
      boolean legit = checkLegit(resBuilder, userId, teamSlotNum, userMonsterId,
      		idsToMonsters, inEnhancing, inHealing);

      boolean successful = false;
      if(legit) {
      	MonsterForUser mfu = idsToMonsters.get(userMonsterId);
    	  successful = writeChangesToDb(userId, teamSlotNum, userMonsterId, mfu);
      }
      
      if (successful) {
    	  resBuilder.setStatus(AddMonsterToBattleTeamStatus.SUCCESS);
      }
      
      AddMonsterToBattleTeamResponseEvent resEvent = new AddMonsterToBattleTeamResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setAddMonsterToBattleTeamResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
//
//      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
//          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
//      resEventUpdate.setTag(event.getTag());
//      server.writeEvent(resEventUpdate);
    } catch (Exception e) {
      log.error("exception in AddMonsterToBattleTeamController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(AddMonsterToBattleTeamStatus.FAIL_OTHER);
    	  AddMonsterToBattleTeamResponseEvent resEvent = new AddMonsterToBattleTeamResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setAddMonsterToBattleTeamResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in AddMonsterToBattleTeamController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, int userId, int teamSlotNum,
  		long userMonsterId, Map<Long, MonsterForUser> idsToMonsters,
  		Map<Long, MonsterEnhancingForUser> inEnhancing, 
		  Map<Long, MonsterHealingForUser> inHealing) {
  	
  	if (!idsToMonsters.containsKey(userMonsterId)) {
  		log.error("no monster_for_user exists with id=" + userMonsterId +
  				" and userId=" + userId);
  		return false;
  	}
  	MonsterForUser mfu = idsToMonsters.get(userMonsterId);

  	//if a monster is already occupying the slot, replace it
  	clearBattleTeamSlot(teamSlotNum, idsToMonsters);
  	
  	//CHECK TO MAKE SURE THE USER MONSTER IS COMPLETE
  	if (!mfu.isComplete()) {
  		log.error("user error: user trying to equip incomplete monster. userId=" +
  				userId + "\t monsterForUser=" + mfu);
  		return false;
  	}
  	
  	//inEnhancing has userMonsterId values for keys
  	//NOT IN ENHANCING
  	if (inEnhancing.containsKey(userMonsterId)) {
  		log.error("user error: user is trying to \"equip\" a monster that is in" +
  				" enhancing." + "\t userId=" + userId + "\t monsterForUser=" + mfu +
  				" inEnhancing=" + inEnhancing);
  		return false;
  	}
  	
  	//inHealing has userMonsterId values for keys
  	//NOT IN HEALING 
  	if (inHealing.containsKey(userMonsterId)) {
  		log.error("user error: user is trying to \"equip\" a monster that is in" +
  				" healing." + "\t userId=" + userId + "\t monsterForUser=" + mfu +
  				" inHealing=" + inHealing);
  		return false;
  	}
  	
  	resBuilder.setStatus(AddMonsterToBattleTeamStatus.SUCCESS);
  	return true;
  }
  
  //if there is a, or are monsters in the map with a teamSlotNum value that equals
  //the argument passed in: teamSlotNum, then remove the existing guys in said slot
  private void clearBattleTeamSlot(int teamSlotNum,
  		Map<Long, MonsterForUser>idsToMonsters) {
  	List<Long> userMonsterIds = new ArrayList<Long>();
  	
  	//gather up the userMonsterIds with a team slot value = teamSlotNum (batch it)
  	for(MonsterForUser mfu : idsToMonsters.values()) {
  		if (mfu.getTeamSlotNum() == teamSlotNum) {
  			log.warn("more than one monster sharing team slot. userMonster=" + mfu); 
  			userMonsterIds.add(mfu.getId());
  		}
  	}
  	
  	if (!userMonsterIds.isEmpty()) {
  		int newTeamSlotNum = 0;
  		//remove these monsters from the team slot with value = teamSlotNum
  		int num = UpdateUtils.get().nullifyMonstersTeamSlotNum(
  				userMonsterIds, newTeamSlotNum);
  		log.warn("removed userMonsterIds from teamSlot=" + teamSlotNum +
  				"\t userMonsterIds=" + userMonsterIds + "\t numRemoved=" + num);
  	}
  	
  }
  
  private boolean writeChangesToDb(int uId, int teamSlotNum, long userMonsterId,
  		MonsterForUser mfu) { 
  	int numUpdated = UpdateUtils.get().updateUserMonsterTeamSlotNum(
  			userMonsterId, teamSlotNum);
  	
  	if (numUpdated == 1) {
  		return true;
  	}
  	log.warn("unexpected error: user monster not updated. " +
  			"actual numUpdated=" + numUpdated + "expected: 1 " +
  			"monsterForUser=" + mfu);
	  return true;
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }
  
}
