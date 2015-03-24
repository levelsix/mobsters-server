package com.lvl6.server.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemMiniEventRewardRequestEvent;
import com.lvl6.events.response.RedeemMiniEventRewardResponseEvent;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardRequestProto;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardRequestProto.RewardTier;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardResponseProto;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardResponseProto.RedeemMiniEventRewardStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.server.controller.actionobjects.RedeemMiniEventRewardAction;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class RedeemMiniEventRewardController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public RedeemMiniEventRewardController() {
		numAllocatedThreads = 4;
	}

	@Autowired
	protected MiniEventForUserRetrieveUtil mefuRetrieveUtil;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new RedeemMiniEventRewardRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REDEEM_MINI_EVENT_REWARD_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RedeemMiniEventRewardRequestProto reqProto = ((RedeemMiniEventRewardRequestEvent) event)
				.getRedeemMiniEventRewardRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProtoWithMaxResources senderProtoMaxResources = reqProto.getSender();
		MinimumUserProto senderProto = senderProtoMaxResources.getMinUserProto();
		String userId = senderProto.getUserUuid();
		RewardTier rt = reqProto.getTierRedeemed();
		int mefplId = reqProto.getMefplId();
		Date clientTime = new Date(reqProto.getClientTime());

		RedeemMiniEventRewardResponseProto.Builder resBuilder = RedeemMiniEventRewardResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(RedeemMiniEventRewardStatus.FAIL_OTHER);

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
			resBuilder.setStatus(RedeemMiniEventRewardStatus.FAIL_OTHER);
			RedeemMiniEventRewardResponseEvent resEvent = new RedeemMiniEventRewardResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRedeemMiniEventRewardResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(userId, this.getClass().getSimpleName());
		try {

			RedeemMiniEventRewardAction rmea = new RedeemMiniEventRewardAction(
					userId, mefplId, rt, clientTime, mefuRetrieveUtil,
					itemForUserRetrieveUtil, insertUtil, updateUtil);

			rmea.execute(resBuilder);

			if (resBuilder.getStatus().equals(RedeemMiniEventRewardStatus.SUCCESS)) {
				//TODO: protofy the rewards
			}

			RedeemMiniEventRewardResponseProto resProto = resBuilder.build();
			RedeemMiniEventRewardResponseEvent resEvent = new RedeemMiniEventRewardResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRedeemMiniEventRewardResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RedeemMiniEventRewardController processEvent",
					e);
			try {
				resBuilder.setStatus(RedeemMiniEventRewardStatus.FAIL_OTHER);
				RedeemMiniEventRewardResponseEvent resEvent = new RedeemMiniEventRewardResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRedeemMiniEventRewardResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RedeemMiniEventRewardController processEvent",
						e);
			}

		} finally {
			server.unlockPlayer(userId, this.getClass().getSimpleName());
		}
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

}
