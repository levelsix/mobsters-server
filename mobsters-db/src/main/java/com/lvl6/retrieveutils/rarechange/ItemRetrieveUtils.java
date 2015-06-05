package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	private static final Logger log = LoggerFactory
			.getLogger(ItemRetrieveUtils.class);

	private static Map<Integer, Item> itemIdsToItems;
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
			log.error("no item for id={}", itemId);
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

	private void setStaticItemIdsToItems() {
		log.debug("setting static map of itemIds to Items");

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

						//loop through each row and convert it into a java object
						while (rs.next()) {
							Item item = convertRSRowToItem(rs);
							int itemId = item.getId();
							itemIdsToItemsTemp.put(itemId, item);

						}

						itemIdsToItems = itemIdsToItemsTemp;
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

	public void reload() {
		setStaticItemIdsToItems();
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
