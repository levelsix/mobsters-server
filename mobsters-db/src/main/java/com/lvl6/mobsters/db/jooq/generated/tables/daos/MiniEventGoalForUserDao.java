/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventGoalForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventGoalForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniEventGoalForUserRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record3;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MiniEventGoalForUserDao extends DAOImpl<MiniEventGoalForUserRecord, MiniEventGoalForUserPojo, Record3<String, Integer, Integer>> {

	/**
	 * Create a new MiniEventGoalForUserDao without any configuration
	 */
	public MiniEventGoalForUserDao() {
		super(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER, MiniEventGoalForUserPojo.class);
	}

	/**
	 * Create a new MiniEventGoalForUserDao with an attached configuration
	 */
	public MiniEventGoalForUserDao(Configuration configuration) {
		super(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER, MiniEventGoalForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record3<String, Integer, Integer> getId(MiniEventGoalForUserPojo object) {
		return compositeKeyRecord(object.getUserId(), object.getMiniEventTimetableId(), object.getMiniEventGoalId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<MiniEventGoalForUserPojo> fetchByUserId(String... values) {
		return fetch(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>mini_event_timetable_id IN (values)</code>
	 */
	public List<MiniEventGoalForUserPojo> fetchByMiniEventTimetableId(Integer... values) {
		return fetch(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER.MINI_EVENT_TIMETABLE_ID, values);
	}

	/**
	 * Fetch records that have <code>mini_event_goal_id IN (values)</code>
	 */
	public List<MiniEventGoalForUserPojo> fetchByMiniEventGoalId(Integer... values) {
		return fetch(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER.MINI_EVENT_GOAL_ID, values);
	}

	/**
	 * Fetch records that have <code>progress IN (values)</code>
	 */
	public List<MiniEventGoalForUserPojo> fetchByProgress(Integer... values) {
		return fetch(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER.PROGRESS, values);
	}
}
