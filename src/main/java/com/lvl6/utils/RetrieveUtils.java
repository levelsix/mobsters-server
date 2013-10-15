package com.lvl6.utils;

import com.lvl6.retrieveutils.StatisticsRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserQuestRetrieveUtils;
import com.lvl6.retrieveutils.UserQuestsCompletedDefeatTypeJobsRetrieveUtils;
import com.lvl6.retrieveutils.UserQuestsCompletedTasksRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.retrieveutils.UserStructRetrieveUtils;
import com.lvl6.spring.AppContext;

public class RetrieveUtils {

	public static UserRetrieveUtils userRetrieveUtils() {
		return AppContext.getApplicationContext()
				.getBean(UserRetrieveUtils.class);
	}

	public static UserClanRetrieveUtils userClanRetrieveUtils() {
		return AppContext.getApplicationContext()
				.getBean(UserClanRetrieveUtils.class);
	}

	public static MonsterForUserRetrieveUtils monsterForUserRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				MonsterForUserRetrieveUtils.class);
	}
	
	public static UserQuestRetrieveUtils userQuestRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				UserQuestRetrieveUtils.class);
	}

	public static UserStructRetrieveUtils userStructRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				UserStructRetrieveUtils.class);
	}

	public static UserQuestsCompletedDefeatTypeJobsRetrieveUtils userQuestsCompletedDefeatTypeJobsRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				UserQuestsCompletedDefeatTypeJobsRetrieveUtils.class);
	}
	
	public static UserQuestsCompletedTasksRetrieveUtils userQuestsCompletedTasksRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				UserQuestsCompletedTasksRetrieveUtils.class);
	}
	
	public static StatisticsRetrieveUtil statisticsRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				StatisticsRetrieveUtil.class);
	}
}
