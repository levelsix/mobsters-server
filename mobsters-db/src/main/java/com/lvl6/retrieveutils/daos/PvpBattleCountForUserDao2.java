package com.lvl6.retrieveutils.daos;

import static org.jooq.impl.DSL.using;

import java.util.List;

import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.PvpBattleCountForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleCountForUserPojo;

@Component
public class PvpBattleCountForUserDao2 extends PvpBattleCountForUserDao {

	
	public PvpBattleCountForUserDao2() {
		super();
	}
	
	@Autowired
	public PvpBattleCountForUserDao2(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}
	
	
	public List<PvpBattleCountForUserPojo> getPvpBattleCountBetweenUsers(String attackerId, List<String> defenderIds) {
		return using(configuration())
				.selectFrom(Tables.PVP_BATTLE_COUNT_FOR_USER)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER.ATTACKER_ID.in(attackerId)
						.and(com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER.DEFENDER_ID.in(defenderIds)))
				.fetch()
				.map(mapper());
	}
	
}
