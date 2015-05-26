package com.lvl6.server.controller.actionobjects;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetClanDataProtoAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String clanId;
	private Clan clan;
	private User user;
	private String userId;
	private List<Date> lastChatTimeContainer;
	private StartUpResource fillMe;
	private ClanHelpRetrieveUtil clanHelpRetrieveUtil;
	private ClanAvengeRetrieveUtil clanAvengeRetrieveUtil;
	private ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil;
	private ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils;
	private ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	private MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	private CreateInfoProtoUtils createInfoProtoUtils;

	public SetClanDataProtoAction(
			String clanId,
			Clan clan,
			User user,
			String userId,
			List<Date> lastChatTimeContainer,
			StartUpResource fillMe,
			ClanHelpRetrieveUtil clanHelpRetrieveUtil,
			ClanAvengeRetrieveUtil clanAvengeRetrieveUtil,
			ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil,
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils,
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil,
			MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil,
			CreateInfoProtoUtils createInfoProtoUtils) {
		super();
		this.clanId = clanId;
		this.clan = clan;
		this.user = user;
		this.userId = userId;
		this.lastChatTimeContainer = lastChatTimeContainer;
		this.fillMe = fillMe;
		this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
		this.clanAvengeRetrieveUtil = clanAvengeRetrieveUtil;
		this.clanAvengeUserRetrieveUtil = clanAvengeUserRetrieveUtil;
		this.clanChatPostRetrieveUtils = clanChatPostRetrieveUtils;
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	public ClanDataProto execute() {
		log.info("setting clanData proto for clan {}", clan);
		ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();

		SetClanChatMessageAction sccma = new SetClanChatMessageAction(cdpb,
				user, clanChatPostRetrieveUtils, createInfoProtoUtils);
		sccma.setUp(fillMe);
		SetClanHelpingsAction scha = new SetClanHelpingsAction(cdpb, user,
				userId, clanHelpRetrieveUtil, createInfoProtoUtils);
		scha.setUp(fillMe);
		SetClanRetaliationsAction scra = new SetClanRetaliationsAction(cdpb,
				user, userId, clanAvengeRetrieveUtil,
				clanAvengeUserRetrieveUtil, createInfoProtoUtils);
		scra.setUp(fillMe);
		SetClanMemberTeamDonationAction scmtda = new SetClanMemberTeamDonationAction(
				cdpb, user, userId, clanMemberTeamDonationRetrieveUtil,
				monsterSnapshotForUserRetrieveUtil, createInfoProtoUtils);
		scmtda.setUp(fillMe);

		fillMe.fetchUsersOnly();
		fillMe.addClan(clanId, clan);

		sccma.execute(fillMe);
		scha.execute(fillMe);
		scra.execute(fillMe);
		scmtda.execute(fillMe);

		lastChatTimeContainer.add(sccma.getLastClanChatPostTime());

		return cdpb.build();

	}

}
