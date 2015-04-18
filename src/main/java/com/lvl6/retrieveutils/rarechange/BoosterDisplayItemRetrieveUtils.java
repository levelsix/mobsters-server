package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.BoosterDisplayItem;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class BoosterDisplayItemRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, BoosterDisplayItem> boosterDisplayItemIdsToBoosterDisplayItems;
	//key:booster pack id --> value:(key: booster item id --> value: booster item)
	private static Map<Integer, Map<Integer, BoosterDisplayItem>> boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds;

	private static final String TABLE_NAME = DBConstants.TABLE_BOOSTER_DISPLAY_ITEM_CONFIG;

	public Map<Integer, BoosterDisplayItem> getBoosterDisplayItemIdsToBoosterDisplayItems() {
		log.debug("retrieving all BoosterDisplayItems data map");
		if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
		}
		return boosterDisplayItemIdsToBoosterDisplayItems;
	}

	public Map<Integer, Map<Integer, BoosterDisplayItem>> getBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds() {
		if (null == boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();
		}
		return boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds;
	}

	public Map<Integer, BoosterDisplayItem> getBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackId(
			int boosterPackId) {
		try {
			log.debug("retrieve boosterPack data for boosterPack "
					+ boosterPackId);
			if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
				setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
			}
			if (boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds == null) {
				//boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds = new HashMap<Integer, Map<Integer, BoosterDisplayItem>>();
				setStaticBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();
			}

			//      List<BoosterDisplayItem> bis = new ArrayList<BoosterDisplayItem>(boosterDisplayItemIdsToBoosterDisplayItems.values());
			//      for(BoosterDisplayItem bi : bis) {
			//        int packId = bi.getBoosterPackId();
			//        if(!boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.containsKey(packId)) {
			//          Map<Integer, BoosterDisplayItem> bItemIdToBItem = new HashMap<Integer, BoosterDisplayItem>();
			//          boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.put(packId, bItemIdToBItem);
			//        }
			//        //each itemId is unique (autoincrementing in the table)
			//        Map<Integer, BoosterDisplayItem> itemIdToItem =
			//            boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds.get(packId);
			//        itemIdToItem.put(bi.getId(), bi);
			//      }
			return boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds
					.get(boosterPackId);
		} catch (Exception e) {
			log.error(
					"error creating a map of booster item ids to booster items.",
					e);
		}
		return null;
	}

	public BoosterDisplayItem getBoosterDisplayItemForBoosterDisplayItemId(
			int boosterDisplayItemId) {
		log.debug("retrieve boosterDisplayItem data for boosterDisplayItem "
				+ boosterDisplayItemId);
		if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
			setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
		}
		return boosterDisplayItemIdsToBoosterDisplayItems
				.get(boosterDisplayItemId);
	}

	public void setStaticBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds() {
		try {
			log.debug("setting static map of boosterPackId to (boosterDisplayItemIds to boosterDisplayItems) ");
			if (boosterDisplayItemIdsToBoosterDisplayItems == null) {
				setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
			}

			boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds = new HashMap<Integer, Map<Integer, BoosterDisplayItem>>();
			List<BoosterDisplayItem> bis = new ArrayList<BoosterDisplayItem>(
					boosterDisplayItemIdsToBoosterDisplayItems.values());
			for (BoosterDisplayItem bi : bis) {
				int packId = bi.getBoosterPackId();
				if (!boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds
						.containsKey(packId)) {
					Map<Integer, BoosterDisplayItem> bItemIdToBItem = new HashMap<Integer, BoosterDisplayItem>();
					boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds
							.put(packId, bItemIdToBItem);
				}
				//each itemId is unique (autoincrementing in the table)
				Map<Integer, BoosterDisplayItem> itemIdToItem = boosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds
						.get(packId);
				itemIdToItem.put(bi.getId(), bi);
			}
		} catch (Exception e) {
			log.error(
					"error creating a map of booster item ids to booster items.",
					e);
		}
	}

	private void setStaticBoosterDisplayItemIdsToBoosterDisplayItems() {
		log.debug("setting static map of boosterDisplayItemIds to boosterDisplayItems");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, BoosterDisplayItem> boosterDisplayItemIdsToBoosterDisplayItemsTemp = new HashMap<Integer, BoosterDisplayItem>();
						while (rs.next()) {  //should only be one
							BoosterDisplayItem boosterDisplayItem = convertRSRowToBoosterDisplayItem(rs);
							if (boosterDisplayItem != null)
								boosterDisplayItemIdsToBoosterDisplayItemsTemp
										.put(boosterDisplayItem.getId(),
												boosterDisplayItem);
						}
						boosterDisplayItemIdsToBoosterDisplayItems = boosterDisplayItemIdsToBoosterDisplayItemsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("booster display item retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticBoosterDisplayItemIdsToBoosterDisplayItems();
		setStaticBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private BoosterDisplayItem convertRSRowToBoosterDisplayItem(
			ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.BOOSTER_DISPLAY_ITEM__ID);
		int boosterPackId = rs
				.getInt(DBConstants.BOOSTER_DISPLAY_ITEM__BOOSTER_PACK_ID);
		boolean isMonster = rs
				.getBoolean(DBConstants.BOOSTER_DISPLAY_ITEM__IS_MONSTER);
		boolean isComplete = rs
				.getBoolean(DBConstants.BOOSTER_DISPLAY_ITEM__IS_COMPLETE);
		String monsterQuality = rs
				.getString(DBConstants.BOOSTER_DISPLAY_ITEM__MONSTER_QUALITY);
		int gemReward = rs.getInt(DBConstants.BOOSTER_DISPLAY_ITEM__GEM_REWARD);
		int quantity = rs.getInt(DBConstants.BOOSTER_DISPLAY_ITEM__QUANTITY);
		int itemId = rs.getInt(DBConstants.BOOSTER_DISPLAY_ITEM__ITEM_ID);
		int itemQuantity = rs
				.getInt(DBConstants.BOOSTER_DISPLAY_ITEM__ITEM_QUANTITY);

		if (null != monsterQuality) {
			String newMonsterQuality = monsterQuality.trim().toUpperCase();
			if (!monsterQuality.equals(newMonsterQuality)) {
				log.error(String.format("monsterQuality incorrect: %s, id=%s",
						monsterQuality, id));
				monsterQuality = newMonsterQuality;
			}
		}

		int rewardId = rs.getInt(DBConstants.BOOSTER_DISPLAY_ITEM__REWARD_ID);

		BoosterDisplayItem boosterDisplayItem = new BoosterDisplayItem(id,
				boosterPackId, isMonster, isComplete, monsterQuality,
				gemReward, quantity, itemId, itemQuantity, rewardId);
		return boosterDisplayItem;
	}
}
