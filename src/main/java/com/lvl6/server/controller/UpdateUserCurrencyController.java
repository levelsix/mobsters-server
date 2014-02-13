package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateUserCurrencyRequestEvent;
import com.lvl6.events.response.UpdateUserCurrencyResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyRequestProto;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto.Builder;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto.UpdateUserCurrencyStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class UpdateUserCurrencyController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


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
    int userId = senderProto.getUserId();
    
    //both positive numbers, server will change to negative
    int cashSpent = reqProto.getCashSpent();
    int oilSpent = reqProto.getOilSpent();
    int gemsSpent = reqProto.getGemsSpent();
    
    Timestamp clientTime = new Timestamp(reqProto.getClientTime());

    //set some values to send to the client (the response proto)
    UpdateUserCurrencyResponseProto.Builder resBuilder = UpdateUserCurrencyResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(UpdateUserCurrencyStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);

      boolean legit = checkLegit(resBuilder, aUser, userId, cashSpent, oilSpent, gemsSpent);


      boolean successful = false;
      if(legit) {
    	  successful = writeChangesToDb(aUser, userId, cashSpent, oilSpent, gemsSpent,
    	  		clientTime);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGold);
      }
      if (successful) {
    	  resBuilder.setStatus(UpdateUserCurrencyStatus.SUCCESS);
      }
      
      UpdateUserCurrencyResponseEvent resEvent = new UpdateUserCurrencyResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setUpdateUserCurrencyResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      if (successful) {
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      	resEventUpdate.setTag(event.getTag());
      	//TODO: KEEP TRACK OF CURRENCY HISTORY
      	server.writeEvent(resEventUpdate);
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
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId, int cashSpent,
  		int oilSpent, int gemsSpent) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
    
    if (cashSpent != Math.abs(cashSpent) || oilSpent != Math.abs(oilSpent) ||
    		gemsSpent != Math.abs(gemsSpent)) {
    	log.error("client sent a negative value! all should be positive :(  cashSpent=" +
    			cashSpent + "\t oilSpent=" + oilSpent + "\t gemsSpent=" + gemsSpent);
    	return false;
    }
    
    //CHECK MONEY
    if (!hasEnoughCash(resBuilder, u, cashSpent)) {
    	return false;
    }
    
    if (!hasEnoughOil(resBuilder, u, oilSpent)) {
    	return false;
    }
    
    if (!hasEnoughGems(resBuilder, u, gemsSpent)) {
    	return false;
    }
    
    resBuilder.setStatus(UpdateUserCurrencyStatus.SUCCESS);
    return true;
  }
  
  private boolean hasEnoughCash(Builder resBuilder, User u, int cashSpent) {
  	int userCash = u.getCash();
  	//if user's aggregate cash is < cost, don't allow transaction
  	if (userCash < cashSpent) {
  		log.error("user error: user does not have enough oil. userCash=" + userCash +
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
  

  private boolean writeChangesToDb(User u, int uId, int cashSpent, int oilSpent, 
  		int gemsSpent, Timestamp clientTime) {
	  
	  //update user diamonds
	  int gemsChange = -1 * gemsSpent;
	  int cashChange = -1 * cashSpent;
	  int oilChange = -1 * oilSpent;
	  if (!updateUser(u, gemsChange, cashChange, oilSpent)) {
		  log.error("unexpected error: could not decrement user's gems by " +
				  gemsChange + ", cash by " + cashChange + ", and oil by " + oilChange);
		  //update num revives for user task
		  return false;
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
  
  //TODO: FIX THIS
  private void writeToUserCurrencyHistory(User aUser, Map<String, Integer> money, Timestamp curTime,
      int previousSilver, int previousGold) {
//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String reasonForChange = ControllerConstants.UCHRFC__BOSS_ACTION;
//    String gems = MiscMethods.gems;
//    String cash = MiscMethods.cash;
//
//    previousGoldSilver.put(gems, previousGold);
//    previousGoldSilver.put(cash, previousSilver);
//    reasonsForChanges.put(gems, reasonForChange);
//    reasonsForChanges.put(cash, reasonForChange);
//
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, curTime, money, 
//        previousGoldSilver, reasonsForChanges);

  }
}
