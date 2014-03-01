package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.PersistentClanEventUserInfoProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterRequestProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto.AttackClanRaidMonsterStatus;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.ClanEventUtil;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

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
	
	@Autowired
	protected ClanEventUtil clanEventUtil;
	
	public ClanEventUtil getClanEventUtil() {
		return clanEventUtil;
	}

	public void setClanEventUtil(ClanEventUtil clanEventUtil) {
		this.clanEventUtil = clanEventUtil;
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
    log.info("reqProto=");
    log.info(reqProto +"");

    MinimumUserProto sender = reqProto.getSender();
    int userId = sender.getUserId();
    MinimumClanProto mcp = sender.getClan();
    int clanId = 0;
    
    PersistentClanEventClanInfoProto eventDetails = reqProto.getEventDetails(); 
    Date curDate = new Date(reqProto.getClientTime());
    Timestamp curTime = new Timestamp(curDate.getTime());
    int damageDealt = reqProto.getDamageDealt(); //remember take min of this with monsters's remaining hp
    
    //extract the new healths for the monster(s)
    List<UserMonsterCurrentHealthProto> monsterHealthProtos = reqProto.getMonsterHealthsList();
    Map<Long, Integer> userMonsterIdToExpectedHealth = new HashMap<Long, Integer>();
    MonsterStuffUtils.getUserMonsterIds(monsterHealthProtos, userMonsterIdToExpectedHealth);
    
    FullUserMonsterProto userMonsterThatAttacked = reqProto.getUserMonsterThatAttacked();
    UserCurrentMonsterTeamProto userMonsterTeam = reqProto.getUserMonsterTeam();
    
    AttackClanRaidMonsterResponseProto.Builder resBuilder = AttackClanRaidMonsterResponseProto.newBuilder();
    resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_OTHER);
    resBuilder.setSender(sender);
    resBuilder.setUserMonsterThatAttacked(userMonsterThatAttacked);
    resBuilder.setDmgDealt(damageDealt);

    //OUTLINE: 
    //get the clan lock; get the clan raid object for the clan;
    //for the first ever (initial attack) in the raid, stage and stageMonster start time
    //are already set.
    //When user kills curMonster, the crsmId changes to the next monster and
    //the stageMonster start time changes to when curMonster was killed.
    //When user kills curMonster and go to the next stage, the stage and stageMonster
    //StartTime and crsmId is set to nothing, crsId changes to the next stage, 
    
    ClanEventPersistentForClan clanEvent = null;
    Map<Integer, ClanEventPersistentForUser> userIdToCepfu = 
    		new HashMap<Integer, ClanEventPersistentForUser>();
    boolean errorless = true;
    //barring error or request failure (but not attacking dead monster), will always be set
    List<ClanEventPersistentForClan> clanEventList =
    		new ArrayList<ClanEventPersistentForClan>();
    
    if (null != mcp && mcp.hasClanId()) {
    	clanId = mcp.getClanId();
    	getLocker().lockClan(clanId);
    }
    try {
    	//so as to prevent another db read call to get the same information
    	
      boolean legitRequest = checkLegitRequest(resBuilder, sender, userId, clanId,
      		eventDetails, curDate, clanEventList);


//      boolean success = false;
      if (legitRequest) {
      	log.info("legitRequest");
      	clanEvent = clanEventList.get(0);
      	ClanEventPersistentForClan clanEventClientSent = clanEventList.get(1);
      	
      	//clanevent MIGHT BE MODIFIED (this will always be sent to the client)
        writeChangesToDB(resBuilder, clanId, userId, damageDealt, curTime, clanEvent,
        		clanEventClientSent, userMonsterTeam, userMonsterIdToExpectedHealth,
        		userIdToCepfu);
      }
      
      if (!clanEventList.isEmpty()) {
      	ClanEventPersistentForClan cepfc = clanEventList.get(0);
      	if (null != cepfc) {
      		PersistentClanEventClanInfoProto updatedEventDetails = CreateInfoProtoUtils
      				.createPersistentClanEventClanInfoProto(cepfc);
      		resBuilder.setEventDetails(updatedEventDetails);
      	}
      }
      
      if (!userIdToCepfu.isEmpty()) {
      	//whenever server has this information send it to the clients
      	List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIdsInClanRaid(userIdToCepfu);
      	
      	Map<Long, MonsterForUser> idsToUserMonsters = RetrieveUtils.monsterForUserRetrieveUtils()
      			.getSpecificUserMonsters(userMonsterIds);
      	
      	for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
      		PersistentClanEventUserInfoProto pceuip = CreateInfoProtoUtils
      				.createPersistentClanEventUserInfoProto(cepfu, idsToUserMonsters, null);
      		resBuilder.addClanUsersDetails(pceuip);
      	}
      }
      
      AttackClanRaidMonsterResponseEvent resEvent = new AttackClanRaidMonsterResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setAttackClanRaidMonsterResponseProto(resBuilder.build()); 
      server.writeEvent(resEvent);
      
      if (AttackClanRaidMonsterStatus.SUCCESS.equals(resBuilder.getStatus()) ||
      		AttackClanRaidMonsterStatus.SUCCESS_MONSTER_JUST_DIED.equals(resBuilder.getStatus())) {
      	server.writeClanEvent(resEvent, clanId);
      }
      
    } catch (Exception e) {
    	log.error("exception in AttackClanRaidMonster processEvent", e);
    	errorless = false;
    	try {
    	  resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_OTHER);
    	  AttackClanRaidMonsterResponseEvent resEvent = new AttackClanRaidMonsterResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setAttackClanRaidMonsterResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in AttackClanRaidMonster processEvent", e);
    	}
    } finally {
    	
    	if (null != mcp && mcp.hasClanId()) {
      	getLocker().unlockClan(clanId);
      }
    	
    }
    
    //not necessary, can just delete this part (purpose is to record in detail, a user's
    //contribution to a clan raid)
    try {
    	ClanEventPersistentForClan clanEventClientSent = clanEventList.get(1);
    	if (errorless && null != clanEvent && !userIdToCepfu.isEmpty()) {
    		int numInserted = InsertUtils.get().insertIntoClanEventPersistentForUserHistoryDetail(
    				curTime, userIdToCepfu, clanEventClientSent);
    		log.info("num raid detail inserted = " + numInserted + "\t should be " +
    				userIdToCepfu.size());
    	}
    } catch (Exception e) {
    	log.warn("could not record more details about clan raid", e);
    }
    
  }

  //want to update user monster healths even if the monster is dead
  private boolean checkLegitRequest(Builder resBuilder, MinimumUserProto mup,
  		int userId, int clanId, PersistentClanEventClanInfoProto eventDetails,  		
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
    
    clanEventList.add(raidStartedByClan);
    clanEventList.add(eventClientSent);
    //still want to deduct user's monsters' healths
//    if (null == raidStartedByClan) {
//    	
//    }
    
    if (null != raidStartedByClan && raidStartedByClan.equals(eventClientSent)) {// &&
    		//null != raidStartedByClan.getStageStartTime()) { 
    	//stageStartTime won't be null in eventClientSent (this would mean stage has not
    	//started, so user can't attack, so this event should not have been sent)
    	
    	resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS);
    	log.info("since data client sent matches up with db info, allowing attack, clanEvent=" +
    			raidStartedByClan);
    	return true;
    }
    
    //still want to deduct user's monsters' healths
