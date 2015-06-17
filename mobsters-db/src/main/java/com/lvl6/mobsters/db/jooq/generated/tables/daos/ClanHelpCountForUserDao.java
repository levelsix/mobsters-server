/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelpCountForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanHelpCountForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanHelpCountForUserRecord;

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
public class ClanHelpCountForUserDao extends DAOImpl<ClanHelpCountForUserRecord, ClanHelpCountForUserPojo, Record3<String, String, Timestamp>> {

	/**
	 * Create a new ClanHelpCountForUserDao without any configuration
	 */
	public ClanHelpCountForUserDao() {
		super(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER, ClanHelpCountForUserPojo.class);
	}

	/**
	 * Create a new ClanHelpCountForUserDao with an attached configuration
	 */
	public ClanHelpCountForUserDao(Configuration configuration) {
		super(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER, ClanHelpCountForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record3<String, String, Timestamp> getId(ClanHelpCountForUserPojo object) {
		return compositeKeyRecord(object.getUserId(), object.getClanId(), object.getDate());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<ClanHelpCountForUserPojo> fetchByUserId(String... values) {
		return fetch(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>clan_id IN (values)</code>
	 */
	public List<ClanHelpCountForUserPojo> fetchByClanId(String... values) {
		return fetch(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.CLAN_ID, values);
	}

	/**
	 * Fetch records that have <code>date IN (values)</code>
	 */
	public List<ClanHelpCountForUserPojo> fetchByDate(Timestamp... values) {
		return fetch(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.DATE, values);
	}

	/**
	 * Fetch records that have <code>solicited IN (values)</code>
	 */
	public List<ClanHelpCountForUserPojo> fetchBySolicited(Integer... values) {
		return fetch(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.SOLICITED, values);
	}

	/**
	 * Fetch records that have <code>given IN (values)</code>
	 */
	public List<ClanHelpCountForUserPojo> fetchByGiven(Integer... values) {
		return fetch(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.GIVEN, values);
	}
}
