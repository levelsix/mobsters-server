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

import com.lvl6.info.ItemSecretGiftForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component
public class ItemSecretGiftForUserRetrieveUtil {
	private static Logger log = LoggerFactory
			.getLogger(ItemSecretGiftForUserRetrieveUtil.class);

	private static final String TABLE_NAME = DBConstants.TABLE_ITEM_SECRET_GIFT_FOR_USER;
	private static final UserItemSecretGiftForClientMapper rowMapper = new UserItemSecretGiftForClientMapper();
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
	public Map<String, ItemSecretGiftForUser> getSpecificOrAllItemSecretGiftForUserMap(
			String userId, Collection<String> ids) {
		Map<String, ItemSecretGiftForUser> idToUserItemSecretGifts = new HashMap<String, ItemSecretGiftForUser>();
		Collection<ItemSecretGiftForUser> secretGifts = getSpecificOrAllItemSecretGiftForUser(
				userId, ids);

		for (ItemSecretGiftForUser afu : secretGifts) {
			String id = afu.getId();

			idToUserItemSecretGifts.put(id, afu);
		}

		return idToUserItemSecretGifts;
	}

	public Collection<ItemSecretGiftForUser> getSpecificOrAllItemSecretGiftForUser(
			String userId, Collection<String> ids) {
		List<ItemSecretGiftForUser> secretGifts = null;
		try {
			List<String> columnsToSelected = UserItemSecretGiftForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(
					DBConstants.ITEM_SECRET_GIFT_FOR_USER__USER_ID, userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != ids && !ids.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions
						.put(DBConstants.ITEM_SECRET_GIFT_FOR_USER__ID, ids);
			}
			String inDelim = getQueryConstructionUtil().getAnd();

			String overallDelim = getQueryConstructionUtil().getAnd();
			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityAndInConditions(columnsToSelected,
							TABLE_NAME, equalityConditions, eqDelim,
							inConditions, inDelim, overallDelim, values,
							preparedStatement);

			log.info(String.format(
					"getSpecificOrAllItemSecretGiftForUser() query=%s", query));

			secretGifts = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error(
					String.format(
							"could not retrieve ItemSecretGiftForUser for userId=%s, ids=%s",
							userId, ids), e);
			secretGifts = new ArrayList<ItemSecretGiftForUser>();
		}

		return secretGifts;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(
			QueryConstructionUtil queryConstructionUtil) {
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
	private static final class UserItemSecretGiftForClientMapper implements
			RowMapper<ItemSecretGiftForUser> {

		private static List<String> columnsSelected;

		@Override
		public ItemSecretGiftForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ItemSecretGiftForUser isgfu = new ItemSecretGiftForUser();
			isgfu.setId(rs.getString(DBConstants.ITEM_SECRET_GIFT_FOR_USER__ID));
			isgfu.setUserId(rs
					.getString(DBConstants.ITEM_SECRET_GIFT_FOR_USER__USER_ID));
			isgfu.setItemId(rs
					.getInt(DBConstants.ITEM_SECRET_GIFT_FOR_USER__ITEM_ID));
			isgfu.setSecsTillCollection(rs
					.getInt(DBConstants.ITEM_SECRET_GIFT_FOR_USER__SECS_UNTIL_COLLECTION));

			Timestamp ts = rs
					.getTimestamp(DBConstants.ITEM_SECRET_GIFT_FOR_USER__CREATE_TIME);
			isgfu.setCreateTime(new Date(ts.getTime()));

			return isgfu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.ITEM_SECRET_GIFT_FOR_USER__ID);
				columnsSelected
						.add(DBConstants.ITEM_SECRET_GIFT_FOR_USER__USER_ID);
				columnsSelected
						.add(DBConstants.ITEM_SECRET_GIFT_FOR_USER__ITEM_ID);
				columnsSelected
						.add(DBConstants.ITEM_SECRET_GIFT_FOR_USER__SECS_UNTIL_COLLECTION);
				columnsSelected
						.add(DBConstants.ITEM_SECRET_GIFT_FOR_USER__CREATE_TIME);
			}
			return columnsSelected;
		}
	}
}