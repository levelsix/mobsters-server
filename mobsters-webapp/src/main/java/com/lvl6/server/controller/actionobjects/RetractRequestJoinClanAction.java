package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component@Scope("prototype")public class RetractRequestJoinClanAction {
	private static Logger log = LoggerFactory.getLogger( RetractRequestJoinClanAction.class);

	private String userId;
	private String clanId;
	private boolean lockedClan;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtils; 
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected DeleteUtil deleteUtil;
	@Autowired protected ClanRetrieveUtils2 clanRetrieveUtils; 
	@Autowired protected UserClanRetrieveUtils2 userClanRetrieveUtils; 

	public RetractRequestJoinClanAction(
			String userId, String clanId,
			boolean lockedClan, UserRetrieveUtils2 userRetrieveUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil,
			ClanRetrieveUtils2 clanRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
		this.clanRetrieveUtils = clanRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	private User user;
	private Clan clan;


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
		if (user.getClanId() != null) {
			resBuilder
			.setStatus(ResponseStatus.FAIL_ALREADY_IN_CLAN);
			log.error("user is already in clan with id {} ", user.getClanId());
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		UserClan uc = userClanRetrieveUtils.getSpecificUserClan(
				user.getId(), clan.getId());
		if (uc == null
				|| !UserClanStatus.REQUESTING.name().equals(uc.getStatus())) {
			resBuilder
			.setStatus(ResponseStatus.FAIL_DID_NOT_REQUEST);
			log.error("user clan request has not been filed");
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		if (!deleteUtil.deleteUserClan(user.getId(), clanId)) {
			log.error("problem with deleting user clan data for user {}, "
					+ "and clan id {}", user, clanId);
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

}
