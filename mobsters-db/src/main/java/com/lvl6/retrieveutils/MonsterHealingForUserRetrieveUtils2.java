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

import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class MonsterHealingForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(MonsterHealingForUserRetrieveUtils2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_HEALING_FOR_USER;
	private static final UserMonsterHealingForClientMapper rowMapper = new UserMonsterHealingForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	////@Cacheable(value="userMonstersForUser", key="#userId")
	public Map<String, MonsterHealingForUser> getMonstersForUser(String userId) {
		log.debug("retrieving user monsters being healined for userId "
				+ userId);

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.MONSTER_HEALING_FOR_USER__USER_ID);

		Map<String, MonsterHealingForUser> monsterIdsToMonsters = new HashMap<String, MonsterHealingForUser>();
		try {
			List<MonsterHealingForUser> userMonstersHealing = this.jdbcTemplate
					.query(query, values, rowMapper);

			for (MonsterHealingForUser userMonster : userMonstersHealing) {
				monsterIdsToMonsters.put(userMonster.getMonsterForUserId(),
						userMonster);
			}

		} catch (Exception e) {
			log.error("monster healing for user retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return monsterIdsToMonsters;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMonsterHealingForClientMapper implements
			RowMapper<MonsterHealingForUser> {

		private static List<String> columnsSelected;

		@Override
		public MonsterHealingForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MonsterHealingForUser mefu = new MonsterHealingForUser();

			mefu.setUserId(rs
					.getString(DBConstants.MONSTER_HEALING_FOR_USER__USER_ID));
			mefu.setMonsterForUserId(rs
					.getString(DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID));

			Timestamp time = rs
					.getTimestamp(DBConstants.MONSTER_HEALING_FOR_USER__QUEUED_TIME);
			if (null != time && !rs.wasNull()) {
				mefu.setQueuedTime(new Date(time.getTime()));
			}

			mefu.setUserStructHospitalId(rs
					.getString(DBConstants.MONSTER_HEALING_FOR_USER__USER_STRUCT_HOSPITAL_ID));
			mefu.setHealthProgress(rs
					.getFloat(DBConstants.MONSTER_HEALING_FOR_USER__HEALTH_PROGRESS));
			mefu.setPriority(rs
					.getInt(DBConstants.MONSTER_HEALING_FOR_USER__PRIORITY));

			mefu.setElapsedSeconds(rs
					.getFloat(DBConstants.MONSTER_HEALING_FOR_USER__ELAPSED_SECONDS));
			return mefu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.MONSTER_HEALING_FOR_USER__USER_ID);
				columnsSelected
						.add(DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID);
				columnsSelected
						.add(DBConstants.MONSTER_HEALING_FOR_USER__QUEUED_TIME);
				columnsSelected
						.add(DBConstants.MONSTER_HEALING_FOR_USER__USER_STRUCT_HOSPITAL_ID);
				columnsSelected
						.add(DBConstants.MONSTER_HEALING_FOR_USER__HEALTH_PROGRESS);
				columnsSelected
						.add(DBConstants.MONSTER_HEALING_FOR_USER__PRIORITY);
				columnsSelected
						.add(DBConstants.MONSTER_HEALING_FOR_USER__ELAPSED_SECONDS);
			}
			return columnsSelected;
		}
	}
}
