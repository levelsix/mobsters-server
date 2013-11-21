package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveCurrencyFromNormStructureRequestEvent;
import com.lvl6.events.response.RetrieveCurrencyFromNormStructureResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto.StructRetrieval;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.RetrieveCurrencyFromNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class RetrieveCurrencyFromNormStructureController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public RetrieveCurrencyFromNormStructureController() {
    numAllocatedThreads = 14;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new RetrieveCurrencyFromNormStructureRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RetrieveCurrencyFromNormStructureRequestProto reqProto = ((RetrieveCurrencyFromNormStructureRequestEvent)event).getRetrieveCurrencyFromNormStructureRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    List<StructRetrieval> structRetrievals = reqProto.getStructRetrievalsList();
    Timestamp curTime = new Timestamp((new Date()).getTime());
    
    Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval =  new HashMap<Integer, Timestamp>();
    List<Integer> duplicates = new ArrayList<Integer>();
    //create map from ids to times and check for duplicates
    getIdsAndTimes(structRetrievals, userStructIdsToTimesOfRetrieval, duplicates); 
    
    RetrieveCurrencyFromNormStructureResponseProto.Builder resBuilder =
    		RetrieveCurrencyFromNormStructureResponseProto.newBuilder();
    resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.OTHER_FAIL);
    resBuilder.setSender(senderProto);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      int previousCash = 0;
      List<Integer> userStructIds = new ArrayList<Integer>(userStructIdsToTimesOfRetrieval.keySet());
      
      Map<Integer, StructureForUser> userStructIdsToUserStructs = getUserStructIdsToUserStructs(userId, userStructIds);
      Map<Integer, Structure> userStructIdsToStructures = getUserStructIdsToStructs(userStructIdsToUserStructs.values());
      
      //this will contain the amount user collects
      List<Integer> cashGained = new ArrayList<Integer>();
      //userStructIdsToTimesOfRetrieval and userStructIdsToUserStructs will be
      //modified to contain only the valid user structs user can retrieve currency from
      boolean legitRetrieval = checkLegitRetrieval(resBuilder, user, userStructIds,
      		userStructIdsToUserStructs, userStructIdsToStructures,
      		userStructIdsToTimesOfRetrieval, duplicates, cashGained);
      
      int cashGain = 0;
      boolean successful = false;
      if (legitRetrieval) {
      	cashGain = cashGained.get(0);
        previousCash = user.getCash();
        
        successful = writeChangesToDb(user, cashGain, userStructIdsToUserStructs,
        		userStructIdsToTimesOfRetrieval);
      }
      if (successful) {
      	resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.SUCCESS);
      }

      RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setRetrieveCurrencyFromNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
      
      if (legitRetrieval) {
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, cashGain, previousCash, curTime,
        		userStructIdsToTimesOfRetrieval);
      }
    } catch (Exception e) {
      log.error("exception in RetrieveCurrencyFromNormStructureController processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  //separate the duplicate ids from the unique ones
  private void getIdsAndTimes(List<StructRetrieval> srList, Map<Integer, Timestamp> structIdsToTimesOfRetrieval,
      List<Integer> duplicates) {
    if (srList.isEmpty()) {
      log.error("RetrieveCurrencyFromNormStruct request did not send any user struct ids.");
      return;
    }
    for(StructRetrieval sr : srList) {
      int key = sr.getUserStructId();
      Timestamp value = new Timestamp(sr.getTimeOfRetrieval());
      
      if(structIdsToTimesOfRetrieval.containsKey(key)) {
        duplicates.add(key);
      } else {
        structIdsToTimesOfRetrieval.put(key, value);
      }
    }
  }
  
  //retrieve these user structs from the db and put them in a map
  private Map<Integer, StructureForUser> getUserStructIdsToUserStructs(int userId,
  		List<Integer> userStructIds) {
    Map<Integer, StructureForUser> returnValue = new HashMap<Integer, StructureForUser>();
    if(null == userStructIds || userStructIds.isEmpty()) {
      log.error("no user struct ids!");
      return returnValue;
    }
    
    List<StructureForUser> userStructList = RetrieveUtils.userStructRetrieveUtils()
        .getSpecificOrAllUserStructsForUser(userId, userStructIds);
    for(StructureForUser us : userStructList) {
      if(null != us) {
        returnValue.put(us.getId(), us);
      } else {
        log.error("could not retrieve one of the user structs. userStructIds to retrieve="
            + userStructIds + ". user structs retrieved=" 
            + userStructList + ". Continuing with processing.");
      }
    }
    return returnValue;
  }
  
  //link up a user struct id with the structure object
  private Map<Integer, Structure> getUserStructIdsToStructs(Collection<StructureForUser> userStructs) {
    Map<Integer, Structure> returnValue = new HashMap<Integer, Structure>();
    Map<Integer, Structure> structIdsToStructs = StructureRetrieveUtils.getStructIdsToStructs();
    
    if(null == userStructs || userStructs.isEmpty()) {
      log.error("There are no user structs.");
    }
    
    for(StructureForUser us : userStructs) {
      int structId = us.getStructId();
      int userStructId = us.getId();
      
      Structure s = structIdsToStructs.get(structId);
      if(null != s) {
        returnValue.put(userStructId, s);
      } else {
        log.error("structure with id " + structId + " does not exist, therefore UserStruct is invalid:" + us);
      }
    }
    
    return returnValue;
  }
  
  private boolean checkLegitRetrieval(Builder resBuilder, User user,
  		List<Integer> userStructIds, 
      Map<Integer, StructureForUser> userStructIdsToUserStructs,
      Map<Integer, Structure> userStructIdsToStructures,
      Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval,
      List<Integer> duplicates, List<Integer> cashGained) {

    int userId = user.getId();
    
    if (user == null || userStructIds.isEmpty() || userStructIdsToUserStructs.isEmpty()
        || userStructIdsToStructures.isEmpty() || userStructIdsToTimesOfRetrieval.isEmpty()) { //|| timeOfRetrieval == null || userStruct.getLastRetrieved() == null) {
      log.error("user is null, or no struct ids, user structs, structures, or retrieval times . user=" +
        user + "\t userStructIds=" + userStructIds + "\t structIdsToUserStructs=" +
        userStructIdsToUserStructs + "\t userStructIdsToStructs=" + userStructIdsToStructures +
        "\t userStructIdsToRetrievalTimes=" + userStructIdsToTimesOfRetrieval);
      return false;
    }
    if (!duplicates.isEmpty()) {
      resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.OTHER_FAIL);
      log.warn("duplicate struct ids in request. ids=" + duplicates);
    }

    //calculate how much user gets
    int cash = 0;
    //go through the userStructIds the user sent, checking which structs can be
    //retrieved
    for (Integer id : userStructIds) {
      StructureForUser userStruct = userStructIdsToUserStructs.get(id);
      Timestamp timeOfRetrieval = userStructIdsToTimesOfRetrieval.get(id);
      Structure struct = userStructIdsToStructures.get(id);
      
      if (userId != userStruct.getUserId() || !userStruct.isComplete()) {
        resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.OTHER_FAIL);
        log.warn("(will continue processing) struct owner is not user, or struct" +
        		" is not complete yet. userStruct=" + userStruct);
        //remove invalid user structure
        userStructIdsToUserStructs.remove(id);
        userStructIdsToTimesOfRetrieval.remove(id);
        continue;
      }
//      if ((timeOfRetrieval.getTime() - userStruct.getLastRetrieved().getTime())  < 60000*struct.getMinutesToGain()) {
//        resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.NOT_LONG_ENOUGH);
//        log.warn("(will continue processing) struct not ready for retrieval yet. " +
//        		"time of retrieval=" + timeOfRetrieval + ", userStruct=" + userStruct +
//        		", takes this many minutes to gain:" + struct.getMinutesToGain()); 
//        //remove invalid user structure
//        userStructIdsToUserStructs.remove(id);
//        userStructIdsToTimesOfRetrieval.remove(id);
//        continue;
//      }
//      cash += struct.getIncome();
    }
    //return to the caller the amount of money the user can collect 
    cashGained.add(cash);
    
    return true;
  }
  
  private boolean writeChangesToDb(User user, int cashGain,
  		Map<Integer, StructureForUser> userStructIdsToUserStructs,
  		Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval) {
  	if (!user.updateRelativeCoinsCoinsretrievedfromstructs(cashGain)) {
      log.error("problem with updating user stats after retrieving " + cashGain + " cash");
      return false;
    }
    if (!UpdateUtils.get().updateUserStructsLastretrieved(userStructIdsToTimesOfRetrieval, userStructIdsToUserStructs)) {
      log.error("problem with updating user structs last retrieved for userStructIds " 
          + userStructIdsToTimesOfRetrieval);
      return false;
    }
    return true;
  }
  
  public void writeToUserCurrencyHistory(User aUser, int cashChange,
  		int previousCash, Timestamp curTime,
  		Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval) {

    Map<String, Integer> gemsCashChange = new HashMap<String, Integer>();
    Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> details = new HashMap<String, String>();
    String cash = MiscMethods.cash;
    String reasonForChange = ControllerConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT;
    StringBuilder detailSb = new StringBuilder();
    detailSb.append("userStructIds to times=");
    detailSb.append(userStructIdsToTimesOfRetrieval);
    
    
    gemsCashChange.put(cash, cashChange);
    previousGemsCash.put(cash, previousCash);
    reasonsForChanges.put(cash, reasonForChange);
    details.put(cash, detailSb.toString());
    
    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, curTime, gemsCashChange,
        previousGemsCash, reasonsForChanges, details);
  }
  
}
