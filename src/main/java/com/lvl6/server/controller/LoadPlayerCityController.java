package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LoadPlayerCityRequestEvent;
import com.lvl6.events.response.LoadPlayerCityResponseEvent;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.info.ExpansionPurchaseForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.proto.CityProto.UserCityExpansionDataProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityRequestProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto.Builder;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto.LoadPlayerCityStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.UserObstacleProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ExpansionPurchaseForUserRetrieveUtils;
import com.lvl6.retrieveutils.ObstacleForUserRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class LoadPlayerCityController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  

	@Autowired
	protected ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil;
	public ObstacleForUserRetrieveUtil getObstacleForUserRetrieveUtil() {
		return obstacleForUserRetrieveUtil;
	}
	public void setObstacleForUserRetrieveUtil(
			ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil) {
		this.obstacleForUserRetrieveUtil = obstacleForUserRetrieveUtil;
	}
	

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
//    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

    try {
      User owner = RetrieveUtils.userRetrieveUtils().getUserById(cityOwnerId);

      List<StructureForUser> userStructs = RetrieveUtils.userStructRetrieveUtils().getUserStructsForUser(cityOwnerId);
      setResponseUserStructs(resBuilder, userStructs);
      setObstacleStuff(resBuilder, cityOwnerId);
      
      List<ExpansionPurchaseForUser> userCityExpansionDataList = ExpansionPurchaseForUserRetrieveUtils.getUserCityExpansionDatasForUserId(senderProto.getUserId());
      List<UserCityExpansionDataProto> userCityExpansionDataProtoList = new ArrayList<UserCityExpansionDataProto>();
      if (userCityExpansionDataList != null) {
    	for(ExpansionPurchaseForUser uced : userCityExpansionDataList) {
    		userCityExpansionDataProtoList.add(CreateInfoProtoUtils.createUserCityExpansionDataProtoFromUserCityExpansionData(uced));
    	}
        resBuilder.addAllUserCityExpansionDataProtoList(userCityExpansionDataProtoList);
      }

      if (owner == null) {
        log.error("owner is null for ownerId = "+cityOwnerId);
      } else {
        resBuilder.setCityOwner(CreateInfoProtoUtils.createMinimumUserProtoFromUser(owner));

        LoadPlayerCityResponseEvent resEvent = new LoadPlayerCityResponseEvent(senderProto.getUserId());
        resEvent.setTag(event.getTag());
        resEvent.setLoadPlayerCityResponseProto(resBuilder.build());  
        server.writeEvent(resEvent);

      }
    } catch (Exception e) {
      log.error("exception in LoadPlayerCity processEvent", e);
    } finally {
//      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  private void setResponseUserStructs(Builder resBuilder,
      List<StructureForUser> userStructs) {
    if (userStructs != null) {
      for (StructureForUser userStruct : userStructs) {
        resBuilder.addOwnerNormStructs(CreateInfoProtoUtils.createFullUserStructureProtoFromUserstruct(userStruct));
      }
    } else {
      resBuilder.setStatus(LoadPlayerCityStatus.FAIL_OTHER);
      log.error("user structs found for user is null");
    }
  }
  
  private void setObstacleStuff(Builder resBuilder, int userId) {
  	List<ObstacleForUser> ofuList = getObstacleForUserRetrieveUtil()
  			.getUserObstacleForUser(userId);
  	
  	if (null == ofuList) {
  		return;
  	}
  	
  	for (ObstacleForUser ofu : ofuList) {
  		UserObstacleProto uop = CreateInfoProtoUtils.createUserObstacleProto(ofu);
  		resBuilder.addObstacles(uop);
  	}
  	
  }

}
