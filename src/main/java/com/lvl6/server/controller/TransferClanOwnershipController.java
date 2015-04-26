package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.TransferClanOwnershipRequestEvent;
import com.lvl6.events.response.TransferClanOwnershipResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipRequestProto;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.Builder;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.TransferClanOwnershipStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class TransferClanOwnershipController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	public TransferClanOwnershipController() {
		numAllocatedThreads = 2;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new TransferClanOwnershipRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_TRANSFER_CLAN_OWNERSHIP;
	}

	@Override
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
		TransferClanOwnershipRequestProto reqProto = ((TransferClanOwnershipRequestEvent) event)
				.getTransferClanOwnershipRequestProto();
		log.info("reqProto=" + reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String newClanOwnerId = reqProto.getClanOwnerUuidNew();
		List<String> userIds = new ArrayList<String>();
		//order matters
		userIds.add(userId);
		userIds.add(newClanOwnerId);

		TransferClanOwnershipResponseProto.Builder resBuilder = TransferClanOwnershipResponseProto
				.newBuilder();
		resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = null;
		if (senderProto.hasClan() && null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}

		UUID userUuid = null;
		UUID newClanOwnerUuid = null;
		UUID clanUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			newClanOwnerUuid = UUID.fromString(newClanOwnerId);
			clanUuid = UUID.fromString(clanId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(
					String.format(
							"UUID error. incorrect userId=%s, newClanOwnerId=%s, clanId=%s",
							userId, newClanOwnerId, clanId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
			TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setTransferClanOwnershipResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
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

			User user = users.get(userId);
			User newClanOwner = users.get(newClanOwnerId);

			boolean legitTransfer = checkLegitTransfer(resBuilder, lockedClan,
					userId, user, newClanOwnerId, newClanOwner, userClans);

			if (legitTransfer) {
				List<UserClanStatus> statuses = new ArrayList<UserClanStatus>();
				//order matters
				statuses.add(UserClanStatus.JUNIOR_LEADER);
				statuses.add(UserClanStatus.LEADER);
				writeChangesToDB(clanId, userIds, statuses);
				setResponseBuilderStuff(resBuilder, clanId, newClanOwner);
			}

			if (!legitTransfer) {
				//if not successful write to guy
				TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setTransferClanOwnershipResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			}

			if (legitTransfer) {
				TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(
						senderProto.getUserUuid());
				resEvent.setTag(event.getTag());
				resEvent.setTransferClanOwnershipResponseProto(resBuilder
						.build());
				server.writeClanEvent(resEvent, clanId);

			}

		} catch (Exception e) {
			log.error("exception in TransferClanOwnership processEvent", e);
			try {
				resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
				TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setTransferClanOwnershipResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in TransferClanOwnership processEvent", e);
			}
		} finally {
			if (clanUuid != null && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}

	private boolean checkLegitTransfer(Builder resBuilder, boolean lockedClan,
			String userId, User user, String newClanOwnerId, User newClanOwner,
			Map<String, UserClan> userClans) {

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}

		if (user == null || newClanOwner == null) {
			resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
			log.error("user is " + user + ", new clan owner is " + newClanOwner);
			return false;
		}
		if (user.getClanId() == null) {
			resBuilder
					.setStatus(TransferClanOwnershipStatus.FAIL_NOT_AUTHORIZED);
			log.error("user not in clan. user=" + user);
			return false;
		}

		if (!newClanOwner.getClanId().equals(user.getClanId())) {
			resBuilder
					.setStatus(TransferClanOwnershipStatus.FAIL_NEW_OWNER_NOT_IN_CLAN);
			log.error("new owner not in same clan as user. new owner= "
					+ newClanOwner + ", user is " + user);
			return false;
		}

		if (!userClans.containsKey(userId)
				|| !userClans.containsKey(newClanOwnerId)) {
			log.error("a UserClan does not exist userId=" + userId
					+ ", newClanOwner=" + newClanOwnerId + "\t userClans="
					+ userClans);
			return false;
		}
		UserClan userClan = userClans.get(user.getId());

		String leaderStatusName = UserClanStatus.LEADER.name();

		if (!leaderStatusName.equals(userClan.getStatus())) {
			resBuilder
					.setStatus(TransferClanOwnershipStatus.FAIL_NOT_AUTHORIZED);
			log.error("user is " + user + ", and user isn't owner. user is:"
					+ userClan);
			return false;
		}
		resBuilder.setStatus(TransferClanOwnershipStatus.SUCCESS);
		return true;
	}

	private void writeChangesToDB(String clanId, List<String> userIdList,
			List<UserClanStatus> statuses) {
		//update clan for user table

		int numUpdated = UpdateUtils.get().updateUserClanStatuses(clanId,
				userIdList, statuses);
		log.info(String.format(
				"num clan_for_user updated=%s, userIdList=%s, statuses=%s",
				numUpdated, userIdList, statuses));
	}

	private void setResponseBuilderStuff(Builder resBuilder, String clanId,
			User newClanOwner) {
		Clan clan = getClanRetrieveUtils().getClanWithId(clanId);
		List<String> clanIdList = Collections.singletonList(clanId);

		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		Map<String, Integer> clanIdToSize = getUserClanRetrieveUtils()
				.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);

		resBuilder.setMinClan(createInfoProtoUtils
				.createMinimumClanProtoFromClan(clan));

		int size = clanIdToSize.get(clanId);
		resBuilder.setFullClan(createInfoProtoUtils
				.createFullClanProtoWithClanSize(clan, size));

		MinimumUserProto mup = createInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(newClanOwner, clan);
		resBuilder.setClanOwnerNew(mup);
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
