package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component@Scope("prototype")public class RemoveUserItemUsedAction {
	private static Logger log = LoggerFactory.getLogger( RemoveUserItemUsedAction.class);

	private String userId;
	private List<String> userItemUsedIdList;
	@Autowired protected DeleteUtil deleteUtil; 

	public RemoveUserItemUsedAction(String userId,
			List<String> userItemUsedIdList, DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.userItemUsedIdList = userItemUsedIdList;
		this.deleteUtil = deleteUtil;
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

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
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

		if (userItemUsedIdList.isEmpty()) {
			log.error(String.format("invalid userItemUsedIdList=%s",
					userItemUsedIdList));
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		//record that user used items
		log.info(String.format("deleting ids: %s", userItemUsedIdList));
		int numDeleted = deleteUtil.deleteItemUsed(userId, userItemUsedIdList);

		log.info(String.format("num ids deleted: %s", numDeleted));

		return true;
	}

}
