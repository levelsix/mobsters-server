/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanEventPersistentForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanEventPersistentForUserRecord;

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
public class ClanEventPersistentForUserDao extends DAOImpl<ClanEventPersistentForUserRecord, ClanEventPersistentForUserPojo, Record2<String, String>> {

	/**
	 * Create a new ClanEventPersistentForUserDao without any configuration
	 */
	public ClanEventPersistentForUserDao() {
		super(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER, ClanEventPersistentForUserPojo.class);
	}

	/**
	 * Create a new ClanEventPersistentForUserDao with an attached configuration
	 */
	public ClanEventPersistentForUserDao(Configuration configuration) {
		super(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER, ClanEventPersistentForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<String, String> getId(ClanEventPersistentForUserPojo object) {
		return compositeKeyRecord(object.getUserId(), object.getClanId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByUserId(String... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>clan_id IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByClanId(String... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CLAN_ID, values);
	}

	/**
	 * Fetch records that have <code>cr_id IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByCrId(Integer... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CR_ID, values);
	}

	/**
	 * Fetch records that have <code>cr_dmg_done IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByCrDmgDone(Integer... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CR_DMG_DONE, values);
	}

	/**
	 * Fetch records that have <code>crs_id IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByCrsId(Integer... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRS_ID, values);
	}

	/**
	 * Fetch records that have <code>crs_dmg_done IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByCrsDmgDone(Integer... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRS_DMG_DONE, values);
	}

	/**
	 * Fetch records that have <code>crsm_id IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByCrsmId(Integer... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRSM_ID, values);
	}

	/**
	 * Fetch records that have <code>crsm_dmg_done IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByCrsmDmgDone(Integer... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRSM_DMG_DONE, values);
	}

	/**
	 * Fetch records that have <code>user_monster_id_one IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByUserMonsterIdOne(String... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_MONSTER_ID_ONE, values);
	}

	/**
	 * Fetch records that have <code>user_monster_id_two IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByUserMonsterIdTwo(String... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_MONSTER_ID_TWO, values);
	}

	/**
	 * Fetch records that have <code>user_monster_id_three IN (values)</code>
	 */
	public List<ClanEventPersistentForUserPojo> fetchByUserMonsterIdThree(String... values) {
		return fetch(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_MONSTER_ID_THREE, values);
	}
}
