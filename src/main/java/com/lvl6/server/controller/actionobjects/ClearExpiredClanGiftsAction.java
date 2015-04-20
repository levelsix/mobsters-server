package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanGiftForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventClanProto.ClearExpiredClanGiftsResponseProto.Builder;
import com.lvl6.proto.EventClanProto.ClearExpiredClanGiftsResponseProto.ClearExpiredClanGiftsStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.DeleteUtil;

public class ClearExpiredClanGiftsAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private UserRetrieveUtils2 userRetrieveUtils;
	private DeleteUtil deleteUtil;
	private List<ClanGiftForUser> listOfClanGifts;

	public ClearExpiredClanGiftsAction() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ClearExpiredClanGiftsAction(String userId, UserRetrieveUtils2 userRetrieveUtils,
			DeleteUtil deleteUtil, List<ClanGiftForUser> listOfClanGifts) {
		super();
		this.userId = userId;
		this.userRetrieveUtils = userRetrieveUtils;
		this.deleteUtil = deleteUtil;
		this.listOfClanGifts = listOfClanGifts;
	}

	private User user;

	public void execute(Builder resBuilder) {

		//check out inputs before db interaction
		//		boolean valid = verifySyntax(resBuilder);
		//
		//		if (!valid) {
		//			return;
		//		}
		resBuilder.setStatus(ClearExpiredClanGiftsStatus.FAIL_OTHER);

		boolean valid = verifySemantics();

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB();
		if (!success) {
			return;
		}
		resBuilder.setStatus(ClearExpiredClanGiftsStatus.SUCCESS);

		return;
	}

	private boolean verifySemantics() {
		user = userRetrieveUtils.getUserById(userId);

		if(user == null) {
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB() {

		//delete the rows in clan gifts for user
		boolean success = deleteUtil.deleteFromClanGiftForUser(listOfClanGifts);
		return success;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		ClearExpiredClanGiftsAction.log = log;
	}


	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
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


	public List<ClanGiftForUser> getListOfClanGifts() {
		return listOfClanGifts;
	}


	public void setListOfClanGifts(List<ClanGiftForUser> listOfClanGifts) {
		this.listOfClanGifts = listOfClanGifts;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}





}
