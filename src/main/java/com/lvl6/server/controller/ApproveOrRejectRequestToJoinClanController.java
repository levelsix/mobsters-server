package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ApproveOrRejectRequestToJoinClanRequestEvent;
import com.lvl6.events.response.ApproveOrRejectRequestToJoinClanResponseEvent;
import com.lvl6.events.response.RetrieveClanDataResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.ApproveOrRejectRequestToJoinClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.CreateClanStatus;
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.ApproveOrRejectRequestToJoinAction;
import com.lvl6.server.controller.actionobjects.SetClanDataProtoAction;
import com.lvl6.server.controller.actionobjects.StartUpResource;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class ApproveOrRejectRequestToJoinClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtil;

	@Autowired
	protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils;

	@Autowired
	protected ClanAvengeRetrieveUtil clanAvengeRetrieveUtil;

	@Autowired
	protected ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil;

	@Autowired
	protected ClanSearch clanSearch;
	
	@Autowired
	protected UpdateUtil updateUtil;
	
	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	protected MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;

	public ApproveOrRejectRequestToJoinClanController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ApproveOrRejectRequestToJoinClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		ApproveOrRejectRequestToJoinClanRequestProto reqProto = ((ApproveOrRejectRequestToJoinClanRequestEvent) event)
				.getApproveOrRejectRequestToJoinClanRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String requesterId = reqProto.getRequesterUuid();
		boolean accept = reqProto.getAccept();

		ApproveOrRejectRequestToJoinClanResponseProto.Builder resBuilder = ApproveOrRejectRequestToJoinClanResponseProto
				.newBuilder();
		resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);
		resBuilder.setAccept(accept);

		String clanId = "";
		if (senderProto.hasClan() && null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}

		UUID userUuid = null;
		UUID requesterUuid = null;
		UUID clanUuid = null;

		boolean invalidUuids = true;
		if (!clanId.isEmpty()) {
			try {
				userUuid = UUID.fromString(userId);
				requesterUuid = UUID.fromString(requesterId);
				clanUuid = UUID.fromString(clanId);
				invalidUuids = false;
			} catch (Exception e) {
				log.error(
						String.format(
								"UUID error. incorrect userId=%s, requesterId=%s, clanId=%s",
								userId, requesterId, clanId), e);
			}
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder
					.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
			ApproveOrRejectRequestToJoinClanResponseEvent resEvent = new ApproveOrRejectRequestToJoinClanResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setApproveOrRejectRequestToJoinClanResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);
			return;
		}

		boolean lockedClan = getLocker().lockClan(clanUuid);
		try {
//			Set<String> userIds = new HashSet<String>();
//			userIds.add(userId);
//			userIds.add(requesterId);
//			Map<String, User> idsToUsers = userRetrieveUtil
//					.getUsersByIds(userIds);
//
//			User user = null;//RetrieveUtils.userRetrieveUtils().getUserById(userId);
//			User requester = null;//RetrieveUtils.userRetrieveUtils().getUserById(requesterId);
//
//			if (idsToUsers.containsKey(userId)) {
//				user = idsToUsers.get(userId);
//			}
//			if (idsToUsers.containsKey(requesterId)) {
//				requester = idsToUsers.get(requesterId);
//			}
//
//			//			log.info(String.format(
//			//				"idsToUsers=%s, user=%s, requester=%s",
//			//				idsToUsers, user, requester));
//			List<Integer> clanSizeList = new ArrayList<Integer>();
//			boolean legitDecision = checkLegitDecision(resBuilder, lockedClan,
//					user, requester, accept, clanSizeList);
//
//			boolean success = false;
//			if (legitDecision) {
//				success = writeChangesToDB(user, requester, accept);
//			}
			
			ApproveOrRejectRequestToJoinAction aorrtja = new ApproveOrRejectRequestToJoinAction(userId,
					requesterId, lockedClan, userRetrieveUtil, updateUtil, deleteUtil, userClanRetrieveUtils);
			aorrtja.execute(resBuilder);

			// Only need to set clan data if user accepted.
			ClanDataProto cdp = null;
			MinimumUserProto requestMup = null;
			if (ApproveOrRejectRequestToJoinClanStatus.SUCCESS.equals(resBuilder.getStatus())) {
				Clan clan = null;

				if (accept) {
					clan = clanRetrieveUtil.getClanWithId(clanId);
					List<Date> lastChatTimeContainer = new ArrayList<Date>();

					StartUpResource fillMe = new StartUpResource(
							userRetrieveUtil, clanRetrieveUtil);

					SetClanDataProtoAction scdpa = new SetClanDataProtoAction(
							clanId, clan, aorrtja.getUser(), userId, lastChatTimeContainer,
							fillMe, clanHelpRetrieveUtil,
							clanAvengeRetrieveUtil, clanAvengeUserRetrieveUtil,
							clanChatPostRetrieveUtils,
							clanMemberTeamDonationRetrieveUtil,
							monsterSnapshotForUserRetrieveUtil,
							createInfoProtoUtils);
					cdp = scdpa.execute();

					//update clan cache
					updateClanCache(clanId, aorrtja.getClanSizeList(),
							lastChatTimeContainer,
							clan.isRequestToJoinRequired());

					log.info(String.format("ClanDataProto=%s", cdp));
					setResponseBuilderStuff(resBuilder, clan, aorrtja.getClanSizeList());
				}

				requestMup = createInfoProtoUtils
						.createMinimumUserProtoFromUserAndClan(aorrtja.getRequester(), clan);
				resBuilder.setRequester(requestMup);
			}

			ApproveOrRejectRequestToJoinClanResponseEvent resEvent = new ApproveOrRejectRequestToJoinClanResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setApproveOrRejectRequestToJoinClanResponseProto(resBuilder
					.build());

			//if fail only to sender
			if (!ApproveOrRejectRequestToJoinClanStatus.SUCCESS.equals(resBuilder.getStatus())) {
				server.writeEvent(resEvent);
			} else {
				//if success to clan and the requester
				server.writeClanEvent(resEvent, clanId);
				// Send message to the new guy
				ApproveOrRejectRequestToJoinClanResponseEvent resEvent2 = new ApproveOrRejectRequestToJoinClanResponseEvent(
						requesterId);
				resEvent2
						.setApproveOrRejectRequestToJoinClanResponseProto(resBuilder
								.build());
				//in case user is not online write an apns
				server.writeAPNSNotificationOrEvent(resEvent2);
				//server.writeEvent(resEvent2);

				sendClanData(event, requestMup, accept, requesterId, cdp);
			}
		} catch (Exception e) {
			log.error(
					"exception in ApproveOrRejectRequestToJoinClan processEvent",
					e);
			try {
				resBuilder
						.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
				ApproveOrRejectRequestToJoinClanResponseEvent resEvent = new ApproveOrRejectRequestToJoinClanResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setApproveOrRejectRequestToJoinClanResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in ApproveOrRejectRequestToJoinClan processEvent",
						e);
			}
		} finally {
			if (null != clanUuid && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}

//	//TODO: Issue one call to clan_for_user table instead of, atm, three 
//	private boolean checkLegitDecision(Builder resBuilder, boolean lockedClan,
//			User user, User requester, boolean accept,
//			List<Integer> clanSizeList) {
//
//		if (!lockedClan) {
//			log.error("could not get lock for clan.");
//			return false;
//		}
//
//		if (user == null || requester == null) {
//			resBuilder
//					.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
//			log.error(String.format("user is %s, requester is %s", user,
//					requester));
//			return false;
//		}
//		String clanId = user.getClanId();
//		String leaderStatus = UserClanStatus.LEADER.name();
//		String jrLeaderStatus = UserClanStatus.JUNIOR_LEADER.name();
//		String memberStatus = UserClanStatus.MEMBER.name();
//		String requestingStatus = UserClanStatus.REQUESTING.name();
//
//		List<String> statuses = new ArrayList<String>();
//		statuses.add(leaderStatus);
//		statuses.add(jrLeaderStatus);
//		statuses.add(UserClanStatus.CAPTAIN.name());
//		statuses.add(memberStatus);
//		statuses.add(requestingStatus);
//		Map<String, String> userIdsToStatuses = userClanRetrieveUtil
//				.getUserIdsToStatuses(clanId, statuses);
//
//		Set<String> uniqUserIds = getAuthorizedClanMembers(leaderStatus,
//				jrLeaderStatus, userIdsToStatuses);
//
//		String userId = user.getId();
//		if (!uniqUserIds.contains(userId)) {
//			resBuilder
//					.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_NOT_AUTHORIZED);
//			log.error(
//					"clan member can't approve clan join request. member={}, requester={}",
//					user, requester);
//			return false;
//		}
//		//check if requester is already in a clan
//		if (requester.getClanId() != null && !requester.getClanId().isEmpty()) {
//			resBuilder
//					.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_ALREADY_IN_A_CLAN);
//			log.error("trying to accept a user that is already in a clan");
//			//the other requests in user_clans table that have a status of 2 (requesting to join clan)
//			//are deleted later on in writeChangesToDB
//			return false;
//		}
//
//		//default not REQUESTING STATUS to stop processing if something's wrong
//		String requesterStatus = getRequesterStatus(requester, memberStatus,
//				userIdsToStatuses);
//		if (!requestingStatus.equals(requesterStatus)) {
//			resBuilder
//					.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_NOT_A_REQUESTER);
//			log.error("requester has not requested for clan with id {}", clanId);
//			return false;
//		}
//
//		//check out the size of the clan
//		int size = calculateClanSize(userIdsToStatuses);
//		int maxSize = ControllerConstants.CLAN__MAX_NUM_MEMBERS;
//		if (size >= maxSize && accept) {
//			resBuilder
//					.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_CLAN_IS_FULL);
//			log.warn(String.format(
//					"trying to add user into already full clan with id %s",
//					user.getClanId()));
//			return false;
//		}
//
//		clanSizeList.add(size);
//		return true;
//	}
//
//	private Set<String> getAuthorizedClanMembers(String leaderStatus,
//			String jrLeaderStatus, Map<String, String> userIdsAndStatuses) {
//		Set<String> uniqUserIds = new HashSet<String>();
//		if (null != userIdsAndStatuses && !userIdsAndStatuses.isEmpty()) {
//
//			//gather up only the leader or jr leader userIds
//			for (String userId : userIdsAndStatuses.keySet()) {
//				String status = userIdsAndStatuses.get(userId);
//				if (leaderStatus.equals(status)
//						|| jrLeaderStatus.equals(status)) {
//					uniqUserIds.add(userId);
//				}
//			}
//		}
//
//		return uniqUserIds;
//	}
//
//	private String getRequesterStatus(User requester, String memberStatus,
//			Map<String, String> userIdsAndStatuses) {
//		String retVal = memberStatus;
//
//		String requesterId = requester.getId();
//		if (userIdsAndStatuses.containsKey(requesterId)) {
//			retVal = userIdsAndStatuses.get(requesterId);
//		}
//
//		return retVal;
//	}
//
//	private int calculateClanSize(Map<String, String> userIdsToStatuses) {
//		int clanSize = 0;
//		if (null == userIdsToStatuses || userIdsToStatuses.isEmpty()) {
//			return clanSize;
//		}
//
//		//do not count requesting members
//		String requestingStatus = UserClanStatus.REQUESTING.name();
//		for (String status : userIdsToStatuses.values()) {
//			if (!requestingStatus.equalsIgnoreCase(status)) {
//				clanSize++;
//			}
//		}
//
//		return clanSize;
//	}
//
//	private boolean writeChangesToDB(User user, User requester, boolean accept) {
//		if (accept) {
//			if (!requester.updateRelativeCoinsAbsoluteClan(0, user.getClanId())) {
//				log.error(String.format(
//						"problem with change requester %s clan id to %s",
//						requester, user.getClanId()));
//				return false;
//			}
//			if (!UpdateUtils.get().updateUserClanStatus(requester.getId(),
//					user.getClanId(), UserClanStatus.MEMBER)) {
//				log.error(String
//						.format("problem updating user clan status to MEMBER for requester %s and clan id %s",
//								requester, user.getClanId()));
//				return false;
//			}
//			DeleteUtils.get().deleteUserClansForUserExceptSpecificClan(
//					requester.getId(), user.getClanId());
//			return true;
//		} else {
//			if (!DeleteUtils.get().deleteUserClan(requester.getId(),
//					user.getClanId())) {
//				log.error(String
//						.format("problem deleting UserClan for requesterId %s, and clan id %s",
//								requester.getId(), user.getClanId()));
//				return false;
//			}
//			return true;
//		}
//	}

	//	private ClanDataProto setClanData( String clanId,
	//		Clan c, User u, String userId, List<Date> lastChatTimeContainer)
	//	{
	//		log.info("setting clanData proto for clan {}", c);
	//		ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();
	//		StartUpResource fillMe = new StartUpResource(
	//			userRetrieveUtil, clanRetrieveUtil );
	//
	//		SetClanChatMessageAction sccma = new SetClanChatMessageAction(
	//			cdpb, u, clanChatPostRetrieveUtils);
	//		sccma.setUp(fillMe);
	//		SetClanHelpingsAction scha = new SetClanHelpingsAction(
	//			cdpb, u, userId, clanHelpRetrieveUtil);
	//		scha.setUp(fillMe);
	//		SetClanRetaliationsAction scra = new SetClanRetaliationsAction(
	//			cdpb, u, userId, clanAvengeRetrieveUtil,
	//			clanAvengeUserRetrieveUtil);
	//		scra.setUp(fillMe);
	//		
	//		
	//		fillMe.fetchUsersOnly();
	//		fillMe.addClan(clanId, c);
	//
	//		sccma.execute(fillMe);
	//		scha.execute(fillMe);
	//		scra.execute(fillMe);
	//		
	//		lastChatTimeContainer.add(sccma.getLastClanChatPostTime());
	//
	//		return cdpb.build();
	//	}

	private void updateClanCache(String clanId, List<Integer> clanSizeList,
			List<Date> lastChatTimeContainer, boolean requestToJoinRequired) {
		//need to account for this user joining clan
		int clanSize = clanSizeList.get(0) + 1;
		Date lastChatTime = lastChatTimeContainer.get(0);

		if (requestToJoinRequired) {
			clanSize = ClanSearch.penalizedClanSize;
			lastChatTime = ControllerConstants.INCEPTION_DATE;
		}

		clanSearch.updateClanSearchRank(clanId, clanSize, lastChatTime);
	}

	private void setResponseBuilderStuff(Builder resBuilder, Clan clan,
			List<Integer> clanSizeList) {

		resBuilder.setMinClan(createInfoProtoUtils
				.createMinimumClanProtoFromClan(clan));

		int size = clanSizeList.get(0);
		resBuilder.setFullClan(createInfoProtoUtils
				.createFullClanProtoWithClanSize(clan, size));
	}

	private void sendClanData(RequestEvent event,
			MinimumUserProto requesterMup, boolean accepted,
			String requesterId, ClanDataProto cdp) {
		if (!accepted || null == cdp) {
			log.warn(String.format("accepted=%s, cdp=%s", accepted, cdp));
			return;
		}
		log.info(String.format("writingClanData to clan. %s", cdp));
		RetrieveClanDataResponseEvent rcdre = new RetrieveClanDataResponseEvent(
				requesterId);
		rcdre.setTag(event.getTag());
		RetrieveClanDataResponseProto.Builder rcdrpb = RetrieveClanDataResponseProto
				.newBuilder();
		rcdrpb.setMup(requesterMup);
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

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtil() {
		return userClanRetrieveUtil;
	}

	public void setUserClanRetrieveUtil(
			UserClanRetrieveUtils2 userClanRetrieveUtil) {
		this.userClanRetrieveUtil = userClanRetrieveUtil;
	}

	public ClanHelpRetrieveUtil getClanHelpRetrieveUtil() {
		return clanHelpRetrieveUtil;
	}

	public void setClanHelpRetrieveUtil(
			ClanHelpRetrieveUtil clanHelpRetrieveUtil) {
		this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
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
