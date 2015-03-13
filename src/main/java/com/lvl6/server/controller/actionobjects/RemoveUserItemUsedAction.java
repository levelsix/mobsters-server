package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto.Builder;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto.RemoveUserItemUsedStatus;
import com.lvl6.utils.utilmethods.DeleteUtil;

public class RemoveUserItemUsedAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<String> userItemUsedIdList;
	private DeleteUtil deleteUtil;

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
		resBuilder.setStatus(RemoveUserItemUsedStatus.FAIL_OTHER);

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

		resBuilder.setStatus(RemoveUserItemUsedStatus.SUCCESS);

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
