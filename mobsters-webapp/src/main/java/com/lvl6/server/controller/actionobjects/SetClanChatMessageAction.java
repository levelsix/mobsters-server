package com.lvl6.server.controller.actionobjects;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;


@Component@Scope("prototype")public class SetClanChatMessageAction implements StartUpAction {

	private static final Logger log = LoggerFactory.getLogger(SetClanChatMessageAction.class);

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils;
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils;
	
	protected ClanDataProto.Builder cdpBuilder;
	protected User user;

	public void wire(ClanDataProto.Builder cdpBuilder,User user) {
		this.cdpBuilder = cdpBuilder;
		this.user = user;
	}

	//derived state
	private Set<String> userIds;
	private String clanId;
	private List<ClanChatPost> activeClanChatPosts;
	private Date lastClanChatPostTime = ControllerConstants.INCEPTION_DATE;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		userIds = new HashSet<String>();
		clanId = user.getClanId();

		if (clanId == null) {
			return;
		}

		int limit = ControllerConstants.RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP;
		activeClanChatPosts = clanChatPostRetrieveUtils
				.getMostRecentClanChatPostsForClan(limit, clanId);

		if (null == activeClanChatPosts || activeClanChatPosts.isEmpty()) {
			log.warn(String.format("no activeClanChatPosts for clanId=%s",
					clanId));
			return;
		}

		for (ClanChatPost p : activeClanChatPosts) {
			userIds.add(p.getPosterId());
		}

		fillMe.addUserId(userIds);
	}

	@Override
	public void execute(StartUpResource useMe) {
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

			GroupChatMessageProto gcmp = createInfoProtoUtils
					.createGroupChatMessageProtoFromClanChatPost(pwp, u, c);

			cdpBuilder.addClanChats(gcmp);

			//find the last clan chat time
			Date timeOfPost = pwp.getTimeOfPost();
			if (timeOfPost.compareTo(lastClanChatPostTime) > 0) {
				lastClanChatPostTime = timeOfPost;
			}
		}
	}

	public Date getLastClanChatPostTime() {
		return lastClanChatPostTime;
	}

}
