package com.lvl6.server.controller.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.AchievementForUser;
import com.lvl6.proto.AchievementStuffProto.UserAchievementProto;

@Component
public class AchievementStuffUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public Map<Integer, UserAchievementProto> achievementIdsToUap(
			Collection<UserAchievementProto> uapCollection) {

		Map<Integer, UserAchievementProto> achievementIdToUap = new HashMap<Integer, UserAchievementProto>();

		if (null == uapCollection || uapCollection.isEmpty()) {
			log.info("achievementIdsToUap() collection of"
					+ " UserAchievementProto is empty, not mapifying");
			return achievementIdToUap;
		}

		for (UserAchievementProto uap : uapCollection) {
			int achievementId = uap.getAchievementId();

			achievementIdToUap.put(achievementId, uap);
		}
		return achievementIdToUap;
	}

	public Map<Integer, AchievementForUser> javafyUserAchievementProto(
			Map<Integer, UserAchievementProto> achievementIdsToUap) {
		Map<Integer, AchievementForUser> achievementIdToAfu = new HashMap<Integer, AchievementForUser>();

		for (Integer achievementId : achievementIdsToUap.keySet()) {
			UserAchievementProto uap = achievementIdsToUap.get(achievementId);

			AchievementForUser afu = new AchievementForUser();

			afu.setAchievementId(uap.getAchievementId());
			afu.setProgress(uap.getProgress());
			afu.setComplete(uap.getIsComplete());
			afu.setRedeemed(uap.getIsRedeemed());

			achievementIdToAfu.put(achievementId, afu);
		}

		return achievementIdToAfu;
	}
}
