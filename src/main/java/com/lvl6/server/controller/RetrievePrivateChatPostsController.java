package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrievePrivateChatPostsRequestEvent;
import com.lvl6.events.response.RetrievePrivateChatPostsResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsRequestProto;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsResponseProto;
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsResponseProto.RetrievePrivateChatPostsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.PrivateChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component @DependsOn("gameServer") public class RetrievePrivateChatPostsController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils;
  @Autowired
  protected ClanRetrieveUtils2 clanRetrieveUtils;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;

  public RetrievePrivateChatPostsController() {
    numAllocatedThreads = 5;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new RetrievePrivateChatPostsRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_RETRIEVE_PRIVATE_CHAT_POST_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RetrievePrivateChatPostsRequestProto reqProto = ((RetrievePrivateChatPostsRequestEvent)event).getRetrievePrivateChatPostsRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    String otherUserId = reqProto.getOtherUserUuid();

    RetrievePrivateChatPostsResponseProto.Builder resBuilder = RetrievePrivateChatPostsResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setOtherUserUuid(otherUserId);

    UUID userUuid = null;
    UUID otherUserUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(userId);
      otherUserUuid = UUID.fromString(otherUserId);

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s, otherUserId=%s",
          userId, otherUserId), e);
      invalidUuids = true;
    }

    //UUID checks
    if (invalidUuids) {
      resBuilder.setStatus(RetrievePrivateChatPostsStatus.FAIL);
      RetrievePrivateChatPostsResponseEvent resEvent = new RetrievePrivateChatPostsResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setRetrievePrivateChatPostsResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }

    try {
      resBuilder.setStatus(RetrievePrivateChatPostsStatus.SUCCESS);

      List <PrivateChatPost> recentPrivateChatPosts = getPrivateChatPostRetrieveUtils().getPrivateChatPostsBetweenUsersBeforePostId(
            ControllerConstants.RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP, userId, otherUserId);
   
      if (recentPrivateChatPosts != null) {
        if (recentPrivateChatPosts != null && recentPrivateChatPosts.size() > 0) {
          List <String> userIds = new ArrayList<String>();
          userIds.add(userId);
          userIds.add(otherUserId);
          Map<String, User> usersByIds = null;
          if (userIds.size() > 0) {
            usersByIds = getUserRetrieveUtils().getUsersByIds(userIds);
            
            //for not hitting the db for every private chat post
            Map<String, MinimumUserProtoWithLevel> userIdsToMups =
                generateUserIdsToMupsWithLevel(usersByIds, userId,
                	senderProto, otherUserId);
            
            //convert private chat post to group chat message proto
            for (PrivateChatPost pwp : recentPrivateChatPosts) {
              String posterId = pwp.getPosterId();
              
              long time = pwp.getTimeOfPost().getTime();
              MinimumUserProtoWithLevel user = userIdsToMups.get(posterId);
              String content = pwp.getContent();
              boolean isAdmin = false;
              
              GroupChatMessageProto gcmp = 
                  CreateInfoProtoUtils.createGroupChatMessageProto(time, user, content, isAdmin, pwp.getId());
              resBuilder.addPosts(gcmp);
            }
          }
        }
      } else {
        log.info("No private chat posts found for userId=" + userId + " and otherUserId=" + otherUserId); 
      }

      RetrievePrivateChatPostsResponseProto resProto = resBuilder.build();

      RetrievePrivateChatPostsResponseEvent resEvent = new RetrievePrivateChatPostsResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setRetrievePrivateChatPostsResponseProto(resProto);

      server.writeEvent(resEvent);
    } catch (Exception e) {
      log.error("exception in RetrievePrivateChatPostsController processEvent", e);
    //don't let the client hang
      try {
    	  resBuilder.setStatus(RetrievePrivateChatPostsStatus.FAIL);
    	  RetrievePrivateChatPostsResponseEvent resEvent = new RetrievePrivateChatPostsResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setRetrievePrivateChatPostsResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in RetrievePrivateChatPostsController processEvent", e);
      }
    }

  }
  
  private Map<String, MinimumUserProtoWithLevel> generateUserIdsToMupsWithLevel(Map<String, User> usersByIds,
      String userId, MinimumUserProto userMup, String otherUserId) {
    Map<String, MinimumUserProtoWithLevel> userIdsToMups = new HashMap<String, MinimumUserProtoWithLevel>();
    
    User aUser = usersByIds.get(userId);
    User otherUser = usersByIds.get(otherUserId);
    
    MinimumUserProtoWithLevel mup1 = CreateInfoProtoUtils.createMinimumUserProtoWithLevel(
    	aUser, null, userMup);
    userIdsToMups.put(userId, mup1);
    
    Clan otherUserClan = null;
    if (otherUser.getClanId() != null) {
    	otherUserClan = getClanRetrieveUtils().getClanWithId(otherUser.getClanId());
    }
    
    MinimumUserProtoWithLevel mup2 = CreateInfoProtoUtils.createMinimumUserProtoWithLevel(
    	otherUser, otherUserClan, null);
    userIdsToMups.put(otherUserId, mup2);
    
    return userIdsToMups;
  }

  public PrivateChatPostRetrieveUtils2 getPrivateChatPostRetrieveUtils() {
    return privateChatPostRetrieveUtils;
  }

  public void setPrivateChatPostRetrieveUtils(
      PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils) {
    this.privateChatPostRetrieveUtils = privateChatPostRetrieveUtils;
  }

  public ClanRetrieveUtils2 getClanRetrieveUtils() {
    return clanRetrieveUtils;
  }

  public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
    this.clanRetrieveUtils = clanRetrieveUtils;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }
  
}