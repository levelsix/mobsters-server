/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MonsterForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterForUserRecord;

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
public class MonsterForUserDao extends DAOImpl<MonsterForUserRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser, String> {

	/**
	 * Create a new MonsterForUserDao without any configuration
	 */
	public MonsterForUserDao() {
		super(MonsterForUser.MONSTER_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser.class);
	}

	/**
	 * Create a new MonsterForUserDao with an attached configuration
	 */
	public MonsterForUserDao(Configuration configuration) {
		super(MonsterForUser.MONSTER_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchById(String... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser fetchOneById(String value) {
		return fetchOne(MonsterForUser.MONSTER_FOR_USER.ID, value);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByUserId(String... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>monster_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByMonsterId(Integer... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>current_experience IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByCurrentExperience(Integer... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.CURRENT_EXPERIENCE, values);
	}

	/**
	 * Fetch records that have <code>current_level IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByCurrentLevel(Byte... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.CURRENT_LEVEL, values);
	}

	/**
	 * Fetch records that have <code>current_health IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByCurrentHealth(Integer... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.CURRENT_HEALTH, values);
	}

	/**
	 * Fetch records that have <code>num_pieces IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByNumPieces(Byte... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.NUM_PIECES, values);
	}

	/**
	 * Fetch records that have <code>is_complete IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByIsComplete(Boolean... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.IS_COMPLETE, values);
	}

	/**
	 * Fetch records that have <code>combine_start_time IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByCombineStartTime(Timestamp... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.COMBINE_START_TIME, values);
	}

	/**
	 * Fetch records that have <code>team_slot_num IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByTeamSlotNum(Byte... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.TEAM_SLOT_NUM, values);
	}

	/**
	 * Fetch records that have <code>source_of_pieces IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchBySourceOfPieces(String... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.SOURCE_OF_PIECES, values);
	}

	/**
	 * Fetch records that have <code>has_all_pieces IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByHasAllPieces(Boolean... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.HAS_ALL_PIECES, values);
	}

	/**
	 * Fetch records that have <code>restricted IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByRestricted(Boolean... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.RESTRICTED, values);
	}

	/**
	 * Fetch records that have <code>offensive_skill_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByOffensiveSkillId(Integer... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.OFFENSIVE_SKILL_ID, values);
	}

	/**
	 * Fetch records that have <code>defensive_skill_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterForUser> fetchByDefensiveSkillId(Integer... values) {
		return fetch(MonsterForUser.MONSTER_FOR_USER.DEFENSIVE_SKILL_ID, values);
	}
}
