package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanGiftRewards;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ClanGiftRewardsRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, ClanGiftRewards> clanGiftRewardsIdsToClanGiftRewards;

	private static Map<Integer, List<ClanGiftRewards>> clanGiftIdsToClanGiftRewards;

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_GIFT_REWARD_CONFIG;

	public Map<Integer, ClanGiftRewards> getClanGiftRewardsIdsToClanGiftRewardss() {
		log.debug("retrieving all clan gift rewards data map");
		if (clanGiftRewardsIdsToClanGiftRewards == null) {
			setStaticClanGiftRewardsIdsToClanGiftRewardss();
		}
		return clanGiftRewardsIdsToClanGiftRewards;
	}

	public ClanGiftRewards getClanGiftRewardsForClanGiftRewardsId(int clanGiftRewardsId) {
		log.debug("retrieve clan gift reward data for clan gift reward "
				+ clanGiftRewardsId);
		if (clanGiftRewardsIdsToClanGiftRewards == null) {
			setStaticClanGiftRewardsIdsToClanGiftRewardss();
		}
		return clanGiftRewardsIdsToClanGiftRewards.get(clanGiftRewardsId);
	}

	public List<ClanGiftRewards> getClanGiftRewardsForClanGift(int clanGiftId) {
		log.debug("retrieve clan gift reward data for clan gift id "
				+ clanGiftId);
		if (clanGiftIdsToClanGiftRewards == null) {
			setStaticClanGiftRewardsIdsToClanGiftRewardss();
		}
		return clanGiftIdsToClanGiftRewards.get(clanGiftId);
	}

	private void setStaticClanGiftRewardsIdsToClanGiftRewardss() {
		log.debug("setting static map of clanGiftRewardsIds to clanGiftRewardss");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, ClanGiftRewards> clanGiftRewardsIdsToClanGiftRewardssTemp = new HashMap<Integer, ClanGiftRewards>();
						HashMap<Integer, List<ClanGiftRewards>> clanGiftIdsToClanGiftRewardsTemp = new HashMap<Integer, List<ClanGiftRewards>>();
						while (rs.next()) {  //should only be one
							ClanGiftRewards clanGiftRewards = convertRSRowToClanGiftRewards(rs);
							if (clanGiftRewards != null) {
								clanGiftRewardsIdsToClanGiftRewardssTemp.put(
										clanGiftRewards.getId(), clanGiftRewards);
								int clanGiftId = clanGiftRewards.getClanGiftId();

								if(clanGiftIdsToClanGiftRewardsTemp.containsKey(clanGiftId)) {
									clanGiftIdsToClanGiftRewardsTemp.get(clanGiftId).add(clanGiftRewards);
								}
								else {
									List<ClanGiftRewards> listOfRewards = new ArrayList<ClanGiftRewards>();
									listOfRewards.add(clanGiftRewards);
									clanGiftIdsToClanGiftRewardsTemp.put(clanGiftId, listOfRewards);
								}
							}
						}
						clanGiftRewardsIdsToClanGiftRewards = clanGiftRewardsIdsToClanGiftRewardssTemp;
						clanGiftIdsToClanGiftRewards = clanGiftIdsToClanGiftRewardsTemp;
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
		setStaticClanGiftRewardsIdsToClanGiftRewardss();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private ClanGiftRewards convertRSRowToClanGiftRewards(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.CLAN_GIFT_REWARD__ID);
		int clanGiftId = rs.getInt(DBConstants.CLAN_GIFT_REWARD__CLAN_GIFT_ID);
		int rewardId = rs.getInt(DBConstants.CLAN_GIFT_REWARD__REWARD_ID);
		float chanceOfDrop = rs.getFloat(DBConstants.CLAN_GIFT_REWARD__CHANCE_OF_DROP);

		ClanGiftRewards clanGiftRewards = new ClanGiftRewards(id, clanGiftId, rewardId, chanceOfDrop);
		return clanGiftRewards;
	}
}
