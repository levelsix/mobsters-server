package com.lvl6.clansearch;

import static org.jooq.impl.DSL.using;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jooq.Configuration;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelpCountForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ClanHelpCountForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanHelpCountForUserPojo;

@Component
public class ClanHelpCountForUserDao2 extends ClanHelpCountForUserDao{
	
	public ClanHelpCountForUserDao2() {
		super();
	}
	
	public ClanHelpCountForUserDao2(Configuration configuration) {
		super(configuration);
	}

	
	
	public ClanHelpCountForUserDao2(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Fetch records on two columns
	 */
	public List<ClanHelpCountForUserPojo> fetchForClanSearch(Date d) {
		Timestamp oneDayAgoTimestamp = new Timestamp(d.getTime());
		return using(configuration())
				.selectFrom(Tables.CLAN_HELP_COUNT_FOR_USER)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER.DATE.greaterThan(oneDayAgoTimestamp))
				.fetch()
				.map(mapper());
	}


}
