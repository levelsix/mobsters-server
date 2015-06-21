package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.proto.EventClanProto.EndClanAvengingResponseProto.Builder;
import com.lvl6.proto.EventClanProto.EndClanAvengingResponseProto.EndClanAvengingStatus;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component@Scope("prototype")public class EndClanAvengingAction {
	private static Logger log = LoggerFactory.getLogger( EndClanAvengingAction.class);

	private String userId;
	private String clanId;
	private List<String> caUuidList;
	@Autowired protected DeleteUtil deleteUtil; 

	public EndClanAvengingAction(String userId, String clanId,
			List<String> caUuidList, DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.caUuidList = caUuidList;
		this.deleteUtil = deleteUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class EndClanAvengingResource {
	//		
	//		
	//		public EndClanAvengingResource() {
	//			
	//		}
	//	}
	//
	//	public EndClanAvengingResource execute() {
	//		
	//	}

	//derived state

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(EndClanAvengingStatus.FAIL_OTHER);

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

		resBuilder.setStatus(EndClanAvengingStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {

		if (caUuidList.isEmpty()) {
			log.error("invalid request: no valid avenging to end");
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		log.info("num clan_avenge: {}", caUuidList.size());

		int numUpdated = deleteUtil.deleteClanAvenge(clanId, caUuidList);
		log.info("numUpdated clan_avenge {}", numUpdated);

		numUpdated = deleteUtil.deleteClanAvengeUser(clanId, caUuidList);
		log.info("numUpdated clan_avenge_user {}", numUpdated);

		return true;
	}

}
