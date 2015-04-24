package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
public class BoosterItemUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public static boolean checkIfMonstersExist(
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

	public static int determineGemReward(List<BoosterItem> boosterItems) {
		int gemReward = 0;
		for (BoosterItem bi : boosterItems) {
			gemReward += bi.getGemReward();
		}

		return gemReward;
	}

	//monsterIdsToNumPieces or completeUserMonsters will be populated
	public static String createUpdateUserMonsterArguments(String userId,
			int boosterPackId, List<BoosterItem> boosterItems,
			Map<Integer, Integer> monsterIdsToNumPieces,
			List<MonsterForUser> completeUserMonsters, Date now,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			MonsterRetrieveUtils monsterRetrieveUtils,
			MonsterStuffUtils monsterStuffUtils) {
		StringBuilder sb = new StringBuilder();
		sb.append(ControllerConstants.MFUSOP__BOOSTER_PACK);
		sb.append(" ");
		sb.append(boosterPackId);
		sb.append(" boosterItemIds ");

		List<Integer> boosterItemIds = new ArrayList<Integer>();
		for (BoosterItem item : boosterItems) {
			Integer id = item.getId();
			Integer monsterId = item.getMonsterId();

			//only keep track of the booster item ids that are a monster reward
			if (monsterId <= 0) {
				continue;
			}
			if (item.isComplete()) {
				//create a "complete" user monster
				boolean hasAllPieces = true;
				boolean isComplete = true;
				Monster monzter = monsterRetrieveUtils
						.getMonsterForMonsterId(monsterId);
				MonsterForUser newUserMonster = monsterStuffUtils
						.createNewUserMonster(userId,
								monzter.getNumPuzzlePieces(), monzter, now,
								hasAllPieces, isComplete, monsterLevelInfoRetrieveUtils);

				//return this monster in the argument list completeUserMonsters, so caller
				//can use it
				completeUserMonsters.add(newUserMonster);

			} else {
				monsterIdsToNumPieces.put(monsterId, item.getNumPieces());
			}
			boosterItemIds.add(id);
		}
		if (!boosterItemIds.isEmpty()) {
			String boosterItemIdsStr = StringUtils.csvList(boosterItemIds);
			sb.append(boosterItemIdsStr);
		}

		log.info(sb.toString());
		return sb.toString();
	}

	public static List<ItemForUser> awardBoosterItemItemRewards(String userId,
			List<BoosterItem> itemsUserReceives,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			UpdateUtil updateUtil) {
		List<ItemForUser> ifuList = BoosterItemUtils
				.calculateBoosterItemItemRewards(userId, itemsUserReceives,
						itemForUserRetrieveUtil);

		log.info("ifuList={}", ifuList);
		if (null != ifuList && !ifuList.isEmpty()) {
			int numUpdated = updateUtil.updateItemForUser(ifuList);
			log.info("items numUpdated={}", numUpdated);
			return ifuList;
		} else {
			return null;
		}

	}

	public static List<ItemForUser> calculateBoosterItemItemRewards(
			String userId, List<BoosterItem> itemsUserReceives,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();

		for (BoosterItem bi : itemsUserReceives) {
			int itemId = bi.getItemId();
			int itemQuantity = bi.getItemQuantity();

			if (itemId <= 0 || itemQuantity <= 0) {
				continue;
			}

			//user could have gotten multiple of the same BoosterItem
			int newQuantity = itemQuantity;
			if (itemIdToQuantity.containsKey(itemId)) {
				newQuantity += itemIdToQuantity.get(itemId);
			}
			itemIdToQuantity.put(itemId, newQuantity);
		}

		return calculateItemRewards(userId, itemForUserRetrieveUtil,
				itemIdToQuantity);
	}

	public static List<ItemForUser> calculateItemRewards(String userId,
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


}
