package com.lvl6.utils;

import com.lvl6.retrieveutils.StatisticsRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils;
import com.lvl6.retrieveutils.QuestForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils;
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
	
	public static QuestForUserRetrieveUtils questForUserRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				QuestForUserRetrieveUtils.class);
	}

	public static StructureForUserRetrieveUtils userStructRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				StructureForUserRetrieveUtils.class);
	}
//	
//	public static QuestTaskHistoryRetrieveUtils userQuestsCompletedTasksRetrieveUtils() {
//		return AppContext.getApplicationContext().getBean(
//				QuestTaskHistoryRetrieveUtils.class);
//	}
//	
	public static StatisticsRetrieveUtil statisticsRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				StatisticsRetrieveUtil.class);
	}
}
