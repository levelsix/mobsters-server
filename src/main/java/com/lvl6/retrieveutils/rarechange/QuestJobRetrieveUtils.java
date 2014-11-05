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

import com.lvl6.info.QuestJob;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class QuestJobRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, QuestJob> questJobIdsToQuestJobs;
  private static Map<Integer, Map<Integer, QuestJob>> questIdsToQuestJobIdsToJobs;
  private static Map<Integer, Map<Integer, QuestJob>> cityIdToQuestJobIdToQuestJobs;

  private static final String TABLE_NAME = DBConstants.TABLE_QUEST_JOB;

  //CONTROLLER LOGIC******************************************************************
  public static List<Integer> getQuestJobIdsForQuestIds(List<Integer> questIdList) {
	  List<Integer> questJobIds = new ArrayList<Integer>();
	  
	  for (Integer questId : questIdList) {
		  Map<Integer, QuestJob> questJobIdsToQuestJobs =
				  getQuestJobsForQuestId(questId);
		  
		  if (null == questJobIdsToQuestJobs ||
				  questJobIdsToQuestJobs.isEmpty()) {
			  continue;
		  }
		  
		  questJobIds.addAll(questJobIdsToQuestJobs.keySet());
	  }
	  return questJobIds;
  }

  //RETRIEVE QUERIES*********************************************************************
  public static Map<Integer, QuestJob> getQuestJobIdsToQuestJobs() {
    log.debug("retrieving all quest job data");
    if (null == questJobIdsToQuestJobs) {
      setStaticQuestJobIdsToQuestJobs();
    }
    return questJobIdsToQuestJobs;
  }

  public static QuestJob getQuestJobForQuestJobId(int questJobId) {
    log.debug("retrieving quest job with questJobId " + questJobId);
    if (null == questJobIdsToQuestJobs) {
      setStaticQuestJobIdsToQuestJobs();
    }
    
    if (!questJobIdsToQuestJobs.containsKey(questJobId)) {
    	log.error("no quest job for id:" + questJobId);
    	return null;
    }
    
    return questJobIdsToQuestJobs.get(questJobId);
  }
  
  public static Map<Integer, QuestJob> getQuestJobsForQuestId(int questId) {
	  log.debug("retrieving quest jobs with questId " + questId);
	  if (null == questIdsToQuestJobIdsToJobs) {
		  setStaticQuestJobIdsToQuestJobs();
	  }
	  
	  if (!questIdsToQuestJobIdsToJobs.containsKey(questId)) {
		  log.error("no quest jobs for quest id " + questId);
		  return new HashMap<Integer, QuestJob>();
	  }
	  
	  return questIdsToQuestJobIdsToJobs.get(questId);
  }
  
  public static Map<Integer, QuestJob> getQuestJobsForCityId(int cityId) {
	  log.debug("retrieving quest jobs with cityId " + cityId);
	  if (null == cityIdToQuestJobIdToQuestJobs) {
		  setStaticQuestJobIdsToQuestJobs();
	  }
	  
	  if (!cityIdToQuestJobIdToQuestJobs.containsKey(cityId)) {
		  log.error("no quest jobs for city id " + cityId);
		  return new HashMap<Integer, QuestJob>();
	  }
	  
	  return cityIdToQuestJobIdToQuestJobs.get(cityId);
  }

  private static void setStaticQuestJobIdsToQuestJobs() {
	  log.debug("setting static map of questJobIds to questJobs");

	  Connection conn = DBConnection.get().getConnection();
	  ResultSet rs = null;
	  try {
		  if (null != conn) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
			  
			  if (null != rs) {
				  setStaticQuestJobIdsToQuestJobsHelper(conn, rs);
			  }
		  }
		  
	  } catch (Exception e) {
		  log.error("quest retrieve db error.", e);
	  } finally {
		  DBConnection.get().close(rs, null, conn);
	  }
  }
  
  private static void setStaticQuestJobIdsToQuestJobsHelper(Connection conn,
		  ResultSet rs) {
	  
	  try {
		  rs.last();
		  rs.beforeFirst();

		  Map<Integer, QuestJob> tmp = new HashMap<Integer, QuestJob>();
		  Map<Integer, Map<Integer, QuestJob>> questIdMapTmp =
				  new HashMap<Integer, Map<Integer, QuestJob>>();
		  Map<Integer, Map<Integer, QuestJob>> cityIdMapTmp =
				  new HashMap<Integer, Map<Integer, QuestJob>>();

		  while(rs.next()) {
			  QuestJob questJob = convertRSRowToQuestJob(rs);
			  if (null == questJob) {
				  continue;
			  }

			  int questJobId = questJob.getId();

			  int questId = questJob.getQuestId();
			  if (!questIdMapTmp.containsKey(questId)) {
				  //base case for when there are no quest jobs for a quest
				  questIdMapTmp.put(questId, new HashMap<Integer, QuestJob>());
			  }
			  
			  int cityId = questJob.getCityId();
			  if (!cityIdMapTmp.containsKey(cityId)) {
				  //base case for when there are no quest jobs for a city
				  cityIdMapTmp.put(cityId, new HashMap<Integer, QuestJob>());
			  }

			  //id to quest job
			  tmp.put(questJobId, questJob);
			  
			  //id to quest job for questId
			  Map<Integer, QuestJob> questJobIdToQuestJobForQuestId =
					  questIdMapTmp.get(questId);
			  questJobIdToQuestJobForQuestId.put(questJobId, questJob);
			  
			  //id to quest job for cityId
			  Map<Integer, QuestJob> questJobIdToQuestJobForCityId =
					  cityIdMapTmp.get(cityId);
			  questJobIdToQuestJobForCityId.put(questJobId, questJob);
		  }

		  questJobIdsToQuestJobs = tmp;
		  questIdsToQuestJobIdsToJobs = questIdMapTmp;
		  cityIdToQuestJobIdToQuestJobs = cityIdMapTmp;
	  } catch (SQLException e) {
		  log.error("problem with database call.", e);

	  }
  }

  public static void reload() {
    setStaticQuestJobIdsToQuestJobs();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static QuestJob convertRSRowToQuestJob(ResultSet rs) throws SQLException {
    int id = rs.getInt(DBConstants.QUEST_JOB__ID);
    int questId = rs.getInt(DBConstants.QUEST_JOB__QUEST_ID);
    String questJobType = rs.getString(DBConstants.QUEST_JOB__QUEST_JOB_TYPE);
    String description = rs.getString(DBConstants.QUEST_JOB__DESCRIPTION);
    int staticDataId = rs.getInt(DBConstants.QUEST_JOB__STATIC_DATA_ID);
    int quantity = rs.getInt(DBConstants.QUEST_JOB__QUANTITY);
    int priority = rs.getInt(DBConstants.QUEST_JOB__PRIORITY);
    
    int cityId = rs.getInt(DBConstants.QUEST_JOB__CITY_ID);
    int cityAssetNum = rs.getInt(DBConstants.QUEST_JOB__CITY_ASSET_NUM);
    
    if (null != questJobType) {
    	String newQuestJobType = questJobType.trim().toUpperCase();
    	if (!questJobType.equals(newQuestJobType)) {
    		log.error(String.format(
    			"questJobType incorrect: %s, id=%s",
    			questJobType, id));
    		questJobType = newQuestJobType;
    	}
    }
    
    QuestJob quest = new QuestJob(id, questId, questJobType, description,
    		staticDataId, quantity, priority, cityId, cityAssetNum);
    
    return quest;
  }

}
