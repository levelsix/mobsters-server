package com.lvl6.pvp;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;

public class PvpUtil2 {

	private static final Logger log = LoggerFactory.getLogger(MiscMethods.class);
	
	//METHODS FOR MATCH MAKING

	public static final double ELO__RANDOM_VAR_MIN = 0.1D;
	public static final double ELO__RANDOM_VAR_MAX = 0.8D;
	public static final double ELO__ICND_MEAN = -0.1D;
	public static final double ELO__ICND_STANDARD_DEVIATION = 0.608D;
	public static final double ELO__MAX_RANGE = 0.4D;

	/*
	 * randVal (aka eloAddend) = [Max Range] x Player's Score x ICND( Random( 0.1, 0.9 ), [Bias], 0.608 )
	 * Recommended Max Range: 40.0%
	 * Recommended Bias: -0.20
	 * 
	 * computed elo = elo + randVal
	 * 
	 * minEloToSearchFor = 95% * computed elo
	 * maxEloToSearchFor = 105% * computed elo
	 * 
	 * elo should be between minElo and maxElo:
	 *  defaultMinElo <= computed elo <= defaultMaxElo
	 */
	public static Map.Entry<Integer, Integer> getMinAndMaxElo(double playerElo) {
		double randVar = ELO__RANDOM_VAR_MIN + (Math.random() * (ELO__RANDOM_VAR_MAX - ELO__RANDOM_VAR_MIN));

		double computedElo = getProspectiveOpponentElo(randVar, playerElo);


		int minElo = (int) (0.95D * computedElo);
		int maxElo = (int) (1.05D * computedElo);
		log.info(String.format(
			"computedElo=%f, minElo=%d, maxElo=%d",
			computedElo, minElo, maxElo));

		//the minimum elo to be searched for is 1000, er PVP__DEFAULT_MIN_ELO
		//TODO: Fix up this hackiness: ensuring DEFAULT MIN ELO is between min (inclusive) and max elo (inclusive)
		minElo = Math.max(ControllerConstants.PVP__DEFAULT_MIN_ELO - 1, minElo);
		maxElo = Math.max(ControllerConstants.PVP__DEFAULT_MIN_ELO + 1, maxElo);
		log.info(String.format(
			"after capping minElo. computedElo=%f, minElo=%d, maxElo=%d",
			computedElo, minElo, maxElo));

		//poor man's pair
		return new AbstractMap.SimpleEntry<Integer, Integer>(minElo,maxElo);
	}
	
	public static double getProspectiveOpponentElo(
		double randVar, double playerElo)
	{
		NormalDistribution eloRangeFunc = new NormalDistribution(
			ELO__ICND_MEAN, ELO__ICND_STANDARD_DEVIATION);

		double cndVal = eloRangeFunc.inverseCumulativeProbability(randVar);
		double eloAddend = ELO__MAX_RANGE * playerElo * cndVal; 
		log.info(String.format(
			"cndVal=%f, playerElo=%f, randVar=%f, eloAddend=%f",
			cndVal, playerElo, randVar, eloAddend));

		return playerElo + eloAddend;
		//		eloAddend = Math.max(eloAddend, ControllerConstants.PVP__DEFAULT_MIN_ELO);

	}
	
}