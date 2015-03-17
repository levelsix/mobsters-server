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

import com.lvl6.info.SalesItem;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SalesItemRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, SalesItem> salesItemIdsToSalesItems;
	//key:sales pack id --> value:(key: sales item id --> value: sales item)
	private static Map<Integer, Map<Integer, SalesItem>> salesItemIdsToSalesItemsForSalesPackIds;

	private static final String TABLE_NAME = DBConstants.TABLE_SALES_ITEM_CONFIG;

	public static Map<Integer, SalesItem> getSalesItemIdsToSalesItems() {
		log.debug("retrieving all SalesItems data map");
		if (salesItemIdsToSalesItems == null) {
			setStaticSalesItemIdsToSalesItems();
		}
		return salesItemIdsToSalesItems;
	}

	public static Map<Integer, Map<Integer, SalesItem>> getSalesItemIdsToSalesItemsForSalesPackIds() {
		if (null == salesItemIdsToSalesItemsForSalesPackIds) {
			setStaticSalesItemIdsToSalesItemsForSalesPackIds();
		}
		return salesItemIdsToSalesItemsForSalesPackIds;
	}

	public static Map<Integer, SalesItem> getSalesItemIdsToSalesItemsForSalesPackId(
			int salesPackId) {
		try {
			log.debug("retrieve salesPack data for salesPack "
					+ salesPackId);
			if (salesItemIdsToSalesItems == null) {
				setStaticSalesItemIdsToSalesItems();
			}
			if (salesItemIdsToSalesItemsForSalesPackIds == null) {
				salesItemIdsToSalesItemsForSalesPackIds = new HashMap<Integer, Map<Integer, SalesItem>>();
				setStaticSalesItemIdsToSalesItemsForSalesPackIds();
			}

			return salesItemIdsToSalesItemsForSalesPackIds
					.get(salesPackId);
		} catch (Exception e) {
			log.error(
					"error creating a map of sales item ids to sales items.",
					e);
		}
		return null;
	}

	public static SalesItem getSalesItemForSalesItemId(int salesItemId) {
		log.debug("retrieve salesItem data for salesItem " + salesItemId);
		if (salesItemIdsToSalesItems == null) {
			setStaticSalesItemIdsToSalesItems();
		}
		return salesItemIdsToSalesItems.get(salesItemId);
	}

	public static void setStaticSalesItemIdsToSalesItemsForSalesPackIds() {
		try {
			log.debug("setting static map of salesPackId to (salesItemIds to salesItems) ");
			if (salesItemIdsToSalesItems == null) {
				setStaticSalesItemIdsToSalesItems();
			}

			salesItemIdsToSalesItemsForSalesPackIds = new HashMap<Integer, Map<Integer, SalesItem>>();
			List<SalesItem> bis = new ArrayList<SalesItem>(
					salesItemIdsToSalesItems.values());
			for (SalesItem bi : bis) {
				int packId = bi.getSalesPackageId();
				if (!salesItemIdsToSalesItemsForSalesPackIds
						.containsKey(packId)) {
					Map<Integer, SalesItem> bItemIdToBItem = new HashMap<Integer, SalesItem>();
					salesItemIdsToSalesItemsForSalesPackIds.put(packId,
							bItemIdToBItem);
				}
				//each itemId is unique (autoincrementing in the table)
				Map<Integer, SalesItem> itemIdToItem = salesItemIdsToSalesItemsForSalesPackIds
						.get(packId);
				itemIdToItem.put(bi.getId(), bi);
			}
		} catch (Exception e) {
			log.error(
					"error creating a map of sales item ids to sales items.",
					e);
		}
	}

	private static void setStaticSalesItemIdsToSalesItems() {
		log.debug("setting static map of salesItemIds to salesItems");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, SalesItem> salesItemIdsToSalesItemsTemp = new HashMap<Integer, SalesItem>();
						while (rs.next()) {  //should only be one
							SalesItem salesItem = convertRSRowToSalesItem(rs);
							if (salesItem != null)
								salesItemIdsToSalesItemsTemp.put(
										salesItem.getId(), salesItem);
						}
						salesItemIdsToSalesItems = salesItemIdsToSalesItemsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("sales item retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticSalesItemIdsToSalesItems();
		setStaticSalesItemIdsToSalesItemsForSalesPackIds();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static SalesItem convertRSRowToSalesItem(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.SALES_ITEM__ID);
		int salesPackageId = rs
				.getInt(DBConstants.SALES_ITEM__SALES_PACKAGE_ID);
		int monsterId = rs.getInt(DBConstants.SALES_ITEM__MONSTER_ID);
		int monsterQuantity = rs.getInt(DBConstants.SALES_ITEM__MONSTER_QUANTITY);
		int itemId = rs
				.getInt(DBConstants.SALES_ITEM__ITEM_ID);
		int itemQuantity = rs.getInt(DBConstants.SALES_ITEM__ITEM_QUANTITY);
		
		SalesItem salesItem = new SalesItem(id, salesPackageId, monsterId, 
				monsterQuantity, itemId, itemQuantity);
		return salesItem;
	}
}
