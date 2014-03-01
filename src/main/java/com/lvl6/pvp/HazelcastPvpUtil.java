package com.lvl6.pvp;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.lvl6.scriptsjava.generatefakeusers.NameGenerator;
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
		
		//need to put this in spring-hazelcast.xml
		//distributed map that is seen across all our servers
		@javax.annotation.Resource(name = "offlinePvpUserMap")
		protected IMap<String, OfflinePvpUser> offlinePvpUserMap;

		public IMap<String, OfflinePvpUser> getOfflinePvpUserMap() {
			return offlinePvpUserMap;
		}

		public void setOfflinePvpUserMap(IMap<String, OfflinePvpUser> offlinePvpUserMap) {
			this.offlinePvpUserMap = offlinePvpUserMap;
		}
		
		
		//Just used to load a .txt file
		@Autowired
		protected TextFileResourceLoaderAware textFileResourceLoaderAware;
		
		//Used to create a distributed map. Distributed map is seen across all our servers
		@Autowired
		protected HazelcastInstance hazel;
		
		//Used to get offline people that can be attacked in pvp
		@Autowired
		protected OfflinePvpUserRetrieveUtils offlinePvpUserRetrieveUtils;
		
		//properties used to create random names, related: textFileResourceLoaderAware
	  private static int syllablesInName1 = 2;
	  private static int syllablesInName2 = 3;
	  private static int numRandomNames = 2000;
	  private static Random rand;
	  private List<String> randomNames; //should this be a distributed collection?
	  private static final String FILE_OF_RANDOM_NAMES = "classpath:namerulesElven.txt";
    
    //TODO: move to OfflinePvpUserRetrieveUtils
    //METHOD TO ACTUALLY USE IMAP, distributed map
    public Set<OfflinePvpUser> retrieveOfflinePvpUsers(int minElo, int maxElo, 
    		Date now) {
    	log.info("querying for people to attack. shieldEndTime should be before now=" +
    		now + "\t elo should be between minElo=" + minElo + ", maxElo=" + maxElo);
    	
    	String inBattleShieldEndTime = PvpConstants.OFFLINE_PVP_USER__IN_BATTLE_END_TIME;
    	String shieldEndTime = PvpConstants.OFFLINE_PVP_USER__SHIELD_END_TIME;
    	String elo = PvpConstants.OFFLINE_PVP_USER__ELO;
    	EntryObject e = new PredicateBuilder().getEntryObject();
    	
    	//the <?, ?> is to prevent a warning from showing up in Eclipse...lol
    	Predicate<?, ?> predicate = e.get(shieldEndTime).lessThan(now)
    			.and(e.get(inBattleShieldEndTime).lessThan(now))
    			.and(e.get(elo).between(minElo, maxElo));
    	
    	Set<OfflinePvpUser> users = (Set<OfflinePvpUser>) offlinePvpUserMap.values(predicate);
    	log.info("users:" + users);
    	
    	return users;
    }
    
    
    //METHODS TO GET AND SET AN OFFLINEPVPUSER, WHICH ALL SERVERS WILL SEE
    public OfflinePvpUser getOfflinePvpUser(int userId) {
    	String userIdStr = String.valueOf(userId);
    	if (playersByPlayerId.containsKey(Integer.parseInt(userIdStr))) {
    		log.info("OfflinePvpUser is online, in ConnectedPlayers map. id=" + userIdStr);
    	}

    	if (!offlinePvpUserMap.containsKey(userIdStr)) {
    		if (0 != userId) {
    			log.warn("trying to access nonexistent OfflinePvpUser with id: " + userIdStr +
    					" PROBABLY because the user is online");
    		}
    		return null;
    	} else {
    		return offlinePvpUserMap.get(userIdStr);
    	}
    }

    public void addOfflinePvpUser(OfflinePvpUser userOpu) {
    	String userIdStr = userOpu.getUserId();
    	offlinePvpUserMap.put(userIdStr, userOpu);
    	
    }
    
    //if the OfflinePvpUser doesn't exist, then don't save it because
    //the OfflinePvpUser is probably online now. Otherwise, store the user.
    public void updateOfflinePvpUser(OfflinePvpUser enemy) {
    	String userIdStr = enemy.getUserId();

    	if (!offlinePvpUserMap.containsKey(userIdStr)) {
    		log.warn("trying to update nonexistent OfflinePvpUser with id: " + userIdStr +
    				" PROBABLY because the user is online, so not saving");
    	} else {
    		OfflinePvpUser existing = offlinePvpUserMap.get(userIdStr);
    		offlinePvpUserMap.put(userIdStr, enemy);
    		log.info("updated offlinePvpUser: " + userIdStr + "\t old=" + existing +
    				"\t new=" + enemy);
    	}

    	if (playersByPlayerId.containsKey(Integer.parseInt(userIdStr))) {
    		log.info("OfflinePvpUser is online, in ConnectedPlayers map. id=" + userIdStr);
    	}
    }
    
    public void removeOfflinePvpUser(int userId) {
    	String userIdStr = String.valueOf(userId);
    	
    	if (offlinePvpUserMap.containsKey(userIdStr)) {
    		log.info("removing userId from available pvp enemies. userId=" + userId);
    	}
    	offlinePvpUserMap.remove(userIdStr);
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
    	setupOfflinePvpUserMap();
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

    protected void setupOfflinePvpUserMap() {

    	//in mvn clean test, error was
      //Index can only be added before adding entries! Add indexes first and only once then put entries.
      //so this might be the issue
    	if (null != offlinePvpUserMap && offlinePvpUserMap.isEmpty()) {
    		//    	log.info("!!!!!!!!!!!!!!!!!!!!!!!clearing offlinePvpUserMap!!!!!!!!!!!!!!!!!!!!!!!");
    		//    	offlinePvpUserMap.clear();
    		log.info("adding indexes to HazelcastPvpUtil.offlinePvpUserMap");
    		addOfflinePvpUserIndexes();
    	}
    	
    	//now we have almost all offline users, put them into the userIdToOfflinePvpUser IMap
    	Collection<OfflinePvpUser> validUsers = getOfflinePvpUserRetrieveUtils().getPvpValidUsers();
    	if (null != validUsers && !validUsers.isEmpty()) {
    		log.info("populating the IMap with users that can be attacked in pvp. numUsers="
    				+ validUsers.size());
    		populateOfflinePvpUserMap(validUsers);
    	} else {
    		log.error("no available users that can be attacked in pvp.");
    	}
    	
    }

    protected void addOfflinePvpUserIndexes() {
    	//when specifying what property the index will be on, look at PvpConstants for the
    	//property name
    	
    	//the true is for indicating that there will be ranged queries on this property
    	offlinePvpUserMap.addIndex(PvpConstants.OFFLINE_PVP_USER__ELO, true);
    	offlinePvpUserMap.addIndex(PvpConstants.OFFLINE_PVP_USER__SHIELD_END_TIME, true);
    	offlinePvpUserMap.addIndex(PvpConstants.OFFLINE_PVP_USER__IN_BATTLE_END_TIME, true);
    }
    
    protected void populateOfflinePvpUserMap(Collection<OfflinePvpUser> validUsers) {
    	//go through all the valid users that can be attacked, and store them into
    	//the hazelcast distributed map
    	for (OfflinePvpUser user : validUsers) {
    		String userId = user.getUserId();
    		
    		offlinePvpUserMap.put(userId, user);
    	}
    }
    
    
    
    
    public HazelcastInstance getHazel() {
    	return hazel;
    }
    
    public void setHazel(HazelcastInstance hazel) {
    	this.hazel = hazel;
    }

    public TextFileResourceLoaderAware getTextFileResourceLoaderAware() {
			return textFileResourceLoaderAware;
		}

		public void setTextFileResourceLoaderAware(
				TextFileResourceLoaderAware textFileResourceLoaderAware) {
			this.textFileResourceLoaderAware = textFileResourceLoaderAware;
		}

		public OfflinePvpUserRetrieveUtils getOfflinePvpUserRetrieveUtils() {
			return offlinePvpUserRetrieveUtils;
		}

		public void setOfflinePvpUserRetrieveUtils(
				OfflinePvpUserRetrieveUtils offlinePvpUserRetrieveUtils) {
			this.offlinePvpUserRetrieveUtils = offlinePvpUserRetrieveUtils;
		}

}