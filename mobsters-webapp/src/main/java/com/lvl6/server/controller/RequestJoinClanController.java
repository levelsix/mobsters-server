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
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.HazelcastClanSearchImpl;
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
import com.lvl6.server.controller.actionobjects.RequestJoinClanAction;
import com.lvl6.server.controller.actionobjects.SetClanDataProtoAction;
import com.lvl6.server.controller.actionobjects.StartUpResource;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component

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
	protected HazelcastClanSearchImpl hzClanSearch;

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	protected MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	protected InsertUtil insertUtil;
	
	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;


	public RequestJoinClanController() {
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
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
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
			log.error("UUID error. incorrect userId={}, clanId={}", userId,
					clanId);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(RequestJoinClanStatus.FAIL_OTHER);
			RequestJoinClanResponseEvent resEvent = new RequestJoinClanResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean lockedClan = false;
		if (clanUuid != null) {
			lockedClan = getLocker().lockClan(clanUuid);
		}
		try {
			RequestJoinClanAction rjca = new RequestJoinClanAction(userId, clanId,
					clientTime, lockedClan, userRetrieveUtil, insertUtil, deleteUtil,
					clanRetrieveUtils, userClanRetrieveUtils);

			rjca.execute(resBuilder);

			//setting minimum user proto for clans based on clan join type
			if (rjca.isRequestToJoinRequired()) {
				//clan raid contribution stuff
				MinimumUserProtoForClans mupfc = createInfoProtoUtils
						.createMinimumUserProtoForClans(rjca.getUser(), null,
								UserClanStatus.REQUESTING, 0F, -1,
								null);
				resBuilder.setRequester(mupfc);
			} else {
				//clan raid contribution stuff
				MinimumUserProtoForClans mupfc = createInfoProtoUtils
						.createMinimumUserProtoForClans(rjca.getUser(), rjca.getClan(),
								UserClanStatus.MEMBER, 0F, -1, null);
				resBuilder.setRequester(mupfc);
			}

			// Only need to set clan data if it's a successful join.
			ClanDataProto cdp = null;
			if (RequestJoinClanStatus.SUCCESS_JOIN.equals(resBuilder.getStatus()) && !rjca.isRequestToJoinRequired()) {
				setResponseBuilderStuff(resBuilder, rjca.getClan(), rjca.getClanSizeList());
				sendClanRaidStuff(resBuilder, rjca.getClan(), rjca.getUser());

				List<Date> lastChatTimeContainer = new ArrayList<Date>();

				StartUpResource fillMe = new StartUpResource(
						getUserRetrieveUtils(), getClanRetrieveUtils());

				SetClanDataProtoAction scdpa = new SetClanDataProtoAction(
						clanId, rjca.getClan(), rjca.getUser(), userId, lastChatTimeContainer,
						fillMe, clanHelpRetrieveUtil, clanAvengeRetrieveUtil,
						clanAvengeUserRetrieveUtil, clanChatPostRetrieveUtils,
						clanMemberTeamDonationRetrieveUtil,
						monsterSnapshotForUserRetrieveUtil, createInfoProtoUtils);
				cdp = scdpa.execute();

				//update clan cache
				updateClanCache(clanId, rjca.getClanSizeList(), lastChatTimeContainer);

			}
			RequestJoinClanResponseEvent resEvent = new RequestJoinClanResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			/* I think I meant write to the clan leader if leader is not on
			 
			//in case user is not online write an apns
			responses.apnsResponseEvents().add((resEvent);
			//responses.normalResponseEvents().add(resEvent);
			 */
			responses.normalResponseEvents().add(resEvent);

			if (RequestJoinClanStatus.SUCCESS_JOIN.equals(resBuilder.getStatus()) || 
					RequestJoinClanStatus.SUCCESS_REQUEST.equals(resBuilder.getStatus())) {
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
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));

				if (!rjca.isRequestToJoinRequired()) {
					//null PvpLeagueFromUser means will pull from hazelcast instead
					UpdateClientUserResponseEvent resEventUpdate = miscMethods
							.createUpdateClientUserResponseEventAndUpdateLeaderboard(
									rjca.getUser(), null, rjca.getClan());
					resEventUpdate.setTag(event.getTag());
					responses.normalResponseEvents().add(resEventUpdate);
					responses.setUserId(userId);
					responses.setClanChanged(true);
					responses.setNewClanId(clanId);

					//this is so user gets all up to date clan information
					sendClanData(event, senderProto, userId, cdp, responses);
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
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in RequestJoinClan processEvent", e);
			}
		} finally {
			if (clanUuid != null && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}




	private void notifyClan(User aUser, Clan aClan, boolean requestToJoinRequired, ToClientEvents responses) {

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
		responses.normalResponseEvents().add(miscMethods.writeNotificationToUser(aNote, clanOwnerId));

		//    GeneralNotificationResponseProto.Builder notificationProto =
		//        aNote.generateNotificationBuilder();
		//    GeneralNotificationResponseEvent aNotification =
		//        new GeneralNotificationResponseEvent(clanOwnerId);
		//    aNotification.setGeneralNotificationResponseProto(notificationProto.build());
		//    
		//    responses.apnsResponseEvents().add((aNotification);
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

		hzClanSearch.updateRankForClanSearch(clanId, new Date(), 0, 0, 0, 0, 1);
	}

	private void sendClanData(RequestEvent event, MinimumUserProto senderProto,	String userId, ClanDataProto cdp, ToClientEvents responses) {
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
		responses.normalResponseEvents().add(rcdre);
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


	public HazelcastClanSearchImpl getHzClanSearch() {
		return hzClanSearch;
	}

	public void setHzClanSearch(HazelcastClanSearchImpl hzClanSearch) {
		this.hzClanSearch = hzClanSearch;
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
