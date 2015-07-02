package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemVoucherForBattleItemRequestEvent;
import com.lvl6.events.response.RedeemVoucherForBattleItemResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.BattleItemForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserBattleItemHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.BattleItemForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleItemsProto.UserBattleItemProto;
import com.lvl6.proto.EventItemProto.RedeemVoucherForBattleItemRequestProto;
import com.lvl6.proto.EventItemProto.RedeemVoucherForBattleItemResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.RedeemVoucherForBattleItemAction;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component

public class RedeemVoucherForBattleItemController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemVoucherForBattleItemController.class);

	public RedeemVoucherForBattleItemController() {

	}

	@Autowired
	protected Locker locker;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils;

	@Autowired
	protected HistoryUtils historyUtils;

	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected UserDao userDao;
	
	@Autowired
	protected BattleItemForUserDao bifuDao;
	
	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected UserBattleItemHistoryDao ubihDao;

	@Override
	public RequestEvent createRequestEvent() {
		return new RedeemVoucherForBattleItemRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REDEEM_VOUCHER_FOR_BATTLE_ITEM_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RedeemVoucherForBattleItemRequestProto reqProto = ((RedeemVoucherForBattleItemRequestEvent) event)
				.getRedeemVoucherForBattleItemRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto mup = reqProto.getSender();
		
		String userId = mup.getUserUuid();
		List<Integer> itemIdsUsed = reqProto.getItemIdsUsedList();
		List<UserBattleItemProto> nuUserBattleItemsProtos = reqProto.getNuUserBattleItemsList();

		long clientTimeLong = reqProto.getClientTime();
		Date clientTime = new Date(clientTimeLong);

		RedeemVoucherForBattleItemResponseProto.Builder resBuilder = RedeemVoucherForBattleItemResponseProto
				.newBuilder();
		resBuilder.setSender(mup);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		if(clientTimeLong == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			RedeemVoucherForBattleItemResponseEvent resEvent = new RedeemVoucherForBattleItemResponseEvent(userId);
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(clientTime, new Date()) >
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", userId);
			RedeemVoucherForBattleItemResponseEvent resEvent =
					new RedeemVoucherForBattleItemResponseEvent(userId);
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
			RedeemVoucherForBattleItemResponseEvent resEvent = new RedeemVoucherForBattleItemResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());
			List<BattleItemForUserPojo> updatedBattleItemForUser = null;
			if (null != nuUserBattleItemsProtos && !nuUserBattleItemsProtos.isEmpty()) {
				updatedBattleItemForUser = convertBattleItemProtosToBattleItem(nuUserBattleItemsProtos);
			}

			RedeemVoucherForBattleItemAction rvfbia = new RedeemVoucherForBattleItemAction(userId, itemIdsUsed, 
					updatedBattleItemForUser, itemRetrieveUtils, userDao, bifuDao, userRetrieveUtil, deleteUtil,
					historyUtils, ubihDao, clientTime);

			rvfbia.execute(resBuilder);

			RedeemVoucherForBattleItemResponseProto resProto = resBuilder.build();
			RedeemVoucherForBattleItemResponseEvent resEvent = new RedeemVoucherForBattleItemResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder
					.getStatus())) {
				UserPojo user = rvfbia.getUser();
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}
			
		} catch (Exception e) {
			log.error(
					"exception in RedeemVoucherForBattleItemController processEvent",
					e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RedeemVoucherForBattleItemResponseEvent resEvent = new RedeemVoucherForBattleItemResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RedeemVoucherForBattleItemController processEvent",
						e);
			}

		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}
	
	public List<BattleItemForUserPojo> convertBattleItemProtosToBattleItem(List<UserBattleItemProto> listOfBattleItemProtos) {
		List<BattleItemForUserPojo> returnList = new ArrayList<BattleItemForUserPojo>();
		for(UserBattleItemProto ubip : listOfBattleItemProtos) {
			BattleItemForUserPojo bifup = new BattleItemForUserPojo();
			bifup.setId(ubip.getUserBattleItemId());
			bifup.setUserId(ubip.getUserUuid());
			bifup.setBattleItemId(ubip.getBattleItemId());
			bifup.setQuantity(ubip.getQuantity());
			returnList.add(bifup);
		}
		return returnList;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
