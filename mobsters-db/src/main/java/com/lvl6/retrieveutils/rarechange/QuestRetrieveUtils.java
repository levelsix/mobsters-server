package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Dialogue;
import com.lvl6.info.Quest;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.QuestGraph;

@Component
@DependsOn("gameServer")
public class QuestRetrieveUtils {

	
	private static final Logger log = LoggerFactory.getLogger(QuestRetrieveUtils.class);


	//private static Map<Integer, List<Quest>> cityIdToQuests;
	private static Map<Integer, Quest> questIdsToQuests;
	private static QuestGraph questGraph;

	private static final String TABLE_NAME = DBConstants.TABLE_QUEST_CONFIG;

	public Map<Integer, Quest> getQuestIdsToQuests() {
		log.debug("retrieving all quest data");
		if (questIdsToQuests == null) {
			setStaticQuestIdsToQuests();
		}
		return questIdsToQuests;
	}

	public Quest getQuestForQuestId(int questId) {
		log.debug("retrieving quest with questId " + questId);
		if (questIdsToQuests == null) {
			setStaticQuestIdsToQuests();
		}
		return questIdsToQuests.get(questId);
	}

	public QuestGraph getQuestGraph() {
		log.debug("retrieving quest graph");
		if (questGraph == null) {
			setStaticQuestGraph();
		}
		return questGraph;
	}

	private void setStaticQuestIdsToQuests() {
		log.debug("setting static map of questIds to quests");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Quest> tmp = new HashMap<Integer, Quest>();
						while (rs.next()) {
							Quest quest = convertRSRowToQuest(rs);
							if (quest != null) {
								tmp.put(quest.getId(), quest);
							}
						}
						questIdsToQuests = tmp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("quest retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private void setStaticQuestGraph() {
		log.debug("setting static quest graph");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						List<Quest> quests = new ArrayList<Quest>();
						while (rs.next()) {  //should only be one
							Quest quest = convertRSRowToQuest(rs);
							if (quest != null) {
								quests.add(quest);
							}
						}
						QuestGraph tmp = new QuestGraph(quests);
						questGraph = tmp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("quest retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		//setStaticCityIdsToQuests();
		setStaticQuestGraph();
		setStaticQuestIdsToQuests();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private Quest convertRSRowToQuest(ResultSet rs) throws SQLException {
		String delimiter = ",";

		int id = rs.getInt(DBConstants.QUEST__ID);
		//    int cityId = rs.getInt(DBConstants.);
		String questName = rs.getString(DBConstants.QUEST__QUEST_NAME);
		String description = rs.getString(DBConstants.QUEST__DESCRIPTION);
		String doneResponse = rs.getString(DBConstants.QUEST__DONE_RESPONSE);

		String acceptDialogueBlob = rs
				.getString(DBConstants.QUEST__ACCEPT_DIALOGUE);
		
		//TODO: Fix this
		Dialogue acceptDialogue = null;/* = miscMethods
				.createDialogue(acceptDialogueBlob);*/

		//    String questType = rs.getString(DBConstants.);
		//    String jobDescription = rs.getString(DBConstants.);
		//    int staticDataId = rs.getInt(DBConstants.);
		//    int quantity = rs.getInt(DBConstants.);
		int cashReward = rs.getInt(DBConstants.QUEST__CASH_REWARD);
		int oilReward = rs.getInt(DBConstants.QUEST__OIL_REWARD);
		int gemReward = rs.getInt(DBConstants.QUEST__GEM_REWARD);
		int expReward = rs.getInt(DBConstants.QUEST__EXP_REWARD);
		int monsterIdReward = rs.getInt(DBConstants.QUEST__MONSTER_ID_REWARD);
		boolean isCompleteMonster = rs
				.getBoolean(DBConstants.QUEST__IS_COMPLETE_MONSTER);

		String questsRequiredForThisString = rs
				.getString(DBConstants.QUEST__QUESTS_REQUIRED_FOR_THIS);
		List<Integer> questsRequiredForThis = new ArrayList<Integer>();
		if (questsRequiredForThisString != null) {
			//TODO: Fix this
			/*miscMethods.explodeIntoInts(questsRequiredForThisString, delimiter,
					questsRequiredForThis);*/
		}

		String questGiverName = rs
				.getString(DBConstants.QUEST__QUEST_GIVER_NAME);
		String questGiverImagePrefix = rs
				.getString(DBConstants.QUEST__QUEST_GIVER_IMAGE_PREFIX);
		int priority = rs.getInt(DBConstants.QUEST__PRIORITY);
		String carrotId = rs.getString(DBConstants.QUEST__CARROT_ID);
		//    boolean isAchievement = rs.getBoolean(DBConstants.);
		String monsterElement = rs
				.getString(DBConstants.QUEST__MONSTER_ELEMENT);

		if (null != monsterElement) {
			String newMonsterElement = monsterElement.trim().toUpperCase();
			if (!monsterElement.equals(newMonsterElement)) {
				log.error(String.format("monsterElement incorrect: %s, id=%s",
						monsterElement, id));
				monsterElement = newMonsterElement;
			}
		}

		Quest quest = new Quest(id, questName, description, doneResponse,
				acceptDialogue, cashReward, oilReward, gemReward, expReward,
				monsterIdReward, isCompleteMonster, questsRequiredForThis,
				questGiverName, questGiverImagePrefix, priority, carrotId,
				monsterElement);

		return quest;
	}

}
