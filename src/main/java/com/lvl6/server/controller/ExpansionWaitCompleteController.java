package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ExpansionWaitCompleteRequestEvent;
import com.lvl6.events.response.ExpansionWaitCompleteResponseEvent;
import com.lvl6.info.ExpansionCost;
import com.lvl6.info.ExpansionPurchaseForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.CityProto.UserCityExpansionDataProto;
import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteRequestProto;
import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteResponseProto;
import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteResponseProto.ExpansionWaitCompleteStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ExpansionPurchaseForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ExpansionCostRetrieveUtils;
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
		resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_OTHER);

		server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

		try {
			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
			List<ExpansionPurchaseForUser> epfuList = ExpansionPurchaseForUserRetrieveUtils
					.getUserCityExpansionDatasForUserId(userId);
			ExpansionPurchaseForUser epfu = selectSpecificExpansion(xPosition, yPosition,
					epfuList);
			
			List<Integer> gemCostList = new ArrayList<Integer>();
			boolean legitExpansionComplete = checkLegitExpansionComplete(user, resBuilder,
					epfuList, epfu, clientTime, speedUp, gemCostList);
			int previousGems = 0;


			if (legitExpansionComplete) {
				previousGems = user.getGems();
				int nthExpansion = epfuList.size();
				int gemCost = gemCostList.get(0);
				Map<String, Integer> money = new HashMap<String, Integer>();
				writeChangesToDB(user, epfu, speedUp, money, clientTime, nthExpansion, gemCost);
				writeToUserCurrencyHistory(user, clientTime, money, previousGems, xPosition, yPosition);
				UserCityExpansionDataProto ucedp = CreateInfoProtoUtils
						.createUserCityExpansionDataProtoFromUserCityExpansionData(epfu); 
				resBuilder.setUcedp(ucedp);
			}
			ExpansionWaitCompleteResponseEvent resEvent = new ExpansionWaitCompleteResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setExpansionWaitCompleteResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
			
		} catch (Exception e) {
			log.error("exception in ExpansionWaitCompleteController processEvent", e);
			//don't let the client hang
      try {
      	resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_OTHER);
      	ExpansionWaitCompleteResponseEvent resEvent = new ExpansionWaitCompleteResponseEvent(userId);
      	resEvent.setTag(event.getTag());
      	resEvent.setExpansionWaitCompleteResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in ExpansionWaitCompleteController processEvent", e);
      }
		} finally {
			server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}

	private void writeChangesToDB(User user, ExpansionPurchaseForUser epfu, boolean speedup, 
			Map<String, Integer> money, Timestamp clientTime, int nthExpansion, int gemCost) {
		int xPosition = epfu.getxPosition();
		int yPosition = epfu.getyPosition();
		if (!UpdateUtils.get().updateUserCityExpansionData(user.getId(),
				xPosition, yPosition, false, clientTime)) {
			log.error("problem with resolving expansion. expansion=" + epfu +
					"\t speedup=" + speedup + "\t user=" + user);
		}

		if (!speedup) {
			return;
		}
		int gemChange = -1 * gemCost;
		if (!user.updateRelativeDiamondsNaive(gemChange)) {
			log.error("problem updating user gems");
		} else {
			//everything went ok
			money.put(MiscMethods.gems, gemChange);
		}
	}

	private ExpansionPurchaseForUser selectSpecificExpansion(int xPosition, int yPosition,
			List<ExpansionPurchaseForUser> epfuList) {
		
		//if there aren't any expansion purchases return null;
		if (null == epfuList || epfuList.isEmpty()) {
			return null;
		}
		
		//go through each of the user's expansion purchases get the one with the
		//corresponding x and y coordinates
		for (ExpansionPurchaseForUser epfu : epfuList) {
			int x = epfu.getxPosition();
			int y = epfu.getyPosition();
			
			if (x == xPosition && y == yPosition) {
				return epfu;
			}
		}
		
		return null;
	}
	
	private boolean checkLegitExpansionComplete(User user, Builder resBuilder,
			List<ExpansionPurchaseForUser> epfuList, ExpansionPurchaseForUser userCityExpansionData,
			Timestamp clientTime, boolean speedUp, List<Integer> gemCostList) {
		int nthExpansion = epfuList.size();
		
		if (userCityExpansionData==null) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_WAS_NOT_EXPANDING);
			log.error("unexpected error: user has no expansion pending");
			return false;
		}
		long curTimeMillis = clientTime.getTime();
		
		ExpansionCost ec = ExpansionCostRetrieveUtils.getCityExpansionCostById(nthExpansion);
		int numMinutes = ec.getNumMinutesToExpand();
		int gemCost = ec.getSpeedupExpansionGemCost();
		
		//check if user has waited long enough to complete expansion (sans using gems)
		long expandStartMillis = userCityExpansionData.getExpandStartTime().getTime();
		long millisForExpansion = 60000 * numMinutes;
		long expandEndMillis = expandStartMillis + millisForExpansion;
		if (!speedUp && expandEndMillis > curTimeMillis) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_NOT_DONE_YET);
			log.error("client error: time is out of sync. Client incorrectly thinks" +
					" that the expansion is done. userCityExpansionData=" + 
					userCityExpansionData);
			return false;      
		}
		
		long secondsForExpansion = millisForExpansion/1000;
		int newGemCost = MiscMethods.speedupCostOverTime(gemCost, expandStartMillis,
				secondsForExpansion, curTimeMillis);
		if (speedUp && user.getGems() < newGemCost) {
			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_INSUFFICIENT_GEMS);
			log.error("user error: user does not have enough gems to speed up expansion." +
					" userCityExpansionData=" + userCityExpansionData + "cost=" + newGemCost);
			return false;      
		}
		
		gemCostList.add(newGemCost);
		resBuilder.setStatus(ExpansionWaitCompleteStatus.SUCCESS);
		return true;  
	}

//	private int expansionGemCost(int nthExpansion, long expandStartMillis, Timestamp clientTime) {
//		ExpansionCost ec = ExpansionCostRetrieveUtils.getCityExpansionCostById(nthExpansion);
//		int gemCost = ec.getExpansionCostGems();
//		
//		long timePassedSeconds = (clientTime.getTime() - expandStartMillis)/1000;
//		long numSecondsForExpansion = calculateMinutesForCurrentExpansion(nthExpansion)*60;
//		long timeRemainingSeconds = numSecondsForExpansion - timePassedSeconds;
//		
//		double percentRemaining = timeRemainingSeconds/(double)(numSecondsForExpansion);
//		
//		gemCost = (int)Math.ceil(gemCost * percentRemaining);
//		return gemCost;
//	}

	//TODO: IMPLEMENT THIS
	private void writeToUserCurrencyHistory(User aUser, Timestamp date, Map<String, Integer> money,
			int previousGems, int xPosition, int yPosition) {
//		Map<String, Integer> previousGemsSilver = new HashMap<String, Integer>();
//		Map<String, String> reasonsForChanges = new HashMap<String, String>();
//		String gems = MiscMethods.gems;
//
//		previousGemsSilver.put(gems, previousGems);
//
//		String reasonForChange = ControllerConstants.UCHRFC__EXPANSION_WAIT_COMPLETE;
//		reasonsForChanges.put(gems, reasonForChange + "xPosition: " + xPosition + " yPosition: " + yPosition);
//		
//
//		MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, money, previousGemsSilver, reasonsForChanges);
	}
}

