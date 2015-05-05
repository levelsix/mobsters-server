package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.ApproveOrRejectRequestToJoinClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.Builder;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class ApproveOrRejectRequestToJoinAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private String requesterId;
	private boolean accept;
	private boolean lockedClan;
	private UserRetrieveUtils2 userRetrieveUtils;
	protected UpdateUtil updateUtil;
	protected DeleteUtil deleteUtil;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;


	public ApproveOrRejectRequestToJoinAction(String userId, String requesterId,
			boolean accept, boolean lockedClan, UserRetrieveUtils2 userRetrieveUtils, 
			UpdateUtil updateUtil, DeleteUtil deleteUtil, 
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		super();
		this.userId = userId;
		this.requesterId = requesterId;
		this.accept = accept;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtils;
		this.updateUtil = updateUtil;
		this.deleteUtil = deleteUtil;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	private User user;
	private User requester;
	private Clan clan;
	private String clanId;
	private String leaderStatus;
	private String jrLeaderStatus;
	private String memberStatus;
	private String requestingStatus;
	private List<Integer> clanSizeList;
	private Map<String, String> userIdsToStatuses;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);

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

		resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		Set<String> userIds = new HashSet<String>();
		userIds.add(userId);
		userIds.add(requesterId);
		Map<String, User> idsToUsers = userRetrieveUtils
				.getUsersByIds(userIds);

		if (idsToUsers.containsKey(userId)) {
			user = idsToUsers.get(userId);
		}
		if (idsToUsers.containsKey(requesterId)) {
			requester = idsToUsers.get(requesterId);
		}
		if (!lockedClan) {
			log.error("could not get lock for clan.");
			return false;
		}
		if (user == null || requester == null) {
			resBuilder
			.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
			log.error(String.format("user is %s, requester is %s", user,
					requester));
			return false;
		}
		log.info("got here");
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		Set<String> uniqUserIds = getAuthorizedClanMembers();

		String userId = user.getId();
		if (!uniqUserIds.contains(userId)) {
			resBuilder
			.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_NOT_AUTHORIZED);
			log.error(
					"clan member can't approve clan join request. member={}, requester={}",
					user, requester);
			return false;
		}
		//check if requester is already in a clan
		if (requester.getClanId() != null && !requester.getClanId().isEmpty()) {
			resBuilder
			.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_ALREADY_IN_A_CLAN);
			log.error("trying to accept a user that is already in a clan");
			//the other requests in user_clans table that have a status of 2 (requesting to join clan)
			//are deleted later on in writeChangesToDB
			return false;
		}

		//default not REQUESTING STATUS to stop processing if something's wrong
		String requesterStatus = getRequesterStatus(requester, memberStatus,
				userIdsToStatuses);
		if (!requestingStatus.equals(requesterStatus)) {
			resBuilder
			.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_NOT_A_REQUESTER);
			log.error("requester has not requested for clan with id {}", clanId);
			return false;
		}
		//check out the size of the clan
		int size = calculateClanSize(userIdsToStatuses);
		int maxSize = ControllerConstants.CLAN__MAX_NUM_MEMBERS;
		if (size >= maxSize && accept) {
			resBuilder
			.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_CLAN_IS_FULL);
			log.warn(String.format(
					"trying to add user into already full clan with id %s",
					user.getClanId()));
			return false;
		}
		clanSizeList = new ArrayList<Integer>();
		clanSizeList.add(size);
		return true;
	}

	private Set<String> getAuthorizedClanMembers() {
		clanId = user.getClanId();
		leaderStatus = UserClanStatus.LEADER.name();
		jrLeaderStatus = UserClanStatus.JUNIOR_LEADER.name();
		memberStatus = UserClanStatus.MEMBER.name();
		requestingStatus = UserClanStatus.REQUESTING.name();

		List<String> statuses = new ArrayList<String>();
		statuses.add(leaderStatus);
		statuses.add(jrLeaderStatus);
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(memberStatus);
		statuses.add(requestingStatus);
		userIdsToStatuses = userClanRetrieveUtils
				.getUserIdsToStatuses(clanId, statuses);

		Set<String> uniqUserIds = new HashSet<String>();
		if (null != userIdsToStatuses && !userIdsToStatuses.isEmpty()) {

			//gather up only the leader or jr leader userIds
			for (String userId : userIdsToStatuses.keySet()) {
				String status = userIdsToStatuses.get(userId);
				if (leaderStatus.equals(status)
						|| jrLeaderStatus.equals(status)) {
					uniqUserIds.add(userId);
				}
			}
		}
		return uniqUserIds;
	}

	private String getRequesterStatus(User requester, String memberStatus,
			Map<String, String> userIdsAndStatuses) {
		String retVal = memberStatus;

		String requesterId = requester.getId();
		if (userIdsAndStatuses.containsKey(requesterId)) {
			retVal = userIdsAndStatuses.get(requesterId);
		}

		return retVal;
	}

	private int calculateClanSize(Map<String, String> userIdsToStatuses) {
		int clanSize = 0;
		if (null == userIdsToStatuses || userIdsToStatuses.isEmpty()) {
			return clanSize;
		}

		//do not count requesting members
		String requestingStatus = UserClanStatus.REQUESTING.name();
		for (String status : userIdsToStatuses.values()) {
			if (!requestingStatus.equalsIgnoreCase(status)) {
				clanSize++;
			}
		}

		return clanSize;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		if (accept) {
			if (!requester.updateRelativeCoinsAbsoluteClan(0, user.getClanId())) {
				log.error("problem with change requester {} clan id to {}",
						requester, user.getClanId());
				return false;
			}
			if (!updateUtil.updateUserClanStatus(requester.getId(),
					user.getClanId(), UserClanStatus.MEMBER)) {
				log.error("problem updating user clan status to MEMBER for requester {} and clan id {}",
								requester, user.getClanId());
				return false;
			}
			deleteUtil.deleteUserClansForUserExceptSpecificClan(
					requester.getId(), user.getClanId());
			return true;
		} else {
			if (!deleteUtil.deleteUserClan(requester.getId(),
					user.getClanId())) {
				log.error("problem deleting UserClan for requesterId {}, and clan id {}", 
						requester.getId(), user.getClanId());
				return false;
			}
			return true;
		}
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

	public String getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(String requesterId) {
		this.requesterId = requesterId;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
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

	


}
