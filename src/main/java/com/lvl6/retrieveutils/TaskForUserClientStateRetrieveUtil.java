package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.TaskForUserClientState;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;
import com.lvl6.utils.utilmethods.StringUtils;

@Component 
public class TaskForUserClientStateRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(TaskForUserClientStateRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_TASK_FOR_USER_CLIENT_STATE; 
	private static final UserTaskClientStateForClientMapper rowMapper = new UserTaskClientStateForClientMapper();
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
	public TaskForUserClientState getTaskForUserClientState( String userId )
	{
		Object[] values = { userId };
		List<String> questions = Collections.nCopies(
			1, "?");
		String questionMarks = StringUtils.implode(questions, ",");
		
		String query = String.format(
			"select * from %s where %s in (%s)",
			TABLE_NAME,
			DBConstants.TASK_FOR_USER_CLIENT_STATE__USER_ID,
			questionMarks);
		
		TaskForUserClientState tfucs = null;
		log.info("getSpecificOrAllItemIdToItemForUserId() query={}", query);
		try {
			List<TaskForUserClientState> tfucsList = this.jdbcTemplate.query(query, values, rowMapper);
			
			if (null != tfucsList && !tfucsList.isEmpty())
			{
				tfucs = tfucsList.get(0);
			}
			
		} catch (Exception e) {
			log.error(
				String.format(
					"could not retrieve TaskForUserClientState for userId=%s",
					userId),
				e);
		}
		
		return tfucs;
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
	private static final class UserTaskClientStateForClientMapper implements RowMapper<TaskForUserClientState> {

		private static List<String> columnsSelected;

		public TaskForUserClientState mapRow(ResultSet rs, int rowNum) throws SQLException {
			TaskForUserClientState tfucs = new TaskForUserClientState();
			tfucs.setUserId(rs.getString(DBConstants.TASK_FOR_USER_CLIENT_STATE__USER_ID));
			tfucs.setClientState(rs.getBytes(DBConstants.TASK_FOR_USER_CLIENT_STATE__CLIENT_STATE));
			
			return tfucs;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.TASK_FOR_USER_CLIENT_STATE__USER_ID);
				columnsSelected.add(DBConstants.TASK_FOR_USER_CLIENT_STATE__CLIENT_STATE);
			}
			return columnsSelected;
		}
	} 	
}
