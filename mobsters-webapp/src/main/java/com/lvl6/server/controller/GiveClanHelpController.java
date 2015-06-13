package com.lvl6.server.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.HazelcastClanSearchImpl;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.GiveClanHelpRequestEvent;
import com.lvl6.events.response.GiveClanHelpResponseEvent;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.ClanHelpCountForUser;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanHelpProto;
import com.lvl6.proto.EventClanProto.GiveClanHelpRequestProto;
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto;
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto.Builder;
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto.GiveClanHelpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class GiveClanHelpController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected TimeUtils timeUtil;
	
	@Autowired
	protected HazelcastClanSearchImpl hzClanSearch;

	public GiveClanHelpController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new GiveClanHelpRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_GIVE_CLAN_HELP_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		GiveClanHelpRequestProto reqProto = ((GiveClanHelpRequestEvent) event)
				.getGiveClanHelpRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String clanId = null;

		if (null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}

		//NOTE: For all these ids, append userId to helperIds property
		List<String> clanHelpIds = reqProto.getClanHelpUuidsList();

		GiveClanHelpResponseProto.Builder resBuilder = GiveClanHelpResponseProto
				.newBuilder();
		resBuilder.setStatus(GiveClanHelpStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);
			UUID.fromString(clanId);

			for (String clanHelpId : clanHelpIds) {
				UUID.fromString(clanHelpId);
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(
					String.format(
							"UUID error. incorrect userId=%s, clanId=%s, clanHelpIds=%s",
							userId, clanId, clanHelpIds), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(GiveClanHelpStatus.FAIL_OTHER);
			GiveClanHelpResponseEvent resEvent = new GiveClanHelpResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
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
			//      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);

			boolean legitLeave = checkLegitLeave(resBuilder, userId, clanId);

			boolean success = false;
			if (legitLeave) {
				success = writeChangesToDB(userId, clanId, clanHelpIds);
			}

			GiveClanHelpResponseEvent resEvent = new GiveClanHelpResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			if (!success) {
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);

			} else {
				//only write to clan if success
				//send back most up to date ClanHelps that changed
				//NOTE: Sending most up to date ClanHelps incurs a db read
				hzClanSearch.updateRankForClanSearch(clanId, new Date(), clanHelpIds.size(), 0, 0, 0, 0);
				setClanHelpings(resBuilder, null, senderProto, clanHelpIds);
				resBuilder.setStatus(GiveClanHelpStatus.SUCCESS);
				resEvent.setResponseProto(resBuilder.build());
				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
			}

		} catch (Exception e) {
			log.error("exception in GiveClanHelp processEvent", e);
			try {
				resBuilder.setStatus(GiveClanHelpStatus.FAIL_OTHER);
				GiveClanHelpResponseEvent resEvent = new GiveClanHelpResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in GiveClanHelp processEvent", e);
			}
		} /*finally {
			if (0 != clanId && lockedClan) {
				getLocker().unlockClan(clanId);
			} else {
				locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
			}
			}*/
	}

	private boolean checkLegitLeave(Builder resBuilder, String userId,
			String clanId) {

		if (userId == null || clanId == null) {
			log.error("invalid: userId=%s, clanId=%s", userId, clanId);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(String userId, String clanId,
			List<String> clanHelpIds) {
		int numUpdated = UpdateUtils.get().updateClanHelp(userId, clanId,
				clanHelpIds);

		log.info(String.format("numUpdated=%s", numUpdated));

		User user = getUserRetrieveUtils().getUserById(userId);
		boolean updated = user.updateClanHelps(clanHelpIds.size());
		log.info(String.format("updated=%s", updated));

		int solicited = 0;
		int given = clanHelpIds.size();
		Date date = timeUtil.createDateAtStartOfDay(new Date());
		ClanHelpCountForUser chcfu = new ClanHelpCountForUser(userId, clanId,
				date, solicited, given);

		try {
			numUpdated = InsertUtils.get().insertIntoUpdateClanHelpCount(chcfu);
			log.info("numUpdated ClanHelpCountForUser={}", numUpdated);
		} catch (Exception e) {
			log.error(String.format(
					"Unable to increment ClanHelpCountForUser: %s", chcfu), e);
		}

		return true;
	}

	private void setClanHelpings(Builder resBuilder, User solicitor,
			MinimumUserProto mup, List<String> clanHelpIds) {
		List<ClanHelp> modifiedSolicitations = clanHelpRetrieveUtil
				.getClanHelpsForIds(clanHelpIds);

		for (ClanHelp aid : modifiedSolicitations) {
			//only need the name
			MinimumUserProto mup2 = MinimumUserProto.newBuilder()
					.setUserUuid(aid.getUserId()).build();

			//only need name not clan
			ClanHelpProto chp = createInfoProtoUtils
					.createClanHelpProtoFromClanHelp(aid, solicitor, null, mup2);

			resBuilder.addClanHelps(chp);
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

	public ClanHelpRetrieveUtil getClanHelpRetrieveUtil() {
		return clanHelpRetrieveUtil;
	}

	public void setClanHelpRetrieveUtil(
			ClanHelpRetrieveUtil clanHelpRetrieveUtil) {
		this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

}
