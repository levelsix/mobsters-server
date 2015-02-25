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
import com.lvl6.events.request.FinishPerformingResearchRequestEvent;
import com.lvl6.events.response.FinishPerformingResearchResponseEvent;
import com.lvl6.info.Research;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchRequestProto;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchResponseProto;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchResponseProto.FinishPerformingResearchStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.FinishPerformingResearchAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component @DependsOn("gameServer") public class FinishPerformingResearchController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected ResearchForUserRetrieveUtils researchForUserRetrieveUtil;


	@Autowired
	protected UpdateUtil updateUtil;
	
	@Autowired
	protected InsertUtil insertUtil;


	public FinishPerformingResearchController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new FinishPerformingResearchRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_PERFORM_RESEARCH_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		FinishPerformingResearchRequestProto reqProto = ((FinishPerformingResearchRequestEvent)event).getFinishPerformingResearchRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();

		String userResearchUuid= reqProto.getUserResearchUuid();

		int gemsSpent = 0;
		if(reqProto.hasGemsSpent()) {
			 gemsSpent = reqProto.getGemsSpent();
		}
		

		//values to send to client
		FinishPerformingResearchResponseProto.Builder resBuilder = FinishPerformingResearchResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(FinishPerformingResearchStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s",
				userId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(FinishPerformingResearchStatus.FAIL_OTHER);
			FinishPerformingResearchResponseEvent resEvent = new FinishPerformingResearchResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setFinishPerformingResearchResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = userRetrieveUtils.getUserById(userId);
			Date now = new Date();
			FinishPerformingResearchAction fpra = new FinishPerformingResearchAction(userId, user, userResearchUuid, 
					gemsSpent, now, updateUtil, researchForUserRetrieveUtil);

			fpra.execute(resBuilder);

			FinishPerformingResearchResponseProto resProto = resBuilder.build();
			FinishPerformingResearchResponseEvent resEvent = new FinishPerformingResearchResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setFinishPerformingResearchResponseProto(resProto);
			server.writeEvent(resEvent);

			Timestamp nowTimestamp = new Timestamp(now.getTime());
			writeToUserCurrencyHistory(userId, nowTimestamp, fpra);

		} catch (Exception e) {
			log.error("exception in FinishPerformingResearchController processEvent", e);
			// don't let the client hang
			try {
				resBuilder.setStatus(FinishPerformingResearchStatus.FAIL_OTHER);
				FinishPerformingResearchResponseEvent resEvent = new FinishPerformingResearchResponseEvent(senderProto.getUserUuid());
				resEvent.setTag(event.getTag());
				resEvent.setFinishPerformingResearchResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SellUserMonsterController processEvent", e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName()); 
		}
	}

	private void writeToUserCurrencyHistory(String userId, Timestamp date,
		FinishPerformingResearchAction pra)
	{
		MiscMethods.writeToUserCurrencyOneUser(userId, date,
			pra.getCurrencyDeltas(), pra.getPreviousCurrencies(),
    		pra.getCurrentCurrencies(), pra.getReasons(),
    		pra.getDetails());
	}


	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtils()
	{
		return timeUtils;
	}

	public void setTimeUtils( TimeUtils timeUtils )
	{
		this.timeUtils = timeUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UpdateUtil getUpdateUtil()
	{
		return updateUtil;
	}

	public void setUpdateUtil( UpdateUtil updateUtil )
	{
		this.updateUtil = updateUtil;
	}

	public ResearchForUserRetrieveUtils getResearchForUserRetrieveUtil() {
		return researchForUserRetrieveUtil;
	}

	public void setResearchForUserRetrieveUtil(
			ResearchForUserRetrieveUtils researchForUserRetrieveUtil) {
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}
	
	

}