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

import com.lvl6.info.MiniEventLeaderboardReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniEventLeaderboardRewardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(MiniEventLeaderboardRewardRetrieveUtils.class);

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_LEADERBOARD_REWARD_CONFIG;

	private static Map<Integer, MiniEventLeaderboardReward> idToReward;
	private static Map<Integer, Collection<MiniEventLeaderboardReward>> miniEventIdToRewards;

	public Map<Integer, MiniEventLeaderboardReward> getAllIdsToMiniEventLeaderboardRewards() {
		if (null == idToReward) {
			setStaticIdsToMiniEventLeaderboardRewards();
		}

		return idToReward;
	}

	public MiniEventLeaderboardReward getMiniEventLeaderboardRewardById(int id) {
		if (null == idToReward) {
			setStaticIdsToMiniEventLeaderboardRewards();
		}
		MiniEventLeaderboardReward ep = idToReward.get(id);
		if (null == ep) {
			log.warn("No MiniEventLeaderboardReward for id={}", id);
		}
		return ep;
	}

	public Collection<MiniEventLeaderboardReward> getRewardsForMiniEventId(
			int miniEventId)
	{
		if (null == miniEventIdToRewards) {
			setStaticIdsToMiniEventLeaderboardRewards();
		}

		if (!miniEventIdToRewards.containsKey(miniEventId)) {
			log.warn("No MiniEventLeaderboardRewards for miniEventId={}", miniEventId);
			return new ArrayList<MiniEventLeaderboardReward>();
		}

		return miniEventIdToRewards.get(miniEventId);
	}

	public void reload() {
		setStaticIdsToMiniEventLeaderboardRewards();
	}

	private void setStaticIdsToMiniEventLeaderboardRewards() {
		log.debug("setting static map of id to MiniEventLeaderboardReward");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, MiniEventLeaderboardReward> idToMiniEventLeaderboardRewardTemp =
								new HashMap<Integer, MiniEventLeaderboardReward>();
						Map<Integer, Collection<MiniEventLeaderboardReward>> miniEventIdToRewardsTemp =
								new HashMap<Integer, Collection<MiniEventLeaderboardReward>>();
						while (rs.next()) {
							MiniEventLeaderboardReward melr = convertRSRowToMiniEventLeaderboardReward(rs);
							if (null == melr) {
								continue;
							}

							int miniEventId = melr.getMiniEventId();

							//base case
							if (!miniEventIdToRewardsTemp.containsKey(miniEventId)) {
								miniEventIdToRewardsTemp.put(miniEventId,
										new ArrayList<MiniEventLeaderboardReward>());

							}

							idToMiniEventLeaderboardRewardTemp.put(melr.getId(), melr);

							Collection<MiniEventLeaderboardReward> goals = miniEventIdToRewardsTemp
									.get(miniEventId);
							goals.add(melr);

						}
						idToReward = idToMiniEventLeaderboardRewardTemp;
						miniEventIdToRewards = miniEventIdToRewardsTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("MiniEventLeaderboardReward retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private MiniEventLeaderboardReward convertRSRowToMiniEventLeaderboardReward(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.MINI_EVENT_LEADERBOARD_REWARD__ID);
		int miniEventId = rs.getInt(DBConstants.MINI_EVENT_LEADERBOARD_REWARD__MINI_EVENT_ID);

		int rewardId = rs.getInt(DBConstants.MINI_EVENT_LEADERBOARD_REWARD__REWARD_ID);
		int leaderboardPos = rs.getInt(DBConstants.MINI_EVENT_LEADERBOARD_REWARD__LEADERBOARD_POS);

		MiniEventLeaderboardReward me = new MiniEventLeaderboardReward(
				id, miniEventId, rewardId, leaderboardPos);
		return me;
	}
}
