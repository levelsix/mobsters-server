package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RequestJoinClanRequestEvent;
import com.lvl6.events.response.RequestJoinClanResponseEvent;
import com.lvl6.events.response.RetrieveClanDataResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.misc.Notification;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.PersistentClanEventUserInfoProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.RequestJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto.RequestJoinClanStatus;
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils2;
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils2;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.SetClanDataProtoAction;
import com.lvl6.server.controller.actionobjects.StartUpResource;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
@DependsOn("gameServer")
public class RequestJoinClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;

	@Autowired
	protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected ClanEventPersistentForClanRetrieveUtils2 clanEventPersistentForClanRetrieveUtils;

	@Autowired
	protected ClanEventPersistentForUserRetrieveUtils2 clanEventPersistentForUserRetrieveUtils;

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils;

	@Autowired
	protected ClanAvengeRetrieveUtil clanAvengeRetrieveUtil;

	@Autowired
	protected ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil;

	@Autowired
	protected ClanSearch clanSearch;

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	protected MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;


	public RequestJoinClanController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RequestJoinClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REQUEST_JOIN_CLAN_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
		RequestJoinClanRequestProto reqProto = ((RequestJoinClanRequestEvent) event)
				.getRequestJoinClanRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String clanId = reqProto.getClanUuid();
		String userId = senderProto.getUserUuid();

		Timestamp clientTime = null;

		if (reqProto.hasClientTime() && reqProto.getClientTime() > 0) {
			clientTime = new Timestamp(reqProto.getClientTime());
		} else {
			clientTime = new Timestamp(new Date().getTime());
		}

		RequestJoinClanResponseProto.Builder resBuilder = RequestJoinClanResponseProto
				.newBuilder();
		resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);
		resBuilder.setClanUuid(clanId);
		resBuilder.setClientTime(clientTime.getTime());

		UUID clanUuid = null;
		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);
			clanUuid = UUID.fromString(clanId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, clanId=%s", userId,
					clanId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
			RequestJoinClanResponseEvent resEvent = new RequestJoinClanResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRequestJoinClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		boolean lockedClan = false;
		if (clanUuid != null) {
			lockedClan = getLocker().lockClan(clanUuid);
		}
		try {
			User user = getUserRetrieveUtils().getUserById(userId);
			Clan clan = getClanRetrieveUtils().getClanWithId(clanId);
			boolean requestToJoinRequired = false; //to be set if request is legit

			List<Integer> clanSizeList = new ArrayList<Integer>();
			boolean legitRequest = checkLegitRequest(resBuilder, lockedClan,
					user, clan, clanSizeList);

			boolean successful = false;
			if (legitRequest) {
				requestToJoinRequired = clan.isRequestToJoinRequired();
				int battlesWon = getPvpLeagueForUserRetrieveUtil()
						.getPvpBattlesWonForUser(userId);

				//setting minimum user proto for clans based on clan join type
				if (requestToJoinRequired) {
					//clan raid contribution stuff
					MinimumUserProtoForClans mupfc = createInfoProtoUtils
							.createMinimumUserProtoForClans(user, null,
									UserClanStatus.REQUESTING, 0F, battlesWon,
									null);
					resBuilder.setRequester(mupfc);
				} else {
					//clan raid contribution stuff
					MinimumUserProtoForClans mupfc = createInfoProtoUtils
							.createMinimumUserProtoForClans(user, clan,
									UserClanStatus.MEMBER, 0F, battlesWon, null);
					resBuilder.setRequester(mupfc);
				}
				successful = writeChangesToDB(resBuilder, user, clan,
						clientTime);
			}

			// Only need to set clan data if it's a successful join.
			ClanDataProto cdp = null;
			if (successful && !requestToJoinRequired) {
				setResponseBuilderStuff(resBuilder, clan, clanSizeList);
				sendClanRaidStuff(resBuilder, clan, user);

				List<Date> lastChatTimeContainer = new ArrayList<Date>();

				StartUpResource fillMe = new StartUpResource(
						getUserRetrieveUtils(), getClanRetrieveUtils());

				SetClanDataProtoAction scdpa = new SetClanDataProtoAction(
						clanId, clan, user, userId, lastChatTimeContainer,
						fillMe, clanHelpRetrieveUtil, clanAvengeRetrieveUtil,
						clanAvengeUserRetrieveUtil, clanChatPostRetrieveUtils,
						clanMemberTeamDonationRetrieveUtil,
						monsterSnapshotForUserRetrieveUtil, createInfoProtoUtils);
				cdp = scdpa.execute();

				//update clan cache
				updateClanCache(clanId, clanSizeList, lastChatTimeContainer);

			}
			RequestJoinClanResponseEvent resEvent = new RequestJoinClanResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRequestJoinClanResponseProto(resBuilder.build());
			/* I think I meant write to the clan leader if leader is not on
			 
			//in case user is not online write an apns
			server.writeAPNSNotificationOrEvent(resEvent);
			//server.writeEvent(resEvent);
			 */
			server.writeEvent(resEvent);

			if (successful) {
				List<String> userIds = new ArrayList<String>();
				userIds.add(userId);
				//get user's current monster team
				Map<String, List<MonsterForUser>> userIdToTeam = getMonsterForUserRetrieveUtils()
						.getUserIdsToMonsterTeamForUserIds(userIds);
				UserCurrentMonsterTeamProto curTeamProto = createInfoProtoUtils
						.createUserCurrentMonsterTeamProto(userId,
								userIdToTeam.get(userId));
				resBuilder.setRequesterMonsters(curTeamProto);

				resBuilder.clearEventDetails(); //could just get rid of this line
				resBuilder.clearClanUsersDetails(); //could just get rid of this line
				resEvent.setRequestJoinClanResponseProto(resBuilder.build());
				server.writeClanEvent(resEvent, clan.getId());

				if (!requestToJoinRequired) {
					//null PvpLeagueFromUser means will pull from hazelcast instead
					UpdateClientUserResponseEvent resEventUpdate = miscMethods
							.createUpdateClientUserResponseEventAndUpdateLeaderboard(
									user, null, clan);
					resEventUpdate.setTag(event.getTag());
					server.writeEvent(resEventUpdate);

					//this is so user gets all up to date clan information
					sendClanData(event, senderProto, userId, cdp);
				}

				// handled by client
				//notifyClan(user, clan, requestToJoinRequired); //write to clan leader or clan
			}
		} catch (Exception e) {
			log.error("exception in RequestJoinClan processEvent", e);
			try {
				resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
				RequestJoinClanResponseEvent resEvent = new RequestJoinClanResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRequestJoinClanResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in RequestJoinClan processEvent", e);
			}
		} finally {
			if (clanUuid != null && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}

	private boolean checkLegitRequest(Builder resBuilder, boolean lockedClan,
			User user, Clan clan, List<Integer> clanSizeList) {

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}
		if (user == null || clan == null) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
			log.error(String.format("user is %s, clan is ", user, clan));
			return false;
		}
		String clanId = user.getClanId();
		if (clanId != null) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_ALREADY_IN_CLAN);
			log.error(String.format("user is already in clan with id %s",
					clanId));
			return false;
		}

		//user does not have clan id, so use the clan's id
		clanId = clan.getId();

		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		statuses.add(UserClanStatus.REQUESTING.name());
		Map<String, String> userIdsToStatuses = userClanRetrieveUtils
				.getUserIdsToStatuses(clanId, statuses);

		String userId = user.getId();
		if (null != userIdsToStatuses && userIdsToStatuses.containsKey(userId)) {
			resBuilder
					.setStatus(RequestJoinClanStatus.FAIL_REQUEST_ALREADY_FILED);
			log.error(String.format("user clan already exists for this: %s",
					userIdsToStatuses.get(userId)));
			return false;
		}

		if (ControllerConstants.CLAN__CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT
				.equals(clanId)) {
			return true;
		}

		//can request as much as desired
		if (clan.isRequestToJoinRequired()) {
			return true;
		}

		//check out the size of the clan since user can just join
		int size = calculateClanSize(userIdsToStatuses);
		int maxSize = ControllerConstants.CLAN__MAX_NUM_MEMBERS;
		if (size >= maxSize) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_CLAN_IS_FULL);
			log.warn(String.format(
					"trying to join full clan with id %s, cur size=%s", clanId,
					maxSize));
			return false;
		}
		clanSizeList.add(size);
		//resBuilder.setStatus(RequestJoinClanStatus.SUCCESS);
		return true;
	}

	private int calculateClanSize(Map<String, String> userIdsToStatuses) {
		int clanSize = 0;
		if (null == userIdsToStatuses || userIdsToStatuses.isEmpty()) {
			return clanSize;
		}

		//do not count requesting members
		String requestingStatus = UserClanStatus.REQUESTING.name();
		for (String status : userIdsToStatuses.values()) {
			if (!requestingStatus.equalsIgnoreCase(status)) {
				clanSize++;
			}
		}

		return clanSize;
	}

	private boolean writeChangesToDB(Builder resBuilder, User user, Clan clan,
			Timestamp clientTime) {
		//clan can be open, or user needs to send a request to join the clan
		boolean requestToJoinRequired = clan.isRequestToJoinRequired();
		String userId = user.getId();
		String clanId = clan.getId(); //user.getClanId(); //this is null still...
		String userClanStatus;
		if (requestToJoinRequired) {
			userClanStatus = UserClanStatus.REQUESTING.name();
			resBuilder.setStatus(RequestJoinClanStatus.SUCCESS_REQUEST);
		} else {
			userClanStatus = UserClanStatus.MEMBER.name();
			resBuilder.setStatus(RequestJoinClanStatus.SUCCESS_JOIN);
		}

		if (!InsertUtils.get().insertUserClan(userId, clanId, userClanStatus,
				clientTime)) {
			log.error(String
					.format("problem inserting user clan data for user=%s, and clan id %s",
							user, clanId));
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
			return false;
		}

		boolean deleteUserClanInserted = false;
		//update user to reflect he joined clan if the clan does not require a request to join
		if (!requestToJoinRequired) {
			if (!user.updateRelativeCoinsAbsoluteClan(0, clanId)) {
				//could not change clan_id for user
				String preface = "could not change clanId for";
				String postface = "Deleting user clan that was just created.";
				log.error(String.format("%s requester %s to %s. %s", preface,
						user, clanId, postface));
				deleteUserClanInserted = true;
			} else {
				//successfully changed clan_id in current user
				//get rid of all other join clan requests
				//don't know if this next line will always work...
				DeleteUtils.get().deleteUserClansForUserExceptSpecificClan(
						userId, clanId);
			}
		}

		boolean successful = true;
		//in case things above didn't work out
		if (deleteUserClanInserted) {
			if (!DeleteUtils.get().deleteUserClan(userId, clanId)) {
				log.error("unexpected error: could not delete user clan inserted.");
			}
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
			successful = false;
		}
		return successful;
	}

	private void notifyClan(User aUser, Clan aClan,
			boolean requestToJoinRequired) {
		String clanId = aUser.getClanId();
		List<String> statuses = Collections.singletonList(UserClanStatus.LEADER
				.name());
		List<String> userIds = getUserClanRetrieveUtils()
				.getUserIdsWithStatuses(clanId, statuses);
		//should just be one id
		String clanOwnerId = null;
		if (null != userIds && !userIds.isEmpty()) {
			clanOwnerId = userIds.get(0);
		}

		int level = aUser.getLevel();
		String requester = aUser.getName();
		Notification aNote = new Notification();

		if (requestToJoinRequired) {
			//notify leader someone requested to join clan
			aNote.setAsUserRequestedToJoinClan(level, requester);
		} else {
			//notify whole clan someone joined the clan <- too annoying, just leader
			//TODO: Maybe exclude the guy who joined from receiving the notification?
			aNote.setAsUserJoinedClan(level, requester);
		}
		miscMethods.writeNotificationToUser(aNote, server, clanOwnerId);

		//    GeneralNotificationResponseProto.Builder notificationProto =
		//        aNote.generateNotificationBuilder();
		//    GeneralNotificationResponseEvent aNotification =
		//        new GeneralNotificationResponseEvent(clanOwnerId);
		//    aNotification.setGeneralNotificationResponseProto(notificationProto.build());
		//    
		//    server.writeAPNSNotificationOrEvent(aNotification);
	}

	private void setResponseBuilderStuff(Builder resBuilder, Clan clan,
			List<Integer> clanSizeList) {

		resBuilder.setMinClan(createInfoProtoUtils
				.createMinimumClanProtoFromClan(clan));
		int size = clanSizeList.get(0);
		resBuilder.setFullClan(createInfoProtoUtils
				.createFullClanProtoWithClanSize(clan, size));
	}

	private void sendClanRaidStuff(Builder resBuilder, Clan clan, User user) {
		if (!RequestJoinClanStatus.SUCCESS_JOIN.equals(resBuilder.getStatus())) {
			return;
		}

		String clanId = clan.getId();
		ClanEventPersistentForClan cepfc = getClanEventPersistentForClanRetrieveUtils()
				.getPersistentEventForClanId(clanId);

		//send to the user the current clan raid details for the clan
		if (null != cepfc) {
			PersistentClanEventClanInfoProto updatedEventDetails = createInfoProtoUtils
					.createPersistentClanEventClanInfoProto(cepfc);
			resBuilder.setEventDetails(updatedEventDetails);
		}

		Map<String, ClanEventPersistentForUser> userIdToCepfu = getClanEventPersistentForUserRetrieveUtils()
				.getPersistentEventUserInfoForClanId(clanId);

		//send to the user the current clan raid details for all the users
		if (!userIdToCepfu.isEmpty()) {
			//whenever server has this information send it to the clients
			List<String> userMonsterIds = monsterStuffUtils
					.getUserMonsterIdsInClanRaid(userIdToCepfu);

			Map<String, MonsterForUser> idsToUserMonsters = getMonsterForUserRetrieveUtils()
					.getSpecificUserMonsters(userMonsterIds);

			for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
				PersistentClanEventUserInfoProto pceuip = createInfoProtoUtils
						.createPersistentClanEventUserInfoProto(cepfu,
								idsToUserMonsters, null);
				resBuilder.addClanUsersDetails(pceuip);
			}

		}
	}

	//  private ClanDataProto setClanData( String clanId,
	//	  Clan c, User u, String userId, List<Date> lastChatTimeContainer)
	//  {
	//	  log.info("setting clanData proto for clan {}", c);
	//	  ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();
	//	  StartUpResource fillMe = new StartUpResource(
	//		  userRetrieveUtil, clanRetrieveUtil );
	//	  
	//	  SetClanChatMessageAction sccma = new SetClanChatMessageAction(
	//		  cdpb, u, clanChatPostRetrieveUtils);
	//	  sccma.setUp(fillMe);
	//	  SetClanHelpingsAction scha = new SetClanHelpingsAction(
	//		  cdpb, u, userId, clanHelpRetrieveUtil);
	//	  scha.setUp(fillMe);
	//	  SetClanRetaliationsAction scra = new SetClanRetaliationsAction(
	//		  cdpb, u, userId, clanAvengeRetrieveUtil,
	//		  clanAvengeUserRetrieveUtil);
	//	  scra.setUp(fillMe);
	//
	//
	//	  fillMe.fetchUsersOnly();
	//	  fillMe.addClan(clanId, c);
	//
	//	  sccma.execute(fillMe);
	//	  scha.execute(fillMe);
	//	  scra.execute(fillMe);
	//	  
	//	  lastChatTimeContainer.add(sccma.getLastClanChatPostTime());
	//	  
	//	  return cdpb.build();
	//  }

	private void updateClanCache(String clanId, List<Integer> clanSizeList,
			List<Date> lastChatTimeContainer) {
		//need to account for this user joining clan
		int clanSize = clanSizeList.get(0) + 1;
		Date lastChatTime = lastChatTimeContainer.get(0);

		clanSearch.updateClanSearchRank(clanId, clanSize, lastChatTime);
	}

	private void sendClanData(RequestEvent event, MinimumUserProto senderProto,
			String userId, ClanDataProto cdp) {
		if (null == cdp) {
			return;
		}
		RetrieveClanDataResponseEvent rcdre = new RetrieveClanDataResponseEvent(
				userId);
		rcdre.setTag(event.getTag());
		RetrieveClanDataResponseProto.Builder rcdrpb = RetrieveClanDataResponseProto
				.newBuilder();
		rcdrpb.setMup(senderProto);
		rcdrpb.setClanData(cdp);

		rcdre.setRetrieveClanDataResponseProto(rcdrpb.build());
		server.writeEvent(rcdre);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public ClanHelpRetrieveUtil getClanHelpRetrieveUtil() {
		return clanHelpRetrieveUtil;
	}

	public void setClanHelpRetrieveUtil(
			ClanHelpRetrieveUtil clanHelpRetrieveUtil) {
		this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtil = clanRetrieveUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtil = userRetrieveUtils;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public ClanEventPersistentForClanRetrieveUtils2 getClanEventPersistentForClanRetrieveUtils() {
		return clanEventPersistentForClanRetrieveUtils;
	}

	public void setClanEventPersistentForClanRetrieveUtils(
			ClanEventPersistentForClanRetrieveUtils2 clanEventPersistentForClanRetrieveUtils) {
		this.clanEventPersistentForClanRetrieveUtils = clanEventPersistentForClanRetrieveUtils;
	}

	public ClanEventPersistentForUserRetrieveUtils2 getClanEventPersistentForUserRetrieveUtils() {
		return clanEventPersistentForUserRetrieveUtils;
	}

	public void setClanEventPersistentForUserRetrieveUtils(
			ClanEventPersistentForUserRetrieveUtils2 clanEventPersistentForUserRetrieveUtils) {
		this.clanEventPersistentForUserRetrieveUtils = clanEventPersistentForUserRetrieveUtils;
	}

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtils() {
		return clanChatPostRetrieveUtils;
	}

	public void setClanChatPostRetrieveUtils(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils) {
		this.clanChatPostRetrieveUtils = clanChatPostRetrieveUtils;
	}

	public ClanAvengeRetrieveUtil getClanAvengeRetrieveUtil() {
		return clanAvengeRetrieveUtil;
	}

	public void setClanAvengeRetrieveUtil(
			ClanAvengeRetrieveUtil clanAvengeRetrieveUtil) {
		this.clanAvengeRetrieveUtil = clanAvengeRetrieveUtil;
	}

	public ClanAvengeUserRetrieveUtil getClanAvengeUserRetrieveUtil() {
		return clanAvengeUserRetrieveUtil;
	}

	public void setClanAvengeUserRetrieveUtil(
			ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil) {
		this.clanAvengeUserRetrieveUtil = clanAvengeUserRetrieveUtil;
	}

	public ClanSearch getClanSearch() {
		return clanSearch;
	}

	public void setClanSearch(ClanSearch clanSearch) {
		this.clanSearch = clanSearch;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ClanMemberTeamDonationRetrieveUtil getClanMemberTeamDonationRetrieveUtil() {
		return clanMemberTeamDonationRetrieveUtil;
	}

	public void setClanMemberTeamDonationRetrieveUtil(
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil) {
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
	}

	public MonsterSnapshotForUserRetrieveUtil getMonsterSnapshotForUserRetrieveUtil() {
		return monsterSnapshotForUserRetrieveUtil;
	}

	public void setMonsterSnapshotForUserRetrieveUtil(
			MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil) {
		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
	}

}
