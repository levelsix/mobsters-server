package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CreateClanRequestEvent;
import com.lvl6.events.response.CreateClanResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.misc.Notification;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.CreateClanRequestProto;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.CreateClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class CreateClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public CreateClanController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new CreateClanRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_CREATE_CLAN_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    CreateClanRequestProto reqProto = ((CreateClanRequestEvent)event).getCreateClanRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    String clanName = reqProto.getName();
    String tag = reqProto.getTag();
    boolean requestToJoinRequired = reqProto.getRequestToJoinClanRequired();
    String description = reqProto.getDescription();
    int clanIconId = reqProto.getClanIconId();
    int gemsSpent = reqProto.getGemsSpent();
    int cashChange = reqProto.getCashChange();
    
    CreateClanResponseProto.Builder resBuilder = CreateClanResponseProto.newBuilder();
    resBuilder.setSender(senderProto);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      Timestamp createTime = new Timestamp(new Date().getTime());
      int previousGold = 0;
      
      boolean legitCreate = checkLegitCreate(resBuilder, user, clanName, tag);

      int clanId = ControllerConstants.NOT_SET;
      if (legitCreate) {
      	
      	//just in case user doesn't input one, set default description
      	if (null == description || description.isEmpty()) {
      		description = "Welcome to " + clanName + "!";
      	}
      	
        clanId = InsertUtils.get().insertClan(clanName, createTime, description,
            tag, requestToJoinRequired);
        if (clanId <= 0) {
          legitCreate = false;
          resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
        } else {
        	Clan newClan = new Clan(clanId, clanName, createTime, description, tag,
        			requestToJoinRequired, clanIconId);
          resBuilder.setClanInfo(CreateInfoProtoUtils.createMinimumClanProtoFromClan(newClan));
        }
      }
      
      CreateClanResponseEvent resEvent = new CreateClanResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setCreateClanResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
      if (legitCreate) {
        previousGold = user.getGems();
        
        Map<String, Integer> money = new HashMap<String, Integer>();
        writeChangesToDB(user, clanId, money);
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        sendGeneralNotification(user.getName(), clanName);
        
        writeToUserCurrencyHistory(user, createTime, money, previousGold, clanId, clanName);
      }
    } catch (Exception e) {
      log.error("exception in CreateClan processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }

  private void writeChangesToDB(User user, int clanId, Map<String, Integer> money) {
    int coinChange = -1*ControllerConstants.CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN;
    if (!user.updateRelativeCoinsAbsoluteClan(coinChange, clanId)) {
      log.error("problem with decreasing user diamonds for creating clan");
    } else {
      //everything went well
      money.put(MiscMethods.cash, coinChange);
    }
    if (!InsertUtils.get().insertUserClan(user.getId(), clanId, UserClanStatus.MEMBER, new Timestamp(new Date().getTime()))) {
      log.error("problem with inserting user clan data for user " + user + ", and clan id " + clanId);
    }
    DeleteUtils.get().deleteUserClansForUserExceptSpecificClan(user.getId(), clanId);
  }

  private boolean checkLegitCreate(Builder resBuilder, User user, String clanName, String tag) {
    if (user == null || clanName == null || clanName.length() <= 0 || tag == null || tag.length() <= 0) {
      resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
      log.error("user is null");
      return false;      
    }
    if (user.getCash() < ControllerConstants.CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN) {
      resBuilder.setStatus(CreateClanStatus.FAIL_NOT_ENOUGH_CASH);
      log.error("user only has " + user.getCash() + ", needs " + ControllerConstants.CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN);
      return false;
    }
    if (clanName.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME) {
      resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
      log.error("clan name " + clanName + " is more than " + ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME + " characters");
      return false;
    }
    
    if (tag.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG) {
      resBuilder.setStatus(CreateClanStatus.FAIL_INVALID_TAG_LENGTH);
      log.error("clan tag " + tag + " is more than " + ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG + " characters");
      return false;
    }
    
    if (user.getClanId() > 0) {
      resBuilder.setStatus(CreateClanStatus.FAIL_ALREADY_IN_CLAN);
      log.error("user already in clan with id " + user.getClanId());
      return false;
    }
    Clan clan = ClanRetrieveUtils.getClanWithNameOrTag(clanName, tag);
    if (clan != null) {
      if (clan.getName().equalsIgnoreCase(clanName)) {
        resBuilder.setStatus(CreateClanStatus.FAIL_NAME_TAKEN);
        log.error("clan name already taken with name " + clanName);
        return false;
      }
      if (clan.getTag().equalsIgnoreCase(tag)) {
        resBuilder.setStatus(CreateClanStatus.FAIL_TAG_TAKEN);
        log.error("clan tag already taken with tag " + tag);
        return false;
      }
    }
    resBuilder.setStatus(CreateClanStatus.SUCCESS);
    return true;
  }
  
  private void sendGeneralNotification (String userName, String clanName) {
	  Notification createClanNotification = new Notification ();
	  createClanNotification.setAsClanCreated(userName, clanName);
	  
	  MiscMethods.writeGlobalNotification(createClanNotification, server);
  }
  
  private void writeToUserCurrencyHistory(User aUser, Timestamp date, Map<String, Integer> money,
      int previousGems, int clanId, String clanName) {
  	int userId = aUser.getId();
  	StringBuilder sb = new StringBuilder();
  	sb.append("clanId=");
  	sb.append(clanId);
  	sb.append(" clanName=");
  	sb.append(clanName);
  	String gems = MiscMethods.gems;
  	String reasonForChange = ControllerConstants.UCHRFC__CREATE_CLAN;
  	
    Map<String, Integer> previousCurrencyMap = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> detailsMap = new HashMap<String, String>();
    detailsMap.put(gems, sb.toString());
    
    previousCurrencyMap.put(gems, previousGems);
    currentCurrencyMap.put(gems, aUser.getGems());
    reasonsForChanges.put(gems, reasonForChange);
    MiscMethods.writeToUserCurrencyOneUser(userId, date, money, previousCurrencyMap,
    		currentCurrencyMap, reasonsForChanges, detailsMap);
  }
}
