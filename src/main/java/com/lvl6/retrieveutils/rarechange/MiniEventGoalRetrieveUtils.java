package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MiniEventGoal;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniEventGoalRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_GOAL_CONFIG;

	private static Map<Integer, MiniEventGoal> idToMiniEventGoal;
	private static Map<Integer, Collection<MiniEventGoal>> miniEventIdToGoals;

	public static Map<Integer, MiniEventGoal> getAllIdsToMiniEventGoals() {
		if (null == idToMiniEventGoal) {
			setStaticIdsToMiniEventGoals();
		}

		return idToMiniEventGoal;
	}

	public static MiniEventGoal getMiniEventGoalById(int id) {
		if (null == idToMiniEventGoal) {
			setStaticIdsToMiniEventGoals();
		}
		MiniEventGoal ep = idToMiniEventGoal.get(id);
		if (null == ep) {
			log.error("No MiniEventGoal for id={}", id);
		}
		return ep;
	}

	public static Collection<MiniEventGoal> getGoalsForMiniEventId(
			int miniEventId)
	{
		if (null == miniEventIdToGoals) {
			setStaticIdsToMiniEventGoals();
		}
		
		if (!miniEventIdToGoals.containsKey(miniEventId)) {
			log.error("No MiniEventGoals for id={}", miniEventId);
			return new ArrayList<MiniEventGoal>();
		}
		
		return miniEventIdToGoals.get(miniEventId);
	}
	
	public static void reload() {
		setStaticIdsToMiniEventGoals();
	}

	private static void setStaticIdsToMiniEventGoals() {
		log.debug("setting static map of id to MiniEventGoal");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, MiniEventGoal> idToMiniEventGoalTemp =
								new HashMap<Integer, MiniEventGoal>();
						Map<Integer, Collection<MiniEventGoal>> miniEventIdToGoalsTemp =
								new HashMap<Integer, Collection<MiniEventGoal>>();
						while (rs.next()) {
							MiniEventGoal meg = convertRSRowToMiniEventGoal(rs);
							if (null == meg) {
								continue;
							}
							
							int miniEventId = meg.getMiniEventId();
							
							//base case
							if (!miniEventIdToGoalsTemp.containsKey(miniEventId)) {
								miniEventIdToGoalsTemp.put(miniEventId,
										new ArrayList<MiniEventGoal>());
								
							}
							
							idToMiniEventGoalTemp.put(meg.getId(), meg);

							Collection<MiniEventGoal> goals = miniEventIdToGoalsTemp
									.get(miniEventId);
							goals.add(meg);
							
						}
						idToMiniEventGoal = idToMiniEventGoalTemp;
						miniEventIdToGoals = miniEventIdToGoalsTemp;
						
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("MiniEventGoal retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private static MiniEventGoal convertRSRowToMiniEventGoal(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.MINI_EVENT__ID);
		int miniEventId = rs.getInt(DBConstants.MINI_EVENT_GOAL__MINI_EVENT_ID);
		String type = rs.getString(DBConstants.MINI_EVENT_GOAL__TYPE);
		
		if (null != type) {
			String newType = type.trim()
					.toUpperCase();
			if (!type.equals(newType)) {
				log.error("incorrect MiniEventGoalType. {}, id={}", type, id);
				type = newType;
			}
		}

		int amt = rs.getInt(DBConstants.MINI_EVENT_GOAL__AMT);
		String desc = rs.getString(DBConstants.MINI_EVENT_GOAL__DESC);
		int ptsReward = rs.getInt(DBConstants.MINI_EVENT_GOAL__PTS_REWARD);

		MiniEventGoal me = new MiniEventGoal(
				id, miniEventId, type, amt, desc, ptsReward);
		return me;
	}
}
