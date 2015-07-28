/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MonsterEvolvingForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterEvolvingForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterEvolvingForUserRecord;

import java.sql.Timestamp;
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
public class MonsterEvolvingForUserDao extends DAOImpl<MonsterEvolvingForUserRecord, MonsterEvolvingForUserPojo, Record3<String, String, String>> {

	/**
	 * Create a new MonsterEvolvingForUserDao without any configuration
	 */
	public MonsterEvolvingForUserDao() {
		super(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER, MonsterEvolvingForUserPojo.class);
	}

	/**
	 * Create a new MonsterEvolvingForUserDao with an attached configuration
	 */
	public MonsterEvolvingForUserDao(Configuration configuration) {
		super(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER, MonsterEvolvingForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record3<String, String, String> getId(MonsterEvolvingForUserPojo object) {
		return compositeKeyRecord(object.getCatalystUserMonsterId(), object.getUserMonsterIdOne(), object.getUserMonsterIdTwo());
	}

	/**
	 * Fetch records that have <code>catalyst_user_monster_id IN (values)</code>
	 */
	public List<MonsterEvolvingForUserPojo> fetchByCatalystUserMonsterId(String... values) {
		return fetch(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.CATALYST_USER_MONSTER_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>catalyst_user_monster_id = value</code>
	 */
	public MonsterEvolvingForUserPojo fetchOneByCatalystUserMonsterId(String value) {
		return fetchOne(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.CATALYST_USER_MONSTER_ID, value);
	}

	/**
	 * Fetch records that have <code>user_monster_id_one IN (values)</code>
	 */
	public List<MonsterEvolvingForUserPojo> fetchByUserMonsterIdOne(String... values) {
		return fetch(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.USER_MONSTER_ID_ONE, values);
	}

	/**
	 * Fetch records that have <code>user_monster_id_two IN (values)</code>
	 */
	public List<MonsterEvolvingForUserPojo> fetchByUserMonsterIdTwo(String... values) {
		return fetch(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.USER_MONSTER_ID_TWO, values);
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<MonsterEvolvingForUserPojo> fetchByUserId(String... values) {
		return fetch(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>start_time IN (values)</code>
	 */
	public List<MonsterEvolvingForUserPojo> fetchByStartTime(Timestamp... values) {
		return fetch(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.START_TIME, values);
	}
}