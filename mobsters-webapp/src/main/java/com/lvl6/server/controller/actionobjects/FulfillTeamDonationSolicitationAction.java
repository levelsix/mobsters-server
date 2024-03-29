package com.lvl6.server.controller.actionobjects;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto.SnapshotType;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component@Scope("prototype")public class FulfillTeamDonationSolicitationAction {
	private static Logger log = LoggerFactory.getLogger( FulfillTeamDonationSolicitationAction.class);

	private String donatorId;
	private String clanId;
	private MonsterSnapshotForUser msfu;
	private ClanMemberTeamDonation cmtd;
	private Date clientTime;
	@Autowired protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil; 
	@Autowired protected UpdateUtil updateUtil; 
	@Autowired protected InsertUtil insertUtil; 

	public FulfillTeamDonationSolicitationAction(
			String donatorId,
			String clanId,
			MonsterSnapshotForUser msfu,
			ClanMemberTeamDonation cmtd,
			Date clientDate,
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil,
			UpdateUtil updateUtil, InsertUtil insertUtil) {
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
	private String solicitorId;
	private ClanMemberTeamDonation solicitation;
	private MonsterSnapshotForUser msfuNew;

	//	private Map<String, Integer> currencyDeltas;
	//	private Map<String, Integer> prevCurrencies;
	//	private Map<String, Integer> curCurrencies;
	//	private Map<String, String> reasonsForChanges;
	//	private Map<String, String> details;

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

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		solicitationId = cmtd.getId();
		solicitorId = cmtd.getUserId();
		solicitation = clanMemberTeamDonationRetrieveUtil
				.getClanMemberTeamDonation(solicitationId, solicitorId);

		if (null == solicitation) {
			log.error("nonexistent solicitation. origClientSolicitation={}",
					cmtd);
			resBuilder
					.setStatus(ResponseStatus.FAIL_DOESNT_EXIST);
			return false;
		}

		if (solicitation.isFulfilled()) {
			log.error("already fulfilled solicitation. {}", solicitation);
			resBuilder
					.setStatus(ResponseStatus.FAIL_ALREADY_FULFILLED);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		solicitation.setFulfilled(true);
		int numUpdated = updateUtil.updateClanMemberTeamDonation(solicitation);
		log.info("numUpdated donations: {}", numUpdated);

		//reduce donated monster's health to 0
		Map<String, Integer> userMonsterIdToExpectedHealth = Collections
				.singletonMap(msfu.getMonsterForUserId(), 0);
		numUpdated = UpdateUtils.get().updateUserMonstersHealth(
				userMonsterIdToExpectedHealth);
		log.info(String.format("numUpdated=%s", numUpdated));

		//insert into monster_snapshot_for_user
		msfuNew = new MonsterSnapshotForUser(msfu);
		msfuNew.setTimeOfEntry(clientTime);
		msfuNew.setType(SnapshotType.TEAM_DONATE.name());
		msfuNew.setIdInTable(solicitationId);

		//need to set the id
		String msfuNewId = insertUtil.insertIntoMonsterSnapshotForUser(msfuNew);
		msfuNew.setId(msfuNewId);
		log.info(
				"MonsterSnapshotForUser before {} \t MonsterSnapshotForUser after {}",
				msfu, msfuNew);
		return true;
	}

	public ClanMemberTeamDonation getSolicitation() {
		return solicitation;
	}

	public void setSolicitation(ClanMemberTeamDonation solicitation) {
		this.solicitation = solicitation;
	}

	public MonsterSnapshotForUser getMsfuNew() {
		return msfuNew;
	}

}
