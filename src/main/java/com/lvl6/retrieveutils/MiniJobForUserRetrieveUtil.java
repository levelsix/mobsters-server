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
import com.lvl6.utils.utilmethods.StringUtils;

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
	public Map<Long, MiniJobForUser> getSpecificOrAllIdToMiniJobForUser(
			int userId, Collection<Long> userMiniJobIds) {
		Map<Long, MiniJobForUser> miniJobIdToUserMiniJobs = null;
		try {
			List<String> columnsToSelected = UserMiniJobForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.MINI_JOB_FOR_USER__USER_ID, userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != userMiniJobIds && !userMiniJobIds.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions.put(DBConstants.MINI_JOB_FOR_USER__ID,
						userMiniJobIds);
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

			List<MiniJobForUser> mjfuList = this.jdbcTemplate
					.query(query, new UserMiniJobForClientMapper());
			miniJobIdToUserMiniJobs =
					new HashMap<Long, MiniJobForUser>();
			for (MiniJobForUser mjfu : mjfuList) {
				long miniJobId = mjfu.getId();
				
				miniJobIdToUserMiniJobs.put(miniJobId, mjfu);
			}
		} catch (Exception e) {
			log.error("could not retrieve user obstacle for userId=" + userId, e);
			miniJobIdToUserMiniJobs =
					new HashMap<Long, MiniJobForUser>();
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

		private static List<String> columnsSelected;

		public MiniJobForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			MiniJobForUser mjfu = new MiniJobForUser();
			mjfu.setId(rs.getLong(DBConstants.MINI_JOB_FOR_USER__ID));
			mjfu.setMiniJobId(rs.getInt(DBConstants.MINI_JOB_FOR_USER__MINI_JOB_ID));
			mjfu.setBaseDmgReceived(rs.getInt(DBConstants.MINI_JOB_FOR_USER__BASE_DMG_RECEIVED));
			mjfu.setDurationMinutes(rs.getInt(DBConstants.MINI_JOB_FOR_USER__DURATION_MINUTES));
			
			try {
				Timestamp time = rs.getTimestamp(DBConstants.MINI_JOB_FOR_USER__TIME_STARTED);
				if (!rs.wasNull()) {
					mjfu.setTimeStarted(new Date(time.getTime()));
				}
			} catch (Exception e) {
				log.error("maybe MiniJobForUser start time is invalid", e);
			}
			
			try {
				String stringToExplode = rs.getString(DBConstants.MINI_JOB_FOR_USER__USER_MONSTER_IDS); 
				if (null != stringToExplode) {
					List<Long> userMonsterIds = StringUtils
							.explodeIntoLongs(stringToExplode, ",");
					mjfu.setUserMonsterIds(userMonsterIds);
					mjfu.setUserMonsterIdStr(stringToExplode);
				}
			} catch (Exception e) {
				log.error("maybe MiniJobForUser user monster ids are invalid", e);
			}
			
			try {
				Timestamp time = rs.getTimestamp(DBConstants.MINI_JOB_FOR_USER__TIME_COMPLETED);
				if (!rs.wasNull()) {
					mjfu.setTimeCompleted(new Date(time.getTime()));
				}
			} catch (Exception e) {
				log.error("maybe MiniJobForUser completed time is invalid", e);
			}
			
			return mjfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__ID);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__MINI_JOB_ID);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__BASE_DMG_RECEIVED);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__DURATION_MINUTES);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__TIME_STARTED);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__USER_MONSTER_IDS);
				columnsSelected.add(DBConstants.MINI_JOB_FOR_USER__TIME_COMPLETED);
			}
			return columnsSelected;
		}

	} 	
}
