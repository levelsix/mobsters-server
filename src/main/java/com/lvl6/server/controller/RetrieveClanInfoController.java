package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveClanInfoRequestEvent;
import com.lvl6.events.response.RetrieveClanInfoResponseEvent;
import com.lvl6.info.CepfuRaidHistory;
import com.lvl6.info.Clan;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoRequestProto;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoRequestProto.ClanInfoGrabType;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto.Builder;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto.RetrieveClanInfoStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.CepfuRaidHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

@Component @DependsOn("gameServer") public class RetrieveClanInfoController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected TimeUtils timeUtils;
  
  public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

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
            List<Clan> clanList = null;
            if (reqProto.hasClanName()) {
              // Can search for clan name or tag name
            	clanList = ClanRetrieveUtils.getClansWithSimilarNameOrTag(clanName, clanName);
              resBuilder.setIsForSearch(true);
            } else if (reqProto.hasClanId()) {
              Clan clan = ClanRetrieveUtils.getClanWithId(clanId);
              clanList = new ArrayList<Clan>();
              clanList.add(clan);
            }
            setClanProtosWithSize(resBuilder, clanList);

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
            
            int nDays = ControllerConstants.CLAN_EVENT_PERSISTENT__NUM_DAYS_FOR_RAID_HISTORY;
            //get the clan raid contribution stuff
            Map<Date, Map<Integer, CepfuRaidHistory>> timesToUserIdToRaidHistory = 
            		CepfuRaidHistoryRetrieveUtils.getRaidHistoryForPastNDaysForClan(clanId, nDays, new Date(), timeUtils);
            Map<Integer, Float> userIdToClanRaidContribution = calculateRaidContribution(
            		timesToUserIdToRaidHistory);

            for (UserClan uc : userClans) {
            	int userId = uc.getUserId();
            	User u = usersMap.get(userId);
            	
            	//user might not have a clan raid entry, so
            	float clanRaidContribution = 0F;
            	if (userIdToClanRaidContribution.containsKey(userId)) {
            		clanRaidContribution = userIdToClanRaidContribution.get(userId);
            	}
              MinimumUserProtoForClans minUser = CreateInfoProtoUtils
              		.createMinimumUserProtoForClans(u, uc.getStatus(), clanRaidContribution);
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
          List<Clan> clanList = null;
          if (beforeClanId <= 0) {
          	clanList = ClanRetrieveUtils.getMostRecentClans(ControllerConstants.RETRIEVE_CLANS__NUM_CLANS_CAP);
          } else {
          	clanList = ClanRetrieveUtils.getMostRecentClansBeforeClanId(ControllerConstants.RETRIEVE_CLANS__NUM_CLANS_CAP, beforeClanId);
            resBuilder.setBeforeThisClanId(reqProto.getBeforeThisClanId());
          }

          setClanProtosWithSize(resBuilder, clanList);
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
  
  private  void setClanProtosWithSize(Builder resBuilder, List<Clan> clanList) {

    if (null == clanList || clanList.isEmpty()) {
    	return;
    }
    List<Integer> clanIds = ClanRetrieveUtils.getClanIdsFromClans(clanList);
    
    List<Integer> statuses = new ArrayList<Integer>();
    statuses.add(UserClanStatus.LEADER_VALUE);
    statuses.add(UserClanStatus.JUNIOR_LEADER_VALUE);
    statuses.add(UserClanStatus.CAPTAIN_VALUE);
    statuses.add(UserClanStatus.MEMBER_VALUE);

    Map<Integer, Integer> clanIdsToSizes = RetrieveUtils.userClanRetrieveUtils()
    		.getClanSizeForClanIdsAndStatuses(clanIds, statuses);

    for (Clan c : clanList) {
    	int clanId = c.getId();
    	int size = clanIdsToSizes.get(clanId);
    	resBuilder.addClanInfo(CreateInfoProtoUtils.createFullClanProtoWithClanSize(c, size));
    }
  }
  
  //clan raid contribution is calculated through summing all the clanCrDmg
  //summing all of a user's crDmg, and taking dividing
  //sumUserCrDmg by sumClanCrDmg 
  private Map<Integer, Float> calculateRaidContribution(
  		Map<Date, Map<Integer, CepfuRaidHistory>> timesToUserIdToRaidHistory) {
  	log.info("calculating clan raid contribution.");
  	
  	//return value
  	Map<Integer, Float> userIdToContribution = new HashMap<Integer, Float>();
  	
  	Map<Integer, Integer> userIdToSumCrDmg = new HashMap<Integer, Integer>();
  	int sumClanCrDmg = 0;
  	
  	//each date represents a raid
  	for (Date aDate : timesToUserIdToRaidHistory.keySet()) {
  		Map<Integer, CepfuRaidHistory> userIdToRaidHistory =
  				timesToUserIdToRaidHistory.get(aDate);
  		
  		sumClanCrDmg += getClanAndCrDmgs(userIdToRaidHistory, userIdToSumCrDmg);
  	}
  	
  	for (Integer userId : userIdToSumCrDmg.keySet()) {
  		int userCrDmg = userIdToSumCrDmg.get(userId);
  		float contribution = ((float) userCrDmg / (float) sumClanCrDmg);
  		
  		userIdToContribution.put(userId, contribution);
  	}
  	
  	log.info("total clan cr dmg=" + sumClanCrDmg + "\t userIdToContribution=" +
  			userIdToContribution);
  	
  	return userIdToContribution;
  }
  
  
  //returns the cr dmg and computes running sum of user's cr dmgs
  private int getClanAndCrDmgs(Map<Integer, CepfuRaidHistory> userIdToRaidHistory,
  		Map<Integer, Integer> userIdToSumCrDmg) {
  	
  	int clanCrDmg = 0;//return value
  	
		//now for each user in this raid, sum up all their damages
		for (Integer userId : userIdToRaidHistory.keySet()) {
			CepfuRaidHistory raidHistory = userIdToRaidHistory.get(userId);
			
			//all of the clanCrDmg values for a CepfuRaidHistory are the same
			clanCrDmg = raidHistory.getClanCrDmg();
				
			int userCrDmg = raidHistory.getCrDmgDone();
			
			//sum up the damage for this raid with the current running sum for this user
			//user might not exist in current running sum
			int userCrDmgSoFar = 0;
			if (userIdToSumCrDmg.containsKey(userId)) {
				userCrDmgSoFar = userIdToSumCrDmg.get(userId);
			}
			
			int totalUserCrDmg = userCrDmg + userCrDmgSoFar;
			
			userIdToSumCrDmg.put(userId, totalUserCrDmg);
		}
		
		return clanCrDmg;
  }
  
}
