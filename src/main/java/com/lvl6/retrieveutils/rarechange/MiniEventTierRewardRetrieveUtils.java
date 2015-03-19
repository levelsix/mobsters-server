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

import com.lvl6.info.MiniEventTierReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniEventTierRewardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_TIER_REWARD_CONFIG;

	private static Map<Integer, MiniEventTierReward> idToMiniEventTierReward;
	private static Map<Integer, Collection<MiniEventTierReward>> mefplIdToReward;

	public static Map<Integer, MiniEventTierReward> getAllIdsToMiniEventTierRewards() {
		if (null == idToMiniEventTierReward) {
			setStaticIdsToMiniEventTierRewards();
		}

		return idToMiniEventTierReward;
	}

	public static MiniEventTierReward getMiniEventTierRewardById(int id) {
		if (null == idToMiniEventTierReward) {
			setStaticIdsToMiniEventTierRewards();
		}
		MiniEventTierReward ep = idToMiniEventTierReward.get(id);
		if (null == ep) {
			log.error("No MiniEventTierReward for id={}", id);
		}
		return ep;
	}

	public static void reload() {
		setStaticIdsToMiniEventTierRewards();
	}

	private static void setStaticIdsToMiniEventTierRewards() {
		log.debug("setting static map of id to MiniEventTierReward");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, MiniEventTierReward> idToMiniEventTierRewardTemp =
								new HashMap<Integer, MiniEventTierReward>();
						Map<Integer, Collection<MiniEventTierReward>> mefplIdToRewardTemp =
								new HashMap<Integer, Collection<MiniEventTierReward>>();


						while (rs.next()) {  //should only be one
							MiniEventTierReward metr = convertRSRowToMiniEventTierReward(rs);
							if (null == metr) {
								continue;
							}

							idToMiniEventTierRewardTemp.put(metr.getId(), metr);

							int miniEventForPlayerLvlId = metr.getMiniEventForPlayerLvlId();
							if (!mefplIdToReward.containsKey(miniEventForPlayerLvlId)) {
								mefplIdToReward.put(miniEventForPlayerLvlId,
										new ArrayList<MiniEventTierReward>());
							}

							Collection<MiniEventTierReward> rewards = mefplIdToRewardTemp
									.get(miniEventForPlayerLvlId);
							rewards.add(metr);

						}
						idToMiniEventTierReward = idToMiniEventTierRewardTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("MiniEventTierReward retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private static MiniEventTierReward convertRSRowToMiniEventTierReward(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.MINI_EVENT_TIER_REWARD__ID);
		int mefplId = rs.getInt(DBConstants.MINI_EVENT_TIER_REWARD__MINI_EVENT_FOR_PLAYER_LVL_ID);
		int rewardId = rs.getInt(DBConstants.MINI_EVENT_TIER_REWARD__REWARD_ID);
		int rewardTier = rs.getInt(DBConstants.MINI_EVENT_TIER_REWARD__REWARD_TIER);

		MiniEventTierReward me = new MiniEventTierReward(
				id, mefplId, rewardId, rewardTier);
		return me;
	}
}
