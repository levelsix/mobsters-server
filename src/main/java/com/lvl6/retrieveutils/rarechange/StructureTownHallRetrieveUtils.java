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

import com.lvl6.info.StructureTownHall;
import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureTownHallRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureTownHall> structIdsToTownHalls;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_TOWN_HALL;

  public static Map<Integer, StructureTownHall> getStructIdsToTownHalls() {
    log.debug("retrieving all structs data");
    if (structIdsToTownHalls == null) {
      setStaticStructIdsToTownHalls();
    }
    return structIdsToTownHalls;
  }

//  public static StructureTownHall getTownHallRequiredForStructId(int structId) {
//    log.debug("retrieve struct data for structId " + structId);
//    if (structIdsToTownHalls == null) {
//      setStaticStructIdsToTownHalls();      
//    }
//    return structIdsToTownHalls.get(structId);
//  }
  
  public static StructureTownHall getUpgradedTownHallForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToTownHalls == null) {
      setStaticStructIdsToTownHalls();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getId();
  		StructureTownHall upgradedStruct = structIdsToTownHalls.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static StructureTownHall getPredecessorTownHallForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToTownHalls == null) {
      setStaticStructIdsToTownHalls();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureTownHall predecessorStruct = structIdsToTownHalls.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToTownHalls() {
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
			      HashMap<Integer, StructureTownHall> structIdsToStructsTemp = new HashMap<Integer, StructureTownHall>();
			      while(rs.next()) {
			        StructureTownHall struct = convertRSRowToTownHall(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToTownHalls = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("TownHall retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToTownHalls();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureTownHall convertRSRowToTownHall(ResultSet rs) throws SQLException {
    int i = 1;
    int structId = rs.getInt(i++);
    int numResourceOneGenerators = rs.getInt(i++);
    int numResourceOneStorages = rs.getInt(i++);
    int numResourceTwoGenerators = rs.getInt(i++);
    int numResourceTwoStorages = rs.getInt(i++);
    int numHospitals = rs.getInt(i++);
    int numResidences = rs.getInt(i++);
    int numMonsterSlots = rs.getInt(i++);
    int numLabs = rs.getInt(i++);
    
    return new StructureTownHall(structId, numResourceOneGenerators,
    		numResourceOneStorages, numResourceTwoGenerators, numResourceTwoStorages,
    		numHospitals, numResidences, numMonsterSlots, numLabs);
  }
}
