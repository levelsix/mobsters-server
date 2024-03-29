package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.TradeItemForBoosterRequestEvent;
import com.lvl6.events.response.ReceivedGiftResponseEvent;
import com.lvl6.events.response.TradeItemForBoosterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.Item;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto.BoosterPackType;
import com.lvl6.proto.EventItemProto.TradeItemForBoosterRequestProto;
import com.lvl6.proto.EventItemProto.TradeItemForBoosterResponseProto;
import com.lvl6.proto.EventItemProto.TradeItemForBoosterResponseProto.Builder;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AwardRewardAction;
import com.lvl6.server.controller.utils.BoosterItemUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class TradeItemForBoosterController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(TradeItemForBoosterController.class);

	public TradeItemForBoosterController() {

	}

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected BoosterItemRetrieveUtils boosterItemRetrieveUtils;

	@Autowired
	protected BoosterPackRetrieveUtils boosterPackRetrieveUtils;

	@Autowired
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	protected GiftRewardRetrieveUtils giftRewardRetrieveUtils;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtil;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtils;

	@Autowired
	private UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected Locker locker;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected BoosterItemUtils boosterItemUtils;

	@Autowired
	protected TimeUtils timeUtils;


	@Override
	public RequestEvent createRequestEvent() {
		return new TradeItemForBoosterRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_TRADE_ITEM_FOR_BOOSTER_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		TradeItemForBoosterRequestProto reqProto = ((TradeItemForBoosterRequestEvent) event)
				.getTradeItemForBoosterRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int itemId = reqProto.getItemId();
		Date now = new Date(reqProto.getClientTime());
		Timestamp nowTimestamp = new Timestamp(reqProto.getClientTime());

		TradeItemForBoosterResponseProto.Builder resBuilder = TradeItemForBoosterResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			TradeItemForBoosterResponseEvent resEvent = new TradeItemForBoosterResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) >
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			TradeItemForBoosterResponseEvent resEvent =
					new TradeItemForBoosterResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			TradeItemForBoosterResponseEvent resEvent = new TradeItemForBoosterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		//TODO: Logic similar to PurchaseBoosterPack, see what else can be optimized/shared
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());
			User aUser = userRetrieveUtils.getUserById(
					senderProto.getUserUuid());
			Item itm = itemRetrieveUtils.getItemForId(itemId);
			//TODO: Consider writing currency history and other history

			List<ItemForUser> ifuContainer = new ArrayList<ItemForUser>();
			List<Integer> boosterPackIdContainer = new ArrayList<Integer>();
			List<Boolean> riggedContainer = new ArrayList<Boolean>();

			boolean legit = checkLegitTrade(resBuilder, aUser, userId, itm,
					itemId, ifuContainer, boosterPackIdContainer,
					riggedContainer);

			ItemForUser ifu = null;
			List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
			int previousGems = 0;
			int boosterPackId = 0;// itm.getStaticDataId();
			if (legit) {
				boosterPackId = boosterPackIdContainer.get(0);

				ifu = ifuContainer.get(0);
				Map<Integer, BoosterItem> idsToBoosterItems = boosterItemRetrieveUtils
						.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);

				previousGems = aUser.getGems();

				int numBoosterItemsUserWants = 1;
				log.info("determining the booster items the user receives.");
				itemsUserReceives = boosterItemUtils
						.determineBoosterItemsUserReceives(
								numBoosterItemsUserWants, idsToBoosterItems,
								serverToggleRetrieveUtils);

				legit = boosterItemUtils.checkIfMonstersExist(itemsUserReceives,
						monsterRetrieveUtils, rewardRetrieveUtil);
			}

			int gemReward = 0;
			int gachaCreditsReward = 0;
			boolean successful = false;
			List<AwardRewardAction> araContainer = new ArrayList<AwardRewardAction>();
			if (legit) {
				boolean rigged = riggedContainer.get(0);
				gemReward = boosterItemUtils.determineGemReward(itemsUserReceives, rewardRetrieveUtil);
				gachaCreditsReward = boosterItemUtils.determineGachaCreditsReward(itemsUserReceives, rewardRetrieveUtil);
				//set the FullUserMonsterProtos (in resBuilder) to send to the client

				successful = writeChangesToDB(resBuilder, aUser, userId, ifu,
						itemId, boosterPackId, itemsUserReceives, now,
						gemReward, gachaCreditsReward, rigged, araContainer);
			}

			if (successful) {
				//assume user only receives 1 item. NEED TO LET CLIENT KNOW THE PRIZE
				if (null != itemsUserReceives && !itemsUserReceives.isEmpty()) {
					BoosterItem bi = itemsUserReceives.get(0);
					BoosterItemProto bip = createInfoProtoUtils
							.createBoosterItemProto(bi, rewardRetrieveUtil);
					resBuilder.setPrize(bip);
				}
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			TradeItemForBoosterResponseProto resProto = resBuilder.build();
			TradeItemForBoosterResponseEvent resEvent = new TradeItemForBoosterResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				if (!araContainer.isEmpty()) {
					AwardRewardAction ara = araContainer.get(0);
					sendClanGiftIfExists(responses, userId, ara, senderProto);
				}

				writeToUserCurrencyHistory(aUser, boosterPackId, nowTimestamp,
						previousGems, itemsUserReceives, gemReward);

				//just assume user can only get one booster pack at a time
				writeToBoosterPackPurchaseHistory(userId, boosterPackId,
						itemsUserReceives, resBuilder.getUpdatedOrNewList(),
						nowTimestamp);
			}
		} catch (Exception e) {
			log.error(
					"exception in TradeItemForBoosterController processEvent",
					e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				TradeItemForBoosterResponseEvent resEvent = new TradeItemForBoosterResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in TradeItemForBoosterController processEvent",
						e);
			}

		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private boolean checkLegitTrade(Builder resBuilder, User aUser,
			String userId, Item itm, int itemId,
			List<ItemForUser> ifuContainer,
			List<Integer> boosterPackIdContainer, List<Boolean> riggedContainer) {

		if (null == aUser || itemId <= 0) {
			log.error(String
					.format("no user for id: %s, or invalid itemId: %s",
							userId, itemId));
			return false;
		}

		if (null == itm || itm.getStaticDataId() <= 0) {
			log.error(String
					.format("no item with id=%s, or item does not have a boosterPackId. item=%s",
							itemId, itm));
			return false;
		}
		int boosterPackId = itm.getStaticDataId();
		BoosterPack aPack = boosterPackRetrieveUtils
				.getBoosterPackForBoosterPackId(boosterPackId);
		if (null == aPack) {
			log.error("no BoosterPack for id={}", boosterPackId);
			return false;
		}

		String type = aPack.getType();

		if (!aUser.isBoughtRiggedBoosterPack()
				&& BoosterPackType.BASIC.name().equals(type)) {
			//when user buys the lowest rated booster pack and hasn't
			//bought a rigged booster pack, rig the purchase
			log.info("rigging booster pack purchase. boosterPack={}, user={}",
					aPack, aUser);
			boosterPackId = aPack.getRiggedId();
			riggedContainer.add(true);
		} else {
			riggedContainer.add(false);
		}

		Map<Integer, BoosterItem> idsToBoosterItems = boosterItemRetrieveUtils
				.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);

		if (null == idsToBoosterItems || idsToBoosterItems.isEmpty()) {
			log.error("no booster items={}", idsToBoosterItems);
			return false;
		}

		Map<Integer, ItemForUser> ifuMap = itemForUserRetrieveUtil
				.getSpecificOrAllItemForUserMap(userId,
						Collections.singleton(itemId));

		if (null == ifuMap || ifuMap.isEmpty()) {
			log.error(String.format("user:%s does not have itemId=%s", aUser,
					itemId));
			return false;
		}

		ItemForUser ifu = ifuMap.get(itemId);

		if (ifu.getQuantity() <= 0) {
			log.error(String.format("not enough item quantity. item=%s", ifu));
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_ITEM);
			return false;
		}

		ifuContainer.add(ifu);
		boosterPackIdContainer.add(boosterPackId);

		return true;
	}

	//TODO: Copy pasted from PurchaseBoosterPackController

	private boolean writeChangesToDB(Builder resBuilder, User user,
			String userId, ItemForUser ifu, int itemId, int bPackId,
			List<BoosterItem> itemsUserReceives, Date now, int gemReward,
			int gachaCreditsReward, boolean rigged,
			List<AwardRewardAction> araContainer) {

		//update user items, user, and user_monsters
		//    int numUpdated = UpdateUtils.get().updateItemForUser(userId, itemId, -1);

		//not using above because, (data abstraction violation) the
		//called method uses -1 as the quantity, and if quantity column
		//in the db is unsigned, then error pops up
		//Data truncation: Out of range value for column 'quantity'
		int newQuantity = ifu.getQuantity() - 1;
		ifu.setQuantity(newQuantity);
		int numUpdated = UpdateUtils.get().updateItemForUser(
				Collections.singletonList(ifu));
		log.info(String.format("num user items updated=%s", numUpdated));

		//update user's money
		//		if (gemReward > 0 && !user.updateRelativeGemsNaive(gemReward, 0)) {
		//			log.error(String.format(
		//				"could not change user's money. gemReward=%s", gemReward));
		//			return false;
		//		}
		boolean updated = user.updateBoughtBoosterPack(gemReward, gachaCreditsReward, now, false,
				rigged);
		log.info("updated, user bought boosterPack? {}", updated);

		List<Reward> listOfRewards = new ArrayList<Reward>();
		for(BoosterItem bi : itemsUserReceives) {
			Reward r = rewardRetrieveUtil.getRewardById(bi.getRewardId());
			listOfRewards.add(r);
		}

		String awardReasonDetail = "booster pack id: " + bPackId;
		AwardRewardAction ara = new AwardRewardAction(userId, user, 0, 0, now,
				"trade item for booster", listOfRewards, userRetrieveUtils,
				itemForUserRetrieveUtil, insertUtil, updateUtil, monsterStuffUtils,
				monsterLevelInfoRetrieveUtils,
				giftRetrieveUtil, giftRewardRetrieveUtils,
				rewardRetrieveUtil, userClanRetrieveUtils, createInfoProtoUtils,
				awardReasonDetail);
		ara.execute();
		createRewardProto(resBuilder, ara);

		araContainer.add(ara);
		return true;
	}

    public void createRewardProto(TradeItemForBoosterResponseProto.Builder resBuilder,
            AwardRewardAction ara) {
        Collection<ItemForUser> nuOrUpdatedItems = ara.getNuOrUpdatedItems();
        Collection<FullUserMonsterProto> fumpList = ara.getNuOrUpdatedMonsters();
        int gemsGained = ara.getGemsGained();
        int cashGained = ara.getCashGained();
        int oilGained = ara.getOilGained();
        int gachaGained = ara.getGachaCreditsGained();

        //TODO: protofy the rewards
        UserRewardProto urp = createInfoProtoUtils.createUserRewardProto(
                nuOrUpdatedItems, fumpList, gemsGained, cashGained, oilGained, gachaGained, null);
        log.info("proto for reward: {}", urp);
        resBuilder.setRewards(urp);
    }

    private void sendClanGiftIfExists(
			ToClientEvents responses,
			String userId,
			AwardRewardAction ara, MinimumUserProto mup) {
		try {
			if (null != ara && ara.existsClanGift()) {
				ReceivedGiftResponseProto rgrp = ara.getClanGift();
				ReceivedGiftResponseEvent rgre = new ReceivedGiftResponseEvent(userId);
				rgre.setResponseProto(rgrp);

				String clanId = mup.getClan().getClanUuid();
				responses.clanResponseEvents().add(new ClanResponseEvent(rgre, clanId, false));
			}
		} catch (Exception e) {
			log.error("failed to send ClanGift notification", e);
		}
	}

	private void writeToUserCurrencyHistory(User aUser, int packId,
			Timestamp date, int previousGems, List<BoosterItem> items,
			int gemReward) {

		String userId = aUser.getId();
		List<Integer> itemIds = new ArrayList<Integer>();
		for (BoosterItem item : items) {
			int id = item.getId();
			itemIds.add(id);
		}

		StringBuilder detailSb = new StringBuilder();
		if (null != items && !items.isEmpty()) {
			detailSb.append(" bItemIds=");
			String itemIdsCsv = StringUtils.csvList(itemIds);
			detailSb.append(itemIdsCsv);
		}
		if (gemReward > 0) {
			detailSb.append(" gemReward=");
			detailSb.append(gemReward);
		}
		String gems = miscMethods.gems;
		String reasonForChange = ControllerConstants.UCHRFC__PURHCASED_BOOSTER_PACK;

		Map<String, Integer> money = new HashMap<String, Integer>();
		Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> details = new HashMap<String, String>();

		int change = gemReward;
		money.put(gems, change);
		previousCurrencies.put(gems, previousGems);
		currentCurrencies.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reasonForChange);
		details.put(gems, detailSb.toString());

		log.info("DETAILS=" + detailSb.toString());
		miscMethods.writeToUserCurrencyOneUser(userId, date, money,
				previousCurrencies, currentCurrencies, reasonsForChanges,
				details);
	}

	private void writeToBoosterPackPurchaseHistory(String userId,
			int boosterPackId, List<BoosterItem> itemsUserReceives,
			List<FullUserMonsterProto> fumpList, Timestamp timeOfPurchase) {
//		//just assuming there is one Booster Item
//		if (itemsUserReceives.isEmpty()) {
//			return;
//		}
//
//
//		int num = insertUtil.insertIntoBoosterPackPurchaseHistory(
//				userId, boosterPackId, timeOfPurchase, bi, userMonsterIds);
//
	}

}
