package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.TradeItemForSpeedUpsRequestEvent;
import com.lvl6.events.response.TradeItemForSpeedUpsResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsRequestProto;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.TradeItemForSpeedUpsStatus;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.ItemsProto.UserItemUsageProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.server.controller.actionobjects.TradeItemForResourcesAction;
import com.lvl6.server.controller.actionobjects.TradeItemForSpeedUpsAction;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.server.controller.utils.ItemUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class TradeItemForSpeedUpsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public TradeItemForSpeedUpsController() {
		numAllocatedThreads = 1;
	}

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected HistoryUtils historyUtils;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new TradeItemForSpeedUpsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_TRADE_ITEM_FOR_SPEED_UPS_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		TradeItemForSpeedUpsRequestProto reqProto = ((TradeItemForSpeedUpsRequestEvent) event)
				.getTradeItemForSpeedUpsRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<UserItemUsageProto> itemsUsedProtos = reqProto.getItemsUsedList();
		List<UserItemProto> nuUserItemsProtos = reqProto.getNuUserItemsList();
		int gemsSpent = reqProto.getGemsSpent();

		TradeItemForSpeedUpsResponseProto.Builder resBuilder = TradeItemForSpeedUpsResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(TradeItemForSpeedUpsStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(TradeItemForSpeedUpsStatus.FAIL_OTHER);
			TradeItemForSpeedUpsResponseEvent resEvent = new TradeItemForSpeedUpsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setTradeItemForSpeedUpsResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(senderProto.getUserUuid(), this.getClass()
				.getSimpleName());
		//TODO: Logic similar to PurchaseSpeedUpsPack, see what else can be optimized/shared
		try {
			List<ItemForUserUsage> itemsUsed = null;
			List<ItemForUser> nuUserItems = null;

			if (null != itemsUsedProtos && !itemsUsedProtos.isEmpty()) {
				itemsUsed = ItemUtil.javafyUserItemUsageProto(itemsUsedProtos);
			}
			if (null != nuUserItemsProtos && !nuUserItemsProtos.isEmpty()) {
				nuUserItems = ItemUtil.javafyUserItemProto(nuUserItemsProtos);
			}

			TradeItemForSpeedUpsAction tifsua = new TradeItemForSpeedUpsAction(
					userId, itemsUsed, nuUserItems, itemForUserRetrieveUtil,
					InsertUtils.get(), UpdateUtils.get(), gemsSpent, miscMethods,
					historyUtils);

			tifsua.execute(resBuilder);

			if (resBuilder.getStatus().equals(
					TradeItemForSpeedUpsStatus.SUCCESS)) {
				List<ItemForUserUsage> itemsUsedWithIds = tifsua
						.getItemForUserUsages();
				if(!itemsUsedWithIds.isEmpty()) {
					List<UserItemUsageProto> uiupList = createInfoProtoUtils
							.createUserItemUsageProto(itemsUsedWithIds);
					resBuilder.addAllItemsUsed(uiupList);
				}
			}
			
			if (resBuilder.getStatus().equals(
					TradeItemForSpeedUpsStatus.SUCCESS)) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								tifsua.getUserPojo(), null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);


				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);
			}

			TradeItemForSpeedUpsResponseProto resProto = resBuilder.build();
			TradeItemForSpeedUpsResponseEvent resEvent = new TradeItemForSpeedUpsResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setTradeItemForSpeedUpsResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in TradeItemForSpeedUpsController processEvent",
					e);
			try {
				resBuilder.setStatus(TradeItemForSpeedUpsStatus.FAIL_OTHER);
				TradeItemForSpeedUpsResponseEvent resEvent = new TradeItemForSpeedUpsResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setTradeItemForSpeedUpsResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in TradeItemForSpeedUpsController processEvent",
						e);
			}

		} finally {
			server.unlockPlayer(senderProto.getUserUuid(), this.getClass()
					.getSimpleName());
		}
	}
	
	
	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

}
