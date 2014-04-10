package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateMonsterHealthRequestEvent;
import com.lvl6.events.response.UpdateMonsterHealthResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthRequestProto;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto.UpdateMonsterHealthStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class UpdateMonsterHealthController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

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
    log.info("reqProto=" + reqProto);

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    Timestamp curTime = new Timestamp(reqProto.getClientTime());
    List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();

    long userTaskId = reqProto.getUserTaskId();
    boolean isUpdateTaskStageForUser = reqProto.getIsUpdateTaskStageForUser();
    int nuTaskStageId = reqProto.getNuTaskStageId();

    //set some values to send to the client (the response proto)
    UpdateMonsterHealthResponseProto.Builder resBuilder = UpdateMonsterHealthResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(UpdateMonsterHealthStatus.FAIL_OTHER); //default

    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
    	Map<Long, Integer> userMonsterIdToExpectedHealth = new HashMap<Long, Integer>();
    	
      boolean legit = checkLegit(resBuilder, userId, umchpList, 
    		  isUpdateTaskStageForUser, userMonsterIdToExpectedHealth);

      boolean successful = false;
      if(legit) {
    	  successful = writeChangesToDb(userId, curTime, userMonsterIdToExpectedHealth,
    			  userTaskId, isUpdateTaskStageForUser, nuTaskStageId);
      }
      
      if (successful) {
    	  resBuilder.setStatus(UpdateMonsterHealthStatus.SUCCESS);
      }
      
      UpdateMonsterHealthResponseEvent resEvent = new UpdateMonsterHealthResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setUpdateMonsterHealthResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

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
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   * Also, returns the expected health for the user monsters
   */
  private boolean checkLegit(Builder resBuilder, int userId,
		  List<UserMonsterCurrentHealthProto> umchpList,
		  boolean isUpdateTaskStageForUser,
		  Map<Long, Integer> userMonsterIdToExpectedHealth) {
  	
	  boolean isUmchpListEmpty = (null == umchpList || umchpList.isEmpty());  
  	if (isUmchpListEmpty && !isUpdateTaskStageForUser) {
  		log.error("client error: no user monsters sent. and is not updating" +
  				" user's current task stage id");
  		return false;
  	} else if (isUmchpListEmpty && isUpdateTaskStageForUser) {
  		log.info("just updating user's current task stage id");
  	}
  	
  	if (!isUmchpListEmpty) {
  		//extract the ids so it's easier to get userMonsters from db
  		List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(umchpList, userMonsterIdToExpectedHealth);
  		Map<Long, MonsterForUser> userMonsters = RetrieveUtils.monsterForUserRetrieveUtils()
  				.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

  		if (null == userMonsters || userMonsters.isEmpty()) {
  			log.error("unexpected error: userMonsterIds don't exist. ids=" + userMonsterIds);
  			return false;
  		}

  		//see if the user has the equips
  		if (userMonsters.size() != umchpList.size()) {
  			log.error("unexpected error: mismatch between user equips client sent and " +
  					"what is in the db. clientUserMonsterIds=" + userMonsterIds + "\t inDb=" +
  					userMonsters + "\t continuing the processing");
  		}
  	}
  	
  	return true;
  	//resBuilder.setStatus(UpdateMonsterHealthStatus.SUCCESS);
  }
  
  private boolean writeChangesToDb(int uId, Timestamp clientTime, 
  		Map<Long, Integer> userMonsterIdToExpectedHealth, long userTaskId,
  		boolean isUpdateTaskStageForUser, int nuTaskStageId) {
	  //replace existing health for these user monsters with new values 
	  if (!userMonsterIdToExpectedHealth.isEmpty()) {
		  log.info("updating user's monsters' healths");
		  int numUpdated = UpdateUtils.get()
				  .updateUserMonstersHealth(userMonsterIdToExpectedHealth);
		  log.info("numUpdated=" + numUpdated);

		  if (numUpdated <= userMonsterIdToExpectedHealth.size()) {
			  log.warn("unexpected error: not all user monsters were updated. " +
					  "actual numUpdated=" + numUpdated + "expected: " +
					  "userMonsterIdToExpectedHealth=" + userMonsterIdToExpectedHealth);
		  }
	  }

	  if (isUpdateTaskStageForUser) {
		  int numUpdated = UpdateUtils.get().updateUserTaskTsId(userTaskId, nuTaskStageId);
		  log.info("task stage for user numUpdated=" + numUpdated);
	  }
	  
	  return true;
  }
  

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }
  
}
