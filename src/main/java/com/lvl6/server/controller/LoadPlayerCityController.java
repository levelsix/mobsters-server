package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LoadPlayerCityRequestEvent;
import com.lvl6.events.response.LoadPlayerCityResponseEvent;
import com.lvl6.info.User;
import com.lvl6.info.UserCityExpansionData;
import com.lvl6.info.UserStruct;
import com.lvl6.proto.CityProto.UserCityExpansionDataProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityRequestProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto.Builder;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto.LoadPlayerCityStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserCityExpansionDataRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class LoadPlayerCityController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public LoadPlayerCityController() {
    numAllocatedThreads = 10;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new LoadPlayerCityRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_LOAD_PLAYER_CITY_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    LoadPlayerCityRequestProto reqProto = ((LoadPlayerCityRequestEvent)event).getLoadPlayerCityRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int cityOwnerId = reqProto.getCityOwnerId();

    LoadPlayerCityResponseProto.Builder resBuilder = LoadPlayerCityResponseProto.newBuilder();
    resBuilder.setSender(senderProto);

    resBuilder.setStatus(LoadPlayerCityStatus.SUCCESS);
    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

    try {
      User owner = RetrieveUtils.userRetrieveUtils().getUserById(cityOwnerId);

      List<UserStruct> userStructs = RetrieveUtils.userStructRetrieveUtils().getUserStructsForUser(cityOwnerId);
      setResponseUserStructs(resBuilder, userStructs);
      
      List<UserCityExpansionData> userCityExpansionDataList = UserCityExpansionDataRetrieveUtils.getUserCityExpansionDatasForUserId(senderProto.getUserId());
      List<UserCityExpansionDataProto> userCityExpansionDataProtoList = new ArrayList<UserCityExpansionDataProto>();
      if (userCityExpansionDataList != null) {
    	for(UserCityExpansionData uced : userCityExpansionDataList) {
    		userCityExpansionDataProtoList.add(CreateInfoProtoUtils.createUserCityExpansionDataProtoFromUserCityExpansionData(uced));
    	}
        resBuilder.addAllUserCityExpansionDataProtoList(userCityExpansionDataProtoList);
      }

      if (owner == null) {
        log.error("owner is null for ownerId = "+cityOwnerId);
      } else {
        resBuilder.setCityOwner(CreateInfoProtoUtils.createMinimumUserProtoFromUser(owner));


//        boolean realPlayersOnly = false;
//        boolean fakePlayersOnly = false;
//        boolean offlinePlayersOnly = false;
//        boolean prestigePlayersOnly = false;
//        boolean inactiveShield = true;
//        
//        if (ownerIsGood != senderIsGood) {    //loading enemy city, load some of owners allies (more enemies from your POV)
//          List<User> ownerAllies = RetrieveUtils.userRetrieveUtils().getUsers(
//              ControllerConstants.LOAD_PLAYER_CITY__APPROX_NUM_USERS_IN_CITY,
//              owner.getLevel(), owner.getId(), false,
//              realPlayersOnly, fakePlayersOnly, offlinePlayersOnly,
//              inactiveShield, null);
//          
//          setResponseOwnerAlliesOrEnemies(resBuilder, ownerAllies, true);
//          
//        } else {                              //loading ally city or your city, creating some of owners enemies
//          List<User> ownerEnemies = RetrieveUtils.userRetrieveUtils().getUsers(
//              ControllerConstants.LOAD_PLAYER_CITY__APPROX_NUM_USERS_IN_CITY,
//              owner.getLevel(), owner.getId(), false,
//              realPlayersOnly, fakePlayersOnly, offlinePlayersOnly,
//              inactiveShield, null);
//          
//          setResponseOwnerAlliesOrEnemies(resBuilder, ownerEnemies, false);
//          
//        }

        LoadPlayerCityResponseEvent resEvent = new LoadPlayerCityResponseEvent(senderProto.getUserId());
        resEvent.setTag(event.getTag());
        resEvent.setLoadPlayerCityResponseProto(resBuilder.build());  
        server.writeEvent(resEvent);

      }
    } catch (Exception e) {
      log.error("exception in LoadPlayerCity processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  private void setResponseOwnerAlliesOrEnemies(Builder resBuilder, List<User> users, boolean ownerAllies) {
//    if (users != null) {
//      for (User user : users) {
//        if (ownerAllies) {
//          resBuilder.addOwnerAllies(CreateInfoProtoUtils.createFullUserProtoFromUser(user));
//        } else {
//          resBuilder.addOwnerEnemies(CreateInfoProtoUtils.createFullUserProtoFromUser(user));          
//        }
//      }
//    } else {
//      resBuilder.setStatus(LoadPlayerCityStatus.OTHER_FAIL);
//      log.error("no users found when loading city owner's allies/enemies, ownerAllies="+ownerAllies);
//    }    
  }

  private void setResponseUserStructs(Builder resBuilder,
      List<UserStruct> userStructs) {
    if (userStructs != null) {
      for (UserStruct userStruct : userStructs) {
        resBuilder.addOwnerNormStructs(CreateInfoProtoUtils.createFullUserStructureProtoFromUserstruct(userStruct));
      }
    } else {
      resBuilder.setStatus(LoadPlayerCityStatus.OTHER_FAIL);
      log.error("user structs found for user is null");
    }
  }

}
