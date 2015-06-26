package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateMiniEventRequestEvent;
import com.lvl6.events.response.UpdateMiniEventResponseEvent;
import com.lvl6.info.MiniEventGoalForUser;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventRequestProto;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.MiniEventProtos.UserMiniEventGoalProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.UpdateMiniEventAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component

public class UpdateMiniEventController extends EventController {

	private static Logger log = LoggerFactory.getLogger(UpdateMiniEventController.class);

	public UpdateMiniEventController() {
		
	}
	
	@Autowired
	protected Locker locker;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new UpdateMiniEventRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_UPDATE_MINI_EVENT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		UpdateMiniEventRequestProto reqProto = ((UpdateMiniEventRequestEvent) event)
				.getUpdateMiniEventRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<UserMiniEventGoalProto> umegpList = reqProto.getUpdatedGoalsList();

		UpdateMiniEventResponseProto.Builder resBuilder = UpdateMiniEventResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);

			for (UserMiniEventGoalProto umegp : umegpList) {
				UUID.fromString(umegp.getUserUuid());
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, umep=%s",
					userId, umegpList), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			UpdateMiniEventResponseEvent resEvent = new UpdateMiniEventResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(UUID.fromString(userId), this.getClass().getSimpleName());
		try {
			List<MiniEventGoalForUser> megfuList = javafyUserMiniEventProto(umegpList);

			UpdateMiniEventAction rmea = new UpdateMiniEventAction(
					userId, megfuList, insertUtil, miniEventGoalRetrieveUtils);

			rmea.execute(resBuilder);

			UpdateMiniEventResponseProto resProto = resBuilder.build();
			UpdateMiniEventResponseEvent resEvent = new UpdateMiniEventResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in UpdateMiniEventController processEvent",
					e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				UpdateMiniEventResponseEvent resEvent = new UpdateMiniEventResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in UpdateMiniEventController processEvent",
						e);
			}

		} finally {
			locker.unlockPlayer(UUID.fromString(userId), this.getClass().getSimpleName());
		}
	}

	private List<MiniEventGoalForUser> javafyUserMiniEventProto(
			List<UserMiniEventGoalProto> umegpList)
	{
		List<MiniEventGoalForUser> megfuList = new ArrayList<MiniEventGoalForUser>();

		for (UserMiniEventGoalProto umegp : umegpList) {

			MiniEventGoalForUser megfu = new MiniEventGoalForUser();
			megfu.setUserId(umegp.getUserUuid());
			megfu.setMiniEventGoalId(umegp.getMiniEventGoalId());
			megfu.setProgress(umegp.getProgress());

			megfuList.add(megfu);
		}

		return megfuList;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
