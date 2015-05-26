package com.lvl6.utils.utilmethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.QuestJobForUser;
import com.lvl6.proto.QuestProto.UserQuestJobProto;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.utils.QuestGraph;

@Component
@DependsOn("gameServer")
public class QuestUtils {

	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils;
	
	
	//	private static final Logger log = LoggerFactory.getLogger(QuestUtils.class);

	public List<Integer> getAvailableQuestsForUser(
			List<Integer> redeemed, List<Integer> inProgress) {
		QuestGraph graph = questRetrieveUtils.getQuestGraph();
		return graph.getQuestsAvailable(redeemed, inProgress);
	}

	public static Map<Integer, UserQuestJobProto> mapifyByQuestJobId(
			List<UserQuestJobProto> userQuestJobs) {
		Map<Integer, UserQuestJobProto> questJobIdToUserQuestJob = new HashMap<Integer, UserQuestJobProto>();

		for (UserQuestJobProto uqjp : userQuestJobs) {
			int questJobId = uqjp.getQuestJobId();

			questJobIdToUserQuestJob.put(questJobId, uqjp);
		}

		return questJobIdToUserQuestJob;
	}

	public static Map<Integer, QuestJobForUser> deserializeUserQuestJobProto(
			Map<Integer, UserQuestJobProto> questJobIdToUserQuestJobProto) {
		Map<Integer, QuestJobForUser> questJobIdToQuestJobForUser = new HashMap<Integer, QuestJobForUser>();

		for (Integer questJobId : questJobIdToUserQuestJobProto.keySet()) {
			UserQuestJobProto uqjp = questJobIdToUserQuestJobProto
					.get(questJobId);
			QuestJobForUser qjfu = deserializeUserQuestJobProto(uqjp);

			questJobIdToQuestJobForUser.put(questJobId, qjfu);
		}

		return questJobIdToQuestJobForUser;
	}

	public static QuestJobForUser deserializeUserQuestJobProto(
			UserQuestJobProto uqjp) {
		QuestJobForUser qjfu = new QuestJobForUser();

		qjfu.setQuestId(uqjp.getQuestId());
		qjfu.setQuestJobId(uqjp.getQuestJobId());
		qjfu.setComplete(uqjp.getIsComplete());
		qjfu.setProgress(uqjp.getProgress());

		return qjfu;
	}
}
