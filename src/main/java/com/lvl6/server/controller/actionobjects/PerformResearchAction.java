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
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class PerformResearchAction
{ 
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private User user;
	private Research research;
	private String userResearchUuid; //update the row when researchs are upgraded
	private int gemsSpent;
	private int resourceChange;
	private ResourceType resourceType;
	private Date now;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	private ResearchForUserRetrieveUtils researchForUserRetrieveUtil;

	public PerformResearchAction( 
		String userId,
		User user,
		Research research,
		String userResearchUuid,
		int gemsSpent,
		int resourceChange,
		ResourceType resourceType,
		Date now,
		InsertUtil insertUtil,
		UpdateUtil updateUtil,
		ResearchForUserRetrieveUtils researchForUserRetrieveUtil
	 )
	{
		super();
		this.userId = userId;
		this.user = user;
		this.research = research;
		this.userResearchUuid = userResearchUuid;
		this.gemsSpent = gemsSpent;
		this.resourceChange = resourceChange;
		this.resourceType = resourceType;
		this.now = now;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}


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
		if(userResearchUuid != null) {
			userHasUserResearch = verifyUserResearch(resBuilder);
			if(!userHasUserResearch) {
				return false;
			}
		}
		
		boolean hasEnoughGems = true;
		boolean hasEnoughResource = true;
		
		if(gemsSpent > 0) {
			hasEnoughGems = verifyEnoughGems(resBuilder);
		}
		if(resourceChange > 0) {
			hasEnoughResource = verifyEnoughResource(resBuilder);
		}
		
		if(hasEnoughGems && hasEnoughResource) {
			return true;
		}
		else return false;
	}
	
	private boolean verifyResearch(Builder resBuilder)
	{
		if (null == research) {
			resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
			log.error("no research for id");
			return false;
		}
		return true;
	}
	
	private boolean verifyUserResearch(Builder resBuilder) {
		List<ResearchForUser> userResearchList = researchForUserRetrieveUtil.getAllResearchForUser(userId);
		boolean containsUserResearch = false;
		for(ResearchForUser rfu : userResearchList) {
			if(rfu.getId().equals(userResearchUuid)) {
				containsUserResearch = true;
			}
		}
		return containsUserResearch;
	}
	
	private boolean verifyEnoughGems(Builder resBuilder) {
		int userGems = user.getGems();

		//check if user can afford to buy however many more user wants to buy
		if (userGems < gemsSpent) {
			resBuilder.setStatus(PerformResearchStatus.FAIL_INSUFFICIENT_GEMS);
			return false; 
		}
		else return true;
	}
	
	private boolean verifyEnoughResource(Builder resBuilder) {
		if(resourceType == ResourceType.CASH) {
			int userCash = user.getCash();
			if(userCash < resourceChange) {
				resBuilder.setStatus(PerformResearchStatus.FAIL_INSUFFICIENT_CASH);
				return false;
			}
		}
		else if(resourceType == ResourceType.OIL) {
			int userOil = user.getOil();
			if(userOil < resourceChange) {
				resBuilder.setStatus(PerformResearchStatus.FAIL_INSUFFICIENT_OIL);
				return false;
			}
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();
		boolean successfulInsertOrUpdate = false;
		String userResearchId = "";
		Timestamp timeOfPurchase = new Timestamp(now.getTime());
		
		if(userResearchUuid != null) {
			successfulInsertOrUpdate = updateUtil.updateUserResearch(userResearchUuid, research.getId(), timeOfPurchase);
		}
		else {
			boolean isComplete = false;
			userResearchId = insertUtil.insertUserResearch(userId, research, timeOfPurchase, isComplete);
			if(!userResearchId.equals("")) {
				successfulInsertOrUpdate = true;
				userResearchUuid = userResearchId;
			}
		}
		
		if(!successfulInsertOrUpdate) {
			return false;
		}
		
		if(!(gemsSpent > 0) && (resourceType != ResourceType.CASH) && (resourceType != ResourceType.OIL)) {
			resBuilder.setStatus(PerformResearchStatus.FAIL_OTHER);
			log.error("not being purchased with gems, cash, or oil, what is this voodoo shit");
			return false;
		}

		int gemsChange = 0;
		int cashChange = 0;
		int oilChange = 0;
		int expChange = 0;
		
		if(gemsSpent > 0) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
			gemsChange = -1*gemsSpent;
		}

		if (resourceType == ResourceType.CASH) {
			prevCurrencies.put(MiscMethods.cash, user.getCash());
			cashChange = -1*resourceChange;
		}
		else if (resourceType == ResourceType.OIL){
			prevCurrencies.put(MiscMethods.oil, user.getOil());
			oilChange = -1*resourceChange;
		}
		
//		user.updateRelativeGemsCashOilExperienceNaive(gemsChange, cashChange, oilChange, expChange);
		
		updateUserCurrency();
		
		prepCurrencyHistory();

		return true;
	}

	private void updateUserCurrency()
	{
		int gemsDelta = -1 * gemsSpent;
		int resourceDelta = -1* resourceChange;
		
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

		if (0 != gemsSpent) {
			currencyDeltas.put(gems, gemsSpent);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
			detailSb.append(" gemsSpent=");
			detailSb.append(gemsSpent);
			details.put(gems, detailSb.toString());
		}
		
		if(resourceChange>0) {
			if(resourceType == ResourceType.CASH) {
				currencyDeltas.put(cash, resourceChange);
				curCurrencies.put(cash, user.getCash());
				reasonsForChanges.put(cash, ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
				detailSb2.append(" cash spent= ");
				detailSb2.append(resourceChange);
				details.put(cash, detailSb2.toString());

			}
			else if(resourceType == ResourceType.OIL) {
				currencyDeltas.put(oil, resourceChange);
				curCurrencies.put(oil, user.getOil());
				reasonsForChanges.put(oil, ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
				detailSb2.append(" oil spent= ");
				detailSb2.append(resourceChange);
				details.put(oil, detailSb2.toString());
			}
		}
	}

	public User getUser() {
		return user;
	}
	
	public Research getResearch() {
		return research;
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
