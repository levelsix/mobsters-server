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

import com.lvl6.info.StructureMiniJob;
import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureMiniJobRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureMiniJob> structIdsToMiniJobs;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_MINI_JOB_CONFIG;

  public static Map<Integer, StructureMiniJob> getStructIdsToMiniJobs() {
    log.debug("retrieving all StructureMiniJob data");
    if (structIdsToMiniJobs == null) {
      setStaticStructIdsToMiniJobs();
    }
    return structIdsToMiniJobs;
  }

  public static StructureMiniJob getMiniJobForStructId(int structId) {
    log.debug("retrieve StructureMiniJob for structId " + structId);
    if (structIdsToMiniJobs == null) {
      setStaticStructIdsToMiniJobs();      
    }
    return structIdsToMiniJobs.get(structId);
  }
  
  public static StructureMiniJob getPredecessorMiniJobForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToMiniJobs == null) {
      setStaticStructIdsToMiniJobs();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureMiniJob predecessorStruct = structIdsToMiniJobs.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToMiniJobs() {
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
			      HashMap<Integer, StructureMiniJob> structIdsToStructsTemp = new HashMap<Integer, StructureMiniJob>();
			      while(rs.next()) {
			        StructureMiniJob struct = convertRSRowToMiniJob(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToMiniJobs = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("MiniJob retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToMiniJobs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureMiniJob convertRSRowToMiniJob(ResultSet rs) throws SQLException {
    int structId = rs.getInt(DBConstants.STRUCTURE_MINI_JOB__STRUCT_ID);
    int generatedJobLimit = rs.getInt(DBConstants.STRUCTURE_MINI_JOB__GENERATED_JOB_LIMIT);
    int hoursBetweenJobGeneration = rs.getInt(DBConstants.STRUCTURE_MINI_JOB__HOURS_BETWEEN_JOB_GENERATION);
    
    return new StructureMiniJob(structId, generatedJobLimit, hoursBetweenJobGeneration);
  }
}
