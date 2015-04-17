package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ExchangeGemsForResourcesRequestEvent;
import com.lvl6.events.response.ExchangeGemsForResourcesResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesRequestProto;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto.ExchangeGemsForResourcesStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;

@Component
@DependsOn("gameServer")
public class ExchangeGemsForResourcesController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	public ExchangeGemsForResourcesController() {
		numAllocatedThreads = 1;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ExchangeGemsForResourcesRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_EXCHANGE_GEMS_FOR_RESOURCES_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		ExchangeGemsForResourcesRequestProto reqProto = ((ExchangeGemsForResourcesRequestEvent) event)
				.getExchangeGemsForResourcesRequestProto();

		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto
				.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
		String userId = senderProto.getUserUuid();
		int numGems = reqProto.getNumGems();
		int numResources = reqProto.getNumResources();
		ResourceType resourceType = reqProto.getResourceType();
		Timestamp curTime = new Timestamp(reqProto.getClientTime());
		int maxCash = senderResourcesProto.getMaxCash();
		int maxOil = senderResourcesProto.getMaxOil();

		Builder resBuilder = ExchangeGemsForResourcesResponseProto.newBuilder();
		resBuilder.setSender(senderResourcesProto);
		resBuilder.setStatus(ExchangeGemsForResourcesStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ExchangeGemsForResourcesStatus.FAIL_OTHER);
			ExchangeGemsForResourcesResponseEvent resEvent = new ExchangeGemsForResourcesResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setExchangeGemsForResourcesResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = userRetrieveUtil.getUserById(userId);

			boolean legit = checkLegit(resBuilder, user, numGems, resourceType,
					numGems);

			boolean successful = false;
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
			if (legit) {
				previousCurrency.put(miscMethods.cash, user.getCash());
				previousCurrency.put(miscMethods.oil, user.getOil());
				previousCurrency.put(miscMethods.gems, user.getGems());
				successful = writeChangesToDb(user, numGems, resourceType,
						numResources, maxCash, maxOil, currencyChange);
			}
			if (successful) {
				resBuilder.setStatus(ExchangeGemsForResourcesStatus.SUCCESS);
			}

			ExchangeGemsForResourcesResponseProto resProto = resBuilder.build();
			ExchangeGemsForResourcesResponseEvent resEvent = new ExchangeGemsForResourcesResponseEvent(
					userId);
			resEvent.setExchangeGemsForResourcesResponseProto(resProto);
			resEvent.setTag(event.getTag());
			server.writeEvent(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(user, previousCurrency,
						currencyChange, curTime, resourceType, numResources,
						numGems);
			}
		} catch (Exception e) {
			log.error(
					"exception in ExchangeGemsForResourcesController processEvent",
					e);
			resBuilder.setStatus(ExchangeGemsForResourcesStatus.FAIL_OTHER);
			ExchangeGemsForResourcesResponseEvent resEvent = new ExchangeGemsForResourcesResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setExchangeGemsForResourcesResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegit(Builder resBuilder, User aUser, int numGems,
			ResourceType resourceType, int numResources) {
		if (null == aUser || null == resourceType || 0 == numGems) {
			log.error(String
					.format("user, resourceType is null, or numGems is 0. user=%s, resourceType=%s numGems=%",
							aUser, resourceType, numGems));
			return false;
		}

		int userGems = aUser.getGems();

		if (userGems < numGems) {
			log.error("user does not have enough gems to exchange for resource."
					+ " userGems="
					+ userGems
					+ "\t resourceType="
					+ resourceType + "\t numResources=" + numResources);
			resBuilder
					.setStatus(ExchangeGemsForResourcesStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDb(User user, int numGems,
			ResourceType resourceType, int numResources, int maxCash,
			int maxOil, Map<String, Integer> currencyChange) {
		boolean success = true;
		log.info(String.format("exchanging %s gems for %s %s", numGems,
				numResources, resourceType.name()));

		int cashChange = 0;
		int oilChange = 0;
		int gemChange = -1 * numGems;

		if (ResourceType.CASH == resourceType) {
			cashChange = numResources;
			if (numResources > 0) {
				int curCash = Math.min(user.getCash(), maxCash); //in case user's cash is more than maxCash.
				int maxCashUserCanGain = maxCash - curCash;
				cashChange = Math.min(numResources, maxCashUserCanGain);
			}
		} else if (ResourceType.OIL == resourceType) {
			oilChange = numResources;
			if (numResources > 0) {
				int curOil = Math.min(user.getOil(), maxOil); //in case user's oil is more than maxOil.
				int maxOilUserCanGain = maxOil - curOil;
				oilChange = Math.min(numResources, maxOilUserCanGain);
			}
		}

		if (0 == oilChange && 0 == cashChange) {
			//  		log.error("oil and cash (user exchanged) for gems are both 0. oilChange=" +
			//  				oilChange + "\t cashChange=" + cashChange + "\t gemChange=" + gemChange +
			//  				"\t maxOil=" + maxOil + "\t maxCash=" + maxCash);
			String preface = "oil and cash (user exchanged) for gems are both 0.";
			log.error(String.format(
					"%s oilChange=%s, cashChange=%s, gemChange=%s", preface,
					oilChange, cashChange, gemChange));
			return false;
		}

		log.info(String.format("user before: %s", user));
		int numUpdated = user.updateRelativeCashAndOilAndGems(cashChange,
				oilChange, gemChange);
		if (2 != numUpdated && 1 != numUpdated) {
			log.error(String.format("did not increase user's %s by %s",
					resourceType, numResources));
			success = false;
		} else {
			if (0 != cashChange) {
				currencyChange.put(miscMethods.cash, cashChange);
			}
			if (0 != oilChange) {
				currencyChange.put(miscMethods.oil, oilChange);
			}
			if (0 != gemChange) {
				currencyChange.put(miscMethods.gems, gemChange);
			}
		}

		log.info(String.format("user after: %s", user));
		return success;
	}

	private void writeToUserCurrencyHistory(User aUser,
			Map<String, Integer> previousCurrency,
			Map<String, Integer> currencyChange, Timestamp curTime,
			ResourceType resourceType, int numResources, int numGems) {
		if (currencyChange.isEmpty()) {
			return;
		}
		String cash = miscMethods.cash;
		String oil = miscMethods.oil;
		String gems = miscMethods.gems;

		String reasonForChange = ControllerConstants.UCHRFC__CURRENCY_EXCHANGE;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append(" exchanged ");
		detailsSb.append(numGems);
		detailsSb.append(" gems for ");
		detailsSb.append(numResources);
		detailsSb.append(" ");
		detailsSb.append(resourceType.name());

		String userId = aUser.getId();
		Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> details = new HashMap<String, String>();

		currentCurrencies.put(cash, aUser.getCash());
		currentCurrencies.put(oil, aUser.getOil());
		currentCurrencies.put(gems, aUser.getGems());
		reasonsForChanges.put(cash, reasonForChange);
		reasonsForChanges.put(oil, reasonForChange);
		reasonsForChanges.put(gems, reasonForChange);
		details.put(cash, detailsSb.toString());
		details.put(oil, detailsSb.toString());
		details.put(gems, detailsSb.toString());

		miscMethods
				.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
						previousCurrency, currentCurrencies, reasonsForChanges,
						details);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
