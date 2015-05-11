/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.records.PvpBattleForUserRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
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
public class PvpBattleForUserDao extends DAOImpl<PvpBattleForUserRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser, String> {

	/**
	 * Create a new PvpBattleForUserDao without any configuration
	 */
	public PvpBattleForUserDao() {
		super(PvpBattleForUser.PVP_BATTLE_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser.class);
	}

	/**
	 * Create a new PvpBattleForUserDao with an attached configuration
	 */
	public PvpBattleForUserDao(Configuration configuration) {
		super(PvpBattleForUser.PVP_BATTLE_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser object) {
		return object.getAttackerId();
	}

	/**
	 * Fetch records that have <code>attacker_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser> fetchByAttackerId(String... values) {
		return fetch(PvpBattleForUser.PVP_BATTLE_FOR_USER.ATTACKER_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>attacker_id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser fetchOneByAttackerId(String value) {
		return fetchOne(PvpBattleForUser.PVP_BATTLE_FOR_USER.ATTACKER_ID, value);
	}

	/**
	 * Fetch records that have <code>defender_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser> fetchByDefenderId(String... values) {
		return fetch(PvpBattleForUser.PVP_BATTLE_FOR_USER.DEFENDER_ID, values);
	}

	/**
	 * Fetch records that have <code>attacker_win_elo_change IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser> fetchByAttackerWinEloChange(Integer... values) {
		return fetch(PvpBattleForUser.PVP_BATTLE_FOR_USER.ATTACKER_WIN_ELO_CHANGE, values);
	}

	/**
	 * Fetch records that have <code>defender_lose_elo_change IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser> fetchByDefenderLoseEloChange(Integer... values) {
		return fetch(PvpBattleForUser.PVP_BATTLE_FOR_USER.DEFENDER_LOSE_ELO_CHANGE, values);
	}

	/**
	 * Fetch records that have <code>attacker_lose_elo_change IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser> fetchByAttackerLoseEloChange(Integer... values) {
		return fetch(PvpBattleForUser.PVP_BATTLE_FOR_USER.ATTACKER_LOSE_ELO_CHANGE, values);
	}

	/**
	 * Fetch records that have <code>defender_win_elo_change IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser> fetchByDefenderWinEloChange(Integer... values) {
		return fetch(PvpBattleForUser.PVP_BATTLE_FOR_USER.DEFENDER_WIN_ELO_CHANGE, values);
	}

	/**
	 * Fetch records that have <code>battle_start_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleForUser> fetchByBattleStartTime(Timestamp... values) {
		return fetch(PvpBattleForUser.PVP_BATTLE_FOR_USER.BATTLE_START_TIME, values);
	}
}