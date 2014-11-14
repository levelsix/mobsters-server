package com.lvl6.server.controller.actionobjects;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetClanChatMessageAction implements StartUpAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());


	private final ClanDataProto.Builder cdpBuilder;
	private final User user;
  private final ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils;
	
	public SetClanChatMessageAction(
		ClanDataProto.Builder cdpBuilder, User user, 
		ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils)
	{
		this.cdpBuilder = cdpBuilder;
		this.user = user;
		this.clanChatPostRetrieveUtils = clanChatPostRetrieveUtils;
	}
	
	private Set<String> userIds;
	private String clanId;
	List<ClanChatPost> activeClanChatPosts;
	
	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe)
	{
		userIds = new HashSet<String>();
		clanId = user.getClanId(); 

		if (clanId == null) {
			return;
		}
		
		int limit = ControllerConstants.RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP;
		activeClanChatPosts = clanChatPostRetrieveUtils
			.getMostRecentClanChatPostsForClan(limit , clanId);

		if (null == activeClanChatPosts || activeClanChatPosts.isEmpty()) {
			log.warn(String.format("no activeClanChatPosts for clanId=%s", clanId));
			return;
		}  		
		
		for (ClanChatPost p : activeClanChatPosts) {
			userIds.add(p.getPosterId());
		}
		
		fillMe.addUserId(userIds);
	}

	@Override
	public void execute( StartUpResource useMe )
	{
		if (null == userIds || userIds.isEmpty()) {
			return;
		}
		Map<String, User> userIdsToUsers = useMe.getUserIdsToUsers(userIds);
		if (userIdsToUsers.isEmpty()) {
			return;
		}
		
		Map<String, Clan> clanIdsToClans = useMe.getClanIdsToClans();
		
		for (int i = activeClanChatPosts.size() - 1; i >= 0; i--) {
			ClanChatPost pwp = activeClanChatPosts.get(i);
			String userId = pwp.getPosterId();
			User u = userIdsToUsers.get(userId);
			String clanId = u.getClanId();
			Clan c = null;
			
			if (clanIdsToClans.containsKey(clanId)) {
				c = clanIdsToClans.get(clanId);
			}
			
			GroupChatMessageProto gcmp = CreateInfoProtoUtils
			.createGroupChatMessageProtoFromClanChatPost(pwp, u, c);
			
			cdpBuilder.addClanChats(gcmp);
		}
	}

}
