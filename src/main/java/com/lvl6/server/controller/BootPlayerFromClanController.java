package com.lvl6.server.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BootPlayerFromClanRequestEvent;
import com.lvl6.events.response.BootPlayerFromClanResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.BootPlayerFromClanStatus;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component @DependsOn("gameServer") public class BootPlayerFromClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Resource(name = "outgoingGameEventsHandlerExecutor")
  protected TaskExecutor executor;
  public TaskExecutor getExecutor() {
    return executor;
  }
  public void setExecutor(TaskExecutor executor) {
    this.executor = executor;
  }
  @Resource(name = "playersByPlayerId")
  protected Map<Integer, ConnectedPlayer> playersByPlayerId;
  public Map<Integer, ConnectedPlayer> getPlayersByPlayerId() {
    return playersByPlayerId;
  }
  public void setPlayersByPlayerId(
      Map<Integer, ConnectedPlayer> playersByPlayerId) {
    this.playersByPlayerId = playersByPlayerId;
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
    int playerToBootId = reqProto.getPlayerToBoot();

    BootPlayerFromClanResponseProto.Builder resBuilder = BootPlayerFromClanResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setPlayerToBoot(playerToBootId);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
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
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  private boolean checkLegitBoot(Builder resBuilder, User user,
      User playerToBoot) {
    if (user == null || playerToBoot == null) {
      resBuilder.setStatus(BootPlayerFromClanStatus.OTHER_FAIL);
      log.error("user is " + user + ", playerToBoot is " + playerToBoot);
      return false;      
    }
    
    int clanId = user.getClanId();
    String status = UserClanStatus.LEADER.toString();
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatus(clanId, status);
    //should just be one id
    int clanOwnerId = 0;
    if (null != userIds && !userIds.isEmpty()) {
    	clanOwnerId = userIds.get(0);
    }
    
    if (clanOwnerId != user.getId()) {
      resBuilder.setStatus(BootPlayerFromClanStatus.NOT_OWNER_OF_CLAN);
      log.error("clan owner isn't this guy, clan owner id is " + clanOwnerId);
      return false;      
    }
    if (playerToBoot.getClanId() != user.getClanId()) {
      resBuilder.setStatus(BootPlayerFromClanStatus.BOOTED_NOT_IN_CLAN);
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
