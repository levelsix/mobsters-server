package com.lvl6.server;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IMap;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.PlayerInAction;
import com.lvl6.utils.PlayerSet;

@Component
public class Locker {
	
	private static final int LOCK_TIMEOUT = 10000;
	public static int LOCK_WAIT_SECONDS = 10;


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
		
		//Use this to lock, unlock things
		@javax.annotation.Resource(name = "lockMap")
		IMap<String, Date> lockMap;

		public IMap<String, Date> getLockMap() {
			return lockMap;
		}

		public void setLockMap(IMap<String, Date> lockMap) {
			this.lockMap = lockMap;
		}

		//this is kind of a history of locks, keeping track of which users are locked by
		//which classes
		@javax.annotation.Resource(name = "playersInAction")
		private PlayerSet playersInAction;

		public PlayerSet getPlayersInAction() {
			return playersInAction;
		}

		public void setPlayersInAction(PlayerSet playersInAction) {
			this.playersInAction = playersInAction;
		}
		
	//LOCKING THINGS. ALL COPIED FROM GameServer.java
    //either returns true or throws exception
    public boolean lockPlayer(int playerId, String lockedByClass) {
  		log.info("Locking player {} from class {}", playerId, lockedByClass);
  		// Lock playerLock = hazel.getLock(playersInAction.lockName(playerId));
  		if (lockMap.tryLock(playersInAction.lockName(playerId), LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
  			log.debug("Got lock for player " + playerId);
  			playersInAction.addPlayer(playerId, lockedByClass);
  			return true;
  		} else {
  			log.warn("failed to aquire lock for " + playersInAction.lockName(playerId)+" from class "+lockedByClass);
  			PlayerInAction playa = playersInAction.getPlayerInAction(playerId);
  			if(playa != null) {
  				log.warn("Player {} already locked by class: {}", playerId, playa.getLockedByClass());
  			}
  			throw new RuntimeException("Unable to obtain lock after " + LOCK_WAIT_SECONDS + " seconds");
  		}
  	}

  	public boolean lockPlayers(int playerId1, int playerId2, String lockedByClass) {
  		log.info("Locking players: " + playerId1 + ", " + playerId2);
  		if (playerId1 == playerId2) {
  			return lockPlayer(playerId1, lockedByClass);
  		}
  		if (playerId1 > playerId2) {
  			return lockPlayer(playerId2, lockedByClass) && lockPlayer(playerId1, lockedByClass);
  		} else {
  			return lockPlayer(playerId1, lockedByClass) && lockPlayer(playerId2, lockedByClass);
  		}
  	}
    
  	public void unlockPlayer(int playerId, String fromClass) {
  		log.info("Unlocking player: " + playerId+" from class: "+fromClass);
  		// ILock lock = hazel.getLock(playersInAction.lockName(playerId));
  		try {
  			if (lockMap.isLocked(playersInAction.lockName(playerId))) {
  				//TODO: temporary hack... should revert back to regular unlock once we figure out the issue with 'Thread not owner of lock'
  				lockMap.forceUnlock(playersInAction.lockName(playerId));
  			}
  			log.debug("Unlocked player " + playerId+" from class: "+fromClass);
  			if (playersInAction.containsPlayer(playerId)) {
  				playersInAction.removePlayer(playerId);
  			}
  		} catch (Exception e) {
  			PlayerInAction playa = playersInAction.getPlayerInAction(playerId);
  			if(playa != null) {
  				log.error("Error unlocking player "+playerId+" from class: "+fromClass+". Locked by class: "+ playa.getLockedByClass(), e);
  			}else {
  				log.error("Error unlocking player " + playerId+" from class: "+fromClass, e);
  			}
  		}
  	}

  	public void unlockPlayers(int playerId1, int playerId2, String fromClass) {
  		log.info("Unlocking players: " + playerId1 + ", " + playerId2+" from class: "+fromClass);
  		if (playerId1 > playerId2) {
  			unlockPlayer(playerId2, fromClass);
  			unlockPlayer(playerId1, fromClass);
  		} else {
  			unlockPlayer(playerId1, fromClass);
  			unlockPlayer(playerId2, fromClass);
  		}
  	}
  	
  	public boolean lockClan(int clanId) {
  		log.debug("Locking clan: " + clanId);
  		if (lockMap.tryLock(clanLockName(clanId), LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
  			log.debug("Got lock for clan " + clanId);
  			
  			try {
  				lockMap.put(clanLockName(clanId), new Date());
  			} catch (Exception e) {
  				log.error("locking exception: " + e.getLocalizedMessage() + "\t\t\t" + "\t\t\t" +
  						e.getMessage(), e);
  			}
  			return true;
  		} else {
  			log.warn("failed to aquire lock for " + clanLockName(clanId));
  			return false;
  			// throw new
  			// RuntimeException("Unable to obtain lock after "+LOCK_WAIT_SECONDS+" seconds");
  		}
  	}

  	public void unlockClan(int clanId) {
  		log.debug("Unlocking clan: " + clanId);
  		try {
  			String clanLockName = clanLockName(clanId);
  			if (lockMap.isLocked(clanLockName)) {
  				lockMap.unlock(clanLockName);
  			}
  			log.debug("Unlocked clan " + clanId);
  			if (lockMap.containsKey(clanLockName)) {
  				lockMap.remove(clanLockName);
  			}
  		} catch (Exception e) {
  			log.error("Error unlocking clan " + clanId, e);
  		}
  	}

  	protected String clanLockName(int clanId) {
  		return "ClanLock: " + clanId;
  	}

  	// TODO: refactor this into a lockmap wrapper class and make it work for any
  	// lock
  	// also consider refactoring playerLocks to use it
  	@Scheduled(fixedDelay = LOCK_TIMEOUT)
  	public void clearOldLocks() {
  		long now = new Date().getTime();
  		log.debug("Removing stale clan locks");
  		for (String key : lockMap.keySet()) {
  			try {
  				if (key != null && key.contains("ClanLock")) {
  					long lockTime = lockMap.get(key).getTime();
  					if (now - lockTime > LOCK_TIMEOUT) {
  						if (lockMap.isLocked(key)) {
  							lockMap.forceUnlock(key);
  						}
  						lockMap.remove(key);
  						log.info("Automatically removing timed out lock: " + key);
  					}
  				} else {
  				}
  			} catch (Exception e) {
  				log.error("Error removing stale lock for clan " + key, e);
  			}
  		}
  	}
}
