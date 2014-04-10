package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginObstacleRemovalRequestEvent;
import com.lvl6.events.response.BeginObstacleRemovalResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalRequestProto;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto.BeginObstacleRemovalStatus;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ObstacleForUserRetrieveUtil;
import com.lvl6.server.Locker;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;


@Component
public class BeginObstacleRemovalController extends EventController{

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected Locker locker;

	@Autowired
	protected ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil;
	
	public BeginObstacleRemovalController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new BeginObstacleRemovalRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BEGIN_OBSTACLE_REMOVAL_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		BeginObstacleRemovalRequestProto reqProto = ((BeginObstacleRemovalRequestEvent)event).getBeginObstacleRemovalRequestProto();
		log.info("reqProto=" + reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		Timestamp clientTime = new Timestamp(reqProto.getCurTime());
		int gemsSpent = reqProto.getGemsSpent();
		int resourceChange = reqProto.getResourceChange();
		ResourceType rt = reqProto.getResourceType();
		int userObstacleId = reqProto.getUserObstacleId();

		BeginObstacleRemovalResponseProto.Builder resBuilder = BeginObstacleRemovalResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(BeginObstacleRemovalStatus.FAIL_OTHER);

		getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

		try {
			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
			ObstacleForUser ofu = getObstacleForUserRetrieveUtil().
					getUserObstacleForId(userObstacleId);
			
			boolean legitComplete = checkLegit(resBuilder, userId, user, userObstacleId, ofu,
					gemsSpent, resourceChange, rt);

			boolean success = false;
			//make it easier to record currency history
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
			if (legitComplete) {
				success = writeChangesToDB(user, userObstacleId, gemsSpent, resourceChange, rt,
						clientTime, currencyChange, previousCurrency);
			}
			
			BeginObstacleRemovalResponseEvent resEvent = new BeginObstacleRemovalResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setBeginObstacleRemovalResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
			
			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	
				writeToUserCurrencyHistory(userId, user, clientTime, currencyChange,
						previousCurrency, ofu, rt);
			}
			
		} catch (Exception e) {
			log.error("exception in BeginObstacleRemovalController processEvent", e);
			//don't let the client hang
      try {
      	resBuilder.setStatus(BeginObstacleRemovalStatus.FAIL_OTHER);
      	BeginObstacleRemovalResponseEvent resEvent = new BeginObstacleRemovalResponseEvent(userId);
      	resEvent.setTag(event.getTag());
      	resEvent.setBeginObstacleRemovalResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in BeginObstacleRemovalController processEvent", e);
      }
		} finally {
			getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}

