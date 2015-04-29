package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AttackClanRaidMonsterRequestEvent;
import com.lvl6.events.response.AttackClanRaidMonsterResponseEvent;
import com.lvl6.events.response.AwardClanRaidStageRewardResponseEvent;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.info.ClanRaidStageReward;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.UserClan;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.PersistentClanEventUserInfoProto;
import com.lvl6.proto.ClanProto.PersistentClanEventUserRewardProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterRequestProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto.AttackClanRaidMonsterStatus;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto.Builder;
import com.lvl6.proto.EventClanProto.AwardClanRaidStageRewardResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils2;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.ClanEventUtil;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class AttackClanRaidMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected ClanStuffUtils clanStuffUtils;

	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected ClanEventUtil clanEventUtil;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtil;

	@Autowired
	protected ClanEventPersistentForClanRetrieveUtils2 clanEventPersistentForClanRetrieveUtil;

	@Autowired
	protected ClanEventPersistentForUserRetrieveUtils2 clanEventPersistentForUserRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;
	
	@Autowired
	protected ClanRaidStageRetrieveUtils clanRaidStageRetrieveUtils;
	
	@Autowired
	protected ClanRaidStageMonsterRetrieveUtils clanRaidStageMonsterRetrieveUtils;
	
	@Autowired 
	protected ClanRaidStageRewardRetrieveUtils clanRaidStageRewardRetrieveUtils;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	


	public AttackClanRaidMonsterController() {
		
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
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		AttackClanRaidMonsterRequestProto reqProto = ((AttackClanRaidMonsterRequestEvent) event)
				.getAttackClanRaidMonsterRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto sender = reqProto.getSender();
		String userId = sender.getUserUuid();
		MinimumClanProto mcp = sender.getClan();

		PersistentClanEventClanInfoProto eventDetails = reqProto
				.getEventDetails();
		Date curDate = new Date(reqProto.getClientTime());
		Timestamp curTime = new Timestamp(curDate.getTime());
		int damageDealt = reqProto.getDamageDealt(); //remember take min of this with monsters's remaining hp

		//extract the new healths for the monster(s)
		List<UserMonsterCurrentHealthProto> monsterHealthProtos = reqProto
				.getMonsterHealthsList();
		Map<String, Integer> userMonsterIdToExpectedHealth = new HashMap<String, Integer>();
		monsterStuffUtils.getUserMonsterIds(monsterHealthProtos,
				userMonsterIdToExpectedHealth);

		FullUserMonsterProto userMonsterThatAttacked = reqProto
				.getUserMonsterThatAttacked();
		UserCurrentMonsterTeamProto userMonsterTeam = reqProto
				.getUserMonsterTeam();

		AttackClanRaidMonsterResponseProto.Builder resBuilder = AttackClanRaidMonsterResponseProto
				.newBuilder();
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
		Map<String, ClanEventPersistentForUser> userIdToCepfu = new HashMap<String, ClanEventPersistentForUser>();
		//boolean errorless = true;
		//barring error or request failure (but not attacking dead monster), will always be set
		List<ClanEventPersistentForClan> clanEventList = new ArrayList<ClanEventPersistentForClan>();

		UUID userUuid = null;
		UUID clanUuid = null;

		String clanId = "";
		boolean invalidUuids = true;
		if (sender.hasClan() && null != mcp) {
			clanId = mcp.getClanUuid();
			try {
				userUuid = UUID.fromString(userId);

				clanUuid = UUID.fromString(clanId);
				invalidUuids = false;
			} catch (Exception e) {
				log.error(String.format(
						"UUID error. incorrect userId=%s, clanId=%s", userId,
						clanId), e);
			}
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_OTHER);
			AttackClanRaidMonsterResponseEvent resEvent = new AttackClanRaidMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setAttackClanRaidMonsterResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean lockedClan = false;
		lockedClan = getLocker().lockClan(clanUuid);
		try {
			//so as to prevent another db read call to get the same information

			boolean legitRequest = checkLegitRequest(resBuilder, lockedClan,
					sender, userId, clanId, eventDetails, curDate,
					clanEventList);

			//      boolean success = false;
			List<ClanEventPersistentUserReward> allRewards = new ArrayList<ClanEventPersistentUserReward>();
			if (legitRequest) {
				log.info("legitRequest");

				if (!clanEventList.isEmpty()) {
					clanEvent = clanEventList.get(0);
				}
				ClanEventPersistentForClan clanEventClientSent = clanStuffUtils
						.createClanEventPersistentForClan(eventDetails);

				//clanevent MIGHT BE MODIFIED (this will always be sent to the client)
				writeChangesToDB(resBuilder, clanId, userId, damageDealt,
						curTime, clanEvent, clanEventClientSent,
						userMonsterTeam, userMonsterIdToExpectedHealth,
						userIdToCepfu, allRewards);
			}

			setClanEventClanDetails(resBuilder, clanEventList);
			setClanEventUserDetails(resBuilder, userIdToCepfu);

			AttackClanRaidMonsterResponseEvent resEvent = new AttackClanRaidMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setAttackClanRaidMonsterResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			//tell whole clan on a successful attack
			if (AttackClanRaidMonsterStatus.SUCCESS.equals(resBuilder
					.getStatus())
					|| AttackClanRaidMonsterStatus.SUCCESS_MONSTER_JUST_DIED
							.equals(resBuilder.getStatus())) {
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));

				if (!allRewards.isEmpty()) {
					setClanEventRewards(allRewards, eventDetails, responses);
				}
			}

		} catch (Exception e) {
			log.error("exception in AttackClanRaidMonster processEvent", e);
			//errorless = false;
			try {
				resBuilder.setStatus(AttackClanRaidMonsterStatus.FAIL_OTHER);
				AttackClanRaidMonsterResponseEvent resEvent = new AttackClanRaidMonsterResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setAttackClanRaidMonsterResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in AttackClanRaidMonster processEvent", e);
			}
		} finally {

			if (null != clanUuid && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}

		}

		//    //not necessary, can just delete this part (purpose is to record in detail, a user's
		//    //contribution to a clan raid) in particular the damage a user has done to a monster
		//    //once the monster is dead
		//    try {
		//    	ClanEventPersistentForClan clanEventClientSent = clanEventList.get(1);
		//    	if (errorless && null != clanEventClientSent && !userIdToCepfu.isEmpty() &&
		//    			AttackClanRaidMonsterStatus.SUCCESS_MONSTER_JUST_DIED.equals(resBuilder.getStatus())) {
		//    		int numInserted = InsertUtils.get().insertIntoClanEventPersistentForUserHistoryDetail(
		//    				curTime, userIdToCepfu, clanEventClientSent);
		//    		log.info("num raid detail inserted = " + numInserted + "\t should be " +
		//    				userIdToCepfu.size());
		//    	}
		//    } catch (Exception e) {
		//    	log.warn("could not record more details about clan raid", e);
		//    }

	}

	//want to update user monster healths even if the monster is dead
	private boolean checkLegitRequest(Builder resBuilder, boolean lockedClan,
			MinimumUserProto mup, String userId, String clanId,
			PersistentClanEventClanInfoProto eventDetails, Date curDate,
			List<ClanEventPersistentForClan> clanEventList) {

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}

		//check if user is in clan
		UserClan uc = userClanRetrieveUtil.getSpecificUserClan(userId, clanId);
		if (null == uc) {
			resBuilder
					.setStatus(AttackClanRaidMonsterStatus.FAIL_USER_NOT_IN_CLAN);
			log.error(String.format("not in clan. user=%s", mup));
			return false;
		}

		if (null == eventDetails) {
			log.error("no PersistentClanEventClanInfoProto set by client.");
			return false;
		}

		//now check if clan already started the event
		ClanEventPersistentForClan raidStartedByClan = clanEventPersistentForClanRetrieveUtil
				.getPersistentEventForClanId(clanId);

		ClanEventPersistentForClan eventClientSent = clanStuffUtils
				.createClanEventPersistentForClan(eventDetails);

		//still want to deduct user's monsters' healths
		//    if (null == raidStartedByClan) {
		//    	
		//    }

		if (null != raidStartedByClan
				&& raidStartedByClan.equals(eventClientSent)) {// &&
			//null != raidStartedByClan.getStageStartTime()) { 
			//stageStartTime won't be null in eventClientSent (this would mean stage has not
			//started, so user can't attack, so this event should not have been sent)

			clanEventList.add(raidStartedByClan);

			resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS);
			log.info(String
					.format("since data client sent matches up with db info, allowing attack, clanEvent=%s",
							raidStartedByClan));
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

	private boolean writeChangesToDB(Builder resBuilder, String clanId,
			String userId, int damageDealt, Timestamp curTime,
			ClanEventPersistentForClan clanEvent,
			ClanEventPersistentForClan clanEventClientSent,
			UserCurrentMonsterTeamProto ucmtp,
			Map<String, Integer> userMonsterIdToExpectedHealth,
			Map<String, ClanEventPersistentForUser> userIdToCepfu,
			List<ClanEventPersistentUserReward> allRewards)  {

		log.info(String.format("clanEventInDb=%s", clanEvent));
		log.info(String.format("clanEventClientSent=%s", clanEventClientSent));

		if (null != clanEvent && clanEvent.equals(clanEventClientSent)
				&& null != clanEvent.getStageStartTime()
				&& null != clanEvent.getStageMonsterStartTime()) {
			//this user might have just dealt the killing blow
			//clanEvent might be modified if the user dealt the killing blow
			updateClanRaid(resBuilder, userId, clanId, damageDealt, curTime,
					clanEvent, clanEventClientSent, ucmtp, userIdToCepfu,
					allRewards);

		} else if (null != clanEvent && clanEvent.equals(clanEventClientSent)
				&& null == clanEvent.getStageStartTime()) {
			//in db the clan event info does not have a start time, meaning no one  began the
			//raid stage yet (probably because a user killed the last stage monster, recently)
			log.warn("possibly remnants of old requests to attack clan raid stage monster. "
					+ " raidInDb="
					+ clanEvent
					+ "\t clanEventClientSent="
					+ clanEventClientSent);
			resBuilder
					.setStatus(AttackClanRaidMonsterStatus.FAIL_MONSTER_ALREADY_DEAD);

		} else if (null == clanEvent) {
			log.warn("possibly remnants of old requests to attack last clan raid stage monster."
					+ " raidInDb="
					+ clanEvent
					+ "\t clanEventClientSent="
					+ clanEventClientSent);
			resBuilder
					.setStatus(AttackClanRaidMonsterStatus.FAIL_NO_STAGE_RAID_IN_PROGRESS);
		}

		//update user's monsters' healths, don't know if it should be blindly done though...
		int numUpdated = UpdateUtils.get().updateUserMonstersHealth(
				userMonsterIdToExpectedHealth);
		log.info(String.format("num monster healths updated:%s", numUpdated));

		return true;
	}

	//Updates tables: clan_event_persistent_for clan/user
	//for the first ever, initial attack, in the raid, stage and stageMonster start time
	//are set.
	//When user kills curMonster, the crsmId changes to the next monster and
	//	the stageMonster start time changes to when curMonster was killed.
	//When user kills curMonster and is the last in the stage, the stage monster startTime
	//	and stage startTime are set to nothing, crsId changes to the next stage and
	//	crsmId changes to the first monster in the stage
	//When user kills curMonster, is the last in the, and no more stages,
	//	the clan raid is over, delete everything and send it to history.

	//userIdToCepfu will hold all clan users' clan raid info, if checkIfMonsterDied = true
	//give this info to caller

	//ClanEventPersistentForClan clanEvent MIGHT BE UPDATED
	//Map<Integer, ClanEventPersistentForUser> userIdToCepfu will be populated if the
	//monster died 
	private void updateClanRaid(Builder resBuilder, String userId,
			String clanId, int dmgDealt, Timestamp curTime,
			ClanEventPersistentForClan clanEvent,
			ClanEventPersistentForClan clanEventClientSent,
			UserCurrentMonsterTeamProto ucmtp,
			Map<String, ClanEventPersistentForUser> userIdToCepfu,
			List<ClanEventPersistentUserReward> allRewards)  {
		log.info("updating clan raid");

		int curCrId = clanEvent.getCrId();
		int curCrsId = clanEvent.getCrsId();
		int curCrsmId = clanEvent.getCrsmId();

		List<Integer> newDmgList = new ArrayList<Integer>();
		int newDmg = dmgDealt;
		boolean monsterDied = checkMonsterDead(clanId, curCrsId, curCrsmId,
				dmgDealt, userIdToCepfu, newDmgList);
		newDmg = newDmgList.get(0);
		log.info(String.format("actual dmg dealt=%s", newDmg));

		if (monsterDied) {
			//if monsterDied then get all the clan users' damages, also update cur user's crsmDmg
			getAllClanUserDmgInfo(userId, clanId, newDmg, userIdToCepfu);
		}

		ClanRaidStage nextStage = clanRaidStageRetrieveUtils
				.getNextStageForClanRaidStageId(curCrsId, curCrId);
		ClanRaidStageMonster curStageNextCrsm = clanRaidStageMonsterRetrieveUtils
				.getNextMonsterForClanRaidStageMonsterId(curCrsmId, curCrsId);

		if (null == nextStage && monsterDied) {
			log.info("user killed the monster and ended the raid!");
			//this user just killed the last clan raid monster: this means
			//put all the clan users' clan raid info into the history table 
			recordClanRaidVictory(clanId, clanEvent, curTime, userIdToCepfu);

			//TODO: GIVE OUT THE REWARDS AFTER EVERY STAGE THAT HAS JUST ENDED
			List<ClanEventPersistentUserReward> rewards = awardRewards(
					curCrsId, clanEventClientSent.getStageStartTime(), curTime,
					clanEventClientSent.getClanEventPersistentId(),
					userIdToCepfu);
			allRewards.addAll(rewards);

		} else if (null == curStageNextCrsm && monsterDied) {
			//this user killed last monster in stage, go to the next one
			//clanEvent will be modified
			log.info("user killed the monster and ended the stage!");
			recordClanRaidStageVictory(clanId, curCrsId, nextStage, curTime,
					clanEvent, userIdToCepfu);

			//TODO: GIVE OUT THE REWARDS AFTER EVERY STAGE THAT HAS JUST ENDED
			List<ClanEventPersistentUserReward> rewards = awardRewards(
					curCrsId, clanEventClientSent.getStageStartTime(), curTime,
					clanEventClientSent.getClanEventPersistentId(),
					userIdToCepfu);
			allRewards.addAll(rewards);

		} else if (monsterDied) {
			log.info("user killed the monster!");
			//this user killed a monster, continue with the next one
			//clanEvent will be modified
			recordClanRaidStageMonsterVictory(userId, clanId, curCrsId,
					curTime, newDmg, curStageNextCrsm, clanEvent, userIdToCepfu);

		} else if (!monsterDied) {
			log.info("user did not deal killing blow.");
			int numUpdated = UpdateUtils.get()
					.updateClanEventPersistentForUserCrsmDmgDone(userId,
							newDmg, curCrsId, curCrsmId);
			log.info(String.format(
					"rows updated when user attacked monster. num=%s",
					numUpdated));
			ClanEventPersistentForUser cepfu = clanEventPersistentForUserRetrieveUtil
					.getPersistentEventUserInfoForUserIdClanId(userId, clanId);

			//want to send to everyone in clan this user's clan event information
			PersistentClanEventUserInfoProto pceuip = createInfoProtoUtils
					.createPersistentClanEventUserInfoProto(cepfu, null,
							ucmtp.getCurrentTeamList());
			resBuilder.addClanUsersDetails(pceuip);

			//update the damage for this raid monster for the clan in hazelcast, add the dmg
			//since it keeps track of how much dmg the clan has done so far
			boolean replaceCrsmDmg = false;
			getClanEventUtil().updateClanIdCrsmDmg(clanId, newDmg,
					replaceCrsmDmg);
		}

		if (monsterDied && 0 != newDmg) {
			resBuilder
					.setStatus(AttackClanRaidMonsterStatus.SUCCESS_MONSTER_JUST_DIED);
		} else if (monsterDied && 0 == newDmg) {
			log.error("not really error since will continue processing. should not be here. "
					+ "what has been processed same as this user killing last monster in the raid");
			resBuilder
					.setStatus(AttackClanRaidMonsterStatus.FAIL_MONSTER_ALREADY_DEAD);
		} else if (!monsterDied) {
			resBuilder.setStatus(AttackClanRaidMonsterStatus.SUCCESS);
		}
		resBuilder.setDmgDealt(newDmg);
	}

	//update damage this user dealt accordingly
	private boolean checkMonsterDead(String clanId, int crsId, int crsmId,
			int dmgDealt,
			Map<String, ClanEventPersistentForUser> userIdToCepfu,
			List<Integer> newDmgList) {
		ClanRaidStageMonster crsm = clanRaidStageMonsterRetrieveUtils
				.getClanRaidStageMonsterForClanRaidStageMonsterId(crsmId);

		//userIdToCepfu might actually be populated
		int dmgSoFar = getCrsmDmgSoFar(clanId, crsId, crsmId, userIdToCepfu);
		int crsmHp = crsm.getMonsterHp();

		log.info(String.format("dmgSoFar=%s", dmgSoFar));
		log.info(String.format("monster's health=%s", crsmHp));
		log.info(String.format("dmgDealt=%s", dmgDealt));

		//default values if monster isn't dead
		int newDmgDealt = dmgDealt;
		boolean monsterDied = false;

		if (dmgSoFar + dmgDealt >= crsmHp) {
			log.info(String.format(
					"monster just died! dmgSoFar=%s, dmgDealt=%s, monster=%s",
					dmgSoFar, dmgDealt, crsm));

			monsterDied = true;
			//need to deal dmg equal to how much hp was left
			newDmgDealt = Math.min(dmgDealt, crsmHp - dmgSoFar);
			newDmgDealt = Math.max(0, newDmgDealt); //in case (crsmHp - dmgSoFar) is negative
		}
		if (dmgSoFar >= crsmHp) {
			//should never go in here if everything went right
			log.error("(won't error out) client sent an attack and server didn't update the"
					+ " ClanEventPersistentForClan when the monster was just killed by a previous."
					+ " AttackClanRaidMonster event");
			//treating it like normal
			newDmgDealt = 0;
		}

		//give caller return values
		newDmgList.add(newDmgDealt);

		log.info(String.format("newDmg=%s", newDmgDealt));
		log.info(String.format("monsterDied=%s", monsterDied));
		log.info(String.format("newUserIdToCepfu=%s", userIdToCepfu));
		return monsterDied;
	}

	//get clan's crsmDmg from Hazelcast first if somehow fails or is 0 then,
	//actually query db for clan users' clan raid info, check if monster is dead,
	//might actually populate userIdToCepfu
	private int getCrsmDmgSoFar(String clanId, int crsId, int crsmId,
			Map<String, ClanEventPersistentForUser> userIdToCepfu) {
		//get the clan raid information for all the clan users
		//shouldn't be null (per the retrieveUtils)

		int dmgSoFar = getClanEventUtil().getCrsmDmgForClanId(clanId);

		if (0 == dmgSoFar) {
			Map<String, ClanEventPersistentForUser> newUserIdToCepfu = clanEventPersistentForUserRetrieveUtil
					.getPersistentEventUserInfoForClanId(clanId);

			//history purposes and so entries in tables don't look weird (crsId and crsmId=0)
			setCrsCrsmId(crsId, crsmId, newUserIdToCepfu);

			dmgSoFar = sumDamageDoneToMonster(newUserIdToCepfu);

			userIdToCepfu.putAll(newUserIdToCepfu);
		}

		return dmgSoFar;
	}

	private void setCrsCrsmId(int crsId, int crsmId,
			Map<String, ClanEventPersistentForUser> newUserIdToCepfu) {

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

	private int sumDamageDoneToMonster(
			Map<String, ClanEventPersistentForUser> userIdToCepfu) {
		int dmgTotal = 0;
		log.info("printing the users who attacked in this raid");
		for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
			log.info(String.format("cepfu=%s", cepfu));
			dmgTotal += cepfu.getCrsmDmgDone();
		}

		return dmgTotal;
	}

	private void getAllClanUserDmgInfo(String userId, String clanId,
			int newDmg, Map<String, ClanEventPersistentForUser> userIdToCepfu) {

		//since monster died, get all clan users' clan raid info, in order to send
		//to the client
		if (null == userIdToCepfu || userIdToCepfu.isEmpty()) {
			//more often than not, don't have it so get it
			//only case where would have it is ClanEventUtil.java (Hazelcast) doesn't 
			Map<String, ClanEventPersistentForUser> newUserIdToCepfu = clanEventPersistentForUserRetrieveUtil
					.getPersistentEventUserInfoForClanId(clanId);
			userIdToCepfu.putAll(newUserIdToCepfu);
		}

		//update the user's crsmDmg
		ClanEventPersistentForUser cepfu = userIdToCepfu.get(userId);
		int newCrsmDmgDone = cepfu.getCrsmDmgDone() + newDmg;
		cepfu.setCrsmDmgDone(newCrsmDmgDone);
	}

	private void recordClanRaidVictory(String clanId,
			ClanEventPersistentForClan clanEvent, Timestamp now,
			Map<String, ClanEventPersistentForUser> userIdToCepfu) {
		int clanEventId = clanEvent.getClanEventPersistentId();
		int crId = clanEvent.getCrId();
		int crsId = clanEvent.getCrsId();
		Timestamp stageStartTime = null;
		if (null != clanEvent.getStageStartTime()) {
			stageStartTime = new Timestamp(clanEvent.getStageStartTime()
					.getTime());
		}
		int crsmId = clanEvent.getCrsmId();
		Timestamp stageMonsterStartTime = null;
		if (null != clanEvent.getStageMonsterStartTime()) {
			stageMonsterStartTime = new Timestamp(clanEvent
					.getStageMonsterStartTime().getTime());
		}
		boolean won = true;

		//record whatever is in the ClanEventPersistentForClan
		int numInserted = InsertUtils.get()
				.insertIntoClanEventPersistentForClanHistory(clanId, now,
						clanEventId, crId, crsId, stageStartTime, crsmId,
						stageMonsterStartTime, won);

		//clan_event_persistent_for_clan_history
		log.info("rows inserted into clan raid info for clan history (should be 1): "
				+ numInserted);

		//delete clan info for clan raid
		DeleteUtils.get().deleteClanEventPersistentForClan(clanId);

		if (null != userIdToCepfu && !userIdToCepfu.isEmpty()) { //should always go in here
			numInserted = InsertUtils.get().insertIntoCepfuRaidHistory(
					clanEventId, now, userIdToCepfu);
			//clan_event_persistent_for_user_history
			log.info(String
					.format("rows inserted into clan raid info for user history (should be %s): %s",
							userIdToCepfu.size(), numInserted));

			//delete clan user info for clan raid
			List<String> userIdList = new ArrayList<String>(
					userIdToCepfu.keySet());
			DeleteUtils.get().deleteClanEventPersistentForUsers(userIdList);

			//record to the clan raid stage user history
			int stageHp = clanRaidStageRetrieveUtils
					.getClanRaidStageHealthForCrsId(crsId);
			numInserted = InsertUtils.get().insertIntoCepfuRaidStageHistory(
					clanEventId, stageStartTime, now, stageHp, userIdToCepfu);
			log.info(String
					.format("clan event persistent for user raid stage history, numInserted=%s",
							numInserted));
		}

		//delete the crsmDmg for this clan
		getClanEventUtil().deleteCrsmDmgForClanId(clanId);
	}

	//last monster in stage. this means:
	//1) for ClanEventPersistentForClan, crsId goes to the next one, stageStartTime
	//goes null, crsmId goes to the first monster in the crs, and stageMonsterStartTime
	//goes null
	//2) for ClanEventPersistentForClan, push crsmDmg to crsDmg, update crs and crsm ids
	private void recordClanRaidStageVictory(String clanId, int curCrsId,
			ClanRaidStage nextStage, Timestamp curTime,
			ClanEventPersistentForClan cepfc,
			Map<String, ClanEventPersistentForUser> userIdToCepfu)
			 {
		int eventId = cepfc.getClanEventPersistentId();
		Timestamp stageStartTime = new Timestamp(cepfc.getStageStartTime()
				.getTime());

		int nextCrsId = nextStage.getId();
		ClanRaidStageMonster nextCrsFirstCrsm = clanRaidStageMonsterRetrieveUtils
				.getFirstMonsterForClanRaidStage(nextCrsId);

		if (null == nextCrsFirstCrsm) {
			//WTF???
			throw new RuntimeException(String.format(
					"WTF!!!! clan raid stage has no monsters! >:( crs=%s",
					nextStage));
		}

		int nextCrsCrsmId = nextCrsFirstCrsm.getId();
		int numUpdated = UpdateUtils.get()
				.updateClanEventPersistentForClanGoToNextStage(clanId,
						nextCrsId, nextCrsCrsmId);
		log.info(String
				.format("clan just cleared stage! nextStage=%s, curEventInfo=%s numUpdated=%s",
						nextStage, cepfc, numUpdated));
		//need to  update clan raid info for all the clan users

		numUpdated = UpdateUtils.get()
				.updateClanEventPersistentForUserGoToNextStage(nextCrsId,
						nextCrsCrsmId, userIdToCepfu);

		log.info(String.format("rows updated when clan cleared stage. num=%s"
				+ numUpdated));

		//need to update ClanEventPersistentForClan cepfc since need to update client
		cepfc.setCrsId(nextCrsId);
		cepfc.setStageStartTime(null);
		cepfc.setCrsmId(nextCrsCrsmId);
		cepfc.setStageMonsterStartTime(null);

		//since just killed monster, another one takes its place. replace the amount
		//in hazelcast with 0 which represents how much damage the clan has done so far
		int newCrsmHp = 0;//nextCrsFirstCrsm.getMonsterHp();
		boolean replaceCrsmDmg = true;
		getClanEventUtil().updateClanIdCrsmDmg(clanId, newCrsmHp,
				replaceCrsmDmg);

		//record to the clan raid stage user history, since stage ended
		int stageHp = clanRaidStageRetrieveUtils
				.getClanRaidStageHealthForCrsId(curCrsId);
		int numInserted = InsertUtils.get().insertIntoCepfuRaidStageHistory(
				eventId, stageStartTime, curTime, stageHp, userIdToCepfu);
		log.info("clan event persistent for user raid stage history, numInserted="
				+ numInserted);
	}

	//user killed monster. this means:
	//1) for clanEventPersistentForClan, crsmId goes to the next one, and stage
	//monser start time changes to now
	//2) for clanEventpersistentForUser, for all clan members update push their crsmDmg
	// to crsDmg and update the crsmId
	private void recordClanRaidStageMonsterVictory(String userId,
			String clanId, int crsId, Timestamp curTime, int newDmg,
			ClanRaidStageMonster nextCrsm, ClanEventPersistentForClan cepfc,
			Map<String, ClanEventPersistentForUser> userIdToCepfu) {

		int nextCrsmId = nextCrsm.getId();

		int numUpdated = UpdateUtils.get()
				.updateClanEventPersistentForClanGoToNextMonster(clanId,
						nextCrsmId, curTime);
		log.info(String.format("rows updated when clan killed monster. num=%s",
				numUpdated));

		numUpdated = UpdateUtils.get()
				.updateClanEventPersistentForUsersGoToNextMonster(crsId,
						nextCrsmId, userIdToCepfu);

		log.info(String.format("rows updated when user killed monster. num=%s",
				numUpdated));

		//need to update ClanEventPersistentForClan cepfc since need to update client
		cepfc.setCrsmId(nextCrsmId);
		cepfc.setStageMonsterStartTime(curTime);

		//since just killed monster, another one takes its place. replace the amount
		//in hazelcast with 0 which represents how much damage the clan has done so far
		int newCrsmHp = 0;//nextCrsm.getMonsterHp();
		boolean replaceCrsmDmg = true;
		getClanEventUtil().updateClanIdCrsmDmg(clanId, newCrsmHp,
				replaceCrsmDmg);
	}

	//for currency rewards, calculate the number user gets, then take a fraction of it
	//for monster reward, calculate user contribution in stage (crsmDmg + crsDmg)
	//	divided it by stageHp, multiply it by the reward's monsterDropRateMultiplier
	//	generate random number and if below computed value, user gets monster
	private List<ClanEventPersistentUserReward> awardRewards(int crsId,
			Date crsStartDate, Date crsEndDate, int clanEventId,
			Map<String, ClanEventPersistentForUser> userIdToCepfu) {
		int stageHp = clanRaidStageRetrieveUtils
				.getClanRaidStageHealthForCrsId(crsId);
		Timestamp crsStartTime = new Timestamp(crsStartDate.getTime());
		Timestamp crsEndTime = new Timestamp(crsEndDate.getTime());

		List<ClanEventPersistentUserReward> allRewards = new ArrayList<ClanEventPersistentUserReward>();

		Map<Integer, ClanRaidStageReward> rewardIdsToRewards = clanRaidStageRewardRetrieveUtils
				.getClanRaidStageRewardsForClanRaidStageId(crsId);

		//for each user generate the rewards he gets based on his contribution
		for (String userId : userIdToCepfu.keySet()) {
			ClanEventPersistentForUser cepfu = userIdToCepfu.get(userId);

			generateAllRewardsForUser(crsId, crsStartDate, crsEndDate,
					clanEventId, cepfu, stageHp, rewardIdsToRewards, allRewards);
		}

		List<String> ids = InsertUtils.get().insertIntoCepUserReward(
				crsStartTime, crsId, crsEndTime, clanEventId, allRewards);
		log.info("num clan event user rewards inserted:" + ids.size());

		//set the ids because these rewards are going to be written back to the client
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			ClanEventPersistentUserReward reward = allRewards.get(i);
			reward.setId(id);
		}

		return allRewards;
	}

	private void generateAllRewardsForUser(int crsId, Date crsStartDate,
			Date crsEndDate, int clanEventId, ClanEventPersistentForUser cepfu,
			int stageHp, Map<Integer, ClanRaidStageReward> rewardIdsToRewards,
			List<ClanEventPersistentUserReward> allRewards) {

		//for each reward, see what this user gets
		for (ClanRaidStageReward reward : rewardIdsToRewards.values()) {

			List<ClanEventPersistentUserReward> someRewards = generateSomeRewardsForUser(
					crsId, crsStartDate, crsEndDate, clanEventId, cepfu,
					stageHp, reward);

			//NOTE: IF WANT TO CUT DOWN ON REWARDS WRITTEN, CAN AGGREGATE someRewards LIST
			//BEFORE ADDING IT ALL INTO allRewards
			allRewards.addAll(someRewards);
		}
	}

	private List<ClanEventPersistentUserReward> generateSomeRewardsForUser(
			int crsId, Date crsStartDate, Date crsEndDate, int clanEventId,
			ClanEventPersistentForUser cepfu, int stageHp,
			ClanRaidStageReward reward) {
		List<ClanEventPersistentUserReward> userRewards = new ArrayList<ClanEventPersistentUserReward>();

		String userId = cepfu.getUserId();
		int userCrsDmg = cepfu.getCrsDmgDone() + cepfu.getCrsmDmgDone();
		float userCrsContribution = (userCrsDmg) / (stageHp);

		int staticDataId = 0;
		//TODO: FIGURE OUT IF CURRENCY CALCULATION IS OK (right now just truncating the value) 
		//create the userOilReward maybe
		int userOilReward = (int) ((reward.getOilDrop()) * userCrsContribution);
		createClanEventPersistentUserReward(miscMethods.OIL, userOilReward,
				staticDataId, crsId, crsStartDate, crsEndDate, clanEventId,
				userId, userRewards);

		//create the userOilReward maybe  	
		int userCashReward = (int) ((reward.getCashDrop()) * userCrsContribution);
		createClanEventPersistentUserReward(miscMethods.CASH, userCashReward,
				staticDataId, crsId, crsStartDate, crsEndDate, clanEventId,
				userId, userRewards);

		int monsterId = reward.getMonsterId();
		if (0 >= monsterId) {
			//not a monster reward
			return userRewards;
		}

		//compute monster reward
		float monsterDropRate = userCrsContribution
				* (reward.getExpectedMonsterRewardQuantity());
		Random rand = reward.getRand();
		if (rand.nextFloat() < monsterDropRate) {
			//user gets the monster reward
			staticDataId = monsterId;
			int quantity = 1;
			createClanEventPersistentUserReward(miscMethods.MONSTER,
					staticDataId, quantity, crsId, crsStartDate, crsEndDate,
					clanEventId, userId, userRewards);
		}

		return userRewards;
	}

	//goal is to make a method that knows when not to create a reward so caller doesn't
	//have to
	private void createClanEventPersistentUserReward(String resourceType,
			int staticDataId, int quantity, int crsId, Date crsStartDate,
			Date crsEndDate, int clanEventId, String userId,
			List<ClanEventPersistentUserReward> userRewards) {

		if (quantity <= 0) {
			return;
		}

		ClanEventPersistentUserReward cepur = new ClanEventPersistentUserReward(
				"", userId, crsStartDate, crsId, crsEndDate, resourceType,
				staticDataId, quantity, clanEventId, null);

		userRewards.add(cepur);
	}

	private void setClanEventClanDetails(Builder resBuilder,
			List<ClanEventPersistentForClan> clanEventList) {
		if (!clanEventList.isEmpty()) {
			ClanEventPersistentForClan cepfc = clanEventList.get(0);
			if (null != cepfc) {
				PersistentClanEventClanInfoProto updatedEventDetails = createInfoProtoUtils
						.createPersistentClanEventClanInfoProto(cepfc);
				resBuilder.setEventDetails(updatedEventDetails);
			}
		}
	}

	private void setClanEventUserDetails(Builder resBuilder,
			Map<String, ClanEventPersistentForUser> userIdToCepfu) {
		if (!userIdToCepfu.isEmpty()) {
			//whenever server has this information send it to the clients
			List<String> userMonsterIds = monsterStuffUtils
					.getUserMonsterIdsInClanRaid(userIdToCepfu);

			Map<String, MonsterForUser> idsToUserMonsters = monsterForUserRetrieveUtil
					.getSpecificUserMonsters(userMonsterIds);

			for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
				PersistentClanEventUserInfoProto pceuip = createInfoProtoUtils
						.createPersistentClanEventUserInfoProto(cepfu,
								idsToUserMonsters, null);
				resBuilder.addClanUsersDetails(pceuip);
			}
		}
	}

	//write to the whole clan the rewards for all the users when the stage ends and
	//there are rewards
	private void setClanEventRewards(
			List<ClanEventPersistentUserReward> allRewards,
			PersistentClanEventClanInfoProto eventDetails, ToClientEvents responses) {
		if (null == allRewards) {
			return;
		}

		String clanId = eventDetails.getClanUuid();
		int crsId = eventDetails.getClanRaidStageId();

		AwardClanRaidStageRewardResponseProto.Builder resBuilder = AwardClanRaidStageRewardResponseProto
				.newBuilder();
		resBuilder.setCrsId(crsId);

		for (ClanEventPersistentUserReward reward : allRewards) {
			PersistentClanEventUserRewardProto rewardProto = createInfoProtoUtils
					.createPersistentClanEventUserRewardProto(reward);
			resBuilder.addAllRewards(rewardProto);
		}

		AwardClanRaidStageRewardResponseEvent resEvent = new AwardClanRaidStageRewardResponseEvent(
				clanId);
		resEvent.setTag(0);
		resEvent.setAwardClanRaidStageRewardResponseProto(resBuilder.build());

		responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
	}

	protected Locker getLocker() {
		return locker;
	}

	protected void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public ClanEventUtil getClanEventUtil() {
		return clanEventUtil;
	}

	public void setClanEventUtil(ClanEventUtil clanEventUtil) {
		this.clanEventUtil = clanEventUtil;
	}

}
