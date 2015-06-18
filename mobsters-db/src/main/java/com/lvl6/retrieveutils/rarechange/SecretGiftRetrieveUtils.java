package com.lvl6.retrieveutils.rarechange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.SecretGiftConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftConfigPojo;
import com.lvl6.mobsters.jooq.pojos.wrapper.SecretGiftConfigWrapper;

@Component
@DependsOn("gameServer")
public class SecretGiftRetrieveUtils {

	private static final Logger log = LoggerFactory
			.getLogger(SecretGiftRetrieveUtils.class);

	private static final SecretGiftComparator comparator = new SecretGiftComparator();
	private static final class SecretGiftComparator implements
			Comparator<SecretGiftConfigWrapper> {
		@Override
		public int compare(SecretGiftConfigWrapper o1, SecretGiftConfigWrapper o2) {
			if (o1.getNormalizedProbability() < o2
					.getNormalizedProbability()) {
				return -1;
			} else if (o1.getNormalizedProbability() > o2
					.getNormalizedProbability()) {
				return 1;
			} else if (o1.getSecretGiftConfigId() < o2.getSecretGiftConfigId()) {
				//since same probability, order by id
				return -1;
			} else if (o1.getSecretGiftConfigId() > o2.getSecretGiftConfigId()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Autowired
	protected SecretGiftConfigDao secretGiftConfigDao;

	private static Map<Integer, SecretGiftConfigPojo> idsToSecretGifts;
	private static double secretGiftProbabilitySum = 0D;
	private static TreeSet<SecretGiftConfigWrapper> christmasTree; //secretGiftTree;

//	private static final String TABLE_NAME = DBConstants.TABLE_SECRET_GIFT_CONFIG;

	public Map<Integer, SecretGiftConfigPojo> getIdsToSecretGifts() {
		if (null == idsToSecretGifts) {
			reload();
		}
		return idsToSecretGifts;
	}

	public SecretGiftConfigPojo getSecretGiftForId(int secretGiftId) {
		if (null == idsToSecretGifts) {
			reload();
		}

		if (!idsToSecretGifts.containsKey(secretGiftId)) {
			log.error("no secretGift for id={}", secretGiftId);
			return null;
		}
		return idsToSecretGifts.get(secretGiftId);
	}

	public Map<Integer, SecretGiftConfigPojo> getsForIds(Collection<Integer> ids) {
		if (null == idsToSecretGifts) {
//			setStaticIdsTos();
			reload();
		}
		Map<Integer, SecretGiftConfigPojo> returnMap = new HashMap<Integer, SecretGiftConfigPojo>();

		for (int id : ids) {
			SecretGiftConfigPojo sgc = getSecretGiftForId(id);
			returnMap.put(id, sgc);
		}
		return returnMap;
	}

	public SecretGiftConfigPojo nextSecretGift(double probability) {
		if (null == christmasTree) {
			log.error("object to select SecretGift nonexistent.");
			return null;
		}
		//selects the  with the least probability that is still greater
		//than the given probability
		SecretGiftConfigWrapper i = new SecretGiftConfigWrapper();
		i.setSecretGiftConfigId(0);
		i.setNormalizedProbability(probability);

		SecretGiftConfigWrapper secretGift = christmasTree.ceiling(i);

		log.info("for given probability={}, selected {}",
				probability, secretGift);

		if (null != secretGift) {
			return getSecretGiftForId(secretGift.getSecretGiftConfigId());
		}
		return null;
	}

	private void setStaticIdsToSecretGifts() {
		log.debug("setting static map of secretGiftIds to SecretGifts");

		Map<Integer, SecretGiftConfigPojo> idsToSecretGiftTemp =
				new HashMap<Integer, SecretGiftConfigPojo>();
		double secretGiftProbabilitySumTemp = 0D;

		try {
			//loop through each row and convert it into a java object
			for (SecretGiftConfigPojo sgc : secretGiftConfigDao.findAll()) {
				int id = sgc.getId();
				idsToSecretGiftTemp.put(id, sgc);

				secretGiftProbabilitySumTemp += sgc.getChanceToBeSelected();
			}
		} catch (Exception e) {
			log.error("retrieve all SecretGifts error.", e);
		}

		idsToSecretGifts = idsToSecretGiftTemp;
		secretGiftProbabilitySum = secretGiftProbabilitySumTemp;
	}

	private void setUpRandomSecretGiftSelection() {
		log.debug("setting setUpRandomSecretGiftSelection");
		if (secretGiftProbabilitySum <= 0) {
			log.error("There are no SecretGifts with probabilities set.");
			return;
		}
		//using a TreeSet to hold the s, so that it is easier
		//to select an  at random to reward a user.
		TreeSet<SecretGiftConfigWrapper> christmasTreeTemp =
				new TreeSet<SecretGiftConfigWrapper>(comparator);

		//sort  ids, not sure if necessary, but whatevs
		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll(idsToSecretGifts.keySet());

		Collections.sort(ids);

		// for each SecretGiftConfig ordered in ascending id numbers, set its chance
		// (out of 1) to be selected as a secret gift
		double doubleSoFar = 0D;
		for (Integer id : ids) {
			SecretGiftConfigPojo sgc = idsToSecretGifts.get(id);

			doubleSoFar += sgc.getChanceToBeSelected();
			double normalizedSecretGiftProbability = doubleSoFar
					/ secretGiftProbabilitySum;

			SecretGiftConfigWrapper sgcw =
					new SecretGiftConfigWrapper(sgc);
			sgcw.setNormalizedProbability(normalizedSecretGiftProbability);

			boolean added = christmasTreeTemp.add(sgcw);
			if (!added) {
				log.error("(shouldn't happen...) can't add SecretGiftWrapper={} to treeSet={}",
						sgc, christmasTreeTemp);
			}
		}

		christmasTree = christmasTreeTemp;
	}

	public void reload() {
		setStaticIdsToSecretGifts();
		setUpRandomSecretGiftSelection();
	}

}
