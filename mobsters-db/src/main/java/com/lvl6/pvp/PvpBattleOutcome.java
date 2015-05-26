package com.lvl6.pvp;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;

public class PvpBattleOutcome {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	

	private static double SCORING_CURVE_BIAS = 0D;
	private static double SCORING_CURVE_LINEARITY = 0.15D;

	private static double ELO_SCALE_DIVIDEND_MULTIPLE = 0.05D;
	private static double RESOURCE_SCALE_DIVIDEND_MULTIPLE = 0.30D;//changed from 0.4

	//used in scale and offset calculation
	private static double OFFSET__VALID_MATCH_RANGE = 2D;

	public static final int CASH__MIN_REWARD = 100;
	public static final int OIL__MIN_REWARD = 100;

	private User attacker;
	private String attackerId;
	private double attackerElo;
	private User defender;
	private String defenderId;
	private double defenderElo;
	private double defenderCash;
	private double defenderOil;
	private ServerToggleRetrieveUtils serverToggleRetrieveUtils;

	public PvpBattleOutcome(User attacker, int attackerElo,
			User defender, int defenderElo,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils)
	{
		super();
		this.attacker = attacker;
		this.attackerId = attacker.getId();
		this.attackerElo = attackerElo;
		this.defender = defender;
		this.defenderId = defender.getId();
		this.defenderElo = defenderElo;
		this.defenderCash = defender.getCash();
		this.defenderOil = defender.getOil();
		this.serverToggleRetrieveUtils = serverToggleRetrieveUtils;

		setLoggingBoolean();
	}

	//derived data
	private boolean loggingOn;
	private NormalDistribution attackerWonCnd;

	private double meanForCnd;
	private double standardDeviationForCnd;
	private double lowerElo;

