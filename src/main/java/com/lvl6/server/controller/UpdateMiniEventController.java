package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateMiniEventRequestEvent;
import com.lvl6.events.response.UpdateMiniEventResponseEvent;
import com.lvl6.info.MiniEventForUser;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventRequestProto;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto.UpdateMiniEventStatus;
import com.lvl6.proto.MiniEventProtos.UserMiniEventProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.controller.actionobjects.UpdateMiniEventAction;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class UpdateMiniEventController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public UpdateMiniEventController() {
		numAllocatedThreads = 4;
	}

	@Autowired
	protected InsertUtil insertUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new UpdateMiniEventRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_UPDATE_MINI_EVENT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		UpdateMiniEventRequestProto reqProto = ((UpdateMiniEventRequestEvent) event)
				.getUpdateMiniEventRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		UserMiniEventProto umep = reqProto.getUpdatedUserMiniEvent();

		UpdateMiniEventResponseProto.Builder resBuilder = UpdateMiniEventResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(UpdateMiniEventStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);
			UUID.fromString(umep.getUserUuid());

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, umep=%s",
					userId, umep), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(UpdateMiniEventStatus.FAIL_OTHER);
			UpdateMiniEventResponseEvent resEvent = new UpdateMiniEventResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setUpdateMiniEventResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(userId, this.getClass().getSimpleName());
		try {
			MiniEventForUser mefu = javafyUserMiniEventProto(umep);

			UpdateMiniEventAction rmea = new UpdateMiniEventAction(
					userId, mefu, insertUtil);

			rmea.execute(resBuilder);

			if (resBuilder.getStatus().equals(UpdateMiniEventStatus.SUCCESS)) {
				resBuilder.setUpdatedUserMiniEvent(umep);
			}

			UpdateMiniEventResponseProto resProto = resBuilder.build();
			UpdateMiniEventResponseEvent resEvent = new UpdateMiniEventResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setUpdateMiniEventResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in UpdateMiniEventController processEvent",
					e);
			try {
				resBuilder.setStatus(UpdateMiniEventStatus.FAIL_OTHER);
				UpdateMiniEventResponseEvent resEvent = new UpdateMiniEventResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setUpdateMiniEventResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in UpdateMiniEventController processEvent",
						e);
			}

		} finally {
			server.unlockPlayer(userId, this.getClass().getSimpleName());
		}
	}

	private MiniEventForUser javafyUserMiniEventProto(UserMiniEventProto umep) {
		MiniEventForUser mefu = new MiniEventForUser();
		mefu.setUserId(umep.getUserUuid());
		mefu.setMiniEventId(umep.getMiniEventId());
		mefu.setUserLvl(umep.getUserLvl());
		mefu.setPtsEarned(umep.getPtsEarned());
		mefu.setTierOneRedeemed(umep.getTierOneRedeemed());
		mefu.setTierTwoRedeemed(umep.getTierTwoRedeemed());
		mefu.setTierThreeRedeemed(umep.getTierThreeRedeemed());

		return mefu;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

}
