package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class MonsterEvolvingForUserRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_EVOLVING_FOR_USER;


	////@Cacheable(value="userMonstersForUser", key="#userId")
	public static Map<Long, MonsterEvolvingForUser> getMonstersForUser(int userId) {
		log.debug("retrieving user monsters being healined for userId " + userId);

		Connection conn = null;
		ResultSet rs = null;
		Map<Long, MonsterEvolvingForUser> userMonsters = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
			userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
		} catch (Exception e) {
    	log.error("monster enhancing for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return userMonsters;
	}

//	public static Map<Long, MonsterEvolvingForUser> getMonstersWithUserAndMonsterIds(
//			int userId, Collection<Long> userMonsterIds) {
//		int size = userMonsterIds.size();
//		List<String> questions = Collections.nCopies(size, "?");
//		String delimiter = ",";
//
//		String query = "select * from " + TABLE_NAME + " where ";
//		List <Object> values = new ArrayList<Object>();
//
//		//creating a "column in (value1,value2,...,value)" condition, prefer this over
//		//chained "or"s  e.g. (column=value1 or column=value2 or...column=value);
//		query += DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID + "=? and ";
//		values.add(userId);
//		query += DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID + " in (" +
//				StringUtils.getListInString(questions, delimiter) + ") and ";
//		values.add(userMonsterIds);
//
//		Connection conn = null;
//		ResultSet rs = null;
//		Map<Long, MonsterEvolvingForUser> incompleteMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			incompleteMonsters = convertRSToUserMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster enhancing for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//		return incompleteMonsters;
//	}

	private static Map<Long, MonsterEvolvingForUser> convertRSToUserMonsterIdsToMonsters(ResultSet rs) {
		Map<Long, MonsterEvolvingForUser> catalystMonsterIdToEvolution = new HashMap<Long, MonsterEvolvingForUser>();
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				while(rs.next()) {
					MonsterEvolvingForUser evolution = convertRSRowToMonster(rs);
					if (evolution != null) {
						catalystMonsterIdToEvolution.put(evolution.getCatalystMonsterForUserId(), evolution);
					}
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);
			}
		}
		return catalystMonsterIdToEvolution;
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static MonsterEvolvingForUser convertRSRowToMonster(ResultSet rs) throws SQLException {
		int i = 1;
		long catalystMonsterForUserId = rs.getLong(i++);
		long monsterForUserIdOne = rs.getLong(i++);
		long monsterForUserIdTwo = rs.getLong(i++);
		
		int userId = rs.getInt(i++);

		Timestamp ts = null;
		Date startTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			startTime = new Date(ts.getTime());
		}

		//    Date queuedTime = null;
		//    try {
		//    	ts = rs.getTimestamp(i++);
		//    	queuedTime = new Date(ts.getTime());
		//    } catch (SQLException e) {
		//    	log.error("expected start time might be null userId=" + userId, e);
		//    }

		MonsterEvolvingForUser userMonster = new MonsterEvolvingForUser(
				catalystMonsterForUserId, monsterForUserIdOne, monsterForUserIdTwo, userId, startTime);
		return userMonster;
	}

}
