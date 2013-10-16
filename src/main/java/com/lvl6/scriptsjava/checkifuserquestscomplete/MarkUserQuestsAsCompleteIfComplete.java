package com.lvl6.scriptsjava.checkifuserquestscomplete;

import java.util.List;
import java.util.Map;

import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.jobs.QuestJobBuildStruct;
import com.lvl6.info.jobs.QuestJobUpgradeStruct;
import com.lvl6.retrieveutils.rarechange.QuestJobBuildStructRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobUpgradeStructRetrieveUtils;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

public class MarkUserQuestsAsCompleteIfComplete {

	public static void main(String[] args) {
		//BasicConfigurator.configure();
		DBConnection.get().init();
		List<QuestForUser> userQuests = RetrieveUtils.questForUserRetrieveUtils().getUnredeemedIncompleteUserQuests();
		for (QuestForUser userQuest : userQuests) {
			Quest quest = QuestRetrieveUtils.getQuestForQuestId(userQuest.getQuestId());
			if (null == quest) {
				continue;
			}
			if (userQuest != null && userQuest.isTasksComplete() && userQuest.isDefeatTypeJobsComplete()) {
				List<Integer> buildStructJobsRequired = quest.getBuildStructJobsRequired();
				List<Integer> upgradeStructJobsRequired = quest.getUpgradeStructJobsRequired();

				if ((buildStructJobsRequired != null && buildStructJobsRequired.size()>0) || 
						(upgradeStructJobsRequired != null && upgradeStructJobsRequired.size()>0)) {
					Map<Integer, List<StructureForUser>> structIdsToUserStructs = RetrieveUtils.userStructRetrieveUtils().getStructIdsToUserStructsForUser(userQuest.getUserId());
					if (structIdsToUserStructs == null || structIdsToUserStructs.size() <= 0) {
						continue;
					}
					if (buildStructJobsRequired != null && buildStructJobsRequired.size()>0) {
						Map<Integer, QuestJobBuildStruct> bsjs = QuestJobBuildStructRetrieveUtils.getBuildStructJobsForBuildStructJobIds(buildStructJobsRequired);
						for (QuestJobBuildStruct bsj : bsjs.values()) {
							int quantityBuilt = 0;
							if (structIdsToUserStructs.get(bsj.getStructId()) != null) {
								for (StructureForUser us : structIdsToUserStructs.get(bsj.getStructId())) {
									if (us.getLastRetrieved() != null) {
										quantityBuilt++;
									}
								}
							}
							if (quantityBuilt <  bsj.getQuantity()) {
								continue;
							} 
						}
					}

					if (upgradeStructJobsRequired != null && upgradeStructJobsRequired.size()>0) {
						Map<Integer, QuestJobUpgradeStruct> usjs = QuestJobUpgradeStructRetrieveUtils.getUpgradeStructJobsForUpgradeStructJobIds(upgradeStructJobsRequired);
						for (QuestJobUpgradeStruct usj : usjs.values()) {
							if (structIdsToUserStructs.get(usj.getStructId()) == null) {
								continue;
							}
							boolean usjComplete = false;
							for (StructureForUser us : structIdsToUserStructs.get(usj.getStructId())) {
								if (us.getLevel() >= usj.getLevelReq()) {
									usjComplete = true;
								}
							}
							if (!usjComplete) {
								continue;
							}
						}
					}
				}
				if (!userQuest.isComplete() && !UpdateUtils.get().updateUserQuestIscomplete(userQuest.getUserId(), userQuest.getQuestId())) {
					System.out.println("problem with marking user quest as complete");
				} else {
					System.out.println("successfull changed user quest");
				}
			}
		}
	}

}
