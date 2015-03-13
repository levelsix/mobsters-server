package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CustomizePvpBoardObstacleRequestEvent;
import com.lvl6.events.response.CustomizePvpBoardObstacleResponseEvent;
import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleRequestProto;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleResponseProto;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleResponseProto.CustomizePvpBoardObstacleStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.UserPvpBoardObstacleProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.PvpBoardObstacleForUserRetrieveUtil;
import com.lvl6.server.controller.actionobjects.CustomizePvpBoardObstacleAction;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class CustomizePvpBoardObstacleController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public CustomizePvpBoardObstacleController() {
		numAllocatedThreads = 4;
	}

	@Autowired
	protected PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new CustomizePvpBoardObstacleRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_CUSTOMIZE_PVP_BOARD_OBSTACLE_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		CustomizePvpBoardObstacleRequestProto reqProto = ((CustomizePvpBoardObstacleRequestEvent) event)
				.getCustomizePvpBoardObstacleRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<UserPvpBoardObstacleProto> nuOrUpdatedObstacles = reqProto
				.getNuOrUpdatedObstaclesList();
		List<Integer> removeUpboIds = reqProto.getRemoveUpboIdsList();

		CustomizePvpBoardObstacleResponseProto.Builder resBuilder = CustomizePvpBoardObstacleResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(CustomizePvpBoardObstacleStatus.FAIL_OTHER);

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
			resBuilder.setStatus(CustomizePvpBoardObstacleStatus.FAIL_OTHER);
			CustomizePvpBoardObstacleResponseEvent resEvent = new CustomizePvpBoardObstacleResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setCustomizePvpBoardObstacleResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);
			return;
		}

		//		server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			Collection<PvpBoardObstacleForUser> newOrUpdatedPbofus = javafyUserPvpBoardObstacleProto(nuOrUpdatedObstacles);
			CustomizePvpBoardObstacleAction rsga = new CustomizePvpBoardObstacleAction(
					userId, newOrUpdatedPbofus, removeUpboIds,
					pvpBoardObstacleForUserRetrieveUtil, insertUtil, deleteUtil);

			rsga.execute(resBuilder);

			CustomizePvpBoardObstacleResponseEvent resEvent = new CustomizePvpBoardObstacleResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setCustomizePvpBoardObstacleResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in CustomizePvpBoardObstacleController processEvent",
					e);
			try {
				resBuilder
						.setStatus(CustomizePvpBoardObstacleStatus.FAIL_OTHER);
				CustomizePvpBoardObstacleResponseEvent resEvent = new CustomizePvpBoardObstacleResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setCustomizePvpBoardObstacleResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in CustomizePvpBoardObstacleController processEvent",
						e);
			}

		} finally {
			//			server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName()); 
		}
	}

	private Collection<PvpBoardObstacleForUser> javafyUserPvpBoardObstacleProto(
			List<UserPvpBoardObstacleProto> protos) {
		List<PvpBoardObstacleForUser> pbofus = new ArrayList<PvpBoardObstacleForUser>();
		for (UserPvpBoardObstacleProto upbop : protos) {
			PvpBoardObstacleForUser newPbofu = new PvpBoardObstacleForUser();
			newPbofu.setId(upbop.getUserPvpBoardObstacleId());
			newPbofu.setUserId(upbop.getUserUuid());
			newPbofu.setObstacleId(upbop.getObstacleId());
			newPbofu.setPosX(upbop.getPosX());
			newPbofu.setPosY(upbop.getPosY());

			pbofus.add(newPbofu);
		}

		return pbofus;
	}

	public PvpBoardObstacleForUserRetrieveUtil getPvpBoardObstacleForUserRetrieveUtil() {
		return pvpBoardObstacleForUserRetrieveUtil;
	}

	public void setPvpBoardObstacleForUserRetrieveUtil(
			PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil) {
		this.pvpBoardObstacleForUserRetrieveUtil = pvpBoardObstacleForUserRetrieveUtil;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

}
