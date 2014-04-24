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

import com.lvl6.info.QuestJobMonsterItem;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class QuestJobMonsterItemRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, QuestJobMonsterItem>> questJobIdsToMonsterIdsToItems;

  private static final String TABLE_NAME = DBConstants.TABLE_QUEST_JOB_MONSTER_ITEM;

  public static Map<Integer, Map<Integer, QuestJobMonsterItem>> getQuestJobIdsToMonsterIdsToItems() {
    log.debug("retrieving all quest job monster item data map");
    if (questJobIdsToMonsterIdsToItems == null) {
      setStaticQuestJobIdsToMonsterIdsToItems();
    }
    return questJobIdsToMonsterIdsToItems;
  }
  
  public static QuestJobMonsterItem getItemForQuestJobAndMonsterId(
		  int questJobId, int monsterId) {
  	if (questJobIdsToMonsterIdsToItems == null) {
  		setStaticQuestJobIdsToMonsterIdsToItems();      
  	}

  	if (!questJobIdsToMonsterIdsToItems.containsKey(questJobId)) {
  		log.warn("no items for questJobId=" + questJobId);
  		return null; 
  	}
  	
  	Map<Integer, QuestJobMonsterItem> monsterIdsToItems =
  			questJobIdsToMonsterIdsToItems.get(questJobId);
  	
  	if (monsterIdsToItems.containsKey(monsterId)) {
  		return monsterIdsToItems.get(monsterId);
  		
  	} else {
  		log.warn("no items for questJobId=" + questJobId + " monsterId=" + monsterId);
  	}
  	return null;
  	
  }

  private static void setStaticQuestJobIdsToMonsterIdsToItems() {
	  log.debug("setting static map of quest job ids to monster ids to items");
	  

	  Connection conn = DBConnection.get().getConnection();
	  ResultSet rs = null;
	  try {
		  if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
				  setStaticQuestJobIdsToMonsterIdsToItemsHelper(conn, rs);
			  }    
		  }
	  } catch (Exception e) {
		  log.error("QuestJobMonsterItem retrieve db error.", e);
	  } finally {
		  DBConnection.get().close(rs, null, conn);
	  }
  }
  
  private static void setStaticQuestJobIdsToMonsterIdsToItemsHelper(
		  Connection conn, ResultSet rs) {
	  Random rand = new Random();
	  try {
		  rs.last();
		  rs.beforeFirst();
		  Map<Integer, Map<Integer, QuestJobMonsterItem>> questJobIdsToMonsterIdsToItemsTemp =
				  new HashMap<Integer, Map<Integer, QuestJobMonsterItem>>();
		  //loop through each row and convert it into a java object
		  while(rs.next()) {  
			  QuestJobMonsterItem qjmi = convertRSRowToQuestMonsterItem(rs);
			  if (null == qjmi) {
				  continue;
			  }
			  qjmi.setRand(rand);


			  int questJobId = qjmi.getQuestJobId();
			  //base case, no key with quest job id exists, so create map with
			  //key: quest job id, to value: another map
			  if (!questJobIdsToMonsterIdsToItemsTemp.containsKey(questJobId)) {
				  questJobIdsToMonsterIdsToItemsTemp.put(questJobId,
						  new HashMap<Integer, QuestJobMonsterItem>());
			  }

			  //stick item into the map of monster ids to
			  //QuestJobMonsterItem objects
			  Map<Integer, QuestJobMonsterItem> monsterIdsToItems =
					  questJobIdsToMonsterIdsToItemsTemp.get(questJobId);

			  int monsterId = qjmi.getMonsterId();
			  monsterIdsToItems.put(monsterId, qjmi);
		  }
		  questJobIdsToMonsterIdsToItems = questJobIdsToMonsterIdsToItemsTemp;
	  } catch (SQLException e) {
		  log.error("problem with database call.", e);

	  }
  }

  public static void reload() {
    setStaticQuestJobIdsToMonsterIdsToItems();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static QuestJobMonsterItem convertRSRowToQuestMonsterItem(ResultSet rs) throws SQLException {
    int i = 1;
    int questJobId = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    int itemId = rs.getInt(i++);
    float itemDropRate = rs.getFloat(i++);
    
    if (itemDropRate > 1F || itemDropRate < 0F) {
    	log.error("incorrect itemDropRate: " + itemDropRate +
    			". Forcing it to be between 1 and 0. id=" + questJobId);
    	itemDropRate = Math.min(1F, itemDropRate);
    	itemDropRate = Math.max(0F, itemDropRate);
    }
    
    QuestJobMonsterItem taskStage = new QuestJobMonsterItem(questJobId,
    		monsterId, itemId, itemDropRate);
        
    return taskStage;
  }
}
