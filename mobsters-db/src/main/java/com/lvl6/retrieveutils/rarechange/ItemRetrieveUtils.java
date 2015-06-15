package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Item;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ItemRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	private static final ItemSecretGiftComparator comparator = new ItemSecretGiftComparator();

	private static final class ItemSecretGiftComparator implements
			Comparator<Item> {
		@Override
		public int compare(Item o1, Item o2) {
			if (o1.getNormalizedSecretGiftProbability() < o2
					.getNormalizedSecretGiftProbability()) {
				return -1;
			} else if (o1.getNormalizedSecretGiftProbability() > o2
					.getNormalizedSecretGiftProbability()) {
				return 1;
			} else if (o1.getId() < o2.getId()) {
				//since same probability, order by id
				return -1;
			} else if (o1.getId() > o2.getId()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private static Map<Integer, Item> itemIdsToItems;
	private static float secretGiftProbabilitySum = 0F;
	private static TreeSet<Item> christmasTree; //secretGiftTree;

	private static final String TABLE_NAME = DBConstants.TABLE_ITEM_CONFIG;

	public Map<Integer, Item> getItemIdsToItems() {
		if (null == itemIdsToItems) {
//			setStaticItemIdsToItems();
			reload();
		}
		return itemIdsToItems;
	}

	public Item getItemForId(int itemId) {
		if (null == itemIdsToItems) {
//			setStaticItemIdsToItems();
			reload();
		}

		if (!itemIdsToItems.containsKey(itemId)) {
			log.error("no item for id=" + itemId);
			return null;
		}
		return itemIdsToItems.get(itemId);
	}

	public Map<Integer, Item> getItemsForIds(Collection<Integer> ids) {
		if (null == itemIdsToItems) {
//			setStaticItemIdsToItems();
			reload();
		}
		Map<Integer, Item> returnMap = new HashMap<Integer, Item>();

		for (int id : ids) {
			Item tsm = getItemForId(id);
			returnMap.put(id, tsm);
		}
		return returnMap;
	}

	public Item nextItem(float probability) {
		if (null == christmasTree) {
			log.error("object to select secret gift nonexistent.");
			return null;
		}
		//selects the item with the least probability that is still greater
		//than the given probability
		Item i = new Item();
		i.setId(0);
		i.setNormalizedSecretGiftProbability(probability);

		Item secretGift = christmasTree.ceiling(i);

		log.info(String.format("for giving probability=%s, selected %s",
				probability, secretGift));
		return secretGift;
	}

	private void setStaticItemIdsToItems() {
		log.debug("setting static map of item ids to items");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Item> itemIdsToItemsTemp = new HashMap<Integer, Item>();
						float secretGiftProbabilitySumTemp = 0F;

						//loop through each row and convert it into a java object
						while (rs.next()) {
							Item item = convertRSRowToItem(rs);

							int itemId = item.getId();
							itemIdsToItemsTemp.put(itemId, item);

							secretGiftProbabilitySumTemp += item
									.getSecretGiftChance();
						}

						itemIdsToItems = itemIdsToItemsTemp;
						secretGiftProbabilitySum = secretGiftProbabilitySumTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("item retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private void setUpRandomItemSelection() {
		log.debug("setting setUpRandomItemSelection");
		if (secretGiftProbabilitySum <= 0) {
			log.error("There are no items with secret gift probabilities set.");
			return;
		}
		//using a TreeSet to hold the items, so that it is easier
		//to select an Item at random to reward a user.
		TreeSet<Item> christmasTreeTemp = new TreeSet<Item>(comparator);

		//sort item ids, not sure if necessary, but whatevs
		List<Integer> itemIds = new ArrayList<Integer>();
		itemIds.addAll(itemIdsToItems.keySet());

		Collections.sort(itemIds);

		// for each item ordered in ascending id numbers, set its chance
		// (out of 1) to be selected as a secret gift
		float floatSoFar = 0F;
		for (Integer itemId : itemIds) {
			Item item = itemIdsToItems.get(itemId);

			floatSoFar += item.getSecretGiftChance();
			float normalizedSecretGiftProbability = floatSoFar
					/ secretGiftProbabilitySum;

			item.setNormalizedSecretGiftProbability(normalizedSecretGiftProbability);

			boolean added = christmasTreeTemp.add(item);
			if (!added) {
				log.error("(shouldn't happen...) can't add item={} to treeSet={}",
						item, christmasTreeTemp);
			}
		}

		christmasTree = christmasTreeTemp;
	}

	public void reload() {
		setStaticItemIdsToItems();
		setUpRandomItemSelection();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private Item convertRSRowToItem(ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.ITEM__ID);
		String name = rs.getString(DBConstants.ITEM__NAME);
		String shortName = rs.getString(DBConstants.ITEM__SHORT_NAME);
		String imgName = rs.getString(DBConstants.ITEM__IMG_NAME);
		String itemType = rs.getString(DBConstants.ITEM__ITEM_TYPE);

		if (null != itemType) {
			String newItemType = itemType.trim().toUpperCase();
			if (!itemType.equals(newItemType)) {
				log.error("itemType incorrect: {}, id={}",
						itemType, id);
				itemType = newItemType;
			}
		}
		int staticDataId = rs.getInt(DBConstants.ITEM__STATIC_DATA_ID);
		int amount = rs.getInt(DBConstants.ITEM__AMOUNT);
		float secretGiftChance = rs
				.getFloat(DBConstants.ITEM__SECRET_GIFT_CHANCE);
		boolean alwaysDisplayToUser = rs
				.getBoolean(DBConstants.ITEM__ALWAYS_DISPLAY_TO_USER);
		String actionGameType = rs.getString(DBConstants.ITEM__ACTION_GAME_TYPE);

		if (null != actionGameType) {
			String newActionGameType = actionGameType.trim().toUpperCase();
			if (!actionGameType.equals(newActionGameType)) {
				log.error("actionGameType incorrect: {}, id={}",
						actionGameType, id);
				actionGameType = newActionGameType;
			}
		}


		String quality = rs.getString(DBConstants.ITEM__QUALITY);

		if (null != quality) {
			String newQuality = quality.trim().toUpperCase();
			if (!quality.equals(newQuality)) {
				log.error("incorrect item quality, {}, id={}",
						quality, id);
				quality = newQuality;
			}
		}


		Item item = new Item(id, name, shortName, imgName, itemType, staticDataId, amount,
				secretGiftChance, alwaysDisplayToUser, actionGameType, quality);
		return item;
	}
}