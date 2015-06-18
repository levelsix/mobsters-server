package com.lvl6.clansearch;

import static org.jooq.impl.DSL.using;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jooq.Configuration;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ClanMemberTeamDonationDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanMemberTeamDonationPojo;

@Component
public class ClanMemberTeamDonationDao2 extends ClanMemberTeamDonationDao{

	
	
	public ClanMemberTeamDonationDao2(Configuration configuration) {
		super(configuration);
	}

	public List<ClanMemberTeamDonationPojo> fetchForClanSearch(Date d) {
		Timestamp oneDayAgoTimestamp = new Timestamp(d.getTime());
		return using(configuration())
				.selectFrom(Tables.CLAN_MEMBER_TEAM_DONATION)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION.TIME_OF_SOLICITATION
						.greaterThan(oneDayAgoTimestamp))
				.fetch()
				.map(mapper());
	}


}
