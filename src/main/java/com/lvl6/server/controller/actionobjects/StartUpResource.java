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
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;

//purpose of this class: limit number of calls to user table and clan table
public class StartUpResource
{
	private Set<String> userIds;
	private Set<String> facebookIds;
	private UserRetrieveUtils2 ur;
	ClanRetrieveUtils2 clanRetrieveUtil;

	public StartUpResource(UserRetrieveUtils2 ur,
		ClanRetrieveUtils2 clanRetrieveUtil)
	{
		userIds = new HashSet<String>();
		facebookIds = new HashSet<String>();
		this.ur = ur;
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	//derived data
	private Map<String, User> userIdsToUsers;
	private Set<String> clanIds;
	private Map<String, Clan> clanIdsToClans;

	public void addUserId(String nuUserId) {
		userIds.add(nuUserId);
	}

	public void addUserId(Collection<String> nuUserIds) {
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

		clanIds = new HashSet<String>();
		for (User u : userIdsToUsers.values()) {
			String clanId = u.getClanId();
			clanIds.add(clanId);
		}

		if (!clanIds.isEmpty()) {
			clanIdsToClans = clanRetrieveUtil.getClansByIds(clanIds);
		} else {
			clanIdsToClans = new HashMap<String, Clan>();
		}
	}

	public void fetchUsersOnly()
	{
		if (!userIds.isEmpty() && facebookIds.isEmpty()) {
			userIdsToUsers = ur.getUsersByIds(userIds);

		} else if (!userIds.isEmpty() || !facebookIds.isEmpty()) {
			userIdsToUsers = ur.getUsersForFacebookIdsOrUserIds(
				new ArrayList<String>(facebookIds),
				new ArrayList<String>(userIds));

		}

		if (null == userIdsToUsers) {
			userIdsToUsers = new HashMap<String, User>();
		}
	}

	public Map<String, User> getUserIdsToUsers() {
		ImmutableMap<String, User> iMap =
			new Builder<String, User>()
			.putAll(userIdsToUsers)
			.build();
		return iMap;
	}

	public Map<String, User> getUserIdsToUsers(Collection<String> userIds) {
		Map<String, User> userIdsToUsersTemp = new HashMap<String, User>();

		if (null != userIds && !userIds.isEmpty()) {
			for (String userId : userIds) {
				if (!userIdsToUsers.containsKey(userId)) {
					continue;
				}
				userIdsToUsersTemp.put(
					userId,
					userIdsToUsers.get(userId));
			}
		}

		ImmutableMap<String, User> iMap =
			new Builder<String, User>()
			.putAll(userIdsToUsersTemp)
			.build();
		return iMap;
	}


	public Map<String, Clan> getClanIdsToClans() {
		if (null == clanIdsToClans) {
			clanIdsToClans = new HashMap<String, Clan>();
		}

		ImmutableMap<String, Clan> iMap =
			new Builder<String, Clan>()
			.putAll(clanIdsToClans)
			.build();
		return iMap;
	}

	public Map<String, Clan> getClanIdsToClans(Collection<String> clanIds) {
		if (null == clanIdsToClans) {
			clanIdsToClans = new HashMap<String, Clan>();
		}

		Map<String, Clan> clanIdsToClansTemp = new HashMap<String, Clan>();
		for (String clanId : clanIds) {
			if (!clanIdsToClans.containsKey(clanId)) {
				continue;
			}
			clanIdsToClansTemp.put(
				clanId,
				clanIdsToClans.get(clanId));
		}

		ImmutableMap<String, Clan> iMap =
			new Builder<String, Clan>()
			.putAll(clanIdsToClansTemp)
			.build();
		return iMap;
	}

	public void addClan(String clanId, Clan c) {
		if (null == clanIdsToClans) {
			clanIdsToClans = new HashMap<String, Clan>();
		}
		clanIdsToClans.put(clanId, c);
	}

	// Map from user id to the clan of that user. Used mostly by CreateInfoProtoUtils
	public Map<String, Clan> getUserIdsToClans(Collection<String> userIds) {
		Map<String, Clan> userIdsToClansTemp = new HashMap<String, Clan>();

		if (null != userIds && !userIds.isEmpty()) {
			for (String userId : userIds) {
				if (!userIdsToUsers.containsKey(userId)) {
					continue;
				}

				User user = userIdsToUsers.get(userId);
				if (user.getClanId() != null) {
					Clan c = clanIdsToClans.get(user.getClanId());
					if (c != null) {
						userIdsToClansTemp.put(
							userId,
							c
							);
					}
				}
			}
		}

		ImmutableMap<String, Clan> iMap =
			new Builder<String, Clan>()
			.putAll(userIdsToClansTemp)
			.build();
		return iMap;
	}
}
