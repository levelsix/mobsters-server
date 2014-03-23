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
import com.lvl6.events.request.TransferClanOwnershipRequestEvent;
import com.lvl6.events.response.TransferClanOwnershipResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipRequestProto;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.Builder;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto.TransferClanOwnershipStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class TransferClanOwnershipController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;
  public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}
  
  public TransferClanOwnershipController() {
    numAllocatedThreads = 2;
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
    int newClanOwnerId = reqProto.getClanOwnerIdNew();
    List<Integer> userIds = new ArrayList<Integer>();
    userIds.add(userId);
    userIds.add(newClanOwnerId);

    TransferClanOwnershipResponseProto.Builder resBuilder = TransferClanOwnershipResponseProto.newBuilder();
    resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_OTHER);
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
    	Map<Integer, UserClan> userClans = RetrieveUtils.userClanRetrieveUtils()
    			.getUserClanForUsers(clanId, userIds);
    	
      User user = users.get(userId);
      User newClanOwner = users.get(newClanOwnerId);
      
      boolean legitTransfer = checkLegitTransfer(resBuilder, lockedClan, userId, user,
      		newClanOwnerId, newClanOwner, userClans);

      if (legitTransfer) {
      	List<UserClanStatus> statuses = new ArrayList<UserClanStatus>();
      	statuses.add(UserClanStatus.JUNIOR_LEADER);
      	statuses.add(UserClanStatus.LEADER);
        writeChangesToDB(clanId, userIds, statuses);
        setResponseBuilderStuff(resBuilder, clanId, newClanOwner);
      }
      
      if (!legitTransfer) {
      	//if not successful write to guy
    	  TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setTransferClanOwnershipResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);      
      }
      
      if (legitTransfer) {
      	TransferClanOwnershipResponseEvent resEvent = new TransferClanOwnershipResponseEvent(senderProto.getUserId());
      	resEvent.setTag(event.getTag());
      	resEvent.setTransferClanOwnershipResponseProto(resBuilder.build());  
      	server.writeClanEvent(resEvent, clanId);
      
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
    	if (0 != clanId) {
    		getLocker().unlockClan(clanId);
    	}
    }
  }

  private boolean checkLegitTransfer(Builder resBuilder, boolean lockedClan, int userId,
  		User user, int newClanOwnerId, User newClanOwner, Map<Integer, UserClan> userClans) {

  	if (!lockedClan) {
  		log.error("couldn't obtain clan lock");
  		return false;
  	}
  	
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
    
    
    if (!userClans.containsKey(userId) || !userClans.containsKey(newClanOwnerId)) {
    	log.error("a UserClan does not exist userId=" + userId + ", newClanOwner=" +
    			newClanOwnerId + "\t userClans=" + userClans);
    }
    UserClan userClan = userClans.get(user.getId());
    
    if (!UserClanStatus.LEADER.equals(userClan.getStatus())) {
      resBuilder.setStatus(TransferClanOwnershipStatus.FAIL_NOT_AUTHORIZED);
      log.error("user is " + user + ", and user isn't owner. user is:" + userClan);
      return false;      
    }
    resBuilder.setStatus(TransferClanOwnershipStatus.SUCCESS);
    return true;
  }

  private void writeChangesToDB(int clanId, List<Integer> userIdList,
  		List<UserClanStatus> statuses) {
  	//update clan for user table
  	
  	int numUpdated = UpdateUtils.get().updateUserClanStatuses(clanId, userIdList, statuses); 
  	log.error("num clan_for_user updated=" + numUpdated + " userIdList=" + userIdList +
  			" statuses=" + statuses);
  }
  
  private void setResponseBuilderStuff(Builder resBuilder, int clanId, User newClanOwner) {
  	Clan clan = ClanRetrieveUtils.getClanWithId(clanId);
  	List<Integer> clanIdList = new ArrayList<Integer>();
  	clanIdList.add(clanId);
  	
  	List<Integer> statuses = new ArrayList<Integer>();
  	statuses.add(UserClanStatus.LEADER_VALUE);
  	statuses.add(UserClanStatus.JUNIOR_LEADER_VALUE);
  	statuses.add(UserClanStatus.CAPTAIN_VALUE);
  	statuses.add(UserClanStatus.MEMBER_VALUE);
  	Map<Integer, Integer> clanIdToSize = RetrieveUtils.userClanRetrieveUtils()
  			.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);
  	
    resBuilder.setMinClan(CreateInfoProtoUtils.createMinimumClanProtoFromClan(clan));

    int size = clanIdToSize.get(clanId);
    resBuilder.setFullClan(CreateInfoProtoUtils.createFullClanProtoWithClanSize(clan, size));
    
    MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(newClanOwner);
    resBuilder.setClanOwnerNew(mup);
  }
  
}
