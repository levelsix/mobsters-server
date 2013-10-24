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

import com.lvl6.info.StaticLevelInfo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StaticLevelInfoRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StaticLevelInfo> levelToStaticLevelInfo;

  private static final String TABLE_NAME = DBConstants.TABLE_STATIC_LEVEL_INFO;

  public static Map<Integer, StaticLevelInfo> getAllStaticLevelInfo() {
    log.debug("retrieving all static level info");
    if (levelToStaticLevelInfo == null) {
      setStaticLevelInfo();
    }
    return levelToStaticLevelInfo;
  }
  
  public static StaticLevelInfo getStaticLevelInfoForLevel(int level) {
  	log.debug("retrieving static level info for a level. level=" + level);
    if (levelToStaticLevelInfo == null) {
      setStaticLevelInfo();
    }
    return levelToStaticLevelInfo.get(level);
  }

  private static void setStaticLevelInfo() {
    log.debug("setting static set of static level info");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map<Integer, StaticLevelInfo> levelToStaticLevelInfoTemp =
          		new HashMap<Integer, StaticLevelInfo>();
          while(rs.next()) { 
            StaticLevelInfo sli = convertRSRowToStaticLevelInfo(rs);
            if (null != sli) {
            	int lvl = sli.getLvl();
            	levelToStaticLevelInfoTemp.put(lvl, sli);
            }
          }
          levelToStaticLevelInfo = levelToStaticLevelInfoTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
        }
      }    
    }
    DBConnection.get().close(rs, null, conn);
  }

  public static void reload() {
    setStaticLevelInfo();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StaticLevelInfo convertRSRowToStaticLevelInfo(ResultSet rs) throws SQLException {
    int i = 1;
    int lvl = rs.getInt(i++);
    int requiredExp = rs.getInt(i++);
    int maxCash = rs.getInt(i++);
    
    StaticLevelInfo sli = new StaticLevelInfo(lvl, requiredExp, maxCash);
    return sli;
  }
}
