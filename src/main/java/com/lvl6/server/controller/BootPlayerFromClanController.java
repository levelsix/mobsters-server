package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BootPlayerFromClanRequestEvent;
import com.lvl6.events.response.BootPlayerFromClanResponseEvent;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.BootPlayerFromClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component @DependsOn("gameServer") public class BootPlayerFromClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;
  public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}
	
  public BootPlayerFromClanController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new BootPlayerFromClanRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_BOOT_PLAYER_FROM_CLAN_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    BootPlayerFromClanRequestProto reqProto = ((BootPlayerFromClanRequestEvent)event).getBootPlayerFromClanRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int playerToBootId = reqProto.getPlayerToBoot();
    List<Integer> userIds = new ArrayList<Integer>();
    userIds.add(userId);
    userIds.add(playerToBootId);

    BootPlayerFromClanResponseProto.Builder resBuilder = BootPlayerFromClanResponseProto.newBuilder();
    resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    int clanId = 0;
    
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    boolean lockedClan = false;
    if (0 != clanId) {
    	lockedClan = getLocker().lockClan(clanId);
    }
    try {
    	Map<Integer,User> users = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIds);
      User user = users.get(userId);
      User playerToBoot = users.get(playerToBootId);

      boolean legitBoot = checkLegitBoot(resBuilder, lockedClan, user, playerToBoot);

      boolean success = false;
      if (legitBoot) { 
      	success = writeChangesToDB(user, playerToBoot);
      }
      
      if (success) {
      	MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(playerToBoot);
      	resBuilder.setPlayerToBoot(mup);
      }
      
      BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setBootPlayerFromClanResponseProto(resBuilder.build()); 

      if (success) {
      	//if successful write to clan
      	server.writeClanEvent(resEvent, clanId);
      } else {
      	//write to user if fail
        server.writeEvent(resEvent);
      }
    } catch (Exception e) {
      log.error("exception in BootPlayerFromClan processEvent", e);
      try {
    	  resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
    	  BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setBootPlayerFromClanResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in BootPlayerFromClan processEvent", e);
    	}
    } finally {
    	if (0 != clanId) {
    		getLocker().unlockClan(clanId);
    	}
    }
  }

  private boolean checkLegitBoot(Builder resBuilder, boolean lockedClan, User user,
      User playerToBoot) {
  	
  	if (!lockedClan) {
  		log.error("couldn't obtain clan lock");
  		return false;
  	}
  	
    if (user == null || playerToBoot == null) {
      resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
      log.error("user is " + user + ", playerToBoot is " + playerToBoot);
      return false;      
    }
    
    int clanId = user.getClanId();
    List<String> statuses = new ArrayList<String>();
    statuses.add(UserClanStatus.LEADER.name());
    statuses.add(UserClanStatus.JUNIOR_LEADER.name());
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatuses(clanId, statuses);
    
    Set<Integer> uniqUserIds = new HashSet<Integer>(); 
    if (null != userIds && !userIds.isEmpty()) {
    	uniqUserIds.addAll(userIds);
    }
    
    int userId = user.getId();
    if (!uniqUserIds.contains(userId)) {
      resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_NOT_AUTHORIZED);
      log.error("user can't boot player. user=" + user +"\t playerToBoot=" + playerToBoot);
      return false;      
    }
    if (playerToBoot.getClanId() != user.getClanId()) {
      resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_BOOTED_NOT_IN_CLAN);
      log.error("playerToBoot is not in user clan. playerToBoot is in " + playerToBoot.getClanId());
      return false;
    }
    resBuilder.setStatus(BootPlayerFromClanStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDB(User user, User playerToBoot) {
    if (!DeleteUtils.get().deleteUserClan(playerToBoot.getId(), playerToBoot.getClanId())) {
      log.error("problem with deleting user clan info for playerToBoot with id " + playerToBoot.getId() + " and clan id " + playerToBoot.getClanId()); 
    }
    if (!playerToBoot.updateRelativeCoinsAbsoluteClan(0, null)) {
      log.error("problem with change playerToBoot " + playerToBoot + " clan id to nothing");
    }
    return true;
  }

}
