package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUserPojo;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.Builder;
import com.lvl6.proto.RewardsProto.GiftProto.GiftType;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.GiftForTangoUserRetrieveUtil;
import com.lvl6.retrieveutils.GiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetGiftsAction implements StartUpAction {

	private static final Logger log = LoggerFactory
			.getLogger(SetGiftsAction.class);

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final String userId;
	private final GiftForUserRetrieveUtils giftForUserRetrieveUtil;
	private final GiftForTangoUserRetrieveUtil giftForTangoUserRetrieveUtil;
	private final GiftRetrieveUtils giftRetrieveUtil;
	private final RewardRetrieveUtils rewardRetrieveUtil;
	private final CreateInfoProtoUtils createInfoProtoUtils;

	public SetGiftsAction(Builder resBuilder, User user, String userId,
			GiftForUserRetrieveUtils giftForUserRetrieveUtil,
			GiftForTangoUserRetrieveUtil giftForTangoUserRetrieveUtil,
			GiftRetrieveUtils giftRetrieveUtil,
			RewardRetrieveUtils rewardRetrieveUtil,
			CreateInfoProtoUtils createInfoProtoUtils) {
		super();
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.giftForUserRetrieveUtil = giftForUserRetrieveUtil;
		this.giftForTangoUserRetrieveUtil = giftForTangoUserRetrieveUtil;
		this.giftRetrieveUtil = giftRetrieveUtil;
		this.rewardRetrieveUtil = rewardRetrieveUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	//derived state
//	private List<ClanGiftForUser> allClanGifts;
	private List<GiftForUserPojo> allGifts;
	private Map<String, GiftForTangoUserPojo> gfuIdToGftu;
	private List<String> userIds;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
//		allClanGifts = clanGiftForUserRetrieveUtils.getUserClanGiftsForUser(userId);
//		log.info(String.format("allClanGifts=%s", allClanGifts));
//
//		if (null == allClanGifts || allClanGifts.isEmpty()) {
//			return;
//		}
//
//		for(ClanGiftForUser cgfu : allClanGifts) {
//			userIds.add(cgfu.getGifterUserId());
//		}

		Set<String> gfuIds = new HashSet<String>();
		userIds = new ArrayList<String>();
		allGifts = giftForUserRetrieveUtil.getUserGiftsForUser(userId);
		for (GiftForUserPojo gfu : allGifts) {
			gfuIds.add(gfu.getId());
			userIds.add(gfu.getGifterUserId());
		}

		gfuIdToGftu = giftForTangoUserRetrieveUtil.getGftuForGfuIdsMap(gfuIds);

		fillMe.addUserId(userIds);
	}

	@Override
	public void execute(StartUpResource useMe) {
//		if (null == allClanGifts || allClanGifts.isEmpty()) {
//			return;
//		}
		if ( allGifts.isEmpty() ) {
			return;
		}

		Map<String, User> gifters = useMe.getUserIdsToUsers(userIds);
		if (null == gifters || gifters.isEmpty()) {
			log.error("no users with ids. Gifts {}", allGifts);
			return;
		}

		//convert all solicitors into MinimumUserProtos
		Map<String, MinimumUserProto> mupGifters = new HashMap<String, MinimumUserProto>();
		for (String gifterId : gifters.keySet()) {
			User gifterUser = gifters.get(gifterId);

			MinimumUserProto mup = createInfoProtoUtils
					.createMinimumUserProtoFromUserAndClan(gifterUser, null);
			mupGifters.put(gifterId, mup);
		}

		for (GiftForUserPojo gfu : allGifts) {
			int giftId = gfu.getGiftId();
			GiftConfigPojo gc = giftRetrieveUtil.getGift(giftId);

			MinimumUserProto gifterMup = mupGifters.get(gfu.getGifterUserId());
			int rewardId = gfu.getRewardId();
			Reward r = rewardRetrieveUtil.getRewardById(rewardId);
			if (null == r) {
				log.error("no reward with id: {}", r);
				continue;
			}

			String giftType = gc.getGiftType();
			GiftForTangoUserPojo gftu = null;

			if (GiftType.TANGO_GIFT.name().equalsIgnoreCase(giftType)) {
				gftu = gfuIdToGftu.get(gfu.getId());

			} else if (GiftType.CLAN_GIFT.name().equalsIgnoreCase(giftType)) {

			} else {
				log.error("unsupported giftType: {}", giftType);
				continue;
			}

			UserGiftProto ugp = createInfoProtoUtils.createUserGiftProto(
					gfu, gifterMup, r, gc, gftu);
			resBuilder.addUserGifts(ugp);
		}

//		for (ClanGiftForUser cgfu : allClanGifts) {
//			//null for clan since mup exists and mup has clan in it
//			MinimumUserProto mup = mupGifters.get(cgfu.getGifterUserId());
//			UserClanGiftProto ucgp = createInfoProtoUtils
//					.createUserClanGiftProto(cgfu, mup);
//
//			resBuilder.addUserClanGifts(ucgp);
//
//		}
	}

}
