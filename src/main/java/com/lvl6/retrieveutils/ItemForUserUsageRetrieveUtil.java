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

import com.lvl6.info.ItemForUserUsage;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ItemForUserUsageRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ItemForUserUsageRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_ITEM_FOR_USER_USAGE; 
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
	public List<ItemForUserUsage> getItemForUserUsage(
	    String userId, Collection<Integer> itemIds) {
		List<ItemForUserUsage> itemUsages = null;
		try {
			List<String> columnsToSelected = UserItemUsageForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.ITEM_FOR_USER_USAGE__USER_ID, userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != itemIds && !itemIds.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions.put(DBConstants.ITEM_FOR_USER_USAGE__ITEM_ID,
						itemIds);
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

			log.info(String.format(
				"getSpecificOrAllItemIdToItemForUserUsageId() query=%s",
				query));

			itemUsages = this.jdbcTemplate
					.query(query, values.toArray(), new UserItemUsageForClientMapper());
		} catch (Exception e) {
			log.error(
				String.format(
					"could not retrieve UserItemUsage for userId=%s, itemIds=%s",
					userId, itemIds),
				e);
			itemUsages = new ArrayList<ItemForUserUsage>();
		}
		
		return itemUsages;
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
	private static final class UserItemUsageForClientMapper implements RowMapper<ItemForUserUsage> {

		private static List<String> columnsSelected;

		public ItemForUserUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
			ItemForUserUsage ifuu = new ItemForUserUsage();
			ifuu.setId(rs.getString(DBConstants.ITEM_FOR_USER_USAGE__ID));
			ifuu.setUserId(rs.getString(DBConstants.ITEM_FOR_USER_USAGE__USER_ID));
			ifuu.setItemId(rs.getInt(DBConstants.ITEM_FOR_USER_USAGE__ITEM_ID));
			
			Timestamp ts = rs.getTimestamp(DBConstants.ITEM_FOR_USER_USAGE__TIME_OF_ENTRY);
			ifuu.setTimeOfEntry(new Date(ts.getTime()));
			
			ifuu.setUserDataId(rs.getString(DBConstants.ITEM_FOR_USER_USAGE__USER_DATA_ID));
			
			String actionType = rs.getString(DBConstants.ITEM_FOR_USER_USAGE__ACTION_TYPE);
			if (null != actionType) {
		    	String newActionType = actionType.trim().toUpperCase();
		    	if (!actionType.equals(newActionType)) {
		    		log.error(String.format(
		    			"actionType incorrect: %s, ItemForUserUsage=%s",
		    			actionType, ifuu));
		    		actionType = newActionType;
		    	}
		    }
			ifuu.setActionType(actionType);
			
			return ifuu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.ITEM_FOR_USER_USAGE__ID);
				columnsSelected.add(DBConstants.ITEM_FOR_USER_USAGE__USER_ID);
				columnsSelected.add(DBConstants.ITEM_FOR_USER_USAGE__ITEM_ID);
				columnsSelected.add(DBConstants.ITEM_FOR_USER_USAGE__TIME_OF_ENTRY);
				columnsSelected.add(DBConstants.ITEM_FOR_USER_USAGE__USER_DATA_ID);
				columnsSelected.add(DBConstants.ITEM_FOR_USER_USAGE__ACTION_TYPE);
			}
			return columnsSelected;
		}
	} 	
}
