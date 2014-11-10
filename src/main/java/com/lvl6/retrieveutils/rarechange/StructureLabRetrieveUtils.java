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

import com.lvl6.info.StructureLab;
import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureLabRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureLab> structIdsToLabs;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_LAB_CONFIG;

  public static Map<Integer, StructureLab> getStructIdsToLabs() {
    log.debug("retrieving all structs data");
    if (structIdsToLabs == null) {
      setStaticStructIdsToLabs();
    }
    return structIdsToLabs;
  }

  public static StructureLab getLabForStructId(int structId) {
    log.debug("retrieve struct data for structId " + structId);
    if (structIdsToLabs == null) {
      setStaticStructIdsToLabs();      
    }
    return structIdsToLabs.get(structId);
  }
  
  public static StructureLab getUpgradedLabForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToLabs == null) {
      setStaticStructIdsToLabs();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getId();
  		StructureLab upgradedStruct = structIdsToLabs.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static StructureLab getPredecessorLabForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToLabs == null) {
      setStaticStructIdsToLabs();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureLab predecessorStruct = structIdsToLabs.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToLabs() {
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
			      HashMap<Integer, StructureLab> structIdsToStructsTemp = new HashMap<Integer, StructureLab>();
			      while(rs.next()) {
			        StructureLab struct = convertRSRowToLab(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToLabs = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("Lab retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToLabs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureLab convertRSRowToLab(ResultSet rs) throws SQLException {
    int structId = rs.getInt(DBConstants.STRUCTURE_LAB__STRUCT_ID);
    int queueSize = rs.getInt(DBConstants.STRUCTURE_LAB__QUEUE_SIZE);
    float pointsMultiplier = rs.getFloat(DBConstants.STRUCTURE_LAB__POINTS_MULTIPLIER);
    float pointsPerSecond = rs.getFloat(DBConstants.STRUCTURE_LAB__POINTS_PER_SECOND);
    
    return new StructureLab(structId, queueSize, pointsMultiplier, pointsPerSecond);
  }
}
