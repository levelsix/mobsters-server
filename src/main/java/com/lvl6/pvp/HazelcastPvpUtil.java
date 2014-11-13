package com.lvl6.pvp;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.util.IterationType;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;
import com.lvl6.scriptsjava.generatefakeusers.NameGenerator;
import com.lvl6.server.Locker;
import com.lvl6.utils.ConnectedPlayer;

@Component
public class HazelcastPvpUtil implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(HazelcastPvpUtil.class);

	//these are the users that are online
	@javax.annotation.Resource(name = "playersByPlayerId")
	IMap<Integer, ConnectedPlayer> playersByPlayerId;

	public IMap<Integer, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}
	public void setPlayersByPlayerId(IMap<Integer, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	} 


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
	@javax.annotation.Resource(name = "pvpUserMap")

	protected IMap<String, PvpUser> pvpUserMap;
	public IMap<String, PvpUser> getPvpUserMap() {
		return pvpUserMap;
	}
	public void setPvpUserMap(IMap<String, PvpUser> pvpUserMap) {
		this.pvpUserMap = pvpUserMap;
	}
	private final String pvpUserMapName = "pvpUserMap"; //refers to IMap name above


	@Autowired
	protected Locker locker;

	//Just used to load a .txt file
	@Autowired
	protected TextFileResourceLoaderAware textFileResourceLoaderAware;

	//		//Used to create a distributed map. Distributed map is seen across all our servers
	//		@Autowired
	//		protected HazelcastInstance hazel;

	//Used to get offline people that can be attacked in pvp
	@Autowired
	protected PvpUserRetrieveUtil pvpUserRetrieveUtil;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	

	//properties used to create random names, related: textFileResourceLoaderAware
	private static int syllablesInName1 = 2;
	private static int syllablesInName2 = 3;
	private static int numRandomNames = 2000;
	private static Random rand;
	private List<String> randomNames; //should this be a distributed collection?
	private static final String FILE_OF_RANDOM_NAMES = "classpath:namerulesElven.txt";
	private boolean useDatabaseInstead = false;
	
	/**
	 * 	In case hazelcast is giving trouble and the database should be
	 *	used instead. Should be called before retrieving users.
	 * @return
	 */
	public boolean isUseDatabaseInstead() {
		return useDatabaseInstead;
	}
	public void setUseDatabaseInstead(boolean useDatabaseInstead) {
		this.useDatabaseInstead = useDatabaseInstead;
	}
	
	//TODO: consider moving to PvpUserRetrieveUtils
	//METHOD TO ACTUALLY USE IMAP, distributed map
	public Set<PvpUser> retrievePvpUsers(int minElo, int maxElo, Date now,
			int limit, Collection<Integer> excludeIds) {
		Collection<String> excludeIdStrs = new ArrayList<String>();
		for (Integer i : excludeIds) {
			excludeIdStrs.add(i.toString());
		}
		
		if (isUseDatabaseInstead()) {
			log.info("querying db instead of hazelcast");
			return getPvpUserRetrieveUtil().retrievePvpUsers(minElo, maxElo,
					now, limit, excludeIdStrs);
		} else {
			log.info("retrieving from hazelcast instead of db");
			return retrievePvpUsersViaHazelcast(minElo, maxElo, now, limit,
					excludeIdStrs);
		}

	}
	
	//look here for examples on using PagingPredicate
	//https://github.com/hazelcast/hazelcast/blob/master/hazelcast/src/test/java/com/hazelcast/map/SortLimitTest.java
	protected Set<PvpUser> retrievePvpUsersViaHazelcast(int minElo, int maxElo,
			Date now, int limit, Collection<String> excludeIds) {
		log.info("querying for people to attack. shieldEndTime should be before now=" +
				now + "\t elo should be between minElo=" + minElo + ", maxElo=" +
				maxElo + "\t (page size aka) limit=" + limit);

		Predicate<?,?> pred = generatePredicate(minElo, maxElo, now, limit, excludeIds);
		
		Set<PvpUser> users = (Set<PvpUser>) pvpUserMap.values(pred);
		log.info("users:" + users);

		
		return users;
	}
	
	/*
	protected Predicate<?, ?> generatePredicate(int minElo, int maxElo, Date now,
			int limit, Collection<String> excludeIds) {
		String inBattleShieldEndTimeStr = PvpConstants.PVP_USER__IN_BATTLE_END_TIME;
		String shieldEndTimeStr = PvpConstants.PVP_USER__SHIELD_END_TIME;
		String eloStr = PvpConstants.PVP_USER__ELO;
		EntryObject e = new PredicateBuilder().getEntryObject();
		Predicate<?, ?> predicate = null;

		if (null != excludeIds && !excludeIds.isEmpty()) {
			String userIdStr = PvpConstants.PVP_USER__USER_ID;
			//doesn't work >:[, list is not a Comparable data type
//			Predicate<?, ?> pIn = Predicates.in(userIdStr, excludeIds);
//			Predicate<?, ?> pNotIn = Predicates.not(pIn);
			
			Map<String, Object> lessThanConditions = null;
			Map<String, Object> greaterThanConditions = null;
			
			boolean isNotIn = true;
			Map<String, Collection<?>> inConditions =
					new HashMap<String, Collection<?>>(); 
			inConditions.put(userIdStr, excludeIds);
			
			String sql = getQueryConstructionUtil().createWhereConditionString(
					lessThanConditions, greaterThanConditions, isNotIn, inConditions);
			log.info("predicate used in querying hazelcast object: " + sql);
			
			Predicate<?,?> sqlPredicate = new SqlPredicate(sql);
			
			
			//the <?, ?> is to prevent a warning from showing up in Eclipse...lol
			predicate = e.get(shieldEndTimeStr).lessThan(now)
					.and(e.get(inBattleShieldEndTimeStr).lessThan(now))
					.and(e.get(eloStr).between(minElo, maxElo))
					.and(sqlPredicate);
			
		} else {
			predicate = e.get(shieldEndTimeStr).lessThan(now)
					.and(e.get(inBattleShieldEndTimeStr).lessThan(now))
					.and(e.get(eloStr).between(minElo, maxElo));
		}
		
		PagingPredicate advPredicate = new PagingPredicate(predicate,
				new PvpUserComparator(true, IterationType.VALUE), limit);
		
		return advPredicate;
	}*/
	
	//doesn't matter if use java.util.Date or java.sql.Timestamp
	public Predicate<?, ?> generatePredicate(int minElo, int maxElo, Date now,
			int limit, Collection<String> excludeIds) {
		String inBattleShieldEndTimeStr =
				PvpConstants.PVP_USER__IN_BATTLE_END_TIME;
		String shieldEndTimeStr = PvpConstants.PVP_USER__SHIELD_END_TIME;
		String eloStr = PvpConstants.PVP_USER__ELO;
		
		//need to enclose whitespaces in a single quote
		String nowStr = "'" + now + "'";
		Map<String, Object> lessThanConditions = new HashMap<String, Object>();
		lessThanConditions.put(inBattleShieldEndTimeStr, nowStr);
		lessThanConditions.put(shieldEndTimeStr, nowStr);
		lessThanConditions.put(eloStr, maxElo);
		
		Map<String, Object> greaterThanConditions =
				new HashMap<String, Object>();
		greaterThanConditions.put(eloStr, minElo);
		
		boolean isNotIn = true;
		Map<String, Collection<?>> inConditions = null;
		if (null != excludeIds && !excludeIds.isEmpty()) {
			String userIdStr = PvpConstants.PVP_USER__USER_ID;
			inConditions = new HashMap<String, Collection<?>>(); 
			inConditions.put(userIdStr, excludeIds);
		}
		
		String sql = getQueryConstructionUtil().createWhereConditionString(
				lessThanConditions, greaterThanConditions, isNotIn, inConditions);
		log.info("predicate used in querying hazelcast object: " + sql);
		
		Predicate<?,?> sqlPredicate = new SqlPredicate(sql);
		
		PagingPredicate advPredicate = new PagingPredicate(sqlPredicate,
				new PvpUserComparator(true, IterationType.VALUE), limit);
		
		return advPredicate;
	}


	//METHODS TO GET AND SET AN OFFLINEPVPUSER, WHICH ALL SERVERS WILL SEE
	public PvpUser getPvpUser(String userId) {
		String userIdStr = String.valueOf(userId);
		if (isUseDatabaseInstead()) {
			log.info("getting a user from db instead of hazelcast");
			return getPvpUserRetrieveUtil().getUserPvpLeagueForId(userIdStr);
		} else {
			log.info("getting a user from hazelcast instead of db");
			return getPvpUserViaHazelcast(userIdStr);
		}
	}
	
	public Map<String, PvpUser> getPvpUsers(Collection<Integer> userIds) {
		List<String> stringIds = Lists.transform(
			new ArrayList<Integer>(userIds), Functions.toStringFunction());
			
		if (isUseDatabaseInstead()) {
			log.info("getting users from db instead of hazelcast. userIds=" + userIds);
			return getPvpUserRetrieveUtil().getUserPvpLeagueForUsers(stringIds);
		}
		
		Map<String, PvpUser> users = new HashMap<String, PvpUser>();
		for (Integer userId : userIds) {
			String userIdStr = String.valueOf(userId);
			
			users.put(userIdStr, getPvpUserViaHazelcast(userIdStr));
		}
		return users;
	}
	
	protected PvpUser getPvpUserViaHazelcast(String userIdStr) {
		if (playersByPlayerId.containsKey(userIdStr)) {
			log.info("PvpUser is online, in ConnectedPlayers map. id=" + userIdStr);
		}

		if (!pvpUserMap.containsKey(userIdStr)) {
			log.warn("trying to access nonexistent PvpUser with id: " + userIdStr);
			return null;
		} else {
			return pvpUserMap.get(userIdStr);
		}
	}

	public void replacePvpUser(PvpUser userOpu, String userId) {
		if (isUseDatabaseInstead()) {
			return;
		}
		String userIdStr = userOpu.getUserId();

		//maybe not necessary to remove, but eh
		removePvpUser(userId);
		pvpUserMap.put(userIdStr, userOpu);

	}

	//can attack people who are online
	//    //if the PvpUser doesn't exist, then don't save it because
	//    //the PvpUser is probably online now. Otherwise, store the user.
	//    public void updatePvpUser(PvpUser enemy) {
	//    	String userIdStr = enemy.getUserId();
	//
	//    	if (!pvpUserMap.containsKey(userIdStr)) {
	//    		log.warn("trying to update nonexistent PvpUser with id: " + userIdStr +
	//    				" PROBABLY because the user is online, so not saving");
	//    	} else {
	//    		PvpUser existing = pvpUserMap.get(userIdStr);
	//    		pvpUserMap.put(userIdStr, enemy);
	//    		log.info("updated pvpUser: " + userIdStr + "\t old=" + existing +
	//    				"\t new=" + enemy);
	//    	}
	//
	//    	if (playersByPlayerId.containsKey(Integer.parseInt(userIdStr))) {
	//    		log.info("PvpUser is online, in ConnectedPlayers map. id=" + userIdStr);
	//    	}
	//    }

