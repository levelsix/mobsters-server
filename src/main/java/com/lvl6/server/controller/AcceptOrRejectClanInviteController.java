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
import com.lvl6.events.request.AcceptOrRejectClanInviteRequestEvent;
import com.lvl6.events.response.AcceptOrRejectClanInviteResponseEvent;
import com.lvl6.events.response.RetrieveClanDataResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.ClanProto.ClanInviteProto;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteRequestProto;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto.AcceptOrRejectClanInviteStatus;
import com.lvl6.proto.EventClanProto.InviteToClanResponseProto.InviteToClanStatus;
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.retrieveutils.ClanInviteRetrieveUtil;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AcceptOrRejectClanInviteAction;
import com.lvl6.server.controller.actionobjects.SetClanChatMessageAction;
import com.lvl6.server.controller.actionobjects.SetClanDataProtoAction;
import com.lvl6.server.controller.actionobjects.SetClanHelpingsAction;
import com.lvl6.server.controller.actionobjects.SetClanRetaliationsAction;
import com.lvl6.server.controller.actionobjects.StartUpResource;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class AcceptOrRejectClanInviteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;
	
	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected ClanInviteRetrieveUtil clanInviteRetrieveUtil;

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
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	protected MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;
	
	
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
		String userId = senderProto.getUserUuid();
		ClanInviteProto accepted = reqProto.getAccepted(); 
		List<ClanInviteProto> rejected = reqProto.getRejectedList();
		Date clientTime = new Date(reqProto.getClientTime());

		AcceptOrRejectClanInviteResponseProto.Builder resBuilder = AcceptOrRejectClanInviteResponseProto.newBuilder();
		resBuilder.setStatus(AcceptOrRejectClanInviteStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = "";
		if (null != accepted && accepted.hasClanUuid()) {
			clanId = accepted.getClanUuid();
		}
		
		UUID userUuid = null;
	    UUID clanUuid = null;
	    
	    boolean invalidUuids = true;
	    if (!clanId.isEmpty()) {
	    	try {
	    		userUuid = UUID.fromString(userId);
				clanUuid = UUID.fromString(clanId);
				invalidUuids = false;
			} catch (Exception e) {
				log.error(String.format(
					"UUID error. incorrect userId=%s, clanId=%s",
					userId, clanId), e);
			}
	    }
	    
	    //UUID checks
	    if (invalidUuids) {
	    	resBuilder.setStatus(AcceptOrRejectClanInviteStatus.FAIL_OTHER);
			AcceptOrRejectClanInviteResponseEvent resEvent = new AcceptOrRejectClanInviteResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setAcceptOrRejectClanInviteResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
	    	return;
	    }
		

		boolean lockedClan = getLocker().lockClan(clanUuid);
		try {
			AcceptOrRejectClanInviteAction aorcia = null;
			if (lockedClan) {
				String inviteId = "";
				String prospectiveMemberId = userId;
				String inviterId = "";
				if (null != accepted) {
					inviteId = accepted.getInviteUuid();
					inviterId = accepted.getInviterUuid();
				}
				List<String> rejectedInviteIds = new ArrayList<String>();

				for (ClanInviteProto invite : rejected) {
					rejectedInviteIds.add( invite.getInviteUuid() );
				}

				aorcia =
					new AcceptOrRejectClanInviteAction(inviteId, prospectiveMemberId,
						inviterId, clanId, clientTime, rejectedInviteIds,
						userRetrieveUtil,
						RetrieveUtils.userClanRetrieveUtils(),
						InsertUtils.get(), DeleteUtils.get(),
						clanRetrieveUtil, clanInviteRetrieveUtil);
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
				List<Date> lastChatTimeContainer = new ArrayList<Date>();
				
				StartUpResource fillMe = new StartUpResource(
						userRetrieveUtil, clanRetrieveUtil);
				
		        SetClanDataProtoAction scdpa = new SetClanDataProtoAction(clanId, clan, user, userId, 
		        		lastChatTimeContainer, fillMe, clanHelpRetrieveUtil,
		        		clanAvengeRetrieveUtil, clanAvengeUserRetrieveUtil, clanChatPostRetrieveUtils,
		        		clanMemberTeamDonationRetrieveUtil, monsterSnapshotForUserRetrieveUtil);
		        ClanDataProto cdp = scdpa.execute();
		        
				sendClanData(event, senderProto, userId, cdp);
				
				//update clan cache
		        updateClanCache(clanId, aorcia.getClanSize(),
		        	lastChatTimeContainer, clan.isRequestToJoinRequired());

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
			if (null != clanUuid && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}


//	private ClanDataProto setClanData( String clanId,
//		Clan c, User u, String userId, List<Date> lastChatTimeContainer )
//	{
//		log.info("setting clanData proto for clan {}", c);
//		ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();
//		StartUpResource fillMe = new StartUpResource(
//			userRetrieveUtil, clanRetrieveUtil );
//
//		SetClanChatMessageAction sccma = new SetClanChatMessageAction(
//			cdpb, u, getClanChatPostRetrieveUtils());
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

	private void sendClanData(
		RequestEvent event,
		MinimumUserProto senderProto,
		String userId,
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

	private void updateClanCache(String clanId,
		int clanSize, List<Date> lastChatTimeContainer,
		boolean requestToJoinRequired)
	{
		//need to account for this user joining clan
		clanSize += 1;
		Date lastChatTime = lastChatTimeContainer.get(0);

		if (requestToJoinRequired) {
			clanSize = ClanSearch.penalizedClanSize;
			lastChatTime = ControllerConstants.INCEPTION_DATE;
		}
		
		clanSearch.updateClanSearchRank(clanId, clanSize, lastChatTime);
	}

	
	
	public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil()
	{
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil( UserRetrieveUtils2 userRetrieveUtil )
	{
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil()
	{
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil( ClanRetrieveUtils2 clanRetrieveUtil )
	{
		this.clanRetrieveUtil = clanRetrieveUtil;
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

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtils() {
		return clanChatPostRetrieveUtils;
	}

	public void setClanChatPostRetrieveUtils(
		ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils) {
		this.clanChatPostRetrieveUtils = clanChatPostRetrieveUtils;
	}

	public ClanAvengeRetrieveUtil getClanAvengeRetrieveUtil()
	{
		return clanAvengeRetrieveUtil;
	}

	public void setClanAvengeRetrieveUtil( ClanAvengeRetrieveUtil clanAvengeRetrieveUtil )
	{
		this.clanAvengeRetrieveUtil = clanAvengeRetrieveUtil;
	}

	public ClanAvengeUserRetrieveUtil getClanAvengeUserRetrieveUtil()
	{
		return clanAvengeUserRetrieveUtil;
	}

	public void setClanAvengeUserRetrieveUtil( ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil )
	{
		this.clanAvengeUserRetrieveUtil = clanAvengeUserRetrieveUtil;
	}

	public ClanSearch getClanSearch()
	{
		return clanSearch;
	}

	public void setClanSearch( ClanSearch clanSearch )
	{
		this.clanSearch = clanSearch;
	}

}
