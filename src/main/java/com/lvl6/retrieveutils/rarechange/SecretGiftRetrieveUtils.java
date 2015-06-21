package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.SecretGift;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SecretGiftRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(SecretGiftRetrieveUtils.class);
	private static final SecretGiftSecretGiftComparator comparator = new SecretGiftSecretGiftComparator();

	private static final class SecretGiftSecretGiftComparator implements
			Comparator<SecretGift> {
		@Override
		public int compare(SecretGift o1, SecretGift o2) {
			if (o1.getNormalizedSecretGiftProbability() < o2
					.getNormalizedSecretGiftProbability()) {
				return -1;
			} else if (o1.getNormalizedSecretGiftProbability() > o2
					.getNormalizedSecretGiftProbability()) {
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

	private static Map<Integer, SecretGift> idsToSecretGifts;
	private static TreeSet<SecretGift> christmasTree; //secretGiftTree
	private static float probabilitySum;

	private static final String TABLE_NAME = DBConstants.TABLE_SECRET_GIFT_CONFIG;

	public SecretGift getGiftForId(int id) {
		log.debug("retrieve SecretGift for id: {}", id);
		if (idsToSecretGifts == null) {
			setStaticIdsToSecretGifts();
		}

		if (!idsToSecretGifts.containsKey(id)) {
			log.error("no SecretGift for id={}", id);
			return null;
		}
		SecretGift gift = idsToSecretGifts.get(id);

		return gift;
	}

	public SecretGift nextGift(float probability) {
		if (null == christmasTree) {
			log.error("object to select secret gift nonexistent.");
			return null;
		}
		//selects the item with the least probability that is still greater
		//than the given probability
		SecretGift sg = new SecretGift();
		sg.setId(0);
		sg.setNormalizedSecretGiftProbability(probability);

		SecretGift secretGift = christmasTree.ceiling(sg);

		log.info("for giving probability={}, selected {}",
				probability, secretGift);
		return secretGift;
	}

	private void setStaticIdsToSecretGifts() {
		log.debug("setting static map of names to SecretGifts");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		Map<Integer, SecretGift> idsToTogglesTemp = new HashMap<Integer, SecretGift>();
		float probabilitySumTemp = 1F;
		float runningProbabilitySumTemp = 0F;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						while (rs.next()) {
							SecretGift sg = convertRSRowToTeamCenter(rs);
							if (sg == null) {
								continue;
							}
							idsToTogglesTemp.put(sg.getRewardId(), sg);
							runningProbabilitySumTemp += sg.getChanceToBeSelected();
						}
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("SecretGift retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		idsToSecretGifts = idsToTogglesTemp;
		if (runningProbabilitySumTemp > 0) {
			probabilitySumTemp = runningProbabilitySumTemp;
		}
		probabilitySum = probabilitySumTemp;
	}

	private void setUpRandomGiftSelection() {
		log.debug("setting setUpRandomItemSelection");
		if (probabilitySum <= 0) {
			log.error("There are no items with secret gift probabilities set.");
			return;
		}
		//using a TreeSet to hold the items, so that it is easier
		//to select an Item at random to reward a user.
		TreeSet<SecretGift> christmasTreeTemp = new TreeSet<SecretGift>(comparator);

		//sort item ids, not sure if necessary, but whatevs
		List<Integer> giftIds = new ArrayList<Integer>();
		giftIds.addAll(idsToSecretGifts.keySet());

		Collections.sort(giftIds);

		// for each item ordered in ascending id numbers, set its chance
		// (out of 1) to be selected as a secret gift
		float floatSoFar = 0F;
		for (Integer giftId : giftIds) {
			SecretGift gift = idsToSecretGifts.get(giftId);

			floatSoFar += gift.getChanceToBeSelected();
			float normalizedSecretGiftProbability = floatSoFar
					/ probabilitySum;

			gift.setNormalizedSecretGiftProbability(normalizedSecretGiftProbability);

			boolean added = christmasTreeTemp.add(gift);
			if (!added) {
				log.error("(shouldn't happen...) can't add SecretGift={} to treeSet={}",
						gift, christmasTreeTemp);
			}
		}

		christmasTree = christmasTreeTemp;
	}

	public void reload() {
		setStaticIdsToSecretGifts();
		setUpRandomGiftSelection();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private SecretGift convertRSRowToTeamCenter(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.SECRET_GIFT__ID);
		int rewardId = rs.getInt(DBConstants.SECRET_GIFT__REWARD_ID);
		float chanceToBeSelected = rs.getFloat(
				DBConstants.SECRET_GIFT__CHANCE_TO_BE_SELECTED);

		return new SecretGift(id, rewardId, chanceToBeSelected);
	}
}