	private boolean checkLegit(Builder resBuilder, int userId, User user, int ofuId,
			ObstacleForUser ofu, int gemsSpent, int resourceChange, ResourceType rt) {
		
		if (null == user || null == ofu) {
			resBuilder.setStatus(BeginObstacleRemovalStatus.FAIL_OTHER);
			log.error("unexpected error: user or obstacle for user is null. user=" + user +
					"\t userId=" + userId + "\t obstacleForUser=" + ofu + "\t ofuId=" + ofuId);
			return false;
		}
		
		 
    if (!hasEnoughGems(resBuilder, user, gemsSpent)) {
    		return false;
    }
    
    if (ResourceType.CASH.equals(rt)) {
    	if (!hasEnoughCash(resBuilder, user, resourceChange)) {
    		return false;
      }
    }

    if (ResourceType.OIL.equals(rt)) {
      if (!hasEnoughOil(resBuilder, user, resourceChange)) {
      		return false;
      }
    }
		
		resBuilder.setStatus(BeginObstacleRemovalStatus.SUCCESS);
		return true;  
	}
	
  
  private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent) {
  	int userGems = u.getGems();
  	//if user's aggregate gems is < cost, don't allow transaction
  	if (userGems < gemsSpent) {
  		log.error("user error: user does not have enough gems. userGems=" + userGems +
  				"\t gemsSpent=" + gemsSpent);
  		resBuilder.setStatus(BeginObstacleRemovalStatus.FAIL_INSUFFICIENT_GEMS);
  		return false;
  	}
  	
  	return true;
  }
  
  private boolean hasEnoughCash(Builder resBuilder, User u, int cashSpent) {
  	int userCash = u.getCash();
  	//if user's aggregate cash is < cost, don't allow transaction
  	if (userCash < cashSpent) {
  		log.error("user error: user does not have enough cash. userCash=" + userCash +
  				"\t cashSpent=" + cashSpent);
  		resBuilder.setStatus(BeginObstacleRemovalStatus.FAIL_INSUFFICIENT_RESOURCE);
  		return false;
  	}
  	
  	return true;
  }

  private boolean hasEnoughOil(Builder resBuilder, User u, int oilSpent) {
  	int userOil = u.getOil();
  	//if user's aggregate oil is < cost, don't allow transaction
  	if (userOil < oilSpent) {
  		log.error("user error: user does not have enough oil. userOil=" + userOil +
  				"\t oilSpent=" + oilSpent);
  		resBuilder.setStatus(BeginObstacleRemovalStatus.FAIL_INSUFFICIENT_RESOURCE);
  		return false;
  	}

  	return true;
  }

  private boolean writeChangesToDB(User user, int ofuId, int gemsSpent, int resourceChange,
  		ResourceType rt, Timestamp clientTime, Map<String, Integer> currencyChange,
  		Map<String, Integer> previousCurrency) {
  	
  	//update user currency
  	int gemsChange = -1 * Math.abs(gemsSpent);
  	int cashChange = 0;
  	int oilChange = 0;
  	
  	if (0 != gemsChange) {
  		previousCurrency.put(MiscMethods.gems, user.getGems());
  	}
  	if (ResourceType.CASH.equals(rt)) {
  		log.info("user spent cash.");
  		cashChange = resourceChange;
  		previousCurrency.put(MiscMethods.cash, user.getCash());
  	}
  	if (ResourceType.OIL.equals(rt)) {
  		log.info("user spent cash.");
  		oilChange = resourceChange;
  		previousCurrency.put(MiscMethods.oil, user.getOil());
  	}
  	
  	if (!updateUser(user, gemsChange, cashChange, oilChange)) {
		  log.error("unexpected error: could not decrement user's gems by " +
				  gemsChange + ", cash by " + cashChange + " or oil by " + oilChange);
		  return false;
	  } else {
	  	if (0 != gemsChange) {
	  		currencyChange.put(MiscMethods.gems, gemsChange);
	  	}
	  	if (0 != cashChange) {
	  		currencyChange.put(MiscMethods.cash, cashChange);
	  	}
	  	if (0 != oilChange) {
	  		currencyChange.put(MiscMethods.oil, oilChange);
	  	}
	  }
	  
  	int numUpdated = UpdateUtils.get().updateObstacleForUserRemovalTime(ofuId, clientTime);
  	log.info("(obstacles, should be 1) numUpdated=" + numUpdated);
  	return true;
  }

  private boolean updateUser(User u, int gemsChange, int cashChange, int oilChange) {
	  int numChange = u.updateRelativeCashAndOilAndGems(cashChange, oilChange, gemsChange);

	  if (numChange <= 0) {
	  	log.error("unexpected error: problem with updating user gems, cash, and oil. gemChange=" +
	  			gemsChange + ", cash= " + cashChange + ", oil=" + oilChange + " user=" + u);
	  	return false;
	  }
	  return true;
  }

	private void writeToUserCurrencyHistory(int userId, User user, Timestamp curTime,
			Map<String, Integer> currencyChange, Map<String, Integer> previousCurrency,
			ObstacleForUser ofu, ResourceType rt) {
		String reason = ControllerConstants.UCHRFC__REMOVE_OBSTACLE;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("obstacleId=");
		detailsSb.append(ofu.getObstacleId());
		detailsSb.append(" x=");
		detailsSb.append(ofu.getXcoord());
		detailsSb.append(" y=");
		detailsSb.append(ofu.getYcoord());
		detailsSb.append(" resourceType=");
		detailsSb.append(rt.name());
		String details = detailsSb.toString();
		
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MiscMethods.gems;
		String cash = MiscMethods.cash;
		String oil = MiscMethods.oil;
		
		if (currencyChange.containsKey(gems)) {
			currentCurrency.put(gems, user.getGems());
			reasonsForChanges.put(gems, reason);
			detailsMap.put(gems, details);
		}
		if (currencyChange.containsKey(cash)) {
			currentCurrency.put(cash, user.getCash());
			reasonsForChanges.put(cash, reason);
			detailsMap.put(cash, details);
		}
		if (currencyChange.containsKey(oil)) {
			currentCurrency.put(oil, user.getOil());
			reasonsForChanges.put(oil, reason);
			detailsMap.put(oil, details);
		}

		MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
        previousCurrency, currentCurrency, reasonsForChanges, detailsMap);

	}
	
	public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}
	public ObstacleForUserRetrieveUtil getObstacleForUserRetrieveUtil() {
		return obstacleForUserRetrieveUtil;
	}
	public void setObstacleForUserRetrieveUtil(
			ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil) {
		this.obstacleForUserRetrieveUtil = obstacleForUserRetrieveUtil;
	}
	
}
