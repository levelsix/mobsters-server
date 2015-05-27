package com.lvl6.server.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CreateClanRequestEvent;
import com.lvl6.events.response.CreateClanResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.misc.MiscMethods;
import com.lvl6.misc.Notification;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.PvpLeagueForUserDao;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventClanProto.CreateClanRequestProto;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.CreateClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.CreateClanAction;
import com.lvl6.server.controller.utils.ResourceUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class CreateClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected ClanSearch clanSearch;
	
	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;
	
	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;
	
	@Autowired
	protected ResourceUtil resourceUtil;
	
	@Autowired
	protected PvpLeagueForUserDao pvpLeagueForUserDao;
	
	
	public CreateClanController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CreateClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_CREATE_CLAN_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		CreateClanRequestProto reqProto = ((CreateClanRequestEvent)event).getCreateClanRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String clanName = reqProto.getName();
		String tag = reqProto.getTag();
		boolean requestToJoinRequired = reqProto.getRequestToJoinClanRequired();
		String description = reqProto.getDescription();
		int clanIconId = reqProto.getClanIconId();
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		//most likely never positive
		int cashChange = reqProto.getCashChange();

		CreateClanResponseProto.Builder resBuilder = CreateClanResponseProto.newBuilder();
		resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s",
					userId), e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			CreateClanResponseEvent resEvent = new CreateClanResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setCreateClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			
			CreateClanAction cca = new CreateClanAction(userId, cashChange, gemsSpent, userRetrieveUtil,
					insertUtil, deleteUtil, miscMethods, clanName, tag, requestToJoinRequired, 
					description, clanIconId, clanRetrieveUtils, resourceUtil, pvpLeagueForUserDao);
			
			cca.execute(resBuilder);

			if (CreateClanStatus.SUCCESS.equals(resBuilder.getStatus())) {
				resBuilder.setClanInfo(createInfoProtoUtils.createMinimumClanProtoFromClan(cca.getCreatedClan()));
				updateClanCache(cca.getCreatedClan());
			}

			CreateClanResponseEvent resEvent = new CreateClanResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setCreateClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (CreateClanStatus.SUCCESS.equals(resBuilder.getStatus())) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(cca.getUser(), null, cca.getCreatedClan());
				resEventUpdate.setTag(event.getTag());
//				server.writeEvent(resEventUpdate);

				sendGeneralNotification(cca.getUser().getName(), clanName);

				writeToUserCurrencyHistory(cca);
			}
		} catch (Exception e) {
			log.error("exception in CreateClan processEvent", e);
			try {
				resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
				CreateClanResponseEvent resEvent = new CreateClanResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setCreateClanResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in CreateClan processEvent", e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private void sendGeneralNotification(String userName, String clanName) {
		Notification createClanNotification = new Notification();
		createClanNotification.setAsClanCreated(userName, clanName);

		miscMethods.writeGlobalNotification(createClanNotification, server);
	}

	private void updateClanCache(Clan clan) {
		String clanId = clan.getId();
		//need to account for this user creating clan
		int clanSize = 1;
		Date lastChatTime = ControllerConstants.INCEPTION_DATE;

		clanSearch.updateClanSearchRank(clanId, clanSize, lastChatTime);
	}

	private void writeToUserCurrencyHistory(CreateClanAction cca) {
		miscMethods.writeToUserCurrencyOneUser(cca.getUserId(), cca.getCreateTime(),
				cca.getCurrencyDeltas(), cca.getPrevCurrencies(), cca.getCurCurrencies(),
				cca.getReasonsForChanges(), cca.getDetails());
	}


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

	public ClanSearch getClanSearch() {
		return clanSearch;
	}

	public void setClanSearch(ClanSearch clanSearch) {
		this.clanSearch = clanSearch;
	}

}
