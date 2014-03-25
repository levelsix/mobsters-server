package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

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
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class EndPvpBattleController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;
  
  @Autowired
  protected Locker locker;
  
  @Autowired
  protected TimeUtils timeUtils;

  public EndPvpBattleController() {
    numAllocatedThreads = 7;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new EndPvpBattleRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_END_PVP_BATTLE_EVENT;
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
    
    //NEED TO LOCK BOTH PLAYERS, well need to lock defender because defender can be online,
    //Lock attacker because someone might be attacking him while attacker is attacking defender?
    if (0 != defenderId) {
    	getLocker().lockPlayers(defenderId, attackerId, this.getClass().getSimpleName());
    	log.info("locked defender and attacker");
    } else {
    	//ONLY ATTACKER IF DEFENDER IS FAKE
    	getLocker().lockPlayer(attackerId, this.getClass().getSimpleName());
    	log.info("locked attacker");
    }
    
    try {
    	//get whatever from db
    	Map<Integer, User> users = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIds);
    	User attacker = users.get(attackerId);
    	User defender = users.get(defenderId);
    	PvpBattleForUser pvpBattleInfo = PvpBattleForUserRetrieveUtils
    			.getPvpBattleForUserForAttacker(attackerId);

    	//could be fake user so could be null, enemy could also be null if he is online
    	//if null then no need to update
    	PvpUser defenderOpu = getHazelcastPvpUtil().getPvpUser(defenderId);
    	boolean legit = checkLegit(resBuilder, attacker, defender, pvpBattleInfo, curDate);

    	boolean successful = false;
    	if(legit) {
    		//it is possible that the defender has a shield, most likely via buying it,
    		//and less likely some other way, regardless, the user can have a shield
    		successful = writeChangesToDb(attacker, attackerId, defender, defenderId,
    				defenderOpu, pvpBattleInfo, oilChange, cashChange, curTime, curDate,
    				attackerAttacked, attackerWon, attackerMaxOil, attackerMaxCash);
    	}

    	if (successful) {
    		resBuilder.setStatus(EndPvpBattleStatus.SUCCESS);
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
    	if (0 != defenderId) {
    		getLocker().unlockPlayers(defenderId, attackerId, this.getClass().getSimpleName());
    		log.info("unlocked defender and attacker");
    	} else {
    		getLocker().unlockPlayer(attackerId, this.getClass().getSimpleName());
    		log.info("unlocked attacker");
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
  		int defenderId,  PvpUser defenderOpu, PvpBattleForUser pvpBattleInfo, 
		  int oilChange, int cashChange, Timestamp clientTime, Date clientDate,
		  boolean attackerAttacked, boolean attackerWon, int attackerMaxOil, int attackerMaxCash) {
  	
	  boolean cancelled = !attackerAttacked;
	  
  	if (cancelled) {
  		//this means that the only thing that changes is defenderOpu's inBattleShieldEndTime
  		//just change it so its not in the future
  		
  		if (null != defender && defenderOpu.getInBattleEndTime().getTime() > clientTime.getTime()) {
  			//since real player and battle end time is after now, change it to now
  			//so defender can be attackable again
  			defenderOpu.setInBattleEndTime(clientDate);
  			getHazelcastPvpUtil().replacePvpUser(defenderOpu);
  		}
  		log.info("writing to pvp history that user cancelled battle");
  		writePvpBattleHistory(attackerId, defenderId, clientTime, pvpBattleInfo, 0, 0,
  				0, 0, 0, 0, attackerWon, cancelled, false, false);
  	} else {
  		//user attacked so either he won or lost
  		
  		//MAKE SURE THE ATTACKER AND DEFENDER'S ELO DON'T GO BELOW 0
  		//TODO: WHEN MAX ELO IS FIGURED OUT, MAKE SURE ELO DOESN'T GO ABOVE THAT
  		List<Integer> attackerEloChangeList = new ArrayList<Integer>();
  		List<Integer> defenderEloChangeList = new ArrayList<Integer>();
  		getEloChanges(attacker, defender, attackerWon, pvpBattleInfo,
  				attackerEloChangeList, defenderEloChangeList);
  		int attackerEloChange = attackerEloChangeList.get(0);
  		int defenderEloChange = defenderEloChangeList.get(0);
  		int attackerOilChange = calculateMaxOilChange(attacker, attackerMaxOil, oilChange, attackerWon);
  		int attackerCashChange = calculateMaxCashChange(attacker, attackerMaxCash, cashChange, attackerWon);
  		
  		//update attacker's cash, oil, elo
  		attacker.updateEloOilCash(attackerId, attackerEloChange, attackerOilChange,
  				attackerCashChange);
  		
  		//need to take into account if defender (online and) spent some cash/oil before
  		//pvp battle ends, defender will not gain resources by winning
  		List<Integer> defenderOilChangeList = new ArrayList<Integer>();
  		List<Integer> defenderCashChangeList = new ArrayList<Integer>();
  		List<Boolean> displayToDefenderList = new ArrayList<Boolean>();
  		
  		//defender could be fake user, in which case no change is made to defender
  		//no change could still be made if the defender is already under attack by another
  		//and this attacker is the second guy attacking the defender
  		defenderEloChangeList.clear();
  		updateDefender(attacker, defender, defenderId, pvpBattleInfo, defenderEloChange,
  				cashChange, oilChange, clientDate, attackerWon, defenderEloChangeList,
  				defenderOilChangeList, defenderCashChangeList, displayToDefenderList);
  		
  		int defenderOilChange = defenderOilChangeList.get(0);
  		int defenderCashChange = defenderCashChangeList.get(0);
  		boolean displayToDefender = displayToDefenderList.get(0); 
  		
  		log.info("writing to pvp history that user finished battle");
  		log.info("attackerEloChange=" + attackerEloChange + "\t defenderEloChange=" +
  				defenderEloChange + "\t attackerOilChange="+ attackerOilChange +
  				"\t defenderOilChange=" + defenderOilChange + "\t attackerCashChange=" +
  				attackerCashChange + "\t defenderCashChange=" + defenderCashChange);
  		writePvpBattleHistory(attackerId, defenderId, clientTime, pvpBattleInfo,
  				attackerEloChange, defenderEloChange, attackerOilChange, defenderOilChange,
  				attackerCashChange, defenderCashChange, attackerWon, cancelled, false,
  				displayToDefender);
  	}
  	
  	log.info("deleting from PvpBattleForUser");
  	//need to delete PvpBattleForUser
  	int numDeleted = DeleteUtils.get().deletePvpBattleForUser(attackerId);
  	log.info("numDeleted (should be 1): " + numDeleted); 
  			
  	
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
  		//figure out the amount he gains (0) and then subtract the extra oil he had
  		int oilLoss = 0;
  		int userOil = user.getOil();
  		if (userOil > maxOil) {
  			log.info("user has more than the max resources");
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
  
  //calculate how much elo changes for the attacker and defender
  //based on whether the attacker won, capping min elo at 0 for attacker & defender
  private void getEloChanges(User attacker, User defender, boolean attackerWon,
  		PvpBattleForUser pvpBattleInfo, List<Integer> attackerEloChangeList,
  		List<Integer> defenderEloChangeList) {
  	//temp variables
  	int attackerEloChange = 0;
  	int defenderEloChange = 0;
  	
  	if (attackerWon) {
  		attackerEloChange = pvpBattleInfo.getAttackerWinEloChange(); //positive value
  		defenderEloChange = pvpBattleInfo.getDefenderLoseEloChange(); //negative value

  		//don't cap fake player's elo
  		if (null != defender && pvpBattleInfo.getDefenderId() > 0) {
  			log.info("attacker fought real player. battleInfo=" + pvpBattleInfo);
  			//make sure defender's elo doesn't go below 0
  			int defenderElo = defender.getElo();
  			if (defenderElo + defenderEloChange < 0) {
  				defenderEloChange = -1 * defenderElo;
  			}
  		} else {
  			log.info("attacker fought fake player. battleInfo=" + pvpBattleInfo);
  		}

  	} else {
  		attackerEloChange = pvpBattleInfo.getAttackerLoseEloChange(); //negative value
  		defenderEloChange = pvpBattleInfo.getDefenderWinEloChange(); // positive value

  		//make sure attacker's elo doesn't go below 0
  		int attackerElo = attacker.getElo();
  		if (attackerElo + attackerEloChange < 0) {
  			attackerEloChange = -1 * attackerElo;
  		}

  	}
  	
  	attackerEloChangeList.add(attackerEloChange);
  	defenderEloChangeList.add(defenderEloChange);
  	
  }
  
  //the elo, oil, cash, display lists are return values
  private void updateDefender(User attacker, User defender, int defenderId,
  		PvpBattleForUser pvpBattleInfo, int defenderEloChange, int oilChange, int cashChange,
  		Date clientDate, boolean attackerWon, List<Integer> defenderEloChangeList,
  		List<Integer> defenderOilChangeList, List<Integer> defenderCashChangeList,
  		List<Boolean> displayToDefenderList) {
  	if (null == defender) {
  		log.info("attacker attacked fake defender. attacker=" + attacker);
  		defenderEloChangeList.clear();
  		defenderEloChangeList.add(0);
  		defenderOilChangeList.add(0);
  		defenderCashChangeList.add(0);
  		displayToDefenderList.add(false);
  		return;
  	}
  	
  	boolean defenderWon = !attackerWon;
  	int defenderOilChange = calculateMaxOilChange(defender, defender.getOil(),
				oilChange, defenderWon);
		int defenderCashChange = calculateMaxCashChange(defender, defender.getCash(),
				cashChange, defenderWon);
		boolean displayToDefender = true;
		
		//if DEFENDER HAS SHIELD THEN DEFENDER SHOULD NOT BE PENALIZED, and 
		//the history for this battle should have the display_to_defender set to false;
		Date inBattleShieldEndTime = defender.getInBattleShieldEndTime();
		Date shieldEndTime = defender.getShieldEndTime();
		if (getTimeUtils().isFirstEarlierThanSecond(clientDate, shieldEndTime)) {
			log.warn("some how attacker attacked a defender with a shield!! pvpBattleInfo=" +
					pvpBattleInfo + "\t attacker=" + attacker + "\t defender=" + defender);
			defenderOilChange = 0;
			defenderCashChange = 0;
			defenderEloChange = 0;
			displayToDefender = false;
			
		} else {
			//NO ONE ELSE ATTACKING DEFENDER. update defender's cash, oil, elo and shields
			log.info("attacker attacked unshielded defender. attacker=" + attacker +
					"\t defender=" + defender + "\t battleInfo=" + pvpBattleInfo);
			if (attackerWon) {
				int hoursAddend = ControllerConstants.PVP__LOST_BATTLE_SHIELD_DURATION_HOURS;
				shieldEndTime = getTimeUtils().createDateAddHours(clientDate, hoursAddend);
				inBattleShieldEndTime = shieldEndTime;
			} //else defender gains elo
			
			defender.updateEloOilCashShields(defenderId, defenderEloChange, defenderOilChange,
					defenderCashChange, shieldEndTime, inBattleShieldEndTime);
			log.info("defender after:" + defender);
		}
		
		defenderEloChangeList.add(defenderEloChange);
		defenderOilChangeList.add(defenderOilChange);
		defenderCashChangeList.add(defenderCashChange);
		displayToDefenderList.add(displayToDefender);
		
		//in common case, the user's elo will change, endtimes might or might not
		updateHazelcastUser(defender, defenderId);
  }
  
  private void updateHazelcastUser(User defender, int defenderId) {
//  	//update the map if the defender exists/is offline
//  	if (null != defenderOpu) {
//  		int defenderElo = defender.getElo();
//  		defenderOpu.setElo(defenderElo);
//  		getHazelcastPvpUtil().updatePvpUser(defenderOpu); 
//  		
//  		defenderOpu.setShieldEndTime(defender.getShieldEndTime());
//  		defenderOpu.setInBattleEndTime(defender.getInBattleShieldEndTime());
//  	}
  	int defenderElo = defender.getElo();                    
		PvpUser pu = new PvpUser();
		pu.setUserId(Integer.toString(defenderId));
		pu.setElo(defenderElo);                        
		pu.setShieldEndTime(defender.getShieldEndTime());               
		pu.setInBattleEndTime(defender.getInBattleShieldEndTime());               
//		getHazelcastPvpUtil().updateOfflinePvpUser(defenderOpu);
		getHazelcastPvpUtil().replacePvpUser(pu);
  }
  
  private void writePvpBattleHistory(int attackerId, int defenderId, Timestamp endTime,
  		PvpBattleForUser pvpBattleInfo, int attackerEloChange, int defenderEloChange,
  		int attackerOilChange, int defenderOilChange, int attackerCashChange,
  		int defenderCashChange, boolean attackerWon, boolean cancelled, boolean gotRevenge,
  		boolean displayToDefender) {
  	
  	Date startDate = pvpBattleInfo.getBattleStartTime();
  	Timestamp battleStartTime = new Timestamp(startDate.getTime());
  	int numInserted = InsertUtils.get().insertIntoPvpBattleHistory(attackerId, defenderId,
  			endTime, battleStartTime, attackerEloChange, defenderEloChange,
  			attackerOilChange, defenderOilChange, attackerCashChange, defenderCashChange,
  			attackerWon, cancelled, gotRevenge, displayToDefender);
  	log.info("num inserted into history=" + numInserted );
  }


	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}
  
}
