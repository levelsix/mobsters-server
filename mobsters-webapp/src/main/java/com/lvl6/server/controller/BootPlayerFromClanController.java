package com.lvl6.server.controller;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.clansearch.HazelcastClanSearchImpl;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BootPlayerFromClanRequestEvent;
import com.lvl6.events.response.BootPlayerFromClanResponseEvent;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanRequestProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto.BootPlayerFromClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.BootPlayerFromClanAction;
import com.lvl6.server.controller.utils.ClanStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class BootPlayerFromClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtil;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;

	@Autowired
	protected HazelcastClanSearchImpl hzClanSearch;
	
	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;
	
	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;
	
	@Autowired
	protected ClanStuffUtils clanStuffUtils;
	
	@Autowired
	protected ClanSearch clanSearch;
	
	@Autowired
	protected ServerToggleRetrieveUtils toggle;
	
	
	public BootPlayerFromClanController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new BootPlayerFromClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BOOT_PLAYER_FROM_CLAN_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		BootPlayerFromClanRequestProto reqProto = ((BootPlayerFromClanRequestEvent) event)
				.getBootPlayerFromClanRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String playerToBootId = reqProto.getPlayerToBootUuid();

		BootPlayerFromClanResponseProto.Builder resBuilder = BootPlayerFromClanResponseProto
				.newBuilder();
		resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = "";

		UUID userUuid = null;
		UUID playerToBootUuid = null;
		UUID clanUuid = null;

		boolean invalidUuids = true;
		if (senderProto.hasClan() && null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
			try {
				userUuid = UUID.fromString(userId);
				playerToBootUuid = UUID.fromString(playerToBootId);

				clanUuid = UUID.fromString(clanId);
				invalidUuids = false;
			} catch (Exception e) {
				log.error(
						String.format(
								"UUID error. incorrect userId=%s, playerToBootId=%s clanId=%s",
								userId, playerToBootId, clanId), e);
			}
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
			BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean lockedClan = false;
		lockedClan = getLocker().lockClan(clanUuid);
		try {
			BootPlayerFromClanAction bpfca = new BootPlayerFromClanAction(userId, playerToBootId,
					lockedClan, userRetrieveUtils, insertUtil, updateUtil, deleteUtil, timeUtils, 
					clanRetrieveUtils, userClanRetrieveUtils, clanStuffUtils, 
					clanChatPostRetrieveUtil, hzClanSearch, clanSearch, toggle);
			bpfca.execute(resBuilder);
			
			if (BootPlayerFromClanStatus.SUCCESS.equals(resBuilder.getStatus())) {
				MinimumUserProto mup = createInfoProtoUtils
						.createMinimumUserProtoFromUserAndClan(bpfca.getPlayerToBoot(),
								null);
				resBuilder.setPlayerToBoot(mup);
			}

			BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());

			if (BootPlayerFromClanStatus.SUCCESS.equals(resBuilder.getStatus())) {
				//if successful write to clan
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
				responses.setUserId(userId);
				responses.setClanChanged(true);
				responses.setNewClanId(clanId);
			} else {
				//write to user if fail
				responses.normalResponseEvents().add(resEvent);
			}
		} catch (Exception e) {
			log.error("exception in BootPlayerFromClan processEvent", e);
			try {
				resBuilder.setStatus(BootPlayerFromClanStatus.FAIL_OTHER);
				BootPlayerFromClanResponseEvent resEvent = new BootPlayerFromClanResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in BootPlayerFromClan processEvent", e);
			}
		} finally {
			if (null != clanUuid && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtil() {
		return timeUtil;
	}

	public void setTimeUtil(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
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

	public HazelcastClanSearchImpl getHzClanSearch() {
		return hzClanSearch;
	}

	public void setHzClanSearch(HazelcastClanSearchImpl hzClanSearch) {
		this.hzClanSearch = hzClanSearch;
	}



}
