package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class NormStructWaitCompleteController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

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
    int userId = senderProto.getUserId();
    List<Integer> userStructIds = reqProto.getUserStructIdList();
    userStructIds = new ArrayList<Integer>(userStructIds);
    Timestamp clientTime = new Timestamp(reqProto.getCurTime());

    //stuff to send to client
    NormStructWaitCompleteResponseProto.Builder resBuilder = NormStructWaitCompleteResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      List<StructureForUser> userStructs = RetrieveUtils.userStructRetrieveUtils()
      		.getSpecificOrAllUserStructsForUser(userId, userStructIds);

      boolean legitWaitComplete = checkLegitWaitComplete(resBuilder, userStructs,
      		userStructIds, senderProto.getUserId(), clientTime);


      boolean success = false;
      if (legitWaitComplete) {
      	//upgrading and building a building is the same thing
        success = writeChangesToDB(userStructs);
      }

      if (success) {
      	resBuilder.setStatus(NormStructWaitCompleteStatus.SUCCESS);
      	List<StructureForUser> newUserStructs = RetrieveUtils
      			.userStructRetrieveUtils()
      			.getSpecificOrAllUserStructsForUser(userId, userStructIds);
      	for (StructureForUser userStruct : newUserStructs) {
      		resBuilder.addUserStruct(CreateInfoProtoUtils.createFullUserStructureProtoFromUserstruct(userStruct));
      	}
      }
      
      NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setNormStructWaitCompleteResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

    } catch (Exception e) {
      log.error("exception in NormStructWaitCompleteController processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  private boolean writeChangesToDB(List<StructureForUser> buildsDone) {
    if (!UpdateUtils.get().updateUserStructsLastretrievedpostbuildIscomplete(buildsDone)) {
      log.error("problem with marking norm struct builds as complete for one of these structs: " + buildsDone);
      return false;
    }
    return true;
  }

  private boolean checkLegitWaitComplete(Builder resBuilder,
      List<StructureForUser> userStructs, List<Integer> userStructIds,
      int userId, Timestamp clientTime) {
  	
    if (userStructs == null || userStructIds == null || clientTime == null || userStructIds.size() != userStructs.size()) {
      resBuilder.setStatus(NormStructWaitCompleteStatus.FAIL_OTHER);
      log.error("userStructs is null, userStructIds is null, clientTime is null, or array lengths different. userStructs="
          + userStructs + ", userStructIds=" + userStructIds + ", clientTime=" + clientTime);
      return false;
    }
//    if (!MiscMethods.checkClientTimeAroundApproximateNow(clientTime)) {
//      resBuilder.setStatus(NormStructWaitCompleteStatus.CLIENT_TOO_APART_FROM_SERVER_TIME);
//      log.error("client time too apart of server time. client time=" + clientTime + ", servertime~="
//          + new Date());
//      return false;
//    }

    //for each user structure complete the ones the client said are done.
    //replace what client sent with the ones that are actually done
    List<StructureForUser> validUserStructs = new ArrayList<StructureForUser>();
    List<Integer> validUserStructIds = new ArrayList<Integer>();
    
    calculateValidUserStructs(userId, clientTime, userStructs, validUserStructIds,
    		validUserStructs);
    
    if (userStructs.size() != validUserStructs.size()) {
    	log.warn("some of what the client sent is invalid. idsClientSent=" +
    			userStructIds + "\t validIds=" + validUserStructIds);
    	userStructs.clear();
    	userStructs.addAll(validUserStructs);
    	
    	userStructIds.clear();
    	userStructIds.addAll(validUserStructIds);
    }
    
    return true;  

  }

  //"validStructIds" and "validUserStructs" will be POPULATED
  private void calculateValidUserStructs(int userId, Timestamp clientTime, 
  		List<StructureForUser> userStructs, List<Integer> validUserStructIds,
  		List<StructureForUser> validUserStructs) {
    Map<Integer, Structure> structures = StructureRetrieveUtils.getStructIdsToStructs();
    
    for (StructureForUser us : userStructs) {
      if (us.getUserId() != userId) {
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
        if (purchaseDate.getTime() + buildTimeMillis > clientTime.getTime()) {
          log.warn("the building is not done yet. userstruct=" + ", client time is " +
          		clientTime + ", purchase time was " + purchaseDate);
          continue;
        }//else this building is done now     
        
        validUserStructIds.add(us.getId());
        validUserStructs.add(us);
        
      } else {
        log.warn("user struct has never been bought or purchased according to db. " + us);
      }
      
    }
    
  }
  
}
