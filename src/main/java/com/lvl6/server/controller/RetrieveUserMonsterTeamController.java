package com.lvl6.server.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveUserMonsterTeamRequestEvent;
import com.lvl6.events.response.RetrieveUserMonsterTeamResponseEvent;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamRequestProto;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto.RetrieveUserMonsterTeamStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.retrieveutils.PvpBoardObstacleForUserRetrieveUtil;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.actionobjects.RetrieveUserMonsterTeamAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component
@DependsOn("gameServer")
public class RetrieveUserMonsterTeamController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	private UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	private ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	private MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;

	@Autowired
	private ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	private MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;

	@Autowired
	private HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	private PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;

	@Autowired
	private PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil;

	@Autowired
	private ResearchForUserRetrieveUtils researchForUserRetrieveUtil;

	@Autowired
	private MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;


	public RetrieveUserMonsterTeamController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveUserMonsterTeamRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_USER_MONSTER_TEAM_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RetrieveUserMonsterTeamRequestProto reqProto = ((RetrieveUserMonsterTeamRequestEvent) event)
				.getRetrieveUserMonsterTeamRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userUuidsList = reqProto.getUserUuidsList();

		RetrieveUserMonsterTeamResponseProto.Builder resBuilder = RetrieveUserMonsterTeamResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(RetrieveUserMonsterTeamStatus.FAIL_OTHER);

		Set<String> userUuidsSet = new HashSet<String>();

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);

			//process only the valid ids
			for (String requestedUserUuid : userUuidsList) {
				try {
					UUID.fromString(requestedUserUuid);
					userUuidsSet.add(requestedUserUuid);
				} catch (Exception e) {
					log.error(String.format("invalid UUID: %s",
							requestedUserUuid), e);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(RetrieveUserMonsterTeamStatus.FAIL_OTHER);
			RetrieveUserMonsterTeamResponseEvent resEvent = new RetrieveUserMonsterTeamResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveUserMonsterTeamResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		//		server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {

			RetrieveUserMonsterTeamAction rumta = new RetrieveUserMonsterTeamAction(
					userId, userUuidsList, userRetrieveUtil, clanRetrieveUtil,
					monsterForUserRetrieveUtil,
					clanMemberTeamDonationRetrieveUtil,
					monsterSnapshotForUserRetrieveUtil, hazelcastPvpUtil,
					pvpLeagueForUserRetrieveUtil,
					pvpBoardObstacleForUserRetrieveUtil, researchForUserRetrieveUtil,
					monsterStuffUtils, serverToggleRetrieveUtil,
					monsterLevelInfoRetrieveUtils);

			rumta.execute(resBuilder);
			if (resBuilder.getStatus().equals(
					RetrieveUserMonsterTeamStatus.SUCCESS)) {
				//TODO: replace QueueUp and Avenge controller logic with this
				List<PvpProto> ppList = createInfoProtoUtils
						.createPvpProtos(
								rumta.getAllUsersExceptRetriever(),
								rumta.getUserIdToClan(),
								null,
								rumta.getUserIdToPvpUsers(),
								rumta.getAllButRetrieverUserIdToUserMonsters(),
								rumta.getAllButRetrieverUserIdToUserMonsterIdToDroppedId(),
								rumta.getAllButRetrieverUserIdToCashLost(),
								rumta.getAllButRetrieverUserIdToOilLost(),
								rumta.getAllButRetrieverUserIdToCmtd(),
								rumta.getAllButRetrieverUserIdToMsfu(),
								rumta.getAllButRetrieverUserIdToMsfuMonsterDropId(),
								rumta.getAllButRetrieverUserIdToPvpBoardObstacles(),
								rumta.getAllButRetrieverUserIdToUserResearch());

				log.info("ppList={}", ppList);
				resBuilder.addAllUserMonsterTeam(ppList);
			}

			RetrieveUserMonsterTeamResponseProto resProto = resBuilder.build();
			RetrieveUserMonsterTeamResponseEvent resEvent = new RetrieveUserMonsterTeamResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveUserMonsterTeamResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in RetrieveUserMonsterTeamController processEvent",
					e);
			try {
				resBuilder.setStatus(RetrieveUserMonsterTeamStatus.FAIL_OTHER);
				RetrieveUserMonsterTeamResponseEvent resEvent = new RetrieveUserMonsterTeamResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRetrieveUserMonsterTeamResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrieveUserMonsterTeamController processEvent",
						e);
			}

			//		} finally {
			//			      server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil() {
		return monsterForUserRetrieveUtil;
	}

	public void setMonsterForUserRetrieveUtil(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil) {
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
	}

	public ClanMemberTeamDonationRetrieveUtil getClanMemberTeamDonationRetrieveUtil() {
		return clanMemberTeamDonationRetrieveUtil;
	}

	public void setClanMemberTeamDonationRetrieveUtil(
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil) {
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
	}

	public MonsterSnapshotForUserRetrieveUtil getMonsterSnapshotForUserRetrieveUtil() {
		return monsterSnapshotForUserRetrieveUtil;
	}

	public void setMonsterSnapshotForUserRetrieveUtil(
			MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil) {
		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

	public PvpBoardObstacleForUserRetrieveUtil getPvpBoardObstacleForUserRetrieveUtil() {
		return pvpBoardObstacleForUserRetrieveUtil;
	}

	public void setPvpBoardObstacleForUserRetrieveUtil(
			PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil) {
		this.pvpBoardObstacleForUserRetrieveUtil = pvpBoardObstacleForUserRetrieveUtil;
	}

	public ResearchForUserRetrieveUtils getResearchForUserRetrieveUtil() {
		return researchForUserRetrieveUtil;
	}

	public void setResearchForUserRetrieveUtil(
			ResearchForUserRetrieveUtils researchForUserRetrieveUtil) {
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	public ServerToggleRetrieveUtils getServerToggleRetrieveUtil() {
		return serverToggleRetrieveUtil;
	}

	public void setServerToggleRetrieveUtil(
			ServerToggleRetrieveUtils serverToggleRetrieveUtil) {
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

}
