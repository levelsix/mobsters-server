package com.lvl6.server.events;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClanChangeServerEvent implements Serializable{
	private static final long	serialVersionUID	= 646327533832982586L;
	
	protected Map<String, String> userIdToNewClanIdMap = new HashMap<>();
	
	public void setUserIdToNewClanIdMap(Map<String, String> userIdToNewClanIdMap) {
		this.userIdToNewClanIdMap = userIdToNewClanIdMap;
	}

	public Map<String, String> getUserIdToNewClanIdMap() {
		return userIdToNewClanIdMap;
	}

	public void add(String userId, String newClanId) {
		userIdToNewClanIdMap.put(userId, newClanId);
	}
}
