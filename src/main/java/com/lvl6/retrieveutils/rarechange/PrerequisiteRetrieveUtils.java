package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Prerequisite;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class PrerequisiteRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Prerequisite> prerequisiteIdsToPrerequisites;

  private static final String TABLE_NAME = DBConstants.TABLE_PREREQUISITE;
  
  public static Map<Integer, Prerequisite> getPrerequisiteIdsToPrerequisites() {
  	if (null == prerequisiteIdsToPrerequisites) {
  		setStaticPrerequisiteIdsToPrerequisites();
  	}
  	return prerequisiteIdsToPrerequisites;
  }
  /*
  public static Prerequisite getPrerequisiteForId(int prerequisiteId) {
	  if (null == prerequisiteIdsToPrerequisites) {
		  setStaticPrerequisiteIdsToPrerequisites();
	  }
	  
	  if (!prerequisiteIdsToPrerequisites.containsKey(prerequisiteId)) {
		  log.error("no prerequisite for id=" + prerequisiteId);
		  return null;
	  }
	  return prerequisiteIdsToPrerequisites.get(prerequisiteId);
  }

  public static Map<Integer, Prerequisite> getPrerequisitesForIds(Collection<Integer> ids) {
  	if (null == prerequisiteIdsToPrerequisites) {
  		setStaticPrerequisiteIdsToPrerequisites();
  	}
  	Map<Integer, Prerequisite> returnMap = new HashMap<Integer, Prerequisite>();
  	
  	for (int id : ids) {
  		Prerequisite tsm = getPrerequisiteForId(id);
  		returnMap.put(id, tsm);
  	}
  	return returnMap;
  }
	*/
  
  private static void setStaticPrerequisiteIdsToPrerequisites() {
    log.debug("setting static map of prerequisite ids to prerequisites");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Prerequisite> prerequisiteIdsToPrerequisitesTemp =
			      		new HashMap<Integer, Prerequisite>();
			      
			      //loop through each row and convert it into a java object
			      while(rs.next()) {  
			        Prerequisite prerequisite = convertRSRowToPrerequisite(rs);
			        
			        int prerequisiteId = prerequisite.getId();
			        prerequisiteIdsToPrerequisitesTemp.put(prerequisiteId, prerequisite);
			      }
			      
			      prerequisiteIdsToPrerequisites = prerequisiteIdsToPrerequisitesTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("prerequisite retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticPrerequisiteIdsToPrerequisites();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Prerequisite convertRSRowToPrerequisite(ResultSet rs) throws SQLException {
    int id = rs.getInt(DBConstants.PREREQUISITE__ID);
    String gameType = rs.getString(DBConstants.PREREQUISITE__GAME_TYPE);
    int gameEntityId = rs.getInt(DBConstants.PREREQUISITE__GAME_ENTITY_ID);
    String prereqGameType = rs.getString(DBConstants.PREREQUISITE__PREREQ_GAME_TYPE);
    int prereqGameEntityId = rs.getInt(DBConstants.PREREQUISITE__PREREQ_GAME_ENTITY_ID);
    int quantity = rs.getInt(DBConstants.PREREQUISITE__QUANTITY);
    
    Prerequisite prerequisite = new Prerequisite(id, gameType, gameEntityId,
    	prereqGameType, prereqGameEntityId, quantity);
    return prerequisite;
  }
  
}
