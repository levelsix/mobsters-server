package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.MoveOrRotateNormStructureRequestEvent;
import com.lvl6.events.response.MoveOrRotateNormStructureResponseEvent;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.StructureForUser;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureRequestProto.MoveOrRotateNormStructType;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureResponseProto.MoveOrRotateNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.StructOrientation;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class MoveOrRotateNormStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected StructureForUserRetrieveUtils2 structureForUserRetrieveUtils;

	public MoveOrRotateNormStructureController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new MoveOrRotateNormStructureRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		MoveOrRotateNormStructureRequestProto reqProto = ((MoveOrRotateNormStructureRequestEvent) event)
				.getMoveOrRotateNormStructureRequestProto();

		//get stuff client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String userStructId = reqProto.getUserStructUuid();
		MoveOrRotateNormStructType type = reqProto.getType();

		CoordinatePair newCoords = null;
		StructOrientation orientation = null;
		if (type == MoveOrRotateNormStructType.MOVE) {
			newCoords = new CoordinatePair(reqProto.getCurStructCoordinates()
					.getX(), reqProto.getCurStructCoordinates().getY());
		}

		MoveOrRotateNormStructureResponseProto.Builder resBuilder = MoveOrRotateNormStructureResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		UUID userStructUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			userStructUuid = UUID.fromString(userStructId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userStructId=%s", userId,
					userStructId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
			MoveOrRotateNormStructureResponseEvent resEvent = new MoveOrRotateNormStructureResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//only locking so you cant moveOrRotate it hella times
		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			boolean legit = true;
			resBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);

			StructureForUser userStruct = getStructureForUserRetrieveUtils()
					.getSpecificUserStruct(userStructId);
			if (userStruct == null) {
				legit = false;
				resBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
			}

			if (type == MoveOrRotateNormStructType.MOVE && newCoords == null) {
				legit = false;
				resBuilder
						.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
				log.error("asked to move, but the coordinates supplied in are null. reqProto's newStructCoordinates="
						+ reqProto.getCurStructCoordinates());
			}

			if (legit) {
				if (type == MoveOrRotateNormStructType.MOVE) {
					if (!UpdateUtils.get().updateUserStructCoord(userStructId,
							newCoords)) {
						resBuilder
								.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
						log.error("problem with updating coordinates to "
								+ newCoords + " for user struct "
								+ userStructId);
					} else {
						resBuilder
								.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
					}
				} else {
					if (!UpdateUtils.get().updateUserStructOrientation(
							userStructId, orientation)) {
						resBuilder
								.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
						log.error("problem with updating orientation to "
								+ orientation + " for user struct "
								+ userStructId);
					} else {
						resBuilder
								.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
					}
				}
			}
			MoveOrRotateNormStructureResponseEvent resEvent = new MoveOrRotateNormStructureResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in MoveOrRotateNormStructure processEvent", e);
			//don't let the client hang
			try {
				resBuilder
						.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
				MoveOrRotateNormStructureResponseEvent resEvent = new MoveOrRotateNormStructureResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in MoveOrRotateNormStructure processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public StructureForUserRetrieveUtils2 getStructureForUserRetrieveUtils() {
		return structureForUserRetrieveUtils;
	}

	public void setStructureForUserRetrieveUtils(
			StructureForUserRetrieveUtils2 structureForUserRetrieveUtils) {
		this.structureForUserRetrieveUtils = structureForUserRetrieveUtils;
	}

}
