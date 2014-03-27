package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RecordClanRaidStatsRequestEvent;
import com.lvl6.events.response.RecordClanRaidStatsResponseEvent;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsRequestProto;
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsResponseProto;
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsResponseProto.Builder;
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsResponseProto.RecordClanRaidStatsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class RecordClanRaidStatsController extends EventController {

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
  protected Locker locker;
  
	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
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

	
	public RecordClanRaidStatsController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new RecordClanRaidStatsRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_RECORD_CLAN_RAID_STATS_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RecordClanRaidStatsRequestProto reqProto = ((RecordClanRaidStatsRequestEvent)event)
    		.getRecordClanRaidStatsRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int clanId = reqProto.getClanId();
    Timestamp now = new Timestamp(reqProto.getClientTime());
    
    RecordClanRaidStatsResponseProto.Builder resBuilder = RecordClanRaidStatsResponseProto.newBuilder();
    resBuilder.setStatus(RecordClanRaidStatsStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    //OUTLINE: 
    //get the clan lock; get the clan raid object for the clan;
    // If does exist, record it and then delete it; same for clan user stuff.
    //Else, do nothing.

    //will hold stuff to be stored to history (but not necessary to do so)
    ClanEventPersistentForClan clanEvent = null;
    Map<Integer, ClanEventPersistentForUser> userIdToClanUserInfo = 
    		new HashMap<Integer, ClanEventPersistentForUser>();
    //boolean errorless = true;
//    if (0 != clanId) {
//    	getLocker().lockClan(clanId);
//    }
    try {
//      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    	clanEvent = ClanEventPersistentForClanRetrieveUtils.getPersistentEventForClanId(clanId);
      boolean legitRequest = checkLegitRequest(resBuilder, senderProto, userId,
      		clanId, clanEvent);

      RecordClanRaidStatsResponseEvent resEvent = new RecordClanRaidStatsResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setRecordClanRaidStatsResponseProto(resBuilder.build()); 

      boolean success = false;
      if (legitRequest) { 
        success = writeChangesToDB(userId, clanId, now, clanEvent, userIdToClanUserInfo);
      }
      
      if (success) {
      }
      server.writeEvent(resEvent);
      
      if (legitRequest) {
      	//only write to the user if the request was valid
      	server.writeClanEvent(resEvent, clanId);
      }
    } catch (Exception e) {
    	//errorless = false;
    	try {
    	  resBuilder.setStatus(RecordClanRaidStatsStatus.FAIL_OTHER);
    	  RecordClanRaidStatsResponseEvent resEvent = new RecordClanRaidStatsResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setRecordClanRaidStatsResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception in RecordClanRaidStats processEvent", e);
      }
    } finally {
    	
//    	if (0 != clanId) {
//      	getLocker().unlockClan(clanId);
//      }
    	
    }
    
//    //not necessary, can just delete this part (purpose is to record in detail, a user's
//    //contribution to a clan raid) on the clan raid stage monster level
//    try {
//    	if (errorless && null != clanEvent && !userIdToClanUserInfo.isEmpty()) {
//    		int numInserted = InsertUtils.get().insertIntoClanEventPersistentForUserHistoryDetail(
//    				now, userIdToClanUserInfo, clanEvent);
//    		log.error("num raid detail inserted = " + numInserted + "\t should be " +
//    				userIdToClanUserInfo.size());
//    	}
//    } catch (Exception e) {
//    	log.warn("could not record more details about clan raid", e);
//    }
  }

  private boolean checkLegitRequest(Builder resBuilder, MinimumUserProto mupfc,
  		int userId, int clanId, ClanEventPersistentForClan clanEvent) {
    if (0 == clanId || 0 == userId || null == clanEvent) {
      log.error("not in clan. user is " + mupfc + "\t or clanId invalid id=" + clanId +
      		"\t or clanEvent is null clanEvent=" + clanEvent);
      return false;      
    }
    
    resBuilder.setStatus(RecordClanRaidStatsStatus.SUCCESS);
    return true;
  }
  
  private boolean writeChangesToDB(int userId, int clanId, Timestamp now,
  		ClanEventPersistentForClan clanEvent,
  		Map<Integer, ClanEventPersistentForUser> userIdToClanUserInfo) {
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
  	
  	//clan_event_persistent_for_clan_history
  	log.info("rows inserted into clan raid info for clan history (should be 1): " + numInserted);
  	//get all the clan raid info for the users, and then delete them
  	Map<Integer, ClanEventPersistentForUser> clanUserInfo = ClanEventPersistentForUserRetrieveUtils
  			.getPersistentEventUserInfoForClanId(clanId);
  	
  	//delete clan info for clan raid
  	DeleteUtils.get().deleteClanEventPersistentForClan(clanId);
  	
  	if (null != clanUserInfo && !clanUserInfo.isEmpty()) {
  		numInserted = InsertUtils.get().insertIntoCepfuRaidHistory(clanEventPersistentId,
  				now, clanUserInfo);
  		//clan_event_persistent_for_user_history
  		log.info("rows inserted into clan raid info for user history (should be " + 
  				clanUserInfo.size() + "): " + numInserted);
  		
  		//delete clan user info for clan raid
  		List<Integer> userIdList = new ArrayList<Integer>(clanUserInfo.keySet());
  		DeleteUtils.get().deleteClanEventPersistentForUsers(userIdList);
  	
  		userIdToClanUserInfo.putAll(clanUserInfo);
  	}
  	return true;
  }

}
