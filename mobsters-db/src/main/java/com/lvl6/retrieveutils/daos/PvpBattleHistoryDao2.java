package com.lvl6.retrieveutils.daos;

import static org.jooq.impl.DSL.using;

import java.util.List;

import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.PvpBattleHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleHistoryPojo;

@Component
public class PvpBattleHistoryDao2 extends PvpBattleHistoryDao {

	public PvpBattleHistoryDao2() {
		super();
	}
	
	@Autowired
	public PvpBattleHistoryDao2(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * select * from pvp_battle_history where (defender_id in (?) or attacker_id in (?)) and cancelled=false order by 
	 * battle_end_time desc limit n
	*/
	public List<PvpBattleHistoryPojo> getRecentNBattlesForUserId(String userId, int n) {
		return using(configuration())
				.selectFrom(Tables.PVP_BATTLE_HISTORY)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleHistory.PVP_BATTLE_HISTORY.DEFENDER_ID.in(userId)
						.or(com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleHistory.PVP_BATTLE_HISTORY.ATTACKER_ID.in(userId))
						.and(com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleHistory.PVP_BATTLE_HISTORY.CANCELLED.eq(false)))
				.orderBy(com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleHistory.PVP_BATTLE_HISTORY.BATTLE_END_TIME.desc())
				.limit(n)
				.fetch()
				.map(mapper());
	}
	
}
