package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginPvpBattleRequestEvent;
import com.lvl6.events.response.BeginPvpBattleResponseEvent;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleRequestProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto;
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto.BeginPvpBattleStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.BeginPvpBattleAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class BeginPvpBattleController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

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
		numAllocatedThreads = 7;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
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
		resBuilder.setStatus(BeginPvpBattleStatus.FAIL_OTHER); //default
		BeginPvpBattleResponseEvent resEvent = new BeginPvpBattleResponseEvent(
				attackerId);
		resEvent.setTag(event.getTag());

		UUID enemyUserUuid = null;
		try {
			UUID.fromString(attackerId);

			enemyUserId = enemyProto.getDefender().getMinUserProto()
					.getUserUuid();
			if (!"".equals(enemyUserId)) {
				enemyUserUuid = UUID.fromString(enemyUserId);
			}

		} catch (Exception e1) {
			log.error(String.format("UUID error. incorrect enemyUserId=%s",
					enemyUserId), e1);
		}

		//lock the user that client is going to attack, in order to prevent others from
		//attacking same guy, only lock a real user
		if (null != enemyUserId && !enemyUserId.isEmpty()) {
			locker.lockPlayer(enemyUserUuid, this.getClass().getSimpleName());
		}
		try {
			Date curDate = new Date(reqProto.getAttackStartTime());
			int enemyElo = enemyProto.getPvpLeagueStats().getElo();

			BeginPvpBattleAction bpa = new BeginPvpBattleAction(attackerId,
					enemyUserId, enemyElo, curDate, exactingRevenge,
					previousBattleEndTime, pvpLeagueForUserRetrieveUtil,
					hazelcastPvpUtil, timeUtil, insertUtil, updateUtil,
					serverToggleRetrieveUtil);

			bpa.execute(resBuilder);

			resEvent.setBeginPvpBattleResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in BeginPvpBattleController processEvent", e);
			//don't let the client hang
			try {
				resEvent.setBeginPvpBattleResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in BeginPvpBattleController processEvent",
						e);
			}

		} finally {
			if (null != enemyUserId && !enemyUserId.isEmpty()) {
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
