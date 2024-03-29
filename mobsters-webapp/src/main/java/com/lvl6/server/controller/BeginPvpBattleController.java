package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginPvpBattleRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.BeginPvpBattleResponseEvent;
import com.lvl6.events.response.ReviveInDungeonResponseEvent;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleRequestProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.BeginPvpBattleAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class BeginPvpBattleController extends EventController {

	private static Logger log = LoggerFactory.getLogger(BeginPvpBattleController.class);

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtil;

	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	public BeginPvpBattleController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new BeginPvpBattleRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BEGIN_PVP_BATTLE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		BeginPvpBattleRequestProto reqProto = ((BeginPvpBattleRequestEvent) event)
				.getBeginPvpBattleRequestProto();
		log.info("reqProto={}", reqProto);

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		//TODO: FIGURE OUT IF STILL NEEDED
		//int senderElo = reqProto.getSenderElo();

		String attackerId = senderProto.getUserUuid();
		PvpProto enemyProto = reqProto.getEnemy();
		String enemyUserId = null;

		boolean exactingRevenge = reqProto.getExactingRevenge();
		Timestamp previousBattleEndTime = null;
		if (exactingRevenge) {
			//the battle that allowed sender to start this revenge battle
			//where sender was the defender and enemy was the attacker
			previousBattleEndTime = new Timestamp(
					reqProto.getPreviousBattleEndTime());
		}

		//set some values to send to the client (the response proto)
		BeginPvpBattleResponseProto.Builder resBuilder = BeginPvpBattleResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default
		BeginPvpBattleResponseEvent resEvent = new BeginPvpBattleResponseEvent(
				attackerId);
		resEvent.setTag(event.getTag());
		
		if(reqProto.getAttackStartTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtil.numMinutesDifference(new Date(reqProto.getAttackStartTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID enemyUserUuid = null;
		boolean invalidUuids = true;
		try {
			UUID.fromString(attackerId);

			enemyUserId = enemyProto.getDefender()
					.getUserUuid();
			if (!"".equals(enemyUserId)) {
				enemyUserUuid = UUID.fromString(enemyUserId);
			}

			invalidUuids = false;
		} catch (Exception e1) {
			log.error(String.format("UUID error. incorrect enemyUserId=%s",
					enemyUserId), e1);
			invalidUuids = true;
		}

		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		//lock the user that client is going to attack, in order to prevent others from
		//attacking same guy, only lock a real user
		try {
			if (null != enemyUserId && !enemyUserId.isEmpty()) {
				gotLock = locker.lockPlayer(enemyUserUuid, this.getClass().getSimpleName());
			}
			Date curDate = new Date(reqProto.getAttackStartTime());
			int enemyElo = enemyProto.getPvpLeagueStats().getElo();

			BeginPvpBattleAction bpa = new BeginPvpBattleAction(attackerId,
					enemyUserId, enemyElo, curDate, exactingRevenge,
					previousBattleEndTime, pvpLeagueForUserRetrieveUtil,
					hazelcastPvpUtil, timeUtil, insertUtil, updateUtil,
					serverToggleRetrieveUtil);

			bpa.execute(resBuilder);

			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in BeginPvpBattleController processEvent", e);
			//don't let the client hang
			try {
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in BeginPvpBattleController processEvent",
						e);
			}

		} finally {
			if (null != enemyUserId && !enemyUserId.isEmpty() && gotLock) {
				//only unlock if real user
				locker.unlockPlayer(enemyUserUuid, this.getClass()
						.getSimpleName());
			}
		}
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtil() {
		return timeUtil;
	}

	public void setTimeUtil(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
	}

	public void setTimeUtils(TimeUtils timeUtil) {
		this.timeUtil = timeUtil;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public ServerToggleRetrieveUtils getServerToggleRetrieveUtil() {
		return serverToggleRetrieveUtil;
	}

	public void setServerToggleRetrieveUtil(
			ServerToggleRetrieveUtils serverToggleRetrieveUtil) {
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

}
