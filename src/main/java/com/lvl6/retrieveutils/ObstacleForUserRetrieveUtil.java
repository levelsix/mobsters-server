package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import com.lvl6.info.ObstacleForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ObstacleForUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ObstacleForUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_OBSTACLE_FOR_USER; 
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

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
	//mimics PvpHistoryProto in Battle.proto
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserObstacleForClientMapper implements RowMapper<ObstacleForUser> {

		public ObstacleForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ObstacleForUser ofu = new ObstacleForUser();
			ofu.setId(rs.getInt(DBConstants.OBSTACLE_FOR_USER__ID));
			ofu.setUserId(rs.getInt(DBConstants.OBSTACLE_FOR_USER__USER_ID));
			ofu.setObstacleId(rs.getInt(DBConstants.OBSTACLE_FOR_USER__OBSTACLE_ID));
			ofu.setXcoord(rs.getInt(DBConstants.OBSTACLE_FOR_USER__XCOORD));
			ofu.setYcoord(rs.getInt(DBConstants.OBSTACLE_FOR_USER__YCOORD));
			Timestamp time = rs.getTimestamp(DBConstants.OBSTACLE_FOR_USER__REMOVAL_TIME);
			ofu.setRemovalTime(time);
			ofu.setOrientation(rs.getString(DBConstants.OBSTACLE_FOR_USER__ORIENTATION));
			return ofu;
		}        
	} 

	//CONTROLLER LOGIC******************************************************************
	
	//RETRIEVE QUERIES*********************************************************************
	public ObstacleForUser getUserObstacleForId(int ofuId) {
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(DBConstants.OBSTACLE_FOR_USER__ID, ofuId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = null;
		boolean preparedStatement = false;
		
		String query = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement);
		
		log.info("query=" + query);
		
		ObstacleForUser ofu = this.jdbcTemplate.queryForObject(
				query, new UserObstacleForClientMapper());
		
		return ofu;
	}
	
}
