package com.lvl6.server.controller.actionobjects;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lvl6.info.ResearchForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchResponseProto.Builder;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchResponseProto.FinishPerformingResearchStatus;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class FinishPerformingResearchAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());


	
	private String userId;
	private User user;
	private String userResearchUuid; //update the row when researchs are upgraded
	private int gemsSpent;
	private Date now;
	protected UpdateUtil updateUtil;
	private ResearchForUserRetrieveUtils researchForUserRetrieveUtil;


	public FinishPerformingResearchAction(
		String userId,
		User user,
		String userResearchUuid,
		int gemsSpent,
		Date now,
		UpdateUtil updateUtil,
		ResearchForUserRetrieveUtils researchForUserRetrieveUtil)
	{
		super();
		this.userId = userId;
		this.user = user;
		this.userResearchUuid = userResearchUuid;
		this.gemsSpent = gemsSpent;
		this.now = now;
		this.updateUtil = updateUtil;
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}

	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	
	public void execute(Builder resBuilder) {
		resBuilder.setStatus(FinishPerformingResearchStatus.FAIL_OTHER);
		
		boolean valid = false;
		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(FinishPerformingResearchStatus.SUCCESS);
	}

	private boolean verifySemantics(Builder resBuilder) {
		if (null == user) {
			resBuilder.setStatus(FinishPerformingResearchStatus.FAIL_OTHER);
			log.error( "no user with id={}", userId );
			return false;
		}
		
		boolean userHasUserResearch = false;
		if(userResearchUuid != null) {
			userHasUserResearch = verifyUserResearch(resBuilder);
		}
		
		if(!userHasUserResearch) {
			log.error("user does not have this research with userresearchid={}", userResearchUuid);
			return false;
		}
		
		boolean hasEnoughGems = true;
		
		if(gemsSpent > 0) {
			hasEnoughGems = verifyEnoughGems(resBuilder);
		}

		if(hasEnoughGems) {
			return true;
		}
		else return false;
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
			resBuilder.setStatus(FinishPerformingResearchStatus.FAIL_NOT_ENOUGH_GEMS);
			return false; 
		}
		else return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();
		boolean successfulInsertOrUpdate = false;

		successfulInsertOrUpdate = updateUtil.updateUserResearchCompleteStatus(userResearchUuid);

		if(!successfulInsertOrUpdate) {
			return false;
		}

		if(gemsSpent > 0) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
			updateUserCurrency();
			prepCurrencyHistory();
		}
		return true;
	}

	private void updateUserCurrency()
	{
		int gemsDelta = -1 * gemsSpent;
		int resourceDelta = 0; //cant use resources only gems
		ResourceType resourceType = null;
		
		boolean updated = user.updateGemsandResourcesFromPerformingResearch(gemsDelta, resourceDelta, resourceType);
		log.info("updated, user paid to complete research {}", updated);
	}
	
	private void prepCurrencyHistory()
	{
		String gems = MiscMethods.gems;
		
		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		StringBuilder detailSb = new StringBuilder();
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