package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.MiniEventGoal;
import com.lvl6.info.MiniEventGoalForUser;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto.Builder;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto.UpdateMiniEventStatus;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component@Scope("prototype")public class UpdateMiniEventAction {
	private static Logger log = LoggerFactory.getLogger( UpdateMiniEventAction.class);

	private String userId;
	private List<MiniEventGoalForUser> megfuList;
	@Autowired protected InsertUtil insertUtil; 
	@Autowired protected MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils; 

	public UpdateMiniEventAction(String userId,
			List<MiniEventGoalForUser> megfuList,
			InsertUtil insertUtil,
			MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils)
	{
		super();
		this.userId = userId;
		this.megfuList = megfuList;
		this.insertUtil = insertUtil;
		this.miniEventGoalRetrieveUtils = miniEventGoalRetrieveUtils;
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

			MiniEventGoal meg = miniEventGoalRetrieveUtils
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
