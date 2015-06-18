package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemSecretGiftRequestEvent;
import com.lvl6.events.response.ReceivedGiftResponseEvent;
import com.lvl6.events.response.RedeemSecretGiftResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftForUserPojo;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftRequestProto;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftResponseProto;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftResponseProto.RedeemSecretGiftStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserSecretGiftProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.ItemSecretGiftForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AwardRewardAction;
import com.lvl6.server.controller.actionobjects.RedeemSecretGiftAction;
import com.lvl6.server.controller.utils.SecretGiftUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class RedeemSecretGiftController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemSecretGiftController.class);

	public RedeemSecretGiftController() {

	}


	@Autowired
	protected Locker locker;

	@Autowired
	ItemSecretGiftForUserRetrieveUtil itemSecretGiftForUserRetrieveUtil;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected SecretGiftUtils secretGiftUtils;

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
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		List<String> idsRedeemed = reqProto.getUisgUuidList();

		RedeemSecretGiftResponseProto.Builder resBuilder = RedeemSecretGiftResponseProto
				.newBuilder();
		resBuilder.setMup(senderProto);
		resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);
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
			resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);
			RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass()
				.getSimpleName());
		try {
			//
			RedeemSecretGiftAction rsga = new RedeemSecretGiftAction(userId,
					idsRedeemed, clientTime, itemSecretGiftForUserRetrieveUtil,
					userRetrieveUtil, itemForUserRetrieveUtil, secretGiftUtils,
					DeleteUtils.get(), UpdateUtils.get(), InsertUtils.get());

			rsga.execute(resBuilder);

			if (RedeemSecretGiftStatus.SUCCESS.equals(resBuilder.getStatus())) {
				Collection<SecretGiftForUserPojo> nuGifts = rsga.getGifts();
				Collection<UserSecretGiftProto> nuGiftsProtos = createInfoProtoUtils
						.createUserSecretGiftProto(nuGifts);
				log.info("setting nuGifts: {},\t protos: {}",
						nuGifts, nuGiftsProtos);
				resBuilder.addAllNuGifts(nuGiftsProtos);
				resBuilder.setRewards(rsga.getUrp());
			}

			RedeemSecretGiftResponseProto resProto = resBuilder.build();
			RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (RedeemSecretGiftStatus.SUCCESS.equals(resBuilder.getStatus())) {
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
				resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);
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
			locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass()
					.getSimpleName());
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


	public ItemSecretGiftForUserRetrieveUtil getSecretGiftForUserPojoRetrieveUtil() {
		return itemSecretGiftForUserRetrieveUtil;
	}

	public void setSecretGiftForUserPojoRetrieveUtil(
			ItemSecretGiftForUserRetrieveUtil itemSecretGiftForUserPojoRetrieveUtil) {
		this.itemSecretGiftForUserRetrieveUtil = itemSecretGiftForUserPojoRetrieveUtil;
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
