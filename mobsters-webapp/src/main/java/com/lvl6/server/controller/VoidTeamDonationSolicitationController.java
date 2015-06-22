package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.VoidTeamDonationSolicitationRequestEvent;
import com.lvl6.events.response.VoidTeamDonationSolicitationResponseEvent;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationRequestProto;
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationResponseProto;
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationResponseProto.VoidTeamDonationSolicitationStatus;
import com.lvl6.proto.MonsterStuffProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.server.controller.actionobjects.VoidTeamDonationSolicitationAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component

public class VoidTeamDonationSolicitationController extends EventController {

	private static Logger log = LoggerFactory.getLogger(VoidTeamDonationSolicitationController.class);

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;
	
	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	public VoidTeamDonationSolicitationController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new VoidTeamDonationSolicitationRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_VOID_TEAM_DONATION_SOLICITATION_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		VoidTeamDonationSolicitationRequestProto reqProto = ((VoidTeamDonationSolicitationRequestEvent) event)
				.getVoidTeamDonationSolicitationRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<ClanMemberTeamDonationProto> solicitationsProtoList = reqProto
				.getSolicitationsList();

		VoidTeamDonationSolicitationResponseProto.Builder resBuilder = VoidTeamDonationSolicitationResponseProto
				.newBuilder();
		resBuilder.setStatus(VoidTeamDonationSolicitationStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		//		String clanId = null;
		//		if (null != senderProto.getClan()) {
		//			clanId = senderProto.getClan().getClanUuid();
		//		}

		Map<String, List<MonsterSnapshotForUser>> donationIdsToSnapshots = new HashMap<String, List<MonsterSnapshotForUser>>();
		Set<String> clanIds = new HashSet<String>();
		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);

			for (ClanMemberTeamDonationProto cmtdp : solicitationsProtoList) {
				String donationUuid = cmtdp.getDonationUuid();
				String clanUuid = cmtdp.getClanUuid();
				//sanity check
				UUID.fromString(donationUuid);
				UUID.fromString(clanUuid);
				clanIds.add(clanUuid);

				for (UserMonsterSnapshotProto umsp : cmtdp.getDonationsList()) {
					//sanity check
					UUID.fromString(umsp.getSnapshotUuid());

					if (!donationIdsToSnapshots.containsKey(donationUuid)) {
						//base case
						donationIdsToSnapshots.put(donationUuid,
								new ArrayList<MonsterSnapshotForUser>());
					}

					MonsterSnapshotForUser msfu = monsterStuffUtils
							.javafyUserMonsterSnapshotProto(umsp);

					List<MonsterSnapshotForUser> msfuList = donationIdsToSnapshots
							.get(donationUuid);
					msfuList.add(msfu);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(
					String.format(
							"UUID error. incorrect userId=%s, solicitationsProtoList=%s",
							userId, solicitationsProtoList), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(VoidTeamDonationSolicitationStatus.FAIL_OTHER);
			VoidTeamDonationSolicitationResponseEvent resEvent = new VoidTeamDonationSolicitationResponseEvent(
					userId);
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
		locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
		}*/
		try {
			VoidTeamDonationSolicitationAction stda = new VoidTeamDonationSolicitationAction(
					userId, donationIdsToSnapshots, DeleteUtils.get());

			stda.execute(resBuilder);

			VoidTeamDonationSolicitationResponseEvent resEvent = new VoidTeamDonationSolicitationResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());

			//only write to user if failed
			if (!resBuilder.getStatus().equals(
					VoidTeamDonationSolicitationStatus.SUCCESS)) {
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);

			} else {
				resBuilder.addAllClanTeamDonateUuid(donationIdsToSnapshots
						.keySet());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);

				//write to the clans of the solicitations if success
				for (String clanId : clanIds) {
					responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
				}
				//this works for other clan members, but not for the person 
				//who left (they see the message when they join a clan, reenter clan house
				//notifyClan(user, clan);

			}
		} catch (Exception e) {
			log.error("exception in VoidTeamDonationSolicitation processEvent",
					e);
			try {
				resBuilder
						.setStatus(VoidTeamDonationSolicitationStatus.FAIL_OTHER);
				VoidTeamDonationSolicitationResponseEvent resEvent = new VoidTeamDonationSolicitationResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in VoidTeamDonationSolicitation processEvent",
						e);
			}
		} /*finally {
			if (0 != clanId && lockedClan) {
			getLocker().unlockClan(clanId);
			} else {
			locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
			}
			}*/
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

}
