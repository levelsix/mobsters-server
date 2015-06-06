package com.lvl6.clansearch;

import static org.jooq.impl.DSL.using;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelp;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelpCountForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ClanHelpCountForUserDao;


public class ClanHelpCountForUserDao2 extends ClanHelpCountForUserDao{

	/**
	 * Fetch records on two columns
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanHelpCountForUser> fetchForClanSearch(Date d) {
		Timestamp oneDayAgoTimestamp = new Timestamp(d.getTime());
		return using(configuration())
				.selectFrom(Tables.CLAN_HELP_COUNT_FOR_USER)
				.where(ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.DATE.greaterThan(oneDayAgoTimestamp))
				.fetch()
				.map(mapper());
	}


}
