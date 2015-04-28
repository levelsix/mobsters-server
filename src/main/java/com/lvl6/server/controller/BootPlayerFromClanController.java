package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BootPlayerFromClanRequestEvent;
import com.lvl6.events.response.BootPlayerFromClanResponseEvent;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.BootPlayerFromClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.ExitClanAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class BootPlayerFromClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtil;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;

	@Autowired
	protected ClanSearch clanSearch;

	public BootPlayerFromClanController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new BootPlayerFromClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BOOT_PLAYER_FROM_CLAN_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		BootPlayerFromClanRequestProto reqProto = ((BootPlayerFromClanRequestEvent) event)
				.getBootPlayerFromClanRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String playerToBootId = reqProto.getPlayerToBootUuid();

		BootPlayerFromClanResponseProto.Builder resBuilder = BootPlayerFromClanResponseProto
				.newBuilder();
		resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = "";

		UUID userUuid = null;
		UUID playerToBootUuid = null;
		UUID clanUuid = null;

		boolean invalidUuids = true;
		if (senderProto.hasClan() && null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
			try {
				userUuid = UUID.fromString(userId);
				playerToBootUuid = UUID.fromString(playerToBootId);

				clanUuid = UUID.fromString(clanId);
				invalidUuids = false;
			} catch (Exception e) {
				log.error(
						String.format(
								"UUID error. incorrect userId=%s, playerToBootId=%s clanId=%s",
								userId, playerToBootId, clanId), e);
			}
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
			BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setBootPlayerFromClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		List<String> userIds = new ArrayList<String>();
		userIds.add(userId);
		userIds.add(playerToBootId);

		boolean lockedClan = false;
		lockedClan = getLocker().lockClan(clanUuid);
		try {
			Map<String, User> users = RetrieveUtils.userRetrieveUtils()
					.getUsersByIds(userIds);
			User user = users.get(userId);
			User playerToBoot = users.get(playerToBootId);

			List<Integer> clanSizeContainer = new ArrayList<Integer>();
			boolean legitBoot = checkLegitBoot(resBuilder, lockedClan, user,
					playerToBoot, clanSizeContainer);

			boolean success = false;
			if (legitBoot) {
				int clanSize = clanSizeContainer.get(0);
				success = writeChangesToDB(user, playerToBoot, clanSize);
			}

			if (success) {
				MinimumUserProto mup = CreateInfoProtoUtils
						.createMinimumUserProtoFromUserAndClan(playerToBoot,
								null);
				resBuilder.setPlayerToBoot(mup);
			}

			BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setBootPlayerFromClanResponseProto(resBuilder.build());

			if (success) {
				//if successful write to clan
				server.writeClanEvent(resEvent, clanId);
			} else {
				//write to user if fail
				server.writeEvent(resEvent);
			}
		} catch (Exception e) {
			log.error("exception in BootPlayerFromClan processEvent", e);
			try {
				resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
				BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setBootPlayerFromClanResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in BootPlayerFromClan processEvent", e);
			}
		} finally {
			if (null != clanUuid && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}

	private boolean checkLegitBoot(Builder resBuilder, boolean lockedClan,
			User user, User playerToBoot, List<Integer> clanSizeContainer) {

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}

		if (user == null || playerToBoot == null) {
			resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
			log.error(String.format("user is %s, playerToBoot is %s", user,
					playerToBoot));
			return false;
		}

		String clanId = user.getClanId();
		String leaderStatus = UserClanStatus.LEADER.name();
		String jrLeaderStatus = UserClanStatus.JUNIOR_LEADER.name();

		List<String> statuses = new ArrayList<String>();
		statuses.add(leaderStatus);
		statuses.add(jrLeaderStatus);
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		Map<String, String> userIdsToStatuses = userClanRetrieveUtils
				.getUserIdsToStatuses(clanId, statuses);

		Set<String> uniqUserIds = getAuthorizedClanMembers(leaderStatus,
				jrLeaderStatus, userIdsToStatuses);

		String userId = user.getId();
		if (!uniqUserIds.contains(userId)) {
			resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_NOT_AUTHORIZED);
			log.error("user can't boot player. user=" + user
					+ "\t playerToBoot=" + playerToBoot);
			return false;
		}

		//TODO: Consider checking UserClanStatus (userStatus > playerToBootStatus)

		String playerToBootClanId = playerToBoot.getClanId();
		if (!playerToBootClanId.equals(user.getClanId())) {
			resBuilder
					.setStatus(BootPlayerFromClanStatus.FAIL_BOOTED_NOT_IN_CLAN);
			log.error(String
					.format("playerToBoot not in user's clan. playerToBoot is in %s, user's clan %s",
							playerToBootClanId, user.getClanId()));
			return false;
		}

		clanSizeContainer.add(userIdsToStatuses.size());
		resBuilder.setStatus(BootPlayerFromClanStatus.SUCCESS);
		return true;
	}

	private Set<String> getAuthorizedClanMembers(String leaderStatus,
			String jrLeaderStatus, Map<String, String> userIdsAndStatuses) {
		Set<String> uniqUserIds = new HashSet<String>();
		if (null != userIdsAndStatuses && !userIdsAndStatuses.isEmpty()) {

			//gather up only the leader or jr leader userIds
			for (String userId : userIdsAndStatuses.keySet()) {
				String status = userIdsAndStatuses.get(userId);
				if (leaderStatus.equals(status)
						|| jrLeaderStatus.equals(status)) {
					uniqUserIds.add(userId);
				}
			}
		}

		return uniqUserIds;
	}

	private boolean writeChangesToDB(User user, User playerToBoot, int clanSize) {
		String userId = playerToBoot.getId();
		String clanId = playerToBoot.getClanId();
		if (!DeleteUtils.get().deleteUserClan(userId, clanId)) {
			log.error(
					"can't delete user clan info for playerToBoot with id={} \t and clanId={}",
					playerToBoot.getId(), playerToBoot.getClanId());

			return false;
		}
		if (!playerToBoot.updateRelativeCoinsAbsoluteClan(0, null)) {
			log.error("can't change playerToBoot={} clan id to nothing",
					playerToBoot);

			return false;
		}

		Date lastChatPost = clanChatPostRetrieveUtil.getLastChatPost(clanId);

		if (null == lastChatPost) {
			//for the clans that have not chatted at all
			lastChatPost = ControllerConstants.INCEPTION_DATE;
		}

		//need to account for this user leaving clan
		ExitClanAction eca = new ExitClanAction(userId, clanId, clanSize - 1,
				lastChatPost, timeUtil, UpdateUtils.get(), clanSearch);
		eca.execute();

		return true;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtil() {
		return timeUtil;
	}

	public void setTimeUtil(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtil() {
		return clanChatPostRetrieveUtil;
	}

	public void setClanChatPostRetrieveUtil(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil) {
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
	}

	public ClanSearch getClanSearch() {
		return clanSearch;
	}

	public void setClanSearch(ClanSearch clanSearch) {
		this.clanSearch = clanSearch;
	}

}
