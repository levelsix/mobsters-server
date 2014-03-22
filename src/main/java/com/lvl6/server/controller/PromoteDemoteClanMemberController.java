package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PromoteDemoteClanMemberRequestEvent;
import com.lvl6.events.response.PromoteDemoteClanMemberResponseEvent;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberRequestProto;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto.Builder;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto.PromoteDemoteClanMemberStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class PromoteDemoteClanMemberController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;
  public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public PromoteDemoteClanMemberController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new PromoteDemoteClanMemberRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_PROMOTE_DEMOTE_CLAN_MEMBER_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    PromoteDemoteClanMemberRequestProto reqProto = ((PromoteDemoteClanMemberRequestEvent)event).getPromoteDemoteClanMemberRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int victimId = reqProto.getVictimId();
    UserClanStatus newUserClanStatus = reqProto.getUserClanStatus();
    List<Integer> userIds = new ArrayList<Integer>();
    userIds.add(userId);
    userIds.add(victimId);
    
    PromoteDemoteClanMemberResponseProto.Builder resBuilder = PromoteDemoteClanMemberResponseProto.newBuilder();
    resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);
    resBuilder.setUserClanStatus(newUserClanStatus);

    int clanId = 0;
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    
    boolean lockedClan = false;
    if (0 != clanId) {
    	lockedClan = getLocker().lockClan(clanId);
    } /*else {
    //MAYBE SHOULD ALSO LOCK THE playerToBootId
    //server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	server.lockPlayers(userId, victimId, this.getClass().getSimpleName());
    }*/
    try {
    	Map<Integer,User> users = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIds);
    	Map<Integer, UserClan> userClans = RetrieveUtils.userClanRetrieveUtils()
    			.getUserClanForUsers(clanId, userIds);

      boolean legitRequest = checkLegitRequest(resBuilder, lockedClan, userId, victimId,
      		newUserClanStatus, users, userClans);

      boolean success = false;
      if (legitRequest) { 
      	User victim = users.get(victimId);
      	UserClan oldInfo = userClans.get(victimId);
      	
      	success = writeChangesToDB(victim, victimId, clanId, oldInfo, newUserClanStatus);
//        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
//        server.writeEvent(resEventUpdate);
      }
      
      if (success) {
        resBuilder.setStatus(PromoteDemoteClanMemberStatus.SUCCESS);
        User victim = users.get(victimId);
        
        MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(victim);
        resBuilder.setVictim(mup);
      }

      //write to promoter
      PromoteDemoteClanMemberResponseEvent resEvent =
      		new PromoteDemoteClanMemberResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setPromoteDemoteClanMemberResponseProto(resBuilder.build()); 
      server.writeEvent(resEvent);
      
      if (success) {
      	//write to victim
      	PromoteDemoteClanMemberResponseEvent resEvent2 =
      			new PromoteDemoteClanMemberResponseEvent(victimId);
      	resEvent2.setPromoteDemoteClanMemberResponseProto(resBuilder.build()); //I think this is supposed to be resEvent2 not resEvent
      	server.writeEvent(resEvent2);
      }

    } catch (Exception e) {
      log.error("exception in PromoteDemoteClanMember processEvent", e);
      try {
    	  resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_OTHER);
    	  PromoteDemoteClanMemberResponseEvent resEvent = new PromoteDemoteClanMemberResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setPromoteDemoteClanMemberResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in PromoteDemoteClanMember processEvent", e);
    	}
    } finally {
    	if (0 != clanId) {
    		getLocker().unlockClan(clanId);
    	} /*else {
    		server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}*/
    }
  }

  private boolean checkLegitRequest(Builder resBuilder, boolean lockedClan, int userId,
  		int victimId, UserClanStatus newUserClanStatus, Map<Integer, User> userIdsToUsers,
  		Map<Integer, UserClan> userIdsToUserClans) {
  	
  	if (!lockedClan) {
  		log.error("couldn't obtain clan lock");
  		return false;
  	}
    if (null == userIdsToUsers || userIdsToUsers.size() != 2 ||
    		null == userIdsToUserClans || userIdsToUserClans.size() != 2) {
      log.error("user or userClan objects do not total 2. users=" + userIdsToUsers +
      		"\t userIdsToUserClans=" + userIdsToUserClans);
      return false;      
    }
    
    //check if users are in the db
    if (!userIdsToUserClans.containsKey(userId) || !userIdsToUsers.containsKey(userId)) {
    	log.error("user promoting or demoting not in clan or db. userId=" + userId +
    			"\t userIdsToUserClans=" + userIdsToUserClans + "\t userIdsToUsers=" +
    			userIdsToUsers);
    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_IN_CLAN);
    	return false;
    }
    if (!userIdsToUserClans.containsKey(victimId) || !userIdsToUsers.containsKey(victimId)) {
    	log.error("user to be promoted or demoted not in clan or db. victim=" + victimId +
    			"\t userIdsToUserClans=" + userIdsToUserClans + "\t userIdsToUsers=" +
    			userIdsToUsers);
    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_IN_CLAN);
    	return false;
    }
    
    //check if user can demote/promote the other one
    UserClan promoterDemoter = userIdsToUserClans.get(userId);
    UserClan victim = userIdsToUserClans.get(victimId);
    
    UserClanStatus first = promoterDemoter.getStatus();
    UserClanStatus second = victim.getStatus();
    if (UserClanStatus.CAPTAIN.equals(first) || 
    		!ClanStuffUtils.firstUserClanStatusAboveSecond(first, second)) {
    	log.error("user not authorized to promote or demote otherUser. clanStatus of user=" +
    			first + "\t clanStatus of other user=" + second);
    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
    	return false;
    }
    if (!ClanStuffUtils.firstUserClanStatusAboveSecond(first, newUserClanStatus)) {
    	log.error("user not authorized to promote or demote otherUser. clanStatus of user=" +
    			first + "\t clanStatus of other user=" + second + "\t newClanStatus of other user=" +
    			newUserClanStatus);
    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
    	return false;
    }
    if (UserClanStatus.REQUESTING.equals(second)) {
    	log.error("user can't promote, demote a non-clan member. UserClan for user=" +
    			promoterDemoter + "\t UserClan for victim=" + victim + "\t users=" + userIdsToUsers);
    	resBuilder.setStatus(PromoteDemoteClanMemberStatus.FAIL_NOT_AUTHORIZED);
    	return false;
    }
    
    return true;
  }

  private boolean writeChangesToDB(User victim, int victimId, int clanId,
  		UserClan oldInfo, UserClanStatus newUserClanStatus) {
  	if (!UpdateUtils.get().updateUserClanStatus(victimId, clanId, newUserClanStatus)) {
      log.error("problem with updating user clan status for user=" + victim +
      		"\t oldInfo=" + oldInfo + "\t newUserClanStatus=" + newUserClanStatus);
      return false;
    }
  	return true;
  }

}
