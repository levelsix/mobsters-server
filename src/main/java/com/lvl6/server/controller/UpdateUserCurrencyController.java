package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateUserCurrencyRequestEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.events.response.UpdateUserCurrencyResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyRequestProto;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto.Builder;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto.UpdateUserCurrencyStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;

@Component @DependsOn("gameServer") public class UpdateUserCurrencyController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;

  public UpdateUserCurrencyController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new UpdateUserCurrencyRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_UPDATE_USER_CURRENCY_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    UpdateUserCurrencyRequestProto reqProto = ((UpdateUserCurrencyRequestEvent)event).getUpdateUserCurrencyRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    
    //all positive numbers, server will change to negative
    int cashSpent = reqProto.getCashSpent();
    int oilSpent = reqProto.getOilSpent();
    int gemsSpent = reqProto.getGemsSpent();
    
    String reason = reqProto.getReason();
    String details = reqProto.getDetails();
    Timestamp clientTime = new Timestamp(reqProto.getClientTime());

    //set some values to send to the client (the response proto)
    UpdateUserCurrencyResponseProto.Builder resBuilder = UpdateUserCurrencyResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_OTHER); //default

    UUID userUuid = null;
    boolean invalidUuids = true;
    try {
        userUuid = UUID.fromString(userId);
        invalidUuids = false;
    } catch (Exception e) {
        log.error(String.format(
            "UUID error. incorrect userId=%s",
            userId), e);
        invalidUuids = true;
    }
	
	//UUID checks
    if (invalidUuids) {
  	  resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_OTHER);
  	  UpdateUserCurrencyResponseEvent resEvent = new UpdateUserCurrencyResponseEvent(userId);
  	  resEvent.setTag(event.getTag());
  	  resEvent.setUpdateUserCurrencyResponseProto(resBuilder.build());
  	  server.writeEvent(resEvent);
    	return;
    }
    
    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      User aUser = getUserRetrieveUtils().getUserById(userId);
      int previousGems = 0;
      int previousCash = 0;
      int previousOil = 0;

      boolean legit = checkLegit(resBuilder, aUser, userId, cashSpent, oilSpent, gemsSpent);


      boolean successful = false;
      Map<String, Integer> currencyChange = new HashMap<String, Integer>();
      if(legit) {
      	previousGems = aUser.getGems();
      	previousCash = aUser.getCash();
      	previousOil = aUser.getOil();
      	
    	  successful = writeChangesToDb(aUser, userId, cashSpent, oilSpent, gemsSpent,
    	  		clientTime, currencyChange);
      }
      if (successful) {
    	  resBuilder.setStatus(UpdateUserCurrencyStatus.SUCCESS);
      }
      
      UpdateUserCurrencyResponseEvent resEvent = new UpdateUserCurrencyResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setUpdateUserCurrencyResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      if (successful) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	
      	writeToUserCurrencyHistory(aUser, currencyChange, clientTime, previousGems,
      			previousCash, previousOil, reason, details);
      	
      }
      
      //cheat code, reset user account
      if (1234 == cashSpent && 1234 == oilSpent && 1234 == gemsSpent) {
    	  log.info("resetting user " + aUser);
    	  aUser.updateResetAccount();
      }
      
    } catch (Exception e) {
      log.error("exception in UpdateUserCurrencyController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_OTHER);
    	  UpdateUserCurrencyResponseEvent resEvent = new UpdateUserCurrencyResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setUpdateUserCurrencyResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in UpdateUserCurrencyController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, String userId, int cashSpent,
  		int oilSpent, int gemsSpent) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
    
    if (cashSpent != Math.abs(cashSpent) || oilSpent != Math.abs(oilSpent) ||
    		gemsSpent != Math.abs(gemsSpent)) {
    	log.error("client sent a negative value! all should be positive :(  cashSpent=" +
    			cashSpent + "\t oilSpent=" + oilSpent + "\t gemsSpent=" + gemsSpent);
    	if (u.isAdmin()) {
    		log.info("it's alright. User is admin.");
    	} else {
    		return false;
    	}
    }
    
    //CHECK MONEY
    if (!hasEnoughCash(resBuilder, u, cashSpent)) {
    	if (u.isAdmin()) {
    		log.info("it's alright. User is admin.");
    	} else {
    		return false;
    	}
    }
    
    if (!hasEnoughOil(resBuilder, u, oilSpent)) {
    	if (u.isAdmin()) {
    		log.info("it's alright. User is admin.");
    	} else {
    		return false;
    	}
    }
    
    if (!hasEnoughGems(resBuilder, u, gemsSpent)) {
    	if (u.isAdmin()) {
    		log.info("it's alright. User is admin.");
    	} else {
    		return false;
    	}
    }
    
    return true;
  }
  
  private boolean hasEnoughCash(Builder resBuilder, User u, int cashSpent) {
  	int userCash = u.getCash();
  	//if user's aggregate cash is < cost, don't allow transaction
  	if (userCash < cashSpent) {
  		log.error("user error: user does not have enough cash. userCash=" + userCash +
  				"\t cashSpent=" + cashSpent);
  		resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_INSUFFICIENT_CASH);
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
  		resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_INSUFFICIENT_OIL);
  		return false;
  	}
  	
  	return true;
  }
  
  private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent) {
  	int userGems = u.getGems();
  	//if user's aggregate gems is < cost, don't allow transaction
  	if (userGems < gemsSpent) {
  		log.error("user error: user does not have enough gems. userGems=" + userGems +
  				"\t gemsSpent=" + gemsSpent);
  		resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_INSUFFICIENT_GEMS);
  		return false;
  	}
  	
  	return true;
  }
  

  private boolean writeChangesToDb(User u, String uId, int cashSpent, int oilSpent, 
  		int gemsSpent, Timestamp clientTime, Map<String, Integer> currencyChange) {
	  
  	//update user currency
  	int gemsChange = -1 * Math.abs(gemsSpent);
  	int cashChange = -1 * Math.abs(cashSpent);
  	int oilChange = -1 * Math.abs(oilSpent);
  	
  	//if user is admin then allow any change
	  if (u.isAdmin()) {
	  	gemsChange = gemsSpent;
	  	cashChange = cashSpent;
	  	oilChange = oilSpent;
	  }
	  
	  if (!updateUser(u, gemsChange, cashChange, oilChange)) {
		  log.error("unexpected error: could not decrement user's gems by " +
				  gemsChange + ", cash by " + cashChange + ", and oil by " + oilChange);
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
  
  private void writeToUserCurrencyHistory(User aUser, Map<String, Integer> currencyChange,
  		Timestamp curTime, int previousGems, int previousCash, int previousOil,
  		String reason, String details) {
	  String userId = aUser.getId();
  	
    Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> detailsMap = new HashMap<String, String>();
    String gems = MiscMethods.gems;
    String cash = MiscMethods.cash;
    String oil = MiscMethods.oil;

    previousCurrency.put(gems, previousGems);
    previousCurrency.put(cash, previousCash);
    previousCurrency.put(oil, previousOil);
    currentCurrency.put(gems, aUser.getGems());
    currentCurrency.put(cash, aUser.getCash());
    currentCurrency.put(oil, aUser.getOil());
    reasonsForChanges.put(gems, reason);
    reasonsForChanges.put(cash, reason);
    reasonsForChanges.put(oil, reason);
    detailsMap.put(gems, details);
    detailsMap.put(cash, details);
    detailsMap.put(oil, details);

    MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
        previousCurrency, currentCurrency, reasonsForChanges, detailsMap);

  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }

}
