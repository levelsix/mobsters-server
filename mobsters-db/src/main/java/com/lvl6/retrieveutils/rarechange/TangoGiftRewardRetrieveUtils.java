package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.TangoGiftReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class TangoGiftRewardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final TangoGiftRewardComparator comparator = new TangoGiftRewardComparator();
	private static final class TangoGiftRewardComparator implements
		Comparator<TangoGiftReward> {

		@Override
		public int compare(TangoGiftReward o1, TangoGiftReward o2) {
			if (o1.getNormalizedProbability() < o2
					.getNormalizedProbability()) {
				return -1;
			} else if (o1.getNormalizedProbability() > o2
					.getNormalizedProbability()) {
				return 1;
			} else if (o1.getId() < o2.getId()) {
				//since same probability, order by id
				return -1;
			} else if (o1.getId() > o2.getId()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	private static Map<Integer, TangoGiftReward> idsToTangoGiftReward;
	private static Map<Integer, List<TangoGiftReward>> tangoGiftIdsToTangoGiftReward;
	private static Map<Integer, TreeSet<TangoGiftReward>> tangoGiftIdToTangoTree;
	private static Map<Integer, Float> tangoGiftIdToProbabilitySum;

	private static final String TABLE_NAME = DBConstants.TABLE_TANGO_GIFT_REWARD_CONFIG;

	public Map<Integer, TangoGiftReward> getIdsToTangoGiftRewards() {
		log.debug("retrieving all tango gift rewards data map");
		if (idsToTangoGiftReward == null) {
			setStaticTangoGiftRewardIdsToTangoGiftReward();
		}
		return idsToTangoGiftReward;
	}

//	public TangoGiftReward getTangoGiftRewardForTangoGiftRewardId(int tangoGiftRewardsId) {
//		log.debug("retrieve tango gift reward data for tango gift reward {}",
//				tangoGiftRewardsId);
//		if (idsToTangoGiftReward == null) {
//			setStaticTangoGiftRewardIdsToTangoGiftReward();
//		}
//		return idsToTangoGiftReward.get(tangoGiftRewardsId);
//	}

	public List<TangoGiftReward> getTangoGiftRewardForTangoGift(int tangoGiftId) {
		log.debug("retrieve tango gift reward data for tango gift id {}",
				tangoGiftId);
		if (tangoGiftIdsToTangoGiftReward == null) {
			setStaticTangoGiftRewardIdsToTangoGiftReward();
		}
		return tangoGiftIdsToTangoGiftReward.get(tangoGiftId);
	}

	public TangoGiftReward nextTangoGiftReward(int tangoGiftId, float probability) {
		if (null == tangoGiftIdsToTangoGiftReward) {
			log.error("object to select TangoGift nonexistent.");
			return null;
		}

		if (!tangoGiftIdsToTangoGiftReward.containsKey(tangoGiftId))
		{
			log.error("object to select TangoGift nonexistent.");
			return null;
		}
		TreeSet<TangoGiftReward> tangoChristmasTree = tangoGiftIdToTangoTree
				.get(tangoGiftId);

		//selects the item with the least probability that is still greater
		//than the given probability
		TangoGiftReward tangoGiftReward = new TangoGiftReward();
		tangoGiftReward.setId(0);
		tangoGiftReward.setNormalizedProbability(probability);

		TangoGiftReward gift = tangoChristmasTree.ceiling(tangoGiftReward);

		log.info("for given probability={}, selected {}",
				probability, gift);
		return gift;
	}

	private void setStaticTangoGiftRewardIdsToTangoGiftReward() {
		log.debug("setting static map of tangoGiftRewardsIds to tangoGiftRewardss");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, TangoGiftReward> idsToTangoGiftRewardsTemp =
								new HashMap<Integer, TangoGiftReward>();
						HashMap<Integer, List<TangoGiftReward>> tangoGiftIdsToTangoGiftRewardTemp =
								new HashMap<Integer, List<TangoGiftReward>>();
						HashMap<Integer, Float> tangoGiftIdToProbabilitySumTemp =
								new HashMap<Integer, Float>();

						while (rs.next()) {
							TangoGiftReward tangoGiftReward = convertRSRowToTangoGiftReward(rs);
							if (null == tangoGiftReward)
							{
								continue;
							}

							int id = tangoGiftReward.getId();
							idsToTangoGiftRewardsTemp.put(id, tangoGiftReward);

							int tangoGiftId = tangoGiftReward.getTangoGiftId();

							//Grouping by tangoGiftId
							if(!tangoGiftIdsToTangoGiftRewardTemp.containsKey(tangoGiftId)) {
								tangoGiftIdsToTangoGiftRewardTemp.put(tangoGiftId, new ArrayList<TangoGiftReward>());
								tangoGiftIdToProbabilitySumTemp.put(tangoGiftId, 0F);
							}

							List<TangoGiftReward> listOfRewards = tangoGiftIdsToTangoGiftRewardTemp.
									get(tangoGiftId);
							listOfRewards.add(tangoGiftReward);

							float probSoFar = tangoGiftIdToProbabilitySumTemp.get(tangoGiftId);
							tangoGiftIdToProbabilitySumTemp.put(tangoGiftId,
									probSoFar + tangoGiftReward.getChanceOfDrop());
						}

						idsToTangoGiftReward = idsToTangoGiftRewardsTemp;
						tangoGiftIdsToTangoGiftReward = tangoGiftIdsToTangoGiftRewardTemp;
						tangoGiftIdToProbabilitySum = tangoGiftIdToProbabilitySumTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("TangoGiftReward retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private void setUpRandomTangoGiftSelection() {
		log.debug("setting setUpRandomTangoGiftSelection");

		if (null == tangoGiftIdsToTangoGiftReward || tangoGiftIdsToTangoGiftReward.isEmpty())
		{
			log.info("no TangoGiftRewards to randomly select from");
			return;
		}

		tangoGiftIdToTangoTree = new HashMap<Integer, TreeSet<TangoGiftReward>>();
		for (Integer tangoGiftId : tangoGiftIdsToTangoGiftReward.keySet()) {
			setUpRandomSelectionForTangoGift(tangoGiftId);
		}

	}

	private void setUpRandomSelectionForTangoGift(Integer tangoGiftId) {
		List<TangoGiftReward> rewards = tangoGiftIdsToTangoGiftReward.get(tangoGiftId);
		float probSum = tangoGiftIdToProbabilitySum.get(tangoGiftId);

		//using a TreeSet to hold the items, so that it is easier
		//to select an Item at random to reward a user.
		TreeSet<TangoGiftReward> tangoTreeTemp = new TreeSet<TangoGiftReward>(comparator);

		float floatSoFar = 0F;
		for (TangoGiftReward tgr : rewards) {
			floatSoFar += tgr.getChanceOfDrop();
			float normalizedProb = floatSoFar / probSum;

			tgr.setNormalizedProbability(normalizedProb);

			boolean added = tangoTreeTemp.add(tgr);
			if (!added) {
				log.error("(shouldn't happen...) can't add TangoGift={} to treeSet={}",
						tgr, tangoTreeTemp);
			}
		}
		tangoGiftIdToTangoTree.put(tangoGiftId, tangoTreeTemp);
	}

	public void reload() {
		setStaticTangoGiftRewardIdsToTangoGiftReward();
		setUpRandomTangoGiftSelection();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private TangoGiftReward convertRSRowToTangoGiftReward(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.TANGO_GIFT_REWARD__ID);
		int tangoGiftId = rs.getInt(DBConstants.TANGO_GIFT_REWARD__TANGO_GIFT_ID);
		int rewardId = rs.getInt(DBConstants.TANGO_GIFT_REWARD__REWARD_ID);
		float chanceOfDrop = rs.getFloat(DBConstants.TANGO_GIFT_REWARD__CHANCE_OF_DROP);

		TangoGiftReward tangoGiftReward = new TangoGiftReward(id, tangoGiftId, rewardId, chanceOfDrop);
		return tangoGiftReward;
	}
}
