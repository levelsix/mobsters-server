package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemMiniEventRewardRequestEvent;
import com.lvl6.events.response.ReceivedGiftResponseEvent;
import com.lvl6.events.response.RedeemMiniEventRewardResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardRequestProto;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardRequestProto.RewardTier;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardResponseProto;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AwardRewardAction;
import com.lvl6.server.controller.actionobjects.RedeemMiniEventRewardAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class RedeemMiniEventRewardController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemMiniEventRewardController.class);

	public RedeemMiniEventRewardController() {

	}

	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected Locker locker;

	@Autowired
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	protected GiftRewardRetrieveUtils giftRewardRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected MiniEventForUserRetrieveUtil mefuRetrieveUtil;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected  MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils;

	@Autowired
	protected MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils;;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Override
	public RequestEvent createRequestEvent() {
		return new RedeemMiniEventRewardRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REDEEM_MINI_EVENT_REWARD_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RedeemMiniEventRewardRequestProto reqProto = ((RedeemMiniEventRewardRequestEvent) event)
				.getRedeemMiniEventRewardRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProtoWithMaxResources senderProtoMaxResources = reqProto.getSender();
		MinimumUserProto senderProto = senderProtoMaxResources.getMinUserProto();
		String userId = senderProto.getUserUuid();
		RewardTier rt = reqProto.getTierRedeemed();
		int mefplId = reqProto.getMefplId();
		Date clientTime = new Date(reqProto.getClientTime());
		int maxCash = senderProtoMaxResources.getMaxCash();
		int maxOil = senderProtoMaxResources.getMaxOil();

		RedeemMiniEventRewardResponseProto.Builder resBuilder = RedeemMiniEventRewardResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			RedeemMiniEventRewardResponseEvent resEvent = 
					new RedeemMiniEventRewardResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s",
					userId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RedeemMiniEventRewardResponseEvent resEvent = new RedeemMiniEventRewardResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(UUID.fromString(userId), this.getClass().getSimpleName());
		try {

			RedeemMiniEventRewardAction rmera = new RedeemMiniEventRewardAction(
					userId, null, maxCash, maxOil, mefplId, rt, clientTime,
					giftRetrieveUtil,
					giftRewardRetrieveUtils, userClanRetrieveUtils,
					userRetrieveUtil, mefuRetrieveUtil, itemForUserRetrieveUtil,
					insertUtil, updateUtil, monsterStuffUtils,
					miniEventForPlayerLvlRetrieveUtils,
					miniEventTierRewardRetrieveUtils,
					rewardRetrieveUtils,
					monsterLevelInfoRetrieveUtils,
					createInfoProtoUtils);

			rmera.execute(resBuilder);

			boolean success = resBuilder.getStatus().equals(ResponseStatus.SUCCESS);
			if (success) {
				Collection<ItemForUser> nuOrUpdatedItems = rmera.getNuOrUpdatedItems();
				Collection<FullUserMonsterProto> fumpList = rmera.getNuOrUpdatedMonsters();
				int gemsGained = rmera.getGemsGained();
				int cashGained = rmera.getCashGained();
				int oilGained = rmera.getOilGained();


				//TODO: protofy the rewards
				UserRewardProto urp = createInfoProtoUtils.createUserRewardProto(
						nuOrUpdatedItems, fumpList, gemsGained, cashGained, oilGained, 0, null);
				resBuilder.setRewards(urp);
			}

			RedeemMiniEventRewardResponseProto resProto = resBuilder.build();
			RedeemMiniEventRewardResponseEvent resEvent = new RedeemMiniEventRewardResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (success && rmera.isAwardedResources()) {
				User u = rmera.getUser();

				if (null != u) {
					UpdateClientUserResponseEvent resEventUpdate = miscMethods()
							.createUpdateClientUserResponseEventAndUpdateLeaderboard(
									u, null, null);
					resEventUpdate.setTag(event.getTag());
					responses.normalResponseEvents().add(resEventUpdate);

					sendClanGiftIfExists(responses, userId, rmera);

					writeToCurrencyHistory(userId, clientTime, rmera);
				} else {
					log.warn("unable to update client's user.");
				}
			}

		} catch (Exception e) {
			log.error("exception in RedeemMiniEventRewardController processEvent",
					e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RedeemMiniEventRewardResponseEvent resEvent = new RedeemMiniEventRewardResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RedeemMiniEventRewardController processEvent",
						e);
			}

		} finally {
			locker.unlockPlayer(UUID.fromString(userId), this.getClass().getSimpleName());
		}
	}

	private void sendClanGiftIfExists(
			ToClientEvents responses,
			String userId,
			RedeemMiniEventRewardAction rmea) {
		try {
			AwardRewardAction ara = rmea.getAra();
			if (null != ara && ara.existsClanGift()) {
				ReceivedGiftResponseProto rgrp = ara.getClanGift();
				ReceivedGiftResponseEvent rgre = new ReceivedGiftResponseEvent(userId);
				rgre.setResponseProto(rgrp);
				String clanId = rmea.getUser().getClanId();

				responses.clanResponseEvents().add(new ClanResponseEvent(rgre, clanId, false));
			}
		} catch (Exception e) {
			log.error("failed to send ClanGift notification", e);
		}
	}


	private void writeToCurrencyHistory(String userId, Date date,
			RedeemMiniEventRewardAction rmera)
	{
		Timestamp timestamp = new Timestamp(date.getTime());
		miscMethods().writeToUserCurrencyOneUser(userId, timestamp,
				rmera.getCurrencyDeltas(), rmera.getPreviousCurrencies(),
				rmera.getCurrentCurrencies(), rmera.getReasons(),
				rmera.getDetails());
	}

	public MiniEventForUserRetrieveUtil getMefuRetrieveUtil() {
		return mefuRetrieveUtil;
	}

	public void setMefuRetrieveUtil(MiniEventForUserRetrieveUtil mefuRetrieveUtil) {
		this.mefuRetrieveUtil = mefuRetrieveUtil;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
