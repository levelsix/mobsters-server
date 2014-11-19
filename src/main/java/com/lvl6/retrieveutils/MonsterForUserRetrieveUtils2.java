package com.lvl6.retrieveutils;

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

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.MonsterForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class MonsterForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_MONSTER_FOR_USER;
	private static final UserMonsterForClientMapper rowMapper = new UserMonsterForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	////@Cacheable(value="userMonstersForUser", key="#userId")
	public List<MonsterForUser> getMonstersForUser(String userId) {
		log.debug("retrieving user monsters for userId " + userId);
		
		Object[] values = { userId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.MONSTER_FOR_USER__USER_ID);
		
		List<MonsterForUser> userMonsters = null;
		try {
			userMonsters = this.jdbcTemplate
				.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error("monster for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userMonsters;
	}

	public Map<String, List<MonsterForUser>> getUserIdsToMonsterTeamForUserIds(
		List<String> userIds) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ");
		sb.append(TABLE_NAME);
		sb.append(" WHERE ");
		sb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
		sb.append(" IN (");
		int amount = userIds.size();
		List<String> questions = Collections.nCopies(amount, "?");
		String questionStr = StringUtils.csvList(questions);
		sb.append(questionStr);
		sb.append(") AND ");
		sb.append(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM);
		sb.append(" > ?;");

		List<Object> values = new ArrayList<Object>();
		values.addAll(userIds);
		values.add(0);

		String query = sb.toString();

		log.info(String.format(
			"RETRIEVING USERS' TEAMS. query=%s, values=%s",
			query, values));

		Map<String, List<MonsterForUser>> userIdsToCurrentTeam =
			new HashMap<String, List<MonsterForUser>>();
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);
			
			for (MonsterForUser userMonster : mfuList) {
				String userId = userMonster.getUserId();

				//if just saw this user for first time, create the list for his team
				if (!userIdsToCurrentTeam.containsKey(userId)) {
					List<MonsterForUser> currentTeam = new ArrayList<MonsterForUser>();
					userIdsToCurrentTeam.put(userId, currentTeam);
				}

				//get the user's team and add in the monster
				List<MonsterForUser> currentTeam = userIdsToCurrentTeam.get(userId);
				currentTeam.add(userMonster);
			}
			
		} catch (Exception e) {
			log.error("monster for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}

		return userIdsToCurrentTeam;
	}

	////@Cacheable(value="specificMonster", key="#userMonsterId")
	public MonsterForUser getSpecificUserMonster(String userMonsterId)
	{
		log.debug(String.format(
			"retrieving user monster for userMonsterId=%s",
			userMonsterId));

		Object[] values = { userMonsterId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.MONSTER_FOR_USER__ID);
			
		MonsterForUser userMonster = null;
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			if (null != mfuList && !mfuList.isEmpty()) {
				userMonster = mfuList.get(0);
			}
		} catch (Exception e) {
			log.error("monster for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userMonster;
	}

	public Map<String, MonsterForUser> getSpecificUserMonsters(List<String> userMonsterIds) {
		log.debug("retrieving user monsters for userMonsterIds: " + userMonsterIds);

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME); 
		querySb.append(" WHERE ");
		querySb.append(DBConstants.MONSTER_FOR_USER__ID);
		querySb.append(" IN (");

		int amount = userMonsterIds.size();
		List<String> questions = Collections.nCopies(amount, "?");
		String questionMarkStr = StringUtils.csvList(questions);

		querySb.append(questionMarkStr);
		querySb.append(");");

		List <Object> values = new ArrayList<Object>();
		values.addAll(userMonsterIds);

		String query = querySb.toString();
		log.info(String.format( "query=%s, values=%s", query, values ));

		Map<String, MonsterForUser> idsToUserMonsters =
			new HashMap<String, MonsterForUser>();
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);
			
			for (MonsterForUser userMonster : mfuList) {
				idsToUserMonsters.put(userMonster.getId(), userMonster);
			}
			
		} catch (Exception e) {
			log.error("monster for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return idsToUserMonsters;
	}


	public Map<String, MonsterForUser> getSpecificOrAllUserMonstersForUser(
		String userId, Collection<String> userMonsterIds)
	{

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME); 
		querySb.append(" WHERE ");
		querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
		querySb.append("=?");
		List <Object> values = new ArrayList<Object>();
		values.add(userId);

		//if user didn't give userMonsterIds then get all the user's monsters 
		if (userMonsterIds != null && !userMonsterIds.isEmpty() ) {
			log.debug("retrieving user monster for userMonsterIds: " + userMonsterIds);
			querySb.append(" AND ");
			querySb.append(DBConstants.MONSTER_FOR_USER__ID);
			querySb.append(" IN (");

			int amount = userMonsterIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userMonsterIds);
		}
		
		String query = querySb.toString();
		log.info(String.format( "query=%s, values=%s", query, values ));

		Map<String, MonsterForUser> idsToUserMonsters =
			new HashMap<String, MonsterForUser>();
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);
			
			for (MonsterForUser userMonster : mfuList) {
				idsToUserMonsters.put(userMonster.getId(), userMonster);
			}

		} catch (Exception e) {
			log.error("monster for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return idsToUserMonsters;
	}

	public Map<String, MonsterForUser> getSpecificOrAllUnrestrictedUserMonstersForUser(
		String userId, Collection<String> userMonsterIds)
	{

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME); 
		querySb.append(" WHERE ");
		querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
		querySb.append("=?");
		querySb.append(" AND ");
		querySb.append(DBConstants.MONSTER_FOR_USER__RESTRICTED);
		querySb.append("=?");

		List <Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(false);

		//if user didn't give userMonsterIds then get all the user's monsters 
		if (userMonsterIds != null && !userMonsterIds.isEmpty() ) {
			log.debug(String.format(
				"retrieving user monster for userMonsterIds=%s",
				userMonsterIds));
			querySb.append(" AND ");
			querySb.append(DBConstants.MONSTER_FOR_USER__ID);
			querySb.append(" IN (");

			int amount = userMonsterIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userMonsterIds);
		}
		String query = querySb.toString();
		log.info(String.format(
			"query=%s, values=%s",
			query, values));

		Map<String, MonsterForUser> idsToUserMonsters = new HashMap<String, MonsterForUser>();
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);
			
			for (MonsterForUser userMonster : mfuList) {
				idsToUserMonsters.put(userMonster.getId(), userMonster);
			}
			
		} catch (Exception e) {
			log.error("monster for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return idsToUserMonsters;
	}


	public Map<String, MonsterForUser> getSpecificOrAllRestrictedUserMonstersForUser(
		String userId, Collection<String> userMonsterIds)
	{
		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME); 
		querySb.append(" WHERE ");
		querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
		querySb.append("=?");
		querySb.append(" AND ");
		querySb.append(DBConstants.MONSTER_FOR_USER__RESTRICTED);
		querySb.append("=?");

		List <Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(true);

		//if user didn't give userMonsterIds then get all the user's monsters 
		if (userMonsterIds != null && !userMonsterIds.isEmpty() ) {
			log.debug("retrieving user monster for userMonsterIds: " + userMonsterIds);
			querySb.append(" AND ");
			querySb.append(DBConstants.MONSTER_FOR_USER__ID);
			querySb.append(" IN (");

			int amount = userMonsterIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userMonsterIds);
		}
		String query = querySb.toString();
		log.info(String.format(
			"query=%s, values=%s",
			query, values));

		Map<String, MonsterForUser> idsToUserMonsters = new HashMap<String, MonsterForUser>();
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);
			
			for (MonsterForUser userMonster : mfuList) {
				idsToUserMonsters.put(userMonster.getId(), userMonster);
			}
			
		} catch (Exception e) {
			log.error(String.format(
				"getSpecific...() monster for user retrieve db error. userId=%s, mfuIds=%s",
				userId, userMonsterIds), e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return idsToUserMonsters;
	}

	public Map<Integer, MonsterForUser> getPieceDeficientIncompleteMonstersWithUserAndMonsterIds(
		String userId, Collection<Integer> monsterIds)
	{
		int size = monsterIds.size();
		List<String> questions = Collections.nCopies(size, "?");
		String delimiter = ",";

		String qStr = StringUtils.getListInString(questions, delimiter);

		String query = String.format(
			"select * from %s where %s=? and %s in (%s) and %s=? and %s=?;",
			TABLE_NAME, DBConstants.MONSTER_FOR_USER__USER_ID,
			DBConstants.MONSTER_FOR_USER__MONSTER_ID, qStr,
			DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES,
			DBConstants.MONSTER_FOR_USER__IS_COMPLETE);

		//creating a "column in (value1,value2,...,value)" condition, prefer this over
		//chained "or"s  e.g. (column=value1 or column=value2 or...column=value);
		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.addAll(monsterIds);
		values.add(false);
		values.add(false);

		Map<Integer, MonsterForUser> monsterIdsToMonsters = 
			new HashMap<Integer, MonsterForUser>();
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);

			for (MonsterForUser userMonster : mfuList) {
				monsterIdsToMonsters.put(
					userMonster.getMonsterId(), userMonster);
			}

		} catch (Exception e) {
			log.error(String.format(
				"getPieceDef...() monster for user retrieve db error. userId=%s, monsterIds=%s",
				userId, monsterIds), e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		
		return monsterIdsToMonsters;
	}

	public Map<String, Map<String, MonsterForUser>> getCompleteMonstersForUser(
		List<String> userIds)
	{
		StringBuilder querySb = new StringBuilder();

		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
		querySb.append(" IN (");

		int amount = userIds.size();
		List<String> questions = Collections.nCopies(amount, "?");
		String questionStr = StringUtils.csvList(questions);
		querySb.append(questionStr);
		querySb.append(") AND ");

		querySb.append(DBConstants.MONSTER_FOR_USER__IS_COMPLETE);
		querySb.append(" =?;");

		List<Object> values = new ArrayList<Object>();
		values.addAll(userIds);
		values.add(true);

		String query = querySb.toString();
		log.info(String.format(
			"query=%s, values=%s",
			query, values));

		Map<String, Map<String, MonsterForUser>> userIdsToMfuIdsToMonsters =
			new HashMap<String, Map<String, MonsterForUser>>();
		try {
			List<MonsterForUser> mfuList = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);

			for (MonsterForUser mfu : mfuList) {
				String userId = mfu.getUserId();

				//base case where have not seen user before
				if (!userIdsToMfuIdsToMonsters.containsKey(userId)) {
					Map<String, MonsterForUser> mfuIdsToMonsters = new HashMap<String, MonsterForUser>();
					userIdsToMfuIdsToMonsters.put(userId, mfuIdsToMonsters);
				}

				Map<String, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters.get(userId);
				mfuIdsToMonsters.put(mfu.getId(), mfu);
			}

		} catch (Exception e) {
			log.error(String.format(
				"getCompleteMonstersForUser monster for user retrieve db error. userIds=%s",
				userIds), e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userIdsToMfuIdsToMonsters;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMonsterForClientMapper implements RowMapper<MonsterForUser> {

		private static List<String> columnsSelected;

		public MonsterForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			MonsterForUser mfu = new MonsterForUser();
			mfu.setId(rs.getString(DBConstants.MONSTER_FOR_USER__ID));
			mfu.setUserId(rs.getString(DBConstants.MONSTER_FOR_USER__USER_ID));
			mfu.setMonsterId(rs.getInt(DBConstants.MONSTER_FOR_USER__MONSTER_ID));
			mfu.setCurrentExp(rs.getInt(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE));
			mfu.setCurrentLvl(rs.getInt(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL));
			mfu.setCurrentHealth(rs.getInt(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH));
			mfu.setNumPieces(rs.getInt(DBConstants.MONSTER_FOR_USER__NUM_PIECES));
			mfu.setHasAllPieces(rs.getBoolean(DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES));
			mfu.setComplete(rs.getBoolean(DBConstants.MONSTER_FOR_USER__IS_COMPLETE));

			try {
				Timestamp time = rs.getTimestamp(DBConstants.MONSTER_FOR_USER__COMBINE_START_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					mfu.setCombineStartTime(date);
				}
			} catch (Exception e) {
				log.error(String.format(
					"maybe combineStartTime is invalid, mfu=%s", mfu), e);
			}

			mfu.setTeamSlotNum(rs.getInt(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM));
			mfu.setSourceOfPieces(rs.getString(DBConstants.MONSTER_FOR_USER__SOURCE_OF_PIECES));
			mfu.setRestricted(rs.getBoolean(DBConstants.MONSTER_FOR_USER__RESTRICTED));
			mfu.setOffensiveSkillId(rs.getInt(DBConstants.MONSTER_FOR_USER__OFFENSIVE_SKILL_ID));
			mfu.setDefensiveSkillId(rs.getInt(DBConstants.MONSTER_FOR_USER__DEFENSIVE_SKILL_ID));

			return mfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__ID);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__MONSTER_ID);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__NUM_PIECES);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__IS_COMPLETE);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__COMBINE_START_TIME);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__SOURCE_OF_PIECES);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__RESTRICTED);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__OFFENSIVE_SKILL_ID);
				columnsSelected.add(DBConstants.MONSTER_FOR_USER__DEFENSIVE_SKILL_ID);
			}
			return columnsSelected;
		}
	} 	
}
