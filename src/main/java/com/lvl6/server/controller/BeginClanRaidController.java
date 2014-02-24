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
import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BeginClanRaidRequestProto;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto.BeginClanRaidStatus;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class BeginClanRaidController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;
  
  
  protected HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	protected void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
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

    MinimumUserProtoForClans senderProto = reqProto.getSender();
    UserClanStatus clanStatus = senderProto.getClanStatus();
    MinimumUserProtoWithLevel mupwl = senderProto.getMinUserProto().getMinUserProtoWithLevel();
    MinimumUserProto mup = mupwl.getMinUserProto();
    int userId = mup.getUserId();
    MinimumClanProto mcp = mup.getClan();
    int clanId = 0;
    
    Date curDate = new Date(reqProto.getCurTime());
    Timestamp curTime = new Timestamp(curDate.getTime());
    int clanRaidId = reqProto.getRaidId();
    
    boolean setMonsterTeamForRaid = reqProto.getSetMonsterTeamForRaid();
    List<Integer> userMonsterIds = reqProto.getUserMonsterIdsList();
    
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
    		getHazelcastPvpUtil().lockClan(clanId);
    	}
    }
    try {
//      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	List<Integer> clanEventPersistentIdList = new ArrayList<Integer>();
      boolean legitRequest = checkLegitRequest(resBuilder, senderProto, userId,
      		clanStatus, clanId, clanRaidId, curDate, setMonsterTeamForRaid,
      		userMonsterIds, clanEventPersistentIdList);

      BeginClanRaidResponseEvent resEvent = new BeginClanRaidResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setBeginClanRaidResponseProto(resBuilder.build()); 

      List<ClanEventPersistentForClan> clanInfoList = new ArrayList<ClanEventPersistentForClan>();
      boolean success = false;
      if (legitRequest) { 
      	int clanEventPersistentId = clanEventPersistentIdList.get(0);
        success = writeChangesToDB(userId, clanId, clanEventPersistentId, clanRaidId,
        		curTime, setMonsterTeamForRaid, userMonsterIds, clanInfoList);
      }
      
      if (success) {
      	ClanEventPersistentForClan cepfc = clanInfoList.get(0);
      	PersistentClanEventClanInfoProto eventDetails = CreateInfoProtoUtils
      			.createPersistentClanEventClanInfoProto(cepfc);
      	resBuilder.setEventDetails(eventDetails);
      }
      server.writeEvent(resEvent);
      
      if (legitRequest) {
      	//only write to the user if the request was valid
      	server.writeClanEvent(resEvent, clanId);
      }
      
    } catch (Exception e) {
      log.error("exception in BeginClanRaid processEvent", e);
    } finally {
    	
    	//ONLY RELEASE CLAN LOCK IF TRYING TO BEGIN A RAID
    	if (null != mcp && mcp.hasClanId()) {
    		if (0 != clanId && !setMonsterTeamForRaid) {
    			getHazelcastPvpUtil().unlockClan(clanId);
    		}
      }
    	
    }
  }

  private boolean checkLegitRequest(Builder resBuilder, MinimumUserProtoForClans mupfc,
  		int userId, UserClanStatus userClanStatus, int clanId, int clanRaidId, Date curDate,
  		boolean setMonsterTeamForRaid, List<Integer> userMOnsterIds, 
  		List<Integer> clanEventPersistentId) {
    if (0 == clanId || 0 == clanRaidId) {
      log.error("not in clan. user is " + mupfc + ", or clanRaidId invalid id=" + clanRaidId);
      return false;      
    }
    //only check if user can start raid if he is not setting his monsters
    if (!setMonsterTeamForRaid) {
    	//established user is in clan and clan raid id is specified
    	//check if user can begin (don't know if necessary because client can check)
    	Set<Integer> authorizedUsers = getAuthorizedUsers(clanId);
    	if (!authorizedUsers.contains(userId)) {
    		resBuilder.setStatus(BeginClanRaidStatus.FAIL_NOT_AUTHORIZED);
    		log.error("user can't start raid. user=" + mupfc);
    		return false;      
    	}
    	//user is authorized to start clan raid
    }
    
    //user can only start raid if an event exists for it, check if event exists,
    //AT THE MOMENT SHOULD ONLY BE ONE ACTIVE CLAN RAID; no clan raids should overlap
    Map<Integer, ClanEventPersistent> clanRaidIdToEvent = ClanEventPersistentRetrieveUtils
    		.getActiveClanRaidIdsToEvents(curDate, timeUtils);
    if (!clanRaidIdToEvent.containsKey(clanRaidId)) {
    	resBuilder.setStatus(BeginClanRaidStatus.FAIL_NO_ACTIVE_CLAN_RAID);
    	log.error("clan raid id requested does not exists. " + clanRaidId + "\t user=" + mupfc);
    	return false;
    }
    if (clanRaidIdToEvent.size() > 1) {
    	log.warn("multiple clan raids and clan raid events overlapping. clanRaidIdToEvent=" +
    			clanRaidIdToEvent);
    }
    
    //give the event id back to the caller
    ClanEventPersistent cep = clanRaidIdToEvent.get(clanRaidId);
    int eventId = cep.getId();
    clanEventPersistentId.add(eventId);

    //check if the clan already has existing raid information
    if (!setMonsterTeamForRaid) {
    	if (!validClanInfo(resBuilder, clanId, clanRaidId, curDate)) {
    		return false;
    	}
    }
    
    if (setMonsterTeamForRaid) {
    	
    }
    
    resBuilder.setStatus(BeginClanRaidStatus.SUCCESS);
    return true;
  }
  
  private boolean validClanInfo(Builder resBuilder, int clanId, int clanRaidId, Date curDate) {
  	//event for the raid exists, now check if clan already started the event
  	ClanEventPersistentForClan raidStartedByClan = ClanEventPersistentForClanRetrieveUtils
  			.getPersistentEventForClanId(clanId);
  	int raidIdStartedByClan = raidStartedByClan.getCrId();
  	if (null != raidStartedByClan && raidIdStartedByClan != clanRaidId) {
  		//TODO:
  		//if clan raid id not the same then, record this (cepfc) in history along with
  		//all the clan users' stuff

  	} else if (null != raidStartedByClan && raidIdStartedByClan == clanRaidId) {
  		//if time clan started the raid is the "same as now" then fail this request
  		//check if the same day of month
  		Date raidStartedByClanDate = raidStartedByClan.getStageStartTime();
  		int dayOfMonthRaidBegan = timeUtils.getDayOfMonthPst(raidStartedByClanDate);
  		int dayOfMonthNow = timeUtils.getDayOfMonthPst(curDate);

  		if (dayOfMonthRaidBegan == dayOfMonthNow) {
  			//return false under the assumption that a clan raid cannot be interspersed 
  			//throughout one day (start at 3am end at 6am then start at 7am end at 9am)
  			resBuilder.setStatus(BeginClanRaidStatus.FAIL_ALREADY_STARTED);
  			log.error("user trying to begin raid that is already started. existing raid" +
  					" started by clan=" + raidStartedByClan + "\t now=" + curDate);
  			return false;
  		}
  		//maybe clan started event last week and didn't push the clan related 
  		//information on the raid to the history table when event ended.
  		//TODO: So do it now and do it for the clan users' stuff as well
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

  private boolean writeChangesToDB(int userId, int clanId, int clanEventPersistentId,
  		int clanRaidId, Timestamp curTime, boolean setMonsterTeamForRaid,
  		List<Integer> userMonsterIds, List<ClanEventPersistentForClan> clanInfo) {
  	
  	if (setMonsterTeamForRaid) {
  		int numInserted = InsertUtils.get().insertIntoUpdateMonstersClanEventPersistentForUser(
  				userId, clanId, clanRaidId, userMonsterIds);
  				

  		log.info("num rows inserted into clan raid info for user table: " + numInserted);

  	} else {
  		ClanRaidStage crs = ClanRaidStageRetrieveUtils.getFirstStageForClanRaid(clanRaidId);
  		int clanRaidStageId = crs.getId();

  		Map<Integer, ClanRaidStageMonster> stageIdToMonster = ClanRaidStageMonsterRetrieveUtils
  				.getClanRaidStageMonstersForClanRaidStageId(clanRaidStageId);
  		ClanRaidStageMonster crsm = stageIdToMonster.get(clanRaidStageId);
  		int crsmId = crsm.getId();

  		//once user begins a raid he auto begins a stage and auto begins the first monster
  		int numInserted = InsertUtils.get().insertIntoClanEventPersistentForClan(clanId,
  				clanEventPersistentId, clanRaidId, clanRaidStageId, curTime, crsmId, curTime);

  		log.info("num rows inserted into clan raid info for user table: " + numInserted);

  		ClanEventPersistentForClan cepfc = new ClanEventPersistentForClan(clanId,
  				clanEventPersistentId, clanRaidId, clanRaidStageId, curTime, crsmId, curTime);
  		clanInfo.add(cepfc);
  	}
  	
  	return true;
  }

}
