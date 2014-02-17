package com.lvl6.server.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IList;
import com.kabam.apiclient.KabamApi;
import com.kabam.apiclient.MobileNaidResponse;
import com.kabam.apiclient.ResponseCode;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.StartupRequestEvent;
import com.lvl6.events.response.StartupResponseEvent;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.EventPersistentForUser;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.PvpBattleForUser;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.Globals;
import com.lvl6.properties.KabamProperties;
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.EventStartupProto.StartupRequestProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.Builder;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupStatus;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.UpdateStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterEvolutionProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.QuestProto.FullUserQuestProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto;
import com.lvl6.proto.TaskProto.UserPersistentEventProto;
import com.lvl6.proto.UserProto.FullUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.OfflinePvpUser;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.EventPersistentForUserRetrieveUtils;
import com.lvl6.retrieveutils.FirstTimeUsersRetrieveUtils;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.LoginHistoryRetrieveUtils;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
import com.lvl6.retrieveutils.PrivateChatPostRetrieveUtils;
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils;
import com.lvl6.scriptsjava.generatefakeusers.NameGeneratorElven;
import com.lvl6.server.GameServer;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
@DependsOn("gameServer")
public class StartupController extends EventController {
//  private static String nameRulesFile = "namerulesElven.txt";
//  private static int syllablesInName1 = 2;
//  private static int syllablesInName2 = 3;

