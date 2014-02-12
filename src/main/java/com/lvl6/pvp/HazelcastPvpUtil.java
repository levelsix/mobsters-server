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
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.lvl6.scriptsjava.generatefakeusers.NameGenerator;
import com.lvl6.utils.ConnectedPlayer;

@Component
public class HazelcastPvpUtil implements InitializingBean {
	
	public static int LOCK_WAIT_SECONDS = 10;
	

		private static final Logger log = LoggerFactory.getLogger(HazelcastPvpUtil.class);
		
		@javax.annotation.Resource(name = "playersByPlayerId")
		IMap<Integer, ConnectedPlayer> playersByPlayerId;

		public IMap<Integer, ConnectedPlayer> getPlayersByPlayerId() {
			return playersByPlayerId;
		}

		public void setPlayersByPlayerId(IMap<Integer, ConnectedPlayer> playersByPlayerId) {
			this.playersByPlayerId = playersByPlayerId;
		} 
		
		
		@Autowired
		protected TextFileResourceLoaderAware textFileResourceLoaderAware;
		
		@Autowired
		protected HazelcastInstance hazel;
		
		@Autowired
		protected OfflinePvpUserRetrieveUtils offlinePvpUserRetrieveUtils;
		
		//properties for random names
	  private static int syllablesInName1 = 2;
	  private static int syllablesInName2 = 3;
	  private static int numRandomNames = 2000;
	  private static Random rand;
	  private List<String> randomNames;
	  private static final String FILE_OF_RANDOM_NAMES = "classpath:namerulesElven.txt";
    
	  public static final String OFFLINE_PVP_USER_MAP = "offlinePvpUserMap";
	  public static final String OFFLINE_PVP_LOCK_NAME = "OfflinePvpLock:";
	  

    
    protected IMap<String, OfflinePvpUser> userIdToOfflinePvpUser;
    
    
    //METHOD TO ACTUALLY USE IMAP
    public Set<OfflinePvpUser> retrieveOfflinePvpUsers(int minElo, int maxElo, 
    		Date now) {
    	log.info("querying for people to attack. shieldEndTime should be before now=" +
    		now + "\t elo should be between minElo=" + minElo + ", maxElo=" + maxElo);
    	
    	String inBattleShieldEndTime = PvpConstants.OFFLINE_PVP_USER__IN_BATTLE_SHIELD_END_TIME;
    	String shieldEndTime = PvpConstants.OFFLINE_PVP_USER__SHIELD_END_TIME;
    	String elo = PvpConstants.OFFLINE_PVP_USER__ELO;
    	EntryObject e = new PredicateBuilder().getEntryObject();
    	
    	//the <?, ?> is to prevent a warning from showing up in Eclipse...lol
    	Predicate<?, ?> predicate = e.get(shieldEndTime).lessThan(now)
    			.and(e.get(inBattleShieldEndTime).lessThan(now))
    			.and(e.get(elo).between(minElo, maxElo));
    	
    	Set<OfflinePvpUser> users = (Set<OfflinePvpUser>) userIdToOfflinePvpUser.values(predicate);
    	log.info("users:" + users);
    	
    	return users;
    }
    
    //for fake users
    public String getRandomName() {
    	int len = randomNames.size();
    	int randInt = rand.nextInt(len);

    	return randomNames.get(randInt);
    }
    
    protected String getPvpLockName(int userId) {
    	return OFFLINE_PVP_LOCK_NAME + userId;
    }
    
    public ILock getPvpPlayerLock(int userId) {
    	String lockName = getPvpLockName(userId);
    			
    	return hazel.getLock(lockName);
    }
    
    //just to make an opposite to lockPvpPlayer
    public void unlockPvpPlayer(ILock playerLock) {
    	playerLock.forceUnlock();
    }
    
    public OfflinePvpUser getOfflinePvpUser(int userId) {
    	String userIdStr = String.valueOf(userId);
    	if (!userIdToOfflinePvpUser.containsKey(userIdStr)) {
    		log.warn("trying to access nonexistent OfflinePvpUser with id: " + userIdStr);
    		return null;
    	} else {
    		return userIdToOfflinePvpUser.get(userIdStr);
    	}
    }
    
    //if the OfflinePvpUser doesn't exist, then don't save it because
    //the OfflinePvpUser is probably online now. Otherwise, store the user.
    public void setOfflinePvpUser(OfflinePvpUser enemy) {
    	String userIdStr = enemy.getUserId();
    	
    	if (!userIdToOfflinePvpUser.containsKey(userIdStr)) {
    		log.warn("trying to update nonexistent OfflinePvpUser with id: " + userIdStr +
    				" PROBABLY because the user is online, so not saving");
    	} else {
    		OfflinePvpUser existing = userIdToOfflinePvpUser.get(userIdStr);
    		userIdToOfflinePvpUser.put(userIdStr, enemy);
    		log.info("updated offlinePvpUser: " + userIdStr + "\t old=" + existing +
    				"\t new=" + enemy);
    	}
    	
    	if (playersByPlayerId.containsKey(Integer.parseInt(userIdStr))) {
    		log.info("OfflinePvpUser is online, in ConnectedPlayers map. id=" + userIdStr);
    	}
    	
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
    	//this will create the map if map doesn't exist
    	userIdToOfflinePvpUser = hazel.getMap(OFFLINE_PVP_USER_MAP);
    	
    	addOfflinePvpUserIndexes();
    	
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
    	userIdToOfflinePvpUser.addIndex(PvpConstants.OFFLINE_PVP_USER__ELO, true);
    	userIdToOfflinePvpUser.addIndex(PvpConstants.OFFLINE_PVP_USER__SHIELD_END_TIME, true);
    	userIdToOfflinePvpUser.addIndex(PvpConstants.OFFLINE_PVP_USER__IN_BATTLE_SHIELD_END_TIME, true);
    }
    
    protected void populateOfflinePvpUserMap(Collection<OfflinePvpUser> validUsers) {
    	//go through all the valid users that can be attacked, and store them into
    	//the hazelcast distributed map
    	for (OfflinePvpUser user : validUsers) {
    		String userId = user.getUserId();
    		
    		userIdToOfflinePvpUser.put(userId, user);
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