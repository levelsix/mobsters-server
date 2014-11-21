package com.lvl6.server.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.TradeItemForResourcesRequestEvent;
import com.lvl6.events.response.TradeItemForResourcesResponseEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesRequestProto;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto.TradeItemForResourcesStatus;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.actionobjects.TradeItemForResourcesAction;
import com.lvl6.server.controller.utils.ItemUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class TradeItemForResourcesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public TradeItemForResourcesController() {
		numAllocatedThreads = 1;
	}

	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new TradeItemForResourcesRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_TRADE_ITEM_FOR_RESOURCES_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		TradeItemForResourcesRequestProto reqProto = ((TradeItemForResourcesRequestEvent)event).getTradeItemForResourcesRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProtoWithMaxResources mupMaxResources = reqProto.getSender();
		int maxCash = mupMaxResources.getMaxCash();
		int maxOil = mupMaxResources.getMaxOil();
		MinimumUserProto senderProto = mupMaxResources.getMinUserProto();
		String userId = senderProto.getUserUuid();
		List<Integer> itemIdsUsed = reqProto.getItemIdsUsedList();
		List<UserItemProto> nuUserItemsProtos = reqProto.getNuUserItemsList();

		TradeItemForResourcesResponseProto.Builder resBuilder = TradeItemForResourcesResponseProto.newBuilder();
		resBuilder.setSender(mupMaxResources);
		resBuilder.setStatus(TradeItemForResourcesStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s",
				userId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(TradeItemForResourcesStatus.FAIL_OTHER);
			TradeItemForResourcesResponseEvent resEvent = new TradeItemForResourcesResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setTradeItemForResourcesResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			List<ItemForUser> nuUserItems = null;
			if (null != nuUserItemsProtos && !nuUserItemsProtos.isEmpty()) {
				nuUserItems = ItemUtil.javafyUserItemProto(nuUserItemsProtos);
			}

			TradeItemForResourcesAction tifsua = new TradeItemForResourcesAction(
				userId, itemIdsUsed, nuUserItems, maxCash, maxOil,
				itemForUserRetrieveUtil, userRetrieveUtil, UpdateUtils.get());

			tifsua.execute(resBuilder);

			TradeItemForResourcesResponseProto resProto = resBuilder.build();
			TradeItemForResourcesResponseEvent resEvent = new TradeItemForResourcesResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setTradeItemForResourcesResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in TradeItemForResourcesController processEvent", e);
			try {
				resBuilder.setStatus(TradeItemForResourcesStatus.FAIL_OTHER);
				TradeItemForResourcesResponseEvent resEvent = new TradeItemForResourcesResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setTradeItemForResourcesResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in TradeItemForResourcesController processEvent", e);
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
