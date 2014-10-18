package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IList;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetGroupChatMessageAction implements StartUpAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final class GroupChatComparator implements Comparator<GroupChatMessageProto> {
		@Override
		public int compare(GroupChatMessageProto o1, GroupChatMessageProto o2) {
			if (o1.getTimeOfChat() < o2.getTimeOfChat()) {
				return -1;
			} else if (o1.getTimeOfChat() > o2.getTimeOfChat()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final IList<GroupChatMessageProto> chatMessages;
	
	public SetGroupChatMessageAction(
		StartupResponseProto.Builder resBuilder, User user,
		IList<GroupChatMessageProto> chatMessages
		)
	{
		this.resBuilder = resBuilder;
		this.user = user;
		this.chatMessages = chatMessages;
	}
	
	private Set<Integer> userIds;
	private int clanId;
	List<ClanChatPost> activeClanChatPosts;
	
	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe)
	{
		
		Iterator<GroupChatMessageProto> it = chatMessages.iterator();
		List<GroupChatMessageProto> globalChats = new ArrayList<GroupChatMessageProto>();
		while (it.hasNext()) {
			globalChats.add(it.next());
		}
		
		Collections.sort(globalChats, new GroupChatComparator());
		// Need to add them in reverse order
		for (int i = 0; i < globalChats.size(); i++) {
			resBuilder.addGlobalChats(globalChats.get(i));
		}
		clanId = user.getClanId(); 

		if (clanId <= 0) {
			return;
		}
		
		int limit = ControllerConstants.RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP;
		activeClanChatPosts = ClanChatPostRetrieveUtils
			.getMostRecentClanChatPostsForClan(limit , clanId);

		if (null == activeClanChatPosts || activeClanChatPosts.isEmpty()) {
			return;
		}  		
		
		userIds = new HashSet<Integer>();
		for (ClanChatPost p : activeClanChatPosts) {
			userIds.add(p.getPosterId());
		}
		
		fillMe.addUserId(userIds);
	}

	@Override
	public void execute( StartUpResource useMe )
	{
		Map<Integer, User> userIdsToUsers = useMe.getUserIdsToUsers(userIds);
		if (userIdsToUsers.isEmpty()) {
			return;
		}
		
		Map<Integer, Clan> clanIdsToClans = useMe.getClanIdsToClans();
		
		for (int i = activeClanChatPosts.size() - 1; i >= 0; i--) {
			ClanChatPost pwp = activeClanChatPosts.get(i);
			int userId = pwp.getPosterId();
			User u = userIdsToUsers.get(userId);
			int clanId = u.getClanId();
			Clan c = null;
			
			if (clanIdsToClans.containsKey(clanId)) {
				c = clanIdsToClans.get(clanId);
			}
			
			GroupChatMessageProto gcmp = CreateInfoProtoUtils
			.createGroupChatMessageProtoFromClanChatPost(pwp, u, c);
			
			resBuilder.addClanChats(gcmp);
		}
	}

}
