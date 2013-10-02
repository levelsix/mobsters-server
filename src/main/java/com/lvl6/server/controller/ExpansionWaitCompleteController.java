package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ExpansionWaitCompleteRequestEvent;
import com.lvl6.events.response.ExpansionWaitCompleteResponseEvent;
import com.lvl6.info.User;
import com.lvl6.info.UserCityExpansionData;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventProto.ExpansionWaitCompleteRequestProto;
import com.lvl6.proto.EventProto.ExpansionWaitCompleteResponseProto;
import com.lvl6.proto.EventProto.ExpansionWaitCompleteResponseProto.Builder;
import com.lvl6.proto.EventProto.ExpansionWaitCompleteResponseProto.ExpansionWaitCompleteStatus;
import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.UserCityExpansionDataRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

/*
 * NOT READY/BEING USED YET
 */

@Component @DependsOn("gameServer") public class ExpansionWaitCompleteController extends EventController{

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public ExpansionWaitCompleteController() {
		numAllocatedThreads = 1;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ExpansionWaitCompleteRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_EXPANSION_WAIT_COMPLETE_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		ExpansionWaitCompleteRequestProto reqProto = ((ExpansionWaitCompleteRequestEvent)event).getExpansionWaitCompleteRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		Timestamp clientTime = new Timestamp(reqProto.getCurTime());
		boolean speedUp = reqProto.getSpeedUp();
		int xPosition = reqProto.getXPosition();
		int yPosition = reqProto.getYPosition();

		ExpansionWaitCompleteResponseProto.Builder resBuilder = ExpansionWaitCompleteResponseProto.newBuilder();
		resBuilder.setSender(senderProto);

		server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

		try {
			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
//			List<UserCityExpansionData> userCityExpansionDataList = UserCityExpansionDataRetrieveUtils.getUserCityExpansionDatasForUserId(senderProto.getUserId());
			UserCityExpansionData uced = UserCityExpansionDataRetrieveUtils.getSpecificUserCityExpansionDataForUserIdAndPosition(userId, xPosition, yPosition);
			boolean legitExpansionComplete = checkLegitExpansionComplete(user, resBuilder, uced, clientTime, speedUp, xPosition, yPosition);
			int previousGold = 0;

			ExpansionWaitCompleteResponseEvent resEvent = new ExpansionWaitCompleteResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setExpansionWaitCompleteResponseProto(resBuilder.build());  

			if (legitExpansionComplete) {
				previousGold = user.getDiamonds();

				Map<String, Integer> money = new HashMap<String, Integer>();
				writeChangesToDB(user, uced, speedUp, money, clientTime, xPosition, yPosition);
				writeToUserCurrencyHistory(user, clientTime, money, previousGold, xPosition, yPosition);
				resBuilder.setUcedp(CreateInfoProtoUtils.createUserCityExpansionDataProtoFromUserCityExpansionData(uced));
			}
			server.writeEvent(resEvent);
		} catch (Exception e) {
			log.error("exception in ExpansionWaitCompleteController processEvent", e);
		} finally {
			server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}

	private void writeChangesToDB(User user, UserCityExpansionData userCityExpansionData, boolean speedUp, 
			Map<String, Integer> money, Timestamp clientTime, int xPosition, int yPosition) {
		
		if (!UpdateUtils.get().updateUserCityExpansionData(user.getId(), xPosition, yPosition, false)) {
			log.error("problem with resolving expansion");
		}

		if (speedUp) {
			int diamondChange = -expansionDiamondCost(user.getId(), userCityExpansionData, clientTime);
			if (!user.updateRelativeDiamondsNaive(diamondChange)) {
				log.error("problem updating user diamonds");
			} else {
				//everything went ok
				money.put(MiscMethods.gold, diamondChange);
			}
		}
	}

	private boolean checkLegitExpansionComplete(User user, Builder resBuilder, UserCityExpansionData userCityExpansionData,
			Timestamp clientTime, boolean speedUp, int xPosition, int yPosition) {
		if (!MiscMethods.checkClientTimeAroundApproximateNow(clientTime)) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.CLIENT_TOO_APART_FROM_SERVER_TIME);
			log.error("user error: user deviates too much from current time.");
			return false;
		}
		
