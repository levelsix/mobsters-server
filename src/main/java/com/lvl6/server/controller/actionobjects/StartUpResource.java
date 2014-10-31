package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils;

//purpose of this class: limit number of calls to user table and clan table
public class StartUpResource
{
	private Set<Integer> userIds;
	private Set<String> facebookIds;
	private UserRetrieveUtils ur;
	
	public StartUpResource(UserRetrieveUtils ur) {
		userIds = new HashSet<Integer>();
		facebookIds = new HashSet<String>();
		this.ur = ur;
	}
	
	//derived data
	private Map<Integer, User> userIdsToUsers;
	private Set<Integer> clanIds;
	private Map<Integer, Clan> clanIdsToClans;
	
	public void addUserId(Integer nuUserId) {
		userIds.add(nuUserId);
	}
	
	public void addUserId(Collection<Integer> nuUserIds) {
		userIds.addAll(nuUserIds);
	}
	
	public void addFacebookId(String fbId) {
		facebookIds.add(fbId);
	}
	public void addFacebookId(Collection<String> fbIds) {
		facebookIds.addAll(fbIds);
	}
	
	public void fetch() {
		
		fetchUsersOnly();
		
		clanIds = new HashSet<Integer>();
		for (User u : userIdsToUsers.values()) {
			int clanId = u.getClanId();
			clanIds.add(clanId);
		}
		
		if (!clanIds.isEmpty()) {
			clanIdsToClans = ClanRetrieveUtils.getClansByIds(clanIds);
		} else {
			clanIdsToClans = new HashMap<Integer, Clan>();
		}
	}

	public void fetchUsersOnly()
	{
		if (!userIds.isEmpty() && facebookIds.isEmpty()) {
			userIdsToUsers = ur.getUsersByIds(userIds);
			
		} else if (!userIds.isEmpty() || !facebookIds.isEmpty()) {
			userIdsToUsers = ur.getUsersForFacebookIdsOrUserIds(
				new ArrayList<String>(facebookIds),
				new ArrayList<Integer>(userIds));
			
		}
		
		if (null == userIdsToUsers) {
			userIdsToUsers = new HashMap<Integer, User>();
		}
	}
	
	public Map<Integer, User> getUserIdsToUsers() {
		ImmutableMap<Integer, User> iMap =
			new Builder<Integer, User>()
			.putAll(userIdsToUsers)
			.build();
		return iMap;
	}
	
	public Map<Integer, User> getUserIdsToUsers(Collection<Integer> userIds) {
		Map<Integer, User> userIdsToUsersTemp = new HashMap<Integer, User>();
		
		if (null != userIds && !userIds.isEmpty()) {
			for (Integer userId : userIds) {
				if (!userIdsToUsers.containsKey(userId)) {
					continue;
				}
				userIdsToUsersTemp.put(
					userId,
					userIdsToUsers.get(userId));
			}
		}
		
		ImmutableMap<Integer, User> iMap =
			new Builder<Integer, User>()
			.putAll(userIdsToUsersTemp)
			.build();
		return iMap;
	}
	

	public Map<Integer, Clan> getClanIdsToClans() {
		if (null == clanIdsToClans) {
			clanIdsToClans = new HashMap<Integer, Clan>();
		}
		
		ImmutableMap<Integer, Clan> iMap =
			new Builder<Integer, Clan>()
			.putAll(clanIdsToClans)
			.build();
		return iMap;
	}
	
	public void addClan(int clanId, Clan c) {
		if (null == clanIdsToClans) {
			clanIdsToClans = new HashMap<Integer, Clan>();
		}
		clanIdsToClans.put(clanId, c);
	}
}