	private void setLoggingBoolean() {
		loggingOn = serverToggleRetrieveUtils
				.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__LOGGING_PVP_BATTLE_OUTCOME_DETAILS);
	}

	public NormalDistribution getAttackerWonCnd() {
		if (null == attackerWonCnd) {
			lowerElo = Math.min(attackerElo, defenderElo);
			if (SCORING_CURVE_BIAS <= 0D) {
				meanForCnd = 0;

			} else {
				meanForCnd = SCORING_CURVE_BIAS * OFFSET__VALID_MATCH_RANGE
						* lowerElo;
			}

			standardDeviationForCnd = SCORING_CURVE_LINEARITY
					* OFFSET__VALID_MATCH_RANGE * lowerElo;

			attackerWonCnd = new NormalDistribution(meanForCnd,
					standardDeviationForCnd);

		}

		if (loggingOn) {
			log.info("meanForCnd={}, standardDeviation={}", meanForCnd,
					standardDeviationForCnd);
		}

		return attackerWonCnd;
	}

	//TODO: Get rid of repeated logic

	//ELO SECTION
	/*
	Lower Score = min(winner score, loser score)

	Match Score = ( CND( Winner's Score - Loser's Score, Mean, Standard Deviation ) - Offset ) x Scale
	Mean = Lower Score x [Scoring Curve Bias]
			Recommended Scoring Curve Bias: 0.0%
	Standard Deviation = Lower Score x [Scoring Curve Linearity]
			Recommended Scoring Curve Linearity: 15.0%
	Match Range = Lower Score x [Valid Match Range]
			Recommended Valid Match Range: 200.0%
	Scale = [Max Match Delta] / ( CND( -Match Range, Mean, Standard Deviation) - Offset )
			Recommended Max Match Delta: 5.0% * Lower Score
	Offset = CND( Match Range, Mean, Standard Deviation)
	*/
	public int getUnsignedEloAttackerWins() {
		if (loggingOn) {
			log.info("getEloAttackerWins() attackerId={}, defenderId={}",
					attackerId, defenderId);
		}

		double eloDiff = attackerElo - defenderElo;

		if (loggingOn) {
			log.info(
					"getEloAttackerWins() attackerElo={}, defenderElo={}, eloDiff={}",
					new Object[] { attackerElo, defenderElo, eloDiff });
		}

		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(
				eloDiff);
		if (loggingOn) {
			log.info("getEloAttackerWins() winnerLoserCndVal={}",
					winnerLoserCndVal);
		}

		double matchRange = lowerElo * OFFSET__VALID_MATCH_RANGE;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		if (loggingOn) {
			log.info(
					"getEloAttackerWins() matchRange (lowerElo * ELO_VALID_MATCH_RANGE)={}, offset={}",
					matchRange, offset);
		}

		double scaleDividend = ELO_SCALE_DIVIDEND_MULTIPLE * lowerElo;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(
				-1 * matchRange)
				- offset;
		double scale = scaleDividend / scaleDivisor;
		if (loggingOn) {
			log.info(
					"getEloAttackerWins() scaleDividend={}, scaleDivisor={}, scale={}",
					new Object[] { scaleDividend, scaleDivisor, scale });
		}

		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal);
		if (loggingOn) {
			log.info(
					"getEloAttackerWins() (winnerLoserCndVal - offset)={}, retVal={}, intRetVal={}",
					new Object[] { (winnerLoserCndVal - offset), retVal,
							intRetVal });
		}

		return intRetVal;
	}

	public int getUnsignedEloAttackerLoses() {
		if (loggingOn) {
			log.info("getEloAttackerLoses() attackerId={}, defenderId={}",
					attackerId, defenderId);
		}

		double eloDiff = defenderElo - attackerElo;
		if (loggingOn) {
			log.info(
					"getEloAttackerLoses() attackerElo={},  defenderElo={}, eloDiff={}",
					new Object[] { attackerElo, defenderElo, eloDiff });
		}

		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(
				eloDiff);
		if (loggingOn) {
			log.info("getEloAttackerLoses() winnerLoserCndVal={}",
					winnerLoserCndVal);
		}

		double matchRange = OFFSET__VALID_MATCH_RANGE * lowerElo;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		if (loggingOn) {
			log.info(
					"getEloAttackerLoses() matchRange (lowerElo * OFFSET__VALID_MATCH_RANGE)={}, offset={}",
					matchRange, offset);
		}

		double scaleDividend = ELO_SCALE_DIVIDEND_MULTIPLE * lowerElo;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(
				-1 * matchRange)
				- offset;
		double scale = scaleDividend / scaleDivisor;
		if (loggingOn) {
			log.info(
					"getEloAttackerLoses() scaleDividend={}, scaleDivisor={}, scale={}",
					new Object[] { scaleDividend, scaleDivisor, scale });
		}

		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal);
		if (loggingOn) {
			log.info(
					"getEloAttackerLoses() (winnerLoserCndVal - offset)={}, retVal={}, intRetVal={}",
					new Object[] { (winnerLoserCndVal - offset), retVal,
							intRetVal });
		}

		return intRetVal;
	}

	//RESOURCE SECTION
	public int getUnsignedCashAttackerWins() {

		if (loggingOn) {
			log.info("attackerId={}, defenderId={}", attackerId, defenderId);
		}

		double eloDiff = attackerElo - defenderElo;
		if (loggingOn) {
			log.info(
					"cashAttackerWins() attackerElo={}, defenderElo={}, eloDiff={}",
					new Object[] { attackerElo, defenderElo, eloDiff });
		}

		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(
				eloDiff);
		if (loggingOn) {
			log.info("cashAttackerWins() winnerLoserCndVal={}",
					winnerLoserCndVal);
		}

		double matchRange = OFFSET__VALID_MATCH_RANGE * lowerElo;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		if (loggingOn) {
			log.info(
					"cashAttackerWins() matchRange (defenderCash)={}, offset={}",
					matchRange, offset);
		}

		//arin's constant to factor in player lvl
		int diffInPlayerLvl = attacker.getLevel() - defender.getLevel();
		double convertBackToDouble = 0.01;
		double scaleDividend = Math
				.abs((Math.abs(diffInPlayerLvl
						* RESOURCE_SCALE_DIVIDEND_MULTIPLE) - RESOURCE_SCALE_DIVIDEND_MULTIPLE * 100))
				* convertBackToDouble * defenderCash;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(
				-1 * matchRange)
				- offset;
		double scale = scaleDividend / scaleDivisor;
		if (loggingOn) {
			log.info(
					"cashAttackerWins() scaleDividend={}, scaleDivisor={}, scale={}",
					new Object[] { scaleDividend, scaleDivisor, scale });
		}

		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal);
		intRetVal += CASH__MIN_REWARD;

		if (loggingOn) {
			log.info(
					"cashAttackerWins() (winnerLoserCndVal - offset)={}, retVal={}, intRetVal={}, min={}",
					new Object[] { (winnerLoserCndVal - offset), retVal,
							intRetVal, CASH__MIN_REWARD });
		}

		return intRetVal;
	}

	//TODO: Consider storing some of these values into some private variables
	public int getUnsignedOilAttackerWins() {

		if (loggingOn) {
			log.info("attackerId={}, defenderId={}", attackerId, defenderId);
		}

		double eloDiff = attackerElo - defenderElo;
		if (loggingOn) {
			log.info(
					"oilAttackerWins() attackerElo={}, defenderElo={}, eloDiff={}",
					new Object[] { attackerElo, defenderElo, eloDiff });
		}

		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(
				eloDiff);
		if (loggingOn) {
			log.info("oilAttackerWins() winnerLoserCndVal={}",
					winnerLoserCndVal);
		}

		//double matchRange = defenderOil;
		double matchRange = OFFSET__VALID_MATCH_RANGE * lowerElo;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		if (loggingOn) {
			log.info(
					"oilAttackerWins() matchRange (defenderOil)={}, offset={}",
					matchRange, offset);
		}

		//		double scaleDividend = RESOURCE_SCALE_DIVIDEND_MULTIPLE * lowerElo;
		int diffInPlayerLvl = attacker.getLevel() - defender.getLevel();
		double convertBackToDouble = 0.01;
		double scaleDividend = Math
				.abs((Math.abs(diffInPlayerLvl
						* RESOURCE_SCALE_DIVIDEND_MULTIPLE) - RESOURCE_SCALE_DIVIDEND_MULTIPLE * 100))
				* convertBackToDouble * defenderOil;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(
				-1 * matchRange)
				- offset;
		double scale = scaleDividend / scaleDivisor;
		if (loggingOn) {
			log.info(
					"oilAttackerWins() scaleDividend={}, scaleDivisor={}, scale={}",
					new Object[] { scaleDividend, scaleDivisor, scale });
		}

		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal);
		intRetVal += OIL__MIN_REWARD;

		if (loggingOn) {
			log.info(
					"oilAttackerWins() (winnerLoserCndVal - offset)={}, retVal={}, intRetVal={}, min={}",
					new Object[] { (winnerLoserCndVal - offset), retVal,
							intRetVal, OIL__MIN_REWARD });
		}

		return intRetVal;
	}

	@Override
	public String toString() {
		return "PvpBattleOutcome [attackerId=" + attackerId + ", attackerElo="
				+ attackerElo + ", defenderId=" + defenderId + ", defenderElo="
				+ defenderElo + ", defenderCash=" + defenderCash
				+ ", defenderOil=" + defenderOil + ", meanForCnd=" + meanForCnd
				+ ", standardDeviationForCnd=" + standardDeviationForCnd
				+ ", lowerElo=" + lowerElo + "]";
	}

}
