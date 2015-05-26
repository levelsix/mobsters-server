package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanRaidStageReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ClanRaidStageRewardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, Map<Integer, ClanRaidStageReward>> clanRaidStageIdsToIdsToRewards;
	private static Map<Integer, ClanRaidStageReward> idsToRewards;

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_RAID_STAGE_REWARD_CONFIG;

	public Map<Integer, Map<Integer, ClanRaidStageReward>> getClanRaidStageIdsToIdsToRewards() {
		log.debug("retrieving all clan raid stage reward data map");
		if (clanRaidStageIdsToIdsToRewards == null) {
			setStaticClanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewards();
		}
		return clanRaidStageIdsToIdsToRewards;
	}

	public Map<Integer, ClanRaidStageReward> getClanRaidStageRewardIdsToRewards() {
		if (null == idsToRewards) {
			setStaticClanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewards();
		}
		return idsToRewards;
	}

	public ClanRaidStageReward getClanRaidStageRewardForClanRaidStageRewardId(
			int clanRaidStageRewardId) {
		if (idsToRewards == null) {
			setStaticClanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewards();
		}

		if (idsToRewards.containsKey(clanRaidStageRewardId)) {
			return idsToRewards.get(clanRaidStageRewardId);
		} else {
			log.error("no reward for clanRaidStageRewardId="
					+ clanRaidStageRewardId);
			return null;
		}
	}

	public Map<Integer, ClanRaidStageReward> getClanRaidStageRewardsForClanRaidStageId(
			int clanRaidStageId) {
		log.debug("retrieve clan raid stage reward data for clanRaidStageId="
				+ clanRaidStageId);
		if (clanRaidStageIdsToIdsToRewards == null) {
			setStaticClanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewards();
		}

		if (clanRaidStageIdsToIdsToRewards.containsKey(clanRaidStageId)) {
			return clanRaidStageIdsToIdsToRewards.get(clanRaidStageId);
		} else {
			log.error("no clan raid stage rewards for clan raid stage id="
					+ clanRaidStageId);
			return new HashMap<Integer, ClanRaidStageReward>();
		}
	}

	private void setStaticClanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewards() {
		log.debug("setting static map of clan raid stage ids to rewards");
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
						Map<Integer, Map<Integer, ClanRaidStageReward>> clanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewardsTemp = new HashMap<Integer, Map<Integer, ClanRaidStageReward>>();
						Map<Integer, ClanRaidStageReward> clanRaidStageRewardIdsToClanRaidStageRewardsTemp = new HashMap<Integer, ClanRaidStageReward>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							ClanRaidStageReward clanRaidStageReward = convertRSRowToClanRaidStageReward(rs);
							if (clanRaidStageReward == null) {
								continue;
							}
							clanRaidStageReward.setRand(rand);

							int clanRaidStageId = clanRaidStageReward
									.getClanRaidStageId();
							//base case, no key with clanRaid id exists, so create map with
							//key: clanRaid id, to value: another map
							if (!clanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewardsTemp
									.containsKey(clanRaidStageId)) {
								clanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewardsTemp
										.put(clanRaidStageId,
												new HashMap<Integer, ClanRaidStageReward>());
							}

							//get map of clanRaid stages related to current clanRaid id
							//stick clanRaidStageReward into the map of ClanRaidStageReward ids to ClanRaidStageReward objects
							Map<Integer, ClanRaidStageReward> clanRaidStageRewardIdsToClanRaidStageRewardsForClanRaidStageReward = clanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewardsTemp
									.get(clanRaidStageId);

							int clanRaidStageRewardId = clanRaidStageReward
									.getId();
							clanRaidStageRewardIdsToClanRaidStageRewardsForClanRaidStageReward
									.put(clanRaidStageRewardId,
											clanRaidStageReward);
							clanRaidStageRewardIdsToClanRaidStageRewardsTemp
									.put(clanRaidStageRewardId,
											clanRaidStageReward);
						}
						clanRaidStageIdsToIdsToRewards = clanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewardsTemp;
						idsToRewards = clanRaidStageRewardIdsToClanRaidStageRewardsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("clan raid stage reward retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticClanRaidIdsToClanRaidStageRewardIdsToClanRaidStageRewards();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private ClanRaidStageReward convertRSRowToClanRaidStageReward(
			ResultSet rs) throws SQLException {
		int i = 1;
		int id = rs.getInt(i++);
		int clanRaidStageId = rs.getInt(i++);
		int minOilReward = rs.getInt(i++);
		int maxOilReward = rs.getInt(i++);
		int minCashReward = rs.getInt(i++);
		int maxCashReward = rs.getInt(i++);
		int monsterId = rs.getInt(i++);
		int expectedMonsterRewardQuantity = rs.getInt(i++);

		ClanRaidStageReward clanRaidStageReward = new ClanRaidStageReward(id,
				clanRaidStageId, minOilReward, maxOilReward, minCashReward,
				maxCashReward, monsterId, expectedMonsterRewardQuantity);

		return clanRaidStageReward;
	}
}
