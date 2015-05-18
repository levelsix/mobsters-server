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
import com.lvl6.events.request.RetrieveStrengthLeaderBoardRequestEvent;
import com.lvl6.events.response.RetrieveStrengthLeaderBoardResponseEvent;
import com.lvl6.leaderboards.LeaderBoardImpl;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveStrengthLeaderBoardRequestProto;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveStrengthLeaderBoardResponseProto;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveStrengthLeaderBoardResponseProto.RetrieveStrengthLeaderBoardStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.actionobjects.RetrieveStrengthLeaderBoardAction;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component
@DependsOn("gameServer")
public class RetrieveStrengthLeaderBoardController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	private UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	private HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	protected LeaderBoardImpl leaderBoard;


	public RetrieveStrengthLeaderBoardController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveStrengthLeaderBoardRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_STRENGTH_LEADER_BOARD_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RetrieveStrengthLeaderBoardRequestProto reqProto = ((RetrieveStrengthLeaderBoardRequestEvent) event)
				.getRetrieveStrengthLeaderBoardRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int minRank = reqProto.getMinRank();
		int maxRank = reqProto.getMaxRank();

		RetrieveStrengthLeaderBoardResponseProto.Builder resBuilder = RetrieveStrengthLeaderBoardResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(RetrieveStrengthLeaderBoardStatus.FAIL_OTHER);

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
			resBuilder.setStatus(RetrieveStrengthLeaderBoardStatus.FAIL_OTHER);
			RetrieveStrengthLeaderBoardResponseEvent resEvent = new RetrieveStrengthLeaderBoardResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveStrengthLeaderBoardResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		//		server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {

			RetrieveStrengthLeaderBoardAction rslba = new RetrieveStrengthLeaderBoardAction(userId, 
					minRank, maxRank, leaderBoard);
			rslba.execute(resBuilder);

			RetrieveStrengthLeaderBoardResponseProto resProto = resBuilder.build();
			RetrieveStrengthLeaderBoardResponseEvent resEvent = new RetrieveStrengthLeaderBoardResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveStrengthLeaderBoardResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error(
					"exception in RetrieveStrengthLeaderBoardController processEvent",
					e);
			try {
				resBuilder.setStatus(RetrieveStrengthLeaderBoardStatus.FAIL_OTHER);
				RetrieveStrengthLeaderBoardResponseEvent resEvent = new RetrieveStrengthLeaderBoardResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRetrieveStrengthLeaderBoardResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrieveStrengthLeaderBoardController processEvent",
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
