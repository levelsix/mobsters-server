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
import org.springframework.stereotype.Component;

import com.lvl6.info.Monster;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
public class TaskStageMonsterRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, List<TaskStageMonster>> taskStageIdsToTaskStageMonsters;
  private static Map<Integer, TaskStageMonster> taskStageMonsterIdsToTaskStageMonsters;

  private static boolean reassignedSkills = false;
  
  private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE_MONSTER;
  
  public static Map<Integer, List<TaskStageMonster>> getTaskStageIdsToTaskStageMonsters() {
    log.debug("retrieving all task stage monster data map");
    if (null == taskStageIdsToTaskStageMonsters) {
      setStaticTaskStageIdsToTaskStageMonster();
    }
    if (!reassignedSkills) {
    	reassignSkills();
    }
    
    return taskStageIdsToTaskStageMonsters;
  }
  
  public static TaskStageMonster getTaskStageMonsterForId(int tsmId) {
	  log.debug("retrieve task stage monster for id=" + tsmId);
	  if (null == taskStageMonsterIdsToTaskStageMonsters) {
		  setStaticTaskStageIdsToTaskStageMonster();      
	  }
	  if (!reassignedSkills) {
		  reassignSkills();
	  }
	  if (!taskStageMonsterIdsToTaskStageMonsters.containsKey(tsmId)) {
		  log.error("no task stage monsters for tsm id=" + tsmId);
		  return null;
	  }

	  return taskStageMonsterIdsToTaskStageMonsters.get(tsmId);
  }

  public static List<TaskStageMonster> getMonstersForTaskStageId(int taskStageId) {
    log.debug("retrieve task stage monster data for stage " + taskStageId);
    if (null == taskStageIdsToTaskStageMonsters) {
      setStaticTaskStageIdsToTaskStageMonster();      
    }
    if (!reassignedSkills) {
    	reassignSkills();
    }
    
    if (!taskStageIdsToTaskStageMonsters.containsKey(taskStageId)) {
    	log.error("no task stage monsters for task stage id=" + taskStageId);
    	return new ArrayList<TaskStageMonster>();
    }
    
    return taskStageIdsToTaskStageMonsters.get(taskStageId);
  }

  public static Map<Integer, TaskStageMonster> getTaskStageMonstersForIds(Collection<Integer> ids) {
  	if (null == taskStageMonsterIdsToTaskStageMonsters) {
  		setStaticTaskStageIdsToTaskStageMonster();
  	}
    if (!reassignedSkills) {
    	reassignSkills();
    }
    
  	Map<Integer, TaskStageMonster> returnMap = new HashMap<Integer, TaskStageMonster>();
  	
  	for (int id : ids) {
  		TaskStageMonster tsm = taskStageMonsterIdsToTaskStageMonsters.get(id);
  		returnMap.put(id, tsm);
  	}
  	return returnMap;
  }

  private static void reassignSkills() {
	  
	  if (null == taskStageMonsterIdsToTaskStageMonsters) {
		  return;
	  }
	  
	  Map<Integer, Monster> idToMonster = MonsterRetrieveUtils.getMonsterIdsToMonsters();
	  if (null == idToMonster) {
		  return;
	  }
	  
	  boolean resetAllSkills = true;

	  //for the TaskStageMonsters that have their defensive skill not set
	  //reassign to the skill the monster has, also set the offensive skill
	  for (TaskStageMonster tsm : taskStageMonsterIdsToTaskStageMonsters.values()) {
		  Monster monzter = null;
		  if (idToMonster.containsKey(tsm.getMonsterId())) {
			  monzter = idToMonster.get(tsm.getMonsterId());
		  }
		  
		  if (null == monzter) {
			  resetAllSkills = false;
			  continue;
		  }
		  
		  tsm.setOffensiveSkillId(monzter.getBaseOffensiveSkillId());
		  
		  if (ControllerConstants.NOT_SET == tsm.getDefensiveSkillId()) {
			  tsm.setDefensiveSkillId(monzter.getBaseDefensiveSkillId());
		  }
	  }
	  
	  reassignedSkills = resetAllSkills;
  }
  
  private static void setStaticTaskStageIdsToTaskStageMonster() {
    log.debug("setting static map of taskStage and taskStageMonster Ids to monsterIds");
    
    reassignedSkills = false;
    
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
		} catch (Exception e) {
    	log.error("task stage monster retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticTaskStageIdsToTaskStageMonster();
    reassignSkills();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static TaskStageMonster convertRSRowToTaskStageMonster(ResultSet rs,
		  Random rand) throws SQLException {
    int id = rs.getInt(DBConstants.TASK_STAGE_MONSTER__ID);
    int stageId = rs.getInt(DBConstants.TASK_STAGE_MONSTER__TASK_STAGE_ID);
    int monsterId = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MONSTER_ID);
    String monsterType = rs.getString(DBConstants.TASK_STAGE_MONSTER__MONSTER_TYPE);
    int expReward = rs.getInt(DBConstants.TASK_STAGE_MONSTER__EXP_REWARD);
    int minCashDrop = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MIN_CASH_DROP);
    int maxCashDrop = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MAX_CASH_DROP);
    int minOilDrop = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MIN_OIL_DROP);
    int maxOilDrop = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MAX_OIL_DROP);
    float puzzlePieceDropRate = rs.getFloat(DBConstants.TASK_STAGE_MONSTER__PUZZLE_PIECE_DROP_RATE);
    int level = rs.getInt(DBConstants.TASK_STAGE_MONSTER__LEVEL);
    float chanceToAppear = rs.getFloat(DBConstants.TASK_STAGE_MONSTER__CHANCE_TO_APPEAR);
    float dmgMultiplier = rs.getFloat(DBConstants.TASK_STAGE_MONSTER__DMG_MULTIPLIER);
    int monsterIdDrop = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MONSTER_ID_DROP);
    int monsterDropLvl = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MONSTER_DROP_LVL);
    int defensiveSkillId = rs.getInt(DBConstants.TASK_STAGE_MONSTER__DEFENSIVE_SKILL_ID);
    
    if (rs.wasNull()) {
    	//default to skill in task stage monster
    	defensiveSkillId = ControllerConstants.NOT_SET;
    }
    
    if (null != monsterType) {
    	String newMonsterType = monsterType.trim().toUpperCase();
    	if (!monsterType.equals(newMonsterType)) {
    		log.error("monster type incorrect: " + monsterType + "\t tsmId=" + id);
    		monsterType = newMonsterType;
    	}
    }
    
    if (puzzlePieceDropRate > 1F || puzzlePieceDropRate < 0F) {
    	log.error("incorrect puzzlePieceDropRate: " + puzzlePieceDropRate +
    			". Forcing it to be in [0,1] inclusive. id=" + id);
    	puzzlePieceDropRate = Math.min(1F, puzzlePieceDropRate);
    	puzzlePieceDropRate = Math.max(0F, puzzlePieceDropRate);
    }
    
    if (chanceToAppear < 0F) {
    	log.error("incorrect chanceToAppear: " + chanceToAppear +
    			". Forcing it to be at/above 0. id=" + id);
    	chanceToAppear = Math.max(0F, chanceToAppear);
    }
    
    if (dmgMultiplier < 0F) {
    	log.error("incorrect dmgMultiplier: " + dmgMultiplier +
			". Forcing it to be at/above 0. id=" + id);
    	dmgMultiplier = Math.max(0F, dmgMultiplier);
    }
    
    
    TaskStageMonster taskStageMonster = new TaskStageMonster(id, stageId, monsterId,
    		monsterType, expReward, minCashDrop, maxCashDrop, minOilDrop, maxOilDrop,
    		puzzlePieceDropRate, level, chanceToAppear, dmgMultiplier, monsterIdDrop,
    		monsterDropLvl, defensiveSkillId, 0);
    
    if (null == monsterType) {
    	log.error("TaskStageMonster, monster type incorrect, offending tsm=" +
    			taskStageMonster);
    }
        
    taskStageMonster.setRand(rand);
    return taskStageMonster;
  }
}
