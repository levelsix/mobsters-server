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

import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanRaidStageMonsterRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, ClanRaidStageMonster>> clanRaidStageIdsToIdsToMonsters;
  private static Map<Integer, ClanRaidStageMonster> idsToMonsters;

  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_RAID_STAGE_MONSTER;

  public static Map<Integer, Map<Integer, ClanRaidStageMonster>> getClanRaidStageIdsToIdsToMonsters() {
    log.debug("retrieving all clan raid stage monster data map");
    if (clanRaidStageIdsToIdsToMonsters == null) {
      setStaticClanRaidStageIdsToIdsToMonsters();
    }
    return clanRaidStageIdsToIdsToMonsters;
  }
  
  public static Map<Integer, ClanRaidStageMonster> getIdsToClanRaidStageMonsters() {
  	if (null == idsToMonsters) {
  		setStaticClanRaidStageIdsToIdsToMonsters();
  	}
  	return idsToMonsters;
  }
  
  public static ClanRaidStageMonster getClanRaidStageMonsterForClanRaidStageMonsterId(int clanRaidStageMonsterId) {
	  if (idsToMonsters == null) {
		  setStaticClanRaidStageIdsToIdsToMonsters();      
	  }
	  
	  if (idsToMonsters.containsKey(clanRaidStageMonsterId)) {
	  	return idsToMonsters.get(clanRaidStageMonsterId); 
	  } else {
	  	log.error("no clan raid stage monster exists with id=" + clanRaidStageMonsterId);
	  	return null;
	  }
  }

  public static Map<Integer, ClanRaidStageMonster> getClanRaidStageMonstersForClanRaidStageId(int clanRaidStageId) {
    log.debug("retrieve clan raid stage monster data for clan raid stage id " + clanRaidStageId);
    if (clanRaidStageIdsToIdsToMonsters == null) {
      setStaticClanRaidStageIdsToIdsToMonsters();      
    }
    
    if (clanRaidStageIdsToIdsToMonsters.containsKey(clanRaidStageId)) {
    	return clanRaidStageIdsToIdsToMonsters.get(clanRaidStageId);
    } else {
    	log.error("no clan raid stage monsters exist for clan raid stage id=" + clanRaidStageId);
    	return new HashMap<Integer, ClanRaidStageMonster>();
    }
  }


  private static void setStaticClanRaidStageIdsToIdsToMonsters() {
    log.debug("setting static map of clanRaidStageIds to monsters");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Map<Integer, ClanRaidStageMonster>> clanRaidIdsToClanRaidStageMonsterIdsToClanRaidStageMonstersTemp =
			          new HashMap<Integer, Map<Integer, ClanRaidStageMonster>>();
			      Map<Integer, ClanRaidStageMonster> clanRaidStageMonsterIdsToClanRaidStageMonstersTemp =
			    		  new HashMap<Integer, ClanRaidStageMonster>();
			      //loop through each row and convert it into a java object
			      while(rs.next()) {  
			        ClanRaidStageMonster clanRaidStageMonster = convertRSRowToClanRaidStageMonster(rs);
			        if (clanRaidStageMonster == null) {
			          continue;
			        }
			        
			        int clanRaidStageId = clanRaidStageMonster.getClanRaidStageId();
			        //base case, no key with clanRaid id exists, so create map with
			        //key: clanRaid id, to value: another map
			        if (!clanRaidIdsToClanRaidStageMonsterIdsToClanRaidStageMonstersTemp.containsKey(clanRaidStageId)) {
			          clanRaidIdsToClanRaidStageMonsterIdsToClanRaidStageMonstersTemp.put(clanRaidStageId, new HashMap<Integer, ClanRaidStageMonster>());
			        }

			        //get map of clanRaid stages related to current clanRaid id
			        //stick clanRaidStageMonster into the map of ClanRaidStageMonster ids to ClanRaidStageMonster objects
			        Map<Integer, ClanRaidStageMonster> clanRaidStageMonsterIdsToClanRaidStageMonstersForClanRaidStageMonster =
			            clanRaidIdsToClanRaidStageMonsterIdsToClanRaidStageMonstersTemp.get(clanRaidStageId);
			        
			        int clanRaidStageMonsterId = clanRaidStageMonster.getId();
			        clanRaidStageMonsterIdsToClanRaidStageMonstersForClanRaidStageMonster.put(clanRaidStageMonsterId, clanRaidStageMonster);
			        clanRaidStageMonsterIdsToClanRaidStageMonstersTemp.put(clanRaidStageMonsterId, clanRaidStageMonster);
			      }
			      clanRaidStageIdsToIdsToMonsters = clanRaidIdsToClanRaidStageMonsterIdsToClanRaidStageMonstersTemp;
			      idsToMonsters = clanRaidStageMonsterIdsToClanRaidStageMonstersTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("clan raid stage monster retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticClanRaidStageIdsToIdsToMonsters();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static ClanRaidStageMonster convertRSRowToClanRaidStageMonster(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int clanRaidStageId = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    int monsterHp = rs.getInt(i++);
    int minDmg = rs.getInt(i++);
    int maxDmg = rs.getInt(i++);
    
    ClanRaidStageMonster clanRaidStageMonster = new ClanRaidStageMonster(id,
    		clanRaidStageId, monsterId, monsterHp, minDmg, maxDmg);

    return clanRaidStageMonster;
  }
}
