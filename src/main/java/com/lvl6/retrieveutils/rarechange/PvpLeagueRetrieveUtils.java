package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.PvpLeague;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
public class PvpLeagueRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Random random = new Random();

	private static Map<Integer, PvpLeague> pvpLeagueIdsToPvpLeagues;
	//in case user's elo is less than lowest league then user is in lowest league
	private static PvpLeague lowestLeague;
	//in case user's elo is higher than highest league then user is in highest league
	private static PvpLeague highestLeague;

	private static final PvpLeagueComparator comparator = new PvpLeagueComparator();

	private static final String TABLE_NAME = DBConstants.TABLE_PVP_LEAGUE_CONFIG;

	//CONTROLLER LOGIC******************************************************************
	public static int getLeagueIdForElo(int elo, int curPvpLeagueId) {
		PvpLeague cur = null;
		if (pvpLeagueIdsToPvpLeagues.containsKey(curPvpLeagueId)) {
			cur = pvpLeagueIdsToPvpLeagues.get(curPvpLeagueId);
		}

		//calculate default return value if elo is too low or too high
		int resultId = 0;
		if (null != lowestLeague && elo < lowestLeague.getMinElo()) {
			log.error(String.format("selecting lowest league=%s", lowestLeague));
			resultId = lowestLeague.getId();

		} else if (null != highestLeague && elo > highestLeague.getMinElo()) {
			log.error(String.format("selecting highest league=%s",
					highestLeague));
			resultId = highestLeague.getId();
		}

		//cur PvpLeague can still be null
		//selecting lowest PvpLeague for the elo provided
		if (null == cur) {
			cur = getMinLeagueForElo(elo); //cur is not null now
			log.warn(String
					.format("no current PvpLeague provided, elo=%s, curPvpLeagueId=%s, will use %s",
							elo, curPvpLeagueId, cur));
			resultId = cur.getId();
		}

		int iterations = pvpLeagueIdsToPvpLeagues.size();
		PvpLeague pvpLeagueAtm = cur;
		while (iterations > 0 && null != pvpLeagueAtm) {
			int minElo = pvpLeagueAtm.getMinElo();
			int maxElo = pvpLeagueAtm.getMaxElo();

			if (minElo <= elo && elo <= maxElo) {
				//minElo <= elo <= maxElo
				resultId = pvpLeagueAtm.getId();
				break;
			} else if (elo < minElo) {
				pvpLeagueAtm = pvpLeagueIdsToPvpLeagues.get(pvpLeagueAtm
						.getPredecessorLeagueId());
			} else if (elo > maxElo) {
				pvpLeagueAtm = pvpLeagueIdsToPvpLeagues.get(pvpLeagueAtm
						.getSuccessorLeagueId());
			}
			//pvpLeagueAtm could be null
			iterations--;
		}

		if (resultId <= 0) {
			log.error(String
					.format("couldn't figure out pvpLeagueId. elo=%s, leagues=%s, choosing lowest league=%s",
							elo, pvpLeagueIdsToPvpLeagues, lowestLeague));
			resultId = lowestLeague.getId();
		}

		return resultId;
	}

	public static PvpLeague getMinLeagueForElo(int elo) {
		List<PvpLeague> leagues = getLeaguesForElo(elo);

		if (leagues.isEmpty()) {
			return lowestLeague;
		} else if (leagues.size() == 1) {
			return leagues.get(0);
		}

		Collections.sort(leagues, comparator);
		return leagues.get(0);

	}

	/**
	 * @param elo If elo < 0, then elo will be set to 0.
	 * @return List of PvpLeague objects that satisfy condition
	 *         PvpLeague.getMinElo() <= elo <= PvpLeague.getMaxElo()
	 */
	public static List<PvpLeague> getLeaguesForElo(int elo) {
		elo = Math.max(0, elo);

		List<PvpLeague> leagues = new ArrayList<PvpLeague>();

		for (PvpLeague pl : pvpLeagueIdsToPvpLeagues.values()) {
			int minElo = pl.getMinElo();
			int maxElo = pl.getMaxElo();

			if (minElo <= elo && elo <= maxElo) {
				//minElo <= elo <= maxElo
				leagues.add(pl);
			}
		}

		log.info("pvp leagues for elo=" + elo + "\t leagues=" + leagues);
		return leagues;
	}

	/**
	 * @param elo If elo < 0, then elo will be set to 0.
	 * @param pvpLeagueId If this is not specified, the pvpLeagueId will be
	 *            selected based on the elo.
	 * @return An integer, rank. The rank the elo is in a league is determined
	 *         by a formula. rank = (1 - ((user_elo - min_elo)/(max_elo -
	 *         min_elo))) * num_ranks Where num_ranks is the number of slots in
	 *         a league.
	 */
	public static int getRankForElo(int elo, int pvpLeagueId) {
		log.info("getRankForElo(), elo=" + elo + "\t pvpLeagueId="
				+ pvpLeagueId);
		elo = Math.max(0, elo);

		if (pvpLeagueId <= 0) {
			pvpLeagueId = getLeagueIdForElo(elo, pvpLeagueId);
		}

		PvpLeague pl = getPvpLeagueForLeagueId(pvpLeagueId);
		if (null == pl) {
			int randRank = random.nextInt(100) + 1;
			log.info("choosing random rank!!!!!!" + randRank);
			return randRank;
		}

		//rank = (1 - ((user_elo - min_elo)/(max_elo - min_elo))) * 
		//       (max_rank - min_rank) + min_rank
		//     = (1 - eloRatio) * (rank_difference) + min_rank

		//calculate eloRatio
		int minElo = pl.getMinElo();
		log.info("minElo=" + minElo);
		int maxElo = pl.getMaxElo();
		log.info("maxElo=" + maxElo);
		float userEloDelta = elo - minElo;
		log.info("userEloDelta=" + userEloDelta);
		float maxEloDelta = maxElo - minElo;
		log.info("maxEloDelta=" + maxEloDelta);

		//0 <= eloRatio <= 1
		float eloRatio = userEloDelta / maxEloDelta;
		log.info("eloRatio=" + eloRatio);
		float eloRatioComplement = 1F - eloRatio;
		log.info("eloRatioComplement=" + eloRatioComplement);

		//calculate rank_difference
		int numRanks = pl.getMaxRank() - pl.getMinRank();

		//calculate actual rank
		int rank = Math.round(eloRatioComplement * numRanks) + pl.getMinRank();
		log.info("rounded rank=" + rank);

		//atm, situation is 0 <= rank <= numRanks, but forcing 0 < rank <= numRanks
		if (rank <= 0) {
			log.info("calculated rank <= 0, setting as 1. rank=" + rank);
			rank = 1;
		}
		return rank;
	}

	//RETRIEVE QUERIES*********************************************************************
	public static Map<Integer, PvpLeague> getPvpLeagueIdsToPvpLeagues() {
		if (null == pvpLeagueIdsToPvpLeagues) {
			setStaticPvpLeagueIdsToPvpLeagues();
		}
		return pvpLeagueIdsToPvpLeagues;
	}

	public static PvpLeague getPvpLeagueForLeagueId(int pvpLeagueId) {
		if (null == pvpLeagueIdsToPvpLeagues) {
			setStaticPvpLeagueIdsToPvpLeagues();
		}

		if (!pvpLeagueIdsToPvpLeagues.containsKey(pvpLeagueId)) {
			log.error("no pvp league with id=" + pvpLeagueId);
			return null;
		}

		PvpLeague pl = pvpLeagueIdsToPvpLeagues.get(pvpLeagueId);
		return pl;
	}

	public static Map<Integer, PvpLeague> getPvpLeaguesForIds(
			Collection<Integer> ids) {
		if (null == pvpLeagueIdsToPvpLeagues) {
			setStaticPvpLeagueIdsToPvpLeagues();
		}
		Map<Integer, PvpLeague> returnMap = new HashMap<Integer, PvpLeague>();

		for (int id : ids) {
			PvpLeague tsm = getPvpLeagueForLeagueId(id);

			if (null != tsm) {
				returnMap.put(id, tsm);
			}
		}
		return returnMap;
	}

	private static void setStaticPvpLeagueIdsToPvpLeagues() {
		log.debug("setting static map of pvpLeague ids to pvpLeagues");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, PvpLeague> pvpLeagueIdsToPvpLeaguesTemp = new HashMap<Integer, PvpLeague>();
						PvpLeague lowestLeagueTemp = null;
						PvpLeague highestLeagueTemp = null;
						//loop through each row and convert it into a java object
						while (rs.next()) {
							PvpLeague pvpLeague = convertRSRowToPvpLeague(rs,
									random);

							int pvpLeagueId = pvpLeague.getId();
							pvpLeagueIdsToPvpLeaguesTemp.put(pvpLeagueId,
									pvpLeague);

							//setting the lowest and highest leagues
							if (null == lowestLeagueTemp) {
								lowestLeagueTemp = pvpLeague;
							}
							if (null == highestLeagueTemp) {
								highestLeagueTemp = pvpLeague;
								continue;
							}

							if (pvpLeague.getMinElo() < lowestLeagueTemp
									.getMinElo()) {
								lowestLeagueTemp = pvpLeague;
							}
							if (pvpLeague.getMinElo() > highestLeagueTemp
									.getMinElo()) {
								highestLeagueTemp = pvpLeague;
							}

						}

						pvpLeagueIdsToPvpLeagues = pvpLeagueIdsToPvpLeaguesTemp;
						lowestLeague = lowestLeagueTemp;
						highestLeague = highestLeagueTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("pvpLeague retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticPvpLeagueIdsToPvpLeagues();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static PvpLeague convertRSRowToPvpLeague(ResultSet rs, Random rand)
			throws SQLException {
		int id = rs.getInt(DBConstants.PVP_LEAGUE__ID);
		String leagueName = rs.getString(DBConstants.PVP_LEAGUE__LEAGUE_NAME);
		String imgPrefix = rs.getString(DBConstants.PVP_LEAGUE__IMG_PREFIX);
		String description = rs.getString(DBConstants.PVP_LEAGUE__DESCRIPTION);
		int minElo = rs.getInt(DBConstants.PVP_LEAGUE__MIN_ELO);
		int maxElo = rs.getInt(DBConstants.PVP_LEAGUE__MAX_ELO);
		if (rs.wasNull()) {
			log.warn("max elo not set. default elo=" + maxElo + " pvpLeagueId="
					+ id);
		}

		int minRank = rs.getInt(DBConstants.PVP_LEAGUE__MIN_RANK);
		int maxRank = rs.getInt(DBConstants.PVP_LEAGUE__MAX_RANK);
		int predecessorLeagueId = rs
				.getInt(DBConstants.PVP_LEAGUE__PREDECESSOR_ID);
		int successorLeagueId = rs.getInt(DBConstants.PVP_LEAGUE__SUCCESSOR_ID);

		PvpLeague pvpLeague = new PvpLeague(id, leagueName, imgPrefix,
				description, minElo, maxElo, minRank, maxRank,
				predecessorLeagueId, successorLeagueId);
		return pvpLeague;
	}

	private static final class PvpLeagueComparator implements
			Comparator<PvpLeague> {
		@Override
		public int compare(PvpLeague o1, PvpLeague o2) {
			if (o1.getMinElo() < o2.getMinElo()) {
				return -1;
			} else if (o1.getMinElo() > o2.getMinElo()) {
				return 1;
			} else if (o1.getMaxElo() < o2.getMaxElo()) {
				return -1;
			} else if (o1.getMaxElo() > o2.getMaxElo()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
