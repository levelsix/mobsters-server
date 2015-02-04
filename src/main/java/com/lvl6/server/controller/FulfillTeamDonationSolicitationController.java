package com.lvl6.server.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.FulfillTeamDonationSolicitationRequestEvent;
import com.lvl6.events.response.FulfillTeamDonationSolicitationResponseEvent;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.proto.ClanProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationRequestProto;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto.FulfillTeamDonationSolicitationStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.actionobjects.FulfillTeamDonationSolicitationAction;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class FulfillTeamDonationSolicitationController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	public FulfillTeamDonationSolicitationController() {
		numAllocatedThreads = 4;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
		FulfillTeamDonationSolicitationRequestProto reqProto = ((FulfillTeamDonationSolicitationRequestEvent)event)
			.getFulfillTeamDonationSolicitationRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String donatorId = senderProto.getUserUuid();
		FullUserMonsterProto fump = reqProto.getFump();
		Date clientTime = new Date(reqProto.getClientTime());
		ClanMemberTeamDonationProto solicitationProto = reqProto
			.getSolicitation();

		MonsterSnapshotForUser msfu = MonsterStuffUtils.javafyFullUserMonsterProto(fump);
		String solicitorId = null;
		ClanMemberTeamDonation cmtd = ClanStuffUtils
			.javafyClanMemberTeamDonationProto(solicitationProto);


		FulfillTeamDonationSolicitationResponseProto.Builder resBuilder = FulfillTeamDonationSolicitationResponseProto.newBuilder();
		resBuilder.setStatus(FulfillTeamDonationSolicitationStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = null;

		if (null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}

		boolean invalidUuids = true;
		try {
			UUID.fromString(donatorId);
			UUID.fromString(clanId);
			UUID.fromString(msfu.getUserId());
			UUID.fromString(msfu.getMonsterForUserId());
			UUID.fromString(cmtd.getId());
			UUID.fromString(cmtd.getUserId());
			UUID.fromString(cmtd.getClanId());

			solicitorId = cmtd.getUserId();
			
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s, clanId=%s, or solicitation=%s",
				donatorId, clanId, cmtd), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(FulfillTeamDonationSolicitationStatus.FAIL_OTHER);
			FulfillTeamDonationSolicitationResponseEvent resEvent = new FulfillTeamDonationSolicitationResponseEvent(donatorId);
			resEvent.setTag(event.getTag());
			resEvent.setFulfillTeamDonationSolicitationResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
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
		server.lockPlayer(solicitorId, this.getClass().getSimpleName());
		try {
			FulfillTeamDonationSolicitationAction ftdsa =
				new FulfillTeamDonationSolicitationAction(
					donatorId, clanId, msfu, cmtd, clientTime,
					clanMemberTeamDonationRetrieveUtil,
					UpdateUtils.get(), InsertUtils.get());

			ftdsa.execute(resBuilder);

			FulfillTeamDonationSolicitationResponseEvent resEvent = new FulfillTeamDonationSolicitationResponseEvent(donatorId);
			resEvent.setTag(event.getTag());
			resEvent.setFulfillTeamDonationSolicitationResponseProto(resBuilder.build());

			//only write to user if failed
			if (!resBuilder.getStatus().equals(FulfillTeamDonationSolicitationStatus.SUCCESS)) {
				resEvent.setFulfillTeamDonationSolicitationResponseProto(resBuilder.build());
				server.writeEvent(resEvent);

			} else {
				//only write to clan if success
				ClanMemberTeamDonation solicitation = ftdsa.getSolicitation();
				MonsterSnapshotForUser msfuNew = ftdsa.getMsfuNew();
				ClanMemberTeamDonationProto cmtdp = CreateInfoProtoUtils
					.createClanMemberTeamDonationProto(solicitation,
						msfuNew, solicitationProto.getSolicitor(), senderProto);
				resBuilder.setSolicitation(cmtdp);
				
				resEvent.setFulfillTeamDonationSolicitationResponseProto(resBuilder.build());
				server.writeClanEvent(resEvent, clanId);
				//this works for other clan members, but not for the person 
				//who left (they see the message when they join a clan, reenter clan house
				//notifyClan(user, clan);
				/*
				if (gemsSpent > 0) {
					User user = stda.getUser();
					UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, null);
					resEventUpdate.setTag(event.getTag());
					server.writeEvent(resEventUpdate);

					writeToCurrencyHistory(userId, clientTime, stda);
				}*/

			}
		} catch (Exception e) {
			log.error("exception in FulfillTeamDonationSolicitation processEvent", e);
			try {
				resBuilder.setStatus(FulfillTeamDonationSolicitationStatus.FAIL_OTHER);
				FulfillTeamDonationSolicitationResponseEvent resEvent = new FulfillTeamDonationSolicitationResponseEvent(donatorId);
				resEvent.setTag(event.getTag());
				resEvent.setFulfillTeamDonationSolicitationResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in FulfillTeamDonationSolicitation processEvent", e);
			}
		} finally {
			server.unlockPlayer(solicitorId, this.getClass().getSimpleName());
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

	public ClanMemberTeamDonationRetrieveUtil getClanMemberTeamDonationRetrieveUtil()
	{
		return clanMemberTeamDonationRetrieveUtil;
	}

	public void setClanMemberTeamDonationRetrieveUtil(
		ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil )
	{
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
	}

}
