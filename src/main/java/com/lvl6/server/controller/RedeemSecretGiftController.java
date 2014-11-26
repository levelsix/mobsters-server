package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemSecretGiftRequestEvent;
import com.lvl6.events.response.RedeemSecretGiftResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventItemProto.RedeemSecretGiftRequestProto;
import com.lvl6.proto.EventItemProto.RedeemSecretGiftResponseProto;
import com.lvl6.proto.EventItemProto.RedeemSecretGiftResponseProto.RedeemSecretGiftStatus;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.actionobjects.RedeemSecretGiftAction;
import com.lvl6.server.controller.utils.ItemUtil;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class RedeemSecretGiftController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public RedeemSecretGiftController() {
		numAllocatedThreads = 1;
	}

	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new RedeemSecretGiftRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REDEEM_SECRET_GIFT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RedeemSecretGiftRequestProto reqProto = ((RedeemSecretGiftRequestEvent)event).getRedeemSecretGiftRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getMup();
		String userId = senderProto.getUserUuid();
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		List<String> itemIdsRedeemed = reqProto.getUisgUuidList();

		Timestamp date = new Timestamp(reqProto.getClientTime());
		
		RedeemSecretGiftResponseProto.Builder resBuilder = RedeemSecretGiftResponseProto.newBuilder();
		resBuilder.setMup(senderProto);
		resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			StringUtils.convertToUUID(itemIdsRedeemed);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s, itemIdsRedeemed=%s",
				userId, itemIdsRedeemed), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			log.info("invalid UUIDS.");
			resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);
			RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setRedeemSecretGiftResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
//
//			RedeemSecretGiftAction tifsua = new RedeemSecretGiftAction(
//				userId, itemIdsRedeemed, nuUserItems, maxCash, maxOil,
//				itemForUserRetrieveUtil, userRetrieveUtil, UpdateUtils.get());
//
//			tifsua.execute(resBuilder);

			RedeemSecretGiftResponseProto resProto = resBuilder.build();
			RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRedeemSecretGiftResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RedeemSecretGiftController processEvent", e);
			try {
				resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);
				RedeemSecretGiftResponseEvent resEvent = new RedeemSecretGiftResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setRedeemSecretGiftResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in RedeemSecretGiftController processEvent", e);
			}

		} finally {
			server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName()); 
		}
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil()
	{
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil( ItemForUserRetrieveUtil itemForUserRetrieveUtil )
	{
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil()
	{
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil( UserRetrieveUtils2 userRetrieveUtil )
	{
		this.userRetrieveUtil = userRetrieveUtil;
	}

}
