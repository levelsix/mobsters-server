package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.GiftForTangoUser;
import com.lvl6.info.GiftForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.TangoGift;
import com.lvl6.info.User;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.Builder;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.GiftForTangoUserRetrieveUtil;
import com.lvl6.retrieveutils.GiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TangoGiftRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetGiftsAction implements StartUpAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final String userId;
//	private final ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils;
	private final GiftForUserRetrieveUtils giftForUserRetrieveUtil;
	private final GiftForTangoUserRetrieveUtil giftForTangoUserRetrieveUtil;
	private final TangoGiftRetrieveUtils tangoGiftRetrieveUtil;
	private final RewardRetrieveUtils rewardRetrieveUtil;
	private final CreateInfoProtoUtils createInfoProtoUtils;

	public SetGiftsAction(Builder resBuilder, User user, String userId,
			GiftForUserRetrieveUtils giftForUserRetrieveUtil,
			GiftForTangoUserRetrieveUtil giftForTangoUserRetrieveUtil,
			TangoGiftRetrieveUtils tangoGiftRetrieveUtil,
			RewardRetrieveUtils rewardRetrieveUtil,
			CreateInfoProtoUtils createInfoProtoUtils) {
		super();
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.giftForUserRetrieveUtil = giftForUserRetrieveUtil;
		this.giftForTangoUserRetrieveUtil = giftForTangoUserRetrieveUtil;
		this.tangoGiftRetrieveUtil = tangoGiftRetrieveUtil;
		this.rewardRetrieveUtil = rewardRetrieveUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	//derived state
//	private List<ClanGiftForUser> allClanGifts;
	private List<GiftForUser> allGifts;
	private Map<String, GiftForTangoUser> gfuIdToGftu;
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
		for (GiftForUser gfu : allGifts) {
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

		for (GiftForUser gfu : allGifts) {
			TangoGift tg = null;
			int staticDataId = gfu.getStaticDataId();
			String giftType = gfu.getGiftType();
			if (RewardType.TANGO_GIFT.name().equalsIgnoreCase(giftType)) {
				tg = tangoGiftRetrieveUtil.getTangoGiftForTangoGiftId(staticDataId);
			} else {
				log.error("unsupported giftType: {}", giftType);
				continue;
			}

			MinimumUserProto gifterMup = mupGifters.get(gfu.getGifterUserId());
			int rewardId = gfu.getRewardId();
			Reward r = rewardRetrieveUtil.getRewardById(rewardId);
			if (null == r) {
				log.error("no reward with id: {}", r);
				continue;
			}
			GiftForTangoUser gftu = gfuIdToGftu.get(gfu.getId());


			UserGiftProto ugp = createInfoProtoUtils.createUserGiftProto(
					gfu, gifterMup, r, null, gftu, tg);
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
