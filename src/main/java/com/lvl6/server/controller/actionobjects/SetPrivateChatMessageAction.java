package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lvl6.info.Clan;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.retrieveutils.PrivateChatPostRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetPrivateChatMessageAction implements StartUpAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final String userId;
	private final PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils;

	public SetPrivateChatMessageAction(StartupResponseProto.Builder resBuilder,
			User user, String userId,
			PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils) {
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.privateChatPostRetrieveUtils = privateChatPostRetrieveUtils;
	}

	private Set<String> userIds;
	private Map<String, PrivateChatPost> postsUserReceived;
	private Map<String, PrivateChatPost> postsUserSent;
	private Map<String, PrivateChatPost> postIdsToPrivateChatPosts;
	private Map<String, String> userIdsToPrivateChatPostIds;

	//vars first used in execute()
	private Map<String, Set<String>> clanIdsToUserIdSet;
	private List<String> clanlessUserUserIds;
	private List<String> privateChatPostIds;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		boolean isRecipient = true;

		//get all the most recent posts sent to this user
		postsUserReceived = privateChatPostRetrieveUtils
				.getMostRecentPrivateChatPostsByOrToUser(
						userId,
						isRecipient,
						ControllerConstants.STARTUP__MAX_PRIVATE_CHAT_POSTS_RECEIVED);

		//get all the most recent posts this user sent
		isRecipient = false;
		postsUserSent = privateChatPostRetrieveUtils
				.getMostRecentPrivateChatPostsByOrToUser(
						userId,
						isRecipient,
						ControllerConstants.STARTUP__MAX_PRIVATE_CHAT_POSTS_SENT);

		userIds = new HashSet<String>();

		if ((null == postsUserReceived || postsUserReceived.isEmpty())
				&& (null == postsUserSent || postsUserSent.isEmpty())) {
			log.info(String.format("user has no private chats. aUser=%s", user));
			return;
		}

		postIdsToPrivateChatPosts = new HashMap<String, PrivateChatPost>();
		//link other users with private chat posts and combine all the posts
		//linking is done to select only the latest post between the duple (userId, otherUserId)
		aggregateOtherUserIdsAndPrivateChatPost();

		if (null != userIdsToPrivateChatPostIds
				&& !userIdsToPrivateChatPostIds.isEmpty()) {
			//retrieve all users
			userIds.addAll(userIdsToPrivateChatPostIds.keySet());
			userIds.add(userId); //userIdsToPrivateChatPostIds contains userIds other than 'this' userId

		} else {
			//user did not send any nor received any private chat posts
			log.error(String
					.format("aggregating PrivateChatPost ids returned nothing, noob user? user=",
							user));
		}

		fillMe.addUserId(userIds);
		//execute is next();
	}

	private void aggregateOtherUserIdsAndPrivateChatPost() {
		userIdsToPrivateChatPostIds = new HashMap<String, String>();

		//go through the posts the specific user received
		if (null != postsUserReceived && !postsUserReceived.isEmpty()) {
			processPostsReceived();
		}

		if (null != postsUserSent && !postsUserSent.isEmpty()) {
			processPostsSent();
		}
	}

	private void processPostsReceived() {
		for (String pcpId : postsUserReceived.keySet()) {
			PrivateChatPost postUserReceived = postsUserReceived.get(pcpId);
			String senderId = postUserReceived.getPosterId();

			//record that the other user and specific user chatted
			userIdsToPrivateChatPostIds.put(senderId, pcpId);
		}
		//combine all the posts together
		postIdsToPrivateChatPosts.putAll(postsUserReceived);
	}

	private void processPostsSent() {
		//go through the posts user sent
		for (String pcpId : postsUserSent.keySet()) {
			PrivateChatPost postUserSent = postsUserSent.get(pcpId);
			String recipientId = postUserSent.getRecipientId();

			//determine the latest post between other recipientId and specific user
			if (!userIdsToPrivateChatPostIds.containsKey(recipientId)) {
				//didn't see this user id yet, record it
				userIdsToPrivateChatPostIds.put(recipientId, pcpId);

			} else {
				//recipientId sent something to specific user, choose the latest one
				String postIdUserReceived = userIdsToPrivateChatPostIds
						.get(recipientId);
				//postsUserReceived can't be null here
				PrivateChatPost postUserReceived = postsUserReceived
						.get(postIdUserReceived);

				Date newDate = postUserSent.getTimeOfPost();
				Date existingDate = postUserReceived.getTimeOfPost();
				if (newDate.getTime() > existingDate.getTime()) {
					//since postUserSent's time is later, choose this post for recipientId
					userIdsToPrivateChatPostIds.put(recipientId, pcpId);
				}
			}
		}

		//combine all the posts together
		postIdsToPrivateChatPosts.putAll(postsUserSent);
	}

	@Override
	public void execute(StartUpResource useMe) {
		if (null == userIds || userIds.isEmpty()) {
			//			log.error(String.format(
			//				"user never private msged. postsUserReceved=%s, postsUserSent=%s, aUser=%s",
			//				postsUserReceived, postsUserSent, user));
			return;
		}
		Map<String, User> userIdsToUsers = useMe.getUserIdsToUsers(userIds);

		//get all the clans for the users (a map: clanId->set(userId))
		//put the clanless users in the second argument: clanlessUserUserIds
		determineClanIdsToUserIdSet(userIdsToUsers);

		//need to get ONLY the clans this object has seen
		Map<String, Clan> clanIdsToClans = useMe
				.getClanIdsToClans(clanIdsToUserIdSet.keySet());

		//		log.info(String.format(
		//			"clanIdsToClans=%s, clanIdsToUserIdSet=%s, userIdsToUsers=%s, clanlessUserUserIds=%s, privateChatPostIds=%s, postIdsToPrivateChatPosts=%s",
		//			clanIdsToClans, clanIdsToUserIdSet, userIdsToUsers, clanlessUserUserIds, privateChatPostIds,
		//			postIdsToPrivateChatPosts));

		//create the protoList
		privateChatPostIds = new ArrayList<String>();
		privateChatPostIds.addAll(userIdsToPrivateChatPostIds.values());
		List<PrivateChatPostProto> pcppList = CreateInfoProtoUtils
				.createPrivateChatPostProtoList(clanIdsToClans,
						clanIdsToUserIdSet, userIdsToUsers,
						clanlessUserUserIds, privateChatPostIds,
						postIdsToPrivateChatPosts);

		resBuilder.addAllPcpp(pcppList);
	}

	private void determineClanIdsToUserIdSet(Map<String, User> userIdsToUsers) {
		clanIdsToUserIdSet = new HashMap<String, Set<String>>();
		clanlessUserUserIds = new ArrayList<String>();
		//go through users and lump them by clan id
		for (String userId : userIdsToUsers.keySet()) {
			User u = userIdsToUsers.get(userId);
			String clanId = u.getClanId();
			if (null == clanId) {
				clanlessUserUserIds.add(userId);
				continue;
			}

			if (clanIdsToUserIdSet.containsKey(clanId)) {
				//clan id exists, add userId in with others
				Set<String> userIdSet = clanIdsToUserIdSet.get(clanId);
				userIdSet.add(userId);
			} else {
				//clan id doesn't exist, create new grouping of userIds
				Set<String> userIdSet = new HashSet<String>();
				userIdSet.add(userId);

				clanIdsToUserIdSet.put(clanId, userIdSet);
			}
		}
	}

}
