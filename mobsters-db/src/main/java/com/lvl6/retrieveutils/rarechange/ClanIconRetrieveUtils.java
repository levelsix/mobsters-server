package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanIcon;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
public class ClanIconRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(ClanIconRetrieveUtils.class);

	private static Map<Integer, ClanIcon> clanIconIdsToClanIcons;
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_ICON_CONFIG;

	//CONTROLLER LOGIC******************************************************************

	//RETRIEVE QUERIES*********************************************************************
	public Map<Integer, ClanIcon> getClanIconIdsToClanIcons() {
		if (null == clanIconIdsToClanIcons) {
			setStaticClanIconIdsToClanIcons();
		}
		return clanIconIdsToClanIcons;
	}

	public ClanIcon getClanIconForId(int id) {
		if (null == clanIconIdsToClanIcons) {
			setStaticClanIconIdsToClanIcons();
		}

		if (!clanIconIdsToClanIcons.containsKey(id)) {
			log.warn("no clan icon with id=" + id);
			return null;
		}
		return clanIconIdsToClanIcons.get(id);
	}

	public Map<Integer, ClanIcon> getClanIconsForIds(
			Collection<Integer> ids) {
		if (null == clanIconIdsToClanIcons) {
			setStaticClanIconIdsToClanIcons();
		}
		Map<Integer, ClanIcon> returnMap = new HashMap<Integer, ClanIcon>();

		for (int id : ids) {
			ClanIcon tsm = clanIconIdsToClanIcons.get(id);
			returnMap.put(id, tsm);
		}
		return returnMap;
	}

	private void setStaticClanIconIdsToClanIcons() {
		log.debug("setting static map of clanIcon ids to clanIcons");

		Random rand = new Random();
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, ClanIcon> clanIconIdsToClanIconsTemp = new HashMap<Integer, ClanIcon>();

						//loop through each row and convert it into a java object
						while (rs.next()) {
							ClanIcon clanIcon = convertRSRowToClanIcon(rs, rand);

							int clanIconId = clanIcon.getId();
							clanIconIdsToClanIconsTemp
									.put(clanIconId, clanIcon);
						}

						clanIconIdsToClanIcons = clanIconIdsToClanIconsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("clanIcon retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticClanIconIdsToClanIcons();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private ClanIcon convertRSRowToClanIcon(ResultSet rs, Random rand)
			throws SQLException {
		int id = rs.getInt(DBConstants.CLAN_ICON__ID);
		String imgName = rs.getString(DBConstants.CLAN_ICON__IMG_NAME);
		boolean isAvailable = rs
				.getBoolean(DBConstants.CLAN_ICON__IS_AVAILABLE);

		ClanIcon clanIcon = new ClanIcon(id, imgName, isAvailable);
		return clanIcon;
	}

}
