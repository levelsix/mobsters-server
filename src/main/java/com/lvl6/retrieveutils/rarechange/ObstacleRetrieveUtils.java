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

@Component @DependsOn("gameServer") public class ObstacleRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Obstacle> obstacleIdsToObstacles;

  private static final String TABLE_NAME = DBConstants.TABLE_OBSTACLE;
  
  public static Map<Integer, Obstacle> getObstacleIdsToObstacles() {
  	if (null == obstacleIdsToObstacles) {
  		setStaticObstacleIdsToObstacles();
  	}
  	return obstacleIdsToObstacles;
  }

  public static Map<Integer, Obstacle> getObstaclesForIds(Collection<Integer> ids) {
  	if (null == obstacleIdsToObstacles) {
  		setStaticObstacleIdsToObstacles();
  	}
  	Map<Integer, Obstacle> returnMap = new HashMap<Integer, Obstacle>();
  	
  	for (int id : ids) {
  		Obstacle tsm = obstacleIdsToObstacles.get(id);
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
			      Map<Integer, Obstacle> obstacleIdsToObstaclesTemp =
			      		new HashMap<Integer, Obstacle>();
			      
			      //loop through each row and convert it into a java object
			      while(rs.next()) {  
			        Obstacle obstacle = convertRSRowToObstacle(rs, rand);
			        
			        int obstacleId = obstacle.getId();
			        obstacleIdsToObstaclesTemp.put(obstacleId, obstacle);
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
  private static Obstacle convertRSRowToObstacle(ResultSet rs, Random rand) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String name = rs.getString(i++);
    String removalCostType = rs.getString(i++);
    int cost = rs.getInt(i++);
    int secondsToRemove = rs.getInt(i++);
    int width = rs.getInt(i++);
    int height = rs.getInt(i++);
    String imgName = rs.getString(i++);
    float imgVerticalPixelOffset = rs.getFloat(i++);
    String description = rs.getString(i++);
    float chanceToAppear = rs.getFloat(i++);
    
    Obstacle obstacle = new Obstacle(id, name, removalCostType, cost,
    		secondsToRemove, width, height, imgName, imgVerticalPixelOffset,
    		description, chanceToAppear);
    return obstacle;
  }
}
