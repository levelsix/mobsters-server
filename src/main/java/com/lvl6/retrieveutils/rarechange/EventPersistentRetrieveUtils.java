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

import com.lvl6.info.EventPersistent;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class EventPersistentRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_EVENT_PERSISTENT;
  
  private static Map<Integer, EventPersistent> eventIdToEvent;
  
  public static Map<Integer, EventPersistent> getAllEventIdsToEvents() {
  	if (null == eventIdToEvent) {
		  setStaticEventIdsToEvents();
	  }
	  
	  return eventIdToEvent;
  }

  public static EventPersistent getEventById(int id) {
	  if (null == eventIdToEvent) {
		  setStaticEventIdsToEvents();
	  }
	  EventPersistent ep = eventIdToEvent.get(id); 
	  if (null == ep) {
	  	log.error("No EventPersistent for id=" + id);
	  }
	  return ep;
  	}
  

  
  public static void reload() {
	  setStaticEventIdsToEvents();
  }
  
  private static void setStaticEventIdsToEvents() {
	  log.debug("setting static map of id to EventPersistent");

	    Connection conn = DBConnection.get().getConnection();
	    ResultSet rs = null;
	    try {
	    	if (conn != null) {
	    		rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

	    		if (rs != null) {
	    			try {
	    				rs.last();
	    				rs.beforeFirst();
	    				Map<Integer, EventPersistent> idToEvent = new HashMap<Integer, EventPersistent>();
	    				while(rs.next()) {  //should only be one
	    					EventPersistent cec = convertRSRowToCityEventPersistent(rs);
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
  
  private static EventPersistent convertRSRowToCityEventPersistent(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String dayOfWeek = rs.getString(i++);
    int startHour = rs.getInt(i++);
    int eventDurationMinutes = rs.getInt(i++);
    int taskId = rs.getInt(i++);
    int cooldownMinutes = rs.getInt(i++);
    String eventType = rs.getString(i++);
    String monsterElemType = rs.getString(i++);
    
    EventPersistent ep = new EventPersistent(id, dayOfWeek, startHour,
    		eventDurationMinutes, taskId, cooldownMinutes, eventType, monsterElemType);
    
    if (null != dayOfWeek) {
    	String newDayOfWeek = dayOfWeek.trim();
    	newDayOfWeek = newDayOfWeek.toUpperCase();
    	if (!dayOfWeek.equals(newDayOfWeek)) {
    		log.error("string for day of week is incorrect. is: " + dayOfWeek +
    				"\t (if spelled correctly), expected: " + newDayOfWeek +
    				"\t eventPersistent obj=" + ep);
    		ep.setDayOfWeek(newDayOfWeek);
    	}
    }
    
    if (null != eventType) {
    	String newEventType = eventType.trim();
    	newEventType = newEventType.toUpperCase();
    	if (!eventType.equals(newEventType)) {
    		log.error("string for event type is incorrect. is: " + eventType +
    				"\t (if spelled correctly), expected: " + newEventType +
    				"\t eventPersistent obj=" + ep);
    		ep.setEventType(newEventType);
    	}
    }
    
    if (null != monsterElemType) {
    	String newMonsterElementType = monsterElemType.trim();
    	newMonsterElementType = newMonsterElementType.toUpperCase();
    	if (!monsterElemType.equals(newMonsterElementType)) {
    		log.error("string for monster element type incorrect. is: " + monsterElemType +
    				"\t (if spelled correctly), expected: " + newMonsterElementType +
    				"\t eventPersistent obj=" + ep);
    	}
    }
    
    return ep;
  }
}
