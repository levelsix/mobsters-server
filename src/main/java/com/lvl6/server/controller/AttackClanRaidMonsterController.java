package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.info.UserClan;
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
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
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
    //for the first ever (initial attack) in the raid, stage and stageMonster start time
    //are already set.
    //When user kills curMonster, the crsmId changes to the next monster and
    //the stageMonster start time changes to when curMonster was killed.
    //When user kills curMonster and go to the next stage, the stage and stageMonster
    //StartTime and crsmId is set to nothing, crsId changes to the next stage, 
    
    if (null != mcp && mcp.hasClanId()) {
    	clanId = mcp.getClanId();
    	getLocker().lockClan(clanId);
    }
    try {
    	//so as to prevent another db read call to get the same information
    	List<ClanEventPersistentForClan> clanEventList =
    			new ArrayList<ClanEventPersistentForClan>();
    	
      boolean legitRequest = checkLegitRequest(resBuilder, sender, userId, clanId,
      		eventDetails, checkIfMonsterDied, monsterIsLastInStage, stageIsLastInRaid,
      		curDate, clanEventList);


      boolean success = false;
      if (legitRequest) {
      	ClanEventPersistentForClan clanEvent = clanEventList.get(0);
      	ClanEventPersistentForClan clanEventClientSent = clanEventList.get(1);
        success = writeChangesToDB(resBuilder, clanId, userId, damageDealt, curTime,
        		clanEvent, clanEventClientSent, checkIfMonsterDied, monsterIsLastInStage,
        		stageIsLastInRaid);
      }
      
      if (success) {
//      	ClanEventPersistentForClan cepfc = clanInfoList.get(0);
//      	PersistentClanEventClanInfoProto eventDetails = CreateInfoProtoUtils
//      			.createPersistentClanEventClanInfoProto(cepfc);
//      	resBuilder.setEventDetails(eventDetails);
      }
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
  		boolean checkIfMonsterDied, boolean monsterIsLastInStage, boolean stageIsLastInRaid,
  		Date curDate, List<ClanEventPersistentForClan> clanEventList) {
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
    
    if (null != raidStartedByClan && raidStartedByClan.equals(eventClientSent)) {// &&
    		//null != raidStartedByClan.getStageStartTime()) { 
    	//stageStartTime won't be null in eventClientSent (this would mean stage has not
    	//started, so user can't attack, so this event should not have been sent)
    	
    	resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS);
    	log.info("since data client sent matches up with db info, allowing attack, clanEvent=" +
    			raidStartedByClan);
    	return true;
    }
    
//    if (null == raidStartedByClan) {
//    	//can take this to mean that the stage timed out or clan raid ended
//    	resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_NO_RAID_IN_PROGRESS);
//    	log.error("no entry exists in ClanEventPersistentForClan. eventDetails=" + eventDetails);
//    	return false;
//    }
    
    //for the first ever, initial attack, in the raid, stage and stageMonster start time
    //are set.
    //When users kill curMonster, the crsmId changes to the next monster and
    //the stageMonster start time changes to when curMonster was killed.
    //When users kill curMonster and go to the next stage, the stage and stageMonster
    //StartTime and crsmId is set to nothing, crsId changes to the next stage,  
    
    clanEventList.add(raidStartedByClan);
    clanEventList.add(eventClientSent);
    
    return true;
  }
  
  private boolean writeChangesToDB(Builder resBuilder, int clanId, int userId,
  		int damageDealt, Timestamp curTime, ClanEventPersistentForClan clanEvent, 
  		ClanEventPersistentForClan clanEventClientSent, boolean checkIfMonsterDied,
  		boolean monsterIsLastInStage, boolean stageIsLastInRaid) {
  	
  	if (null != clanEvent && clanEventClientSent.equals(clanEvent)) {
  		//this user might have just dealt the killing blow
  		boolean isKillingBlow = attackClanRaidMonster(clanId, clanEvent, clanEventClientSent,
  				checkIfMonsterDied, monsterIsLastInStage, stageIsLastInRaid, damageDealt);
  		
  		if (isKillingBlow) {
  			resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS_MONSTER_JUST_DIED);
  		}
  	}
  	
  	//todo update user's monsters' healths1
  	
  	return true;
  }
  
  //Updates tables: clan_event_persistent_for clan/user
  //for the first ever, initial attack, in the raid, stage and stageMonster start time
  //are set.
  //When user kills curMonster, the crsmId changes to the next monster and
  //the stageMonster start time changes to when curMonster was killed.
  //When user kills curMonster and goes to the next stage, the stage and stageMonster
  //StartTime and crsmId is set to nothing, crsId changes to the next stage,
  //When user kills curMonster and no more stages, the clan raid is over,
  //delete everything and send it to history
  private boolean attackClanRaidMonster(int clanId, ClanEventPersistentForClan clanEvent,
  		ClanEventPersistentForClan clanEventClientSent, boolean checkIfMonsterDied,
  		boolean monsterIsLastInStage, boolean stageIsLastInRaid, int dmgDealt) {
  	
  	int monsterId = clanEvent.getCrsmId();
  	ClanRaidStageMonster crsm = ClanRaidStageMonsterRetrieveUtils
  			.getClanRaidStageMonsterForClanRaidStageMonsterId(monsterId);
  	
  	boolean monsterDied = false;
  	if (checkIfMonsterDied) {
  		//get the clan raid information for all the clan users
  		//shouldn't be null (per the retrieveUtils)
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu = ClanEventPersistentForUserRetrieveUtils
  				.getPersistentEventUserInfoForClanId(clanId);
  		int dmgSoFar = sumDamageDoneToMonster(userIdToCepfu);
  		
  		if (dmgSoFar >= crsm.getMonsterHp()) {
  			//should never go in here if nothing went wrong
  			log.error("client knows that crsm is dead, but still sent an attack and " +
  					" server didn't update the ClanEventPersistentForClan when the monster" +
  					" was just killed");
  		}
  	}
  	
  	return monsterDied;
  }
  
  private int sumDamageDoneToMonster(Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
  	int dmgTotal = 0;
  	for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
  		dmgTotal += cepfu.getCrsmDmgDone();
  	}
  	
  	return dmgTotal;	
  }

}
