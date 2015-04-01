package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.MiniEventGoal;
import com.lvl6.info.MiniEventGoalForUser;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto.Builder;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto.UpdateMiniEventStatus;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

public class UpdateMiniEventAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<MiniEventGoalForUser> megfuList;
	private InsertUtil insertUtil;

	public UpdateMiniEventAction(String userId,
			List<MiniEventGoalForUser> megfuList,
			InsertUtil insertUtil)
	{
		super();
		this.userId = userId;
		this.megfuList = megfuList;
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

		resBuilder.setStatus(UpdateMiniEventStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (megfuList.isEmpty()) {
			log.error("client didn't send any MiniEventGoalForUsers to be updated");
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		for(MiniEventGoalForUser megfu : megfuList) {
			int miniEventGoalId = megfu.getMiniEventGoalId();

			MiniEventGoal meg = MiniEventGoalRetrieveUtils
					.getMiniEventGoalById(miniEventGoalId);
			if (null == meg) {
				log.error("nonexistent MiniEventGoalForUser: {}, ", megfu);
				return false;
			}
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		boolean success = insertUtil.insertIntoUpdateMiniEventGoalForUser(megfuList);
		log.info("successful update: {}. {}", success, megfuList);
		return success;
	}

}
