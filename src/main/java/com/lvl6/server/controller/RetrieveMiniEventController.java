package com.lvl6.server.controller;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveMiniEventRequestEvent;
import com.lvl6.events.response.RetrieveMiniEventResponseEvent;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventRequestProto;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.RetrieveMiniEventStatus;
import com.lvl6.proto.MiniEventProtos.UserMiniEventProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventGoalForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventLeaderboardRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.server.controller.actionobjects.RetrieveMiniEventAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@DependsOn("gameServer")
public class RetrieveMiniEventController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public RetrieveMiniEventController() {
		numAllocatedThreads = 4;
	}

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected AchievementForUserRetrieveUtil achievementForUserRetrieveUtil;
	
	@Autowired
	protected MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil;

	@Autowired
	protected MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;
	
	@Autowired
	protected MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils;
	
	@Autowired
	protected MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils;
	
	@Autowired
	protected MiniEventRetrieveUtils miniEventRetrieveUtils;
	
	@Autowired
	protected MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils;
	
	@Autowired
	protected MiniEventLeaderboardRewardRetrieveUtils miniEventLeaderboardRewardRetrieveUtils;
	
	@Autowired
	protected TimeUtils timeUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveMiniEventRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_MINI_EVENT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
		//should really be renamed to something like UpdateToCurrentMiniEvent or something
		RetrieveMiniEventRequestProto reqProto = ((RetrieveMiniEventRequestEvent) event)
				.getRetrieveMiniEventRequestProto();

		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Date now = new Date();

		RetrieveMiniEventResponseProto.Builder resBuilder = RetrieveMiniEventResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(RetrieveMiniEventStatus.FAIL_OTHER);

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s",
					userId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(RetrieveMiniEventStatus.FAIL_OTHER);
			RetrieveMiniEventResponseEvent resEvent = new RetrieveMiniEventResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveMiniEventResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(userId, this.getClass().getSimpleName());
		try {

			RetrieveMiniEventAction rmea = new RetrieveMiniEventAction(
					userId, now, true, userRetrieveUtil,
					achievementForUserRetrieveUtil,
					miniEventForUserRetrieveUtil, miniEventGoalForUserRetrieveUtil,
					insertUtil, deleteUtil, miniEventGoalRetrieveUtils,
					miniEventForPlayerLvlRetrieveUtils, miniEventRetrieveUtils,
					miniEventTierRewardRetrieveUtils,
					miniEventLeaderboardRewardRetrieveUtils, timeUtil);

			rmea.execute(resBuilder);

			if (resBuilder.getStatus().equals(RetrieveMiniEventStatus.SUCCESS) &&
					null != rmea.getCurActiveMiniEvent())
			{
				//get UserMiniEvent info and create the proto to set into resBuilder
				//TODO: Consider protofying MiniEvent stuff
				UserMiniEventProto umep = createInfoProtoUtils
						.createUserMiniEventProto(
								rmea.getMefu(), rmea.getCurActiveMiniEvent(),
								rmea.getMegfus(),
								rmea.getLvlEntered(), rmea.getRewards(),
								rmea.getGoals(), rmea.getLeaderboardRewards());
				resBuilder.setUserMiniEvent(umep);
			}

			RetrieveMiniEventResponseProto resProto = resBuilder.build();
			log.info("resProto for MiniEvent={}", resProto);
			RetrieveMiniEventResponseEvent resEvent = new RetrieveMiniEventResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveMiniEventResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RetrieveMiniEventController processEvent",
					e);
			try {
				resBuilder.setStatus(RetrieveMiniEventStatus.FAIL_OTHER);
				RetrieveMiniEventResponseEvent resEvent = new RetrieveMiniEventResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRetrieveMiniEventResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrieveMiniEventController processEvent",
						e);
			}

		} finally {
			server.unlockPlayer(userId, this.getClass().getSimpleName());
		}
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public MiniEventForUserRetrieveUtil getMiniEventForUserRetrieveUtil() {
		return miniEventForUserRetrieveUtil;
	}

	public void setMiniEventForUserRetrieveUtil(
			MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil) {
		this.miniEventForUserRetrieveUtil = miniEventForUserRetrieveUtil;
	}

	public MiniEventGoalForUserRetrieveUtil getMiniEventGoalForUserRetrieveUtil() {
		return miniEventGoalForUserRetrieveUtil;
	}

	public void setMiniEventGoalForUserRetrieveUtil(
			MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil) {
		this.miniEventGoalForUserRetrieveUtil = miniEventGoalForUserRetrieveUtil;
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
