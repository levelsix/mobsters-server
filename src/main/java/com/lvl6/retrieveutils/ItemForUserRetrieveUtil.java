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

import com.lvl6.info.ItemForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ItemForUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ItemForUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_ITEM_FOR_USER; 
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
	public Map<Integer, ItemForUser> getSpecificOrAllItemForUserMap(
	    String userId, Collection<Integer> itemIds)
    {
		Map<Integer, ItemForUser> itemIdToUserItems =
			new HashMap<Integer, ItemForUser>();
		
		List<ItemForUser> ifuList = getSpecificOrAllItemForUser(userId, itemIds);
		
		for (ItemForUser afu : ifuList) {
			int itemId = afu.getItemId();

			itemIdToUserItems.put(itemId, afu);
		}
		
		return itemIdToUserItems;
	}
	
	public List<ItemForUser> getSpecificOrAllItemForUser(
	    String userId, Collection<Integer> itemIds)
	{
		List<ItemForUser> userItems = null;
		try {
			List<String> columnsToSelected = UserItemForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.ITEM_FOR_USER__USER_ID, userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != itemIds && !itemIds.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions.put(DBConstants.ITEM_FOR_USER__ITEM_ID,
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

			log.info("getSpecificOrAllItemIdToItemForUserId() query=" +
					query);

			userItems = this.jdbcTemplate
					.query(query, values.toArray(), new UserItemForClientMapper());
			
		} catch (Exception e) {
			log.error(
				String.format(
					"could not retrieve user item for userId=%s, itemIds=%s",
					userId, itemIds),
				e);
			userItems = new ArrayList<ItemForUser>();
		}
		
		return userItems;
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
	private static final class UserItemForClientMapper implements RowMapper<ItemForUser> {

		private static List<String> columnsSelected;

		public ItemForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ItemForUser ifu = new ItemForUser();
			ifu.setUserId(rs.getString(DBConstants.ITEM_FOR_USER__USER_ID));
			ifu.setItemId(rs.getInt(DBConstants.ITEM_FOR_USER__ITEM_ID));
			ifu.setQuantity(rs.getInt(DBConstants.ITEM_FOR_USER__QUANTITY));
			return ifu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.ITEM_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.ITEM_FOR_USER__ITEM_ID);
				columnsSelected.add(DBConstants.ITEM_FOR_USER__QUANTITY);
			}
			return columnsSelected;
		}
	} 	
}
