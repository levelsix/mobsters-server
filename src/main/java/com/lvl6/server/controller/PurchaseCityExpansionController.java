//package com.lvl6.server.controller;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.events.RequestEvent;
//import com.lvl6.events.request.PurchaseCityExpansionRequestEvent;
//import com.lvl6.events.response.PurchaseCityExpansionResponseEvent;
//import com.lvl6.events.response.UpdateClientUserResponseEvent;
//import com.lvl6.info.ExpansionCost;
//import com.lvl6.info.User;
//import com.lvl6.info.ExpansionPurchaseForUser;
//import com.lvl6.leaderboards.LeaderBoardUtil;
//import com.lvl6.misc.MiscMethods;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.proto.EventCityProto.PurchaseCityExpansionRequestProto;
//import com.lvl6.proto.EventCityProto.PurchaseCityExpansionResponseProto;
//import com.lvl6.proto.EventCityProto.PurchaseCityExpansionResponseProto.Builder;
//import com.lvl6.proto.EventCityProto.PurchaseCityExpansionResponseProto.PurchaseCityExpansionStatus;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.ExpansionPurchaseForUserRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.ExpansionCostRetrieveUtils;
//import com.lvl6.server.Locker;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.RetrieveUtils;
//import com.lvl6.utils.utilmethods.InsertUtils;
//
///*
// * NOT READY/BEING USED YET
// */
//
//@Component @DependsOn("gameServer") public class PurchaseCityExpansionController extends EventController {
//
//	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//	@Autowired
//	protected Locker locker;
//
//	@Autowired
//	protected LeaderBoardUtil leaderboard;
//
//	public LeaderBoardUtil getLeaderboard() {
//		return leaderboard;
//	}
//
//	public void setLeaderboard(LeaderBoardUtil leaderboard) {
//		this.leaderboard = leaderboard;
//	}
//
//	public PurchaseCityExpansionController() {
//		
//	}
//
//	@Override
//	public RequestEvent createRequestEvent() {
//		return new PurchaseCityExpansionRequestEvent();
//	}
//
//	@Override
//	public EventProtocolRequest getEventType() {
//		return EventProtocolRequest.C_PURCHASE_CITY_EXPANSION_EVENT;
//	}
//
//	@Override
//	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
//		PurchaseCityExpansionRequestProto reqProto = ((PurchaseCityExpansionRequestEvent)event).getPurchaseCityExpansionRequestProto();
//
//		//variables client sent
//		MinimumUserProto senderProto = reqProto.getSender();
//		int userId = senderProto.getUserUuid();
//		//in relation to center square (the origin 0,0)
//		int xPosition = reqProto.getXPosition();
//		int yPosition = reqProto.getYPosition();
//		Timestamp timeOfPurchase = new Timestamp(reqProto.getTimeOfPurchase());
//
//		PurchaseCityExpansionResponseProto.Builder resBuilder = PurchaseCityExpansionResponseProto.newBuilder();
//		resBuilder.setSender(senderProto);
//
//		//so someone doesn't steal user's silver during transaction
//		getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
//		try {
//			
//			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());
//			List<ExpansionPurchaseForUser> userCityExpansionDataList =
//					ExpansionPurchaseForUserRetrieveUtils.getUserCityExpansionDatasForUserId(userId);
//			//used to calculate cost for buying expansion
//			int numExpansions = 0;
//			if (null != userCityExpansionDataList) {
//				numExpansions = userCityExpansionDataList.size();
//			}
//			
//			List<Integer> cityExpansionCostList = new ArrayList<Integer>();
//			boolean legitExpansion = checkLegitExpansion(resBuilder, timeOfPurchase, user,
//					userCityExpansionDataList, numExpansions, cityExpansionCostList);
//
//			//write to the client
//			PurchaseCityExpansionResponseEvent resEvent = new PurchaseCityExpansionResponseEvent(senderProto.getUserUuid());
//			resEvent.setTag(event.getTag());
//			resEvent.setPurchaseCityExpansionResponseProto(resBuilder.build());  
//			responses.normalResponseEvents().add(resEvent);
//
//			if (legitExpansion) {
//				//update database tables
//				int previousCash = user.getCash();
//				int cost = cityExpansionCostList.get(0);
//				
//				Map<String, Integer> currencyChange = new HashMap<String, Integer>();
//				writeChangesToDB(user, timeOfPurchase, xPosition, yPosition, true,
//						numExpansions, cost, currencyChange);
//				
//				//modified user object, need to update the client to reflect this
//				//null PvpLeagueFromUser means will pull from hazelcast instead
//				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
//						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
//								user, null);
//				ExpansionPurchaseForUser uced =
//						ExpansionPurchaseForUserRetrieveUtils
//						.getSpecificUserCityExpansionDataForUserIdAndPosition(
//								user.getId(), xPosition, yPosition);
//				resBuilder.setUcedp(CreateInfoProtoUtils
//						.createUserCityExpansionDataProtoFromUserCityExpansionData(uced));
//				resEventUpdate.setTag(event.getTag());
//				responses.normalResponseEvents().add(resEventUpdate);
//
//				writeToUserCurrencyHistory(user, timeOfPurchase, xPosition,
//						yPosition, previousCash, currencyChange);
//			}
//		} catch (Exception e) {
//			log.error("exception in PurchaseCityExpansion processEvent", e);
//			try {
//    	  resBuilder.setStatus(PurchaseCityExpansionStatus.OTHER_FAIL);
//    	  PurchaseCityExpansionResponseEvent resEvent = new PurchaseCityExpansionResponseEvent(userId);
//    	  resEvent.setTag(event.getTag());
//    	  resEvent.setPurchaseCityExpansionResponseProto(resBuilder.build());
//    	  responses.normalResponseEvents().add(resEvent);
//      } catch (Exception e2) {
//    	  log.error("exception2 in BeginDungeonController processEvent", e);
//      }
//		} finally {
//			getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());      
//		}
//	}
//
//	private void writeChangesToDB(User user, Timestamp timeOfPurchase, int xPosition,
//			int yPosition, boolean isExpanding, int numOfExpansions, int cost,
//			Map<String, Integer> currencyChange) {
//
//		int cashChange = cost * -1;
//		if (!user.updateRelativeCashNaive(cashChange)) {
//			log.error("problem with updating cash purchasing a city expansion");
//
//		} else {
//			//everything went ok
//			if (!InsertUtils.get().insertUserCityExpansionData(user.getId(), timeOfPurchase, 
//				      xPosition, yPosition, isExpanding)) {
//				log.error("problem with updating user expansion info after purchase");
//			}
//			currencyChange.put(MiscMethods.cash, cashChange);
//		}
//	}
//
//	private boolean checkLegitExpansion(Builder resBuilder, Timestamp timeOfPurchase, User user, 
//			List<ExpansionPurchaseForUser> userCityExpansionDataList, int numOfExpansions,
//			List<Integer> costList) {
//
////		if (!MiscMethods.checkClientTimeAroundApproximateNow(timeOfPurchase)) {
////			resBuilder.setStatus(PurchaseCityExpansionStatus.CLIENT_TOO_APART_FROM_SERVER_TIME);
////			return false;
////		}
//
//		boolean isExpanding = false;
//		//loop through each expansion and see if any expansions are still expanding
//		if (userCityExpansionDataList != null) {
//			for(ExpansionPurchaseForUser uced : userCityExpansionDataList) {
//				if(uced.isExpanding()) {
//					isExpanding = true;
//					break;
//				}
//			}
//		}
//		
//		//user cannot buy another expansion while an expansion is still being constructed
//		if(isExpanding) {
//			log.error("user has a current expansion going on still");
//			resBuilder.setStatus(PurchaseCityExpansionStatus.ALREADY_EXPANDING);
//			return false;
//		}
//
//		//see if user has enough to buy next expansion
//		int cost = calculateExpansionCost(numOfExpansions + 1);
//		if (user.getCash() < cost) {
//			resBuilder.setStatus(PurchaseCityExpansionStatus.NOT_ENOUGH_COINS);
//			return false;            
//		}
//		
//		costList.add(cost);
//		resBuilder.setStatus(PurchaseCityExpansionStatus.SUCCESS);
//		return true;  
//	}
//	
//	private int calculateExpansionCost(int numOfExpansions) {
//		ExpansionCost cec = ExpansionCostRetrieveUtils.getCityExpansionCostById(numOfExpansions);
//		//log.info("cec=" + cec);
//		//log.info("all expansion stuff" + CityExpansionCostRetrieveUtils.getAllExpansionNumsToCosts());
//		return cec.getExpansionCostCash();
//	}
//
//	public void writeToUserCurrencyHistory(User aUser, Timestamp date,
//			int xPosition, int yPosition, int previousCash,
//			Map<String, Integer> changeMap) {
//		
//		StringBuilder detailSb = new StringBuilder();
//		detailSb.append("Expanding xPosition: ");
//		detailSb.append(xPosition);
//		detailSb.append(", yPosition: ");
//		detailSb.append(yPosition);
//		
//		int userId = aUser.getId();
//	    Map<String, Integer> previousCurencyMap = new HashMap<String, Integer>();
//	    Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
//	    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//	    Map<String, String> details = new HashMap<String, String>();
//	    String cash = MiscMethods.cash;
//	    String reasonForChange = ControllerConstants.UCHRFC__PURCHASE_CITY_EXPANSION;
//		
//
//	    previousCurencyMap.put(cash, previousCash);
//	    currentCurrencyMap.put(cash, aUser.getCash());
//		reasonsForChanges.put(cash, reasonForChange);
//		details.put(cash, detailSb.toString());
//
//		MiscMethods.writeToUserCurrencyOneUser(userId, date, changeMap,
//				previousCurencyMap, currentCurrencyMap, reasonsForChanges,
//	    		details);
//	}
//
//	public Locker getLocker() {
//		return locker;
//	}
//
//	public void setLocker(Locker locker) {
//		this.locker = locker;
//	}
//	
//}
