package com.lvl6.clansearch;

import static org.jooq.impl.DSL.using;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ClanChatPostDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanChatPostPojo;

@Component
public class ClanChatPostDao2 extends ClanChatPostDao{
	
	public ClanChatPostDao2() {
		super();
	}
	
	@Autowired
	public ClanChatPostDao2(DefaultConfiguration configuration) {
		super(configuration);
	}


	public ClanChatPostDao2(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Fetch records on two columns
	 */
	public List<ClanChatPostPojo> fetchForClanSearch(Date d) {
		Timestamp oneHourAgoTimestamp = new Timestamp(d.getTime());
		return using(configuration())
				.selectFrom(Tables.CLAN_CHAT_POST)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.ClanChatPost.CLAN_CHAT_POST.TIME_OF_POST.greaterThan(oneHourAgoTimestamp))
				.fetch()
				.map(mapper());
	}


}
