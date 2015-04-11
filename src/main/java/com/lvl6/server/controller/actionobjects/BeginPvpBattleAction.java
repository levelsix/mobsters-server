package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.PvpBattleForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto.BeginPvpBattleStatus;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto.Builder;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

public class BeginPvpBattleAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String attackerId;
	private String defenderId;
	private int defenderElo;
	private Date clientDate;
	private boolean exactingRevenge;
	private Timestamp previousBattleEndTime;
	private PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;
	private HazelcastPvpUtil hazelcastPvpUtil;
	private TimeUtils timeUtil;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;

	public BeginPvpBattleAction(String attackerId, String defenderId,
			int defenderElo, Date clientDate, boolean exactingRevenge,
			Timestamp previousBattleEndTime,
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil,
			HazelcastPvpUtil hazelcastPvpUtil, TimeUtils timeUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil) {
		super();
		this.attackerId = attackerId;
		this.defenderId = defenderId;
		this.defenderElo = defenderElo;
		this.clientDate = clientDate;
		this.exactingRevenge = exactingRevenge;
		this.previousBattleEndTime = previousBattleEndTime;
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
		this.hazelcastPvpUtil = hazelcastPvpUtil;
		this.timeUtil = timeUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class QueueUpResource {
	//		
	//		
	//		public QueueUpResource() {
	//			
	//		}
	//	}
	//
	//	public QueueUpResource execute() {
	//		
	//	}

	//derived state
	boolean fakeDefender;
	//	private PvpUser attacker;
	private PvpLeagueForUser attackerPlfu;
	private PvpLeagueForUser defenderPlfu;
	private int attackerWinEloChange;
	private int defenderLoseEloChange;
	private int defenderWinEloChange;
	private int attackerLoseEloChange;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(BeginPvpBattleStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(BeginPvpBattleStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {
		if (null == defenderId || defenderId.isEmpty()) {
			//if fake user, just allow this to happen
			fakeDefender = true;
			return true;
		} else {
			fakeDefender = false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		List<String> userIds = new ArrayList<String>();
		//Not pulling from hazelcast since want up to date attacker info
		//		attacker = hazelcastPvpUtil.getPvpUser(attackerId);
		//		if (null == attacker) {
		//			log.warn("User has no PvpUser in hazelcast, going to query db");
		//			userIds.add(attackerId);
		//		}

		userIds.add(attackerId);
		if (!fakeDefender) {
			log.info("defender is real user. {}", defenderId);
			userIds.add(defenderId);
		}

		Map<String, PvpLeagueForUser> userIdsToPlfus = pvpLeagueForUserRetrieveUtil
				.getUserPvpLeagueForUsers(userIds);

		if (!fakeDefender) {
			if (!userIdsToPlfus.containsKey(defenderId)) {
				log.error("enemy has no PvpLeagueForUser info. defenderId={}",
						defenderId);
				return false;
			} else {
				defenderPlfu = userIdsToPlfus.get(defenderId);
			}
		}

		if (!userIdsToPlfus.containsKey(attackerId)) {
			log.error("no attacker PvpLeagueForUser with id={}", attackerId);
			return false;
		} else {
			attackerPlfu = userIdsToPlfus.get(attackerId);
		}

		// Disabling shield until further optimizations -- Affecting revenge too much
		//	    //check the shield times just to make sure this user is still attackable
		//	    //his shield end times should be in the past
		//	    Date shieldEndTime = enemyPlfu.getShieldEndTime();
		//	    Date inBattleEndTime = enemyPlfu.getInBattleShieldEndTime();
		//    
		//	    if (shieldEndTime.getTime() > curDate.getTime() ||
		//	    		inBattleEndTime.getTime() > curDate.getTime()) {
		//	    	//this is possible if another attacker got to this person first
		//	    	resBuilder.setStatus(BeginPvpBattleStatus.FAIL_ENEMY_UNAVAILABLE); 
		//	    	log.warn("The user this client wants to attack has already been atttacked" +
		//	    			" or is being attacked. pvpUser=" + enemyPlfu + "\t curDate" + curDate);
		//	    	return false;
		//	    }

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		calculateEloChange();

		//if user exacting revenge update the history to say he can't exact revenge anymore
		//also turn off sender's shield if it's on (in the case of revenge)
		log.info("inserting into PvpBattleForUser");
		PvpBattleForUser pbfu = new PvpBattleForUser(attackerId, defenderId,
				attackerWinEloChange, defenderLoseEloChange,
				attackerLoseEloChange, defenderWinEloChange, clientDate);
		int numInserted = insertUtil.insertUpdatePvpBattleForUser(pbfu);
		log.info("numInserted (should be 1): {}", numInserted);

		//ACCOUNTING FOR FAKE DEFENDERS!
		if (!fakeDefender) {
			//assume that the longest a battle can go for is one hour from now
			//so other users can't attack this person (who is under attack atm) for one hour
			long nowMillis = clientDate.getTime();
			Date newInBattleEndTime = new Date(nowMillis
					+ ControllerConstants.PVP__MAX_BATTLE_DURATION_MILLIS);

			// Only update if this new time is more in the future than the current shield time
			if (defenderPlfu.getInBattleShieldEndTime().getTime() < newInBattleEndTime
					.getTime()) {

				defenderPlfu.setInBattleShieldEndTime(newInBattleEndTime);

				//replace hazelcast object
				PvpUser nuEnemyPu = new PvpUser(defenderPlfu);
				hazelcastPvpUtil.replacePvpUser(nuEnemyPu, defenderId);

				//as well as update db
				Timestamp nuInBattleEndTime = new Timestamp(
						newInBattleEndTime.getTime());
				log.info("now={}", clientDate);
				log.info("should be one hour later, battleEndTime={}",
						nuInBattleEndTime);
				int numUpdated = UpdateUtils.get()
						.updatePvpLeagueForUserShields(defenderId, null,
								nuInBattleEndTime);
				log.info("(defender shield) num updated={}", numUpdated);
			}

			//turn off attacker's shield if it's on, attacker can't revenge fake person
			exactRevenge();
		}

		return true;
	}

	private void calculateEloChange() {
		int attackerElo = attackerPlfu.getElo();
		int defenderElo = this.defenderElo;

		if (!fakeDefender) {
			defenderElo = defenderPlfu.getElo();
		}

		defenderElo = Math.max(defenderElo,
				ControllerConstants.PVP__DEFAULT_MIN_ELO);

		User attacker = new User();
		attacker.setId(attackerId);

		User user = new User();
		user.setCash(0);
		user.setOil(0);
		user.setId(defenderId);
		PvpBattleOutcome results = new PvpBattleOutcome(attacker, attackerElo,
				defenderElo, user);

		attackerWins(defenderElo, results);
		attackerLoses(attackerElo, results);
	}

	private void attackerWins(int defenderElo, PvpBattleOutcome results) {
		attackerWinEloChange = results.getUnsignedEloAttackerWins();
		defenderLoseEloChange = -1 * attackerWinEloChange;
		//when defender loses elo, it should not go below a minimum
		if ((defenderElo + defenderLoseEloChange) < ControllerConstants.PVP__DEFAULT_MIN_ELO) {
			defenderLoseEloChange = 0;
		}

		if (attackerWinEloChange < 0) {
			String preface = "WTF, attacker wins negative elo. ";
			log.error("{}, {}", preface, results);
		}
	}

	private void attackerLoses(int attackerElo, PvpBattleOutcome results) {
		defenderWinEloChange = results.getUnsignedEloAttackerLoses();
		attackerLoseEloChange = -1 * defenderWinEloChange;
		//when attacker loses elo, it should not go below a minimum
		if ((attackerElo + attackerLoseEloChange) < ControllerConstants.PVP__DEFAULT_MIN_ELO) {
			attackerLoseEloChange = 0;
		}

		if (defenderWinEloChange < 0) {
			String preface = "WTF, defender wins negative elo. ";
			log.error("{}, {}", preface, results);
		}
	}

	private void exactRevenge() {
		Timestamp clientTime = new Timestamp(clientDate.getTime());
		if (!exactingRevenge) {
			log.info("not exacting revenge");
			return;
		}
		if (null == previousBattleEndTime) {
			log.info("not exacting revenge, prevBattleEndTime is null");
			return;
		}
		log.info("exacting revenge");
		//need to switch the ids, because when exacting revenge roles are reversed
		//when viewing from the battle that started this revenge battle
		String historyAttackerId = defenderId;
		String historyDefenderId = attackerId;
		int numUpdated = UpdateUtils.get().updatePvpBattleHistoryExactRevenge(
				historyAttackerId, historyDefenderId, previousBattleEndTime);
		log.info("recorded that user exacted revenge. numUpdated (should be 1)="
				+ numUpdated);

		PvpLeagueForUser attackerPlfu = pvpLeagueForUserRetrieveUtil
				.getUserPvpLeagueForId(attackerId);

		//if user has a shield up (defined as Time(now) < Time(shieldEnds)), change
		//shield end time to sometime //login
		//TODO: this is the same logic in QueueUpController
		Date curShieldEndTime = attackerPlfu.getShieldEndTime();
		if (timeUtil.isFirstEarlierThanSecond(clientDate, curShieldEndTime)) {
			log.info("user shield end time is now being reset since he's attacking with a shield");
			log.info("1cur pvpuser={}", hazelcastPvpUtil.getPvpUser(attackerId));
			//			Date login = attacker.getLastLogin();
			//			Timestamp loginTime = new Timestamp(login.getTime());

			numUpdated = updateUtil.updatePvpLeagueForUserShields(attackerId,
					null, clientTime);
			log.info("(defender shield) num updated={}", numUpdated);

			PvpUser attackerOpu = new PvpUser(attackerPlfu);
			//			attackerOpu.setShieldEndTime(login);
			//			attackerOpu.setInBattleEndTime(login);               
			attackerOpu.setShieldEndTime(clientDate);
			attackerOpu.setInBattleEndTime(clientDate);
			hazelcastPvpUtil.replacePvpUser(attackerOpu, attackerId);
			//Note: changes in Hazelcast take time to propagate 
			//			log.info("2cur pvpuser=" + getHazelcastPvpUtil().getPvpUser(attackerId));
			log.info("(should be same as 1cur pvpUser) 3cur pvpuser={}",
					attackerOpu);
		}
	}
}
