package com.lvl6.server.controller.actionobjects;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.StructureRetrieval;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.RetrieveCurrencyFromNormStructureStatus;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class RetrieveCurrencyFromNormStructureAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private int maxCash;
	private int maxOil;
	private List<String> duplicates;
	private Map<String, StructureRetrieval> userStructIdsToStructRetrievals;
	private UserRetrieveUtils2 userRetrieveUtil;
	private StructureForUserRetrieveUtils2 userStructRetrieveUtil;
	private StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils;
	private StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils;
	private UpdateUtil updateUtil;
	private MiscMethods miscMethods;

	public RetrieveCurrencyFromNormStructureAction(String userId, int maxCash,
			int maxOil, List<String> duplicates,
			Map<String, StructureRetrieval> userStructIdsToStructRetrievals,
			UserRetrieveUtils2 userRetrieveUtil,
			StructureForUserRetrieveUtils2 userStructRetrieveUtil,
			StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils,
			StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils,
			UpdateUtil updateUtil, MiscMethods miscMethods) {
		super();
		this.userId = userId;
		this.maxCash = maxCash;
		this.maxOil = maxOil;
		this.duplicates = duplicates;
		this.userStructIdsToStructRetrievals = userStructIdsToStructRetrievals;
		this.userRetrieveUtil = userRetrieveUtil;
		this.userStructRetrieveUtil = userStructRetrieveUtil;
		this.structureMoneyTreeRetrieveUtils = structureMoneyTreeRetrieveUtils;
		this.structureResourceGeneratorRetrieveUtils = structureResourceGeneratorRetrieveUtils;
		this.updateUtil = updateUtil;
		this.miscMethods = miscMethods;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RetrieveCurrencyFromNormStructureResource {
	//
	//
	//		public RetrieveCurrencyFromNormStructureResource() {
	//
	//		}
	//	}
	//
	//	public RetrieveCurrencyFromNormStructureResource execute() {
	//
	//	}

	//derived state
	private Collection<String> userStructIds;
	private Map<String, StructureForUser> sfuIdToSfu;
	private Map<String, StructureMoneyTree> sfuIdToMoneyTree;
	private Map<String, StructureResourceGenerator> sfuIdToResourceGenerator;
	private int gemsGained;
	private int cashGained;
	private int oilGained;
	private User user;

	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder
				.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (userStructIdsToStructRetrievals.isEmpty()) {
			log.error("no user structs sent");
			return false;
		}

		if (!duplicates.isEmpty()) {
			log.error("clients sent duplicates={}, {}", duplicates,
					userStructIdsToStructRetrievals);
			return false;
		}

		userStructIds = userStructIdsToStructRetrievals.keySet();

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		getUserStructIdsToUserStructs();
		if (sfuIdToSfu.isEmpty()) {
			log.error("no StructureForUsers for ids: {}", userStructIds);
			return false;
		}

		getUserStructIdsToMoneyTrees();
		getUserStructIdsToResourceGenerators();
		if (sfuIdToMoneyTree.isEmpty() && sfuIdToResourceGenerator.isEmpty()) {
			log.error("client sent invalid ids. {}",
					userStructIdsToStructRetrievals);
			return false;
		}

		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error(String.format("no user with id=%s", userId));
			return false;
		}

		//go through the userStructIds the user sent, checking which structs can be
		//retrieved
		gemsGained = 0;
		int cashGainedTemp = 0;
		int oilGainedTemp = 0;
		for (String id : userStructIds) {
			StructureForUser userStruct = sfuIdToSfu.get(id);
			int amountCollected = userStructIdsToStructRetrievals.get(id)
					.getAmountCollected();

			if (!userStruct.isComplete()) {
				resBuilder
						.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
				log.error("(will continue processing) struct not complete {}",
						userStruct);
				//remove invalid user structure
				sfuIdToSfu.remove(id);
				continue;
			}

			if (sfuIdToMoneyTree.containsKey(id)) {
				log.info("collected gems: amountCollected={}", amountCollected);
				gemsGained += amountCollected;

			} else {
				StructureResourceGenerator struct = sfuIdToResourceGenerator
						.get(id);

				String type = struct.getResourceTypeGenerated();
				ResourceType rt = ResourceType.valueOf(type);
				if (ResourceType.CASH.equals(rt)) {
					log.info("collected cash: amountCollected={}",
							amountCollected);
					cashGainedTemp += amountCollected;

				} else if (ResourceType.OIL.equals(rt)) {
					log.info("collected oil: amountCollected={}",
							amountCollected);
					oilGainedTemp += amountCollected;

				} else {
					log.error(
							"(will continue processing) unknown resource type: {}",
							rt);
					//remove invalid user structure
					sfuIdToSfu.remove(id);
				}
			}
		}

		boolean invalidChange = gemsGained < 0 || cashGainedTemp < 0
				|| oilGainedTemp < 0;
		if (invalidChange) {
			log.error("gems, cash, or oil invalid. gems={}, cash={}, oil={}",
					new Object[] { gemsGained, cashGainedTemp, oilGainedTemp });
			return false;
		}

		int curCash = Math.min(user.getCash(), maxCash); //in case user's cash is more than maxCash
		int maxCashUserCanGain = maxCash - curCash; //this is the max cash the user can gain
		cashGained = Math.min(maxCashUserCanGain, cashGainedTemp);

		int curOil = Math.min(user.getOil(), maxOil); //in case user's oil is more than maxOil
		int maxOilUserCanGain = maxOil - curOil;
		oilGained = Math.min(maxOilUserCanGain, oilGainedTemp);

		boolean noChange = 0 == gemsGained && 0 == cashGainedTemp
				&& 0 == oilGainedTemp;
		if (noChange) {
			String prefix = "after capping resources, gems, cash, oil invalid.";
			log.error(
					"{} gems={}, cash={}, oil={}, maxCash={}, maxOil={}, cappedCash={}, cappedOil={}",
					new Object[] { prefix, gemsGained, cashGainedTemp,
							oilGainedTemp, maxCash, maxOil, cashGained,
							oilGained });
			return false;
		}

		return true;
	}

	//retrieve these user structs from the db and put them in a map
	private void getUserStructIdsToUserStructs() {
		sfuIdToSfu = new HashMap<String, StructureForUser>();

		List<StructureForUser> userStructList = userStructRetrieveUtil
				.getSpecificOrAllUserStructsForUser(userId, userStructIds);
		for (StructureForUser us : userStructList) {
			if (null != us) {
				sfuIdToSfu.put(us.getId(), us);
			} else {
				String preface = "could not retrieve one of the user structs.";
				log.warn(
						"{} userStructIds to retrieve={}. user structs retrieved={}. Will continue processing.",
						new Object[] { preface, userStructIds, userStructList });
			}
		}
	}

	//link up a user struct id with the MoneyTree object is possible
	private void getUserStructIdsToMoneyTrees() {
		sfuIdToMoneyTree = new HashMap<String, StructureMoneyTree>();

		for (StructureForUser us : sfuIdToSfu.values()) {
			int structId = us.getStructId();
			String userStructId = us.getId();

			StructureMoneyTree s = structureMoneyTreeRetrieveUtils
					.getMoneyTreeForStructId(structId);
			if (null != s) {
				sfuIdToMoneyTree.put(userStructId, s);
			}
		}
	}

	//link up a user struct id with the structure object
	private void getUserStructIdsToResourceGenerators() {
		sfuIdToResourceGenerator = new HashMap<String, StructureResourceGenerator>();

		for (StructureForUser us : sfuIdToSfu.values()) {
			int structId = us.getStructId();
			String userStructId = us.getId();

			StructureResourceGenerator s = structureResourceGeneratorRetrieveUtils
					.getResourceGeneratorForStructId(structId);
			if (null != s) {
				sfuIdToResourceGenerator.put(userStructId, s);
			}
		}

	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		if (0 != gemsGained) {
			prevCurrencies.put(miscMethods.gems, user.getGems());
		}
		if (0 != cashGained) {
			prevCurrencies.put(miscMethods.cash, user.getCash());
		}
		if (0 != oilGained) {
			prevCurrencies.put(miscMethods.oil, user.getOil());
		}

		//give user the resources
		log.info("user before: {} \t\t", user);
		if (!user.updateRelativeCoinsOilRetrievedFromStructs(cashGained,
				oilGained, gemsGained)) {
			log.error(
					"can't update user stats after retrieving {} cash, {} oil, {} gems",
					new Object[] { cashGained, oilGained, gemsGained });
			return false;

		}
		log.info("user after: {}", user);

		if (!updateUtil.updateUserStructsLastRetrieved(
				userStructIdsToStructRetrievals, sfuIdToSfu)) {
			log.error(
					"problem updating user structs last retrieved for userStructIds {}",
					userStructIdsToStructRetrievals);
			return false;
		}

		prepCurrencyHistory();

		return true;
	}

	private void prepCurrencyHistory() {
		String gems = miscMethods.gems;
		String cash = miscMethods.cash;
		String oil = miscMethods.oil;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		if (0 != gemsGained) {
			currencyDeltas.put(gems, gemsGained);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges
					.put(gems,
							ControllerConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_MONEY_TREE);
		}
		if (0 != cashGained) {
			currencyDeltas.put(cash, cashGained);
			curCurrencies.put(cash, user.getCash());
			reasonsForChanges
					.put(cash,
							ControllerConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT);
		}
		if (0 != oilGained) {
			currencyDeltas.put(oil, oilGained);
			curCurrencies.put(oil, user.getOil());
			reasonsForChanges
					.put(oil,
							ControllerConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT);
		}

		String reason = "(userStructId,time,%s.amount)=";
		StringBuilder cashDetailSb = new StringBuilder();
		cashDetailSb.append(String.format(reason, cash));
		StringBuilder oilDetailSb = new StringBuilder();
		oilDetailSb.append(String.format(reason, oil));
		StringBuilder gemsDetailSb = new StringBuilder();
		gemsDetailSb.append(String.format(reason, gems));
		details = new HashMap<String, String>();

		//being descriptive, separating cash stuff from oil stuff
		String cashType = ResourceType.CASH.name();
		String oilType = ResourceType.OIL.name();
		for (String sfuId : sfuIdToSfu.keySet()) {
			StructureRetrieval sr = userStructIdsToStructRetrievals.get(sfuId);
			Date d = sr.getTimeOfRetrieval();
			int amount = sr.getAmountCollected();

			String detail = String.format("(%s,%s,%s)", sfuId,
					d, amount );

			if (sfuIdToMoneyTree.containsKey(sfuId)) {
				gemsDetailSb.append(detail);

			} else if (sfuIdToResourceGenerator.containsKey(sfuId)) {
				StructureResourceGenerator srg = sfuIdToResourceGenerator
						.get(sfuId);
				String type = srg.getResourceTypeGenerated();
				if (cashType.equals(type)) {
					cashDetailSb.append(detail);

				} else if (oilType.equals(type)) {
					oilDetailSb.append(detail);
				}
			}
		}

		details.put(gems, gemsDetailSb.toString());
		details.put(cash, cashDetailSb.toString());
		details.put(oil, oilDetailSb.toString());
	}

	public User getUser() {
		return user;
	}

	public Map<String, Integer> getCurrencyDeltas() {
		return currencyDeltas;
	}

	public Map<String, Integer> getPreviousCurrencies() {
		return prevCurrencies;
	}

	public Map<String, Integer> getCurrentCurrencies() {
		return curCurrencies;
	}

	public Map<String, String> getReasons() {
		return reasonsForChanges;
	}

	public Map<String, String> getDetails() {
		return details;
	}
}
