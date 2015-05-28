package com.lvl6.server.controller.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.PvpLeagueForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpLeagueForUser;

@Component
public class PvpUtils {

	private static final Logger log = LoggerFactory.getLogger(PvpUtils.class);

	public void updateClanIdInPvpLeagueForUser(String userId, String clanId,
			PvpLeagueForUserDao pvpLeagueForUserDao) {
//		List<PvpLeagueForUser> pvplfuList = pvpLeagueForUserDao.fetchByUserId(userId);
//		if(pvplfuList.size() > 1) {
//			log.error("userId {} has more than one pvp league for user??", userId);
//		}
//		for(PvpLeagueForUser pvplfu : pvplfuList) {
//			pvplfu.setClanId(clanId);
//		}
//		pvpLeagueForUserDao.update(pvplfuList);
	}




}
