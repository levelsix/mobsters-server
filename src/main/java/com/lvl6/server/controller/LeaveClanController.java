package com.lvl6.server.controller;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.clansearch.HazelcastClanSearchImpl;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LeaveClanRequestEvent;
import com.lvl6.events.response.LeaveClanResponseEvent;
import com.lvl6.proto.EventClanProto.LeaveClanRequestProto;
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto;
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto.LeaveClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.LeaveClanAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class LeaveClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(LeaveClanController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;

	@Autowired
	protected HazelcastClanSearchImpl hzClanSearch;
	
	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected ClanSearch clanSearch;
	
	@Autowired
	protected ServerToggleRetrieveUtils toggle;
	
	
	public LeaveClanController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new LeaveClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_LEAVE_CLAN_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		LeaveClanRequestProto reqProto = ((LeaveClanRequestEvent) event)
				.getLeaveClanRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();

		LeaveClanResponseProto.Builder resBuilder = LeaveClanResponseProto
				.newBuilder();
		resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = null;
		if (senderProto.hasClan() && null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}

		UUID userUuid = null;
		UUID clanUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			clanUuid = UUID.fromString(clanId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, clanId=%s", userId,
					clanId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
			LeaveClanResponseEvent resEvent = new LeaveClanResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setLeaveClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		//maybe should get clan lock instead of locking person
		//but going to modify user, so lock user. however maybe locking is not necessary
		boolean lockedClan = false;
		if (null != clanId) {
			lockedClan = getLocker().lockClan(clanUuid);
		}/* else {
			server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
			}*/
		try {
			LeaveClanAction lca = new LeaveClanAction(userId, clanId, lockedClan,
					userRetrieveUtils, insertUtil, deleteUtil, clanRetrieveUtils, 
					userClanRetrieveUtils, hzClanSearch, clanChatPostRetrieveUtil, 
					timeUtils, clanSearch, toggle);
			lca.execute(resBuilder);
			
			LeaveClanResponseEvent resEvent = new LeaveClanResponseEvent(userId);
			resEvent.setTag(event.getTag());
			//only write to user if failed
			if (!LeaveClanStatus.SUCCESS.equals(resBuilder.getStatus())) {
				resEvent.setLeaveClanResponseProto(resBuilder.build());
				server.writeEvent(resEvent);

			} else {
				//only write to clan if success
				resEvent.setLeaveClanResponseProto(resBuilder.build());
				server.writeClanEvent(resEvent, clanId);
				//this works for other clan members, but not for the person 
				//who left (they see the message when they join a clan, reenter clan house
				//notifyClan(user, clan);
			}
		} catch (Exception e) {
			log.error("exception in LeaveClan processEvent", e);
			try {
				resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);
				LeaveClanResponseEvent resEvent = new LeaveClanResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setLeaveClanResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in LeaveClan processEvent", e);
			}
		} finally {
			if (null != clanUuid && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}/* else {
				server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
				}*/
		}
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

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtil() {
		return clanChatPostRetrieveUtil;
	}

	public void setClanChatPostRetrieveUtil(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil) {
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
	}

	public TimeUtils getTimeUtil() {
		return timeUtils;
	}

	public void setTimeUtil(TimeUtils timeUtil) {
		this.timeUtils = timeUtil;
	}

	public HazelcastClanSearchImpl getHzClanSearch() {
		return hzClanSearch;
	}

	public void setHzClanSearch(HazelcastClanSearchImpl hzClanSearch) {
		this.hzClanSearch = hzClanSearch;
	}


}
