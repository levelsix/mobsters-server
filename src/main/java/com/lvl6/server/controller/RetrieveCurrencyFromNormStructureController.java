package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveCurrencyFromNormStructureRequestEvent;
import com.lvl6.events.response.RetrieveCurrencyFromNormStructureResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto.StructRetrieval;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.RetrieveCurrencyFromNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class RetrieveCurrencyFromNormStructureController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;
  
  @Autowired
  protected StructureForUserRetrieveUtils2 userStructRetrieveUtils;

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
    log.info(String.format("reqProto=%s", reqProto));
    //get stuff client sent
    MinimumUserProtoWithMaxResources senderResourcesProto = reqProto.getSender();
    MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
    String userId = senderProto.getUserUuid();
    List<StructRetrieval> structRetrievals = reqProto.getStructRetrievalsList();
    Timestamp curTime = new Timestamp((new Date()).getTime());
    int maxCash = senderResourcesProto.getMaxCash();
    int maxOil = senderResourcesProto.getMaxOil();
    
    Map<String, Timestamp> userStructIdsToTimesOfRetrieval =  new HashMap<String, Timestamp>();
    Map<String, Integer> userStructIdsToAmountCollected = new HashMap<String, Integer>();
    List<String> duplicates = new ArrayList<String>();
    //create map from ids to times and check for duplicates
    getIdsAndTimes(structRetrievals, duplicates,
    		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected); 
    
    List<String> userStructIds = new ArrayList<String>(userStructIdsToTimesOfRetrieval.keySet());
    
    RetrieveCurrencyFromNormStructureResponseProto.Builder resBuilder =
    		RetrieveCurrencyFromNormStructureResponseProto.newBuilder();
    resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
    resBuilder.setSender(senderResourcesProto);

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
      resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
      RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setRetrieveCurrencyFromNormStructureResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }

    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      User user = getUserRetrieveUtils().getUserById(senderProto.getUserUuid());
      int previousCash = 0;
      int previousOil = 0;
      int previousGems = 0;
      
      Map<String, StructureForUser> userStructIdsToUserStructs = 
      		getUserStructIdsToUserStructs(userId, userStructIds);
      Map<String, StructureResourceGenerator> userStructIdsToGenerators =
      		getUserStructIdsToResourceGenerators(userStructIdsToUserStructs.values());
      
      //this will contain the amount user collects
      Map<String, Integer> resourcesGained = new HashMap<String, Integer>();
      //userStructIdsToTimesOfRetrieval and userStructIdsToUserStructs will be
      //modified to contain only the valid user structs user can retrieve currency from
      boolean legitRetrieval = checkLegitRetrieval(resBuilder, user, userStructIds,
      		userStructIdsToUserStructs, userStructIdsToGenerators, duplicates,
      		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected, resourcesGained);
      
      int cashGain = 0;
      int oilGain = 0;
      int gemsGain = 0;
      Map<String, Integer> currencyChange = new HashMap<String, Integer>();
      boolean successful = false;
      if (legitRetrieval) {
      	cashGain = resourcesGained.get(MiscMethods.cash);
        previousCash = user.getCash();
        oilGain = resourcesGained.get(MiscMethods.oil);
        previousOil = user.getOil();
        gemsGain = resourcesGained.get(MiscMethods.gems);
        previousGems = user.getGems();
        
        successful = writeChangesToDb(user, cashGain, oilGain, gemsGain,
        		userStructIdsToUserStructs, userStructIdsToTimesOfRetrieval,
        		userStructIdsToAmountCollected, maxCash, maxOil,
        		currencyChange);
      }
      if (successful) {
      	resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.SUCCESS);
      }

      RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setRetrieveCurrencyFromNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
      
      if (legitRetrieval) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods
        		.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, null);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, previousCash, previousOil, previousGems,
        		curTime, userStructIdsToUserStructs, userStructIdsToGenerators,
        		userStructIdsToTimesOfRetrieval,
        		userStructIdsToAmountCollected, currencyChange);
      }
    } catch (Exception e) {
      log.error("exception in RetrieveCurrencyFromNormStructureController processEvent", e);
      //don't let the client hang
      try {
        resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
        RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(userId);
        resEvent.setTag(event.getTag());
        resEvent.setRetrieveCurrencyFromNormStructureResponseProto(resBuilder.build());
        server.writeEvent(resEvent);
      } catch (Exception e2) {
        log.error("exception2 in RetrieveCurrencyFromNormStructureController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());      
    }
  }

  //separate the duplicate ids from the unique ones
  private void getIdsAndTimes(List<StructRetrieval> srList,  List<String> duplicates,
  		Map<String, Timestamp> structIdsToTimesOfRetrieval,
  		Map<String, Integer> structIdsToAmountCollected) {
    if (srList.isEmpty()) {
      log.error("RetrieveCurrencyFromNormStruct request did not send any user struct ids.");
      return;
    }
    
    for(StructRetrieval sr : srList) {
      String key = sr.getUserStructUuid();
      Timestamp value = new Timestamp(sr.getTimeOfRetrieval());
      int amount = sr.getAmountCollected();
      
      if(structIdsToTimesOfRetrieval.containsKey(key)) {
        duplicates.add(key);
      } else {
        structIdsToTimesOfRetrieval.put(key, value);
        structIdsToAmountCollected.put(key, amount);
      }
    }
  }
  
  //retrieve these user structs from the db and put them in a map
  private Map<String, StructureForUser> getUserStructIdsToUserStructs(String userId,
  		List<String> userStructIds) {
    Map<String, StructureForUser> returnValue = new HashMap<String, StructureForUser>();
    if(null == userStructIds || userStructIds.isEmpty()) {
      log.error("no user struct ids!");
      return returnValue;
    }
    
    List<StructureForUser> userStructList = getUserStructRetrieveUtils()
        .getSpecificOrAllUserStructsForUser(userId, userStructIds);
    for(StructureForUser us : userStructList) {
      if(null != us) {
        returnValue.put(us.getId(), us);
      } else {
    	  String preface = "could not retrieve one of the user structs.";
        log.error(String.format(
        	"%s userStructIds to retrieve=%s. user structs retrieved=%s. Will continue processing.",
            preface, userStructIds, userStructList));
      }
    }
    return returnValue;
  }
  
  //link up a user struct id with the structure object
  private Map<String, StructureResourceGenerator> getUserStructIdsToResourceGenerators(
  		Collection<StructureForUser> userStructs) {
    Map<String, StructureResourceGenerator> returnValue =
    		new HashMap<String, StructureResourceGenerator>();
    Map<Integer, StructureResourceGenerator> structIdsToStructs = 
    		StructureResourceGeneratorRetrieveUtils.getStructIdsToResourceGenerators();
    
    if(null == userStructs || userStructs.isEmpty()) {
      log.error("There are no user structs.");
    }
    
    for(StructureForUser us : userStructs) {
      int structId = us.getStructId();
      String userStructId = us.getId();
      
      StructureResourceGenerator s = structIdsToStructs.get(structId);
      if(null != s) {
        returnValue.put(userStructId, s);
      } else {
        log.error(String.format(
        	"structure with id %s does not exist, therefore UserStruct is invalid:%s",
        	structId, us));
      }
    }
    
    return returnValue;
  }
  
  private boolean checkLegitRetrieval(Builder resBuilder, User user,
  		List<String> userStructIds, 
      Map<String, StructureForUser> userStructIdsToUserStructs,
      Map<String, StructureResourceGenerator> userStructIdsToGenerators,
      List<String> duplicates,
      Map<String, Timestamp> userStructIdsToTimesOfRetrieval,
      Map<String, Integer> userStructIdsToAmountCollected,
      Map<String, Integer> resourcesGained) {

    String userId = user.getId();
    
    if (user == null || userStructIds.isEmpty() || userStructIdsToUserStructs.isEmpty()
        || userStructIdsToGenerators.isEmpty() || userStructIdsToTimesOfRetrieval.isEmpty()) { 
      log.error("user is null, or no struct ids, user structs, structures, or retrieval times . user=" +
        user + "\t userStructIds=" + userStructIds + "\t structIdsToUserStructs=" +
        userStructIdsToUserStructs + "\t userStructIdsToGenerators=" + userStructIdsToGenerators +
        "\t userStructIdsToRetrievalTimes=" + userStructIdsToTimesOfRetrieval);
      return false;
    }
    if (!duplicates.isEmpty()) {
      resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
      log.warn(String.format(
    	  "duplicate struct ids in request. ids=%s", duplicates));
    }

    //go through the userStructIds the user sent, checking which structs can be
    //retrieved
    int cash = 0;
    int oil = 0;
    int gems = 0;
    
    //get all user money trees
    Map<String, StructureForUser> userMoneyTreeMap = userStructRetrieveUtils.getMoneyTreeForUserMap(userId, null);
    
    for (String id : userStructIds) {
      StructureForUser userStruct = userStructIdsToUserStructs.get(id);
      
      if (null == userStruct || !userId.equals(userStruct.getUserId()) || !userStruct.isComplete()) {
        resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
        log.error("(will continue processing) struct owner is not user, or struct" +
        		" is not complete yet. userStruct=" + userStruct);
        //remove invalid user structure
        userStructIdsToUserStructs.remove(id);
        userStructIdsToTimesOfRetrieval.remove(id);
        userStructIdsToAmountCollected.remove(id);
        continue;
      }

      if(userMoneyTreeMap.containsKey(id)) {
    	  gems += userStructIdsToAmountCollected.get(id);
      }
      else {
    	  StructureResourceGenerator struct = userStructIdsToGenerators.get(id);

    	  String type = struct.getResourceTypeGenerated();
    	  ResourceType rt = ResourceType.valueOf(type);
    	  if (ResourceType.CASH.equals(rt)) {
    		  cash += userStructIdsToAmountCollected.get(id);
    	  } else if (ResourceType.OIL.equals(rt)) {
    		  oil += userStructIdsToAmountCollected.get(id);
    	  } else {
    		  log.error("(will continue processing) unknown resource type: " + rt);
    		  //remove invalid user structure
    		  userStructIdsToUserStructs.remove(id);
    		  userStructIdsToTimesOfRetrieval.remove(id);
    		  userStructIdsToAmountCollected.remove(id);
    	  }
      }
    }
    //return to the caller the amount of money the user can collect 
    resourcesGained.put(MiscMethods.cash, cash);
    resourcesGained.put(MiscMethods.oil, oil);
    resourcesGained.put(MiscMethods.gems, gems);
    
    return true;
  }
  
  private boolean writeChangesToDb(User user, int cashGain, int oilGain, int gemsGain,
  		Map<String, StructureForUser> userStructIdsToUserStructs,
  		Map<String, Timestamp> userStructIdsToTimesOfRetrieval,
  		Map<String, Integer> userStructIdsToAmountCollected, int maxCash,
  		int maxOil, Map<String, Integer> currencyChange) {
  	//capping how much the user can gain of a certain resource
  	int curCash = Math.min(user.getCash(), maxCash); //in case user's cash is more than maxCash
  	int maxCashUserCanGain = maxCash - curCash; //this is the max cash the user can gain
  	cashGain = Math.min(maxCashUserCanGain, cashGain);
  			
  	int curOil = Math.min(user.getOil(), maxOil); //in case user's oil is more than maxOil
  	int maxOilUserCanGain = maxOil - curOil;
  	oilGain = Math.min(maxOilUserCanGain, oilGain);
  	
  	if (cashGain <= 0 && oilGain <= 0 && gemsGain <= 0)
  	{
  		log.error(String.format(
  			"cash,oil, gems all invalid. cash=%s \t oil=%s \t gem=%s",
  			cashGain, oilGain, gemsGain) );
  		return false;
  	}

  	if (!user.updateRelativeCoinsOilRetrievedFromStructs(cashGain, oilGain, gemsGain))
  	{
  		log.error(String.format(
  			"can't update user stats after retrieving %s cash \t %s oil.",
  			cashGain, oilGain) );
  		return false;

  	} else {
  		if (0 != oilGain) {
  			currencyChange.put(MiscMethods.oil, oilGain);
  		}
  		if (0 != cashGain) {
  			currencyChange.put(MiscMethods.cash, cashGain);
  		}
  		if (0 != gemsGain) {
  			currencyChange.put(MiscMethods.gems, gemsGain);
  		}
  	}
  	
    if (!UpdateUtils.get().updateUserStructsLastRetrieved(userStructIdsToTimesOfRetrieval, userStructIdsToUserStructs)) {
      log.error(String.format(
    	  "problem updating user structs last retrieved for userStructIds %s", 
          userStructIdsToTimesOfRetrieval));
      return false;
    }
    return true;
  }
  
  public void writeToUserCurrencyHistory(User aUser, int previousCash,
		  int previousOil, int previousGems, Timestamp curTime,
		  Map<String, StructureForUser> userStructIdsToUserStructs,
		  Map<String, StructureResourceGenerator> userStructIdsToGenerators,
		  Map<String, Timestamp> userStructIdsToTimesOfRetrieval,
		  Map<String, Integer> userStructIdsToAmountCollected,
		  Map<String, Integer> currencyChange) {

	  String userId = aUser.getId();
	  Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
	  Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
	  Map<String, String> reasonsForChanges = new HashMap<String, String>();
	  Map<String, String> details = new HashMap<String, String>();
	  String cash = MiscMethods.cash;
	  String oil = MiscMethods.oil;
	  String gems = MiscMethods.gems;
	  String reasonForChange = ControllerConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT;
	  String reasonForGemChange = ControllerConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_MONEY_TREE;
	  StringBuilder cashDetailSb = new StringBuilder();
	  cashDetailSb.append("(userStructId,time,amount)=");
	  StringBuilder oilDetailSb = new StringBuilder();
	  oilDetailSb.append("(userStructId,time,amount)=");
	  StringBuilder gemsDetailSb = new StringBuilder();
	  gemsDetailSb.append("(userStructId,time,amount)=");

	  Map<String, StructureForUser> userMoneyTreeMap = userStructRetrieveUtils.getMoneyTreeForUserMap(userId, null);

	  //being descriptive, separating cash stuff from oil stuff
	  for(String id : userStructIdsToAmountCollected.keySet()) {
		  StructureForUser sfu = userStructIdsToUserStructs.get(id);
		  Timestamp t = userStructIdsToTimesOfRetrieval.get(id);
		  int amount = userStructIdsToAmountCollected.get(id);

		  if(userMoneyTreeMap.containsKey(id)) {
			  gemsDetailSb.append("(");
			  gemsDetailSb.append(id);
			  gemsDetailSb.append(",");
			  gemsDetailSb.append(t);
			  gemsDetailSb.append(",");
			  gemsDetailSb.append(amount);
			  gemsDetailSb.append(")");
		  }
		  else {

			  StructureResourceGenerator struct = userStructIdsToGenerators.get(id);


			  String type = struct.getResourceTypeGenerated();
			  ResourceType rt = ResourceType.valueOf(type);
			  if (ResourceType.CASH.equals(rt)) {
				  cashDetailSb.append("(");
				  cashDetailSb.append(id);
				  cashDetailSb.append(",");
				  cashDetailSb.append(t);
				  cashDetailSb.append(",");
				  cashDetailSb.append(amount);
				  cashDetailSb.append(")");

			  } else if (ResourceType.OIL.equals(rt)) {
				  oilDetailSb.append("(");
				  oilDetailSb.append(id);
				  oilDetailSb.append(",");
				  oilDetailSb.append(t);
				  oilDetailSb.append(",");
				  oilDetailSb.append(amount);
				  oilDetailSb.append(")");
			  }
		  }
	  }

	  previousCurrencies.put(cash, previousCash);
	  previousCurrencies.put(oil, previousOil);
	  previousCurrencies.put(gems,  previousGems);
	  currentCurrencies.put(cash, aUser.getCash());
	  currentCurrencies.put(oil, aUser.getOil());
	  currentCurrencies.put(gems,  aUser.getGems());
	  reasonsForChanges.put(cash, reasonForChange);
	  reasonsForChanges.put(oil, reasonForChange);
	  reasonsForChanges.put(gems, reasonForGemChange);
	  details.put(cash, cashDetailSb.toString());
	  details.put(oil, oilDetailSb.toString());
	  details.put(gems, gemsDetailSb.toString());

	  MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
			  previousCurrencies, currentCurrencies, reasonsForChanges, details);
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

  public StructureForUserRetrieveUtils2 getUserStructRetrieveUtils() {
    return userStructRetrieveUtils;
  }

  public void setUserStructRetrieveUtils(
      StructureForUserRetrieveUtils2 userStructRetrieveUtils) {
    this.userStructRetrieveUtils = userStructRetrieveUtils;
  }

}
