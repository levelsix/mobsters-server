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

import com.lvl6.info.Board;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class BoardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, Board> idsToBoards;

	private static final String TABLE_NAME = DBConstants.TABLE_BOARD_CONFIG;

	public static Map<Integer, Board> getIdsToBoards() {
		log.debug("retrieving all Boards data map");
		if (null == idsToBoards) {
			setStaticIdsToBoards();
		}
		return idsToBoards;
	}

	public static Board getBoardForId(int boardId) {
		log.debug(String.format(
			"retrieve board data for board=%s", boardId));
		if (null == idsToBoards) {
			setStaticIdsToBoards();
		}
		return idsToBoards.get(boardId);
	}

	private static void setStaticIdsToBoards() {
		log.debug("setting static map of boardIds to boards");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Board> idsToBoardsTemp =
							new HashMap<Integer, Board>();
						//loop through each row and convert it into a java object
						while(rs.next()) {  
							Board board = convertRSRowToBoard(rs);
							if (board == null) {
								continue;
							}

							int boardId = board.getId();
							idsToBoardsTemp.put(boardId, board);
						}
						idsToBoards = idsToBoardsTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}    
			}
		} catch (Exception e) {
			log.error("board retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticIdsToBoards();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static Board convertRSRowToBoard(ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.BOARD__ID);
		int width = rs.getInt(DBConstants.BOARD__WIDTH);
		int height = rs.getInt(DBConstants.BOARD__HEIGHT);
		String orbElements = rs.getString(DBConstants.BOARD__ORB_ELEMENTS);
		
		
		if (null != orbElements) {
	    	String newOrbElements = orbElements.trim();
	    	if (!orbElements.equals(newOrbElements)) {
	    		log.error(String.format(
	    			"orbElements has spaces.%s, id=%s",
	    			orbElements, id));
	    		orbElements = newOrbElements;
	    	}
	    } else {
	    	log.error("elements is not set boardId={}", id);
	    	return null;
	    }
		
		//make sure orbElements has only 0s and 1s
		String orbElementsSansOnes = orbElements.replace("1", "");
		String orbElementsEmpty = orbElementsSansOnes.replace("0", "");
		
		if (!orbElementsEmpty.isEmpty()) {
			log.error(String.format(
				"ORB_ELEMENTS INCORRECT: %s, ID=%s",
				orbElements, id));
			return null;
	    }
		
		int orbElementsInt = 0;
		try {
			orbElementsInt = Integer.parseInt(orbElements);
			
		} catch (NumberFormatException e) {
			log.error(String.format(
				"ORB_ELEMENTS INCORRECT: %s, ID=%s",
				orbElements, id),
				e);
		}
		
		Board board = new Board(id, width, height, orbElementsInt);
		return board;
	}
	
}
