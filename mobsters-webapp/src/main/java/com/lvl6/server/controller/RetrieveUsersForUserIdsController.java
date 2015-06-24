package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveUsersForUserIdsRequestEvent;
import com.lvl6.events.response.RetrieveUsersForUserIdsResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ResearchForUser;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsRequestProto;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.ResearchsProto.AllUserResearchProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.daos.ResearchForUserDao2;
import com.lvl6.retrieveutils.rarechange.ResearchPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.server.controller.utils.ResearchUtil;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component

public class RetrieveUsersForUserIdsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(RetrieveUsersForUserIdsController.class);

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	protected ResearchForUserDao2 rfuDao;
	
	@Autowired
	protected ResearchUtil researchUtil;
	
	@Autowired
	protected ResearchRetrieveUtils researchRetrieveUtils;
	
	@Autowired
	protected ResearchPropertyRetrieveUtils researchPropertyRetrieveUtils;


	public RetrieveUsersForUserIdsController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveUsersForUserIdsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_USERS_FOR_USER_IDS_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RetrieveUsersForUserIdsRequestProto reqProto = ((RetrieveUsersForUserIdsRequestEvent) event)
				.getRetrieveUsersForUserIdsRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		List<String> requestedUserIds = reqProto.getRequestedUserUuidsList();
		boolean includeCurMonsterTeam = reqProto.getIncludeCurMonsterTeam();

		RetrieveUsersForUserIdsResponseProto.Builder resBuilder = RetrieveUsersForUserIdsResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			if (requestedUserIds != null) {
				for (String userId : requestedUserIds) {
					userUuid = UUID.fromString(userId);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect requestedUserIds=%s",
					requestedUserIds), e);
			invalidUuids = true;
		}

		//    boolean includePotentialPoints = reqProto.getIncludePotentialPointsForClanTowers();
		//    User sender = includePotentialPoints ? RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid()) : null;
		Map<String, User> usersByIds = getUserRetrieveUtils().getUsersByIds(
				requestedUserIds);
		if (usersByIds != null) {

			Set<String> clanIds = new HashSet<String>();
			for (User user : usersByIds.values()) {
				String clanId = user.getClanId();
				if (clanId != null && !clanId.isEmpty()) {
					clanIds.add(clanId);
				}
			}

			Map<String, Clan> clanIdToClan = new HashMap<String, Clan>();
			if (!clanIds.isEmpty()) {
				clanIdToClan = getClanRetrieveUtils().getClansByIds(clanIds);
			}

			for (User user : usersByIds.values()) {

				//TODO: consider getting from db
				//pull from hazelcast for now
				String userId = user.getId();
				PvpUser pu = getHazelcastPvpUtil().getPvpUser(userId);
				PvpLeagueForUser plfu = null;

				if (null != pu) {
					plfu = new PvpLeagueForUser(pu);
				}

				Clan clan = null;
				if (null != user.getClanId()) {
					clan = clanIdToClan.get(user.getClanId());
				}

				resBuilder.addRequestedUsers(createInfoProtoUtils
						.createFullUserProtoFromUser(user, plfu, clan));

			}

			List<UserCurrentMonsterTeamProto> teams = null;
			List<AllUserResearchProto> research = null;
			if (includeCurMonsterTeam) {
				teams = constructTeamsForUsers(requestedUserIds);
				research = createResearchProtos(requestedUserIds);
			}

			if (null != teams && !teams.isEmpty()) {
				resBuilder.addAllCurTeam(teams);
			}
			if (null != research && !research.isEmpty()) {
				resBuilder.addAllUserResearch(research);
			}

		} else {
			log.error("no users with the ids " + requestedUserIds);
		}
		RetrieveUsersForUserIdsResponseProto resProto = resBuilder.build();
		RetrieveUsersForUserIdsResponseEvent resEvent = new RetrieveUsersForUserIdsResponseEvent(
				senderProto.getUserUuid());
		resEvent.setTag(event.getTag());
		resEvent.setResponseProto(resProto);
		responses.normalResponseEvents().add(resEvent);
	}

	private List<UserCurrentMonsterTeamProto> constructTeamsForUsers(
			List<String> userIds) {
		Map<String, List<MonsterForUser>> userIdsToCurrentTeam = getMonsterForUserRetrieveUtils()
				.getUserIdsToMonsterTeamForUserIds(userIds);

		//for each user construct his current team
		List<UserCurrentMonsterTeamProto> retVal = new ArrayList<UserCurrentMonsterTeamProto>();
		for (String userId : userIdsToCurrentTeam.keySet()) {
			List<MonsterForUser> currentTeam = userIdsToCurrentTeam.get(userId);

			List<FullUserMonsterProto> currentTeamProto = createInfoProtoUtils
					.createFullUserMonsterProtoList(currentTeam);

			//create the proto via the builder
			UserCurrentMonsterTeamProto.Builder teamForUser = UserCurrentMonsterTeamProto
					.newBuilder();
			teamForUser.setUserUuid(userId);
			teamForUser.addAllCurrentTeam(currentTeamProto);

			retVal.add(teamForUser.build());
		}

		return retVal;
	}
	
	public List<AllUserResearchProto> createResearchProtos(List<String> userIds) {
		Map<String, List<ResearchForUser>> rfuMap = rfuDao.fetchByUserIds(userIds);
		return researchUtil.createAllUserResearchProto(rfuMap, researchRetrieveUtils, researchPropertyRetrieveUtils);
	}
	

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

}
