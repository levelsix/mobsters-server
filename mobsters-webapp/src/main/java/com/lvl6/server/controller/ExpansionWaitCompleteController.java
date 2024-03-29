//package com.lvl6.server.controller;
//
//import java.sql.Timestamp;
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
//import com.lvl6.events.request.ExpansionWaitCompleteRequestEvent;
//import com.lvl6.events.response.ExpansionWaitCompleteResponseEvent;
//import com.lvl6.info.ExpansionCost;
//import com.lvl6.info.ExpansionPurchaseForUser;
//import com.lvl6.info.User;
//import com.lvl6.misc.MiscMethods;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.proto.CityProto.UserCityExpansionDataProto;
//import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteRequestProto;
//import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteResponseProto;
//import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteResponseProto.Builder;
//import com.lvl6.proto.EventStructureProto.ExpansionWaitCompleteResponseProto.ExpansionWaitCompleteStatus;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.ExpansionPurchaseForUserRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.ExpansionCostRetrieveUtils;
//import com.lvl6.server.Locker;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.RetrieveUtils;
//import com.lvl6.utils.utilmethods.UpdateUtils;
//
//
//@Component  public class ExpansionWaitCompleteController extends EventController{
//
//	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//	@Autowired
//	protected Locker locker;
//
//	public ExpansionWaitCompleteController() {
//		
//	}
//
//	@Override
//	public RequestEvent createRequestEvent() {
//		return new ExpansionWaitCompleteRequestEvent();
//	}
//
//	@Override
//	public EventProtocolRequest getEventType() {
//		return EventProtocolRequest.C_EXPANSION_WAIT_COMPLETE_EVENT;
//	}
//
//	@Override
//	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
//		ExpansionWaitCompleteRequestProto reqProto = ((ExpansionWaitCompleteRequestEvent)event).getExpansionWaitCompleteRequestProto();
//
//		MinimumUserProto senderProto = reqProto.getSender();
//		String userId = senderProto.getUserUuid();
//		Timestamp clientTime = new Timestamp(reqProto.getCurTime());
//		int xPosition = reqProto.getXPosition();
//		int yPosition = reqProto.getYPosition();
//		boolean speedUp = reqProto.getSpeedUp();
//		int gemCostToSpeedup = reqProto.getGemCostToSpeedup();
//
//		ExpansionWaitCompleteResponseProto.Builder resBuilder = ExpansionWaitCompleteResponseProto.newBuilder();
//		resBuilder.setSender(senderProto);
//		resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_OTHER);
//
//		getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
//		try {
//			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());
//			List<ExpansionPurchaseForUser> epfuList = ExpansionPurchaseForUserRetrieveUtils
//					.getUserCityExpansionDatasForUserId(userId);
//			ExpansionPurchaseForUser epfu = selectSpecificExpansion(xPosition, yPosition,
//					epfuList);
//			
//			boolean legitExpansionComplete = checkLegitExpansionComplete(user, resBuilder,
//					epfuList, epfu, clientTime, speedUp, gemCostToSpeedup);
//			int previousGems = 0;
//
//
//			boolean success = false;
//			Map<String, Integer> money = new HashMap<String, Integer>();
//			if (legitExpansionComplete) {
//				previousGems = user.getGems();
//				success = writeChangesToDB(user, epfu, speedUp, money, clientTime, gemCostToSpeedup);
//			}
//			
//			if (success) {
//				UserCityExpansionDataProto ucedp = CreateInfoProtoUtils
//						.createUserCityExpansionDataProtoFromUserCityExpansionData(epfu); 
//				resBuilder.setUcedp(ucedp);
//			}
//			
//			ExpansionWaitCompleteResponseEvent resEvent = new ExpansionWaitCompleteResponseEvent(senderProto.getUserUuid());
//			resEvent.setTag(event.getTag());
//			resEvent.setResponseProto(resBuilder.build());  
//			responses.normalResponseEvents().add(resEvent);
//			
//			if (success) {
//				writeToUserCurrencyHistory(userId, user, xPosition, yPosition,
//						clientTime, money, previousGems);
//				UserCityExpansionDataProto ucedp = CreateInfoProtoUtils
//						.createUserCityExpansionDataProtoFromUserCityExpansionData(epfu); 
//				resBuilder.setUcedp(ucedp);
//			}
//			
//		} catch (Exception e) {
//			log.error("exception in ExpansionWaitCompleteController processEvent", e);
//			//don't let the client hang
//      try {
//      	resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_OTHER);
//      	ExpansionWaitCompleteResponseEvent resEvent = new ExpansionWaitCompleteResponseEvent(userId);
//      	resEvent.setTag(event.getTag());
//      	resEvent.setResponseProto(resBuilder.build());
//      	responses.normalResponseEvents().add(resEvent);
//      } catch (Exception e2) {
//      	log.error("exception2 in ExpansionWaitCompleteController processEvent", e);
//      }
//		} finally {
//			getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());      
//		}
//	}
//
//	private ExpansionPurchaseForUser selectSpecificExpansion(int xPosition, int yPosition,
//			List<ExpansionPurchaseForUser> epfuList) {
//		
//		//if there aren't any expansion purchases return null;
//		if (null == epfuList || epfuList.isEmpty()) {
//			return null;
//		}
//		
//		//go through each of the user's expansion purchases get the one with the
//		//corresponding x and y coordinates
//		for (ExpansionPurchaseForUser epfu : epfuList) {
//			int x = epfu.getxPosition();
//			int y = epfu.getyPosition();
//			
//			if (x == xPosition && y == yPosition) {
//				return epfu;
//			}
//		}
//		
//		return null;
//	}
//	
//	private boolean checkLegitExpansionComplete(User user, Builder resBuilder,
//			List<ExpansionPurchaseForUser> epfuList, ExpansionPurchaseForUser userCityExpansionData,
//			Timestamp clientTime, boolean speedUp, int gemCostToSpeedup) {
//		int nthExpansion = epfuList.size();
//		
//		if (userCityExpansionData==null) {
//			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_WAS_NOT_EXPANDING);
//			log.error("unexpected error: user has no expansion pending");
//			return false;
//		}
//		long curTimeMillis = clientTime.getTime();
//		
//		ExpansionCost ec = ExpansionCostRetrieveUtils.getCityExpansionCostById(nthExpansion);
//		int numMinutes = ec.getNumMinutesToExpand();
//		
//		//check if user has waited long enough to complete expansion (sans using gems)
//		long expandStartMillis = userCityExpansionData.getExpandStartTime().getTime();
//		long millisForExpansion = 60000 * numMinutes;
//		long expandEndMillis = expandStartMillis + millisForExpansion;
//		if (!speedUp && expandEndMillis > curTimeMillis) {
//			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_NOT_DONE_YET);
//			log.error("client error: time is out of sync. Client incorrectly thinks" +
//					" that the expansion is done. userCityExpansionData=" + 
//					userCityExpansionData);
//			return false;      
//		}
//		
//		if (speedUp && user.getGems() < gemCostToSpeedup) {
//			resBuilder.setStatus(ExpansionWaitCompleteStatus.FAIL_INSUFFICIENT_GEMS);
//			log.error("user error: user does not have enough gems to speed up expansion." +
//					" userCityExpansionData=" + userCityExpansionData + "cost=" + gemCostToSpeedup);
//			return false;      
//		}
//		
//		resBuilder.setStatus(ExpansionWaitCompleteStatus.SUCCESS);
//		return true;  
//	}
//
//	private boolean writeChangesToDB(User user, ExpansionPurchaseForUser epfu, boolean speedup, 
//			Map<String, Integer> money, Timestamp clientTime, int gemCost) {
//		if (speedup) {
//			int gemChange = -1 * gemCost;
//			if (!user.updateRelativeGemsNaive(gemChange)) {
//				log.error("problem updating user gems. gemChange=" + gemChange);
//				return false;
//			} else {
//				//everything went ok
//				money.put(MiscMethods.gems, gemChange);
//			}
//		}
//		
//		int xPosition = epfu.getxPosition();
//		int yPosition = epfu.getyPosition();
//		if (!UpdateUtils.get().updateUserCityExpansionData(user.getId(),
//				xPosition, yPosition, false, clientTime)) {
//			log.error("problem with resolving expansion. expansion=" + epfu +
//					"\t speedup=" + speedup + "\t user=" + user);
//			return false;
//		}
//		return true;
//	}
//
//	private void writeToUserCurrencyHistory(int userId, User aUser,
//			int xPosition, int yPosition, Timestamp date,
//			Map<String, Integer> currencyChange, int previousGems) {
//		if (currencyChange.isEmpty()) {
//			return;
//		}
//		String gems = MiscMethods.gems;
//		
//		String reason = ControllerConstants.UCHRFC__EXPANSION_WAIT_COMPLETE; 
//		StringBuilder detailsSb = new StringBuilder();
//		detailsSb.append("xPos=");
//		detailsSb.append(xPosition);
//		detailsSb.append(", yPos=");
//		detailsSb.append(yPosition);
//		String details = detailsSb.toString();
//		
//		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
//		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
//		Map<String, String> reasonsForChanges = new HashMap<String, String>();
//		Map<String, String> detailsMap = new HashMap<String, String>();
//
////		if (currencyChange.containsKey(gems)) {
//		previousCurrency.put(gems, previousGems);
//		currentCurrency.put(gems, aUser.getGems());
//		reasonsForChanges.put(gems, reason);
//		detailsMap.put(gems, details);
////		}
//		
//		MiscMethods.writeToUserCurrencyOneUser(userId, date, currencyChange, 
//				previousCurrency, currentCurrency, reasonsForChanges, detailsMap);
//		
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
//
