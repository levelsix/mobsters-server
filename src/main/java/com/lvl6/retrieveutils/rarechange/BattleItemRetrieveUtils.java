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

import com.lvl6.info.BattleItem;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class BattleItemRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, BattleItem> idsToBattleItems;
	private static final String TABLE_NAME = DBConstants.TABLE_BATTLE_ITEM_CONFIG;

	public static Map<Integer, BattleItem> getBattleItemIdsToBattleItems() {
		if (null == idsToBattleItems) {
			setStaticIdsToBattleItems();
		}
		return idsToBattleItems;
	}

	public static BattleItem getBattleItemForId(int id) {
		if (null == idsToBattleItems) {
			setStaticIdsToBattleItems();
		}

		if (!idsToBattleItems.containsKey(id)) {
			log.error("no battle item for id=" + id);
			return null;
		}
		return idsToBattleItems.get(id);
	}

	public static Map<Integer, BattleItem> getBattleItemsForIds(
			Collection<Integer> ids) {
		if (null == idsToBattleItems) {
			setStaticIdsToBattleItems();
		}
		Map<Integer, BattleItem> returnMap = new HashMap<Integer, BattleItem>();

		for (int id : ids) {
			BattleItem bi = getBattleItemForId(id);
			returnMap.put(id, bi);
		}
		return returnMap;
	}

	private static void setStaticIdsToBattleItems() {
		log.debug("setting static map of ids to battle items");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, BattleItem> idsToBattleItemTemp = new HashMap<Integer, BattleItem>();
						while (rs.next()) {
							BattleItem bi = convertRSRowToBattleItem(rs);
							if (bi != null)
								idsToBattleItemTemp.put(bi.getId(), bi);
						}
						idsToBattleItems = idsToBattleItemTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("BattleItem retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticIdsToBattleItems();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static BattleItem convertRSRowToBattleItem(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.BATTLE_ITEM__ID);
		String type = rs.getString(DBConstants.BATTLE_ITEM__TYPE);
		String category = rs
				.getString(DBConstants.BATTLE_ITEM__CATEGORY);
		String createResourceType = rs
				.getString(DBConstants.BATTLE_ITEM__CREATE_RESOURCE_TYPE);
		int createCost = rs.getInt(DBConstants.BATTLE_ITEM__CREATE_COST);
		String name = rs.getString(DBConstants.BATTLE_ITEM__NAME);
		String description = rs
				.getString(DBConstants.BATTLE_ITEM__DESCRIPTION);
		int powerAmount = rs
				.getInt(DBConstants.BATTLE_ITEM__POWER_AMOUNT);
		String imageName = rs
				.getString(DBConstants.BATTLE_ITEM__IMAGE_NAME);
		int priority = rs.getInt(DBConstants.BATTLE_ITEM__PRIORITY);
		int minutesToCreate = rs
				.getInt(DBConstants.BATTLE_ITEM__MINUTES_TO_CREATE);
		int inBattleGemCost = rs
				.getInt(DBConstants.BATTLE_ITEM__IN_BATTLE_GEM_COST);
		int amount = rs.getInt(DBConstants.BATTLE_ITEM__AMOUNT);

		BattleItem bi = new BattleItem(id, type, category, createResourceType,
				createCost, name, description, powerAmount, imageName,
				priority, minutesToCreate, inBattleGemCost, amount);

		return bi;
	}
}
