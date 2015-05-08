package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.BoosterItem;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.Reward;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
public class BoosterItemUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public boolean checkIfMonstersExist(
			List<BoosterItem> itemsUserReceives,
			MonsterRetrieveUtils monsterRetrieveUtils, 
			RewardRetrieveUtils rewardRetrieveUtils) {
		boolean monstersExist = true;

		Map<Integer, Monster> monsterIdsToMonsters = monsterRetrieveUtils
				.getMonsterIdsToMonsters();
		for (BoosterItem bi : itemsUserReceives) {
			Reward r = rewardRetrieveUtils.getRewardById(bi.getRewardId());
			if(r.getType().equalsIgnoreCase(RewardType.MONSTER.name())) {
				int monsterId = r.getStaticDataId();
				if (!monsterIdsToMonsters.containsKey(monsterId)) {
					log.error("This booster item contains nonexistent monsterId. item="
							+ bi);
					monstersExist = false;
				}
			}
		}
		return monstersExist;
	}

	public int determineGemReward(List<BoosterItem> boosterItems, 
			RewardRetrieveUtils rewardRetrieveUtils) {
		int gemReward = 0;
		for (BoosterItem bi : boosterItems) {
			Reward r = rewardRetrieveUtils.getRewardById(bi.getRewardId());
			if(r.getType().equalsIgnoreCase(RewardType.GEMS.name())) {
				gemReward += r.getAmt();
			}
		}

		return gemReward;
	}
	
	public int determineGachaCreditsReward(List<BoosterItem> boosterItems, 
			RewardRetrieveUtils rewardRetrieveUtils) {
		int gachaCreditsReward = 0;
		for (BoosterItem bi : boosterItems) {
			Reward r = rewardRetrieveUtils.getRewardById(bi.getRewardId());
			if(r.getType().equalsIgnoreCase(RewardType.GACHA_CREDITS.name())) {
				gachaCreditsReward += r.getAmt();
			}
		}

		return gachaCreditsReward;
	}

//	//monsterIdsToNumPieces or completeUserMonsters will be populated
//	public static String createUpdateUserMonsterArguments(String userId,
//			int boosterPackId, List<BoosterItem> boosterItems,
//			Map<Integer, Integer> monsterIdsToNumPieces,
//			List<MonsterForUser> completeUserMonsters, Date now,
//			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
//			MonsterRetrieveUtils monsterRetrieveUtils,
//			MonsterStuffUtils monsterStuffUtils) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(ControllerConstants.MFUSOP__BOOSTER_PACK);
//		sb.append(" ");
//		sb.append(boosterPackId);
//		sb.append(" boosterItemIds ");
//	}
		

//	public List<ItemForUser> awardBoosterItemItemRewards(String userId,
//			List<BoosterItem> itemsUserReceives,
//			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
//			UpdateUtil updateUtil) {
//		List<ItemForUser> ifuList = calculateBoosterItemItemRewards(userId, itemsUserReceives,
//						itemForUserRetrieveUtil);
//
//		log.info("ifuList={}", ifuList);
//		if (null != ifuList && !ifuList.isEmpty()) {
//			int numUpdated = updateUtil.updateItemForUser(ifuList);
//			log.info("items numUpdated={}", numUpdated);
//			return ifuList;
//		} else {
//			return null;
//		}
//
//	}

