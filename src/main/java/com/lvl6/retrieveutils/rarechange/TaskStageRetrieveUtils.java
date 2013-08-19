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

import com.lvl6.info.TaskStage;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskStageRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, TaskStage>> taskIdsToTaskStageIdsToTaskStages;
  private static Map<Integer, TaskStage> taskStageIdsToTaskStages;

  private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE;

  public static Map<Integer, Map<Integer, TaskStage>> gettaskIdsToTaskStageIdsToTaskStages() {
    log.debug("retrieving all task stage data map");
    if (taskIdsToTaskStageIdsToTaskStages == null) {
      setStatictaskIdsToTaskStageIdsToTaskStages();
    }
    return taskIdsToTaskStageIdsToTaskStages;
  }
  
  public static TaskStage getTaskStageForTaskStageId(int taskStageId) {
	  if (taskStageIdsToTaskStages == null) {
		  setStatictaskIdsToTaskStageIdsToTaskStages();      
	  }
	  return taskStageIdsToTaskStages.get(taskStageId); 
  }

  public static Map<Integer, TaskStage> getTaskStagesForTaskId(int taskId) {
    log.debug("retrieve monster data for monster " + taskId);
    if (taskIdsToTaskStageIdsToTaskStages == null) {
      setStatictaskIdsToTaskStageIdsToTaskStages();      
    }
    return taskIdsToTaskStageIdsToTaskStages.get(taskId);
  }


  private static void setStatictaskIdsToTaskStageIdsToTaskStages() {
    log.debug("setting static map of taskIds to monsters");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map<Integer, Map<Integer, TaskStage>> taskIdsToTaskStageIdsToTaskStagesTemp =
              new HashMap<Integer, Map<Integer, TaskStage>>();
          Map<Integer, TaskStage> taskStageIdsToTaskStagesTemp =
        		  new HashMap<Integer, TaskStage>();
          //loop throughe each row and convert it into a java object
          while(rs.next()) {  
            TaskStage taskStage = convertRSRowToTaskStage(rs);
            if (taskStage == null) {
              continue;
            }
            
            int taskId = taskStage.getTaskId();
            //base case, no key with task id exists, so create map with
            //key: task id, to value: another map
            if (!taskIdsToTaskStageIdsToTaskStagesTemp.containsKey(taskId)) {
              taskIdsToTaskStageIdsToTaskStagesTemp.put(taskId, new HashMap<Integer, TaskStage>());
            }

            //get map of task stages related to current task id
            //stick taskStage into the map of TaskStage ids to TaskStage objects
            Map<Integer, TaskStage> taskStageIdsToTaskStagesForTask =
                taskIdsToTaskStageIdsToTaskStagesTemp.get(taskId);
            
            int taskStageId = taskStage.getId();
            taskStageIdsToTaskStagesForTask.put(taskStageId, taskStage);
            taskStageIdsToTaskStagesTemp.put(taskStageId, taskStage);
          }
          taskIdsToTaskStageIdsToTaskStages = taskIdsToTaskStageIdsToTaskStagesTemp;
          taskStageIdsToTaskStages = taskStageIdsToTaskStagesTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs, null, conn);
  }

  public static void reload() {
    setStatictaskIdsToTaskStageIdsToTaskStages();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static TaskStage convertRSRowToTaskStage(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int taskId = rs.getInt(i++);
    int stageNum = rs.getInt(i++);
    float equipDropRate = rs.getFloat(i++);
    
    TaskStage taskStage = new TaskStage(id, taskId, stageNum, equipDropRate);
        
    return taskStage;
  }
}
