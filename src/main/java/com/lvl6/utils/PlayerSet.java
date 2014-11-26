package com.lvl6.utils;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;

public class PlayerSet implements HazelcastInstanceAware {

	
	private static final int LOCK_TIMEOUT = 10000;

	org.slf4j.Logger log = LoggerFactory.getLogger(PlayerSet.class);
	
	private IMap<String, PlayerInAction> players;

	public IMap<String, PlayerInAction> getPlayers() {
		return players;
	}

	public void setPlayers(IMap<String, PlayerInAction> players) {
		this.players = players;
	}

	
	@Resource(name="lockMap")
	IMap<String, Date> lockMap;

	public IMap<String, Date> getLockMap() {
		return lockMap;
	}

	public void setLockMap(IMap<String, Date> lockMap) {
		this.lockMap = lockMap;
	}
	
	/**
	 * lock a player
	 * 
	 * @throws InterruptedException
	 */
	public void addPlayer(String playerId, String lockedByClass) {
		players.put(playerId, new PlayerInAction(playerId, lockedByClass));
	}

	public PlayerInAction getPlayerInAction(String playerId) {
		return players.get(playerId);
	}
	
	public void removePlayer(String playerId) {
		if(containsPlayer(playerId))
			players.remove(playerId);
	}

	public boolean containsPlayer(String playerId) {
		return players.containsKey(playerId);
	}
	
	
	public String lockName(String playerId) {
		return "PlayerLock:"+playerId;
	}
	
	@Scheduled(fixedDelay=LOCK_TIMEOUT)
	public void clearOldLocks(){
		long now = new Date().getTime();
		log.debug("Removing stale player locks");
		for(String player:players.keySet()){
			try {
				PlayerInAction play = players.get(player);
				if(play != null && play.getLockTime() != null) {
					if(now - play.getLockTime().getTime() > LOCK_TIMEOUT){
						String lockName = lockName(player);
						if(lockMap.isLocked(lockName)) {
							lockMap.forceUnlock(lockName);
						}
						removePlayer(player);
						log.info("Automatically removing timed out lock for player: "+play.getPlayerId());
					}
				}else {
					log.warn("Player was null when cleaning up locks in PlayerSet: {}", player);
				}
			}catch(Exception e) {
				log.error("Error removing stale lock for player {} {}", player, e.getMessage() );
			}
		}
	}
	
	protected HazelcastInstance hazel;
	@Override
	@Autowired
	public void setHazelcastInstance(HazelcastInstance instance) {
		hazel = instance;
	}


}// EventQueue
