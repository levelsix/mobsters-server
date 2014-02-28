package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveClanInfoRequestEvent;
import com.lvl6.events.response.RetrieveClanInfoResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoRequestProto;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoRequestProto.ClanInfoGrabType;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto.Builder;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto.RetrieveClanInfoStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

@Component @DependsOn("gameServer") public class RetrieveClanInfoController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public RetrieveClanInfoController() {
    numAllocatedThreads = 8;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new RetrieveClanInfoRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_RETRIEVE_CLAN_INFO_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RetrieveClanInfoRequestProto reqProto = ((RetrieveClanInfoRequestEvent)event).getRetrieveClanInfoRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int clanId = reqProto.getClanId();
    String clanName = reqProto.getClanName();
    int beforeClanId = reqProto.getBeforeThisClanId();
    ClanInfoGrabType grabType = reqProto.getGrabType();

    RetrieveClanInfoResponseProto.Builder resBuilder = RetrieveClanInfoResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setIsForBrowsingList(reqProto.getIsForBrowsingList());
    resBuilder.setIsForSearch(false);
    
    if (reqProto.hasClanName()) resBuilder.setClanName(clanName);
    if (reqProto.hasClanId()) resBuilder.setClanId(clanId);

    try {
      //User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());

      boolean legitCreate = checkLegitCreate(resBuilder, clanName, clanId);

      if (legitCreate) {
        if (reqProto.hasClanName() || reqProto.hasClanId()) {
          if (grabType == ClanInfoGrabType.ALL || grabType == ClanInfoGrabType.CLAN_INFO) {
            List<Clan> clans = null;
            if (reqProto.hasClanName()) {
              // Can search for clan name or tag name
              clans = ClanRetrieveUtils.getClansWithSimilarNameOrTag(clanName, clanName);
              resBuilder.setIsForSearch(true);
            } else if (reqProto.hasClanId()) {
              Clan clan = ClanRetrieveUtils.getClanWithId(clanId);
              clans = new ArrayList<Clan>();
              clans.add(clan);
            }

            if (clans != null && clans.size() > 0) {
              for (Clan c : clans) {
                resBuilder.addClanInfo(CreateInfoProtoUtils.createFullClanProtoWithClanSize(c));
              }
            }
          }
          if (grabType == ClanInfoGrabType.ALL || grabType == ClanInfoGrabType.MEMBERS) {
            List<UserClan> userClans = RetrieveUtils.userClanRetrieveUtils().getUserClansRelatedToClan(clanId);
            
            Set<Integer> userIds = new HashSet<Integer>();
            //this is because clan with 1k+ users overflows buffer when sending to client and need to 
            //include clan owner
            //UPDATE: well, the user clan status now specifies whether a person is a leader, so 
            //owner id in clan is not needed
//            Clan c = ClanRetrieveUtils.getClanWithId(clanId);
//            int ownerId = c.getOwnerId();
            for (UserClan uc: userClans) {
              userIds.add(uc.getUserId());
            }
//            userIds.add(ownerId);
            List<Integer> userIdList = new ArrayList<Integer>(userIds);

            //get the users
            Map<Integer, User> usersMap = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIdList);
            //get the monster battle teams for the users
            Map<Integer, List<MonsterForUser>> userIdsToMonsterTeams = RetrieveUtils
            		.monsterForUserRetrieveUtils().getUserIdsToMonsterTeamForUserIds(userIdList);
            
            //get the clan raid contribution stuff
//            ClanEventPersistentForClan cepfc = ClanEventPersistentForClanRetrieveUtils
//            		.getPersistentEventForClanId(clanId);
//            if (null != cepfc) {
//            	
//            }

            for (UserClan uc : userClans) {
            	int userId = uc.getUserId();
            	User u = usersMap.get(userId);
            	//TODO: TODO: FIGURE OUT THIS CLAN RAID CONTRIBUTION FLOAT
              MinimumUserProtoForClans minUser = CreateInfoProtoUtils
              		.createMinimumUserProtoForClans(u, uc.getStatus(), 0F);
              resBuilder.addMembers(minUser);
              
              //create the monster team for this user if possible
              if (userIdsToMonsterTeams.containsKey(userId)) {
              	List<MonsterForUser> monsterTeam = userIdsToMonsterTeams.get(userId);
              	List<FullUserMonsterProto> proto = CreateInfoProtoUtils
              			.createFullUserMonsterProtoList(monsterTeam);
              	
              	//create the user monster team proto via the builder
            		UserCurrentMonsterTeamProto.Builder teamForUser = UserCurrentMonsterTeamProto.newBuilder();
            		teamForUser.setUserId(userId);
            		teamForUser.addAllCurrentTeam(proto);
            		resBuilder.addMonsterTeams(teamForUser.build());
              }
              
            }
            
          }
        } else {
          List<Clan> clans = null;
          if (beforeClanId <= 0) {
            clans = ClanRetrieveUtils.getMostRecentClans(ControllerConstants.RETRIEVE_CLANS__NUM_CLANS_CAP);
          } else {
            clans = ClanRetrieveUtils.getMostRecentClansBeforeClanId(ControllerConstants.RETRIEVE_CLANS__NUM_CLANS_CAP, beforeClanId);
            resBuilder.setBeforeThisClanId(reqProto.getBeforeThisClanId());
          }

          for (Clan clan : clans) {
            resBuilder.addClanInfo(CreateInfoProtoUtils.createFullClanProtoWithClanSize(clan));
          }
        }
      }

      RetrieveClanInfoResponseEvent resEvent = new RetrieveClanInfoResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setRetrieveClanInfoResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
    } catch (Exception e) {
      log.error("exception in RetrieveClanInfo processEvent", e);
    }
  }

  private boolean checkLegitCreate(Builder resBuilder, String clanName, int clanId) {
    if ((clanName == null || clanName.length() != 0) && clanId != 0) {
      resBuilder.setStatus(RetrieveClanInfoStatus.OTHER_FAIL);
      log.error("clan name and clan id set");
      return false;
    }
    resBuilder.setStatus(RetrieveClanInfoStatus.SUCCESS);
    return true;
  }
}
