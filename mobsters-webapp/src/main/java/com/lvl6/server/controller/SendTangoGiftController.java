package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SendTangoGiftRequestEvent;
import com.lvl6.events.response.ReceivedGiftResponseEvent;
import com.lvl6.events.response.SendTangoGiftResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUserPojo;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.EventRewardProto.SendTangoGiftRequestProto;
import com.lvl6.proto.EventRewardProto.SendTangoGiftResponseProto;
import com.lvl6.proto.EventRewardProto.SendTangoGiftResponseProto.SendTangoGiftStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.actionobjects.SendTangoGiftAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class SendTangoGiftController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(SendTangoGiftController.class);

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	protected GiftRewardRetrieveUtils giftRewardRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtil;


	@Autowired
	protected MiscMethods miscMethods;

	public SendTangoGiftController() {
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SendTangoGiftRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SEND_TANGO_GIFT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses){
		SendTangoGiftRequestProto reqProto = ((SendTangoGiftRequestEvent) event)
				.getSendTangoGiftRequestProto();
		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Date clientTime = new Date(reqProto.getClientTime());
		List<String> tangoIds = reqProto.getTangoUserIdsList();
		String senderTangoUserId = reqProto.getSenderTangoUserId();
		int gemReward = reqProto.getGemReward();
		String senderTangoName = reqProto.getSenderTangoName();

		SendTangoGiftResponseProto.Builder resBuilder = SendTangoGiftResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(SendTangoGiftStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(SendTangoGiftStatus.FAIL_OTHER);
			SendTangoGiftResponseEvent resEvent = new SendTangoGiftResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//    server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			Set<String> uniqTangoIds = new HashSet<String>(tangoIds);
			SendTangoGiftAction stga = new SendTangoGiftAction(
					userId, senderTangoUserId, senderTangoName, gemReward,
					clientTime, uniqTangoIds,
					userRetrieveUtil, giftRetrieveUtil,
					giftRewardRetrieveUtil, insertUtil);
			stga.execute(resBuilder);

			if ( SendTangoGiftStatus.SUCCESS.equals(resBuilder.getStatus()) ) {
				Collection<String> tangoIdsNotInToonSquad = stga
						.getNonToonSquadTangoUserIds();
				resBuilder.addAllTangoUserIdsNotInToonSquad(tangoIdsNotInToonSquad);

				Collection<String> tangoIdsInToonSquad = stga
						.getToonSquadTangoUserIds();
				resBuilder.addAllTangoUserIdsInToonSquad(tangoIdsInToonSquad);
			}

			SendTangoGiftResponseProto resProto = resBuilder.build();
			log.info("resProto={}", resProto);
			SendTangoGiftResponseEvent resEvent = new SendTangoGiftResponseEvent(
					senderProto.getUserUuid());
			resEvent.setResponseProto(resProto);
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);

			if ( SendTangoGiftStatus.SUCCESS.equals(resBuilder.getStatus()) ) {

				List<GiftForUserPojo> receiverGifts = stga.getReceiverGifts();
				log.info("receiverGifts={}", receiverGifts);
				if (null != receiverGifts && !receiverGifts.isEmpty()) {
					//notify users who did get gifts
					Map<String, GiftForTangoUserPojo> gfuIdToGftu = stga
							.getGiftForUserPojoIdToGiftForTangoUserPojo();
					GiftConfigPojo tg = stga.getTangoGift();
					for (GiftForUserPojo gfu : receiverGifts) {
						String receiverUserId = gfu.getReceiverUserId();
						ReceivedGiftResponseEvent rgre = new ReceivedGiftResponseEvent(
								receiverUserId);
						ReceivedGiftResponseProto.Builder rgrp = ReceivedGiftResponseProto
								.newBuilder();
						UserGiftProto ugp = createUserGiftProto(senderProto,
								gfuIdToGftu, tg, gfu, rgrp);
						rgrp.addUserGifts(ugp);

						rgre.setResponseProto(rgrp.build());

						log.info("sending responseProto={}", rgrp.build());
						responses.normalResponseEvents().add(rgre);
					}
				}
				User gifter = stga.getGifter();
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								gifter, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToCurrencyHistory(userId, clientTime, stga);

			}

		} catch (Exception e) {
			log.error("exception in SendTangoGiftController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(SendTangoGiftStatus.FAIL_OTHER);
				SendTangoGiftResponseEvent resEvent = new SendTangoGiftResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SendTangoGiftController processEvent",
						e);
			}
		} finally {
			//      server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}
	}

	private UserGiftProto createUserGiftProto(MinimumUserProto senderProto,
			Map<String, GiftForTangoUserPojo> gfuIdToGftu, GiftConfigPojo tg,
			GiftForUserPojo gfu, ReceivedGiftResponseProto.Builder rgrp) {
		rgrp.setSender(senderProto);
		rgrp.setScope(ChatScope.PRIVATE);

		int rewardId = gfu.getRewardId();
		Reward r = rewardRetrieveUtil.getRewardById(rewardId);
		GiftForTangoUserPojo gftu = gfuIdToGftu.get(gfu.getId());

		UserGiftProto ugp = createInfoProtoUtils.createUserGiftProto(
				gfu, senderProto, r, tg, gftu);
		return ugp;
	}

	private void writeToCurrencyHistory(String userId, Date date,
			SendTangoGiftAction stga)
	{
		Timestamp timestamp = new Timestamp(date.getTime());
		miscMethods.writeToUserCurrencyOneUser(userId, timestamp,
				stga.getCurrencyDeltas(), stga.getPreviousCurrencies(),
				stga.getCurrentCurrencies(), stga.getReasons(),
				stga.getDetails());
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}
}
