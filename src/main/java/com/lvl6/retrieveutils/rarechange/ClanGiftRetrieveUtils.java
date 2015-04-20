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

import com.lvl6.info.ClanGift;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ClanGiftRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, ClanGift> clanGiftIdsToClanGifts;

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_GIFT_CONFIG;

	public Map<Integer, ClanGift> getClanGiftIdsToClanGifts() {
		log.debug("retrieving all clan gifts data map");
		if (clanGiftIdsToClanGifts == null) {
			setStaticClanGiftIdsToClanGifts();
		}
		return clanGiftIdsToClanGifts;
	}

	public Map<String, ClanGift> getClanGiftNamesToClanGifts() {
		log.debug("retrieving all clan gifts by name data map");
		if (clanGiftIdsToClanGifts == null) {
			setStaticClanGiftIdsToClanGifts();
		}

		Map<String, ClanGift> returnMap = new HashMap<String, ClanGift>();
		for(Integer i : clanGiftIdsToClanGifts.keySet()) {
			ClanGift sp = clanGiftIdsToClanGifts.get(i);
			returnMap.put(sp.getName(), sp);
		}
		return returnMap;
	}

	public ClanGift getClanGiftForClanGiftId(int clanGiftId) {
		log.debug("retrieve clan gift data for clan gift "
				+ clanGiftId);
		if (clanGiftIdsToClanGifts == null) {
			setStaticClanGiftIdsToClanGifts();
		}
		return clanGiftIdsToClanGifts.get(clanGiftId);
	}

	private void setStaticClanGiftIdsToClanGifts() {
		log.debug("setting static map of clanGiftIds to clanGifts");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, ClanGift> clanGiftIdsToClanGiftsTemp = new HashMap<Integer, ClanGift>();
						while (rs.next()) {  //should only be one
							ClanGift clanGift = convertRSRowToClanGift(rs);
							if (clanGift != null)
								clanGiftIdsToClanGiftsTemp.put(
										clanGift.getId(), clanGift);
						}
						clanGiftIdsToClanGifts = clanGiftIdsToClanGiftsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("sales pack retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticClanGiftIdsToClanGifts();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private ClanGift convertRSRowToClanGift(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.CLAN_GIFT__ID);
		String name = rs.getString(DBConstants.CLAN_GIFT__NAME);
		int hoursUntilExpiration = rs.getInt(DBConstants.CLAN_GIFT__HOURS_UNTIL_EXPIRATION);

		ClanGift clanGift = new ClanGift(id, name, hoursUntilExpiration);
		return clanGift;
	}
}
