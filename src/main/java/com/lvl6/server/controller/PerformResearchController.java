package com.lvl6.server.controller;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PerformResearchRequestEvent;
import com.lvl6.events.response.PerformResearchResponseEvent;
import com.lvl6.proto.EventStructureProto.PerformResearchRequestProto;
import com.lvl6.proto.EventStructureProto.PerformResearchResponseProto;
import com.lvl6.proto.EventStructureProto.PerformResearchResponseProto.PerformResearchStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.PerformResearchAction;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.StringUtils;


@Component
@DependsOn("gameServer")
public class PerformResearchController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;


	public PerformResearchController() {
		numAllocatedThreads = 2;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new PerformResearchRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_DESTROY_MONEY_TREE_STRUCTURE_EVENT;
	}

	/*
	 * db stuff done before sending event to eventwriter/client because the
	 * client's not waiting on it immediately anyways
	 */
	// @SuppressWarnings("deprecation")
	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		PerformResearchRequestProto reqProto = ((PerformResearchRequestEvent) event)
				.getPerformResearchRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userStructUuIdList = reqProto.getUserStructUuidList();

		PerformResearchResponseProto.Builder resBuilder = PerformResearchResponseProto.newBuilder();
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			StringUtils.convertToUUID(userStructUuIdList);
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
			PerformResearchResponseEvent resEvent = new PerformResearchResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setPerformResearchResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		// Lock this player's ID
		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			

			writeChangesToDb(userId, resBuilder, userStructUuIdList);

			if (!resBuilder.hasStatus()) {
				resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
			}

			PerformResearchResponseProto resProto = resBuilder.build();

			PerformResearchResponseEvent resEvent = new PerformResearchResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setPerformResearchResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in PerformResearchController processEvent", e);
			//don't let the client hang
		} finally {
			// Unlock this player
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private void writeChangesToDb(
			String userId,
			PerformResearchResponseProto.Builder resBuilder,
			List<String> userStructIdsList
			)
	{
		try {
			Date now = new Date();
			PerformResearchAction dmtsa = new PerformResearchAction(userId,
					userStructIdsList, now, deleteUtil);

			dmtsa.execute(resBuilder);

			if (resBuilder.getStatus().equals(PerformResearchStatus.SUCCESS))
			{
				log.info("successful money tree destroy from user {}",
						userId);
			}
		} catch (Exception e) {
			log.error("problem with destroying user's money tree", e);
		}
	}



	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public DeleteUtil getDeleteUtil()
	{
		return deleteUtil;
	}

	public void setDeleteUtil( DeleteUtil deleteUtil )
	{
		this.deleteUtil = deleteUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil()
	{
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil( UserRetrieveUtils2 userRetrieveUtil )
	{
		this.userRetrieveUtil = userRetrieveUtil;
	}




}
