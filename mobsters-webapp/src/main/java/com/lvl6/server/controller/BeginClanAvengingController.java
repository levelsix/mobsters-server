package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginClanAvengingRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.BeginClanAvengingResponseEvent;
import com.lvl6.info.ClanAvenge;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpClanAvengeProto;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.EventClanProto.BeginClanAvengingRequestProto;
import com.lvl6.proto.EventClanProto.BeginClanAvengingResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.BeginClanAvengingAction;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class BeginClanAvengingController extends EventController {

	private static Logger log = LoggerFactory.getLogger(BeginClanAvengingController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ClanStuffUtils clanStuffUtils;
	
	@Autowired
	protected TimeUtils timeUtils;
	

	public BeginClanAvengingController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new BeginClanAvengingRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BEGIN_CLAN_AVENGING_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		BeginClanAvengingRequestProto reqProto = ((BeginClanAvengingRequestEvent) event)
				.getBeginClanAvengingRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<PvpHistoryProto> recentNBattles = reqProto.getRecentNBattlesList();
		Date clientTime = new Date(reqProto.getClientTime());

		BeginClanAvengingResponseProto.Builder resBuilder = BeginClanAvengingResponseProto
				.newBuilder();
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = "";
		if (null != senderProto && senderProto.hasClan()) {
			MinimumClanProto mcp = senderProto.getClan();
			if (null != mcp && mcp.hasClanUuid()) {
				clanId = mcp.getClanUuid();
			}
		}
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			BeginClanAvengingResponseEvent resEvent = new BeginClanAvengingResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(clientTime, new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			BeginClanAvengingResponseEvent resEvent = new BeginClanAvengingResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		UUID clanUuid = null;
		boolean invalidUuids = true;
		if (!clanId.isEmpty()) {
			try {
				userUuid = UUID.fromString(userId);
				clanUuid = UUID.fromString(clanId);
				invalidUuids = false;

				List<PvpHistoryProto> validPhpList = new ArrayList<PvpHistoryProto>();
				for (PvpHistoryProto php : recentNBattles) {
					try {
						String attackerUuid = php.getAttacker().getUserUuid();
						UUID.fromString(attackerUuid);
						validPhpList.add(php);
					} catch (Exception e) {
						log.error(
								"invalid UUID for attacker. not processing {}",
								php);
					}
				}
				recentNBattles = validPhpList;

			} catch (Exception e) {
				log.error(String.format(
						"UUID error. incorrect userId=%s, clanId=%s", userId,
						clanId), e);
				invalidUuids = true;
			}
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			BeginClanAvengingResponseEvent resEvent = new BeginClanAvengingResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//		boolean lockedClan = getLocker().lockClan(clanUuid);
		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			List<ClanAvenge> caList = clanStuffUtils.javafyPvpHistoryProto(
					userId, clanId, recentNBattles, clientTime);
			Map<String, MinimumUserProto> attackerMupMap = clanStuffUtils
					.extractAttackerFullUserProto(recentNBattles);

			BeginClanAvengingAction bcaa = new BeginClanAvengingAction(userId,
					clanId, clientTime, caList, UpdateUtils.get(),
					InsertUtils.get());
			bcaa.execute(resBuilder);

			BeginClanAvengingResponseEvent resEvent = new BeginClanAvengingResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());

			if (resBuilder.getStatus().equals(ResponseStatus.SUCCESS)) {
				//only write to clan if success
				List<ClanAvenge> retaliationRequestsWithIds = bcaa
						.getRetaliationRequestsWithIds();
				List<PvpClanAvengeProto> retaliationProtos = createInfoProtoUtils
						.createPvpClanAvengeProto(retaliationRequestsWithIds,
								senderProto, clanId, attackerMupMap);

				resBuilder.addAllClanAvengings(retaliationProtos);

				resEvent.setResponseProto(resBuilder.build());
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));

				//				User user = bcaa.getProspectiveMember();
				//				Clan clan = bcaa.getProspectiveClan();
				//				ClanDataProto cdp = setClanData(clanId, clan, user, userId);
				//				sendClanData(event, senderProto, userId, cdp);

			} else {
				//only write to user if just reject or fail
				responses.normalResponseEvents().add(resEvent);
			}

		} catch (Exception e) {
			log.error("exception in BeginClanAvenging processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				BeginClanAvengingResponseEvent resEvent = new BeginClanAvengingResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in BeginClanAvenging processEvent", e);
			}
		} finally {
			//			if (null != clanUuid && lockedClan) {
			//				getLocker().unlockClan(clanUuid);
			//			}
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	//	private ClanDataProto setClanData( String clanId,
	//		Clan c, User u, String userId )
	//	{
	//		ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();
	//		StartUpResource fillMe = new StartUpResource(
	//			userRetrieveUtil, clanRetrieveUtil );
	//
	//		SetClanChatMessageAction sccma = new SetClanChatMessageAction(cdpb, u, getClanChatPostRetrieveUtils());
	//		sccma.setUp(fillMe);
	//		SetClanHelpingsAction scha = new SetClanHelpingsAction(cdpb, u, userId, clanHelpRetrieveUtil);
	//		scha.setUp(fillMe);
	//
	//		fillMe.fetchUsersOnly();
	//		fillMe.addClan(clanId, c);
	//
	//		sccma.execute(fillMe);
	//		scha.execute(fillMe);
	//
	//		return cdpb.build();
	//	}

	//	private void sendClanData(
	//		  RequestEvent event,
	//		  MinimumUserProto senderProto,
	//		  String userId,
	//		  ClanDataProto cdp )
	//	  {
	//		  if (null == cdp) {
	//			  return;
	//		  }
	//		  RetrieveClanDataResponseEvent rcdre =
	//			  new RetrieveClanDataResponseEvent(userId);
	//		  rcdre.setTag(event.getTag());
	//		  RetrieveClanDataResponseProto.Builder rcdrpb =
	//			  RetrieveClanDataResponseProto.newBuilder();
	//		  rcdrpb.setMup(senderProto);
	//		  rcdrpb.setClanData(cdp);
	//
	//		  rcdre.setRetrieveClanDataResponseProto(rcdrpb.build());
	//		  responses.normalResponseEvents().add(rcdre);
	//	  }

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
