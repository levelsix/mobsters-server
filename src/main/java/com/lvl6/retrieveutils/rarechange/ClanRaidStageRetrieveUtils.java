package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanRaidStageRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, ClanRaidStage>> clanRaidIdsToClanRaidStageIdsToClanRaidStages;
  private static Map<Integer, Map<Integer, ClanRaidStage>> clanRaidIdsToStageNumToClanRaidStages;
  private static Map<Integer, ClanRaidStage> clanRaidStageIdsToClanRaidStages;

  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_RAID_STAGE_CONFIG;
  
  public static int getClanRaidStageHealthForCrsId(int crsId) {
  	log.debug("retrieving stage health for crsId=" + crsId);
  	
  	if (null == clanRaidStageIdsToClanRaidStages) {
  		setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();
  	}
  	
  	if (clanRaidStageIdsToClanRaidStages.containsKey(crsId)) {
	  	ClanRaidStage crs = clanRaidStageIdsToClanRaidStages.get(crsId);
	  	return crs.getStageHealth();
	  } else {
	  	log.error("clan raid stage does not exist. clanRaidStageId=" + crsId);
	  	return 0;
	  }
  	
  }

  public static Map<Integer, Map<Integer, ClanRaidStage>> getClanRaidIdsToClanRaidStageIdsToClanRaidStages() {
    log.debug("retrieving all clan raid stage data map");
    if (null == clanRaidIdsToClanRaidStageIdsToClanRaidStages) {
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
  	if (!clanRaidIdsToClanRaidStageIdsToClanRaidStages.containsKey(clanRaidId)) {
    	log.error("no clan raid stages for clanRaidId=" + clanRaidId);
    	return null;
    }
  	Map<Integer, ClanRaidStage> stageNumsToStages = clanRaidIdsToStageNumToClanRaidStages.get(clanRaidId);
  	if (null == stageNumsToStages || stageNumsToStages.isEmpty()) {
  		log.error("no clan raid stages for clanRaidId=" + clanRaidId);
    	return null;
  	}
  	
  	List<Integer> stageNumsAsc = new ArrayList<Integer>(stageNumsToStages.keySet());
  	Collections.sort(stageNumsAsc);
  	int firstStageNum = stageNumsAsc.get(0);
  	
  	ClanRaidStage crs = stageNumsToStages.get(firstStageNum);
  	return crs;
  }

  public static ClanRaidStage getNextStageForClanRaidStageId(int clanRaidStageId, int clanRaidId) {
  	log.debug("retrieving the clan raid stage after clanRaidStageId=" + clanRaidStageId);
  	if (null == clanRaidIdsToClanRaidStageIdsToClanRaidStages) {
      setStaticClanRaidIdsToClanRaidStageIdsToClanRaidStages();      
    }
  	
  	if (!clanRaidStageIdsToClanRaidStages.containsKey(clanRaidStageId)) {
  		log.error("no clan raid stage exists for clanRaidStageId=" + clanRaidStageId);
  		return null;
  	}
  	ClanRaidStage curCrs = clanRaidStageIdsToClanRaidStages.get(clanRaidStageId);
  	int stageNum = curCrs.getStageNum();
  	
  	Map<Integer, ClanRaidStage> stageNumToCrs = clanRaidIdsToStageNumToClanRaidStages
  			.get(clanRaidId);
  	
  	List<Integer> stageNumsAsc = new ArrayList<Integer>(stageNumToCrs.keySet());
  	Collections.sort(stageNumsAsc);
  	int index = stageNumsAsc.indexOf(stageNum);
  	
  	int indexOfNextStage = index + 1;
  	if (indexOfNextStage >= stageNumsAsc.size()) {
  		return null;
  	}
  	int stageNumOfNextStage = stageNumsAsc.get(indexOfNextStage);
  	return stageNumToCrs.get(stageNumOfNextStage);
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
			      Map<Integer, Map<Integer, ClanRaidStage>> clanRaidIdsToStageNumToClanRaidStagesTemp =
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
			          clanRaidIdsToStageNumToClanRaidStagesTemp.put(clanRaidId, new HashMap<Integer, ClanRaidStage>());
			        }

			        //get map of clanRaid stages related to current clanRaid id
			        //stick clanRaidStage into the map of ClanRaidStage ids to ClanRaidStage objects
			        Map<Integer, ClanRaidStage> clanRaidStageIdsToClanRaidStagesForClanRaidStage =
			            clanRaidIdsToClanRaidStageIdsToClanRaidStagesTemp.get(clanRaidId);
			        
			        int clanRaidStageId = clanRaidStage.getId();
			        clanRaidStageIdsToClanRaidStagesForClanRaidStage.put(clanRaidStageId, clanRaidStage);
			        clanRaidStageIdsToClanRaidStagesTemp.put(clanRaidStageId, clanRaidStage);
			        
			        //enabling getting stages in order
			        Map<Integer, ClanRaidStage> stageNumToClanRaidStages =
			        		clanRaidIdsToStageNumToClanRaidStagesTemp.get(clanRaidId);
			        int stageNum = clanRaidStage.getStageNum();
			        stageNumToClanRaidStages.put(stageNum, clanRaidStage);
			      }
			      clanRaidIdsToClanRaidStageIdsToClanRaidStages = clanRaidIdsToClanRaidStageIdsToClanRaidStagesTemp;
			      clanRaidStageIdsToClanRaidStages = clanRaidStageIdsToClanRaidStagesTemp;
			      clanRaidIdsToStageNumToClanRaidStages = clanRaidIdsToStageNumToClanRaidStagesTemp;
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
    setStageHealths();
  }
  
  public static void setStageHealths() {
  	log.debug("setting the stage health attribute in clan raid stages");
  	ClanRaidStageMonsterRetrieveUtils.reload();
  	
  	for (Integer crsId : clanRaidStageIdsToClanRaidStages.keySet()) {
  		ClanRaidStage crs = clanRaidStageIdsToClanRaidStages.get(crsId);
  		
  		Map<Integer, ClanRaidStageMonster> crsmIdToCrsm = ClanRaidStageMonsterRetrieveUtils
  				.getClanRaidStageMonstersForClanRaidStageId(crsId);
  		
  		int stageHealth = 0;
  		for (ClanRaidStageMonster crsm : crsmIdToCrsm.values()) {
  			stageHealth += crsm.getMonsterHp();
  		}
			crs.setStageHealth(stageHealth);
  	}
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
    String name = rs.getString(i++);
    
    ClanRaidStage clanRaidStage = new ClanRaidStage(id, clanRaidId, eventDuration,
    		stageNum, name);
        
    return clanRaidStage;
  }
}
