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

import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureResourceGeneratorRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureResourceGenerator> structIdsToResourceGenerators;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_RESOURCE_GENERATOR;

  public static Map<Integer, StructureResourceGenerator> getStructIdsToResourceGenerators() {
    log.debug("retrieving all structs data");
    if (structIdsToResourceGenerators == null) {
      setStaticStructIdsToResourceGenerators();
    }
    return structIdsToResourceGenerators;
  }

  public static StructureResourceGenerator getResourceGeneratorForStructId(int structId) {
    log.debug("retrieve struct data for structId " + structId);
    if (structIdsToResourceGenerators == null) {
      setStaticStructIdsToResourceGenerators();      
    }
    return structIdsToResourceGenerators.get(structId);
  }
  
  public static StructureResourceGenerator getUpgradedResourceGeneratorForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToResourceGenerators == null) {
      setStaticStructIdsToResourceGenerators();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getId();
  		StructureResourceGenerator upgradedStruct = structIdsToResourceGenerators.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static StructureResourceGenerator getPredecessorResourceGeneratorForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToResourceGenerators == null) {
      setStaticStructIdsToResourceGenerators();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureResourceGenerator predecessorStruct = structIdsToResourceGenerators.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToResourceGenerators() {
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
			      HashMap<Integer, StructureResourceGenerator> structIdsToStructsTemp = new HashMap<Integer, StructureResourceGenerator>();
			      while(rs.next()) {
			        StructureResourceGenerator struct = convertRSRowToResourceGenerator(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToResourceGenerators = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("resourceGenerator retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToResourceGenerators();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureResourceGenerator convertRSRowToResourceGenerator(ResultSet rs) throws SQLException {
    int i = 1;
    int structId = rs.getInt(i++);
    String resourceTypeGenerated = rs.getString(i++);
    float productionRate = rs.getFloat(i++);
    int capacity = rs.getInt(i++);
    
    return new StructureResourceGenerator(structId, resourceTypeGenerated, productionRate,
    		capacity);
  }
}
