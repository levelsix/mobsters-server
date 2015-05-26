package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.TransferClanOwnershipStatus;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.Builder;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class TransferClanOwnershipAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private String newClanOwnerId;
	private boolean lockedClan;
	private UserRetrieveUtils2 userRetrieveUtils;
	protected UpdateUtil updateUtil;
	protected DeleteUtil deleteUtil;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;


	public TransferClanOwnershipAction(String userId, String newClanOwnerId,
			boolean lockedClan, UserRetrieveUtils2 userRetrieveUtils, 
			UpdateUtil updateUtil, DeleteUtil deleteUtil, 
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		super();
		this.userId = userId;
		this.newClanOwnerId = newClanOwnerId;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtils;
		this.updateUtil = updateUtil;
		this.deleteUtil = deleteUtil;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	private User user;
	private User newClanOwner;
	private List<String> userIds;
	private Clan clan;
	private Map<String, UserClan> userClans;
	private String clanId;
	private String leaderStatus;
	private String jrLeaderStatus;
	private String memberStatus;
	private String requestingStatus;
	private List<Integer> clanSizeList;
	private Map<String, String> userIdsToStatuses;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);

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

		resBuilder.setStatus(TransferClanOwnershipStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		userIds = new ArrayList<String>();
		//order matters
		userIds.add(userId);
		userIds.add(newClanOwnerId);
		
		Map<String, User> users = getUserRetrieveUtils().getUsersByIds(
				userIds);
		user = users.get(userId);
		newClanOwner = users.get(newClanOwnerId);
		clanId = user.getClanId();
		userClans = getUserClanRetrieveUtils()
				.getUserClanForUsers(clanId, userIds);
		
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
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
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

	private boolean writeChangesToDB(Builder resBuilder) {
		//update clan for user table
		List<UserClanStatus> statuses = new ArrayList<UserClanStatus>();
		//order matters
		statuses.add(UserClanStatus.JUNIOR_LEADER);
		statuses.add(UserClanStatus.LEADER);
		
		int numUpdated = updateUtil.updateUserClanStatuses(clanId,
				userIds, statuses);
		log.info("num clan_for_user updated={}, userIdList={}, statuses={}",
				new Object [] {numUpdated, userIds, statuses});
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

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isLockedClan() {
		return lockedClan;
	}

	public void setLockedClan(boolean lockedClan) {
		this.lockedClan = lockedClan;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public Clan getClan() {
		return clan;
	}

	public void setClan(Clan clan) {
		this.clan = clan;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
	}

	public String getLeaderStatus() {
		return leaderStatus;
	}

	public void setLeaderStatus(String leaderStatus) {
		this.leaderStatus = leaderStatus;
	}

	public String getJrLeaderStatus() {
		return jrLeaderStatus;
	}

	public void setJrLeaderStatus(String jrLeaderStatus) {
		this.jrLeaderStatus = jrLeaderStatus;
	}

	public String getMemberStatus() {
		return memberStatus;
	}

	public void setMemberStatus(String memberStatus) {
		this.memberStatus = memberStatus;
	}

	public String getRequestingStatus() {
		return requestingStatus;
	}

	public void setRequestingStatus(String requestingStatus) {
		this.requestingStatus = requestingStatus;
	}

	public List<Integer> getClanSizeList() {
		return clanSizeList;
	}

	public void setClanSizeList(List<Integer> clanSizeList) {
		this.clanSizeList = clanSizeList;
	}

	public Map<String, String> getUserIdsToStatuses() {
		return userIdsToStatuses;
	}

	public void setUserIdsToStatuses(Map<String, String> userIdsToStatuses) {
		this.userIdsToStatuses = userIdsToStatuses;
	}

	public String getNewClanOwnerId() {
		return newClanOwnerId;
	}

	public void setNewClanOwnerId(String newClanOwnerId) {
		this.newClanOwnerId = newClanOwnerId;
	}

	public User getNewClanOwner() {
		return newClanOwner;
	}

	public void setNewClanOwner(User newClanOwner) {
		this.newClanOwner = newClanOwner;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public Map<String, UserClan> getUserClans() {
		return userClans;
	}

	public void setUserClans(Map<String, UserClan> userClans) {
		this.userClans = userClans;
	}
	
	
}
