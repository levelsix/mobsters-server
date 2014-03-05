package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BootPlayerFromClanRequestEvent;
import com.lvl6.events.response.BootPlayerFromClanResponseEvent;
import com.lvl6.events.response.BootPlayerFromClanResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.BootPlayerFromClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.BootPlayerFromClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component @DependsOn("gameServer") public class BootPlayerFromClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

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

    BootPlayerFromClanResponseProto.Builder resBuilder = BootPlayerFromClanResponseProto.newBuilder();
    resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);
    resBuilder.setPlayerToBoot(playerToBootId);

    int clanId = 0;
    
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    
    if (0 != clanId) {
    	server.lockClan(clanId);
    } else {
    //MAYBE SHOULD LOCK THE playerToBootId INSTEAD OF userId
    //server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	server.lockPlayer(playerToBootId, this.getClass().getSimpleName());
    }
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      User playerToBoot = RetrieveUtils.userRetrieveUtils().getUserById(playerToBootId);

      boolean legitBoot = checkLegitBoot(resBuilder, user, playerToBoot);

      BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setBootPlayerFromClanResponseProto(resBuilder.build()); 

      if (legitBoot) { 
        server.writeClanEvent(resEvent, user.getClanId());

        BootPlayerFromClanResponseEvent resEvent2 = new BootPlayerFromClanResponseEvent(playerToBootId);
        resEvent2.setBootPlayerFromClanResponseProto(resBuilder.build()); //I think this is supposed to be resEvent2 not resEvent
        server.writeEvent(resEvent2);

        writeChangesToDB(user, playerToBoot);
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);

      } else {
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
    		server.unlockClan(clanId);
    	} else {
    		server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}
    }
  }

  private boolean checkLegitBoot(Builder resBuilder, User user,
      User playerToBoot) {
    if (user == null || playerToBoot == null) {
      resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
      log.error("user is " + user + ", playerToBoot is " + playerToBoot);
      return false;      
    }
    
    int clanId = user.getClanId();
    List<Integer> statuses = new ArrayList<Integer>();
    statuses.add(UserClanStatus.LEADER_VALUE);
    statuses.add(UserClanStatus.JUNIOR_LEADER_VALUE);
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

  private void writeChangesToDB(User user, User playerToBoot) {
    if (!DeleteUtils.get().deleteUserClan(playerToBoot.getId(), playerToBoot.getClanId())) {
      log.error("problem with deleting user clan info for playerToBoot with id " + playerToBoot.getId() + " and clan id " + playerToBoot.getClanId()); 
    }
    if (!playerToBoot.updateRelativeCoinsAbsoluteClan(0, null)) {
      log.error("problem with change playerToBoot " + playerToBoot + " clan id to nothing");
    }
  }

}
