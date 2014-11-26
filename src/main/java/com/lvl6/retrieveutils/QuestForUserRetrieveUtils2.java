package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.lvl6.info.QuestForUser;
import com.lvl6.properties.DBConstants;

@Component @DependsOn("gameServer") public class QuestForUserRetrieveUtils2 {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	private final String TABLE_NAME = DBConstants.TABLE_QUEST_FOR_USER;
	private static final UserQuestForClientMapper rowMapper = new UserQuestForClientMapper(); 
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//only used in script
	public List<QuestForUser> getUnredeemedIncompleteUserQuests(String userId) {
		log.debug("retrieving unredeemed and incomplete user quests");

		List<QuestForUser> userQuests = null;
		try {
			Object[] values = { userId, false, false };
			String query = String.format(
				"select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.QUEST_FOR_USER__USER_ID,
				DBConstants.QUEST_FOR_USER__IS_REDEEMED,
				DBConstants.QUEST_FOR_USER__IS_COMPLETE);
			
			userQuests = this.jdbcTemplate
				.query(query, values, rowMapper);
			
		} catch (Exception e) {
			log.error("quest for user retrieve db error.", e);
			userQuests = new ArrayList<QuestForUser>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userQuests;
	}

	////@Cacheable(value="unredeemedUserQuestsForUser", key="#userId")
	public List<QuestForUser> getUnredeemedUserQuestsForUser(String userId) {
		log.debug("retrieving unredeemed user quests for userId " + userId);

		List<QuestForUser> userQuests = null;
		try {
			Object[] values = { userId, false };
			String query = String.format(
				"select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.QUEST_FOR_USER__USER_ID,
				DBConstants.QUEST_FOR_USER__IS_REDEEMED);
			
			userQuests = this.jdbcTemplate
				.query(query, values, rowMapper);
			
		} catch (Exception e) {
			log.error("quest for user retrieve db error.", e);
			userQuests = new ArrayList<QuestForUser>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userQuests;
	}

	public Map<Integer, QuestForUser> getQuestIdToUnredeemedUserQuests(String userId) {
		log.debug("retrieving unredeemed user quests map for userId " + userId);

		Map<Integer, QuestForUser> questIdsToUnredeemedUserQuests =
			new HashMap<Integer, QuestForUser>();
		List<QuestForUser> userQuests = getUnredeemedUserQuestsForUser(userId);
		for (QuestForUser uq : userQuests) {
			int questId = uq.getQuestId();
			questIdsToUnredeemedUserQuests.put(questId, uq);
		}
		
		return questIdsToUnredeemedUserQuests;
	}

	////@Cacheable(value="userQuestsForUser", key="#userId")
	public List<QuestForUser> getUserQuestsForUser(String userId) {
		log.debug("retrieving user quests for userId " + userId);

		List<QuestForUser> userQuests = null;
		try {
			Object[] values = { userId };
			String query = String.format(
				"select * from %s where %s=?",
				TABLE_NAME, DBConstants.QUEST_FOR_USER__USER_ID);

			userQuests = this.jdbcTemplate
				.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error("quest for user retrieve db error.", e);
			userQuests = new ArrayList<QuestForUser>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userQuests;
	}

	public QuestForUser getSpecificUnredeemedUserQuest(String userId, int questId) {
		log.debug("retrieving specific unredeemed user quest for userid " + userId + " and questId " + questId);
		
		QuestForUser userQuest = null;
		try {
			Object[] values = { userId, questId, false };
			String query = String.format(
				"select * from %s where %s=? and %s=? and %s=?",
				TABLE_NAME, DBConstants.QUEST_FOR_USER__USER_ID,
				DBConstants.QUEST_FOR_USER__QUEST_ID,
				DBConstants.QUEST_FOR_USER__IS_REDEEMED);
			
			List<QuestForUser> qfuList = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			if (null != qfuList && !qfuList.isEmpty()) {
				userQuest = qfuList.get(0);
			}
		} catch (Exception e) {
			log.error("quest for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userQuest;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserQuestForClientMapper implements RowMapper<QuestForUser> {

		private static List<String> columnsSelected;

		public QuestForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			QuestForUser qfu = new QuestForUser();
			qfu.setUserId(rs.getString(DBConstants.QUEST_FOR_USER__USER_ID));
			qfu.setQuestId(rs.getInt(DBConstants.QUEST_FOR_USER__QUEST_ID));
			qfu.setRedeemed(rs.getBoolean(DBConstants.QUEST_FOR_USER__IS_REDEEMED));
			qfu.setComplete(rs.getBoolean(DBConstants.QUEST_FOR_USER__IS_COMPLETE));
			return qfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.QUEST_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.QUEST_FOR_USER__QUEST_ID);
				columnsSelected.add(DBConstants.QUEST_FOR_USER__IS_REDEEMED);
				columnsSelected.add(DBConstants.QUEST_FOR_USER__IS_COMPLETE);
			}
			return columnsSelected;
		}
	} 	
}
