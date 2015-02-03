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
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto.SnapshotType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetClanMemberTeamDonationAction implements StartUpAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static String type = SnapshotType.TEAM_DONATE.name();
	
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
			log.info("clanId null.");
			return;
		}
		
		List<ClanMemberTeamDonation> clanMemberTeamDonations = clanMemberTeamDonationRetrieveUtil
			.getClanMemberTeamDonationForClanId(clanId);
		log.info("clanMemberTeamDonations={}", clanMemberTeamDonations);
		
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
		
		log.info("userIdToDonationSolicitations={}", userIdToDonationSolicitations);
		
		fillMe.addUserId(userIdToDonationSolicitations.keySet());
	}

	@Override
	public void execute( StartUpResource useMe )
	{
		if (null == userIdToDonationSolicitations ||
			userIdToDonationSolicitations.isEmpty())
		{
			log.info("userIdToDonationSolicitations={}", userIdToDonationSolicitations);
			return;
		}
		Map<String, User> solicitors = useMe.getUserIdsToUsers(
			userIdToDonationSolicitations.keySet());
		
		if (null == solicitors || solicitors.isEmpty()) {
			log.info("solicitors={}", solicitors);
			return;
		}
		
		Clan c = useMe.getClan(clanId);
		log.info("c={}", c);
		
		//convert all solicitors into MinimumUserProtos
		Map<String, MinimumUserProto> mupSolicitors = new HashMap<String, MinimumUserProto>();
		for (String solicitorId : solicitors.keySet()) {
			User solicitor = solicitors.get(solicitorId);
			
			MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(solicitor, c);
			mupSolicitors.put(solicitorId, mup);
		}
		
		log.info("mupSolicitors={}", mupSolicitors);

		List<MonsterSnapshotForUser> msfuList = monsterSnapshotForUserRetrieveUtil.
			getMonstersSnapshotsForUser(userId,
				UserMonsterSnapshotProto.SnapshotType.TEAM_DONATE.name());
		Map<String, Map<String, MonsterSnapshotForUser>> typeToIdInTableToMsfu =
			new HashMap<String, Map<String, MonsterSnapshotForUser>>(); 
			
		//mapify for easier use
		//not all TeamDonationSolicitations have MonsterSnapshotForUser,
		for (MonsterSnapshotForUser msfu : msfuList) {
			String type = msfu.getType();
			String idInTable = msfu.getIdInTable();
			
			if (!typeToIdInTableToMsfu.containsKey(type)) {
				typeToIdInTableToMsfu.put(
					type, new HashMap<String, MonsterSnapshotForUser>());
			}
			
			Map<String, MonsterSnapshotForUser> typeInTableToMsfu =
				typeToIdInTableToMsfu.get(type);
			typeInTableToMsfu.put(idInTable, msfu);
		}
		
		//create protos
		for (String solicitationId : userIdToDonationSolicitations.keySet()) {
			ClanMemberTeamDonation cmtd = 
				userIdToDonationSolicitations.get(solicitationId);
			
			//if ClanMemberTeamDonation has MonsterSnapshotForUser, get it
			MonsterSnapshotForUser msfu = null;
			if (typeToIdInTableToMsfu.containsKey(type)) {
				
				Map<String, MonsterSnapshotForUser> typeInTableToMsfu =
					typeToIdInTableToMsfu.get(type);
				if (typeInTableToMsfu.containsKey(solicitationId)) {
					msfu = typeInTableToMsfu.get(solicitationId);
				}
			}
			
			String userId = cmtd.getUserId();
			MinimumUserProto solicitor = mupSolicitors.get(userId);
			
			ClanMemberTeamDonationProto cmtdp = CreateInfoProtoUtils
				.createClanMemberTeamDonationProto(cmtd, msfu, solicitor);

			log.info("cmtdp={}", cmtdp);
			cdpBuilder.addClanDonationSolicitations(cmtdp);
		}
	}
	
}
