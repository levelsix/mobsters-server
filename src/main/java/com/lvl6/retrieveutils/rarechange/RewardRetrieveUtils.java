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

import com.lvl6.info.Reward;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class RewardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_REWARD_CONFIG;

	private static Map<Integer, Reward> idToReward;

	public static Map<Integer, Reward> getAllIdsToRewards() {
		if (null == idToReward) {
			setStaticIdsToRewards();
		}

		return idToReward;
	}

	public static Reward getRewardById(int id) {
		if (null == idToReward) {
			setStaticIdsToRewards();
		}
		Reward ep = idToReward.get(id);
		if (null == ep) {
			log.error("No Reward for id={}", id);
		}
		return ep;
	}

	public static void reload() {
		setStaticIdsToRewards();
	}

	private static void setStaticIdsToRewards() {
		log.debug("setting static map of id to Reward");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Reward> idToRewardTemp =
								new HashMap<Integer, Reward>();
						while (rs.next()) {
							Reward reward = convertRSRowToReward(rs);
							if (null == reward) {
								continue;
							}

							idToRewardTemp.put(reward.getId(), reward);

						}
						idToReward = idToRewardTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("Reward retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private static Reward convertRSRowToReward(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.REWARD__ID);
		int staticDataId = rs.getInt(DBConstants.REWARD__STATIC_DATA_ID);
		String type = rs.getString(DBConstants.REWARD__TYPE);

		if (null != type) {
			String newType = type.trim()
					.toUpperCase();
			if (!type.equals(newType)) {
				log.error("incorrect RewardType. {}, id={}", type, id);
				type = newType;
			}
		}

		int amt = rs.getInt(DBConstants.REWARD__AMT);

		Reward me = new Reward(
				id, staticDataId, type, amt);
		return me;
	}
}
