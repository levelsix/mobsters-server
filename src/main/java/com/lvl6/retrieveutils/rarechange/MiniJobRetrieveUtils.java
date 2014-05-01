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

import com.lvl6.info.MiniJob;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class MiniJobRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, MiniJob> miniJobIdsToMiniJobs;
  private static Map<Integer, Map<Integer, MiniJob>> structureIdToMiniJobIdToMiniJob;
  

  private static final String TABLE_NAME = DBConstants.TABLE_MINI_JOB;

  //CONTROLLER LOGIC******************************************************************

  //RETRIEVE QUERIES*********************************************************************
  public static Map<Integer, MiniJob> getMiniJobIdsToMiniJobs() {
    log.debug("retrieving all miniJob data");
    if (miniJobIdsToMiniJobs == null) {
      setStaticMiniJobIdsToMiniJobs();
    }
    return miniJobIdsToMiniJobs;
  }

  public static MiniJob getMiniJobForMiniJobId(int miniJobId) {
    log.debug("retrieving miniJob with miniJobId " + miniJobId);
    if (null == miniJobIdsToMiniJobs) {
      setStaticMiniJobIdsToMiniJobs();
    }
    return miniJobIdsToMiniJobs.get(miniJobId);
  }

  private static void setStaticMiniJobIdsToMiniJobs() {
    log.debug("setting static map of miniJobIds to miniJobs");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, MiniJob> tmp = new HashMap<Integer, MiniJob>();
			      Map<Integer, Map<Integer, MiniJob>> structIdToMiniJobIdToMiniJob =
			    		  new HashMap<Integer, Map<Integer, MiniJob>>();
			      
			      while(rs.next()) {
			        MiniJob miniJob = convertRSRowToMiniJob(rs);
			        if (null == miniJob) {
			        	continue;
			        }

			        int structId = miniJob.getRequiredStructId();
			        if (!structIdToMiniJobIdToMiniJob.containsKey(structId)) {
			        	structIdToMiniJobIdToMiniJob.put(structId,
			        			new HashMap<Integer, MiniJob>());
			        }
			        int miniJobId = miniJob.getId();
			        
			        tmp.put(miniJobId, miniJob);
			        
			        Map<Integer, MiniJob> miniJobIdToMiniJob =
			        		structIdToMiniJobIdToMiniJob.get(structId);
			        miniJobIdToMiniJob.put(miniJobId, miniJob);
			        
			      }
			      miniJobIdsToMiniJobs = tmp;
			      structureIdToMiniJobIdToMiniJob = structIdToMiniJobIdToMiniJob;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }
			}
		} catch (Exception e) {
    	log.error("miniJob retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticMiniJobIdsToMiniJobs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static MiniJob convertRSRowToMiniJob(ResultSet rs) throws SQLException {

    int i = 1;
    int id = rs.getInt(i++);
    int requiredStructId = rs.getInt(i++);
    String miniJobName = rs.getString(i++);
    int cashReward = rs.getInt(i++);
    int oilReward = rs.getInt(i++);
    int gemReward = rs.getInt(i++);
    int monsterIdReward = rs.getInt(i++);
    String quality = rs.getString(i++);
    int maxNumMonstersAllowed = rs.getInt(i++);
    float chanceToAppear = rs.getInt(i++);
    int hpRequired = rs.getInt(i++);
    int atkRequired = rs.getInt(i++);
    int minDmgDealt = rs.getInt(i++);
    int maxDmgDealt = rs.getInt(i++);
    
    if (null != quality) {
    	String newQuality = quality.trim().toUpperCase();
    	if (!quality.equals(newQuality)) {
    		log.error("quality incorrect: " + quality + "\t id=" + id);
    		quality = newQuality;
    	}
    }
    
    MiniJob miniJob = new MiniJob(id, requiredStructId, miniJobName,
    		cashReward, oilReward, gemReward, monsterIdReward, quality,
    		maxNumMonstersAllowed, chanceToAppear, hpRequired, atkRequired,
    		minDmgDealt, maxDmgDealt);
    
    return miniJob;
  }

}
