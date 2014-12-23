package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LeaveClanRequestEvent;
import com.lvl6.events.response.LeaveClanResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.LeaveClanRequestProto;
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto;
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto.LeaveClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.ExitClanAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class LeaveClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;
  
  @Autowired
  protected TimeUtils timeUtil;
  
  @Autowired
  protected ClanRetrieveUtils2 clanRetrieveUtils;
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;
  
  @Autowired
  protected UserClanRetrieveUtils2 userClanRetrieveUtils;
  
  @Autowired
  protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;

  @Autowired
  protected ClanSearch clanSearch;
  
  public LeaveClanController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new LeaveClanRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_LEAVE_CLAN_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    LeaveClanRequestProto reqProto = ((LeaveClanRequestEvent)event).getLeaveClanRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();

    LeaveClanResponseProto.Builder resBuilder = LeaveClanResponseProto.newBuilder();
    resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    String clanId = null;
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanUuid();
    }

    UUID userUuid = null;
    UUID clanUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(userId);
      clanUuid = UUID.fromString(clanId);

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s, clanId=%s",
          userId, clanId), e);
      invalidUuids = true;
    }

    //UUID checks
    if (invalidUuids) {
      resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
      LeaveClanResponseEvent resEvent = new LeaveClanResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setLeaveClanResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }
    
    //maybe should get clan lock instead of locking person
    //but going to modify user, so lock user. however maybe locking is not necessary
    boolean lockedClan = false;
    if (null != clanId) {
    	lockedClan = getLocker().lockClan(clanUuid);
    }/* else {
    	server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
    }*/
    try {
      User user = getUserRetrieveUtils().getUserById(senderProto.getUserUuid());
      Clan clan = getClanRetrieveUtils().getClanWithId(clanId);

      List<String> clanOwnerIdList = new ArrayList<String>();
      List<Integer> clanSizeContainer = new ArrayList<Integer>();
      boolean legitLeave = checkLegitLeave(resBuilder, lockedClan,
    	  user, clan, clanOwnerIdList, clanSizeContainer);
      
      boolean success = false;
      if (legitLeave) {
        String clanOwnerId = clanOwnerIdList.get(0);
        int clanSize = clanSizeContainer.get(0);
      	success = writeChangesToDB(user, clan, clanOwnerId, clanSize);
      }

      LeaveClanResponseEvent resEvent = new LeaveClanResponseEvent(userId);
      resEvent.setTag(event.getTag());
      //only write to user if failed
      if (!success) {
      	resEvent.setLeaveClanResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      	
      } else {
      	//only write to clan if success
        resBuilder.setStatus(LeaveClanStatus.SUCCESS);
        resEvent.setLeaveClanResponseProto(resBuilder.build());
      	server.writeClanEvent(resEvent, clanId);
        //this works for other clan members, but not for the person 
        //who left (they see the message when they join a clan, reenter clan house
        //notifyClan(user, clan);
      }
    } catch (Exception e) {
      log.error("exception in LeaveClan processEvent", e);
      try {
    	  resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
    	  LeaveClanResponseEvent resEvent = new LeaveClanResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setLeaveClanResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in LeaveClan processEvent", e);
    	}
    } finally {
    	if (null != clanUuid && lockedClan) {
    		getLocker().unlockClan(clanUuid);
    	}/* else {
    		server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
    	}*/
    }
  }

  private boolean checkLegitLeave(Builder resBuilder, boolean lockedClan, User user,
  		Clan clan, List<String> clanOwnerIdList, List<Integer> clanSizeContainer) {

  	if (!lockedClan) {
  		log.error("couldn't obtain clan lock");
  		return false;
  	}
    if (user == null || clan == null) {
      log.error("user is null");
      return false;      
    }
    if (user.getClanId() == null || !user.getClanId().equals(clan.getId())) {
      resBuilder.setStatus(LeaveClanStatus.FAIL_NOT_IN_CLAN);
      log.error(String.format(
    	  "user's clan id=%s, clan id=%s",
    	  user.getClanId(), clan.getId()));
      return false;
    }

    //one query to get the leader and the members in a clan
    String clanId = user.getClanId();
    String leaderStatus = UserClanStatus.LEADER.name();
    List<String> statuses = new ArrayList<String>();
    statuses.add(leaderStatus);
    statuses.add(UserClanStatus.JUNIOR_LEADER.name());
    statuses.add(UserClanStatus.CAPTAIN.name());
    statuses.add(UserClanStatus.MEMBER.name());
    Map<String, String> userIdsAndStatuses = userClanRetrieveUtils
    		.getUserIdsToStatuses(clanId, statuses);
    
    String clanOwnerId = getClanOwnerId(leaderStatus, userIdsAndStatuses);

    int userClanMembersInClan = userIdsAndStatuses.size();
    if (clanOwnerId.equals(user.getId())) {
    	if (userClanMembersInClan > 1) {
    		resBuilder.setStatus(LeaveClanStatus.FAIL_OWNER_OF_CLAN_WITH_OTHERS_STILL_IN);
    		String preface = "user is owner and he's not alone in clan,";
    		String preface2 = "can't leave without switching ownership."; 
    		log.error(String.format(
    			"%s %s user clan members are %s", 
    			preface, preface2, userClanMembersInClan));
    		return false;
    	}
    }

    clanOwnerIdList.add(clanOwnerId);
    clanSizeContainer.add(userClanMembersInClan);
    return true;
  }

  private String getClanOwnerId( String leaderStatus, Map<String, String> userIdsAndStatuses )
  {
	  String clanOwnerId = null;
	  if (null != userIdsAndStatuses && !userIdsAndStatuses.isEmpty()) {

		  //find clanOwnerId
		  for (String userId : userIdsAndStatuses.keySet()) {
			  String status = userIdsAndStatuses.get(userId);

			  if (leaderStatus.equals(status)) {
				  clanOwnerId = userId;
			  }
		  }
	  }
	  return clanOwnerId;
  }

  private boolean writeChangesToDB(User user, Clan clan,
	  String clanOwnerId, int clanSize)
  {
    String userId = user.getId();
    String clanId = clan.getId();

    if (userId.equals(clanOwnerId)) {
      List<String> userIds = getUserClanRetrieveUtils().getUserIdsRelatedToClan(clanId);
      deleteClan(clan, userIds, user);
    } else {
      if (!DeleteUtils.get().deleteUserClan(userId, clanId)) {
        log.error(String.format(
        	"problem deleting UserClan. user=%s, clan=%s",
        	user, clan));
        return false;
      }
      if (!user.updateRelativeCoinsAbsoluteClan(0, null)) {
        log.error("problem with making clanid for user null");
        return false;
      }
    }
    Date lastChatPost = clanChatPostRetrieveUtil.getLastChatPost(clanId);

    //need to account for this user leaving clan
    ExitClanAction eca = new ExitClanAction(userId, clanId, clanSize - 1,
    	lastChatPost, timeUtil, UpdateUtils.get(), clanSearch);
    eca.execute();
    
    return true;
  }

  private void deleteClan(Clan clan, List<String> userIds, User user) {
	  if (!user.updateRelativeCoinsAbsoluteClan(0, null)) {
		  log.error(String.format(
			  "problem marking clan id null for users with ids in %s", userIds));
		  return;
	  }

	  if (!DeleteUtils.get().deleteUserClanDataRelatedToClanId(clan.getId(), userIds.size())) {
		  log.error(String.format(
			  "problem with deleting user clan data for clan with id %s",
			  clan.getId()));
	  } else {
		  if (!DeleteUtils.get().deleteClanWithClanId(clan.getId())) {
			  log.error(String.format(
				  "problem with deleting clan with id %s",
				  clan.getId()));
		  }
	  }

  }
  /*
  private void notifyClan(User aUser, Clan aClan) {
    int clanId = aClan.getId();
    
    int level = aUser.getLevel();
    String deserter = aUser.getName();
    Notification aNote = new Notification();
    
    aNote.setAsUserLeftClan(level, deserter);
    MiscMethods.writeClanApnsNotification(aNote, server, clanId);
  }*/
  
  public Locker getLocker() {
	  return locker;
  }
  public void setLocker(Locker locker) {
	  this.locker = locker;
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

  public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
    return userClanRetrieveUtils;
  }

  public void setUserClanRetrieveUtils(
      UserClanRetrieveUtils2 userClanRetrieveUtils) {
	  this.userClanRetrieveUtils = userClanRetrieveUtils;
  }

  public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtil()
  {
	  return clanChatPostRetrieveUtil;
  }

  public void setClanChatPostRetrieveUtil( ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil )
  {
	  this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
  }

  public TimeUtils getTimeUtil()
  {
	  return timeUtil;
  }

  public void setTimeUtil( TimeUtils timeUtil )
  {
	  this.timeUtil = timeUtil;
  }

  public ClanSearch getClanSearch()
  {
	  return clanSearch;
  }

  public void setClanSearch( ClanSearch clanSearch )
  {
	  this.clanSearch = clanSearch;
  }
  
}
