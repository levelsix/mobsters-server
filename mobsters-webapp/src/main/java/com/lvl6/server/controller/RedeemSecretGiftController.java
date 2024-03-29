package com.lvl6.server.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemSecretGiftRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.ReceivedGiftResponseEvent;
import com.lvl6.events.response.RedeemSecretGiftResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftForUserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftRequestProto;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserSecretGiftProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.SecretGiftForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AwardRewardAction;
import com.lvl6.server.controller.actionobjects.RedeemSecretGiftAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.SecretGiftUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class RedeemSecretGiftController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemSecretGiftController.class);

	public RedeemSecretGiftController() {

	}

	@Autowired
	SecretGiftForUserRetrieveUtil secretGiftForUserRetrieveUtil;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected Locker locker;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtil;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	private RewardRetrieveUtils rewardRetrieveUtil;

	@Autowired
	private UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	private CreateInfoProtoUtils createInfoProtoUtil;

	@Autowired
	protected SecretGiftUtils secretGiftUtils;

	@Autowired
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	private GiftRewardRetrieveUtils giftRewardRetrieveUtil;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected TimeUtils timeUtils;

	@Override
	public RequestEvent createRequestEvent() {
		return new RedeemSecretGiftRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REDEEM_SECRET_GIFT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RedeemSecretGiftRequestProto reqProto = ((RedeemSecretGiftRequestEvent) event)
				.getRedeemSecretGiftRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Date clientTime = new Date(reqProto.getClientTime());
		List<String> idsRedeemed = reqProto.getUisgUuidList();

		RedeemSecretGiftResponseProto.Builder resBuilder = RedeemSecretGiftResponseProto
				.newBuilder();
		resBuilder.setMup(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) >
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			RedeemSecretGiftResponseEvent resEvent =
					new RedeemSecretGiftResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			StringUtils.convertToUUID(idsRedeemed);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, itemIdsRedeemed=%s",
					userId, idsRedeemed), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			log.info("invalid UUIDS.");
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());
			RedeemSecretGiftAction rsga = new RedeemSecretGiftAction(userId,
					idsRedeemed, clientTime, secretGiftForUserRetrieveUtil,
					userRetrieveUtil, itemForUserRetrieveUtil,
					monsterStuffUtil, monsterLevelInfoRetrieveUtils,
					giftRetrieveUtil,
					giftRewardRetrieveUtil, rewardRetrieveUtil,
					userClanRetrieveUtils, createInfoProtoUtils,
					secretGiftUtils,
					deleteUtil, updateUtil,insertUtil);
			rsga.execute(resBuilder);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				Collection<SecretGiftForUserPojo> nuGifts = rsga.getGifts();
				Collection<UserSecretGiftProto> nuGiftsProtos = createInfoProtoUtils
						.createUserSecretGiftProto(nuGifts);
				log.info("setting nuGifts: {},\t protos: {}",
						nuGifts, nuGiftsProtos);
				resBuilder.addAllNuGifts(nuGiftsProtos);

				resBuilder.setReward(rsga.getUrp());
			}

			RedeemSecretGiftResponseProto resProto = resBuilder.build();
			RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				//last_secret_gift time in user is modified, need to
				//update client's user
				User u = rsga.getUser();
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								u, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				sendClanGiftIfExists(responses, userId, rsga);
			}

		} catch (Exception e) {
			log.error("exception in RedeemSecretGiftController processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RedeemSecretGiftController processEvent",
						e);
			}

		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private void sendClanGiftIfExists(
			ToClientEvents responses,
			String userId,
			RedeemSecretGiftAction rsga) {
		try {
			AwardRewardAction ara = rsga.getAra();
			if (null != ara && ara.existsClanGift()) {
				ReceivedGiftResponseProto rgrp = ara.getClanGift();
				ReceivedGiftResponseEvent rgre = new ReceivedGiftResponseEvent(userId);
				rgre.setResponseProto(rgrp);
				String clanId = rsga.getUser().getClanId();

				responses.clanResponseEvents().add(new ClanResponseEvent(rgre, clanId, false));
			}
		} catch (Exception e) {
			log.error("failed to send ClanGift notification", e);
		}
	}


	public SecretGiftForUserRetrieveUtil getSecretGiftForUserPojoRetrieveUtil() {
		return secretGiftForUserRetrieveUtil;
	}

	public void setSecretGiftForUserPojoRetrieveUtil(
			SecretGiftForUserRetrieveUtil secretGiftForUserPojoRetrieveUtil) {
		this.secretGiftForUserRetrieveUtil = secretGiftForUserPojoRetrieveUtil;
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

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
