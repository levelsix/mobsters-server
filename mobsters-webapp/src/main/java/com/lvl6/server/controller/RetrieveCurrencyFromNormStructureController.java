package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveCurrencyFromNormStructureRequestEvent;
import com.lvl6.events.response.RetrieveCurrencyFromNormStructureResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.StructureRetrieval;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto.StructRetrieval;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.RetrieveCurrencyFromNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.RetrieveCurrencyFromNormStructureAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class RetrieveCurrencyFromNormStructureController extends
		EventController {

	private static Logger log = LoggerFactory.getLogger(RetrieveCurrencyFromNormStructureController.class);

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected StructureForUserRetrieveUtils2 userStructRetrieveUtil;
	
	@Autowired
	protected StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils;
	
	@Autowired
	protected StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils;

	@Autowired
	protected UpdateUtil updateUtil;

	public RetrieveCurrencyFromNormStructureController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveCurrencyFromNormStructureRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RetrieveCurrencyFromNormStructureRequestProto reqProto = ((RetrieveCurrencyFromNormStructureRequestEvent) event)
				.getRetrieveCurrencyFromNormStructureRequestProto();
		log.info("reqProto={}", reqProto);
		//get stuff client sent
		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto
				.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
		String userId = senderProto.getUserUuid();
		List<StructRetrieval> structRetrievals = reqProto
				.getStructRetrievalsList();
		Timestamp curTime = new Timestamp((new Date()).getTime());
		int maxCash = senderResourcesProto.getMaxCash();
		int maxOil = senderResourcesProto.getMaxOil();

		//		Map<String, Timestamp> userStructIdsToTimesOfRetrieval =  new HashMap<String, Timestamp>();
		//		Map<String, Integer> userStructIdsToAmountCollected = new HashMap<String, Integer>();
		//create map from ids to times and check for duplicates
		//		getIdsAndTimes(structRetrievals, duplicates,
		//				userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected); 

		//		List<String> userStructIds = new ArrayList<String>(userStructIdsToTimesOfRetrieval.keySet());

		List<String> duplicates = new ArrayList<String>();
		Map<String, StructureRetrieval> userStructIdsToStructRetrievals = getStructureRetrievalMap(
				structRetrievals, duplicates);

		RetrieveCurrencyFromNormStructureResponseProto.Builder resBuilder = RetrieveCurrencyFromNormStructureResponseProto
				.newBuilder();
		resBuilder
				.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
		resBuilder.setSender(senderResourcesProto);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			for (String userStructId : userStructIdsToStructRetrievals.keySet()) {
				UUID.fromString(userStructId);
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userStructIds=%s",
					userId, structRetrievals), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder
					.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
			RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {

			RetrieveCurrencyFromNormStructureAction rcfnsa = new RetrieveCurrencyFromNormStructureAction(
					userId, maxCash, maxOil, duplicates,
					userStructIdsToStructRetrievals, userRetrieveUtil,
					userStructRetrieveUtil, structureMoneyTreeRetrieveUtils, 
					structureResourceGeneratorRetrieveUtils, updateUtil,
					miscMethods);

			rcfnsa.execute(resBuilder);

			RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);

			if (RetrieveCurrencyFromNormStructureStatus.SUCCESS
					.equals(resBuilder.getStatus())) {
				User user = rcfnsa.getUser();
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToCurrencyHistory(userId, curTime, rcfnsa);
			}
		} catch (Exception e) {
			log.error(
					"exception in RetrieveCurrencyFromNormStructureController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder
						.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
				RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrieveCurrencyFromNormStructureController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private Map<String, StructureRetrieval> getStructureRetrievalMap(
			List<StructRetrieval> structRetrievalProtos, List<String> duplicates) {
		Map<String, StructureRetrieval> userStructIdToStructureRetrieval = new HashMap<String, StructureRetrieval>();
		if (null == structRetrievalProtos || structRetrievalProtos.isEmpty()) {
			log.error("RetrieveCurrencyFromNormStruct request did not send any user struct ids.");
			return userStructIdToStructureRetrieval;
		}

		for (StructRetrieval srProto : structRetrievalProtos) {
			StructureRetrieval sr = new StructureRetrieval();
			String userStructId = srProto.getUserStructUuid();

			if (userStructIdToStructureRetrieval.containsKey(userStructId)) {
				duplicates.add(userStructId);
				continue;
			}

			sr.setUserStructId(userStructId);
			Date timeOfRetrieval = null;
			long retrievalTime = srProto.getTimeOfRetrieval();
			if (retrievalTime > 0) {
				timeOfRetrieval = new Date(retrievalTime);
			}

			sr.setTimeOfRetrieval(timeOfRetrieval);
			sr.setAmountCollected(srProto.getAmountCollected());

			userStructIdToStructureRetrieval.put(userStructId, sr);
		}

		return userStructIdToStructureRetrieval;
	}

	private void writeToCurrencyHistory(String userId, Timestamp date,
			RetrieveCurrencyFromNormStructureAction rcfnsa) {
		miscMethods.writeToUserCurrencyOneUser(userId, date,
				rcfnsa.getCurrencyDeltas(), rcfnsa.getPreviousCurrencies(),
				rcfnsa.getCurrentCurrencies(), rcfnsa.getReasons(),
				rcfnsa.getDetails());
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public StructureForUserRetrieveUtils2 getUserStructRetrieveUtil() {
		return userStructRetrieveUtil;
	}

	public void setUserStructRetrieveUtil(
			StructureForUserRetrieveUtils2 userStructRetrieveUtil) {
		this.userStructRetrieveUtil = userStructRetrieveUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

}
