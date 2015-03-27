package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MiniEventForPlayerLvl;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniEventForPlayerLvlRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_FOR_PLAYER_LVL_CONFIG;

	private static Map<Integer, MiniEventForPlayerLvl> idToMiniEventForPlayerLvl;
	private static Map<Integer, TreeSet<MiniEventForPlayerLvl>> miniEventIdToOrderedMefpl;
	private static final MefplComparator comparator = new MefplComparator();

	private static final class MefplComparator implements Comparator<MiniEventForPlayerLvl>
	{
		//have to define max level, but want it to be unbounded
//		@Override
//		public int compare(MiniEventForPlayerLvl o1, MiniEventForPlayerLvl o2)
//		{
//			int lvlMax1 = o1.getPlayerLvlMax();
//			int lvlMax2 = o2.getPlayerLvlMax();
//
//			if (lvlMax1 < lvlMax2) {
//				return -1;
//			} else if (lvlMax1 > lvlMax2) {
//				return 1;
//			} else if (o1.getId() < o2.getId()) {
//				return -1;
//			} else if (o1.getId() > o2.getId()) {
//				return 1;
//			} else {
//				return 0;
//			}
//		}

		@Override
		public int compare(MiniEventForPlayerLvl o1, MiniEventForPlayerLvl o2)
		{
			int lvlMin1 = o1.getPlayerLvlMin();
			int lvlMin2 = o2.getPlayerLvlMin();

			if (lvlMin1 < lvlMin2) {
				return -1;
			} else if (lvlMin1 > lvlMin2) {
				return 1;
			} else if (o1.getId() < o2.getId()) {
				return -1;
			} else if (o1.getId() > o2.getId()) {
				return 1;
			} else {
				return 0;
			}

		}

	}

	public static Map<Integer, MiniEventForPlayerLvl> getAllIdsToMiniEventForPlayerLvls() {
		if (null == idToMiniEventForPlayerLvl) {
			setStaticIdsToMiniEventForPlayerLvls();
		}

		return idToMiniEventForPlayerLvl;
	}

	public static MiniEventForPlayerLvl getMiniEventForPlayerLvlById(int id) {
		if (null == idToMiniEventForPlayerLvl) {
			setStaticIdsToMiniEventForPlayerLvls();
		}
		MiniEventForPlayerLvl ep = idToMiniEventForPlayerLvl.get(id);
		if (null == ep) {
			log.error("No MiniEventForPlayerLvl for id={}", id);
		}
		return ep;
	}

	public static MiniEventForPlayerLvl getMiniEventForPlayerLvl(
			int miniEventId, int playerLvl)
	{
		if (null == miniEventIdToOrderedMefpl) {
			log.warn("no ordered MiniEventForPlayerLvl, reloading");
			setStaticIdsToMiniEventForPlayerLvls();
		}

		if (null == miniEventIdToOrderedMefpl) {
			log.warn("no ordered MiniEventForPlayerLvl");
			return null;
		}

		if (!miniEventIdToOrderedMefpl.containsKey(miniEventId))
		{
			log.warn("no MiniEventForPlayerLvl for eventId={}",
					miniEventId);
			return null;
		}

		TreeSet<MiniEventForPlayerLvl> orderedMefpl =
				miniEventIdToOrderedMefpl.get(miniEventId);

		MiniEventForPlayerLvl curLvl = new MiniEventForPlayerLvl();
		curLvl.setId(0);
		curLvl.setPlayerLvlMax(playerLvl);

		MiniEventForPlayerLvl mefpl = orderedMefpl.ceiling(curLvl);

		if (null == mefpl) {
			log.warn("no MiniEventForPlayerLvl for eventId={}, lvl={}",
					miniEventId, playerLvl);
		}
		return mefpl;
	}

	public static void reload() {
		setStaticIdsToMiniEventForPlayerLvls();
		setOrderedMiniEventForPlayerLvls();
	}

	private static void setStaticIdsToMiniEventForPlayerLvls() {
		log.debug("setting static map of id to MiniEventForPlayerLvl");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, MiniEventForPlayerLvl> idToMiniEventForPlayerLvlTemp = new HashMap<Integer, MiniEventForPlayerLvl>();
						while (rs.next()) {  //should only be one
							MiniEventForPlayerLvl cec = convertRSRowToMiniEventForPlayerLvl(rs);
							if (null != cec)
								idToMiniEventForPlayerLvlTemp.put(cec.getId(), cec);
						}
						idToMiniEventForPlayerLvl = idToMiniEventForPlayerLvlTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("MiniEventForPlayerLvl retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private static void setOrderedMiniEventForPlayerLvls()
	{
		if (null == idToMiniEventForPlayerLvl)
		{
			log.warn("no MiniEventForPlayerLvl");
			return;
		}

		//TreeSets are used so I don't have to manually search for the
		//MiniEventForPlayerLvl that sandwiches a given playerLevel
		Map<Integer, TreeSet<MiniEventForPlayerLvl>> miniEventIdToOrderedMefplTemp =
				new HashMap<Integer, TreeSet<MiniEventForPlayerLvl>>();

		for (MiniEventForPlayerLvl mefpl : idToMiniEventForPlayerLvl.values())
		{
			int miniEventId = mefpl.getMiniEventId();

			if (!miniEventIdToOrderedMefplTemp.containsKey(miniEventId))
			{
				miniEventIdToOrderedMefplTemp.put(miniEventId,
						new TreeSet<MiniEventForPlayerLvl>(comparator));
			}

			TreeSet<MiniEventForPlayerLvl> orderedMefpl =
					miniEventIdToOrderedMefplTemp.get(miniEventId);

			orderedMefpl.add(mefpl);
		}

		miniEventIdToOrderedMefpl = miniEventIdToOrderedMefplTemp;

	}

	private static MiniEventForPlayerLvl convertRSRowToMiniEventForPlayerLvl(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.MINI_EVENT_FOR_PLAYER_LVL__ID);
		int miniEventId = rs.getInt(DBConstants.MINI_EVENT_FOR_PLAYER_LVL__MINI_EVENT_ID);
		int lvlMin = rs.getInt(DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MIN);
		int lvlMax = rs.getInt(DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MAX);
		int tierOneMinPts = rs.getInt(DBConstants.MINI_EVENT_FOR_PLAYER_LVL__TIER_ONE_MIN_PTS);
		int tierTwoMinPts = rs.getInt(DBConstants.MINI_EVENT_FOR_PLAYER_LVL__TIER_TWO_MIN_PTS);
		int tierThreeMinPts = rs.getInt(DBConstants.MINI_EVENT_FOR_PLAYER_LVL__TIER_THREE_MIN_PTS);

		MiniEventForPlayerLvl me = new MiniEventForPlayerLvl(
				id, miniEventId, lvlMin, lvlMax, tierOneMinPts,
				tierTwoMinPts, tierThreeMinPts);
		return me;
	}
}
