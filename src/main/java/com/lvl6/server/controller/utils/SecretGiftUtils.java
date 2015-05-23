package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.SecretGift;
import com.lvl6.info.SecretGiftForUser;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.rarechange.SecretGiftRetrieveUtils;

@Component
@DependsOn("gameServer")
public class SecretGiftUtils {

	@Autowired
	protected SecretGiftRetrieveUtils secretGiftRetrieveUtils;

	private static final Logger log = LoggerFactory
			.getLogger(MiscMethods.class);


	public List<SecretGiftForUser> calculateGiftsForUser(
			String userId, int numGifts, long now) {
		List<SecretGiftForUser> gifts = new ArrayList<SecretGiftForUser>();

		//random to select an item
		Random rand = ControllerConstants.RAND;

		//chi random to calculate seconds till collection
		ChiSquaredDistribution randChi = ControllerConstants.SECRET_GIFT_FOR_USER__RANDOM;

		//(round((chisq(df = 4)^3) * 6.5)+329)
		for (int giftI = 0; giftI < numGifts; giftI++) {
			float randFloat = rand.nextFloat();
			SecretGift secretGift = secretGiftRetrieveUtils.nextGift(randFloat);

			SecretGiftForUser isgfu = new SecretGiftForUser();
			isgfu.setUserId(userId);
			isgfu.setRewardId(secretGift.getRewardId());

			//so the client knows which item came first
			Date newTime = new Date(now + giftI * 1000);
			isgfu.setCreateTime(newTime);

			double randDoub = randChi.sample();
			log.info("randDoub={}", randDoub);

			randDoub = Math.pow(randDoub, 3D);
			log.info("(randDoub ^ 3)={}", randDoub);

			double waitTimeSecs = randDoub * 6.5 + 329;
			log.info("uncapped waitTimeSecs={}", waitTimeSecs);

			//(round((chisq(df = 4)^3) * 6.5)+329)
			waitTimeSecs = Math
					.min(waitTimeSecs,
							ControllerConstants.SECRET_GIFT_FOR_USER__MAX_SECS_WAIT_TIME);
			//			waitTimeSecs = Math.max(waitTimeSecs, ControllerConstants
			//				.ITEM_SECRET_GIFT_FOR_USER__MIN_SECS_WAIT_TIME);
			log.info("capped waitTimeSecs={}", waitTimeSecs);

			isgfu.setSecsTillCollection((int) Math.round(waitTimeSecs));

			log.info("gift={}", isgfu);

			gifts.add(isgfu);
		}

		return gifts;
	}

}
