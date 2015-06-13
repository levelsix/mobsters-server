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
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig;
import com.lvl6.utils.TimeUtils;

@Component
public class MiniEventTimetableRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(MiniEventTimetableRetrieveUtils.class);

//	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_TIMETABLE_CONFIG;

	private Map<Integer, MiniEventTimetableConfig> idToMiniEventTimetableConfig;
	private TreeSet<MiniEventTimetableConfig> meStartTimeTree;
	private TreeSet<MiniEventTimetableConfig> meEndTimeTree;
	private static final MeStartTimeComparator stComparator = new MeStartTimeComparator();
	private static final MeEndTimeComparator etComparator = new MeEndTimeComparator();

	@Autowired
	protected MiniEventTimetableConfigDao miniEventTimetableConfigDao;

	@Autowired
	protected TimeUtils timeUtil;

	private static final class MeStartTimeComparator implements Comparator<MiniEventTimetableConfig>
	{
		@Override
		public int compare(MiniEventTimetableConfig o1, MiniEventTimetableConfig o2) {
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

	private static final class MeEndTimeComparator implements Comparator<MiniEventTimetableConfig>
	{
		@Override
		public int compare(MiniEventTimetableConfig o1, MiniEventTimetableConfig o2) {
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

	public MiniEventTimetableConfig getCurrentlyActiveMiniEvent( Date now ) {
		if ( null == meStartTimeTree || null == meEndTimeTree ) {
			log.warn("no MiniEventTimetableConfig trees created");
			reload();
		}

		if (null == meStartTimeTree || null == meEndTimeTree) {
			log.warn("no miniEvents are currently active");
			return null;
		}

		Timestamp nowTimestamp = new Timestamp(now.getTime());
		MiniEventTimetableConfig me = new MiniEventTimetableConfig();
		me.setId(0);
		me.setEndTime(nowTimestamp);
		me.setStartTime(nowTimestamp);
		//contains the MiniEventTimetableConfigs that have StartTime before $now
		SortedSet<MiniEventTimetableConfig> started = meStartTimeTree.headSet(me, true);
		//contains the MiniEventTimetableConfigs that have EndTime after $now
		SortedSet<MiniEventTimetableConfig> notYetEnded = meEndTimeTree.tailSet(me, true);

		Sets.SetView<MiniEventTimetableConfig> ongoing = Sets.intersection(started, notYetEnded);
		if (ongoing.isEmpty()) {
			log.info("no active MiniEventTimetableConfigs. started:{}, notYetEnded:{}",
					started, notYetEnded);
			return null;
		} else {
			MiniEventTimetableConfig active = ongoing.iterator().next();
			log.info("now={}, selected MiniEventTimetableConfig:{}, allOngoing:{}",
					new Object[] { now, active, ongoing });
			return active;
		}
	}

	public MiniEventTimetableConfig getTimetableForMiniEventId(int miniEventId) {
		log.debug("retrieving Timetable for miniEventId={}", miniEventId);

		if (null == idToMiniEventTimetableConfig || idToMiniEventTimetableConfig.isEmpty()) {
			reload();
		}

		if (!idToMiniEventTimetableConfig.containsKey(miniEventId)) {
			log.error("no MiniEventTimetable for miniEventId={}", miniEventId);
			return null;
		}
		return idToMiniEventTimetableConfig.get(miniEventId);
	}

	public Map<Integer, MiniEventTimetableConfig> getAllIdsToMiniEventTimetableConfigs() {
		if (null == idToMiniEventTimetableConfig) {
			reload();
		}

		return idToMiniEventTimetableConfig;
	}

	public void reload() {
		setStaticIdsToMiniEventTimetableConfigs();
		setOrderedMiniEventTimetableConfigs();
	}

	private void setStaticIdsToMiniEventTimetableConfigs() {
		log.debug("setting static map of miniEventId to MiniEventTimetableConfig");

		Map<Integer, MiniEventTimetableConfig> miniEventIdToMiniEventTimetableConfigTemp = new HashMap<Integer, MiniEventTimetableConfig>();
		for (MiniEventTimetableConfig metc : miniEventTimetableConfigDao.findAll())
		{
			int miniEventId = metc.getMiniEventId();
			miniEventIdToMiniEventTimetableConfigTemp.put(miniEventId, metc);
		}

		idToMiniEventTimetableConfig = miniEventIdToMiniEventTimetableConfigTemp;
	}

	private void setOrderedMiniEventTimetableConfigs() {
		if ( null == idToMiniEventTimetableConfig) {
			log.warn("NO MINIEVENTS!");
			return;
		}

		TreeSet<MiniEventTimetableConfig> meStartTimeTreeTemp = new TreeSet<MiniEventTimetableConfig>(stComparator);
		TreeSet<MiniEventTimetableConfig> meEndTimeTreeTemp = new TreeSet<MiniEventTimetableConfig>(etComparator);
		for (MiniEventTimetableConfig me : idToMiniEventTimetableConfig.values()) {
			boolean added = meStartTimeTreeTemp.add(me);
			if (!added) {
				log.error("(shouldn't happen...) can't add MiniEventTimetableConfig={} to treeSet={}",
						me, meStartTimeTreeTemp);
			}

			added = meEndTimeTreeTemp.add(me);
			if (!added) {
				log.error("(shouldn't happen...) didn't add MiniEventTimetableConfig={} to treeSet={}",
						me, meEndTimeTreeTemp);
			}
		}

		meStartTimeTree = meStartTimeTreeTemp;
		meEndTimeTree = meEndTimeTreeTemp;

	}

	public TreeSet<MiniEventTimetableConfig> getMeStartTimeTree() {
		return meStartTimeTree;
	}

	public void setMeStartTimeTree(TreeSet<MiniEventTimetableConfig> meStartTimeTree) {
		this.meStartTimeTree = meStartTimeTree;
	}

	public TreeSet<MiniEventTimetableConfig> getMeEndTimeTree() {
		return meEndTimeTree;
	}

	public void setMeEndTimeTree(TreeSet<MiniEventTimetableConfig> meEndTimeTree) {
		this.meEndTimeTree = meEndTimeTree;
	}

}
