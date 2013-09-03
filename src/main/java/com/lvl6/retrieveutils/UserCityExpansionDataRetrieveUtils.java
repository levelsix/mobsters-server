package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserCityExpansionData;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class UserCityExpansionDataRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_USER_CITY_EXPANSION_DATA;



	public static List<UserCityExpansionData> getUserCityExpansionDatasForUserId(int userId) {

		/*Connection conn = DBConnection.get().getConnection();
		List<String> columns = null; //all columns
		Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
		Map<String, Object> relativeGreaterThanConditionParams =
				new HashMap<String, Object>();
		Map<String, Object> relativeLessThanConditionParams = null;
		Map<String, Object> likeCondParams = null;
		String tablename = TABLE_NAME;
		String conddelim = "AND";
		String orderByColumn = null;
		boolean orderByAsc = false;
		int limit = ControllerConstants.NOT_SET;
		boolean random = false;

		absoluteConditionParams.put(DBConstants.USER_CITY_EXPANSION_DATA__USER_ID, userId);

		ResultSet rs = DBConnection.get().selectRows(conn, columns,
				absoluteConditionParams, relativeGreaterThanConditionParams,
				relativeLessThanConditionParams, likeCondParams,
				tablename, conddelim, orderByColumn, orderByAsc, limit, random); */
		
		Connection conn = DBConnection.get().getConnection();
	    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
		List<UserCityExpansionData> userCityExpansionDatas = grabUserCityExpansionDatasFromRS(rs);
		DBConnection.get().close(rs, null, conn);
		return userCityExpansionDatas;
	}
	
	public static UserCityExpansionData getSpecificUserCityExpansionDataForUserIdAndPosition(int userId, int xPosition, int yPosition) {
		List<UserCityExpansionData> ucedList = getUserCityExpansionDatasForUserId(userId);
		for(UserCityExpansionData uced : ucedList) {
			if(uced.getxPosition() == xPosition && uced.getyPosition() == yPosition) 
				return uced;
		}
		return null;
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
		}catch(Exception e) {

		}finally {
			DBConnection.get().close(null, null, conn);
		}
		log.warn("No user expanions found when counting for user id="+userId);
		return 0;
	}

	private static List<UserCityExpansionData> grabUserCityExpansionDatasFromRS(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				List<UserCityExpansionData> userCityExpansionDatas = new ArrayList<UserCityExpansionData>();
				while(rs.next()) {
					UserCityExpansionData uc = convertRSRowToUserCityExpansionData(rs);
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
	private static UserCityExpansionData convertRSRowToUserCityExpansionData(ResultSet rs) throws SQLException {
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

		return new UserCityExpansionData(userId, xPosition, yPosition, isExpanding, expandStartTime);
	}

}
