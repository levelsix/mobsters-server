package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
  private static Map<Integer, Integer> taskIdsToFirstTaskStageIds;
  
  private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE;

  public static Map<Integer, Map<Integer, TaskStage>> gettaskIdsToTaskStageIdsToTaskStages() {
    log.debug("retrieving all task stage data map");
    if (null == taskIdsToTaskStageIdsToTaskStages) {
		  setStatictaskIdsToTaskStageIdsToTaskStages();
	  }
    return taskIdsToTaskStageIdsToTaskStages;
  }
  
  public static TaskStage getTaskStageForTaskStageId(int taskStageId) {
	  if (null == taskStageIdsToTaskStages) {
		  setStatictaskIdsToTaskStageIdsToTaskStages();      
	  }
	  if (!taskStageIdsToTaskStages.containsKey(taskStageId)) {
		  log.warn("no task stage for taskStageId=" + taskStageId);
		  return null;
	  }
	  return taskStageIdsToTaskStages.get(taskStageId); 
  }

  public static Map<Integer, TaskStage> getTaskStagesForTaskId(int taskId) {
    log.debug("retrieve monster data for monster " + taskId);
    if (null == taskIdsToTaskStageIdsToTaskStages) {
		  setStatictaskIdsToTaskStageIdsToTaskStages();
	  }
    return taskIdsToTaskStageIdsToTaskStages.get(taskId);
  }

  public static int getFirstTaskStageIdForTaskId(int taskId) {
	  log.debug("retrieving the first task stage for taskId " + taskId);
	  if (null == taskIdsToFirstTaskStageIds) {
		  setStatictaskIdsToTaskStageIdsToTaskStages();
	  }
	  
	  if (!taskIdsToFirstTaskStageIds.containsKey(taskId)) {
		  log.info("no task for taskId=" + taskId);
		  return 0;
	  }
	  return taskIdsToFirstTaskStageIds.get(taskId);
  }

  private static void setStatictaskIdsToTaskStageIdsToTaskStages() {
    log.debug("setting static map of taskIds to monsters");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
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
			      setTaskIdsToFirstTaskStageIds(taskIdsToTaskStageIdsToTaskStages.keySet());
			      
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("task stage retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }
  
  private static void setTaskIdsToFirstTaskStageIds(Set<Integer> taskIds) {
	  taskIdsToFirstTaskStageIds = new HashMap<Integer, Integer>();
	  for (Integer taskId : taskIds) {
		  int taskStageId = computeFirstTaskStageIdForTaskId(taskId);
		  taskIdsToFirstTaskStageIds.put(taskId, taskStageId);
	  }
	  
  }
  
  private static int computeFirstTaskStageIdForTaskId(int taskId) {
	  Map<Integer, TaskStage> taskStageIdsToTaskStages =
			  taskIdsToTaskStageIdsToTaskStages.get(taskId);
	  
	  Map<Integer, Integer> stageNumToStageId = new HashMap<Integer, Integer>(); 
	  
	  for (Integer taskStageId : taskStageIdsToTaskStages.keySet()) {
		  TaskStage ts = taskStageIdsToTaskStages.get(taskStageId);
		  int taskStageNum = ts.getStageNum();
		  stageNumToStageId.put(taskStageNum, taskStageId);
	  }
	  
	  List<Integer> stageNums = new ArrayList<Integer>(
			  stageNumToStageId.keySet());
	  Collections.sort(stageNums);
	  
	  if (!stageNums.isEmpty()) {
		  int firstStageNum = stageNums.get(0);
		  int firstTaskStageForTaskId = stageNumToStageId.get(firstStageNum);
		  return firstTaskStageForTaskId;  
	  }
	  return 0;
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
    
    TaskStage taskStage = new TaskStage(id, taskId, stageNum);
        
    return taskStage;
  }
}
