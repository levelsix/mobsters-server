package com.lvl6.server.controller;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateClientTaskStateRequestEvent;
import com.lvl6.events.response.UpdateClientTaskStateResponseEvent;
import com.lvl6.info.TaskForUserClientState;
import com.lvl6.proto.EventUserProto.UpdateClientTaskStateRequestProto;
import com.lvl6.proto.EventUserProto.UpdateClientTaskStateResponseProto;
import com.lvl6.proto.EventUserProto.UpdateClientTaskStateResponseProto.UpdateClientTaskStateStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class UpdateClientTaskStateController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected InsertUtil insertUtil;

	public UpdateClientTaskStateController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new UpdateClientTaskStateRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_UPDATE_CLIENT_TASK_STATE_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		UpdateClientTaskStateRequestProto reqProto = ((UpdateClientTaskStateRequestEvent) event)
				.getUpdateClientTaskStateRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		ByteString bs = reqProto.getTaskState();

		//set some values to send to the client (the response proto)
		UpdateClientTaskStateResponseProto.Builder resBuilder = UpdateClientTaskStateResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setTaskState(bs);
		resBuilder.setStatus(UpdateClientTaskStateStatus.FAIL_OTHER); //default

		UpdateClientTaskStateResponseEvent resEvent = new UpdateClientTaskStateResponseEvent(
				userId);
		resEvent.setTag(event.getTag());

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
			resBuilder.setStatus(UpdateClientTaskStateStatus.FAIL_OTHER);
			resEvent = new UpdateClientTaskStateResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setUpdateClientTaskStateResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			TaskForUserClientState tfucs = new TaskForUserClientState();
			tfucs.setUserId(userId);
			tfucs.setClientState(bs.toByteArray());

			List<TaskForUserClientState> tfucsList = Collections
					.singletonList(tfucs);
			int numUpdated = insertUtil
					.insertIntoUpdateClientTaskState(tfucsList);
			log.info("numInserted TaskForUserClientState: {}", numUpdated);

			resBuilder.setStatus(UpdateClientTaskStateStatus.SUCCESS);
			resEvent.setUpdateClientTaskStateResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in UpdateClientTaskStateController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(UpdateClientTaskStateStatus.FAIL_OTHER);
				resEvent = new UpdateClientTaskStateResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setUpdateClientTaskStateResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in UpdateClientTaskStateController processEvent",
						e);
			}
		} finally {
			//			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

}
