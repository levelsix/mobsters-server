package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CompleteMiniJobRequestEvent;
import com.lvl6.events.response.CompleteMiniJobResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobResponseProto;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
public class CompleteMiniJobController extends EventController {

	private static Logger log = LoggerFactory.getLogger(CompleteMiniJobController.class);

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;
	
	@Autowired
	protected TimeUtils timeUtils;
	

	public CompleteMiniJobController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CompleteMiniJobRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COMPLETE_MINI_JOB_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		CompleteMiniJobRequestProto reqProto = ((CompleteMiniJobRequestEvent) event)
				.getCompleteMiniJobRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		String userMiniJobId = reqProto.getUserMiniJobUuid();

		boolean isSpeedUp = reqProto.getIsSpeedUp();
		int gemCost = reqProto.getGemCost();

		CompleteMiniJobResponseProto.Builder resBuilder = CompleteMiniJobResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			CompleteMiniJobResponseEvent resEvent = 
					new CompleteMiniJobResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		UUID userUuid = null;
		UUID userMiniJobUuid = null;

		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			userMiniJobUuid = UUID.fromString(userMiniJobId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userMiniJobId=%s",
					userId, userMiniJobId), e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			CompleteMiniJobResponseEvent resEvent = new CompleteMiniJobResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			//retrieve whatever is necessary from the db
			//TODO: consider only retrieving user if the request is valid
			User user = RetrieveUtils.userRetrieveUtils().getUserById(
					senderProto.getUserUuid());
			int previousGems = 0;

			boolean legit = checkLegit(resBuilder, userId, user, userMiniJobId,
					isSpeedUp, gemCost);

			boolean success = false;
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();

			if (legit) {
				previousGems = user.getGems();
				success = writeChangesToDB(userId, user, userMiniJobId,
						isSpeedUp, gemCost, clientTime, currencyChange);
			}

			if (success) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			CompleteMiniJobResponseEvent resEvent = new CompleteMiniJobResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(user, userMiniJobId, currencyChange,
						clientTime, previousGems);
			}

		} catch (Exception e) {
			log.error("exception in CompleteMiniJobController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				CompleteMiniJobResponseEvent resEvent = new CompleteMiniJobResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in CompleteMiniJobController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegit(Builder resBuilder, String userId, User user,
			String userMiniJobId, boolean isSpeedUp, int gemCost) {

		//sanity check
		if (null == userMiniJobId || userMiniJobId.isEmpty()) {
			log.error(String.format("invalid userMiniJobId. userMiniJobId=%s",
					userMiniJobId));
			return false;
		}

		Collection<String> userMiniJobIds = Collections
				.singleton(userMiniJobId);
		Map<String, MiniJobForUser> idToUserMiniJob = miniJobForUserRetrieveUtil
				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);

		if (idToUserMiniJob.isEmpty()) {
			log.error(String.format("no UserMiniJob exists with id=%s",
					userMiniJobId));
			resBuilder.setStatus(ResponseStatus.FAIL_DOESNT_EXIST);
			return false;
		}

		if (isSpeedUp && !hasEnoughGems(resBuilder, user, gemCost)) {
			return false;
		}

		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < Math.abs(gemsSpent)) {
			log.error(String
					.format("user does not have enough gems. userGems=%s, gemsSpent=%s",
							userGems, gemsSpent));
			resBuilder.setStatus(ResponseStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(String userId, User user,
			String userMiniJobId, boolean isSpeedUp, int gemCost,
			Timestamp clientTime, Map<String, Integer> currencyChange) {

		//update user currency
		int gemsChange = -1 * Math.abs(gemCost);
		int cashChange = 0;
		int oilChange = 0;

		if (isSpeedUp && !updateUser(user, gemsChange, cashChange, oilChange)) {
			log.error(String
					.format("could not decrement user gems by %s, cash by %s, and oil by %s",
							gemsChange, cashChange, oilChange));
			return false;
		} else {
			if (0 != gemsChange) {
				currencyChange.put(miscMethods.gems, gemsChange);
			}
		}

		//update complete time for MiniJobForUser
		int numUpdated = UpdateUtils.get().updateMiniJobForUserCompleteTime(
				userId, userMiniJobId, clientTime);

		log.info(String.format("writeChangesToDB() numUpdated=%s", numUpdated));

		return true;
	}

	private boolean updateUser(User u, int gemsChange, int cashChange,
			int oilChange) {
		int numChange = u.updateRelativeCashAndOilAndGems(cashChange,
				oilChange, gemsChange, 0);

		if (numChange <= 0) {
			log.error(String
					.format("problem with updating resources. gemChange=%s, cash=%s, oil=%s, user=%s",
							gemsChange, cashChange, oilChange, u));
			return false;
		}
		return true;
	}

	private void writeToUserCurrencyHistory(User aUser, String userMiniJobId,
			Map<String, Integer> currencyChange, Timestamp curTime,
			int previousGems) {
		String userId = aUser.getId();
		String reason = ControllerConstants.UCHRFC__SPED_UP_COMPLETE_MINI_JOB;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("userMiniJobId=");
		detailsSb.append(userMiniJobId);

		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = miscMethods.gems;

		previousCurrency.put(gems, previousGems);
		currentCurrency.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, detailsSb.toString());

		miscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);

	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
		return miniJobForUserRetrieveUtil;
	}

	public void setMiniJobForUserRetrieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
	}

}
