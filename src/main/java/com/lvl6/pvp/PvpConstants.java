package com.lvl6.pvp;

public class PvpConstants {
	
	//hazelcast stuff
	//these names should match the actual properties (in OfflinePvpUser class) verbatim
	public static String OFFLINE_PVP_USER__ELO = "elo";
	public static String OFFLINE_PVP_USER__SHIELD_END_TIME = "shieldEndTime";
	public static String OFFLINE_PVP_USER__IN_BATTLE_SHIELD_END_TIME = "inBattleShieldEndTime";
	//these names should match the actual properties (in MonsterForPvp class) verbatim
	public static String MONSTER_FOR_PVP__ELO = "elo";
	
	
	
	//key for a set. The name for a set in jedis
	public static String OFFLINE_USERS_BY_ELO = "offline_users_by_elo";
	
	
	
	
	
	//OLD AOC LOGIC****************************************************************
  public static String BATTLES_WON = "battles_won";
  public static String BATTLES_TOTAL = "battles_total";
  public static String BATTLES_WON_TO_TOTAL_BATTLES_RATIO = "battles_won_to_battles_total_ratio";
  public static String COIN_WORTH = "coin_worth";
  public static String EXPERIENCE = "experience";
  
  public static String BATTLES_WON_FOR_TOURNAMENT(Integer tournament) {
	  return "battles_won_for_tournament_"+tournament;
  }
  
  public static String RANK_FOR_EVENT(Integer event) {
	  return "rank_for_event_"+event;
  }

}