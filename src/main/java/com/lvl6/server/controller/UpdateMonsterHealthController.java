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
import com.lvl6.events.request.UpdateMonsterHealthRequestEvent;
import com.lvl6.events.response.UpdateMonsterHealthResponseEvent;
import com.lvl6.info.UserEquip;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthRequestProto;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto.UpdateMonsterHealthStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class UpdateMonsterHealthController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public UpdateMonsterHealthController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new UpdateMonsterHealthRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_UPDATE_MONSTER_HEALTH_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    UpdateMonsterHealthRequestProto reqProto = ((UpdateMonsterHealthRequestEvent)event).getUpdateMonsterHealthRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    Timestamp curTime = new Timestamp(reqProto.getClientTime());
    List<FullUserMonsterProto> fumpList = reqProto.getFumpsList();

    //set some values to send to the client (the response proto)
    UpdateMonsterHealthResponseProto.Builder resBuilder = UpdateMonsterHealthResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(UpdateMonsterHealthStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      //User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	
    	Map<Long, Integer> userEquipIdToExpectedDurability = new HashMap<Long, Integer>();
    	
      boolean legit = checkLegit(resBuilder, userId, fumpList, 
      		userEquipIdToExpectedDurability);

      boolean successful = false;
      if(legit) {
    	  successful = writeChangesToDb(userId, curTime, userEquipIdToExpectedDurability);
      }
      
      if (successful) {
      	//send back the equip protos that updated
      	//no reason to believe some equip will not be updated so
      	//send back what client gave
      	resBuilder.addAllFumps(fumpList);
    	  resBuilder.setStatus(UpdateMonsterHealthStatus.SUCCESS);
      }
      
      UpdateMonsterHealthResponseEvent resEvent = new UpdateMonsterHealthResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setUpdateMonsterHealthResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
//
//      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
//          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
//      resEventUpdate.setTag(event.getTag());
//      server.writeEvent(resEventUpdate);
    } catch (Exception e) {
      log.error("exception in UpdateMonsterHealthController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(UpdateMonsterHealthStatus.FAIL_OTHER);
    	  UpdateMonsterHealthResponseEvent resEvent = new UpdateMonsterHealthResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setUpdateMonsterHealthResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in UpdateMonsterHealthController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   * Also, returns the expected durabilities for the new equips
   */
  private boolean checkLegit(Builder resBuilder, int userId,
		  List<FullUserMonsterProto> fuepList, 
		  Map<Long, Integer> userEquipIdToExpectedDurability) {
  	
  	if (null == fuepList || fuepList.isEmpty()) {
  		log.error("client error: no user equips sent.");
  		return false;
  	}
  	
  	//extract the ids so it's easier to get userEquips from db
  	List<Long> userEquipIds = getUserMonsterIds(fuepList, userEquipIdToExpectedDurability);
  	List<UserMonster> userEquips = RetrieveUtils.userEquipRetrieveUtils()
  			.getSpecificUserMonsters(userEquipIds);
  	
  	if (null == userEquips || userEquips.isEmpty()) {
  		log.error("unexpected error: userEquipIds don't exist. ids=" + userEquipIds);
  		return false;
  	}

  	//see if the user has the equips
  	if (userEquips.size() != fuepList.size()) {
  		log.error("unexpected error: mismatch between user equips client sent and " +
  				"what is in the db. clientUserMonsterIds=" + userEquipIds + "\t inDb=" +
  				userEquips + "\t continuing the processing");
  	}
  	
  	return true;
  	//resBuilder.setStatus(UpdateMonsterHealthStatus.SUCCESS);
  }
  
  //extract the ids from the protos
  private List<Long> getUserMonsterIds(List<FullUserMonsterProto> fuepList,
  		Map<Long, Integer> userEquipIdToExpectedDurability) {
  	List<Long> idList = new ArrayList<Long>();
  	
  	for(FullUserMonsterProto fuep : fuepList) {
  		long id = fuep.getUserMonsterId();
  		idList.add(id);
  		int durability = fuep.getCurrentDurability();
  		userEquipIdToExpectedDurability.put(id, durability);
  	}
  	return idList;
  }
  
  private boolean writeChangesToDb(int uId, Timestamp clientTime, 
  		Map<Long, Integer> userEquipIdToExpectedDurability) {
  	//replace existing durability for these user equips w/ new values 
  	List<Long> userEquipIds = null;
  	List<Integer> currentDurability = null;
  	int numUpdated = UpdateUtils.get().updateUserMonstersDurability(
  			userEquipIds, currentDurability, userEquipIdToExpectedDurability);
  	
  	if (numUpdated >= userEquipIdToExpectedDurability.size()) {
  		return true;
  	}
  	log.warn("unexpected error: not all user equips were updated. " +
  			"actual numUpdated=" + numUpdated + "expected: " +
  			"userEquipIdToExpectedDurability=" + userEquipIdToExpectedDurability);
	  return true;
  }
  
}
