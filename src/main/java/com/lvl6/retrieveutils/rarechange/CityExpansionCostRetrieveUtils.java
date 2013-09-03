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

import com.lvl6.info.CityExpansionCost;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class CityExpansionCostRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_CITY_EXPANSION_COST;
  
  private static Map<Integer, CityExpansionCost> expansionNumToCityExpansionCost;

  public static CityExpansionCost getCityExpansionCostById(int id) {
	  if (null == expansionNumToCityExpansionCost) {
		  setStaticExpansionNumToCityExpansionCost();
	  }
	  
	  return expansionNumToCityExpansionCost.get(id);
  	}
  

  
  public static void reload() {
	  setStaticExpansionNumToCityExpansionCost();
  }
  
  private static void setStaticExpansionNumToCityExpansionCost() {
	  log.debug("setting static map of expansion num to CityExpansionCost");

	    Connection conn = DBConnection.get().getConnection();
	    ResultSet rs = null;
	    if (conn != null) {
	      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

	      if (rs != null) {
	        try {
	          rs.last();
	          rs.beforeFirst();
	          Map<Integer, CityExpansionCost> expansionNumToCost = new HashMap<Integer, CityExpansionCost>();
	          while(rs.next()) {  //should only be one
	            CityExpansionCost cec = convertRSRowToCityExpansionCost(rs);
	            if (null != cec)
	            	expansionNumToCost.put(cec.getId(), cec);
	          }
	          expansionNumToCityExpansionCost = expansionNumToCost;
	        } catch (SQLException e) {
	          log.error("problem with database call.", e);
	          
	        }
	      }    
	    }
	    DBConnection.get().close(rs, null, conn);
  }
  
  private static CityExpansionCost convertRSRowToCityExpansionCost(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int expansionCost = rs.getInt(i++);

    return new CityExpansionCost(id, expansionCost);
  }
}
