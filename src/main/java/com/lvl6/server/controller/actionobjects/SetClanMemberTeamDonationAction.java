package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.ClanProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetClanMemberTeamDonationAction implements StartUpAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final ClanDataProto.Builder cdpBuilder;
	private final User user;
	private final String userId;
	private final ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	private final MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	public SetClanMemberTeamDonationAction(
		ClanDataProto.Builder cdpBuilder, User user, String userId,
		ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil,
		MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil)
	{
		super();
		this.cdpBuilder = cdpBuilder;
		this.user = user;
		this.userId = userId;
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
	}

	//derived state
	private String clanId;
	private Map<String, ClanMemberTeamDonation> userIdToDonationSolicitations;
	
	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe)
	{
		clanId = user.getClanId();
		
		if (null == clanId) {
			return;
		}
		
		List<ClanMemberTeamDonation> clanMemberTeamDonations = clanMemberTeamDonationRetrieveUtil
			.getClanMemberTeamDonationForClanId(clanId);
		
		//log.info(String.format("clanMemberTeamDonations=%s", clanMemberTeamDonations));
		if (null == clanMemberTeamDonations || clanMemberTeamDonations.isEmpty())
		{
			return;
		}
		userIdToDonationSolicitations = new HashMap<String, ClanMemberTeamDonation>();
		for (ClanMemberTeamDonation cmtd : clanMemberTeamDonations) {
			String solicitorId = cmtd.getUserId();
			userIdToDonationSolicitations.put(solicitorId, cmtd);
		}
		
		fillMe.addUserId(userIdToDonationSolicitations.keySet());
	}

	@Override
	public void execute( StartUpResource useMe )
	{
		if (null == userIdToDonationSolicitations ||
			userIdToDonationSolicitations.isEmpty())
		{
			return;
		}
		Map<String, User> solicitors = useMe.getUserIdsToUsers(
			userIdToDonationSolicitations.keySet());
		
		if (null == solicitors || solicitors.isEmpty()) {
			return;
		}
		
		Clan c = useMe.getClan(clanId);
		
		//convert all solicitors into MinimumUserProtos
		Map<String, MinimumUserProto> mupSolicitors = new HashMap<String, MinimumUserProto>();
		for (String solicitorId : solicitors.keySet()) {
			User solicitor = solicitors.get(solicitorId);
			
			MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(solicitor, c);
			mupSolicitors.put(solicitorId, mup);
		}

		List<MonsterSnapshotForUser> msfuList = monsterSnapshotForUserRetrieveUtil.
			getMonstersSnapshotsForUser(userId,
				UserMonsterSnapshotProto.SnapshotType.TEAM_DONATE.name());
			
		for (MonsterSnapshotForUser msfu : msfuList) {
			String solicitorId = msfu.getUserId();
			ClanMemberTeamDonation solicitation = userIdToDonationSolicitations
				.get(solicitorId);

//			User solicitor = solicitors.get( solicitorId );
			MinimumUserProto mup = mupSolicitors.get( solicitorId );

			//null for clan since mup exists and mup has clan in it
			ClanMemberTeamDonationProto cmtdp = CreateInfoProtoUtils
				.createClanMemberTeamDonationProto(solicitation, msfu, mup);

			cdpBuilder.addClanDonationSolicitations(cmtdp);
		}
	}
	
}
