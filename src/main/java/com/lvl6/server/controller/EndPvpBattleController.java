package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.ILock;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndPvpBattleRequestEvent;
import com.lvl6.events.response.EndPvpBattleResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.PvpBattleForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventPvpProto.EndPvpBattleRequestProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto.EndPvpBattleStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.OfflinePvpUser;
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component @DependsOn("gameServer") public class EndPvpBattleController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;
  

  public EndPvpBattleController() {
    numAllocatedThreads = 7;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new EndPvpBattleRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_BEGIN_PVP_BATTLE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    EndPvpBattleRequestProto reqProto = ((EndPvpBattleRequestEvent)event).getEndPvpBattleRequestProto();
//    log.info("reqProto=" + reqProto);

    //get values sent from the client (the request proto)
    MinimumUserProtoWithMaxResources senderProtoMaxResources = reqProto.getSender();
    MinimumUserProto senderProto = senderProtoMaxResources.getMinUserProto();
    int attackerId = senderProto.getUserId();
    int defenderId = reqProto.getDefenderId();
    boolean attackerAttacked = reqProto.getUserAttacked();
    boolean attackerWon = reqProto.getUserWon();
    int oilChange = reqProto.getOilChange();
    int cashChange = reqProto.getCashChange();
    
    int attackerMaxOil = senderProtoMaxResources.getMaxOil(); 
    int attackerMaxCash = senderProtoMaxResources.getMaxCash();
    
    Timestamp curTime = new Timestamp(reqProto.getClientTime());
    Date curDate = new Date(curTime.getTime());
    
    //set some values to send to the client (the response proto)
    EndPvpBattleResponseProto.Builder resBuilder = EndPvpBattleResponseProto.newBuilder();
    resBuilder.setSender(senderProtoMaxResources);
    resBuilder.setStatus(EndPvpBattleStatus.FAIL_OTHER); //default
    EndPvpBattleResponseEvent resEvent = new EndPvpBattleResponseEvent(attackerId);
    resEvent.setTag(event.getTag());

    List<Integer> userIds = new ArrayList<Integer>();
    userIds.add(attackerId);
    userIds.add(defenderId); //doesn't matter if fake i.e. enemyUserId=0
//    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    
    //TODO: OVERHAUL THIS LOCKING THING AND MIMIC GameServer.java
    //TODO: NEED TO LOCK BOTH PLAYERS
    ILock lock = getHazelcastPvpUtil().getPvpPlayerLock(defenderId);
    boolean gotLock = false;
    try {
    	User attacker = null;
    	User defender = null;
    	boolean successful = false;
    	
    	if (lock.tryLock(HazelcastPvpUtil.LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
    		gotLock = true;
    		
    		//get whatever from db
    		Map<Integer, User> users = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIds);
    		attacker = users.get(attackerId);
    		defender = users.get(defenderId);
    		PvpBattleForUser pvpBattleInfo = PvpBattleForUserRetrieveUtils
    				.getPvpBattleForUserForAttacker(attackerId);
    		
    		//could be fake user so could be null, enemy could also be null if he is online
    		//if null then no need to update
    		OfflinePvpUser defenderOpu = getHazelcastPvpUtil().getOfflinePvpUser(defenderId);
    		boolean legit = checkLegit(resBuilder, attacker, defender, pvpBattleInfo, curDate);
    		
    		if(legit) {
    			successful = writeChangesToDb(attacker, attackerId, defender, defenderId,
    					defenderOpu, pvpBattleInfo, oilChange, cashChange, curTime, curDate,
    					attackerAttacked, attackerWon, attackerMaxOil, attackerMaxCash);
    		}
    		
    		if (successful) {
    			resBuilder.setStatus(EndPvpBattleStatus.SUCCESS);
    		}
    		
    	} else {
    		log.warn("FAILED TO ACQUIRE LOCK FOR OFFLINE USER: " + defenderId);
    		resBuilder.setStatus(EndPvpBattleStatus.FAIL_OTHER);
    	}
      
    	//respond to the attacker
      resEvent.setEndPvpBattleResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      
      if (successful) {
      	//respond to the defender
      	EndPvpBattleResponseEvent resEventDefender = new EndPvpBattleResponseEvent(defenderId);
      	resEvent.setTag(0);
      	resEventDefender.setEndPvpBattleResponseProto(resBuilder.build());
      	server.writeEvent(resEventDefender);
      	
      	//regardless of whether the attacker won, his elo will change
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(attacker);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);
      	
				//defender's elo and resources changed only if attacker won, and defender is real
      	if (attackerWon && null != defender) {
      		UpdateClientUserResponseEvent resEventUpdateDefender = MiscMethods
        			.createUpdateClientUserResponseEventAndUpdateLeaderboard(defender);
  				resEventUpdate.setTag(event.getTag());
  				server.writeEvent(resEventUpdateDefender);
      	}
      	//TODO: TRACK CURRENCY HISTORY
      }


    } catch (Exception e) {
      log.error("exception in EndPvpBattleController processEvent", e);
      //don't let the client hang
      try {
    	  resEvent.setEndPvpBattleResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in EndPvpBattleController processEvent", e);
      }
      
    } finally {
//      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	if (gotLock) {
    		getHazelcastPvpUtil().unlockPvpPlayer(lock);
    	}
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User attacker, User defender,
  		PvpBattleForUser pvpInfo, Date curDate) {
  	
  	if (null == pvpInfo) {
  		log.error("unexpected error: no battle exists for the attacker. attacker=" +
  				attacker + "\t defender=" + defender);
  		return false;
  	}
  	
  	//if the battle has finished one hour after the battle started, don't count it
  	long nowMillis = curDate.getTime();
  	long battleEndTime = pvpInfo.getBattleStartTime().getTime() +
  			ControllerConstants.PVP__MAX_BATTLE_DURATION_MILLIS;
  	if (nowMillis > battleEndTime) {
  		resBuilder.setStatus(EndPvpBattleStatus.FAIL_BATTLE_TOOK_TOO_LONG);
  		log.error("the client took too long to finish a battle. pvpInfo=" + pvpInfo +
  				"\t now=" + curDate);
  		return false;
  	}
    
//  	if (0 == defenderUserId || !userAttacked) {
//  		//if fake user, or user didn't attack just allow this to happen
//  		return true;
//  	}
  	
    return true;
  }
  
  private boolean writeChangesToDb(User attacker, int attackerId, User defender, 
  		int defenderId,  OfflinePvpUser defenderOpu, PvpBattleForUser pvpBattleInfo, 
		  int oilChange, int cashChange, Timestamp clientTime, Date clientDate,
		  boolean attackerAttacked, boolean attackerWon, int attackerMaxOil, int attackerMaxCash) {
	  
  	if (!attackerAttacked) {
  		//this means that the only thing that changes is defenderOpu's inBattleShieldEndTime
  		//just change it so its not in the future
  		
  		if (null != defender && defenderOpu.getInBattleEndTime().getTime() > clientTime.getTime()) {
  			//since real player and battle end time is after now, change it to now
  			//so defender can be attackable again
  			defenderOpu.setInBattleEndTime(clientDate);
  			getHazelcastPvpUtil().setOfflinePvpUser(defenderOpu);
  		}
  	} else {
  		//user attacked so either he won or lost
  		//so the defender either loses or gains elo
  		
  		int maxAttackerOilChange = calculateMaxOilChange(attacker, attackerMaxOil, oilChange, attackerWon);
  		int maxAttackerCashChange = calculateMaxCashChange(attacker, attackerMaxCash, cashChange, attackerWon);
  		
  		//MAKE SURE THE ATTACKER AND DEFENDER'S ELO DON'T GO BELOW 0
  		//TODO: WHEN MAX ELO IS FIGURED OUT, MAKE SURE ELO DOESN'T GO ABOVE THAT
  		int attackerEloChange = 0;
  		int defenderEloChange = 0;
  		if (attackerWon) {
  			attackerEloChange = pvpBattleInfo.getAttackerWinEloChange(); //positive value
  			defenderEloChange = pvpBattleInfo.getDefenderLoseEloChange(); //negative value
  			
  			//make sure defender's elo doesn't go below 0
  			int defenderElo = defender.getElo();
  			if (defenderElo + defenderEloChange < 0) {
  				defenderEloChange = defenderElo;
  			}
  			
  		} else {
  			attackerEloChange = pvpBattleInfo.getAttackerLoseEloChange(); //negative value
  			defenderEloChange = pvpBattleInfo.getDefenderWinEloChange(); // positive value
  			
  			//make sure attacker's elo doesn't go below 0
  			int attackerElo = attacker.getElo();
  			if (attackerElo + attackerEloChange < 0) {
  				attackerEloChange = attackerElo;
  			}
  			
  		}
  		
  		//update attacker's cash, oil, elo
  		attacker.updateEloOilCash(attackerId, attackerEloChange, maxAttackerOilChange,
  				maxAttackerCashChange);
  		
  		//defender could be fake user
  		if (null != defender) {
  			int defenderElo = defender.getElo();
  			if (defenderElo - defenderEloChange < 0) {
  				defenderEloChange = defenderElo;
  			}
  			//since defender is real, update defender's cash, oil, elo
  			defender.updateEloOilCash(defenderId, defenderEloChange, 0, 0);
  		}
  	}
  	
  	log.info("deleting from PvpBattleForUser");
  	//need to delete PvpBattleForUser
  	int numDeleted = DeleteUtils.get().deletePvpBattleForUser(attackerId);
  	log.info("numInserted (should be 1): " + numDeleted); 
  			
  	
	  return true;
  }
  
  //oilChange is positive number,
  //returns a signed number representing oilChange
  private int calculateMaxOilChange(User user, int maxOil, int oilChange, boolean userWon) {
  	if (null == user) {
  		//this is for fake user
  		return 0;
  	}
  	
  	if (userWon) {
  		//if user somehow has more than max oil, first treat user as having max oil,
  		//figure out the amount he gains (0) and then subtract, the extra oil he had
  		int oilLoss = 0;
  		int userOil = user.getOil();
  		if (userOil > maxOil) {
  			oilLoss = userOil - maxOil;
  		}
  		
  		int curOil = Math.min(user.getOil(), maxOil); //in case user's oil is more than maxOil.
  		int maxOilUserCanGain = maxOil - curOil;
  		int maxOilChange = Math.min(oilChange, maxOilUserCanGain);
  		
  		//IF USER IS ABOVE maxOil, need to drag him down to maxOil
  		return maxOilChange - oilLoss;
  		
  	} else {
  		//if user somehow has more than max oil, first treat user as having max oil,
  		//figure out the max amount he loses, then subtract the extra oil he had
  		int additionalOilLoss = 0;
  		int userOil = user.getOil();
  		if (userOil > maxOil) {
  			log.warn("somehow user has more than maxOil. maxOil=" + maxOil + "\t user=" + user
  					+ "\t oilChange=" + oilChange);
  			additionalOilLoss = userOil - maxOil;
  			userOil = maxOil; 
  		}
  				
  		int maxOilUserCanLose = userOil;
  		int maxOilChange = Math.min(oilChange, maxOilUserCanLose);
  		maxOilChange = maxOilChange + additionalOilLoss;
  		
  		return -1 * maxOilChange;
  	}
  }

  //cashChange is positive number,
  //returns a signed number representing oilChange
  private int calculateMaxCashChange(User user, int maxCash, int cashChange, boolean userWon) {
  	if (null == user) {
  		//this is for fake user
  		return 0;
  	}

  	if (userWon) {
  		//if user somehow has more than max cash, first treat user as having max cash,
  		//figure out the amount he gains (0) and then subtract, the extra cash he had
  		int cashLoss = 0;
  		int userCash = user.getCash();
  		if (userCash > maxCash) {
  			cashLoss = userCash - maxCash;
  		}
  		
  		int curCash = Math.min(user.getCash(), maxCash); //in case user's cash is more than maxOil.
  		int maxCashUserCanGain = maxCash - curCash;
  		int maxCashChange = Math.min(cashChange, maxCashUserCanGain);
  		
  		//IF USER IS ABOVE maxOil, need to drag him down to maxOil
  		return maxCashChange - cashLoss;
  		
  	} else {
  		//if user somehow has more than max cash, first treat user as having max oil,
  		//figure out the max amount he loses, then subtract the extra oil he had
  		int additionalCashLoss = 0;
  		int userCash = user.getCash();
  		if (userCash > maxCash) {
  			log.warn("somehow user has more than maxCash. maxCash=" + maxCash + "\t user=" + user
  					+ "\t cashChange=" + cashChange);
  			additionalCashLoss = userCash - maxCash;
  			userCash = maxCash; 
  		}
  				
  		int maxCashUserCanLose = userCash;
  		int maxCashChange = Math.min(cashChange, maxCashUserCanLose);
  		maxCashChange = maxCashChange + additionalCashLoss;
  		
  		return -1 * maxCashChange;
  	}
  }


	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}
  
}
