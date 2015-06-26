package com.lvl6.server.controller.actionobjects;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.proto.EventStructureProto.DestroyMoneyTreeStructureResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component@Scope("prototype")public class DestroyMoneyTreeStructureAction {
	private static Logger log = LoggerFactory.getLogger( DestroyMoneyTreeStructureAction.class);

	private String userId;
	private List<String> userStructIdsList;
	private Date now;
	private StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2;
	@Autowired protected StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils; 
	@Autowired protected DeleteUtil deleteUtil;

	public DestroyMoneyTreeStructureAction(String userId,
			List<String> userStructIdsList, Date now,
			StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2,
			StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils,
			DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.userStructIdsList = userStructIdsList;
		this.now = now;
		this.structureForUserRetrieveUtils2 = structureForUserRetrieveUtils2;
		this.structureMoneyTreeRetrieveUtils = structureMoneyTreeRetrieveUtils;
		this.deleteUtil = deleteUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class DestroyMoneyTreeStructureResource {
	//		
	//		
	//		public DestroyMoneyTreeStructureResource() {
	//			
	//		}
	//	}
	//
	//	public DestroyMoneyTreeStructureResource execute() {
	//		
	//	}

	//derived state
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = false;

		valid = verifySemantics(resBuilder);

		if (!valid) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_NOT_EXPIRED_YET);
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);

	}

	private boolean verifySemantics(Builder resBuilder) {
		boolean success = false;
		List<StructureForUser> sfuList = structureForUserRetrieveUtils2
				.getSpecificOrAllUserStructsForUser(userId, userStructIdsList);

		for (StructureForUser sfu : sfuList) {
			StructureMoneyTree smt = structureMoneyTreeRetrieveUtils
					.getMoneyTreeForStructId(sfu.getStructId());

			int millisecondsConvertingToOneDayConstant = 1000 * 60 * 60 * 24;
			Date purchaseTime = sfu.getPurchaseTime();

			int timeOfDuration = (int) ((now.getTime() - purchaseTime.getTime()) / (millisecondsConvertingToOneDayConstant * smt
					.getDaysOfDuration()));

			if (!(timeOfDuration > 0)) {
				log.error(
						"not done collecting from money tree with userstructid {}",
						sfu.getId());
				success = false;
			} else
				success = true;

			Date lastRetrieved = sfu.getLastRetrieved();
			int timeForRenewal = (int) ((now.getTime() - lastRetrieved
					.getTime()) / (millisecondsConvertingToOneDayConstant * smt
					.getDaysForRenewal()));

			if (!(timeForRenewal > 0)) {
				log.error(
						"renewal period not over yet for money tree with userstructid {}",
						sfu.getId());
				success = false;
			} else
				success = true;
		}

		return success;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		boolean success = false;
		for (String userStructId : userStructIdsList) {
			success = deleteUtil.deleteUserStruct(userStructId);
			if (!success) {
				return success;
			}

		}
		return success;
	}

	public Map<String, String> getReasons() {
		return reasonsForChanges;
	}

	public Map<String, String> getDetails() {
		return details;
	}

}
