package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.User;
import com.lvl6.leaderboards.LeaderBoardImpl;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class UpdateUserStrengthAction {
	private static Logger log = LoggerFactory.getLogger( UpdateUserStrengthAction.class);

	private String userId;
	private long updatedStrength;
	private UserRetrieveUtils2  userRetrieveUtils;
	@Autowired protected UpdateUtil updateUtil; 

	public UpdateUserStrengthAction(String userId,
			long updatedStrength, UserRetrieveUtils2 userRetrieveUtils,
			UpdateUtil updateUtil)	{
		super();
		this.userId = userId;
		this.updatedStrength = updatedStrength;
		this.userRetrieveUtils = userRetrieveUtils;
		this.updateUtil = updateUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class UpdateUserStrengthResource {
	//
	//
	//		public UpdateUserStrengthResource() {
	//
	//		}
	//	}
	//
	//	public UpdateUserStrengthResource execute() {
	//
	//	}

	//derived state
	private User user;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

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
		
		if (user == null) {
			log.error("user is null");
			return false;
		}
		
		if(updatedStrength < 0) {
			log.error("strength is negative for userId: {}", userId);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		boolean success = updateUtil.updateUserStrength(userId, updatedStrength);
		log.info("successful update of user strength: {}", success );
		user.setTotalStrength(updatedStrength);
		return success;
	}
	
	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		UpdateUserStrengthAction.log = log;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getUpdatedStrength() {
		return updatedStrength;
	}

	public void setUpdatedStrength(long updatedStrength) {
		this.updatedStrength = updatedStrength;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}
