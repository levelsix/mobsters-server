package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto.SnapshotType;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

//shared logic between controllers where a user leaves a lcan
@Component@Scope("prototype")public class ExitClanAction {
	private static Logger log = LoggerFactory.getLogger( ExitClanAction.class);

	@Autowired protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	@Autowired protected TimeUtils timeUtil;
	@Autowired protected UpdateUtil updateUtil;
	@Autowired protected DeleteUtil deleteUtil;
	@Autowired private ClanSearch clanSearch;
	@Autowired private ServerToggleRetrieveUtils toggle;

	private String userId;
	private String clanId;
	private int clanSize;
	private Date lastChatPost;

	public void wire(
			String userId,
			String clanId,
			int clanSize,
			Date lastChatPost)
	{
		this.userId = userId;
		this.clanId = clanId;
		this.clanSize = clanSize;
		this.lastChatPost = lastChatPost;
	}

	public void execute() {

		int numUpdated = updateUtil.closeClanHelp(userId, clanId);
		log.info("num ClanHelps closed: {}", numUpdated);

		//mark pvp_history ten days ago to have clan_avenged = true
		//ten days is arbitrary amount. It's just 10 times longer than
		//the clan_avenge time limit.
		Date now = new Date();
		int minutes = -10
				* ControllerConstants.PVP__REQUEST_CLAN_TO_AVENGE_TIME_LIMIT_MINS;

		Date battleEndDate = timeUtil.createDateAddMinutes(now, minutes);
		Timestamp battleEndTime = new Timestamp(battleEndDate.getTime());

		numUpdated = updateUtil
				.updateRecentPvpBattleHistoryClanRetaliated(userId,
						battleEndTime);

		log.info("num PvpBattleHistory clan_avenged marked as true: {}",
				numUpdated);

		//2015-07-02 delete every team donation
		List<ClanMemberTeamDonation> allTeamDonations = clanMemberTeamDonationRetrieveUtil
				.getClanMemberTeamDonationForUserId(userId);
		List<String> cmtdIds = new ArrayList<String>();
		for (ClanMemberTeamDonation cmtd : allTeamDonations) {
			cmtdIds.add(cmtd.getId());
		}

		if (!cmtdIds.isEmpty()) {
			int numDeleted = deleteUtil.deleteClanMemberTeamDonationSolicitation(cmtdIds);
			log.info("(expected 0/1) numDeleted donations={}\t {}",
					numDeleted, allTeamDonations );
			//delete the associated snapshots

			numDeleted = deleteUtil.deleteMonsterSnapshotsFromUser(
					SnapshotType.TEAM_DONATE.name(),
					cmtdIds);
			log.info("(expected 0/1) numDeleted toon snapshots={}",
					numDeleted );
		}


		//if user is last user in clan, clan gets removed from hz
//		if(toggle.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__OLD_CLAN_SEARCH)) {
//			if (clanSize > 0) {
//				clanSearch.updateClanSearchRank(clanId, clanSize, lastChatPost);
//			}
//		}
//		else {
//			hzClanSearch.updateRankForClanSearch(clanId, now, 0, 0, 0, 0, -1);
//		}
	}
}
