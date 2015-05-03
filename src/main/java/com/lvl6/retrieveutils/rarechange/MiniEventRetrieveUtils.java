package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MiniEvent;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniEventRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_CONFIG;

	private static Map<Integer, MiniEvent> idToMiniEvent;
	private static TreeSet<MiniEvent> miniEventTree;
	private static final MiniEventComparator comparator = new MiniEventComparator();

	@Autowired
	protected TimeUtils timeUtil;
	
	private static final class MiniEventComparator implements Comparator<MiniEvent>
	{
		@Override
		public int compare(MiniEvent o1, MiniEvent o2) {
			long o1Time = o1.getStartTime().getTime();
			long o2Time = o2.getStartTime().getTime();
			if ( o1Time < o2Time ) {
				return -1;
			} else if ( o1Time > o2Time ) {
				return 1;
			} else if (o1.getId() < o2.getId()) {
				return -1;
			} else if (o1.getId() > o2.getId()) {
				return 1;
			} else {
				return 0; 
			}

		}
	}

	public MiniEvent getCurrentlyActiveMiniEvent( Date now )
	{
		if (null == miniEventTree) {
			log.warn("no MiniEvent tree created");
			reload();
		}

		if (null == miniEventTree) {
			log.warn("no miniEvents are currently active");
			return null;
		}

		MiniEvent me = new MiniEvent();
		me.setId(0);
//		me.setEndTime(now);
		me.setStartTime(now);
//		MiniEvent active = miniEventTree.ceiling(me);
		MiniEvent active = miniEventTree.floor(me);
		
		log.info("found active={}", active);

		//found the MiniEvent with a startTime before $now
		//need to make sure MiniEvent endTime is after $now
		if (null != me) {
			Date activeEndTime = active.getEndTime();
			if (timeUtil.isFirstEarlierThanSecond(activeEndTime, now))
			{
				log.info ("end time invalid. invalidating active.");
				active = null;
			}
			
		}
		log.info("for given time={}, selected {}",
				now, active);
		
		return active;
	}

	public Map<Integer, MiniEvent> getAllIdsToMiniEvents() {
		if (null == idToMiniEvent) {
			reload();
		}

		return idToMiniEvent;
	}

	public MiniEvent getMiniEventById(int id) {
		if (null == idToMiniEvent) {
			reload();
		}
		MiniEvent ep = idToMiniEvent.get(id);
		if (null == ep) {
			log.error("No MiniEvent for id={}", id);
		}
		return ep;
	}

	public void reload() {
		setStaticIdsToMiniEvents();
		setOrderedMiniEvents();
	}

	private void setStaticIdsToMiniEvents() {
		log.debug("setting static map of id to MiniEvent");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, MiniEvent> idToMiniEventTemp = new HashMap<Integer, MiniEvent>();
						while (rs.next()) {  //should only be one
							MiniEvent cec = convertRSRowToMiniEvent(rs);
							if (null != cec)
								idToMiniEventTemp.put(cec.getId(), cec);
						}
						idToMiniEvent = idToMiniEventTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("MiniEvent retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private void setOrderedMiniEvents() {
		if ( null == idToMiniEvent) {
			log.warn("NO MINIEVENTS!");
			return;
		}

		TreeSet<MiniEvent> miniEventTreeTemp = new TreeSet<MiniEvent>(comparator);

		for (MiniEvent me : idToMiniEvent.values()) {
			boolean added = miniEventTreeTemp.add(me);
			if (!added) {
				log.error("(shouldn't happen...) can't add MiniEvent={} to treeSet={}",
						me, miniEventTreeTemp);
			}
		}

		miniEventTree = miniEventTreeTemp;

	}

	private MiniEvent convertRSRowToMiniEvent(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.MINI_EVENT__ID);
		Timestamp ts = rs.getTimestamp(DBConstants.MINI_EVENT__START_TIME);
		Date startDate = null;
		if (null != ts && !rs.wasNull()) {
			startDate = new Date(ts.getTime());
		}

		ts = rs.getTimestamp(DBConstants.MINI_EVENT__END_TIME);
		Date endDate = null;
		if (null != ts && !rs.wasNull()) {
			endDate = new Date(ts.getTime());
		}

		String name = rs.getString(DBConstants.MINI_EVENT__NAME);
		String desc = rs.getString(DBConstants.MINI_EVENT__DESCRIPTION);
		String img = rs.getString(DBConstants.MINI_EVENT__IMG);
		String icon = rs.getString(DBConstants.MINI_EVENT__ICON);

		MiniEvent me = new MiniEvent(id, startDate, endDate,
				name, desc, img, icon);
		return me;
	}
}
