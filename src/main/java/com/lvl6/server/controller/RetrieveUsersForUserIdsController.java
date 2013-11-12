package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveUsersForUserIdsRequestEvent;
import com.lvl6.events.response.RetrieveUsersForUserIdsResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsRequestProto;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class RetrieveUsersForUserIdsController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

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
    List<Integer> requestedUserIds = reqProto.getRequestedUserIdsList();
    boolean includeCurMonsterTeam = reqProto.getIncludeCurMonsterTeam();
    
    RetrieveUsersForUserIdsResponseProto.Builder resBuilder = RetrieveUsersForUserIdsResponseProto.newBuilder();
    resBuilder.setSender(senderProto);

//    boolean includePotentialPoints = reqProto.getIncludePotentialPointsForClanTowers();
//    User sender = includePotentialPoints ? RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId()) : null;
    Map<Integer, User> usersByIds = RetrieveUtils.userRetrieveUtils().getUsersByIds(requestedUserIds);
    if (usersByIds != null) {
      for (User user : usersByIds.values()) {
        resBuilder.addRequestedUsers(CreateInfoProtoUtils.createFullUserProtoFromUser(user));
        
//        if (includePotentialPoints) {
//          int pointsGained = MiscMethods.pointsGainedForClanTowerUserBattle(sender, user);
//          int pointsLost = MiscMethods.pointsGainedForClanTowerUserBattle(user, sender);
//          resBuilder.addPotentialPointsGained(pointsGained);
//          resBuilder.addPotentialPointsLost(pointsLost);
//        }
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
    RetrieveUsersForUserIdsResponseEvent resEvent = new RetrieveUsersForUserIdsResponseEvent(senderProto.getUserId());
    resEvent.setTag(event.getTag());
    resEvent.setRetrieveUsersForUserIdsResponseProto(resProto);
    server.writeEvent(resEvent);
  }
  
  private List<UserCurrentMonsterTeamProto> constructTeamsForUsers(List<Integer> userIds) {
  	Map<Integer, List<MonsterForUser>> userIdsToCurrentTeam = RetrieveUtils
  			.monsterForUserRetrieveUtils().getUserIdsToMonsterTeamForUserIds(userIds);

  	//for each user construct his current team
  	List<UserCurrentMonsterTeamProto> retVal = new ArrayList<UserCurrentMonsterTeamProto>();
  	for (Integer userId : userIdsToCurrentTeam.keySet()) {
  		List<MonsterForUser> currentTeam = userIdsToCurrentTeam.get(userId);

  		List<FullUserMonsterProto> currentTeamProto = CreateInfoProtoUtils
  				.createFullUserMonsterProtoList(currentTeam);
  		
  		//create the proto via the builder
  		UserCurrentMonsterTeamProto.Builder teamForUser = UserCurrentMonsterTeamProto.newBuilder();
  		teamForUser.setUserId(userId);
  		teamForUser.addAllCurrentTeam(currentTeamProto);
  		
  		retVal.add(teamForUser.build());
  	}
  	
  	return retVal;
  }

}
