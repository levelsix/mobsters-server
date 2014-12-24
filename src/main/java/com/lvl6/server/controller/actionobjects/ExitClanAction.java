package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

//shared logic between controllers where a user leaves a lcan
public class ExitClanAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private String clanId;
	private int clanSize;
	private Date lastChatPost;
	private TimeUtils timeUtil;
	private UpdateUtil updateUtil;
	private ClanSearch cs;
	
	public ExitClanAction(
		String userId,
		String clanId,
		int clanSize,
		Date lastChatPost,
		TimeUtils timeUtil,
		UpdateUtil updateUtil,
		ClanSearch cs)
	{
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.clanSize = clanSize;
		this.lastChatPost = lastChatPost;
		this.timeUtil = timeUtil;
		this.updateUtil = updateUtil;
		this.cs = cs;
	}

	public void execute() {

		int numUpdated = updateUtil.closeClanHelp(userId, clanId);
		log.info("num ClanHelps closed: {}", numUpdated);

		//mark pvp_history ten days ago to have clan_avenged = true 
		Date now = new Date();
		int minutes = -10 *
			ControllerConstants.PVP__REQUEST_CLAN_TO_AVENGE_TIME_LIMIT_MINS;

		Date battleEndDate = timeUtil.createDateAddMinutes(
			now, minutes);
		Timestamp battleEndTime = new Timestamp(battleEndDate.getTime());

		numUpdated = UpdateUtils.get().updateRecentPvpBattleHistoryClanRetaliated(
			userId, battleEndTime);
		    
		log.info("num PvpBattleHistory clan_avenged marked as true: {}",
			numUpdated);
		
		//exiting a clan can delete clan if the user who left is owner
		//so only update clan search rank if clan contains members
		if (clanSize > 0) {
			cs.updateClanSearchRank(clanId, clanSize, lastChatPost);
		}
	}
}