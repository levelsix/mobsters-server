package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndClanAvengingRequestEvent;
import com.lvl6.events.response.EndClanAvengingResponseEvent;
import com.lvl6.proto.EventClanProto.EndClanAvengingRequestProto;
import com.lvl6.proto.EventClanProto.EndClanAvengingResponseProto;
import com.lvl6.proto.EventClanProto.EndClanAvengingResponseProto.EndClanAvengingStatus;
import com.lvl6.proto.EventClanProto.InviteToClanResponseProto.InviteToClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.EndClanAvengingAction;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component

public class EndClanAvengingController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	public EndClanAvengingController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EndClanAvengingRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_END_CLAN_AVENGING_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		EndClanAvengingRequestProto reqProto = ((EndClanAvengingRequestEvent) event)
				.getEndClanAvengingRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> clanAvengeUuids = reqProto.getClanAvengeUuidsList();

		EndClanAvengingResponseProto.Builder resBuilder = EndClanAvengingResponseProto
				.newBuilder();
		resBuilder.setStatus(EndClanAvengingStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = "";
		if (null != senderProto && senderProto.hasClan()) {
			MinimumClanProto mcp = senderProto.getClan();
			if (null != mcp && mcp.hasClanUuid()) {
				clanId = mcp.getClanUuid();
			}
		}

		UUID userUuid = null;
		UUID clanUuid = null;
		boolean invalidUuids = true;
		//	    if (!clanId.isEmpty()) {
		try {
			userUuid = UUID.fromString(userId);
			clanUuid = UUID.fromString(clanId);
			invalidUuids = false;

			List<String> validClanAvengeUuidsList = new ArrayList<String>();
			for (String clanAvengeUuid : clanAvengeUuids) {
				try {
					UUID.fromString(clanAvengeUuid);
					validClanAvengeUuidsList.add(clanAvengeUuid);
				} catch (Exception e) {
					log.error("invalid UUID. not processing {}", clanAvengeUuid);
				}
			}
			clanAvengeUuids = validClanAvengeUuidsList;

		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}
		//	    }

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(EndClanAvengingStatus.FAIL_OTHER);
			EndClanAvengingResponseEvent resEvent = new EndClanAvengingResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setEndClanAvengingResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//		boolean lockedClan = getLocker().lockClan(clanUuid);
		//	    locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			EndClanAvengingAction bcaa = new EndClanAvengingAction(userId,
					clanId, clanAvengeUuids, DeleteUtils.get());
			bcaa.execute(resBuilder);

			EndClanAvengingResponseEvent resEvent = new EndClanAvengingResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setEndClanAvengingResponseProto(resBuilder.build());

			if (resBuilder.getStatus().equals(InviteToClanStatus.SUCCESS)) {
				resBuilder.addAllClanAvengeUuids(clanAvengeUuids);
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));

				//				User user = bcaa.getProspectiveMember();
				//				Clan clan = bcaa.getProspectiveClan();
				//				ClanDataProto cdp = setClanData(clanId, clan, user, userId);
				//				sendClanData(event, senderProto, userId, cdp);

			} else {
				//only write to user if fail
				responses.normalResponseEvents().add(resEvent);
			}

		} catch (Exception e) {
			log.error("exception in EndClanAvenging processEvent", e);
			try {
				resBuilder.setStatus(EndClanAvengingStatus.FAIL_OTHER);
				EndClanAvengingResponseEvent resEvent = new EndClanAvengingResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setEndClanAvengingResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in EndClanAvenging processEvent", e);
			}
		} finally {
			//			if (null != clanUuid && lockedClan) {
			//				getLocker().unlockClan(clanUuid);
			//			}
			//			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
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
