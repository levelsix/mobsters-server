package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanGiftRewards;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ClanGiftRewardsRetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

public class AwardClanGiftsAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String gifterUserId;
	private String clanId;
	private int clanGiftId;
	private UserClanStatus userClanStatus;
	private String reasonForGift;
	private ClanGiftRewardsRetrieveUtils clanGiftRewardsRetrieveUtils;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private InsertUtil insertUtil;

	public AwardClanGiftsAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AwardClanGiftsAction(String gifterUserId, int clanGiftId, String reasonForGift,
			ClanGiftRewardsRetrieveUtils clanGiftRewardsRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils, InsertUtil insertUtil,
			UserClanStatus userClanStatus) {
		super();
		this.gifterUserId = gifterUserId;
		this.clanGiftId = clanGiftId;
		this.userClanStatus = userClanStatus;
		this.reasonForGift = reasonForGift;
		this.clanGiftRewardsRetrieveUtils = clanGiftRewardsRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.insertUtil = insertUtil;
	}

	private List<ClanGiftRewards> rewardsForClanGift;
	private List<UserClan> clanMembers;


	public boolean execute() {

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}

		boolean valid = verifySemantics();

		if (!valid) {
			return false;
		}

		boolean success = writeChangesToDB();
		if (!success) {
			return false;
		}

		return true;
	}

	private boolean verifySemantics() {
		rewardsForClanGift = clanGiftRewardsRetrieveUtils.getClanGiftRewardsForClanGift(clanGiftId);

		if(rewardsForClanGift == null || rewardsForClanGift.isEmpty()) {
			log.error("there are no rewards for clan gift id {}", clanGiftId);
			return false;
		}

		clanMembers = userClanRetrieveUtils.getUserClansRelatedToClan(clanId);
		if(clanMembers == null || clanMembers.isEmpty()) {
			log.error("there are no clan members for clan id {}", clanId);
			return false;
		}

		return true;

	}

	private boolean writeChangesToDB() {
		//create a map of userId to rewardId they received
		Map<String, Integer> userIdToRewardId = new HashMap<String, Integer>();

		for(UserClan uc : clanMembers) {
			if(!uc.getStatus().equalsIgnoreCase(UserClanStatus.REQUESTING.toString())) {
				String receiverUserId = uc.getUserId();
				int rewardId = determineReward();
				userIdToRewardId.put(receiverUserId, rewardId);
			}
		}

		//insert all the clan gifts for users into table
		boolean success = insertUtil.insertClanGiftForUsers(userIdToRewardId, gifterUserId, clanGiftId, reasonForGift);
		if(!success) {
			log.error("error inserting clan gifts into clan gift for user table");
		}

		return true;
	}

	public int determineReward() {
		double randomValue = Math.random();
		float runningCount = 0;
		ClanGiftRewards currentClanGiftRewards = null;

		for(ClanGiftRewards cgr : rewardsForClanGift) {
			currentClanGiftRewards = cgr;
			runningCount += cgr.getChanceOfDrop();
			if(runningCount > randomValue) {
				return cgr.getRewardId();
			}
		}

		return currentClanGiftRewards.getRewardId();
	}



	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		AwardClanGiftsAction.log = log;
	}

	public String getGifterUserId() {
		return gifterUserId;
	}

	public void setGifterUserId(String gifterUserId) {
		this.gifterUserId = gifterUserId;
	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
	}

	public int getClanGiftId() {
		return clanGiftId;
	}

	public void setClanGiftId(int clanGiftId) {
		this.clanGiftId = clanGiftId;
	}

	public UserClanStatus getUserClanStatus() {
		return userClanStatus;
	}

	public void setUserClanStatus(UserClanStatus userClanStatus) {
		this.userClanStatus = userClanStatus;
	}

	public String getReasonForGift() {
		return reasonForGift;
	}

	public void setReasonForGift(String reasonForGift) {
		this.reasonForGift = reasonForGift;
	}

	public ClanGiftRewardsRetrieveUtils getClanGiftRewardsRetrieveUtils() {
		return clanGiftRewardsRetrieveUtils;
	}

	public void setClanGiftRewardsRetrieveUtils(
			ClanGiftRewardsRetrieveUtils clanGiftRewardsRetrieveUtils) {
		this.clanGiftRewardsRetrieveUtils = clanGiftRewardsRetrieveUtils;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public List<ClanGiftRewards> getRewardsForClanGift() {
		return rewardsForClanGift;
	}

	public void setRewardsForClanGift(List<ClanGiftRewards> rewardsForClanGift) {
		this.rewardsForClanGift = rewardsForClanGift;
	}

	public List<UserClan> getClanMembers() {
		return clanMembers;
	}

	public void setClanMembers(List<UserClan> clanMembers) {
		this.clanMembers = clanMembers;
	}





}