		if (userCityExpansionData==null) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.WAS_NOT_EXPANDING);
			log.error("unexpected error: user has no expansion pending");
			return false;
		}
		if(userCityExpansionData.getxPosition() != xPosition || userCityExpansionData.getyPosition() != yPosition) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.OTHER_FAIL);
			log.error("unexpected error: x and y coordinates sent don't match db." +
					" userCityExpansionData=" + userCityExpansionData + "x=" + xPosition +
					" yPosition=" + yPosition);
			return false;
		}
		
		if (!speedUp && userCityExpansionData.getExpandStartTime().getTime() 
				+ 60000*calculateMinutesForCurrentExpansion(user.getId(), userCityExpansionData) > clientTime.getTime()) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.NOT_DONE_YET);
			log.error("client error: time is out of sync. Client incorrectly thinks" +
					" that the expansion is done. userCityExpansionData=" + 
					userCityExpansionData);
			return false;      
		}
		int cost = expansionDiamondCost(user.getId(), userCityExpansionData, clientTime);
		if (speedUp && user.getDiamonds() < cost) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.OTHER_FAIL);
			log.error("user error: user does not have enough gold to speed up expansion." +
					" userCityExpansionData=" + userCityExpansionData + "cost=" + cost);
			return false;      
		}
		resBuilder.setStatus(ExpansionWaitCompleteStatus.SUCCESS);
		return true;  
	}

//	private UserCityExpansionData getUserCityExpansionDataCurrentlyExpanding(List<UserCityExpansionData> userCityExpansionDataList) {
//		UserCityExpansionData uced = null;
//		for(UserCityExpansionData uced2 : userCityExpansionDataList) {
//			if(uced2.isExpanding()) {
//				uced = uced2;
//				return uced;
//			}
//		}
//		return null;
//	}
	
	private int calculateMinutesForCurrentExpansion(int userId, UserCityExpansionData userCityExpansionData) {
		int numCompletedExpansionsSoFar = UserCityExpansionDataRetrieveUtils.numberOfUserExpansions(userId);
		return (ControllerConstants.EXPANSION_WAIT_COMPLETE__HOUR_CONSTANT + 
				ControllerConstants.EXPANSION_WAIT_COMPLETE__HOUR_INCREMENT_BASE*(numCompletedExpansionsSoFar + 1))*60;
	}

	private int calculateExpansionSpeedupCost(int userId, UserCityExpansionData userCityExpansionData) {
		return calculateMinutesForCurrentExpansion(userId, userCityExpansionData)/ControllerConstants.EXPANSION_WAIT_COMPLETE__BASE_MINUTES_TO_ONE_GOLD;
	}

	private int expansionDiamondCost(int userId, UserCityExpansionData userCityExpansionData, Timestamp clientTime) {
		long timePassed = (clientTime.getTime() - userCityExpansionData.getExpandStartTime().getTime())/1000;
		long timeRemaining = calculateMinutesForCurrentExpansion(userId, userCityExpansionData)*60;
		double percentRemaining = timeRemaining/(double)(timeRemaining+timePassed);
		double speedUpConstant = 1+ControllerConstants.EXPANSION_LATE_SPEEDUP_CONSTANT*(1-percentRemaining);

		int diamondCost = (int)Math.ceil(speedUpConstant*percentRemaining*calculateExpansionSpeedupCost(userId, userCityExpansionData));
		return diamondCost;

	}

	private void writeToUserCurrencyHistory(User aUser, Timestamp date, Map<String, Integer> money,
			int previousGold, int xPosition, int yPosition) {
		Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		String gold = MiscMethods.gold;

		previousGoldSilver.put(gold, previousGold);

		String reasonForChange = ControllerConstants.UCHRFC__EXPANSION_WAIT_COMPLETE;
		reasonsForChanges.put(gold, reasonForChange + "xPosition: " + xPosition + " yPosition: " + yPosition);
		

		MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, date, money, previousGoldSilver, reasonsForChanges);
	}
}

