package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetractRequestJoinClanRequestEvent;
import com.lvl6.events.response.RetractRequestJoinClanResponseEvent;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.RetractRequestJoinClanAction;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component

public class RetractRequestJoinClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(RetractRequestJoinClanController.class);

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	public RetractRequestJoinClanController() {
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
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RetractRequestJoinClanRequestProto reqProto = ((RetractRequestJoinClanRequestEvent) event)
				.getRetractRequestJoinClanRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String clanId = reqProto.getClanUuid();

		RetractRequestJoinClanResponseProto.Builder resBuilder = RetractRequestJoinClanResponseProto
				.newBuilder();
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
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
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RetractRequestJoinClanResponseEvent resEvent = new RetractRequestJoinClanResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean lockedClan = false;
		try {
			if (clanUuid != null) {
				lockedClan = locker.lockClan(clanUuid);
			}
			RetractRequestJoinClanAction rrjca = new RetractRequestJoinClanAction(userId, clanId,
					lockedClan, userRetrieveUtils, insertUtil, deleteUtil, clanRetrieveUtils,
					userClanRetrieveUtils);
			rrjca.execute(resBuilder);

			RetractRequestJoinClanResponseEvent resEvent = new RetractRequestJoinClanResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);


			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
			}

		} catch (Exception e) {
			log.error("exception in RetractRequestJoinClan processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RetractRequestJoinClanResponseEvent resEvent = new RetractRequestJoinClanResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in RetractRequestJoinClan processEvent",
						e);
			}
		} finally {
			if (clanUuid != null && lockedClan) {
				locker.unlockClan(clanUuid);
			}
		}
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
