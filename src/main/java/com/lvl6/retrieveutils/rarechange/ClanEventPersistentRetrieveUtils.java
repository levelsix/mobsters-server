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

import com.lvl6.info.ClanEventPersistent;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanEventPersistentRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT;
  
  private static Map<Integer, ClanEventPersistent> eventIdToEvent;
  
  public static Map<Integer, ClanEventPersistent> getAllEventIdsToEvents() {
  	if (null == eventIdToEvent) {
		  setStaticEventIdsToEvents();
	  }
	  
	  return eventIdToEvent;
  }

  public static ClanEventPersistent getEventById(int id) {
	  if (null == eventIdToEvent) {
		  setStaticEventIdsToEvents();
	  }
	  ClanEventPersistent ep = eventIdToEvent.get(id); 
	  if (null == ep) {
	  	log.error("No ClanEventPersistent for id=" + id);
	  }
	  return ep;
  	}
  

  
  public static void reload() {
	  setStaticEventIdsToEvents();
  }
  
  private static void setStaticEventIdsToEvents() {
	  log.debug("setting static map of id to ClanEventPersistent");

	    Connection conn = DBConnection.get().getConnection();
	    ResultSet rs = null;
	    try {
	    	if (conn != null) {
	    		rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

	    		if (rs != null) {
	    			try {
	    				rs.last();
	    				rs.beforeFirst();
	    				Map<Integer, ClanEventPersistent> idToEvent = new HashMap<Integer, ClanEventPersistent>();
	    				while(rs.next()) { 
	    					ClanEventPersistent cec = convertRSRowToClanEventPersistent(rs);
	    					if (null != cec)
	    						idToEvent.put(cec.getId(), cec);
	    				}
	    				eventIdToEvent = idToEvent;
	    			} catch (SQLException e) {
	    				log.error("problem with database call.", e);

	    			}
	    		}    
	    	}
	    } catch (Exception e) {
	    	log.error("event persistent retrieve db error.", e);
	    } finally {
	    	DBConnection.get().close(rs, null, conn);
	    }
  }
  
  private static ClanEventPersistent convertRSRowToClanEventPersistent(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String dayOfWeek = rs.getString(i++);
    int startHour = rs.getInt(i++);
    int eventDurationMinutes = rs.getInt(i++);
    int clanRaidId = rs.getInt(i++);
    
    ClanEventPersistent ep = new ClanEventPersistent(id, dayOfWeek, startHour,
    		eventDurationMinutes, clanRaidId);
    
    if (null != dayOfWeek) {
    	String newDayOfWeek = dayOfWeek.trim();
    	newDayOfWeek = newDayOfWeek.toUpperCase();
    	if (!dayOfWeek.equals(newDayOfWeek)) {
    		log.error("string for day of week is incorrect. is: " + dayOfWeek +
    				"\t (if spelled correctly), expected: " + newDayOfWeek +
    				"\t clanEventPersistent obj=" + ep);
    		ep.setDayOfWeek(newDayOfWeek);
    	}
    }
    
    return ep;
  }
}
