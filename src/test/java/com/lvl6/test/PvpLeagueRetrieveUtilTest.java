package com.lvl6.test;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.info.PvpLeague;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class PvpLeagueRetrieveUtilTest extends TestCase {
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
	@Test
	public void testLeagueIdForElo() {
		log.info("testing computing league id for elo");
		
		int elo = getTestElo();
		boolean selectRandom = false;
		int curPvpLeagueId = getBronzeLeagueId();
		
		int calculatedLeagueId = PvpLeagueRetrieveUtils.getLeagueIdForElo(elo,
				selectRandom, curPvpLeagueId);
		
		assertTrue("ExpectedLeagueId: " + curPvpLeagueId + ". Actual=" +
				calculatedLeagueId, curPvpLeagueId == calculatedLeagueId);
		
	}
	
	@Test
	public void testLeaguesForElo() {
		log.info("testing computing leagues for elo");
		
		int elo = getTestElo();
		int expectedLeagues = getNumLeaguesForElo();
		List<PvpLeague> leagues = PvpLeagueRetrieveUtils.getLeaguesForElo(elo);
		
		assertTrue("Expected leagues: not null. Actual=" + leagues,
				null != leagues);
		
		int size = leagues.size();
		assertTrue("Expected size: 2. Actual=" + leagues, expectedLeagues == size);
		
		int bronzeLeagueId = getBronzeLeagueId();
		PvpLeague bronzeLeague = PvpLeagueRetrieveUtils
				.getPvpLeagueForLeagueId(bronzeLeagueId);
		assertTrue("Expected league: not null. Actual=" + bronzeLeague,
				null != bronzeLeague);
		
		PvpLeague calculatedPvpLeague = leagues.get(0);
		int calculatedId = calculatedPvpLeague.getId();
		assertTrue("Expected id: " + bronzeLeagueId + ". Actual=" +
				calculatedId, bronzeLeagueId == calculatedId);
		
	}
	
	@Test
	public void testRankForElo() {
		//rank = (1 - ((user_elo - min_elo)/(max_elo - min_elo))) * num_ranks
		log.info("testing computing rank for elo");
		
		int elo = getTestElo();
		int bronzeLeagueId = getBronzeLeagueId();
		int expectedRank = getTestRank();
		
		int computedRank = PvpLeagueRetrieveUtils.getRankForElo(elo, bronzeLeagueId);
		assertTrue("Expected rank: " + expectedRank + ". Actual=" +
				computedRank, expectedRank == computedRank);
		
		computedRank = PvpLeagueRetrieveUtils.getRankForElo(elo, 0);
		assertTrue("2Expected rank: " + expectedRank + ". Actual=" +
				computedRank, expectedRank == computedRank);
		
		
		PvpLeague bronze = PvpLeagueRetrieveUtils
				.getPvpLeagueForLeagueId(bronzeLeagueId);
		elo = bronze.getMinElo();
		expectedRank = bronze.getNumRanks();
		computedRank = PvpLeagueRetrieveUtils.getRankForElo(elo, bronzeLeagueId);
		assertTrue("3Expected rank: " + expectedRank + ". Actual=" +
				computedRank, expectedRank == computedRank);
		
	}
	
	protected int getTestElo() {
		return 50;
	}
	protected int getNumLeaguesForElo() {
		return 2;
	}
	protected int getBronzeLeagueId() {
		//atm this is what the db says
		return 1;
	}
	protected int getTestRank() {
		//this is what the Elo maps to using the formula
		//rank = (1 - ((user_elo - min_elo)/(max_elo - min_elo))) * num_ranks
		return 1;  
	}
}
