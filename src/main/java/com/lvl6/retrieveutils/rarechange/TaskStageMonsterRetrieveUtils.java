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

import com.lvl6.info.TaskStageMonster;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskStageMonsterRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, Integer>> taskStageIdsToMonsterIdsToQuantity;

  private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE_MONSTER;

  public static Map<Integer, Map<Integer, Integer>> getTaskStageIdsToMonsterIds() {
    log.debug("retrieving all task stage monster data map");
    if (taskStageIdsToMonsterIdsToQuantity == null) {
      setStaticTaskStageIdsToMonsterIds();
    }
    return taskStageIdsToMonsterIdsToQuantity;
  }

  public static List<Integer> getMonsterIdsForTaskStageId(int taskStageId) {
    log.debug("retrieve monster data for monster " + taskStageId);
    if (taskStageIdsToMonsterIdsToQuantity == null) {
      setStaticTaskStageIdsToMonsterIds();      
    }
    List<Integer> monsterIds = new ArrayList<Integer>();
    
    Map<Integer, Integer> monsterIdsToQuantity = taskStageIdsToMonsterIdsToQuantity.get(taskStageId);
    log.info("for taskStageId= " + taskStageId + ", monsterIdsToQuantity=" + 
    		taskStageIdsToMonsterIdsToQuantity);
    for (int monsterId : monsterIdsToQuantity.keySet()) {
    	int quantity = monsterIdsToQuantity.get(monsterId);
    	List<Integer> ids = Collections.nCopies(quantity, monsterId);
    	monsterIds.addAll(ids);
    }
    return monsterIds;
  }


  private static void setStaticTaskStageIdsToMonsterIds() {
    log.debug("setting static map of taskStageIds to monsterIds");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map<Integer, Map<Integer, Integer>> taskStageIdsToMonsterIdsTemp =
              new HashMap<Integer, Map<Integer, Integer>>();
          
          //loop through each row and convert it into a java object
          while(rs.next()) {  
            TaskStageMonster taskStageMonster = convertRSRowToTaskStageMonster(rs);
            if (taskStageMonster == null) {
              continue;
            }
            
            int stageId = taskStageMonster.getStageId();
            int monsterId = taskStageMonster.getMonsterId();
            if (!taskStageIdsToMonsterIdsTemp.containsKey(stageId)) {
              taskStageIdsToMonsterIdsTemp.put(stageId, new HashMap<Integer, Integer>());
            }

            Map<Integer, Integer> monsterIds = taskStageIdsToMonsterIdsTemp.get(stageId);
            monsterIds.put(monsterId, 1);
          }
          taskStageIdsToMonsterIdsToQuantity = taskStageIdsToMonsterIdsTemp;
          
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs, null, conn);
  }

  public static void reload() {
    setStaticTaskStageIdsToMonsterIds();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static TaskStageMonster convertRSRowToTaskStageMonster(ResultSet rs) throws SQLException {
    int i = 1;
    int monsterId = rs.getInt(i++);
    int stageId = rs.getInt(i++);
    
    TaskStageMonster taskStageMonster = new TaskStageMonster(monsterId, stageId);
        
    return taskStageMonster;
  }
}
