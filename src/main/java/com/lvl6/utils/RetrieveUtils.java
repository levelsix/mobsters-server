package com.lvl6.utils;

import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.QuestForUserRetrieveUtils2;
import com.lvl6.retrieveutils.StatisticsRetrieveUtil;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.spring.AppContext;

public class RetrieveUtils {

	public static UserRetrieveUtils2 userRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				UserRetrieveUtils2.class);
	}

	public static UserClanRetrieveUtils2 userClanRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				UserClanRetrieveUtils2.class);
	}

	public static MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				MonsterForUserRetrieveUtils2.class);
	}

	public static QuestForUserRetrieveUtils2 questForUserRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				QuestForUserRetrieveUtils2.class);
	}

	public static StructureForUserRetrieveUtils2 userStructRetrieveUtils() {
		return AppContext.getApplicationContext().getBean(
				StructureForUserRetrieveUtils2.class);
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
