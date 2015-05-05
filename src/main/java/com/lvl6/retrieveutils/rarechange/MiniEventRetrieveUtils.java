package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
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

	private Map<Integer, MiniEvent> idToMiniEvent;
	private TreeSet<MiniEvent> meStartTimeTree;
	private TreeSet<MiniEvent> meEndTimeTree;
	private static final MeStartTimeComparator stComparator = new MeStartTimeComparator();
	private static final MeEndTimeComparator etComparator = new MeEndTimeComparator();

	@Autowired
	protected TimeUtils timeUtil;

	private static final class MeStartTimeComparator implements Comparator<MiniEvent>
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

	private static final class MeEndTimeComparator implements Comparator<MiniEvent>
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

	public MiniEvent getCurrentlyActiveMiniEvent( Date now ) {
		if ( null == meStartTimeTree || null == meEndTimeTree ) {
			log.warn("no MiniEvent trees created");
			reload();
		}

		if (null == meStartTimeTree || null == meEndTimeTree) {
			log.warn("no miniEvents are currently active");
			return null;
		}

		MiniEvent me = new MiniEvent();
		me.setId(0);
		me.setEndTime(now);
		me.setStartTime(now);
		//contains the MiniEvents that have StartTime before $now
		SortedSet<MiniEvent> started = meStartTimeTree.headSet(me, true);
		//contains the MiniEvents that have EndTime after $now
		SortedSet<MiniEvent> notYetEnded = meEndTimeTree.tailSet(me, true);

		Sets.SetView<MiniEvent> ongoing = Sets.intersection(started, notYetEnded);
		if (ongoing.isEmpty()) {
			log.info("no active MiniEvents. started:{}, notYetEnded:{}",
					started, notYetEnded);
			return null;
		} else {
			MiniEvent active = ongoing.iterator().next();
			log.info("selected MiniEvent {}, allOngoing:{}",
					active, ongoing);
			return active;
		}
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

		TreeSet<MiniEvent> meStartTimeTreeTemp = new TreeSet<MiniEvent>(stComparator);
		TreeSet<MiniEvent> meEndTimeTreeTemp = new TreeSet<MiniEvent>(etComparator);
		for (MiniEvent me : idToMiniEvent.values()) {
			boolean added = meStartTimeTreeTemp.add(me);
			if (!added) {
				log.error("(shouldn't happen...) can't add MiniEvent={} to treeSet={}",
						me, meStartTimeTreeTemp);
			}

			added = meEndTimeTreeTemp.add(me);
			if (!added) {
				log.error("(shouldn't happen...) didn't add MiniEvent={} to treeSet={}",
						me, meEndTimeTreeTemp);
			}
		}

		meStartTimeTree = meStartTimeTreeTemp;
		meEndTimeTree = meEndTimeTreeTemp;
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

	public TreeSet<MiniEvent> getMeStartTimeTree() {
		return meStartTimeTree;
	}

	public void setMeStartTimeTree(TreeSet<MiniEvent> meStartTimeTree) {
		this.meStartTimeTree = meStartTimeTree;
	}

	public TreeSet<MiniEvent> getMeEndTimeTree() {
		return meEndTimeTree;
	}

	public void setMeEndTimeTree(TreeSet<MiniEvent> meEndTimeTree) {
		this.meEndTimeTree = meEndTimeTree;
	}

}
