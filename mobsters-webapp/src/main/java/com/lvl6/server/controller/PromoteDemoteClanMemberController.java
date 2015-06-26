package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PromoteDemoteClanMemberRequestEvent;
import com.lvl6.events.response.PromoteDemoteClanMemberResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberRequestProto;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.PromoteDemoteClanMemberAction;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class PromoteDemoteClanMemberController extends EventController {

	private static Logger log = LoggerFactory.getLogger(PromoteDemoteClanMemberController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected ClanStuffUtils clanStuffUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected DeleteUtil deleteUtil;


	public PromoteDemoteClanMemberController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new PromoteDemoteClanMemberRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_PROMOTE_DEMOTE_CLAN_MEMBER_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		PromoteDemoteClanMemberRequestProto reqProto = ((PromoteDemoteClanMemberRequestEvent) event)
				.getPromoteDemoteClanMemberRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String victimId = reqProto.getVictimUuid();
		UserClanStatus newUserClanStatus = reqProto.getUserClanStatus();
		List<String> userIds = new ArrayList<String>();
		userIds.add(userId);
		userIds.add(victimId);

		PromoteDemoteClanMemberResponseProto.Builder resBuilder = PromoteDemoteClanMemberResponseProto
				.newBuilder();
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);
		resBuilder.setUserClanStatus(newUserClanStatus);

		String clanId = null;
		if (senderProto.hasClan() && null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}

		UUID userUuid = null;
		UUID victimUuid = null;
		UUID clanUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			victimUuid = UUID.fromString(victimId);
			clanUuid = UUID.fromString(clanId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, victimId=%s, clanId=%s",
					userId, victimId, clanId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			PromoteDemoteClanMemberResponseEvent resEvent = new PromoteDemoteClanMemberResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean lockedClan = false;
		if (clanUuid != null) {
			lockedClan = getLocker().lockClan(clanUuid);
		}
		try {
			PromoteDemoteClanMemberAction pdcma = new PromoteDemoteClanMemberAction(userId, victimId,
					newUserClanStatus, lockedClan, userRetrieveUtils, updateUtil, deleteUtil,
					userClanRetrieveUtils, clanStuffUtils);
			pdcma.execute(resBuilder);

			PromoteDemoteClanMemberResponseEvent resEvent = new PromoteDemoteClanMemberResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			//only write to user if failed
			if (!ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);

			} else {
				//TODO: Is clan needed here?
				//only write to clan if success
				User victim = pdcma.getUsers().get(victimId);
				Clan victimClan = clanRetrieveUtils.getClanWithId(clanId);
				MinimumUserProto mup = createInfoProtoUtils
						.createMinimumUserProtoFromUserAndClan(victim,
								victimClan);
				resBuilder.setVictim(mup);

				resEvent.setResponseProto(resBuilder
						.build());
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
			}
		} catch (Exception e) {
			log.error("exception in PromoteDemoteClanMember processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				PromoteDemoteClanMemberResponseEvent resEvent = new PromoteDemoteClanMemberResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in PromoteDemoteClanMember processEvent",
						e);
			}
		} finally {
			if (clanId != null && lockedClan) {
				getLocker().unlockClan(clanUuid);
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
