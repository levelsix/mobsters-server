//package com.lvl6.pvp;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.annotation.Resource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.Tuple;
//
//import com.lvl6.info.User;
//import com.lvl6.properties.ControllerConstants;
//
//public class PvpUtilImpl implements PvpUtil {
//
//	Logger log = LoggerFactory.getLogger(getClass());
//
////	protected JdbcTemplate jdbc;
////
////	@Resource
////	protected DataSource dataSource;
////
////	public DataSource getDataSource() {
////		return dataSource;
////	}
////
////	public void setDataSource(DataSource dataSource) {
////		this.dataSource = dataSource;
////		jdbc = new JdbcTemplate(dataSource);
////	}
//
//	@Resource
//	protected JedisPool jedisPool;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.lvl6.leaderboards.LeaderBoardUtil#getJedis()
//	 */
//	@Override
//	public JedisPool getJedisPool() {
//		return jedisPool;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.lvl6.leaderboards.LeaderBoardUtil#setJedis(com.lvl6.leaderboards.
//	 * Lvl6Jedis)
//	 */
//	@Override
//	public void setJedisPool(JedisPool jedis) {
//		this.jedisPool = jedis;
//	}
//
//	@Override
//	public void removeOfflineEloForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zrem(PvpConstants.OFFLINE_USERS_BY_ELO, userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool when a user. user=" + userId, e);
//		} finally {
//			if (null != jedis) {
//				jedisPool.returnResource(jedis);
//			}
//		}
//	}
//
//	//if userId is already in the sorted set, then existing elo (the score/key) will change
//	//to be the new elo that is passed in
//	@Override
//	public void setOfflineEloForUser(Integer userId, Integer elo) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zadd(PvpConstants.OFFLINE_USERS_BY_ELO, elo, userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool when adding elo for a user. user=" + userId +
//					"\t elo=" + elo, e);
//		} finally {
//			if (null != jedis) {
//				jedisPool.returnResource(jedis);
//			}
//		}
//	}
//	
//	//if null is returned, this means no such userId exists
//	@Override
//	public Double getOfflineEloForUser(Integer userId) {
//		Double res = null;
//		
//		Jedis jedis = jedisPool.getResource();
//		try {
//			res = jedis.zscore(PvpConstants.OFFLINE_USERS_BY_ELO,
//					userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool when getting elo for userId:" + userId, e);
//		} finally {
//			if (null != jedis)
//				jedisPool.returnResource(jedis);
//		}
//		return res;
//	}
//	
//	
//	//if update is false, this means remove the user
//	@Override
//	public void updatePvpForUser(User user, boolean update) {
//		if (null == user) {
//			return;
//		}
//		long startTime = new Date().getTime();
//		try {
//			int userId = user.getId();
//			int elo = user.getElo();
//			
//			if (update) {
//				//if user doesn't exist, this should create an entry for him
//				setOfflineEloForUser(userId, elo);
//				
//			} else {
//				//since not updating, remove him
//				removeOfflineEloForUser(userId);
//			}
//			
//		} catch (Exception e) {
//			log.error("Error updating pvp for user: " + user + " update=" + update, e);
//		}
//		long endTime = new Date().getTime();
//		log.info("Update pvp for user {} took {}ms", user.getId(), endTime-startTime);
//	}
//	
//	
//	
//	//groups of elos
//	@Override
//	public Set<Tuple> getOfflineEloTopN(Integer start, Integer stop, int offset, int limit) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Set<Tuple> idsAndElos = jedis.zrevrangeByScoreWithScores(PvpConstants.OFFLINE_USERS_BY_ELO,
//					stop, start, offset, limit);
//			return idsAndElos;
//		} catch (Exception e) {
//			log.error("Error in jedis pool when getting entries. start=" + start + "\t stop=" +
//					stop + "\t offset=" + offset + "\t limit=" + limit, e);
//		} finally {
//			if (null != jedis) {
//				jedisPool.returnResource(jedis);
//			}
//		}
//		return new HashSet<Tuple>();
//	}
//	
//	@Override
//	public long numOffLineEloTopN(Integer start, Integer stop) {
//		long count = 0;
//
//		Jedis jedis = jedisPool.getResource();
//		try {
//			count = jedis.zcount(PvpConstants.OFFLINE_USERS_BY_ELO, start, stop);
//		} catch (Exception e) {
//			log.error("Error in jedis pool when calculating num entries between start=" + 
//					start + "\t stop=" + stop, e);
//		} finally {
//			if (null != jedis)
//				jedisPool.returnResource(jedis);
//		}
//		return count;
//	}
//	
//	
//	
//	
//	
//	
//	
//	//OLD AOC LOGIC****************************************************************
//	/*@Override
//	public void setBattlesWonForUser(Integer userId, Double battlesWon) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zadd(PvpConstants.BATTLES_WON, battlesWon,
//					userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//	}
//
//	@Override
//	public void setBattlesWonOverTotalBattlesRatioForUser(Integer userId,
//			Double battlesWonOfTotalBattles) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zadd(PvpConstants.BATTLES_WON_TO_TOTAL_BATTLES_RATIO,
//					battlesWonOfTotalBattles, userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//	}
//
//	@Override
//	public Double getBattlesWonForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Double res = jedis.zscore(PvpConstants.BATTLES_WON,
//					userId.toString());
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0d;
//	}
//
//	@Override
//	public Double getBattlesWonOverTotalBattlesRatioForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			return jedis.zscore(PvpConstants.BATTLES_WON_TO_TOTAL_BATTLES_RATIO,userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0d;
//	}
//
//	@Override
//	public long getBattlesWonRankForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Long res = jedis.zrevrank(PvpConstants.BATTLES_WON,	userId.toString()) + 1;
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0;
//	}
//
//	@Override
//	public long getBattlesWonOverTotalBattlesRatioRankForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Long res = jedis.zrevrank(
//					PvpConstants.BATTLES_WON_TO_TOTAL_BATTLES_RATIO,
//					userId.toString()) + 1;
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0;
//	}
//
//	@Override
//	public Set<Tuple> getBattlesWonTopN(Integer start, Integer stop) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Set<Tuple> ids = jedis.zrevrangeWithScores(PvpConstants.BATTLES_WON,start, stop);
//			return ids;//convertToIdStringsToInts(ids);
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return new HashSet<Tuple>();
//	}
//
//	@Override
//	public Set<Tuple> getBattlesWonOverTotalBattlesRatioTopN(Integer start,
//			Integer stop) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Set<Tuple> ids = jedis.zrevrangeWithScores(
//					PvpConstants.BATTLES_WON_TO_TOTAL_BATTLES_RATIO,
//					start, stop);
//			return ids;//convertToIdStringsToInts(ids);
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return new HashSet<Tuple>();
//	}
//
//
//	@Override
//	public void setTotalCoinValueForUser(Integer userId, Double coinWorth) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zadd(PvpConstants.COIN_WORTH, coinWorth,
//					userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//	}
//
//	@Override
//	public void setExperienceForUser(Integer userId, Double experience) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zadd(PvpConstants.EXPERIENCE, experience,
//					userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//	}
//
//	// @Override
//	// public void incrementTotalCoinValueForUser(Integer userId, Double amount)
//	// {
//	// jedis.zincrby(PvpConstants.SILVER, amount, userId.toString());
//	// }
//	@Override
//	public Double getTotalCoinValueForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Double res = jedis.zscore(PvpConstants.COIN_WORTH,
//					userId.toString());
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0d;
//	}
//
//	@Override
//	public Double getExperienceForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Double res = jedis.zscore(PvpConstants.EXPERIENCE,
//					userId.toString());
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0d;
//	}
//
//	@Override
//	public long getTotalCoinValueRankForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Long res = jedis.zrevrank(PvpConstants.COIN_WORTH,
//					userId.toString()) + 1;
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0;
//	}
//
//	@Override
//	public long getExperienceRankForUser(Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Long res = jedis.zrevrank(PvpConstants.EXPERIENCE,
//					userId.toString()) + 1;
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0;
//	}
//
//	@Override
//	public Set<Tuple> getTotalCoinValueForTopN(Integer start, Integer stop) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Set<Tuple> ids = jedis.zrevrangeWithScores(PvpConstants.COIN_WORTH,
//					start, stop);
//			return ids;//convertToIdStringsToInts(ids);
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return new HashSet<Tuple>();
//	}
//
//	@Override
//	public Set<Tuple> getExperienceTopN(Integer start, Integer stop) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Set<Tuple> ids = jedis.zrevrangeWithScores(PvpConstants.EXPERIENCE,
//					start, stop);
//			return ids;//convertToIdStringsToInts(ids);
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return new HashSet<Tuple>();
//	}
//
//	protected List<Integer> convertToIdStringsToInts(Set<String> ids) {
//		List<Integer> userIds = new ArrayList<Integer>();
//		if (ids != null) {
//			for (String id : ids) {
//				userIds.add(Integer.parseInt(id));
//			}
//		}
//		return userIds;
//	}
//
//	@Override
//	public void updateLeaderboardForUser(User user) {
//		if (user != null) {
//			long startTime = new Date().getTime();
//			try {
//				setBattlesWonForUser(user.getId(),
//						(double) user.getBattlesWon());
//
//				if (user.getBattlesWon() + user.getBattlesLost() > ControllerConstants.LEADERBOARD__MIN_BATTLES_REQUIRED_FOR_KDR_CONSIDERATION) {
//					setBattlesWonOverTotalBattlesRatioForUser(user.getId(),
//						 ((double) user.getBattlesWon() / (user
//									.getBattlesLost() + user.getBattlesWon())));
//				} else {
//					setBattlesWonOverTotalBattlesRatioForUser(user.getId(), 0.0);
//				}
//				setTotalCoinValueForUser(user.getId(), (double) user.getCash());
//				setExperienceForUser(user.getId(),
//						(double) user.getExperience());
//
//			} catch (Exception e) {
//				log.error("Error updating leaderboard for user: " + user, e);
//			}
//			long endTime = new Date().getTime();
//			log.info("Update Leaderboard for user {} took {}ms", user.getId(), endTime-startTime);
//		}
//	}
//
//	@Override
//	public void setBattlesWonForUser(Integer tournament, Integer userId,Double battlesWon) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zadd(PvpConstants.BATTLES_WON_FOR_TOURNAMENT(tournament), battlesWon,	userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//	}
//
//	@Override
//	public Double getBattlesWonForUser(Integer tournament, Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Double res = jedis.zscore(PvpConstants.BATTLES_WON_FOR_TOURNAMENT(tournament), userId.toString());
//			if(res != null)
//				return res;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0d;
//	}
//
//	@Override
//	public void setScoreForEventAndUser(Integer eventId, Integer userId, Double score) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			jedis.zadd(PvpConstants.RANK_FOR_EVENT(eventId), score, userId.toString());
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//	}
//
//	@Override
//	public Double getScoreForEventAndUser(Integer eventId, Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Double res = jedis.zscore(PvpConstants.RANK_FOR_EVENT(eventId), userId.toString());
//			if(res != null)
//				return res; 
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0d;
//	}
//
//	@Override
//	public long getRankForEventAndUser(Integer eventId, Integer userId) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Long res = jedis.zrevrank(PvpConstants.RANK_FOR_EVENT(eventId), userId.toString());
//			if(res != null)
//				return res+1;
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return 0;
//	}
//
//	
//	
//	@Override
//	public Set<Tuple> getEventTopN(Integer eventId, Integer start, Integer stop) {
//		Jedis jedis = jedisPool.getResource();
//		try {
//			Set<Tuple> ids = jedis.zrevrangeWithScores(PvpConstants.RANK_FOR_EVENT(eventId),
//					start, stop);
//			return ids;//convertToIdStringsToInts(ids);
//		} catch (Exception e) {
//			log.error("Error in jedis pool", e);
//		} finally {
//			if (jedis != null)
//				jedisPool.returnResource(jedis);
//		}
//		return new HashSet<Tuple>();
//	}
//*/
//}
