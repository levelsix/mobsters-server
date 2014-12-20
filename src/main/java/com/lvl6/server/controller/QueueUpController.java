package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QueueUpRequestEvent;
import com.lvl6.events.response.QueueUpResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForPvp;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventPvpProto.QueueUpRequestProto;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.QueueUpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterForPvpRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;


@Component @DependsOn("gameServer") public class QueueUpController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;
	
	@Autowired
	protected MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtils;
	
	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;
  
  @Autowired
  protected ClanRetrieveUtils2 clanRetrieveUtils;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;
  
  @Autowired
  protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	
//	@Autowired
//	protected PvpUtil pvpUtil;
//
//	public PvpUtil getPvpUtil() {
//		return pvpUtil;
//	}
//
//	public void setPvpUtil(PvpUtil pvpUtil) {
//		this.pvpUtil = pvpUtil;
//	}

	public QueueUpController() {
		numAllocatedThreads = 10;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new QueueUpRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_QUEUE_UP_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		QueueUpRequestProto reqProto = ((QueueUpRequestEvent) event)
				.getQueueUpRequestProto();
		
		log.info("reqProto=" + reqProto); 

		MinimumUserProto attackerProto = reqProto.getAttacker();
		String attackerId = attackerProto.getUserUuid();
		//TODO: Maybe delete this since going to use db value anyway
//		int attackerElo = reqProto.getAttackerElo();
		
		//positive means refund, negative means charge user; don't forsee being positive
//		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
//		int cashChange = reqProto.getCashChange();
		
		List<String> seenUserIds = reqProto.getSeenUserUuidsList();
		Set<String> uniqSeenUserIds = new HashSet<String>(seenUserIds);
		//don't want the attacker to see himself
		uniqSeenUserIds.add(attackerId);
		
		Date clientDate = new Date(reqProto.getClientTime());
		Timestamp clientTime = new Timestamp(clientDate.getTime());
		
		//set some values to send to the client
		QueueUpResponseProto.Builder resBuilder = QueueUpResponseProto.newBuilder();
		resBuilder.setAttacker(attackerProto);
		resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);

    UUID userUuid = null;
    UUID seenUserUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(attackerId);

      if (uniqSeenUserIds != null) {
        for (String seenUserId : uniqSeenUserIds) {
          seenUserUuid = UUID.fromString(seenUserId);
        }
      }

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s, seenUserIds=%s",
          attackerId, uniqSeenUserIds), e);
      invalidUuids = true;
    }

    if (invalidUuids) {
    	resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
		QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
		resEvent.setTag(event.getTag());
		resEvent.setQueueUpResponseProto(resBuilder.build());  
		server.writeEvent(resEvent);
		return;
    }
    
		try {
			User attacker = getUserRetrieveUtils().getUserById(attackerId);
			PvpLeagueForUser plfu = getPvpLeagueForUserRetrieveUtil()
					.getUserPvpLeagueForId(attackerId);
			//check if user can search for a player to attack
			boolean legitQueueUp = checkLegitQueueUp(resBuilder, attacker, clientDate);
					//gemsSpent, cashChange);

			boolean success = false;
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			if (legitQueueUp) {
				setProspectivePvpMatches(resBuilder, attacker, uniqSeenUserIds, clientDate,
						plfu.getElo());
				
				//update the user, and his shield
				success = writeChangesToDB(attackerId, attacker, clientTime, plfu,
						currencyChange);
						//gemsSpent, cashChange, clientTime,
			}				

			if (success) {
				resBuilder.setStatus(QueueUpStatus.SUCCESS);
			}
			
			//write event to the client
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
			
			if (success) {
				//UPDATE CLIENT 
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								attacker, plfu, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);
//				
//				//TODO: tracking user currency change, among other things, if charging
//				
			}
      
		} catch (Exception e) {
			log.error("exception in QueueUp processEvent", e);
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
		} 
	}
	

	private boolean checkLegitQueueUp(Builder resBuilder, User u, Date clientDate) {
			//int gemsSpent, int cashChange) {
		if (null == u) {
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			log.error("problem with QueueUp- attacker is null. user is " + u);
			return false;
		}

		//see if user has enough money to find a person to fight
		//CHECK MONEY
//		if (!hasEnoughGems(resBuilder, u, gemsSpent, cashChange)) {
//			return false;
//		}
//
//		if (!hasEnoughCash(resBuilder, u, gemsSpent, cashChange)) {
//			return false;
//		}

		//		User queuedOpponent = queuedOpponent(attacker, elo, seenUserIds, clientDate);
		//
		//		queuedOpponentList.add(queuedOpponent);
		resBuilder.setStatus(QueueUpStatus.SUCCESS);
		return true;
	}

