package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AcceptOrRejectClanInviteRequestEvent;
import com.lvl6.events.response.AcceptOrRejectClanInviteResponseEvent;
import com.lvl6.events.response.RetrieveClanDataResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.ClanProto.ClanInviteProto;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteRequestProto;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto.AcceptOrRejectClanInviteStatus;
import com.lvl6.proto.EventClanProto.InviteToClanResponseProto.InviteToClanStatus;
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.retrieveutils.ClanInviteRetrieveUtil;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AcceptOrRejectClanInviteAction;
import com.lvl6.server.controller.actionobjects.SetClanChatMessageAction;
import com.lvl6.server.controller.actionobjects.SetClanHelpingsAction;
import com.lvl6.server.controller.actionobjects.StartUpResource;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class AcceptOrRejectClanInviteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;


	@Autowired
	protected ClanInviteRetrieveUtil clanInviteRetrieveUtil;
	
	@Autowired
	protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;


	public AcceptOrRejectClanInviteController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new AcceptOrRejectClanInviteRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_ACCEPT_OR_REJECT_CLAN_INVITE_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		AcceptOrRejectClanInviteRequestProto reqProto = ((AcceptOrRejectClanInviteRequestEvent)event).getAcceptOrRejectClanInviteRequestProto();

		log.info(String.format("reqProto=%s", reqProto));
		
		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserUuid();
		ClanInviteProto accepted = reqProto.getAccepted(); 
		List<ClanInviteProto> rejected = reqProto.getRejectedList();
		Date clientTime = new Date(reqProto.getClientTime());

		AcceptOrRejectClanInviteResponseProto.Builder resBuilder = AcceptOrRejectClanInviteResponseProto.newBuilder();
		resBuilder.setStatus(AcceptOrRejectClanInviteStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		int clanId = 0;
		if (null != accepted && accepted.hasClanId()) {
			clanId = accepted.getClanId();
		}

		boolean lockedClan = false;
		if (0 != clanId) {
			lockedClan = getLocker().lockClan(clanId);
		}
		try {
			AcceptOrRejectClanInviteAction aorcia = null;
			if (lockedClan) {
				int inviteId = 0;
				int prospectiveMemberId = userId;
				int inviterId = 0;
				if (null != accepted) {
					inviteId = accepted.getInviteId();
					inviterId = accepted.getInviterId();
				}
				List<Integer> rejectedInviteIds = new ArrayList<Integer>();

				for (ClanInviteProto invite : rejected) {
					rejectedInviteIds.add( invite.getInviteId() );
				}

				aorcia =
					new AcceptOrRejectClanInviteAction(inviteId, prospectiveMemberId,
						inviterId, clanId, clientTime, rejectedInviteIds,
						RetrieveUtils.userClanRetrieveUtils(),
						InsertUtils.get(), DeleteUtils.get(),
						clanInviteRetrieveUtil);
				aorcia.execute(resBuilder);
			}


			AcceptOrRejectClanInviteResponseEvent resEvent =
				new AcceptOrRejectClanInviteResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setAcceptOrRejectClanInviteResponseProto(resBuilder.build());

			if (resBuilder.getStatus().equals(InviteToClanStatus.SUCCESS) &&
				null != accepted)
			{
				//only write to clan if user accepted and success
				server.writeClanEvent(resEvent, clanId);
				
				User user = aorcia.getProspectiveMember();
				Clan clan = aorcia.getProspectiveClan();
				ClanDataProto cdp = setClanData(clanId, clan, user, userId);
				sendClanData(event, senderProto, userId, cdp);
				
			} else {
				//only write to user if just reject or fail
				server.writeEvent(resEvent);
			}

		} catch (Exception e) {
			log.error("exception in AcceptOrRejectClanInvite processEvent", e);
			try {
				resBuilder.setStatus(AcceptOrRejectClanInviteStatus.FAIL_OTHER);
				AcceptOrRejectClanInviteResponseEvent resEvent = new AcceptOrRejectClanInviteResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setAcceptOrRejectClanInviteResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in AcceptOrRejectClanInvite processEvent", e);
			}
		} finally {
			if (0 != clanId && lockedClan) {
				getLocker().unlockClan(clanId);
			}
		}
	}


	private ClanDataProto setClanData( int clanId,
		Clan c, User u, int userId )
	{
		ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();
		StartUpResource fillMe = new StartUpResource(
			RetrieveUtils.userRetrieveUtils());

		SetClanChatMessageAction sccma = new SetClanChatMessageAction(cdpb, u);
		sccma.setUp(fillMe);
		SetClanHelpingsAction scha = new SetClanHelpingsAction(cdpb, u, userId, clanHelpRetrieveUtil);
		scha.setUp(fillMe);

		fillMe.fetchUsersOnly();
		fillMe.addClan(clanId, c);

		sccma.execute(fillMe);
		scha.execute(fillMe);

		return cdpb.build();
	}
	
	private void sendClanData(
		  RequestEvent event,
		  MinimumUserProto senderProto,
		  int userId,
		  ClanDataProto cdp )
	  {
		  if (null == cdp) {
			  return;
		  }
		  RetrieveClanDataResponseEvent rcdre =
			  new RetrieveClanDataResponseEvent(userId);
		  rcdre.setTag(event.getTag());
		  RetrieveClanDataResponseProto.Builder rcdrpb =
			  RetrieveClanDataResponseProto.newBuilder();
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

	public ClanInviteRetrieveUtil getClanInviteRetrieveUtil()
	{
		return clanInviteRetrieveUtil;
	}

	public void setClanInviteRetrieveUtil( ClanInviteRetrieveUtil clanInviteRetrieveUtil )
	{
		this.clanInviteRetrieveUtil = clanInviteRetrieveUtil;
	}

	public ClanHelpRetrieveUtil getClanHelpRetrieveUtil()
	{
		return clanHelpRetrieveUtil;
	}

	public void setClanHelpRetrieveUtil( ClanHelpRetrieveUtil clanHelpRetrieveUtil )
	{
		this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
	}

}
