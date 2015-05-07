package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.ApproveOrRejectRequestToJoinClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.Builder;
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
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

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
	protected ClanStuffUtils clanStuffUtils;
	
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
			ApproveOrRejectRequestToJoinAction aorrtja = new ApproveOrRejectRequestToJoinAction(userId,
					requesterId, accept, lockedClan, userRetrieveUtil, updateUtil, deleteUtil, 
					userClanRetrieveUtils, clanStuffUtils);
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
