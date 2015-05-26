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

import com.lvl6.info.TangoGift;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class TangoGiftRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, TangoGift> tangoGiftIdsToTangoGifts;
	private static final String TABLE_NAME = DBConstants.TABLE_TANGO_GIFT_CONFIG;

	public Map<Integer, TangoGift> getTangoGiftIdsToTangoGifts() {
		log.debug("retrieving all tango gifts data map");
		if (tangoGiftIdsToTangoGifts == null) {
			setStaticTangoGiftIdsToTangoGifts();
		}
		return tangoGiftIdsToTangoGifts;
	}

	public TangoGift getTangoGiftForTangoGiftId(int tangoGiftId) {
		log.debug("retrieve tango gift data for tango gift "
				+ tangoGiftId);
		if (tangoGiftIdsToTangoGifts == null) {
			setStaticTangoGiftIdsToTangoGifts();
		}
		return tangoGiftIdsToTangoGifts.get(tangoGiftId);
	}

	private void setStaticTangoGiftIdsToTangoGifts() {
		log.debug("setting static map of tangoGiftIds to tangoGifts");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, TangoGift> tangoGiftIdsToTangoGiftsTemp = new HashMap<Integer, TangoGift>();

						while (rs.next()) {
							TangoGift tangoGift = convertRSRowToTangoGift(rs);

							if (null == tangoGift) {
								continue;
							}
							tangoGiftIdsToTangoGiftsTemp.put(
									tangoGift.getId(), tangoGift);
						}
						tangoGiftIdsToTangoGifts = tangoGiftIdsToTangoGiftsTemp;
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
		setStaticTangoGiftIdsToTangoGifts();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private TangoGift convertRSRowToTangoGift(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.TANGO_GIFT__ID);
		String name = rs.getString(DBConstants.TANGO_GIFT__NAME);
		int hoursUntilExpiration = rs.getInt(DBConstants.TANGO_GIFT__HOURS_UNTIL_EXPIRATION);
		String imageName = rs.getString(DBConstants.TANGO_GIFT__IMAGE_NAME);

		TangoGift tangoGift = new TangoGift(id, name, hoursUntilExpiration, imageName);
		return tangoGift;
	}
}
