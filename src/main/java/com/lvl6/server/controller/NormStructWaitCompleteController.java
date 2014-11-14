package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.NormStructWaitCompleteRequestEvent;
import com.lvl6.events.response.NormStructWaitCompleteResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteRequestProto;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteResponseProto;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteResponseProto.NormStructWaitCompleteStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class NormStructWaitCompleteController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  @Autowired
  protected StructureForUserRetrieveUtils2 structureForUserRetrieveUtils;

  public NormStructWaitCompleteController() {
    numAllocatedThreads = 5;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new NormStructWaitCompleteRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_NORM_STRUCT_WAIT_COMPLETE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    NormStructWaitCompleteRequestProto reqProto = ((NormStructWaitCompleteRequestEvent)event).getNormStructWaitCompleteRequestProto();

    //stuff client sent
    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    List<String> userStructIds = reqProto.getUserStructUuidList();
    userStructIds = new ArrayList<String>(userStructIds);
    Timestamp clientTime = new Timestamp(reqProto.getCurTime());

    //stuff to send to client
    NormStructWaitCompleteResponseProto.Builder resBuilder = NormStructWaitCompleteResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);

    UUID userUuid = null;
    UUID userStructUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(userId);

      if (userStructIds != null) {
        for (String userStructId : userStructIds) {
          userStructUuid = UUID.fromString(userStructId);
        }
      }

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s, userStructIds=%s",
          userId, userStructIds), e);
      invalidUuids = true;
    }

    //UUID checks
    if (invalidUuids) {
      resBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);
      NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setNormStructWaitCompleteResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }

    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      List<StructureForUser> userStructs = getStructureForUserRetrieveUtils()
      		.getSpecificOrAllUserStructsForUser(userId, userStructIds);

      List<Timestamp> newRetrievedTimes = new ArrayList<Timestamp>();
      boolean legitWaitComplete = checkLegitWaitComplete(resBuilder, userStructs,
      		userStructIds, senderProto.getUserUuid(), clientTime, 
      		newRetrievedTimes);


      boolean success = false;
      if (legitWaitComplete) {
      	//upgrading and building a building is the same thing
        success = writeChangesToDB(userId, userStructs, newRetrievedTimes);
      }

      if (success) {
      	resBuilder.setStatus(NormStructWaitCompleteStatus.SUCCESS);
      	List<StructureForUser> newUserStructs = getStructureForUserRetrieveUtils()
      			.getSpecificOrAllUserStructsForUser(userId, userStructIds);
      	for (StructureForUser userStruct : newUserStructs) {
      		resBuilder.addUserStruct(CreateInfoProtoUtils.createFullUserStructureProtoFromUserstruct(userStruct));
      	}
      }
      
      NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setNormStructWaitCompleteResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

    } catch (Exception e) {
      log.error("exception in NormStructWaitCompleteController processEvent", e);
      //don't let the client hang
      try {
        resBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);
        NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(userId);
        resEvent.setTag(event.getTag());
        resEvent.setNormStructWaitCompleteResponseProto(resBuilder.build());
        server.writeEvent(resEvent);
      } catch (Exception e2) {
        log.error("exception2 in NormStructWaitCompleteController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());      
    }
  }

  private boolean checkLegitWaitComplete(Builder resBuilder,
      List<StructureForUser> userStructs, List<String> userStructIds,
      String userId, Timestamp clientTime, 
      List<Timestamp> newRetrievedTimes) {
  	
    if (userStructs == null || userStructIds == null || clientTime == null || userStructIds.size() != userStructs.size()) {
      resBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);
      log.error("userStructs is null, userStructIds is null, clientTime is null, or array lengths different. userStructs="
          + userStructs + ", userStructIds=" + userStructIds + ", clientTime=" + clientTime);
      return false;
    }

    //for each user structure complete the ones the client said are done.
    //replace what client sent with the ones that are actually done
    List<StructureForUser> validUserStructs = new ArrayList<StructureForUser>();
    List<String> validUserStructIds = new ArrayList<String>();
    
    List<Timestamp> timesBuildsFinished = calculateValidUserStructs(userId,
    	clientTime, userStructs, validUserStructIds, validUserStructs);
    
    if (userStructs.size() != validUserStructs.size()) {
    	log.warn("some of what the client sent is invalid. idsClientSent=" +
    			userStructIds + "\t validIds=" + validUserStructIds);
    	userStructs.clear();
    	userStructs.addAll(validUserStructs);
    	
    	userStructIds.clear();
    	userStructIds.addAll(validUserStructIds);
    }
    
    newRetrievedTimes.addAll(timesBuildsFinished);
    return true;  

  }

  //"validUserStructIds" and "validUserStructs" WILL BE POPULATED
  private List<Timestamp> calculateValidUserStructs(String userId, Timestamp clientTime, 
  		List<StructureForUser> userStructs, 
  		List<String> validUserStructIds, List<StructureForUser> validUserStructs) {
  	List<Timestamp> timesBuildsFinished = new ArrayList<Timestamp>();
    Map<Integer, Structure> structures = StructureRetrieveUtils.getStructIdsToStructs();
    
    for (StructureForUser us : userStructs) {
      if (!us.getUserId().equals(userId)) {
        log.warn("user struct's owner's id is " + us.getUserId() + ", and user id is " + userId);
        continue;
      }
      Structure struct = structures.get(us.getStructId());
      if (struct == null) {
        log.warn("no struct in db exists with id " + us.getStructId());
        continue;
      }
      
      Date purchaseDate = us.getPurchaseTime();
      long buildTimeMillis = 60000*struct.getMinutesToBuild();
      
      if (null != purchaseDate) {
      	long timeBuildFinishes = purchaseDate.getTime() + buildTimeMillis;
      	
      	if (timeBuildFinishes > clientTime.getTime()) {
      		log.warn(String.format(
      			"building not done yet. userstruct=%s, client_time=%s, purchase_time=%s, time_build_finishes=%s",
      			us, clientTime, purchaseDate, timeBuildFinishes));
      		continue;
        }//else this building is done now     
        
        validUserStructIds.add(us.getId());
        validUserStructs.add(us);
        timesBuildsFinished.add(new Timestamp(timeBuildFinishes));
        
      } else {
        log.warn("user struct has never been bought or purchased according to db. " + us);
      }
    }
    return timesBuildsFinished;
  }
  

  private boolean writeChangesToDB(String userId, List<StructureForUser> buildsDone,
  		List<Timestamp> newRetrievedTimes) {
    if (!UpdateUtils.get().updateUserStructsBuildingIsComplete(userId, buildsDone,
    		newRetrievedTimes)) {
      log.error("problem with marking norm struct builds as complete for one of these structs: " + buildsDone);
      return false;
    }
    return true;
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public StructureForUserRetrieveUtils2 getStructureForUserRetrieveUtils() {
    return structureForUserRetrieveUtils;
  }

  public void setStructureForUserRetrieveUtils(
      StructureForUserRetrieveUtils2 structureForUserRetrieveUtils) {
    this.structureForUserRetrieveUtils = structureForUserRetrieveUtils;
  }

}
