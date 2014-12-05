package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginPvpBattleRequestEvent;
import com.lvl6.events.response.BeginPvpBattleResponseEvent;
import com.lvl6.info.PvpLeagueForUser;
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
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class BeginPvpBattleController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;
  
  @Autowired
  protected Locker locker;
  
  @Autowired
  protected TimeUtils timeUtils;
  
  @Autowired
  protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;
  
  

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
    log.info(String.format("reqProto=%s", reqProto));

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    //TODO: FIGURE OUT IF STILL NEEDED
    //int senderElo = reqProto.getSenderElo();
    
    String attackerId = senderProto.getUserUuid();
    Timestamp curTime = new Timestamp(reqProto.getAttackStartTime());
    Date curDate = new Date(curTime.getTime());
    PvpProto enemyProto = reqProto.getEnemy();
    String enemyUserId = enemyProto.getDefender().getMinUserProto().getUserUuid();
    
    boolean exactingRevenge = reqProto.getExactingRevenge();
    Timestamp previousBattleEndTime = null;
    if (exactingRevenge) {
    	//the battle that allowed sender to start this revenge battle
    	//where sender was the defender and enemy was the attacker
    	previousBattleEndTime = new Timestamp(reqProto.getPreviousBattleEndTime());
    }
    
    //set some values to send to the client (the response proto)
    BeginPvpBattleResponseProto.Builder resBuilder = BeginPvpBattleResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(BeginPvpBattleStatus.FAIL_OTHER); //default
    BeginPvpBattleResponseEvent resEvent = new BeginPvpBattleResponseEvent(attackerId);
    resEvent.setTag(event.getTag());

    UUID enemyUserUuid = null; 
    try {
    	if (!"".equals(enemyUserId)) {
    		enemyUserUuid = UUID.fromString(enemyUserId);
    	}
    } catch (Exception e1) {
    	log.error(String.format(
			"UUID error. incorrect enemyUserId=%s",
			enemyUserId), e1);
    }
    
    
    //lock the user that client is going to attack, in order to prevent others from
    //attacking same guy, only lock a real user
    if (null != enemyUserId && !enemyUserId.isEmpty()) {
    	getLocker().lockPlayer(enemyUserUuid, this.getClass().getSimpleName());
    }
    try {
    	User attacker = RetrieveUtils.userRetrieveUtils().getUserById(attackerId); 
    	PvpLeagueForUser enemyPlfu = null;
    	if (null != enemyUserUuid) {
    		enemyPlfu = pvpLeagueForUserRetrieveUtil
    				.getUserPvpLeagueForId(enemyUserId);
    	}
//    	PvpUser enemyPu = getHazelcastPvpUtil().getPvpUser(enemyUserId);
    	boolean legit = checkLegit(resBuilder, enemyPlfu, enemyUserId,
    			enemyProto, curDate);

    	boolean successful = false;
    	if(legit) {
    		//since enemy exists, update the enemy's inBattleShieldEndTime in 
    		//hazelcast and db
    		//record that the attacker is attacking the enemy
    		//calculateEloChange() will populate attackerEloChange and defenderEloChange
    		//the first values in both lists will be when attacker wins
    		//second values will be when attacker loses
    		List<Integer> attackerEloChange = new ArrayList<Integer>();
    		List<Integer> defenderEloChange = new ArrayList<Integer>();
    		calculateEloChange(attackerId, enemyUserId, enemyPlfu, attackerEloChange, defenderEloChange);
    		//enemyProto could be a pastVersion of the current version of enemy if
    		//revenging

    		//if user exacting revenge update the history to say he can't exact revenge anymore
    		//also turn off sender's shield if it's on (in the case of revenge)
    		successful = writeChangesToDb(attacker, attackerId, enemyUserId,
    				enemyPlfu, attackerEloChange, defenderEloChange, curTime,
    				exactingRevenge, previousBattleEndTime);
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
    	if (null != enemyUserId && !enemyUserId.isEmpty()) {
    		//only unlock if real user
    		getLocker().unlockPlayer(enemyUserUuid, this.getClass().getSimpleName());
    	}
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, PvpLeagueForUser enemyPlfu,
		  String enemyUserId, PvpProto enemyProto, Date curDate) {
  	
  	if (null == enemyUserId || enemyUserId.isEmpty()) {
  		//if fake user, just allow this to happen
  		return true;
  	}
    if (null == enemyPlfu) {
      log.error("unexpected error: enemy is null. enemyUserId=" + enemyUserId + 
      		"\t enemyProto client sent="+ enemyProto);
      return false;
    }
    
    // Disabling shield until further optimizations -- Affecting revenge too much
//    //check the shield times just to make sure this user is still attackable
//    //his shield end times should be in the past
//    Date shieldEndTime = enemyPlfu.getShieldEndTime();
//    Date inBattleEndTime = enemyPlfu.getInBattleShieldEndTime();
//    
//    if (shieldEndTime.getTime() > curDate.getTime() ||
//    		inBattleEndTime.getTime() > curDate.getTime()) {
//    	//this is possible if another attacker got to this person first
//    	resBuilder.setStatus(BeginPvpBattleStatus.FAIL_ENEMY_UNAVAILABLE); 
//    	log.warn("The user this client wants to attack has already been atttacked" +
//    			" or is being attacked. pvpUser=" + enemyPlfu + "\t curDate" + curDate);
//    	return false;
//    }
    
    return true;
  }
  
  //fills up the lists attackerEloChange, defenderEloChange
  private void calculateEloChange(String attackerId,
	  String defenderId, PvpLeagueForUser defender,
	  List<Integer> attackerEloChange, List<Integer> defenderEloChange)
  {
	  //TODO: Hackery!!!  Getting attacker elo,  make it more efficient.
	  PvpUser attacker = getHazelcastPvpUtil().getPvpUser(attackerId);
	  int attackerElo;
	  if (null == attacker)
	  {
		  log.warn("User has no PvpUser in hazelcast, querying db");
		  PvpLeagueForUser plfu = pvpLeagueForUserRetrieveUtil
			  .getUserPvpLeagueForId(attackerId);
		  attackerElo = plfu.getElo();
	  } else {
		  attackerElo = attacker.getElo();
	  }
  	
	  int defenderElo = ControllerConstants.PVP__DEFAULT_MIN_ELO;
	  
	  if (null != defender) {
		  defenderElo = defender.getElo();
	  }
	
	  //TODO: Figure out a way to not even have to set 0 for resources
	  PvpBattleOutcome results = new PvpBattleOutcome(attackerId, attackerElo, defenderId, defenderElo, 0, 0);
	  
  	//calculate the actual values. And account for fake defenders!
  	//case where attacker wins
  	int attackerWinEloChange = results.getUnsignedEloAttackerWins();
  	int defenderLoseEloChange = -1 * attackerWinEloChange;
  	//when defender loses elo, it should not go below a minimum
  	if ((defenderElo + defenderLoseEloChange) <
  		ControllerConstants.PVP__DEFAULT_MIN_ELO)
  	{
  		defenderLoseEloChange = 0;
  	}
  	
  	//case where attacker loses
  	int defenderWinEloChange = results.getUnsignedEloAttackerLoses();
  	int attackerLoseEloChange = -1 * defenderWinEloChange;
  	//when attacker loses elo, it should not go below a minimum
  	if ((attackerElo + attackerLoseEloChange) <
  		ControllerConstants.PVP__DEFAULT_MIN_ELO) 
  	{
  		attackerLoseEloChange = 0;
  	}
  	
  	//values are ordered by attacker win then attacker loses
  	attackerEloChange.add(attackerWinEloChange);
  	attackerEloChange.add(attackerLoseEloChange);
  	
  	defenderEloChange.add(defenderLoseEloChange);
  	defenderEloChange.add(defenderWinEloChange);
  }
  
  	//the first values in both elo change lists will be when attacker wins
	//and second values will be when attacker loses
  private boolean writeChangesToDb(User attacker, String attackerId, String enemyId,
  		PvpLeagueForUser enemy, List<Integer> attackerEloChange,
  		List<Integer> defenderEloChange, Timestamp clientTime, boolean exactingRevenge,
  		Timestamp previousBattleEndTime) {
	  
	  //record that attacker is attacking defender
  	int attackerWinEloChange = attackerEloChange.get(0);
  	int defenderLoseEloChange = defenderEloChange.get(0);
  	int attackerLoseEloChange = attackerEloChange.get(1);
  	int defenderWinEloChange = defenderEloChange.get(1);

  	log.info("inserting into PvpBattleForUser");
  	int numInserted = InsertUtils.get().insertUpdatePvpBattleForUser(attackerId,
  			enemyId, attackerWinEloChange, defenderLoseEloChange, attackerLoseEloChange,
  			defenderWinEloChange, clientTime);
  			
  	log.info("numInserted (should be 1): " + numInserted); 
  			
  	//ACCOUNTING FOR FAKE DEFENDERS!
  	if (null != enemyId && !enemyId.isEmpty()) {
  		//assume that the longest a battle can go for is one hour from now
  		//so other users can't attack this person (who is under attack atm) for one hour
  		long nowMillis = clientTime.getTime();
  		Date newInBattleEndTime = new Date(nowMillis + ControllerConstants.PVP__MAX_BATTLE_DURATION_MILLIS);
  		
  		// Only update if this new time is more in the future than the current shield time
  		if (enemy.getInBattleShieldEndTime().getTime() < newInBattleEndTime.getTime()) {
  		    
  	        enemy.setInBattleShieldEndTime(newInBattleEndTime);
  	        
  	        //replace hazelcast object
  	        PvpUser nuEnemyPu = new PvpUser(enemy);
  	        getHazelcastPvpUtil().replacePvpUser(nuEnemyPu, enemyId);
  	        
  	        //as well as update db
  	        Timestamp nuInBattleEndTime = new Timestamp(newInBattleEndTime.getTime());
  	        log.info("now=" + clientTime);
  	        log.info("should be one hour later, battleEndTime=" + nuInBattleEndTime);
  	        int numUpdated = UpdateUtils.get().updatePvpLeagueForUserShields(enemyId,
  	                null, nuInBattleEndTime);
  	        log.info("(defender shield) num updated=" + numUpdated);
  		}
  		
  		//turn off attacker's shield if it's on, attacker can't revenge fake person
  		exactRevenge(attacker, attackerId, enemyId, clientTime, previousBattleEndTime,
  				exactingRevenge);
  	}
  	
  	return true;
  }

  private void exactRevenge(User attacker, String attackerId, String defenderId,
	  Timestamp clientTime, Timestamp prevBattleEndTime, boolean exactingRevenge) {
	  if (!exactingRevenge) {
		  log.info("not exacting revenge");
		  return;
	  }
	  if (null == prevBattleEndTime) {
		  log.info("not exacting revenge, prevBattleEndTime is null");
	  }
	  log.info("exacting revenge");
	  //need to switch the ids, because when exacting revenge roles are reversed
	  //when viewing from the battle that started this revenge battle
	  String historyAttackerId = defenderId;
	  String historyDefenderId = attackerId;
	  int numUpdated = UpdateUtils.get().updatePvpBattleHistoryExactRevenge(
		  historyAttackerId, historyDefenderId, prevBattleEndTime);
	  log.info("recorded that user exacted revenge. numUpdated (should be 1)=" + numUpdated);

	  PvpLeagueForUser attackerPlfu = pvpLeagueForUserRetrieveUtil
		  .getUserPvpLeagueForId(attackerId);

	  //if user has a shield up (defined as Time(now) < Time(shieldEnds)), change
	  //shield end time to login
	  //TODO: this is the same logic in QueueUpController
	  Date curShieldEndTime = attackerPlfu.getShieldEndTime();
	  Date nowDate = new Date(clientTime.getTime());
	  if (getTimeUtils().isFirstEarlierThanSecond(nowDate, curShieldEndTime)) {
		  log.info("user shield end time is now being reset since he's attacking with a shield");
		  log.info("1cur pvpuser=" + getHazelcastPvpUtil().getPvpUser(attackerId));
		  Date login = attacker.getLastLogin();
		  Timestamp loginTime = new Timestamp(login.getTime());

		  numUpdated = UpdateUtils.get().updatePvpLeagueForUserShields(attackerId,
			  null, loginTime);
		  log.info("(defender shield) num updated=" + numUpdated);

		  PvpUser attackerOpu = new PvpUser(attackerPlfu);
		  attackerOpu.setShieldEndTime(login);
		  attackerOpu.setInBattleEndTime(login);               
		  getHazelcastPvpUtil().replacePvpUser(attackerOpu, attackerId);
		  log.info("2cur pvpuser=" + getHazelcastPvpUtil().getPvpUser(attackerId));
		  log.info("(should be same as 2cur pvpUser) 3cur pvpuser=" + attackerOpu);
	  }
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

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}
	
}
