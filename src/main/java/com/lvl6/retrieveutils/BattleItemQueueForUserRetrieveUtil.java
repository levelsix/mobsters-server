package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.BattleItemQueueForUser;

import com.lvl6.properties.DBConstants;

@Component @DependsOn("gameServer") public class BattleItemQueueForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_BATTLE_ITEM_QUEUE_FOR_USER;
	private static final UserBattleItemQueueForClientMapper rowMapper = new UserBattleItemQueueForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	public List<BattleItemQueueForUser> getUserBattleItemQueuesForUser(String userId) {
		log.debug(String.format(
				"retrieving user battle item queues for userId %s", userId));

		Object[] values = { userId };
		String query = String.format(
				"select * from %s where %s=?",
				TABLE_NAME, DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__USER_ID);

		List<BattleItemQueueForUser> userBattleItemQueues = null;
		try {
			userBattleItemQueues = this.jdbcTemplate
					.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("battle item queue for user retrieve db error.", e);
			userBattleItemQueues = new ArrayList<BattleItemQueueForUser>();
		}
		return userBattleItemQueues;
	}


	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserBattleItemQueueForClientMapper implements RowMapper<BattleItemQueueForUser> {

		private static List<String> columnsSelected;

		public BattleItemQueueForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			BattleItemQueueForUser biqfu = new BattleItemQueueForUser();
			biqfu.setPriority(rs.getInt(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__PRIORITY));
			biqfu.setUserId(rs.getString(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__USER_ID));
			biqfu.setBattleItemId(rs.getInt(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__BATTLE_ITEM_ID));
			biqfu.setExpectedStartTime(rs.getTimestamp(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__EXPECTED_START_TIME));
			
			return biqfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__PRIORITY);
				columnsSelected.add(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__BATTLE_ITEM_ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__EXPECTED_START_TIME);
			}
			return columnsSelected;
		}
	} 	

}
