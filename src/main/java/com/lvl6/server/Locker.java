package com.lvl6.server;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IMap;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.PlayerInAction;
import com.lvl6.utils.PlayerSet;

@Component
public class Locker {
	
	private static final int LOCK_TIMEOUT = 10000;
	public static int LOCK_WAIT_SECONDS = 10;


		private static final Logger log = LoggerFactory.getLogger(Locker.class);
		
		//these are the users that are online
		@javax.annotation.Resource(name = "playersByPlayerId")
		IMap<String, ConnectedPlayer> playersByPlayerId;

		public IMap<String, ConnectedPlayer> getPlayersByPlayerId() {
			return playersByPlayerId;
		}
		public void setPlayersByPlayerId(IMap<String, ConnectedPlayer> playersByPlayerId) {
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
		
		
		//LOCKING THINGS. 
		protected String hzMapLockName(String mapToLock) {
			return "HzMapLock: " + mapToLock;
		}
		public boolean lockHazelcastMap(String mapToLock) {
			log.debug("Locking hazelcast map: " + mapToLock);
  		try {
			if (lockMap.tryLock(hzMapLockName(mapToLock), LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
				log.debug("Got lock for hz map " + mapToLock);
				
				try {
					lockMap.put(hzMapLockName(mapToLock), new Date());
				} catch (Exception e) {
					log.error("locking exception: " + e.getLocalizedMessage() + "\t\t\t" + "\t\t\t" +
							e.getMessage(), e);
					return false;
				}
				return true;
			}
		} catch (Exception e) {
			log.warn("1failed to aquire lock for " + hzMapLockName(mapToLock), e);
		}
  		log.warn("2failed to aquire lock for " + hzMapLockName(mapToLock));
  		return false;
  			// throw new
  			// RuntimeException("Unable to obtain lock after "+LOCK_WAIT_SECONDS+" seconds");
		}
		
		public void unlockHazelcastMap(String mapToLock) {
  		log.debug("Unlocking hazelcast map: " + mapToLock);
  		try {
  			String lockName = hzMapLockName(mapToLock);
  			if (lockMap.isLocked(lockName)) {
  				lockMap.unlock(lockName);
  			}
  			log.debug("Unlocked hazelcast map: " + mapToLock);
  			if (lockMap.containsKey(lockName)) {
  				lockMap.remove(lockName);
  			}
  		} catch (Exception e) {
  			log.error("Error unlocking hazelcast map " + mapToLock, e);
  		}
  	}
		
		//ALL COPIED FROM GameServer.java
    //either returns true or throws exception
    public boolean lockPlayer(UUID playerUuid, String lockedByClass) {
    	String playerId = playerUuid.toString();
  		log.info("Locking player {} from class {}", playerId, lockedByClass);
  		// Lock playerLock = hazel.getLock(playersInAction.lockName(playerId));
  		try {
			if (lockMap.tryLock(playersInAction.lockName(playerId), LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
				log.debug("Got lock for player " + playerId);
				playersInAction.addPlayer(playerId, lockedByClass);
				return true;
			}
		} catch (Exception e) {
			log.warn("1Unable to obtain lock after " + LOCK_WAIT_SECONDS + " seconds", e);
		}

  		log.warn("failed to aquire lock for " + playersInAction.lockName(playerId)+" from class "+lockedByClass);
  		PlayerInAction playa = playersInAction.getPlayerInAction(playerId);
  		if(playa != null) {
  			log.warn("Player {} already locked by class: {}", playerId, playa.getLockedByClass());
  		}
  		throw new RuntimeException("2Unable to obtain lock after " + LOCK_WAIT_SECONDS + " seconds");
  	}

  	public boolean lockPlayers(UUID playerId1, UUID playerId2, String lockedByClass) {
  		log.info("Locking players: " + playerId1 + ", " + playerId2);
  		if (playerId1.equals(playerId2)) {
  			return lockPlayer(playerId1, lockedByClass);
  		}
//  		if (playerId1 > playerId2) {
  		if (playerId1.compareTo(playerId2) > 0) {
  			return lockPlayer(playerId2, lockedByClass) && lockPlayer(playerId1, lockedByClass);
  		} else {
  			return lockPlayer(playerId1, lockedByClass) && lockPlayer(playerId2, lockedByClass);
  		}
  	}
    
  	public void unlockPlayer(UUID playerUuid, String fromClass) {
  		String playerId = playerUuid.toString();
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

  	public void unlockPlayers(UUID playerId1, UUID playerId2, String fromClass) {
  		log.info("Unlocking players: " + playerId1 + ", " + playerId2+" from class: "+fromClass);
//  		if (playerId1 > playerId2) {
  		if (playerId1.compareTo(playerId2) > 0) {
  			unlockPlayer(playerId2, fromClass);
  			unlockPlayer(playerId1, fromClass);
  		} else {
  			unlockPlayer(playerId1, fromClass);
  			unlockPlayer(playerId2, fromClass);
  		}
  	}
  	
  	public boolean lockFbId(String fbId) {
  		log.debug("Locking fbId: " + fbId);
  		try {
			if (lockMap.tryLock(fbIdLockName(fbId), LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
				log.debug("Got lock for fbId " + fbId);
				
				try {
					lockMap.put(fbIdLockName(fbId), new Date());
				} catch (Exception e) {
					log.error("locking exception: " + e.getLocalizedMessage() + "\t\t\t" + "\t\t\t" +
							e.getMessage(), e);
					return false;
				}
				return true;
			}
		} catch (Exception e) {
			log.warn("1failed to aquire lock for " + fbIdLockName(fbId), e);
		}

  		log.warn("2failed to aquire lock for " + fbIdLockName(fbId) + " after " +
  				LOCK_WAIT_SECONDS+" seconds");
  		return false;
  		// throw new
  		// RuntimeException("Unable to obtain lock after "+LOCK_WAIT_SECONDS+" seconds");
  	}
  	
  	public void unlockFbId(String fbId) {
  		log.debug("Unlocking fbId: " + fbId);
  		try {
  			String fbIdLockName = fbIdLockName(fbId);
  			if (lockMap.isLocked(fbIdLockName)) {
  				lockMap.unlock(fbIdLockName);
  			}
  			log.debug("Unlocked fbId " + fbId);
  			if (lockMap.containsKey(fbIdLockName)) {
  				lockMap.remove(fbIdLockName);
  			}
  		} catch (Exception e) {
  			log.error("Error unlocking fbId " + fbId, e);
  		}
  	}

  	protected String fbIdLockName(String fbId) {
  		return "FbIdLock: " + fbId;
  	}
  	
  	public boolean lockClan(UUID clanUuid) {
  		String clanId = clanUuid.toString();
  		log.debug("Locking clan: " + clanId);
  		try {
			if (lockMap.tryLock(clanLockName(clanId), LOCK_WAIT_SECONDS, TimeUnit.SECONDS)) {
				log.debug("Got lock for clan " + clanId);
				
				try {
					lockMap.put(clanLockName(clanId), new Date());
				} catch (Exception e) {
					log.error("locking exception: " + e.getLocalizedMessage() + "\t\t\t" + "\t\t\t" +
							e.getMessage(), e);
					return false;
				}
				return true;
			}
		} catch (Exception e) {
			log.warn("1failed to aquire lock for " + clanLockName(clanId), e);
		}

  		log.warn("2failed to aquire lock for " + clanLockName(clanId) + " after " + 
  				LOCK_WAIT_SECONDS+" seconds");
  		return false;
  		// throw new
  		// RuntimeException("Unable to obtain lock after "+LOCK_WAIT_SECONDS+" seconds");
  	}

  	public void unlockClan(UUID clanUuid) {
  		String clanId = clanUuid.toString();
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

  	protected String clanLockName(String clanId) {
  		return "ClanLock: " + clanId;
  	}

  	// TODO: refactor this into a lockmap wrapper class and make it work for any lock
  	// also consider refactoring playerLocks to use it
  	@Scheduled(fixedDelay = LOCK_TIMEOUT)
  	public void clearOldLocks() {
  		long now = new Date().getTime();
  		log.debug("Removing stale clan or facebook id locks");
  		for (String key : lockMap.keySet()) {
  			try {
  				if (key != null && (key.contains("ClanLock") || key.contains("FbIdLock"))) {
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
