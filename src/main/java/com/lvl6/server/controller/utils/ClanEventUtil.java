package com.lvl6.server.controller.utils;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IMap;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils;

@Component
public class ClanEventUtil implements InitializingBean {
	
		private static final Logger log = LoggerFactory.getLogger(ClanEventUtil.class);
		
		
		//need to put this in spring-hazelcast.xml
		//distributed map that is seen across all our servers
		@javax.annotation.Resource(name = "clanRaidMonsterDmgMap")
		protected IMap<String, Integer> clanRaidMonsterDmgMap;

		public IMap<String, Integer> getClanRaidMonsterDmgMap() {
			return clanRaidMonsterDmgMap;
		}

		public void setClanRaidMonsterDmgMap(IMap<String, Integer> clanRaidMonsterDmgMap) {
			this.clanRaidMonsterDmgMap = clanRaidMonsterDmgMap;
		}

		
		
		
		public int getCrsmDmgForClanId(Integer clanId) {
			String clanIdStr = clanId.toString();
			
			if (!clanRaidMonsterDmgMap.containsKey(clanIdStr)) {
				log.info("no crsmDmg for clanId=" + clanIdStr + " returning 0");
				return 0;
			}
			
			int curCrsmDmg = clanRaidMonsterDmgMap.get(clanIdStr);
			log.info("crsmDmg exists for clanId=" + clanIdStr + "\t dmg=" + curCrsmDmg);
			return curCrsmDmg;
		}
		
		
		//method either relatively or absolutely updates a clan's crsmDmg
		public void updateClanIdCrsmDmg(Integer clanId, Integer crsmDmgDelta,
				boolean replaceCrsmDmg) {
			String clanIdStr = clanId.toString();
			
			if (replaceCrsmDmg) {
				log.info("replacing clan's crsmDmg. clanId=" + clanId + "\t dmg=" + crsmDmgDelta);
				clanRaidMonsterDmgMap.put(clanIdStr, crsmDmgDelta);
				return;
			}
			
			if (!clanRaidMonsterDmgMap.containsKey(clanIdStr)) {
				log.info("updating clan's crsmDmg. clanId=" + clanId + "\t dmg=" + crsmDmgDelta);
				clanRaidMonsterDmgMap.put(clanIdStr, crsmDmgDelta);
				return;
				
			} else {
				int curCrsmDmg = clanRaidMonsterDmgMap.get(clanIdStr);
				int newCrsmDmg = curCrsmDmg + crsmDmgDelta;
				
				log.info("adding to clan's crsmDmg. clanId=" + clanId + "\t dmg=" + crsmDmgDelta +
						"\t curCrsmDmg=" + curCrsmDmg + "\t newCrsmDmg=" + newCrsmDmg);
				clanRaidMonsterDmgMap.put(clanIdStr, newCrsmDmg);
				return;
			}
			
			
		}
		
		
		
		
    
    //SETUP STUFF
    //REQUIRED INITIALIZING BEAN STUFF
		@Override
    public void afterPropertiesSet() throws Exception {
			setupClanRaidMonsterDmgMap();
    }
		
    protected void setupClanRaidMonsterDmgMap() {

    	//now we have all clans' crsmDmgs, put them into the clanRaidMonsterDmgMap IMap
    	List<Integer> clanIds = null;
			Map<Integer, Integer> clanIdsToCrsmDmgs = ClanEventPersistentForUserRetrieveUtils
					.getTotalCrsmDmgForClanIds(clanIds);
			
			populateClanRaidMonsterDmgMap(clanIdsToCrsmDmgs);
    }

    protected void populateClanRaidMonsterDmgMap(Map<Integer, Integer> clanIdsToCrsmDmgs) {
    	//go through all the clans, and store them into the hazelcast distributed map
    	for (Integer clanId : clanIdsToCrsmDmgs.keySet()) {
    		String userId = clanId.toString();
    		Integer crsmDmg = clanIdsToCrsmDmgs.get(clanId);
    		
    		clanRaidMonsterDmgMap.put(userId, crsmDmg);
    	}
    }

}