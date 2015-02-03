package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationResponseProto.Builder;
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationResponseProto.VoidTeamDonationSolicitationStatus;
import com.lvl6.utils.utilmethods.DeleteUtil;

public class VoidTeamDonationSolicitationAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private Map<String, List<MonsterSnapshotForUser>> donationIdsToSnapshots;
	private DeleteUtil deleteUtil;

	
	public VoidTeamDonationSolicitationAction(
		String userId,
		Map<String, List<MonsterSnapshotForUser>> donationIdsToSnapshots,
		DeleteUtil deleteUtil )
	{
		super();
		this.userId = userId;
		this.donationIdsToSnapshots = donationIdsToSnapshots;
		this.deleteUtil = deleteUtil;
	}

//	//encapsulates the return value from this Action Object
//	static class VoidTeamDonationSolicitationResource {
//		
//		
//		public VoidTeamDonationSolicitationResource() {
//			
//		}
//	}
//
//	public VoidTeamDonationSolicitationResource execute() {
//		
//	}

	//derived state
	
	public void execute(Builder resBuilder) {
		resBuilder.setStatus(VoidTeamDonationSolicitationStatus.FAIL_OTHER);
		
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
		
		resBuilder.setStatus(VoidTeamDonationSolicitationStatus.SUCCESS);
		
	}
	
	private boolean verifySyntax(Builder resBuilder) {
		
		if (null == donationIdsToSnapshots ||donationIdsToSnapshots.isEmpty()) {
			log.error("no ids to delete were sent.");
			return false;
		}
		
		return true;
	}
	
	private boolean verifySemantics(Builder resBuilder) {
	  	
		return true;
	}
	
	
	private boolean writeChangesToDB(Builder resBuilder) {
		List<String> donationIds = new ArrayList<String>(
			donationIdsToSnapshots.keySet());
		
		int numDeleted = deleteUtil
			.deleteClanMemberTeamDonationSolicitation(
				donationIds);
		log.info("numDeleted donationSolicitations: {}", numDeleted);
		
		List<MonsterSnapshotForUser> snapshots =
			new ArrayList<MonsterSnapshotForUser>();
		for (List<MonsterSnapshotForUser> snapshot : donationIdsToSnapshots.values())
		{
			snapshots.addAll(snapshot);
		}
		
		numDeleted = deleteUtil
			.deleteMonsterSnapshotForUser(snapshots, userId);
		log.info("numDeleted donationSolicitations: {}", numDeleted);
		
		return true;
	}
	
}
