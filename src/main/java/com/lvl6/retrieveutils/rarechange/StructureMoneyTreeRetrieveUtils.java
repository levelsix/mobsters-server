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

import com.lvl6.info.StructureMoneyTree;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class StructureMoneyTreeRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, StructureMoneyTree> structIdsToMoneyTrees;

  private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_MONEY_TREE_CONFIG;

  public static Map<Integer, StructureMoneyTree> getStructIdsToMoneyTrees() {
    log.debug("retrieving all structs data");
    if (structIdsToMoneyTrees == null) {
      setStaticStructIdsToMoneyTrees();
    }
    return structIdsToMoneyTrees;
  }

  public static StructureMoneyTree getMoneyTreeForStructId(int structId) {
    log.debug("retrieve money tree data for structId " + structId);
    if (structIdsToMoneyTrees == null) {
      setStaticStructIdsToMoneyTrees();      
    }
    return structIdsToMoneyTrees.get(structId);
  }

  private static void setStaticStructIdsToMoneyTrees() {
    log.debug("setting static map of structIds to money trees");

    HashMap<Integer, StructureMoneyTree> structIdsToMoneyTreesTemp = new HashMap<Integer, StructureMoneyTree>();
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      while(rs.next()) {
			        StructureMoneyTree struct = convertRSRowToStructureMoneyTree(rs);
			        if (struct != null)
			          structIdsToMoneyTreesTemp.put(struct.getStructId(), struct);
			      }
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("Money Tree retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    structIdsToMoneyTrees = structIdsToMoneyTreesTemp;
  }

  public static void reload() {
    setStaticStructIdsToMoneyTrees();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static StructureMoneyTree convertRSRowToStructureMoneyTree(ResultSet rs) throws SQLException {
    int structId = rs.getInt(DBConstants.STRUCTURE_MONEY_TREE__STRUCT_ID);
    float productionRate = rs.getInt(DBConstants.STRUCTURE_MONEY_TREE__PRODUCTION_RATE);
    int capacity = rs.getInt(DBConstants.STRUCTURE_MONEY_TREE__CAPACITY);
    int daysOfDuration = rs.getInt(DBConstants.STRUCTURE_MONEY_TREE__DAYS_OF_DURATION);
    int daysForRenewal = rs.getInt(DBConstants.STRUCTURE_MONEY_TREE__DAYS_FOR_RENEWAL);
    
    if(structId < 10000) { //money tree ids should be 1000 or greater
    	log.error(String.format(
    			"structId incorrect, should be > 10000. structId = %s",
    			structId));
    }
    
    if(productionRate < 0 || productionRate > capacity) {
    	log.error(String.format(
    			"something's prob fucked up with the "
    			+ "production rate and capacity numbers"));
    }
    
    return new StructureMoneyTree(structId, productionRate, capacity, daysOfDuration, daysForRenewal);
  }
}
