package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import com.lvl6.proto.ClanProto.PersistentClanEventUserInfoProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BeginClanRaidRequestProto;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto.BeginClanRaidStatus;
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils2;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.ClanEventUtil;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class BeginClanRaidController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtil;

	@Autowired
	protected ClanEventPersistentForClanRetrieveUtils2 clanEventPersistentForClanRetrieveUtil;

	@Autowired
	protected ClanEventPersistentForUserRetrieveUtils2 clanEventPersistentForUserRetrieveUtil;
	
	@Autowired
	protected ClanEventPersistentRetrieveUtils clanEventPersistentRetrieveUtils;
	
	@Autowired
	protected ClanRaidStageRetrieveUtils clanRaidStageRetrieveUtils;
	
	@Autowired
	protected ClanRaidStageMonsterRetrieveUtils clanRaidStageMonsterRetrieveUtils;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected ClanEventUtil clanEventUtil;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

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
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
		BeginClanRaidRequestProto reqProto = ((BeginClanRaidRequestEvent) event)
				.getBeginClanRaidRequestProto();

		log.info(String.format("reqProto=%s", reqProto));
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		MinimumClanProto mcp = senderProto.getClan();
		int clanEventPersistentId = reqProto.getClanEventId();

		Date curDate = new Date(reqProto.getCurTime());
		Timestamp curTime = new Timestamp(curDate.getTime());
		int clanRaidId = reqProto.getRaidId(); //not really needed

		boolean setMonsterTeamForRaid = reqProto.getSetMonsterTeamForRaid();
		List<FullUserMonsterProto> userMonsters = reqProto
				.getUserMonstersList();
		List<String> userMonsterIds = monsterStuffUtils
				.getUserMonsterIds(userMonsters);
		boolean isFirstStage = reqProto.getIsFirstStage();

		BeginClanRaidResponseProto.Builder resBuilder = BeginClanRaidResponseProto
				.newBuilder();
		resBuilder.setStatus(BeginClanRaidStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		//OUTLINE: 
		//get the clan lock; get the clan raid object for the clan;
		// If doesn't exist, create it. If does exist, check to see if the raids are different.
		// If different, replace it with a new one. Else, do nothing.
		String clanId = "";

		UUID userUuid = null;
		UUID clanUuid = null;
		boolean invalidUuids = true;
		if (null != mcp && mcp.hasClanUuid()) {
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
			resBuilder.setStatus(BeginClanRaidStatus.FAIL_OTHER);
			BeginClanRaidResponseEvent resEvent = new BeginClanRaidResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setBeginClanRaidResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		//ONLY GET CLAN LOCK IF TRYING TO BEGIN A RAID
		boolean lockedClan = false;
		if (null != clanUuid && !setMonsterTeamForRaid) {
			log.info(String.format("locking clanId=%s", clanId));
			lockedClan = getLocker().lockClan(clanUuid);
		}
		try {
			//      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
			UserClan uc = RetrieveUtils.userClanRetrieveUtils()
					.getSpecificUserClan(userId, clanId);
			boolean legitRequest = checkLegitRequest(resBuilder, lockedClan,
					senderProto, userId, clanId, uc, clanEventPersistentId,
					clanRaidId, curDate, curTime, setMonsterTeamForRaid,
					userMonsterIds, isFirstStage);

			List<ClanEventPersistentForClan> clanInfoList = new ArrayList<ClanEventPersistentForClan>();
			boolean success = false;
			if (legitRequest) {
				log.info("recording in the db that the clan began a clan raid or setting monsters."
						+ " or starting a stage. isFirstStage=" + isFirstStage);
				success = writeChangesToDB(userId, clanId,
						clanEventPersistentId, clanRaidId, curTime,
						setMonsterTeamForRaid, userMonsterIds, isFirstStage,
						clanInfoList);
			}

			if (success) {
				if (!setMonsterTeamForRaid) {
					ClanEventPersistentForClan cepfc = clanInfoList.get(0);
					PersistentClanEventClanInfoProto eventDetails = createInfoProtoUtils
							.createPersistentClanEventClanInfoProto(cepfc);
					resBuilder.setEventDetails(eventDetails);
				}
				if (setMonsterTeamForRaid) {
					setClanAndUserDetails(resBuilder, userId, clanId,
							clanRaidId, userMonsterIds, userMonsters);
				}
				resBuilder.setStatus(BeginClanRaidStatus.SUCCESS);
				log.info("BEGIN CLAN RAID EVENT SUCCESS!!!!!!!");
			}

			BeginClanRaidResponseEvent resEvent = new BeginClanRaidResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setBeginClanRaidResponseProto(resBuilder.build());
			log.info("resBuilder=" + resBuilder.build());
			server.writeEvent(resEvent);

			if (success) {
				//only write to the user if the request was valid
				server.writeClanEvent(resEvent, clanId);
			}

		} catch (Exception e) {
			log.error("exception in BeginClanRaid processEvent", e);
			try {
				resBuilder.setStatus(BeginClanRaidStatus.FAIL_OTHER);
				BeginClanRaidResponseEvent resEvent = new BeginClanRaidResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setBeginClanRaidResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in BeginClanRaid processEvent", e);
			}
		} finally {

			//ONLY RELEASE CLAN LOCK IF TRYING TO BEGIN A RAID
			//if (null != mcp && mcp.hasClanId()) {
			//if (0 != clanId && !setMonsterTeamForRaid && lockedClan) {
			if (lockedClan) {
				getLocker().unlockClan(clanUuid);
				log.info(String.format("unlocked clanId=%s", clanId));
			}
			//}

		}
	}

	private boolean checkLegitRequest(Builder resBuilder, boolean lockedClan,
			MinimumUserProto mupfc, String userId, String clanId, UserClan uc,
			int clanEventId, int clanRaidId, Date curDate, Timestamp curTime,
			boolean setMonsterTeamForRaid, List<String> userMonsterIds,
			boolean isFirstStage) {

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}
		if (clanId.isEmpty() || 0 == clanRaidId || null == uc) {
			log.error("not in clan. user is " + mupfc
					+ "\t or clanRaidId invalid id=" + clanRaidId
					+ "\t or no user clan exists. uc=" + uc);
			return false;
		}

		//user can only start raid if an event exists for it, check if event exists,
		//clan raid events CAN overlap
		Map<Integer, ClanEventPersistent> clanEventIdToEvent = clanEventPersistentRetrieveUtils
				.getActiveClanEventIdsToEvents(curDate, timeUtils);
		if (!clanEventIdToEvent.containsKey(clanEventId)) {
			resBuilder.setStatus(BeginClanRaidStatus.FAIL_NO_ACTIVE_CLAN_RAID);
			log.error(String.format("no active clan event with id=%s, user=%s",
					clanEventId, mupfc));
			return false;
		}

		//give the event id back to the caller
		ClanEventPersistent cep = clanEventIdToEvent.get(clanEventId);
		int eventRaidId = cep.getClanRaidId();
		if (clanRaidId != eventRaidId) {
			resBuilder.setStatus(BeginClanRaidStatus.FAIL_NO_ACTIVE_CLAN_RAID);
			log.error(String.format(
					"no active clan event with raidId=%s, event=%s, user=%s",
					clanRaidId, cep, mupfc));
			return false;
		}

		//only check if user can start raid if he is not setting his monsters
		//check if the clan already has existing raid information
		if (!setMonsterTeamForRaid) {
			Set<String> authorizedUsers = getAuthorizedUsers(clanId);
			if (!authorizedUsers.contains(userId)) {
				resBuilder.setStatus(BeginClanRaidStatus.FAIL_NOT_AUTHORIZED);
				log.error(String.format("user can't start raid. user=%s"
						+ mupfc));
				return false;
			}
			//user is authorized to start clan raid

			if (!validClanInfo(resBuilder, clanId, clanEventId, clanRaidId,
					curDate, curTime, isFirstStage)) {
				return false;
			}
		}

		//Don't think any checks need to be made
		//(user needs to equip user monsters before beginning raid; checks are done there) 
		if (setMonsterTeamForRaid
				&& (null == userMonsterIds || userMonsterIds.isEmpty())) {
			resBuilder.setStatus(BeginClanRaidStatus.FAIL_NO_MONSTERS_SENT);
			log.error("client did not send any monster ids to set for clan raid.");
			return false;
		}

		return true;
	}

	//get all the members in a clan
	private Set<String> getAuthorizedUsers(String clanId) {
		Set<String> authorizedUsers = new HashSet<String>();
		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		List<String> userIds = userClanRetrieveUtil.getUserIdsWithStatuses(
				clanId, statuses);

		if (null != userIds && !userIds.isEmpty()) {
			authorizedUsers.addAll(userIds);
		}

		return authorizedUsers;
	}

	private boolean validClanInfo(Builder resBuilder, String clanId,
			int clanEventId, int clanRaidId, Date curDate, Timestamp now,
			boolean isFirstStage) {
		//check if clan already started the event
		ClanEventPersistentForClan raidStartedByClan = clanEventPersistentForClanRetrieveUtil
				.getPersistentEventForClanId(clanId);

		if (null == raidStartedByClan && isFirstStage) {
			return true;
		} else if (null == raidStartedByClan && !isFirstStage) {
			log.error("clan has not started a raid/event (nothing in clan_event_persistent_for_clan)"
					+ " but client claims clan started one. clanId="
					+ clanId
					+ "\t clanEventId="
					+ clanEventId
					+ "\t clanRaidId="
					+ clanRaidId + "\t isFirstStage=" + isFirstStage);
			return false;
		}

		//clan raid/event entry exists for clan
		int ceId = raidStartedByClan.getClanEventPersistentId();
		int crId = raidStartedByClan.getCrId();

		if ((ceId != clanEventId || crId != clanRaidId)
				|| (null != raidStartedByClan && isFirstStage)) {
			log.warn("possibly encountered clan raid data that should have been pushed to"
					+ " history. pushing now. clanEvent="
					+ raidStartedByClan
					+ "\t clanEventId="
					+ clanEventId
					+ "\t clanRaidId="
					+ clanRaidId + "\t isFirstStage=" + isFirstStage);
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
			log.warn(String
					.format("the clan raid stage start time is not null when beginning clan raid. clanEvent=%s",
							raidStartedByClan));
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

	//copy pasted from RecordClanRaidStatsController.writeChangesToD
	private void pushCurrentClanEventDataToHistory(String clanId,
			Timestamp now, ClanEventPersistentForClan clanEvent) {
		int clanEventPersistentId = clanEvent.getClanEventPersistentId();
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
		boolean won = false;

		//record whatever is in the ClanEventPersistentForClan
		int numInserted = InsertUtils.get()
				.insertIntoClanEventPersistentForClanHistory(clanId, now,
						clanEventPersistentId, crId, crsId, stageStartTime,
						crsmId, stageMonsterStartTime, won);

		log.info(String.format(
				"rows inserted into clan raid info for clan (should be 1): %s",
				numInserted));
		//get all the clan raid info for the users, and then delete them
		Map<String, ClanEventPersistentForUser> clanUserInfo = clanEventPersistentForUserRetrieveUtil
				.getPersistentEventUserInfoForClanId(clanId);

		//delete clan info for clan raid
		DeleteUtils.get().deleteClanEventPersistentForClan(clanId);

		if (null != clanUserInfo && !clanUserInfo.isEmpty()) {
			//record whatever is in the ClanEventPersistentForUser
			numInserted = InsertUtils.get().insertIntoCepfuRaidHistory(
					clanEventPersistentId, now, clanUserInfo);
			log.info(String
					.format("rows inserted into clan raid info for user (should be %s): %s",
							clanUserInfo.size(), numInserted));

			//delete clan user info for clan raid
			List<String> userIdList = new ArrayList<String>(
					clanUserInfo.keySet());
			DeleteUtils.get().deleteClanEventPersistentForUsers(userIdList);

		}
	}

	private boolean writeChangesToDB(String userId, String clanId,
			int clanEventPersistentId, int clanRaidId, Timestamp curTime,
			boolean setMonsterTeamForRaid, List<String> userMonsterIds,
			boolean isFirstStage, List<ClanEventPersistentForClan> clanInfo) {

		if (setMonsterTeamForRaid) {
			int numInserted = InsertUtils.get()
					.insertIntoUpdateMonstersClanEventPersistentForUser(userId,
							clanId, clanRaidId, userMonsterIds);

			log.info("num rows inserted into clan raid info for user table: "
					+ numInserted);

		} else if (isFirstStage) {
			ClanRaidStage crs = clanRaidStageRetrieveUtils
					.getFirstStageForClanRaid(clanRaidId);
			int clanRaidStageId = crs.getId();

			Map<Integer, ClanRaidStageMonster> stageIdToMonster = clanRaidStageMonsterRetrieveUtils
					.getClanRaidStageMonstersForClanRaidStageId(clanRaidStageId);
			ClanRaidStageMonster crsm = stageIdToMonster.get(clanRaidStageId);
			int crsmId = crsm.getId();

			//once user begins a raid he auto begins a stage and auto begins the first monster
			int numInserted = InsertUtils.get()
					.insertIntoClanEventPersistentForClan(clanId,
							clanEventPersistentId, clanRaidId, clanRaidStageId,
							curTime, crsmId, curTime);

			log.info(String.format(
					"num rows inserted into clan raid info for clan table: %s",
					numInserted));

			ClanEventPersistentForClan cepfc = new ClanEventPersistentForClan(
					clanId, clanEventPersistentId, clanRaidId, clanRaidStageId,
					curTime, crsmId, curTime);
			clanInfo.add(cepfc);

			//since beginning the first stage of a clan raide stage, zero out the damage
			//for this raid for the clan in hazelcast
			getClanEventUtil().updateClanIdCrsmDmg(clanId, 0, true);

		} else {
			log.info("starting another clan raid stage!!!!!!!!!");
			int numUpdated = UpdateUtils.get()
					.updateClanEventPersistentForClanStageStartTime(clanId,
							curTime);
			log.info("num rows updated in clan raid info for clan table: "
					+ numUpdated);

			ClanEventPersistentForClan cepfc = clanEventPersistentForClanRetrieveUtil
					.getPersistentEventForClanId(clanId);
			clanInfo.add(cepfc);
		}

		return true;
	}

	private void setClanAndUserDetails(Builder resBuilder, String userId,
			String clanId, int clanRaidId, List<String> userMonsterIds,
			List<FullUserMonsterProto> userMonsters) {
		//crsId and crsmId is not needed in PersistentClanEventUserInfoProto
		//should already be in the db table 
		//  	ClanEventPersistentForClan cepfc = ClanEventPersistentForClanRetrieveUtils
		//    		.getPersistentEventForClanId(clanId);

		String userMonsterIdOne = "";
		String userMonsterIdTwo = "";
		String userMonsterIdThree = "";

		if (userMonsterIds.size() >= 1) {
			userMonsterIdOne = userMonsterIds.get(0);
		}
		if (userMonsterIds.size() >= 2) {
			userMonsterIdTwo = userMonsterIds.get(1);
		}
		if (userMonsterIds.size() >= 3) {
			userMonsterIdThree = userMonsterIds.get(2);
		}

		ClanEventPersistentForUser cepfu = new ClanEventPersistentForUser(
				userId, clanId, clanRaidId, 0, 0, 0, 0, 0, userMonsterIdOne,
				userMonsterIdTwo, userMonsterIdThree);
		PersistentClanEventUserInfoProto userDetails = createInfoProtoUtils
				.createPersistentClanEventUserInfoProto(cepfu, null,
						userMonsters);
		resBuilder.setUserDetails(userDetails);
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
