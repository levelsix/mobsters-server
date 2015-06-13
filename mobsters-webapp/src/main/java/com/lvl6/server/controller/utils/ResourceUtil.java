package com.lvl6.server.controller.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.CreateClanStatus;

@Component
public class ResourceUtil {


	private static final Logger log = LoggerFactory
			.getLogger(ResourceUtil.class);

	/**
	 *
	 * @param user - Need access to whatever resource is specified by resourceStr.
	 * @param maxResource - Maximum amount of resources a user can have.
	 * @param minResource - Minimum amount of resources a user needs to be left with.
	 * @param resourceChange
	 * @param gainResources
	 * @param resourceStr
	 * @return A signed number.
	 */
	public int calculateMaxResourceChange(User user, int maxResource,
			int minResource, int resourceChange,
			boolean gainResources, String resourceStr)
	{
		if (null == user) {
			log.info("calculateMaxResourceChange user is null! resourceChange=0 {}",
					resourceStr);
			//this is for fake user
			return 0;
		}
		//if user somehow has more than max cash, first treat user as having max cash,
		//figure out the amount he gains and then subtract the extra cash he had
		int resource;
		if (resourceStr.equalsIgnoreCase(MiscMethods.cash)) {
			resource = user.getCash();
		} else {
			resource = user.getOil();
		}
		int amountOverMax = calculateAmountOverMaxResource(user, resource,
				maxResource, resourceStr);
		if (amountOverMax > 0) {
			log.info("calculateMaxResourceChange amount over max={}",
					amountOverMax);
		}

		if (gainResources) {
			return calculateMaxResourceGained(user, maxResource,
					resourceChange, resourceStr, resource, amountOverMax);
		} else {
			return calculateMaxResourceLost(user, maxResource, minResource,
					resourceChange, resource, amountOverMax);
		}
	}

	private int calculateAmountOverMaxResource(User u, int userResource,
			int maxResource, String resource) {
//		log.info("calculateAmountOverMaxResource resource={}",
//				resource);
		int resourceLoss = 0;
		if (userResource > maxResource) {
			log.info("wtf!!!!! user has more than max {}! user={}\t cutting him down to maxResource={}",
					new Object[] { resource, u, maxResource } );
			resourceLoss = userResource - maxResource;
		}
		return resourceLoss;
	}

	private int calculateMaxResourceGained(User user, int maxResource,
			int resourceChange, String resourceStr,
			int resource, int amountOverMax)
	{
		log.info("calculateMaxResourceChange userWon!");
		int curResource = Math.min(resource, maxResource); //in case user's cash is more than maxOil.
		log.info("calculateMaxResourceChange curResource={}", curResource);
		int maxResourceUserCanGain = maxResource - curResource;
		log.info("calculateMaxResourceChange  maxResourceUserCanGain={}",
				maxResourceUserCanGain);
		int maxResourceChange = Math.min(resourceChange, maxResourceUserCanGain);
		log.info("calculateMaxResourceChange maxResourceChange={}",
				maxResourceChange);

		//IF USER IS ABOVE maxResource, need to drag him down to maxResource
		int actualResourceChange = maxResourceChange - amountOverMax;
		log.info( "calculateMaxResourceChange  actualResourceChange={}",
				actualResourceChange );
		return actualResourceChange;
	}

	private int calculateMaxResourceLost(User user, int maxResource,
			int minResource, int resourceChange, int resource, int amountOverMax)
	{
		log.info("calculateMaxResourceLost userLost!.");
		int maxResourceUserCanLose = Math.min(resource, maxResource);
		//before user can lose all his resources. now he can only
		//lose an amount that won't allow him to go below his min limit
		maxResourceUserCanLose = maxResourceUserCanLose - minResource;
		maxResourceUserCanLose = Math.max(maxResourceUserCanLose, 0);

		//always non negative number
		int maxResourceChange = Math.min(resourceChange, maxResourceUserCanLose);

		int actualResourceChange = -1 * (amountOverMax + maxResourceChange);
		log.info( "calculateMaxResourceChange  actualResourceChange={}",
				actualResourceChange );
		return actualResourceChange;
	}

	public boolean hasEnoughCash(User user, int cashChange) {
		//if user's aggregate cash is < cost, don't allow transaction
		int userCash = user.getCash();

		//since negative resourceChange means charge, then negative of that is
		//the cost. If resourceChange is positive, meaning refund, user will always
		//have more than a negative amount
		int cashRequired = -1 * cashChange;
		if (userCash < cashRequired) {
			log.error("not enough cash. userCash={}\t cashChange={}",
					userCash, cashChange);
			return false;
		}
		return true;
	}

	public boolean hasEnoughOil(User user, int oilChange) {
		//if user's aggregate oil is < cost, don't allow transaction
		int userOil = user.getOil();

		//since negative resourceChange means charge, then negative of that is
		//the cost. If resourceChange is positive, meaning refund, user will always
		//have more than a negative amount
		int oilRequired = -1 * oilChange;
		if (userOil < oilRequired) {
			log.error("not enough oil. userOil={}\t oilChange={}",
					userOil, oilChange);
			return false;
		}
		return true;
	}

	public boolean hasEnoughGachaCredits(User user, int gachaCreditsChange) {
		//if user's aggregate gachaCredits is < cost, don't allow transaction
		int userGachaCredits = user.getGachaCredits();

		//since negative resourceChange means charge, then negative of that is
		//the cost. If resourceChange is positive, meaning refund, user will always
		//have more than a negative amount
		int gachaCreditsRequired = -1 * gachaCreditsChange;
		if (userGachaCredits < gachaCreditsRequired) {
			log.error("not enough gachaCredits. userGachaCredits={}\t gachaCreditsChange={}",
					userGachaCredits, gachaCreditsChange);
			return false;
		}
		return true;
	}

	public boolean hasEnoughGems(User user, int gemsSpent) {
		if (gemsSpent > 0) {
			int userGems = user.getGems();
			//check if user can afford to buy however many more user wants to buy
			if (userGems < gemsSpent) {
				log.error("not enough gems. userGems={}\t gemsSpent={}",
						userGems, gemsSpent);
				return false;
			}
		}
		return true;
	}



}
