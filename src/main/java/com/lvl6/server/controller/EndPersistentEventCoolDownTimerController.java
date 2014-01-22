package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndPersistentEventCoolDownTimerRequestEvent;
import com.lvl6.events.response.EndPersistentEventCoolDownTimerResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.EndPersistentEventCoolDownTimerRequestProto;
import com.lvl6.proto.EventDungeonProto.EndPersistentEventCoolDownTimerResponseProto;
import com.lvl6.proto.EventDungeonProto.EndPersistentEventCoolDownTimerResponseProto.Builder;
import com.lvl6.proto.EventDungeonProto.EndPersistentEventCoolDownTimerResponseProto.EndPersistentEventCoolDownTimerStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component @DependsOn("gameServer") public class EndPersistentEventCoolDownTimerController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public EndPersistentEventCoolDownTimerController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new EndPersistentEventCoolDownTimerRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_END_PERSISTENT_EVENT_COOL_DOWN_TIMER_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    EndPersistentEventCoolDownTimerRequestProto reqProto = ((EndPersistentEventCoolDownTimerRequestEvent)event).getEndPersistentEventCoolDownTimerRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int gemsSpent = reqProto.getGemsSpent();
    int eventPersistentId = reqProto.getEventPersistentId();

    //set some values to send to the client (the response proto)
    EndPersistentEventCoolDownTimerResponseProto.Builder resBuilder = EndPersistentEventCoolDownTimerResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(EndPersistentEventCoolDownTimerStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
//      int previousGems = 0;

      boolean legit = checkLegit(resBuilder, aUser, userId, gemsSpent);

      boolean successful = false;
      if(legit) {
//        previousGems = aUser.getGems();
    	  successful = writeChangesToDb(aUser, userId, gemsSpent, eventPersistentId);
//        writeToUserCurrencyHistory(aUser, money, curTime, previousSilver, previousGems);
      }
      if (successful) {
    	  resBuilder.setStatus(EndPersistentEventCoolDownTimerStatus.SUCCESS);
      }
      
      EndPersistentEventCoolDownTimerResponseEvent resEvent = new EndPersistentEventCoolDownTimerResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setEndPersistentEventCoolDownTimerResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
    } catch (Exception e) {
      log.error("exception in EndPersistentEventCoolDownTimerController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(EndPersistentEventCoolDownTimerStatus.FAIL_OTHER);
    	  EndPersistentEventCoolDownTimerResponseEvent resEvent = new EndPersistentEventCoolDownTimerResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setEndPersistentEventCoolDownTimerResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in EndPersistentEventCoolDownTimerController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, User u, int userId, int gemsSpent) {
    if (null == u) {
      log.error("unexpected error: user is null. user=" + u);
      return false;
    }
    
    //make sure user has enough diamonds/gold?
    int userGems = u.getGems();
    int cost = ControllerConstants.EVENT_PERSISTENT__END_COOL_DOWN_TIMER_GEM_COST;
    if (cost > userGems) {
    	log.error("user error: user does not have enough gems to end cool down timer. cost=" +
    			cost + "\t userGems=" + userGems);
    	resBuilder.setStatus(EndPersistentEventCoolDownTimerStatus.FAIL_INSUFFICIENT_FUNDS);
    	return false;
    }
    
    return true;
  }

  private boolean writeChangesToDb(User u, int uId, int gemsSpent, int eventId) {
	  
	  //update user gems
	  int gemChange = -1 * ControllerConstants.EVENT_PERSISTENT__END_COOL_DOWN_TIMER_GEM_COST;
	  
	  if (!updateUser(u, gemChange)) {
		  log.error("unexpected error: could not decrement user's gems by " + gemChange);
		  return false;
	  }
	  
	  int numUpdated = DeleteUtils.get().deleteEventPersistentForUser(uId, eventId); 
	  if (1 != numUpdated) {
		  log.error("unexpected error: event_persistent_for_user not updated correctly." +
		  		" Attempting to give back gems. eventId=" + eventId + "\t numUpdated=" +
				  numUpdated + "\t user=" + u);
		  //undo gem charge
		  gemChange = -1 * gemChange;
		  if (!updateUser(u, gemChange)) {
			  log.error("unexpected error: could not change user's gold by " +
					  gemChange);
		  }
		  return false;
	  }
	  return true;
  }
  
  private boolean updateUser(User u, int gemChange) {
	  if (!u.updateRelativeDiamondsNaive(gemChange)) {
		  log.error("unexpected error: problem with updating user gems for ending cool down" +
		  		" timer. gemChange=" + gemChange + "user=" + u);
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
