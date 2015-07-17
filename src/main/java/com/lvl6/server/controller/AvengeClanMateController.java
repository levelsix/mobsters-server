package com.lvl6.server.controller;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AvengeClanMateRequestEvent;
import com.lvl6.events.response.AvengeClanMateResponseEvent;
import com.lvl6.info.ClanAvengeUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.User;
import com.lvl6.proto.BattleProto.PvpClanAvengeProto;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventClanProto.AvengeClanMateRequestProto;
import com.lvl6.proto.EventClanProto.AvengeClanMateResponseProto;
import com.lvl6.proto.EventClanProto.AvengeClanMateResponseProto.AvengeClanMateStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AvengeClanMateAction;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class AvengeClanMateController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected ClanAvengeRetrieveUtil clanAvengeRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;
	
	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;
	
	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;
	
	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;  
	
	
	public AvengeClanMateController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new AvengeClanMateRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_AVENGE_CLAN_MATE_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		AvengeClanMateRequestProto reqProto = ((AvengeClanMateRequestEvent)event).getAvengeClanMateRequestProto();

		log.info(String.format("reqProto=%s", reqProto));
		
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		PvpClanAvengeProto clanAvenge = reqProto.getClanAvenge();
		long clientTime = reqProto.getClientTime();

		AvengeClanMateResponseProto.Builder resBuilder = AvengeClanMateResponseProto.newBuilder();
		resBuilder.setStatus(AvengeClanMateStatus.FAIL_OTHER);
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
	    
	    String clanAvengeId = null;
//	    if (!clanId.isEmpty()) {
	    try {
	    	userUuid = UUID.fromString(userId);
	    	clanUuid = UUID.fromString(clanId);

	    	clanAvengeId = clanAvenge.getClanAvengeUuid();
	    	UUID.fromString(clanAvengeId);
	    	
	    	invalidUuids = false;

	    } catch (Exception e) {
	    	log.error(String.format(
	    		"UUID error. incorrect userId=%s",
	    		userId), e);
	    	invalidUuids = true;
	    }
//	    }
	    
	    //UUID checks
	    if (invalidUuids) {
	    	resBuilder.setStatus(AvengeClanMateStatus.FAIL_OTHER);
			AvengeClanMateResponseEvent resEvent = new AvengeClanMateResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setAvengeClanMateResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
	    	return;
	    }
		
//		boolean lockedClan = getLocker().lockClan(clanUuid);
//	    locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
//			List<ClanAvengeUser> cauList = ClanStuffUtils
//				.extractClanAvengeUser(clanAvenge.getUsersAvengingList());
//			ClanAvengeUser cau = null;
//			
//			if (null != cauList && !cauList.isEmpty())
//			{
//				cau = cauList.get(0);
//				
//				if (cauList.size() > 1) {
//					log.warn("client sent more than one user to avenge clan mate. {}",
//						clanAvenge.getUsersAvengingList());
//				}
//			}
			ClanAvengeUser cau = new ClanAvengeUser(clanId, clanAvengeId, userId, 
				new Date(clientTime));
				
			AvengeClanMateAction bcaa =
				new AvengeClanMateAction(userId, clanId, clanAvengeId,
					cau, clanAvengeRetrieveUtil, userRetrieveUtil,
					hazelcastPvpUtil, pvpLeagueForUserRetrieveUtil,
					monsterForUserRetrieveUtil, InsertUtils.get());
			bcaa.execute(resBuilder);


			AvengeClanMateResponseEvent resEvent =
				new AvengeClanMateResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setAvengeClanMateResponseProto(resBuilder.build());

			if (resBuilder.getStatus().equals(AvengeClanMateStatus.SUCCESS))
			{
				User defender = bcaa.getVictim();
				PvpLeagueForUser plfu = bcaa.getVictimPvpLeagueInfo();
				PvpUser defenderPu = bcaa.getCachedVictimPvpLeaguenfo();
				Collection<MonsterForUser> defenderMonsters = bcaa.getVictimMonsters();
				Map<String, Integer> userMonsterIdToDropped = bcaa.getVictimMonsterDrops();
				int expectedCashLost = bcaa.getVictimProspectiveCashLoss();
				int expectedOilLost = bcaa.getVictimProspectiveOilLoss();
				String defendingMessage = defender.getPvpDefendingMessage();

				//the clanAvenge.getAttacker() is now the victim
				PvpProto pp = CreateInfoProtoUtils.createPvpProto(
					defender.getId(), plfu, defenderPu, defenderMonsters,
					userMonsterIdToDropped, expectedCashLost, expectedOilLost,
					clanAvenge.getAttacker(), defendingMessage);
				
				resBuilder.setVictim(pp);
				resEvent.setAvengeClanMateResponseProto(resBuilder.build());
				
			}
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in AvengeClanMate processEvent", e);
			try {
				resBuilder.setStatus(AvengeClanMateStatus.FAIL_OTHER);
				AvengeClanMateResponseEvent resEvent = new AvengeClanMateResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setAvengeClanMateResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in AvengeClanMate processEvent", e);
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
//		  server.writeEvent(rcdre);
//	  }
	
	public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public ClanAvengeRetrieveUtil getClanAvengeRetrieveUtil()
	{
		return clanAvengeRetrieveUtil;
	}

	public void setClanAvengeRetrieveUtil( ClanAvengeRetrieveUtil clanAvengeRetrieveUtil )
	{
		this.clanAvengeRetrieveUtil = clanAvengeRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil()
	{
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil( UserRetrieveUtils2 userRetrieveUtil )
	{
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public HazelcastPvpUtil getHazelcastPvpUtil()
	{
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil( HazelcastPvpUtil hazelcastPvpUtil )
	{
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil()
	{
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
		PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil )
	{
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil()
	{
		return monsterForUserRetrieveUtil;
	}

	public void setMonsterForUserRetrieveUtil(
		MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil )
	{
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
	}

}
