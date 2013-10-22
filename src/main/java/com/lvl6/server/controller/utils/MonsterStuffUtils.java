package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;


public class MonsterStuffUtils {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	//extract and return the ids from the UserMonsterCurrentHealthProtos, also
	//return mapping of userMonsterIdToExpectedHealth
  public static List<Long> getUserMonsterIds(List<UserMonsterCurrentHealthProto> umchpList,
  		Map<Long, Integer> userMonsterIdToExpectedHealth) {
  	List<Long> idList = new ArrayList<Long>();
  	
  	if(null == umchpList) {
  		return idList;
  	}
  	
  	for(UserMonsterCurrentHealthProto umchp : umchpList) {
  		long id = umchp.getUserMonsterId();
  		idList.add(id);
  		int health = umchp.getCurrentHealth();
  		userMonsterIdToExpectedHealth.put(id, health);
  	}
  	return idList;
  }
  
  //transforming list to map with key = monsterForUserId.
  public static Map<Long, UserMonsterHealingProto> convertIntoUserMonsterIdToUmhpProtoMap(
  		List<UserMonsterHealingProto> umhpList) {
  	Map<Long, UserMonsterHealingProto> returnMap = new HashMap<Long, UserMonsterHealingProto>();
  	if (null == umhpList) {
  		return returnMap;
  	}
  	for (UserMonsterHealingProto umhp : umhpList) {
  		long id = umhp.getUserMonsterId();
  		returnMap.put(id, umhp);
  	}
  	
  	return returnMap;
  }
  
  public static Map<Long, UserEnhancementItemProto> convertIntoUserMonsterIdToUeipProtoMap(
  		List<UserEnhancementItemProto> ueipList) {
  	Map<Long, UserEnhancementItemProto> returnMap = new HashMap<Long, UserEnhancementItemProto>();
  	if(null == ueipList) {
  		return returnMap;
  	}
  	for (UserEnhancementItemProto ueip : ueipList) {
  		long id = ueip.getUserMonsterId();
  		returnMap.put(id, ueip);
  	}
  	
  	return returnMap;
  }
  
  /*
   * selected monsters (the second argument) might be modified
   */
  public static void retainValidMonsters(Set<Long> domain,  Map<Long, ?> selectedMonsters,
  		boolean keepThingsInDomain, boolean keepThingsNotInDomain) {
  	Set<Long> selectedIds = selectedMonsters.keySet();
  	
  	for (Long selectedId : selectedIds) {
  		if (domain.contains(selectedId) && keepThingsInDomain) {
  			continue;
  		}
  		if (!domain.contains(selectedId) && keepThingsNotInDomain) {
  			continue;
  		}
  		//since selectedId isn't in the domain and want to keep things in domain
  		//or is in the domain and want to keep things not in domain, remove it
  		Object umhp = selectedMonsters.remove(selectedId);
  		log.warn("Not valid. object=" + umhp + "; keepThingsInDomain=" + keepThingsInDomain +
  				"; keepThingsNotInDomain=" + keepThingsNotInDomain);
  	}
  }

  public static List<MonsterHealingForUser> convertToMonsterHealingForUser(
  		int userId, Map<Long, UserMonsterHealingProto> protos) {
  	
  	List<MonsterHealingForUser> nonProtos = new ArrayList<MonsterHealingForUser>();
  	
  	for(UserMonsterHealingProto umhp: protos.values()) {
  		Long monsterForUserId = umhp.getUserMonsterId();
  		Date expectedStartTime = new Date(umhp.getExpectedStartTimeMillis());
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		MonsterHealingForUser mhfu = new MonsterHealingForUser(userId,
  				monsterForUserId, expectedStartTime);//, queuedTime);
  		nonProtos.add(mhfu);
  	}
  	
  	return nonProtos;
  }
  
  public static List<MonsterEnhancingForUser> convertToMonsterEnhancingForUser(
  		int userId, Map<Long, UserEnhancementItemProto> protos) {
  	
  	List<MonsterEnhancingForUser> nonProtos = new ArrayList<MonsterEnhancingForUser>();
  	
  	for(UserEnhancementItemProto ueip: protos.values()) {
  		Long monsterForUserId = ueip.getUserMonsterId();
  		long startTimeMillis = ueip.getExpectedStartTimeMillis();
  		Date expectedStartTime;
  		
  		if (!ueip.hasExpectedStartTimeMillis() || startTimeMillis <= 0) {
  			expectedStartTime = null;
  		} else {
  			expectedStartTime = new Date(ueip.getExpectedStartTimeMillis());
  		}
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		MonsterEnhancingForUser mefu = new MonsterEnhancingForUser(userId,
  				monsterForUserId, expectedStartTime);//, queuedTime);
  		nonProtos.add(mefu);
  	}
  	
  	return nonProtos;
  }
  
}