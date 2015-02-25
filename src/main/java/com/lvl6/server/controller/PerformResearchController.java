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
import com.lvl6.events.request.PerformResearchRequestEvent;
import com.lvl6.events.response.PerformResearchResponseEvent;
import com.lvl6.info.Research;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventResearchProto.PerformResearchRequestProto;
import com.lvl6.proto.EventResearchProto.PerformResearchResponseProto;
import com.lvl6.proto.EventResearchProto.PerformResearchResponseProto.PerformResearchStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.PerformResearchAction;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component @DependsOn("gameServer") public class PerformResearchController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected ResearchForUserRetrieveUtils researchForUserRetrieveUtils;
	
	@Autowired
	protected ResearchRetrieveUtils researchRetrieveUtils;

	@Autowired
	protected UpdateUtil updateUtil;
	
	@Autowired
	protected InsertUtil insertUtil;


	public PerformResearchController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new PerformResearchRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_PERFORM_RESEARCH_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		PerformResearchRequestProto reqProto = ((PerformResearchRequestEvent)event).getPerformResearchRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int researchId = reqProto.getResearchId();
		
		String userResearchUuid = null;
		if(reqProto.hasUserResearchUuid()) {
			 userResearchUuid= reqProto.getUserResearchUuid();
		}
		
		int gemsSpent = 0;
		if(reqProto.hasGemsSpent()) {
			 gemsSpent = reqProto.getGemsSpent();
		}
		
		int resourceChange = 0;
		if(reqProto.hasResourceChange()) {
			resourceChange = reqProto.getResourceChange();
		}
		
		ResourceType resourceType = null;
		if(reqProto.hasResourceType()) {
			resourceType = reqProto.getResourceType();
		}
		Date now = new Date(reqProto.getClientTime());
		Timestamp nowTimestamp = new Timestamp(now.getTime());

		//values to send to client
		PerformResearchResponseProto.Builder resBuilder = PerformResearchResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);

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
			resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
			PerformResearchResponseEvent resEvent = new PerformResearchResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setPerformResearchResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = userRetrieveUtils.getUserById(userId);
			Research research = ResearchRetrieveUtils.getResearchForId(researchId);
			PerformResearchAction pra = new PerformResearchAction(userId, user, research, userResearchUuid, 
					gemsSpent, resourceChange, resourceType, nowTimestamp, insertUtil, updateUtil, 
					researchForUserRetrieveUtils);

			pra.execute(resBuilder);

			if (PerformResearchStatus.SUCCESS.equals(resBuilder.getStatus())) {

				userResearchUuid = pra.getUserResearchUuid();
				if (!(userResearchUuid.equals("")) || null != userResearchUuid) {
					resBuilder.setUserResearchUuid(userResearchUuid);
				}
			}
			PerformResearchResponseProto resProto = resBuilder.build();
			PerformResearchResponseEvent resEvent = new PerformResearchResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setPerformResearchResponseProto(resProto);
			server.writeEvent(resEvent);

			writeToUserCurrencyHistory(userId, nowTimestamp, pra);

		} catch (Exception e) {
			log.error("exception in PerformResearchController processEvent", e);
			// don't let the client hang
			try {
				resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
				PerformResearchResponseEvent resEvent = new PerformResearchResponseEvent(senderProto.getUserUuid());
				resEvent.setTag(event.getTag());
				resEvent.setPerformResearchResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in SellUserMonsterController processEvent", e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName()); 
		}
	}

	private void writeToUserCurrencyHistory(String userId, Timestamp date,
		PerformResearchAction pra)
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

}