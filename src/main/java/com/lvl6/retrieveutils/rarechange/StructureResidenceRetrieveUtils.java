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

import com.lvl6.info.StructureResidence;
import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureResidenceRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureResidence> structIdsToResidences;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_RESIDENCE;

  public static Map<Integer, StructureResidence> getStructIdsToResidences() {
    log.debug("retrieving all structs data");
    if (structIdsToResidences == null) {
      setStaticStructIdsToResidences();
    }
    return structIdsToResidences;
  }

  public static StructureResidence getResidenceForStructId(int structId) {
    log.debug("retrieve struct data for structId " + structId);
    if (structIdsToResidences == null) {
      setStaticStructIdsToResidences();      
    }
    return structIdsToResidences.get(structId);
  }
  
  public static StructureResidence getUpgradedResidenceForStructId(int structId) {
  	log.debug("retrieve upgraded struct data for structId " + structId);
  	if (structIdsToResidences == null) {
      setStaticStructIdsToResidences();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int successorStructId = curStruct.getId();
  		StructureResidence upgradedStruct = structIdsToResidences.get(successorStructId);
  		return upgradedStruct;
  	}
  	return null;
  }
  
  public static StructureResidence getPredecessorResidenceForStructId(int structId) {
  	log.debug("retrieve predecessor struct data for structId " + structId);
  	if (structIdsToResidences == null) {
      setStaticStructIdsToResidences();      
    }
  	Structure curStruct = StructureRetrieveUtils.getUpgradedStructForStructId(structId);
  	if (null != curStruct) {
  		int predecessorStructId = curStruct.getId();
  		StructureResidence predecessorStruct = structIdsToResidences.get(predecessorStructId);
  		return predecessorStruct;
  	}
  	return null;
  }

  private static void setStaticStructIdsToResidences() {
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
			      HashMap<Integer, StructureResidence> structIdsToStructsTemp = new HashMap<Integer, StructureResidence>();
			      while(rs.next()) {
			        StructureResidence struct = convertRSRowToResidence(rs);
			        if (struct != null)
			          structIdsToStructsTemp.put(struct.getStructId(), struct);
			      }
			      structIdsToResidences = structIdsToStructsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("residence retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticStructIdsToResidences();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureResidence convertRSRowToResidence(ResultSet rs) throws SQLException {
    int structId = rs.getInt(DBConstants.STRUCTURE_RESIDENCE__STRUCT_ID);
    int numMonsterSlots = rs.getInt(DBConstants.STRUCTURE_RESIDENCE__NUM_MONSTER_SLOTS);
    int numBonusMonsterSlots = rs.getInt(DBConstants.STRUCTURE_RESIDENCE__NUM_BONUS_MONSTER_SLOTS);
    int numGemsRequired = rs.getInt(DBConstants.STRUCTURE_RESIDENCE__NUM_GEMS_REQUIRED);
    int numAcceptedFbInvites = rs.getInt(DBConstants.STRUCTURE_RESIDENCE__NUM_ACCEPETED_FB_INVITES);
    String occupationName = rs.getString(DBConstants.STRUCTURE_RESIDENCE__OCCUPATION_NAME);
    String imgSuffix = rs.getString(DBConstants.STRUCTURE_RESIDENCE__IMG_SUFFIX);
    
    return new StructureResidence(structId, numMonsterSlots, numBonusMonsterSlots,
    		numGemsRequired, numAcceptedFbInvites, occupationName, imgSuffix);
  }
}
