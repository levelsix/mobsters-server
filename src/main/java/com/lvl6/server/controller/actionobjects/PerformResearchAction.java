package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Research;
import com.lvl6.info.ResearchForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventResearchProto.PerformResearchResponseProto.Builder;
import com.lvl6.proto.EventResearchProto.PerformResearchResponseProto.PerformResearchStatus;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class PerformResearchAction
{ 
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private UserRetrieveUtils2 userRetrieveUtils;
	private int researchId;
	private String userResearchUuid; //update the row when researchs are upgraded
	private int gemsCost;
	private int resourceCost;
	private ResourceType resourceType;
	private Date now;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	private ResearchForUserRetrieveUtils researchForUserRetrieveUtil;

	public PerformResearchAction( 
		String userId,
		UserRetrieveUtils2 userRetrieveUtils,
		int researchId,
		String userResearchUuid,
		int gemsCost,
		int resourceCost,
		ResourceType resourceType,
		Date now,
		InsertUtil insertUtil,
		UpdateUtil updateUtil,
		ResearchForUserRetrieveUtils researchForUserRetrieveUtil
	 )
	{
		super();
		this.userId = userId;
		this.userRetrieveUtils = userRetrieveUtils;
		this.researchId = researchId;
		this.userResearchUuid = userResearchUuid;
		this.gemsCost = gemsCost;
		this.resourceCost = resourceCost;
		this.resourceType = resourceType;
		this.now = now;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}

	private User user;
	private Research research;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
		
		boolean valid = false;
		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(PerformResearchStatus.SUCCESS);
	}

	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtils.getUserById(userId);
		if (null == user) {
			resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
			log.error( "no user with id={}", userId );
			return false;
		}
		
		boolean legitResearch = verifyResearch(resBuilder);
		if (!legitResearch) {
			return false;
		}
		
		boolean userHasUserResearch = false;
//		if(userResearchUuid != null) {
//			userHasUserResearch = verifyUserResearch(resBuilder);
//			if(!userHasUserResearch) {
//				return false;
//			}
//		}
		
		boolean hasEnoughGems = true;
		boolean hasEnoughResource = true;
		
		if(gemsCost > 0) {
			hasEnoughGems = verifyEnoughGems(resBuilder);
		}
		if(resourceCost > 0) {
			hasEnoughResource = verifyEnoughResource(resBuilder);
		}
		
		if(hasEnoughGems && hasEnoughResource) {
			return true;
		}
		else return false;
	}
	
	private boolean verifyResearch(Builder resBuilder)
	{
		research = ResearchRetrieveUtils.getResearchForId(researchId);
		if (null == research) {
			resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
			log.error("no research for id: " + researchId);
			return false;
		}
		return true;
	}
	
//	private boolean verifyUserResearch(Builder resBuilder) {
//		ResearchForUser userResearch = researchForUserRetrieveUtil.getResearchForUser(userResearchUuid);
//		if(userResearch == null) {
//			log.error("user research is null for userresearchid: " + userResearchUuid);
//			return false;
//		}
//		else return true;
//	}
	
	private boolean verifyEnoughGems(Builder resBuilder) {
		int userGems = user.getGems();

		//check if user can afford to buy however many more user wants to buy
		if (userGems < gemsCost) {
			resBuilder.setStatus(PerformResearchStatus.FAIL_INSUFFICIENT_GEMS);
			log.error("user has less gems then amount spent in request, user gems= " + userGems);
			return false; 
		}
		else return true;
	}
	
	private boolean verifyEnoughResource(Builder resBuilder) {
		if(resourceType == ResourceType.CASH) {
			int userCash = user.getCash();
			if(userCash < resourceCost) {
				resBuilder.setStatus(PerformResearchStatus.FAIL_INSUFFICIENT_CASH);
				log.error("user has less cash then amount spent in request, user gems= " + userCash);

				return false;
			}
		}
		else if(resourceType == ResourceType.OIL) {
			int userOil = user.getOil();
			if(userOil < resourceCost) {
				resBuilder.setStatus(PerformResearchStatus.FAIL_INSUFFICIENT_OIL);
				log.error("user has less oil then amount spent in request, user oil= " + userOil);

				return false;
			}
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();
		boolean successfulInsertOrUpdate = false;
		Timestamp timeOfPurchase = new Timestamp(now.getTime());

		
		if(userResearchUuid != null && !userResearchUuid.equals("")) {
			successfulInsertOrUpdate = updateUtil.updateUserResearch(userResearchUuid, research.getId(), timeOfPurchase);
		}
		else {
			boolean isComplete = false;
			userResearchUuid = insertUtil.insertUserResearch(userId, research, timeOfPurchase, isComplete);
			if(userResearchUuid != null) {
				successfulInsertOrUpdate = true;
			}
		}
		
		if(!successfulInsertOrUpdate) {
			log.error("failed to insert or updated user research");
			return false;
		}
		
		if(!(gemsCost > 0) && (resourceType != ResourceType.CASH) && (resourceType != ResourceType.OIL)) {
			resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
			log.error("not being purchased with gems, cash, or oil, what is this voodoo shit");
			return false;
		}

		int gemsChange = 0;
		int cashChange = 0;
		int oilChange = 0;
		int expChange = 0;
		
		if(gemsCost > 0) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
			gemsChange = -1*gemsCost;
		}

		if (resourceType == ResourceType.CASH) {
			prevCurrencies.put(MiscMethods.cash, user.getCash());
			cashChange = -1*resourceCost;
		}
		else if (resourceType == ResourceType.OIL){
			prevCurrencies.put(MiscMethods.oil, user.getOil());
			oilChange = -1*resourceCost;
		}
		
//		user.updateRelativeGemsCashOilExperienceNaive(gemsChange, cashChange, oilChange, expChange);
		
		updateUserCurrency();
		
		prepCurrencyHistory();

		return true;
	}

	private void updateUserCurrency()
	{
		int gemsDelta = -1 * gemsCost;
		int resourceDelta = -1* resourceCost;
		
		boolean updated = user.updateGemsandResourcesFromPerformingResearch(gemsDelta, resourceDelta, resourceType);
		log.info("updated, user paid for research {}", updated);
	}
	
	private void prepCurrencyHistory()
	{
		String gems = MiscMethods.gems;
		String cash = MiscMethods.cash;
		String oil = MiscMethods.oil;
		
		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		StringBuilder detailSb = new StringBuilder();
		StringBuilder detailSb2 = new StringBuilder();
		details = new HashMap<String, String>();

		if (0 != gemsCost) {
			currencyDeltas.put(gems, gemsCost);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
			detailSb.append(" gemsSpent=");
			detailSb.append(gemsCost);
			details.put(gems, detailSb.toString());
		}
		
		if(resourceCost>0) {
			if(resourceType == ResourceType.CASH) {
				currencyDeltas.put(cash, resourceCost);
				curCurrencies.put(cash, user.getCash());
				reasonsForChanges.put(cash, ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
				detailSb2.append(" cash spent= ");
				detailSb2.append(resourceCost);
				details.put(cash, detailSb2.toString());

			}
			else if(resourceType == ResourceType.OIL) {
				currencyDeltas.put(oil, resourceCost);
				curCurrencies.put(oil, user.getOil());
				reasonsForChanges.put(oil, ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
				detailSb2.append(" oil spent= ");
				detailSb2.append(resourceCost);
				details.put(oil, detailSb2.toString());
			}
		}
	}

	public User getUser() {
		return user;
	}
	
	public String getUserResearchUuid() {
		return userResearchUuid;
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
