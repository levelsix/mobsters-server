package com.lvl6.server.controller.actionobjects;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto.Builder;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto.FulfillTeamDonationSolicitationStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto.SnapshotType;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class FulfillTeamDonationSolicitationAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String donatorId;
	private String clanId;
	private MonsterSnapshotForUser msfu;
	private ClanMemberTeamDonation cmtd;
	private Date clientTime;
	private ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	private UpdateUtil updateUtil;
	private InsertUtil insertUtil;

	public FulfillTeamDonationSolicitationAction(
		String donatorId,
		String clanId,
		MonsterSnapshotForUser msfu,
		ClanMemberTeamDonation cmtd,
		Date clientDate,
		ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil,
		UpdateUtil updateUtil,
		InsertUtil insertUtil )
	{
		super();
		this.donatorId = donatorId;
		this.clanId = clanId;
		this.msfu = msfu;
		this.cmtd = cmtd;
		this.clientTime = clientDate;
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
		this.updateUtil = updateUtil;
		this.insertUtil = insertUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class FulfillTeamDonationSolicitationResource {
	//		
	//		
	//		public FulfillTeamDonationSolicitationResource() {
	//			
	//		}
	//	}
	//
	//	public FulfillTeamDonationSolicitationResource execute() {
	//		
	//	}

	//derived state
	private String solicitationId;
	private ClanMemberTeamDonation solicitation;
	private MonsterSnapshotForUser msfuNew;
	
	//	private Map<String, Integer> currencyDeltas;
	//	private Map<String, Integer> prevCurrencies;
	//	private Map<String, Integer> curCurrencies;
	//	private Map<String, String> reasonsForChanges;
	//	private Map<String, String> details;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(FulfillTeamDonationSolicitationStatus.FAIL_OTHER);

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

		resBuilder.setStatus(FulfillTeamDonationSolicitationStatus.SUCCESS);
		
	}

	private boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		solicitationId = cmtd.getId();
		solicitation = clanMemberTeamDonationRetrieveUtil
			.getClanMemberTeamDonation(solicitationId, donatorId);

		if (null == solicitation) {
			log.error("nonexistent solicitation. {}", solicitation);
			resBuilder.setStatus(FulfillTeamDonationSolicitationStatus.FAIL_NONEXISTENT_SOLICITATION);
			return false;
		}

		if (solicitation.isFulfilled()) {
			log.error("already fulfilled solicitation. {}", solicitation);
			return false;
		}

		return true;
	}


	private boolean writeChangesToDB(Builder resBuilder) {

		solicitation.setFulfilled(true);
		int numUpdated = updateUtil.updateClanMemberTeamDonation(solicitation);
		log.info("numUpdated donations: {}", numUpdated);
		
		//insert into monster_snapshot_for_user
		msfuNew = new MonsterSnapshotForUser(msfu);
		msfuNew.setTimeOfEntry(clientTime);
		msfuNew.setType(SnapshotType.TEAM_DONATE.name());
		msfuNew.setIdInTable(solicitationId);
		
		//need to set the id
		String msfuNewId = insertUtil.insertIntoMonsterSnapshotForUser(msfuNew);
		msfuNew.setId(msfuNewId);
		log.info("MonsterSnapshotForUser before {} \t MonsterSnapshotForUser after {}",
			msfu, msfuNew);
		return true;
	}

	public ClanMemberTeamDonation getSolicitation()
	{
		return solicitation;
	}

	public void setSolicitation( ClanMemberTeamDonation solicitation )
	{
		this.solicitation = solicitation;
	}

	public MonsterSnapshotForUser getMsfuNew()
	{
		return msfuNew;
	}
	
}
