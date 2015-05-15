package com.lvl6.server.controller;

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
import com.lvl6.info.GiftForTangoUser;
import com.lvl6.info.GiftForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.TangoGift;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.EventRewardProto.SendTangoGiftRequestProto;
import com.lvl6.proto.EventRewardProto.SendTangoGiftResponseProto;
import com.lvl6.proto.EventRewardProto.SendTangoGiftResponseProto.SendTangoGiftStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TangoGiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TangoGiftRewardRetrieveUtils;
import com.lvl6.server.controller.actionobjects.SendTangoGiftAction;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class SendTangoGiftController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected TangoGiftRetrieveUtils tangoGiftRetrieveUtil;

	@Autowired
	protected TangoGiftRewardRetrieveUtils tangoGiftRewardRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtil;

	public SendTangoGiftController() {
		numAllocatedThreads = 1;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
		SendTangoGiftRequestProto reqProto = ((SendTangoGiftRequestEvent) event)
				.getSendTangoGiftRequestProto();
		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Date clientTime = new Date(reqProto.getClientTime());
		List<String> tangoIds = reqProto.getTangoUserIdsList();
		String senderTangoUserId = reqProto.getSenderTangoUserId();

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
			resEvent.setSendTangoGiftResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		//    server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			Set<String> uniqTangoIds = new HashSet<String>(tangoIds);
			SendTangoGiftAction stga = new SendTangoGiftAction(
					userId, senderTangoUserId, clientTime, uniqTangoIds,
					userRetrieveUtil, tangoGiftRetrieveUtil,
					tangoGiftRewardRetrieveUtil, insertUtil);
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
			SendTangoGiftResponseEvent resEvent = new SendTangoGiftResponseEvent(
					senderProto.getUserUuid());
			resEvent.setSendTangoGiftResponseProto(resProto);
			server.writeEvent(resEvent);

			if ( SendTangoGiftStatus.SUCCESS.equals(resBuilder.getStatus()) ) {

				//notify users who did get gifts
				List<GiftForUser> receiverGifts = stga.getReceiverGifts();
				Map<String, GiftForTangoUser> gfuIdToGftu = stga.getGiftForUserIdToGiftForTangoUser();
				TangoGift tg = stga.getTangoGift();

				for (GiftForUser gfu : receiverGifts)
				{
					String receiverUserId = gfu.getReceiverUserId();
					ReceivedGiftResponseEvent rgre = new ReceivedGiftResponseEvent(receiverUserId);
					ReceivedGiftResponseProto.Builder rgrp = ReceivedGiftResponseProto.newBuilder();
					UserGiftProto ugp = createUserGiftProto(senderProto,
							gfuIdToGftu, tg, gfu, rgrp);
					rgrp.addUserGifts(ugp);

					rgre.setReceivedGiftResponseProto(rgrp.build());

					server.writeEvent(rgre);
				}
			}

		} catch (Exception e) {
			log.error("exception in SendTangoGiftController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(SendTangoGiftStatus.FAIL_OTHER);
				SendTangoGiftResponseEvent resEvent = new SendTangoGiftResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setSendTangoGiftResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
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
			Map<String, GiftForTangoUser> gfuIdToGftu, TangoGift tg,
			GiftForUser gfu, ReceivedGiftResponseProto.Builder rgrp) {
		rgrp.setSender(senderProto);
		rgrp.setScope(ChatScope.PRIVATE);

		int rewardId = gfu.getRewardId();
		Reward r = rewardRetrieveUtil.getRewardById(rewardId);
		GiftForTangoUser gftu = gfuIdToGftu.get(gfu.getId());

		UserGiftProto ugp = createInfoProtoUtils.createUserGiftProto(
				gfu, senderProto, r, null, gftu, tg);
		return ugp;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}
}
