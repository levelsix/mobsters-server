package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.NormStructWaitCompleteRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.NormStructWaitCompleteResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteRequestProto;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteResponseProto;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class NormStructWaitCompleteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(NormStructWaitCompleteController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected StructureForUserRetrieveUtils2 structureForUserRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected TimeUtils timeUtils;

	public NormStructWaitCompleteController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new NormStructWaitCompleteRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_NORM_STRUCT_WAIT_COMPLETE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		NormStructWaitCompleteRequestProto reqProto = ((NormStructWaitCompleteRequestEvent) event)
				.getNormStructWaitCompleteRequestProto();
		log.info("reqProto={}", reqProto);

		//stuff client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userStructIds = reqProto.getUserStructUuidList();
		userStructIds = new ArrayList<String>(userStructIds);
		Timestamp timeOfCompletion = new Timestamp(reqProto.getTimeOfCompletion());
		Date currentClientTime = new Date(reqProto.getCurrentClientTime());

		//stuff to send to client
		NormStructWaitCompleteResponseProto.Builder resBuilder = NormStructWaitCompleteResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		
		if(reqProto.getCurrentClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(currentClientTime, new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			NormStructWaitCompleteResponseEvent resEvent = 
					new NormStructWaitCompleteResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		UUID userStructUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (userStructIds != null) {
				for (String userStructId : userStructIds) {
					userStructUuid = UUID.fromString(userStructId);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userStructIds=%s",
					userId, userStructIds), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			List<StructureForUser> userStructs = getStructureForUserRetrieveUtils()
					.getSpecificOrAllUserStructsForUser(userId, userStructIds);

			List<Timestamp> newRetrievedTimes = new ArrayList<Timestamp>();
			boolean legitWaitComplete = checkLegitWaitComplete(resBuilder,
					userStructs, userStructIds, senderProto.getUserUuid(),
					timeOfCompletion, newRetrievedTimes);

			boolean success = false;
			if (legitWaitComplete) {
				//upgrading and building a building is the same thing
				success = writeChangesToDB(userId, userStructs,
						newRetrievedTimes);
			}

			if (success) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
				List<StructureForUser> newUserStructs = getStructureForUserRetrieveUtils()
						.getSpecificOrAllUserStructsForUser(userId,
								userStructIds);
				for (StructureForUser userStruct : newUserStructs) {
					resBuilder
							.addUserStruct(createInfoProtoUtils
									.createFullUserStructureProtoFromUserstruct(userStruct));
				}
			}

			NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in NormStructWaitCompleteController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				NormStructWaitCompleteResponseEvent resEvent = new NormStructWaitCompleteResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in NormStructWaitCompleteController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private boolean checkLegitWaitComplete(Builder resBuilder,
			List<StructureForUser> userStructs, List<String> userStructIds,
			String userId, Timestamp clientTime,
			List<Timestamp> newRetrievedTimes) {

		if (userStructs == null || userStructIds == null || clientTime == null
				|| userStructIds.size() != userStructs.size()) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			String preface = "userStructs, userStructIds, or clientTime is null,";
			String preface2 = " or array lengths different.";
			log.error(String.format(
					"%s %s userStructs=%s, userStructIds=%s, clientTime=%s",
					preface, preface2, userStructs, userStructIds, clientTime));
			return false;
		}

		//for each user structure complete the ones the client said are done.
		//replace what client sent with the ones that are actually done
		List<StructureForUser> validUserStructs = new ArrayList<StructureForUser>();
		List<String> validUserStructIds = new ArrayList<String>();

		List<Timestamp> timesBuildsFinished = calculateValidUserStructs(userId,
				clientTime, userStructs, validUserStructIds, validUserStructs);

		if (userStructs.size() != validUserStructs.size()) {
			String preface = "some of what the client sent is invalid.";
			log.warn(String.format("%s idsClientSent=%s \t validIds=%s",
					preface, userStructIds, validUserStructIds));
			userStructs.clear();
			userStructs.addAll(validUserStructs);

			userStructIds.clear();
			userStructIds.addAll(validUserStructIds);
		}

		newRetrievedTimes.addAll(timesBuildsFinished);
		return true;

	}

	//"validUserStructIds" and "validUserStructs" WILL BE POPULATED
	private List<Timestamp> calculateValidUserStructs(String userId,
			Timestamp clientTime, List<StructureForUser> userStructs,
			List<String> validUserStructIds,
			List<StructureForUser> validUserStructs) {
		List<Timestamp> timesBuildsFinished = new ArrayList<Timestamp>();
		Map<Integer, Structure> structures = structureRetrieveUtils
				.getStructIdsToStructs();

		for (StructureForUser us : userStructs) {
			if (!us.getUserId().equals(userId)) {
				log.warn(String.format(
						"user struct's ownerId=%s, and userId=%s",
						us.getUserId(), userId));
				continue;
			}
			Structure struct = structures.get(us.getStructId());
			if (struct == null) {
				log.warn(String.format("no struct in db exists with id=%s",
						us.getStructId()));
				continue;
			}

			Date purchaseDate = us.getPurchaseTime();

			// Commented out to allow speedups to work
			//      long buildTimeMillis = 60000*struct.getMinutesToBuild();

			if (null != purchaseDate) {
				//      	long timeBuildFinishes = purchaseDate.getTime() + buildTimeMillis;
				//
				//      	if (timeBuildFinishes > clientTime.getTime()) {
				//      		log.warn(String.format(
				//      			"building not done yet. userstruct=%s, client_time=%s, purchase_time=%s, time_build_finishes=%s",
				//      			us, clientTime, purchaseDate, timeBuildFinishes));
				//      		continue;
				//        }//else this building is done now

				validUserStructIds.add(us.getId());
				validUserStructs.add(us);
				timesBuildsFinished.add(clientTime);

			} else {
				log.warn(String
						.format("user struct never bought mor purchased according to db. %s",
								us));
			}
		}
		return timesBuildsFinished;
	}

	private boolean writeChangesToDB(String userId,
			List<StructureForUser> buildsDone, List<Timestamp> newRetrievedTimes) {
		if (!UpdateUtils.get().updateUserStructsBuildingIsComplete(userId,
				buildsDone, newRetrievedTimes)) {
			boolean allComplete = true;
			for(StructureForUser sfu : buildsDone) {
				if(!sfu.isComplete()) {
					allComplete = false;
				}
			}
			if(allComplete) {
				log.error("all the structures are already complete, structures: {}", buildsDone);
			}
			else {
				log.error(String
						.format("problem marking norm struct builds as complete for a struct: %s",
								buildsDone));
				return false;
			}
		}

		int expReward = 0;
		for (StructureForUser sfu : buildsDone) {
			int structId = sfu.getStructId();
			Structure s = structureRetrieveUtils.getStructForStructId(structId);
			expReward += s.getExpReward();
		}

		if (expReward > 0) {
			User u = userRetrieveUtil.getUserById(userId);
			log.info(String.format("user before rewarding exp: %s", u));
			u.updateRelativeGemsNaive(0, expReward);
			log.info(String.format("user after rewarding exp: %s", u));
		}

		return true;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public StructureForUserRetrieveUtils2 getStructureForUserRetrieveUtils() {
		return structureForUserRetrieveUtils;
	}

	public void setStructureForUserRetrieveUtils(
			StructureForUserRetrieveUtils2 structureForUserRetrieveUtils) {
		this.structureForUserRetrieveUtils = structureForUserRetrieveUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

}
