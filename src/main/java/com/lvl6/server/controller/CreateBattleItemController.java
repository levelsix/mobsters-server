package com.lvl6.server.controller;

import java.sql.Timestamp;
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
import com.lvl6.events.request.CreateBattleItemRequestEvent;
import com.lvl6.events.response.CreateBattleItemResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemRequestProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.CreateBattleItemStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.CreateBattleItemAction;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component @DependsOn("gameServer") public class CreateBattleItemController extends EventController{

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected StructureForUserRetrieveUtils2 userStructRetrieveUtil;
	
	@Autowired
	protected UpdateUtil updateUtil;

	public CreateBattleItemController() {
		numAllocatedThreads = 8;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CreateBattleItemRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_CREATE_BATTLE_ITEM_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		CreateBattleItemRequestProto reqProto = ((CreateBattleItemRequestEvent)event)
				.getCreateBattleItemRequestProto();
		log.info("reqProto={}", reqProto);
		//get stuff client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		//the new items added to queue, updated refers to those finished as well as 
		//priorities changing, deleted refers to those removed from queue and completed
		List<BattleItemQueueForUserProto> deletedBattleItemQueueList = reqProto.getUmhDeleteList();
		List<BattleItemQueueForUserProto> removedBattleItemQueueList = reqProto.getUmhRemovedList();
		List<BattleItemQueueForUserProto> updatedBattleItemQueueList = reqProto.getUmhUpdateList();
		List<BattleItemQueueForUserProto> newBattleItemQueueList = reqProto.getUmhNewList();

		List<BattleItemQueueForUser> deletedMap = 
				getIdsToBattleItemQueueForUserFromProto(deletedBattleItemQueueList, userId);
		List<BattleItemQueueForUser> removedMap = 
				getIdsToBattleItemQueueForUserFromProto(removedBattleItemQueueList, userId);
		List<BattleItemQueueForUser> updatedMap = 
				getIdsToBattleItemQueueForUserFromProto(updatedBattleItemQueueList, userId);
		List<BattleItemQueueForUser> newMap = 
				getIdsToBattleItemQueueForUserFromProto(newBattleItemQueueList, userId);
		
		int cashChange = reqProto.getCashChange();
		int oilChange = reqProto.getOilChange();
		int gemCostForCreating = reqProto.getGemCostForCreating();
		boolean isSpeedup = reqProto.getIsSpeedup();
		int gemsForSpeedUp = 0;
		if(isSpeedup) {
			gemsForSpeedUp = reqProto.getGemsForSpeedup();
		}

		CreateBattleItemResponseProto.Builder resBuilder =
				CreateBattleItemResponseProto.newBuilder();
		resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, ",
					userId));
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
			CreateBattleItemResponseEvent resEvent = new CreateBattleItemResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setCreateBattleItemResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}
		
		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			
			CreateBattleItemAction cbia =
					new CreateBattleItemAction(
							userId, maxCash, maxOil, duplicates,
							userStructIdsToStructRetrievals,
							userRetrieveUtil, userStructRetrieveUtil, updateUtil);
			
			cbia.execute(resBuilder);

			CreateBattleItemResponseEvent resEvent = new 
					CreateBattleItemResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setCreateBattleItemResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (CreateBattleItemStatus.SUCCESS.equals(resBuilder.getStatus())) {
				User user = rcfnsa.getUser();
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToCurrencyHistory(userId, curTime, rcfnsa);
			}
		} catch (Exception e) {
			log.error("exception in CreateBattleItemController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
				CreateBattleItemResponseEvent resEvent = new CreateBattleItemResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setCreateBattleItemResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in CreateBattleItemController processEvent", e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());      
		}
	}

	private List<BattleItemQueueForUser> getIdsToBattleItemQueueForUserFromProto
	(List<BattleItemQueueForUserProto> protosList, String userId) {
		List<BattleItemQueueForUser> idsToBattleItemQueueForUser = null;
		if(protosList == null || protosList.isEmpty()) {
			log.error("CreateBattleItem request did not send any battle item queue for user ids");
			return idsToBattleItemQueueForUser;
		}
		
		idsToBattleItemQueueForUser = new ArrayList<BattleItemQueueForUser>();
		
		for(BattleItemQueueForUserProto biqfuProto : protosList) {
			BattleItemQueueForUser biqfu = new BattleItemQueueForUser();
			biqfu.setPriority(biqfuProto.getPriority());
			
			String protoUserId = biqfuProto.getUserUuid();
			if(protoUserId.equals(userId)) {
				biqfu.setUserId(protoUserId);
			}
			else {
				log.error("CreateBattleItem request sends conflicting battle item queue "
						+ "for user's user id with user id sent");
				return new ArrayList<BattleItemQueueForUser>();
			}
			biqfu.setBattleItemId(biqfuProto.getBattleItemId());
			biqfu.setExpectedStartTime(biqfu.getExpectedStartTime());
			biqfu.setPriority(biqfuProto.getPriority());
			idsToBattleItemQueueForUser.add(biqfu);
		}
		
		return idsToBattleItemQueueForUser;
		
	}
	
	
	private void writeToCurrencyHistory(String userId, Timestamp date,
			CreateBattleItemAction rcfnsa)
		{
			MiscMethods.writeToUserCurrencyOneUser(userId, date,
				rcfnsa.getCurrencyDeltas(), rcfnsa.getPreviousCurrencies(),
	    		rcfnsa.getCurrentCurrencies(), rcfnsa.getReasons(),
	    		rcfnsa.getDetails());
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

	public StructureForUserRetrieveUtils2 getUserStructRetrieveUtil() {
		return userStructRetrieveUtil;
	}

	public void setUserStructRetrieveUtil(
			StructureForUserRetrieveUtils2 userStructRetrieveUtil) {
		this.userStructRetrieveUtil = userStructRetrieveUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

}
