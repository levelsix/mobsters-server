package com.lvl6.server.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.DestroyMoneyTreeStructureRequestEvent;
import com.lvl6.events.response.DestroyMoneyTreeStructureResponseEvent;
import com.lvl6.proto.EventStructureProto.DestroyMoneyTreeStructureRequestProto;
import com.lvl6.proto.EventStructureProto.DestroyMoneyTreeStructureResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.DestroyMoneyTreeStructureAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component

public class DestroyMoneyTreeStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(DestroyMoneyTreeStructureController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2;

	@Autowired
	protected StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils;

	public DestroyMoneyTreeStructureController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new DestroyMoneyTreeStructureRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_DESTROY_MONEY_TREE_STRUCTURE_EVENT;
	}

	/*
	 * db stuff done before sending event to eventwriter/client because the
	 * client's not waiting on it immediately anyways
	 */
	// @SuppressWarnings("deprecation")
	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		DestroyMoneyTreeStructureRequestProto reqProto = ((DestroyMoneyTreeStructureRequestEvent) event)
				.getDestroyMoneyTreeStructureRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userStructIdList = reqProto.getUserStructUuidList();

		DestroyMoneyTreeStructureResponseProto.Builder resBuilder = DestroyMoneyTreeStructureResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

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
			DestroyMoneyTreeStructureResponseEvent resEvent = new DestroyMoneyTreeStructureResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			// Lock this player's ID
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			writeChangesToDb(userId, resBuilder, userStructIdList);

			if (!resBuilder.hasStatus()) {
				resBuilder
						.setStatus(ResponseStatus.FAIL_OTHER);
			}

			DestroyMoneyTreeStructureResponseProto resProto = resBuilder
					.build();

			DestroyMoneyTreeStructureResponseEvent resEvent = new DestroyMoneyTreeStructureResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in DestroyMoneyTreeStructureController processEvent",
					e);
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

			DestroyMoneyTreeStructureResponseEvent resEvent = new DestroyMoneyTreeStructureResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			//don't let the client hang
		} finally {
			if (gotLock) {
				// Unlock this player
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private void writeChangesToDb(String userId,
			DestroyMoneyTreeStructureResponseProto.Builder resBuilder,
			List<String> userStructIdsList) {
		try {
			Date now = new Date();
			DestroyMoneyTreeStructureAction dmtsa = new DestroyMoneyTreeStructureAction(
					userId, userStructIdsList, now,
					structureForUserRetrieveUtils2, structureMoneyTreeRetrieveUtils,
					deleteUtil);

			dmtsa.execute(resBuilder);

			if (resBuilder.getStatus().equals(
					ResponseStatus.SUCCESS)) {
				log.info("successful money tree destroy from user {}", userId);
			}
		} catch (Exception e) {
			log.error("problem with destroying user's money tree", e);
		}
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

}
