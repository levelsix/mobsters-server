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

import com.lvl6.info.ResearchForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class ResearchForUserRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_RESEARCH_FOR_USER;
	private static final UserResearchForClientMapper rowMapper = new UserResearchForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	////@Cacheable(value="userMonstersForUser", key="#userId")
	public List<ResearchForUser> getAllResearchForUser(String userId) {
		log.debug("retrieving researchForUser for userId {}", userId);
		
		Object[] values = { userId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.RESEARCH_FOR_USER__USER_ID);
		
		List<ResearchForUser> userResearch = null;
		try {
			userResearch = this.jdbcTemplate
				.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error("ResearchForUser retrieve db error.", e);
			userResearch = new ArrayList<ResearchForUser>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userResearch;
	}
	
	public ResearchForUser getResearchForUser(String userResearchUuid) {
		log.debug("retrieving researchForUser for userResearchUuid {}", userResearchUuid);
		
		Object[] values = { userResearchUuid };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.RESEARCH_FOR_USER__ID);
		
		List<ResearchForUser> userResearch = null;
		try {
			userResearch = this.jdbcTemplate
				.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error("ResearchForUser retrieve db error.", e);
			userResearch = new ArrayList<ResearchForUser>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userResearch.get(0);
	}


	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserResearchForClientMapper implements RowMapper<ResearchForUser> {

		private static List<String> columnsSelected;

		public ResearchForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ResearchForUser rfu = new ResearchForUser();
			
			rfu.setId(rs.getString(DBConstants.RESEARCH_FOR_USER__ID));
			rfu.setUserId(rs.getString(DBConstants.RESEARCH_FOR_USER__USER_ID));
			rfu.setResearchId(rs.getInt(DBConstants.RESEARCH_FOR_USER__RESEARCH_ID));
			rfu.setTimePurchased(rs.getTimestamp(DBConstants.RESEARCH_FOR_USER__TIME_PURCHASED));
			rfu.setComplete(rs.getBoolean(DBConstants.RESEARCH_FOR_USER__IS_COMPLETE));
			
			return rfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.RESEARCH_FOR_USER__ID);
				columnsSelected.add(DBConstants.RESEARCH_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.RESEARCH_FOR_USER__RESEARCH_ID);
				columnsSelected.add(DBConstants.RESEARCH_FOR_USER__TIME_PURCHASED);
				columnsSelected.add(DBConstants.RESEARCH_FOR_USER__IS_COMPLETE);
			}
			return columnsSelected;
		}
	} 	
}
