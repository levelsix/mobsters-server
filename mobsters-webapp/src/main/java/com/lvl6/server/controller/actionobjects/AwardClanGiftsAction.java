package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftRewardConfig;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

public class AwardClanGiftsAction {

	private static final Logger log = LoggerFactory
			.getLogger(AwardClanGiftsAction.class);

	private String gifterUserId;
	private User gifterUser;
	private String clanId;
	private int giftId;
	private String reasonForGift;
	private GiftRetrieveUtils giftRetrieveUtil;
	private GiftRewardRetrieveUtils giftRewardRetrieveUtils;
	private RewardRetrieveUtils rewardRetrieveUtil;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private InsertUtil insertUtil;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private Random rand;

	public AwardClanGiftsAction() {
		super();
	}

	public AwardClanGiftsAction(String gifterUserId, User gifterUser, int giftId,
			String reasonForGift, GiftRetrieveUtils giftRetrieveUtil,
			GiftRewardRetrieveUtils giftRewardRetrieveUtils,
			RewardRetrieveUtils rewardRetrieveUtil,
			UserClanRetrieveUtils2 userClanRetrieveUtils, InsertUtil insertUtil,
			CreateInfoProtoUtils createInfoProtoUtils) {
		super();
		this.gifterUserId = gifterUserId;
		this.gifterUser = gifterUser;
		this.giftId = giftId;
		this.reasonForGift = reasonForGift;
		this.giftRetrieveUtil = giftRetrieveUtil;
		this.giftRewardRetrieveUtils = giftRewardRetrieveUtils;
		this.rewardRetrieveUtil = rewardRetrieveUtil;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.insertUtil = insertUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;

		this.rand = ControllerConstants.RAND;
	}

//	private Collection<GiftRewardConfig> rewardsForClanGift;
	private GiftConfig gc;
	private List<UserClan> clanMembers;
	private ReceivedGiftResponseProto.Builder chatProto;
	private MinimumUserProto mup; //this is the dude who sent the gifts
	private GiftForUser giftersClanGift;

	public boolean execute() {

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}

		setUpReceivedClanGiftResponseProtoBuilder();

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

	public void setUpReceivedClanGiftResponseProtoBuilder() {
		chatProto = ReceivedGiftResponseProto.newBuilder();

		mup = createInfoProtoUtils.createMinimumUserProtoFromUserAndClan(gifterUser, null);

		chatProto.setSender(mup);
		chatProto.setScope(ChatScope.CLAN);
	}

	private boolean verifySemantics() {
		gc = giftRetrieveUtil.getGift(giftId);
		if (null == gc) {
			log.error("no ClanGift for id {}", giftId);
		}

		boolean rewardsExist = giftRewardRetrieveUtils.rewardsExist(giftId);
		if(!rewardsExist) {
			log.error("there are no rewards for ClanGift id {}", giftId);
			return false;
		}

		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		clanMembers = userClanRetrieveUtils.getUserUserClansWithStatuses(
				clanId, statuses);
		if(clanMembers == null || clanMembers.isEmpty()) {
			log.error("there are no clan members for clan id {}", clanId);
			return false;
		}

		return true;

	}

	private boolean writeChangesToDB() {
		//create a map of userId to rewardId all clan MEMBERS received
		Timestamp toe = new Timestamp((new Date()).getTime());
		Collection<GiftForUser> gifts = new ArrayList<GiftForUser>();

		for(UserClan uc : clanMembers) {
//			if(!uc.getStatus().equalsIgnoreCase(UserClanStatus.REQUESTING.toString())) {
			String receiverUserId = uc.getUserId();

			GiftRewardConfig grc = determineReward();
			int rewardId = grc.getRewardId();

			GiftForUser gfu = new GiftForUser();
			gfu.setGiftId(giftId);
			gfu.setGifterUserId(gifterUserId);
			gfu.setReasonForGift(reasonForGift);
			gfu.setReceiverUserId(receiverUserId);
			gfu.setRewardId(rewardId);
			gfu.setTimeOfEntry(toe);
			gfu.setCollected(false);

			if(receiverUserId.equalsIgnoreCase(gifterUserId)) {
				giftersClanGift = gfu;
			}
//			}
		}

		//insert all the clan gifts for users into table
		boolean success = insertUtil.insertGiftForUser(gifts);
		if(!success) {
			log.error("error inserting clan gifts into GiftForUser table");
		}

		for (GiftForUser gfu : gifts) {
			int rewardId = gfu.getRewardId();
			Reward r = rewardRetrieveUtil.getRewardById(rewardId);
			UserGiftProto ugp = createInfoProtoUtils.createUserGiftProto(
					gfu, mup, r, gc, null);

			chatProto.addUserGifts(ugp);
		}
		return true;
	}

	private GiftRewardConfig determineReward() {
		double probability = rand.nextDouble();
		GiftRewardConfig grc = giftRewardRetrieveUtils.nextGiftReward(giftId, probability);

		return grc;
	}

	public ReceivedGiftResponseProto getChatProto() {
		return chatProto.build();
	}

}
