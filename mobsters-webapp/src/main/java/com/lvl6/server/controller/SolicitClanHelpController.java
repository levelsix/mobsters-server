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

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SolicitClanHelpRequestEvent;
import com.lvl6.events.response.SolicitClanHelpResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.ClanHelpCountForUser;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanHelpNoticeProto;
import com.lvl6.proto.ClanProto.ClanHelpProto;
import com.lvl6.proto.EventClanProto.SolicitClanHelpRequestProto;
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto;
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto.Builder;
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto.SolicitClanHelpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
@DependsOn("gameServer")
public class SolicitClanHelpController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected TimeUtils timeUtil;

	public SolicitClanHelpController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SolicitClanHelpRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SOLICIT_CLAN_HELP_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		SolicitClanHelpRequestProto reqProto = ((SolicitClanHelpRequestEvent) event)
				.getSolicitClanHelpRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		List<ClanHelpNoticeProto> chnpList = reqProto.getNoticeList();
		String userId = senderProto.getUserUuid();
		Date clientDate = new Date(reqProto.getClientTime());
		int maxHelpers = reqProto.getMaxHelpers();

		SolicitClanHelpResponseProto.Builder resBuilder = SolicitClanHelpResponseProto
				.newBuilder();
		resBuilder.setStatus(SolicitClanHelpStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = null;

		if (null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
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
			resBuilder.setStatus(SolicitClanHelpStatus.FAIL_OTHER);
			SolicitClanHelpResponseEvent resEvent = new SolicitClanHelpResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setSolicitClanHelpResponseProto(resBuilder.build());
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
			server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}*/
		try {
			User user = getUserRetrieveUtils().getUserById(userId);

			boolean legitLeave = checkLegitLeave(resBuilder, user, clanId);

			boolean success = false;
			List<ClanHelp> clanHelpContainer = new ArrayList<ClanHelp>();
			if (legitLeave) {
				success = writeChangesToDB(userId, user, clanId, chnpList,
						clientDate, maxHelpers, clanHelpContainer);
			}

			SolicitClanHelpResponseEvent resEvent = new SolicitClanHelpResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			//only write to user if failed
			if (!success) {
				resEvent.setSolicitClanHelpResponseProto(resBuilder.build());
				server.writeEvent(resEvent);

			} else {
				//only write to clan if success
				for (ClanHelp ch : clanHelpContainer) {
					ClanHelpProto chp = createInfoProtoUtils
							.createClanHelpProtoFromClanHelp(ch, user, null,
									senderProto);
					resBuilder.addHelpProto(chp);
				}

				resBuilder.setStatus(SolicitClanHelpStatus.SUCCESS);
				resEvent.setSolicitClanHelpResponseProto(resBuilder.build());
				server.writeClanEvent(resEvent, clanId);
				//this works for other clan members, but not for the person 
				//who left (they see the message when they join a clan, reenter clan house
				//notifyClan(user, clan);
			}
		} catch (Exception e) {
			log.error("exception in SolicitClanHelp processEvent", e);
			try {
				resBuilder.setStatus(SolicitClanHelpStatus.FAIL_OTHER);
				SolicitClanHelpResponseEvent resEvent = new SolicitClanHelpResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setSolicitClanHelpResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SolicitClanHelp processEvent", e);
			}
		} /*finally {
			if (0 != clanId && lockedClan) {
				getLocker().unlockClan(clanId);
			} else {
				server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
			}
			}*/
	}

	private boolean checkLegitLeave(Builder resBuilder, User user, String clanId) {
		if (null == user) {
			log.error("user is null");
			return false;
		}

		String clanIdUser = user.getClanId();
		if (null == clanIdUser || clanIdUser.isEmpty()) {
			resBuilder.setStatus(SolicitClanHelpStatus.FAIL_NOT_IN_CLAN);
			log.error("user's clanId={}", clanId);
			return false;
		}

		Clan clan = getClanRetrieveUtil().getClanWithId(clanIdUser);
		if (null == clan) {
			resBuilder.setStatus(SolicitClanHelpStatus.FAIL_NOT_IN_CLAN);
			log.error("no clan with clanId={}", clanIdUser);
			return false;
		}

		if (!clanIdUser.equals(clanId)) {
			resBuilder.setStatus(SolicitClanHelpStatus.FAIL_NOT_IN_CLAN);
			log.error("inconsistent clanIds. proto clanId={}, clanIdUser={}",
					clanId, clanIdUser);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(String userId, User user, String clanId,
			List<ClanHelpNoticeProto> chnpList, Date clientDate,
			int maxHelpers, List<ClanHelp> clanHelpContainer) {
		List<ClanHelp> solicitations = new ArrayList<ClanHelp>();
		for (ClanHelpNoticeProto chnp : chnpList) {
			ClanHelp ch = new ClanHelp();
			ch.setClanId(clanId);
			ch.setUserId(userId);
			ch.setMaxHelpers(maxHelpers);
			ch.setTimeOfEntry(clientDate);
			ch.setUserDataId(chnp.getUserDataUuid());
			ch.setStaticDataId(chnp.getStaticDataId());
			ch.setHelpType(chnp.getHelpType().name());
			ch.setOpen(true);
			solicitations.add(ch);
		}

		List<String> clanHelpIds = InsertUtils.get().insertIntoClanHelpGetId(
				solicitations);

		for (int index = 0; index < clanHelpIds.size(); index++) {
			ClanHelp ch = solicitations.get(index);
			String clanHelpId = clanHelpIds.get(index);
			ch.setId(clanHelpId);
		}
		log.info(String.format("new clanHelps: %s", solicitations));

		if (null == clanHelpIds || clanHelpIds.isEmpty()) {
			return false;
		}

		int solicited = clanHelpIds.size();
		int given = 0;
		Date date = timeUtil.createDateAtStartOfDay(clientDate);
		ClanHelpCountForUser chcfu = new ClanHelpCountForUser(userId, clanId,
				date, solicited, given);

		try {
			int numUpdated = InsertUtils.get().insertIntoUpdateClanHelpCount(
					chcfu);
			log.info("numUpdated ClanHelpCountForUser={}", numUpdated);
		} catch (Exception e) {
			log.error(String.format(
					"Unable to increment ClanHelpCountForUser: %s", chcfu), e);
		}

		clanHelpContainer.addAll(solicitations);
		return true;
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

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public TimeUtils getTimeUtil() {
		return timeUtil;
	}

	public void setTimeUtil(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
	}

}
