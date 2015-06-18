package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.clansearch.HazelcastClanSearchImpl;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.BootPlayerFromClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.Builder;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class BootPlayerFromClanAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private String bootedUserId;
	private boolean lockedClan;
	private UserRetrieveUtils2 userRetrieveUtils;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	protected DeleteUtil deleteUtil;
	private TimeUtils timeUtils;
	private ClanRetrieveUtils2 clanRetrieveUtils;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private ClanStuffUtils clanStuffUtils;
	private ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;
	private HazelcastClanSearchImpl hzClanSearch;
	private ClanSearch clanSearch;
	private ServerToggleRetrieveUtils toggle;


	public BootPlayerFromClanAction(
			String userId, String bootedUserId,
			boolean lockedClan, UserRetrieveUtils2 userRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			DeleteUtil deleteUtil, TimeUtils timeUtils,
			ClanRetrieveUtils2 clanRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			ClanStuffUtils clanStuffUtils,
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil,
			HazelcastClanSearchImpl hzClanSearch,
			ClanSearch clanSearch,
			ServerToggleRetrieveUtils toggle) {
		super();
		this.userId = userId;
		this.bootedUserId = bootedUserId;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.deleteUtil = deleteUtil;
		this.timeUtils = timeUtils;
		this.clanRetrieveUtils = clanRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.clanStuffUtils = clanStuffUtils;
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
		this.hzClanSearch = hzClanSearch;
		this.clanSearch = clanSearch;
		this.toggle = toggle;
	}

	private User user;
	private User playerToBoot;
	private List<Integer> clanSizeContainer;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);

		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(BootPlayerFromClanStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		List<String> userIds = new ArrayList<String>();
		userIds.add(userId);
		userIds.add(bootedUserId);
		
		Map<String, User> users = userRetrieveUtils.getUsersByIds(userIds);
		user = users.get(userId);
		playerToBoot = users.get(bootedUserId);
		
		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}

		if (user == null || playerToBoot == null) {
			resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
			log.error("user is {}, playerToBoot is {}", user,
					playerToBoot);
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		String clanId = user.getClanId();
		String leaderStatus = UserClanStatus.LEADER.name();
		String jrLeaderStatus = UserClanStatus.JUNIOR_LEADER.name();
		String memberStatus = UserClanStatus.MEMBER.name();
		String requestingStatus = UserClanStatus.REQUESTING.name();

		List<String> statuses = new ArrayList<String>();
		statuses.add(leaderStatus);
		statuses.add(jrLeaderStatus);
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(memberStatus);
		statuses.add(requestingStatus);
		Map<String, String> userIdsToStatuses = userClanRetrieveUtils
				.getUserIdsToStatuses(clanId, statuses);
		Set<String> uniqUserIds = clanStuffUtils.getAuthorizedClanMembers(user, 
				userClanRetrieveUtils, userIdsToStatuses, leaderStatus, jrLeaderStatus);
		String userId = user.getId();
		if (!uniqUserIds.contains(userId)) {
			resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_NOT_AUTHORIZED);
			log.error("user can't boot player. user= {}, playerToBoot = {}", user, playerToBoot);
			return false;
		}

		//TODO: Consider checking UserClanStatus (userStatus > playerToBootStatus)
		
		String playerToBootClanId = playerToBoot.getClanId();
		if (!playerToBootClanId.equals(user.getClanId())) {
			resBuilder
					.setStatus(BootPlayerFromClanStatus.FAIL_BOOTED_NOT_IN_CLAN);
			log.error("playerToBoot not in user's clan. playerToBoot is in {}, user's clan ={}",
							playerToBootClanId, user.getClanId());
			return false;
		}
		clanSizeContainer = new ArrayList<Integer>();
		clanSizeContainer.add(uniqUserIds.size());
		resBuilder.setStatus(BootPlayerFromClanStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		String clanId = playerToBoot.getClanId();
		if (!deleteUtil.deleteUserClan(bootedUserId, clanId)) {
			log.error("can't delete user clan info for playerToBoot with id={} \t and clanId={}",
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
		ExitClanAction eca = new ExitClanAction(bootedUserId, clanId, clanSizeContainer.size() - 1,
				lastChatPost, timeUtils, updateUtil, hzClanSearch, clanSearch, toggle);
		eca.execute();

		return true;
	}
	

	public User getUser() {
		return user;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isLockedClan() {
		return lockedClan;
	}

	public void setLockedClan(boolean lockedClan) {
		this.lockedClan = lockedClan;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public String getBootedUserId() {
		return bootedUserId;
	}

	public void setBootedUserId(String bootedUserId) {
		this.bootedUserId = bootedUserId;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public ClanStuffUtils getClanStuffUtils() {
		return clanStuffUtils;
	}

	public void setClanStuffUtils(ClanStuffUtils clanStuffUtils) {
		this.clanStuffUtils = clanStuffUtils;
	}

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtil() {
		return clanChatPostRetrieveUtil;
	}

	public void setClanChatPostRetrieveUtil(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil) {
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
	}


	public HazelcastClanSearchImpl getHzClanSearch() {
		return hzClanSearch;
	}

	public void setHzClanSearch(HazelcastClanSearchImpl hzClanSearch) {
		this.hzClanSearch = hzClanSearch;
	}

	public User getPlayerToBoot() {
		return playerToBoot;
	}

	public void setPlayerToBoot(User playerToBoot) {
		this.playerToBoot = playerToBoot;
	}

	public List<Integer> getClanSizeContainer() {
		return clanSizeContainer;
	}

	public void setClanSizeContainer(List<Integer> clanSizeContainer) {
		this.clanSizeContainer = clanSizeContainer;
	}

}
