package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import com.lvl6.info.CoordinatePair;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class StructureForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_FOR_USER;
	private static final UserStructureForClientMapper rowMapper = new UserStructureForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	public List<StructureForUser> getUserStructsForUser(String userId) {
		log.debug(String.format(
				"retrieving user structs for userId %s", userId));

		Object[] values = { userId };
		String query = String.format(
				"select * from %s where %s=?",
				TABLE_NAME, DBConstants.STRUCTURE_FOR_USER__USER_ID);

		List<StructureForUser> userStructs = null;
		try {
			userStructs = this.jdbcTemplate
					.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userStructs = new ArrayList<StructureForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userStructs;
	}

	public List<StructureForUser> getMoneyTreeForUserList(String userId, String userStructId) {
		log.debug(String.format(
				"retrieving money tree for userId %s", userId));
		
		Map<Integer, StructureMoneyTree> moneyTreesMap = StructureMoneyTreeRetrieveUtils.getStructIdsToMoneyTrees();
		List<StructureForUser> userStructList = getUserStructsForUser(userId);
		List<StructureForUser> returnList = new ArrayList<StructureForUser>();

		if(userStructId == null) {
			for(StructureForUser sfu : userStructList) {
				int structId = sfu.getStructId();
				for(Integer id : moneyTreesMap.keySet()) {
					if(id == structId) {
						returnList.add(sfu);				
					}
				}
			}
			return returnList;
		}
		else {
			Object[] values2 = { userId, userStructId };
			String query2 = String.format(
					"select * from %s where %s=? and %s=?",
					TABLE_NAME, DBConstants.STRUCTURE_FOR_USER__USER_ID, 
					DBConstants.STRUCTURE_FOR_USER__ID);

			List<StructureForUser> userStructs2 = null;
			try {
				userStructs2 = this.jdbcTemplate
						.query(query2, values2, rowMapper);

			} catch (Exception e) {
				log.error("structure for user retrieve db error.", e);
				userStructs2 = new ArrayList<StructureForUser>();
			}
			return userStructs2;
		}
	}
	
	public Map<String, StructureForUser> getMoneyTreeForUserMap(String userId, String userStructId) {
		log.debug(String.format(
				"retrieving money tree for userId %s", userId));
		
		Map<Integer, StructureMoneyTree> moneyTreesMap = StructureMoneyTreeRetrieveUtils.getStructIdsToMoneyTrees();
		List<StructureForUser> userStructList = getUserStructsForUser(userId);
		Map<String, StructureForUser> returnMap = new HashMap<String, StructureForUser>();
		
		if(userStructId == null) {
			for(StructureForUser sfu : userStructList) {
				int structId = sfu.getStructId();
				for(Integer id : moneyTreesMap.keySet()) {
					if(id == structId) {
						returnMap.put(sfu.getId(), sfu);
					}
				}
			}
			return returnMap;
		}
		else {
			Object[] values2 = { userId, userStructId };
			String query2 = String.format(
					"select * from %s where %s=? and %s=?",
					TABLE_NAME, DBConstants.STRUCTURE_FOR_USER__USER_ID, 
					DBConstants.STRUCTURE_FOR_USER__ID);

			List<StructureForUser> userStructs2 = null;
			try {
				userStructs2 = this.jdbcTemplate
						.query(query2, values2, rowMapper);

			} catch (Exception e) {
				log.error("structure for user retrieve db error.", e);
				userStructs2 = new ArrayList<StructureForUser>();
			}
			
			for(StructureForUser sfu : userStructs2) {
				returnMap.put(sfu.getId(), sfu);
			}
			
			return returnMap;
		}
	}


	////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
	public Map<Integer, List<StructureForUser>> getStructIdsToUserStructsForUser(String userId) {
		log.debug("retrieving map of struct id to userstructs for userId " + userId);

		Map<Integer, List<StructureForUser>> structIdToUserStructs =
				new HashMap<Integer, List<StructureForUser>>();
		try {

			List<StructureForUser> pbfuList = getUserStructsForUser(userId);

			for (StructureForUser sfu : pbfuList) {
				int structId = sfu.getStructId();
				if (!structIdToUserStructs.containsKey(structId)) {
					structIdToUserStructs.put(structId, new ArrayList<StructureForUser>());
				}

				List<StructureForUser> userStructsForStructId = structIdToUserStructs.get(structId);
				userStructsForStructId.add(sfu);
			}

		} catch (Exception e) {
			log.error(String.format(
					"structure for user retrieve db error. userId=%s", userId), e);

			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return structIdToUserStructs;
	}

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public StructureForUser getSpecificUserStruct(String userStructId) {
		log.debug(String.format(
				"retrieving user struct with id %s", userStructId));

		Object[] values = { userStructId };
		String query = String.format(
				"select * from %s where %s=?",
				TABLE_NAME, DBConstants.STRUCTURE_FOR_USER__ID);

		StructureForUser userStruct = null;
		try {
			List<StructureForUser> sfuList = this.jdbcTemplate
					.query(query, values, rowMapper);

			if (null != sfuList && !sfuList.isEmpty()) {
				userStruct = sfuList.get(0);
			}

		} catch (Exception e) {
			log.error(String.format(
					"structure for user retrieve db error. id=%s", userStructId), e);

			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userStruct;
	}


	public List<StructureForUser> getSpecificOrAllUserStructsForUser(String userId,
			List<String> userStructIds) {

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
		if (userStructIds != null && !userStructIds.isEmpty() ) {
			log.debug(String.format("retrieving userStructs with ids %s", userStructIds));
			querySb.append(" AND ");
			querySb.append(DBConstants.STRUCTURE_FOR_USER__ID);
			querySb.append(" IN (");

			int amount = userStructIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userStructIds);
		}

		String query = querySb.toString();
		log.info(String.format(
				"query=%s, values=%s",
				query, values));

		List<StructureForUser> userStructs = null;
		try {
			userStructs = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userStructs = new ArrayList<StructureForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userStructs;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserStructureForClientMapper implements RowMapper<StructureForUser> {

		private static List<String> columnsSelected;

		public StructureForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			StructureForUser sfu = new StructureForUser();
			sfu.setId(rs.getString(DBConstants.STRUCTURE_FOR_USER__ID));
			sfu.setUserId(rs.getString(DBConstants.STRUCTURE_FOR_USER__USER_ID));
			sfu.setStructId(rs.getInt(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID));

			try {
				Timestamp time = rs.getTimestamp(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					sfu.setLastRetrieved(date);
				}
			} catch (Exception e) {
				log.error(String.format(
						"maybe last retrieved time is invalid, sfu=%s", sfu), e);
			}

			CoordinatePair coordinates = new CoordinatePair(rs.getInt(DBConstants.STRUCTURE_FOR_USER__X_COORD), rs.getInt(DBConstants.STRUCTURE_FOR_USER__Y_COORD));
			sfu.setCoordinates(coordinates);

			try {
				Timestamp time = rs.getTimestamp(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					sfu.setPurchaseTime(date);
				}
			} catch (Exception e) {
				log.error(String.format(
						"maybe last retrieved time is invalid, sfu=%s", sfu), e);
			}

			sfu.setComplete(rs.getBoolean(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE));

			String orientation = rs.getString(DBConstants.STRUCTURE_FOR_USER__ORIENTATION);
			if (null != orientation) {
				String newOrientation = orientation.trim().toUpperCase();
				if (!orientation.equals(newOrientation)) {
					log.error(String.format(
							"orientation incorrect: %s, id=%s",
							orientation, sfu.getId()));
					orientation = newOrientation;
				}
			}
			sfu.setOrientation(orientation);

			sfu.setFbInviteStructLvl(rs.getInt(DBConstants.STRUCTURE_FOR_USER__FB_INVITE_STRUCT_LVL));

			return sfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__ID);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__X_COORD);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__Y_COORD);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__ORIENTATION);
				columnsSelected.add(DBConstants.STRUCTURE_FOR_USER__FB_INVITE_STRUCT_LVL);
			}
			return columnsSelected;
		}
	} 	

}
