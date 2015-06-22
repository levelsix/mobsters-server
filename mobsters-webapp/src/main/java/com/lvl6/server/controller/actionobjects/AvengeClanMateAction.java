package com.lvl6.server.controller.actionobjects;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanAvenge;
import com.lvl6.info.ClanAvengeUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventClanProto.AvengeClanMateResponseProto.AvengeClanMateStatus;
import com.lvl6.proto.EventClanProto.AvengeClanMateResponseProto.Builder;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component@Scope("prototype")public class AvengeClanMateAction {
	private static Logger log = LoggerFactory.getLogger( AvengeClanMateAction.class);

	private String avengerId;
	private String avengerClanId;
	private String clanAvengeId;
	private ClanAvengeUser cau;
	@Autowired protected ClanAvengeRetrieveUtil clanAvengeRetrieveUtil; 
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 
	@Autowired protected HazelcastPvpUtil hazelcastPvpUtil; 
	@Autowired protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil; 
	@Autowired protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil; 
	@Autowired protected InsertUtil insertUtil; 

	public AvengeClanMateAction(String avengerId, String clanId,
			String clanAvengeId, ClanAvengeUser cau,
			ClanAvengeRetrieveUtil clanAvengeRetrieveUtil,
			UserRetrieveUtils2 userRetrieveUtil,
			HazelcastPvpUtil hazelcastPvpUtil,
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil,
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil,
			InsertUtil insertUtil) {
		super();
		this.avengerId = avengerId;
		this.avengerClanId = clanId;
		this.clanAvengeId = clanAvengeId;
		this.cau = cau;
		this.clanAvengeRetrieveUtil = clanAvengeRetrieveUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.hazelcastPvpUtil = hazelcastPvpUtil;
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
		this.insertUtil = insertUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class AvengeClanMateResource {
	//		
	//		
	//		public AvengeClanMateResource() {
	//			
	//		}
	//	}
	//
	//	public AvengeClanMateResource execute() {
	//		
	//	}

	//derived state
	protected String victimId;
	protected User victim;
	protected PvpUser victimPu;
	protected PvpLeagueForUser victimPlfu;
	protected Collection<MonsterForUser> victimMonsters;
	protected Map<String, Integer> victimMonsterDrops;
	protected int expectedCashVictimLost;
	protected int expectedOilVictimLost;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(AvengeClanMateStatus.FAIL_OTHER);

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

		resBuilder.setStatus(AvengeClanMateStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {

		if (null == cau) {
			log.error("client didn't send user who is avenging clan mate");
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		List<ClanAvenge> caList = clanAvengeRetrieveUtil
				.getClanAvengesForIds(Collections.singletonList(clanAvengeId));

		if (null == caList || caList.isEmpty()) {
			log.error("no ClanAvenge in db with id={}", clanAvengeId);
			return false;
		}

		ClanAvenge ca = caList.get(0);
		victimId = ca.getAttackerId();
		victim = userRetrieveUtil.getUserById(victimId);
		if (null == victim) {
			log.error(
					"invalid ClanAvenge, no user with id (the attackerId) {}",
					ca);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		/*
		//prepare PvpProto arguments
		victimPu = hazelcastPvpUtil.getPvpUser(victimId);
		PvpUser avengerPu = hazelcastPvpUtil.getPvpUser(avengerId);
		//paranoia: make sure PvpUser exists for both
		if (null == victimPu || null == avengerPu)
		{
			List<String> userIds = new ArrayList<String>();
			userIds.add(victimId);
			userIds.add(avengerId);
			Map<String, PvpLeagueForUser> plfuMap = 
				pvpLeagueForUserRetrieveUtil
				.getUserPvpLeagueForUsers(userIds);
			
			victimPlfu = plfuMap.get(victimId);
			PvpLeagueForUser avengerPlfu = plfuMap.get(avengerId);
			
			//paranoia: make sure PvpLeagueForUser exists for both
			if (null == victimPlfu || null == avengerPlfu) {
				log.error("no PvpLeagueForUser for victimId {} or avengerId {} \t {}",
					new Object[] {victimId, avengerId, plfuMap});
				return false;
			}
				
			victimPu = new PvpUser(victimPlfu);
			avengerPu = new PvpUser(avengerPlfu);
		}
		

		Map<String, List<MonsterForUser>> victimMonsterMap = selectMonstersForUsers(
			Collections.singletonList(victimId));
		
		if (null != victimMonsterMap &&
			victimMonsterMap.containsKey(victimId))
		{
			victimMonsters = victimMonsterMap.get(victimId);
		} else {
			log.error("no monsters for {}", victim);	
			return false;
		}
		
		Map<String, Map<String, Integer>> userIdToUserMonsterIdToDroppedId =
			MonsterStuffUtils.calculatePvpDrops(victimMonsterMap);
		if (null != userIdToUserMonsterIdToDroppedId &&
			userIdToUserMonsterIdToDroppedId.containsKey(victimId))
		{
			victimMonsterDrops = userIdToUserMonsterIdToDroppedId.get(victimId);
		} else {
			log.error("no monster drops for {}", victimMonsterMap);
			return false;
		}
		
		int attackerElo = avengerPu.getElo();
		PvpBattleOutcome potentialResult = new PvpBattleOutcome(
			avengerId, attackerElo, victimId, victimPu.getElo(),
			victim.getCash(), victim.getOil());
		
		expectedCashVictimLost = potentialResult.getUnsignedCashAttackerWins();
		expectedOilVictimLost = potentialResult.getUnsignedOilAttackerWins();
		*/

		//insert into the clan_avenge_user table
		int numInserted = insertUtil.insertIntoClanAvengeUser(Collections
				.singletonList(cau));
		log.info("numInserted int clan_avenge_user (should be 1) {}",
				numInserted);

		return true;
	}

	/*
	//TODO: copy pasted from QueueUpController, figure another way to do this
	private Map<String, List<MonsterForUser>> selectMonstersForUsers(
		List<String> userIdList)
	{
		
		//return value
		Map<String, List<MonsterForUser>> userIdsToUserMonsters =
				new HashMap<String, List<MonsterForUser>>();
		
		//for all these users, get all their complete monsters
		Map<String, Map<String, MonsterForUser>> userIdsToMfuIdsToMonsters = monsterForUserRetrieveUtil
		    .getCompleteMonstersForUser(userIdList);
		
		
		for (int index = 0; index < userIdList.size(); index++) {
			//extract a user's monsters
		  String defenderId = userIdList.get(index);
			Map<String, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters.get(defenderId);
			
			if (null == mfuIdsToMonsters || mfuIdsToMonsters.isEmpty()) {
				log.error("WTF!!!!!!!! user has no monsters!!!!! userId=" + defenderId +
						"\t will move on to next guy.");
				continue;
			}
			//try to select at most 3 monsters for this user
			List<MonsterForUser> defenderMonsters = selectMonstersForUser(mfuIdsToMonsters);
			
			//if the user still doesn't have 3 monsters, then too bad
			userIdsToUserMonsters.put(defenderId, defenderMonsters);
		}
		
		return userIdsToUserMonsters;
	}

	private List<MonsterForUser> selectMonstersForUser(Map<String, MonsterForUser> mfuIdsToMonsters) {

		//get all the monsters the user has on a team (at the moment, max is 3)
		List<MonsterForUser> defenderMonsters = getEquippedMonsters(mfuIdsToMonsters);

		if (defenderMonsters.size() < 3) {
			//need more monsters so select them randomly, fill up "defenderMonsters" list
			getRandomMonsters(mfuIdsToMonsters, defenderMonsters);
		}
		
		if (defenderMonsters.size() > 3) {
			//only get three monsters
			defenderMonsters = defenderMonsters.subList(0, 3);
		}
		
		return defenderMonsters;
	}


	private List<MonsterForUser> getEquippedMonsters(Map<String, MonsterForUser> userMonsters) {
		List<MonsterForUser> equipped = new ArrayList<MonsterForUser>();
		
		for (MonsterForUser mfu : userMonsters.values()) {
			if (mfu.getTeamSlotNum() <= 0) {
				//only want equipped monsters
				continue;
			}
			equipped.add(mfu);
			
		}
		return equipped;
	}

	private void getRandomMonsters(Map<String, MonsterForUser> possibleMonsters,
			List<MonsterForUser> defenderMonsters) {
		
		Map<String, MonsterForUser> possibleMonstersTemp =
				new HashMap<String, MonsterForUser>(possibleMonsters);
		
		//remove the defender monsters from possibleMonstersTemp, since defenderMonsters
		//were already selected from possibleMonsters
		for (MonsterForUser m : defenderMonsters) {
		  String mfuId = m.getId();
			
			possibleMonstersTemp.remove(mfuId);
		}
		
		int amountLeftOver = possibleMonstersTemp.size();
		int amountNeeded = 3 - defenderMonsters.size();
		
		if (amountLeftOver < amountNeeded) {
			defenderMonsters.addAll(possibleMonstersTemp.values());
			return;
		}
		
		//randomly select enough monsters to total 3
		List<MonsterForUser> mfuList = new ArrayList<MonsterForUser>(possibleMonstersTemp.values());
		Random rand = ControllerConstants.RAND;
		
		//for each monster gen rand float, and if it "drops" select it
		for (int i = 0; i < mfuList.size(); i++) {
			
			//eg. need 2, have 3. If first one is not picked, then need 2, have 2.
			float probToBeChosen = amountNeeded / (amountLeftOver - i);
			float randFloat = rand.nextFloat();
			
			if (randFloat < probToBeChosen) {
				//we have a winner! select this monster
				MonsterForUser mfu = mfuList.get(i);
				defenderMonsters.add(mfu);
				
				//need to decrement amount needed
				amountNeeded--;
			}
			
			//stop at three monsters, don't want to get more
			if (defenderMonsters.size() >= 3) {
				break;
			}
		}
	}
	*/
	/*
	public User getVictim()
	{
		return victim;
	}

	public PvpUser getCachedVictimPvpLeaguenfo() {
		return victimPu;
	}
	
	public PvpLeagueForUser getVictimPvpLeagueInfo() {
		return victimPlfu; 
	}
	
	public Collection<MonsterForUser> getVictimMonsters()
	{
		return victimMonsters;
	}
	
	public Map<String, Integer> getVictimMonsterDrops()
	{
		return victimMonsterDrops;
	}

	public int getVictimProspectiveCashLoss()
	{
		return expectedCashVictimLost;
	}

	public int getVictimProspectiveOilLoss()
	{
		return expectedOilVictimLost;
	}
	*/

}
