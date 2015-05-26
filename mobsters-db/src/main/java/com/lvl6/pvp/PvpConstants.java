package com.lvl6.pvp;

public class PvpConstants {

	//hazelcast stuff
	//these names should match the actual properties (in PvpUser class) verbatim
	public static String PVP_USER__USER_ID = "userId";
	public static String PVP_USER__PVP_LEAGUE_ID = "pvpLeagueId";
	public static String PVP_USER__RANK = "rank";
	public static String PVP_USER__ELO = "elo";
	public static String PVP_USER__SHIELD_END_TIME = "shieldEndTime";
	public static String PVP_USER__IN_BATTLE_END_TIME = "inBattleEndTime";
	public static String PVP_USER__ATTACKS_WON = "attacksWon";
	public static String PVP_USER__DEFENSES_WON = "defensesWon";
	public static String PVP_USER__ATTACKS_LOST = "attacksLost";
	public static String PVP_USER__DEFENSES_LOST = "defensesLost";
	//these names should match the actual properties (in MonsterForPvp class) verbatim
	public static String MONSTER_FOR_PVP__ELO = "elo";

	//key for a set. The name for a set in jedis
	public static String PVP_USERS_BY_ELO = "pvp_users_by_elo";

	//OLD AOC LOGIC****************************************************************
	public static String BATTLES_WON = "battles_won";
	public static String BATTLES_TOTAL = "battles_total";
	public static String BATTLES_WON_TO_TOTAL_BATTLES_RATIO = "battles_won_to_battles_total_ratio";
	public static String COIN_WORTH = "coin_worth";
	public static String EXPERIENCE = "experience";

	public static String BATTLES_WON_FOR_TOURNAMENT(Integer tournament) {
		return "battles_won_for_tournament_" + tournament;
	}

	public static String RANK_FOR_EVENT(Integer event) {
		return "rank_for_event_" + event;
	}

}