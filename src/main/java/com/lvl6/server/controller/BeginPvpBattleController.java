package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginPvpBattleRequestEvent;
import com.lvl6.events.response.BeginPvpBattleResponseEvent;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleRequestProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto.BeginPvpBattleStatus;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.OfflinePvpUser;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class BeginPvpBattleController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;
  

  public BeginPvpBattleController() {
    numAllocatedThreads = 7;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new BeginPvpBattleRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_BEGIN_PVP_BATTLE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    BeginPvpBattleRequestProto reqProto = ((BeginPvpBattleRequestEvent)event).getBeginPvpBattleRequestProto();
//    log.info("reqProto=" + reqProto);

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int senderElo = reqProto.getSenderElo();
    int userId = senderProto.getUserId();
    Timestamp curTime = new Timestamp(reqProto.getAttackStartTime());
    Date curDate = new Date(curTime.getTime());
    PvpProto enemyProto = reqProto.getEnemy();
    int enemyUserId = enemyProto.getDefender().getMinUserProto().getUserId();
    
    //set some values to send to the client (the response proto)
    BeginPvpBattleResponseProto.Builder resBuilder = BeginPvpBattleResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(BeginPvpBattleStatus.FAIL_OTHER); //default
    BeginPvpBattleResponseEvent resEvent = new BeginPvpBattleResponseEvent(userId);
    resEvent.setTag(event.getTag());

    //lock the user that client is going to attack, in order to prevent others from
    //attacking same guy, only lock a real user
    if (0 != enemyUserId) {
    	getHazelcastPvpUtil().lockPlayer(enemyUserId, this.getClass().getSimpleName());
    }
    try {
    	OfflinePvpUser enemy = getHazelcastPvpUtil().getOfflinePvpUser(enemyUserId);
    	boolean legit = checkLegit(resBuilder, enemy, enemyUserId, enemyProto, curDate);

    	boolean successful = false;
    	if(legit) {
    		//since enemy exists, update the OfflinePvpUser's inBattleShieldEndTime
    		//record that the attacker is attacking the user
    		//calculateEloChange() will populate attackerEloChange and defenderEloChange
    		//the first values in both lists will be when attacker wins
    		//second values will be when attacker loses
    		List<Integer> attackerEloChange = new ArrayList<Integer>();
    		List<Integer> defenderEloChange = new ArrayList<Integer>();
    		calculateEloChange(senderElo, enemyProto, attackerEloChange, defenderEloChange);

    		successful = writeChangesToDb(userId, enemyUserId, enemyProto, enemy,
    				attackerEloChange, defenderEloChange, curTime);
    	}

    	if (successful) {
    		resBuilder.setStatus(BeginPvpBattleStatus.SUCCESS);
    	}

    	resEvent.setBeginPvpBattleResponseProto(resBuilder.build());
    	server.writeEvent(resEvent);

    } catch (Exception e) {
      log.error("exception in BeginPvpBattleController processEvent", e);
      //don't let the client hang
      try {
    	  resEvent.setBeginPvpBattleResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in BeginPvpBattleController processEvent", e);
      }
      
    } finally {
    	if (0 != enemyUserId) {
    		//only unlock if real user
    		getHazelcastPvpUtil().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, OfflinePvpUser enemy, int enemyUserId,
  		PvpProto enemyProto, Date curDate) {
  	
  	if (0 == enemyUserId) {
  		//if fake user, just allow this to happen
  		return true;
  	}
    if (null == enemy) {
      log.error("unexpected error: enemy is null. enemyUserId=" + enemyUserId + 
      		"\t enemyProto="+ enemyProto);
      return false;
    }
    
    //check the shield times just to make sure this user is still attackable
    //his shield end times should be in the past
    //this is possible if another attacker got to this person first
    Date shieldEndTime = enemy.getShieldEndTime();
    Date inBattleEndTime = enemy.getInBattleEndTime();
    
    if (shieldEndTime.getTime() > curDate.getTime() ||
    		inBattleEndTime.getTime() > curDate.getTime()) {
    	resBuilder.setStatus(BeginPvpBattleStatus.FAIL_ENEMY_UNAVAILABLE); 
    	log.warn("The offline user this client wants to attack has already been atttacked" +
    			" or is being attacked. offlineUser=" + enemy + "\t curDate" + curDate);
    	return false;
    }
    
    return true;
  }
  
  //fills up the lists attackerEloChange, defenderEloChange
  private void calculateEloChange(int attackerElo, PvpProto defenderProto, 
  		List<Integer> attackerEloChange, List<Integer> defenderEloChange) {
  	
  	//TODO: calculate the actual values! And account for fake users!
  	//case where attacker wins
  	int attackerWinEloChange = attackerElo + 10;
  	int defenderElo = defenderProto.getCurElo();
  	int defenderLoseEloChange = Math.min(0, defenderElo - 10);
  	
  	
  	//case where attacker loses
  	int attackerLoseEloChange = Math.min(0, attackerElo - 10);
  	int defenderWinEloChange = defenderElo + 10;
  	
  	
  	//values are ordered by attacker win then attacker loses
  	attackerEloChange.add(attackerWinEloChange);
  	attackerEloChange.add(attackerLoseEloChange);
  	
  	defenderEloChange.add(defenderLoseEloChange);
  	defenderEloChange.add(defenderWinEloChange);
  }
  
  //the first values in both lists will be when attacker wins
	//second values will be when attacker loses
  private boolean writeChangesToDb(int attackerId, int defenderId, PvpProto enemyProto,
		  OfflinePvpUser enemy, List<Integer> attackerEloChange,
		  List<Integer> defenderEloChange, Timestamp clientTime) {
	  
	  //record that attacker is attacking defender
  	int attackerWinEloChange = attackerEloChange.get(0);
  	int defenderLoseEloChange = defenderEloChange.get(0);
  	int attackerLoseEloChange = attackerEloChange.get(1);
  	int defenderWinEloChange = defenderEloChange.get(1);

  	log.info("inserting into PvpBattleForUser");
  	int numInserted = InsertUtils.get().insertUpdatePvpBattleForUser(attackerId,
  			defenderId, attackerWinEloChange, defenderLoseEloChange, attackerLoseEloChange,
  			defenderWinEloChange, clientTime);
  			
  	log.info("numInserted (should be 1): " + numInserted); 
  			
  	//for defender, update the inBattleShieldEndTime but don't persist to db because
  	//A) If defender is still online after attacker finishes the battle
  	//1) attacker loses, but defender can't be attacked again until defender logs out
  	//2) attacker wins, in which case defender's shield would be activated and so can't
  	//be attacked until shield ends even if defender logs out
  	
  	//B) If defender not online after attacker finishes the battle
  	//1) attacker loses, in which case defender can be attacked again, so revert
  	// inBattleShieldEndTime to original time before attacker attacked defender
  	//2) attacker wins, in which case defender's shield is activated
	  
  	//ACCOUNTING FOR FAKE DEFENDERS!
  	if (0 != defenderId) {
  		//assume that the longest a battle can go for is one hour from now
  		//so other users can't attack this person for one hour
  		long nowMillis = clientTime.getTime();
  		Date newInBattleEndTime = new Date(nowMillis + ControllerConstants.PVP__MAX_BATTLE_DURATION_MILLIS);
  		enemy.setInBattleEndTime(newInBattleEndTime);
  		getHazelcastPvpUtil().updateOfflinePvpUser(enemy);
  		
  		User defender = RetrieveUtils.userRetrieveUtils().getUserById(defenderId);
  		defender.updateInBattleEndTime(newInBattleEndTime);
  	}
  	
	  return true;
  }

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}
  
}
