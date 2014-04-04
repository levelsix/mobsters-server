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
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil;
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
  protected PvpLeagueForUserRetrieveUtil pvpLeagueForUserRetrieveUtil;
  
  

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
    log.info("reqProto=" + reqProto);

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int senderElo = reqProto.getSenderElo();
    int attackerId = senderProto.getUserId();
    Timestamp curTime = new Timestamp(reqProto.getAttackStartTime());
    Date curDate = new Date(curTime.getTime());
    PvpProto enemyProto = reqProto.getEnemy();
    int enemyUserId = enemyProto.getDefender().getMinUserProto().getUserId();
    
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

    //lock the user that client is going to attack, in order to prevent others from
    //attacking same guy, only lock a real user
    if (0 != enemyUserId) {
    	getLocker().lockPlayer(enemyUserId, this.getClass().getSimpleName());
    }
    try {
    	User attacker = RetrieveUtils.userRetrieveUtils().getUserById(attackerId); 
    	PvpLeagueForUser enemyPlfu = null;
    	if (0 != enemyUserId) {
    		enemyPlfu = getPvpLeagueForUserRetrieveUtil()
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
    		calculateEloChange(senderElo, enemyProto, attackerEloChange, defenderEloChange);
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
    	if (0 != enemyUserId) {
    		//only unlock if real user
    		getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, PvpLeagueForUser enemyPlfu,
		  int enemyUserId, PvpProto enemyProto, Date curDate) {
  	
  	if (0 == enemyUserId) {
  		//if fake user, just allow this to happen
  		return true;
  	}
    if (null == enemyPlfu) {
      log.error("unexpected error: enemy is null. enemyUserId=" + enemyUserId + 
      		"\t enemyProto client sent="+ enemyProto);
      return false;
    }
    
    //check the shield times just to make sure this user is still attackable
    //his shield end times should be in the past
    Date shieldEndTime = enemyPlfu.getShieldEndTime();
    Date inBattleEndTime = enemyPlfu.getInBattleShieldEndTime();
    
    if (shieldEndTime.getTime() > curDate.getTime() ||
    		inBattleEndTime.getTime() > curDate.getTime()) {
    	//this is possible if another attacker got to this person first
    	resBuilder.setStatus(BeginPvpBattleStatus.FAIL_ENEMY_UNAVAILABLE); 
    	log.warn("The user this client wants to attack has already been atttacked" +
    			" or is being attacked. pvpUser=" + enemyPlfu + "\t curDate" + curDate);
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
  	int defenderElo = 4;
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
  
  	//the first values in both elo change lists will be when attacker wins
	//and second values will be when attacker loses
  private boolean writeChangesToDb(User attacker, int attackerId, int enemyId,
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
  	if (0 != enemyId) {
  		//assume that the longest a battle can go for is one hour from now
  		//so other users can't attack this person (who is under attack atm) for one hour
  		long nowMillis = clientTime.getTime();
  		Date newInBattleEndTime = new Date(nowMillis + ControllerConstants.PVP__MAX_BATTLE_DURATION_MILLIS);
  		enemy.setInBattleShieldEndTime(newInBattleEndTime);
  		
  		//replace hazelcast object
  		PvpUser nuEnemyPu = new PvpUser(enemy);
  		getHazelcastPvpUtil().replacePvpUser(nuEnemyPu, enemyId);
  		
  		//as well as update db
  		Timestamp nuInBattleEndTime = new Timestamp(newInBattleEndTime.getTime());
  		int numUpdated = UpdateUtils.get().updatePvpLeagueForUserShields(enemyId,
  				null, nuInBattleEndTime);
  		log.info("(defender shield) num updated=" + numUpdated);
  		
  		//turn off attacker's shield if it's on, attacker can't revenge fake person
  		exactRevenge(attacker, attackerId, enemyId, clientTime, previousBattleEndTime,
  				exactingRevenge);
  	}
  	
	  return true;
  }
  
  private void exactRevenge(User attacker, int attackerId, int defenderId,
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
  	int historyAttackerId = defenderId;
  	int historyDefenderId = attackerId;
  	int numUpdated = UpdateUtils.get().updatePvpBattleHistoryExactRevenge(
  			historyAttackerId, historyDefenderId, prevBattleEndTime);
  	log.info("recorded that user exacted revenge. numUpdated (should be 1)=" + numUpdated);
  	
  	PvpLeagueForUser attackerPlfu = getPvpLeagueForUserRetrieveUtil()
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

	public PvpLeagueForUserRetrieveUtil getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}
	
}
