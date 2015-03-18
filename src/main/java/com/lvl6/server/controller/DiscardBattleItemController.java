package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.DiscardBattleItemRequestEvent;
import com.lvl6.events.response.DiscardBattleItemResponseEvent;
import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.User;
import com.lvl6.proto.BattleItemsProto.UserBattleItemProto;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemRequestProto;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemResponseProto;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemResponseProto.DiscardBattleItemStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.DiscardBattleItemAction;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class DiscardBattleItemController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;
	
	@Autowired
	protected BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	public DiscardBattleItemController() {
		numAllocatedThreads = 8;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new DiscardBattleItemRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_DISCARD_BATTLE_ITEM_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		DiscardBattleItemRequestProto reqProto = ((DiscardBattleItemRequestEvent) event)
				.getDiscardBattleItemRequestProto();
		log.info("reqProto={}", reqProto);
		//get stuff client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		//the new items added to queue, updated refers to those finished as well as 
		//priorities changing, deleted refers to those removed from queue and completed
		List<Integer> discardedBattleItemIdsList = reqProto
				.getDiscardedBattleItemIdsList();
		Map<Integer, Integer> battleItemIdsToQuantity = battleItemIdsToQuantity(discardedBattleItemIdsList);
		
		DiscardBattleItemResponseProto.Builder resBuilder = DiscardBattleItemResponseProto
				.newBuilder();
		resBuilder.setStatus(DiscardBattleItemStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String
					.format("UUID error. incorrect userId=%s, ", userId));
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(DiscardBattleItemStatus.FAIL_OTHER);
			DiscardBattleItemResponseEvent resEvent = new DiscardBattleItemResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setDiscardBattleItemResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = userRetrieveUtil.getUserById(userId);

			DiscardBattleItemAction dbia = new DiscardBattleItemAction(userId,
					user, battleItemIdsToQuantity, battleItemForUserRetrieveUtil, updateUtil);

			dbia.execute(resBuilder);

			DiscardBattleItemResponseEvent resEvent = new DiscardBattleItemResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setDiscardBattleItemResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in DiscardBattleItemController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(DiscardBattleItemStatus.FAIL_OTHER);
				DiscardBattleItemResponseEvent resEvent = new DiscardBattleItemResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setDiscardBattleItemResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in DiscardBattleItemController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private List<BattleItemForUser> getBattleItemForUserListFromProtos(
			List<UserBattleItemProto> protosList) {
		List<BattleItemForUser> battleItemForUserList = null;
		if (protosList == null || protosList.isEmpty()) {
			log.error("DiscardBattleItem request did not send any battle items for user ids");
			return battleItemForUserList;
		}

		battleItemForUserList = new ArrayList<BattleItemForUser>();

		for (UserBattleItemProto ubiProto : protosList) {
			BattleItemForUser bifu = new BattleItemForUser();
			bifu.setBattleItemId(ubiProto.getBattleItemId());
			bifu.setUserId(ubiProto.getUserUuid());
			bifu.setQuantity(ubiProto.getQuantity());
			battleItemForUserList.add(bifu);
		}

		return battleItemForUserList;
	}
	
	private Map<Integer, Integer> battleItemIdsToQuantity(List<Integer> battleItemIdsList) {
		Map<Integer, Integer> returnMap = new HashMap<Integer, Integer>();
		for(Integer id : battleItemIdsList) {
			if(returnMap.containsKey(id)) {
				returnMap.put(id, returnMap.get(id) + 1);
			}
			else {
				returnMap.put(id, 1);
			}
		}
		return returnMap;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

}
