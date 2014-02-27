package com.lvl6.server.controller.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;


public class ClanStuffUtils {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  public static ClanEventPersistentForClan createClanEventPersistentForClan(
  		PersistentClanEventClanInfoProto pceip) {
  	log.info("creating ClanEventPersistentForClan");
  	
  	int clanId = pceip.getClanEventId();
  	int clanEventPersistentId = pceip.getClanEventId();
  	int crId = pceip.getClanEventId();
  	int crsId = pceip.getClanRaidStageId();
  	Date stageStartTime = null;
  	long stageStartTimeMillis = pceip.getStageStartTime();
  	if (stageStartTimeMillis > 0) {
  		stageStartTime = new Date(stageStartTimeMillis);
  	}
  	int crsmId = pceip.getCrsmId();
  	
  	Date stageMonsterStartTime = null;
  	long stageMonsterStartTimeMillis = pceip.getStageMonsterStartTime();
  	if (stageMonsterStartTimeMillis > 0) { 
  		stageMonsterStartTime = new Date(stageMonsterStartTimeMillis);
  	}
  	
  	return new ClanEventPersistentForClan(clanId, clanEventPersistentId, crId, crsId,
  			stageStartTime, crsmId, stageMonsterStartTime);
  }
}
