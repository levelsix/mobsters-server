package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Achievement;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class AchievementRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Achievement> achievementIdsToAchievements;

  private static final String TABLE_NAME = DBConstants.TABLE_ACHIEVEMENT;

  //CONTROLLER LOGIC******************************************************************
  public static Set<Integer> getAchievementIds() {
	  Map<Integer, Achievement> achievementIdsToAchievements =
			  getAchievementIdsToAchievements();
	  
	  if (null == achievementIdsToAchievements) {
		  return new HashSet<Integer>();
	  }
	  return new HashSet<Integer>(achievementIdsToAchievements.keySet());
  }

  //RETRIEVE QUERIES*********************************************************************
  public static Map<Integer, Achievement> getAchievementIdsToAchievements() {
    log.debug("retrieving all achievement data");
    if (achievementIdsToAchievements == null) {
      setStaticAchievementIdsToAchievements();
    }
    return achievementIdsToAchievements;
  }

  public static Achievement getAchievementForAchievementId(int achievementId) {
    log.debug("retrieving achievement with achievementId " + achievementId);
    if (null == achievementIdsToAchievements) {
      setStaticAchievementIdsToAchievements();
    }
    return achievementIdsToAchievements.get(achievementId);
  }

  private static void setStaticAchievementIdsToAchievements() {
    log.debug("setting static map of achievementIds to achievements");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Achievement> tmp = new HashMap<Integer, Achievement>();
			      while(rs.next()) {
			        Achievement achievement = convertRSRowToAchievement(rs);
			        if (achievement != null) {
			            tmp.put(achievement.getId(), achievement);
			        }
			      }
			      achievementIdsToAchievements = tmp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }
			}
		} catch (Exception e) {
    	log.error("achievement retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticAchievementIdsToAchievements();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Achievement convertRSRowToAchievement(ResultSet rs) throws SQLException {

    int i = 1;
    int id = rs.getInt(i++);
    String achievementName = rs.getString(i++);
    String description = rs.getString(i++);
    int gemReward = rs.getInt(i++);
    int lvl = rs.getInt(i++);
    String achievementType = rs.getString(i++);
    String resourceType = rs.getString(i++);
    String monsterElement = rs.getString(i++);
    String monsterQuality = rs.getString(i++);
    int staticDataId = rs.getInt(i++);
    int quantity = rs.getInt(i++);
    int priority = rs.getInt(i++);
    int prerequisiteId = rs.getInt(i++);
    int successorId = rs.getInt(i++);
    
    if (null != achievementType) {
    	String newAchievementType = achievementType.trim().toUpperCase();
    	if (!achievementType.equals(newAchievementType)) {
    		log.error("achievementType incorrect: " + achievementType +
    				"\t id=" + id);
    		achievementType = newAchievementType;
    	}
    }
    if (null != resourceType) {
    	String newResourceType = resourceType.trim().toUpperCase();
    	if (!resourceType.equals(newResourceType)) {
    		log.error("resourceType incorrect: " + resourceType +
    				"\t id=" + id);
    		resourceType = newResourceType;
    	}
    }
    if (null != monsterElement) {
    	String newMonsterElement = monsterElement.trim().toUpperCase();
    	if (!monsterElement.equals(newMonsterElement)) {
    		log.error("monsterElement incorrect: " + monsterElement +
    				"\t id=" + id);
    		monsterElement = newMonsterElement;
    	}
    }
    if (null != monsterQuality) {
    	String newMonsterQuality = monsterQuality.trim().toUpperCase();
    	if (!monsterQuality.equals(newMonsterQuality)) {
    		log.error("monsterQuality incorrect: " + monsterQuality +
    				"\t id=" + id);
    		monsterQuality = newMonsterQuality;
    	}
    }
    
    Achievement achievement = new Achievement(id, achievementName,
    		description, gemReward, lvl, achievementType, resourceType,
    		monsterElement, monsterQuality, staticDataId, quantity, priority,
    		prerequisiteId, successorId);
    
    return achievement;
  }

}
