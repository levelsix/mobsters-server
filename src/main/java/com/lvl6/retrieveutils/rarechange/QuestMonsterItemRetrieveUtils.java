package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.QuestMonsterItem;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class QuestMonsterItemRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, QuestMonsterItem>> questIdsToMonsterIdsToItems;

  private static final String TABLE_NAME = DBConstants.TABLE_QUEST_MONSTER_ITEM;

  public static Map<Integer, Map<Integer, QuestMonsterItem>> getQuestIdsToMonsterIdsToItems() {
    log.debug("retrieving all task stage data map");
    if (questIdsToMonsterIdsToItems == null) {
      setStaticQuestIdsToMonsterIdsToItems();
    }
    return questIdsToMonsterIdsToItems;
  }
  
  public static QuestMonsterItem getItemForQuestAndMonsterId(int questId, int monsterId) {
  	if (questIdsToMonsterIdsToItems == null) {
  		setStaticQuestIdsToMonsterIdsToItems();      
  	}

  	if (!questIdsToMonsterIdsToItems.containsKey(questId)) {
  		log.warn("no items for questId=" + questId);
  		return null; 
  	}
  	
  	Map<Integer, QuestMonsterItem> monsterIdsToItems =
  			questIdsToMonsterIdsToItems.get(questId);
  	
  	if (monsterIdsToItems.containsKey(monsterId)) {
  		return monsterIdsToItems.get(monsterId);
  		
  	} else {
  		log.warn("no items for questId=" + questId + " monsterId=" + monsterId);
  	}
  	return null;
  	
  }
  
  //duple (questId, monsterId) will have at most one row in quest_monster_item table.
//  public static Map<Integer, QuestMonsterItem> getItemsForQuestId(int questId) {
//    log.debug("retrieve quest_monster_item data for questId " + questId);
//    if (questIdsToMonsterIdsToItems == null) {
//      setStaticQuestIdsToMonsterIdsToItems();      
//    }
//    return questIdsToMonsterIdsToItems.get(questId);
//  }


  private static void setStaticQuestIdsToMonsterIdsToItems() {
    log.debug("setting static map of quest ids to monster ids to items");
    Random rand = new Random();

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Map<Integer, QuestMonsterItem>> questIdsToMonsterIdsToItemsTemp =
			          new HashMap<Integer, Map<Integer, QuestMonsterItem>>();
			      //loop throughe each row and convert it into a java object
			      while(rs.next()) {  
			        QuestMonsterItem qmi = convertRSRowToQuestMonsterItem(rs);
			        if (qmi == null) {
			          continue;
			        }
			        qmi.setRand(rand);
			        
			        
			        int questId = qmi.getQuestId();
			        //base case, no key with quest id exists, so create map with
			        //key: quest id, to value: another map
			        if (!questIdsToMonsterIdsToItemsTemp.containsKey(questId)) {
			          questIdsToMonsterIdsToItemsTemp.put(questId, new HashMap<Integer, QuestMonsterItem>());
			        }

			        //stick item into the map of monster ids to QuestMonsterItem objects
			        Map<Integer, QuestMonsterItem> monsterIdsToItems =
			            questIdsToMonsterIdsToItemsTemp.get(questId);
			        
			        int monsterId = qmi.getMonsterId();
			        monsterIdsToItems.put(monsterId, qmi);
			      }
			      questIdsToMonsterIdsToItems = questIdsToMonsterIdsToItemsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("task stage retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticQuestIdsToMonsterIdsToItems();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static QuestMonsterItem convertRSRowToQuestMonsterItem(ResultSet rs) throws SQLException {
    int i = 1;
    int questId = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    int itemId = rs.getInt(i++);
    float itemDropRate = rs.getFloat(i++);
    
    QuestMonsterItem taskStage = new QuestMonsterItem(questId, monsterId, itemId, itemDropRate);
        
    return taskStage;
  }
}
