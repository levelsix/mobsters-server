package com.lvl6.server.controller;

import java.util.ArrayList;
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

    //set some values to send to the client (the response proto)
    CombineUserMonsterPiecesResponseProto.Builder resBuilder = CombineUserMonsterPiecesResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(CombineUserMonsterPiecesStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      Map<Long, MonsterForUser> idsToUserMonsters = RetrieveUtils
      		.monsterForUserRetrieveUtils().getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
      
      boolean legit = checkLegit(resBuilder, userId, aUser, userMonsterIds,
      		idsToUserMonsters);

      boolean successful = false;
      if(legit) {
    	  successful = writeChangesToDb(aUser, userMonsterIds);
      }
      
      if (successful) {
    	  resBuilder.setStatus(CombineUserMonsterPiecesStatus.SUCCESS);
      }
      
      CombineUserMonsterPiecesResponseEvent resEvent = new CombineUserMonsterPiecesResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setCombineUserMonsterPiecesResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      
      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      resEventUpdate.setTag(event.getTag());
      server.writeEvent(resEventUpdate);
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
   *  completed/combined, 'b' is missing a piece, 'c' doesn't exist
   */
  private boolean checkLegit(Builder resBuilder, int userId, User u,
  		List<Long> userMonsterIds, Map<Long, MonsterForUser> idsToUserMonsters) {
  	
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
  	
  	
  	resBuilder.setStatus(CombineUserMonsterPiecesStatus.SUCCESS);
  	return true;
  }
  
  private boolean writeChangesToDb(User aUser, List<Long> userMonsterIds) { 
  	boolean success = true;
  	
  	int num = UpdateUtils.get().updateCompleteUserMonster(userMonsterIds);
  	
  	if (num != userMonsterIds.size()) {
  		log.error("problem with updating user monster is_complete. numUpdated=" +
  				num + "\t userMonsterIds=" + userMonsterIds);
  	}
  	return success;
  }
  
}
