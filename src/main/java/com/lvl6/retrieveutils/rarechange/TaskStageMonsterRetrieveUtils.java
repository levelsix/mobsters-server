package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.TaskStageMonster;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.TaskProto.TaskStageMonsterProto.MonsterType;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskStageMonsterRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, List<TaskStageMonster>> taskStageIdsToTaskStageMonsters;
  private static Map<Integer, TaskStageMonster> taskStageMonsterIdsToTaskStageMonsters;

  private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE_MONSTER;

  public static Map<Integer, List<TaskStageMonster>> getTaskStageIdsToTaskStageMonsters() {
    log.debug("retrieving all task stage monster data map");
    if (taskStageIdsToTaskStageMonsters == null) {
      setStaticTaskStageIdsToTaskStageMonster();
    }
    return taskStageIdsToTaskStageMonsters;
  }

  public static List<TaskStageMonster> getTaskStagesForTaskStageId(int taskStageId) {
    log.debug("retrieve task stage data for stage " + taskStageId);
    if (taskStageIdsToTaskStageMonsters == null) {
      setStaticTaskStageIdsToTaskStageMonster();      
    }
    return taskStageIdsToTaskStageMonsters.get(taskStageId);
  }

  public static Map<Integer, TaskStageMonster> getTaskStageMonstersForIds(Collection<Integer> ids) {
  	if (null == taskStageMonsterIdsToTaskStageMonsters) {
  		setStaticTaskStageIdsToTaskStageMonster();
  	}
  	Map<Integer, TaskStageMonster> returnMap = new HashMap<Integer, TaskStageMonster>();
  	
  	for (int id : ids) {
  		TaskStageMonster tsm = taskStageMonsterIdsToTaskStageMonsters.get(id);
  		returnMap.put(id, tsm);
  	}
  	return returnMap;
  }

  private static void setStaticTaskStageIdsToTaskStageMonster() {
    log.debug("setting static map of taskStageIds to monsterIds");

    Random rand = new Random();
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map<Integer, List<TaskStageMonster>> taskStageIdsToTaskStageMonstersTemp =
              new HashMap<Integer, List<TaskStageMonster>>();
          Map<Integer, TaskStageMonster> taskStageMonsterIdsToTaskStageMonstersTemp =
          		new HashMap<Integer, TaskStageMonster>();
          
          //loop through each row and convert it into a java object
          while(rs.next()) {  
            TaskStageMonster taskStageMonster = convertRSRowToTaskStageMonster(rs, rand);
            
            int stageId = taskStageMonster.getStageId();
            if (!taskStageIdsToTaskStageMonstersTemp.containsKey(stageId)) {
              taskStageIdsToTaskStageMonstersTemp.put(stageId, new ArrayList<TaskStageMonster>());
            }

            List<TaskStageMonster> monsters = taskStageIdsToTaskStageMonstersTemp.get(stageId);
            monsters.add(taskStageMonster);
            
            int taskStageMonsterId = taskStageMonster.getId();
            taskStageMonsterIdsToTaskStageMonstersTemp.put(taskStageMonsterId, taskStageMonster);
          }
          taskStageIdsToTaskStageMonsters = taskStageIdsToTaskStageMonstersTemp;
          taskStageMonsterIdsToTaskStageMonsters = taskStageMonsterIdsToTaskStageMonstersTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs, null, conn);
  }

  public static void reload() {
    setStaticTaskStageIdsToTaskStageMonster();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static TaskStageMonster convertRSRowToTaskStageMonster(ResultSet rs, Random rand) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int stageId = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    MonsterType monsterType = MonsterType.valueOf(rs.getInt(i++));
    int expReward = rs.getInt(i++);
    int minSilverDrop = rs.getInt(i++);
    int maxSilverDrop = rs.getInt(i++);
    float puzzlePieceDropRate = rs.getFloat(i++);
    int level = rs.getInt(i++);
    float chanceToAppear = rs.getFloat(i++);
    
    TaskStageMonster taskStageMonster = new TaskStageMonster(id, stageId, monsterId, monsterType, expReward, minSilverDrop, maxSilverDrop, puzzlePieceDropRate, level, chanceToAppear);
        
    taskStageMonster.setRand(rand);
    return taskStageMonster;
  }
}
