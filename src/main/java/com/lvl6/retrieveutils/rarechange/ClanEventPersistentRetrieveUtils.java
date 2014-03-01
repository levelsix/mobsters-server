package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanEventPersistent;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanEventPersistentRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT;
  private static String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY",
		"SATURDAY", "SUNDAY"};
  private static Set<String> daysSet;
  
  private static Map<Integer, ClanEventPersistent> eventIdToEvent;
  

	//clan raids overlap so more than one can be active at the moment
	public static Map<Integer, ClanEventPersistent> getActiveClanEventIdsToEvents(
			Date curDate, TimeUtils timeUtils) {
		log.debug("retrieving data for current persistent clan event");
		if (null == eventIdToEvent) {
			setStaticEventIdsToEvents();
		}
		Map<Integer, ClanEventPersistent> clanEventIdToEvent =
				new HashMap<Integer, ClanEventPersistent>();
		
		DateTime dt = new DateTime(curDate);
		log.info("dtNow=" + dt);
		DateTime pstDt = new DateTime(curDate, DateTimeZone
  		.forTimeZone(TimeZone.getTimeZone("America/Los_Angeles")));
		log.info("pstDtNow=" + pstDt);
		
		curDate = timeUtils.createPstDateAddMinutes(curDate, 0);
		
		//go through each event and see which ones are active
		//Event is active if today is between the event's start time and end time
		for (ClanEventPersistent cep : eventIdToEvent.values()) {
//			if (!dow.equalsIgnoreCase(cep.getDayOfWeek())) {
//				continue;
//			}
			log.info("cep=" + cep);
			//check if correct time
			int eventDayOfWeek = timeUtils.getDayOfWeek(cep.getDayOfWeek());
			if (eventDayOfWeek <= 0 || eventDayOfWeek >= 8) {
				log.error("ClanEventPersistent has invalid DayOfWeek. event=" + cep);
				//days of week go from 1 to 7, with 1 being Monday
				continue;
			}
			int curDayOfWeekPst = timeUtils.getDayOfWeekPst(curDate);
			
			//either 0 or negative number
			int dayOffset = calculateEventStartDayOffset(curDayOfWeekPst, eventDayOfWeek);
			int hour = cep.getStartHour();
			int minutesAddend = 0;
			
			Date eventStartTime = timeUtils.createPstDate(curDate, dayOffset, hour, minutesAddend);
			
			minutesAddend = cep.getEventDurationMinutes();
			Date eventEndTime = timeUtils.createPstDateAddMinutes(eventStartTime, minutesAddend);
			
			log.info("eventStartTime=" + eventStartTime);
			log.info("eventEndTime=" + eventEndTime);
			
			//eventStartTime is always earlier than curDate, given the way it's calculated
			if (!timeUtils.isFirstEarlierThanSecond(curDate, eventEndTime)) {
				//event has ended already.
				continue;
			}
			
			//current date is in between start and end time for event.
//			int clanRaidId = cep.getClanRaidId();
//			raidIdToEvent.put(clanRaidId, cep);
			int clanEventId = cep.getId();
			clanEventIdToEvent.put(clanEventId, cep);
		}
		return clanEventIdToEvent;
	}

	//ex.
	// days of the week in numbers are: 1 is Monday and 7 is Sunday.
	//today is Sunday i.e. curDayOfWeekPst = 7
	//let's say the event starts Mondays, i.e. 1 (So the event started 6 days ago)
	//so the day offset should be 6, so the return value should be -6
	//formula:
	// {[(curDayOfWeekPst - eventDayOfWeek) + 7] % 7} * -1
	private static int calculateEventStartDayOffset(int curDayOfWeekPst, int eventDayOfWeek) {
		int dayDiff = curDayOfWeekPst - eventDayOfWeek;
		dayDiff = dayDiff + 7;
		dayDiff = dayDiff % 7;
		dayDiff = dayDiff * -1;
		
		return dayDiff;
	}
  
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
  	daysSet = new HashSet<String>(Arrays.asList(days));
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
    	if (!dayOfWeek.equals(newDayOfWeek) || !daysSet.contains(newDayOfWeek)) {
    		log.error("string for day of week is incorrect. is: " + dayOfWeek +
    				"\t (if spelled correctly), expected: " + newDayOfWeek +
    				"\t clanEventPersistent obj=" + ep);
    		ep.setDayOfWeek(newDayOfWeek);
    	}
    }
    
    return ep;
  }
}
