package com.lvl6.retrieveutils.rarechange;

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
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.MiniEventTimetableConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfigPojo;
import com.lvl6.utils.TimeUtils;

@Component
public class MiniEventTimetableRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(MiniEventTimetableRetrieveUtils.class);

//	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_TIMETABLE_CONFIG;

	private Map<Integer, MiniEventTimetableConfigPojo> miniEventIdToMiniEventTimetable;
	private TreeSet<MiniEventTimetableConfigPojo> meStartTimeTree;
	private TreeSet<MiniEventTimetableConfigPojo> meEndTimeTree;
	private static final MeStartTimeComparator stComparator = new MeStartTimeComparator();
	private static final MeEndTimeComparator etComparator = new MeEndTimeComparator();

	@Autowired
	protected MiniEventTimetableConfigDao miniEventTimetableConfigDao;

	@Autowired
	protected TimeUtils timeUtil;

	private static final class MeStartTimeComparator implements Comparator<MiniEventTimetableConfigPojo>
	{
		@Override
		public int compare(MiniEventTimetableConfigPojo o1, MiniEventTimetableConfigPojo o2) {
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

	private static final class MeEndTimeComparator implements Comparator<MiniEventTimetableConfigPojo>
	{
		@Override
		public int compare(MiniEventTimetableConfigPojo o1, MiniEventTimetableConfigPojo o2) {
			long o1Time = o1.getEndTime().getTime();
			long o2Time = o2.getEndTime().getTime();
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

	public MiniEventTimetableConfigPojo getCurrentlyActiveMiniEvent( Date now ) {
		if ( null == meStartTimeTree || null == meEndTimeTree ) {
			log.warn("no MiniEventTimetableConfigPojo trees created");
			reload();
		}

		if (null == meStartTimeTree || null == meEndTimeTree) {
			log.warn("no miniEvents are currently active");
			return null;
		}

		Timestamp nowTimestamp = new Timestamp(now.getTime());
		MiniEventTimetableConfigPojo me = new MiniEventTimetableConfigPojo();
		me.setId(0);
		me.setEndTime(nowTimestamp);
		me.setStartTime(nowTimestamp);
		//contains the MiniEventTimetableConfigPojos that have StartTime before $now
		SortedSet<MiniEventTimetableConfigPojo> started = meStartTimeTree.headSet(me, true);
		//contains the MiniEventTimetableConfigPojos that have EndTime after $now
		SortedSet<MiniEventTimetableConfigPojo> notYetEnded = meEndTimeTree.tailSet(me, true);

		Sets.SetView<MiniEventTimetableConfigPojo> ongoing = Sets.intersection(started, notYetEnded);
		if (ongoing.isEmpty()) {
			log.info("no active MiniEventTimetableConfigPojos. started:{}, notYetEnded:{}",
					started, notYetEnded);
			return null;
		} else {
			MiniEventTimetableConfigPojo active = ongoing.iterator().next();
			log.info("now={}, selected MiniEventTimetableConfigPojo:{}, allOngoing:{}",
					new Object[] { now, active, ongoing });
			return active;
		}
	}

	public MiniEventTimetableConfigPojo getTimetableForMiniEventId(int miniEventId) {
		log.debug("retrieving Timetable for miniEventId={}", miniEventId);

		if (null == miniEventIdToMiniEventTimetable || miniEventIdToMiniEventTimetable.isEmpty()) {
			reload();
		}

		if (!miniEventIdToMiniEventTimetable.containsKey(miniEventId)) {
			log.error("no MiniEventTimetable for miniEventId={}", miniEventId);
			return null;
		}
		return miniEventIdToMiniEventTimetable.get(miniEventId);
	}

	public Map<Integer, MiniEventTimetableConfigPojo> getAllIdsToMiniEventTimetableConfigs() {
		if (null == miniEventIdToMiniEventTimetable) {
			reload();
		}

		return miniEventIdToMiniEventTimetable;
	}

	public void reload() {
		setStaticIdsToMiniEventTimetableConfigPojos();
		setOrderedMiniEventTimetableConfigs();
	}

	private void setStaticIdsToMiniEventTimetableConfigPojos() {
		log.debug("setting static map of miniEventId to MiniEventTimetableConfigPojo");

		Map<Integer, MiniEventTimetableConfigPojo> miniEventIdToMiniEventTimetableConfigPojoTemp = new HashMap<Integer, MiniEventTimetableConfigPojo>();
		for (MiniEventTimetableConfigPojo metc : miniEventTimetableConfigDao.findAll())
		{
			int miniEventId = metc.getMiniEventId();
			miniEventIdToMiniEventTimetableConfigPojoTemp.put(miniEventId, metc);
		}

		miniEventIdToMiniEventTimetable = miniEventIdToMiniEventTimetableConfigPojoTemp;
	}

	private void setOrderedMiniEventTimetableConfigs() {
		if ( null == miniEventIdToMiniEventTimetable) {
			log.warn("NO MINIEVENTS!");
			return;
		}

		TreeSet<MiniEventTimetableConfigPojo> meStartTimeTreeTemp = new TreeSet<MiniEventTimetableConfigPojo>(stComparator);
		TreeSet<MiniEventTimetableConfigPojo> meEndTimeTreeTemp = new TreeSet<MiniEventTimetableConfigPojo>(etComparator);
		for (MiniEventTimetableConfigPojo me : miniEventIdToMiniEventTimetable.values()) {
			boolean added = meStartTimeTreeTemp.add(me);
			if (!added) {
				log.error("(shouldn't happen...) can't add MiniEventTimetableConfigPojo={} to treeSet={}",
						me, meStartTimeTreeTemp);
			}

			added = meEndTimeTreeTemp.add(me);
			if (!added) {
				log.error("(shouldn't happen...) didn't add MiniEventTimetableConfigPojo={} to treeSet={}",
						me, meEndTimeTreeTemp);
			}
		}

		meStartTimeTree = meStartTimeTreeTemp;
		meEndTimeTree = meEndTimeTreeTemp;

	}

	public TreeSet<MiniEventTimetableConfigPojo> getMeStartTimeTree() {
		return meStartTimeTree;
	}

	public void setMeStartTimeTree(TreeSet<MiniEventTimetableConfigPojo> meStartTimeTree) {
		this.meStartTimeTree = meStartTimeTree;
	}

	public TreeSet<MiniEventTimetableConfigPojo> getMeEndTimeTree() {
		return meEndTimeTree;
	}

	public void setMeEndTimeTree(TreeSet<MiniEventTimetableConfigPojo> meEndTimeTree) {
		this.meEndTimeTree = meEndTimeTree;
	}

}
