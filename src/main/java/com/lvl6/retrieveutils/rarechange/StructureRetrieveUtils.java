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

import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Structure> structIdsToStructs;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE;

  public static Map<Integer, Structure> getStructIdsToStructs() {
    log.debug("retrieving all structs data");
    if (structIdsToStructs == null) {
      setStaticStructIdsToStructs();
    }
    return structIdsToStructs;
  }

  public static Structure getStructForStructId(int structId) {
    log.debug("retrieve struct data for structId " + structId);
    if (structIdsToStructs == null) {
      setStaticStructIdsToStructs();      
    }
    return structIdsToStructs.get(structId);
  }
  
  public static Structure getUpgradedStructForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToStructs == null) {
      setStaticStructIdsToStructs();      
    }
  	Structure curStruct = structIdsToStructs.get(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getSuccessorStructId(); 
  		
  		Structure upgradedStruct = structIdsToStructs.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static Structure getPredecessorStructForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToStructs == null) {
      setStaticStructIdsToStructs();      
    }
  	Structure curStruct = structIdsToStructs.get(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getPredecessorStructId();
  		
  		Structure predecessorStruct = structIdsToStructs.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToStructs() {
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
			      HashMap<Integer, Structure> structIdsToStructsTemp = new HashMap<Integer, Structure>();
			      while(rs.next()) {
			        Structure struct = convertRSRowToStruct(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getId(), struct);
			      }
			      structIdsToStructs = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("structure retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToStructs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Structure convertRSRowToStruct(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String name = rs.getString(i++);
    int level = rs.getInt(i++);
    String structType = rs.getString(i++);
    String buildResourceType = rs.getString(i++);
    int buildCost = rs.getInt(i++);
    int minutesToBuild = rs.getInt(i++);
    int requiredTownHallId = rs.getInt(i++);
    int width = rs.getInt(i++);
    int height = rs.getInt(i++);
    String spriteImgName = rs.getString(i++);
    int predecessorStructId = rs.getInt(i++);
    int successorStructId = rs.getInt(i++);
    
    Structure s = new Structure(id, name, level, structType, buildResourceType, buildCost,
    		minutesToBuild, requiredTownHallId, width, height, spriteImgName,
    		predecessorStructId, successorStructId);
    
    if (null != structType) {
    	String newStructType = structType.trim();
    	newStructType = newStructType.toUpperCase();
    	if (!structType.equals(newStructType)) {
    		log.error("string for struct type is incorrect. is=" + structType +
    				"\t (if spelled correctly) expected=" + newStructType + "\t struct obj=" + s);
    		s.setStructType(newStructType);
    	}
    	
    } else {
    	log.error("structure obj's struct type is null!!!. obj=" + s);
    }
    
    if (null != buildResourceType) {
    	String newBuildResourceType = buildResourceType.trim();
    	newBuildResourceType = newBuildResourceType.toUpperCase();
    	if (!buildResourceType.equals(newBuildResourceType)) {
    		log.error("string for resource type is incorrect. is=" + buildResourceType +
    				"\t (if spelled correctly) expected=" + newBuildResourceType +
    				"\t struct obj=" + s);
    		s.setBuildResourceType(newBuildResourceType);
    	}
    	
    } else {
    	log.error("structure obj's resource type is null!!!. obj=" + s);
    }
    return s;
  }
}
