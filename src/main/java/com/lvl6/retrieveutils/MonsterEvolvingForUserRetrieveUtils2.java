package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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

import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.properties.DBConstants;

@Component @DependsOn("gameServer") public class MonsterEvolvingForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_EVOLVING_FOR_USER;
	private static final UserMonsterEvolvingForClientMapper rowMapper = new UserMonsterEvolvingForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	public Map<String, MonsterEvolvingForUser> getCatalystIdsToEvolutionsForUser(int userId) {
		log.debug("retrieving user monsters being evolved for userId " + userId);

		Object[] values = { userId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID);
		
		Map<String, MonsterEvolvingForUser> catalystMonsterIdToEvolution = null;
		try {
			List<MonsterEvolvingForUser> userMonstersEvolving = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			catalystMonsterIdToEvolution = new HashMap<String, MonsterEvolvingForUser>();
			for (MonsterEvolvingForUser evolution : userMonstersEvolving ) {
				catalystMonsterIdToEvolution.put(evolution.getCatalystMonsterForUserId(), evolution);
			}
			
		} catch (Exception e) {
			log.error("monster enhancing for user retrieve db error.", e);
			//    } finally {
			//    	DBConnection.get().close(rs, null, conn);
		}
		return catalystMonsterIdToEvolution;
	}
	
	
	public MonsterEvolvingForUser getEvolutionForUser(int userId) {
		log.debug("retrieving user monsters being healined for userId " + userId);

		Map<String, MonsterEvolvingForUser> catalystMonsterIdToEvolution =
			getCatalystIdsToEvolutionsForUser(userId);
		
		MonsterEvolvingForUser mefu = null;
		if (null != catalystMonsterIdToEvolution && !catalystMonsterIdToEvolution.isEmpty()) {
			Collection<MonsterEvolvingForUser> evolutions = catalystMonsterIdToEvolution.values();
			mefu = (MonsterEvolvingForUser) evolutions.toArray()[0];
		}
		return mefu;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMonsterEvolvingForClientMapper implements RowMapper<MonsterEvolvingForUser> {

		private static List<String> columnsSelected;

		public MonsterEvolvingForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			MonsterEvolvingForUser mefu = new MonsterEvolvingForUser();
			
			mefu.setCatalystMonsterForUserId(rs.getString(DBConstants.MONSTER_EVOLVING_FOR_USER__CATALYST_USER_MONSTER_ID));
			mefu.setMonsterForUserIdOne(rs.getString(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_ONE));

			mefu.setMonsterForUserIdTwo(rs.getString(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_TWO));
			mefu.setUserId(rs.getString(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_ID));
			
			Timestamp time = rs.getTimestamp(DBConstants.MONSTER_EVOLVING_FOR_USER__START_TIME);
			if (null != time && !rs.wasNull()) {
				mefu.setStartTime(new Date(time.getTime()));
			}
			return mefu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.MONSTER_EVOLVING_FOR_USER__CATALYST_USER_MONSTER_ID);
				columnsSelected.add(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_ONE);
				columnsSelected.add(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_TWO);
				columnsSelected.add(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.MONSTER_EVOLVING_FOR_USER__START_TIME);
			}
			return columnsSelected;
		}
	} 	
	
}
