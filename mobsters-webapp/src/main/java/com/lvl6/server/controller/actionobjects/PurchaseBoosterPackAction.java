package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserCurrencyHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserCurrencyHistoryPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto.BoosterPackType;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.Builder;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.PurchaseBoosterPackStatus;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.BoosterItemUtils;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.ResourceUtil;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class PurchaseBoosterPackAction {

	private static final Logger log = LoggerFactory
			.getLogger(PurchaseBoosterPackAction.class);

	private String userId;
	private int boosterPackId;
	private Date now;
	private Timestamp clientTime;
	private boolean freeBoosterPack;
	private boolean buyingInBulk;
	private int gemsSpent;
	private int gachaCreditsChange;
	private TimeUtils timeUtil;
	private GiftRetrieveUtils giftRetrieveUtil;
	private GiftRewardRetrieveUtils giftRewardRetrieveUtils;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private UserRetrieveUtils2 userRetrieveUtil;
	private BoosterPackRetrieveUtils boosterPackRetrieveUtils;
	private BoosterItemRetrieveUtils boosterItemRetrieveUtils;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private UpdateUtil updateUtil;
	private MiscMethods miscMethods;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private MonsterRetrieveUtils monsterRetrieveUtils;
	private RewardRetrieveUtils rewardRetrieveUtils;
	private InsertUtil insertUtil;
	private ServerToggleRetrieveUtils serverToggleRetrieveUtils;
	private BoosterItemUtils boosterItemUtils;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private HistoryUtils historyUtils;
	private ResourceUtil resourceUtil;

	public PurchaseBoosterPackAction(String userId, int boosterPackId,
			Date now, Timestamp clientTime, boolean freeBoosterPack,
			boolean buyingInBulk, int gemsSpent, int gachaCreditsChange,
			TimeUtils timeUtil,
			GiftRetrieveUtils giftRetrieveUtil,
			GiftRewardRetrieveUtils giftRewardRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			UserRetrieveUtils2 userRetrieveUtil,
			BoosterPackRetrieveUtils boosterPackRetrieveUtils,
			BoosterItemRetrieveUtils boosterItemRetrieveUtils,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			UpdateUtil updateUtil, MiscMethods miscMethods,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			MonsterRetrieveUtils monsterRetrieveUtils,
			RewardRetrieveUtils rewardRetrieveUtils,
			InsertUtil insertUtil,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils,
			BoosterItemUtils boosterItemUtils,
			CreateInfoProtoUtils createInfoProtoUtils,
			HistoryUtils historyUtils, ResourceUtil resourceUtil) {
		super();
		this.userId = userId;
		this.boosterPackId = boosterPackId;
		this.clientTime = clientTime;
		this.now = now;
		this.freeBoosterPack = freeBoosterPack;
		this.buyingInBulk = buyingInBulk;
		this.gemsSpent = gemsSpent;
		this.gachaCreditsChange = gachaCreditsChange;
		this.timeUtil = timeUtil;
		this.giftRetrieveUtil = giftRetrieveUtil;
		this.giftRewardRetrieveUtils = giftRewardRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.userRetrieveUtil = userRetrieveUtil;
		this.boosterPackRetrieveUtils = boosterPackRetrieveUtils;
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.updateUtil = updateUtil;
		this.miscMethods = miscMethods;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.monsterRetrieveUtils = monsterRetrieveUtils;
		this.rewardRetrieveUtils = rewardRetrieveUtils;
		this.insertUtil = insertUtil;
		this.serverToggleRetrieveUtils = serverToggleRetrieveUtils;
		this.boosterItemUtils = boosterItemUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.historyUtils = historyUtils;
		this.resourceUtil = resourceUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class PurchaseBoosterPackResource {
	//
	//
	//		public PurchaseBoosterPackResource() {
	//
	//		}
	//	}
	//
	//	public PurchaseBoosterPackResource execute() {
	//
	//	}

	//derived state
	private User user;
	private BoosterPack aPack;
	private boolean riggedPack;
	private int boosterPackIdPurchased;
	private Map<Integer, BoosterItem> boosterItemIdsToBoosterItems;
	private int prevUserGachaCredits;
	private int prevUserGems;
	private List<BoosterItem> itemsUserReceives;
	private List<Reward> listOfRewards;
	private List<Integer> rewardIds;
	private List<ItemForUser> ifuList;
	private AwardRewardAction ara;
	private UserCurrencyHistoryDao uchDao;

	private int gemReward;
	private int gachaCreditsReward;

	public void execute(Builder resBuilder) {
		setUpDaos();
		resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);
		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);
		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(PurchaseBoosterPackStatus.SUCCESS);

	}
	
	public void setUpDaos() {
		//Configuration config = new DefaultConfiguration().set(DBConnection.get().getConnection()).set(SQLDialect.MYSQL);
		uchDao = AppContext.getApplicationContext().getBean(UserCurrencyHistoryDao.class);//TODO: These actions should be created in spring
		//I will modify them to support autowiring
	}

	private boolean verifySyntax(Builder resBuilder) {
		if (gemsSpent < 0) {
			log.warn("client sent negative gems. {}. Making positive", gemsSpent);
			gemsSpent = Math.abs(gemsSpent);
		}

		if (gachaCreditsChange >= 0 && gemsSpent <= 0) {
			log.warn("client sent nonnegative gachaCredits={}, nonpositive gems={}",
					gachaCreditsChange, gemsSpent);
//			gachaCreditsChange = -1 * gachaCreditsChange;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no user with id={}", userId);
			return false;
		}
		prevUserGems = user.getGems();
		prevUserGachaCredits = user.getGachaCredits();
		if (!verifyRiggedOrUnriggedBoosterPack(resBuilder)) {
			return false;
		}

		if (!verifyBoosterItems(resBuilder)) {
			return false;
		}

		if (freeBoosterPack) {
			if (!validFreeBoosterPack(resBuilder)) {
				return false;
		}

		} else {
			if ( !resourceUtil.hasEnoughGems(user, gemsSpent) ) {
				resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
				return false;
			} else if ( !resourceUtil.hasEnoughGachaCredits(user, gachaCreditsChange) ) {
				resBuilder.setStatus(
						PurchaseBoosterPackStatus.FAIL_INSUFFICIENT_GACHA_CREDITS);
			return false;
		}
		}

		return true;
	}

	private boolean verifyRiggedOrUnriggedBoosterPack(Builder resBuilder) {
		boolean legitPack = verifyBoosterPack(resBuilder, boosterPackId);
		if (!legitPack) {
			log.error("verifyBoosterPack1, probably missing booster pack data.");
			return false;
		}

		//now check if need to rig booster pack
		boosterPackIdPurchased = 0;
		String type = aPack.getType();

		if (!user.isBoughtRiggedBoosterPack()
				&& BoosterPackType.BASIC.name().equals(type))
		{
			//when user buys the lowest rated booster pack and hasn't
			//bought a rigged booster pack, rig the purchase
			log.info("rigging booster pack purchase. boosterPack={}, user={}",
					aPack, user);
			boosterPackIdPurchased = aPack.getRiggedId();
			riggedPack = true;
			legitPack = verifyBoosterPack(resBuilder, boosterPackIdPurchased);
		} else {
			riggedPack = false;
			boosterPackIdPurchased = boosterPackId;
		}

		if (!legitPack) {
			log.error("verifyBoosterPack2, rigged booster pack probably missing data");
		}

		return legitPack;
		}

	private boolean verifyBoosterPack(Builder resBuilder, int bpId) {
		aPack = boosterPackRetrieveUtils.getBoosterPackForBoosterPackId(bpId);

		if (null == aPack) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no BoosterPack for id={}", bpId);
			return false;
		}

		return true;
	}

	private boolean verifyBoosterItems(Builder resBuilder) {
		boosterItemIdsToBoosterItems = boosterItemRetrieveUtils
				.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackIdPurchased);

		if (null == boosterItemIdsToBoosterItems
				|| boosterItemIdsToBoosterItems.isEmpty()) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no booster items={}", boosterItemIdsToBoosterItems);
			return false;
		}
		return true;
	}

	private boolean validFreeBoosterPack(Builder resBuilder) {
		Date lastFreeDate = user.getLastFreeBoosterPackTime();
		if (null == lastFreeDate) {
			return true;
		}
		if (!timeUtil.isFirstEarlierThanSecond(lastFreeDate, now)) {
			// lastFreeDate is not earlier than now
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error(
					"client incorrectly says time now={} is before lastFreeBoosterPackDate={}",
					now, lastFreeDate);
			return false;

		} else if (Math.abs(timeUtil.numDaysDifference(lastFreeDate, now)) == 0) {
			// lastFreeDate is earlier than now but
			// lastFreeDate is on same day as now
			log.error(
					"client already received free booster pack today. lastFreeBoosterPackDate={}, now={}",
					lastFreeDate, now);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		selectBoosterItems();

		boolean legit = boosterItemUtils.checkIfMonstersExist(itemsUserReceives,
				monsterRetrieveUtils, rewardRetrieveUtils);

		if (!legit) {
			log.error("invalid booster items, {}", itemsUserReceives);
			return false;
		}

		processBoosterPacksPurchased();

		updateUserCurrency();

		return true;
	}

	public void selectBoosterItems() {
		int numBoosterItemsUserWants = 1;
		if(buyingInBulk) {
			numBoosterItemsUserWants = ControllerConstants.BOOSTER_PACK__AMOUNT_RECEIVED_FROM_BULK_PURCHASE;
		}

		log.info("numBoosterItemsUserWants: {}", numBoosterItemsUserWants);
		itemsUserReceives = boosterItemUtils.determineBoosterItemsUserReceives(
				numBoosterItemsUserWants, boosterItemIdsToBoosterItems, serverToggleRetrieveUtils);

	}

	public void processBoosterPacksPurchased() {

		Map<Integer, Reward> idsToRewards = rewardRetrieveUtils.getRewardIdsToRewards();

		listOfRewards = new ArrayList<Reward>();
		rewardIds = new ArrayList<Integer>();
		for(BoosterItem bi : itemsUserReceives) {
			int rewardId = bi.getRewardId();
			Reward r = idsToRewards.get(rewardId);
			listOfRewards.add(r);
			rewardIds.add(rewardId);
		}

		String awardReasonDetail = "booster pack id: " + boosterPackId;
		ara = new AwardRewardAction(userId, user, 0, 0, now,
				"booster packs id " + aPack.getId(), listOfRewards,
				userRetrieveUtil, itemForUserRetrieveUtil, insertUtil,
				updateUtil, monsterStuffUtils, monsterLevelInfoRetrieveUtils,
				giftRetrieveUtil,
				giftRewardRetrieveUtils, rewardRetrieveUtils,
				userClanRetrieveUtils, createInfoProtoUtils, awardReasonDetail);

		ara.execute();

	}

	//TODO: allow multiple free packs?
	private void updateUserCurrency() {
		gemReward = boosterItemUtils.determineGemReward(itemsUserReceives, rewardRetrieveUtils);
		gachaCreditsReward = boosterItemUtils.determineGachaCreditsReward(itemsUserReceives, rewardRetrieveUtils);
		
		if (freeBoosterPack) {
			gachaCreditsChange = 0;
		}
		gachaCreditsChange += gachaCreditsReward;

		int gemChange = (-1 * gemsSpent) + gemReward;

		//update user's flag concerning whether or not he's bought a rigged pack
		//update user's last free booster pack time
		boolean updated = user.updateBoughtBoosterPack(gemChange, gachaCreditsChange, now,
				freeBoosterPack, riggedPack);
		log.info("updated, user bought boosterPack? {}", updated);
		
		saveCurrencyHistory(gemChange);
	}

	private void saveCurrencyHistory(int gemChange) {
		List<UserCurrencyHistoryPojo> uchList = new ArrayList<UserCurrencyHistoryPojo>();
		String detail = String.format("rewardIds:%s", rewardIds);
		UserCurrencyHistoryPojo uch = historyUtils
				.createUserCurrencyHistory(userId, now, MiscMethods.gachaCredits,
						gachaCreditsChange, prevUserGachaCredits,
						prevUserGachaCredits + gachaCreditsChange,
						ControllerConstants.UCHRFC__SPIN_GACHA,
						detail);
		uchList.add(uch);

		if(gemChange != 0) {
			uch = historyUtils
					.createUserCurrencyHistory(userId, now, MiscMethods.gems,
							gemChange, prevUserGems,
							prevUserGems + gemChange,
							ControllerConstants.UCHRFC__SPIN_GACHA,
							detail);
			uchList.add(uch);
		}

		uchDao.insert(uchList);
	}


	public User getUser() {
		return user;
	}

	public List<BoosterItem> getItemsUserReceives() {
		return itemsUserReceives;
	}

	public List<ItemForUser> getIfuList() {
		return ifuList;
	}

	public String getUserId() {
		return userId;
	}

	public BoosterPack getaPack() {
		return aPack;
	}

	public boolean isRiggedPack() {
		return riggedPack;
	}

	public int getBoosterPackIdPurchased() {
		return boosterPackIdPurchased;
	}

	public Map<Integer, BoosterItem> getBoosterItemIdsToBoosterItems() {
		return boosterItemIdsToBoosterItems;
	}

	public List<Reward> getListOfRewards() {
		return listOfRewards;
	}

	public AwardRewardAction getAra() {
		return ara;
	}

	public int getGemReward() {
		return gemReward;
	}

	public int getGachaCreditsChange() {
		return gachaCreditsChange;
	}

	public void setGachaCreditsChange(int gachaCreditsChange) {
		this.gachaCreditsChange = gachaCreditsChange;
	}

	public int getGachaCreditsReward() {
		return gachaCreditsReward;
	}

}
