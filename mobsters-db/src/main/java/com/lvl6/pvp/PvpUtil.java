//package com.lvl6.pvp;
//
//import java.util.Set;
//
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.Tuple;
//
//import com.lvl6.info.User;
//
//public interface PvpUtil {
//	
//	public abstract JedisPool getJedisPool();
//
//	public abstract void setJedisPool(JedisPool jedis);
//
//
//	public abstract void removeOfflineEloForUser(Integer userId);
//	
//	public abstract void setOfflineEloForUser(Integer userId, Integer elo);
//	
//	//can be used to determine if user is in the map
//	public abstract Double getOfflineEloForUser(Integer userId);
//	
//	//if update is false, this means remove the user
//	public abstract void updatePvpForUser(User user, boolean update);
//	
//	
//	//groups of elos
//	public abstract Set<Tuple> getOfflineEloTopN(Integer start, Integer stop, int offset, int limit);
//	
//	public abstract long numOffLineEloTopN(Integer start, Integer stop);
//	
//	
//	//OLD AOC LOGIC****************************************************************
//	/*
//	public abstract void setBattlesWonForUser(Integer userId, Double battlesWon);
//	public abstract void setBattlesWonForUser(Integer tournament, Integer userId, Double battlesWon);
//
//	public abstract void setBattlesWonOverTotalBattlesRatioForUser(	Integer userId, Double battlesWonOfTotalBattles);
//
//	public abstract void setTotalCoinValueForUser(Integer userId,
//			Double coinValue);
//
//	public abstract void setExperienceForUser(Integer userId, Double experience);
//
//	//
//	// public abstract void incrementBattlesWonForUser(Integer userId);
//	// public abstract void
//	// incrementBattlesWonOverTotalBattlesRatioForUser(Integer userId);
//	// public abstract void incrementTotalCoinValueForUser(Integer userId,
//	// Double incrementAmount);
//	//
//	public abstract Double getBattlesWonForUser(Integer userId);
//	public abstract Double getBattlesWonForUser(Integer tournament, Integer userId);
//	
//	
//	public abstract Double getBattlesWonOverTotalBattlesRatioForUser(
//			Integer userId);
//
//	public abstract Double getTotalCoinValueForUser(Integer userId);
//
//	public abstract Double getExperienceForUser(Integer userId);
//
//	public abstract long getBattlesWonRankForUser(Integer userId);
//
//	public abstract long getBattlesWonOverTotalBattlesRatioRankForUser(
//			Integer userId);
//
//	public abstract long getTotalCoinValueRankForUser(Integer userId);
//
//	public abstract long getExperienceRankForUser(Integer userId);
//
//	public abstract Set<Tuple> getBattlesWonTopN(Integer start, Integer stop);
//
//	public abstract Set<Tuple> getBattlesWonOverTotalBattlesRatioTopN(
//			Integer start, Integer stop);
//
//	public abstract Set<Tuple> getTotalCoinValueForTopN(Integer start,
//			Integer stop);
//
//	public abstract Set<Tuple> getExperienceTopN(Integer start, Integer stop);
//
//	public abstract void updateLeaderboardForUser(User user);
//
//	public abstract void setScoreForEventAndUser(Integer eventId, Integer userId, Double score);
//	public abstract Double getScoreForEventAndUser(Integer eventId, Integer userId);
//	public abstract long getRankForEventAndUser(Integer eventId, Integer userId);
//	public abstract Set<Tuple> getEventTopN(Integer eventId, Integer start, Integer stop);
//	*/
//	
//
//}