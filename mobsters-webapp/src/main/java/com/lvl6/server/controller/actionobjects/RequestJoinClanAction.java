package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
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
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component@Scope("prototype")public class RequestJoinClanAction {
	private static Logger log = LoggerFactory.getLogger( RequestJoinClanAction.class);

	private String userId;
	private String clanId;
	private Timestamp clientTime;
	private boolean lockedClan;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtils; 
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected DeleteUtil deleteUtil;
	@Autowired protected ClanRetrieveUtils2 clanRetrieveUtils; 
	@Autowired protected UserClanRetrieveUtils2 userClanRetrieveUtils; 

	public RequestJoinClanAction(
			String userId, String clanId, Timestamp clientTime,
			boolean lockedClan, UserRetrieveUtils2 userRetrieveUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil,
			ClanRetrieveUtils2 clanRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.clientTime = clientTime;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
		this.clanRetrieveUtils = clanRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	private User user;
	private Clan clan;
	private List<Integer> clanSizeList;
	private boolean requestToJoinRequired;


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

		if (requestToJoinRequired) {
			resBuilder.setStatus(ResponseStatus.SUCCESS_REQUEST);
		}
		else {
			resBuilder.setStatus(ResponseStatus.SUCCESS_JOIN);
		}
	}

	private boolean verifySyntax(Builder resBuilder) {
		user = userRetrieveUtils.getUserById(userId);
		clan = clanRetrieveUtils.getClanWithId(clanId);
		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}
		if (user == null || clan == null) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			log.error("user is {}, clan is {}", user, clan);
			return false;
		}
		String userClanId = user.getClanId();
		if (userClanId != null) {
			resBuilder.setStatus(ResponseStatus.FAIL_ALREADY_IN_CLAN);
			log.error("user is already in clan with id {}",
					userClanId);
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		statuses.add(UserClanStatus.REQUESTING.name());
		Map<String, String> userIdsToStatuses = userClanRetrieveUtils
				.getUserIdsToStatuses(clanId, statuses);

		if (null != userIdsToStatuses && userIdsToStatuses.containsKey(userId)) {
			resBuilder
			.setStatus(ResponseStatus.FAIL_REQUEST_ALREADY_FILED);
			log.error("user clan already exists for this: {}",
					userIdsToStatuses.get(userId));
			return false;
		}

		if (ControllerConstants.CLAN__CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT
				.equals(clanId)) {
			return true;
		}

		//can request as much as desired
		if (clan.isRequestToJoinRequired()) {
			return true;
		}

		//check out the size of the clan since user can just join
		int size = calculateClanSize(userIdsToStatuses);
		int maxSize = ControllerConstants.CLAN__MAX_NUM_MEMBERS;
		if (size >= maxSize) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLAN_IS_FULL);
			log.warn("trying to join full clan with id {}, cur size={}", clanId,
					maxSize);
			return false;
		}
		clanSizeList = new ArrayList<Integer>();
		clanSizeList.add(size);
		//resBuilder.setStatus(RequestJoinClanStatus.SUCCESS);
		return true;
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
		requestToJoinRequired = clan.isRequestToJoinRequired();
		String userClanStatus;
		if (requestToJoinRequired) {
			userClanStatus = UserClanStatus.REQUESTING.name();
		} 
		else {
			userClanStatus = UserClanStatus.MEMBER.name();
		}

		log.info("inserting user clan");
		if (!insertUtil.insertUserClan(userId, clanId, userClanStatus,
				clientTime)) {
			log.error("problem inserting user clan data for user={}, and clan id {}",
							user, clanId);
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			return false;
		}

		boolean deleteUserClanInserted = false;
		//update user to reflect he joined clan if the clan does not require a request to join
		if (!requestToJoinRequired) {
			if (!user.updateRelativeCoinsAbsoluteClan(0, clanId)) {
				//could not change clan_id for user
				String preface = "could not change clanId for";
				String postface = "Deleting user clan that was just created.";
				log.error("{} requester {} to {}. {}", new Object[] {preface,
						user, clanId, postface});
				deleteUserClanInserted = true;
			} else {
				//successfully changed clan_id in current user
				//get rid of all other join clan requests
				//don't know if this next line will always work...
				deleteUtil.deleteUserClansForUserExceptSpecificClan(
						userId, clanId);
			}
		}

		boolean successful = true;
		//in case things above didn't work out
		if (deleteUserClanInserted) {
			if (!deleteUtil.deleteUserClan(userId, clanId)) {
				log.error("unexpected error: could not delete user clan inserted.");
			}
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			successful = false;
		}
		return successful;
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

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getClientTime() {
		return clientTime;
	}

	public void setClientTime(Timestamp clientTime) {
		this.clientTime = clientTime;
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

	public Clan getClan() {
		return clan;
	}

	public void setClan(Clan clan) {
		this.clan = clan;
	}

	public List<Integer> getClanSizeList() {
		return clanSizeList;
	}

	public void setClanSizeList(List<Integer> clanSizeList) {
		this.clanSizeList = clanSizeList;
	}

	public boolean isRequestToJoinRequired() {
		return requestToJoinRequired;
	}

	public void setRequestToJoinRequired(boolean requestToJoinRequired) {
		this.requestToJoinRequired = requestToJoinRequired;
	}
	

}