//	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent, int cashChange) {
//		int userGems = u.getGems();
//		//if user's aggregate gems is < cost, don't allow transaction
//		if (userGems < gemsSpent) {
//			log.error("user error: user does not have enough gems. userGems=" + userGems +
//					"\t gemsSpent=" + gemsSpent + "\t user=" + u);
//			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_GEMS);
//			return false;
//		}
//		return true;
//	}
//
//	private boolean hasEnoughCash(Builder resBuilder, User u, int gemsSpent, int cashChange) {
//		int userCash = u.getCash(); 
//		//positive 'cashChange' means refund, negative means charge user
//		int cost = -1 * cashChange;
//
//		//if user not spending gems and is just spending cash, check if he has enough
//		if (0 == gemsSpent && userCash < cost) {
//			log.error("user error: user does not have enough cash. userCash=" + userCash +
//					"\t cost=" + cost + "\t user=" + u);
//			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_CASH);
//			return false;
//		}
//		return true;
//	}
	
	private void setProspectivePvpMatches(Builder resBuilder, User attacker,
			Set<String> uniqSeenUserIds, Date clientDate, int attackerElo) {
	//so as to not recompute elo range
//		List<Integer> minEloList = new ArrayList<Integer>();
//		List<Integer> maxEloList = new ArrayList<Integer>();
//		getMinMaxElo(attackerElo, minEloList, maxEloList);
//		
//		int minElo = minEloList.get(0);
//		int maxElo = maxEloList.get(0);
		
		//just select people above and below attacker's elo
//		int minElo = Math.max(0, attackerElo - ControllerConstants.PVP__ELO_RANGE_SUBTRAHEND);
//		int maxElo = attackerElo + ControllerConstants.PVP__ELO_RANGE_ADDEND;
		Map.Entry<Integer, Integer> minAndMaxElo = MiscMethods
			.getMinAndMaxElo(attackerElo);
		int minElo = minAndMaxElo.getKey();
		int maxElo = minAndMaxElo.getValue();
		
		//ids are for convenience 
		List<String> queuedOpponentIdsList = new ArrayList<String>();
		//if want up-to-date info comment this out and query from db instead 
		Map<String, PvpUser> userIdToPvpUser = new HashMap<String, PvpUser>();
		
		//get the users that the attacker will fight
		List<User> queuedOpponents = getQueuedOpponents(attacker, attackerElo, minElo,
				maxElo, uniqSeenUserIds, clientDate, queuedOpponentIdsList,
				userIdToPvpUser);
		
		int numWanted = ControllerConstants.PVP__MAX_QUEUE_SIZE;
		List<PvpProto> pvpProtoList = new ArrayList<PvpProto>();
		
		if (null == queuedOpponents || queuedOpponents.size() < numWanted) {
			numWanted = numWanted - queuedOpponentIdsList.size();
			
			//GENERATE THE FAKE DEFENDER AND MONSTERS, not enough enemies, get fake ones
			log.info(String.format(
				"no valid users for attacker=%s", attacker));
			log.info("generating fake users.");
			Set<MonsterForPvp> fakeMonsters = getMonsterForPvpRetrieveUtils().
					retrievePvpMonsters(minElo, maxElo);
			
			//group monsters off
			//IGNORE //by 3;
			//25% of the time one monster
			//50% of the time two monsters
			//25% of the tiem three monsters
			//limit the number of groups of 3
			//NOTE: this is assuming there are more than enough monsters...
			List<List<MonsterForPvp>> fakeUserMonsters = createFakeUserMonsters(fakeMonsters,numWanted);
			
			if (!fakeUserMonsters.isEmpty())
			{
				List<PvpProto> pvpProtoListTemp = createPvpProtosFromFakeUser(fakeUserMonsters);
				pvpProtoList.addAll(pvpProtoListTemp);
			} else {
				log.error("no fake users generated. minElo={} \t maxElo={}", minElo, maxElo);
			}
			
		} 
		
		if (null != queuedOpponents && !queuedOpponents.isEmpty()) {
			log.info("there are people to attack!");
			log.info(String.format(
				"queuedOpponentIdsList=%s", queuedOpponentIdsList));
			log.info(String.format(
				"queuedOpponents:%s", queuedOpponents));
			
			Map<String, Clan> userIdToClan = getClans(queuedOpponents);
			
			/*
			Map<Integer, PvpLeagueForUser> userIdToPvpLeagueInfo = 
					getPvpLeagueForUserRetrieveUtil()
					.getUserPvpLeagueForUsers(queuedOpponentIdsList);*/
			
			//get the 3 monsters for each defender: ideally should be equipped, but 
			//will randomly select if user doesn't have 3 equipped
			Map<String, List<MonsterForUser>> userIdToUserMonsters = 
					selectMonstersForUsers(queuedOpponentIdsList);
			Map<String, Map<String, Integer>> userIdToUserMonsterIdToDroppedId =
				MonsterStuffUtils.calculatePvpDrops(userIdToUserMonsters);
			Map<String, Integer> userIdToProspectiveCashReward = new HashMap<String, Integer>();
			Map<String, Integer> userIdToProspectiveOilReward = new HashMap<String, Integer>();
			
			calculateCashOilRewards(attacker.getId(), attackerElo,
				queuedOpponents, userIdToPvpUser, userIdToProspectiveCashReward,
				userIdToProspectiveOilReward);
			
			//create the protos for all this
			List<PvpProto> pvpProtoListTemp = CreateInfoProtoUtils.createPvpProtos(
					queuedOpponents, userIdToClan, null, userIdToPvpUser,
					userIdToUserMonsters, userIdToUserMonsterIdToDroppedId,
					userIdToProspectiveCashReward, userIdToProspectiveOilReward);
			
			//user should see real people before fake ones
			pvpProtoList.addAll(0, pvpProtoListTemp);
		}
		resBuilder.addAllDefenderInfoList(pvpProtoList);
		log.info("pvpProtoList=" + pvpProtoList);
		
	}
	
	//TODO: Is getting clans necessary?
	//given bunch o users, get their clans and pair them up
	private Map<String, Clan> getClans(List<User> queuedOpponents) {
		Set<String> clanIds = new HashSet<String>();
		
		for (User u : queuedOpponents) {
		  String clanId = u.getClanId();
			
			if (clanId != null) {
				clanIds.add(clanId);
			}
		}
		
		Map<String, Clan> clanIdToClan = getClanRetrieveUtils().getClansByIds(clanIds);
		
		//pair up user and clan
		Map<String, Clan> userIdsToClans = new HashMap<String, Clan>();
		for (User u : queuedOpponents) {
		  String clanId = u.getClanId();
			
			if (clanIdToClan.containsKey(clanId)) {
				userIdsToClans.put( u.getId(), clanIdToClan.get(clanId) );
			}
		}
		return userIdsToClans;
	}
	
	/*
	//unused method. Purpose was user randomly gets users from an elo range
	//out of six possible elo ranges.
	private void getMinMaxElo(int attackerElo, List<Integer> minEloList,
			List<Integer> maxEloList) {

		int firstEloBound = Math.max(0,
				attackerElo - ControllerConstants.PVP__ELO_DISTANCE_THREE);
		int secondEloBound = Math.max(0,
				attackerElo - ControllerConstants.PVP__ELO_DISTANCE_TWO);
		int thirdEloBound = Math.max(0,
				attackerElo - ControllerConstants.PVP__ELO_DISTANCE_ONE);
		int fourthEloBound = attackerElo +
				ControllerConstants.PVP__ELO_DISTANCE_ONE;
		int fifthEloBound = attackerElo +
				ControllerConstants.PVP__ELO_DISTANCE_TWO;
		int sixthEloBound = attackerElo +
				ControllerConstants.PVP__ELO_DISTANCE_THREE;
		
		//get the min and max elo, initial values are dummy values
		int minElo = 0;
		int maxElo = attackerElo;
		Random rand = new Random();

		float randFloat = rand.nextFloat();
		if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_ONE_PAIRING_CHANCE) {
			log.info("in first elo category");
			minElo = firstEloBound;
			maxElo = secondEloBound;
			
		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_TWO_PAIRING_CHANCE) {
			log.info("in second elo category");
			minElo = secondEloBound;
			maxElo = thirdEloBound;
			
		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_THREE_PAIRING_CHANCE) {
			log.info("in third elo category");
			minElo = thirdEloBound;
			maxElo = attackerElo;
			
		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_FOUR_PAIRING_CHANCE) {
			log.info("in fourth elo category");
			
			minElo = attackerElo;
			maxElo = fourthEloBound;
			
		} else if(randFloat <
				ControllerConstants.PVP__ELO_CATEGORY_FIVE_PAIRING_CHANCE) {
			log.info("in fifth elo category");
			minElo = fourthEloBound;
			maxElo = fifthEloBound;
			
		} else {
			log.info("in sixth elo category");
			minElo = fifthEloBound;
			maxElo = sixthEloBound;
			
		}
		
		//this is to ensure that elos being searched for are not below 0
		log.info("minElo before maxing with 0: " + minElo);
		log.info("maxElo before maxing with 0: " + maxElo);
		minElo = Math.max(0, minElo);
		maxElo = Math.max(0, maxElo);
		log.info("minElo after maxing with 0: " + minElo);
		log.info("maxElo after maxing with 0: " + maxElo);
		
		minEloList.add(minElo);
		maxEloList.add(maxElo);
	}*/

	//purpose of userIdList is to prevent another iteration through the return list just
	//to extract the user ids
	private List<User> getQueuedOpponents(User attacker, int attackerElo, int minElo,
			int maxElo, Set<String> seenUserIds, Date clientDate,
			List<String> userIdList, Map<String, PvpUser> userIdToPvpUser) {
		//inefficient: 5 db calls
//		List<User> qList = getUserRetrieveUtils().retrieveCompleteQueueList(
//				attacker, elo, seenUserIds, clientDate);
//		if(qList.isEmpty()) {
//			return null;
//		}
//		else {
//			Random rand = new Random();
//			User queuedOpponent = qList.get(rand.nextInt(qList.size()));
//			return queuedOpponent;
//		}
		//		User defender = null;

		//jedis, redis stuff
//		//now having elo range, figure out the offset 
//		//could make a range call and do stuff after but eh
//		int offset = 0;
//		int limit = ControllerConstants.PVP__NUM_ENEMIES_LIMIT;
//		
//		Set<Tuple> prospectiveDefenders = getPvpUtil().getEloTopN(minElo, maxElo,
//				offset, limit);
//		
//		//go through them and select the one that has not been seen yet
//		for (Tuple t : prospectiveDefenders) {
//			int userId = Integer.valueOf(t.getElement());
//			int elo = (int) t.getScore();
//			
//			if (!seenUserIds.contains(userId)) {
//				//we have a winner!
//				defender = RetrieveUtils.userRetrieveUtils().getUserById(userId);
//			}
//		
//		
//		}
		//use hazelcast distributed map to get the defenders, limit the amount
		int numNeeded = ControllerConstants.PVP__MAX_QUEUE_SIZE;
		Set<PvpUser> prospectiveDefenders = getHazelcastPvpUtil()
				.retrievePvpUsers(minElo, maxElo, clientDate, numNeeded,
						seenUserIds); 
		
		int numDefenders = prospectiveDefenders.size();
		log.info("users returned from hazelcast pvp util. users=" + prospectiveDefenders);

		//choose users either randomly or all of them
		selectUsers(numNeeded, numDefenders, prospectiveDefenders,
				userIdList, userIdToPvpUser);
		
		List<User> selectedDefenders = new ArrayList<User>();
		if (!prospectiveDefenders.isEmpty()) {
			Map<String, User> selectedDefendersMap = getUserRetrieveUtils()
					.getUsersByIds(userIdList);
			selectedDefenders.addAll(selectedDefendersMap.values());
		}
		
		log.info("the lucky people who get to be attacked! defenders=" + selectedDefenders);
		return selectedDefenders;
	}
	
	private void selectUsers(int numNeeded, int numDefenders,
			Set<PvpUser> prospectiveDefenders, List<String> userIdList,
			Map<String, PvpUser> userIdToPvpUser) {
		Random rand = new Random();
		
		float numNeededSoFar = numNeeded;
		float numDefendersLeft = numDefenders;
		//go through them and select the one that has not been seen yet
		for (PvpUser pvpUser : prospectiveDefenders) {
			//when prospectiveDefenders is out, loop breaks,
			//regardless of numNeededSoFar
			log.info("pvp opponents, numNeeded=" + numNeededSoFar);
			log.info("pvp opponents, numAvailable=" + numDefendersLeft);
			
			String userId = pvpUser.getUserId();
			
			if (userIdList.size() >= ControllerConstants.PVP__MAX_QUEUE_SIZE) {
				//don't want to send every eligible victim to user.
				log.info("reached queue length of " + ControllerConstants.PVP__MAX_QUEUE_SIZE);
				break;
			}
			
			//if we whittle down the entire applicant pool to the minimum we want
			//select all of them
			if (numNeededSoFar >= numDefendersLeft) {
				userIdList.add(userId);
				userIdToPvpUser.put(userId, pvpUser);
				numNeededSoFar -= 1;
				numDefendersLeft -= 1;
				continue;
			}
			
			//randomly pick people
			float randFloat = rand.nextFloat();
			float probabilityToBeSelected = numNeededSoFar/numDefendersLeft;
			log.info("randFloat=" + randFloat);
			log.info("probabilityToBeSelected=" + probabilityToBeSelected);
			if (randFloat < probabilityToBeSelected) {
				//we have a winner!
				userIdList.add(userId);
				userIdToPvpUser.put(userId, pvpUser);
				numNeededSoFar -= 1;
			}
			numDefendersLeft -= 1;
		}
		

	}

	//given users, get the 3 monsters for each user
	private Map<String, List<MonsterForUser>> selectMonstersForUsers(
		List<String> userIdList)
	{
		
		//return value
		Map<String, List<MonsterForUser>> userIdsToUserMonsters =
				new HashMap<String, List<MonsterForUser>>();
		
		//for all these users, get all their complete monsters
		Map<String, Map<String, MonsterForUser>> userIdsToMfuIdsToMonsters = getMonsterForUserRetrieveUtils()
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
		Random rand = new Random();
		
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
	
	/*
	//separate monsters into groups of three, limit the number of groups of three
	private List<List<MonsterForPvp>> createFakeUserMonsters(Set<MonsterForPvp> fakeMonsters,
			int numWanted) {
		List<List<MonsterForPvp>> fakeUserMonsters = new ArrayList<List<MonsterForPvp>>();
		
		//this will contain a group of three monsters
		List<MonsterForPvp> tempFakeUser = new ArrayList<MonsterForPvp>();
		
		for (MonsterForPvp mfp : fakeMonsters) {
			
			tempFakeUser.add(mfp);
			
			//limit how many monsters can go in a group
			if (tempFakeUser.size() >= 3) {
				fakeUserMonsters.add(tempFakeUser);
				//reset the group of three
				tempFakeUser = new ArrayList<MonsterForPvp>();
				
			}
			
			//if the num groups created are more than or equal to the limit of num groups of
			//three, then exit
			if (fakeUserMonsters.size() >= numWanted) {
				break;
			}
		}
		
		return fakeUserMonsters;
	}*/
	
	private List<List<MonsterForPvp>> createFakeUserMonsters(Set<MonsterForPvp> fakeMonsters,
		int numGroupsWanted)
	{
		List<List<MonsterForPvp>> retVal = new ArrayList<List<MonsterForPvp>>();
		
		if (null == fakeMonsters || fakeMonsters.isEmpty())
		{
			return retVal;
		}
		
		Random rand = ControllerConstants.RAND;
		int numAvailableMonsters = fakeMonsters.size();
		
		//generate |numGroupsWanted| amount of groups
		//25% of the time one monster
		//50% of the time two monsters
		//25% of the time three monsters
		List<MonsterForPvp> monstersAvailable = new ArrayList<MonsterForPvp>(fakeMonsters);
		for (int i = 0; i < numGroupsWanted; i++)
		{
			//a hack to simulate the probability listed
			int numMonstersWanted = rand.nextInt(4) + 1;
			if (numMonstersWanted % 2 == 0) {
				//50 percent of time, want 2 monsters
				numMonstersWanted = 2;
			}
			
			List<MonsterForPvp> grouping = createFakeUserMonstersGrouping(
				rand, numAvailableMonsters, monstersAvailable, numMonstersWanted);
			
			retVal.add(grouping);
		}
		
		return retVal;
	}

	private List<MonsterForPvp> createFakeUserMonstersGrouping(
		Random rand,
		int numAvailableMonsters,
		List<MonsterForPvp> fakeMonstersList,
		int numMonsters )
	{
		List<MonsterForPvp> grouping = new ArrayList<MonsterForPvp>(numMonsters);
		
		for (int index = 0; index < numMonsters; index++)
		{
			int monsterIndex = rand.nextInt(numAvailableMonsters);
			MonsterForPvp mfp = fakeMonstersList.get(monsterIndex);
			grouping.add(mfp);
		}
		
		return grouping;
	}
	
	private void calculateCashOilRewards(String attackerId, int attackerElo,
		List<User> queuedOpponents, Map<String, PvpUser> userIdToPvpUser,
		Map<String, Integer> userIdToProspectiveCashReward,
		Map<String, Integer> userIdToProspectiveOilReward)
	{
		//TODO: Need to account for the user's uncollected resources
		for (User queuedOpponent : queuedOpponents) {
		  String userId = queuedOpponent.getId();
			PvpUser pu = userIdToPvpUser.get(userId);
			
			PvpBattleOutcome potentialResult = new PvpBattleOutcome(
				attackerId, attackerElo, userId, pu.getElo(),
				queuedOpponent.getCash(), queuedOpponent.getOil());
			
			userIdToProspectiveCashReward.put(userId,
				potentialResult.getUnsignedCashAttackerWins());
			
			userIdToProspectiveOilReward.put(userId, 
				potentialResult.getUnsignedOilAttackerWins());
		}
	}
	
	private List<PvpProto> createPvpProtosFromFakeUser(List<List<MonsterForPvp>> fakeUserMonsters) {
		log.info("creating fake users for pvp!!!!");
		List<PvpProto> ppList = new ArrayList<PvpProto>();
		
		for (List<MonsterForPvp> mons : fakeUserMonsters) {
			PvpProto user = createFakeUser(mons);
			ppList.add(user);
		}
		
		log.info("num fake users created: " + ppList.size());
		return ppList;
	}
	
	//CREATES ONE FAKE USER FOR PVP
	private PvpProto createFakeUser(List<MonsterForPvp> mfpList) {
		//to create the fake user, need userId=0, some random name, empty clan
		//for lvl do something like (elo / 50)
		//for cur elo avg out the monsters elos
		
		List<Integer> cashWinnings = new ArrayList<Integer>();
		List<Integer> oilWinnings = new ArrayList<Integer>();
		int avgElo = determineAvgEloAndCashOilReward(mfpList, cashWinnings, oilWinnings);
		
		String userId = null;
		String randomName = getHazelcastPvpUtil().getRandomName();
		int lvl = avgElo / ControllerConstants.PVP__FAKE_USER_LVL_DIVISOR;
		
		int prospectiveCashWinnings = cashWinnings.get(0);
		int prospectiveOilWinnings = oilWinnings.get(0);
		
		log.info("fake user created: name={} \t avgElo={} \t cash={} \t oil={} \t lvl={}",
			new Object[] { randomName, avgElo, prospectiveCashWinnings,
			prospectiveOilWinnings, lvl } );
		
		List<Integer> monsterIdsDropped = calculateDrops(mfpList);
		
		//it's important that monsterIdsDropped be in the same order
		//as mfpList
		PvpProto fakeUser = CreateInfoProtoUtils.createFakePvpProto(userId,
			randomName, lvl, avgElo, prospectiveCashWinnings,
			prospectiveOilWinnings, mfpList, monsterIdsDropped);
		return fakeUser;
	}
	
	//assumes the List<MonsterForPvp> mons is not empty
	private int determineAvgEloAndCashOilReward(List<MonsterForPvp> mons,
			List<Integer> cashWinnings, List<Integer> oilWinnings) {
		int avgElo = 0;
		int prospectiveCashWinnings = 0;
		int prospectiveOilWinnings = 0;
		
		for (MonsterForPvp mon : mons) {
			avgElo += mon.getElo();
			
			prospectiveCashWinnings += mon.getCashDrop();
			prospectiveOilWinnings += mon.getOilDrop();
		}
		
		avgElo = avgElo / mons.size();
		cashWinnings.add(prospectiveCashWinnings);
		oilWinnings.add(prospectiveOilWinnings);
		
		return avgElo;
	}
	
	private List<Integer> calculateDrops(List<MonsterForPvp> mfpList)
	{
		List<Integer> monsterDropIds = new ArrayList<Integer>();
		
		for (MonsterForPvp mfp : mfpList) {
			int monsterId = mfp.getMonsterId();
			int monserLvl = mfp.getMonsterLvl();
			
			boolean monsterDropped = MonsterLevelInfoRetrieveUtils
				.didPvpMonsterDrop(monsterId, monserLvl);
			
			int monsterDropId = ControllerConstants.NOT_SET;
			
			Monster mon = MonsterRetrieveUtils
				.getMonsterForMonsterId(monsterId); 
			if (monsterDropped) {
				monsterDropId = mon.getPvpMonsterDropId();
			}
			
			log.info("for fake monster {}, set pvpMonsterDropId={}",
				mfp, monsterDropId);
			monsterDropIds.add(monsterDropId);
		}
		
		return monsterDropIds;
	}
	
	// remove his shield if he has one, since he is going to attack some one
	private boolean writeChangesToDB(String attackerId, User attacker, //int gemsSpent, int cashChange, 
			Timestamp queueTime, PvpLeagueForUser plfu, Map<String, Integer> money) {
		
//		//CHARGE THE USER
//		int oilChange = 0;
//		int gemChange = -1 * gemsSpent;
//		
//		int numChange = attacker.updateRelativeCashAndOilAndGems(cashChange, oilChange, gemChange); 
//		if (1 != numChange) {
//			log.error("problem with updating user stats: gemChange=" + gemChange
//					+ ", cashChange=" + cashChange + ", user is " + attacker);
//			return false;
//		} else {
//			//everything went well
//			if (0 != oilChange) {
//				money.put(MiscMethods.cash, cashChange);
//			}
//			if (0 != gemsSpent) {
//				money.put(MiscMethods.gems, gemChange);
//			}
//		}
		//update the user who queued things

		//TODO: this is the same logic in BeginPvpBattleController 
		//turn off user's shield if he has one active
		Date curShieldEndTime = plfu.getShieldEndTime();
		Date queueDate = new Date(queueTime.getTime());
		if (getTimeUtils().isFirstEarlierThanSecond(queueDate, curShieldEndTime)) {
			log.info("shield end time is now being reset since he's attacking with a shield");
			log.info("1cur pvpuser=" + getHazelcastPvpUtil().getPvpUser(attackerId));
			Date login = attacker.getLastLogin();
			Timestamp loginTime = new Timestamp(login.getTime());
			UpdateUtils.get().updatePvpLeagueForUserShields(attackerId,
					loginTime, loginTime);
			
			PvpUser attackerOpu = new PvpUser(plfu);
			attackerOpu.setShieldEndTime(login);
			attackerOpu.setInBattleEndTime(login);               
			getHazelcastPvpUtil().replacePvpUser(attackerOpu, attackerId);
			log.info("2cur pvpuser=" + getHazelcastPvpUtil().getPvpUser(attackerId));
			log.info("(should be same as 2cur pvpUser) 3cur pvpuser=" + attackerOpu);
		}
		
		return true;
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public MonsterForPvpRetrieveUtils getMonsterForPvpRetrieveUtils() {
		return monsterForPvpRetrieveUtils;
	}

	public void setMonsterForPvpRetrieveUtils(
			MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtils) {
		this.monsterForPvpRetrieveUtils = monsterForPvpRetrieveUtils;
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

  public ClanRetrieveUtils2 getClanRetrieveUtils() {
    return clanRetrieveUtils;
  }

  public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
    this.clanRetrieveUtils = clanRetrieveUtils;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }

  public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
    return monsterForUserRetrieveUtils;
  }

  public void setMonsterForUserRetrieveUtils(
      MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
    this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
  }

}
