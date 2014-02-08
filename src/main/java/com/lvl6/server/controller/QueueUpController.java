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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QueueUpRequestEvent;
import com.lvl6.events.response.QueueUpResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterForUser;
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
import com.lvl6.pvp.HazelcastPvpUtil.OfflinePvpUser;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;


@Component @DependsOn("gameServer") public class QueueUpController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected InsertUtil insertUtils;

	@Autowired
	protected UserRetrieveUtils userRetrieveUtils;

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;
	
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

		MinimumUserProto attackerProto = reqProto.getAttacker();
		int attackerId = attackerProto.getUserId();
		int attackerElo = reqProto.getAttackerElo();
		
		//positive means refund, negative means charge user; don't forsee being positive
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int cashChange = reqProto.getCashChange();
		
		List<Integer> seenUserIds = reqProto.getSeenUserIdsList();
		Set<Integer> uniqSeenUserIds = new HashSet<Integer>(seenUserIds);
		Date clientDate = new Date(reqProto.getClientTime());
		
		//set some values to send to the client
		QueueUpResponseProto.Builder resBuilder = QueueUpResponseProto.newBuilder();
		resBuilder.setAttacker(attackerProto);
		resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);

		try {
			User attacker = RetrieveUtils.userRetrieveUtils().getUserById(attackerId);
			
			//check if user can search for a player to attack
			boolean legitQueueUp = checkLegitQueueUp(resBuilder, attacker, clientDate,
					gemsSpent, cashChange);

			boolean success = false;
			if (legitQueueUp) {
				//get the users that the attacker will fight
				List<Integer> queuedOpponentIdsList = new ArrayList<Integer>();
				List<User> queuedOpponents = getQueuedOpponent(attacker, attackerElo,
						uniqSeenUserIds, clientDate, queuedOpponentIdsList);
				
				if (null == queuedOpponents || queuedOpponents.isEmpty()) {
					//TODO: generate the fake defender and monsters
					log.info("no valid users for attacker=" + attacker);
				} else {
					log.info("there are people to attack!");
					log.info("queuedOpponentIdsList=" + queuedOpponentIdsList);
					log.info("queuedOpponents:" + queuedOpponents);
					//get the 3 monsters for each defender: ideally should be equipped, but 
					//will randomly select if user doesn't have 3 equipped
					Map<Integer, List<MonsterForUser>> userIdToUserMonsters = 
							selectMonstersForUsers(queuedOpponentIdsList);
					
					Map<Integer, Integer> userIdToProspectiveCashReward = new HashMap<Integer, Integer>();
					Map<Integer, Integer> userIdToProspectiveOilReward = new HashMap<Integer, Integer>();
					
					calculateCashOilRewards(queuedOpponents, userIdToProspectiveCashReward,
							userIdToProspectiveOilReward);
					
					//create the protos for all this
					List<PvpProto> pvpProtoList = CreateInfoProtoUtils.createPvpProtos(
							queuedOpponents, userIdToUserMonsters, userIdToProspectiveCashReward,
							userIdToProspectiveOilReward);
					
					resBuilder.addAllDefenderInfoList(pvpProtoList);
					log.info("pvpProtoList=" + pvpProtoList);
				}
				
				resBuilder.setStatus(QueueUpStatus.SUCCESS);
				success = true;
			}
//			
//			if (success) {
//				//send to the client the player he can attack
//				User queuedOpponent = queuedOpponentList.get(0);
//				MinimumUserProto mup =
//						CreateInfoProtoUtils.createMinimumUserProtoFromUser(queuedOpponent);
//				resBuilder.setDefender(mup);			
//				
//				//set how many coins the user can win
//				int winnings = calculatePossibleCoinWinnings(queuedOpponent);
//				resBuilder.setPossibleCoinReward(winnings);
//			} else {
//				resBuilder.setStatus(QueueUpStatus.OTHER_FAIL);
//			}
			
			//write event to the client
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
			
			if (success) {
				//UPDATE CLIENT 
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(attacker);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);
//				
//				//TODO: TRACKING USER CURRENCY CHANGE, AMONG OTHER THINGS
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
	

	private boolean checkLegitQueueUp(Builder resBuilder, User u, Date clientDate,
			int gemsSpent, int cashChange) {
		if (null == u) {
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			log.error("problem with QueueUp- attacker is null. user is " + u);
			return false;
		}

		//see if user has enough money to find a person to fight
		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, cashChange)) {
			return false;
		}

		if (!hasEnoughCash(resBuilder, u, gemsSpent, cashChange)) {
			return false;
		}

		//		User queuedOpponent = queuedOpponent(attacker, elo, seenUserIds, clientDate);
		//
		//		queuedOpponentList.add(queuedOpponent);
		resBuilder.setStatus(QueueUpStatus.SUCCESS);
		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent, int cashChange) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent + "\t user=" + u);
			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughCash(Builder resBuilder, User u, int gemsSpent, int cashChange) {
		int userCash = u.getCash(); 
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * cashChange;

		//if user not spending gems and is just spending cash, check if he has enough
		if (0 == gemsSpent && userCash < cost) {
			log.error("user error: user does not have enough cash. userCash=" + userCash +
					"\t cost=" + cost + "\t user=" + u);
			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_CASH);
			return false;
		}
		return true;
	}

	//purpose of userIdList is to prevent another iteration through the return list just
	//to extract the user ids
	private List<User> getQueuedOpponent(User attacker, int attackerElo,
			Set<Integer> seenUserIds, Date clientDate, List<Integer> userIdList) {
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
		
		int firstEloBound = Math.max(0, attackerElo - ControllerConstants.PVP__ELO_DISTANCE_THREE);
		int secondEloBound = Math.max(0, attackerElo - ControllerConstants.PVP__ELO_DISTANCE_TWO);
		int thirdEloBound = Math.max(0, attackerElo - ControllerConstants.PVP__ELO_DISTANCE_ONE);
		int fourthEloBound = attackerElo + ControllerConstants.PVP__ELO_DISTANCE_ONE;
		int fifthEloBound = attackerElo + ControllerConstants.PVP__ELO_DISTANCE_TWO;
		int sixthEloBound = attackerElo + ControllerConstants.PVP__ELO_DISTANCE_THREE;
		
		//get the min and max elo, initial values are dummy values
		int minElo = 0;
		int maxElo = attackerElo;
		Random rand = new Random();

		float randFloat = rand.nextFloat();
		if(randFloat < ControllerConstants.PVP__ELO_CATEGORY_ONE_PAIRING_CHANCE) {
			minElo = firstEloBound;
			maxElo = secondEloBound;
			
		} else if(randFloat < ControllerConstants.PVP__ELO_CATEGORY_TWO_PAIRING_CHANCE) {
			minElo = secondEloBound;
			maxElo = thirdEloBound;
			
		} else if(randFloat < ControllerConstants.PVP__ELO_CATEGORY_THREE_PAIRING_CHANCE) {
			minElo = thirdEloBound;
			maxElo = attackerElo;
			
		} else if(randFloat < ControllerConstants.PVP__ELO_CATEGORY_FOUR_PAIRING_CHANCE) {
			minElo = attackerElo;
			maxElo = fourthEloBound;
			
		} else if(randFloat < ControllerConstants.PVP__ELO_CATEGORY_FIVE_PAIRING_CHANCE) {
			minElo = fourthEloBound;
			maxElo = fifthEloBound;
			
		} else {
			minElo = fifthEloBound;
			maxElo = sixthEloBound;
			
		}
		
//		User defender = null;

		//jedis, redis stuff
//		//now having elo range, figure out the offset 
//		//could make a range call and do stuff after but eh
//		int offset = 0;
//		int limit = ControllerConstants.PVP__NUM_ENEMIES_LIMIT;
//		
//		Set<Tuple> prospectiveDefenders = getPvpUtil().getOfflineEloTopN(minElo, maxElo,
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
		List<User> selectedDefenders = new ArrayList<User>();
		
		//use hazelcast distributed map to get the defenders  (size of this must be humongous
		//since it contains every valid offline user between min and max elo...)
		Set<OfflinePvpUser> prospectiveDefenders = getHazelcastPvpUtil()
				.retrieveOfflinePvpUsers(minElo, maxElo, clientDate); 
		
		log.info("users returned from hazelcast pvp util. users=" + prospectiveDefenders);
		
		//go through them and select the one that has not been seen yet
		for (OfflinePvpUser pvpUser : prospectiveDefenders) {
			int userId = Integer.valueOf(pvpUser.getUserId());
			
			if (!seenUserIds.contains(userId)) {
				//we have a winner!
				User defender = RetrieveUtils.userRetrieveUtils().getUserById(userId);
				selectedDefenders.add(defender);
				userIdList.add(userId);
			}
		}

		log.info("the lucky people who get to be attacked! defenders=" + selectedDefenders);
		return selectedDefenders;
	}

	//given users, get the 3 monsters for each user
	private Map<Integer, List<MonsterForUser>> selectMonstersForUsers(
			List<Integer> userIdList) {
		
		//return value
		Map<Integer, List<MonsterForUser>> userIdsToUserMonsters =
				new HashMap<Integer, List<MonsterForUser>>();
		
		//for all these users, get all their complete monsters
		Map<Integer, Map<Long, MonsterForUser>> userIdsToMfuIdsToMonsters = RetrieveUtils
				.monsterForUserRetrieveUtils().getCompleteMonstersForUser(userIdList);
		
		
		for (int index = 0; index < userIdList.size(); index++) {
			//extract a user's monsters
			int defenderId = userIdList.get(index);
			Map<Long, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters.get(defenderId);
			
			//try to select at most 3 monsters for this user
			List<MonsterForUser> defenderMonsters = selectMonstersForUser(mfuIdsToMonsters);
			
			//if the user still doesn't have 3 monsters, then too bad
			userIdsToUserMonsters.put(defenderId, defenderMonsters);
		}
		
		return userIdsToUserMonsters;
	}
	
	private List<MonsterForUser> selectMonstersForUser(Map<Long, MonsterForUser> mfuIdsToMonsters) {

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
	
	
	private List<MonsterForUser> getEquippedMonsters(Map<Long, MonsterForUser> userMonsters) {
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

	private void getRandomMonsters(Map<Long, MonsterForUser> possibleMonsters,
			List<MonsterForUser> defenderMonsters) {
		
		Map<Long, MonsterForUser> possibleMonstersTemp =
				new HashMap<Long, MonsterForUser>(possibleMonsters);
		
		//remove the defender monsters from possibleMonstersTemp, since defenderMonsters
		//were already selected from possibleMonsters
		for (MonsterForUser m : defenderMonsters) {
			long mfuId = m.getId();
			
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
	
	private void calculateCashOilRewards(List<User> queuedOpponents,
			Map<Integer, Integer> userIdToProspectiveCashReward,
			Map<Integer, Integer> userIdToProspectiveOilReward) {
		
		for (User queuedOpponent : queuedOpponents) {
			int userId = queuedOpponent.getId();
			
			int cashReward = calculateCashRewardFromUser(queuedOpponent);
			int oilReward = calculateOilRewardFromUser(queuedOpponent);
			
			userIdToProspectiveCashReward.put(userId, cashReward);
			userIdToProspectiveOilReward.put(userId, oilReward);
		}
	}
	
	private int calculateCashRewardFromUser(User queuedOpponent) {
		int cash = queuedOpponent.getCash();
		int cashLost = (int) ControllerConstants.PVP__PERCENT_CASH_LOST * cash;
		
		log.info("amount cash user will lose: " + cashLost + "\t user=" + queuedOpponent);
		
		return cashLost;
	}

	private int calculateOilRewardFromUser(User queuedOpponent) {
		int oil = queuedOpponent.getOil();
		int oilLost = (int) ControllerConstants.PVP__PERCENT_OIL_LOST * oil;
		
		log.info("amount cash user will lose: " + oilLost + "\t user=" + queuedOpponent);
		
		return oilLost;
	}
	
	
	// change user silver value and remove his shield if he has one, since he is
	// going to attack some one
	private boolean writeChangesToDB(User attacker, int cost, Timestamp queueTime,
			User queuedUser) {
		
		boolean success = queuedUser.updateLastTimeQueued(queueTime);
		if(success) {
			//only change user shield stuff, no wins, loss flees
			return attacker.updateRelativeExperienceCoinsBattlesWonBattlesLostFlees (
					0, -1*cost, 0, 0, 0, queueTime, true, false, false, 0, 0, 0, 0);  
		}
		else {
			log.error("unexpected error: could not update user's last queue time");
			return false;
		}
	}

	private int calculatePossibleCoinWinnings(User personToBeAttacked) {
		//TODO: DETERMINE HOW THIS IS CALCULATED
		return 1000;
	}


	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}
	
	public InsertUtil getInsertUtils() {
		return insertUtils;
	}

	public void setInsertUtils(InsertUtil insertUtils) {
		this.insertUtils = insertUtils;
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}


}