//	public void removePvpUser(String userId) {
	public void removePvpUser(String userIdStr) {
		if (isUseDatabaseInstead()) {
			return;
		}
//		String userIdStr = String.valueOf(userId);

		if (pvpUserMap.containsKey(userIdStr)) {
			log.info("removing userId from available pvp enemies. userId=" + userIdStr);
		}
		pvpUserMap.remove(userIdStr);
	}

	//FOR FAKE USERS
	public String getRandomName() {
		int len = randomNames.size();
		int randInt = rand.nextInt(len);

		return randomNames.get(randInt);
	}





	//SETUP STUFF
	//REQUIRED INITIALIZING BEAN STUFF
	@Override
	public void afterPropertiesSet() throws Exception {
		createRandomNames();
		setupPvpUserMap();
	}

	//creates the random names for fake users
	protected void createRandomNames() {
		rand = new Random();
		Resource nameFile = getTextFileResourceLoaderAware().getResource(FILE_OF_RANDOM_NAMES);

		try {
			//maybe don't need to close, deallocate nameFile?
			if (!nameFile.exists()) {
				log.error("file with random names does not exist. filePath=" + FILE_OF_RANDOM_NAMES);
				return;
			}

			randomNames = new ArrayList<String>();
			NameGenerator nameGenerator = new NameGenerator(nameFile);
			if (null != nameGenerator) {
				log.info("creating random ELVEN NAMES");
				for (int i = 0; i < numRandomNames; i++) {
					createName(rand, nameGenerator);
				}

				log.info("num rand ELVEN NAMES created:" + randomNames.size());
			}

		} catch (Exception e) {
			log.error("could not create fake user name", e);
		}
	}

	protected void createName(Random rand, NameGenerator nameGenerator) {
		int syllablesInName = (Math.random() < .5) ? syllablesInName1 : syllablesInName2;
		String name = nameGenerator.compose(syllablesInName);
		if (Math.random() < .5) {
			name = name.toLowerCase();
		}
		if (Math.random() < .3) {
			name = name + (int)(Math.ceil(Math.random() * 98));
		}
		randomNames.add(name);

	}

	protected void setupPvpUserMap() {

		boolean gotLock = getLocker().lockHazelcastMap(pvpUserMapName);
		try {
			if (gotLock) {
				boolean pvpUserMapInitialized = false;
				if (initializationFlagsMap.containsKey(pvpUserMapName)) {
					pvpUserMapInitialized = initializationFlagsMap.get(pvpUserMapName);
				}

				if (!pvpUserMapInitialized) {
					log.debug("initializing pvpUserMap");
					initializePvpUserMap();
					//record that pvpUserMap is initialized
					initializationFlagsMap.put(pvpUserMapName, true);
				} else {
					log.info("\t\t hazelcast map for pvp users is initialized already");
				}

			} else {
				log.warn("not initializing pvpUserMap. didn't get lock: " + pvpUserMapName);
			}

		} catch(Exception e) {
			log.error("failed to initialize pvpUserMap", e);
		} finally {

			if (gotLock) {
				getLocker().unlockHazelcastMap(pvpUserMapName);
			}
		}

	}

	protected void initializePvpUserMap() {
		//in mvn clean test, error was
		//Index can only be added before adding entries!
		//Add indexes first and only once then put entries.
		//so this might be the issue
		if (null != pvpUserMap && pvpUserMap.isEmpty()) {
			//    	log.info("!!!!!!!!!!!!!!!!!!clearing pvpUserMap!!!!!!!!!!!!!!!!!!!");
			//    	pvpUserMap.clear();
			log.info("adding indexes to HazelcastPvpUtil.pvpUserMap");
			addPvpUserIndexes();
		}

		//now we have almost all users, put them into the userIdToPvpUser IMap
		Collection<PvpUser> validUsers = getPvpUserRetrieveUtil().getPvpValidUsers();
		if (null != validUsers && !validUsers.isEmpty()) {
			log.info("populating the IMap with users that can be attacked in pvp. numUsers="
					+ validUsers.size());
			populatePvpUserMap(validUsers);
			useDatabaseInstead = false;
		} else {
			log.error("no available users that can be attacked in pvp.");
			useDatabaseInstead = true;
		}
	}

	protected void addPvpUserIndexes() {
		//when specifying what property the index will be on, look at PvpConstants for the
		//property name

		//the true is for indicating that there will be ranged queries on this property
		pvpUserMap.addIndex(PvpConstants.PVP_USER__ELO, true);
		pvpUserMap.addIndex(PvpConstants.PVP_USER__SHIELD_END_TIME, true);
		pvpUserMap.addIndex(PvpConstants.PVP_USER__IN_BATTLE_END_TIME, true);
	}

	protected void populatePvpUserMap(Collection<PvpUser> validUsers) {
		//go through all the valid users that can be attacked, and store them into
		//the hazelcast distributed map
		for (PvpUser user : validUsers) {
			String userId = user.getUserId();

			pvpUserMap.put(userId, user);
		}
	}


	public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TextFileResourceLoaderAware getTextFileResourceLoaderAware() {
		return textFileResourceLoaderAware;
	}
	public void setTextFileResourceLoaderAware(
			TextFileResourceLoaderAware textFileResourceLoaderAware) {
		this.textFileResourceLoaderAware = textFileResourceLoaderAware;
	}

	public PvpUserRetrieveUtil getPvpUserRetrieveUtil() {
		return pvpUserRetrieveUtil;
	}
	public void setPvpUserRetrieveUtil(
			PvpUserRetrieveUtil pvpUserRetrieveUtil) {
		this.pvpUserRetrieveUtil = pvpUserRetrieveUtil;
	}
	
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	
}