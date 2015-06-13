package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.info.Item;
import com.lvl6.info.ItemSecretGiftForUser;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;

@Component

public class SecretGiftUtils {

	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils;
	
	private static final Logger log = LoggerFactory
			.getLogger(MiscMethods.class);
	
	
	public List<ItemSecretGiftForUser> calculateGiftsForUser(
			String userId, int numGifts, long now) {
		List<ItemSecretGiftForUser> gifts = new ArrayList<ItemSecretGiftForUser>();

		//random to select an item
		Random rand = ControllerConstants.RAND;

		//chi random to calculate seconds till collection
		ChiSquaredDistribution randChi = ControllerConstants.ITEM_SECRET_GIFT_FOR_USER__RANDOM;

		//(round((chisq(df = 4)^3) * 6.5)+329)
		for (int giftI = 0; giftI < numGifts; giftI++) {
			float randFloat = rand.nextFloat();
			Item secretGift = itemRetrieveUtils.nextItem(randFloat);

			ItemSecretGiftForUser isgfu = new ItemSecretGiftForUser();
			isgfu.setUserId(userId);
			isgfu.setItemId(secretGift.getId());

			//so the client knows which item came first
			Date newTime = new Date(now + giftI * 1000);
			isgfu.setCreateTime(newTime);

			double randDoub = randChi.sample();
			log.info(String.format("randDoub=%s", randDoub));

			randDoub = Math.pow(randDoub, 3D);
			log.info(String.format("(randDoub ^ 3)=%s", randDoub));

			double waitTimeSecs = randDoub * 6.5 + 329;
			log.info(String.format("uncapped waitTimeSecs=%s", waitTimeSecs));

			//(round((chisq(df = 4)^3) * 6.5)+329)
			waitTimeSecs = Math
					.min(waitTimeSecs,
							ControllerConstants.ITEM_SECRET_GIFT_FOR_USER__MAX_SECS_WAIT_TIME);
			//			waitTimeSecs = Math.max(waitTimeSecs, ControllerConstants
			//				.ITEM_SECRET_GIFT_FOR_USER__MIN_SECS_WAIT_TIME);
			log.info(String.format("capped waitTimeSecs=%s", waitTimeSecs));

			isgfu.setSecsTillCollection((int) Math.round(waitTimeSecs));

			log.info(String.format("gift=%s", isgfu));

			gifts.add(isgfu);
		}

		return gifts;
	}
	
	
}
