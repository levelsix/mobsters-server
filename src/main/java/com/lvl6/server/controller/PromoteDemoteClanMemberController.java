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
import com.lvl6.events.request.PromoteDemoteClanMemberRequestEvent;
import com.lvl6.events.response.PromoteDemoteClanMemberResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberRequestProto;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto.Builder;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto.PromoteDemoteClanMemberStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class PromoteDemoteClanMemberController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

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
		resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_OTHER);
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
			resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_OTHER);
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
			Map<String, User> users = getUserRetrieveUtils().getUsersByIds(
					userIds);
			Map<String, UserClan> userClans = getUserClanRetrieveUtils()
					.getUserClanForUsers(clanId, userIds);

			boolean legitRequest = checkLegitRequest(resBuilder, lockedClan,
					userId, victimId, newUserClanStatus, users, userClans);

			boolean success = false;
			if (legitRequest) {
				User victim = users.get(victimId);
				UserClan oldInfo = userClans.get(victimId);
				try {
					UserClanStatus ucs = UserClanStatus.valueOf(oldInfo
							.getStatus());
					resBuilder.setPrevUserClanStatus(ucs);
				} catch (Exception e) {
					log.error("incorrect user clan status. userClan=" + oldInfo);
				}

				success = writeChangesToDB(victim, victimId, clanId, oldInfo,
						newUserClanStatus);
			}

			PromoteDemoteClanMemberResponseEvent resEvent = new PromoteDemoteClanMemberResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			//only write to user if failed
			if (!success) {
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);

			} else {
				//TODO: Is clan needed here?
				//only write to clan if success
				resBuilder.setStatus(PromoteDemoteClanMemberStatus.SUCCESS);
				User victim = users.get(victimId);
				Clan victimClan = getClanRetrieveUtils().getClanWithId(clanId);
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
				resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_OTHER);
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

	private boolean checkLegitRequest(Builder resBuilder, boolean lockedClan,
			String userId, String victimId, UserClanStatus newUserClanStatus,
			Map<String, User> userIdsToUsers,
			Map<String, UserClan> userIdsToUserClans) {

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}
		if (null == userIdsToUsers || userIdsToUsers.size() != 2
				|| null == userIdsToUserClans || userIdsToUserClans.size() != 2) {
			log.error("user or userClan objects do not total 2. users="
					+ userIdsToUsers + "\t userIdsToUserClans="
					+ userIdsToUserClans);
			return false;
		}

		//check if users are in the db
		if (!userIdsToUserClans.containsKey(userId)
				|| !userIdsToUsers.containsKey(userId)) {
			log.error("user promoting or demoting not in clan or db. userId="
					+ userId + "\t userIdsToUserClans=" + userIdsToUserClans
					+ "\t userIdsToUsers=" + userIdsToUsers);
			resBuilder
					.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_IN_CLAN);
			return false;
		}
		if (!userIdsToUserClans.containsKey(victimId)
				|| !userIdsToUsers.containsKey(victimId)) {
			log.error("user to be promoted or demoted not in clan or db. victim="
					+ victimId
					+ "\t userIdsToUserClans="
					+ userIdsToUserClans
					+ "\t userIdsToUsers=" + userIdsToUsers);
			resBuilder
					.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_IN_CLAN);
			return false;
		}

		//check if user can demote/promote the other one
		UserClan promoterDemoter = userIdsToUserClans.get(userId);
		UserClan victim = userIdsToUserClans.get(victimId);

		UserClanStatus first = UserClanStatus.valueOf(promoterDemoter
				.getStatus());
		UserClanStatus second = UserClanStatus.valueOf(victim.getStatus());
		if (UserClanStatus.CAPTAIN.equals(first)
				|| !clanStuffUtils
						.firstUserClanStatusAboveSecond(first, second)) {
			log.error("user not authorized to promote or demote otherUser. clanStatus of user="
					+ first + "\t clanStatus of other user=" + second);
			resBuilder
					.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
			return false;
		}
		if (!clanStuffUtils.firstUserClanStatusAboveSecond(first,
				newUserClanStatus)) {
			log.error("user not authorized to promote or demote otherUser. clanStatus of user="
					+ first
					+ "\t clanStatus of other user="
					+ second
					+ "\t newClanStatus of other user=" + newUserClanStatus);
			resBuilder
					.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
			return false;
		}
		if (UserClanStatus.REQUESTING.equals(second)) {
			log.error("user can't promote, demote a non-clan member. UserClan for user="
					+ promoterDemoter
					+ "\t UserClan for victim="
					+ victim
					+ "\t users=" + userIdsToUsers);
			resBuilder
					.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(User victim, String victimId,
			String clanId, UserClan oldInfo, UserClanStatus newUserClanStatus) {
		if (!UpdateUtils.get().updateUserClanStatus(victimId, clanId,
				newUserClanStatus)) {
			log.error("problem with updating user clan status for user="
					+ victim + "\t oldInfo=" + oldInfo
					+ "\t newUserClanStatus=" + newUserClanStatus);
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
