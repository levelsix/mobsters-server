package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ObstacleRemovalCompleteRequestEvent;
import com.lvl6.events.response.ObstacleRemovalCompleteResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteRequestProto;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto.ObstacleRemovalCompleteStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ObstacleForUserRetrieveUtil;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;


@Component @DependsOn("gameServer") public class ObstacleRemovalCompleteController extends EventController{

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public ObstacleRemovalCompleteController() {
		numAllocatedThreads = 4;
	}
	
	@Autowired
	protected ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil;
	public ObstacleForUserRetrieveUtil getObstacleForUserRetrieveUtil() {
		return obstacleForUserRetrieveUtil;
	}
	public void setObstacleForUserRetrieveUtil(
			ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil) {
		this.obstacleForUserRetrieveUtil = obstacleForUserRetrieveUtil;
	}
	

	@Override
	public RequestEvent createRequestEvent() {
		return new ObstacleRemovalCompleteRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_OBSTACLE_REMOVAL_COMPLETE_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		ObstacleRemovalCompleteRequestProto reqProto = ((ObstacleRemovalCompleteRequestEvent)event).getObstacleRemovalCompleteRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		Timestamp clientTime = new Timestamp(reqProto.getCurTime());
		boolean speedUp = reqProto.getSpeedUp();
		int gemCostToSpeedUp = reqProto.getGemsSpent();
		int userObstacleId = reqProto.getUserObstacleId();

		ObstacleRemovalCompleteResponseProto.Builder resBuilder = ObstacleRemovalCompleteResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ObstacleRemovalCompleteStatus.FAIL_OTHER);

		server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

		try {
			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
			ObstacleForUser ofu = getObstacleForUserRetrieveUtil().
					getUserObstacleForId(userObstacleId);
			
			boolean legitExpansionComplete = checkLegit(resBuilder, userId, user,
					userObstacleId, ofu, speedUp, gemCostToSpeedUp);
			int previousGems = 0;

			boolean success = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			if (legitExpansionComplete) {
				previousGems = user.getGems();
				success = writeChangesToDB(user, userObstacleId, speedUp, gemCostToSpeedUp,
						clientTime, money);
			}
			
			ObstacleRemovalCompleteResponseEvent resEvent = new ObstacleRemovalCompleteResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setObstacleRemovalCompleteResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
			
			if (success && speedUp) {
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	
				writeToUserCurrencyHistory(userId, user, clientTime, money, previousGems, ofu);
			}
			
		} catch (Exception e) {
			log.error("exception in ObstacleRemovalCompleteController processEvent", e);
			//don't let the client hang
      try {
      	resBuilder.setStatus(ObstacleRemovalCompleteStatus.FAIL_OTHER);
      	ObstacleRemovalCompleteResponseEvent resEvent = new ObstacleRemovalCompleteResponseEvent(userId);
      	resEvent.setTag(event.getTag());
      	resEvent.setObstacleRemovalCompleteResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in ObstacleRemovalCompleteController processEvent", e);
      }
		} finally {
			server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}
	private boolean writeChangesToDB(User user, int ofuId, boolean speedUp, 
			int gemCost, Timestamp clientTime, Map<String, Integer> money) {
		if (speedUp) {
			int gemChange = -1 * gemCost;
			if (!user.updateRelativeGemsNaive(gemChange)) {
				log.error("problem updating user gems. gemChange=" + gemChange);
				return false;
			} else {
				//everything went ok
				money.put(MiscMethods.gems, gemChange);
			}
		}
		
		int numDeleted = DeleteUtils.get().deleteObstacleForUser(ofuId);
		log.info("(obstacles) numDeleted=" + numDeleted);
		return true;
	}

	private boolean checkLegit(Builder resBuilder, int userId, User user,
			int ofuId, ObstacleForUser ofu, boolean speedUp, int gemCostToSpeedup) {
		
		if (null == user || null == ofu) {
			resBuilder.setStatus(ObstacleRemovalCompleteStatus.FAIL_OTHER);
			log.error("unexpected error: user or obstacle for user is null. user=" + user +
					"\t userId=" + userId + "\t obstacleForUser=" + ofu + "\t ofuId=" + ofuId);
			return false;
		}
		
		if (speedUp && user.getGems() < gemCostToSpeedup) {
			resBuilder.setStatus(ObstacleRemovalCompleteStatus.FAIL_INSUFFICIENT_GEMS);
			log.error("user error: user does not have enough gems to speed up removal." +
					"\t obstacleForUser=" + ofu + "\t cost=" + gemCostToSpeedup);
			return false;      
		}
		
		resBuilder.setStatus(ObstacleRemovalCompleteStatus.SUCCESS);
		return true;  
	}

	private void writeToUserCurrencyHistory(int userId, User user, Timestamp curTime,
			Map<String, Integer> currencyChange, int previousGems, ObstacleForUser ofu) {
		String reason = ControllerConstants.UCHRFC__SPED_UP_REMOVE_OBSTACLE;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("obstacleId=");
		detailsSb.append(ofu.getObstacleId());
		detailsSb.append(" x=");
		detailsSb.append(ofu.getXcoord());
		detailsSb.append(" y=");
		detailsSb.append(ofu.getYcoord());
		String details = detailsSb.toString();
		
		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MiscMethods.gems;
		
		previousCurrency.put(gems, previousGems);
		currentCurrency.put(gems, user.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, details);

		MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
        previousCurrency, currentCurrency, reasonsForChanges, detailsMap);

	}
}

