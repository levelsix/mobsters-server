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

import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class LevelRequiredExperienceRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Integer> levelToRequiredExperienceForLevel;

  private static final String TABLE_NAME = DBConstants.TABLE_LEVEL_REQUIRED_EXPERIENCE;

  public static Map<Integer, Integer> getLevelToRequiredExperienceForLevel() {
    log.debug("retrieving all exp-requirements-for-level data");
    if (levelToRequiredExperienceForLevel == null) {
      setStaticLevelToRequiredExperienceForLevel();
    }
    log.info("level required experience for level. \t" + levelToRequiredExperienceForLevel);
    return levelToRequiredExperienceForLevel;
  }

  public static int getRequiredExperienceForLevel(int level) {
    log.debug("retrieving exp-requirement for level " + level);
    if (levelToRequiredExperienceForLevel == null) {
      setStaticLevelToRequiredExperienceForLevel();
    }
    if (level > ControllerConstants.LEVEL_UP__MAX_LEVEL_FOR_USER) {
      return levelToRequiredExperienceForLevel.get(ControllerConstants.LEVEL_UP__MAX_LEVEL_FOR_USER)*2;
    }
    return levelToRequiredExperienceForLevel.get(level);
  }

  public static void reload() {
    setStaticLevelToRequiredExperienceForLevel();
  }

  private static void setStaticLevelToRequiredExperienceForLevel() {
    log.debug("setting static map of level to required experience for level");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map <Integer, Integer> levelToRequiredExperienceForLevelTemp = new HashMap<Integer, Integer>();
          while(rs.next()) {
            int i = 1;
            levelToRequiredExperienceForLevelTemp.put(rs.getInt(i++), rs.getInt(i++));
          }
          levelToRequiredExperienceForLevel = levelToRequiredExperienceForLevelTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      } else {
        log.error("query to select whole table failed somehow because ResultSet returned is null.");
      }
    }
    DBConnection.get().close(rs, null, conn);
  }
}
