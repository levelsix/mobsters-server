package com.lvl6.server.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RemoveUserItemUsedRequestEvent;
import com.lvl6.events.response.RemoveUserItemUsedResponseEvent;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedRequestProto;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.RemoveUserItemUsedAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component

public class RemoveUserItemUsedController extends EventController {

	private static Logger log = LoggerFactory.getLogger(RemoveUserItemUsedController.class);

	public RemoveUserItemUsedController() {

	}


	@Autowired
	protected Locker locker;

	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new RemoveUserItemUsedRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REMOVE_USER_ITEM_USED_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RemoveUserItemUsedRequestProto reqProto = ((RemoveUserItemUsedRequestEvent) event)
				.getRemoveUserItemUsedRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userItemUsedIdList = reqProto.getUserItemUsedUuidList();

		RemoveUserItemUsedResponseProto.Builder resBuilder = RemoveUserItemUsedResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			StringUtils.convertToUUID(userItemUsedIdList);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userItemUsedIdList=%s",
					userId, userItemUsedIdList), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RemoveUserItemUsedResponseEvent resEvent = new RemoveUserItemUsedResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}



		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			RemoveUserItemUsedAction tifsua = new RemoveUserItemUsedAction(
					userId, userItemUsedIdList, DeleteUtils.get());

			tifsua.execute(resBuilder);

			RemoveUserItemUsedResponseProto resProto = resBuilder.build();
			RemoveUserItemUsedResponseEvent resEvent = new RemoveUserItemUsedResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in RemoveUserItemUsedController processEvent",
					e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RemoveUserItemUsedResponseEvent resEvent = new RemoveUserItemUsedResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RemoveUserItemUsedController processEvent",
						e);
			}

		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
