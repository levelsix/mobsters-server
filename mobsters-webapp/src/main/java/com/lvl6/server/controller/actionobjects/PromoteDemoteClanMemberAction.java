package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class PromoteDemoteClanMemberAction {
	private static Logger log = LoggerFactory.getLogger( PromoteDemoteClanMemberAction.class);

	private String userId;
	private String victimId;
	private UserClanStatus newUserClanStatus;
	private boolean lockedClan;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtils; 
	@Autowired protected UpdateUtil updateUtil;
	@Autowired protected DeleteUtil deleteUtil;
	@Autowired protected UserClanRetrieveUtils2 userClanRetrieveUtils; 
	@Autowired protected ClanStuffUtils clanStuffUtils; 


	public PromoteDemoteClanMemberAction(String userId, String victimId,
			UserClanStatus newUserClanStatus, boolean lockedClan, 
			UserRetrieveUtils2 userRetrieveUtils, UpdateUtil updateUtil, 
			DeleteUtil deleteUtil, 
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			ClanStuffUtils clanStuffUtils) {
		super();
		this.userId = userId;
		this.victimId = victimId;
		this.newUserClanStatus = newUserClanStatus;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtils;
		this.updateUtil = updateUtil;
		this.deleteUtil = deleteUtil;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.clanStuffUtils = clanStuffUtils;
	}

	private User user;
	private User victimUser;
	private List<String> userIds;
	private Clan clan;
	private Map<String, UserClan> userClans;
	private Map<String, User> users;
	private String clanId;
	private String leaderStatus;
	private String jrLeaderStatus;
	private String memberStatus;
	private String requestingStatus;
	private List<Integer> clanSizeList;
	private Map<String, String> userIdsToStatuses;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

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

		resBuilder.setStatus(ResponseStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		userIds = new ArrayList<String>();
		//order matters
		userIds.add(userId);
		userIds.add(victimId);

		users = getUserRetrieveUtils().getUsersByIds(
				userIds);
		user = users.get(userId);
		victimUser = users.get(victimId);
		clanId = user.getClanId();
		userClans = getUserClanRetrieveUtils()
				.getUserClanForUsers(clanId, userIds);

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}
		if (null == users || users.size() != 2
				|| null == userClans || userClans.size() != 2) {
			log.error("user or userClan objects do not total 2. users= {}, "
					+ "userIdsToUserClans = {}", users, userClans);
			return false;
		}

		//check if users are in the db
		if (!userClans.containsKey(userId)
				|| !userClans.containsKey(userId)) {
			log.error("user promoting or demoting not in clan or db. userId= {}, "
					+ "userIdsToUserClans = {}, userIdsToUsers = {}", 
					new Object[] {userId, userClans, users});
			resBuilder
			.setStatus(ResponseStatus.FAIL_NOT_IN_CLAN);
			return false;
		}
		if (!userClans.containsKey(victimId)
				|| !userClans.containsKey(victimId)) {
			log.error("user to be promoted or demoted not in clan or db. victimId= {}, "
					+ "userIdsToUserClans = {}, userIdsToUsers = {}", 
					new Object[] {userId, userClans, users});
			resBuilder
			.setStatus(ResponseStatus.FAIL_NOT_IN_CLAN);
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		//check if user can demote/promote the other one
		UserClan promoterDemoter = userClans.get(userId);
		UserClan victim = userClans.get(victimId);

		UserClanStatus first = UserClanStatus.valueOf(promoterDemoter
				.getStatus());
		UserClanStatus second = UserClanStatus.valueOf(victim.getStatus());
		if (UserClanStatus.CAPTAIN.equals(first)
				|| !clanStuffUtils
				.firstUserClanStatusAboveSecond(first, second)) {
			log.error("user not authorized to promote or demote otherUser. clanStatus of user = {},"
					+ " clanStatus of other user = {}", first, second);
			resBuilder
			.setStatus(ResponseStatus.FAIL_NOT_AUTHORIZED);
			return false;
		}
		if (!clanStuffUtils.firstUserClanStatusAboveSecond(first,
				newUserClanStatus)) {
			log.error("user not authorized to promote or demote otherUser. clanStatus of user = {}, "
					+ "clanStatus of other user = {}, newClanStatus of other user = {}", 
					new Object[] {first, second, newUserClanStatus});
			resBuilder
			.setStatus(ResponseStatus.FAIL_NOT_AUTHORIZED);
			return false;
		}
		if (UserClanStatus.REQUESTING.equals(second)) {
			log.error("user can't promote, demote a non-clan member. UserClan for user = {}, "
					+ "UserClan for victim = {}, users = {}", new Object[] 
							{promoterDemoter, victim, users});
			resBuilder
			.setStatus(ResponseStatus.FAIL_NOT_AUTHORIZED);
			return false;
		}
		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		UserClan oldInfo = userClans.get(victimId);
		UserClanStatus ucs = UserClanStatus.valueOf(oldInfo
				.getStatus());
		resBuilder.setPrevUserClanStatus(ucs);
		
		if (!updateUtil.updateUserClanStatus(victimId, clanId,
				newUserClanStatus)) {
			log.error("problem with updating user clan status for user = {}, oldInfo = {}, "
					+ "newUserClanStatus = {}", new Object [] {victimUser, oldInfo,
							newUserClanStatus});
			return false;
		}
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

	public String getVictimId() {
		return victimId;
	}

	public void setVictimId(String victimId) {
		this.victimId = victimId;
	}

	public UserClanStatus getNewUserClanStatus() {
		return newUserClanStatus;
	}

	public void setNewUserClanStatus(UserClanStatus newUserClanStatus) {
		this.newUserClanStatus = newUserClanStatus;
	}

	public ClanStuffUtils getClanStuffUtils() {
		return clanStuffUtils;
	}

	public void setClanStuffUtils(ClanStuffUtils clanStuffUtils) {
		this.clanStuffUtils = clanStuffUtils;
	}

	public User getVictimUser() {
		return victimUser;
	}

	public void setVictimUser(User victimUser) {
		this.victimUser = victimUser;
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}


}
