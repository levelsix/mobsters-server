package com.lvl6.utils.utilmethods;

import java.util.List;

import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.utils.QuestGraph;

public class QuestUtils {


//	private static final Logger log = LoggerFactory.getLogger(QuestUtils.class);

  public static List<Integer> getAvailableQuestsForUser(List<Integer> redeemed, List<Integer> inProgress) {
    QuestGraph graph = QuestRetrieveUtils.getQuestGraph();
    return graph.getQuestsAvailable(redeemed, inProgress);
  }


}
