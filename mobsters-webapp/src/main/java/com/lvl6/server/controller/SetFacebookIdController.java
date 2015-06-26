package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetFacebookIdRequestEvent;
import com.lvl6.events.response.SetFacebookIdResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.SetFacebookIdRequestProto;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component

public class SetFacebookIdController extends EventController {

	private static Logger log = LoggerFactory.getLogger(SetFacebookIdController.class);

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	public SetFacebookIdController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SetFacebookIdRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SET_FACEBOOK_ID_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SetFacebookIdRequestProto reqProto = ((SetFacebookIdRequestEvent) event)
				.getSetFacebookIdRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String fbId = reqProto.getFbId();
		boolean isUserCreate = reqProto.getIsUserCreate();
		String email = reqProto.getEmail();
		String fbData = reqProto.getFbData();

		//basically, if fbId is empty make it null
		if (null != fbId && fbId.isEmpty()) {
			fbId = null;
		}

		//prepping the arguments to query the db
		List<String> facebookIds = null;
		if (null != fbId) {
			facebookIds = new ArrayList<String>();
			facebookIds.add(fbId);
		}
		List<String> userIds = new ArrayList<String>();
		userIds.add(userId);

		Builder resBuilder = SetFacebookIdResponseProto.newBuilder();
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

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
			SetFacebookIdResponseEvent resEvent = new SetFacebookIdResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			//      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());
			Map<String, User> userMap = getUserRetrieveUtils()
					.getUsersForFacebookIdsOrUserIds(facebookIds, userIds);
			User user = userMap.get(userId);

			boolean legit = checkLegitRequest(resBuilder, user, fbId, userMap);

			if (legit) {
				legit = writeChangesToDb(user, fbId, isUserCreate, email,
						fbData);
			}

			if (legit) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			SetFacebookIdResponseProto resProto = resBuilder.build();
			SetFacebookIdResponseEvent resEvent = new SetFacebookIdResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in SetFacebookIdController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SetFacebookIdResponseEvent resEvent = new SetFacebookIdResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SetFacebookIdController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegitRequest(Builder resBuilder, User user,
			String newFbId, Map<String, User> userMap) {
		if (newFbId == null || newFbId.isEmpty() || user == null) {
			log.error(String.format(
					"fbId not set or user is null. fbId='%s', user=%s",
					newFbId, user));
			return false;
		}

		String existingFbId = user.getFacebookId();
		boolean existingFbIdSet = existingFbId != null
				&& !existingFbId.isEmpty();

		if (existingFbIdSet) {
			log.error(String
					.format("fbId already set for user. existingFbId='%s', user=%s, newFbId=%s",
							existingFbId, user, newFbId));
			resBuilder
					.setStatus(ResponseStatus.FAIL_USER_FB_ID_ALREADY_SET);
			return false;
		}

		//fbId is something and user doesn't have fbId
		//now check if other users have the newFbId

		if (userMap.size() > 1) {
			//queried for a userId and a facebook id
			log.error(String.format(
					"fbId already taken. fbId='%s', usersInDb=%s", newFbId,
					userMap));
			resBuilder.setStatus(ResponseStatus.FAIL_FB_ID_EXISTS);

			//client wants the user who has the facebook id
			for (User u : userMap.values()) {

				if (!newFbId.equals(u.getFacebookId())) {
					continue;
				}

				MinimumUserProto existingProto = createInfoProtoUtils
						.createMinimumUserProtoFromUserAndClan(u, null);
				resBuilder.setExisting(existingProto);
				break;
			}

			return false;
		}

		return true;
	}

	private boolean writeChangesToDb(User user, String fbId,
			boolean isUserCreate, String email, String fbData) {

		if (!user.updateSetFacebookId(fbId, isUserCreate, email, fbData)) {
			log.error("problem with setting user's facebook id to " + fbId);
			return false;
		}

		return true;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

}
