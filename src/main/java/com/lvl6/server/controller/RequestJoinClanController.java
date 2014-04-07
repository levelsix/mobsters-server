package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RequestJoinClanRequestEvent;
import com.lvl6.events.response.RequestJoinClanResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.misc.MiscMethods;
import com.lvl6.misc.Notification;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.PersistentClanEventUserInfoProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.RequestJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto.RequestJoinClanStatus;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class RequestJoinClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;
  
  @Autowired
  protected PvpLeagueForUserRetrieveUtil pvpLeagueForUserRetrieveUtil;
  
  public RequestJoinClanController() {
	  numAllocatedThreads = 4;
  }


  @Override
  public RequestEvent createRequestEvent() {
    return new RequestJoinClanRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_REQUEST_JOIN_CLAN_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RequestJoinClanRequestProto reqProto = ((RequestJoinClanRequestEvent)event).getRequestJoinClanRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int clanId = reqProto.getClanId();
    int userId = senderProto.getUserId();

    RequestJoinClanResponseProto.Builder resBuilder = RequestJoinClanResponseProto.newBuilder();
    resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);
    resBuilder.setClanId(clanId);

    boolean lockedClan = false;
    if (0 != clanId) {
    	lockedClan = getLocker().lockClan(clanId);
    }
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      Clan clan = ClanRetrieveUtils.getClanWithId(clanId);
      boolean requestToJoinRequired = false; //to be set if request is legit

      List<Integer> clanSizeList = new ArrayList<Integer>();
      boolean legitRequest = checkLegitRequest(resBuilder, lockedClan, user, clan,
    		  clanSizeList);
      
      
      boolean successful = false;
      if (legitRequest) {
    	  requestToJoinRequired = clan.isRequestToJoinRequired();
    	  int battlesWon = getPvpLeagueForUserRetrieveUtil()
    			  .getPvpBattlesWonForUser(userId);
    	  
        //setting minimum user proto for clans based on clan join type
        if (requestToJoinRequired) {
        	//clan raid contribution stuff
          MinimumUserProtoForClans mupfc = CreateInfoProtoUtils
        		  .createMinimumUserProtoForClans(user,
        				  UserClanStatus.REQUESTING, 0F, battlesWon);
          resBuilder.setRequester(mupfc);
        } else {
        	//clan raid contribution stuff
          MinimumUserProtoForClans mupfc = CreateInfoProtoUtils
        		  .createMinimumUserProtoForClans(
        				  user, UserClanStatus.MEMBER, 0F, battlesWon);
          resBuilder.setRequester(mupfc);
        }
        successful = writeChangesToDB(resBuilder, user, clan);
      }
      
      if (successful) {
      	setResponseBuilderStuff(resBuilder, clan, clanSizeList);
        sendClanRaidStuff(resBuilder, clan, user);
      }
      RequestJoinClanResponseEvent resEvent = new RequestJoinClanResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setRequestJoinClanResponseProto(resBuilder.build());
      /* I think I meant write to the clan leader if leader is not on
       
      //in case user is not online write an apns
      server.writeAPNSNotificationOrEvent(resEvent);
      //server.writeEvent(resEvent);
       */
      server.writeEvent(resEvent);
      
      if (successful) {
      	List<Integer> userIds = new ArrayList<Integer>();
      	userIds.add(userId);
      	//get user's current monster team
      	Map<Integer, List<MonsterForUser>> userIdToTeam = RetrieveUtils
      			.monsterForUserRetrieveUtils().getUserIdsToMonsterTeamForUserIds(userIds); 
      	UserCurrentMonsterTeamProto curTeamProto = CreateInfoProtoUtils
      			.createUserCurrentMonsterTeamProto(userId, userIdToTeam.get(userId));
      	resBuilder.setRequesterMonsters(curTeamProto);
      	
      	
      	resBuilder.clearEventDetails(); //could just get rid of this line
      	resBuilder.clearClanUsersDetails(); //could just get rid of this line
      	resEvent.setRequestJoinClanResponseProto(resBuilder.build());
        server.writeClanEvent(resEvent, clan.getId());
        
        //null PvpLeagueFromUser means will pull from hazelcast instead
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods
        		.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        notifyClan(user, clan, requestToJoinRequired); //write to clan leader or clan
      }
    } catch (Exception e) {
    	log.error("exception in RequestJoinClan processEvent", e);
      try {
    	  resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
    	  RequestJoinClanResponseEvent resEvent = new RequestJoinClanResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setRequestJoinClanResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in RequestJoinClan processEvent", e);
    	}
    } finally {
    	if (0 != clanId) {
    		getLocker().unlockClan(clanId);
    	}
    }
  }

  private boolean checkLegitRequest(Builder resBuilder, boolean lockedClan, User user,
  		Clan clan, List<Integer> clanSizeList) {
  	
  	if (!lockedClan) {
  		log.error("couldn't obtain clan lock");
  		return false;
  	}
    if (user == null || clan == null) {
      resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
      log.error("user is " + user + ", clan is " + clan);
      return false;      
    }
    int clanId = user.getClanId();
    if (clanId > 0) {
      resBuilder.setStatus(RequestJoinClanStatus.FAIL_ALREADY_IN_CLAN);
      log.error("user is already in clan with id " + clanId);
      return false;      
    }
    
    //user does not have clan id, so use the clan's id
    clanId = clan.getId();
//    if (clan.isGood() != MiscMethods.checkIfGoodSide(user.getType())) {
//      resBuilder.setStatus(RequestJoinClanStatus.WRONG_SIDE);
//      log.error("user is good " + user.getType() + ", user type is good " + user.getType());
//      return false;      
//    }
    UserClan uc = RetrieveUtils.userClanRetrieveUtils().getSpecificUserClan(user.getId(), clanId);
    if (uc != null) {
      resBuilder.setStatus(RequestJoinClanStatus.FAIL_REQUEST_ALREADY_FILED);
      log.error("user clan already exists for this: " + uc);
      return false;      
    }

    if (ControllerConstants.CLAN__ALLIANCE_CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT == clanId ||
        ControllerConstants.CLAN__LEGION_CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT == clanId) {
      return true;
    }
    
    //can request as much as desired
    if (clan.isRequestToJoinRequired()) {
    	return true;
    }
    
    //check out the size of the clan since user can just join
    
    List<Integer> clanIdList = Collections.singletonList(clanId);
    List<String> statuses = new ArrayList<String>();
  	statuses.add(UserClanStatus.LEADER.name());
  	statuses.add(UserClanStatus.JUNIOR_LEADER.name());
  	statuses.add(UserClanStatus.CAPTAIN.name());
  	statuses.add(UserClanStatus.MEMBER.name());
  	Map<Integer, Integer> clanIdToSize = RetrieveUtils.userClanRetrieveUtils()
  			.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);
  	
  	int size = clanIdToSize.get(clanId);
    int maxSize = ControllerConstants.CLAN__MAX_NUM_MEMBERS;
    if (size >= maxSize) {
      resBuilder.setStatus(RequestJoinClanStatus.FAIL_CLAN_IS_FULL);
      log.warn("user error: trying to join full clan with id " + clanId +
      		" cur size=" + maxSize);
      return false;      
    }
    clanSizeList.add(size);
    //resBuilder.setStatus(RequestJoinClanStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDB(Builder resBuilder, User user, Clan clan) {
    //clan can be open, or user needs to send a request to join the clan
    boolean requestToJoinRequired = clan.isRequestToJoinRequired();
    int userId = user.getId();
    int clanId = clan.getId(); //user.getClanId(); //this is null still...
    String userClanStatus;
    if (requestToJoinRequired) {
      userClanStatus = UserClanStatus.REQUESTING.name();
      resBuilder.setStatus(RequestJoinClanStatus.SUCCESS_REQUEST);
    } else {
      userClanStatus = UserClanStatus.MEMBER.name();
      resBuilder.setStatus(RequestJoinClanStatus.SUCCESS_JOIN);
    }
    
    if (!InsertUtils.get().insertUserClan(userId, clanId, userClanStatus, new Timestamp(new Date().getTime()))) {
      log.error("unexpected error: problem with inserting user clan data for user " + user + ", and clan id " + clanId);
      resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
      return false;
    } 
    
    boolean deleteUserClanInserted = false;
    //update user to reflect he joined clan if the clan does not require a request to join
    if (!requestToJoinRequired) {
      if (!user.updateRelativeCoinsAbsoluteClan(0, clanId)) {
        //could not change clan_id for user
        log.error("unexpected error: could not change clan id for requester " + user + " to " + clanId 
            + ". Deleting user clan that was just created.");
        deleteUserClanInserted = true;
      } else {
        //successfully changed clan_id in current user
        //get rid of all other join clan requests
        //don't know if this next line will always work...
        DeleteUtils.get().deleteUserClansForUserExceptSpecificClan(userId, clanId);
      }
    }
    
    boolean successful = true;
    //in case things above didn't work out
    if (deleteUserClanInserted) {
      if (!DeleteUtils.get().deleteUserClan(userId, clanId)){
        log.error("unexpected error: could not delete user clan inserted.");
      }
      resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
      successful = false;
    }
    return successful;
  }
  
  private void notifyClan(User aUser, Clan aClan, boolean requestToJoinRequired) {
  	int clanId = aUser.getClanId();
    List<String> statuses = Collections.singletonList(UserClanStatus.LEADER.name());
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatuses(clanId, statuses);
    //should just be one id
    int clanOwnerId = 0;
    if (null != userIds && !userIds.isEmpty()) {
    	clanOwnerId = userIds.get(0);
    }
    
    int level = aUser.getLevel();
    String requester = aUser.getName();
    Notification aNote = new Notification();
    
    if (requestToJoinRequired) {
      //notify leader someone requested to join clan
      aNote.setAsUserRequestedToJoinClan(level, requester);
    } else {
      //notify whole clan someone joined the clan <- too annoying, just leader
      //TODO: Maybe exclude the guy who joined from receiving the notification?
      aNote.setAsUserJoinedClan(level, requester);
    }
    MiscMethods.writeNotificationToUser(aNote, server, clanOwnerId);
    
//    GeneralNotificationResponseProto.Builder notificationProto =
//        aNote.generateNotificationBuilder();
//    GeneralNotificationResponseEvent aNotification =
//        new GeneralNotificationResponseEvent(clanOwnerId);
//    aNotification.setGeneralNotificationResponseProto(notificationProto.build());
//    
//    server.writeAPNSNotificationOrEvent(aNotification);
  }
  
  private void setResponseBuilderStuff(Builder resBuilder, Clan clan,
		  List<Integer> clanSizeList) {
  	
    resBuilder.setMinClan(CreateInfoProtoUtils.createMinimumClanProtoFromClan(clan));
    int size = clanSizeList.get(0);
    resBuilder.setFullClan(CreateInfoProtoUtils.createFullClanProtoWithClanSize(clan, size));
  }
  
  private void sendClanRaidStuff(Builder resBuilder, Clan clan, User user) {
  	if (!RequestJoinClanStatus.SUCCESS_JOIN.equals(resBuilder.getStatus())) {
  		return;
  	}
  	
  	int clanId = clan.getId();
  	ClanEventPersistentForClan cepfc = ClanEventPersistentForClanRetrieveUtils
  			.getPersistentEventForClanId(clanId);
  	
  	//send to the user the current clan raid details for the clan
  	if (null != cepfc) {
  		PersistentClanEventClanInfoProto updatedEventDetails = CreateInfoProtoUtils
  				.createPersistentClanEventClanInfoProto(cepfc);
  		resBuilder.setEventDetails(updatedEventDetails);
  	}
  	
  	Map<Integer, ClanEventPersistentForUser> userIdToCepfu =
  			ClanEventPersistentForUserRetrieveUtils.getPersistentEventUserInfoForClanId(clanId);
  	
  	
  	//send to the user the current clan raid details for all the users
  	if (!userIdToCepfu.isEmpty()) {
    	//whenever server has this information send it to the clients
    	List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIdsInClanRaid(userIdToCepfu);
    	
    	Map<Long, MonsterForUser> idsToUserMonsters = RetrieveUtils.monsterForUserRetrieveUtils()
    			.getSpecificUserMonsters(userMonsterIds);
    	
    	for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
    		PersistentClanEventUserInfoProto pceuip = CreateInfoProtoUtils
    				.createPersistentClanEventUserInfoProto(cepfu, idsToUserMonsters, null);
    		resBuilder.addClanUsersDetails(pceuip);
    	}

  	}
  }
  
  public Locker getLocker() {
	  return locker;
  }
  public void setLocker(Locker locker) {
	  this.locker = locker;
  }


  public PvpLeagueForUserRetrieveUtil getPvpLeagueForUserRetrieveUtil() {
	  return pvpLeagueForUserRetrieveUtil;
  }
  public void setPvpLeagueForUserRetrieveUtil(
		  PvpLeagueForUserRetrieveUtil pvpLeagueForUserRetrieveUtil) {
	  this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
  }
  
}
