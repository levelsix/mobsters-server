package com.lvl6.server.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RemoveUserItemUsedRequestEvent;
import com.lvl6.events.response.RemoveUserItemUsedResponseEvent;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedRequestProto;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto.RemoveUserItemUsedStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.server.controller.actionobjects.RemoveUserItemUsedAction;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class RemoveUserItemUsedController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public RemoveUserItemUsedController() {
		numAllocatedThreads = 1;
	}

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
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
		RemoveUserItemUsedRequestProto reqProto = ((RemoveUserItemUsedRequestEvent) event)
				.getRemoveUserItemUsedRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userItemUsedIdList = reqProto.getUserItemUsedUuidList();

		RemoveUserItemUsedResponseProto.Builder resBuilder = RemoveUserItemUsedResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(RemoveUserItemUsedStatus.FAIL_OTHER);

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
			resBuilder.setStatus(RemoveUserItemUsedStatus.FAIL_OTHER);
			RemoveUserItemUsedResponseEvent resEvent = new RemoveUserItemUsedResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRemoveUserItemUsedResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(senderProto.getUserUuid(), this.getClass()
				.getSimpleName());
		try {

			RemoveUserItemUsedAction tifsua = new RemoveUserItemUsedAction(
					userId, userItemUsedIdList, DeleteUtils.get());

			tifsua.execute(resBuilder);

			RemoveUserItemUsedResponseProto resProto = resBuilder.build();
			RemoveUserItemUsedResponseEvent resEvent = new RemoveUserItemUsedResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRemoveUserItemUsedResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RemoveUserItemUsedController processEvent",
					e);
			try {
				resBuilder.setStatus(RemoveUserItemUsedStatus.FAIL_OTHER);
				RemoveUserItemUsedResponseEvent resEvent = new RemoveUserItemUsedResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRemoveUserItemUsedResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RemoveUserItemUsedController processEvent",
						e);
			}

		} finally {
			server.unlockPlayer(senderProto.getUserUuid(), this.getClass()
					.getSimpleName());
		}
	}

}
