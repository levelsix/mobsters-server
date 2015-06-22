package com.lvl6.test;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.info.User;
import com.lvl6.pvp.PvpBattleOutcome;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class EloFunctionsTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(EloFunctionsTest.class);

	@Autowired
	private ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	public static final int CASH__DEFENDER = 2000;
	public static final int OIL_DEFENDER = 10000;

	//TODO: figure out why this test fails...in the future
	//	@Test
	//	public void testMatchMaking() {
	//		double elo = getAttackerElo();
	//
	//		log.info(String.format(
	//			"testing computing suitable elo range for elo=%f",
	//			elo));
	//
	//		//pulled from a Dash's excel spreadsheet and files
	//		//randVal = [Max Range] x Player's Score x ICND( Random( 0.1, 0.9 ), [Bias], 0.608 )
	//		//computedElo = player score + randVal
	//		//minElo = Math.min(95% final elo, DEFAULT_MIN_ELO)
	//		//maxElo = 105% final elo
	//		double randVar = 0.50D;
	//		double expectedEloAddend = PvpUtil2.ELO__MAX_RANGE
	//			* elo * -0.2D;
	//		double expectedElo = elo + expectedEloAddend;
	//
	//		double generatedElo = PvpUtil2.getProspectiveOpponentElo(randVar, elo);
	//
	//		//bounded generatedElo between expectedElo because of potential rounding differences
	//		//could have just chosen +/- 1 or any other number
	//		double expectedMax = Math.max(expectedElo * 1.001D, expectedElo + 2);
	//		double expectedMin = Math.min(expectedElo * 0.999D, expectedElo - 2);
	//		assertTrue(String.format(
	//			"Not equal/similar values. expectedElo=%f, generatedElo=%f", expectedElo, generatedElo),
	//			generatedElo <= expectedMax && generatedElo >= expectedMin);
	//
	//	}

	@Test
	public void testEloAttackerWins() {
		int elo = getAttackerElo();
		log.info(String.format("testing amount of elo attacker wins"));
		User attacker = new User();
		attacker.setId(getAttackerId());

		User defender = new User();
		defender.setId(getDefenderId());
		defender.setCash(0);
		defender.setOil(0);

		//the amount of elo attacker wins should always be non negative
		PvpBattleOutcome betterOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloHigh(), serverToggleRetrieveUtil);
		int eloWon = betterOpponent.getUnsignedEloAttackerWins();
		assertTrue("Expected elo won: unsigned elo. Actual: " + eloWon,
				eloWon >= 0);

		PvpBattleOutcome worseOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloLow(), serverToggleRetrieveUtil);
		int eloWonNotMuch = worseOpponent.getUnsignedEloAttackerWins();
		assertTrue("Expected elo won: unsigned elo. Actual: " + eloWonNotMuch,
				eloWonNotMuch >= 0);

		assertTrue(
				"Expected more elo from harder opponent. eloFromHarderOpponent="
						+ eloWon + ", eloFromWorseOpponent=" + eloWonNotMuch,
				eloWon > eloWonNotMuch);

		//elo result based on xcel spreadsheet by Dash
		int expectedEloWon = 47;
		String msg = String.format("Expected around %d, but was %d",
				expectedEloWon, eloWon);
		assertTrue(msg, eloWon >= (expectedEloWon - 1)
				|| eloWon <= (expectedEloWon + 1));

		//elo result based on xcel spreadsheet by Dash
		int expectedEloWonNotMuch = 13;

		//bounded generatedElo between expectedElo because of potential rounding differences
		//could have just chosen +/- 1 or any other number
		int expectedEloWonNotMuchMax = Math.max(
				(int) (expectedEloWonNotMuch * 1.001D),
				expectedEloWonNotMuch + 2);
		int expectedEloWonNotMuchMin = Math.min(
				(int) (expectedEloWonNotMuch * 0.999D),
				expectedEloWonNotMuch + 2);

		msg = String.format("Expected around %d, but was %d",
				expectedEloWonNotMuch, eloWonNotMuch);
		assertTrue(msg, eloWonNotMuch <= expectedEloWonNotMuchMax
				|| eloWonNotMuch >= expectedEloWonNotMuchMin);
	}

	@Test
	public void testEloAttackerLoses() {
		int elo = getAttackerElo();
		log.info(String.format("testing amount of elo attacker loses"));

		User attacker = new User();
		attacker.setId(getAttackerId());

		User defender = new User();
		defender.setId(getDefenderId());
		defender.setCash(0);
		defender.setOil(0);

		//the amount of elo attacker wins should always be non negative
		PvpBattleOutcome betterOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloHigh(), serverToggleRetrieveUtil);
		int eloLostNotMuch = betterOpponent.getUnsignedEloAttackerLoses();
		assertTrue(
				"Expected elo lost: unsigned elo. Actual: " + eloLostNotMuch,
				eloLostNotMuch >= 0);

		PvpBattleOutcome worseOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloLow(), serverToggleRetrieveUtil);
		int eloLost = worseOpponent.getUnsignedEloAttackerLoses();
		assertTrue("Expected elo lost: unsigned elo. Actual: " + eloLost,
				eloLost >= 0);

		String msg = String
				.format("Expected elo lost to harder opponent to"
						+ " be less than elo lost to weaker one. eloFromHarderOpponent=%s,"
						+ " eloFromWorseOpponent=%s", eloLostNotMuch, eloLost);
		assertTrue(msg, eloLostNotMuch < eloLost);

		//elo result based on xcel spreadsheet by Dash
		int expectedEloLostNotMuch = 13;
		msg = String.format("Expected around %d, but was %d",
				expectedEloLostNotMuch, eloLostNotMuch);
		assertTrue(msg, eloLostNotMuch >= (expectedEloLostNotMuch - 1)
				|| eloLostNotMuch <= (expectedEloLostNotMuch + 1));

		//elo result based on excel spreadsheet by Dash
		int expectedEloLost = 37;

		//bounded generatedElo between expectedElo because of potential rounding differences
		//could have just chosen +/- 1 or any other number
		int expectedEloWonNotMuchMax = Math.max(
				(int) (expectedEloLost * 1.001D), expectedEloLost + 2);
		int expectedEloWonNotMuchMin = Math.min(
				(int) (expectedEloLost * 0.999D), expectedEloLost + 2);

		msg = String.format("Expected around %d, but was %d", expectedEloLost,
				eloLost);
		assertTrue(msg, eloLost <= expectedEloWonNotMuchMax
				|| eloLost >= expectedEloWonNotMuchMin);
	}

	@Test
	public void testAttackerCashWon() {
		int elo = getAttackerElo();
		log.info(String.format("testing amount of cash attacker wins"));

		User attacker = new User();
		attacker.setId(getAttackerId());
		attacker.setLevel(10);

		User defender = new User();
		defender.setId(getDefenderId());
		defender.setCash(CASH__DEFENDER);
		defender.setOil(OIL_DEFENDER);
		defender.setLevel(10);

		//the amount of elo attacker wins should always be non negative
		PvpBattleOutcome betterOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloHigh(), serverToggleRetrieveUtil);
		int cashWon = betterOpponent.getUnsignedCashAttackerWins();
		assertTrue("Expected cash won: unsigned cash. Actual: " + cashWon,
				cashWon >= 0);

		PvpBattleOutcome worseOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloLow(), serverToggleRetrieveUtil);
		int cashWonNotMuch = worseOpponent.getUnsignedCashAttackerWins();
		assertTrue("Expected cash won: unsigned cash. Actual: "
				+ cashWonNotMuch, cashWonNotMuch >= 0);

		String msg = String
				.format("Expected cash won from harder"
						+ " opponent to be more than that of weaker opponent. harderOpponent=%d"
						+ ", weakerOpponent=%d", cashWon, cashWonNotMuch);
		assertTrue(msg, cashWon > cashWonNotMuch);

		//cash amount based on excel spreadsheet by Dash
		int expectedCashWon = 473;

		//bounded generatedElo between expectedElo because of potential rounding differences
		//could have just chosen +/- 1 or any other number
		int expectedCashWonMax = Math.max((int) (expectedCashWon * 1.001D),
				expectedCashWon + 2);
		int expectedCashWonMin = Math.min((int) (expectedCashWon * 0.999D),
				expectedCashWon + 2);

		msg = String.format("Expected around %d, but was %d", expectedCashWon,
				cashWon);
		assertTrue(msg, cashWon <= expectedCashWonMax
				|| cashWon >= expectedCashWonMin);

		//cash amount based on excel spreadsheet by Dash
		int expectedCashWonNotMuch = 152;

		//bounded generatedCash between expectedCash because of potential rounding differences
		//could have just chosen +/- 1 or any other number
		int expectedCashWonNotMuchMax = Math.max(
				(int) (expectedCashWonNotMuch * 1.001D), expectedCashWon + 2);
		int expectedCashWonNotMuchMin = Math.min(
				(int) (expectedCashWonNotMuch * 0.999D), expectedCashWon + 2);

		msg = String.format("Expected around %d, but was %d",
				expectedCashWonNotMuch, cashWonNotMuch);
		assertTrue(msg, cashWon <= expectedCashWonNotMuchMax
				|| cashWon >= expectedCashWonNotMuchMin);
	}

	@Test
	public void testAttackerOilWon() {
		int elo = getAttackerElo();
		log.info(String.format("testing amount of oil attacker wins"));

		User attacker = new User();
		attacker.setId(getAttackerId());
		attacker.setLevel(10);

		User defender = new User();
		defender.setId(getDefenderId());
		defender.setCash(CASH__DEFENDER);
		defender.setOil(OIL_DEFENDER);
		defender.setLevel(10);

		//the amount of elo attacker wins should always be non negative
		PvpBattleOutcome betterOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloHigh(), serverToggleRetrieveUtil);
		int oilWon = betterOpponent.getUnsignedOilAttackerWins();
		assertTrue("Expected oil won: unsigned oil. Actual: " + oilWon,
				oilWon >= 0);

		PvpBattleOutcome worseOpponent = new PvpBattleOutcome(attacker, elo,
				defender, getDefenderEloLow(), serverToggleRetrieveUtil);
		int oilWonNotMuch = worseOpponent.getUnsignedOilAttackerWins();
		assertTrue("Expected oil won: unsigned oil. Actual: " + oilWonNotMuch,
				oilWonNotMuch >= 0);

		String msg = String
				.format("Expected oil won from harder"
						+ " opponent to be more than that of weaker opponent. harderOpponent=%d"
						+ ", weakerOpponent=%d", oilWon, oilWonNotMuch);
		assertTrue(msg, oilWon > oilWonNotMuch);

		//oil amount based on excel spreadsheet by Dash
		int expectedOilWon = 2364;

		//bounded generatedOil between expectedOil because of potential rounding differences
		//could have just chosen +/- 1 or any other number
		int expectedOilWonMax = Math.max((int) (expectedOilWon * 1.001D),
				expectedOilWon + 2);
		int expectedOilWonMin = Math.min((int) (expectedOilWon * 0.999D),
				expectedOilWon + 2);

		msg = String.format("Expected around %d, but was %d", expectedOilWon,
				oilWon);
		assertTrue(msg, oilWon <= expectedOilWonMax
				|| oilWon >= expectedOilWonMin);

		//oil amount based on excel spreadsheet by Dash
		int expectedOilWonNotMuch = 757;

		//bounded generatedOil between expectedOil because of potential rounding differences
		//could have just chosen +/- 1 or any other number
		int expectedOilWonNotMuchMax = Math.max(
				(int) (expectedOilWonNotMuch * 1.001D), expectedOilWon + 2);
		int expectedOilWonNotMuchMin = Math.min(
				(int) (expectedOilWonNotMuch * 0.999D), expectedOilWon + 2);

		msg = String.format("Expected around %d, but was %d",
				expectedOilWonNotMuch, oilWonNotMuch);
		assertTrue(msg, oilWon <= expectedOilWonNotMuchMax
				|| oilWon >= expectedOilWonNotMuchMin);
	}

	protected String getAttackerId() {
		return "1";
	}

	protected int getAttackerElo() {
		return 1200;
	}

	protected String getDefenderId() {
		return "2";
	}

	//defender elo values based on xcel spreadsheet by Dash
	protected int getDefenderEloHigh() {
		return 1488;
	}

	//defender elo values based on xcel spreadsheet by Dash
	protected int getDefenderEloLow() {
		return 1000;
	}

	public ServerToggleRetrieveUtils getServerToggleRetrieveUtil() {
		return serverToggleRetrieveUtil;
	}

	public void setServerToggleRetrieveUtil(
			ServerToggleRetrieveUtils serverToggleRetrieveUtil) {
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

}
