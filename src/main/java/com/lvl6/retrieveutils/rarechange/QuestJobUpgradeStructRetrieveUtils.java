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

import com.lvl6.info.jobs.QuestJobUpgradeStruct;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class QuestJobUpgradeStructRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, QuestJobUpgradeStruct> upgradeStructJobIdsToUpgradeStructJobs;

  private static final String TABLE_NAME = DBConstants.TABLE_QUEST_JOB_UPGRADE_STRUCT;

  public static Map<Integer, QuestJobUpgradeStruct> getUpgradeStructJobIdsToUpgradeStructJobs() {
    log.debug("retrieving all upgrade struct job data");
    if (upgradeStructJobIdsToUpgradeStructJobs == null) {
      setStaticUpgradeStructJobIdsToUpgradeStructJobs();
    }
    return upgradeStructJobIdsToUpgradeStructJobs;
  }

  public static Map<Integer, QuestJobUpgradeStruct> getUpgradeStructJobsForUpgradeStructJobIds(List<Integer> ids) {
    log.debug("retrieving upgrade struct jobs with ids " + ids);
    if (upgradeStructJobIdsToUpgradeStructJobs == null) {
      setStaticUpgradeStructJobIdsToUpgradeStructJobs();
    }
    Map<Integer, QuestJobUpgradeStruct> toreturn = new HashMap<Integer, QuestJobUpgradeStruct>();
    for (Integer id : ids) {
      toreturn.put(id,  upgradeStructJobIdsToUpgradeStructJobs.get(id));
    }
    return toreturn;
  }

  public static QuestJobUpgradeStruct getUpgradeStructJobForUpgradeStructJobId(int upgradeStructJobId) {
    log.debug("retrieving upgrade struct job data for upgrade struct job id " + upgradeStructJobId);
    if (upgradeStructJobIdsToUpgradeStructJobs == null) {
      setStaticUpgradeStructJobIdsToUpgradeStructJobs();
    }
    return upgradeStructJobIdsToUpgradeStructJobs.get(upgradeStructJobId);
  }

  private static void setStaticUpgradeStructJobIdsToUpgradeStructJobs() {
    log.debug("setting static map of upgrade struct job id to upgrade struct job");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map <Integer, QuestJobUpgradeStruct> upgradeStructJobIdsToUpgradeStructJobsTemp = new HashMap<Integer, QuestJobUpgradeStruct>();
          while(rs.next()) {  //should only be one
            QuestJobUpgradeStruct usj = convertRSRowToUpgradeStructJob(rs);
            if (usj != null)
              upgradeStructJobIdsToUpgradeStructJobsTemp.put(usj.getId(), usj);
          }
          upgradeStructJobIdsToUpgradeStructJobs = upgradeStructJobIdsToUpgradeStructJobsTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs,  null, conn);
  }

  public static void reload() {
    setStaticUpgradeStructJobIdsToUpgradeStructJobs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static QuestJobUpgradeStruct convertRSRowToUpgradeStructJob(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int structId = rs.getInt(i++);
    int levelReq = rs.getInt(i++);

    return new QuestJobUpgradeStruct(id, structId, levelReq);
  }
}
