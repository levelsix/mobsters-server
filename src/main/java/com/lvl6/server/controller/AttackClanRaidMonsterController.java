package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AttackClanRaidMonsterRequestEvent;
import com.lvl6.events.response.AttackClanRaidMonsterResponseEvent;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterRequestProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto.AttackClanRaidMonsterStatus;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.RetrieveUtils;

@Component @DependsOn("gameServer") public class AttackClanRaidMonsterController extends EventController {

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

	
	public AttackClanRaidMonsterController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new AttackClanRaidMonsterRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_ATTACK_CLAN_RAID_MONSTER_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    AttackClanRaidMonsterRequestProto reqProto = ((AttackClanRaidMonsterRequestEvent)event).getAttackClanRaidMonsterRequestProto();

    MinimumUserProto sender = reqProto.getSender();
    int userId = sender.getUserId();
    MinimumClanProto mcp = sender.getClan();
    int clanId = 0;
    
    PersistentClanEventClanInfoProto eventDetails = reqProto.getEventDetails(); 
    Date curDate = new Date(reqProto.getClientTime());
    Timestamp curTime = new Timestamp(curDate.getTime());
    int damageDealt = reqProto.getDamageDealt(); //remember take min of this with monsters's remaining hp
    List<UserMonsterCurrentHealthProto> monsterHealthProtos = reqProto.getMonsterHealthsList();
    
    //client indicates when to begin checking if monster is dead
    boolean checkIfMonsterDied = reqProto.getCheckIfMonsterDied();
    //if true and above true (monster is dead), then for clan users, carry over current
    //crsmDmg over to crsDmg
    boolean monsterIsLastInStage = reqProto.getMonsterIsLastInStage();
    //if true and above both true, then for clan users, carry over current crsmDmg over to
    //crsDmg and resulting crsDmg over to crDmg
    boolean stageIsLastInRaid = reqProto.getStageIsLastInRaid();
    
    AttackClanRaidMonsterResponseProto.Builder resBuilder = AttackClanRaidMonsterResponseProto.newBuilder();
    resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_OTHER);
    resBuilder.setSender(sender);

    //OUTLINE: 
    //get the clan lock; get the clan raid object for the clan;
    // If doesn't exist, create it. If does exist, check to see that what the object is
    // and what the user has are the same. 
    // If different then fail and do nothing. If same then update the cr*Dmg properties
    if (null != mcp && mcp.hasClanId()) {
    	clanId = mcp.getClanId();
    	getLocker().lockClan(clanId);
    }
    try {
    	
      boolean legitRequest = checkLegitRequest(resBuilder, sender, userId, clanId,
      		eventDetails, checkIfMonsterDied, monsterIsLastInStage, stageIsLastInRaid,
      		curDate);


//      List<ClanEventPersistentForClan> clanInfoList = new ArrayList<ClanEventPersistentForClan>();
//      boolean success = false;
//      if (legitRequest) { 
//      	int clanEventPersistentId = clanEventPersistentIdList.get(0);
//        success = writeChangesToDB(clanId, clanEventPersistentId, clanRaidId,
//        		//curTime,
//        		clanInfoList);
//      }
//      
//      if (success) {
//      	ClanEventPersistentForClan cepfc = clanInfoList.get(0);
//      	PersistentClanEventClanInfoProto eventDetails = CreateInfoProtoUtils
//      			.createPersistentClanEventClanInfoProto(cepfc);
////      	resBuilder.setEventDetails(eventDetails);
//      }
      AttackClanRaidMonsterResponseEvent resEvent = new AttackClanRaidMonsterResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setAttackClanRaidMonsterResponseProto(resBuilder.build()); 
      server.writeEvent(resEvent);
      
      if (legitRequest) {
      	//only write to the user if the request was valid
      	server.writeClanEvent(resEvent, clanId);
      }
      
    } catch (Exception e) {
    	try {
    	  resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_OTHER);
    	  AttackClanRaidMonsterResponseEvent resEvent = new AttackClanRaidMonsterResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setAttackClanRaidMonsterResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception in AttackClanRaidMonster processEvent", e);
    	}
    } finally {
    	
    	if (null != mcp && mcp.hasClanId()) {
      	getLocker().unlockClan(clanId);
      }
    	
    }
  }

  //want to update user monster healths even if the monster is dead
  private boolean checkLegitRequest(Builder resBuilder, MinimumUserProto mup,
  		int userId, int clanId, PersistentClanEventClanInfoProto eventDetails,
  		boolean checkIfMonsterDied, boolean monsterIsLastInStage,
  		boolean stageIsLastInRaid, Date curDate) {
  	//check if user is in clan
  	UserClan uc = RetrieveUtils.userClanRetrieveUtils().getSpecificUserClan(userId, clanId);
    if (null == uc) {
    	resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_USER_NOT_IN_CLAN);
      log.error("not in clan. user=" + mup);
      return false;      
    }
    
    if (null == eventDetails) {
    	log.error("no PersistentClanEventClanInfoProto set by client.");
    	return false;
    }
    
    //now check if clan already started the event
    ClanEventPersistentForClan raidStartedByClan = ClanEventPersistentForClanRetrieveUtils
    		.getPersistentEventForClanId(clanId);
    
    ClanEventPersistentForClan eventClientSent = ClanStuffUtils
    		.createClanEventPersistentForClan(eventDetails);
    
    if (null != raidStartedByClan && raidStartedByClan.equals(eventClientSent)) {
    	resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS);
    	log.info("since data client sent matches up with db info, allowing attack");
    	return true;
    }
    
    if (null == raidStartedByClan) {
    	//can take this to mean that the stage timed out or clan raid ended
    	resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_NO_RAID_IN_PROGRESS);
    	log.error("no entry exists in ClanEventPersistentForClan. eventDetails=" + eventDetails);
    	return false;
    }
    
    //for the first ever, initial attack, in the raid, stage and stageMonster start time
    //are set.
    //When users kill curMonster, the crsmId changes to the next monster and
    //the stageMonster start time changes to when curMonster was killed.
    //When users kill curMonster and go to the next stage, the stage and stageMonster
    //StartTime and crsmId is set to nothing, crsId changes to the next stage,  
    
    return true;
  }
  
