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

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private static Random random = new Random();

	private static Map<Integer, PvpLeague> pvpLeagueIdsToPvpLeagues;
	//in case user's elo is less than lowest league then user is in lowest league
	private static PvpLeague lowestLeague;
	//in case user's elo is higher than highest league then user is in highest league
	private static PvpLeague highestLeague;
	

	private static final String TABLE_NAME = DBConstants.TABLE_PVP_LEAGUE;
	
	//CONTROLLER LOGIC******************************************************************
	
	/**
	 * @param elo 
	 * 		Finds leagues where its minElo is less than or equal to this elo and its
	 * 		maxElo is more than or equal to this elo. If elo < 0, elo will be set to 0.
	 * 		league.minElo <= elo <= league.maxElo
	 * 		However, if none exist then select highest or lowest league.
	 * @param selectRandom
	 * 	 	If true and multiple leagues exist for given elo, select randomly.
	 * @param pvpLeagueId
	 * 		If selectRandom is false, multiple leagues exist for given elo, and
	 * 		pvpLeagueId is one of the multiple leagues, return pvpLeagueId;
	 * @return
	 * 		If selectRandom is false, multiple leagues exist, pvpLeagueId is not one
	 * 		of the multiple leagues, then return league with lowest minElo;
	 */
	public static int getLeagueIdForElo(int elo, boolean selectRandom, int pvpLeagueId) {
		List<PvpLeague> leagues = getLeaguesForElo(elo);
		elo = Math.max(0, elo);
		
		if (leagues.isEmpty()) {
			log.error("no league for elo: " + elo + "\t selecting highest/lowest league");
			
			if (null != lowestLeague && elo < lowestLeague.getMinElo()) {
				log.error("selecting lowest league=" + lowestLeague);
				return lowestLeague.getId();
				
			} else if (null != highestLeague && elo > highestLeague.getMinElo()) {
				log.error("selecting highest league=" + highestLeague);
				return highestLeague.getId();
				
			} else {
				log.error("there appears to be a gap between leagues. elo=" + elo +
						"leagues=" + pvpLeagueIdsToPvpLeagues + "choosing lowest league");
				return lowestLeague.getId();
			}
		}
		
		int numLeagues = leagues.size();
		if (1 == numLeagues) {
			PvpLeague pl = leagues.get(0);
			return pl.getId();
		}
		
		//more than one league because leagues can have overlapping elo
		if (selectRandom) {
			int randIndex = random.nextInt(numLeagues);
			PvpLeague pl = leagues.get(randIndex); 
			log.info("asked to select random league: " + pl +
					"\t for elo:" + elo);
			return pl.getId();
		}
		
		for (PvpLeague pl : leagues) {
			if (pl.getId() == pvpLeagueId) {
				log.info("multiple leagues but still in current pvpLeagueId=" +
						pvpLeagueId);
				return pvpLeagueId;
			}
		}
		
		//getting league with the lowest elo
		Collections.sort(leagues, new PvpLeagueComparator());
		int firstIndex = 0;
		PvpLeague pl = leagues.get(firstIndex); 
		log.info("multiple leagues. selecting league-with-lowest-minElo: " +
				pl + "\t leagues=" + leagues + "\t for elo:" + elo);
		return pl.getId();
	}
	
	/**
	 * @param elo
	 * 		If elo < 0, then elo will be set to 0.
	 * @return
	 * 		List of PvpLeague objects that satisfy condition
	 * 		PvpLeague.getMinElo() <= elo <= PvpLeague.getMaxElo()
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
	 * @param elo
	 * 		If elo < 0, then elo will be set to 0.
	 * @param pvpLeagueId
	 * 		If this is not specified, the pvpLeagueId will be selected based on the elo.
	 * @return
	 * 		An integer, rank. The rank the elo is in a league is determined by a formula.
	 * 		rank = (1 - ((user_elo - min_elo)/(max_elo - min_elo))) * num_ranks
	 * 		Where num_ranks is the number of slots in a league.
	 */
	public static int getRankForElo(int elo, int pvpLeagueId) {
		log.info("getRankForElo(), elo=" + elo + "\t pvpLeagueId=" +
				pvpLeagueId);
		elo = Math.max(0, elo);
		
		if (pvpLeagueId <= 0) {
			pvpLeagueId = getLeagueIdForElo(elo, false, pvpLeagueId);
		}
		
		PvpLeague pl = getPvpLeagueForLeagueId(pvpLeagueId);
		if (null == pl) {
			int randRank = random.nextInt(100) + 1;
			log.info("choosing random rank!!!!!!" + randRank);
			return randRank;
		}
		//rank = (1 - ((user_elo - min_elo)/(max_elo - min_elo))) * num_ranks
		
		
		int minElo = pl.getMinElo();
		log.info("minElo=" + minElo);
		int maxElo = pl.getMaxElo();
		log.info("maxElo=" + maxElo);
		float userEloDelta = elo - minElo;
		log.info("userEloDelta=" + userEloDelta);
		float maxEloDelta = maxElo - minElo;
		log.info("maxEloDelta=" + maxEloDelta);
		
		int numRanks = pl.getNumRanks();
		
		//0 <= eloRatio <= 1
		float eloRatio = userEloDelta / maxEloDelta;
		log.info("eloRatio=" + eloRatio);
		
		float eloRatioComplement = 1F - eloRatio;
		log.info("eloRatioComplement=" + eloRatioComplement);
		
		int rank = Math.round(eloRatioComplement * numRanks);
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

	public static Map<Integer, PvpLeague> getPvpLeaguesForIds(Collection<Integer> ids) {
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
						Map<Integer, PvpLeague> pvpLeagueIdsToPvpLeaguesTemp =
								new HashMap<Integer, PvpLeague>();
						lowestLeague = null;
						highestLeague = null;
						//loop through each row and convert it into a java object
						while(rs.next()) {  
							PvpLeague pvpLeague = convertRSRowToPvpLeague(rs, random);

							int pvpLeagueId = pvpLeague.getId();
							pvpLeagueIdsToPvpLeaguesTemp.put(pvpLeagueId, pvpLeague);
							
							//setting the lowest and highest leagues
							if (null == lowestLeague) {
								lowestLeague = pvpLeague;
							}
							if (null == highestLeague) {
								highestLeague = pvpLeague;
								continue;
							}
							
							if (pvpLeague.getMinElo() < lowestLeague.getMinElo()) {
								lowestLeague = pvpLeague;
							}
							if (pvpLeague.getMinElo() > highestLeague.getMinElo()) {
								highestLeague = pvpLeague;
							}
							
						}

						pvpLeagueIdsToPvpLeagues = pvpLeagueIdsToPvpLeaguesTemp;
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
	private static PvpLeague convertRSRowToPvpLeague(ResultSet rs, Random rand) throws SQLException {
		int i = 1;
		int id = rs.getInt(i++);
		String leagueName = rs.getString(i++);
		String imgPrefix = rs.getString(i++);
		int numRanks = rs.getInt(i++);
		String description = rs.getString(i++);
		int minElo = rs.getInt(i++);
		int maxElo = rs.getInt(i++);
		if (rs.wasNull()) {
			log.warn("max elo not set. default elo=" + maxElo + " pvpLeagueId=" + id);
		}

		PvpLeague pvpLeague = new PvpLeague(id, leagueName,
				imgPrefix, numRanks, description, minElo, maxElo);
		return pvpLeague;
	}
	
	private static final class PvpLeagueComparator implements Comparator<PvpLeague> {
		  @Override
		  public int compare(PvpLeague o1, PvpLeague o2) {
			  if (o1.getMinElo() < o2.getMinElo()) {
				  return -1;
			  } else if (o1.getMinElo() > o2.getMinElo()) {
				  return 1;
			  } else {
				  return 0;
			  }
		  }
	  }
}
