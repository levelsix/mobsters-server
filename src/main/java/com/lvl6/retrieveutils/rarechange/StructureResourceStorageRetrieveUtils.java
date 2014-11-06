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
import com.lvl6.info.StructureResourceStorage;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureResourceStorageRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureResourceStorage> structIdsToResourceStorages;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_RESOURCE_STORAGE;

  public static Map<Integer, StructureResourceStorage> getStructIdsToResourceStorages() {
    log.debug("retrieving all structs data");
    if (structIdsToResourceStorages == null) {
      setStaticStructIdsToResourceStorages();
    }
    return structIdsToResourceStorages;
  }

  public static StructureResourceStorage getResourceStorageForStructId(int structId) {
    log.debug("retrieve struct data for structId " + structId);
    if (structIdsToResourceStorages == null) {
      setStaticStructIdsToResourceStorages();      
    }
    return structIdsToResourceStorages.get(structId);
  }
  
  public static StructureResourceStorage getUpgradedResourceStorageForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToResourceStorages == null) {
      setStaticStructIdsToResourceStorages();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getId();
  		StructureResourceStorage upgradedStruct = structIdsToResourceStorages.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static StructureResourceStorage getPredecessorResourceStorageForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToResourceStorages == null) {
      setStaticStructIdsToResourceStorages();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureResourceStorage predecessorStruct = structIdsToResourceStorages.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToResourceStorages() {
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
			      HashMap<Integer, StructureResourceStorage> structIdsToStructsTemp = new HashMap<Integer, StructureResourceStorage>();
			      while(rs.next()) {
			        StructureResourceStorage struct = convertRSRowToResourceStorage(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToResourceStorages = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("resourceStorage retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToResourceStorages();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureResourceStorage convertRSRowToResourceStorage(ResultSet rs) throws SQLException {
    int structId = rs.getInt(DBConstants.STRUCTURE_RESOURCE_STORAGE__STRUCT_ID);
    String resourceTypeStored = rs.getString(DBConstants.STRUCTURE_RESOURCE_STORAGE__RESOURCE_TYPE_STORED);
    int capacity = rs.getInt(DBConstants.STRUCTURE_RESOURCE_STORAGE__CAPACITY);
    
    if (null != resourceTypeStored) {
    	String newResourceTypeStored = resourceTypeStored.trim().toUpperCase();
    	if (!resourceTypeStored.equals(newResourceTypeStored)) {
    		log.error(String.format(
    			"incorrect ResourceType. %s, id=%s",
    				resourceTypeStored, structId));
    		resourceTypeStored = newResourceTypeStored;
    	}
    }

    StructureResourceStorage srs = new StructureResourceStorage(structId,
    		resourceTypeStored, capacity);

    return srs;
  }
}
