package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

//shared logic between controllers where a user leaves a lcan
@Component@Scope("prototype")public class ExitClanAction {
	private static Logger log = LoggerFactory.getLogger( ExitClanAction.class);

	private String userId;
	private String clanId;
	private int clanSize;
	private Date lastChatPost;
	@Autowired protected TimeUtils timeUtil; 
	@Autowired protected UpdateUtil updateUtil; 
	private ClanSearch clanSearch;
	private ServerToggleRetrieveUtils toggle;

	public ExitClanAction(String userId, String clanId, int clanSize,
			Date lastChatPost, TimeUtils timeUtil, UpdateUtil updateUtil,
			ClanSearch clanSearch,
			ServerToggleRetrieveUtils toggle) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.clanSize = clanSize;
		this.lastChatPost = lastChatPost;
		this.timeUtil = timeUtil;
		this.updateUtil = updateUtil;
		this.clanSearch = clanSearch;
		this.toggle = toggle;
	}

	public void execute() {

		int numUpdated = updateUtil.closeClanHelp(userId, clanId);
		log.info("num ClanHelps closed: {}", numUpdated);

		//mark pvp_history ten days ago to have clan_avenged = true 
		Date now = new Date();
		int minutes = -10
				* ControllerConstants.PVP__REQUEST_CLAN_TO_AVENGE_TIME_LIMIT_MINS;

		Date battleEndDate = timeUtil.createDateAddMinutes(now, minutes);
		Timestamp battleEndTime = new Timestamp(battleEndDate.getTime());

		numUpdated = UpdateUtils.get()
				.updateRecentPvpBattleHistoryClanRetaliated(userId,
						battleEndTime);

		log.info("num PvpBattleHistory clan_avenged marked as true: {}",
				numUpdated);

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
