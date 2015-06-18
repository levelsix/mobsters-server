package com.lvl6.retrieveutils.rarechange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.GiftRewardConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftRewardConfigPojo;
import com.lvl6.mobsters.jooq.pojos.wrapper.GiftRewardConfigWrapper;

@Component
@DependsOn("gameServer")
public class GiftRewardRetrieveUtils {

	private static final Logger log = LoggerFactory
			.getLogger(GiftRewardRetrieveUtils.class);

	private static final GiftRewardComparator comparator = new GiftRewardComparator();
	private static final class GiftRewardComparator implements Comparator<GiftRewardConfigWrapper> {

		@Override
		public int compare(GiftRewardConfigWrapper o1,
				GiftRewardConfigWrapper o2)
		{
			double o1Chance = o1.getNormalizedProbability();
			double o2Chance = o2.getNormalizedProbability();

			if (o1Chance < o2Chance) {
				return -1;
			} else if (o1Chance > o2Chance) {
				return 1;
			} else if (o1.getGiftRewardConfigId() < o2.getGiftRewardConfigId()) {
				//since same probability, order by id
				return -1;
			} else if (o1.getGiftRewardConfigId() > o2.getGiftRewardConfigId()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	@Autowired
	protected GiftRewardConfigDao giftRewardConfigDao;

//	private static final String TABLE_NAME = DBConstants.TABLE_GIFT_REWARD_CONFIG;
	private static Map<Integer, GiftRewardConfigPojo> idsToGiftRewards;
	private static Map<Integer, Double> giftIdToProbabilitySum;
	private static Map<Integer, Map<Integer, GiftRewardConfigWrapper>> giftIdToGiftRewards;
	private static Map<Integer, TreeSet<GiftRewardConfigWrapper>> giftIdToGiftRewardsTree;

	public boolean rewardsExist(int giftId) {
		log.debug("checking if rewardsExist for giftId {}", giftId);
		if (null == giftIdToGiftRewards) {
			setStaticIdsToGifts();
		}
		if (!giftIdToGiftRewards.containsKey(giftId)) {
			log.warn("no GiftReward entry with giftId={}", giftId);
			return false;
		}

		if (giftIdToGiftRewards.get(giftId).isEmpty()) {
			log.warn("no GiftRewards with giftId={}", giftId);
			return false;
		}

		return true;
	}

	public Map<Integer, GiftRewardConfigPojo> getIdsToGiftRewards() {
		log.debug("retrieving all GiftRewards");
		if (null == idsToGiftRewards) {
			setStaticIdsToGifts();
		}
		return idsToGiftRewards;
	}

	public Collection<GiftRewardConfigPojo> getGiftRewardForGiftId(int giftId) {
		log.debug("retrieving all GiftReward for giftId {}", giftId);
		if (null == giftIdToGiftRewards) {
			setStaticIdsToGifts();
		}
		if (!giftIdToGiftRewards.containsKey(giftId)) {
			log.warn("no GiftRewards with giftId={}", giftId);
			return new ArrayList<GiftRewardConfigPojo>();
		}


		Map<Integer, GiftRewardConfigWrapper> idToReward =
				giftIdToGiftRewards.get(giftId);

		Collection<GiftRewardConfigPojo> rewards = new ArrayList<GiftRewardConfigPojo>();
		for (Integer id : idToReward.keySet())
		{
			GiftRewardConfigPojo grc = getGiftReward(id);
			if (null != grc) {
				rewards.add(grc);
			}
		}

		return rewards;
	}

	public GiftRewardConfigPojo getGiftReward(int id) {
		log.debug("retrieving all GiftReward for id {}", id);
		if (null == idsToGiftRewards) {
			setStaticIdsToGifts();
		}
		if (!idsToGiftRewards.containsKey(id)) {
			log.warn("no GiftReward with id={}", id);
			return null;
		}

		return idsToGiftRewards.get(id);
	}

	public GiftRewardConfigPojo nextGiftReward(int giftId, double probability) {
		if (null == giftIdToGiftRewardsTree || giftIdToGiftRewardsTree.isEmpty()) {
			log.error("object to select random GiftRewards nonexistent.");
			return null;
		}

		if (!giftIdToGiftRewardsTree.containsKey(giftId)) {
			log.error("object to select random GiftRewards doesn't contain giftId={}",
					giftId);
			return null;
		}

		TreeSet<GiftRewardConfigWrapper> rewardTree = giftIdToGiftRewardsTree.get(giftId);

		//selects the GiftReward with the least probability that is still
		//greater than the given probability
		GiftRewardConfigWrapper grcw = new GiftRewardConfigWrapper(0, probability);

		GiftRewardConfigWrapper nextGiftReward = rewardTree.ceiling(grcw);

		if (null == nextGiftReward) {
			log.error("for given giftId={} and probability={}, no reward exists",
					giftId, probability);
			return null;
		}
		int giftRewardId = nextGiftReward.getGiftRewardConfigId();
		return getGiftReward(giftRewardId);
	}

	private void setStaticIdsToGifts() {
		log.debug("setting static map of giftIds to GiftRewards");

		Map<Integer, GiftRewardConfigPojo> idsToGiftRewardsTemp = new HashMap<Integer, GiftRewardConfigPojo>();
		Map<Integer, Double> giftIdToProbabilitySumTemp = new HashMap<Integer, Double>();
		Map<Integer, Map<Integer, GiftRewardConfigWrapper>> giftIdToGiftRewardsTemp =
				new HashMap<Integer, Map<Integer, GiftRewardConfigWrapper>>();
		try {
			for (GiftRewardConfigPojo grc : giftRewardConfigDao.findAll()) {
				int id = grc.getId();
				idsToGiftRewardsTemp.put(id, grc);

				int giftId = grc.getGiftId();
				double sumSoFar = 0D;
				if (giftIdToProbabilitySumTemp.containsKey(giftId)) {
					sumSoFar = giftIdToProbabilitySumTemp.get(giftId);
				}
				sumSoFar += grc.getChanceOfDrop();
				giftIdToProbabilitySumTemp.put(giftId, sumSoFar);

				//prepping to normalize probabilities
				if (!giftIdToGiftRewardsTemp.containsKey(giftId)) {
					giftIdToGiftRewardsTemp.put(giftId, new HashMap<Integer, GiftRewardConfigWrapper>());
				}
				Map<Integer, GiftRewardConfigWrapper> idToWrapper =
						giftIdToGiftRewardsTemp.get(giftId);
				idToWrapper.put(id, new GiftRewardConfigWrapper(grc));
			}

		} catch (Exception e) {
			log.error("retrieve all GiftRewards error.", e);
		}
		idsToGiftRewards = idsToGiftRewardsTemp;
		giftIdToProbabilitySum = giftIdToProbabilitySumTemp;
		giftIdToGiftRewards = giftIdToGiftRewardsTemp;

	}

	private void setUpRandomGiftRewardSelection() {
		if (null == giftIdToProbabilitySum || giftIdToProbabilitySum.isEmpty()) {
			log.error("No GiftRewards with which to set up random selection");
			return;
		}

		if (null == giftIdToGiftRewards) {
			log.info("reloading GiftRewards");
			setStaticIdsToGifts();
		}
		if (null == giftIdToGiftRewards || giftIdToGiftRewards.isEmpty()) {
			log.error("No GiftRewards");
			return;
		}

		Map<Integer, TreeSet<GiftRewardConfigWrapper>> temp = normalizeGiftRewards();
		giftIdToGiftRewardsTree = temp;
	}

	private Map<Integer, TreeSet<GiftRewardConfigWrapper>> normalizeGiftRewards()
	{
		Map<Integer, TreeSet<GiftRewardConfigWrapper>> temp =
				new HashMap<Integer, TreeSet<GiftRewardConfigWrapper>>();

		for (Integer giftId : giftIdToGiftRewards.keySet()) {
			Map<Integer, GiftRewardConfigWrapper> idToGiftRewards =
					giftIdToGiftRewards.get(giftId);

			double probSum = giftIdToProbabilitySum.get(giftId);

			TreeSet<GiftRewardConfigWrapper> aTree =
					normalizeGiftRewardsForGift(giftId, probSum, idToGiftRewards);

			temp.put(giftId, aTree);
		}

		return temp;
	}

	private TreeSet<GiftRewardConfigWrapper> normalizeGiftRewardsForGift(
			int giftId, double probSum,
			Map<Integer, GiftRewardConfigWrapper> idToGiftRewardWrappers)
	{
		TreeSet<GiftRewardConfigWrapper> grcwTree =
				new TreeSet<GiftRewardConfigWrapper>(comparator);
		if (probSum <= 0) {
			log.warn("no probability for giftId={}. {}", giftId, idToGiftRewardWrappers);
			return grcwTree;
		}

		Set<Integer> ids = idToGiftRewardWrappers.keySet();

		//sortIds, not sure if necessary, but whatevs
		List<Integer> idsList = new ArrayList<Integer>(ids);
		Collections.sort(idsList);

		double runningSum = 0D;
		for (Integer id : idsList) {
			GiftRewardConfigWrapper grcw = idToGiftRewardWrappers.get(id);
			runningSum += grcw.getNormalizedProbability();
			double normalizedProbability = runningSum / probSum;

			grcw.setNormalizedProbability(normalizedProbability);
			boolean added = grcwTree.add(grcw);
			if (!added) {
				log.error("unable to add {} to TreeSet={}", grcw, grcwTree);
			}
		}

		return grcwTree;
	}

	public void reload() {
		setStaticIdsToGifts();
		setUpRandomGiftRewardSelection();
	}

}
