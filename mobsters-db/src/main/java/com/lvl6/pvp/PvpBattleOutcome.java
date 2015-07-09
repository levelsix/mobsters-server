package com.lvl6.pvp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleCountForUserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;

public class PvpBattleOutcome {
	private static Logger log = LoggerFactory.getLogger( PvpBattleOutcome.class);
	

	private static double SCORING_CURVE_BIAS = 0D;
	private static double SCORING_CURVE_LINEARITY = 0.15D;

	private static double ELO_SCALE_DIVIDEND_MULTIPLE = 0.05D;
	private static double RESOURCE_SCALE_DIVIDEND_MULTIPLE = 0.30D;//changed from 0.4

	private static double RESOURCE_GENERATOR_CONSTANT = 1.67; 
	//amount multiple stolen from generators relative to storage/status quo			
	
	private static double DECREASING_AMOUNT_PER_ATTACK = 0.67;
	private static int PVP_COUNT_PAST_DAY = 2;
	
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
	private List<PvpBattleCountForUserPojo> battleCount;

	public PvpBattleOutcome(User attacker, int attackerElo,
			User defender, int defenderElo,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils,
			List<PvpBattleCountForUserPojo> battleCount)
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
		this.battleCount = battleCount;
		
		setLoggingBoolean();
	}

	//derived data
	private boolean loggingOn;
	private NormalDistribution attackerWonCnd;

	private double meanForCnd;
	private double standardDeviationForCnd;
	private double lowerElo;
	private float percentageStealFromGenerator;

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
				* convertBackToDouble;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(
				-1 * matchRange)
				- offset;
		double scale = scaleDividend / scaleDivisor;
		if (loggingOn) {
			log.info(
					"cashAttackerWins() scaleDividend={}, scaleDivisor={}, scale={}",
					new Object[] { scaleDividend, scaleDivisor, scale });
		}
		
		int count = 0;
		//calculating the pvp battle count
		if(battleCount != null) {
			List<PvpBattleCountForUserPojo> pvpBattleCount = 
					new ArrayList<PvpBattleCountForUserPojo>();
			for(PvpBattleCountForUserPojo pbcfu : battleCount) {
				if(pbcfu.getDefenderId().equals(defenderId)) {
					pvpBattleCount.add(pbcfu);
				}
			}

			Date now = new Date();
			for(PvpBattleCountForUserPojo pbcfur : pvpBattleCount) {
				Date battleDate = new Date(pbcfur.getDate().getTime());
				Days days = Days.daysBetween((new DateTime(now)).toLocalDate(),
						(new DateTime(battleDate)).toLocalDate());

				if(days.getDays() < PVP_COUNT_PAST_DAY) {
					count = count + pbcfur.getCount();
				}
			}
		}

		double finalPercentageToSteal = (winnerLoserCndVal - offset) * scale;
		double retVal = finalPercentageToSteal * defenderCash;
		log.info("retVal before count: {}", retVal);
		log.info("count: {}", count);
		
		percentageStealFromGenerator = (float)(finalPercentageToSteal * RESOURCE_GENERATOR_CONSTANT);
		
		if(count != 0) {
			retVal = retVal * Math.pow(DECREASING_AMOUNT_PER_ATTACK, count); 
			percentageStealFromGenerator = percentageStealFromGenerator * 
					(float)Math.pow(DECREASING_AMOUNT_PER_ATTACK, count);
		}
		log.info("retVal after count: {}", retVal);
		log.info("PERCENTAGE TO STEAL FROM GENERATOR IS {}", percentageStealFromGenerator);
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
				* convertBackToDouble;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(
				-1 * matchRange)
				- offset;
		double scale = scaleDividend / scaleDivisor;

		if (loggingOn) {
			log.info(
					"oilAttackerWins() scaleDividend={}, scaleDivisor={}, scale={}",
					new Object[] { scaleDividend, scaleDivisor, scale });
		}

		//calculating the pvp battle count
		List<PvpBattleCountForUserPojo> pvpBattleCount = 
				new ArrayList<PvpBattleCountForUserPojo>();
		for(PvpBattleCountForUserPojo pbcfu : battleCount) {
			if(pbcfu.getDefenderId().equals(defenderId)) {
				pvpBattleCount.add(pbcfu);
			}
		}

		int count = 0;
		//calculating the pvp battle count
		if(battleCount != null) {
			List<PvpBattleCountForUserPojo> pvpBattleCount2 = 
					new ArrayList<PvpBattleCountForUserPojo>();
			for(PvpBattleCountForUserPojo pbcfu : battleCount) {
				if(pbcfu.getDefenderId().equals(defenderId)) {
					pvpBattleCount2.add(pbcfu);
				}
			}

			Date now = new Date();
			for(PvpBattleCountForUserPojo pbcfur : pvpBattleCount2) {
				Date battleDate = new Date(pbcfur.getDate().getTime());
				Days days = Days.daysBetween((new DateTime(now)).toLocalDate(),
						(new DateTime(battleDate)).toLocalDate());

				if(days.getDays() < PVP_COUNT_PAST_DAY) {
					count = count + pbcfur.getCount();
				}
			}
		}

		double percentage = (winnerLoserCndVal - offset) * scale;
		double retVal = percentage * defenderOil;
		
		if(count != 0) {
			retVal = retVal * Math.pow(DECREASING_AMOUNT_PER_ATTACK, count); 
		}
		
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

	public float getPercentageStealFromGenerator() {
		return percentageStealFromGenerator;
	}

	public void setPercentageStealFromGenerator(float percentageStealFromGenerator) {
		this.percentageStealFromGenerator = percentageStealFromGenerator;
	}


	
	

}
