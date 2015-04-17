package com.lvl6.retrieveutils.rarechange;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MiniJob;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.SharedEnumConfigProto.Quality;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniJobRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final MiniJobProxyComparator comparator = new MiniJobProxyComparator();
	private static final class MiniJobProxyComparator implements Comparator<MiniJobProxy>
	{
		@Override
		public int compare(MiniJobProxy o1, MiniJobProxy o2) {
			float o1Chance = o1.getNormalizedProbabilityPosition();
			float o2Chance = o2.getNormalizedProbabilityPosition();

			if (o1Chance < o2Chance) {
				return -1;
			} else if (o1Chance > o2Chance) {
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

	private static String COMMON = Quality.COMMON.name();
	private static String RARE = Quality.RARE.name();
	private static String SUPER = Quality.SUPER.name();
	private static String ULTRA = Quality.ULTRA.name();
	private static String EPIC = Quality.EPIC.name();
	private static String LEGENDARY = Quality.LEGENDARY.name();

	private static Map<Integer, MiniJob> miniJobIdsToMiniJobs;
	private static Map<Integer, Map<Integer, MiniJob>> structureIdToMiniJobIdToMiniJob;
	private static Map<Integer, Float> structureIdToSumMiniJobProbability;

	private static Map<Integer, Map<String, TreeSet<MiniJobProxy>>> structIdToQualityToTree;
	private static Map<Integer, Map<String, Float>> structIdToQualityToProbabilitySum;

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_JOB_CONFIG;

	//CONTROLLER LOGIC******************************************************************

	//RETRIEVE QUERIES*********************************************************************
	public Map<Integer, MiniJob> getMiniJobIdsToMiniJobs() {
		log.debug("retrieving all miniJob data");
		if (null == miniJobIdsToMiniJobs) {
			setStaticMiniJobIdsToMiniJobs();
		}
		return miniJobIdsToMiniJobs;
	}

	public MiniJob getMiniJobForMiniJobId(int miniJobId) {
		log.debug("retrieving miniJob with miniJobId " + miniJobId);
		if (null == miniJobIdsToMiniJobs) {
			setStaticMiniJobIdsToMiniJobs();
		}
		if (!miniJobIdsToMiniJobs.containsKey(miniJobId)) {
			log.error("no MiniJobs for miniJobId=" + miniJobId);
			return null;
		}
		return miniJobIdsToMiniJobs.get(miniJobId);
	}

	public Map<Integer, MiniJob> getMiniJobForStructId(int structId) {
		log.debug("retrieving miniJob with structId " + structId);
		if (null == structureIdToMiniJobIdToMiniJob) {
			setStaticMiniJobIdsToMiniJobs();
		}

		if (!structureIdToMiniJobIdToMiniJob.containsKey(structId)) {
			log.error("no MiniJobs for structId=" + structId);
			return new HashMap<Integer, MiniJob>();
		}

		Map<Integer, MiniJob> miniJobIdToMiniJobs = structureIdToMiniJobIdToMiniJob
				.get(structId);

		return miniJobIdToMiniJobs;
	}

	public float getMiniJobProbabilitySumForStructId(int structId) {
		log.debug("retrieving MiniJob probability sum for structId=" + structId);
		if (null == structureIdToSumMiniJobProbability) {
			setStaticMiniJobIdsToMiniJobs();
		}

		if (!structureIdToSumMiniJobProbability.containsKey(structId)) {
			log.error("no MiniJobs with probabilities for structId=" + structId);
			return 1F;
		}

		float probabilitySum = structureIdToSumMiniJobProbability.get(structId);

		if (0 == probabilitySum) {
			log.error("sum of probabilities of MiniJobs for structId="
					+ structId + ", is 0, setting it to 1");
			probabilitySum = 1F;
		}

		return probabilitySum;
	}

	public MiniJob nextMiniJob(
			int structId, String quality, float probability)
	{
		if ( null == structIdToQualityToTree )
		{
			log.error("object to select random MiniJob nonexistent");
			return null;
		}
		if ( !structIdToQualityToTree.containsKey(structId)) {
			log.error("object to select random MiniJob doesn't contain structId={}",
					structId);
			return null;
		}
		Map<String, TreeSet<MiniJobProxy>> possibleMiniJobs =
				structIdToQualityToTree.get(structId);
		if ( !possibleMiniJobs.containsKey(quality) )
		{
			log.error("object to select random MiniJob doesn't contain quality={}",
					quality);
			return null;
		}

		TreeSet<MiniJobProxy> miniJobTree = possibleMiniJobs.get(quality);

		//selects the MiniJob with the least probability that is still greater
		//than the given probability
		MiniJobProxy mjp = new MiniJobProxy();
		mjp.setId(0);
		mjp.setNormalizedProbabilityPosition(probability);

		MiniJobProxy nextMj = miniJobTree.ceiling(mjp);

		int miniJobId = nextMj.getId();

		return miniJobIdsToMiniJobs.get(miniJobId);
	}

	//RETRIEVE QUERIES*********************************************************************


	private void setStaticMiniJobIdsToMiniJobs() {
		log.debug("setting static map of miniJobIds to miniJobs");

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
						Map<Integer, MiniJob> tmp = new HashMap<Integer, MiniJob>();
						Map<Integer, Map<Integer, MiniJob>> structIdToMiniJobIdToMiniJob = new HashMap<Integer, Map<Integer, MiniJob>>();
						Map<Integer, Float> structIdToProbabilitySum = new HashMap<Integer, Float>();

						Map<Integer, Map<String, Float>> structIdToQualityToProbabilitySumTemp =
								new HashMap<Integer, Map<String, Float>>();

						while (rs.next()) {
							MiniJob miniJob = convertRSRowToMiniJob(rs, rand);
							if (null == miniJob) {
								continue;
							}

							int structId = miniJob.getRequiredStructId();
							if (!structIdToMiniJobIdToMiniJob
									.containsKey(structId)) {
								structIdToMiniJobIdToMiniJob.put(structId,
										new HashMap<Integer, MiniJob>());
							}
							int miniJobId = miniJob.getId();
							tmp.put(miniJobId, miniJob);

							Map<Integer, MiniJob> miniJobIdToMiniJob = structIdToMiniJobIdToMiniJob
									.get(structId);
							miniJobIdToMiniJob.put(miniJobId, miniJob);

							float prob = miniJob.getChanceToAppear();
							float probSumSoFar = 0;
							if (structIdToProbabilitySum.containsKey(structId)) {
								probSumSoFar = structIdToProbabilitySum
										.get(structId);
							}

							structIdToProbabilitySum.put(structId, prob
									+ probSumSoFar);


							//linking a structure id with probability sums of its MiniJobs separated by Quality
							setMiniJobProbabilityPos(structIdToQualityToProbabilitySumTemp,
									miniJob, structId);
						}
						miniJobIdsToMiniJobs = tmp;
						structureIdToMiniJobIdToMiniJob = structIdToMiniJobIdToMiniJob;
						structureIdToSumMiniJobProbability = structIdToProbabilitySum;

						structIdToQualityToProbabilitySum = structIdToQualityToProbabilitySumTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("miniJob retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private void setMiniJobProbabilityPos(
			Map<Integer, Map<String, Float>> structIdToQualityToProbabilitySumTemp,
			MiniJob miniJob, int structId)
	{
		if (!structIdToQualityToProbabilitySumTemp.containsKey(structId)) {
			structIdToQualityToProbabilitySumTemp.put(
					structId, new HashMap<String, Float>());
		}
		Map<String, Float> qualityToSum = structIdToQualityToProbabilitySumTemp
				.get(structId);

		String quality = miniJob.getQuality();
		float chanceToAppear = miniJob.getChanceToAppear();

		updateProbabilitySumsForQuality(qualityToSum, quality, chanceToAppear);
	}

	private void updateProbabilitySumsForQuality(Map<String, Float> qualityToSum,
			String quality, float chanceToAppear) {
		String qualityTemp = quality;
		if (LEGENDARY.equals(qualityTemp)) {
			updateProbabilitySumForQuality(chanceToAppear, qualityTemp, qualityToSum);
			qualityTemp = EPIC;
		}

		if (EPIC.equals(qualityTemp)) {
			updateProbabilitySumForQuality(chanceToAppear, qualityTemp, qualityToSum);
			qualityTemp = ULTRA;
		}

		if (ULTRA.equals(qualityTemp)) {
			updateProbabilitySumForQuality(chanceToAppear, qualityTemp, qualityToSum);
			qualityTemp = SUPER;
		}

		if (SUPER.equals(qualityTemp)) {
			updateProbabilitySumForQuality(chanceToAppear, qualityTemp, qualityToSum);
			qualityTemp = RARE;
		}

		if (RARE.equals(qualityTemp)) {
			updateProbabilitySumForQuality(chanceToAppear, qualityTemp, qualityToSum);
			qualityTemp = COMMON;

		}

		if (COMMON.equals(qualityTemp)) {
			updateProbabilitySumForQuality(chanceToAppear, qualityTemp, qualityToSum);

		} else {
			log.error("Quality not supported: {}", qualityTemp);
		}
	}

	private void updateProbabilitySumForQuality(float chanceToAppear, String quality,
			Map<String, Float> qualityToSum)
	{
		float probabilitySum = 0;
		if (qualityToSum.containsKey(quality)) {
			probabilitySum = qualityToSum.get(quality);
		}
		qualityToSum.put(quality, probabilitySum + chanceToAppear);
	}

	private void setUpRandomMiniJobSelection() {
		if (null == structIdToQualityToProbabilitySum ||
				structIdToQualityToProbabilitySum.isEmpty())
		{
			log.error("There are no mini jobs with chance to appear set.");
			return;
		}

		if (null == structureIdToMiniJobIdToMiniJob ||
				structureIdToMiniJobIdToMiniJob.isEmpty())
		{
			log.info("reloading");
			setStaticMiniJobIdsToMiniJobs();
		}
		//precaution
		if (null == structIdToQualityToProbabilitySum ||
				structIdToQualityToProbabilitySum.isEmpty())
		{
			log.error("There are no mini jobs with chance to appear set.");
			return;
		}
		if (null == structureIdToMiniJobIdToMiniJob ||
				structureIdToMiniJobIdToMiniJob.isEmpty())
		{
			return;
		}

		Map<Integer, Map<String, TreeSet<MiniJobProxy>>> structIdToQualityToTreeTemp =
				new HashMap<Integer, Map<String, TreeSet<MiniJobProxy>>>();

		normalizeMiniJobProbabilitiesForAllStructures(structIdToQualityToTreeTemp);

		structIdToQualityToTree = structIdToQualityToTreeTemp;
	}

	private void normalizeMiniJobProbabilitiesForAllStructures(
			Map<Integer, Map<String, TreeSet<MiniJobProxy>>> structIdToQualityToTreeTemp)
	{

		for (Integer structId : structureIdToMiniJobIdToMiniJob.keySet()) {
			Map<Integer, MiniJob> miniJobIdToMj = structureIdToMiniJobIdToMiniJob.get(structId);
			Set<Integer> mjIds = miniJobIdToMj.keySet();

			//sort ids, not sure if necessary, but whatevs
			List<Integer> miniJobIds = new ArrayList<Integer>();
			miniJobIds.addAll(mjIds);
			Collections.sort(miniJobIds);

			//holds the summed probability, to be used to calculate the normalized
			//probabilities
			if (!structIdToQualityToProbabilitySum.containsKey(structId)) {
				log.error("no probability sum for structId: {}. existing: {}",
						structId, structIdToQualityToProbabilitySum);
				continue;
			}
			Map<String, Float> qualityToProbabilitySum =
					structIdToQualityToProbabilitySum.get(structId);

			Map<String, TreeSet<MiniJobProxy>> qualityToMiniJobTree =
					normalizeMiniJobProbabilities(structId, miniJobIdToMj, mjIds,
							qualityToProbabilitySum);

			structIdToQualityToTreeTemp.put(structId, qualityToMiniJobTree);
		}

	}

	private Map<String, TreeSet<MiniJobProxy>> normalizeMiniJobProbabilities(
			Integer structId, Map<Integer, MiniJob> miniJobIdToMj,
			Set<Integer> mjIds, Map<String, Float> qualityToProbabilitySum)
	{
		Map<String, TreeSet<MiniJobProxy>> qualityToMiniJobTree =
				new HashMap<String, TreeSet<MiniJobProxy>>();

		//holds the running probability sum
		Map<String, Float> qualityToRunningSum = new HashMap<String, Float>();
		for (Integer mjId : mjIds)
		{
			//compute this mini job's normalizedProbabilityPosition
			MiniJob mj = miniJobIdToMj.get(mjId);
			String quality = mj.getQuality();
			float chanceToAppear = mj.getChanceToAppear();

			if (!qualityToProbabilitySum.containsKey(quality)) {
				log.error("no probability sum for structId={}, quality={})",
						structId, quality);
				continue;
			}
			Map<String, Float> qualityToNormalizedProbability =
					computeNormalizedProbabilityForQualities(mj, mjId, quality,
					chanceToAppear, qualityToProbabilitySum, qualityToRunningSum);

			//keep track of the running probability sum for all the
			//appropriate qualities
			updateProbabilitySumsForQuality(qualityToRunningSum, quality,
					chanceToAppear);

			//populate qualityToMiniJobTree
			arborizeProbabilities(mjId, qualityToNormalizedProbability,
					qualityToMiniJobTree);
		}
		return qualityToMiniJobTree;
	}

	private Map<String, Float> computeNormalizedProbabilityForQualities(
			MiniJob mj, int mjId,
			String quality, float chanceToAppear,
			Map<String, Float> qualityToProbabilitySum,
			Map<String, Float> qualityToRunningSum)
	{
		Map<String, Float> qualityToNormalizedProbability =
				new HashMap<String, Float>();
		float floatSoFar = chanceToAppear;

		String qualityTemp = quality;
		if (LEGENDARY.equals(qualityTemp)) {
			computeNormalizedProbabilityForQuality(qualityTemp,
					qualityToProbabilitySum, qualityToRunningSum,
					qualityToNormalizedProbability, floatSoFar);
			qualityTemp = EPIC;
		}
		if (EPIC.equals(qualityTemp)) {
			computeNormalizedProbabilityForQuality(qualityTemp,
					qualityToProbabilitySum, qualityToRunningSum,
					qualityToNormalizedProbability, floatSoFar);
			qualityTemp = ULTRA;
		}
		if (ULTRA.equals(qualityTemp)) {
			computeNormalizedProbabilityForQuality(qualityTemp,
					qualityToProbabilitySum, qualityToRunningSum,
					qualityToNormalizedProbability, floatSoFar);
			qualityTemp = SUPER;
		}
		if (SUPER.equals(qualityTemp)) {
			computeNormalizedProbabilityForQuality(qualityTemp,
					qualityToProbabilitySum, qualityToRunningSum,
					qualityToNormalizedProbability, floatSoFar);
			qualityTemp = RARE;
		}
		if (RARE.equals(qualityTemp)) {
			computeNormalizedProbabilityForQuality(qualityTemp,
					qualityToProbabilitySum, qualityToRunningSum,
					qualityToNormalizedProbability, floatSoFar);
			qualityTemp = COMMON;
		}

		if (COMMON.equals(qualityTemp)) {
			computeNormalizedProbabilityForQuality(qualityTemp,
					qualityToProbabilitySum, qualityToRunningSum,
					qualityToNormalizedProbability, floatSoFar);

		} else {
			log.error("invalid quality:{}, mj={}", qualityTemp, mj);
		}

		return qualityToNormalizedProbability;
	}

	private void computeNormalizedProbabilityForQuality(String quality,
			Map<String, Float> qualityToProbabilitySum,
			Map<String, Float> qualityToRunningSum,
			Map<String, Float> qualityToNormalizedProbability, float floatSoFar)
	{
		if (qualityToRunningSum.containsKey(quality)) {
			floatSoFar += qualityToRunningSum.get(quality);
		}
		float probabilitySum = qualityToProbabilitySum.get(quality);
		float normalizedProbability = floatSoFar / probabilitySum;

		qualityToNormalizedProbability.put(quality, normalizedProbability);
	}

	//populate qualityToMiniJobTree
	private void arborizeProbabilities( int mjId,
			Map<String, Float> qualityToNormalizedProbability,
			Map<String, TreeSet<MiniJobProxy>> qualityToMiniJobTree )
	{
		for (String quality : qualityToNormalizedProbability.keySet())
		{
			float probability = qualityToNormalizedProbability.get(quality);
			MiniJobProxy mjp = new MiniJobProxy(mjId, probability);

			if (!qualityToMiniJobTree.containsKey(quality))
			{
				TreeSet<MiniJobProxy> mjpTree = new TreeSet<MiniJobProxy>(comparator);
				qualityToMiniJobTree.put(quality, mjpTree);
			}

			TreeSet<MiniJobProxy> mjpTree = qualityToMiniJobTree.get(quality);
			boolean added = mjpTree.add(mjp);
			if (!added)
			{
				log.error("could not add MiniJobProxy={} to TreeSet={}",
						mjp, mjpTree);
			}
		}

	}


	public void reload() {
		setStaticMiniJobIdsToMiniJobs();
		setUpRandomMiniJobSelection();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private MiniJob convertRSRowToMiniJob(ResultSet rs, Random rand)
			throws SQLException {

		int id = rs.getInt(DBConstants.MINI_JOB__ID);
		int requiredStructId = rs
				.getInt(DBConstants.MINI_JOB__REQUIRED_STRUCT_ID);
		String miniJobName = rs.getString(DBConstants.MINI_JOB__NAME);
		int cashReward = rs.getInt(DBConstants.MINI_JOB__CASH_REWARD);
		int oilReward = rs.getInt(DBConstants.MINI_JOB__OIL_REWARD);
		int gemReward = rs.getInt(DBConstants.MINI_JOB__GEM_REWARD);
		int monsterIdReward = rs
				.getInt(DBConstants.MINI_JOB__MONSTER_ID_REWARD);
		int itemIdReward = rs.getInt(DBConstants.MINI_JOB__ITEM_ID_REWARD);
		int itemRewardQuantity = rs
				.getInt(DBConstants.MINI_JOB__ITEM_REWARD_QUANTITY);
		int secondItemIdReward = rs
				.getInt(DBConstants.MINI_JOB__SECOND_ITEM_ID_REWARD);
		int secondItemRewardQuantity = rs
				.getInt(DBConstants.MINI_JOB__SECOND_ITEM_REWARD_QUANTITY);
		String quality = rs.getString(DBConstants.MINI_JOB__QUALITY);
		int maxNumMonstersAllowed = rs
				.getInt(DBConstants.MINI_JOB__MAX_NUM_MONSTERS_ALLOWED);
		float chanceToAppear = rs
				.getFloat(DBConstants.MINI_JOB__CHANCE_TO_APPEAR);
		int hpRequired = rs.getInt(DBConstants.MINI_JOB__HP_REQUIRED);
		int atkRequired = rs.getInt(DBConstants.MINI_JOB__ATK_REQUIRED);
		int minDmgDealt = rs.getInt(DBConstants.MINI_JOB__MIN_DMG);
		int maxDmgDealt = rs.getInt(DBConstants.MINI_JOB__MAX_DMG);
		int durationMinMinutes = rs
				.getInt(DBConstants.MINI_JOB__DURATION_MIN_MINUTES);
		int durationMaxMinutes = rs
				.getInt(DBConstants.MINI_JOB__DURATION_MAX_MINUTES);
		int expReward = rs.getInt(DBConstants.MINI_JOB__EXP_REWARD);

		if (null != quality) {
			String newQuality = quality.trim().toUpperCase();
			if (!quality.equals(newQuality)) {
				log.error(String.format("quality incorrect: %s id=%s", quality,
						id));
				quality = newQuality;
			}
		}

		if (chanceToAppear < 0F) {
			log.error(String
					.format("incorrect chanceToAppear: %s. Forcing it to be above 0. id=%s",
							chanceToAppear, id));
			chanceToAppear = Math.max(0F, chanceToAppear);
		}

		MiniJob miniJob = new MiniJob(id, requiredStructId, miniJobName,
				cashReward, oilReward, gemReward, monsterIdReward,
				itemIdReward, itemRewardQuantity, secondItemIdReward,
				secondItemRewardQuantity, quality, maxNumMonstersAllowed,
				chanceToAppear, hpRequired, atkRequired, minDmgDealt,
				maxDmgDealt, durationMinMinutes, durationMaxMinutes, expReward);

		if (maxDmgDealt < minDmgDealt
				|| durationMaxMinutes < durationMinMinutes) {
			log.error(String.format("FUCKED UP (dmg, or time) MiniJob!!! %s",
					miniJob));
		}

		miniJob.setRand(rand);
		return miniJob;
	}

	protected static class MiniJobProxy implements Serializable {

		private static final long serialVersionUID = -3783653905582797762L;

		private int id;
		private float normalizedProbabilityPosition;

		public MiniJobProxy() {
			super();
		}

		public MiniJobProxy(int id, float normalizedProbabilityPosition) {
			super();
			this.id = id;
			this.normalizedProbabilityPosition = normalizedProbabilityPosition;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public float getNormalizedProbabilityPosition() {
			return normalizedProbabilityPosition;
		}

		public void setNormalizedProbabilityPosition(float normalizedProbabilityPosition) {
			this.normalizedProbabilityPosition = normalizedProbabilityPosition;
		}

		@Override
		public String toString() {
			return "MiniJobProxy [id=" + id + ", normalizedProbabilityPosition="
					+ normalizedProbabilityPosition + "]";
		}

	}
}
