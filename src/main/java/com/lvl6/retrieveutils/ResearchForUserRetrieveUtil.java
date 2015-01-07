package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import com.lvl6.info.ResearchForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ResearchForUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ResearchForUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_RESEARCH_FOR_USER; 
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
	public Map<Integer, ResearchForUser> getSpecificOrAllResearchForUserMap(
	    String userId, Collection<Integer> researchIds)
    {
		Map<Integer, ResearchForUser> researchIdToUserResearchs =
			new HashMap<Integer, ResearchForUser>();
		
		List<ResearchForUser> ifuList = getSpecificOrAllResearchForUser(userId, researchIds);
		
		for (ResearchForUser afu : ifuList) {
			int researchId = afu.getResearchId();

			researchIdToUserResearchs.put(researchId, afu);
		}
		
		return researchIdToUserResearchs;
	}
	
	public List<ResearchForUser> getSpecificOrAllResearchForUser(
	    String userId, Collection<Integer> researchIds)
	{
		List<ResearchForUser> userResearchs = null;
		try {
			List<String> columnsToSelected = UserResearchForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.RESEARCH_FOR_USER__USER_ID, userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != researchIds && !researchIds.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions.put(DBConstants.RESEARCH_FOR_USER__RESEARCH_ID,
						researchIds);
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

			log.info("getSpecificOrAllResearchIdToResearchForUserId() query=" +
					query);

			userResearchs = this.jdbcTemplate
					.query(query, values.toArray(), new UserResearchForClientMapper());
			
		} catch (Exception e) {
			log.error(
				String.format(
					"could not retrieve user research for userId=%s, researchIds=%s",
					userId, researchIds),
				e);
			userResearchs = new ArrayList<ResearchForUser>();
		}
		
		return userResearchs;
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
	private static final class UserResearchForClientMapper implements RowMapper<ResearchForUser> {

		private static List<String> columnsSelected;

		public ResearchForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ResearchForUser rfu = new ResearchForUser();
			rfu.setId(rs.getString(DBConstants.RESEARCH_FOR_USER__ID));
			rfu.setUserId(rs.getString(DBConstants.RESEARCH_FOR_USER__USER_ID));
			rfu.setResearchId(rs.getInt(DBConstants.RESEARCH_FOR_USER__RESEARCH_ID));
			
			try {
				Timestamp time = rs.getTimestamp(DBConstants.TASK_FOR_USER_ONGOING__START_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					rfu.setTimePurchased(date);
				}
			} catch (Exception e) {
				log.error(String.format(
					"maybe timePurchased is invalid, rfu=%s", rfu), e);
			}
			
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