  private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());

  public StartupController() {
    numAllocatedThreads = 3;
  }

  @Autowired
  protected NameGeneratorElven nameGeneratorElven;

  public NameGeneratorElven getNameGeneratorElven() {
    return nameGeneratorElven;
  }

  public void setNameGeneratorElven(NameGeneratorElven nameGeneratorElven) {
    this.nameGeneratorElven = nameGeneratorElven;
  }

  @Resource(name = "goodEquipsRecievedFromBoosterPacks")
  protected IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks;

  public IList<RareBoosterPurchaseProto> getGoodEquipsRecievedFromBoosterPacks() {
    return goodEquipsRecievedFromBoosterPacks;
  }

  public void setGoodEquipsRecievedFromBoosterPacks(
      IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks) {
    this.goodEquipsRecievedFromBoosterPacks = goodEquipsRecievedFromBoosterPacks;
  }

  @Resource(name = "globalChat")
  protected IList<GroupChatMessageProto> chatMessages;

  public IList<GroupChatMessageProto> getChatMessages() {
    return chatMessages;
  }

  public void setChatMessages(IList<GroupChatMessageProto> chatMessages) {
    this.chatMessages = chatMessages;
  }
  
  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

  
  

  @Override
  public RequestEvent createRequestEvent() {
    return new StartupRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_STARTUP_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    StartupRequestProto reqProto = ((StartupRequestEvent) event).getStartupRequestProto();
    log.info("Processing startup request event");
    UpdateStatus updateStatus;
    String udid = reqProto.getUdid();
    String apsalarId = reqProto.hasApsalarId() ? reqProto.getApsalarId() : null;
    String fbId = reqProto.getFbId();
    boolean freshRestart = reqProto.getIsFreshRestart();

    StartupResponseProto.Builder resBuilder = StartupResponseProto.newBuilder();

    MiscMethods.setMDCProperties(udid, null, MiscMethods.getIPOfPlayer(server, null, udid));

    double tempClientVersionNum = reqProto.getVersionNum() * 10;
    double tempLatestVersionNum = GameServer.clientVersionNumber * 10;

    // Check version number
    if ((int) tempClientVersionNum < (int) tempLatestVersionNum && tempClientVersionNum > 12.5) {
      updateStatus = UpdateStatus.MAJOR_UPDATE;
      log.info("player has been notified of forced update");
    } else if (tempClientVersionNum < tempLatestVersionNum) {
      updateStatus = UpdateStatus.MINOR_UPDATE;
    } else {
      updateStatus = UpdateStatus.NO_UPDATE;
    }

    resBuilder.setUpdateStatus(updateStatus);
    resBuilder.setAppStoreURL(Globals.APP_STORE_URL());
    resBuilder.setReviewPageURL(Globals.REVIEW_PAGE_URL());
    resBuilder.setReviewPageConfirmationMessage(Globals.REVIEW_PAGE_CONFIRMATION_MESSAGE);

    User user = null;

    // Don't fill in other fields if it is a major update
    StartupStatus startupStatus = StartupStatus.USER_NOT_IN_DB;

    Timestamp now = new Timestamp(new Date().getTime());
    boolean isLogin = true;

    int newNumConsecutiveDaysLoggedIn = 0;

    if (updateStatus != UpdateStatus.MAJOR_UPDATE) {
    	List<User> users = RetrieveUtils.userRetrieveUtils().getUserByUDIDorFbId(udid, fbId);
      user = selectUser(users, udid, fbId);//RetrieveUtils.userRetrieveUtils().getUserByUDID(udid);
      if (user != null) {
        getHazelcastPvpUtil().lockPlayer(user.getId(), this.getClass().getSimpleName());
        try {
          startupStatus = StartupStatus.USER_IN_DB;
          log.info("No major update... getting user info");
//          newNumConsecutiveDaysLoggedIn = setDailyBonusInfo(resBuilder, user, now);
          setInProgressAndAvailableQuests(resBuilder, user);
          setUserClanInfos(resBuilder, user);
          setNotifications(resBuilder, user);
          setNoticesToPlayers(resBuilder, user);
          setChatMessages(resBuilder, user);
          setPrivateChatPosts(resBuilder, user);
          setUserMonsterStuff(resBuilder, user);
          setBoosterPurchases(resBuilder);
          setFacebookAndExtraSlotsStuff(resBuilder, user);
          setCompletedTasks(resBuilder, user);
          setAllStaticData(resBuilder, user);
          setEventStuff(resBuilder, user);
          //if server sees that the user is in a pvp battle, decrement user's elo
          pvpBattleStuff(user, freshRestart); 
          
          setWhetherPlayerCompletedInAppPurchase(resBuilder, user);
          setUnhandledForgeAttempts(resBuilder, user);
          setLockBoxEvents(resBuilder, user);
//          setLeaderboardEventStuff(resBuilder);
          setAllies(resBuilder, user);
//          setAllBosses(resBuilder, user.getType());

          FullUserProto fup = CreateInfoProtoUtils.createFullUserProtoFromUser(user);
          resBuilder.setSender(fup);

          boolean isNewUser = false;
          InsertUtils.get().insertIntoLoginHistory(udid, user.getId(), now, isLogin, isNewUser);
        } catch (Exception e) {
          log.error("exception in StartupController processEvent", e);
        } finally {
          // server.unlockClanTowersTable();
          getHazelcastPvpUtil().unlockPlayer(user.getId(), this.getClass().getSimpleName());
        }
      } else {
        log.info("tutorial player with udid " + udid);

        boolean userLoggedIn = LoginHistoryRetrieveUtils.userLoggedInByUDID(udid);
        int numOldAccounts = RetrieveUtils.userRetrieveUtils().numAccountsForUDID(udid);
        boolean alreadyInFirstTimeUsers = FirstTimeUsersRetrieveUtils.userExistsWithUDID(udid);
        boolean isFirstTimeUser = false;
        // log.info("userLoggedIn=" + userLoggedIn + ", numOldAccounts="
        // + numOldAccounts
        // + ", alreadyInFirstTimeUsers=" + alreadyInFirstTimeUsers);
        if (!userLoggedIn && 0 >= numOldAccounts && !alreadyInFirstTimeUsers) {
          isFirstTimeUser = true;
        }
        
        log.info("\n userLoggedIn=" + userLoggedIn + "\t numOldAccounts=" +
        		numOldAccounts + "\t alreadyInFirstTimeUsers=" +
        		alreadyInFirstTimeUsers + "\t isFirstTimeUser=" + isFirstTimeUser);

        if (isFirstTimeUser) {
          log.info("new player with udid " + udid);
          InsertUtils.get().insertIntoFirstTimeUsers(udid, null,
              reqProto.getMacAddress(), reqProto.getAdvertiserId(), now);
        }
        
        if (Globals.OFFERCHART_ENABLED() && isFirstTimeUser) {
          sendOfferChartInstall(now, reqProto.getAdvertiserId());
        }

        boolean goingThroughTutorial = true;
        InsertUtils.get().insertIntoLoginHistory(udid, 0, now, isLogin, goingThroughTutorial);
      }
      resBuilder.setStartupStatus(startupStatus);
      setConstants(resBuilder, startupStatus);
    }

    if (Globals.KABAM_ENABLED()) {
      String naid = retrieveKabamNaid(user, udid, reqProto.getMacAddress(),
          reqProto.getAdvertiserId());
      resBuilder.setKabamNaid(naid);
    }
    
    //startup time
    resBuilder.setServerTimeMillis((new Date()).getTime());
    StartupResponseProto resProto = resBuilder.build();
    StartupResponseEvent resEvent = new StartupResponseEvent(udid);
    resEvent.setTag(event.getTag());
    resEvent.setStartupResponseProto(resProto);

    // log.info("Sending struct");
    // sendAllStructs(udid, user);

    log.debug("Writing event response: " + resEvent);
    server.writePreDBEvent(resEvent, udid);
    log.debug("Wrote response event: " + resEvent);
    // for things that client doesn't need
    log.debug("After response tasks");

    // if app is not in force tutorial execute this function,
    // regardless of whether the user is new or restarting from an account
    // reset
    updateLeaderboard(apsalarId, user, now, newNumConsecutiveDaysLoggedIn);
  }

  //priority of user returned is 
  //user with specified fbId
  //user with specified udid
  //null
  private User selectUser(List<User> users, String udid, String fbId) {
  	int numUsers = users.size();
//  	if (numUsers > 2) {
//  		log.error("there are more than 2 users with the same udid and fbId. udid=" + udid +
//  				" fbId=" + fbId + " users=" + users);
//  	}
  	if (1 == numUsers) {
  		return users.get(0);
  	}
  	
  	User udidUser = null;
  	
  	for (User u : users) {
  		String userFbId = u.getFacebookId();
  		String userUdid = u.getUdid();
  		
  		if (fbId != null && fbId.equals(userFbId)) {
  			return u;
  		} else if (null == udidUser && udid != null && udid.equals(userUdid)) {
  			//so this is the first user with specified udid, don't change reference
  			//to this user once set
  			udidUser = u;
  		}
  	}
  	
  	//didn't find user with specified fbId
  	return udidUser;
  }

  private void setInProgressAndAvailableQuests(Builder resBuilder, User user) {
  	  List<QuestForUser> inProgressAndRedeemedUserQuests = RetrieveUtils.questForUserRetrieveUtils()
  	      .getUserQuestsForUser(user.getId());
//  	  log.info("user quests: " + inProgressAndRedeemedUserQuests);
  	  
  	  List<QuestForUser> inProgressQuests = new ArrayList<QuestForUser>();
  	  List<Integer> redeemedQuestIds = new ArrayList<Integer>();
  	  
  	  Map<Integer, Quest> questIdToQuests = QuestRetrieveUtils.getQuestIdsToQuests();
  	  for (QuestForUser uq : inProgressAndRedeemedUserQuests) {
  	  	
  	    if (!uq.isRedeemed()) {
  	    	//unredeemed quest section
  	    	inProgressQuests.add(uq);
  	    } else {
  	    	int questId = uq.getQuestId();
  	    	redeemedQuestIds.add(questId);
  	    }
  	  }
  	  
  	  //generate the user quests
  	  List<FullUserQuestProto> currentUserQuests = CreateInfoProtoUtils
  	  		.createFullUserQuestDataLarges(inProgressQuests, questIdToQuests);
  	  resBuilder.addAllUserQuests(currentUserQuests);
  	  
  	  //generate the redeemed quest ids
  	  resBuilder.addAllRedeemedQuestIds(redeemedQuestIds);
  }
  
  private void setUserClanInfos(StartupResponseProto.Builder resBuilder, User user) {
    List<UserClan> userClans = RetrieveUtils.userClanRetrieveUtils().getUserClansRelatedToUser(
        user.getId());
    for (UserClan uc : userClans) {
      resBuilder.addUserClanInfo(CreateInfoProtoUtils.createFullUserClanProtoFromUserClan(uc));
    }
  }

  private void setNotifications(Builder resBuilder, User user) {
//    List<Integer> userIds = new ArrayList<Integer>();

//    Timestamp earliestBattleNotificationTimeToRetrieve = new Timestamp(new Date().getTime()
//        - ControllerConstants.STARTUP__HOURS_OF_BATTLE_NOTIFICATIONS_TO_SEND * 3600000);
//
//    List<ClanChatPost> clanChatPosts = null;
//    if (user.getClanId() > 0) {
//      clanChatPosts = ClanChatPostRetrieveUtils.getMostRecentClanChatPostsForClan(
//          ControllerConstants.RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP, user.getClanId());
//      for (ClanChatPost p : clanChatPosts) {
//        userIds.add(p.getPosterId());
//      }
//    }
//
//    Map<Integer, User> usersByIds = null;
//    if (userIds.size() > 0) {
//      usersByIds = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIds);
//    }
//
//    if (clanChatPosts != null && clanChatPosts.size() > 0) {
//
//    }
  }
  
  private void setNoticesToPlayers(Builder resBuilder, User user) {
  	List<String> notices = StartupStuffRetrieveUtils.getAllActiveAlerts();
  	if (null != notices) {
  	  for (String notice : notices) {
  	    resBuilder.addNoticesToPlayers(notice);
  	  }
  	}

  }
  


  
  
  private void setChatMessages(StartupResponseProto.Builder resBuilder, User user) {
  	  if (user.getClanId() > 0) {
  	    List<ClanChatPost> activeClanChatPosts;
  	    activeClanChatPosts = ClanChatPostRetrieveUtils.getMostRecentClanChatPostsForClan(
  	        ControllerConstants.RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP, user.getClanId());
  	
  	    if (activeClanChatPosts != null) {
  	      if (activeClanChatPosts != null && activeClanChatPosts.size() > 0) {
  	        List<Integer> userIds = new ArrayList<Integer>();
  	        for (ClanChatPost p : activeClanChatPosts) {
  	          userIds.add(p.getPosterId());
  	        }
  	        Map<Integer, User> usersByIds = null;
  	        if (userIds.size() > 0) {
  	          usersByIds = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIds);
  	          for (int i = activeClanChatPosts.size() - 1; i >= 0; i--) {
  	            ClanChatPost pwp = activeClanChatPosts.get(i);
  	            resBuilder.addClanChats(CreateInfoProtoUtils
  	                .createGroupChatMessageProtoFromClanChatPost(pwp,
  	                    usersByIds.get(pwp.getPosterId())));
  	          }
  	        }
  	      }
  	    }
  	  }
  	
  	  Iterator<GroupChatMessageProto> it = chatMessages.iterator();
  	  List<GroupChatMessageProto> globalChats = new ArrayList<GroupChatMessageProto>();
  	  while (it.hasNext()) {
  	    globalChats.add(it.next());
  	  }
  	
  	  Comparator<GroupChatMessageProto> c = new Comparator<GroupChatMessageProto>() {
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
  	  };
  	  Collections.sort(globalChats, c);
  	  // Need to add them in reverse order
  	  for (int i = 0; i < globalChats.size(); i++) {
  	    resBuilder.addGlobalChats(globalChats.get(i));
  	  }
  }

  private void setPrivateChatPosts(Builder resBuilder, User aUser) {
    int userId = aUser.getId();
    boolean isRecipient = true;
    Map<Integer, Integer> userIdsToPrivateChatPostIds = null;
    Map<Integer, PrivateChatPost> postIdsToPrivateChatPosts = new HashMap<Integer, PrivateChatPost>();
    Map<Integer, User> userIdsToUsers = null;
    Map<Integer, Set<Integer>> clanIdsToUserIdSet = null;
    Map<Integer, Clan> clanIdsToClans = null;
    List<Integer> clanlessUserIds = new ArrayList<Integer>();
    List<Integer> clanIdList = new ArrayList<Integer>();
    List<Integer> privateChatPostIds = new ArrayList<Integer>();

    //get all the most recent posts sent to this user
    Map<Integer, PrivateChatPost> postsUserReceived = 
        PrivateChatPostRetrieveUtils.getMostRecentPrivateChatPostsByOrToUser(
            userId, isRecipient, ControllerConstants.STARTUP__MAX_PRIVATE_CHAT_POSTS_RECEIVED);

    //get all the most recent posts this user sent
    isRecipient = false;
    Map<Integer, PrivateChatPost> postsUserSent = 
        PrivateChatPostRetrieveUtils.getMostRecentPrivateChatPostsByOrToUser(
            userId, isRecipient, ControllerConstants.STARTUP__MAX_PRIVATE_CHAT_POSTS_SENT);

    if ((null == postsUserReceived || postsUserReceived.isEmpty()) &&
        (null == postsUserSent || postsUserSent.isEmpty()) ) {
      log.info("user has no private chats. aUser=" + aUser);
      return;
    }

    //link other users with private chat posts and combine all the posts
    //linking is done to select only the latest post between the duple (userId, otherUserId)
    userIdsToPrivateChatPostIds = aggregateOtherUserIdsAndPrivateChatPost(postsUserReceived, postsUserSent, postIdsToPrivateChatPosts);

    if (null != userIdsToPrivateChatPostIds && !userIdsToPrivateChatPostIds.isEmpty()) {
      //retrieve all users
      List<Integer> userIdList = new ArrayList<Integer>();
      userIdList.addAll(userIdsToPrivateChatPostIds.keySet());
      userIdList.add(userId); //userIdsToPrivateChatPostIds contains userIds other than 'this' userId
      userIdsToUsers = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIdList);
    } else {
      //user did not send any nor received any private chat posts
      log.error("unexpected error: aggregating private chat post ids returned nothing");
      return;
    }
    if (null == userIdsToUsers || userIdsToUsers.isEmpty() ||
        userIdsToUsers.size() == 1) {
      log.error("unexpected error: perhaps user talked to himself. postsUserReceved="
          + postsUserReceived + ", postsUserSent=" + postsUserSent + ", aUser=" + aUser);
      return;
    }

    //get all the clans for the users (a map: clanId->set(userId))
    //put the clanless users in the second argument: userIdsToClanlessUsers
    clanIdsToUserIdSet = determineClanIdsToUserIdSet(userIdsToUsers, clanlessUserIds);
    if (null != clanIdsToUserIdSet && !clanIdsToUserIdSet.isEmpty()) {
      clanIdList.addAll(clanIdsToUserIdSet.keySet());
      //retrieve all clans for the users
      clanIdsToClans = ClanRetrieveUtils.getClansByIds(clanIdList);
    }


    //create the protoList
    privateChatPostIds.addAll(userIdsToPrivateChatPostIds.values());
    List<PrivateChatPostProto> pcppList = CreateInfoProtoUtils.createPrivateChatPostProtoList(
        clanIdsToClans, clanIdsToUserIdSet, userIdsToUsers, clanlessUserIds, privateChatPostIds,
        postIdsToPrivateChatPosts);

    resBuilder.addAllPcpp(pcppList);
  }
  
  private Map<Integer, Integer> aggregateOtherUserIdsAndPrivateChatPost(
      Map<Integer, PrivateChatPost> postsUserReceived, Map<Integer, PrivateChatPost> postsUserSent,
      Map<Integer, PrivateChatPost> postIdsToPrivateChatPosts) {
    Map<Integer, Integer> userIdsToPrivateChatPostIds = new HashMap<Integer, Integer>();

    //go through the posts specific user received
    if (null != postsUserReceived && !postsUserReceived.isEmpty()) {
      for (int pcpId : postsUserReceived.keySet()) {
        PrivateChatPost postUserReceived = postsUserReceived.get(pcpId);
        int senderId = postUserReceived.getPosterId();

        //record that the other user and specific user chatted
        userIdsToPrivateChatPostIds.put(senderId, pcpId);
      }
      //combine all the posts together
      postIdsToPrivateChatPosts.putAll(postsUserReceived);
    }

    if (null != postsUserSent && !postsUserSent.isEmpty()) {
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
    return userIdsToPrivateChatPostIds;
  }
  
  private Map<Integer, Set<Integer>> determineClanIdsToUserIdSet(Map<Integer, User> userIdsToUsers,
      List<Integer> clanlessUserUserIds) {
    Map<Integer, Set<Integer>> clanIdsToUserIdSet = new HashMap<Integer, Set<Integer>>();
    if (null == userIdsToUsers  || userIdsToUsers.isEmpty()) {
      return clanIdsToUserIdSet;
    }
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
    return clanIdsToUserIdSet;
  }


  private void setUserMonsterStuff(Builder resBuilder, User user) {
  	int userId = user.getId();
    List<MonsterForUser> userMonsters= RetrieveUtils.monsterForUserRetrieveUtils()
        .getMonstersForUser(userId);
    
    if (null != userMonsters && !userMonsters.isEmpty()) {
    	for (MonsterForUser mfu : userMonsters) {
    		FullUserMonsterProto fump = CreateInfoProtoUtils.createFullUserMonsterProtoFromUserMonster(mfu);
    		resBuilder.addUsersMonsters(fump);
    	}
    }
    
    //monsters in healing
    Map<Long, MonsterHealingForUser> userMonstersHealing = MonsterHealingForUserRetrieveUtils
    		.getMonstersForUser(userId);
    if (null != userMonstersHealing && !userMonstersHealing.isEmpty()) {

    	Collection<MonsterHealingForUser> healingMonsters = userMonstersHealing.values();
    	for (MonsterHealingForUser mhfu : healingMonsters) {
    		UserMonsterHealingProto umhp = CreateInfoProtoUtils.createUserMonsterHealingProtoFromObj(mhfu);
    		resBuilder.addMonstersHealing(umhp);
    	}
    }
    
    //enhancing monsters
    Map<Long, MonsterEnhancingForUser> userMonstersEnhancing = MonsterEnhancingForUserRetrieveUtils
    		.getMonstersForUser(userId);
    if (null != userMonstersEnhancing && !userMonstersEnhancing.isEmpty()) {
    	//find the monster that is being enhanced
    	Collection<MonsterEnhancingForUser> enhancingMonsters = userMonstersEnhancing.values();
    	UserEnhancementItemProto baseMonster = null;
    	
    	List<UserEnhancementItemProto> feeders = new ArrayList<UserEnhancementItemProto>();
    	for (MonsterEnhancingForUser mefu : enhancingMonsters) {
    		UserEnhancementItemProto ueip = CreateInfoProtoUtils.createUserEnhancementItemProtoFromObj(mefu);
    		
    		//TODO: if user has no monsters with null start time
    		//(if user has all monsters with a start time), or if user has more than one
    		//monster with a null start time
    		//STORE THEM AND DELETE THEM OR SOMETHING
    		
    		//search for the monster that is being enhanced, the one with null start time
    		Date startTime = mefu.getExpectedStartTime();
    		if(null == startTime) {
    			//found him
    			baseMonster = ueip;
    		} else {
    			//just a feeder, add him to the list
    			feeders.add(ueip);
    		}
    	}
    	
    	UserEnhancementProto uep = CreateInfoProtoUtils.createUserEnhancementProtoFromObj(
    			userId, baseMonster, feeders);
    	
    	resBuilder.setEnhancements(uep);
    }
    
    //evolving monsters
    Map<Long, MonsterEvolvingForUser> userMonsterEvolving = MonsterEvolvingForUserRetrieveUtils
    		.getCatalystIdsToEvolutionsForUser(userId);
    if (null != userMonsterEvolving && !userMonsterEvolving.isEmpty()) {
    	
    	for (MonsterEvolvingForUser mefu : userMonsterEvolving.values()) {
    		UserMonsterEvolutionProto uep = CreateInfoProtoUtils
    				.createUserEvolutionProtoFromEvolution(mefu);
    		
    		//TODO: NOTE THAT IF MORE THAN ONE EVOLUTION IS ALLLOWED AT A TIME, THIS METHOD
    		//CALL NEEDS TO CHANGE
    		resBuilder.setEvolution(uep);
    	}
    }
    
    
  }

  private void setBoosterPurchases(StartupResponseProto.Builder resBuilder) {
    Iterator<RareBoosterPurchaseProto> it = goodEquipsRecievedFromBoosterPacks.iterator();
    List<RareBoosterPurchaseProto> boosterPurchases = new ArrayList<RareBoosterPurchaseProto>();
    while (it.hasNext()) {
      boosterPurchases.add(it.next());
    }

    Comparator<RareBoosterPurchaseProto> c = new Comparator<RareBoosterPurchaseProto>() {
      @Override
      public int compare(RareBoosterPurchaseProto o1, RareBoosterPurchaseProto o2) {
        if (o1.getTimeOfPurchase() < o2.getTimeOfPurchase()) {
          return -1;
        } else if (o1.getTimeOfPurchase() > o2.getTimeOfPurchase()) {
          return 1;
        } else {
          return 0;
        }
      }
    };
    Collections.sort(boosterPurchases, c);
    // Need to add them in reverse order
    for (int i = 0; i < boosterPurchases.size(); i++) {
      resBuilder.addRareBoosterPurchases(boosterPurchases.get(i));
    }
  }  
  
  private void setFacebookAndExtraSlotsStuff(Builder resBuilder, User thisUser) {
  	//gather up data so as to make only one user retrieval query
  	int userId = thisUser.getId();
  	
  	//get the invites where this user is the recipient, get unaccepted, hence, unredeemed invites
  	Map<Integer, UserFacebookInviteForSlot> idsToInvitesToMe = new HashMap<Integer, UserFacebookInviteForSlot>();
  	String fbId = thisUser.getFacebookId();
  	List<Integer> specificInviteIds = null;
  	boolean filterByAccepted = true;
  	boolean isAccepted = false;
  	boolean filterByRedeemed = false;
  	boolean isRedeemed = false; //doesn't matter
  	//base case where user does not have facebook id
  	if (null != fbId && !fbId.isEmpty()) {
  		idsToInvitesToMe = UserFacebookInviteForSlotRetrieveUtils
  				.getSpecificOrAllInvitesForRecipient(fbId, specificInviteIds, filterByAccepted,
  						isAccepted, filterByRedeemed, isRedeemed);
  	}
  	
  	//get the invites where this user is the inviter, get accepted, unredeemed does not matter 
  	isAccepted = true;
  	Map<Integer, UserFacebookInviteForSlot> idsToInvitesFromMe = 
  			UserFacebookInviteForSlotRetrieveUtils.getSpecificOrAllInvitesForInviter(
  					userId, specificInviteIds, filterByAccepted, isAccepted, filterByRedeemed, isRedeemed);
  	
  	List<String> recipientFacebookIds = getRecipientFbIds(idsToInvitesFromMe);
  	
  	//to make it easier later on, get the inviter ids for these invites and
  	//map inviter id to an invite
  	Map<Integer, UserFacebookInviteForSlot> inviterIdsToInvites =
  			new HashMap<Integer, UserFacebookInviteForSlot>();
  	//inviterIdsToInvites will be populated by getInviterIds(...)
  	List<Integer> inviterUserIds = getInviterIds(idsToInvitesToMe, inviterIdsToInvites);
  	
  	
  	//base case where user never did any invites
  	if ((null == recipientFacebookIds || recipientFacebookIds.isEmpty()) &&
  			(null == inviterUserIds || inviterUserIds.isEmpty())) {
  		//no facebook stuff
  		return;
  	}
  	

  	//GET THE USERS
  	Map<Integer, User> idsToUsers = RetrieveUtils.userRetrieveUtils()
  			.getUsersForFacebookIdsOrUserIds(recipientFacebookIds, inviterUserIds);
  	List<User> recipients = new ArrayList<User>();
  	List<User> inviters = new ArrayList<User>();
  	separateUsersIntoRecipientsAndInviters(idsToUsers, recipientFacebookIds,
  			inviterUserIds, recipients, inviters);
  	
  	
  	//send all the invites where this user is the one being invited
  	for (Integer inviterId : inviterUserIds) {
  		User inviter = idsToUsers.get(inviterId);
  		MinimumUserProtoWithFacebookId inviterProto = null;
  		UserFacebookInviteForSlot invite = inviterIdsToInvites.get(inviterId);
  		UserFacebookInviteForSlotProto inviteProto = CreateInfoProtoUtils
  				.createUserFacebookInviteForSlotProtoFromInvite(invite, inviter, inviterProto);
  		
  		resBuilder.addInvitesToMeForSlots(inviteProto);
  	}
  	
  	//send all the invites where this user is the one inviting
  	MinimumUserProtoWithFacebookId thisUserProto = CreateInfoProtoUtils
  			.createMinimumUserProtoWithFacebookIdFromUser(thisUser);
  	for (UserFacebookInviteForSlot invite : idsToInvitesFromMe.values()) {
  		UserFacebookInviteForSlotProto inviteProto = CreateInfoProtoUtils
  				.createUserFacebookInviteForSlotProtoFromInvite(invite, thisUser, thisUserProto);
  		resBuilder.addInvitesFromMeForSlots(inviteProto);
  	}
  }
  
  private List<String> getRecipientFbIds(Map<Integer, UserFacebookInviteForSlot> idsToInvitesFromMe) {
  	List<String> fbIds = new ArrayList<String>();
  	for (UserFacebookInviteForSlot invite : idsToInvitesFromMe.values()) {
  		String fbId = invite.getRecipientFacebookId();
  		fbIds.add(fbId);
  	}
  	return fbIds;
  }
  
  //inviterIdsToInvites will be populated
  private List<Integer> getInviterIds(Map<Integer, UserFacebookInviteForSlot> idsToInvites,
  		Map<Integer, UserFacebookInviteForSlot> inviterIdsToInvites) {
  	
  	List<Integer> inviterIds = new ArrayList<Integer>(); 
  	for (UserFacebookInviteForSlot invite : idsToInvites.values()) {
  		int userId = invite.getInviterUserId();
  		inviterIds.add(userId);
  		
  		inviterIdsToInvites.put(userId, invite);
  	}
  	return inviterIds;
  }
  
  //given map of userIds to users, list of recipient facebook ids and list of inviter
  //user ids, separate the map of users into recipient and inviter
  private void separateUsersIntoRecipientsAndInviters(Map<Integer, User> idsToUsers,
  		List<String> recipientFacebookIds, List<Integer> inviterUserIds,
  		List<User> recipients, List<User> inviters) {
  	
  	Set<String> recipientFacebookIdsSet = new HashSet<String>(recipientFacebookIds);
  	
  	//set the recipients
  	for (Integer userId : idsToUsers.keySet()) {
  		User u = idsToUsers.get(userId);
  		String facebookId = u.getFacebookId();
  		
  		if (null != facebookId && recipientFacebookIdsSet.contains(facebookId)) {
  			//this is a recipient
  			recipients.add(u);
  		}
  	}
  	
  	//set the inviters
  	for (Integer inviterId : inviterUserIds) {
  		if (idsToUsers.containsKey(inviterId)) {
  			User u = idsToUsers.get(inviterId);
  			inviters.add(u);
  		}
  	}
  	
  }
  
  
  private void setCompletedTasks(Builder resBuilder, User user) {
  	List<Integer> taskIds = TaskForUserCompletedRetrieveUtils
  			.getAllTaskIdsForUser(user.getId());
  	resBuilder.addAllCompletedTaskIds(taskIds);
  }
  
  private void setAllStaticData(Builder resBuilder, User user) {
  	int userId = user.getId();
  	StaticDataProto sdp = MiscMethods.getAllStaticData(userId);
  	
  	resBuilder.setStaticDataStuffProto(sdp);
  }
  
  private void setEventStuff(Builder resBuilder, User user) {
  	int userId = user.getId();
  	List<EventPersistentForUser> events = EventPersistentForUserRetrieveUtils
  			.getUserPersistentEventForUserId(userId);
  	
  	for (EventPersistentForUser epfu : events) {
  		UserPersistentEventProto upep = CreateInfoProtoUtils.createUserPersistentEventProto(epfu);
  		resBuilder.addUserEvents(upep);
  	}
  	
  }
  
  private void pvpBattleStuff(User user, boolean isFreshRestart) {
  	//remove this user from the users available to be attacked in pvp
  	int userId = user.getId();
  	getHazelcastPvpUtil().removeOfflinePvpUser(userId);
  	
  	//if bool isFreshRestart is true, then deduct user's elo by amount specified in
  	//the table (pvp_battle_for_user), since user auto loses
  	PvpBattleForUser battle = PvpBattleForUserRetrieveUtils
  			.getPvpBattleForUserForAttacker(userId);
  	
  	if (null == battle) {
  		return;
  	}
  	//capping max elo attacker loses
  	int eloAttackerLoses = battle.getAttackerLoseEloChange();
  	if (user.getElo() + eloAttackerLoses < 0) {
  		eloAttackerLoses = -1 * user.getElo();
  	}
  	
  	int defenderId = battle.getDefenderId();
  	int eloDefenderWins = battle.getDefenderLoseEloChange();
  	
  	//user has unfinished battle, reward defender and penalize attacker
  	//nested try catch's in order to prevent exception bubbling up, all because of
  	//some stinkin' elo XP
  	try {
  		//NOTE: this lock ordering might result in a temp deadlock
  		//doesn't reeeally matter if can't penalize defender...
  		
  		//only lock real users
  		if (0 != defenderId) {
  			getHazelcastPvpUtil().lockPlayer(defenderId, this.getClass().getSimpleName());
  		}
  		try {
  			User defender = RetrieveUtils.userRetrieveUtils().getUserById(defenderId);
  			OfflinePvpUser defenderOpu = getHazelcastPvpUtil().getOfflinePvpUser(defenderId);
  			
  			//update attacker
  			user.updateEloOilCash(userId, eloAttackerLoses, 0, 0);
  			
  			//update defender if real, might need to cap defenderElo
  			if (null != defender) {
  				defender.updateEloOilCash(userId, eloDefenderWins, 0, 0);
  			}
  			if (null != defenderOpu) { //update if exists
  				int defenderElo = defender.getElo();
  				defenderOpu.setElo(defenderElo);
  				getHazelcastPvpUtil().updateOfflinePvpUser(defenderOpu);
  			}
  			
  			//delete that this battle occurred
  			DeleteUtils.get().deletePvpBattleForUser(userId);
  			log.info("successfully penalized, rewarded attacker, defender respectively. battle= " +
  					battle);
  			
  		} catch (Exception e){
  			log.error("tried to penalize, reward attacker, defender respectively. battle=" +
  					battle, e);
  		} finally {
  			if (0 != defenderId) {
  				getHazelcastPvpUtil().unlockPlayer(defenderId, this.getClass().getSimpleName());
  			}
  		}
  	} catch (Exception e2) {
  		log.error("could not successfully penalize, reward attacker, defender respectively." +
  				" battle=" + battle, e2);
  	}
  	
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
//  private void setAllBosses(Builder resBuilder, UserType type) {
//    Map<Integer, Monster> bossIdsToBosses = 
//        MonsterRetrieveUtils.getBossIdsToBosses();
//
//    for (Monster b : bossIdsToBosses.values()) {
//      FullBossProto fbp =
//          CreateInfoProtoUtils.createFullBossProtoFromBoss(type, b);
//      resBuilder.addBosses(fbp);
//    }
//  }

  // retrieve's the active leaderboard event prizes and rewards for the events
//  private void setLeaderboardEventStuff(StartupResponseProto.Builder resBuilder) {
//    resBuilder.addAllLeaderboardEvents(MiscMethods.currentTournamentEventProtos());
//  }

  private void sendOfferChartInstall(Date installTime, String advertiserId) {
    String clientId = "15";
    String appId = "648221050";
    String geo = "N/A";
    String installTimeStr = ""+installTime.getTime();
    String devicePlatform = "iphone";
    String deviceType = "iphone";

    String urlString = "http://offerchart.com/mobileapp/api/send_install_ping?" +
        "client_id="+clientId +
        "&app_id="+appId +
        "&device_id="+advertiserId +
        "&device_type="+deviceType +
        "&geo="+geo +
        "&install_time="+installTimeStr +
        "&device_platform="+devicePlatform;

    log.info("Sending offerchart request:\n"+urlString);
    DefaultHttpClient httpclient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(urlString);

    try {
      HttpResponse response1 = httpclient.execute(httpGet);
      BufferedReader rd = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
      String responseString = "";
      String line;
      while ((line = rd.readLine()) != null) {
        responseString += line;
      }
      log.info("Received response: " + responseString);
    } catch (Exception e) {
      log.error("failed to make offer chart call", e);
    }
  }

  private String retrieveKabamNaid(User user, String openUdid, String mac, String advertiserId) {
    String host;
    int port = 443;
    int clientId;
    String secret;
    if (Globals.IS_SANDBOX()) {
      host = KabamProperties.SANDBOX_API_URL;
      clientId = KabamProperties.SANDBOX_CLIENT_ID;
      secret = KabamProperties.SANDBOX_SECRET;
    } else {
      host = KabamProperties.PRODUCTION_API_URL;
      clientId = KabamProperties.PRODUCTION_CLIENT_ID;
      secret = KabamProperties.PRODUCTION_SECRET;
    }

    KabamApi kabamApi = new KabamApi(host, port, secret);
    String userId = openUdid;
    String platform = "iphone";

    String biParams = "{\"open_udid\":\"" + userId + "\",\"mac\":\"" + mac
        + "\",\"mac_hash\":\"" + DigestUtils.md5Hex(mac) + "\",\"advertiser_id\":\"" + advertiserId
        + "\"}";

    MobileNaidResponse naidResponse;
    try {
      naidResponse = kabamApi.mobileGetNaid(userId, clientId, platform, biParams,
          new Date().getTime() / 1000);
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }

    if (naidResponse.getReturnCode() == ResponseCode.Success) {
      if (user != null) {
        user.updateSetKabamNaid(naidResponse.getNaid());
      }
      log.info("Successfully got kabam naid.");
      return naidResponse.getNaid()+"";
    } else {
      log.error("Error retrieving kabam naid: " + naidResponse.getReturnCode());
    }
    return "";
  }

  private void setLockBoxEvents(StartupResponseProto.Builder resBuilder, User user) {
//    resBuilder.addAllLockBoxEvents(MiscMethods.currentLockBoxEvents());
//    Map<Integer, UserLockBoxEvent> map = UserLockBoxEventRetrieveUtils
//        .getLockBoxEventIdsToLockBoxEventsForUser(user.getId());
//    for (LockBoxEventProto p : resBuilder.getLockBoxEventsList()) {
//      UserLockBoxEvent e = map.get(p.getLockBoxEventId());
//      if (e != null) {
//        resBuilder.addUserLockBoxEvents(CreateInfoProtoUtils.createUserLockBoxEventProto(e,
//            user.getType()));
//      }
//    }
  }

  protected void updateLeaderboard(String apsalarId, User user, Timestamp now,
      int newNumConsecutiveDaysLoggedIn) {
    if (user != null) {
      log.info("Updating leaderboard for user " + user.getId());
      syncApsalaridLastloginConsecutivedaysloggedinResetBadges(user, apsalarId, now,
          newNumConsecutiveDaysLoggedIn);
      LeaderBoardUtil leaderboard = AppContext.getApplicationContext().getBean(LeaderBoardUtil.class);
      leaderboard.updateLeaderboardForUser(user);
    }
  }


  private void setUnhandledForgeAttempts(Builder resBuilder, User user) {
//    Map<Integer, BlacksmithAttempt> blacksmithIdToBlacksmithAttempt = 
//        UnhandledBlacksmithAttemptRetrieveUtils.getUnhandledBlacksmithAttemptsForUser(user.getId());
//    int numEquipsBeingForged = blacksmithIdToBlacksmithAttempt.size();
//    int numEquipsUserCanForge = ControllerConstants.FORGE_DEFAULT_NUMBER_OF_FORGE_SLOTS
//        + user.getNumAdditionalForgeSlots();
//
//    if (blacksmithIdToBlacksmithAttempt != null && numEquipsBeingForged <= numEquipsUserCanForge) {
//      for (BlacksmithAttempt ba : blacksmithIdToBlacksmithAttempt.values()) {
//
//        int baId = ba.getId();
//        resBuilder.addUnhandledForgeAttempt(
//            CreateInfoProtoUtils.createUnhandledBlacksmithAttemptProtoFromBlacksmithAttempt(
//                blacksmithIdToBlacksmithAttempt.get(baId))
//            );
//
//        int equipId = ba.getEquipId();
//        Equipment e = EquipmentRetrieveUtils.getEquipmentIdsToEquipment().get(equipId);
//        resBuilder.addForgeAttemptEquip(
//            CreateInfoProtoUtils.createFullEquipProtoFromEquip(e)
//            );
//      }
//    }
//
//    if (blacksmithIdToBlacksmithAttempt != null && numEquipsBeingForged > numEquipsUserCanForge) {
//      log.error("user has too many blacksmith attempts, should only have " + numEquipsUserCanForge + 
//          ". blacksmith attempts = " + blacksmithIdToBlacksmithAttempt);
//    }
  }

  private void setWhetherPlayerCompletedInAppPurchase(Builder resBuilder, User user) {
    boolean hasPurchased = IAPHistoryRetrieveUtils.checkIfUserHasPurchased(user.getId());
    resBuilder.setPlayerHasBoughtInAppPurchase(hasPurchased);
  }

  // returns the total number of consecutive days the user logged in,
  // awards user if user logged in for an additional consecutive day
//  private int setDailyBonusInfo(Builder resBuilder, User user, Timestamp now) {
//    // will keep track of total consecutive days user has logged in, just
//    // for funzies
//    List<Integer> numConsecDaysList = new ArrayList<Integer>();
//    int totalConsecutiveDaysPlayed = 1;
//    List<Boolean> rewardUserList = new ArrayList<Boolean>();
//    boolean rewardUser = false;
//
//    int consecutiveDaysPlayed = determineCurrentConsecutiveDay(user, now, numConsecDaysList,
//        rewardUserList);
//    if (!numConsecDaysList.isEmpty()) {
//      totalConsecutiveDaysPlayed = numConsecDaysList.get(0);
//      rewardUser = rewardUserList.get(0);
//    }
//
//    DailyBonusReward rewardForUser = determineRewardForUser(user);
//    // function does nothing if null reward was returned from
//    // determineRewardForUser
//    Map<String, Integer> currentDayReward = selectRewardFromDailyBonusReward(rewardForUser,
//        consecutiveDaysPlayed);
//
//    List<Integer> equipIdRewardedList = new ArrayList<Integer>();
//    // function does nothing if previous function returned null, or
//    // either updates user's money or "purchases" booster pack for user
//    boolean successful = writeDailyBonusRewardToDB(user, currentDayReward, rewardUser, now,
//        equipIdRewardedList);
//    if (successful) {
//      int equipIdRewarded = equipIdRewardedList.get(0);
//      writeToUserDailyBonusRewardHistory(user, currentDayReward, consecutiveDaysPlayed, now,
//          equipIdRewarded);
//    }
//    setDailyBonusStuff(resBuilder, user, rewardUser, rewardForUser);
//    return totalConsecutiveDaysPlayed;
//  }

  // totalConsecutiveDaysList will contain one element the actual number of
  // consecutive
  // days the user has logged into our game, not really necessary to keep
  // track...
//  private int determineCurrentConsecutiveDay(User user, Timestamp now,
//      List<Integer> totalConsecutiveDaysList, List<Boolean> rewardUserList) {
//    // SETTING STUFF UP
//    int userId = user.getId();
//    UserDailyBonusRewardHistory lastReward = UserDailyBonusRewardHistoryRetrieveUtils
//        .getLastDailyRewardAwardedForUserId(userId);
//    Date nowDate = new Date(now.getTime());
//    long nowDateMillis = nowDate.getTime();
//
//    if (null == lastReward) {
//      log.info("user has never received a daily bonus reward. Setting consecutive days played to 1.");
//      totalConsecutiveDaysList.add(1);
//      rewardUserList.add(true);
//      return 1;
//    }
//    // let days = consecutive day amount corresponding to the reward user
//    // was given
//    // if reward was more than one day ago (in past), return 1
//    // else if user was rewarded yesterday return the (1 + days)
//    // else reward was today return days
//    int nthConsecutiveDay = lastReward.getNthConsecutiveDay();
//    Date dateLastAwarded = lastReward.getDateAwarded();
//    long dateLastMillis = dateLastAwarded.getTime();
//    boolean awardedInThePast = nowDateMillis > dateLastMillis;
//
//    int dayDiff = MiscMethods.dateDifferenceInDays(dateLastAwarded, nowDate);
//    // log.info("dateLastAwarded=" + dateLastAwarded + ", nowDate=" +
//    // nowDate + ", day difference=" + dayDiff);
//    if (1 < dayDiff && awardedInThePast) {
//      // log.info("user broke their logging in streak. previous daily bonus reward: "
//      // + lastReward
//      // + ", now=" + now);
//      // been a while since user last logged in
//      totalConsecutiveDaysList.add(1);
//      rewardUserList.add(true);
//      return 1;
//    } else if (1 == dayDiff && awardedInThePast) {
//      // log.info("awarding user. previous daily bonus reward: " +
//      // lastReward + ", now=" + now);
//      // user logged in yesterday
//      totalConsecutiveDaysList.add(user.getNumConsecutiveDaysPlayed() + 1);
//      rewardUserList.add(true);
//      return nthConsecutiveDay % ControllerConstants.STARTUP__DAILY_BONUS_MAX_CONSECUTIVE_DAYS + 1;
//    } else {
//      // either user logged in today or user tried faking time, but who
//      // cares...
//      totalConsecutiveDaysList.add(user.getNumConsecutiveDaysPlayed());
//      rewardUserList.add(false);
//      // log.info("user already collected his daily reward. previous daily bonus reward: "
//      // + lastReward + ", now=" + now);
//      return nthConsecutiveDay;
//    }
//  }

//  private DailyBonusReward determineRewardForUser(User aUser) {
//    Map<Integer, DailyBonusReward> allDailyRewards = DailyBonusRewardRetrieveUtils
//        .getDailyBonusRewardIdsToDailyBonusRewards();
//    // sanity check, exit if it fails
//    if (null == allDailyRewards || allDailyRewards.isEmpty()) {
//      log.error("unexpected error: There are no daily bonus rewards set up in the daily_bonus_reward table");
//      return null;
//    }
//
//    int level = aUser.getLevel();
//    // determine daily bonus reward for this user's level, exit if there it
//    // doesn't exist
//    DailyBonusReward reward = selectDailyBonusRewardForLevel(allDailyRewards, level);
//    if (null == reward) {
//      log.error("unexpected error: no daily bonus rewards available for level=" + level);
//    }
//    return reward;
//  }

//  private DailyBonusReward selectDailyBonusRewardForLevel(Map<Integer, DailyBonusReward> allRewards,
//      int userLevel) {
//    DailyBonusReward returnValue = null;
//    for (int id : allRewards.keySet()) {
//      DailyBonusReward dbr = allRewards.get(id);
//      int minLevel = dbr.getMinLevel();
//      int maxLevel = dbr.getMaxLevel();
//      if (minLevel <= userLevel && userLevel <= maxLevel) {
//        // we found the reward to return
//        returnValue = dbr;
//        break;
//      }
//    }
//    return returnValue;
//  }
//
//  private Map<String, Integer> selectRewardFromDailyBonusReward(DailyBonusReward rewardForUser,
//      int numConsecutiveDaysPlayed) {
//    if (null == rewardForUser) {
//      return null;
//    }
//    if (5 < numConsecutiveDaysPlayed || 0 >= numConsecutiveDaysPlayed) {
//      log.error("unexpected error: number of consecutive days played is not in the range [1,5]. "
//          + "numConsecutiveDaysPlayed=" + numConsecutiveDaysPlayed);
//      return null;
//    }
//    Map<String, Integer> reward = getCurrentDailyReward(rewardForUser, numConsecutiveDaysPlayed);
//    return reward;
//  }

  // sets the rewards the user gets/ will get in the daily bonus info builder
//  private Map<String, Integer> getCurrentDailyReward(DailyBonusReward reward, int numConsecutiveDaysPlayed) {
//    Map<String, Integer> returnValue = new HashMap<String, Integer>();
//    String key = "";
//    int value = ControllerConstants.NOT_SET;
//
//    String silver = MiscMethods.silver;
//    String gold = MiscMethods.gold;
//    String boosterPackIdString = MiscMethods.boosterPackId;
//
//    // mimicking fall through in switch statement, setting reward user just
//    // got
//    // today and will get in future logins in 5 consecutive day spans
//    if (5 == numConsecutiveDaysPlayed) {
//      // can't set reward in the builder; currently have booster pack id
//      // need equip id
//      key = boosterPackIdString;
//      List<Integer> boosterPackIds = reward.getDayFiveBoosterPackIds();
//      value = MiscMethods.getRandomIntFromList(boosterPackIds);
//    }
//    if (4 == numConsecutiveDaysPlayed) {
//      key = silver;
//      value = reward.getDayFourCoins();
//    }
//    if (3 == numConsecutiveDaysPlayed) {
//      key = gold;
//      value = reward.getDayThreeDiamonds();
//    }
//    if (2 == numConsecutiveDaysPlayed) {
//      key = silver;
//      value = reward.getDayTwoCoins();
//    }
//    if (1 == numConsecutiveDaysPlayed) {
//      key = silver;
//      value = reward.getDayOneCoins();
//    }
//    returnValue.put(key, value);
//    return returnValue;
//  }

  // returns the equip id user "purchased" by logging in
  // mimics purchase booster pack controller except the argument checking and
  // dealing with money
  private int purchaseBoosterPack(int boosterPackId, User aUser, int numBoosterItemsUserWants, Timestamp now) {
    int equipId = ControllerConstants.NOT_SET;
//    try {
//      // local vars
//      int userId = aUser.getId();
//      BoosterPack aPack = BoosterPackRetrieveUtils.getBoosterPackForBoosterPackId(boosterPackId);
//      Map<Integer, BoosterItem> boosterItemIdsToBoosterItems = BoosterItemRetrieveUtils
//          .getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);
//      Map<Integer, Integer> boosterItemIdsToNumCollected = UserBoosterItemRetrieveUtils
//          .getBoosterItemIdsToQuantityForUser(userId);
//      Map<Integer, Integer> newBoosterItemIdsToNumCollected = new HashMap<Integer, Integer>();
//      List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
//      List<Boolean> collectedBeforeReset = new ArrayList<Boolean>();
//      List<Long> userEquipIds = new ArrayList<Long>();
//
//      // actually selecting equips
//      boolean resetOccurred = MiscMethods.getAllBoosterItemsForUser(boosterItemIdsToBoosterItems,
//          boosterItemIdsToNumCollected, numBoosterItemsUserWants, aUser, aPack, itemsUserReceives,
//          collectedBeforeReset);
//      newBoosterItemIdsToNumCollected = new HashMap<Integer, Integer>(boosterItemIdsToNumCollected);
//      boolean successful = writeBoosterStuffToDB(aUser, boosterItemIdsToNumCollected,
//          newBoosterItemIdsToNumCollected, itemsUserReceives, collectedBeforeReset,
//          resetOccurred, now, userEquipIds);
//      if (successful) {
//        //exclude from daily limit check in PurchaseBoosterPackController
//        boolean excludeFromLimitCheck = true;
//        MiscMethods.writeToUserBoosterPackHistoryOneUser(userId, boosterPackId,
//            numBoosterItemsUserWants, now, itemsUserReceives, excludeFromLimitCheck,
//            userEquipIds);
//        equipId = getEquipId(numBoosterItemsUserWants, itemsUserReceives);
//      }
//
//    } catch (Exception e) {
//      log.error("unexpected error: ", e);
//    }
    return equipId;
  }

//  private int getEquipId(int numBoosterItemsUserWants, List<BoosterItem> itemsUserReceives) {
//    if (1 != numBoosterItemsUserWants) {
//      log.error("unexpected error: trying to buy more than one equip from booster pack. boosterItems="
//          + MiscMethods.shallowListToString(itemsUserReceives));
//      return ControllerConstants.NOT_SET;
//    }
//    BoosterItem bi = itemsUserReceives.get(0);
//    return bi.getEquipId();
//  }

  private boolean writeBoosterStuffToDB(User aUser, Map<Integer, Integer> boosterItemIdsToNumCollected,
      Map<Integer, Integer> newBoosterItemIdsToNumCollected, List<BoosterItem> itemsUserReceives,
      List<Boolean> collectedBeforeReset, boolean resetOccurred, Timestamp now,
      List<Long> userEquipIdsForHistoryTable) {
//    int userId = aUser.getId();
//    List<Long> userEquipIds = MiscMethods.insertNewUserEquips(userId,
//        itemsUserReceives, now, ControllerConstants.UER__DAILY_BONUS_REWARD);
//    if (null == userEquipIds || userEquipIds.isEmpty() || userEquipIds.size() != itemsUserReceives.size()) {
//      log.error("unexpected error: failed to insert equip for user. boosteritems="
//          + MiscMethods.shallowListToString(itemsUserReceives) + "\t userEquipIds="+ userEquipIds);
//      return false;
//    }
//
//    if (!MiscMethods.updateUserBoosterItems(itemsUserReceives, collectedBeforeReset,
//        boosterItemIdsToNumCollected, newBoosterItemIdsToNumCollected, userId, resetOccurred)) {
//      // failed to update user_booster_items
//      log.error("unexpected error: failed to update user_booster_items for userId: " + userId
//          + " attempting to delete equips given: " + MiscMethods.shallowListToString(userEquipIds));
//      DeleteUtils.get().deleteUserEquips(userEquipIds);
//      return false;
//    }
//    userEquipIdsForHistoryTable.addAll(userEquipIds);
    return true;
  }

  private boolean writeDailyBonusRewardToDB(User aUser, Map<String, Integer> currentDayReward,
      boolean giveToUser, Timestamp now, List<Integer> equipIdRewardedList) {
    int equipId = ControllerConstants.NOT_SET;
    if (!giveToUser || null == currentDayReward || 0 == currentDayReward.size()) {
      return false;
    }
    String key = "";
    int value = ControllerConstants.NOT_SET;
    // sanity check, should only be one reward: gold, silver, equipId
    if (1 == currentDayReward.size()) {
      String[] keys = new String[1];
      currentDayReward.keySet().toArray(keys);
      key = keys[0];
      value = currentDayReward.get(key);
    } else {
      log.error("unexpected error: current day's reward for a user is more than one. rewards="
          + currentDayReward);
      return false;
    }

    int previousSilver = aUser.getCash();
    int previousGold = aUser.getGems();
    if (key.equals(MiscMethods.boosterPackId)) {
      // since user got a booster pack id as reward, need to "buy it" for
      // him
      int numBoosterItemsUserWants = 1;
      // calling this will already give the user an equip
      equipId = purchaseBoosterPack(value, aUser, numBoosterItemsUserWants, now);
      if (ControllerConstants.NOT_SET == equipId) {
        log.error("unexpected error: failed to 'buy' booster pack for user. packId=" + value
            + ", user=" + aUser);
        return false;
      }
    }
    if (key.equals(MiscMethods.cash)) {
      if (!aUser.updateRelativeCashNaive(value)) {
        log.error("unexpected error: could not give silver bonus of " + value + " to user " + aUser);
        return false;
      } else {// gave user silver
        writeToUserCurrencyHistory(aUser, key, previousSilver, currentDayReward);
      }
    }
    if (key.equals(MiscMethods.gems)) {
      if (!aUser.updateRelativeGemsNaive(value)) {
        log.error("unexpected error: could not give silver bonus of " + value + " to user " + aUser);
        return false;
      } else {// gave user gold
        writeToUserCurrencyHistory(aUser, key, previousGold, currentDayReward);
      }
    }
    equipIdRewardedList.add(equipId);
    return true;
  }

  private void writeToUserDailyBonusRewardHistory(User aUser, Map<String, Integer> rewardForUser,
      int nthConsecutiveDay, Timestamp now, int equipIdRewarded) {
//    int userId = aUser.getId();
//    int currencyRewarded = ControllerConstants.NOT_SET;
//    boolean isCoins = false;
//    int boosterPackIdRewarded = ControllerConstants.NOT_SET;
//
//    String boosterPackId = MiscMethods.boosterPackId;
//    String silver = MiscMethods.silver;
//    String gold = MiscMethods.gold;
//    if (rewardForUser.containsKey(boosterPackId)) {
//      boosterPackIdRewarded = rewardForUser.get(boosterPackId);
//    }
//    if (rewardForUser.containsKey(silver)) {
//      currencyRewarded = rewardForUser.get(silver);
//      isCoins = true;
//    }
//    if (rewardForUser.containsKey(gold)) {
//      currencyRewarded = rewardForUser.get(gold);
//    }
//    int numInserted = InsertUtils.get().insertIntoUserDailyRewardHistory(userId, currencyRewarded,
//        isCoins, boosterPackIdRewarded, equipIdRewarded, nthConsecutiveDay, now);
//    if (1 != numInserted) {
//      log.error("unexpected error: could not record that user got a reward for this day: " + now);
//    }
  }

//  private void setDailyBonusStuff(Builder resBuilder, User aUser, boolean rewardUser,
//      DailyBonusReward rewardForUser) {
//    // log.info("rewardUser=" + rewardUser + "rewardForUser=" +
//    // rewardForUser + "user=" + aUser);
//
//    int userId = aUser.getId();
//    // there should be a reward inserted if things saved sans a hitch
//    UserDailyBonusRewardHistory udbrh = UserDailyBonusRewardHistoryRetrieveUtils
//        .getLastDailyRewardAwardedForUserId(userId);
//
//    if (null == udbrh || null == rewardForUser) {
//      log.error("unexpected error: no daily bonus reward history exists for user=" + aUser);
//      return;
//    }
//    int consecutiveDaysPlayed = udbrh.getNthConsecutiveDay();
//
//    DailyBonusInfo.Builder dbib = DailyBonusInfo.newBuilder();
//    if (5 == consecutiveDaysPlayed) {
//      // user just got an equip
//      int boosterPackId = udbrh.getBoosterPackIdRewarded();
//      BoosterPack bp = BoosterPackRetrieveUtils.getBoosterPackForBoosterPackId(boosterPackId);
//      Map<Integer, BoosterItem> biMap = BoosterItemRetrieveUtils
//          .getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);
//      Collection<BoosterItem> biList = biMap.values();
//      BoosterPackProto aBoosterPackProto = CreateInfoProtoUtils.createBoosterPackProto(bp, biList);
//      dbib.setBoosterPack(aBoosterPackProto);
//
//      // log.info("setting 5th consecutive day reward");
//      int equipId = udbrh.getEquipIdRewarded();
//      dbib.setEquipId(equipId);
//    }
//    if (4 >= consecutiveDaysPlayed) {
//      // log.info("setting 4th consecutive day reward");
//      dbib.setDayFourCoins(rewardForUser.getDayFourCoins());
//    }
//    if (3 >= consecutiveDaysPlayed) {
//      // log.info("setting 3rd consecutive day reward");
//      dbib.setDayThreeDiamonds(rewardForUser.getDayThreeDiamonds());
//    }
//    if (2 >= consecutiveDaysPlayed) {
//      // log.info("setting 2nd consecutive day reward");
//      dbib.setDayTwoCoins(rewardForUser.getDayTwoCoins());
//    }
//    if (1 == consecutiveDaysPlayed) {
//      // log.info("setting first consecutive day reward");
//      dbib.setDayOneCoins(rewardForUser.getDayOneCoins());
//    }
//    // log.info("nth consecutive day=" + consecutiveDaysPlayed);
//    Date dateAwarded = udbrh.getDateAwarded();
//    long dateAwardedMillis = dateAwarded.getTime();
//    dbib.setTimeAwarded(dateAwardedMillis);
//    dbib.setNumConsecutiveDaysPlayed(consecutiveDaysPlayed);
//    resBuilder.setDailyBonusInfo(dbib.build());
//  }

  private void syncApsalaridLastloginConsecutivedaysloggedinResetBadges(User user, String apsalarId,
      Timestamp loginTime, int newNumConsecutiveDaysLoggedIn) {
    if (user.getApsalarId() != null && apsalarId == null) {
      apsalarId = user.getApsalarId();
    }
    if (!user.updateAbsoluteApsalaridLastloginBadgesNumConsecutiveDaysLoggedIn(apsalarId, loginTime, 0,
        newNumConsecutiveDaysLoggedIn)) {
      log.error("problem with updating apsalar id to " + apsalarId + ", last login to " + loginTime
          + ", and badge count to 0 for " + user + " and newNumConsecutiveDaysLoggedIn is "
          + newNumConsecutiveDaysLoggedIn);
    }
    if (!InsertUtils.get().insertLastLoginLastLogoutToUserSessions(user.getId(), loginTime, null)) {
      log.error("problem with inserting last login time for user " + user + ", loginTime=" + loginTime);
    }

    if (user.getNumBadges() != 0) {
      if (user.getDeviceToken() != null) {
        /*
         * handled locally?
         */
        // ApnsServiceBuilder builder =
        // APNS.newService().withCert(APNSProperties.PATH_TO_CERT,
        // APNSProperties.CERT_PASSWORD);
        // if (Globals.IS_SANDBOX()) {
        // builder.withSandboxDestination();
        // }
        // ApnsService service = builder.build();
        // service.push(newDeviceToken,
        // APNS.newPayload().badge(0).build());
        // service.stop();
      }
    }
  }

  private void setAllies(Builder resBuilder, User user) {
//    boolean realPlayersOnly = false;
//    boolean fakePlayersOnly = false;
//    boolean offlinePlayersOnly = false; //does not include fake players
//    boolean inactiveShield = false;
//    List<Integer> forbiddenUserIds = new ArrayList<Integer>();
//    forbiddenUserIds.add(user.getId());
//
//    List<User> allies = RetrieveUtils.userRetrieveUtils().getUsers(
//        ControllerConstants.STARTUP__APPROX_NUM_ALLIES_TO_SEND, user.getLevel(),
//        user.getId(), false, realPlayersOnly,
//        fakePlayersOnly, offlinePlayersOnly,
//        inactiveShield, forbiddenUserIds);
//    if (allies != null && allies.size() > 0) {
//      for (User ally : allies) {
//        resBuilder.addAllies(CreateInfoProtoUtils.createMinimumUserProtoWithLevelFromUser(ally));
//      }
//    }
  }

//  private int getNumTasksCompleteForUserCity(User user, City city,
//      Map<Integer, Integer> taskIdToNumTimesActedInRank) {
//    List<Task> tasks = TaskRetrieveUtils.getAllTasksForCityId(city.getId());
//    int numCompletedTasks = 0;
//    if (tasks != null) {
//      for (Task t : tasks) {
//        if (taskIdToNumTimesActedInRank.containsKey(t.getId())
//            && taskIdToNumTimesActedInRank.get(t.getId()) >= t.getNumForCompletion()) {
//          numCompletedTasks++;
//        }
//      }
//    }
//    return numCompletedTasks;
//  }

  private void setConstants(Builder startupBuilder, StartupStatus startupStatus) {
    startupBuilder.setStartupConstants(MiscMethods.createStartupConstantsProto());
    if (startupStatus == StartupStatus.USER_NOT_IN_DB) {
      setTutorialConstants(startupBuilder);
    }
  }

  private void setTutorialConstants(Builder resBuilder) {
//    Map<Integer, Equipment> equipmentIdsToEquipment = EquipmentRetrieveUtils.getEquipmentIdsToEquipment();
//
//    UserType aGoodType = UserType.GOOD_ARCHER;
//    UserType aBadType = UserType.BAD_ARCHER;
//
//    Task task = TaskRetrieveUtils.getTaskForTaskId(ControllerConstants.TUTORIAL__FIRST_TASK_ID);
////    task.setPotentialLootEquipIds(new ArrayList<Integer>());
//    
//    FullTaskProto ftpGood = CreateInfoProtoUtils.createFullTaskProtoFromTask(aGoodType, task);
//    FullTaskProto ftpBad = CreateInfoProtoUtils.createFullTaskProtoFromTask(aBadType, task);
//
//    task = TaskRetrieveUtils.getTaskForTaskId(ControllerConstants.TUTORIAL__FAKE_QUEST_TASK_ID);
////    task.setPotentialLootEquipIds(new ArrayList<Integer>());
//    FullTaskProto questFtpGood = CreateInfoProtoUtils.createFullTaskProtoFromTask(aGoodType, task);
//    FullTaskProto questFtpBad = CreateInfoProtoUtils.createFullTaskProtoFromTask(aBadType, task);
//
//    Dialogue goodAcceptDialogue = MiscMethods
//        .createDialogue(ControllerConstants.TUTORIAL__FAKE_QUEST_GOOD_ACCEPT_DIALOGUE);
//    Dialogue badAcceptDialogue = MiscMethods
//        .createDialogue(ControllerConstants.TUTORIAL__FAKE_QUEST_BAD_ACCEPT_DIALOGUE);
//
//    FullTutorialQuestProto tqbp = FullTutorialQuestProto
//        .newBuilder()
//        .setGoodName(ControllerConstants.TUTORIAL__FAKE_QUEST_GOOD_NAME)
//        .setBadName(ControllerConstants.TUTORIAL__FAKE_QUEST_BAD_NAME)
//        .setGoodDescription(ControllerConstants.TUTORIAL__FAKE_QUEST_GOOD_DESCRIPTION)
//        .setBadDescription(ControllerConstants.TUTORIAL__FAKE_QUEST_BAD_DESCRIPTION)
//        .setGoodDoneResponse(ControllerConstants.TUTORIAL__FAKE_QUEST_GOOD_DONE_RESPONSE)
//        .setBadDoneResponse(ControllerConstants.TUTORIAL__FAKE_QUEST_BAD_DONE_RESPONSE)
//        .setGoodAcceptDialogue(
//            CreateInfoProtoUtils.createDialogueProtoFromDialogue(goodAcceptDialogue))
//            .setBadAcceptDialogue(CreateInfoProtoUtils.createDialogueProtoFromDialogue(badAcceptDialogue))
//            .setAssetNumWithinCity(ControllerConstants.TUTORIAL__FAKE_QUEST_ASSET_NUM_WITHIN_CITY)
//            .setCoinsGained(ControllerConstants.TUTORIAL__FAKE_QUEST_COINS_GAINED)
//            .setExpGained(ControllerConstants.TUTORIAL__FAKE_QUEST_EXP_GAINED)
//            .setTaskGood(questFtpGood)
//            .setTaskBad(questFtpBad)
////            .setTaskCompleteCoinGain(MiscMethods.calculateCoinsGainedFromTutorialTask(task))
//            .setEquipReward(
//                CreateInfoProtoUtils.createFullEquipProtoFromEquip(equipmentIdsToEquipment
//                    .get(ControllerConstants.TUTORIAL__FAKE_QUEST_AMULET_LOOT_EQUIP_ID))).build();
//
//    PlayerWallPost pwp = new PlayerWallPost(-1,
//        ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL, -1, new Date(),
//        ControllerConstants.USER_CREATE__FIRST_WALL_POST_TEXT);
//    User poster = RetrieveUtils.userRetrieveUtils().getUserById(
//        ControllerConstants.USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL);
//
//    String name = "";
//    int syllablesInName = (Math.random() < .5) ? syllablesInName1 : syllablesInName2;
//    name = nameGeneratorElven.compose(syllablesInName);
//
//
//    TutorialConstants.Builder builder = TutorialConstants
//    		.newBuilder();
//    builder.setInitEnergy(ControllerConstants.TUTORIAL__INIT_ENERGY);
//    builder.setInitStamina(ControllerConstants.TUTORIAL__INIT_STAMINA)
//    .setInitHealth(ControllerConstants.TUTORIAL__INIT_HEALTH);
//    builder.setStructToBuild(ControllerConstants.TUTORIAL__FIRST_STRUCT_TO_BUILD);
//    builder.setDiamondCostToInstabuildFirstStruct(ControllerConstants.TUTORIAL__DIAMOND_COST_TO_INSTABUILD_FIRST_STRUCT);
//    builder.setArcherInitAttack(ControllerConstants.TUTORIAL__ARCHER_INIT_ATTACK);
//    builder.setArcherInitDefense(ControllerConstants.TUTORIAL__ARCHER_INIT_DEFENSE);
//    FullEquipProto fep = CreateInfoProtoUtils.createFullEquipProtoFromEquip(
//    		equipmentIdsToEquipment.get(ControllerConstants.TUTORIAL__ARCHER_INIT_WEAPON_ID));
//    builder.setArcherInitWeapon(fep);
//    builder.setArcherInitArmor(
//    		CreateInfoProtoUtils.createFullEquipProtoFromEquip(equipmentIdsToEquipment
//    				.get(ControllerConstants.TUTORIAL__ARCHER_INIT_ARMOR_ID)));
//    builder.setMageInitAttack(ControllerConstants.TUTORIAL__MAGE_INIT_ATTACK);
//    builder.setMageInitDefense(ControllerConstants.TUTORIAL__MAGE_INIT_DEFENSE);
//    builder.setMageInitWeapon(
//    		CreateInfoProtoUtils.createFullEquipProtoFromEquip(equipmentIdsToEquipment
//    				.get(ControllerConstants.TUTORIAL__MAGE_INIT_WEAPON_ID)));
//    builder.setMageInitArmor(
//    		CreateInfoProtoUtils.createFullEquipProtoFromEquip(equipmentIdsToEquipment
//    				.get(ControllerConstants.TUTORIAL__MAGE_INIT_ARMOR_ID)));
//    builder.setWarriorInitAttack(ControllerConstants.TUTORIAL__WARRIOR_INIT_ATTACK);
//    builder.setWarriorInitDefense(ControllerConstants.TUTORIAL__WARRIOR_INIT_DEFENSE);
//    builder.setWarriorInitWeapon(
//    		CreateInfoProtoUtils.createFullEquipProtoFromEquip(equipmentIdsToEquipment
//    				.get(ControllerConstants.TUTORIAL__WARRIOR_INIT_WEAPON_ID)));
//    builder.setWarriorInitArmor(
//    		CreateInfoProtoUtils.createFullEquipProtoFromEquip(equipmentIdsToEquipment
//    				.get(ControllerConstants.TUTORIAL__WARRIOR_INIT_ARMOR_ID)));
//    builder.setTutorialQuest(tqbp);
//    builder.setMinNameLength(ControllerConstants.USER_CREATE__MIN_NAME_LENGTH);
//    builder.setTutorialQuest(tqbp);
//    builder.setMaxNameLength(ControllerConstants.USER_CREATE__MAX_NAME_LENGTH);
//    builder.setCoinRewardForBeingReferred(
//    		ControllerConstants.USER_CREATE__COIN_REWARD_FOR_BEING_REFERRED);
//    builder.setInitDiamonds(Globals.INITIAL_DIAMONDS());
//    builder.setInitCoins(ControllerConstants.TUTORIAL__INIT_COINS);
//    builder.setFirstBattleCoinGain(ControllerConstants.TUTORIAL__FIRST_BATTLE_COIN_GAIN);
//    builder.setFirstBattleExpGain(ControllerConstants.TUTORIAL__FIRST_BATTLE_EXP_GAIN);
//    builder.setFirstTaskGood(ftpGood);
//    builder.setFirstTaskBad(ftpBad);
//    builder.setExpRequiredForLevelTwo(
//    		LevelsRequiredExperienceRetrieveUtils.getLevelsToRequiredExperienceForLevels().get(2));
//    builder.setExpRequiredForLevelThree(
//    		LevelsRequiredExperienceRetrieveUtils.getLevelsToRequiredExperienceForLevels().get(3));
//    builder.setFirstWallPost(
//    		CreateInfoProtoUtils.createPlayerWallPostProtoFromPlayerWallPost(pwp, poster));
//    builder.setDefaultName(name);
//    builder.setCostToSpeedUpForge(ControllerConstants.TUTORIAL__COST_TO_SPEED_UP_FORGE);
//
//
//    List<NeutralCityElement> neutralCityElements = NeutralCityElementsRetrieveUtils
//    		.getNeutralCityElementsForCity(ControllerConstants.TUTORIAL__FIRST_NEUTRAL_CITY_ID);
//    if (neutralCityElements != null) {
//    	for (NeutralCityElement nce : neutralCityElements) {
//        builder.addFirstCityElementsForGood(CreateInfoProtoUtils
//            .createNeutralCityElementProtoFromNeutralCityElement(nce, aGoodType));
//        builder.addFirstCityElementsForBad(CreateInfoProtoUtils
//            .createNeutralCityElementProtoFromNeutralCityElement(nce, aBadType));
//      }
//    }
//
//    Map<Integer, Structure> structIdsToStructs = StructureRetrieveUtils.getStructIdsToStructs();
//    for (Structure struct : structIdsToStructs.values()) {
//      if (struct != null) {
//        FullStructureProto fsp = CreateInfoProtoUtils.createFullStructureProtoFromStructure(struct);
//        builder.addCarpenterStructs(fsp);
//        if (struct.getMinLevel() == 2) {
//          builder.addNewlyAvailableStructsAfterLevelup(CreateInfoProtoUtils
//              .createFullStructureProtoFromStructure(struct));
//        }
//      }
//    }
//
//    List<City> availCities = MiscMethods
//        .getCitiesAvailableForUserLevel(ControllerConstants.USER_CREATE__START_LEVEL);
//    for (City city : availCities) {
//      if (city.getMinLevel() == ControllerConstants.USER_CREATE__START_LEVEL) {
//        builder.addCitiesNewlyAvailableToUserAfterLevelup(CreateInfoProtoUtils
//            .createFullCityProtoFromCity(city));
//      }
//    }
//
//    Map<Integer, Equipment> equipIdToEquips = EquipmentRetrieveUtils.getEquipmentIdsToEquipment();
//    if (equipIdToEquips != null) {
//      for (Equipment e : equipIdToEquips.values()) {
//        if (e != null && e.getMinLevel() == ControllerConstants.USER_CREATE__START_LEVEL
//            && (e.getRarity() == Rarity.EPIC || e.getRarity() == Rarity.LEGENDARY)) {
//          builder.addNewlyEquippableEpicsAndLegendariesForAllClassesAfterLevelup(CreateInfoProtoUtils
//              .createFullEquipProtoFromEquip(e));
//        }
//      }
//    }
//    resBuilder.setTutorialConstants(builder.build());
  }

  //TODO: FIX THIS
  public void writeToUserCurrencyHistory(User aUser, String goldSilver, int previousMoney,
      Map<String, Integer> goldSilverChange) {
    String cash = MiscMethods.cash;
    String gems = MiscMethods.gems;

//    Timestamp date = new Timestamp((new Date()).getTime());
//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String reasonForChange = ControllerConstants.UCHRFC__STARTUP_DAILY_BONUS;
//
//    if (goldSilver.equals(cash)) {
//      previousGoldSilver.put(cash, previousMoney);
//      reasonsForChanges.put(cash, reasonForChange);
//    } else {
//      previousGoldSilver.put(gems, previousMoney);
//      reasonsForChanges.put(gems, reasonForChange);
//    }
//
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, goldSilverChange,
//        previousGoldSilver, reasonsForChanges);
  }

}
