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
  
  private static final String TABLE_NAME = DBConstants.TABLE_TASK_MAP_ELEMENT;


  public static Map<Integer, TaskMapElement> getTaskMapElement() {
    log.debug("retrieve task map elements");
    if (null == idToTaskMapElement) {
    	setStaticIdToTaskMapElement();
	  }
    return idToTaskMapElement;
  }


  private static void setStaticIdToTaskMapElement() {
    log.debug("setting static map of ids to TaskMapElement elements");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    Map<Integer, TaskMapElement> idsToTaskMapElementsTemp =
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
        
    TaskMapElement taskMap = new TaskMapElement(id, taskId, xPos, yPos,
    	element, boss, bossImgName, itemDropId, sectionName);
        
    return taskMap;
  }
}
