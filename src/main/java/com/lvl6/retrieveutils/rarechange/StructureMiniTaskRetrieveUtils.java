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

import com.lvl6.info.StructureMiniTask;
import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureMiniTaskRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureMiniTask> structIdsToMiniTasks;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_LAB;

  public static Map<Integer, StructureMiniTask> getStructIdsToMiniTasks() {
    log.debug("retrieving all struct mini task data");
    if (structIdsToMiniTasks == null) {
      setStaticStructIdsToMiniTasks();
    }
    return structIdsToMiniTasks;
  }

  public static StructureMiniTask getMiniTaskForStructId(int structId) {
    log.debug("retrieve struct data for structId " + structId);
    if (structIdsToMiniTasks == null) {
      setStaticStructIdsToMiniTasks();      
    }
    return structIdsToMiniTasks.get(structId);
  }
  
  public static StructureMiniTask getUpgradedMiniTaskForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToMiniTasks == null) {
      setStaticStructIdsToMiniTasks();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getId();
  		StructureMiniTask upgradedStruct = structIdsToMiniTasks.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static StructureMiniTask getPredecessorMiniTaskForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToMiniTasks == null) {
      setStaticStructIdsToMiniTasks();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureMiniTask predecessorStruct = structIdsToMiniTasks.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToMiniTasks() {
    log.debug("setting static map of structIds to structs");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      HashMap<Integer, StructureMiniTask> structIdsToStructsTemp = new HashMap<Integer, StructureMiniTask>();
			      while(rs.next()) {
			        StructureMiniTask struct = convertRSRowToMiniTask(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToMiniTasks = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("MiniTask retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToMiniTasks();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureMiniTask convertRSRowToMiniTask(ResultSet rs) throws SQLException {
    int i = 1;
    int structId = rs.getInt(i++);
    int generatedTaskLimit = rs.getInt(i++);
    int hoursBetweenTaskGeneration = rs.getInt(i++);
    
    return new StructureMiniTask(structId, generatedTaskLimit, hoursBetweenTaskGeneration);
  }
}
