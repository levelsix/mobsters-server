package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.BoardProperty;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class BoardPropertyRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, BoardProperty> idsToBoardProperties;
	private static Map<Integer, Collection<BoardProperty>> boardIdsToProperties;

	private static final String TABLE_NAME = DBConstants.TABLE_BOARD_PROPERTY_CONFIG;

	public Map<Integer, BoardProperty> getIdsToBoardProperties() {
		log.debug("retrieving all BoardProperties data map");
		if (null == idsToBoardProperties) {
			setStaticIdsToBoardProperties();
		}
		return idsToBoardProperties;
	}

	public Map<Integer, Collection<BoardProperty>> getBoardIdsToProperties() {
		log.debug("retrieving all boardIdsToBoardProperties data map");
		if (null == boardIdsToProperties) {
			setStaticIdsToBoardProperties();
		}
		return boardIdsToProperties;
	}

	public BoardProperty getBoardPropertyForId(int boardPropertyId) {
		log.debug(String.format(
				"retrieve boardProperty data for boardProperty=%s",
				boardPropertyId));
		if (null == idsToBoardProperties) {
			setStaticIdsToBoardProperties();
		}

		if (!idsToBoardProperties.containsKey(boardPropertyId)) {
			log.error("no property for boardPropertyId={}", boardPropertyId);
		}
		return idsToBoardProperties.get(boardPropertyId);
	}

	public Collection<BoardProperty> getPropertiesForBoardId(int boardId) {
		log.debug(String.format("retrieve boardProperty data for boardId=%s",
				boardId));

		if (null == boardIdsToProperties) {
			setStaticIdsToBoardProperties();
		}

		if (!boardIdsToProperties.containsKey(boardId)) {
			log.error("no properties for boardId={}", boardId);
		}

		return boardIdsToProperties.get(boardId);
	}

	private void setStaticIdsToBoardProperties() {
		log.debug("setting static map of boardPropertyIds to boardProperties");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, BoardProperty> idsToBoardPropertiesTemp = new HashMap<Integer, BoardProperty>();

						Map<Integer, Collection<BoardProperty>> boardIdsToBoardPropertiesTemp = new HashMap<Integer, Collection<BoardProperty>>();

						//loop through each row and convert it into a java object
						while (rs.next()) {
							BoardProperty boardProperty = convertRSRowToBoardProperty(rs);
							if (boardProperty == null) {
								continue;
							}

							int boardPropertyId = boardProperty.getId();
							idsToBoardPropertiesTemp.put(boardPropertyId,
									boardProperty);

							int boardId = boardProperty.getBoardId();
							if (!boardIdsToBoardPropertiesTemp
									.containsKey(boardId)) {
								//base case
								boardIdsToBoardPropertiesTemp.put(boardId,
										new ArrayList<BoardProperty>());
							}

							Collection<BoardProperty> bpCollection = boardIdsToBoardPropertiesTemp
									.get(boardId);
							bpCollection.add(boardProperty);
						}

						idsToBoardProperties = idsToBoardPropertiesTemp;
						boardIdsToProperties = boardIdsToBoardPropertiesTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("boardProperty retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticIdsToBoardProperties();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private BoardProperty convertRSRowToBoardProperty(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.BOARD_PROPERTY__ID);
		int boardId = rs.getInt(DBConstants.BOARD_PROPERTY__BOARD_ID);
		String name = rs.getString(DBConstants.BOARD_PROPERTY__NAME);
		int posX = rs.getInt(DBConstants.BOARD_PROPERTY__POS_X);
		int posY = rs.getInt(DBConstants.BOARD_PROPERTY__POS_Y);
		String element = rs.getString(DBConstants.BOARD_PROPERTY__ELEMENT);
		int value = rs.getInt(DBConstants.BOARD_PROPERTY__VALUE);
		int quantity = rs.getInt(DBConstants.BOARD_PROPERTY__QUANTITY);

		if (null != name) {
			String newName = name.trim().toUpperCase();
			if (!name.equals(newName)) {
				log.error(String.format("name incorrect: %s, id=%s", name, id));
				name = newName;
			}
		}

		if (null != element) {
			String newElement = element.trim().toUpperCase();
			if (!element.equals(newElement)) {
				log.error(String.format("element incorrect: %s, id=%s",
						element, id));
				element = newElement;
			}
		}

		BoardProperty boardProperty = new BoardProperty(id, boardId, name,
				posX, posY, element, value, quantity);
		return boardProperty;
	}

}
