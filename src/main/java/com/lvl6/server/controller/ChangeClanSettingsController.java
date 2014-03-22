package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ChangeClanSettingsRequestEvent;
import com.lvl6.events.response.ChangeClanSettingsResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanIcon;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsRequestProto;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto.Builder;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto.ChangeClanSettingsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanIconRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class ChangeClanSettingsController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected ClanIconRetrieveUtils clanIconRetrieveUtils;
  public ClanIconRetrieveUtils getClanIconRetrieveUtils() {
		return clanIconRetrieveUtils;
	}
	public void setClanIconRetrieveUtils(ClanIconRetrieveUtils clanIconRetrieveUtils) {
		this.clanIconRetrieveUtils = clanIconRetrieveUtils;
	}
	

	public ChangeClanSettingsController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new ChangeClanSettingsRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_CHANGE_CLAN_SETTINGS_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    ChangeClanSettingsRequestProto reqProto = ((ChangeClanSettingsRequestEvent)event).getChangeClanSettingsRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    boolean isChangeDescription = reqProto.getIsChangeDescription();
    String description = reqProto.getDescriptionNow();
    boolean isChangeJoinType = reqProto.getIsChangeJoinType();
    boolean requestToJoinRequired = reqProto.getRequestToJoinRequired();
    boolean isChangeIcon = reqProto.getIsChangeIcon();
    int iconId = reqProto.getIconId();

    ChangeClanSettingsResponseProto.Builder resBuilder = ChangeClanSettingsResponseProto.newBuilder();
    resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    int clanId = 0;
    
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    
    if (0 != clanId) {
    	server.lockClan(clanId);
    } else {
    	server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      Clan clan = ClanRetrieveUtils.getClanWithId(user.getClanId());
      
      boolean legitChange = checkLegitChange(resBuilder, userId, user, clanId, clan);

      if (legitChange) {
      	//clan will be modified
        writeChangesToDB(resBuilder, clanId, clan, isChangeDescription, description,
        		isChangeJoinType, requestToJoinRequired, isChangeIcon, iconId);
        resBuilder.setMinClan(CreateInfoProtoUtils.createMinimumClanProtoFromClan(clan));
        resBuilder.setFullClan(CreateInfoProtoUtils.createFullClanProtoWithClanSize(clan));
      }
      
      ChangeClanSettingsResponseEvent resEvent = new ChangeClanSettingsResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setChangeClanSettingsResponseProto(resBuilder.build());  
      server.writeClanEvent(resEvent, clan.getId());

//      if (legitChange) {
//        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
//        resEventUpdate.setTag(event.getTag());
//        server.writeEvent(resEventUpdate);
//      }
    } catch (Exception e) {
      log.error("exception in ChangeClanSettings processEvent", e);
      try {
    	  resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
    	  ChangeClanSettingsResponseEvent resEvent = new ChangeClanSettingsResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setChangeClanSettingsResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in ChangeClanSettings processEvent", e);
    	}
    } finally {
    	if (0 != clanId) {
    		server.unlockClan(clanId);
    	} else {
    		server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}
    }
  }
  
  private boolean checkLegitChange(Builder resBuilder, int userId, User user,
  		int clanId, Clan clan) {
    if (user == null || clan == null) {
      resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
      log.error("userId is " + userId + ", user is " + user + "\t clanId is " + clanId +
      		", clan is " + clan);
      return false;      
    }
    if (user.getClanId() <= 0) {
      resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_NOT_IN_CLAN);
      log.error("user not in clan");
      return false;      
    }
    
    List<Integer> statuses = new ArrayList<Integer>();
    statuses.add(UserClanStatus.LEADER_VALUE);
    statuses.add(UserClanStatus.JUNIOR_LEADER_VALUE);
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatuses(clanId, statuses);
    
    Set<Integer> uniqUserIds = new HashSet<Integer>(); 
    if (null != userIds && !userIds.isEmpty()) {
    	uniqUserIds.addAll(userIds);
    }
    
    if (!uniqUserIds.contains(userId)) {
      resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_NOT_AUTHORIZED);
      log.error("clan member can't change clan description member=" + user);
      return false;      
    }
    resBuilder.setStatus(ChangeClanSettingsStatus.SUCCESS);
    return true;
  }

  private void writeChangesToDB(Builder resBuilder, int clanId, Clan clan, 
  		boolean isChangeDescription, String description, boolean isChangeJoinType,
  		boolean requestToJoinRequired, boolean isChangeIcon, int iconId) {
  	
  	if (isChangeDescription) {
  		if (description.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_DESCRIPTION) {
  			resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
  			log.warn("description is " + description + ", and length of that is " + description.length() + ", max size is " + 
  					ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_DESCRIPTION);
  		} else {
  			clan.setDescription(description);
  		}
  	}
  	
  	if (isChangeJoinType) {
  		clan.setRequestToJoinRequired(requestToJoinRequired);
  	}

  	if (isChangeIcon) {
  		ClanIcon ci = ClanIconRetrieveUtils.getClanIconForId(iconId);
  		if (null == ci) {
  			resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
  			log.warn("no clan icon with id=" + iconId);
  		} else {
  			clan.setClanIconId(iconId);
  		}
  	}
  	
  	int numUpdated = UpdateUtils.get().updateClan(clanId, isChangeDescription, description,
  			isChangeJoinType, requestToJoinRequired, isChangeIcon, iconId);
  	
  	log.info("numUpdated (should be 1)=" + numUpdated);
  }
}
