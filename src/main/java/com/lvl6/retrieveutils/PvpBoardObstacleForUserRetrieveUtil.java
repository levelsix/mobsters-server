package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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

import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class PvpBoardObstacleForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_PVP_BOARD_OBSTACLE_FOR_USER;
	private static final UserBoardObstacleForClientMapper rowMapper = new UserBoardObstacleForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<PvpBoardObstacleForUser> getPvpBoardObstacleForUserId(String userId) {
		Object[] values = { userId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__USER_ID);
		
		List<PvpBoardObstacleForUser> userPersistentEvents = null;//new ArrayList<PvpBoardObstacleForUser>();
		try {
			userPersistentEvents = this.jdbcTemplate
				.query(query, values, rowMapper);
			
		} catch (Exception e) {
			log.error("PvpBoardObstacleForUser retrieve db error.", e);
			userPersistentEvents = new ArrayList<PvpBoardObstacleForUser>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userPersistentEvents;
	}

	public Map<String, List<PvpBoardObstacleForUser>> getPvpBoardObstacleForUserIds(
			Collection<String> userIds)
	{
		Object[] values = userIds.toArray();
		int amount = userIds.size();
		List<String> questionMarks = Collections.nCopies(amount, "?");
		String questionMarksStr = StringUtils.csvList(questionMarks);
		
		String query = String.format(
			"select * from %s where %s in (%s)",
			TABLE_NAME, DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__USER_ID,
			questionMarksStr);

		log.info("query={}, values={}", query, values);
		
		Map<String, List<PvpBoardObstacleForUser>> userIdToPvpBoardObstacles =
				new HashMap<String, List<PvpBoardObstacleForUser>>();
		try {
			List<PvpBoardObstacleForUser> userPvpBoardObstacles = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			for (PvpBoardObstacleForUser pbofu : userPvpBoardObstacles)
			{
				String userId = pbofu.getUserId();
				
				//base case when initially populating for a user
				if (!userIdToPvpBoardObstacles.containsKey(userId)) {
					userIdToPvpBoardObstacles.put(userId,
							new ArrayList<PvpBoardObstacleForUser>());
				}
				List<PvpBoardObstacleForUser> obstacles = userIdToPvpBoardObstacles
						.get(userId);
				obstacles.add(pbofu);
			}
		} catch (Exception e) {
			log.error("PvpBoardObstacleForUser retrieve db error.", e);
		}
		return userIdToPvpBoardObstacles;
	}
	
	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserBoardObstacleForClientMapper implements RowMapper<PvpBoardObstacleForUser> {

		private static List<String> columnsSelected;

		public PvpBoardObstacleForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			PvpBoardObstacleForUser pbofu = new PvpBoardObstacleForUser();
			pbofu.setId(rs.getInt(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__ID));
			pbofu.setUserId(rs.getString(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__USER_ID));
			pbofu.setObstacleId(rs.getInt(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__OBSTACLE_ID));
			pbofu.setPosX(rs.getInt(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__POS_X));
			pbofu.setPosY(rs.getInt(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__POS_Y));
			
			return pbofu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__ID);
				columnsSelected.add(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__OBSTACLE_ID);
				columnsSelected.add(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__POS_X);
				columnsSelected.add(DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__POS_Y);
			}
			return columnsSelected;
		}
	} 	

}