//    if (null != raidStartedByClan && null == raidStartedByClan.getStageStartTime()) {
//    	resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_NO_STAGE_RAID_IN_PROGRESS);
//    	log.warn("possibly remnants of old requests to attack clan raid stage monster. " +
//    			" raidInDb=" + raidStartedByClan + "\t eventDetailsClientSent=" + eventDetails);
//    	return false;
//    }
    
    
    return true;
  }
  
  private boolean writeChangesToDB(Builder resBuilder, int clanId, int userId,
  		int damageDealt, Timestamp curTime, ClanEventPersistentForClan clanEvent, 
  		ClanEventPersistentForClan clanEventClientSent,
  		UserCurrentMonsterTeamProto ucmtp,
  		Map<Long, Integer> userMonsterIdToExpectedHealth,
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu) throws Exception {
  	
  	log.info("clanEventInDb=" + clanEvent);
  	log.info("clanEventClientSent=" + clanEventClientSent);
  	
  	if (null != clanEvent && clanEvent.equals(clanEventClientSent) && 
  			null != clanEvent.getStageStartTime() &&
  			null != clanEvent.getStageMonsterStartTime()) {
  		//this user might have just dealt the killing blow
  		//clanEvent might be modified if the user dealt the killing blow
  		updateClanRaid(resBuilder, userId, clanId, damageDealt, curTime, clanEvent,
  				clanEventClientSent, ucmtp, userIdToCepfu);
  		
  		
  	} else if (null != clanEvent && clanEvent.equals(clanEventClientSent) && 
  			null == clanEvent.getStageStartTime()) {
  		//in db the clan event info does not have a start time, meaning no one  began the
  		//raid stage yet (probably because a user killed the last stage monster, recently)
  		log.warn("possibly remnants of old requests to attack clan raid stage monster. " +
    			" raidInDb=" + clanEvent + "\t clanEventClientSent=" + clanEventClientSent);
  		resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_MONSTER_ALREADY_DEAD);
  		
  		
  	} else if (null == clanEvent) {
  		log.warn("possibly remnants of old requests to attack last clan raid stage monster." +
    			" raidInDb=" + clanEvent + "\t clanEventClientSent=" + clanEventClientSent);
  		resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_NO_STAGE_RAID_IN_PROGRESS);
  	}
  	
  	//update user's monsters' healths, don't know if it should be blindly done though...
  	int numUpdated = UpdateUtils.get().updateUserMonstersHealth(userMonsterIdToExpectedHealth);
  	log.info("num monster healths updated:" + numUpdated);
  	
  	return true;
  }
  
  //Updates tables: clan_event_persistent_for clan/user
  //for the first ever, initial attack, in the raid, stage and stageMonster start time
  //are set.
  //When user kills curMonster, the crsmId changes to the next monster and
  //	the stageMonster start time changes to when curMonster was killed.
  //When user kills curMonster and is the last in the stage, the stageMonster,
  //	StartTime, and crsmId are set to nothing, crsId changes to the next stage.
  //When user kills curMonster, is the last in the, and no more stages,
  //	the clan raid is over, delete everything and send it to history.
  
  //userIdToCepfu will hold all clan users' clan raid info, if checkIfMonsterDied = true
  //give this info to caller
  
  //ClanEventPersistentForClan clanEvent WILL BE UPDATED
  private void updateClanRaid(Builder resBuilder, int userId, int clanId, int dmgDealt,
  		Timestamp curTime, ClanEventPersistentForClan clanEvent,
  		ClanEventPersistentForClan clanEventClientSent,
  		UserCurrentMonsterTeamProto ucmtp,
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu) throws Exception {
  	log.info("updating clan raid");
  	
  	int curCrId = clanEvent.getCrId();
  	int curCrsId = clanEvent.getCrsId();
  	int curCrsmId = clanEvent.getCrsmId();
  	
  	List<Integer> newDmgList = new ArrayList<Integer>();
  	int newDmg = dmgDealt;
  	boolean monsterDied = checkMonsterDead(clanId, curCrsId, curCrsmId, dmgDealt,
  					userIdToCepfu, newDmgList);
  	newDmg = newDmgList.get(0);
  	log.info("actual dmg dealt=" + newDmg);

  	if (monsterDied) {
  		//if monsterDied then get all the clan users' damages
  		getAllClanUserDmgInfo(userId, clanId, newDmg, userIdToCepfu);
  	}
  	
  	ClanRaidStage nextStage = ClanRaidStageRetrieveUtils.getNextStageForClanRaidStageId(curCrsId, curCrId);
  	ClanRaidStageMonster curStageNextCrsm = ClanRaidStageMonsterRetrieveUtils
  			.getNextMonsterForClanRaidStageMonsterId(curCrsmId, curCrsId);
  	
  	if (null == nextStage && monsterDied) {
  		log.info("user killed the monster and ended the raid!");
  		//this user just killed the last clan raid monster: this means
  		//put all the clan users' clan raid info into the history table 
  		
  		recordClanRaidVictory(clanId, clanEvent, curTime, userIdToCepfu);
  		//TODO: GIVE OUT THE REWARDS AFTER EVERY STAGE THAT HAS JUST ENDED
  		
  		
  	} else if (null == curStageNextCrsm && monsterDied) {
  		//this user killed last monster in stage, go to the next one
  		log.info("user killed the monster and ended the stage!");
  		recordClanRaidStageVictory(clanId, nextStage, curTime, clanEvent, userIdToCepfu);
  		//TODO: GIVE OUT THE REWARDS AFTER EVERY STAGE THAT HAS JUST ENDED
  		
  		
  	} else if (monsterDied) {
  		log.info("user killed the monster!");
  		//this user killed a monster, continue with the next one
  		recordClanRaidStageMonsterVictory(userId, clanId, curCrsId, curTime, newDmg,
  				curStageNextCrsm, clanEvent, userIdToCepfu);
  		
  		
  	} else if (!monsterDied) {
  		log.info("user did not deal killing blow.");
  		int numUpdated = UpdateUtils.get().updateClanEventPersistentForUserCrsmDmgDone(
  				userId, newDmg, curCrsId, curCrsmId);
    	log.info("rows updated when user attacked monster. num=" + numUpdated);
    	ClanEventPersistentForUser cepfu = ClanEventPersistentForUserRetrieveUtils
    			.getPersistentEventUserInfoForUserIdClanId(userId, clanId);
    	
    	//want to send to everyone in clan this user's clan event information
    	PersistentClanEventUserInfoProto pceuip = CreateInfoProtoUtils
    			.createPersistentClanEventUserInfoProto(cepfu, null, ucmtp.getCurrentTeamList());
    	resBuilder.addClanUsersDetails(pceuip);
    	
    	//update the damage for this raid for the clan in hazelcast
    	boolean replaceCrsmDmg = false;
  		getClanEventUtil().updateClanIdCrsmDmg(clanId, newDmg, replaceCrsmDmg);
  	}
  	
  	
  	if (monsterDied && 0 != newDmg) {
			resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS_MONSTER_JUST_DIED);
		} else if (monsterDied && 0 == newDmg) {
			log.error("not really error since will continue processing. should not be here. " +
					"what has been processed same as this user killing last monster in the raid");
			resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_MONSTER_ALREADY_DEAD);
		} else if (!monsterDied) {
			resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS);
		}
  	resBuilder.setDmgDealt(newDmg);
  }
  
  //update damage this user dealt accordingly
  private boolean checkMonsterDead(int clanId, int crsId, int crsmId, int dmgDealt,
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu, List<Integer> newDmgList) {
  	ClanRaidStageMonster crsm = ClanRaidStageMonsterRetrieveUtils
  			.getClanRaidStageMonsterForClanRaidStageMonsterId(crsmId);
  	
  	//userIdToCepfu might actually be populated
  	int dmgSoFar = getCrsmDmgSoFar(clanId, crsId, crsmId, userIdToCepfu);
  	int crsmHp = crsm.getMonsterHp();
  	
  	log.info("dmgSoFar=" + dmgSoFar);
  	log.info("monster's health=" + crsmHp);
  	log.info("dmgDealt=" + dmgDealt);
  	
  	//default values if monster isn't dead
  	int newDmgDealt = dmgDealt;
  	boolean monsterDied = false;
  	
  	if (dmgSoFar + dmgDealt >= crsmHp) {
  		log.info("monster just died! dmgSoFar=" + dmgSoFar + "\t dmgDealt=" + dmgDealt +
  				"\t monster=" + crsm);

  		monsterDied = true;
  		//need to deal dmg equal to how much hp was left
  		newDmgDealt = Math.min(dmgDealt, crsmHp - dmgSoFar);
  		newDmgDealt = Math.max(0, newDmgDealt); //in case (crsmHp - dmgSoFar) is negative
  	}
  	if (dmgSoFar >= crsmHp) {
  		//should never go in here if everything went right
  		log.error("(won't error out) client sent an attack and server didn't update the" +
  				" ClanEventPersistentForClan when the monster was just killed by a previous." +
  				" AttackClanRaidMonster event");
  		//treating it like normal
  		newDmgDealt = 0;
  	}
  	
  	//give caller return values
  	newDmgList.add(newDmgDealt);
  	
  	log.info("newDmg=" + newDmgDealt);
  	log.info("monsterDied=" + monsterDied);
  	log.info("newUserIdToCepfu=" + userIdToCepfu);
  	return monsterDied;
  }

  //get clan's crsmDmg from Hazelcast first if somehow fails or is 0 then,
  //actually query db for clan users' clan raid info, check if monster is dead,
  //might actually populate userIdToCepfu
  private int getCrsmDmgSoFar(int clanId, int crsId, int crsmId,
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
  	//get the clan raid information for all the clan users
  	//shouldn't be null (per the retrieveUtils)
  	
  	int dmgSoFar = getClanEventUtil().getCrsmDmgForClanId(clanId);
  	
  	if (0 == dmgSoFar) {
  		Map<Integer, ClanEventPersistentForUser> newUserIdToCepfu =
  				ClanEventPersistentForUserRetrieveUtils.getPersistentEventUserInfoForClanId(clanId);

  		//history purposes and so entries in tables don't look weird (crsId and crsmId=0)
  		setCrsCrsmId(crsId, crsmId, newUserIdToCepfu);

  		dmgSoFar = sumDamageDoneToMonster(newUserIdToCepfu);
  		
  		userIdToCepfu.putAll(newUserIdToCepfu);
  	}
  	
  	return dmgSoFar;
  }
  
  private void setCrsCrsmId(int crsId, int crsmId,
  		Map<Integer, ClanEventPersistentForUser> newUserIdToCepfu) {
  	
  	for (ClanEventPersistentForUser cepfu : newUserIdToCepfu.values()) {
  		int cepfuCrsId = cepfu.getCrsId();
  		if (0 == cepfuCrsId) {
  			cepfu.setCrsId(crsId);
  		}
  		
  		int cepfuCrsmId = cepfu.getCrsmId();
  		if (0 == cepfuCrsmId) {
  			cepfu.setCrsmId(crsmId);
  		}
  	}
  }

  private int sumDamageDoneToMonster(Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
  	int dmgTotal = 0;
  	log.info("printing the users who attacked in this raid");
  	for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
  		log.info("cepfu=" + cepfu);
  		dmgTotal += cepfu.getCrsmDmgDone();
  	}
  	
  	return dmgTotal;	
  }
  
  
  private void getAllClanUserDmgInfo(int userId, int clanId, int newDmg,
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
  	
  	//since monster died, get all clan users' clan raid info, in order to send
		//to the client
		if (null == userIdToCepfu || userIdToCepfu.isEmpty()) {
			//more often than not, don't have it so get it
			//only case where would have it is ClanEventUtil.java (Hazelcast) doesn't 
			Map<Integer, ClanEventPersistentForUser> newUserIdToCepfu =
  				ClanEventPersistentForUserRetrieveUtils.getPersistentEventUserInfoForClanId(clanId);
			userIdToCepfu.putAll(newUserIdToCepfu);
		}
		
		//update the user's crsmDmg
		ClanEventPersistentForUser cepfu = userIdToCepfu.get(userId);
		int newCrsmDmgDone = cepfu.getCrsmDmgDone() + newDmg;
		cepfu.setCrsmDmgDone(newCrsmDmgDone);
  }
  
  
  private void recordClanRaidVictory(int clanId, ClanEventPersistentForClan clanEvent,
  		Timestamp now, Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
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
  	boolean won = true;
  	
  	//record whatever is in the ClanEventPersistentForClan
  	int numInserted = InsertUtils.get().insertIntoClanEventPersistentForClanHistory(clanId,
  			now, clanEventPersistentId, crId, crsId, stageStartTime, crsmId,
  			stageMonsterStartTime, won);
  	
  	//clan_event_persistent_for_clan_history
  	log.info("rows inserted into clan raid info for clan history (should be 1): " + numInserted);
  	
  	//delete clan info for clan raid
  	DeleteUtils.get().deleteClanEventPersistentForClan(clanId);
  	
  	if (null != userIdToCepfu && !userIdToCepfu.isEmpty()) { //should always go in here
  		numInserted = InsertUtils.get().insertIntoClanEventPersistentForUserHistory(
  				clanEventPersistentId, now, userIdToCepfu);
  		//clan_event_persistent_for_user_history
  		log.info("rows inserted into clan raid info for user history (should be " + 
  				userIdToCepfu.size() + "): " + numInserted);
  		
  		//delete clan user info for clan raid
  		List<Integer> userIdList = new ArrayList<Integer>(userIdToCepfu.keySet());
  		DeleteUtils.get().deleteClanEventPersistentForUsers(userIdList);
  	
  	}
  }
  
	//last monster in stage. this means:
	//1) for ClanEventPersistentForClan, crsId goes to the next one, stageStartTime
	//goes null, crsmId goes to the first monster in the crs, and stageMonsterStartTime
	//goes null
	//2) for ClanEventPersistentForClan, push crsmDmg to crsDmg, update crs and crsm ids
  private void recordClanRaidStageVictory(int clanId, ClanRaidStage nextStage,
  		Timestamp curTime, ClanEventPersistentForClan cepfc,
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu) throws Exception {
  	
  	int nextCrsId = nextStage.getId();
		ClanRaidStageMonster nextCrsFirstCrsm = ClanRaidStageMonsterRetrieveUtils
				.getFirstMonsterForClanRaidStage(nextCrsId);
		
		if (null == nextCrsFirstCrsm) {
			//WTF???
			throw new Exception("WTF!!!! clan raid stage has no monsters! >:( crs=" + nextStage);
		}
  	
  	int nextCrsCrsmId = nextCrsFirstCrsm.getId();
		int numUpdated = UpdateUtils.get().updateClanEventPersistentForClanGoToNextStage(
				clanId, nextCrsId, nextCrsCrsmId);
		log.info("clan just cleared stage! nextStage=" + nextStage + "\t curEventInfo" +
				cepfc + "\t numUpdated=" + numUpdated);
		//need to  update clan raid info for all the clan users
		
		numUpdated = UpdateUtils.get().updateClanEventPersistentForUserGoToNextStage(
				nextCrsId, nextCrsCrsmId, userIdToCepfu);
		
		log.info("rows updated when clan cleared stage. num=" + numUpdated);
		
		//need to update ClanEventPersistentForClan cepfc since need to update client
		cepfc.setCrsId(nextCrsId);
		cepfc.setStageStartTime(null);
  	cepfc.setCrsmId(nextCrsCrsmId);
  	cepfc.setStageMonsterStartTime(null);
  }
  
  //user killed monster. this means:
	//1) for clanEventPersistentForClan, crsmId goes to the next one, and stage
	//monser start time changes to now
	//2) for clanEventpersistentForUser, for all clan members update push their crsmDmg
  // to crsDmg and update the crsmId
  private void recordClanRaidStageMonsterVictory(int userId, int clanId, int crsId,
  		Timestamp curTime, int newDmg, ClanRaidStageMonster nextCrsm,
  		ClanEventPersistentForClan cepfc,
  		Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
  		
  	int nextCrsmId = nextCrsm.getId();
  	
  	int numUpdated = UpdateUtils.get().updateClanEventPersistentForClanGoToNextMonster(
  			clanId, nextCrsmId, curTime);
  	log.info("rows updated when clan killed monster. num=" + numUpdated);
  	
  	numUpdated = UpdateUtils.get().updateClanEventPersistentForUsersGoToNextMonster(
				crsId, nextCrsmId, userIdToCepfu);
  	
  	log.info("rows updated when user killed monster. num=" + numUpdated);
  	
  	//need to update ClanEventPersistentForClan cepfc since need to update client
  	cepfc.setCrsmId(nextCrsmId);
  	cepfc.setStageMonsterStartTime(curTime);
  }
}
