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
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.QuestGraph;

@Component @DependsOn("gameServer") public class QuestRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  //private static Map<Integer, List<Quest>> cityIdToQuests;
  private static Map<Integer, Quest> questIdsToQuests;
  private static QuestGraph questGraph;

  private static final String TABLE_NAME = DBConstants.TABLE_QUEST;

  public static Map<Integer, Quest> getQuestIdsToQuests() {
    log.debug("retrieving all quest data");
    if (questIdsToQuests == null) {
      setStaticQuestIdsToQuests();
    }
    return questIdsToQuests;
  }

//  public static List<Quest> getQuestsInCity(int cityId) {
//    log.debug("retrieving all quest data");
//    if (questIdsToQuests == null) {
//      setStaticQuestIdsToQuests();
//    }
//    List <Quest> questsInCity = new ArrayList<Quest>();
//    for (Integer questId : questIdsToQuests.keySet()) {
//      Quest q = questIdsToQuests.get(questId);
//      if (q.getCityId() == cityId) {
//        questsInCity.add(q);
//      }
//    }
//    return questsInCity;
//  }

  public static Quest getQuestForQuestId(int questId) {
    log.debug("retrieving quest with questId " + questId);
    if (questIdsToQuests == null) {
      setStaticQuestIdsToQuests();
    }
    return questIdsToQuests.get(questId);
  }

  public static QuestGraph getQuestGraph() {
    log.debug("retrieving quest graph");
    if (questGraph == null) {
      setStaticQuestGraph();
    }
    return questGraph;
  }

  private static void setStaticQuestIdsToQuests() {
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
			      while(rs.next()) {
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

  private static void setStaticQuestGraph() {
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
			      while(rs.next()) {  //should only be one
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

  public static void reload() {
    //setStaticCityIdsToQuests();
    setStaticQuestGraph();
    setStaticQuestIdsToQuests();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Quest convertRSRowToQuest(ResultSet rs) throws SQLException {
    String delimiter = ", ";

    int i = 1;
    int id = rs.getInt(i++);
    int cityId = rs.getInt(i++);
    String questName = rs.getString(i++);
    String description = rs.getString(i++);
    String doneResponse = rs.getString(i++);

    String acceptDialogueBlob = rs.getString(i++);
    Dialogue acceptDialogue = MiscMethods.createDialogue(acceptDialogueBlob);

    String questType = rs.getString(i++);
    String jobDescription = rs.getString(i++);
    int staticDataId = rs.getInt(i++);
    int quantity = rs.getInt(i++);
    int cashReward = rs.getInt(i++);
    int oilReward = rs.getInt(i++);
    int gemReward = rs.getInt(i++);
    int expReward = rs.getInt(i++);
    int monsterIdReward = rs.getInt(i++);
    boolean isCompleteMonster = rs.getBoolean(i++);
    
    String questsRequiredForThisString = rs.getString(i++);
    List<Integer> questsRequiredForThis = new ArrayList<Integer>();
    if (questsRequiredForThisString != null) {
      MiscMethods.explodeIntoInts(questsRequiredForThisString,
      		delimiter, questsRequiredForThis);
    }

    String questGiverName = rs.getString(i++);
    String questGiverImagePrefix = rs.getString(i++);
    int priority = rs.getInt(i++);
    String carrotId = rs.getString(i++);
    boolean isAchievement = rs.getBoolean(i++);
    String monsterElement = rs.getString(i++);
    
    Quest quest = new Quest(id, cityId, questName, description, doneResponse,
    		acceptDialogue, questType, jobDescription, staticDataId, quantity,
    		cashReward, oilReward, gemReward, expReward, monsterIdReward,
    		isCompleteMonster, questsRequiredForThis, questGiverName,
    		questGiverImagePrefix, priority, carrotId, isAchievement,
    		monsterElement); 
    
    return quest;
  }

}
