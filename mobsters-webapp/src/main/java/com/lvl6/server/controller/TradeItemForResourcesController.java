package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.TradeItemForResourcesRequestEvent;
import com.lvl6.events.response.TradeItemForResourcesResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesRequestProto;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.TradeItemForResourcesAction;
import com.lvl6.server.controller.utils.ItemUtil;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.TimeUtils;

@Component

public class TradeItemForResourcesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(TradeItemForResourcesController.class);

	public TradeItemForResourcesController() {

	}

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Override
	public RequestEvent createRequestEvent() {
		return new TradeItemForResourcesRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_TRADE_ITEM_FOR_RESOURCES_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		TradeItemForResourcesRequestProto reqProto = ((TradeItemForResourcesRequestEvent) event)
				.getTradeItemForResourcesRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProtoWithMaxResources mupMaxResources = reqProto.getSender();
		int maxCash = mupMaxResources.getMaxCash();
		int maxOil = mupMaxResources.getMaxOil();
		MinimumUserProto senderProto = mupMaxResources.getMinUserProto();
		String userId = senderProto.getUserUuid();
		List<Integer> itemIdsUsed = reqProto.getItemIdsUsedList();
		List<UserItemProto> nuUserItemsProtos = reqProto.getNuUserItemsList();
		int gemsSpent = reqProto.getGemsSpent();

		Timestamp date = new Timestamp(reqProto.getClientTime());

		TradeItemForResourcesResponseProto.Builder resBuilder = TradeItemForResourcesResponseProto
				.newBuilder();
		resBuilder.setSender(mupMaxResources);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			TradeItemForResourcesResponseEvent resEvent = new TradeItemForResourcesResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) >
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			TradeItemForResourcesResponseEvent resEvent =
					new TradeItemForResourcesResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

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
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			TradeItemForResourcesResponseEvent resEvent = new TradeItemForResourcesResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());
			List<ItemForUser> nuUserItems = null;
			if (null != nuUserItemsProtos && !nuUserItemsProtos.isEmpty()) {
				nuUserItems = ItemUtil.javafyUserItemProto(nuUserItemsProtos);
			}

//			TradeItemForResourcesAction tifsua = new TradeItemForResourcesAction(
//					userId, itemIdsUsed, nuUserItems, maxCash, maxOil,
//					itemForUserRetrieveUtil, itemRetrieveUtils, userRetrieveUtil,
//					UpdateUtils.get(), miscMethods, gemsSpent, historyUtils);
			TradeItemForResourcesAction tifra = AppContext.getBean(TradeItemForResourcesAction.class);
			tifra.wire(userId, itemIdsUsed, nuUserItems, maxCash, maxOil, gemsSpent);
			tifra.execute(resBuilder);

			TradeItemForResourcesResponseProto resProto = resBuilder.build();
			TradeItemForResourcesResponseEvent resEvent = new TradeItemForResourcesResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder
					.getStatus())) {
				User user = tifra.getUser();
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToCurrencyHistory(userId, date, tifra);
			}

		} catch (Exception e) {
			log.error(
					"exception in TradeItemForResourcesController processEvent",
					e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				TradeItemForResourcesResponseEvent resEvent = new TradeItemForResourcesResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in TradeItemForResourcesController processEvent",
						e);
			}

		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private void writeToCurrencyHistory(String userId, Timestamp date,
			TradeItemForResourcesAction tifra) {
		miscMethods.writeToUserCurrencyOneUser(userId, date,
				tifra.getCurrencyDeltas(), tifra.getPreviousCurrencies(),
				tifra.getCurrentCurrencies(), tifra.getReasons(),
				tifra.getDetails());
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
