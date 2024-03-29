package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;
import com.lvl6.info.BattleReplayForUser;
import com.lvl6.info.PvpBattleForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.PvpBattleHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserCurrencyHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserCurrencyHistoryPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.StructStolen;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.daos.PvpBattleCountForUserDao2;
import com.lvl6.retrieveutils.daos.StructureForUserDao2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.PvpBattleCountUtils;
import com.lvl6.server.controller.utils.ResourceUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class EndPvpBattleAction {
	private static Logger log = LoggerFactory.getLogger( EndPvpBattleAction.class);

	private String attackerId;
	private String defenderId;
	private boolean attackerAttacked;
	private boolean attackerWon;
	private int oilStolenFromStorage;
	private int cashStolenFromStorage;
	private float nuPvpDmgMultiplier;
	private List<Integer> monsterDropIds;
	private int attackerMaxOil;
	private int attackerMaxCash;
	private long clientTime;
	private Date curDateTime;
	private Timestamp curTime;
	private ByteString replay;
	@Autowired protected ResourceUtil resourceUtil; 
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 
	@Autowired protected PvpBattleForUserRetrieveUtils2 pvpBattleForUserRetrieveUtil; 
	@Autowired protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil; 
	@Autowired protected ClanRetrieveUtils2 clanRetrieveUtil; 
	@Autowired protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil; 
	@Autowired protected MonsterStuffUtils monsterStuffUtils; 
	@Autowired protected PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils; 
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils; 
	@Autowired protected ServerToggleRetrieveUtils serverToggleRetrieveUtil; 
	@Autowired protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtil; 
	private MiscMethods miscMethods;
	@Autowired protected HazelcastPvpUtil hazelcastPvpUtil; 
	@Autowired protected TimeUtils timeUtil; 
	@Autowired protected InsertUtil insertUtil; 
	@Autowired protected UpdateUtil updateUtil; 
	private List<StructStolen> listOfGenerators;
	private int oilStolenFromGenerators;
	private int cashStolenFromGenerators;
	private StructureForUserDao2 sfuDao;
	@Autowired protected HistoryUtils historyUtils; 
	private PvpBattleHistoryDao pbhDao;
	private UserCurrencyHistoryDao uchDao;
	private PvpBattleCountForUserDao2 pbcfuDao;
	private TimeUtils timeUtils;
	private PvpBattleCountUtils pbcUtils;


	public EndPvpBattleAction(String attackerId, String defenderId,
			boolean attackerAttacked, boolean attackerWon, int oilStolen,
			int cashStolen, float nuPvpDmgMultiplier,
			List<Integer> monsterDropIds, int attackerMaxOil,
			int attackerMaxCash, long clientTime, Date curDateTime,
			Timestamp curTime, ByteString replay,
			ResourceUtil resourceUtil, UserRetrieveUtils2 userRetrieveUtil,
			PvpBattleForUserRetrieveUtils2 pvpBattleForUserRetrieveUtil,
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil,
			ClanRetrieveUtils2 clanRetrieveUtil,
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils,
			CreateInfoProtoUtils createInfoProtoUtils,
			ServerToggleRetrieveUtils serverToggleRetrieveUtil,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtil,
			MiscMethods miscMethods, HazelcastPvpUtil hazelcastPvpUtil,
			TimeUtils timeUtil, InsertUtil insertUtil,
			UpdateUtil updateUtil, List<StructStolen> listOfGenerators,
			int oilStolenFromGenerators, int cashStolenFromGenerators,
			StructureForUserDao2 sfuDao, HistoryUtils historyUtils,
			PvpBattleHistoryDao pbhDao, UserCurrencyHistoryDao uchDao,
			PvpBattleCountForUserDao2 pbcfuDao, TimeUtils timeUtils,
			PvpBattleCountUtils pbcUtils)
	{
		super();
		this.attackerId = attackerId;
		this.defenderId = defenderId;
		this.attackerAttacked = attackerAttacked;
		this.attackerWon = attackerWon;
		this.oilStolenFromStorage = oilStolen;
		this.cashStolenFromStorage = cashStolen;
		this.nuPvpDmgMultiplier = nuPvpDmgMultiplier;
		this.monsterDropIds = monsterDropIds;
		this.attackerMaxOil = attackerMaxOil;
		this.attackerMaxCash = attackerMaxCash;
		this.clientTime = clientTime;
		this.curDateTime = curDateTime;
		this.curTime = curTime;
		this.replay = replay;
		this.resourceUtil = resourceUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.pvpBattleForUserRetrieveUtil = pvpBattleForUserRetrieveUtil;
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
		this.clanRetrieveUtil = clanRetrieveUtil;
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.pvpLeagueRetrieveUtils = pvpLeagueRetrieveUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
		this.monsterLevelInfoRetrieveUtil = monsterLevelInfoRetrieveUtil;
		this.miscMethods = miscMethods;
		this.hazelcastPvpUtil = hazelcastPvpUtil;
		this.timeUtil = timeUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.listOfGenerators = listOfGenerators;
		this.oilStolenFromGenerators = oilStolenFromGenerators;
		this.cashStolenFromGenerators = cashStolenFromGenerators;
		this.sfuDao = sfuDao;
		this.historyUtils = historyUtils;
		this.pbhDao = pbhDao;
		this.uchDao = uchDao;
		this.pbcfuDao = pbcfuDao;
		this.timeUtils = timeUtils;  
		this.pbcUtils = pbcUtils;
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
	protected Date clientDateTime;
	protected Map<String, User> idToUser;
	protected PvpBattleForUser pvpBattleInfo;
	protected User attacker;
	protected PvpLeagueForUser attackerPlfu;
	protected int attackerEloChange;
	protected int attackerEloBefore;
	protected int attackerEloAfter;
	protected int attackerPrevLeague;
	protected int attackerCurLeague;
	protected int attackerPrevRank;
	protected int attackerCurRank;
	protected int attackerStorageCashChange;
	protected int attackerStorageOilChange;
	protected int attackerAttacksWonDelta;
	protected int attackerAttacksLostDelta;
	protected int attackerPrevCash;
	protected int attackerPrevOil;
	protected int defenderPrevCash;
	protected int defenderPrevOil;

	protected User defender;
	protected PvpLeagueForUser defenderPlfu;
	protected boolean isRealDefender;
	protected boolean cancelled;
	protected int defenderEloChange;
	protected int defenderEloBefore;
	protected int defenderEloAfter;
	protected int defenderPrevLeague;
	protected int defenderCurLeague;
	protected int defenderPrevRank;
	protected int defenderCurRank;
	protected int defenderStorageCashChange;
	protected int defenderStorageOilChange;
	protected int defenderDefensesWonDelta;
	protected int defenderDefensesLostDelta;
	protected Date defenderShieldEndTime;
	protected boolean displayToDefender;

	private BattleReplayForUser brfu;
	private PvpBattleHistoryPojo pbh;
	
	private Map<String, Long> generatorsMap;
	private List<StructureForUserPojo> updateList;

	private Map<String, Map<String, Integer>> currencyDeltasMap;
	private Map<String, Map<String, Integer>> prevCurrenciesMap;
	private Map<String, Map<String, Integer>> curCurrenciesMap;
	private Map<String, Map<String, String>> reasonsForChangesMap;
	private Map<String, Map<String, String>> detailsMap;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//		if (!valid) {
//			return;
//		}
		clientDateTime = new Date(clientTime);
		boolean valid = verifySemantics(resBuilder);
		if (!valid) {
			return;
		}
		resBuilder.setStatsBefore(createInfoProtoUtils
				.createUserPvpLeagueProto(attackerId, attackerPlfu,
						null, false));

		boolean success = writeChangesToDB(resBuilder);
		resBuilder.setStatsAfter(createInfoProtoUtils
				.createUserPvpLeagueProto(attackerId, attackerPlfu,
						null, false));

		if (!success) {
			return;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
	}

//	private boolean verifySyntax(Builder resBuilder) {
//
//		return true;
//	}

	private boolean verifySemantics(Builder resBuilder) {
		pvpBattleInfo = pvpBattleForUserRetrieveUtil
				.getPvpBattleForUserForAttacker(attackerId);
		log.info("pvpBattleInfo={}", pvpBattleInfo);
		if (null == pvpBattleInfo) {
			log.error("no battle exists for the attacker. attackerId={},\t defenderId={}",
					attackerId, defenderId);
			return false;
		}

		//if the battle has finished one hour after the battle started, don't count it
		long battleEndTime = pvpBattleInfo.getBattleStartTime().getTime()
				+ ControllerConstants.PVP__MAX_BATTLE_DURATION_MILLIS;
		if (clientTime > battleEndTime) {
			resBuilder.setStatus(ResponseStatus.FAIL_BATTLE_TOOK_TOO_LONG);
			log.error("client took too long to finish a battle. pvpInfo={},\t now={}",
					pvpBattleInfo, clientDateTime);
			return false;
		}

		retrieveDbInfo();
		isRealDefender = null != defender && null != pvpBattleInfo.getDefenderId()
				&& !pvpBattleInfo.getDefenderId().isEmpty();

		return true;
	}

	private void retrieveDbInfo() {
		List<String> userIds = new ArrayList<String>();
		userIds.add(attackerId);
		if (!defenderId.isEmpty()) {
			userIds.add(defenderId);
		}
		idToUser = userRetrieveUtil.getUsersByIds(userIds);

		attacker = idToUser.get(attackerId);
		if (!defenderId.isEmpty()) {
			defender = idToUser.get(defenderId);
		}

		Map<String, PvpLeagueForUser> plfuMap = pvpLeagueForUserRetrieveUtil
				.getUserPvpLeagueForUsers(userIds);
		log.info("plfuMap={}", plfuMap);
		//these objects will be updated if not null
		attackerPlfu = null;
		defenderPlfu = null;

		if (plfuMap.containsKey(attackerId)) {
			attackerPlfu = plfuMap.get(attackerId);
		}
		//could be fake user so could be null
		if (plfuMap.containsKey(defenderId)) {
			defenderPlfu = plfuMap.get(defenderId);
		}
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		//it is possible that the defender has a shield, most likely via
		//buying it, and less likely locks didn't work, regardless, the
		//user can have a shield

		cancelled = !attackerAttacked;
		if (cancelled) {
			log.info("battle cancelled");
			//this means that the only thing that changes is defenderOpu's inBattleShieldEndTime
			//just change it so its not in the future
			processCancellation();
		} else{
			//user attacked so either he won or lost
			processOutcome();
			awardMonsters(resBuilder);
			doCurrencyHistory();
		}

		//need to delete PvpBattleForUser
		int numDeleted = DeleteUtils.get().deletePvpBattleForUser(attackerId);
		log.info("numDeleted PvpBattleForUser (should be 1): {}", numDeleted);
		
		if(attackerWon) {
			pbcUtils.insertOrUpdatePvpBattleCount(attackerId, defenderId, pbcfuDao, timeUtils);
		}
		return true;
	}

	private void processCancellation() {
		cancellationSetup();
		insertIntoPvpBattleHistory();
	}

	private void cancellationSetup() {
		if (isRealDefender
				&& defenderPlfu.getInBattleShieldEndTime().getTime() > clientTime) {
			//since real player and "battle end time" is after now, change it
			//so defender can be attackable again
			defenderPlfu.setInBattleShieldEndTime(defenderPlfu
					.getShieldEndTime());
			PvpUser defenderOpu = new PvpUser(defenderPlfu);
			hazelcastPvpUtil.replacePvpUser(defenderOpu, defenderId);
			log.info("changed battleEndTime to shieldEndTime. defenderPvpUser={}",
					defenderOpu);
		}


		//since battle cancelled, nothing should have changed
		log.info("writing to pvp history that user cancelled battle");

		attackerEloChange = 0;
		attackerEloBefore = attackerPlfu.getElo();
		attackerEloAfter = attackerEloBefore;
		attackerPrevLeague = attackerPlfu.getPvpLeagueId();
		attackerCurLeague = attackerPrevLeague;
		attackerPrevRank = attackerPlfu.getRank();
		attackerCurRank = attackerPrevRank;
		attackerStorageCashChange = 0;
		attackerStorageOilChange = 0;
		attackerWon = false;

		defenderEloChange = 0;
		defenderEloBefore = 0;
		defenderPrevLeague = 0;
		defenderPrevRank = 0;
		if (isRealDefender) {
			defenderEloBefore = defenderPlfu.getElo();
			defenderPrevLeague = defenderPlfu.getPvpLeagueId();
			defenderPrevRank = defenderPlfu.getRank();
		}
		defenderEloAfter = defenderEloBefore;
		defenderCurLeague = defenderPrevLeague;
		defenderCurRank = defenderPrevRank;
		defenderStorageCashChange = 0;
		defenderStorageOilChange = 0;
	}

	private void insertIntoPvpBattleHistory() {
		String replayId = null;
		if (null != brfu) {
			replayId = brfu.getId();
		}
		pbh = historyUtils.createPvpBattleHistory(attackerId, defenderId, 
				clientDateTime, pvpBattleInfo.getBattleStartTime(), attackerEloChange, 
				attackerEloBefore, attackerEloAfter, defenderEloChange, defenderEloBefore, 
				defenderEloAfter, attackerPrevLeague, attackerCurLeague, defenderPrevLeague, 
				defenderCurLeague, attackerPrevRank, attackerCurRank, defenderPrevRank, 
				defenderCurRank, attackerStorageCashChange, attackerStorageOilChange, 
				defenderStorageCashChange, defenderStorageOilChange, cashStolenFromStorage, 
				cashStolenFromGenerators, oilStolenFromStorage, oilStolenFromGenerators,
				cancelled, false, nuPvpDmgMultiplier, false, attackerWon, replayId);
		pbhDao.insert(pbh);
	}

	private void processOutcome() {
		getAttackerDefenderEloChanges();

		prevCurrenciesMap = new HashMap<String, Map<String, Integer>>();
		attackerOutcomeSetup();
		updateAttacker();

		defenderOutcomeSetup();
		updateDefender();

		createReplay();
		int numInserted = insertUtil.insertBattleReplayForUser(brfu);
		insertIntoPvpBattleHistory();
		log.info("num inserted into battle replay history={}", numInserted);
	}

	private void getAttackerDefenderEloChanges() {
		if (attackerWon) {
			log.info("getEloChanges attacker won.");
			attackerEloChange = pvpBattleInfo.getAttackerWinEloChange(); //positive value
			defenderEloChange = pvpBattleInfo.getDefenderLoseEloChange(); //negative value

			//no need to cap fake player's elo
			if (isRealDefender) {
				log.info("getEloChanges attacker fought real player. battleInfo={}",
						pvpBattleInfo);
				//make sure defender's elo doesn't go below 0
				defenderEloChange = capPlayerMinimumElo(defenderPlfu,
						defenderEloChange);
			} else {
				log.info("getEloChanges attacker fought fake player. battleInfo={}",
						pvpBattleInfo);
			}
		} else {
			attackerEloChange = pvpBattleInfo.getAttackerLoseEloChange(); //negative value
			defenderEloChange = pvpBattleInfo.getDefenderWinEloChange(); // positive value
			//make sure attacker's elo doesn't go below 0
			attackerEloChange = capPlayerMinimumElo(attackerPlfu,
					attackerEloChange);
		}

		log.info("getEloChanges attackerEloChange={}",
				attackerEloChange);
		log.info("getEloChanges defenderEloChange={}",
				defenderEloChange);

	}

	private int capPlayerMinimumElo(PvpLeagueForUser playerPlfu,
			int playerEloChange) {
		int playerElo = playerPlfu.getElo();
		log.info("capPlayerMinimumElo plfu={},  eloChange={}",
				playerPlfu, playerEloChange);

		if (playerElo + playerEloChange < 0) {
			String preface = "capPlayerMinimumElo player loses more elo than has atm.";
			log.info("{} playerElo={}\t playerEloChange={}",
					new Object[] { preface, playerElo, playerEloChange } );
			playerEloChange = -1 * playerElo;
		}

		log.info( "capPlayerMinimumElo updated playerEloChange={}",
				playerEloChange );
		return playerEloChange;
	}

	private void attackerOutcomeSetup() {
		attackerEloBefore = attackerPlfu.getElo();
		attackerPrevLeague = attackerPlfu.getPvpLeagueId();
		attackerPrevRank = attackerPlfu.getRank();
		int minResource = 0;

		attackerEloAfter = attackerEloBefore + attackerEloChange;
		attackerCurLeague = pvpLeagueRetrieveUtils.getLeagueIdForElo(
				attackerEloAfter, attackerPrevLeague);
		attackerCurRank = pvpLeagueRetrieveUtils.getRankForElo(attackerEloAfter,
				attackerCurLeague);
		attackerStorageCashChange = resourceUtil.calculateMaxResourceChange(attacker,
				attackerMaxCash, minResource, cashStolenFromStorage + cashStolenFromGenerators, attackerWon, MiscMethods.cash);
		attackerStorageOilChange = resourceUtil.calculateMaxResourceChange(attacker,
				attackerMaxOil, minResource, oilStolenFromStorage + cashStolenFromGenerators, attackerWon, MiscMethods.oil);

		if (attackerWon) {
			attackerAttacksWonDelta = 1;
			attackerAttacksLostDelta = 0;
			attackerPlfu.setAttacksWon(attackerAttacksWonDelta + attackerPlfu.getAttacksWon());
		} else {
			attackerAttacksWonDelta = 0;
			attackerAttacksLostDelta = 1;
			attackerPlfu.setAttacksLost(attackerAttacksLostDelta  + attackerPlfu.getAttacksLost());
		}

		attackerPrevCash = attacker.getCash();
		attackerPrevOil = attacker.getOil();		
	}

	private void updateAttacker() {
		//update attacker's cash, oil
		if (0 != attackerStorageOilChange || 0 != attackerStorageCashChange) {
			log.info( "attacker before currency update: {}",
					attacker);
			int numUpdated = attacker.updateRelativeCashAndOilAndGems(
					attackerStorageCashChange, attackerStorageOilChange, 0, 0);
			log.info( "attacker after currency update: {}",
					attacker );
			log.info( "num updated when changing attacker's currency={}",
					numUpdated );
		}
		log.info( "attacker PvpLeagueForUser before battle outcome: {}",
				attackerPlfu );

		Date shieldEndDateTime =  attackerPlfu.getShieldEndTime();
		Timestamp shieldEndTime = new Timestamp(shieldEndDateTime.getTime());
		Date inBattleShieldEndDateTime = attackerPlfu.getInBattleShieldEndTime();
		Timestamp inBattleShieldEndTime = new Timestamp(inBattleShieldEndDateTime.getTime());
		int numUpdated = updateUtil.updatePvpLeagueForUser(attackerId,
				attackerCurLeague, attackerCurRank, attackerEloChange,
				shieldEndTime, inBattleShieldEndTime,
				attackerAttacksWonDelta, 0, attackerAttacksLostDelta,
				0, nuPvpDmgMultiplier);

		log.info("num updated when changing attacker's elo={}", numUpdated);

		attackerPlfu.setElo(attackerEloAfter);
		attackerPlfu.setPvpLeagueId(attackerCurLeague);
		attackerPlfu.setRank(attackerCurRank);

		//update hazelcast's object
		PvpUser attackerPu = new PvpUser(attackerPlfu);
		hazelcastPvpUtil.replacePvpUser(attackerPu, attackerId);
		log.info("attacker PvpLeagueForUser after battle outcome:{}",
				attackerPlfu);
	}

	//defender could be fake user, in which case no change is made to defender
	//no change could still be made if the defender is already under attack by another
	//and this attacker is the second guy attacking the defender
	private void defenderOutcomeSetup()
	{
		if (!isRealDefender) {
			log.info( "attacker attacked fake defender." );
			defenderStorageCashChange = 0;
			defenderStorageOilChange = 0;
			displayToDefender = false;
			return;
		}
//		PvpLeagueForUser defenderPrevPlfu = null;
//		if (isRealDefender) {
//			defenderPrevPlfu = new PvpLeagueForUser(defenderPlfu);
//		}

		defenderEloBefore = defenderPlfu.getElo();
		defenderPrevLeague = defenderPlfu.getPvpLeagueId();
		defenderPrevRank = defenderPlfu.getRank();

		defenderEloAfter = defenderEloBefore + defenderEloChange;
		defenderCurLeague = pvpLeagueRetrieveUtils.getLeagueIdForElo(
				defenderEloAfter, defenderPrevLeague);
		defenderCurRank = pvpLeagueRetrieveUtils.getRankForElo(
				defenderEloAfter, defenderCurLeague);

		boolean defenderWon = !attackerWon;
		defenderPrevCash = defender.getCash();
		defenderPrevOil = defender.getOil();
		int minResource = 0;
		defenderStorageCashChange = resourceUtil.calculateMaxResourceChange(defender,
				defenderPrevCash, minResource, cashStolenFromStorage, defenderWon, MiscMethods.cash);
		defenderStorageOilChange = resourceUtil.calculateMaxResourceChange(defender,
				defenderPrevOil, minResource, oilStolenFromStorage, defenderWon, MiscMethods.oil);
		displayToDefender = true;

		if (defenderWon) {
			defenderDefensesWonDelta = 1;
			defenderDefensesLostDelta = 0;
			defenderPlfu.setDefensesWon(defenderDefensesWonDelta + defenderPlfu.getDefensesWon());
			defenderShieldEndTime = defenderPlfu.getInBattleShieldEndTime();
		} else {
			defenderDefensesWonDelta = 0;
			defenderDefensesLostDelta = 1;
			defenderPlfu.setDefensesLost(defenderDefensesLostDelta + defenderPlfu.getDefensesLost());
			defenderShieldEndTime = timeUtil.createDateAddHours(clientDateTime,
					ControllerConstants.PVP__LOST_BATTLE_SHIELD_DURATION_HOURS);
		}
	}

	private void updateDefender()
	{
		log.info("attacker attacked defender. {}\t defender={}\t battleInfo={}",
				new Object[] { attacker, defender, pvpBattleInfo });

		if (!isRealDefender) {
			return;
		}

		//if attacker won then defender money would need to be updated
		boolean resourceChanged = (0 != defenderStorageCashChange) ||
				(0 != defenderStorageOilChange);
		if (attackerWon) {
			log.info("updating defender user struct's to account for resources stolen");
			updateDefenderUserStructs();
			if(resourceChanged) {
				log.info("defender before currency update:{}", defender);
				int numUpdated = defender.updateRelativeCashAndOilAndGems(
						defenderStorageCashChange, defenderStorageOilChange, 0, 0);
				log.info("num updated when changing defender's currency={}",
						numUpdated);
				log.info("defender after currency update: {}",
						defender);
			}
		}

		Timestamp shieldEndTime = new Timestamp(
				defenderShieldEndTime.getTime());
		int numUpdated = updateUtil.updatePvpLeagueForUser(defenderId,
				defenderCurLeague, defenderCurRank, defenderEloChange,
				shieldEndTime, shieldEndTime, 0, defenderDefensesWonDelta,
				0, defenderDefensesLostDelta, -1);
		log.info("num updated when changing defender's elo={}",
				numUpdated);

		defenderPlfu.setShieldEndTime(defenderShieldEndTime);
		defenderPlfu.setInBattleShieldEndTime(defenderShieldEndTime);
		defenderPlfu.setElo(defenderEloAfter);
		defenderPlfu.setPvpLeagueId(defenderCurLeague);
		defenderPlfu.setRank(defenderCurRank);

		PvpUser defenderPu = new PvpUser(defenderPlfu);
		hazelcastPvpUtil.replacePvpUser(defenderPu, defenderId);
		log.info( "defender PvpLeagueForUser after battle outcome: %s",
				defenderPlfu );
	}
	
	public void updateDefenderUserStructs() {
		generatorsMap = new HashMap<String, Long>();
		for(StructStolen ss : listOfGenerators) {
			generatorsMap.put(ss.getUserStructUuid(), ss.getTimeOfRetrieval());
		}
		List<StructureForUserPojo> defenderUserStructs = sfuDao.fetchByUserId(defenderId);
		Map<String, StructureForUserPojo> defenderSfuMap = new HashMap<String, StructureForUserPojo>();
		for(StructureForUserPojo sfu : defenderUserStructs) {
			defenderSfuMap.put(sfu.getId(), sfu);
		}
		updateList = new ArrayList<StructureForUserPojo>();
		for(String generatorsId : generatorsMap.keySet()) {
			StructureForUserPojo sfu = defenderSfuMap.get(generatorsId);
			Long clientCollectTime = generatorsMap.get(generatorsId);
			Date clientCollectTimeDate = new Date(clientCollectTime);
			Timestamp dbLastCollectTime = sfu.getLastRetrieved();
			Date dbLastCollectTimeDate = new Date(dbLastCollectTime.getTime());
			if(timeUtil.isFirstEarlierThanSecond(dbLastCollectTimeDate, clientCollectTimeDate)) {
				sfu.setLastRetrieved(new Timestamp(clientCollectTimeDate.getTime()));
				updateList.add(sfu);
			}
			else {
				generatorsMap.put(generatorsId, dbLastCollectTimeDate.getTime());
			}
		}
		if(!updateList.isEmpty()) {
			sfuDao.update(updateList);
		}
	}

	private void createReplay() {
		brfu = new BattleReplayForUser();

		brfu.setCreatorId(attackerId);
		brfu.setReplay(replay.toByteArray());
		brfu.setTimeCreated(clientDateTime);
	}

	private void awardMonsters(Builder resBuilder)
	{
		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();

		for (Integer monsterId : monsterDropIds) {
			if (!monsterIdToNumPieces.containsKey(monsterId)) {
				monsterIdToNumPieces.put(monsterId, 0);
			}
			int nuQuantity = monsterIdToNumPieces.get(monsterId) + 1;
			monsterIdToNumPieces.put(monsterId, nuQuantity);
		}

		String mfusop = String.format("%s a=%s, d=%s, time=%s",
				new Object[] {
						ControllerConstants.MFUSOP__PVP, attackerId,
						defenderId, clientDateTime });

		List<FullUserMonsterProto> newOrUpdated = monsterStuffUtils
				.updateUserMonsters(attackerId, monsterIdToNumPieces, null, mfusop,
						clientDateTime, monsterLevelInfoRetrieveUtil);
		resBuilder.addAllUpdatedOrNew(newOrUpdated);
	}

//	private void prepCurrencyHistory() {
//		String cash = MiscMethods.cash;
//		String oil = MiscMethods.oil;
//
//		currencyDeltasMap = new HashMap<String, Map<String, Integer>>();
//		Map<String, Integer> intMap = new HashMap<String, Integer>();
//		intMap.put(cash, attackerStorageCashChange);
//		intMap.put(oil, attackerStorageOilChange);
//		currencyDeltasMap.put(attackerId, intMap);
//
//		curCurrenciesMap = new HashMap<String, Map<String, Integer>>();
//		intMap = new HashMap<String, Integer>();
//		intMap.put(cash, attacker.getCash());
//		intMap.put(oil, attacker.getOil());
//		curCurrenciesMap.put(attackerId, intMap);
//
//		reasonsForChangesMap = new HashMap<String, Map<String, String>>();
//		String reasonForChange = ControllerConstants.UCHRFC__PVP_BATTLE;
//		Map<String, String> strMap = new HashMap<String, String>();
//		strMap.put(cash, reasonForChange);
//		strMap.put(oil, reasonForChange);
//		reasonsForChangesMap.put(attackerId, strMap);
//
//		int aWon = 0;
//		if (attackerWon) {
//			aWon = 1;
//		}
//		detailsMap = new HashMap<String, Map<String, String>>();
//		String detailSb = String.format("a:{}, d:{}, t:{}, aWon:{}, "
//				+ "cashstolefromgenerator {}, oilstolenfromgenerator {}",
//				new Object[] { attackerId, defenderId, clientDateTime, aWon,
//						cashStolenFromGenerators, oilStolenFromGenerators} );
//		strMap = new HashMap<String, String>();
//		strMap.put(MiscMethods.cash, detailSb);
//		strMap.put(MiscMethods.oil, detailSb);
//		detailsMap.put(attackerId, strMap);
//
//		if (isRealDefender) {
//			intMap = new HashMap<String, Integer>();
//			intMap.put(cash, defenderStorageCashChange);
//			intMap.put(oil, defenderStorageOilChange);
//			currencyDeltasMap.put(defenderId, intMap);
//
//			intMap = new HashMap<String, Integer>();
//			intMap.put(cash, defender.getCash());
//			intMap.put(oil, defender.getOil());
//			curCurrenciesMap.put(defenderId, intMap);
//
//			strMap = new HashMap<String, String>();
//			strMap.put(cash, reasonForChange);
//			strMap.put(oil, reasonForChange);
//			reasonsForChangesMap.put(defenderId, strMap);
//
//			detailsMap.put(defenderId, strMap);
//		}
//	}
	
	public void doCurrencyHistory() {
		List<UserCurrencyHistoryPojo> uchList = new ArrayList<UserCurrencyHistoryPojo>();
		String reasonForChange = ControllerConstants.UCHRFC__PVP_BATTLE;
		if(attackerWon) {
			UserCurrencyHistoryPojo attackerCashUch = historyUtils.createUserCurrencyHistory(attackerId, 
					curDateTime, MiscMethods.cash, attackerStorageCashChange, attackerPrevCash, attacker.getCash(), 
					reasonForChange, "beat " + defenderId);
			uchList.add(attackerCashUch);
			UserCurrencyHistoryPojo attackerOilUch = historyUtils.createUserCurrencyHistory(attackerId, 
					curDateTime, MiscMethods.oil, attackerStorageOilChange, attackerPrevOil, attacker.getOil(), 
					reasonForChange, "beat " + defenderId);
			uchList.add(attackerOilUch);
			if (isRealDefender) {
				UserCurrencyHistoryPojo defenderCashUch = historyUtils.createUserCurrencyHistory(defenderId, 
						curDateTime, MiscMethods.cash, defenderStorageCashChange, defenderPrevCash, defender.getCash(), 
						reasonForChange, "lost to " + attackerId);
				uchList.add(defenderCashUch);
				UserCurrencyHistoryPojo defenderOilUch = historyUtils.createUserCurrencyHistory(defenderId, 
						curDateTime, MiscMethods.oil, defenderStorageOilChange, defenderPrevCash, defender.getCash(), 
						reasonForChange, "lost to " + attackerId);
				uchList.add(defenderOilUch);
			}
			uchDao.insert(uchList);
		}
	}

	public User getAttacker() {
		return attacker;
	}

	public void setAttacker(User attacker) {
		this.attacker = attacker;
	}

	public PvpLeagueForUser getAttackerPlfu() {
		return attackerPlfu;
	}

	public void setAttackerPlfu(PvpLeagueForUser attackerPlfu) {
		this.attackerPlfu = attackerPlfu;
	}

	public User getDefender() {
		return defender;
	}

	public void setDefender(User defender) {
		this.defender = defender;
	}

	public PvpLeagueForUser getDefenderPlfu() {
		return defenderPlfu;
	}

	public void setDefenderPlfu(PvpLeagueForUser defenderPlfu) {
		this.defenderPlfu = defenderPlfu;
	}

	public Map<String, User> getIdToUser() {
		return idToUser;
	}

	public void setIdToUser(Map<String, User> idToUser) {
		this.idToUser = idToUser;
	}

	public PvpBattleHistoryPojo getPbh() {
		return pbh;
	}

	public void setPbh(PvpBattleHistoryPojo pbh) {
		this.pbh = pbh;
	}

//	public Map<String, BattleReplayForUser> getReplayIdToReplay()
//	{
//		if (null == replay)
//		{
//			return new HashMap<String, BattleReplayForUser>();
//		}
//		return Collections.singletonMap(brfu.getId(), brfu);
//
//	}

	public Map<String, Long> getGeneratorsMap() {
		return generatorsMap;
	}

	public void setGeneratorsMap(Map<String, Long> generatorsMap) {
		this.generatorsMap = generatorsMap;
	}

	public List<StructureForUserPojo> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<StructureForUserPojo> updateList) {
		this.updateList = updateList;
	}

	public int getOilStolenFromStorage() {
		return oilStolenFromStorage;
	}

	public void setOilStolenFromStorage(int oilStolenFromStorage) {
		this.oilStolenFromStorage = oilStolenFromStorage;
	}

	public int getCashStolenFromStorage() {
		return cashStolenFromStorage;
	}

	public void setCashStolenFromStorage(int cashStolenFromStorage) {
		this.cashStolenFromStorage = cashStolenFromStorage;
	}

	public int getOilStolenFromGenerators() {
		return oilStolenFromGenerators;
	}

	public void setOilStolenFromGenerators(int oilStolenFromGenerators) {
		this.oilStolenFromGenerators = oilStolenFromGenerators;
	}

	public int getCashStolenFromGenerators() {
		return cashStolenFromGenerators;
	}

	public void setCashStolenFromGenerators(int cashStolenFromGenerators) {
		this.cashStolenFromGenerators = cashStolenFromGenerators;
	}
	
	
	
}
