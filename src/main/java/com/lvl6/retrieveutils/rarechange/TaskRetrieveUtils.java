package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Task;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, List<Task>> cityIdsToTasks;
  private static Map<Integer, Task> taskIdsToTasks;

  private static final String TABLE_NAME = DBConstants.TABLE_TASK;

  //CONTROLLER LOGIC******************************************************************
  public static int getTaskIdForCityElement(int cityId, int assetId) {
	  log.debug("retrieving task id for city element, cityId=" + cityId +
			  " assetId=" + assetId);
	  if (null == cityIdsToTasks) {
		  setStaticCityIdsToTasks();
	  }
	  
	  if (!cityIdsToTasks.containsKey(cityId)) {
		  log.warn("no task for cityId" + cityId + " assetId=" + assetId);
		  return 0;
	  }
	  
	  List<Task> tasksForCity = cityIdsToTasks.get(cityId);
	  
	  for (Task aTask : tasksForCity) {
		  if (aTask.getAssetNumberWithinCity() == assetId) {
			  return aTask.getId();
		  }
	  }
	  log.error("no task id for city element, cityId=" + cityId +
			  " assetId=" + assetId);
	  
	  return 0;
  }
  
  //RETRIEVE QUERIES*********************************************************************


  public static Map<Integer, Task> getTaskIdsToTasks() {
    log.debug("retrieving all tasks data map");
    if (taskIdsToTasks == null) {
      setStaticTaskIdsToTasks();
    }
    return taskIdsToTasks;
  }

  public static Task getTaskForTaskId(int taskId) {
    log.debug("retrieve task data for task " + taskId);
    if (taskIdsToTasks == null) {
      setStaticTaskIdsToTasks();      
    }
    return taskIdsToTasks.get(taskId);
  }

  public static Map<Integer, Task> getTasksForTaskIds(List<Integer> ids) {
    log.debug("retrieve task data for taskids " + ids);
    if (taskIdsToTasks == null) {
      setStaticTaskIdsToTasks();      
    }
    Map<Integer, Task> toreturn = new HashMap<Integer, Task>();
    for (Integer id : ids) {
      toreturn.put(id,  taskIdsToTasks.get(id));
    }
    return toreturn;
  }

  public static List<Task> getAllTasksForCityId(int cityId) {
    log.debug("retrieving all tasks for cityId " + cityId);
    if (cityIdsToTasks == null) {
      setStaticCityIdsToTasks();
    }
    return cityIdsToTasks.get(cityId);
  }
  
  public static Set<Integer> getAllTaskIdsForCityId(int cityId) {
  	log.debug("retrieving all taskIds for cityId=" + cityId);
  	if (cityIdsToTasks == null) {
      setStaticCityIdsToTasks();
    }
  	List<Task> tasksForCity = null;
  	tasksForCity = cityIdsToTasks.get(cityId);
  	
  	if (null == tasksForCity) {
  		return new HashSet<Integer>();
  	}
  	
  	Set<Integer> retVal = new HashSet<Integer>();
  	for (Task t : tasksForCity) {
  		int taskId = t.getId();
  		retVal.add(taskId);
  	}
  	return retVal;
  }
  
  public static int getCityIdForTask(int taskId) {
    if (cityIdsToTasks == null) {
      setStaticCityIdsToTasks();
    }
    
    
    if (!taskIdsToTasks.containsKey(taskId)) {
    	return 0;
    }
    
    Task aTask = taskIdsToTasks.get(taskId);
    int cityId = aTask.getCityId();
    return cityId;
  }

  private static void setStaticCityIdsToTasks() {
    log.debug("setting static map of cityId to tasks");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, List<Task>> cityIdToTasksTemp = new HashMap<Integer, List<Task>>();
			      while(rs.next()) {  //should only be one
			        Task task = convertRSRowToTask(rs);
			        if (task != null) {
			          if (cityIdToTasksTemp.get(task.getCityId()) == null) {
			            cityIdToTasksTemp.put(task.getCityId(), new ArrayList<Task>());
			          }
			          cityIdToTasksTemp.get(task.getCityId()).add(task);
			        }
			      }
			      cityIdsToTasks = cityIdToTasksTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("task retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  private static void setStaticTaskIdsToTasks() {
    log.debug("setting static map of taskIds to tasks");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      HashMap<Integer, Task> taskIdsToTasksTemp = new HashMap<Integer, Task>();
			      while(rs.next()) {  //should only be one
			        Task task = convertRSRowToTask(rs);
			        if (task != null)
			          taskIdsToTasksTemp.put(task.getId(), task);
			      }
			      taskIdsToTasks = taskIdsToTasksTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("task retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticCityIdsToTasks();
    setStaticTaskIdsToTasks();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Task convertRSRowToTask(ResultSet rs) throws SQLException {
    int id = rs.getInt(DBConstants.TASK__ID);
    String goodName = rs.getString(DBConstants.TASK__GOOD_NAME);
    String description = rs.getString(DBConstants.TASK__DESCRIPTION);
    int cityId = rs.getInt(DBConstants.TASK__CITY_ID);
//    int energyCost = rs.getInt(DBConstants.);
    int assetNumberWithinCity = rs.getInt(DBConstants.TASK__ASSET_NUM_WITHIN_CITY);
    int prerequisiteTaskId = rs.getInt(DBConstants.TASK__PREREQUISITE_TASK_ID);
    int prerequisiteQuestId = rs.getInt(DBConstants.TASK__PREREQUISITE_QUEST_ID);
    int boardWidth = rs.getInt(DBConstants.TASK__BOARD_WIDTH);
    int boardHeight = rs.getInt(DBConstants.TASK__BOARD_HEIGHT);
    String groundImgPrefix = rs.getString(DBConstants.TASK__GROUND_IMG_PREFIX);
    
    Task task = new Task(id, goodName, description, cityId, assetNumberWithinCity,
    		prerequisiteTaskId, prerequisiteQuestId, boardWidth, boardHeight,
    		groundImgPrefix);
    return task;
  }
}