//	public List<ItemForUser> calculateBoosterItemItemRewards(
//			String userId, List<BoosterItem> itemsUserReceives,
//			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
//		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();
//
//		for (BoosterItem bi : itemsUserReceives) {
//			int itemId = bi.getItemId();
//			int itemQuantity = bi.getItemQuantity();
//
//			if (itemId <= 0 || itemQuantity <= 0) {
//				continue;
//			}
//
//			//user could have gotten multiple of the same BoosterItem
//			int newQuantity = itemQuantity;
//			if (itemIdToQuantity.containsKey(itemId)) {
//				newQuantity += itemIdToQuantity.get(itemId);
//			}
//			itemIdToQuantity.put(itemId, newQuantity);
//		}
//
//		return calculateItemRewards(userId, itemForUserRetrieveUtil,
//				itemIdToQuantity);
//	}

	public List<ItemForUser> calculateItemRewards(String userId,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			Map<Integer, Integer> itemIdToQuantity) {
		List<ItemForUser> ifuList = null;
		if (!itemIdToQuantity.isEmpty()) {
			//aggregate rewarded items with user's current items
			Map<Integer, ItemForUser> itemIdToIfu = itemForUserRetrieveUtil
					.getSpecificOrAllItemForUserMap(userId,
							itemIdToQuantity.keySet());

			for (Integer itemId : itemIdToQuantity.keySet()) {
				int newQuantity = itemIdToQuantity.get(itemId);

				ItemForUser ifu = null;
				if (itemIdToIfu.containsKey(itemId)) {
					ifu = itemIdToIfu.get(itemId);
				} else {
					//user might not have the item
					ifu = new ItemForUser(userId, itemId, 0);
					itemIdToIfu.put(itemId, ifu);
				}

				newQuantity += ifu.getQuantity();
				ifu.setQuantity(newQuantity);
			}

			ifuList = new ArrayList<ItemForUser>(itemIdToIfu.values());
		}
		return ifuList;
	}

	//no arguments are modified
	public List<BoosterItem> determineBoosterItemsUserReceives(
			int amountUserWantsToPurchase,
			Map<Integer, BoosterItem> boosterItemIdsToBoosterItemsForPackId,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils) {
		//return value
		List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();

		Collection<BoosterItem> items = boosterItemIdsToBoosterItemsForPackId
				.values();
		List<BoosterItem> itemsList = new ArrayList<BoosterItem>(items);
		float sumOfProbabilities = sumProbabilities(boosterItemIdsToBoosterItemsForPackId
				.values());

		//selecting items at random with replacement
		for (int purchaseN = 0; purchaseN < amountUserWantsToPurchase; purchaseN++) {
			BoosterItem bi = selectBoosterItem(itemsList, sumOfProbabilities,
					serverToggleRetrieveUtils);
			if (null == bi) {
				continue;
			}
			itemsUserReceives.add(bi);
		}

		return itemsUserReceives;
	}
	
	private float sumProbabilities(Collection<BoosterItem> boosterItems) {
		float sumOfProbabilities = 0.0f;
		for (BoosterItem bi : boosterItems) {
			sumOfProbabilities += bi.getChanceToAppear();
		}
		return sumOfProbabilities;
	}

	private BoosterItem selectBoosterItem(List<BoosterItem> itemsList,
			float sumOfProbabilities, 
			ServerToggleRetrieveUtils serverToggleRetrieveUtils) {
		Random rand = new Random();
		float unnormalizedProbabilitySoFar = 0f;
		float randFloat = rand.nextFloat();

		boolean logBoosterItemDetails = serverToggleRetrieveUtils
				.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__LOGGING_BOOSTER_ITEM_SELECTION_DETAILS);
		if (logBoosterItemDetails) {
			log.info(
					"selecting booster item. sumOfProbabilities={} \t randFloat={}",
					sumOfProbabilities, randFloat);
		}

		int size = itemsList.size();
		//for each item, normalize before seeing if it is selected
		for (int i = 0; i < size; i++) {
			BoosterItem item = itemsList.get(i);

			//normalize probability
			unnormalizedProbabilitySoFar += item.getChanceToAppear();
			float normalizedProbabilitySoFar = unnormalizedProbabilitySoFar
					/ sumOfProbabilities;

			if (logBoosterItemDetails) {
				log.info("boosterItem={} \t normalizedProbabilitySoFar={}",
						item, normalizedProbabilitySoFar);
			}

			if (randFloat < normalizedProbabilitySoFar) {
				//we have a winner! current boosterItem is what the user gets
				return item;
			}
		}

		log.error("maybe no boosterItems exist. boosterItems={}", itemsList);
		return null;
	}

}
