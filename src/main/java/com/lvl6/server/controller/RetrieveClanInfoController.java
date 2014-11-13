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
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.CepfuRaidHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

@Component @DependsOn("gameServer") public class RetrieveClanInfoController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected TimeUtils timeUtils;
  
  @Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;

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

    log.info(String.format("reqProto=%s", reqProto));
    
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
      //User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());

      boolean legitCreate = checkLegitCreate(resBuilder, clanName, clanId);

      if (legitCreate) {
        setBuilder(reqProto, clanId, clanName, beforeClanId, grabType, resBuilder);
      }

      RetrieveClanInfoResponseEvent resEvent = new RetrieveClanInfoResponseEvent(senderProto.getUserUuid());
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
  

  private void setBuilder(
	  RetrieveClanInfoRequestProto reqProto,
	  int clanId,
	  String clanName,
	  int beforeClanId,
	  ClanInfoGrabType grabType,
	  RetrieveClanInfoResponseProto.Builder resBuilder )
  {
	  if (!reqProto.hasClanName() && !reqProto.hasClanId()) {
		  List<Clan> clanList = null;
		  if (beforeClanId <= 0) {
			  clanList = ClanRetrieveUtils.getMostRecentClans(ControllerConstants.RETRIEVE_CLANS__NUM_CLANS_CAP);
		  } else {
			  clanList = ClanRetrieveUtils.getMostRecentClansBeforeClanId(ControllerConstants.RETRIEVE_CLANS__NUM_CLANS_CAP, beforeClanId);
			  resBuilder.setBeforeThisClanId(reqProto.getBeforeThisClanId());
		  }

		  log.info(String.format("clanList=%s", clanList));
		  setClanProtosWithSize(resBuilder, clanList);
		  return;
	  }
	  
	  
	  
	  List<Clan> clanList = null;
	  if (grabType == ClanInfoGrabType.ALL || grabType == ClanInfoGrabType.CLAN_INFO) {
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
	  
	  if (ClanInfoGrabType.CLAN_INFO.equals(grabType)) {
		  return;
	  }
	  
	  //map clanId->clan so easier to create protos
	  Map<Integer, Clan> clanIdToClan = new HashMap<Integer, Clan>();
	  if (ClanInfoGrabType.MEMBERS.equals(grabType)) {
		  
		  Clan clan = ClanRetrieveUtils.getClanWithId(clanId);
		  clanIdToClan.put(clanId, clan);
		  
	  } else if (null != clanList){
		  for (Clan c : clanList) {
			  clanIdToClan.put(c.getId(), c);
		  }
	  }
	  
	  
	  if (grabType == ClanInfoGrabType.ALL || grabType == ClanInfoGrabType.MEMBERS) {
		  log.info("getUserClansRelatedToClan clanId=" + clanId);
		  List<UserClan> userClans = RetrieveUtils.userClanRetrieveUtils().getUserClansRelatedToClan(clanId);
		  log.info("user clans related to clanId:" + clanId + "\t " + userClans);
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
			  Clan c = null;
			  int cId = u.getClanId();
			  
			  if (clanIdToClan.containsKey(cId)) {
				  c = clanIdToClan.get(cId);
			  }

			  //user might not have a clan raid entry, so
			  float clanRaidContribution = 0F;
			  if (userIdToClanRaidContribution.containsKey(userId)) {
				  clanRaidContribution = userIdToClanRaidContribution.get(userId);
			  }

			  //might be better if just got all user's battle wons from db
			  //instead of one by one from hazelcast 
			  int battlesWon = getBattlesWonForUser(userId);
			  MinimumUserProtoForClans minUser = CreateInfoProtoUtils
				  .createMinimumUserProtoForClans(u, c, uc.getStatus(),
					  clanRaidContribution, battlesWon);
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
  }
  
  private  void setClanProtosWithSize(Builder resBuilder, List<Clan> clanList) {

    if (null == clanList || clanList.isEmpty()) {
    	return;
    }
    List<Integer> clanIds = ClanRetrieveUtils.getClanIdsFromClans(clanList);
    
    List<String> statuses = new ArrayList<String>();
    statuses.add(UserClanStatus.LEADER.name());
    statuses.add(UserClanStatus.JUNIOR_LEADER.name());
    statuses.add(UserClanStatus.CAPTAIN.name());
    statuses.add(UserClanStatus.MEMBER.name());

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
			
			//all of the clanCrDmg values for "userIdToRaidHistory" are the same
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
  
  private int getBattlesWonForUser(int userId) {
	  PvpUser pu = getHazelcastPvpUtil().getPvpUser(userId);
	  
	  int battlesWon = 0;
	  if (null != pu) {
		battlesWon = pu.getBattlesWon();  
	  }
	  
	  return battlesWon;
  }


  public TimeUtils getTimeUtils() {
	  return timeUtils;
  }
  public void setTimeUtils(TimeUtils timeUtils) {
	  this.timeUtils = timeUtils;
  }

  public HazelcastPvpUtil getHazelcastPvpUtil() {
	  return hazelcastPvpUtil;
  }
  public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
	  this.hazelcastPvpUtil = hazelcastPvpUtil;
  }
}
