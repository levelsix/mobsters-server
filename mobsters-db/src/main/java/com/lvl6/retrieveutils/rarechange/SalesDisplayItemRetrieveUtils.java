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

import com.lvl6.info.SalesDisplayItem;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SalesDisplayItemRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(SalesDisplayItemRetrieveUtils.class);

	private static Map<Integer, SalesDisplayItem> salesDisplayItemIdsToSalesDisplayItems;
	//key:sales pack id --> value:(key: sales item id --> value: sales item)
	private static Map<Integer, Map<Integer, SalesDisplayItem>> salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds;

	private static final String TABLE_NAME = DBConstants.TABLE_SALES_DISPLAY_ITEM_CONFIG;

	public Map<Integer, SalesDisplayItem> getSalesDisplayItemIdsToSalesDisplayItems() {
		log.debug("retrieving all SalesDisplayItems data map");
		if (salesDisplayItemIdsToSalesDisplayItems == null) {
			setStaticSalesDisplayItemIdsToSalesDisplayItems();
		}
		return salesDisplayItemIdsToSalesDisplayItems;
	}

	public Map<Integer, Map<Integer, SalesDisplayItem>> getSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds() {
		if (null == salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds) {
			setStaticSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds();
		}
		return salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds;
	}

	public Map<Integer, SalesDisplayItem> getSalesDisplayItemIdsToSalesDisplayItemsForSalesPackId(
			int salesPackId) {
		try {
			log.debug("retrieve salesPack data for salesPack "
					+ salesPackId);
			if (salesDisplayItemIdsToSalesDisplayItems == null) {
				setStaticSalesDisplayItemIdsToSalesDisplayItems();
			}
			if (salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds == null) {
				//salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds = new HashMap<Integer, Map<Integer, SalesDisplayItem>>();
				setStaticSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds();
			}

			//      List<SalesDisplayItem> bis = new ArrayList<SalesDisplayItem>(salesDisplayItemIdsToSalesDisplayItems.values());
			//      for(SalesDisplayItem bi : bis) {
			//        int packId = bi.getSalesPackId();
			//        if(!salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds.containsKey(packId)) {
			//          Map<Integer, SalesDisplayItem> bItemIdToBItem = new HashMap<Integer, SalesDisplayItem>();
			//          salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds.put(packId, bItemIdToBItem);
			//        }
			//        //each itemId is unique (autoincrementing in the table)
			//        Map<Integer, SalesDisplayItem> itemIdToItem =
			//            salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds.get(packId);
			//        itemIdToItem.put(bi.getId(), bi);
			//      }
			return salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds
					.get(salesPackId);
		} catch (Exception e) {
			log.error(
					"error creating a map of sales item ids to sales items.",
					e);
		}
		return null;
	}

	public SalesDisplayItem getSalesDisplayItemForSalesDisplayItemId(
			int salesDisplayItemId) {
		log.debug("retrieve salesDisplayItem data for salesDisplayItem "
				+ salesDisplayItemId);
		if (salesDisplayItemIdsToSalesDisplayItems == null) {
			setStaticSalesDisplayItemIdsToSalesDisplayItems();
		}
		return salesDisplayItemIdsToSalesDisplayItems
				.get(salesDisplayItemId);
	}

	public void setStaticSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds() {
		try {
			log.debug("setting static map of salesPackId to (salesDisplayItemIds to salesDisplayItems) ");
			if (salesDisplayItemIdsToSalesDisplayItems == null) {
				setStaticSalesDisplayItemIdsToSalesDisplayItems();
			}

			salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds = new HashMap<Integer, Map<Integer, SalesDisplayItem>>();
			List<SalesDisplayItem> bis = new ArrayList<SalesDisplayItem>(
					salesDisplayItemIdsToSalesDisplayItems.values());
			for (SalesDisplayItem bi : bis) {
				int packId = bi.getSalesPackageId();
				if (!salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds
						.containsKey(packId)) {
					Map<Integer, SalesDisplayItem> bItemIdToBItem = new HashMap<Integer, SalesDisplayItem>();
					salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds
							.put(packId, bItemIdToBItem);
				}
				//each itemId is unique (autoincrementing in the table)
				Map<Integer, SalesDisplayItem> itemIdToItem = salesDisplayItemIdsToSalesDisplayItemsForSalesPackIds
						.get(packId);
				itemIdToItem.put(bi.getId(), bi);
			}
		} catch (Exception e) {
			log.error(
					"error creating a map of sales item ids to sales items.",
					e);
		}
	}

	private void setStaticSalesDisplayItemIdsToSalesDisplayItems() {
		log.debug("setting static map of salesDisplayItemIds to salesDisplayItems");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, SalesDisplayItem> salesDisplayItemIdsToSalesDisplayItemsTemp = new HashMap<Integer, SalesDisplayItem>();
						while (rs.next()) {  //should only be one
							SalesDisplayItem salesDisplayItem = convertRSRowToSalesDisplayItem(rs);
							if (salesDisplayItem != null)
								salesDisplayItemIdsToSalesDisplayItemsTemp
										.put(salesDisplayItem.getId(),
												salesDisplayItem);
						}
						salesDisplayItemIdsToSalesDisplayItems = salesDisplayItemIdsToSalesDisplayItemsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("sales display item retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticSalesDisplayItemIdsToSalesDisplayItems();
		setStaticSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private SalesDisplayItem convertRSRowToSalesDisplayItem(
			ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.SALES_DISPLAY_ITEM__ID);
		int salesPackageId = rs
				.getInt(DBConstants.SALES_DISPLAY_ITEM__SALES_PACKAGE_ID);
		int rewardId = rs.getInt(DBConstants.SALES_DISPLAY_ITEM__REWARD_ID);


		SalesDisplayItem salesDisplayItem = new SalesDisplayItem(id, salesPackageId,
				rewardId);

		return salesDisplayItem;
	}
}
