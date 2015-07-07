package com.lvl6.server.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveMiniEventLeaderBoardRequestEvent;
import com.lvl6.events.response.RetrieveMiniEventLeaderBoardResponseEvent;
import com.lvl6.mobsters.jooq.daos.service.MiniEventLeaderBoardService;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveMiniEventLeaderBoardRequestProto;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveMiniEventLeaderBoardResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniEventTimetableRetrieveUtils;
import com.lvl6.server.controller.actionobjects.RetrieveMiniEventLeaderBoardAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component
@DependsOn("gameServer")
public class RetrieveMiniEventLeaderBoardController extends EventController {


	private static final Logger log = LoggerFactory.getLogger(RetrieveMiniEventLeaderBoardController.class);
	
	@Autowired
	private UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	private HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected MiniEventLeaderBoardService leaderBoard;
	
	@Autowired
	protected MiniEventTimetableRetrieveUtils miniEventTimetableRetrieveUtils;


	public RetrieveMiniEventLeaderBoardController() {
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveMiniEventLeaderBoardRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_MINI_EVENT_LEADER_BOARD_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses) {
		RetrieveMiniEventLeaderBoardRequestProto reqProto = ((RetrieveMiniEventLeaderBoardRequestEvent) event)
				.getRetrieveMiniEventLeaderBoardRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int miniEventTimetableId = reqProto.getMiniEventTimetableId();
		int minRank = reqProto.getMinRank();
		int maxRank = reqProto.getMaxRank();

		RetrieveMiniEventLeaderBoardResponseProto.Builder resBuilder = RetrieveMiniEventLeaderBoardResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		Set<String> userUuidsSet = new HashSet<String>();

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
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RetrieveMiniEventLeaderBoardResponseEvent resEvent = new RetrieveMiniEventLeaderBoardResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//		server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {

			RetrieveMiniEventLeaderBoardAction rslba = new RetrieveMiniEventLeaderBoardAction(userId, 
					miniEventTimetableId, minRank, maxRank, leaderBoard, userRetrieveUtil, 
					createInfoProtoUtils, miniEventTimetableRetrieveUtils);
			rslba.execute(resBuilder);

			RetrieveMiniEventLeaderBoardResponseProto resProto = resBuilder.build();
			RetrieveMiniEventLeaderBoardResponseEvent resEvent = new RetrieveMiniEventLeaderBoardResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in RetrieveMiniEventLeaderBoardController processEvent",
					e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RetrieveMiniEventLeaderBoardResponseEvent resEvent = new RetrieveMiniEventLeaderBoardResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrieveMiniEventLeaderBoardController processEvent",
						e);
			}

			//		} finally {
			//			      server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}


}
