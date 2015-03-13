package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Obstacle;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ObstacleRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, Obstacle> obstacleIdsToObstacles;

	private static final String TABLE_NAME = DBConstants.TABLE_OBSTACLE_CONFIG;

	public static Map<Integer, Obstacle> getObstacleIdsToObstacles() {
		if (null == obstacleIdsToObstacles) {
			setStaticObstacleIdsToObstacles();
		}
		return obstacleIdsToObstacles;
	}

	public static Obstacle getObstacleForId(int obstacleId) {
		if (null == obstacleIdsToObstacles) {
			setStaticObstacleIdsToObstacles();
		}

		if (!obstacleIdsToObstacles.containsKey(obstacleId)) {
			log.error("no obstacle exists for id=" + obstacleId);
			return null;
		}

		return obstacleIdsToObstacles.get(obstacleId);
	}

	public static Map<Integer, Obstacle> getObstaclesForIds(
			Collection<Integer> ids) {
		if (null == obstacleIdsToObstacles) {
			setStaticObstacleIdsToObstacles();
		}
		Map<Integer, Obstacle> returnMap = new HashMap<Integer, Obstacle>();

		for (int id : ids) {
			Obstacle tsm = getObstacleForId(id);
			returnMap.put(id, tsm);
		}
		return returnMap;
	}

	private static void setStaticObstacleIdsToObstacles() {
		log.debug("setting static map of obstacle ids to obstacles");

		Random rand = new Random();
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Obstacle> obstacleIdsToObstaclesTemp = new HashMap<Integer, Obstacle>();

						//loop through each row and convert it into a java object
						while (rs.next()) {
							Obstacle obstacle = convertRSRowToObstacle(rs, rand);

							int obstacleId = obstacle.getId();
							obstacleIdsToObstaclesTemp
									.put(obstacleId, obstacle);
						}

						obstacleIdsToObstacles = obstacleIdsToObstaclesTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("obstacle retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticObstacleIdsToObstacles();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static Obstacle convertRSRowToObstacle(ResultSet rs, Random rand)
			throws SQLException {
		int id = rs.getInt(DBConstants.OBSTACLE__ID);
		String name = rs.getString(DBConstants.OBSTACLE__NAME);
		String removalCostType = rs
				.getString(DBConstants.OBSTACLE__REMOVAL_COST_TYPE);
		int cost = rs.getInt(DBConstants.OBSTACLE__COST);
		int secondsToRemove = rs
				.getInt(DBConstants.OBSTACLE__SECONDS_TO_REMOVE);
		int width = rs.getInt(DBConstants.OBSTACLE__WIDTH);
		int height = rs.getInt(DBConstants.OBSTACLE__HEIGHT);
		String imgName = rs.getString(DBConstants.OBSTACLE__IMG_NAME);
		float imgVerticalPixelOffset = rs
				.getFloat(DBConstants.OBSTACLE__IMG_VERTICAL_PIXEL_OFFSET);
		String description = rs.getString(DBConstants.OBSTACLE__DESCRIPTION);
		float chanceToAppear = rs
				.getFloat(DBConstants.OBSTACLE__CHANCE_TO_APPEAR);
		String shadowImgName = rs
				.getString(DBConstants.OBSTACLE__SHADOW_IMG_NAME);
		float shadowVerticalOffset = rs
				.getFloat(DBConstants.OBSTACLE__SHADOW_VERTICAL_OFFSET);
		float shadowHorizontalOffset = rs
				.getFloat(DBConstants.OBSTACLE__SHADOW_HORIZONTAL_OFFSET);

		if (null != removalCostType) {
			String newRemovalCostType = removalCostType.trim().toUpperCase();
			if (!removalCostType.equals(newRemovalCostType)) {
				log.error(String.format(
						"removalCostType, ResourceType, incorrect: %s, id=%s",
						removalCostType, id));
				removalCostType = newRemovalCostType;
			}
		}

		Obstacle obstacle = new Obstacle(id, name, removalCostType, cost,
				secondsToRemove, width, height, imgName,
				imgVerticalPixelOffset, description, chanceToAppear,
				shadowImgName, shadowVerticalOffset, shadowHorizontalOffset);
		return obstacle;
	}
}
