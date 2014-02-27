package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginClanRaidRequestEvent;
import com.lvl6.events.response.BeginClanRaidResponseEvent;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BeginClanRaidRequestProto;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto.BeginClanRaidStatus;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class BeginClanRaidController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;
  
  
  protected Locker getLocker() {
		return locker;
	}

	protected void setLocker(Locker locker) {
		this.locker = locker;
	}
	
	@Autowired
	protected TimeUtils timeUtils;

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	
	public BeginClanRaidController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new BeginClanRaidRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_BEGIN_CLAN_RAID_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    BeginClanRaidRequestProto reqProto = ((BeginClanRaidRequestEvent)event).getBeginClanRaidRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    MinimumClanProto mcp = senderProto.getClan();
    int clanId = mcp.getClanId();
    int clanEventPersistentId = reqProto.getClanEventId();
    
    Date curDate = new Date(reqProto.getCurTime());
    Timestamp curTime = new Timestamp(curDate.getTime());
    int clanRaidId = reqProto.getRaidId(); //not really needed
    
    boolean setMonsterTeamForRaid = reqProto.getSetMonsterTeamForRaid();
    List<Integer> userMonsterIds = reqProto.getUserMonsterIdsList();
    boolean isFirstStage = reqProto.getIsFirstStage();
    
    BeginClanRaidResponseProto.Builder resBuilder = BeginClanRaidResponseProto.newBuilder();
    resBuilder.setStatus(BeginClanRaidStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    //OUTLINE: 
    //get the clan lock; get the clan raid object for the clan;
    // If doesn't exist, create it. If does exist, check to see if the raids are different.
    // If different, replace it with a new one. Else, do nothing.
    
    //ONLY GET CLAN LOCK IF TRYING TO BEGIN A RAID
    if (null != mcp && mcp.hasClanId()) {
    	clanId = mcp.getClanId();
    	if (0 != clanId && !setMonsterTeamForRaid) {
    		getLocker().lockClan(clanId);
    		log.info("locked clanId=" + clanId);
    	}
    }
    try {
//      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	UserClan uc = RetrieveUtils.userClanRetrieveUtils().getSpecificUserClan(userId, clanId);
      boolean legitRequest = checkLegitRequest(resBuilder, senderProto, userId,
      		clanId, uc, clanEventPersistentId, clanRaidId, curDate, curTime, 
      		setMonsterTeamForRaid, isFirstStage);

      BeginClanRaidResponseEvent resEvent = new BeginClanRaidResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setBeginClanRaidResponseProto(resBuilder.build()); 

      List<ClanEventPersistentForClan> clanInfoList = new ArrayList<ClanEventPersistentForClan>();
      boolean success = false;
      if (legitRequest) { 
      	log.info("recording in the db that the clan began a clan raid or setting monsters." +
      			" or starting a stage. isFirstStage=" + isFirstStage);
        success = writeChangesToDB(userId, clanId, clanEventPersistentId, clanRaidId,
        		curTime, setMonsterTeamForRaid, userMonsterIds, isFirstStage, clanInfoList);
      }
      
      if (success) {
      	ClanEventPersistentForClan cepfc = clanInfoList.get(0);
      	PersistentClanEventClanInfoProto eventDetails = CreateInfoProtoUtils
      			.createPersistentClanEventClanInfoProto(cepfc);
      	resBuilder.setEventDetails(eventDetails);
        resBuilder.setStatus(BeginClanRaidStatus.SUCCESS);
        log.info("BEGIN CLAN RAID EVENT SUCCESS!!!!!!!");
      }
      
      log.info("resBuilder=" + resBuilder);
      server.writeEvent(resEvent);
      
      if (success && !setMonsterTeamForRaid) {
      	//only write to the user if the request was valid and user is beginning a raid
      	server.writeClanEvent(resEvent, clanId);
      }
      
    } catch (Exception e) {
    	log.error("exception in BeginClanRaid processEvent", e);
    	try {
    	  resBuilder.setStatus(BeginClanRaidStatus.FAIL_OTHER);
    	  BeginClanRaidResponseEvent resEvent = new BeginClanRaidResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setBeginClanRaidResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in BeginClanRaid processEvent", e);
      }
    } finally {
    	
    	//ONLY RELEASE CLAN LOCK IF TRYING TO BEGIN A RAID
    	if (null != mcp && mcp.hasClanId()) {
    		if (0 != clanId && !setMonsterTeamForRaid) {
    			getLocker().unlockClan(clanId);
      		log.info("unlocked clanId=" + clanId);
    		}
      }
    	
    }
  }

  private boolean checkLegitRequest(Builder resBuilder, MinimumUserProto mupfc,
  		int userId, int clanId, UserClan uc, int clanEventId, int clanRaidId, Date curDate,
  		Timestamp curTime, boolean setMonsterTeamForRaid, boolean isFirstStage) {
    if (0 == clanId || 0 == clanRaidId || null == uc) {
      log.error("not in clan. user is " + mupfc + "\t or clanRaidId invalid id=" +
      		clanRaidId + "\t or no user clan exists. uc=" + uc);
      return false;      
    }
    
    //user can only start raid if an event exists for it, check if event exists,
    //clan raid events CAN overlap
    Map<Integer, ClanEventPersistent> clanEventIdToEvent = ClanEventPersistentRetrieveUtils
    		.getActiveClanEventIdsToEvents(curDate, timeUtils);
    if (!clanEventIdToEvent.containsKey(clanEventId)) {
    	resBuilder.setStatus(BeginClanRaidStatus.FAIL_NO_ACTIVE_CLAN_RAID);
    	log.error("no active clan event with id=" + clanEventId + "\t user=" + mupfc);
    	return false;
    }
    
    //give the event id back to the caller
    ClanEventPersistent cep = clanEventIdToEvent.get(clanEventId);
    int eventRaidId = cep.getClanRaidId();
    if (clanRaidId != eventRaidId) {
    	resBuilder.setStatus(BeginClanRaidStatus.FAIL_NO_ACTIVE_CLAN_RAID);
    	log.error("no active clan event with raidId=" + clanRaidId + "\t event=" + cep + 
    	"\t user=" + mupfc);
    	return false;
    }


    //only check if user can start raid if he is not setting his monsters
    //check if the clan already has existing raid information
    if (!setMonsterTeamForRaid) {
    	Set<Integer> authorizedUsers = getAuthorizedUsers(clanId);
    	if (!authorizedUsers.contains(userId)) {
    		resBuilder.setStatus(BeginClanRaidStatus.FAIL_NOT_AUTHORIZED);
    		log.error("user can't start raid. user=" + mupfc);
    		return false;      
    	}
    	//user is authorized to start clan raid
    	
    	if (!validClanInfo(resBuilder, clanId, clanEventId, clanRaidId, curDate,
    			curTime, isFirstStage)) {
    		return false;
    	}
    }
    
    //Don't think any checks need to be made
    //(user needs to equip user monsters before beginning raid; checks are done there) 
    if (setMonsterTeamForRaid) {
    	
    }
    
    return true;
  }
  
  //get all the members in a clan
  private Set<Integer> getAuthorizedUsers(int clanId) {
  	Set<Integer> authorizedUsers = new HashSet<Integer>();
  	List<Integer> statuses = new ArrayList<Integer>();
    statuses.add(UserClanStatus.LEADER_VALUE);
    statuses.add(UserClanStatus.JUNIOR_LEADER_VALUE);
    statuses.add(UserClanStatus.CAPTAIN_VALUE);
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatuses(clanId, statuses);
    
    if (null != userIds && !userIds.isEmpty()) {
    	authorizedUsers.addAll(userIds);
    }
    
    return authorizedUsers;
  }
  
  private boolean validClanInfo(Builder resBuilder, int clanId, int clanEventId, 
  		int clanRaidId, Date curDate, Timestamp now, boolean isFirstStage) {
  	//check if clan already started the event
  	ClanEventPersistentForClan raidStartedByClan = ClanEventPersistentForClanRetrieveUtils
  			.getPersistentEventForClanId(clanId);
  	
  	if (null == raidStartedByClan && isFirstStage) {
  		return true;
  	} else if (null == raidStartedByClan && !isFirstStage) {
  		log.error("clan has not started a raid/event (nothing in clan_event_persistent_for_clan)" +
  				" but client claims clan started one. clanId=" + clanId + "\t clanEventId=" +
  				clanEventId + "\t clanRaidId=" + clanRaidId + "\t isFirstStage=" + isFirstStage);
  		return false;
  	}
  	
  	//clan raid/event entry exists for clan
  	int ceId = raidStartedByClan.getClanEventPersistentId();
  	int crId = raidStartedByClan.getCrId();
  	
  	if ((ceId != clanEventId || crId != clanRaidId) ||
  			(null != raidStartedByClan && isFirstStage)) {
  		log.warn("possibly encountered clan raid data that should have been pushed to" +
  				" history. pushing now. clanEvent=" + raidStartedByClan + "\t clanEventId=" + 
  				clanEventId + "\t clanRaidId=" + clanRaidId + "\t isFirstStage=" + isFirstStage);
  		//record this (raidStartedByClan) in history along with all the clan users' stuff
  		//but should I be doing this
  		pushCurrentClanEventDataToHistory(clanId, now, raidStartedByClan);
  		return true;
  	}
  	
  	//entered case where null != raidStartedByClan && !isFirstStage
  	//verified clanEventId and clan raid id are consistent, now time to verify
  	//the clan raid stage
  	
  	//the clan raid stage start time should be null/not set
  	if (null != raidStartedByClan.getStageStartTime()) {
  		log.warn("the clan raid stage start time is not null when beginning clan raid." +
  				" clanEvent=" + raidStartedByClan);
  		//let the testers/users notify us that something is wrong, because
  		//I don't know how to resolve this issue
  		return false;
  	}
  	
  	//maybe clan started event last week and didn't push the clan related 
  	//information on the raid to the history table when event ended.
  	//TODO: if so, do it now and do it for the clan users' stuff as well
//  	Date raidStartedByClanDate = raidStartedByClan.getStageStartTime();
//  	int dayOfMonthRaidBegan = timeUtils.getDayOfMonthPst(raidStartedByClanDate);
//  	int dayOfMonthNow = timeUtils.getDayOfMonthPst(curDate);

  	log.info("valid clan info, can begin raid.");
  	return true;
  }
  
  //copy pasted from RecordClanRaidStatsController.writeChangesToDB
  private void pushCurrentClanEventDataToHistory(int clanId, Timestamp now,
  		ClanEventPersistentForClan clanEvent) {
  	int clanEventPersistentId = clanEvent.getClanEventPersistentId();
  	int crId = clanEvent.getCrId();
  	int crsId = clanEvent.getCrsId();
  	Timestamp stageStartTime = null;
  	if (null != clanEvent.getStageStartTime()) {
  		stageStartTime = new Timestamp(clanEvent.getStageStartTime().getTime());
  	}
  	int crsmId = clanEvent.getCrsmId();
  	Timestamp stageMonsterStartTime = null;
  	if (null != clanEvent.getStageMonsterStartTime()) {
  		stageMonsterStartTime = new Timestamp(clanEvent.getStageMonsterStartTime().getTime());
  	}
  	boolean won = false;
  	
  	//record whatever is in the ClanEventPersistentForClan
  	int numInserted = InsertUtils.get().insertIntoClanEventPersistentForClanHistory(clanId,
  			now, clanEventPersistentId, crId, crsId, stageStartTime, crsmId,
  			stageMonsterStartTime, won);
  	
  	log.info("rows inserted into clan raid info for clan (should be 1): " + numInserted);
  	//get all the clan raid info for the users, and then delete them
  	Map<Integer, ClanEventPersistentForUser> clanUserInfo = ClanEventPersistentForUserRetrieveUtils
  			.getPersistentEventUserInfoForClanId(clanId);
  	
  	
  	//delete clan info for clan raid
  	DeleteUtils.get().deleteClanEventPersistentForClan(clanId);
  	
  	if (null != clanUserInfo && !clanUserInfo.isEmpty()) {
  		//record whatever is in the ClanEventPersistentForUser
  		numInserted = InsertUtils.get().insertIntoClanEventPersistentForUserHistory(
  				clanEventPersistentId, now, clanUserInfo);
  		log.info("rows inserted into clan raid info for user (should be " + 
  				clanUserInfo.size() + "): " + numInserted);
  		
  		//delete clan user info for clan raid
  		List<Integer> userIdList = new ArrayList<Integer>(clanUserInfo.keySet());
  		DeleteUtils.get().deleteClanEventPersistentForUsers(userIdList);
  	
  	}
  }
  
  private boolean writeChangesToDB(int userId, int clanId, int clanEventPersistentId,
  		int clanRaidId, Timestamp curTime, boolean setMonsterTeamForRaid,
  		List<Integer> userMonsterIds, boolean isFirstStage, List<ClanEventPersistentForClan> clanInfo) {
  	
  	if (setMonsterTeamForRaid) {
  		int numInserted = InsertUtils.get().insertIntoUpdateMonstersClanEventPersistentForUser(
  				userId, clanId, clanRaidId, userMonsterIds);
  				

  		log.info("num rows inserted into clan raid info for user table: " + numInserted);

  	} else if (isFirstStage) {
  		ClanRaidStage crs = ClanRaidStageRetrieveUtils.getFirstStageForClanRaid(clanRaidId);
  		int clanRaidStageId = crs.getId();

  		Map<Integer, ClanRaidStageMonster> stageIdToMonster = ClanRaidStageMonsterRetrieveUtils
  				.getClanRaidStageMonstersForClanRaidStageId(clanRaidStageId);
  		ClanRaidStageMonster crsm = stageIdToMonster.get(clanRaidStageId);
  		int crsmId = crsm.getId();

  		//once user begins a raid he auto begins a stage and auto begins the first monster
  		int numInserted = InsertUtils.get().insertIntoClanEventPersistentForClan(clanId,
  				clanEventPersistentId, clanRaidId, clanRaidStageId, curTime, crsmId, curTime);

  		log.info("num rows inserted into clan raid info for clan table: " + numInserted);

  		ClanEventPersistentForClan cepfc = new ClanEventPersistentForClan(clanId,
  				clanEventPersistentId, clanRaidId, clanRaidStageId, curTime, crsmId, curTime);
  		clanInfo.add(cepfc);
  	} else {
  		log.info("starting another clan raid stage!!!!!!!!!");
  		int numUpdated = UpdateUtils.get().updateClanEventPersistentForClanStageStartTime(
  				clanId, curTime);
  		log.info("num rows updated in clan raid info for clan table: " + numUpdated);

  	}
  	
  	return true;
  }

}
