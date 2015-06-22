package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.MonsterStuffProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto.SnapshotType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component@Scope("prototype")public class SetClanMemberTeamDonationAction implements StartUpAction {
	private static Logger log = LoggerFactory.getLogger( SetClanMemberTeamDonationAction.class);

	private static String typeDonation = SnapshotType.TEAM_DONATE.name();

	protected ClanDataProto.Builder cdpBuilder;
	protected User user;
	protected String userId;
	@Autowired protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	@Autowired protected MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils;

	public void wire(ClanDataProto.Builder cdpBuilder,	User user, String userId) {
		this.cdpBuilder = cdpBuilder;
		this.user = user;
		this.userId = userId;
	}

	//derived state
	private String clanId;
	private Map<String, ClanMemberTeamDonation> userIdToDonationSolicitations;
	private MonsterSnapshotForUser donationToMe;
	private String donatorId;

	private Map<String, MinimumUserProto> mupSolicitors;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		clanId = user.getClanId();

		if (null == clanId) {
			log.info("clanId null.");
			return;
		}

		List<ClanMemberTeamDonation> clanMemberTeamDonations = clanMemberTeamDonationRetrieveUtil
				.getClanMemberTeamDonationForClanId(clanId);
		log.debug("clanMemberTeamDonations={}", clanMemberTeamDonations);

		//log.info(String.format("clanMemberTeamDonations=%s", clanMemberTeamDonations));
		if (null == clanMemberTeamDonations
				|| clanMemberTeamDonations.isEmpty()) {
			return;
		}
		userIdToDonationSolicitations = new HashMap<String, ClanMemberTeamDonation>();

		//users in a clan can have at most one solicitation for ClanMemberTeamDonation
		ClanMemberTeamDonation mySolicitation = null;
		for (ClanMemberTeamDonation cmtd : clanMemberTeamDonations) {
			String solicitorId = cmtd.getUserId();
			userIdToDonationSolicitations.put(solicitorId, cmtd);

			if (solicitorId.equals(userId)) {
				mySolicitation = cmtd;
			}

		}

		log.debug("userIdToDonationSolicitations={}",
				userIdToDonationSolicitations);

		fillMe.addUserId(userIdToDonationSolicitations.keySet());

		//need to get the person who donated to this user, if any
		if (null != mySolicitation && mySolicitation.isFulfilled()) {
			//only fulfilled solicitation has MonsterSnapshot
			List<MonsterSnapshotForUser> donationsToMe = monsterSnapshotForUserRetrieveUtil
					.getMonstersSnapshots(typeDonation, mySolicitation.getId());

			//since for user's team, there's only one donation
			if (null == donationsToMe || donationsToMe.isEmpty()) {
				log.error("{} has no donations {}",
						"fulfilled ClanMemberTeamDonation solicitation",
						mySolicitation);

			} else if (donationsToMe.size() > 1) {
				String preface = "fulfilled ClanMemberTeamDonation solicitation";
				log.error("{} {} has multiple donations: {}", new Object[] {
						preface, mySolicitation, donationsToMe });
				donationToMe = donationsToMe.get(0);

			} else {
				donationToMe = donationsToMe.get(0);
			}

		}

		if (null != donationToMe) {
			donatorId = donationToMe.getUserId();
			fillMe.addUserId(donatorId);
		}

	}

	@Override
	public void execute(StartUpResource useMe) {
		if (null == userIdToDonationSolicitations
				|| userIdToDonationSolicitations.isEmpty()) {
			log.debug("userIdToDonationSolicitations={}",
					userIdToDonationSolicitations);
			return;
		}
		Map<String, User> solicitors = useMe
				.getUserIdsToUsers(userIdToDonationSolicitations.keySet());

		if (null == solicitors || solicitors.isEmpty()) {
			log.debug("solicitors={}", solicitors);
			return;
		}

		Clan c = useMe.getClan(clanId);
		log.debug("c={}", c);

		User donator = null;
		if (null != donatorId) {
			donator = useMe.getUser(donatorId);
		}

		//convert all solicitors into MinimumUserProtos
		protofySolicitors(solicitors, c);

		//create protos
		for (String solicitationId : userIdToDonationSolicitations.keySet()) {
			ClanMemberTeamDonation cmtd = userIdToDonationSolicitations
					.get(solicitationId);

			String solicitorId = cmtd.getUserId();
			MinimumUserProto solicitor = mupSolicitors.get(solicitorId);

			MonsterSnapshotForUser msfu = null;
			MinimumUserProto donatorProto = null;
			//only need MonsterSnapshotForUser for current user's solicitation
			if (solicitorId.equals(userId) && null != donationToMe) {
				msfu = donationToMe;
				donatorProto = createInfoProtoUtils
						.createMinimumUserProtoFromUserAndClan(donator, c);
			}

			ClanMemberTeamDonationProto cmtdp = createInfoProtoUtils
					.createClanMemberTeamDonationProto(cmtd, msfu, solicitor,
							donatorProto);

//			log.info("cmtdp={}", cmtdp);
			cdpBuilder.addClanDonationSolicitations(cmtdp);
		}
	}

	private void protofySolicitors(Map<String, User> solicitors, Clan c) {
		mupSolicitors = new HashMap<String, MinimumUserProto>();
		for (String solicitorId : solicitors.keySet()) {
			User solicitor = solicitors.get(solicitorId);

			MinimumUserProto mup = createInfoProtoUtils
					.createMinimumUserProtoFromUserAndClan(solicitor, c);
			mupSolicitors.put(solicitorId, mup);
		}
	}

}
