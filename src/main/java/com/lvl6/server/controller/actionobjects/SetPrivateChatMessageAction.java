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
import com.lvl6.retrieveutils.PrivateChatPostRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetPrivateChatMessageAction implements StartUpAction
{

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final int userId;
	
	public SetPrivateChatMessageAction(
		StartupResponseProto.Builder resBuilder, User user, int userId
		)
	{
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
	}
	
	private Set<Integer> userIds;
	boolean isRecipient;
	private Map<Integer, PrivateChatPost> postsUserReceived;
	private Map<Integer, PrivateChatPost> postsUserSent;
	private Map<Integer, PrivateChatPost> postIdsToPrivateChatPosts;
	private Map<Integer, Integer> userIdsToPrivateChatPostIds;
	
	//vars first used in execute()
	private Map<Integer, Set<Integer>> clanIdsToUserIdSet;
	private List<Integer> clanlessUserUserIds;
	private List<Integer> privateChatPostIds;	
	
	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe)
	{
		isRecipient = true;
		
		//get all the most recent posts sent to this user
		postsUserReceived =  PrivateChatPostRetrieveUtils
			.getMostRecentPrivateChatPostsByOrToUser(
				userId, isRecipient, ControllerConstants.STARTUP__MAX_PRIVATE_CHAT_POSTS_RECEIVED);

		//get all the most recent posts this user sent
		isRecipient = false;
		postsUserSent =  PrivateChatPostRetrieveUtils
			.getMostRecentPrivateChatPostsByOrToUser(
				userId, isRecipient, ControllerConstants.STARTUP__MAX_PRIVATE_CHAT_POSTS_SENT);

		if ((null == postsUserReceived || postsUserReceived.isEmpty()) &&
			(null == postsUserSent || postsUserSent.isEmpty()) ) {
			log.info(String.format(
				"user has no private chats. aUser=%s", user));
			return;
		}

		postIdsToPrivateChatPosts = new HashMap<Integer, PrivateChatPost>();
		//link other users with private chat posts and combine all the posts
		//linking is done to select only the latest post between the duple (userId, otherUserId)
		aggregateOtherUserIdsAndPrivateChatPost();

		if (null != userIdsToPrivateChatPostIds && !userIdsToPrivateChatPostIds.isEmpty()) {
			//retrieve all users
			userIds = new HashSet<Integer>();
			userIds.addAll(userIdsToPrivateChatPostIds.keySet());
			userIds.add(userId); //userIdsToPrivateChatPostIds contains userIds other than 'this' userId
			
		} else {
			//user did not send any nor received any private chat posts
			log.error(String.format(
				"aggregating PrivateChatPost ids returned nothing, noob user? user=", user));
		}
		
		fillMe.addUserId(userIds);
		//execute is next();
	}
	
	private void aggregateOtherUserIdsAndPrivateChatPost() {
		userIdsToPrivateChatPostIds = new HashMap<Integer, Integer>();

		//go through the posts the specific user received
		if (null != postsUserReceived && !postsUserReceived.isEmpty()) {
			processPostsReceived();
		}

		if (null != postsUserSent && !postsUserSent.isEmpty()) {
			processPostsSent();
		}
	}

	private void processPostsReceived()
	{
		for (int pcpId : postsUserReceived.keySet()) {
			PrivateChatPost postUserReceived = postsUserReceived.get(pcpId);
			int senderId = postUserReceived.getPosterId();

			//record that the other user and specific user chatted
			userIdsToPrivateChatPostIds.put(senderId, pcpId);
		}
		//combine all the posts together
		postIdsToPrivateChatPosts.putAll(postsUserReceived);
	}
	
	private void processPostsSent()
	{
		//go through the posts user sent
		for (int pcpId: postsUserSent.keySet()) {
			PrivateChatPost postUserSent = postsUserSent.get(pcpId);
			int recipientId = postUserSent.getRecipientId();
			
			//determine the latest post between other recipientId and specific user
			if (!userIdsToPrivateChatPostIds.containsKey(recipientId)) {
				//didn't see this user id yet, record it
				userIdsToPrivateChatPostIds.put(recipientId, pcpId);
				
			} else {
				//recipientId sent something to specific user, choose the latest one
				int postIdUserReceived = userIdsToPrivateChatPostIds.get(recipientId);
				//postsUserReceived can't be null here
				PrivateChatPost postUserReceived = postsUserReceived.get(postIdUserReceived);
				
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
	public void execute( StartUpResource useMe )
	{
		if (userIds.isEmpty()) {
			log.error(String.format(
				"user never private msged. postsUserReceved=%s, postsUserSent=%s, aUser=%s",
				postsUserReceived, postsUserSent, user));
			return;
		}
		Map<Integer, User> userIdsToUsers = useMe.getUserIdsToUsers(userIds);
		
		//get all the clans for the users (a map: clanId->set(userId))
		//put the clanless users in the second argument: clanlessUserUserIds
		determineClanIdsToUserIdSet(userIdsToUsers);
		Map<Integer, Clan> clanIdsToClans = useMe.getClanIdsToClans();
		
		//create the protoList
		privateChatPostIds = new ArrayList<Integer>();
		privateChatPostIds.addAll(userIdsToPrivateChatPostIds.values());
		List<PrivateChatPostProto> pcppList = CreateInfoProtoUtils.createPrivateChatPostProtoList(
			clanIdsToClans, clanIdsToUserIdSet, userIdsToUsers, clanlessUserUserIds, privateChatPostIds,
			postIdsToPrivateChatPosts);

		resBuilder.addAllPcpp(pcppList);
	}

	private void determineClanIdsToUserIdSet(
		Map<Integer, User> userIdsToUsers
		)
	{
		clanIdsToUserIdSet = new HashMap<Integer, Set<Integer>>();
		clanlessUserUserIds = new ArrayList<Integer>();
		//go through users and lump them by clan id
		for (int userId : userIdsToUsers.keySet()) {
			User u = userIdsToUsers.get(userId);
			int clanId = u.getClanId();
			if (ControllerConstants.NOT_SET == clanId) {
				clanlessUserUserIds.add(userId);
				continue;	      
			}

			if (clanIdsToUserIdSet.containsKey(clanId)) {
				//clan id exists, add userId in with others
				Set<Integer> userIdSet = clanIdsToUserIdSet.get(clanId);
				userIdSet.add(userId);
			} else {
				//clan id doesn't exist, create new grouping of userIds
				Set<Integer> userIdSet = new HashSet<Integer>();
				userIdSet.add(userId);

				clanIdsToUserIdSet.put(clanId, userIdSet);
			}
		}
	}
}
