package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MiniJobRefreshItem;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniJobRefreshItemRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_JOB_REFRESH_ITEM_CONFIG;

	private static Map<Integer, Map<Integer, MiniJobRefreshItem>> structIdToItemIdToMjri;

	public Map<Integer, Map<Integer, MiniJobRefreshItem>> getMiniJobRefreshItem()
	{
		if (null == structIdToItemIdToMjri) {
			setStaticIdsToMiniJobRefreshItems();
		}
		if (null == structIdToItemIdToMjri) {
			return new HashMap<Integer, Map<Integer, MiniJobRefreshItem>>();
		}

		return structIdToItemIdToMjri;
	}

	public Map<Integer, MiniJobRefreshItem> getMiniJobRefreshItemById(int structId) {
		if (null == structIdToItemIdToMjri) {
			setStaticIdsToMiniJobRefreshItems();
		}
		if (null == structIdToItemIdToMjri) {
			return new HashMap<Integer, MiniJobRefreshItem>();
		}

		if (!structIdToItemIdToMjri.containsKey(structId))
		{
			log.error("no MiniJobRefreshItems for structId={}", structId);
			return null;
		}

		Map<Integer, MiniJobRefreshItem> itemIdToMjri =
				structIdToItemIdToMjri.get(structId);
		return itemIdToMjri;
	}

	public void reload() {
		setStaticIdsToMiniJobRefreshItems();
	}

	private void setStaticIdsToMiniJobRefreshItems() {
		log.debug("setting static map of id to MiniJobRefreshItem");
		Map<Integer, Map<Integer, MiniJobRefreshItem>> structIdToItemIdToMjriTemp
			= new HashMap<Integer, Map<Integer, MiniJobRefreshItem>>();

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						while (rs.next()) {
							MiniJobRefreshItem miniJobRefreshItem = convertRSRowToMiniJobRefreshItem(rs);
							if (null == miniJobRefreshItem) {
								continue;
							}

							int structId = miniJobRefreshItem.getStructId();
							if (!structIdToItemIdToMjriTemp.containsKey(structId)) {
								structIdToItemIdToMjriTemp.put(structId,
										new HashMap<Integer, MiniJobRefreshItem>());
							}

							Map<Integer, MiniJobRefreshItem> itemIdToMjri =
									structIdToItemIdToMjriTemp.get(structId);

							int itemId = miniJobRefreshItem.getItemId();
							itemIdToMjri.put(itemId, miniJobRefreshItem);

						}

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("MiniJobRefreshItem retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		structIdToItemIdToMjri = structIdToItemIdToMjriTemp;
	}

	private MiniJobRefreshItem convertRSRowToMiniJobRefreshItem(ResultSet rs)
			throws SQLException {
		int structId = rs.getInt(DBConstants.MINI_JOB_REFRESH_ITEM__STRUCT_ID);
		int itemId = rs.getInt(DBConstants.MINI_JOB_REFRESH_ITEM__ITEM_ID);
		int gemPrice = rs.getInt(DBConstants.MINI_JOB_REFRESH_ITEM__GEM_PRICE);

		MiniJobRefreshItem me = new MiniJobRefreshItem(
				structId, itemId, gemPrice);
		return me;
	}
}
