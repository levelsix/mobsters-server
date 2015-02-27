package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.BattleItemForUser;

import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class BattleItemForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_BATTLE_ITEM_FOR_USER;
	private static final UserBattleItemForClientMapper rowMapper = new UserBattleItemForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	public List<BattleItemForUser> getUserBattleItemsForUser(String userId) {
		log.debug(String.format(
				"retrieving user battle items for userId %s", userId));

		Object[] values = { userId };
		String query = String.format(
				"select * from %s where %s=?",
				TABLE_NAME, DBConstants.STRUCTURE_FOR_USER__USER_ID);

		List<BattleItemForUser> userBattleItems = null;
		try {
			userBattleItems = this.jdbcTemplate
					.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("battle item for user retrieve db error.", e);
			userBattleItems = new ArrayList<BattleItemForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userBattleItems;
	}

	////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
	public Map<Integer, List<BattleItemForUser>> getBattleItemIdsToUserBattleItemForUser(String userId) {
		log.debug("retrieving map of battle item id to userbattleitems for userId " + userId);

		Map<Integer, List<BattleItemForUser>> battleItemIdToBattleItemForUser =
				new HashMap<Integer, List<BattleItemForUser>>();
		try {

			List<BattleItemForUser> bifuList = getUserBattleItemsForUser(userId);

			for (BattleItemForUser bifu : bifuList) {
				int battleItemId = bifu.getBattleItemId();
				if (!battleItemIdToBattleItemForUser.containsKey(battleItemId)) {
					battleItemIdToBattleItemForUser.put(battleItemId, new ArrayList<BattleItemForUser>());
				}

				List<BattleItemForUser> userBattleItemsForBattleItemId = battleItemIdToBattleItemForUser.get(battleItemId);
				userBattleItemsForBattleItemId.add(bifu);
			}

		} catch (Exception e) {
			log.error(String.format(
					"battle item for user retrieve db error. userId=%s", userId), e);
		}
	
		return battleItemIdToBattleItemForUser;
	}

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public BattleItemForUser getSpecificUserBattleItem(String userBattleItemId) {
		log.debug(String.format(
				"retrieving user battle item with id %s", userBattleItemId));

		Object[] values = { userBattleItemId };
		String query = String.format(
				"select * from %s where %s=?",
				TABLE_NAME, DBConstants.BATTLE_ITEM_FOR_USER__ID);

		BattleItemForUser userBattleItem = null;
		try {
			List<BattleItemForUser> bifuList = this.jdbcTemplate
					.query(query, values, rowMapper);

			if (null != bifuList && !bifuList.isEmpty()) {
				userBattleItem = bifuList.get(0);
			}

		} catch (Exception e) {
			log.error(String.format(
					"battle item for user retrieve db error. id=%s", userBattleItemId), e);
		}

		return userBattleItem;
	}


	public List<BattleItemForUser> getSpecificOrAllUserBattleItemsForUser(String userId,
			List<String> userBattleItemIds) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME); 
		querySb.append(" WHERE ");
		querySb.append(DBConstants.STRUCTURE_FOR_USER__USER_ID);
		querySb.append("=?");
		List <Object> values = new ArrayList<Object>();
		values.add(userId);

		//if user didn't give any userStructIds then get all the user's structs
		//else get the specific ids
		if (userBattleItemIds != null && !userBattleItemIds.isEmpty() ) {
			log.debug(String.format("retrieving userBattleItems with ids %s", userBattleItemIds));
			querySb.append(" AND ");
			querySb.append(DBConstants.STRUCTURE_FOR_USER__ID);
			querySb.append(" IN (");

			int amount = userBattleItemIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userBattleItemIds);
		}

		String query = querySb.toString();
		log.info(String.format(
				"query=%s, values=%s",
				query, values));

		List<BattleItemForUser> userBattleItems = null;
		try {
			userBattleItems = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userBattleItems = new ArrayList<BattleItemForUser>();

		}
		
		return userBattleItems;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserBattleItemForClientMapper implements RowMapper<BattleItemForUser> {

		private static List<String> columnsSelected;

		public BattleItemForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			BattleItemForUser bifu = new BattleItemForUser();
			bifu.setId(rs.getString(DBConstants.BATTLE_ITEM_FOR_USER__ID));
			bifu.setUserId(rs.getString(DBConstants.BATTLE_ITEM_FOR_USER__USER_ID));
			bifu.setBattleItemId(rs.getInt(DBConstants.BATTLE_ITEM_FOR_USER__BATTLE_ITEM_ID));
			bifu.setQuantity(rs.getInt(DBConstants.BATTLE_ITEM_FOR_USER__QUANTITY));
			
			return bifu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__BATTLE_ITEM_ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__QUANTITY);
			}
			return columnsSelected;
		}
	} 	

}
