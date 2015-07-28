/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.TournamentEventForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.TournamentEventForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.TournamentEventForUserRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;
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
public class TournamentEventForUserDao extends DAOImpl<TournamentEventForUserRecord, TournamentEventForUserPojo, Record2<Integer, String>> {

	/**
	 * Create a new TournamentEventForUserDao without any configuration
	 */
	public TournamentEventForUserDao() {
		super(TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER, TournamentEventForUserPojo.class);
	}

	/**
	 * Create a new TournamentEventForUserDao with an attached configuration
	 */
	public TournamentEventForUserDao(Configuration configuration) {
		super(TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER, TournamentEventForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<Integer, String> getId(TournamentEventForUserPojo object) {
		return compositeKeyRecord(object.getTournamentEventId(), object.getUserId());
	}

	/**
	 * Fetch records that have <code>tournament_event_id IN (values)</code>
	 */
	public List<TournamentEventForUserPojo> fetchByTournamentEventId(Integer... values) {
		return fetch(TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER.TOURNAMENT_EVENT_ID, values);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<TournamentEventForUserPojo> fetchByUserId(String... values) {
		return fetch(TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>battles_won IN (values)</code>
	 */
	public List<TournamentEventForUserPojo> fetchByBattlesWon(Integer... values) {
		return fetch(TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER.BATTLES_WON, values);
	}

	/**
	 * Fetch records that have <code>battles_lost IN (values)</code>
	 */
	public List<TournamentEventForUserPojo> fetchByBattlesLost(Integer... values) {
		return fetch(TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER.BATTLES_LOST, values);
	}

	/**
	 * Fetch records that have <code>battles_fled IN (values)</code>
	 */
	public List<TournamentEventForUserPojo> fetchByBattlesFled(Integer... values) {
		return fetch(TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER.BATTLES_FLED, values);
	}
}