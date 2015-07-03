package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SolicitTeamDonationRequestEvent;
import com.lvl6.events.response.SolicitTeamDonationResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventClanProto.SolicitTeamDonationRequestProto;
import com.lvl6.proto.EventClanProto.SolicitTeamDonationResponseProto;
import com.lvl6.proto.MonsterStuffProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.actionobjects.SolicitTeamDonationAction;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class SolicitTeamDonationController extends EventController {


	private static final Logger log = LoggerFactory.getLogger(SolicitTeamDonationController.class);

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

//	@Autowired
//	protected HazelcastClanSearchImpl hzClanSearch;

	@Autowired
	protected TimeUtils timeUtils;

	public SolicitTeamDonationController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SolicitTeamDonationRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SOLICIT_TEAM_DONATION_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SolicitTeamDonationRequestProto reqProto = ((SolicitTeamDonationRequestEvent) event)
				.getSolicitTeamDonationRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String msg = reqProto.getMsg();
		int powerLimit = reqProto.getPowerLimit();
		Date clientTime = new Date(reqProto.getClientTime());
		int gemsSpent = reqProto.getGemsSpent();

		SolicitTeamDonationResponseProto.Builder resBuilder = SolicitTeamDonationResponseProto
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
			SolicitTeamDonationResponseEvent resEvent = new SolicitTeamDonationResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(clientTime, new Date()) >
				ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			SolicitTeamDonationResponseEvent resEvent = new SolicitTeamDonationResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);
			UUID.fromString(clanId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, clanId=%s", userId,
					clanId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			SolicitTeamDonationResponseEvent resEvent = new SolicitTeamDonationResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
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
		locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
		}*/
		try {
			SolicitTeamDonationAction stda = new SolicitTeamDonationAction(
					userId, clanId, msg, powerLimit, clientTime, gemsSpent,
					userRetrieveUtil, clanMemberTeamDonationRetrieveUtil,
					InsertUtils.get(), UpdateUtils.get(), miscMethods);

			stda.execute(resBuilder);

			SolicitTeamDonationResponseEvent resEvent = new SolicitTeamDonationResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());

			//only write to user if failed
			if (!resBuilder.getStatus().equals(
					ResponseStatus.SUCCESS)) {
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);

			} else {
				//only write to clan if success
				ClanMemberTeamDonation solicitation = stda.getSolicitation();
				ClanMemberTeamDonationProto cmtdp = createInfoProtoUtils
						.createClanMemberTeamDonationProto(solicitation, null,
								senderProto, null);
				resBuilder.setSolicitation(cmtdp);

//				hzClanSearch.updateRankForClanSearch(clanId, clientTime, 0, 1, 0, 0, 0);
				resEvent.setResponseProto(resBuilder.build());
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));

				//this works for other clan members, but not for the person
				//who left (they see the message when they join a clan, reenter clan house
				//notifyClan(user, clan);
				if (gemsSpent > 0) {
					User user = stda.getUser();
					UpdateClientUserResponseEvent resEventUpdate = miscMethods
							.createUpdateClientUserResponseEventAndUpdateLeaderboard(
									user, null, null);
					resEventUpdate.setTag(event.getTag());
					responses.normalResponseEvents().add(resEventUpdate);

					writeToCurrencyHistory(userId, clientTime, stda);
				}

			}
		} catch (Exception e) {
			log.error("exception in SolicitTeamDonation processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SolicitTeamDonationResponseEvent resEvent = new SolicitTeamDonationResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SolicitTeamDonation processEvent", e);
			}
		} /*finally {
			if (0 != clanId && lockedClan) {
			getLocker().unlockClan(clanId);
			} else {
			locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
			}
			}*/
	}

	private void writeToCurrencyHistory(String userId, Date date,
			SolicitTeamDonationAction stda) {
		miscMethods.writeToUserCurrencyOneUser(userId,
				new Timestamp(date.getTime()), stda.getCurrencyDeltas(),
				stda.getPreviousCurrencies(), stda.getCurrentCurrencies(),
				stda.getReasons(), stda.getDetails());
	}

	/*
	private void notifyClan(User aUser, Clan aClan) {
	int clanId = aClan.getId();

	int level = aUser.getLevel();
	String deserter = aUser.getName();
	Notification aNote = new Notification();

	aNote.setAsUserLeftClan(level, deserter);
	MiscMethods.writeClanApnsNotification(aNote, server, clanId);
	}*/

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

}
