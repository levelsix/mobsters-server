package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CombineUserMonsterPiecesRequestEvent;
import com.lvl6.events.response.CombineUserMonsterPiecesResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesRequestProto;
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesResponseProto;
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesResponseProto.CombineUserMonsterPiecesStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class CombineUserMonsterPiecesController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public CombineUserMonsterPiecesController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new CombineUserMonsterPiecesRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_COMBINE_USER_MONSTER_PIECES_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    CombineUserMonsterPiecesRequestProto reqProto = ((CombineUserMonsterPiecesRequestEvent)event).getCombineUserMonsterPiecesRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    List<Long> userMonsterIds = reqProto.getUserMonsterIdsList();
    userMonsterIds = new ArrayList<Long>(userMonsterIds);
    int gemCost = reqProto.getGemCost();
    Date curDate = new Date();
    Timestamp curTime = new Timestamp(curDate.getTime());
//    log.info("reqProto=" + reqProto);

    //set some values to send to the client (the response proto)
    CombineUserMonsterPiecesResponseProto.Builder resBuilder = CombineUserMonsterPiecesResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
    	int previousGems = 0;
    	
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      Map<Long, MonsterForUser> idsToUserMonsters = RetrieveUtils
      		.monsterForUserRetrieveUtils().getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
      
      boolean legit = checkLegit(resBuilder, userId, aUser, userMonsterIds,
      		idsToUserMonsters, gemCost);

      boolean successful = false;
      Map<String, Integer> money = new HashMap<String, Integer>();
      if(legit) {
      	previousGems = aUser.getGems();
    	  successful = writeChangesToDb(aUser, userMonsterIds, gemCost, money);
      }
      
      if (successful) {
    	  resBuilder.setStatus(CombineUserMonsterPiecesStatus.SUCCESS);
      }
      
      CombineUserMonsterPiecesResponseEvent resEvent = new CombineUserMonsterPiecesResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setCombineUserMonsterPiecesResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      
      if (successful && gemCost > 0) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	
      	writeToUserCurrencyHistory(aUser, money, curTime, previousGems, userMonsterIds);
      }
    } catch (Exception e) {
      log.error("exception in CombineUserMonsterPiecesController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_OTHER);
    	  CombineUserMonsterPiecesResponseEvent resEvent = new CombineUserMonsterPiecesResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setCombineUserMonsterPiecesResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in CombineUserMonsterPiecesController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   * userMonsterIds might be modified to contain only those user monsters that
   * can be combined
   * 
   * Example. client gives ids (a, b, c, d). Let's say 'a' is already
   *  completed/combined, 'b' is missing a piece, 'c' doesn't exist and 'd'
   *  can be completed  so "userMonsterIds" will be modified to only contain
	 * 'd'
   */
  private boolean checkLegit(Builder resBuilder, int userId, User u,
  		List<Long> userMonsterIds, Map<Long, MonsterForUser> idsToUserMonsters,
  		int gemCost) {
  	
  	if (null == u) {
  		log.error("user is null. no user exists with id=" + userId + "");
  		return false;
  	}
  	if (null == userMonsterIds || userMonsterIds.isEmpty() ||
  			idsToUserMonsters.isEmpty()) {
  		log.error("no user monsters exist. userMonsterIds=" + userMonsterIds +
  				"\t idsToUserMonsters=" + idsToUserMonsters);
  		return false;
  	}
  	
  	//only complete the user monsters that exist
  	if (userMonsterIds.size() != idsToUserMonsters.size()) {
  		log.warn("not all monster_for_user_ids exist. userMonsterIds=" + userMonsterIds +
  				"\t idsToUserMonsters=" + idsToUserMonsters + "\t. Will continue processing");
  		
  		//retaining only the user monster ids that exist
  		userMonsterIds.clear();
  		userMonsterIds.addAll(idsToUserMonsters.keySet());
  	}
  	
  	List<Long> wholeUserMonsterIds = MonsterStuffUtils
  			.getWholeButNotCombinedUserMonsters(idsToUserMonsters);
  	if (wholeUserMonsterIds.size() != userMonsterIds.size()) {
  		log.warn("client trying to combine already combined or incomplete monsters." +
  				" clientSentIds=" + userMonsterIds + "\t wholeButIncompleteMonsterIds=" +
  				wholeUserMonsterIds + "\t idsToUserMonsters=" + idsToUserMonsters +
  				"\t Will continue processing");
  		
  		//retaining only user monsters that have all pieces but are incomplete
  		userMonsterIds.clear();
  		userMonsterIds.addAll(wholeUserMonsterIds);
  	}
  	
  	if (userMonsterIds.isEmpty()) {
  		resBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_OTHER);
  		log.error("the user didn't send any userMonsters to complete!.");
  		return false;
  	}
  	
  	
  	if (gemCost > 0 && userMonsterIds.size() > 1) {
  		//user speeding up combining multiple monsters, can only speed up one
  		log.error("user speeding up combining pieces for multiple monsters can only " +
  				"speed up one monster. gemCost=" + gemCost + "\t userMonsterIds=" + userMonsterIds);
  		resBuilder.setStatus(CombineUserMonsterPiecesStatus
  				.FAIL_MORE_THAN_ONE_MONSTER_FOR_SPEEDUP);
  		return false;
  	}
  	
  	//check user gems
  	int userGems = u.getGems();
  	if (userGems < gemCost) {
  		log.error("user doesn't have enough gems to speed up combining. userGems=" +
  				userGems + "\t gemCost=" + gemCost + "\t userMonsterIds=" + userMonsterIds);
  		resBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_INSUFFUCIENT_GEMS);
  		return false;
  	}
  	
  	return true;
  }
  
  private boolean writeChangesToDb(User aUser, List<Long> userMonsterIds,
  		int gemCost, Map<String, Integer> money) { 
  	
  	//if user sped up stuff then charge him
  	if (gemCost > 0) {
  		int gemChange = -1 * gemCost;
  		if (!aUser.updateRelativeGemsNaive(gemChange)) {
  			log.error("problem with updating user gems for speedup. gemChange=" + gemChange + 
  					"\t userMonsterIds=" + userMonsterIds);
  			return false;
  		} else {
  			money.put(MiscMethods.gems, gemChange);
  		}
  	}
  	
  	int num = UpdateUtils.get().updateCompleteUserMonster(userMonsterIds);
  	
  	if (num != userMonsterIds.size()) {
  		log.error("problem with updating user monster is_complete. numUpdated=" +
  				num + "\t userMonsterIds=" + userMonsterIds);
  	}
  	return true;
  }
  
  private void writeToUserCurrencyHistory(User aUser, Map<String, Integer> money,
  		Timestamp curTime, int previousGems, List<Long> userMonsterIds) {
  	if (null == money || money.isEmpty()) {
  		return;
  	}
  	int userId = aUser.getId();
  	String gems = MiscMethods.gems;
  	String reasonForChange = ControllerConstants.UCHRFC__SPED_UP_COMBINING_MONSTER;
  	
    Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> detailsList = new HashMap<String, String>();

    previousCurrencies.put(gems, previousGems);
    currentCurrencies.put(gems, aUser.getGems());
    reasonsForChanges.put(gems, reasonForChange);
    detailsList.put(gems, "userMonsterIds=" + userMonsterIds);
    MiscMethods.writeToUserCurrencyOneUser(userId, curTime, money, 
        previousCurrencies, currentCurrencies, reasonsForChanges, detailsList);

  }
}
