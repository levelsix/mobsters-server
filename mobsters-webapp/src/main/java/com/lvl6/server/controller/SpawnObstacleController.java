package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SpawnObstacleRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.SpawnObstacleResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.SpawnObstacleRequestProto;
import com.lvl6.proto.EventStructureProto.SpawnObstacleResponseProto;
import com.lvl6.proto.EventStructureProto.SpawnObstacleResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.StructureProto.MinimumObstacleProto;
import com.lvl6.proto.StructureProto.UserObstacleProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.StructureStuffUtil;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
public class SpawnObstacleController extends EventController {

	private static Logger log = LoggerFactory.getLogger(SpawnObstacleController.class);

	@Autowired
	protected StructureStuffUtil structureStuffUtil;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected TimeUtils timeUtils;

	public SpawnObstacleController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SpawnObstacleRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SPAWN_OBSTACLE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SpawnObstacleRequestProto reqProto = ((SpawnObstacleRequestEvent) event)
				.getSpawnObstacleRequestProto();
		log.info("reqProto=" + reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Timestamp clientTime = new Timestamp(reqProto.getCurTime());
		List<MinimumObstacleProto> mopList = reqProto
				.getProspectiveObstaclesList();

		SpawnObstacleResponseProto.Builder resBuilder = SpawnObstacleResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		
		if(reqProto.getCurTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			SpawnObstacleResponseEvent resEvent = new SpawnObstacleResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(new Date(reqProto.getCurTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			SpawnObstacleResponseEvent resEvent = 
					new SpawnObstacleResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

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
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			SpawnObstacleResponseEvent resEvent = new SpawnObstacleResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = getUserRetrieveUtils().getUserById(
					senderProto.getUserUuid());

			boolean legitComplete = checkLegit(resBuilder, userId, user,
					mopList);

			boolean success = false;
			List<ObstacleForUser> ofuList = new ArrayList<ObstacleForUser>();
			if (legitComplete) {
				success = writeChangesToDB(user, userId, clientTime, mopList,
						ofuList);
			}

			if (success) {
				//client needs the object protos but with the ids set
				for (ObstacleForUser ofu : ofuList) {
					UserObstacleProto obp = createInfoProtoUtils
							.createUserObstacleProto(ofu);
					resBuilder.addSpawnedObstacles(obp);
				}
			}

			SpawnObstacleResponseEvent resEvent = new SpawnObstacleResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (success) {
				//modified the user, the last obstacle removed time
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

			}

		} catch (Exception e) {
			log.error("exception in SpawnObstacleController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SpawnObstacleResponseEvent resEvent = new SpawnObstacleResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SpawnObstacleController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegit(Builder resBuilder, String userId, User user,
			List<MinimumObstacleProto> mopList) {

		if (null == user) {
			log.error("unexpected error: user is null. user=" + user
					+ "\t userId=" + userId);
			return false;
		}

		if (null == mopList || mopList.isEmpty()) {
			log.error("no obstacles to spawn for user. mopList=" + mopList
					+ "\t user=" + user);
			return false;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDB(User user, String userId,
			Timestamp clientTime, List<MinimumObstacleProto> mopList,
			List<ObstacleForUser> ofuList) {
		//convert the protos to java objects
		List<ObstacleForUser> ofuListTemp = getStructureStuffUtil()
				.createObstacleForUserFromUserObstacleProtos(userId, mopList);
		log.info("inserting obstacles into obstacle_for_user. ofuListTemp="
				+ ofuListTemp);

		//need to get the ids in order to set the objects' ids so client will know how
		//to reference said objects
		List<String> ofuIdList = InsertUtils.get()
				.insertIntoObstaclesForUserGetIds(userId, ofuListTemp);
		if (null == ofuIdList || ofuIdList.isEmpty()) {
			log.error("could not insert into obstacle for user obstacles="
					+ ofuListTemp);
			return false;
		}

		log.info("updating last obstacle spawned time:" + clientTime);
		if (!user.updateRelativeGemsObstacleTimeNumRemoved(0, clientTime, 0)) {
			log.error("could not update last obstacle spawned time to "
					+ clientTime);
			return false;
		}

		getStructureStuffUtil().setObstacleForUserIds(ofuIdList, ofuListTemp);

		ofuList.addAll(ofuListTemp);
		return true;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

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
