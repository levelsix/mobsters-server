package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import com.lvl6.info.AchievementForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class AchievementForUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(AchievementForUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_ACHIEVEMENT_FOR_USER; 
	private static final UserAchievementForClientMapper rowMapper = new UserAchievementForClientMapper();
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
	public Map<Integer, AchievementForUser> getSpecificOrAllAchievementIdToAchievementForUserId(
		String userId, Collection<Integer> achievementIds)
	{
		Map<Integer, AchievementForUser> achievementIdToUserAchievements = null;
		try {
			List<String> columnsToSelected = UserAchievementForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.ACHIEVEMENT_FOR_USER__USER_ID, userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != achievementIds && !achievementIds.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions.put(DBConstants.ACHIEVEMENT_FOR_USER__ACHIEVEMENT_ID,
						achievementIds);
			}
			String inDelim = getQueryConstructionUtil().getAnd(); 

			String overallDelim = getQueryConstructionUtil().getAnd();
			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityAndInConditions(
							columnsToSelected, TABLE_NAME, equalityConditions,
							eqDelim, inConditions, inDelim, overallDelim,
							values, preparedStatement);

			log.info("getSpecificOrAllAchievementIdToAchievementForUserId() query=" +
					query);

			List<AchievementForUser> afuList = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);
			achievementIdToUserAchievements =
					new HashMap<Integer, AchievementForUser>();
			for (AchievementForUser afu : afuList) {
				int achievementId = afu.getAchievementId();
				
				achievementIdToUserAchievements.put(achievementId, afu);
			}
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve user achievement for userId=%s", userId), e);
			achievementIdToUserAchievements =
					new HashMap<Integer, AchievementForUser>();
		}
		
		return achievementIdToUserAchievements;
	}
	
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	//Date twenty4ago = new DateTime().minusDays(1).toDate();
	protected String formatDateToString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String formatted = format.format(date);
		return formatted;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserAchievementForClientMapper implements RowMapper<AchievementForUser> {

		private static List<String> columnsSelected;

		public AchievementForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			AchievementForUser afu = new AchievementForUser();
			afu.setAchievementId(rs.getInt(DBConstants.ACHIEVEMENT_FOR_USER__ACHIEVEMENT_ID));
			afu.setProgress(rs.getInt(DBConstants.ACHIEVEMENT_FOR_USER__PROGRESS));
			afu.setComplete(rs.getBoolean(DBConstants.ACHIEVEMENT_FOR_USER__IS_COMPLETE));
			afu.setRedeemed(rs.getBoolean(DBConstants.ACHIEVEMENT_FOR_USER__IS_REDEEMED));
			return afu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.ACHIEVEMENT_FOR_USER__ACHIEVEMENT_ID);
				columnsSelected.add(DBConstants.ACHIEVEMENT_FOR_USER__PROGRESS);
				columnsSelected.add(DBConstants.ACHIEVEMENT_FOR_USER__IS_COMPLETE);
				columnsSelected.add(DBConstants.ACHIEVEMENT_FOR_USER__IS_REDEEMED);
			}
			return columnsSelected;
		}
	} 	
}
