package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
  private static Map<Integer, Float> structureIdToSumMiniJobProbability;

  private static final String TABLE_NAME = DBConstants.TABLE_MINI_JOB;

  //CONTROLLER LOGIC******************************************************************

  //RETRIEVE QUERIES*********************************************************************
  public static Map<Integer, MiniJob> getMiniJobIdsToMiniJobs() {
    log.debug("retrieving all miniJob data");
    if (null == miniJobIdsToMiniJobs) {
      setStaticMiniJobIdsToMiniJobs();
    }
    return miniJobIdsToMiniJobs;
  }

  public static MiniJob getMiniJobForMiniJobId(int miniJobId) {
    log.debug("retrieving miniJob with miniJobId " + miniJobId);
    if (null == miniJobIdsToMiniJobs) {
      setStaticMiniJobIdsToMiniJobs();
    }
    if (!miniJobIdsToMiniJobs.containsKey(miniJobId)) {
    	log.error("no MiniJobs for miniJobId=" + miniJobId);
    	return null;
    }
    return miniJobIdsToMiniJobs.get(miniJobId);
  }

  public static Map<Integer, MiniJob> getMiniJobForStructId(int structId) {
	  log.debug("retrieving miniJob with structId " + structId);
	  if (null == structureIdToMiniJobIdToMiniJob) {
		  setStaticMiniJobIdsToMiniJobs();
	  }

	  if (!structureIdToMiniJobIdToMiniJob.containsKey(structId)) {
		  log.error("no MiniJobs for structId=" + structId);
		  return new HashMap<Integer, MiniJob>();
	  }

	  Map<Integer, MiniJob> miniJobIdToMiniJobs =
			  structureIdToMiniJobIdToMiniJob.get(structId);

	  return miniJobIdToMiniJobs;
  }
  
  public static float getMiniJobProbabilitySumForStructId(int structId) {
	  log.debug("retrieving MiniJob probability sum for structId=" + structId);
	  if (null == structureIdToSumMiniJobProbability) {
		  setStaticMiniJobIdsToMiniJobs();
	  }
	  
	  if (!structureIdToSumMiniJobProbability.containsKey(structId)) {
		  log.error("no MiniJobs with probabilities for structId=" +
				  structId);
		  return 1F;
	  }
	  
	  float probabilitySum = structureIdToSumMiniJobProbability
			  .get(structId);
	  
	  if (0 == probabilitySum) {
		  log.error("sum of probabilities of MiniJobs for structId=" +
				  structId + ", is 0, setting it to 1");
		  probabilitySum = 1F;
	  }
	  
	  return probabilitySum;
  }
 
  private static void setStaticMiniJobIdsToMiniJobs() {
    log.debug("setting static map of miniJobIds to miniJobs");

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
			      Map<Integer, MiniJob> tmp = new HashMap<Integer, MiniJob>();
			      Map<Integer, Map<Integer, MiniJob>> structIdToMiniJobIdToMiniJob =
			    		  new HashMap<Integer, Map<Integer, MiniJob>>();
			      Map<Integer, Float> structIdToProbabilitySum =
			    		  new HashMap<Integer, Float>();
			      
			      while(rs.next()) {
			        MiniJob miniJob = convertRSRowToMiniJob(rs, rand);
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
			        
			        float prob = miniJob.getChanceToAppear();
			        float probSumSoFar = 0;
			        if (structIdToProbabilitySum.containsKey(structId)) {
			        	probSumSoFar = structIdToProbabilitySum.get(structId);
			        }
			        
			        structIdToProbabilitySum.put(structId, prob + probSumSoFar);
			      }
			      miniJobIdsToMiniJobs = tmp;
			      structureIdToMiniJobIdToMiniJob = structIdToMiniJobIdToMiniJob;
			      structureIdToSumMiniJobProbability = structIdToProbabilitySum;
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
  private static MiniJob convertRSRowToMiniJob(ResultSet rs,
		  Random rand) throws SQLException {

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
    float chanceToAppear = rs.getFloat(i++);
    int hpRequired = rs.getInt(i++);
    int atkRequired = rs.getInt(i++);
    int minDmgDealt = rs.getInt(i++);
    int maxDmgDealt = rs.getInt(i++);
    int durationMinMinutes = rs.getInt(i++);
    int durationMaxMinutes = rs.getInt(i++);
    
    if (null != quality) {
    	String newQuality = quality.trim().toUpperCase();
    	if (!quality.equals(newQuality)) {
    		log.error("quality incorrect: " + quality + "\t id=" + id);
    		quality = newQuality;
    	}
    }
    
    if (chanceToAppear < 0F) {
    	log.error("incorrect chanceToAppear: " + chanceToAppear +
    			". Forcing it to be above 0. id=" + id);
    	chanceToAppear = Math.max(0F, chanceToAppear);
    }
    
    MiniJob miniJob = new MiniJob(id, requiredStructId, miniJobName,
    		cashReward, oilReward, gemReward, monsterIdReward, quality,
    		maxNumMonstersAllowed, chanceToAppear, hpRequired, atkRequired,
    		minDmgDealt, maxDmgDealt, durationMinMinutes, durationMaxMinutes);
    
    miniJob.setRand(rand);
    return miniJob;
  }

}
