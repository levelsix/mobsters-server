package com.lvl6.server.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Random;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QueueUpRequestEvent;
import com.lvl6.events.response.QueueUpResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;

import com.lvl6.info.Location;

import com.lvl6.info.User;

import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventProto.QuestAcceptResponseProto.QuestAcceptStatus;
import com.lvl6.proto.EventProto.QueueUpRequestProto;
import com.lvl6.proto.EventProto.QueueUpResponseProto;
import com.lvl6.proto.EventProto.QueueUpResponseProto.QueueUpStatus;
import com.lvl6.proto.EventProto.QueueUpResponseProto.Builder;

import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.InfoProto.UserType;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.UserRetrieveUtils;

import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;
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
		//client keeps adding to this list, prevents same users coming up in queue
		List<Integer> seenUserIds = reqProto.getSeenUserIdsList();
		long millis = reqProto.getClientTime();
		Date clientDate = new Date(millis);
		Timestamp queueTime = new Timestamp(millis);
		
		//set some values to send to the client
		QueueUpResponseProto.Builder resBuilder = QueueUpResponseProto.newBuilder();
		resBuilder.setAttacker(attackerProto);
		resBuilder.setStatus(QueueUpStatus.OTHER_FAIL);

		try {
			User attacker = RetrieveUtils.userRetrieveUtils().getUserById(attackerProto.getUserId());
			int attackerElo = attacker.getElo();
			
			boolean legitQueueUp = checkLegitQueueUp(resBuilder, attacker, attackerElo, seenUserIds, clientDate);

			//check if user can search for a player to attack
			if (legitQueueUp) {
				writeChangesToDB(attacker, attackerElo, queueTime);
				User defender = queuedOpponent(attacker, attackerElo, seenUserIds, clientDate);
				resBuilder.setDefender(CreateInfoProtoUtils.createMinimumUserProtoFromUser(defender));
			}
			
			//write event to the client
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			resEvent.setQueueUpResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
			
			//UPDATE CLIENT 
			UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(attacker);
			resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
      
		} catch (Exception e) {
			log.error("exception in QueueUp processEvent", e);
			resBuilder.setStatus(QueueUpStatus.OTHER_FAIL);
			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerId);
			resEvent.setTag(event.getTag());
			
		} 
	}


	private int calculateQueueCost(User attacker, int elo) {
		//TODO: make formula for queue search cost factoring in user lvl and elo
		return 1000;
	}


	private User queuedOpponent(User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		List<User> qList = getUserRetrieveUtils().retrieveCompleteQueueList(attacker, elo, seenUserIds, clientDate);

		if(qList.size()==0) {
			return null;
		}
		else {
			Random rand = new Random();
			User queuedOpponent = qList.get(rand.nextInt(qList.size()));
			return queuedOpponent;
		}
	}


	private boolean checkLegitQueueUp(Builder resBuilder, User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		if (attacker == null) {
			resBuilder.setStatus(QueueUpStatus.OTHER_FAIL);
			log.error("problem with QueueUp- attacker is null. attacker is " + attacker);
			return false;
		}
		if (attacker.getCoins() < calculateQueueCost(attacker, attacker.getElo())) {
			resBuilder.setStatus(QueueUpStatus.FAIL_NOT_ENOUGH_SILVER);
			log.error("problem with QueueUp- attacker doesn't have enough silver to search queue");
			return false;
		}

		User queuedOpponent = queuedOpponent(attacker, elo, seenUserIds, clientDate);
		if(queuedOpponent==null) {
			resBuilder.setStatus(QueueUpStatus.FAIL_CANT_FIND_ANYONE);
			log.error("no users to match up with");
			return false;
		}

		resBuilder.setStatus(QueueUpStatus.SUCCESS);
		return true;
	}

	// change user silver value and remove his shield if he has one
	private boolean writeChangesToDB(User attacker, int elo, List<Integer> seenUserIds, boolean firstTimeQueue, Timestamp queueTime) {
		int queueCost = 0;
		if(firstTimeQueue) {
			queueCost = 0;
		}
		else queueCost = calculateQueueCost(attacker, elo);
		User queuedUser = queuedOpponent(attacker, elo, seenUserIds, queueTime);
		boolean success = queuedUser.updateLastQueueTime(queueTime);
		if(success) {
			return attacker.updateRelativeEnergyExperienceCoinsBattlesWonBattlesLostFleesSimulateEnergyRefill (
					0, 0, -1*queueCost, 0, 0, 0,  false, queueTime, true, false, false);  
		}
		else return false;
	}



	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}


}