//  //get all the members in a clan
//  private Set<Integer> getAuthorizedUsers(int clanId) {
//  	Set<Integer> authorizedUsers = new HashSet<Integer>();
//  	List<Integer> statuses = new ArrayList<Integer>();
//    statuses.add(UserClanStatus.LEADER_VALUE);
//    statuses.add(UserClanStatus.JUNIOR_LEADER_VALUE);
//    statuses.add(UserClanStatus.CAPTAIN_VALUE);
//    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
//    		.getUserIdsWithStatuses(clanId, statuses);
//    
//    if (null != userIds && !userIds.isEmpty()) {
//    	authorizedUsers.addAll(userIds);
//    }
//    
//    return authorizedUsers;
//  }

  private boolean writeChangesToDB(int clanId, int clanEventPersistentId, int clanRaidId,
  		//Timestamp curTime,
  		List<ClanEventPersistentForClan> clanInfo) {
  	ClanRaidStage crs = ClanRaidStageRetrieveUtils.getFirstStageForClanRaid(clanRaidId);
  	int clanRaidStageId = crs.getId();
  	
  	Map<Integer, ClanRaidStageMonster> stageIdToMonster = ClanRaidStageMonsterRetrieveUtils
  			.getClanRaidStageMonstersForClanRaidStageId(clanRaidStageId);
  	ClanRaidStageMonster crsm = stageIdToMonster.get(clanRaidStageId);
  	int crsmId = crsm.getId();
  	
  	//NOTE: once user begins a raid he auto begins a stage and auto begins the first monster
//  	int numInserted = InsertUtils.get().insertIntoClanEventPersistentForClan(clanId,
//  			clanEventPersistentId, clanRaidId, clanRaidStageId, curTime, crsmId, curTime);
//  	
//  	log.info("num rows inserted into clan raid info table: " + numInserted);
//  	
//  	ClanEventPersistentForClan cepfc = new ClanEventPersistentForClan(clanId,
//  			clanEventPersistentId, clanRaidId, clanRaidStageId, curTime, crsmId, curTime);
//  	clanInfo.add(cepfc);
  	
  	return true;
  }

}
