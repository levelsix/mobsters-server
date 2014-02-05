package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import com.lvl6.info.User;
import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementResponseProto.SubmitMonsterEnhancementStatus;
import com.lvl6.proto.EventPvpProto.QueueUpRequestProto;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto.QueueUpStatus;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
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

	public void setInsertUtils(InsertUtil insertUtils) {
		this.insertUtils = insertUtils;
	}

	@Autowired
	protected LeaderBoardUtil leaderUtil;
	public void setLeaderUtil(LeaderBoardUtil leaderUtil) {
		this.leaderUtil = leaderUtil;
	}

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
			
			//prevent another db call to fetch the defender/victim for the client to attack
			List<User> queuedOpponentList = new ArrayList<User>();

			//check if user can search for a player to attack
			boolean legitQueueUp = checkLegitQueueUp(resBuilder, attacker, cashChange, clientDate);

//			boolean success = false;
//			if (legitQueueUp) {
//				int cost = costList.get(0);
//				User queuedOpponent = queuedOpponentList.get(0);
//				success = writeChangesToDB(attacker, cost, queueTime, queuedOpponent);
//				
//			}
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
			
//			if (success) {
//				//UPDATE CLIENT 
//				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
//						.createUpdateClientUserResponseEventAndUpdateLeaderboard(attacker);
//				resEventUpdate.setTag(event.getTag());
//				server.writeEvent(resEventUpdate);
//				
//				//TODO: TRACKING USER CURRENCY CHANGE, AMONG OTHER THINGS
//				
//			}
      
		} catch (Exception e) {
			log.error("exception in QueueUp processEvent", e);
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
		} 
	}

	private User queuedOpponent(User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		List<User> qList = getUserRetrieveUtils().retrieveCompleteQueueList(
				attacker, elo, seenUserIds, clientDate);

		if(qList.isEmpty()) {
			return null;
		}
		else {
			Random rand = new Random();
			User queuedOpponent = qList.get(rand.nextInt(qList.size()));
			return queuedOpponent;
		}
	}


	private boolean checkLegitQueueUp(Builder resBuilder, User attacker, int cashChange,
			Date clientDate) {
		if (attacker == null) {
			resBuilder.setStatus(QueueUpStatus.FAIL_OTHER);
			log.error("problem with QueueUp- attacker is null. attacker is " + attacker);
			return false;
		}
		
		//see if user has enough money to find a person to fight
//		if (!hasEnoughOil(resBuilder, attacker, gemsSpent, cashChange)) {
//			return false;
//		}

		
//		User queuedOpponent = queuedOpponent(attacker, elo, seenUserIds, clientDate);
//
//		queuedOpponentList.add(queuedOpponent);
		resBuilder.setStatus(QueueUpStatus.SUCCESS);
		return true;
	}
	
	private boolean hasEnoughCash(Builder resBuilder, User u, int cashChange,
			int gemsSpent, Map<Long, UserEnhancementItemProto> deleteMap,
			Map<Long, UserEnhancementItemProto> updateMap,
			Map<Long, UserEnhancementItemProto> newMap) {
		int userOil = u.getOil(); 
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * cashChange;
		
		//if user not spending gems and is just spending cash, check if he has enough
		if (0 == gemsSpent && userOil < cost) {
			log.error("user error: user does not have enough oil. userOil=" + userOil +
					"\t cost=" + cost + "\t deleteMap=" + deleteMap + "\t newMap=" +
					newMap + "\t updateMap=" + updateMap + "\t user=" + u);
			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_CASH);
			return false;
		}
		return true;
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


}
