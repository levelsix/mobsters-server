package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SpawnMiniJobRequestEvent;
import com.lvl6.events.response.SpawnMiniJobResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MiniJob;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobResponseProto;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobResponseProto.Builder;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobResponseProto.SpawnMiniJobStatus;
import com.lvl6.proto.MiniJobConfigProto.UserMiniJobProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.utils.StructureStuffUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
public class SpawnMiniJobController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected StructureStuffUtil structureStuffUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected MiniJobRetrieveUtils miniJobRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	//	@Autowired
	//	protected Locker locker;

	public SpawnMiniJobController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SpawnMiniJobRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SPAWN_MINI_JOB_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		SpawnMiniJobRequestProto reqProto = ((SpawnMiniJobRequestEvent) event)
				.getSpawnMiniJobRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int numToSpawn = reqProto.getNumToSpawn();
		numToSpawn = Math.max(0, numToSpawn);
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		Date now = new Date(reqProto.getClientTime());
		int structId = reqProto.getStructId();

		SpawnMiniJobResponseProto.Builder resBuilder = SpawnMiniJobResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(SpawnMiniJobStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(SpawnMiniJobStatus.FAIL_OTHER);
			SpawnMiniJobResponseEvent resEvent = new SpawnMiniJobResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setSpawnMiniJobResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		//TODO: figure out if locking is needed
		//		getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			User user = getUserRetrieveUtils().getUserById(userId);
			Map<Integer, MiniJob> miniJobIdToMiniJob = miniJobRetrieveUtils
					.getMiniJobForStructId(structId);

			boolean legit = checkLegitRequest(resBuilder, userId, user,
					numToSpawn, structId, miniJobIdToMiniJob);

			List<MiniJob> spawnedMiniJobs = null;
			List<MiniJobForUser> spawnedUserMiniJobs = null;
			boolean success = false;
			if (legit) {

				spawnedMiniJobs = spawnMiniJobs(numToSpawn, structId,
						miniJobIdToMiniJob);
				spawnedUserMiniJobs = convertIntoUserMiniJobs(userId,
						spawnedMiniJobs);
				success = writeChangesToDB(user, userId, numToSpawn,
						clientTime, now, spawnedUserMiniJobs);
			}

			if (success) {
				resBuilder.setStatus(SpawnMiniJobStatus.SUCCESS);
				List<UserMiniJobProto> userMiniJobProtos = createInfoProtoUtils
						.createUserMiniJobProtos(spawnedUserMiniJobs,
								miniJobIdToMiniJob, rewardRetrieveUtil);
				resBuilder.addAllMiniJobs(userMiniJobProtos);
			}

			SpawnMiniJobResponseEvent resEvent = new SpawnMiniJobResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setSpawnMiniJobResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (success) {
				//modified the user, the last obstacle removed time
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

			}

		} catch (Exception e) {
			log.error("exception in SpawnMiniJobController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(SpawnMiniJobStatus.FAIL_OTHER);
				SpawnMiniJobResponseEvent resEvent = new SpawnMiniJobResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setSpawnMiniJobResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SpawnMiniJobController processEvent",
						e);
			}
			//		} finally {
			//			getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}
	}

	private boolean checkLegitRequest(Builder resBuilder, String userId,
			User user, int numToSpawn, int structId,
			Map<Integer, MiniJob> miniJobIdToMiniJob) {

		if (null == user) {
			log.error("invalid userId, since user doesn't exist: " + userId);
			return false;
		}

		if (0 == structId) {
			log.error("invalid structId=" + structId);
			return false;
		}

		if (null == miniJobIdToMiniJob || miniJobIdToMiniJob.isEmpty()) {
			log.error("no minijobs for structId=" + structId);
			return false;
		}

		return true;
	}

	private List<MiniJob> spawnMiniJobs(int numToSpawn, int structId,
			Map<Integer, MiniJob> miniJobIdToMiniJob) {
		List<MiniJob> spawnedMiniJobs = new ArrayList<MiniJob>();

		if (0 == numToSpawn) {
			log.info("client just reseting the last spawn time");
			return spawnedMiniJobs;
		}

		float probabilitySum = miniJobRetrieveUtils
				.getMiniJobProbabilitySumForStructId(structId);
		Random rand = new Random();
		log.info("probabilitySum=" + probabilitySum);
		//		log.info("miniJobIdToMiniJob=" + miniJobIdToMiniJob);

		int numToSpawnCopy = numToSpawn;
		while (numToSpawnCopy > 0) {
			float randFloat = rand.nextFloat();
			log.info("randFloat=" + randFloat);

			float probabilitySoFar = 0;
			for (MiniJob mj : miniJobIdToMiniJob.values()) {
				float chanceToAppear = mj.getChanceToAppear();

				probabilitySoFar += chanceToAppear;
				float normalizedProb = probabilitySoFar / probabilitySum;

				//				log.info("probabilitySoFar=" + probabilitySoFar);
				//				log.info("normalizedProb=" + normalizedProb);
				if (randFloat > normalizedProb) {
					continue;
				}

				log.info("selected MiniJob!: " + mj);
				//we have a winner
				spawnedMiniJobs.add(mj);
				break;
			}
			//regardless of whether or not we find one,
			//prevent it from infinite looping
			numToSpawnCopy--;
		}
		if (spawnedMiniJobs.size() != numToSpawn) {
			log.error("Could not spawn " + numToSpawn + " mini jobs. spawned: "
					+ spawnedMiniJobs);
		}
		return spawnedMiniJobs;
	}

	private List<MiniJobForUser> convertIntoUserMiniJobs(String userId,
			List<MiniJob> miniJobs) {
		List<MiniJobForUser> userMiniJobs = new ArrayList<MiniJobForUser>();

		for (MiniJob mj : miniJobs) {
			MiniJobForUser mjfu = new MiniJobForUser();

			int miniJobId = mj.getId();
			int dmgDealt = mj.getDmgDealt();
			int durationSeconds = mj.getDurationSeconds();

			mjfu.setUserId(userId);
			mjfu.setMiniJobId(miniJobId);
			mjfu.setBaseDmgReceived(dmgDealt);
			mjfu.setDurationSeconds(durationSeconds);

			userMiniJobs.add(mjfu);
		}

		return userMiniJobs;
	}

	private boolean writeChangesToDB(User user, String userId, int numToSpawn,
			Timestamp clientTime, Date now,
			List<MiniJobForUser> spawnedUserMiniJobs) {

		if (numToSpawn > 0 && spawnedUserMiniJobs.size() > 0) {
			log.info("inserting user mini jobs: " + spawnedUserMiniJobs
					+ "\t numToSpawn: " + numToSpawn);
			//give user his mini jobs
			List<String> userMiniJobIds = InsertUtils
					.get()
					.insertIntoMiniJobForUserGetIds(userId, spawnedUserMiniJobs);
			log.info("inserted MiniJobForUser into mini_job_for_user:"
					+ spawnedUserMiniJobs + "\t ids=" + userMiniJobIds);

			//TODO: move to some other class
			for (int index = 0; index < userMiniJobIds.size(); index++) {
				String userMiniJobId = userMiniJobIds.get(index);
				MiniJobForUser mjfu = spawnedUserMiniJobs.get(index);
				mjfu.setId(userMiniJobId);
			}
		}

		//update user's last mini job spawn time
		boolean updated = user.updateLastMiniJobGeneratedTime(now, clientTime);

		log.info("updated LastMiniJobGeneratedTime: " + updated);

		return true;
	}

	//  public Locker getLocker() {
	//	  return locker;
	//  }
	//  public void setLocker(Locker locker) {
	//	  this.locker = locker;
	//  }

	public StructureStuffUtil getStructureStuffUtil() {
		return structureStuffUtil;
	}

	public void setStructureStuffUtil(StructureStuffUtil structureStuffUtil) {
		this.structureStuffUtil = structureStuffUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

}
