package com.lvl6.server.controller.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleCountForUserPojo;
import com.lvl6.retrieveutils.daos.PvpBattleCountForUserDao2;
import com.lvl6.utils.TimeUtils;

@Component
public class PvpBattleCountUtils {
	
	private static final Logger log = LoggerFactory
			.getLogger(PvpBattleCountUtils.class);
	
	
	public int getCountForPastWeek(String attackerId, List<String> defenderIds, 
			PvpBattleCountForUserDao2 pbcfuDao, TimeUtils timeUtils) {
		List<PvpBattleCountForUserPojo> pvpBattleCount = 
				pbcfuDao.getPvpBattleCountBetweenUsers(attackerId, defenderIds);
		
		int count = 0;
		Date now = new Date();
		for(PvpBattleCountForUserPojo pbcfur : pvpBattleCount) {
			Date battleDate = new Date(pbcfur.getDate().getTime());
			if(Math.abs(timeUtils.numDaysDifference(now, battleDate)) < 8) {
				count = count + pbcfur.getCount();
			}
		}
		return count;
	}
	
	public void insertOrUpdatePvpBattleCount(String attackerId, String defenderId,
			PvpBattleCountForUserDao2 pbcfuDao, TimeUtils timeUtils) {
		List<String> defenderIds = new ArrayList<String>();
		defenderIds.add(defenderId);
		List<PvpBattleCountForUserPojo> pvpBattleCount = 
				pbcfuDao.getPvpBattleCountBetweenUsers(attackerId, defenderIds);
		
		Date now = new Date();
		boolean updated = false;
		for(PvpBattleCountForUserPojo pbcfur : pvpBattleCount) {
			Date battleDate = new Date(pbcfur.getDate().getTime());
			if(timeUtils.numMinutesDifference(now, battleDate) < 1440) {
				pbcfur.setCount(pbcfur.getCount()+1);
				pbcfuDao.update(pbcfur);
				updated = true;
				break;
			}
		}
		
		if(!updated) {
			PvpBattleCountForUserPojo pvcfup = new PvpBattleCountForUserPojo();
			pvcfup.setAttackerId(attackerId);
			pvcfup.setDefenderId(defenderId);
			pvcfup.setDate(new Timestamp(now.getTime()));
			pvcfup.setCount(1);
			pbcfuDao.insert(pvcfup);
		}	
	}
	
}
