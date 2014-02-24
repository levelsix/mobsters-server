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

import com.lvl6.info.ClanRaidStage;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanRaidStageRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, ClanRaidStage>> clanRaidIdsToClanRaidStageIdsToClanRaidStages;
  private static Map<Integer, ClanRaidStage> clanRaidStageIdsToClanRaidStages;

  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_RAID_STAGE;

  public static Map<Integer, Map<Integer, ClanRaidStage>> getClanRaidIdsToClanRaidStageIdsToClanRaidStages() {
    log.debug("retrieving all clan raid stage data map");
    if (clanRaidIdsToClanRaidStageIdsToClanRaidStages == null) {
      setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();
    }
    return clanRaidIdsToClanRaidStageIdsToClanRaidStages;
  }
  
  public static Map<Integer, ClanRaidStage> getClanRaidStageIdsToStages() {
  	if (null == clanRaidStageIdsToClanRaidStages) {
  		setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();
  	}
  	return clanRaidStageIdsToClanRaidStages;
  }
  
  public static ClanRaidStage getClanRaidStageForClanRaidStageId(int clanRaidStageId) {
	  if (null == clanRaidStageIdsToClanRaidStages) {
		  setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();      
	  }
	  
	  if (clanRaidStageIdsToClanRaidStages.containsKey(clanRaidStageId)) {
	  	return clanRaidStageIdsToClanRaidStages.get(clanRaidStageId); 
	  } else {
	  	log.error("clan raid stage does not exist. clanRaidStageId=" + clanRaidStageId);
	  	return null;
	  }
	  
  }

  public static Map<Integer, ClanRaidStage> getClanRaidStagesForClanRaidId(int clanRaidId) {
    log.debug("retrieve clan raid stage data for clanRaidId " + clanRaidId);
    if (null == clanRaidIdsToClanRaidStageIdsToClanRaidStages) {
      setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();      
    }
    //check to see if stages exist for clanRaidId
    if (clanRaidIdsToClanRaidStageIdsToClanRaidStages.containsKey(clanRaidId)) {
    	return clanRaidIdsToClanRaidStageIdsToClanRaidStages.get(clanRaidId);
    } else {
    	log.error("no clan raid stages for clanRaidId=" + clanRaidId);
    	return new HashMap<Integer, ClanRaidStage>();
    }
  }
  
  public static ClanRaidStage getFirstStageForClanRaid(int clanRaidId) {
  	log.debug("retrieving the first clan raid stage for clanRaidId=" + clanRaidId);
  	if (null == clanRaidIdsToClanRaidStageIdsToClanRaidStages) {
      setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();      
    }
    //check to see if stages exist for clanRaidId
  	if (clanRaidIdsToClanRaidStageIdsToClanRaidStages.containsKey(clanRaidId)) {
    	log.error("no clan raid stages for clanRaidId=" + clanRaidId);
    	return null;
    }
  	Map<Integer, ClanRaidStage> stages = clanRaidIdsToClanRaidStageIdsToClanRaidStages.get(clanRaidId);
  	
  	int curLowestStageNum = Integer.MAX_VALUE;
  	ClanRaidStage crs = null;
  	for (ClanRaidStage tempCrs : stages.values()) {
  		int tempStageNum = tempCrs.getStageNum();
  		
  		if (tempStageNum < curLowestStageNum) {
  			curLowestStageNum = tempStageNum;
  			crs = tempCrs;
  		}
  	}
  	
  	return crs;
  }


  private static void setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages() {
    log.debug("setting static map of clanRaidIds to stages");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Map<Integer, ClanRaidStage>> clanRaidIdsToClanRaidStageIdsToClanRaidStagesTemp =
			          new HashMap<Integer, Map<Integer, ClanRaidStage>>();
			      Map<Integer, ClanRaidStage> clanRaidStageIdsToClanRaidStagesTemp =
			    		  new HashMap<Integer, ClanRaidStage>();
			      //loop through each row and convert it into a java object
			      while(rs.next()) {  
			        ClanRaidStage clanRaidStage = convertRSRowToClanRaidStage(rs);
			        if (clanRaidStage == null) {
			          continue;
			        }
			        
			        int clanRaidId = clanRaidStage.getClanRaidId();
			        //base case, no key with clanRaid id exists, so create map with
			        //key: clanRaid id, to value: another map
			        if (!clanRaidIdsToClanRaidStageIdsToClanRaidStagesTemp.containsKey(clanRaidId)) {
			          clanRaidIdsToClanRaidStageIdsToClanRaidStagesTemp.put(clanRaidId, new HashMap<Integer, ClanRaidStage>());
			        }

			        //get map of clanRaid stages related to current clanRaid id
			        //stick clanRaidStage into the map of ClanRaidStage ids to ClanRaidStage objects
			        Map<Integer, ClanRaidStage> clanRaidStageIdsToClanRaidStagesForClanRaidStage =
			            clanRaidIdsToClanRaidStageIdsToClanRaidStagesTemp.get(clanRaidId);
			        
			        int clanRaidStageId = clanRaidStage.getId();
			        clanRaidStageIdsToClanRaidStagesForClanRaidStage.put(clanRaidStageId, clanRaidStage);
			        clanRaidStageIdsToClanRaidStagesTemp.put(clanRaidStageId, clanRaidStage);
			      }
			      clanRaidIdsToClanRaidStageIdsToClanRaidStages = clanRaidIdsToClanRaidStageIdsToClanRaidStagesTemp;
			      clanRaidStageIdsToClanRaidStages = clanRaidStageIdsToClanRaidStagesTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("clanRaid stage retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static ClanRaidStage convertRSRowToClanRaidStage(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int clanRaidId = rs.getInt(i++);
    int eventDuration = rs.getInt(i++);
    int stageNum = rs.getInt(i++);
    
    ClanRaidStage clanRaidStage = new ClanRaidStage(id, clanRaidId, eventDuration, stageNum);
        
    return clanRaidStage;
  }
}
