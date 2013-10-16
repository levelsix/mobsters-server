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

import com.lvl6.info.jobs.QuestJobBuildStruct;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class QuestJobBuildStructRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, QuestJobBuildStruct> buildStructJobIdsToBuildStructJobs;

  private static final String TABLE_NAME = DBConstants.TABLE_QUEST_JOB_BUILD_STRUCT;

  public static Map<Integer, QuestJobBuildStruct> getBuildStructJobIdsToBuildStructJobs() {
    log.debug("retrieving all build struct job data");
    if (buildStructJobIdsToBuildStructJobs == null) {
      setStaticBuildStructJobIdsToBuildStructJobs();
    }
    return buildStructJobIdsToBuildStructJobs;
  }

  public static Map<Integer, QuestJobBuildStruct> getBuildStructJobsForBuildStructJobIds(List<Integer> ids) {
    log.debug("retrieving map of build struct jobs with ids " + ids);
    if (buildStructJobIdsToBuildStructJobs == null) {
      setStaticBuildStructJobIdsToBuildStructJobs();
    }
    Map<Integer, QuestJobBuildStruct> toreturn = new HashMap<Integer, QuestJobBuildStruct>();
    for (Integer id : ids) {
      toreturn.put(id,  buildStructJobIdsToBuildStructJobs.get(id));
    }
    return toreturn;
  }

  public static QuestJobBuildStruct getBuildStructJobForBuildStructJobId(int buildStructJobId) {
    log.debug("retrieving build struct job data for build struct job id " + buildStructJobId);
    if (buildStructJobIdsToBuildStructJobs == null) {
      setStaticBuildStructJobIdsToBuildStructJobs();
    }
    return buildStructJobIdsToBuildStructJobs.get(buildStructJobId);
  }

  private static void setStaticBuildStructJobIdsToBuildStructJobs() {
    log.debug("setting static map of build struct job id to build struct job");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map <Integer, QuestJobBuildStruct> buildStructJobIdsToBuildStructJobsTemp = new HashMap<Integer, QuestJobBuildStruct>();
          while(rs.next()) {  //should only be one
            QuestJobBuildStruct bsj = convertRSRowToBuildStructJob(rs);
            if (bsj != null)
              buildStructJobIdsToBuildStructJobsTemp.put(bsj.getId(), bsj);
          }
          buildStructJobIdsToBuildStructJobs = buildStructJobIdsToBuildStructJobsTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }  
    }
    DBConnection.get().close(rs, null, conn);
  }

  public static void reload() {
    setStaticBuildStructJobIdsToBuildStructJobs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static QuestJobBuildStruct convertRSRowToBuildStructJob(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int structId = rs.getInt(i++);
    int quantity = rs.getInt(i++);
    return new QuestJobBuildStruct(id, structId, quantity);
  }
}
