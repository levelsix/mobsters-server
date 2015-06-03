package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IList;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PurchaseBoosterPackRequestEvent;
import com.lvl6.events.response.PurchaseBoosterPackResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackRequestProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.PurchaseBoosterPackStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanGiftRewardsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.PurchaseBoosterPackAction;
import com.lvl6.server.controller.utils.BoosterItemUtils;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class PurchaseBoosterPackController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public static int BOOSTER_PURCHASES_MAX_SIZE = 50;

	@Autowired
	protected Locker locker;

	@Autowired
	protected BoosterItemUtils boosterItemUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	private ClanGiftRewardsRetrieveUtils clanGiftRewardsRetrieveUtils;

	@Autowired
	private UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected BoosterPackRetrieveUtils boosterPackRetrieveUtils;

	@Autowired
	protected BoosterItemRetrieveUtils boosterItemRetrieveUtils;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;
	
	@Autowired
	protected HistoryUtils historyUtils;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtils;

	@Resource(name = "goodEquipsRecievedFromBoosterPacks")
	protected IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks;

	public PurchaseBoosterPackController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new PurchaseBoosterPackRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_PURCHASE_BOOSTER_PACK_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		PurchaseBoosterPackRequestProto reqProto = ((PurchaseBoosterPackRequestEvent) event)
				.getPurchaseBoosterPackRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int boosterPackId = reqProto.getBoosterPackId();
		Date now = new Date(reqProto.getClientTime());
		Timestamp nowTimestamp = new Timestamp(now.getTime());
		boolean buyingInBulk = reqProto.getBuyingInBulk();
		int gemsSpent = reqProto.getGemsSpent();
		int gachaCreditsChange = reqProto.getGachaCreditsChange();

		boolean freeBoosterPack = reqProto.getDailyFreeBoosterPack();

		log.info("reqProto: {}", reqProto);

		//values to send to client
		PurchaseBoosterPackResponseProto.Builder resBuilder = PurchaseBoosterPackResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);

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
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setPurchaseBoosterPackResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			PurchaseBoosterPackAction pbpa = new PurchaseBoosterPackAction(
					userId, boosterPackId, now, nowTimestamp, freeBoosterPack,
					timeUtils, clanGiftRewardsRetrieveUtils,
					userClanRetrieveUtils,
					userRetrieveUtils, boosterPackRetrieveUtils,
					boosterItemRetrieveUtils, itemForUserRetrieveUtil,
					monsterStuffUtils, updateUtil, miscMethods, monsterLevelInfoRetrieveUtils,
					monsterRetrieveUtils, buyingInBulk, rewardRetrieveUtils, insertUtil,
					serverToggleRetrieveUtils, boosterItemUtils, gemsSpent, gachaCreditsChange,
					createInfoProtoUtils, historyUtils);

			pbpa.execute(resBuilder);

			List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();

			if (PurchaseBoosterPackStatus.SUCCESS
					.equals(resBuilder.getStatus())) {
				//assume user only purchases 1 item. NEED TO LET CLIENT KNOW THE PRIZE
				itemsUserReceives = pbpa
						.getItemsUserReceives();

				resBuilder.addAllPrize(convertBoosterItemIntoProtos(itemsUserReceives));

				UserRewardProto urp = createInfoProtoUtils.createUserRewardProto(pbpa.getAra().getNuOrUpdatedItems(),
						pbpa.getAra().getNuOrUpdatedMonsters(), pbpa.getAra().getGemsGained(),
						pbpa.getAra().getCashGained(), pbpa.getAra().getOilGained(), pbpa.getGachaCreditsReward(), null);

				resBuilder.setReward(urp);

			}

			//check if setting the items the user won
			PurchaseBoosterPackResponseProto resProto = resBuilder.build();
			PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setPurchaseBoosterPackResponseProto(resProto);
			server.writeEvent(resEvent);

			if (PurchaseBoosterPackStatus.SUCCESS
					.equals(resBuilder.getStatus())) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								pbpa.getUser(), null, null);

				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(userId, nowTimestamp, pbpa);

				//just assume user can only buy one booster pack at a time
				writeToBoosterPackPurchaseHistory(userId, boosterPackId,
						itemsUserReceives,
						resBuilder.getReward().getUpdatedOrNewMonstersList(), nowTimestamp);
				//				sendBoosterPurchaseMessage(user, aPack, itemsUserReceives);
			}
		} catch (Exception e) {
			log.error(
					"exception in PurchaseBoosterPackController processEvent",
					e);
			// don't let the client hang
			try {
				resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
				PurchaseBoosterPackResponseEvent resEvent = new PurchaseBoosterPackResponseEvent(
						senderProto.getUserUuid());
				resEvent.setTag(event.getTag());
				resEvent.setPurchaseBoosterPackResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SellUserMonsterController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	public List<BoosterItemProto> convertBoosterItemIntoProtos(List<BoosterItem> itemsUserReceives) {
		List<BoosterItemProto> bipList = new ArrayList<BoosterItemProto>();

		for(BoosterItem bi : itemsUserReceives) {
			BoosterItemProto bip = createInfoProtoUtils.createBoosterItemProto(bi, rewardRetrieveUtils);
			bipList.add(bip);
		}
		return bipList;
	}

	private void sendBoosterPurchaseMessage(User user, BoosterPack aPack,
			List<BoosterItem> itemsUserReceives) {
		//    Map<Integer, Monster> equipMap = MonsterRetrieveUtils.getMonsterIdsToMonster();
		//    Date d = new Date();
		//    for (BoosterItem bi : itemsUserReceives) {
		//      Monster eq = equipMap.get(bi.getEquipId());
		//      if (eq.getRarity().compareTo(Rarity.SUPERRARE) >= 0) {
		//        RareBoosterPurchaseProto r = CreateInfoProtoUtils.createRareBoosterPurchaseProto(aPack, user, eq, d);
		//
		//        goodEquipsRecievedFromBoosterPacks.add(0, r);
		//        //remove older messages
		//        try {
		//          while(goodEquipsRecievedFromBoosterPacks.size() > BOOSTER_PURCHASES_MAX_SIZE) {
		//            goodEquipsRecievedFromBoosterPacks.remove(BOOSTER_PURCHASES_MAX_SIZE);
		//          }
		//        } catch(Exception e) {
		//          log.error("Error adding rare booster purchase.", e);
		//        }
		//
		//        ReceivedRareBoosterPurchaseResponseProto p = ReceivedRareBoosterPurchaseResponseProto.newBuilder().setRareBoosterPurchase(r).build();
		//        ReceivedRareBoosterPurchaseResponseEvent e = new ReceivedRareBoosterPurchaseResponseEvent(user.getId());
		//        e.setReceivedRareBoosterPurchaseResponseProto(p);
		//        eventWriter.processGlobalChatResponseEvent(e);
		//      }
		//    }
	}

	//  private int getNumEquipsPurchasedToday(int userId, int boosterPackId,
	//      DateTime startOfDayInLA) {
	//    //get the time at the start of the day in UTC
	//    DateTimeZone utcTZ = DateTimeZone.UTC;
	//    DateTime startOfDayInLAInUtc = startOfDayInLA.withZone(utcTZ);
	//    Timestamp startTime = new Timestamp(startOfDayInLAInUtc.toDate().getTime());
	//
	//    int numPurchased = UserBoosterPackRetrieveUtils
	//        .getNumPacksPurchasedAfterDateForUserAndPackId(userId, boosterPackId, startTime);
	//
	//    return numPurchased;
	//  }

	private void writeToUserCurrencyHistory(String userId, Timestamp date,
			PurchaseBoosterPackAction pbpa) {
		miscMethods.writeToUserCurrencyOneUser(userId, date,
				pbpa.getAra().getCurrencyDeltas(), pbpa.getAra().getPreviousCurrencies(),
				pbpa.getAra().getCurrentCurrencies(), pbpa.getAra().getReasons(),
				pbpa.getAra().getDetails());
	}

	private void writeToBoosterPackPurchaseHistory(String userId,
			int boosterPackId, List<BoosterItem> itemsUserReceives,
			List<FullUserMonsterProto> fumpList, Timestamp timeOfPurchase) {
		//just assuming there is one Booster Item
		if (itemsUserReceives.isEmpty()) {
			return;
		}
		BoosterItem bi = itemsUserReceives.get(0);

		List<String> userMonsterIds = monsterStuffUtils
				.getUserMonsterIds(fumpList);

//		int num = insertUtil.insertIntoBoosterPackPurchaseHistory(
//				userId, boosterPackId, timeOfPurchase, bi, userMonsterIds);
//
//		log.info(
//				"wrote to booster pack history!!!! \t numInserted={}\t boosterItem={}",
//				num, itemsUserReceives);
	}

	public IList<RareBoosterPurchaseProto> getGoodEquipsRecievedFromBoosterPacks() {
		return goodEquipsRecievedFromBoosterPacks;
	}

	public void setGoodEquipsRecievedFromBoosterPacks(
			IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks) {
		this.goodEquipsRecievedFromBoosterPacks = goodEquipsRecievedFromBoosterPacks;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

}
