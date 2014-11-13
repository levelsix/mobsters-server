package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil.UserAchievementForClientMapper;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class ClanRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLANS;
	private static final ClanForClientMapper rowMapper = new ClanForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//CONTROLLER LOGIC******************************************************************
	public static List<String> getClanIdsFromClans(Collection<Clan> clanList) {
		List<String> clanIdList = new ArrayList<String>();

		for (Clan clan : clanList) {
			String clanId = clan.getId();
			clanIdList.add(clanId);
		}
		return clanIdList;
	}

	//RETRIEVE QUERIES*********************************************************************
	//@Cacheable(value="clanWithId", key="#clanId")
	public static Clan getClanWithId(String clanId) {
		log.debug("retrieving clan with id " + clanId);

		TreeMap <String, Object> absoluteParams = new TreeMap<String, Object>();
		absoluteParams.put(DBConstants.CLANS__ID, clanId);

		Clan clan = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteParams, TABLE_NAME);
			clan = convertRSToSingleClan(rs);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return clan;
	}



	public static Map<Integer, Clan> getClansByIds(Collection<Integer> clanIds) {
		log.debug("retrieving clans with ids " + clanIds);

		if (clanIds == null || clanIds.size() <= 0 ) {
			return new HashMap<Integer, Clan>();
		}

		/*
    String query = "select * from " + TABLE_NAME + " where (";
    List<String> condClauses = new ArrayList<String>();
    List <Object> values = new ArrayList<Object>();
    for (Integer clanId : clanIds) {
      condClauses.add(DBConstants.CLANS__ID + "=?");
      values.add(clanId);
    }
    query += StringUtils.getListInString(condClauses, "or") + ")";
		 */
		List<Object> values = new ArrayList<Object>();
		values.addAll(clanIds);

		List<String> questions = Collections.nCopies(clanIds.size(), "?");
		String csQuestions = StringUtils.getListInString(questions, ",");
		String query = String.format(
			"SELECT * FROM %s WHERE %s IN (%s)",
			TABLE_NAME, DBConstants.CLANS__ID, csQuestions);

		Connection conn = null;
		ResultSet rs = null;
		Map<Integer, Clan> clanIdToClanMap = new HashMap<Integer, Clan>();
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			clanIdToClanMap = convertRSToClanIdToClanMap(rs);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return clanIdToClanMap;

	}

	public static List<Clan> getClansWithSimilarNameOrTag(String name, String tag) {
		log.debug(String.format(
			"retrieving clan with name=%s, tag=%s",
			name, tag));

		TreeMap <String, Object> likeParams = new TreeMap<String, Object>();
		likeParams.put(DBConstants.CLANS__NAME, "%"+name+"%");
		likeParams.put(DBConstants.CLANS__TAG, "%"+tag+"%");

		Connection conn = null;
		ResultSet rs = null;
		List<Clan> clans = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsLikeOr(conn, likeParams, TABLE_NAME);
			clans = convertRSToClansList(rs);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return clans;
	}

	public static Clan getClanWithNameOrTag(String name, String tag) {
		log.debug(String.format(
			"retrieving clan with name=%s, tag=%s",
			name, tag));

		TreeMap <String, Object> absoluteParams = new TreeMap<String, Object>();
		absoluteParams.put(DBConstants.CLANS__NAME, name);
		absoluteParams.put(DBConstants.CLANS__TAG, tag);

		Connection conn = null;
		ResultSet rs = null;
		Clan clan = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteOr(conn, absoluteParams, TABLE_NAME);
			clan = convertRSToSingleClan(rs);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return clan;
	}

	public static List<Clan> getMostRecentClansBeforeClanId(int limit, int clanId) {
		TreeMap <String, Object> lessThanParamsToVals = new TreeMap<String, Object>();
		lessThanParamsToVals.put(DBConstants.CLANS__ID, clanId);

		Connection conn = null;
		ResultSet rs = null;
		List<Clan> clans = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAndOrderbydescLimitLessthan(conn, null, TABLE_NAME, DBConstants.CLANS__ID, limit, lessThanParamsToVals);
			clans = convertRSToClansList(rs);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return clans;
	}

	public static List<Clan> getMostRecentClans(int limit) {
		Connection conn = null;
		ResultSet rs = null;
		List<Clan> clans = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAndOrderbydescLimit(conn, null, TABLE_NAME, DBConstants.CLANS__ID, limit);
			clans = convertRSToClansList(rs);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return clans;
	}

	private static Clan convertRSToSingleClan(
		ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				while(rs.next()) {
					Clan clan = convertRSRowToClan(rs);
					return clan;
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return null;
	}

	private static List<Clan> convertRSToClansList(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				List<Clan> clansList = new ArrayList<Clan>();
				while(rs.next()) {
					Clan clan = convertRSRowToClan(rs);
					clansList.add(clan);
				}
				return clansList;
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return null;
	}

	private static Map<Integer, Clan> convertRSToClanIdToClanMap(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				Map<Integer, Clan> clanIdsToClans = new HashMap<Integer, Clan>();
				while (rs.next()) {
					Clan c = convertRSRowToClan(rs);
					if (null != c) {
						int clanId = c.getId();
						clanIdsToClans.put(clanId, c);
					}
				}
				return clanIdsToClans;
			} catch(SQLException e) {
				log.error("problem with database call.", e);
			}
		}
		return null;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class ClanForClientMapper implements RowMapper<Clan> {

		private static List<String> columnsSelected;

		public Clan mapRow(ResultSet rs, int rowNum) throws SQLException {
			Clan clan = new Clan();
			clan.setId(rs.getString(DBConstants.CLANS__ID));
			clan.setName(rs.getString(DBConstants.CLANS__NAME));
			try {
				Timestamp time = rs.getTimestamp(DBConstants.CLANS__CREATE_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					clan.setCreateTime(date);
				}
			} catch (Exception e) {
				log.error(String.format(
					"maybe ocreateTime is invalid, clan=%s", clan), e);
			}
			
			clan.setDescription(rs.getString(DBConstants.CLANS__DESCRIPTION));
			clan.setTag(rs.getString(DBConstants.CLANS__TAG));
			clan.setRequestToJoinRequired(rs.getBoolean(DBConstants.CLANS__REQUEST_TO_JOIN_REQUIRED));
			clan.setClanIconId(rs.getInt(DBConstants.CLANS__CLAN_ICON_ID));
			
			return clan;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLANS__ID);
				columnsSelected.add(DBConstants.CLANS__NAME);
				columnsSelected.add(DBConstants.CLANS__CREATE_TIME);
				columnsSelected.add(DBConstants.CLANS__DESCRIPTION);
				columnsSelected.add(DBConstants.CLANS__TAG);
				columnsSelected.add(DBConstants.CLANS__REQUEST_TO_JOIN_REQUIRED);
				columnsSelected.add(DBConstants.CLANS__CLAN_ICON_ID);
			}
			return columnsSelected;
		}
	} 	
}
