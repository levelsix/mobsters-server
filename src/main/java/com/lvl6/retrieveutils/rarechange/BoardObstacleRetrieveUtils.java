package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.BoardObstacle;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class BoardObstacleRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, BoardObstacle> idsToBoardObstacles;

	private static final String TABLE_NAME = DBConstants.TABLE_BOARD_OBSTACLE_CONFIG;

	public static Map<Integer, BoardObstacle> getIdsToBoardObstacles() {
		log.debug("retrieving all BoardObstacles data map");
		if (null == idsToBoardObstacles) {
			setStaticIdsToBoardObstacles();
		}
		return idsToBoardObstacles;
	}

	public static BoardObstacle getBoardObstacleForId(int boardObstacleId) {
		log.debug(String.format(
				"retrieve boardObstacle data for boardObstacle=%s", boardObstacleId));
		if (null == idsToBoardObstacles) {
			setStaticIdsToBoardObstacles();
		}
		return idsToBoardObstacles.get(boardObstacleId);
	}

	private static void setStaticIdsToBoardObstacles() {
		log.debug("setting static map of boardObstacleIds to boardObstacles");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, BoardObstacle> idsToBoardObstaclesTemp =
								new HashMap<Integer, BoardObstacle>();
						//loop through each row and convert it into a java object
						while(rs.next()) {  
							BoardObstacle boardObstacle = convertRSRowToBoardObstacle(rs);
							if (boardObstacle == null) {
								continue;
							}

							int boardObstacleId = boardObstacle.getId();
							idsToBoardObstaclesTemp.put(boardObstacleId, boardObstacle);
						}
						idsToBoardObstacles = idsToBoardObstaclesTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}    
			}
		} catch (Exception e) {
			log.error("boardObstacle retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticIdsToBoardObstacles();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static BoardObstacle convertRSRowToBoardObstacle(ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.BOARD_OBSTACLE__ID);
		String name = rs.getString(DBConstants.BOARD_OBSTACLE__NAME);
		String type = rs.getString(DBConstants.BOARD_OBSTACLE__TYPE);
		
		if (null != type) {
			String newType = type.trim().toUpperCase();
			if (!type.equals(newType)) {
				log.error("type has spaces.{}, id={}", type, id);
				type = newType;
			}
		}
		
		int powerAmt = rs.getInt(DBConstants.BOARD_OBSTACLE__POWER_AMT);
		boolean initAvailable = rs.getBoolean(DBConstants.BOARD_OBSTACLE__INIT_AVAILABLE);

		BoardObstacle boardObstacle = new BoardObstacle(id, name, type, powerAmt, initAvailable);
		return boardObstacle;
	}

}
