package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

	//CONTROLLER LOGIC******************************************************************
	
	//RETRIEVE QUERIES*********************************************************************
	public ObstacleForUser getUserObstacleForId(int ofuId) {
		ObstacleForUser ofu = null;
		try {
			List<String> columnsToSelected = UserObstacleForClientMapper.getColumnsSelected();
			
			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.OBSTACLE_FOR_USER__ID, ofuId);
			String conditionDelimiter = getQueryConstructionUtil().getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
					columnsToSelected, TABLE_NAME, equalityConditions, conditionDelimiter,
					values, preparedStatement);

			log.info("query=" + query);

			ofu = this.jdbcTemplate.queryForObject(query, new UserObstacleForClientMapper());
		} catch (Exception e) {
			log.error("could not retrieve user obstacle for id=" + ofuId, e);
		}
		
		return ofu;
	}
	
	public List<ObstacleForUser> getUserObstacleForUser(int userId) {
		List<ObstacleForUser> ofuList = null;
		try {
			List<String> columnsToSelected = UserObstacleForClientMapper.getColumnsSelected();
			
			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.OBSTACLE_FOR_USER__USER_ID, userId);
			String conditionDelimiter = getQueryConstructionUtil().getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
					columnsToSelected, TABLE_NAME, equalityConditions, conditionDelimiter,
					values, preparedStatement);


			log.info("query=" + query);

			ofuList = this.jdbcTemplate.query(query, new UserObstacleForClientMapper());
		} catch (Exception e) {
			log.error("could not retrieve user obstacle for userId=" + userId, e);
			ofuList = new ArrayList<ObstacleForUser>();
		}
		
		return ofuList;
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
	private static final class UserObstacleForClientMapper implements RowMapper<ObstacleForUser> {

		private static List<String> columnsSelected;

		public ObstacleForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ObstacleForUser ofu = new ObstacleForUser();
			ofu.setId(rs.getInt(DBConstants.OBSTACLE_FOR_USER__ID));
			ofu.setUserId(rs.getInt(DBConstants.OBSTACLE_FOR_USER__USER_ID));
			ofu.setObstacleId(rs.getInt(DBConstants.OBSTACLE_FOR_USER__OBSTACLE_ID));
			ofu.setXcoord(rs.getInt(DBConstants.OBSTACLE_FOR_USER__XCOORD));
			ofu.setYcoord(rs.getInt(DBConstants.OBSTACLE_FOR_USER__YCOORD));
			try {
				Timestamp time = rs.getTimestamp(DBConstants.OBSTACLE_FOR_USER__REMOVAL_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					ofu.setRemovalTime(date);
				}
			} catch (Exception e) {
				log.error(String.format(
					"maybe obstacle removal time is invalid, ofu=%s", ofu), e);
			}
			String orientation = rs.getString(DBConstants.OBSTACLE_FOR_USER__ORIENTATION);
				
			if (null != orientation) {
		    	String newOrientation = orientation.trim().toUpperCase();
		    	if (!orientation.equals(newOrientation)) {
		    		log.error(String.format(
		    			"orientation incorrect: %s, ofu=%s",
		    			orientation, ofu));
		    		orientation = newOrientation;
		    	}
		    }
				
			ofu.setOrientation(orientation);
			
			return ofu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.OBSTACLE_FOR_USER__ID);
				columnsSelected.add(DBConstants.OBSTACLE_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.OBSTACLE_FOR_USER__OBSTACLE_ID);
				columnsSelected.add(DBConstants.OBSTACLE_FOR_USER__XCOORD);
				columnsSelected.add(DBConstants.OBSTACLE_FOR_USER__YCOORD);
				columnsSelected.add(DBConstants.OBSTACLE_FOR_USER__REMOVAL_TIME);
				columnsSelected.add(DBConstants.OBSTACLE_FOR_USER__ORIENTATION);
			}
			return columnsSelected;
		}
	} 	
}
