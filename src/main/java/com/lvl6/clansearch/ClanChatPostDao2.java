package com.lvl6.clansearch;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.using;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanChatPost;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ClanChatPostDao;

@Component
public class ClanChatPostDao2 extends ClanChatPostDao{

	/**
	 * Fetch records on two columns
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanChatPost> fetchForClanSearch(Date d) {
		Timestamp oneHourAgoTimestamp = new Timestamp(d.getTime());
		return using(configuration())
				.selectFrom(Tables.CLAN_CHAT_POST)
				.where(ClanChatPost.CLAN_CHAT_POST.TIME_OF_POST.greaterThan(oneHourAgoTimestamp))
				.fetch()
				.map(mapper());
	}


}
