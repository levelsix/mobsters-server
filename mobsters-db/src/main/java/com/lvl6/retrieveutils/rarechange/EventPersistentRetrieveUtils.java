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

@Component
@DependsOn("gameServer")
public class EventPersistentRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_EVENT_PERSISTENT_CONFIG;

	private static Map<Integer, EventPersistent> eventIdToEvent;

	public Map<Integer, EventPersistent> getAllEventIdsToEvents() {
		if (null == eventIdToEvent) {
			setStaticEventIdsToEvents();
		}

		return eventIdToEvent;
	}

	public EventPersistent getEventById(int id) {
		if (null == eventIdToEvent) {
			setStaticEventIdsToEvents();
		}
		EventPersistent ep = eventIdToEvent.get(id);
		if (null == ep) {
			log.error("No EventPersistent for id=" + id);
		}
		return ep;
	}

	public void reload() {
		setStaticEventIdsToEvents();
	}

	private void setStaticEventIdsToEvents() {
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
						while (rs.next()) {  //should only be one
							EventPersistent cec = convertRSRowToEventPersistent(rs);
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

	private EventPersistent convertRSRowToEventPersistent(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.EVENT_PERSISTENT__ID);
		String dayOfWeek = rs
				.getString(DBConstants.EVENT_PERSISTENT__DAY_OF_WEEK);
		int startHour = rs.getInt(DBConstants.EVENT_PERSISTENT__START_HOUR);
		int eventDurationMinutes = rs
				.getInt(DBConstants.EVENT_PERSISTENT__EVENT_DURATION_MINUTES);
		int taskId = rs.getInt(DBConstants.EVENT_PERSISTENT__TASK_ID);
		int cooldownMinutes = rs
				.getInt(DBConstants.EVENT_PERSISTENT__COOLDOWN_MINUTES);
		String eventType = rs
				.getString(DBConstants.EVENT_PERSISTENT__EVENT_TYPE);
		String monsterElemType = rs
				.getString(DBConstants.EVENT_PERSISTENT__MONSTER_ELEMENT);

		if (null != dayOfWeek) {
			String newDayOfWeek = dayOfWeek.trim().toUpperCase();
			if (!dayOfWeek.equals(newDayOfWeek)) {
				log.error(String.format("DayOfWeek is incorrect: %s, id=%s",
						dayOfWeek, id));
				dayOfWeek = newDayOfWeek;
			}
		}

		if (null != eventType) {
			String newEventType = eventType.trim();
			newEventType = newEventType.toUpperCase();
			if (!eventType.equals(newEventType)) {
				log.error(String.format("EventType incorrect: %s, id=%s",
						eventType, id));
				eventType = newEventType;
			}
		}

		if (null != monsterElemType) {
			String newMonsterElementType = monsterElemType.trim().toUpperCase();
			if (!monsterElemType.equals(newMonsterElementType)) {
				log.error(String.format("monsterElement incorrect: %s, id=%s",
						monsterElemType, id));
				monsterElemType = newMonsterElementType;
			}
		}

		EventPersistent ep = new EventPersistent(id, dayOfWeek, startHour,
				eventDurationMinutes, taskId, cooldownMinutes, eventType,
				monsterElemType);

		return ep;
	}
}
