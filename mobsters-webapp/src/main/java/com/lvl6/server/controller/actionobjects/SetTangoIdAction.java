package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.User;
import com.lvl6.proto.EventUserProto.SetTangoIdResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;

@Component@Scope("prototype")public class SetTangoIdAction {
	private static Logger log = LoggerFactory.getLogger( SetTangoIdAction.class);

	private String userId;
	private String tangoId;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 

	public SetTangoIdAction(String userId,
			String tangoId, UserRetrieveUtils2 userRetrieveUtil) {
		super();
		this.userId = userId;
		this.tangoId = tangoId;
		this.userRetrieveUtil = userRetrieveUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RemoveUserItemUsedResource {
	//
	//
	//		public RemoveUserItemUsedResource() {
	//
	//		}
	//	}
	//
	//	public RemoveUserItemUsedResource execute() {
	//
	//	}

	//derived state
	protected User user;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}
//
		boolean valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);

	}

//	private boolean verifySyntax(Builder resBuilder) {
//		return true;
//	}
//
	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error("no user with id={}", userId);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		try {
			if (!user.updateTangoId(tangoId)) {
				log.error("problem with setting user's TangoId to {}",
						tangoId);
			}
			return true;
		} catch (Exception e) {
			log.error(String.format(
					"problem with updating user TangoId. user=%s \t tangoId=%s",
					user,  tangoId), e);
		}

		return false;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
