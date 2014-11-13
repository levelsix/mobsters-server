package com.lvl6.pvp;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpBattleOutcome
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	
	private static double SCORING_CURVE_BIAS = 0D;
	private static double SCORING_CURVE_LINEARITY = 0.15D;
	
	private static double ELO_SCALE_DIVIDEND_MULTIPLE = 0.05D;
	private static double RESOURCE_SCALE_DIVIDEND_MULTIPLE = 0.30D;
	
	//used in scale and offset calculation
	private static double OFFSET__VALID_MATCH_RANGE = 2D;
	
	private String attackerId;
	private double attackerElo;
	
	private String defenderId;
	private double defenderElo;
	private double defenderCash;
	private double defenderOil;
	
	private NormalDistribution attackerWonCnd;
	
	private double meanForCnd;
	private double standardDeviationForCnd;
	private double lowerElo;
	
	public PvpBattleOutcome(
		String attackerId,
		int attackerElo,
		String defenderId,
		int defenderElo,
		int defenderCash,
		int defenderOil )
	{
		super();
		this.attackerId = attackerId;
		this.attackerElo = attackerElo;
		this.defenderId = defenderId;
		this.defenderElo = defenderElo;
		this.defenderCash = defenderCash;
		this.defenderOil = defenderOil;
	}

	public NormalDistribution getAttackerWonCnd()
	{
		if (null == attackerWonCnd) {
			lowerElo = Math.min(attackerElo, defenderElo);
			if (SCORING_CURVE_BIAS <= 0D) {
				meanForCnd = 0;
				
			} else {
				meanForCnd = SCORING_CURVE_BIAS * OFFSET__VALID_MATCH_RANGE * lowerElo;
			}
			
			standardDeviationForCnd = SCORING_CURVE_LINEARITY * OFFSET__VALID_MATCH_RANGE
				* lowerElo;
			
			attackerWonCnd = new NormalDistribution(
				meanForCnd, standardDeviationForCnd);
			
		}
		String msg = String.format( "meanForCnd=%f, standardDeviation=%f",
			meanForCnd, standardDeviationForCnd );
		log.info(msg);
		
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
		log.info(String.format(
			"getEloAttackerWins() attackerId=%d, defenderId=%d",
			attackerId, defenderId));
		
		double eloDiff = attackerElo - defenderElo;
		log.info(String.format(
			"getEloAttackerWins() attackerElo=%f,  defenderElo=%f, eloDiff=%f",
			attackerElo, defenderElo, eloDiff));
		
		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(eloDiff);
		log.info(String.format(
			"getEloAttackerWins() winnerLoserCndVal=%f",
			winnerLoserCndVal));
		
		double matchRange = lowerElo * OFFSET__VALID_MATCH_RANGE;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		log.info(String.format(
			"getEloAttackerWins() matchRange (lowerElo * ELO_VALID_MATCH_RANGE)=%f, offset=%f",
			matchRange, offset));
		
		double scaleDividend = ELO_SCALE_DIVIDEND_MULTIPLE * lowerElo;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(-1 * matchRange) - offset;
		double scale = scaleDividend / scaleDivisor;
		log.info(String.format(
			"getEloAttackerWins() scaleDividend=%f, scaleDivisor=%f, scale=%f",
			scaleDividend, scaleDivisor, scale));
		
		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal); 
		log.info(String.format(
			"getEloAttackerWins() (winnerLoserCndVal - offset)=%f, retVal=%f, intRetVal=%d",
			(winnerLoserCndVal - offset), retVal, intRetVal));
		
		return intRetVal;
	}
	
	public int getUnsignedEloAttackerLoses() {
		log.info(String.format(
			"getEloAttackerLoses() attackerId=%d, defenderId=%d",
			attackerId, defenderId));
		
		double eloDiff = defenderElo - attackerElo;
		log.info(String.format(
			"getEloAttackerLoses() attackerElo=%f,  defenderElo=%f, eloDiff=%f",
			attackerElo, defenderElo, eloDiff));
		
		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(eloDiff);
		log.info(String.format(
			"getEloAttackerLoses() winnerLoserCndVal=%f",
			winnerLoserCndVal));
		
		double matchRange = OFFSET__VALID_MATCH_RANGE* lowerElo;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		log.info(String.format(
			"getEloAttackerLoses() matchRange (lowerElo * OFFSET__VALID_MATCH_RANGE)=%f, offset=%f",
			matchRange, offset));
		
		double scaleDividend = ELO_SCALE_DIVIDEND_MULTIPLE * lowerElo;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(-1 * matchRange) - offset;
		double scale = scaleDividend / scaleDivisor;
		log.info(String.format(
			"getEloAttackerLoses() scaleDividend=%f, scaleDivisor=%f, scale=%f",
			scaleDividend, scaleDivisor, scale));
		
		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal); 
		log.info(String.format(
			"getEloAttackerLoses() (winnerLoserCndVal - offset)=%f, retVal=%f, intRetVal=%d",
			(winnerLoserCndVal - offset), retVal, intRetVal));
		
		return intRetVal;
	}
	
	//RESOURCE SECTION
	public int getUnsignedCashAttackerWins() {
		
		log.info(String.format(
			"attackerId=%d, defenderId=%d",
			attackerId, defenderId));
		
		double eloDiff = attackerElo - defenderElo;
		log.info(String.format(
			"cashAttackerWins() attackerElo=%f,  defenderElo=%f, eloDiff=%f",
			attackerElo, defenderElo, eloDiff));
		
		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(eloDiff);
		log.info(String.format(
			"cashAttackerWins() winnerLoserCndVal=%f",
			winnerLoserCndVal));
		
		double matchRange = OFFSET__VALID_MATCH_RANGE * lowerElo;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		log.info(String.format(
			"cashAttackerWins() matchRange (defenderCash)=%f, offset=%f",
			matchRange, offset));

		double scaleDividend = RESOURCE_SCALE_DIVIDEND_MULTIPLE * defenderCash;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(-1 * matchRange) - offset;
		double scale = scaleDividend / scaleDivisor;
		log.info(String.format(
			"cashAttackerWins() scaleDividend=%f, scaleDivisor=%f, scale=%f",
			scaleDividend, scaleDivisor, scale));

		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal); 
		log.info(String.format(
			"cashAttackerWins() (winnerLoserCndVal - offset)=%f, retVal=%f, intRetVal=%d",
			(winnerLoserCndVal - offset), retVal, intRetVal));
		
		return intRetVal;
	}
	
	//TODO: Consider storing some of these values into some private variables
	public int getUnsignedOilAttackerWins() {

		log.info(String.format(
			"attackerId=%d, defenderId=%d",
			attackerId, defenderId));
		
		double eloDiff = attackerElo - defenderElo;
		log.info(String.format(
			"oilAttackerWins() attackerElo=%f,  defenderElo=%f, eloDiff=%f",
			attackerElo, defenderElo, eloDiff));
		
		double winnerLoserCndVal = getAttackerWonCnd().cumulativeProbability(eloDiff);
		log.info(String.format(
			"oilAttackerWins() winnerLoserCndVal=%f",
			winnerLoserCndVal));
		
		//double matchRange = defenderOil;
		double matchRange = OFFSET__VALID_MATCH_RANGE * lowerElo;
		double offset = getAttackerWonCnd().cumulativeProbability(matchRange);
		log.info(String.format(
			"oilAttackerWins() matchRange (defenderOil)=%f, offset=%f",
			matchRange, offset));
		

//		double scaleDividend = RESOURCE_SCALE_DIVIDEND_MULTIPLE * lowerElo;
		double scaleDividend = RESOURCE_SCALE_DIVIDEND_MULTIPLE * defenderOil;
		double scaleDivisor = getAttackerWonCnd().cumulativeProbability(-1 * matchRange) - offset;
		double scale = scaleDividend / scaleDivisor;
		log.info(String.format(
			"oilAttackerWins() scaleDividend=%f, scaleDivisor=%f, scale=%f",
			scaleDividend, scaleDivisor, scale));
		
		double retVal = (winnerLoserCndVal - offset) * scale;
		//July 24, 2014. The amount shouldn't be greater than
		//2 billion...shouldn't be more than one million atm...
		int intRetVal = (int) Math.round(retVal); 
		log.info(String.format(
			"oilAttackerWins() (winnerLoserCndVal - offset)=%f, retVal=%f, intRetVal=%d",
			(winnerLoserCndVal - offset), retVal, intRetVal));
		
		return intRetVal;
	}

}
