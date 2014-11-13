package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveUsersForUserIdsRequestEvent;
import com.lvl6.events.response.RetrieveUsersForUserIdsResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsRequestProto;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpUser;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class RetrieveUsersForUserIdsController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;

  public RetrieveUsersForUserIdsController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new RetrieveUsersForUserIdsRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_RETRIEVE_USERS_FOR_USER_IDS_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RetrieveUsersForUserIdsRequestProto reqProto = ((RetrieveUsersForUserIdsRequestEvent)event).getRetrieveUsersForUserIdsRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    List<String> requestedUserIds = reqProto.getRequestedUserUuidsList();
    boolean includeCurMonsterTeam = reqProto.getIncludeCurMonsterTeam();
    
    RetrieveUsersForUserIdsResponseProto.Builder resBuilder = RetrieveUsersForUserIdsResponseProto.newBuilder();
    resBuilder.setSender(senderProto);

    UUID userUuid = null;
    boolean invalidUuids = true;
    try {
      if (requestedUserIds != null) {
        for (String userId : requestedUserIds) {
          userUuid = UUID.fromString(userId);
        }
      }

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect requestedUserIds=%s",
          requestedUserIds), e);
      invalidUuids = true;
    }

//    boolean includePotentialPoints = reqProto.getIncludePotentialPointsForClanTowers();
//    User sender = includePotentialPoints ? RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid()) : null;
    Map<String, User> usersByIds = RetrieveUtils.userRetrieveUtils().getUsersByIds(requestedUserIds);
    if (usersByIds != null) {
      for (User user : usersByIds.values()) {
    	  
    	  //TODO: consider getting from db
    	  //pull from hazelcast for now
        String userId = user.getId();
    	  PvpUser pu = getHazelcastPvpUtil().getPvpUser(userId);
    	  PvpLeagueForUser plfu = null;
    	  
    	  if (null != pu) {
    		  plfu = new PvpLeagueForUser(pu);
    	  }
    	  resBuilder.addRequestedUsers(CreateInfoProtoUtils
    			  .createFullUserProtoFromUser(user, plfu));
        
      }
      
      List<UserCurrentMonsterTeamProto> teams = null;
      if (includeCurMonsterTeam) {
      	teams = constructTeamsForUsers(requestedUserIds);
      }
      
      if (null != teams && !teams.isEmpty()) {
      	resBuilder.addAllCurTeam(teams);
      }
      
    } else {
      log.error("no users with the ids " + requestedUserIds);
    }
    RetrieveUsersForUserIdsResponseProto resProto = resBuilder.build();
    RetrieveUsersForUserIdsResponseEvent resEvent = new RetrieveUsersForUserIdsResponseEvent(senderProto.getUserUuid());
    resEvent.setTag(event.getTag());
    resEvent.setRetrieveUsersForUserIdsResponseProto(resProto);
    server.writeEvent(resEvent);
  }
  
  private List<UserCurrentMonsterTeamProto> constructTeamsForUsers(List<String> userIds) {
  	Map<String, List<MonsterForUser>> userIdsToCurrentTeam = RetrieveUtils
  			.monsterForUserRetrieveUtils().getUserIdsToMonsterTeamForUserIds(userIds);

  	//for each user construct his current team
  	List<UserCurrentMonsterTeamProto> retVal = new ArrayList<UserCurrentMonsterTeamProto>();
  	for (String userId : userIdsToCurrentTeam.keySet()) {
  		List<MonsterForUser> currentTeam = userIdsToCurrentTeam.get(userId);

  		List<FullUserMonsterProto> currentTeamProto = CreateInfoProtoUtils
  				.createFullUserMonsterProtoList(currentTeam);
  		
  		//create the proto via the builder
  		UserCurrentMonsterTeamProto.Builder teamForUser = UserCurrentMonsterTeamProto.newBuilder();
  		teamForUser.setUserUuid(userId);
  		teamForUser.addAllCurrentTeam(currentTeamProto);
  		
  		retVal.add(teamForUser.build());
  	}
  	
  	return retVal;
  }

  public HazelcastPvpUtil getHazelcastPvpUtil() {
	  return hazelcastPvpUtil;
  }

  public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
	  this.hazelcastPvpUtil = hazelcastPvpUtil;
  }

}
