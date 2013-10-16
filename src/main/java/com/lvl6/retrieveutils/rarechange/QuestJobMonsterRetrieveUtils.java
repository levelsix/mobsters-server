package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.jobs.QuestJobMonster;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class QuestJobMonsterRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, QuestJobMonster> monsterJobIdsToMonsterJobs;

  private static final String TABLE_NAME = DBConstants.TABLE_QUEST_JOB_MONSTER;

  public static Map<Integer, QuestJobMonster> getMonsterJobIdsToMonsterJobs() {
    log.debug("retrieving all upgrade struct job data");
    if (monsterJobIdsToMonsterJobs == null) {
      setStaticMonsterJobIdsToMonsterJobs();
    }
    return monsterJobIdsToMonsterJobs;
  }

  public static Map<Integer, QuestJobMonster> getMonsterJobsForMonsterJobIds(List<Integer> ids) {
    log.debug("retrieving monster jobs with ids " + ids);
    if (monsterJobIdsToMonsterJobs == null) {
      setStaticMonsterJobIdsToMonsterJobs();
    }
    Map<Integer, QuestJobMonster> toreturn = new HashMap<Integer, QuestJobMonster>();
    for (Integer id : ids) {
      toreturn.put(id,  monsterJobIdsToMonsterJobs.get(id));
    }
    return toreturn;
  }

  public static QuestJobMonster getMonsterJobForMonsterJobId(int monsterJobId) {
    log.debug("retrieving monster job data for monster job id " + monsterJobId);
    if (monsterJobIdsToMonsterJobs == null) {
      setStaticMonsterJobIdsToMonsterJobs();
    }
    return monsterJobIdsToMonsterJobs.get(monsterJobId);
  }

  private static void setStaticMonsterJobIdsToMonsterJobs() {
    log.debug("setting static map of monster job id to monster job");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map <Integer, QuestJobMonster> monsterJobIdsToMonsterJobsTemp = new HashMap<Integer, QuestJobMonster>();
          while(rs.next()) {  //should only be one
            QuestJobMonster usj = convertRSRowToMonsterJob(rs);
            if (usj != null)
              monsterJobIdsToMonsterJobsTemp.put(usj.getId(), usj);
          }
          monsterJobIdsToMonsterJobs = monsterJobIdsToMonsterJobsTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs,  null, conn);
  }

  public static void reload() {
    setStaticMonsterJobIdsToMonsterJobs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static QuestJobMonster convertRSRowToMonsterJob(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    int quantity = rs.getInt(i++);
    int monsterJobType = rs.getInt(i++);

    return new QuestJobMonster(id, monsterId, quantity, monsterJobType);
  }
}
