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
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto.BoosterPackType;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.Builder;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.PurchaseBoosterPackStatus;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.BoosterItemUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class PurchaseBoosterPackAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private int boosterPackId;
	private Date now;
	private Timestamp clientTime;
	private boolean freeBoosterPack;
	private TimeUtils timeUtil;
	private UserRetrieveUtils2 userRetrieveUtil;
	private BoosterPackRetrieveUtils boosterPackRetrieveUtils;
	private BoosterItemRetrieveUtils boosterItemRetrieveUtils;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private UpdateUtil updateUtil;
	private MiscMethods miscMethods;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private MonsterRetrieveUtils monsterRetrieveUtils;
	private boolean buyingInBulk;
	private RewardRetrieveUtils rewardRetrieveUtils;
	private InsertUtil insertUtil;
	private ServerToggleRetrieveUtils serverToggleRetrieveUtils;
	private BoosterItemUtils boosterItemUtils;

	public PurchaseBoosterPackAction(String userId, int boosterPackId,
			Date now, Timestamp clientTime, boolean freeBoosterPack,
			TimeUtils timeUtil, UserRetrieveUtils2 userRetrieveUtil,
			BoosterPackRetrieveUtils boosterPackRetrieveUtils,
			BoosterItemRetrieveUtils boosterItemRetrieveUtils,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			UpdateUtil updateUtil, MiscMethods miscMethods,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			MonsterRetrieveUtils monsterRetrieveUtils,
			boolean buyingInBulk, RewardRetrieveUtils rewardRetrieveUtils,
			InsertUtil insertUtil,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils, 
			BoosterItemUtils boosterItemUtils) {
		super();
		this.userId = userId;
		this.boosterPackId = boosterPackId;
		this.clientTime = clientTime;
		this.now = now;
		this.freeBoosterPack = freeBoosterPack;
		this.timeUtil = timeUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.boosterPackRetrieveUtils = boosterPackRetrieveUtils;
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.updateUtil = updateUtil;
		this.miscMethods = miscMethods;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.monsterRetrieveUtils = monsterRetrieveUtils;
		this.buyingInBulk = buyingInBulk;
		this.rewardRetrieveUtils = rewardRetrieveUtils;
		this.insertUtil = insertUtil;
		this.serverToggleRetrieveUtils = serverToggleRetrieveUtils;
		this.boosterItemUtils = boosterItemUtils;
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
	private int gemPrice;
	private Map<Integer, BoosterItem> boosterItemIdsToBoosterItems;
	private int userGems;
	private List<BoosterItem> itemsUserReceives;
	private List<Reward> listOfRewards;
	private List<ItemForUser> ifuList;
	private AwardRewardAction ara;

	private int gemReward;
	private int gemChange;


	public void execute(Builder resBuilder) {
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
			//			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			return;
		}

		resBuilder.setStatus(PurchaseBoosterPackStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no user with id={}", userId);
			return false;
		}

		boolean legitPack = verifyBoosterPack(resBuilder, boosterPackId);
		if (!legitPack) {
			return false;
		}
		
		if (boosterPackIdPurchased > 0) {
			legitPack = verifyBoosterPack(resBuilder, boosterPackIdPurchased);
		}

		if (!legitPack) {
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		boosterPackIdPurchased = 0;
		String type = aPack.getType();

		if (!user.isBoughtRiggedBoosterPack()
				&& BoosterPackType.BASIC.name().equals(type)) {
			//when user buys the lowest rated booster pack and hasn't
			//bought a rigged booster pack, rig the purchase
			log.info("rigging booster pack purchase. boosterPack={}, user={}",
					aPack, user);
			boosterPackIdPurchased = aPack.getRiggedId();
			riggedPack = true;
		} else {
			riggedPack = false;
			boosterPackIdPurchased = boosterPackId;
		}

		boosterItemIdsToBoosterItems = boosterItemRetrieveUtils
				.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackIdPurchased);

		if (null == boosterItemIdsToBoosterItems
				|| boosterItemIdsToBoosterItems.isEmpty()) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no booster items={}", boosterItemIdsToBoosterItems);
			return false;
		}

		userGems = user.getGems();

		//check if user can afford to buy however many more user wants to buy
		if (!freeBoosterPack) {
			if (userGems < gemPrice) {
				resBuilder
						.setStatus(PurchaseBoosterPackStatus.FAIL_INSUFFICIENT_GEMS);
				return false;
			}
		} else {
			boolean legitFree = verifyFreeBoosterPack(resBuilder);
			if (!legitFree) {
				return false;
			}
		}

		return true;
	}

	private boolean verifyBoosterPack(Builder resBuilder, int bpId) {
		aPack = boosterPackRetrieveUtils.getBoosterPackForBoosterPackId(bpId);

		if (null == aPack) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no BoosterPack for id={}", bpId);
			return false;
		}

		gemPrice = aPack.getGemPrice();

		if(buyingInBulk) {
			gemPrice = gemPrice * ControllerConstants.BOOSTER_PACK__AMOUNT_NEEDED_TO_PURCHASE;
		}

		return true;
	}

	private boolean verifyFreeBoosterPack(Builder resBuilder) {
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

			log.error(String
					.format("client already received free booster pack today. lastFreeBoosterPackDate=%s, now=%s",
							lastFreeDate, now));
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		selectBoosterItems();

		boolean legit = boosterItemUtils.checkIfMonstersExist(itemsUserReceives, monsterRetrieveUtils,
				rewardRetrieveUtils);

		if (!legit) {
			log.error("illegal to verify booster items, {}", itemsUserReceives);
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

		log.info("numBoosterItemsUserWants: " + numBoosterItemsUserWants);
		itemsUserReceives = boosterItemUtils.determineBoosterItemsUserReceives(
				numBoosterItemsUserWants, boosterItemIdsToBoosterItems, serverToggleRetrieveUtils);

	}

	public void processBoosterPacksPurchased() {

		Map<Integer, Reward> idsToRewards = rewardRetrieveUtils.getRewardIdsToRewards();

		listOfRewards = new ArrayList<Reward>();
		for(BoosterItem bi : itemsUserReceives) {
			Reward r = idsToRewards.get(bi.getRewardId());
			listOfRewards.add(r);
			log.info("size of listofrewards: " + listOfRewards.size());
		}

		ara = new AwardRewardAction(userId, user, 0, 0, now, "booster packs", listOfRewards,
				userRetrieveUtil, itemForUserRetrieveUtil, insertUtil, updateUtil, monsterStuffUtils,
				monsterLevelInfoRetrieveUtils);

		ara.execute();
		
	}

	//TODO: allow multiple free packs?
	private void updateUserCurrency() {
		gemReward = ara.getGemsGained();

		gemChange = -1 * gemPrice;
		if (freeBoosterPack) {
			gemChange = 0;
		}
		gemChange += gemReward;

		//update user's flag concerning whether or not he's bought a rigged pack
		//update user's last free booster pack time
		boolean updated = user.updateBoughtBoosterPack(gemChange, now,
				freeBoosterPack, riggedPack);
		log.info("updated, user bought boosterPack? {}", updated);
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

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getBoosterPackId() {
		return boosterPackId;
	}

	public void setBoosterPackId(int boosterPackId) {
		this.boosterPackId = boosterPackId;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public Timestamp getClientTime() {
		return clientTime;
	}

	public void setClientTime(Timestamp clientTime) {
		this.clientTime = clientTime;
	}

	public boolean isFreeBoosterPack() {
		return freeBoosterPack;
	}

	public void setFreeBoosterPack(boolean freeBoosterPack) {
		this.freeBoosterPack = freeBoosterPack;
	}

	public TimeUtils getTimeUtil() {
		return timeUtil;
	}

	public void setTimeUtil(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public BoosterPackRetrieveUtils getBoosterPackRetrieveUtils() {
		return boosterPackRetrieveUtils;
	}

	public void setBoosterPackRetrieveUtils(
			BoosterPackRetrieveUtils boosterPackRetrieveUtils) {
		this.boosterPackRetrieveUtils = boosterPackRetrieveUtils;
	}

	public BoosterItemRetrieveUtils getBoosterItemRetrieveUtils() {
		return boosterItemRetrieveUtils;
	}

	public void setBoosterItemRetrieveUtils(
			BoosterItemRetrieveUtils boosterItemRetrieveUtils) {
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public MiscMethods getMiscMethods() {
		return miscMethods;
	}

	public void setMiscMethods(MiscMethods miscMethods) {
		this.miscMethods = miscMethods;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

	public MonsterRetrieveUtils getMonsterRetrieveUtils() {
		return monsterRetrieveUtils;
	}

	public void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils) {
		this.monsterRetrieveUtils = monsterRetrieveUtils;
	}

	public boolean isBuyingInBulk() {
		return buyingInBulk;
	}

	public void setBuyingInBulk(boolean buyingInBulk) {
		this.buyingInBulk = buyingInBulk;
	}

	public RewardRetrieveUtils getRewardRetrieveUtils() {
		return rewardRetrieveUtils;
	}

	public void setRewardRetrieveUtils(RewardRetrieveUtils rewardRetrieveUtils) {
		this.rewardRetrieveUtils = rewardRetrieveUtils;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public BoosterPack getaPack() {
		return aPack;
	}

	public void setaPack(BoosterPack aPack) {
		this.aPack = aPack;
	}

	public boolean isRiggedPack() {
		return riggedPack;
	}

	public void setRiggedPack(boolean riggedPack) {
		this.riggedPack = riggedPack;
	}

	public int getBoosterPackIdPurchased() {
		return boosterPackIdPurchased;
	}

	public void setBoosterPackIdPurchased(int boosterPackIdPurchased) {
		this.boosterPackIdPurchased = boosterPackIdPurchased;
	}

	public int getGemPrice() {
		return gemPrice;
	}

	public void setGemPrice(int gemPrice) {
		this.gemPrice = gemPrice;
	}

	public Map<Integer, BoosterItem> getBoosterItemIdsToBoosterItems() {
		return boosterItemIdsToBoosterItems;
	}

	public void setBoosterItemIdsToBoosterItems(
			Map<Integer, BoosterItem> boosterItemIdsToBoosterItems) {
		this.boosterItemIdsToBoosterItems = boosterItemIdsToBoosterItems;
	}

	public int getUserGems() {
		return userGems;
	}

	public void setUserGems(int userGems) {
		this.userGems = userGems;
	}

	public List<Reward> getListOfRewards() {
		return listOfRewards;
	}

	public void setListOfRewards(List<Reward> listOfRewards) {
		this.listOfRewards = listOfRewards;
	}

	public AwardRewardAction getAra() {
		return ara;
	}

	public void setAra(AwardRewardAction ara) {
		this.ara = ara;
	}

	public int getGemReward() {
		return gemReward;
	}

	public void setGemReward(int gemReward) {
		this.gemReward = gemReward;
	}

	public int getGemChange() {
		return gemChange;
	}

	public void setGemChange(int gemChange) {
		this.gemChange = gemChange;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setItemsUserReceives(List<BoosterItem> itemsUserReceives) {
		this.itemsUserReceives = itemsUserReceives;
	}

	public void setIfuList(List<ItemForUser> ifuList) {
		this.ifuList = ifuList;
	}


}
