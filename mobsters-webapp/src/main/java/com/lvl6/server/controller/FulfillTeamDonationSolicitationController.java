package com.lvl6.server.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.FulfillTeamDonationSolicitationRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.FulfillTeamDonationSolicitationResponseEvent;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationRequestProto;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto;
import com.lvl6.proto.MonsterStuffProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.FulfillTeamDonationSolicitationAction;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class FulfillTeamDonationSolicitationController extends EventController {

	private static Logger log = LoggerFactory.getLogger(FulfillTeamDonationSolicitationController.class);


	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected ClanStuffUtils clanStuffUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;


	@Autowired
	protected TimeUtils timeUtils;


	public FulfillTeamDonationSolicitationController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new FulfillTeamDonationSolicitationRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_FULFILL_TEAM_DONATION_SOLICITATION_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		FulfillTeamDonationSolicitationRequestProto reqProto = ((FulfillTeamDonationSolicitationRequestEvent) event)
				.getFulfillTeamDonationSolicitationRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String donatorId = senderProto.getUserUuid();
		FullUserMonsterProto fump = reqProto.getFump();
		Date clientTime = new Date(reqProto.getClientTime());
		ClanMemberTeamDonationProto solicitationProto = reqProto
				.getSolicitation();

		MonsterSnapshotForUser msfu = monsterStuffUtils
				.javafyFullUserMonsterProto(fump);
		String solicitorId = null;
		ClanMemberTeamDonation cmtd = clanStuffUtils
				.javafyClanMemberTeamDonationProto(solicitationProto);

		FulfillTeamDonationSolicitationResponseProto.Builder resBuilder = FulfillTeamDonationSolicitationResponseProto
				.newBuilder();
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = null;

		if (null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}

		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			FulfillTeamDonationSolicitationResponseEvent resEvent = new FulfillTeamDonationSolicitationResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(clientTime, new Date()) >
				ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			FulfillTeamDonationSolicitationResponseEvent resEvent =
					new FulfillTeamDonationSolicitationResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean invalidUuids = true;
		UUID solicitorUuid = null;
		try {
			UUID.fromString(donatorId);
			UUID.fromString(clanId);
			UUID.fromString(msfu.getUserId());
			UUID.fromString(msfu.getMonsterForUserId());
			UUID.fromString(cmtd.getId());
			UUID.fromString(cmtd.getUserId());
			UUID.fromString(cmtd.getClanId());

			solicitorId = cmtd.getUserId();
			solicitorUuid = UUID.fromString(solicitorId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(
					String.format(
							"UUID error. incorrect userId=%s, clanId=%s, or solicitation=%s",
							donatorId, clanId, cmtd), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_OTHER);
			FulfillTeamDonationSolicitationResponseEvent resEvent = new FulfillTeamDonationSolicitationResponseEvent(
					donatorId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		/*int clanId = 0;
		if (senderProto.hasClan() && null != senderProto.getClan()) {
		clanId = senderProto.getClan().getClanId();
		}

		//maybe should get clan lock instead of locking person
		//but going to modify user, so lock user. however maybe locking is not necessary
		boolean lockedClan = false;
		if (0 != clanId) {
		lockedClan = getLocker().lockClan(clanId);
		} else {
		}*/
		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(solicitorUuid, this.getClass().getSimpleName());

			FulfillTeamDonationSolicitationAction ftdsa = new FulfillTeamDonationSolicitationAction(
					donatorId, clanId, msfu, cmtd, clientTime,
					clanMemberTeamDonationRetrieveUtil, UpdateUtils.get(),
					InsertUtils.get());

			ftdsa.execute(resBuilder);

			FulfillTeamDonationSolicitationResponseEvent resEvent = new FulfillTeamDonationSolicitationResponseEvent(
					donatorId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());

			//only write to user if failed
			if (!resBuilder.getStatus().equals(
					ResponseStatus.SUCCESS)) {
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);

			} else {
				//only write to clan if success
				ClanMemberTeamDonation solicitation = ftdsa.getSolicitation();
				MonsterSnapshotForUser msfuNew = ftdsa.getMsfuNew();
				ClanMemberTeamDonationProto cmtdp = createInfoProtoUtils
						.createClanMemberTeamDonationProto(solicitation,
								msfuNew, solicitationProto.getSolicitor(),
								senderProto);
				resBuilder.setSolicitation(cmtdp);

//				hzClanSearch.updateRankForClanSearch(clanId, clientTime, 0, 0, 1, 0, 0);

				resEvent.setResponseProto(resBuilder
						.build());
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
				//this works for other clan members, but not for the person
				//who left (they see the message when they join a clan, reenter clan house
				//notifyClan(user, clan);
				/*
				if (gemsSpent > 0) {
					User user = stda.getUser();
					UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, null);
					resEventUpdate.setTag(event.getTag());
					responses.normalResponseEvents().add(resEventUpdate);

					writeToCurrencyHistory(userId, clientTime, stda);
				}*/

			}
		} catch (Exception e) {
			log.error(
					"exception in FulfillTeamDonationSolicitation processEvent",
					e);
			try {
				resBuilder
						.setStatus(ResponseStatus.FAIL_OTHER);
				FulfillTeamDonationSolicitationResponseEvent resEvent = new FulfillTeamDonationSolicitationResponseEvent(
						donatorId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in FulfillTeamDonationSolicitation processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(solicitorUuid, this.getClass().getSimpleName());
			}
		}
	}

	//	private void writeToCurrencyHistory(String userId, Date date,
	//		FulfillTeamDonationSolicitationAction stda)
	//	{
	//		MiscMethods.writeToUserCurrencyOneUser(userId,
	//			new Timestamp(date.getTime()),
	//			stda.getCurrencyDeltas(), stda.getPreviousCurrencies(),
	//			stda.getCurrentCurrencies(), stda.getReasons(),
	//			stda.getDetails());
	//	}

	/*
	private void notifyClan(User aUser, Clan aClan) {
	int clanId = aClan.getId();

	int level = aUser.getLevel();
	String deserter = aUser.getName();
	Notification aNote = new Notification();

	aNote.setAsUserLeftClan(level, deserter);
	MiscMethods.writeClanApnsNotification(aNote, server, clanId);
	}*/

	public ClanMemberTeamDonationRetrieveUtil getClanMemberTeamDonationRetrieveUtil() {
		return clanMemberTeamDonationRetrieveUtil;
	}

	public void setClanMemberTeamDonationRetrieveUtil(
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil) {
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
