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

import com.lvl6.info.MiniTask;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class MiniTaskRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, MiniTask> miniTaskIdsToMiniTasks;
  private static Map<Integer, Map<Integer, MiniTask>> structureIdToMiniTaskIdToMiniTask;
  

  private static final String TABLE_NAME = DBConstants.TABLE_MINI_TASK;

  //CONTROLLER LOGIC******************************************************************

  //RETRIEVE QUERIES*********************************************************************
  public static Map<Integer, MiniTask> getMiniTaskIdsToMiniTasks() {
    log.debug("retrieving all miniTask data");
    if (miniTaskIdsToMiniTasks == null) {
      setStaticMiniTaskIdsToMiniTasks();
    }
    return miniTaskIdsToMiniTasks;
  }

  public static MiniTask getMiniTaskForMiniTaskId(int miniTaskId) {
    log.debug("retrieving miniTask with miniTaskId " + miniTaskId);
    if (null == miniTaskIdsToMiniTasks) {
      setStaticMiniTaskIdsToMiniTasks();
    }
    return miniTaskIdsToMiniTasks.get(miniTaskId);
  }

  private static void setStaticMiniTaskIdsToMiniTasks() {
    log.debug("setting static map of miniTaskIds to miniTasks");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, MiniTask> tmp = new HashMap<Integer, MiniTask>();
			      Map<Integer, Map<Integer, MiniTask>> structIdToMiniTaskIdToMiniTask =
			    		  new HashMap<Integer, Map<Integer, MiniTask>>();
			      
			      while(rs.next()) {
			        MiniTask miniTask = convertRSRowToMiniTask(rs);
			        if (null == miniTask) {
			        	continue;
			        }

			        int structId = miniTask.getRequiredStructId();
			        if (!structIdToMiniTaskIdToMiniTask.containsKey(structId)) {
			        	structIdToMiniTaskIdToMiniTask.put(structId,
			        			new HashMap<Integer, MiniTask>());
			        }
			        int miniTaskId = miniTask.getId();
			        
			        tmp.put(miniTaskId, miniTask);
			        
			        Map<Integer, MiniTask> miniTaskIdToMiniTask =
			        		structIdToMiniTaskIdToMiniTask.get(structId);
			        miniTaskIdToMiniTask.put(miniTaskId, miniTask);
			        
			      }
			      miniTaskIdsToMiniTasks = tmp;
			      structureIdToMiniTaskIdToMiniTask = structIdToMiniTaskIdToMiniTask;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }
			}
		} catch (Exception e) {
    	log.error("miniTask retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticMiniTaskIdsToMiniTasks();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static MiniTask convertRSRowToMiniTask(ResultSet rs) throws SQLException {

    int i = 1;
    int id = rs.getInt(i++);
    int requiredStructId = rs.getInt(i++);
    String miniTaskName = rs.getString(i++);
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
    
    MiniTask miniTask = new MiniTask(id, requiredStructId, miniTaskName,
    		cashReward, oilReward, gemReward, monsterIdReward, quality,
    		maxNumMonstersAllowed, chanceToAppear, hpRequired, atkRequired,
    		minDmgDealt, maxDmgDealt);
    
    return miniTask;
  }

}
