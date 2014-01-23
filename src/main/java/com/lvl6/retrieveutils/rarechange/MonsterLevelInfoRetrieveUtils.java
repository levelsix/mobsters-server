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

import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class MonsterLevelInfoRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, MonsterLevelInfo>> monsterIdToLevelToInfo;

  private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_LEVEL_INFO;

  public static Map<Integer, Map<Integer, MonsterLevelInfo>> getMonsterIdToLevelToInfo() {
    log.debug("retrieving all monster lvl info data");
    if (monsterIdToLevelToInfo == null) {
      setStaticMonsterIdToLevelToInfo();
    }
    return monsterIdToLevelToInfo;
  }
  
  public static Map<Integer, MonsterLevelInfo> getMonsterLevelInfoForMonsterId(int id) {
  	log.debug("retrieving monster lvl info for monster id=" + id);
  	if (null == monsterIdToLevelToInfo) {
  		setStaticMonsterIdToLevelToInfo();
  	}
  	
  	if (!monsterIdToLevelToInfo.containsKey(id)) {
  		log.error("no monster level info for monsterId=" + id);
  	}
  	
  	return monsterIdToLevelToInfo.get(id);
  }

  private static void setStaticMonsterIdToLevelToInfo() {
    log.debug("setting static map of monster id to level to info");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Map<Integer, MonsterLevelInfo>> monsterIdToLevelToTemp =
			      		new HashMap<Integer, Map<Integer, MonsterLevelInfo>>();
			      while(rs.next()) {
			        MonsterLevelInfo mefe = convertRSRowToMonsterLevelInfo(rs);
			        if (mefe == null) {
			        	continue;
			        }
			        
			        int monsterId = mefe.getMonsterId();
			        if (!monsterIdToLevelToTemp.containsKey(monsterId)) {
			        	//base case where have not encountered this monster id before
			        	Map<Integer, MonsterLevelInfo> levelToFeederExpTemp =
			        			new HashMap<Integer, MonsterLevelInfo>();
			        	monsterIdToLevelToTemp.put(monsterId, levelToFeederExpTemp);
			        }
			        
			        Map<Integer, MonsterLevelInfo> levelToFeederExpTemp =
			        		monsterIdToLevelToTemp.get(monsterId);
			        
			        int level = mefe.getLevel();
			        levelToFeederExpTemp.put(level, mefe);

			      }
			      monsterIdToLevelToInfo = monsterIdToLevelToTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("resourceStorage retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticMonsterIdToLevelToInfo();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static MonsterLevelInfo convertRSRowToMonsterLevelInfo(ResultSet rs) throws SQLException {
    int i = 1;
    int monsterId = rs.getInt(i++);
    int level = rs.getInt(i++);
    int hp = rs.getInt(i++);
//    int attack = rs.getInt(i++);
    int curLvlRequiredExp = rs.getInt(i++);
    int feederExp = rs.getInt(i++);
    int fireDmg = rs.getInt(i++);
    int grassDmg = rs.getInt(i++);
    int waterDmg = rs.getInt(i++);
    int lightningDmg = rs.getInt(i++);
    int darknessDmg = rs.getInt(i++);
    int rockDmg = rs.getInt(i++);
    
    MonsterLevelInfo srs = new MonsterLevelInfo(monsterId, level, hp, curLvlRequiredExp,
    		feederExp, fireDmg, grassDmg, waterDmg, lightningDmg, darknessDmg, rockDmg);
    
    return srs;
  }
}
