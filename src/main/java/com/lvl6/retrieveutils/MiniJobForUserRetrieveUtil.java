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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.MiniJobForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class MiniJobForUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(MiniJobForUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_MINI_JOB_FOR_USER; 
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	//CONTROLLER LOGIC******************************************************************
	
	//RETRIEVE QUERIES*********************************************************************
	public Map<Integer, MiniJobForUser> getSpecificOrAllMiniJobIdToMiniJobForUserId(
			int userId, Collection<Integer> miniJobIds) {
		Map<Integer, MiniJobForUser> miniJobIdToUserMiniJobs = null;
		try {
			List<String> columnsToSelected = UserMiniJobForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.MINI_JOB_FOR_USER__USER_ID, userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != miniJobIds && !miniJobIds.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions.put(DBConstants.MINI_JOB_FOR_USER__MINI_JOB_ID,
						miniJobIds);
			}
			String inDelim = getQueryConstructionUtil().getAnd(); 

			String overallDelim = getQueryConstructionUtil().getAnd();
			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityAndInConditions(
							columnsToSelected, TABLE_NAME, equalityConditions,
							eqDelim, inConditions, inDelim, overallDelim,
							values, preparedStatement);

			log.info("getSpecificOrAllMiniJobIdToMiniJobForUserId() query=" +
					query);

			List<MiniJobForUser> afuList = this.jdbcTemplate
					.query(query, new UserMiniJobForClientMapper());
			miniJobIdToUserMiniJobs =
					new HashMap<Integer, MiniJobForUser>();
			for (MiniJobForUser afu : afuList) {
				int miniJobId = afu.getMiniJobId();
				
				miniJobIdToUserMiniJobs.put(miniJobId, afu);
			}
		} catch (Exception e) {
			log.error("could not retrieve user obstacle for userId=" + userId, e);
			miniJobIdToUserMiniJobs =
					new HashMap<Integer, MiniJobForUser>();
		}
		
		return miniJobIdToUserMiniJobs;
	}
	
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMiniJobForClientMapper implements RowMapper<MiniJobForUser> {

		@Autowired
		protected QueryConstructionUtil queryConstructionUtil;
		
		private static List<String> columnsSelected;

		public MiniJobForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			MiniJobForUser mtfu = new MiniJobForUser();
			mtfu.setMiniJobId(rs.getInt(DBConstants.MINI_JOB_FOR_USER__MINI_JOB_ID));
			mtfu.setBaseDmgReceived(rs.getInt(DBConstants.MINI_JOB_FOR_USER__BASE_DMG_RECEIVED));
			
			try {
				Timestamp time = rs.getTimestamp(DBConstants.MINI_JOB_FOR_USER__TIME_STARTED);
				mtfu.setTimeStarted(new Date(time.getTime()));
			} catch (Exception e) {
				log.error("maybe mini job for user start time is invalid", e);
			}
			
			String stringToExplode = rs.getString(DBConstants.MINI_JOB_FOR_USER__USER_MONSTER_IDS); 
			List<Integer> userMonsterIds = getQueryConstructionUtil()
					.explodeIntoInts(stringToExplode, ",");
			mtfu.setUserMonsterIds(userMonsterIds);
			
			return mtfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__MINI_JOB_ID);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__BASE_DMG_RECEIVED);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__TIME_STARTED);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__USER_MONSTER_IDS);
			}
			return columnsSelected;
		}

		public QueryConstructionUtil getQueryConstructionUtil() {
			return queryConstructionUtil;
		}

		@SuppressWarnings("unused")
		public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
			this.queryConstructionUtil = queryConstructionUtil;
		}
		
	} 	
}
