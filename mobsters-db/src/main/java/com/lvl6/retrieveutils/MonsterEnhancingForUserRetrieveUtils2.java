package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
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

import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class MonsterEnhancingForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(MonsterEnhancingForUserRetrieveUtils2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER;
	private static final UserMonsterEnhancingForClientMapper rowMapper = new UserMonsterEnhancingForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	////@Cacheable(value="userMonstersForUser", key="#userId")
	public Map<String, MonsterEnhancingForUser> getMonstersForUser(String userId) {
		log.debug(String
				.format("retrieving user monsters being healined for userId %s",
						userId));

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID);

		Map<String, MonsterEnhancingForUser> monsterIdsToMonsters = new HashMap<String, MonsterEnhancingForUser>();
		try {
			List<MonsterEnhancingForUser> userMonsters = this.jdbcTemplate
					.query(query, values, rowMapper);

			for (MonsterEnhancingForUser userMonster : userMonsters) {
				monsterIdsToMonsters.put(userMonster.getMonsterForUserId(),
						userMonster);
			}

		} catch (Exception e) {
			log.error("monster enhancing for user retrieve db error.", e);
			//    } finally {
			//    	DBConnection.get().close(rs, null, conn);
		}
		return monsterIdsToMonsters;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMonsterEnhancingForClientMapper implements
			RowMapper<MonsterEnhancingForUser> {

		private static List<String> columnsSelected;

		@Override
		public MonsterEnhancingForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MonsterEnhancingForUser mefu = new MonsterEnhancingForUser();
			mefu.setUserId(rs
					.getString(DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID));
			mefu.setMonsterForUserId(rs
					.getString(DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID));

			Timestamp time = rs
					.getTimestamp(DBConstants.MONSTER_ENHANCING_FOR_USER__EXPECTED_START_TIME);
			if (null != time) {
				mefu.setExpectedStartTime(new Date(time.getTime()));
			}
			mefu.setEnhancingCost(rs
					.getInt(DBConstants.MONSTER_ENHANCING_FOR_USER__ENHANCING_COST));
			mefu.setEnhancingComplete(rs
					.getBoolean(DBConstants.MONSTER_ENHANCING_FOR_USER__ENHANCING_COMPLETE));
			return mefu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID);
				columnsSelected
						.add(DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID);
				columnsSelected
						.add(DBConstants.MONSTER_ENHANCING_FOR_USER__EXPECTED_START_TIME);
				columnsSelected
						.add(DBConstants.MONSTER_ENHANCING_FOR_USER__ENHANCING_COST);
				columnsSelected
						.add(DBConstants.MONSTER_ENHANCING_FOR_USER__ENHANCING_COMPLETE);
			}
			return columnsSelected;
		}
	}

}
