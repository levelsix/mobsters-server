package com.lvl6.server.controller.utils;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IMap;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils2;
import com.lvl6.server.Locker;

@Component
public class ClanEventUtil implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(ClanEventUtil.class);

	@Autowired
	protected ClanEventPersistentForUserRetrieveUtils2 clanEventPersistentForUserRetrieveUtil;

	@javax.annotation.Resource(name = "initializationFlags")
	protected IMap<String, Boolean> initializationFlagsMap;

	public IMap<String, Boolean> getInitializationFlagsMap() {
		return initializationFlagsMap;
	}
	public void setInitializationFlagsMap(
			IMap<String, Boolean> initializationFlagsMap) {
		this.initializationFlagsMap = initializationFlagsMap;
	}


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
	//refers to IMap name above
	private final String clanRaidMonsterDmgMapName = "clanRaidMonsterDmgMap"; 
	
	@Autowired
	protected Locker locker;



	/**
	 * @param clanId
	 * @return returns 0 if no entry for clanId exists, otherwise returns value stored.
	 */
	public int getCrsmDmgForClanId(String clanIdStr) {
//		String clanIdStr = clanId.toString();

		if (!clanRaidMonsterDmgMap.containsKey(clanIdStr)) {
			log.info(String.format(
				"no crsmDmg for clanId=%s, returning 0",
				clanIdStr));
			return 0;
		}

		int curCrsmDmg = clanRaidMonsterDmgMap.get(clanIdStr);
		log.info(String.format("crsmDmg exists for clanId=%s, dmg=%s",
			clanIdStr, curCrsmDmg));
		return curCrsmDmg;
	}

	//method either relatively or absolutely updates a clan's crsmDmg
	/**
	 * @param clanId
	 * @param crsmDmgDelta
	 * @param replaceCrsmDmg If false then perform relative update. Otherwise, replace
	 * 		existing damage with crsmDmgDelta
	 */
	public void updateClanIdCrsmDmg(String clanIdStr, Integer crsmDmgDelta,
			boolean replaceCrsmDmg) {
//		String clanIdStr = clanId.toString();

		if (replaceCrsmDmg) {
			log.info(String.format(
				"replacing clan's crsmDmg. clanId=%s, dmg=%s",
				clanIdStr, crsmDmgDelta));
			clanRaidMonsterDmgMap.put(clanIdStr, crsmDmgDelta);
			return;
		}

		if (!clanRaidMonsterDmgMap.containsKey(clanIdStr)) {
			log.info(String.format(
				"updating clan's crsmDmg. clanId=%s, dmg=%s",
				clanIdStr, crsmDmgDelta));
			clanRaidMonsterDmgMap.put(clanIdStr, crsmDmgDelta);
			return;

		} else {
			int curCrsmDmg = clanRaidMonsterDmgMap.get(clanIdStr);
			int newCrsmDmg = curCrsmDmg + crsmDmgDelta;

			log.info(String.format(
				"adding to clan's crsmDmg. clanId=%s, dmg=%s, curCrsmDmg=%s, newCrsmDmg=%s",
				clanIdStr, crsmDmgDelta, curCrsmDmg, newCrsmDmg));
			clanRaidMonsterDmgMap.put(clanIdStr, newCrsmDmg);
			return;
		}

	}

	public void deleteCrsmDmgForClanId(String clanId) {
		log.info(String.format(
			"deleting crsm dmg for clanId=%s", clanId));
		String clanIdStr = clanId.toString();
		if (clanRaidMonsterDmgMap.containsKey(clanIdStr)) {
			clanRaidMonsterDmgMap.remove(clanIdStr);
			log.info(String.format(
				"removed crsmDmg for clanId=%s", clanIdStr));
		}
	}





	//SETUP STUFF
	//REQUIRED INITIALIZING BEAN STUFF
	@Override
	public void afterPropertiesSet() throws Exception {
		setupClanRaidMonsterDmgMap();
	}

	protected void setupClanRaidMonsterDmgMap() {
		
		boolean gotLock = getLocker().lockHazelcastMap(clanRaidMonsterDmgMapName);
		try {
			if (gotLock) {
				boolean clanRaidMonsterDmgMapInitialized = false;
				if (initializationFlagsMap.containsKey(clanRaidMonsterDmgMapName)) {
					clanRaidMonsterDmgMapInitialized =
							initializationFlagsMap.get(clanRaidMonsterDmgMapName);
				}

				if (!clanRaidMonsterDmgMapInitialized) {
					log.debug("initializing clanRaidMonsterDmgMap");
					initializeClanRaidMonsterDmgMap();
					//record that clanRaidMonsterDmgMap is initialized
					initializationFlagsMap.put(clanRaidMonsterDmgMapName, true);
				}

			} else {
				log.warn("not initializing clanRaidMonsterDmgMap. didn't get lock for " +
						clanRaidMonsterDmgMapName);
			}

		} catch(Exception e) {
			log.error("failed to initialize clanRaidMonsterDmgMap", e);
		} finally {

			if (gotLock) {
				getLocker().unlockHazelcastMap(clanRaidMonsterDmgMapName);
			}
		}

	}
	
	protected void initializeClanRaidMonsterDmgMap() {
		//now we have all clans' crsmDmgs, put them into the clanRaidMonsterDmgMap IMap
		List<String> clanIds = null;
		Map<String, Integer> clanIdsToCrsmDmgs = clanEventPersistentForUserRetrieveUtil
				.getTotalCrsmDmgForClanIds(clanIds);

		populateClanRaidMonsterDmgMap(clanIdsToCrsmDmgs);
	}

	protected void populateClanRaidMonsterDmgMap(Map<String, Integer> clanIdsToCrsmDmgs) {
		//go through all the clans, and store them into the hazelcast distributed map
		for (String clanId : clanIdsToCrsmDmgs.keySet()) {
			String userId = clanId.toString();
			Integer crsmDmg = clanIdsToCrsmDmgs.get(clanId);

			clanRaidMonsterDmgMap.put(userId, crsmDmg);
		}
	}
	
	
	public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}
	
}
