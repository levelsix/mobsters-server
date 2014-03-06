package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.TransferClanOwnershipRequestEvent;
import com.lvl6.events.response.TransferClanOwnershipResponseEvent;
import com.lvl6.events.response.TransferClanOwnershipResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipRequestProto;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.TransferClanOwnershipStatus;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.Builder;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.TransferClanOwnershipStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class TransferClanOwnershipController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public TransferClanOwnershipController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new TransferClanOwnershipRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_TRANSFER_CLAN_OWNERSHIP;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    TransferClanOwnershipRequestProto reqProto = ((TransferClanOwnershipRequestEvent)event).getTransferClanOwnershipRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int newClanOwnerId = reqProto.getNewClanOwnerId();

    TransferClanOwnershipResponseProto.Builder resBuilder = TransferClanOwnershipResponseProto.newBuilder();
    resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      User newClanOwner = RetrieveUtils.userRetrieveUtils().getUserById(newClanOwnerId);
      Clan clan = ClanRetrieveUtils.getClanWithId(user.getClanId());
      
      boolean legitTransfer = checkLegitTransfer(resBuilder, user, newClanOwner, clan);

      if (legitTransfer) {
        writeChangesToDB(user, newClanOwner);
        Clan newClan = ClanRetrieveUtils.getClanWithId(clan.getId());
        resBuilder.setMinClan(CreateInfoProtoUtils.createMinimumClanProtoFromClan(newClan));
        resBuilder.setFullClan(CreateInfoProtoUtils.createFullClanProtoWithClanSize(newClan));
      }
      
      TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setTransferClanOwnershipResponseProto(resBuilder.build());  
      server.writeClanEvent(resEvent, clan.getId());
      
      if (legitTransfer) {
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
      }

    } catch (Exception e) {
      log.error("exception in TransferClanOwnership processEvent", e);
      try {
    	  resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
    	  TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setTransferClanOwnershipResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in TransferClanOwnership processEvent", e);
    	}
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  private boolean checkLegitTransfer(Builder resBuilder, User user, User newClanOwner, Clan clan) {
    if (user == null || newClanOwner == null) {
      resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
      log.error("user is " + user + ", new clan owner is " + newClanOwner);
      return false;      
    }
    if (user.getClanId() <= 0) {
      resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_NOT_AUTHORIZED);
      log.error("user not in clan. user=" + user);
      return false;      
    }
    
    if (newClanOwner.getClanId() != user.getClanId()) {
      resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_NEW_OWNER_NOT_IN_CLAN);
      log.error("new owner not in same clan as user. new owner= " + newClanOwner + ", user is " + user);
      return false;     
    }
    
    int clanId = user.getClanId();
    List<Integer> statuses = new ArrayList<Integer>();
    statuses.add(UserClanStatus.LEADER_VALUE);
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatuses(clanId, statuses);
    //should just be one id
    int clanOwnerId = 0;
    if (null != userIds && !userIds.isEmpty()) {
    	clanOwnerId = userIds.get(0);
    }
    
    if (clan == null || clanOwnerId != user.getId()) {
      resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_NOT_AUTHORIZED);
      log.error("clan is " + clan + ", and user isn't owner");
      return false;      
    }
    resBuilder.setStatus(TransferClanOwnershipStatus.SUCCESS);
    return true;
  }

  private void writeChangesToDB(User user, User newClanOwner) {
    if (!UpdateUtils.get().updateClanOwnerDescriptionForClan(user.getClanId(), newClanOwner.getId(), null)) {
      log.error("problem with changing clan owner for clan with id " + user.getClanId() + " from user " + user + " to new owner " + newClanOwner);
    }
  }
}
