package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PurchaseCityExpansionRequestEvent;
import com.lvl6.events.response.BeginDungeonResponseEvent;
import com.lvl6.events.response.PurchaseCityExpansionResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.CityExpansionCost;
import com.lvl6.info.User;
import com.lvl6.info.UserCityExpansionData;
import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventProto.PurchaseCityExpansionRequestProto;
import com.lvl6.proto.EventProto.PurchaseCityExpansionResponseProto;
import com.lvl6.proto.EventProto.PurchaseCityExpansionResponseProto.Builder;
import com.lvl6.proto.EventProto.PurchaseCityExpansionResponseProto.PurchaseCityExpansionStatus;
import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.UserCityExpansionDataRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityExpansionCostRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

/*
 * NOT READY/BEING USED YET
 */

@Component @DependsOn("gameServer") public class PurchaseCityExpansionController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected LeaderBoardUtil leaderboard;

	public LeaderBoardUtil getLeaderboard() {
		return leaderboard;
	}

	public void setLeaderboard(LeaderBoardUtil leaderboard) {
		this.leaderboard = leaderboard;
	}

	public PurchaseCityExpansionController() {
		numAllocatedThreads = 1;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new PurchaseCityExpansionRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_PURCHASE_CITY_EXPANSION_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		PurchaseCityExpansionRequestProto reqProto = ((PurchaseCityExpansionRequestEvent)event).getPurchaseCityExpansionRequestProto();

		//variables client sent
		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		//in relation to center square (the origin 0,0)
		int xPosition = reqProto.getXPosition();
		int yPosition = reqProto.getYPosition();
		Timestamp timeOfPurchase = new Timestamp(reqProto.getTimeOfPurchase());

		PurchaseCityExpansionResponseProto.Builder resBuilder = PurchaseCityExpansionResponseProto.newBuilder();
		resBuilder.setSender(senderProto);

		//so someone doesn't steal user's silver during transaction
		server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
		try {
			
			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
			List<UserCityExpansionData> userCityExpansionDataList =
					UserCityExpansionDataRetrieveUtils.getUserCityExpansionDatasForUserId(userId);
			//used to calculate cost for buying expansion
			int numExpansions = 0;
			if (null != userCityExpansionDataList) {
				numExpansions = userCityExpansionDataList.size();
			}
			
			List<Integer> cityExpansionCostList = new ArrayList<Integer>();
			boolean legitExpansion = checkLegitExpansion(resBuilder, timeOfPurchase, user,
					userCityExpansionDataList, numExpansions, cityExpansionCostList);

			//write to the client
			PurchaseCityExpansionResponseEvent resEvent = new PurchaseCityExpansionResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setPurchaseCityExpansionResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (legitExpansion) {
				//update database tables
				int previousSilver = user.getCoins() + user.getVaultBalance();
				int cost = cityExpansionCostList.get(0);
				
				Map<String, Integer> currencyChange = new HashMap<String, Integer>();
				writeChangesToDB(user, timeOfPurchase, xPosition, yPosition, true,
						numExpansions, cost, currencyChange);
				
				//modified user object, need to update the client to reflect this
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
				UserCityExpansionData uced = UserCityExpansionDataRetrieveUtils.getSpecificUserCityExpansionDataForUserIdAndPosition(user.getId(), xPosition, yPosition);
				resBuilder.setUcedp(CreateInfoProtoUtils.createUserCityExpansionDataProtoFromUserCityExpansionData(uced));
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				//writeToUserCurrencyHistory(user, timeOfPurchase, xPosition, yPosition, currencyChange, previousSilver);
			}
		} catch (Exception e) {
			log.error("exception in PurchaseCityExpansion processEvent", e);
			try {
    	  resBuilder.setStatus(PurchaseCityExpansionStatus.OTHER_FAIL);
    	  PurchaseCityExpansionResponseEvent resEvent = new PurchaseCityExpansionResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setPurchaseCityExpansionResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in BeginDungeonController processEvent", e);
      }
		} finally {
			server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}

	private void writeChangesToDB(User user, Timestamp timeOfPurchase, int xPosition,
			int yPosition, boolean isExpanding, int numOfExpansions, int cost,
			Map<String, Integer> currencyChange) {

		int coinChange = cost * -1;
		if (!user.updateRelativeCoinsNaive(coinChange)) {
			log.error("problem with updating coins purchasing a city expansion");

		} else {
			//everything went ok
			if (!InsertUtils.get().insertUserCityExpansionData(user.getId(), timeOfPurchase, 
				      xPosition, yPosition, isExpanding)) {
				log.error("problem with updating user expansion info after purchase");
			}
			currencyChange.put(MiscMethods.silver, coinChange);
		}
	}

	private boolean checkLegitExpansion(Builder resBuilder, Timestamp timeOfPurchase, User user, 
			List<UserCityExpansionData> userCityExpansionDataList, int numOfExpansions,
			List<Integer> costList) {

		if (!MiscMethods.checkClientTimeAroundApproximateNow(timeOfPurchase)) {
			resBuilder.setStatus(PurchaseCityExpansionStatus.CLIENT_TOO_APART_FROM_SERVER_TIME);
			return false;
		}

		boolean isExpanding = false;
		//loop through each expansion and see if any expansions are still expanding
		if (userCityExpansionDataList != null) {
			for(UserCityExpansionData uced : userCityExpansionDataList) {
				if(uced.isExpanding()) {
					isExpanding = true;
					break;
				}
			}
		}
		
		//user cannot buy another expansion while an expansion is still being constructed
		if(isExpanding) {
			log.error("user has a current expansion going on still");
			resBuilder.setStatus(PurchaseCityExpansionStatus.ALREADY_EXPANDING);
			return false;
		}

		//see if user has enough to buy next expansion
		int cost = calculateExpansionCost(numOfExpansions + 1);
		if (user.getCoins() < cost) {
			resBuilder.setStatus(PurchaseCityExpansionStatus.NOT_ENOUGH_COINS);
			return false;            
		}
		
		costList.add(cost);
		resBuilder.setStatus(PurchaseCityExpansionStatus.SUCCESS);
		return true;  
	}
	
	private int calculateExpansionCost(int numOfExpansions) {
		CityExpansionCost cec = CityExpansionCostRetrieveUtils.getCityExpansionCostById(numOfExpansions);
		//log.info("cec=" + cec);
		//log.info("all expansion stuff" + CityExpansionCostRetrieveUtils.getAllExpansionNumsToCosts());
		return cec.getExpansionCost();
	}

	public void writeToUserCurrencyHistory(User aUser, Timestamp date, int xPosition, int yPosition,
			Map<String, Integer> goldSilverChange, int previousSilver) {
		Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		String silver = MiscMethods.silver;
		String reasonForChange = "Expanding xPosition: " + xPosition + ", yPosition: " + yPosition;

		previousGoldSilver.put(silver, previousSilver);
		reasonsForChanges.put(silver, reasonForChange);

		MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, date, goldSilverChange,
				previousGoldSilver, reasonsForChanges);
	}
}
