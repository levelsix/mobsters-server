package com.lvl6.server.controller.actionobjects;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.proto.EventRewardProto.DeleteGiftResponseProto.Builder;
import com.lvl6.proto.EventRewardProto.DeleteGiftResponseProto.DeleteGiftStatus;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component@Scope("prototype")public class DeleteGiftAction {

	private static Logger log = LoggerFactory.getLogger(DeleteGiftAction.class);

	private String userId;
	private Set<String> giftForUserIds;
	private Set<String> giftForTangoUserGfuIds;
	@Autowired protected DeleteUtil deleteUtil; 

	public DeleteGiftAction(String userId, Set<String> giftForUserIds,
			Set<String> giftForTangoUserGfuIds, DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.giftForUserIds = giftForUserIds;
		this.giftForTangoUserGfuIds = giftForTangoUserGfuIds;
		this.deleteUtil = deleteUtil;
	}

	public void execute(Builder resBuilder) {

		//check out inputs before db interaction
		boolean valid = verifySyntax();

		if (!valid) {
			return;
		}

//		boolean valid = verifySemantics();
//		if (!valid) {
//			return;
//		}

		boolean success = writeChangesToDB();
		if (!success) {
			return;
		}
		resBuilder.setStatus(DeleteGiftStatus.SUCCESS);

		return;
	}

	private boolean verifySyntax() {
        if (giftForUserIds.isEmpty()) {
        	log.error("no ids to delete.");
        	return false;
        }

		return true;
	}

//	private boolean verifySemantics() {
//
//		return true;
//	}

	private boolean writeChangesToDB() {
		int numDeleted = deleteUtil.deleteGiftForUser(giftForUserIds);
		log.info("num GiftForUser deleted={}", numDeleted);
		if (!giftForTangoUserGfuIds.isEmpty()) {
			numDeleted = deleteUtil.deleteGiftForTangoUser(giftForTangoUserGfuIds);
			log.info("num GiftForTangoUser deleted={}", numDeleted);
		}

		return true;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		DeleteGiftAction.log = log;
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

}
