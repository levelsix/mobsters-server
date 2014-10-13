package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

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
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class LeaveClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;

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
    int userId = senderProto.getUserId();

    LeaveClanResponseProto.Builder resBuilder = LeaveClanResponseProto.newBuilder();
    resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    int clanId = 0;
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    
    //maybe should get clan lock instead of locking person
    //but going to modify user, so lock user. however maybe locking is not necessary
    boolean lockedClan = false;
    if (0 != clanId) {
    	lockedClan = getLocker().lockClan(clanId);
    }/* else {
    	server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }*/
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      Clan clan = ClanRetrieveUtils.getClanWithId(clanId);

      List<Integer> clanOwnerIdList = new ArrayList<Integer>();
      boolean legitLeave = checkLegitLeave(resBuilder, lockedClan, user, clan, clanOwnerIdList);
      
      boolean success = false;
      if (legitLeave) {
      	int clanOwnerId = clanOwnerIdList.get(0);
      	success = writeChangesToDB(user, clan, clanOwnerId);
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
    	if (0 != clanId && lockedClan) {
    		getLocker().unlockClan(clanId);
    	}/* else {
    		server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}*/
    }
  }

  private boolean checkLegitLeave(Builder resBuilder, boolean lockedClan, User user,
  		Clan clan, List<Integer> clanOwnerIdList) {

  	if (!lockedClan) {
  		log.error("couldn't obtain clan lock");
  		return false;
  	}
    if (user == null || clan == null) {
      log.error("user is null");
      return false;      
    }
    if (user.getClanId() != clan.getId()) {
      resBuilder.setStatus(LeaveClanStatus.FAIL_NOT_IN_CLAN);
      log.error("user's clan id is " + user.getClanId() + ", clan id is " + clan.getId());
      return false;
    }

    int clanId = user.getClanId();
    List<String> statuses = new ArrayList<String>();
    statuses.add(UserClanStatus.LEADER.name());
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatuses(clanId, statuses);
    //should just be one id
    int clanOwnerId = 0;
    if (null != userIds && !userIds.isEmpty()) {
    	clanOwnerId = userIds.get(0);
    }
    
    if (clanOwnerId == user.getId()) {
    	List<Integer> clanIdList = Collections.singletonList(clanId);
    	//add in the other "in clan" statuses with the existing leader status
    	statuses.add(UserClanStatus.JUNIOR_LEADER.name());
	    statuses.add(UserClanStatus.CAPTAIN.name());
	    statuses.add(UserClanStatus.MEMBER.name());
    	Map<Integer, Integer> clanIdToSize = RetrieveUtils.userClanRetrieveUtils()
    			.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);
    	int userClanMembersInClan = clanIdToSize.get(clanId);
      if (userClanMembersInClan > 1) {
        resBuilder.setStatus(LeaveClanStatus.FAIL_OWNER_OF_CLAN_WITH_OTHERS_STILL_IN);
        log.error("user is owner and he's not alone in clan, can't leave without switching ownership. user clan members are " 
            + userClanMembersInClan);
        return false;
      }
    }
    
    clanOwnerIdList.add(clanOwnerId);
    return true;
  }

  private boolean writeChangesToDB(User user, Clan clan, int clanOwnerId) {
    int userId = user.getId();
    int clanId = clan.getId();

    if (userId == clanOwnerId) {
      List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils().getUserIdsRelatedToClan(clanId);
      deleteClan(clan, userIds, user);
    } else {
      if (!DeleteUtils.get().deleteUserClan(userId, clanId)) {
        log.error("problem with deleting user clan for " + user + " and clan " + clan);
      }
      if (!user.updateRelativeCoinsAbsoluteClan(0, null)) {
        log.error("problem with making clanid for user null");
      }
    }
    
    int numUpdated = UpdateUtils.get().closeClanHelp(userId, clanId);
    log.info(String.format("num ClanHelps closed: %s", numUpdated));
    
    return true;
  }

  private void deleteClan(Clan clan, List<Integer> userIds, User user) {
    if (!user.updateRelativeCoinsAbsoluteClan(0, null)) {
      log.error("problem with marking clan id null for users with ids in " + userIds);
    } else {
      if (!DeleteUtils.get().deleteUserClanDataRelatedToClanId(clan.getId(), userIds.size())) {
        log.error("problem with deleting user clan data for clan with id " + clan.getId());
      } else {
        if (!DeleteUtils.get().deleteClanWithClanId(clan.getId())) {
          log.error("problem with deleting clan with id " + clan.getId());
        }
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
  
}
