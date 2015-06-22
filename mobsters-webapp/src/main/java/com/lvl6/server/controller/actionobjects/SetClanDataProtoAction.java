package com.lvl6.server.controller.actionobjects;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component@Scope("prototype")public class SetClanDataProtoAction {
	private static Logger log = LoggerFactory.getLogger( SetClanDataProtoAction.class);

	@Autowired protected ClanAvengeRetrieveUtil clanAvengeRetrieveUtil;
	@Autowired protected ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil;
	@Autowired protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	@Autowired protected MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils;

	
	private String clanId;
	private Clan clan;
	private User user;
	private String userId;
	private List<Date> lastChatTimeContainer;
	private StartUpResource fillMe;

	public void wire(
			String clanId,
			Clan clan,
			User user,
			String userId,
			List<Date> lastChatTimeContainer,
			StartUpResource fillMe) {
		this.clanId = clanId;
		this.clan = clan;
		this.user = user;
		this.userId = userId;
		this.lastChatTimeContainer = lastChatTimeContainer;
		this.fillMe = fillMe;
	}

	public ClanDataProto execute() {
		log.info("setting clanData proto for clan {}", clan);
		ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();

		SetClanChatMessageAction sccma = AppContext.getBean(SetClanChatMessageAction.class); 
		sccma.wire(cdpb, user);
		sccma.setUp(fillMe);
		SetClanHelpingsAction scha = AppContext.getBean(SetClanHelpingsAction.class); 
		scha.wire(cdpb, user,userId);
		scha.setUp(fillMe);
		SetClanRetaliationsAction scra = AppContext.getBean(SetClanRetaliationsAction.class); 
		scra.wire(cdpb,	user, userId);
		scra.setUp(fillMe);
		SetClanMemberTeamDonationAction scmtda = AppContext.getBean(SetClanMemberTeamDonationAction.class);
		scmtda.wire(cdpb, user, userId);
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
