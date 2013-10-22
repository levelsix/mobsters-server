package com.lvl6.server.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class AddMonsterToBattleTeamController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


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

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      //User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);

    	//make sure it exists
    	MonsterForUser mfu = RetrieveUtils.monsterForUserRetrieveUtils().getSpecificUserMonster(userMonsterId);
    	//get the ones that aren't in enhancing or healing
    	Map<Long, MonsterEnhancingForUser> inEnhancing =
    			MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
    	Map<Long, MonsterHealingForUser> inHealing =
    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
    	
      boolean legit = checkLegit(resBuilder, userId, userMonsterId, mfu, inEnhancing, inHealing);

      boolean successful = false;
      if(legit) {
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
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, int userId, long userMonsterId,
		  MonsterForUser mfu, Map<Long, MonsterEnhancingForUser> inEnhancing, 
		  Map<Long, MonsterHealingForUser> inHealing) {
  	
  	if (null == mfu) {
  		log.error("no monster_for_user exists with id=" + userMonsterId);
  		return false;
  	}

  	//check to make sure this is indeed the user's monster
  	int mfuUserId = mfu.getUserId();
  	if (mfuUserId != userId) {
  		log.error("what is this I don't even...client trying to \"equip\" " +
  				"another user's monster. userId=" + userId + "\t monsterForUser=" + mfu);
  		return false;
  	}
  	
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
  
}
