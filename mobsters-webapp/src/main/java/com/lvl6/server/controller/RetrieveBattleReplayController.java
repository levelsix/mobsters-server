package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveBattleReplayRequestEvent;
import com.lvl6.events.response.RetrieveBattleReplayResponseEvent;
import com.lvl6.info.BattleReplayForUser;
import com.lvl6.proto.EventPvpProto.RetrieveBattleReplayRequestProto;
import com.lvl6.proto.EventPvpProto.RetrieveBattleReplayResponseProto;
import com.lvl6.proto.EventPvpProto.RetrieveBattleReplayResponseProto.RetrieveBattleReplayStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.BattleReplayForUserRetrieveUtil;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.RetrieveBattleReplayAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component
@DependsOn("gameServer")
public class RetrieveBattleReplayController extends EventController {


	private static final Logger log = LoggerFactory.getLogger(RetrieveBattleReplayController.class);
	public RetrieveBattleReplayController() {
	}

	@Autowired
	BattleReplayForUserRetrieveUtil battleReplayForUserRetrieveUtil;

	@Autowired
	CreateInfoProtoUtils createInfoProtoUtil;

	@Autowired
	protected Locker locker;

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveBattleReplayRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_BATTLE_REPLAY_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses) {
		RetrieveBattleReplayRequestProto reqProto = ((RetrieveBattleReplayRequestEvent) event)
				.getRetrieveBattleReplayRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String replayId = reqProto.getReplayId();

		RetrieveBattleReplayResponseProto.Builder resBuilder = RetrieveBattleReplayResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(RetrieveBattleReplayStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			log.info("invalid UUIDS.");
			resBuilder.setStatus(RetrieveBattleReplayStatus.FAIL_OTHER);
			RetrieveBattleReplayResponseEvent resEvent = new RetrieveBattleReplayResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(senderProto.getUserUuid(), this.getClass()
				.getSimpleName());
		try {
			//
			RetrieveBattleReplayAction rsga = new RetrieveBattleReplayAction(userId, replayId,
					battleReplayForUserRetrieveUtil);

			rsga.execute(resBuilder);

			if (RetrieveBattleReplayStatus.SUCCESS.equals(resBuilder.getStatus()))
			{
				BattleReplayForUser brfu = rsga.getBrfu();

				resBuilder.setBrp(createInfoProtoUtil.createBattleReplayProto(brfu));
			}

			RetrieveBattleReplayResponseProto resProto = resBuilder.build();
			RetrieveBattleReplayResponseEvent resEvent = new RetrieveBattleReplayResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in RetrieveBattleReplayController processEvent", e);
			try {
				resBuilder.setStatus(RetrieveBattleReplayStatus.FAIL_OTHER);
				RetrieveBattleReplayResponseEvent resEvent = new RetrieveBattleReplayResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrieveBattleReplayController processEvent",
						e);
			}

		} finally {
			locker.unlockPlayer(senderProto.getUserUuid(), this.getClass()
					.getSimpleName());
		}
	}

	public BattleReplayForUserRetrieveUtil getBattleReplayForUserRetrieveUtil() {
		return battleReplayForUserRetrieveUtil;
	}

	public void setBattleReplayForUserRetrieveUtil(
			BattleReplayForUserRetrieveUtil battleReplayForUserRetrieveUtil) {
		this.battleReplayForUserRetrieveUtil = battleReplayForUserRetrieveUtil;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtil() {
		return createInfoProtoUtil;
	}

	public void setCreateInfoProtoUtil(CreateInfoProtoUtils createInfoProtoUtil) {
		this.createInfoProtoUtil = createInfoProtoUtil;
	}

}
