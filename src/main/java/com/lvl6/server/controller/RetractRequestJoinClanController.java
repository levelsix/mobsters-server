package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetractRequestJoinClanRequestEvent;
import com.lvl6.events.response.RetractRequestJoinClanResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanResponseProto;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanResponseProto.RetractRequestJoinClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component
@DependsOn("gameServer")
public class RetractRequestJoinClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	public RetractRequestJoinClanController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RetractRequestJoinClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRACT_REQUEST_JOIN_CLAN_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
		RetractRequestJoinClanRequestProto reqProto = ((RetractRequestJoinClanRequestEvent) event)
				.getRetractRequestJoinClanRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String clanId = reqProto.getClanUuid();

		RetractRequestJoinClanResponseProto.Builder resBuilder = RetractRequestJoinClanResponseProto
				.newBuilder();
		resBuilder.setStatus(RetractRequestJoinClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);
		resBuilder.setClanUuid(clanId);

		UUID userUuid = null;
		UUID clanUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			clanUuid = UUID.fromString(clanId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, clanId=%s", userId,
					clanId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(RetractRequestJoinClanStatus.FAIL_OTHER);
			RetractRequestJoinClanResponseEvent resEvent = new RetractRequestJoinClanResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRetractRequestJoinClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		boolean lockedClan = false;
		if (clanUuid != null) {
			lockedClan = getLocker().lockClan(clanUuid);
		}
		try {
			User user = getUserRetrieveUtils().getUserById(
					senderProto.getUserUuid());
			Clan clan = getClanRetrieveUtils().getClanWithId(clanId);

			boolean legitRetract = checkLegitRequest(resBuilder, lockedClan,
					user, clan);

			boolean success = false;
			if (legitRetract) {
				success = writeChangesToDB(user, clanId);
			}

			if (success) {
				resBuilder.setStatus(RetractRequestJoinClanStatus.SUCCESS);
			}

			RetractRequestJoinClanResponseEvent resEvent = new RetractRequestJoinClanResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRetractRequestJoinClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (success) {
				server.writeClanEvent(resEvent, clan.getId());
			}

		} catch (Exception e) {
			log.error("exception in RetractRequestJoinClan processEvent", e);
			try {
				resBuilder.setStatus(RetractRequestJoinClanStatus.FAIL_OTHER);
				RetractRequestJoinClanResponseEvent resEvent = new RetractRequestJoinClanResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRetractRequestJoinClanResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in RetractRequestJoinClan processEvent",
						e);
			}
		} finally {
			if (clanUuid != null && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}

	private boolean checkLegitRequest(Builder resBuilder, boolean lockedClan,
			User user, Clan clan) {

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}
		if (user == null || clan == null) {
			resBuilder.setStatus(RetractRequestJoinClanStatus.FAIL_OTHER);
			log.error("user is " + user + ", clan is " + clan);
			return false;
		}
		if (user.getClanId() != null) {
			resBuilder
					.setStatus(RetractRequestJoinClanStatus.FAIL_ALREADY_IN_CLAN);
			log.error("user is already in clan with id " + user.getClanId());
			return false;
		}
		UserClan uc = getUserClanRetrieveUtils().getSpecificUserClan(
				user.getId(), clan.getId());
		if (uc == null
				|| !UserClanStatus.REQUESTING.name().equals(uc.getStatus())) {
			resBuilder
					.setStatus(RetractRequestJoinClanStatus.FAIL_DID_NOT_REQUEST);
			log.error("user clan request has not been filed");
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(User user, String clanId) {
		if (!DeleteUtils.get().deleteUserClan(user.getId(), clanId)) {
			log.error("problem with deleting user clan data for user " + user
					+ ", and clan id " + clanId);
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

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

}
