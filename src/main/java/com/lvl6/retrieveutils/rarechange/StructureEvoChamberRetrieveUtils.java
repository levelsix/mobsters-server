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

import com.lvl6.info.StructureEvoChamber;
import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureEvoChamberRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureEvoChamber> structIdsToEvoChambers;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_EVO_CHAMBER;

  public static Map<Integer, StructureEvoChamber> getStructIdsToEvoChambers() {
    log.debug("retrieving all structs data");
    if (structIdsToEvoChambers == null) {
      setStaticStructIdsToEvoChambers();
    }
    return structIdsToEvoChambers;
  }

  public static StructureEvoChamber getEvoChamberForStructId(int structId) {
    log.debug("retrieve struct data for structId " + structId);
    if (structIdsToEvoChambers == null) {
      setStaticStructIdsToEvoChambers();      
    }
    return structIdsToEvoChambers.get(structId);
  }
  
  public static StructureEvoChamber getUpgradedEvoChamberForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToEvoChambers == null) {
      setStaticStructIdsToEvoChambers();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getId();
  		StructureEvoChamber upgradedStruct = structIdsToEvoChambers.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static StructureEvoChamber getPredecessorEvoChamberForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToEvoChambers == null) {
      setStaticStructIdsToEvoChambers();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureEvoChamber predecessorStruct = structIdsToEvoChambers.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToEvoChambers() {
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
			      HashMap<Integer, StructureEvoChamber> structIdsToStructsTemp = new HashMap<Integer, StructureEvoChamber>();
			      while(rs.next()) {
			        StructureEvoChamber struct = convertRSRowToEvoChamber(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToEvoChambers = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("EvoChamber retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToEvoChambers();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureEvoChamber convertRSRowToEvoChamber(ResultSet rs) throws SQLException {
    int structId = rs.getInt(DBConstants.STRUCTURE_EVO__STRUCT_ID);
    String qualityUnlocked = rs.getString(DBConstants.STRUCTURE_EVO__QUALITY_UNLOCKED);
    int evoTierUnlocked = rs.getInt(DBConstants.STRUCTURE_EVO__EVO_TIER_UNLOCKED);
    
    if (null != qualityUnlocked) {
    	String newQualityUnlocked = qualityUnlocked.trim().toUpperCase();
    	if (!qualityUnlocked.equals(newQualityUnlocked)) {
    		log.error(String.format(
    			"qualityUnlocked incorrect: %s, structId=%s",
    			qualityUnlocked, structId));
    		qualityUnlocked = newQualityUnlocked;
    	}
    }
    
    return new StructureEvoChamber(structId, qualityUnlocked, evoTierUnlocked);
  }
}
