package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.MiniEventForUser;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto.Builder;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto.UpdateMiniEventStatus;
import com.lvl6.utils.utilmethods.InsertUtil;

public class UpdateMiniEventAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private MiniEventForUser mefu;
	private InsertUtil insertUtil;

	public UpdateMiniEventAction(String userId,
			MiniEventForUser mefu,
			InsertUtil insertUtil)
	{
		super();
		this.userId = userId;
		this.mefu = mefu;
		this.insertUtil = insertUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class UpdateMiniEventResource {
	//
	//
	//		public UpdateMiniEventResource() {
	//
	//		}
	//	}
	//
	//	public UpdateMiniEventResource execute() {
	//
	//	}

	//derived state

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(UpdateMiniEventStatus.FAIL_OTHER);

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}
//
//		valid = verifySemantics(resBuilder);
//
//		if (!valid) {
//			return;
//		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(UpdateMiniEventStatus.SUCCESS);

	}

//	private boolean verifySyntax(Builder resBuilder) {
//
//		return true;
//	}
//
//	private boolean verifySemantics(Builder resBuilder) {
//
//		return true;
//	}

	private boolean writeChangesToDB(Builder resBuilder) {

		boolean success = insertUtil.insertIntoUpdateMiniEventForUser(mefu);
		log.info("successful update: {}. {}", success, mefu);
		return success;
	}

}
