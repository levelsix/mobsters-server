package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ExpansionPurchaseForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ExpansionPurchaseForUserRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_EXPANSION_PURCHASE_FOR_USER;



	public static List<ExpansionPurchaseForUser> getUserCityExpansionDatasForUserId(int userId) {
		Connection conn = null;
		ResultSet rs = null;
		List<ExpansionPurchaseForUser> userCityExpansionDatas = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
			userCityExpansionDatas = grabUserCityExpansionDatasFromRS(rs);
		} catch (Exception e) {
    	log.error("expansion purchase for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return userCityExpansionDatas;
	}
	
	public static ExpansionPurchaseForUser getSpecificUserCityExpansionDataForUserIdAndPosition(int userId, int xPosition, int yPosition) {
		Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
		absoluteConditionParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__USER_ID, userId);
		absoluteConditionParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__X_POSITION, xPosition);
		absoluteConditionParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__Y_POSITION, yPosition);
		
		Connection conn = null;
		ResultSet rs = null;
		List<ExpansionPurchaseForUser> userCityExpansionDatas = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn,
					absoluteConditionParams, TABLE_NAME);
			userCityExpansionDatas = grabUserCityExpansionDatasFromRS(rs);
		} catch (Exception e) {
    	log.error("expansion purchase for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		
		log.info("userCityExpansionDatas=" + userCityExpansionDatas);
		
		if (null != userCityExpansionDatas && !userCityExpansionDatas.isEmpty()) {
			return userCityExpansionDatas.get(0);
		} else {
			return null;
		}
	}

	public static Integer numberOfUserExpansions(int userId){
		List<Object> params = new ArrayList<Object>();
		params.add(userId);
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, "select count(*) from "+TABLE_NAME+" where user_id = ?;", params) ;
		try {
			if(rs != null) {
				Integer count;
				try {
					if(rs.first()) {
						count = rs.getInt(1);
						return count;
					}
				} catch (SQLException e) {

				}
			}
		} catch (Exception e) {
    	log.error("expansion purchase for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		log.warn("No user expanions found when counting for user id="+userId);
		return 0;
	}

	private static List<ExpansionPurchaseForUser> grabUserCityExpansionDatasFromRS(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				List<ExpansionPurchaseForUser> userCityExpansionDatas = new ArrayList<ExpansionPurchaseForUser>();
				while(rs.next()) {
					ExpansionPurchaseForUser uc = convertRSRowToUserCityExpansionData(rs);
					userCityExpansionDatas.add(uc);
				}
				return userCityExpansionDatas;
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return null;
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static ExpansionPurchaseForUser convertRSRowToUserCityExpansionData(ResultSet rs) throws SQLException {
		int i = 1;
		int userId = rs.getInt(i++);
		int xPosition = rs.getInt(i++);
		int yPosition = rs.getInt(i++);
		boolean isExpanding = rs.getBoolean(i++);

		Date expandStartTime = null;
		Timestamp ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			expandStartTime = new Date(ts.getTime());
		}

		return new ExpansionPurchaseForUser(userId, xPosition, yPosition, isExpanding, expandStartTime);
	}

}
