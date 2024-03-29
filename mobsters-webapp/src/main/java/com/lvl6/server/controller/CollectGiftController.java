package com.lvl6.server.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CollectGiftRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.CollectGiftResponseEvent;
import com.lvl6.events.response.ReceivedGiftResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventRewardProto.CollectGiftRequestProto;
import com.lvl6.proto.EventRewardProto.CollectGiftResponseProto;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.GiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AwardRewardAction;
import com.lvl6.server.controller.actionobjects.CollectGiftAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class CollectGiftController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(CollectGiftController.class);

	public CollectGiftController() {
	}

	@Autowired
	protected GiftForUserRetrieveUtils giftForUserRetrieveUtil;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	protected GiftRewardRetrieveUtils giftRewardRetrieveUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected  MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Override
	public RequestEvent createRequestEvent() {
		return new CollectGiftRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COLLECT_GIFT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses){
		CollectGiftRequestProto reqProto = ((CollectGiftRequestEvent) event)
				.getCollectGiftRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProtoWithMaxResources senderProtoMaxResources = reqProto.getSender();
		MinimumUserProto senderProto = senderProtoMaxResources.getMinUserProto();
		String userId = senderProto.getUserUuid();
		Date clientTime = new Date(reqProto.getClientTime());
		List<String> ugIds = reqProto.getUgUuidsList();

		int maxCash = senderProtoMaxResources.getMaxCash();
		int maxOil = senderProtoMaxResources.getMaxOil();

		CollectGiftResponseProto.Builder resBuilder = CollectGiftResponseProto
				.newBuilder();
		resBuilder.setSender(senderProtoMaxResources);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);
			StringUtils.convertToUUID(ugIds);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, ugIds=%s",
					userId, ugIds), e);
			invalidUuids = true;
		}
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			CollectGiftResponseEvent resEvent = new CollectGiftResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			CollectGiftResponseEvent resEvent = 
					new CollectGiftResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//UUID checks
		if (invalidUuids) {
			log.info("invalid UUIDS.");
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			CollectGiftResponseEvent resEvent = new CollectGiftResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());

			CollectGiftAction cga = new CollectGiftAction(userId,
					maxCash, maxOil, ugIds, clientTime, giftForUserRetrieveUtil,
					userRetrieveUtil, rewardRetrieveUtil, itemForUserRetrieveUtil,
					insertUtil, updateUtil, monsterStuffUtils,
					monsterLevelInfoRetrieveUtils, giftRetrieveUtil,
					giftRewardRetrieveUtils, userClanRetrieveUtils,
					createInfoProtoUtils);
			cga.execute(resBuilder);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				resBuilder.setReward(cga.getUrp());
			}

			CollectGiftResponseProto resProto = resBuilder.build();
			CollectGiftResponseEvent resEvent = new CollectGiftResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				//last_secret_gift time in user is modified, need to
				//update client's user
				User u = cga.getUser();
				UpdateClientUserResponseEvent resEventUpdate =
						miscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(u, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				sendClanGiftIfExists(responses, userId, cga);
			}

		} catch (Exception e) {
			log.error("exception in CollectGiftController processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				CollectGiftResponseEvent resEvent = new CollectGiftResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in CollectGiftController processEvent",
						e);
			}

		} finally {
			if (gotLock) {
				locker.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
			}
		}
	}

	private void sendClanGiftIfExists(
			ToClientEvents responses,
			String userId,
			CollectGiftAction cga) {
		try {
			AwardRewardAction ara = cga.getAra();
			if (null != ara && ara.existsClanGift()) {
				ReceivedGiftResponseProto rgrp = ara.getClanGift();
				ReceivedGiftResponseEvent rgre = new ReceivedGiftResponseEvent(userId);
				rgre.setResponseProto(rgrp);
				String clanId = cga.getUser().getClanId();

				responses.clanResponseEvents().add(new ClanResponseEvent(rgre, clanId, false));
			}
		} catch (Exception e) {
			log.error("failed to send ClanGift notification", e);
		}
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

}
