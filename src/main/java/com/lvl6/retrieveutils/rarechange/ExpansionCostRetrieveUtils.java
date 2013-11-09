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

import com.lvl6.info.ExpansionCost;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ExpansionCostRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_EXPANSION_COST;
  
  private static Map<Integer, ExpansionCost> expansionNumToExpansionCost;
  
  public static Map<Integer, ExpansionCost> getAllExpansionNumsToCosts() {
  	if (null == expansionNumToExpansionCost) {
		  setStaticExpansionNumToCityExpansionCost();
	  }
	  
	  return expansionNumToExpansionCost;
  }

  public static ExpansionCost getCityExpansionCostById(int id) {
	  if (null == expansionNumToExpansionCost) {
		  setStaticExpansionNumToCityExpansionCost();
	  }
	  
	  return expansionNumToExpansionCost.get(id);
  	}
  

  
  public static void reload() {
	  setStaticExpansionNumToCityExpansionCost();
  }
  
  private static void setStaticExpansionNumToCityExpansionCost() {
	  log.debug("setting static map of expansion num to ExpansionCost");

	    Connection conn = DBConnection.get().getConnection();
	    ResultSet rs = null;
	    if (conn != null) {
	      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

	      if (rs != null) {
	        try {
	          rs.last();
	          rs.beforeFirst();
	          Map<Integer, ExpansionCost> expansionNumToCost = new HashMap<Integer, ExpansionCost>();
	          while(rs.next()) {  //should only be one
	            ExpansionCost cec = convertRSRowToCityExpansionCost(rs);
	            if (null != cec)
	            	expansionNumToCost.put(cec.getId(), cec);
	          }
	          expansionNumToExpansionCost = expansionNumToCost;
	        } catch (SQLException e) {
	          log.error("problem with database call.", e);
	          
	        }
	      }    
	    }
	    DBConnection.get().close(rs, null, conn);
  }
  
  private static ExpansionCost convertRSRowToCityExpansionCost(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int expansionCostCash = rs.getInt(i++);
    int numMinutesToExpand = rs.getInt(i++);
    
    return new ExpansionCost(id, expansionCostCash, numMinutesToExpand);
  }
}
