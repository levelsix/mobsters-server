package com.lvl6.mobsters.jooq.daos.service;

import static org.jooq.impl.DSL.using;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ClanStrengthRecord;

@Component
public class ClanStrengthService {
	
	private Configuration configuration;

	@Autowired
	public ClanStrengthService(Configuration config) {
		this.configuration = config;
	}
	
	public List<String> getTopNClans(int num) {
		List<ClanStrengthRecord> clanStrengths = using(configuration)
				.selectFrom(Tables.CLAN_STRENGTH)
				.orderBy(com.lvl6.mobsters.db.jooq.generated.tables.ClanStrength.CLAN_STRENGTH.STRENGTH.desc())
				.fetch();
		
		List<String> returnList = new ArrayList<String>();
		for(int i=0; i<num; i++) {
			returnList.add(clanStrengths.get(i).getClan());
		}
		return returnList;
	}
	
	public Map<String, Long> getClanMemberCountMap() {
		List<Record2<String, Long>> clanMemberCount = using(configuration)
				.select(com.lvl6.mobsters.db.jooq.generated.tables.ClanStrength.CLAN_STRENGTH.CLAN, com.lvl6.mobsters.db.jooq.generated.tables.ClanStrength.CLAN_STRENGTH.MEMBERS)
				.fetch();
		
		Map<String, Long> returnMap = new HashMap<String, Long>();
		for(Record2<String, Long> record : clanMemberCount) {
			returnMap.put(record.value1(), record.value2());
		}
		return returnMap;
	}

}
