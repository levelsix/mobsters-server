package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Item;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemSecretGiftForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventItemProto.RedeemSecretGiftResponseProto.Builder;
import com.lvl6.proto.EventItemProto.RedeemSecretGiftResponseProto.RedeemSecretGiftStatus;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.ItemSecretGiftForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class RedeemSecretGiftAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<String> idsRedeemed;
	private Timestamp clientTime;
	private ItemSecretGiftForUserRetrieveUtil itemSecretGiftForUserRetrieveUtil;
	private UserRetrieveUtils2 userRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private DeleteUtil deleteUtil;
	private UpdateUtil updateUtil;
	private InsertUtil insertUtil;

	public RedeemSecretGiftAction(
		String userId,
		List<String> itemIdsRedeemed,
		Timestamp clientTime,
		ItemSecretGiftForUserRetrieveUtil itemSecretGiftForUserRetrieveUtil,
		UserRetrieveUtils2 userRetrieveUtil,
		ItemForUserRetrieveUtil itemForUserRetrieveUtil,
		DeleteUtil deleteUtil,
		UpdateUtil updateUtil,
		InsertUtil insertUtil )
	{
		super();
		this.userId = userId;
		this.idsRedeemed = itemIdsRedeemed;
		this.clientTime = clientTime;
		this.itemSecretGiftForUserRetrieveUtil = itemSecretGiftForUserRetrieveUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.deleteUtil = deleteUtil;
		this.updateUtil = updateUtil;
		this.insertUtil = insertUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RedeemSecretGiftResource {
	//		
	//		
	//		public RedeemSecretGiftResource() {
	//			
	//		}
	//	}
	//
	//	public RedeemSecretGiftResource execute() {
	//		
	//	}

	//derived state
	private User user;
	private Map<String, ItemSecretGiftForUser> idToSecretGift;

	private Map<Integer,Integer> itemIdToNuQuantity;
	private Map<Integer, ItemForUser> itemIdToUserItem;
	private List<ItemSecretGiftForUser> gifts;

	//	private Map<String, Integer> currencyDeltas;
	//	private Map<String, Integer> prevCurrencies;
	//	private Map<String, Integer> curCurrencies;
	//	private Map<String, String> reasonsForChanges;
	//	private Map<String, String> details;


	//select items at random to gift to the user
	public static List<ItemSecretGiftForUser> calculateGiftsForUser(String userId,
		int numGifts, long now)
	{
		List<ItemSecretGiftForUser> gifts =
			new ArrayList<ItemSecretGiftForUser>();
		
		//random to select an item
		Random rand = ControllerConstants.RAND;
		
		//chi random to calculate seconds till collection
		ChiSquaredDistribution randChi = ControllerConstants
			.ITEM_SECRET_GIFT_FOR_USER__RANDOM;

		//(round((chisq(df = 4)^3) * 6.5)+329)
		for (int giftI = 0; giftI < numGifts; giftI++) {
			float randFloat = rand.nextFloat();
			Item secretGift = ItemRetrieveUtils.nextItem(randFloat);

			ItemSecretGiftForUser isgfu = new ItemSecretGiftForUser();
			isgfu.setUserId(userId);
			isgfu.setItemId(
				secretGift.getId());
			
			//so the client knows which item came first
			Date newTime = new Date(now + giftI * 1000);
			isgfu.setCreateTime(newTime);
			
			double randDoub = randChi.sample();
			log.info(String.format(
				"randDoub=%s", randDoub));
			
			randDoub = Math.pow(randDoub, 3D);
			log.info(String.format(
				"(randDoub ^ 3)=%s", randDoub));
			
			double waitTimeSecs = randDoub * 6.5 + 329;
			log.info(String.format(
				"uncapped waitTimeSecs=%s", waitTimeSecs));
			
			//(round((chisq(df = 4)^3) * 6.5)+329)
			waitTimeSecs = Math.min(waitTimeSecs, ControllerConstants
				.ITEM_SECRET_GIFT_FOR_USER__MAX_SECS_WAIT_TIME);
//			waitTimeSecs = Math.max(waitTimeSecs, ControllerConstants
//				.ITEM_SECRET_GIFT_FOR_USER__MIN_SECS_WAIT_TIME);
			log.info(String.format(
				"capped waitTimeSecs=%s", waitTimeSecs));
			
			isgfu.setSecsTillCollection( (int)Math.round(waitTimeSecs) );

			log.info(String.format(
				"gift=%s", isgfu));
			
			gifts.add(isgfu);
		}

		return gifts;
	}

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);

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

		resBuilder.setStatus(RedeemSecretGiftStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (null == idsRedeemed || idsRedeemed.isEmpty()) {
			log.error(String.format(
				"invalid itemIdsRedeemed=%s.",
				idsRedeemed));
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		//get the secret gifts to redeem, check to see if they exist
		idToSecretGift = itemSecretGiftForUserRetrieveUtil
			.getSpecificOrAllItemSecretGiftForUserMap(
				userId,
				idsRedeemed);

		if (null == idToSecretGift || idToSecretGift.size() != idsRedeemed.size()) {
			log.info(String.format(
				"inconsistent itemSecretGiftForUser in db: %s and what client asked: %s",
				idToSecretGift, idsRedeemed));
			return false;
		}

		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error(String.format(
				"no user with id=%s", userId));
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		//		prevCurrencies = new HashMap<String, Integer>();
		//		
		//		if (0 != gemsGained) {
		//			prevCurrencies.put(MiscMethods.gems, user.getGems());
		//		}
		//		if (0 != cashGained) {
		//			prevCurrencies.put(MiscMethods.cash, user.getCash());
		//		}
		//		if (0 != oilGained) {
		//			prevCurrencies.put(MiscMethods.oil, user.getOil());
		//		}
		//		

		//delete the SecretGifts
		deleteUtil.deleteItemSecretGifts(userId, idsRedeemed);

		//update the user saying he got the gifts
		user.updateLastSecretGiftCollectTime(clientTime);

		//award the SecretGifts to the user (aggregate by itemId)
		aggregateGifts();
		//determine the new absolute amount of items for this user
		calcNuUserItems();
		//update db
		List<ItemForUser> ifuList = new ArrayList<ItemForUser>();
		ifuList.addAll(itemIdToUserItem.values());
		int numUpdated = updateUtil.updateItemForUser(ifuList);
		log.info(String.format(
			"itemSecretGiftForUser numUpdated: %s, itemIdToUserItem=%s",
			numUpdated, itemIdToUserItem));

		//create new SecretGifts
		gifts = calculateGiftsForUser(userId,
			ControllerConstants.ITEM_SECRET_GIFT_FOR_USER__NUM_NEW_GIFTS,
			clientTime.getTime());
		
		List<String> ids = null;
		if (null != gifts && !gifts.isEmpty()) {
			//save new SecretGifts
			ids = insertUtil.insertIntoItemSecretGiftForUserGetId(gifts);
		}
		
		if (null != ids && ids.size() == gifts.size()) {
			setGiftIds(ids);
		} else {
			log.error(String.format(
				"Error calculating the new SecretGifts. nuGifts=%s, ids=%s",
				gifts, ids));
		}

		//		prepCurrencyHistory();

		return true;
	}

	private void aggregateGifts() {
		itemIdToNuQuantity = new HashMap<Integer, Integer>();

		for (ItemSecretGiftForUser gif : idToSecretGift.values())
		{
			int itemId = gif.getItemId();
			if (!itemIdToNuQuantity.containsKey(itemId)) {
				itemIdToNuQuantity.put(itemId, 0);
			}

			int nuQuantity = itemIdToNuQuantity.get(itemId) + 1;
			itemIdToNuQuantity.put(itemId, nuQuantity);
		}
	}

	private void calcNuUserItems() {
		Collection<Integer> itemIds = itemIdToNuQuantity.keySet();

		itemIdToUserItem = itemForUserRetrieveUtil
			.getSpecificOrAllItemForUserMap(userId, itemIds);

		for (Integer itemId : itemIds)
		{
			if (!itemIdToUserItem.containsKey(itemId))
			{
				ItemForUser nuItemForUser = new ItemForUser(userId, itemId, 0);
				itemIdToUserItem.put(itemId, nuItemForUser);
			}

			//combine quantity user has currently with amount he just got
			ItemForUser ifu = itemIdToUserItem.get(itemId);
			int nuQuantity = ifu.getQuantity() + itemIdToNuQuantity.get(itemId);

			ifu.setQuantity(nuQuantity);
		}

	}

	private void setGiftIds(List<String> ids) {
		for (int index = 0; index < ids.size(); index++) {
			String id = ids.get(index);
			ItemSecretGiftForUser isgfu = gifts.get(index);

			isgfu.setId(id);
		}
	}

	//	private void prepCurrencyHistory()
	//	{
	//		String gems = MiscMethods.gems;
	//		String cash = MiscMethods.cash;
	//		String oil = MiscMethods.oil;
	//		
	//		currencyDeltas = new HashMap<String, Integer>();
	//		curCurrencies = new HashMap<String, Integer>();
	//		reasonsForChanges = new HashMap<String, String>();
	//		if (0 != gemsGained) {
	//			currencyDeltas.put(gems, gemsGained);
	//			curCurrencies.put(gems, user.getGems());
	//			reasonsForChanges.put(gems,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		if (0 != cashGained) {
	//			currencyDeltas.put(cash, cashGained);
	//			curCurrencies.put(cash, user.getCash());
	//			reasonsForChanges.put(cash,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		if (0 != oilGained) {
	//			currencyDeltas.put(oil, oilGained);
	//			curCurrencies.put(oil, user.getOil());
	//			reasonsForChanges.put(oil,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		
	//		details = new HashMap<String, String>();
	//		for (Integer key : itemIdToResourceToQuantities.keySet()) {
	//			String value = itemIdToResourceToQuantities.get(key).toString(); 
	//			
	//			details.put(key.toString(), value);
	//		}
	//	}

	public User getUser() {
		return user;
	}

	public List<ItemSecretGiftForUser> getGifts()
	{
		return gifts;
	}

	//	public Map<String, Integer> getCurrencyDeltas() {
	//		return currencyDeltas;
	//	}
	//	
	//	public Map<String, Integer> getPreviousCurrencies() {
	//		return prevCurrencies;
	//	}
	//
	//	public Map<String, Integer> getCurrentCurrencies() {
	//		return curCurrencies;
	//	}
	//	
	//	public Map<String, String> getReasons() {
	//		return reasonsForChanges;
	//	}
	//	
	//	public Map<String, String> getDetails() {
	//		return details;
	//	}
}
