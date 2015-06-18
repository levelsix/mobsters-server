package com.lvl6.server.controller.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftForUserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.rarechange.SecretGiftRetrieveUtils;

@Component

public class SecretGiftUtils {

	@Autowired
	protected SecretGiftRetrieveUtils secretGiftRetrieveUtils;

	private static final Logger log = LoggerFactory
			.getLogger(MiscMethods.class);


	public List<SecretGiftForUserPojo> calculateGiftsForUser(
			String userId, int numGifts, long now) {
		List<SecretGiftForUserPojo> gifts = new ArrayList<SecretGiftForUserPojo>();

		//random to select an item
		Random rand = ControllerConstants.RAND;

		//chi random to calculate seconds till collection
		ChiSquaredDistribution randChi = ControllerConstants.SECRET_GIFT_FOR_USER__RANDOM;

		//(round((chisq(df = 4)^3) * 6.5)+329)
		for (int giftI = 0; giftI < numGifts; giftI++) {
			float randFloat = rand.nextFloat();
			SecretGiftConfigPojo secretGift = secretGiftRetrieveUtils.nextSecretGift(randFloat);

			SecretGiftForUserPojo sgfu = new SecretGiftForUserPojo();
			sgfu.setUserId(userId);
			sgfu.setRewardId(secretGift.getRewardId());

			//so the client knows which item came first
			Timestamp newTime = new Timestamp(now + giftI * 1000);
			sgfu.setCreateTime(newTime);

			double randDoub = randChi.sample();
			log.debug("randDoub={}", randDoub);

			randDoub = Math.pow(randDoub, 3D);
			log.debug("(randDoub ^ 3)={}", randDoub);

			double waitTimeSecs = randDoub * 6.5D + 329D;
			log.debug("uncapped waitTimeSecs={}", waitTimeSecs);

			//(round((chisq(df = 4)^3) * 6.5)+329)
			waitTimeSecs = Math
					.min(waitTimeSecs,
							ControllerConstants.SECRET_GIFT_FOR_USER__MAX_SECS_WAIT_TIME);
			//			waitTimeSecs = Math.max(waitTimeSecs, ControllerConstants
			//				.SECRET_GIFT_FOR_USER__MIN_SECS_WAIT_TIME);
			log.debug("capped waitTimeSecs={}", waitTimeSecs);

			sgfu.setSecsUntilCollection((int) Math.round(waitTimeSecs));

			log.info("SecretGift={}", sgfu);

			gifts.add(sgfu);
		}

		return gifts;
	}


}
