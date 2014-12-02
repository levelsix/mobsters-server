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

  private static final String TABLE_NAME = DBConstants.TABLE_ACHIEVEMENT_CONFIG;

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

    int id = rs.getInt(DBConstants.ACHIEVEMENT__ID);
    String achievementName = rs.getString(DBConstants.ACHIEVEMENT__NAME);
    String description = rs.getString(DBConstants.ACHIEVEMENT__DESCRIPTION);
    int gemReward = rs.getInt(DBConstants.ACHIEVEMENT__GEM_REWARD);
    int lvl = rs.getInt(DBConstants.ACHIEVEMENT__LVL);
    String achievementType = rs.getString(DBConstants.ACHIEVEMENT__ACHIEVEMENT_TYPE);
    String resourceType = rs.getString(DBConstants.ACHIEVEMENT__RESOURCE_TYPE);
    String monsterElement = rs.getString(DBConstants.ACHIEVEMENT__MONSTER_ELEMENT);
    String monsterQuality = rs.getString(DBConstants.ACHIEVEMENT__MONSTER_QUALITY);
    int staticDataId = rs.getInt(DBConstants.ACHIEVEMENT__STATIC_DATA_ID);
    int quantity = rs.getInt(DBConstants.ACHIEVEMENT__QUANTITY);
    int priority = rs.getInt(DBConstants.ACHIEVEMENT__PRIORITY);
    int prerequisiteId = rs.getInt(DBConstants.ACHIEVEMENT__PREREQUISITE_ID);
    int successorId = rs.getInt(DBConstants.ACHIEVEMENT__SUCCESSOR_ID);
    int expReward = rs.getInt(DBConstants.ACHIEVEMENT__EXP_REWARD);
    
    if (null != achievementType) {
    	String newAchievementType = achievementType.trim().toUpperCase();
    	if (!achievementType.equals(newAchievementType)) {
    		log.error(String.format(
    			"achievementType incorrect: %s, id=%s",
    			achievementType, id));
    		achievementType = newAchievementType;
    	}
    }
    if (null != resourceType) {
    	String newResourceType = resourceType.trim().toUpperCase();
    	if (!resourceType.equals(newResourceType)) {
    		log.error(String.format(
    			"resourceType incorrect: %s, id=%s",
    			resourceType, id));
    		resourceType = newResourceType;
    	}
    }
    if (null != monsterElement) {
    	String newMonsterElement = monsterElement.trim().toUpperCase();
    	if (!monsterElement.equals(newMonsterElement)) {
    		log.error(String.format(
    			"monsterElement incorrect: %s, id=%s",
    			monsterElement, id));
    		monsterElement = newMonsterElement;
    	}
    }
    if (null != monsterQuality) {
    	String newMonsterQuality = monsterQuality.trim().toUpperCase();
    	if (!monsterQuality.equals(newMonsterQuality)) {
    		log.error(String.format(
    			"monsterQuality incorrect: %s, id=%s",
    			monsterQuality, id));
    		monsterQuality = newMonsterQuality;
    	}
    }
    
    Achievement achievement = new Achievement(id, achievementName,
    		description, gemReward, lvl, achievementType, resourceType,
    		monsterElement, monsterQuality, staticDataId, quantity, priority,
    		prerequisiteId, successorId, expReward);
    
    return achievement;
  }

}
