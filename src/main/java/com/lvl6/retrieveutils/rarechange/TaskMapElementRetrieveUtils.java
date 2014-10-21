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

import com.lvl6.info.TaskMapElement;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskMapElementRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, TaskMapElement> idToTaskMapElement;
  private static Map<Integer, TaskMapElement> taskIdToTaskMapElement;
  
  private static final String TABLE_NAME = DBConstants.TABLE_TASK_MAP_ELEMENT;


  public static Map<Integer, TaskMapElement> getTaskMapElement() {
    log.debug("retrieve task map elements");
    if (null == idToTaskMapElement) {
    	setStaticIdToTaskMapElement();
	  }
    return idToTaskMapElement;
  }

  public static TaskMapElement getTaskMapElementForTaskId(int taskId) {
	  log.debug(String.format("retrieve task map element for taskId=%s", taskId));
	  if (null == idToTaskMapElement) {
		  setStaticIdToTaskMapElement();
	  }
	  if (!taskIdToTaskMapElement.containsKey(taskId)) {
		  log.error(String.format("no task map element for taskId=%s", taskId));
		  return null;
	  }
	  return taskIdToTaskMapElement.get(taskId);
  }

  private static void setStaticIdToTaskMapElement() {
    log.debug("setting static map of ids to TaskMapElement elements");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    Map<Integer, TaskMapElement> idsToTaskMapElementsTemp =
    	new HashMap<Integer, TaskMapElement>();
    Map<Integer, TaskMapElement> taskIdsToTaskMapElementsTemp =
    	new HashMap<Integer, TaskMapElement>();
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      //loop throughe each row and convert it into a java object
			      while(rs.next()) {  
			        TaskMapElement taskMap = convertRSRowToTaskMapElement(rs);
			        if (taskMap == null) {
			          continue;
			        }
			        
			        int id = taskMap.getId();
			        idsToTaskMapElementsTemp.put(id, taskMap);
			        taskIdsToTaskMapElementsTemp.put(taskMap.getTaskId(), taskMap);
			      }
			      
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("task map retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    idToTaskMapElement = idsToTaskMapElementsTemp;
    taskIdToTaskMapElement = taskIdsToTaskMapElementsTemp;
  }
  
  public static void reload() {
	  setStaticIdToTaskMapElement();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static TaskMapElement convertRSRowToTaskMapElement(ResultSet rs) throws SQLException {
    int id = rs.getInt(DBConstants.TASK_MAP_ELEMENT__ID);
    int taskId = rs.getInt(DBConstants.TASK_MAP_ELEMENT__TASK_ID);
    int xPos = rs.getInt(DBConstants.TASK_MAP_ELEMENT__X_POS);
    int yPos = rs.getInt(DBConstants.TASK_MAP_ELEMENT__Y_POS);
    String element = rs.getString(DBConstants.TASK_MAP_ELEMENT__ELEMENT);
    boolean boss = rs.getBoolean(DBConstants.TASK_MAP_ELEMENT__IS_BOSS);
    String bossImgName = rs.getString(DBConstants.TASK_MAP_ELEMENT__BOSS_IMG_NAME);
    int itemDropId = rs.getInt(DBConstants.TASK_MAP_ELEMENT__ITEM_DROP_ID); 
    String sectionName = rs.getString(DBConstants.TASK_MAP_ELEMENT__SECTION_NAME);
    int cashReward = rs.getInt(DBConstants.TASK_MAP_ELEMENT__CASH_REWARD);
    int oilReward = rs.getInt(DBConstants.TASK_MAP_ELEMENT__OIL_REWARD);
    String characterImgName = rs.getString(DBConstants.TASK_MAP_ELEMENT__CHARACTER_IMG_NAME);
    int charImgVertPixelOffset = rs.getInt(DBConstants.TASK_MAP_ELEMENT__CHAR_VERT_PIXEL_OFFSET);
    int charImgHorizPixelOffset = rs.getInt(DBConstants.TASK_MAP_ELEMENT__CHAR_HORIZ_PIXEL_OFFSET);
    float charImgScaleFactor = rs.getFloat(DBConstants.TASK_MAP_ELEMENT__CHAR_SCALE_FACTOR);
        
    TaskMapElement taskMap = new TaskMapElement(id, taskId, xPos, yPos,
    	element, boss, bossImgName, itemDropId, sectionName, cashReward,
    	oilReward, characterImgName, charImgVertPixelOffset,
    	charImgHorizPixelOffset, charImgScaleFactor);
        
    return taskMap;
  }
}
