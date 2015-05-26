//package com.lvl6.retrieveutils.rarechange;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.TournamentEvent;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//
//@Component @DependsOn("gameServer") public class TournamentEventRetrieveUtils {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  private static Map<Integer, TournamentEvent> idsToLeaderBoardEvents;
//
//  private static final String TABLE_NAME = DBConstants.TABLE_TOURNAMENT_EVENT_CONFIG;
//
//  public static Map<Integer, TournamentEvent> getIdsToTournamentEvents(Boolean reloadStaticData) {
//    log.debug("retrieving tournament event data");
//    if (idsToLeaderBoardEvents == null || reloadStaticData) {
//      setStaticIdsToTournamentEvents();
//    }
//    return idsToLeaderBoardEvents;
//  }
//
//  public static Map<Integer, TournamentEvent> getTournamentEventsForIds(List<Integer> ids) {
//    log.debug("retrieving TournamentEvents with ids " + ids);
//    if (idsToLeaderBoardEvents == null) {
//      setStaticIdsToTournamentEvents();
//    }
//    Map<Integer, TournamentEvent> toreturn = new HashMap<Integer, TournamentEvent>();
//    for (Integer id : ids) {
//      toreturn.put(id,  idsToLeaderBoardEvents.get(id));
//    }
//    return toreturn;
//  }
//
//  public static TournamentEvent getTournamentEventForId(int id) {
//    log.debug("retrieving TournamentEvent for id " + id);
//    if (idsToLeaderBoardEvents == null) {
//      setStaticIdsToTournamentEvents();
//    }
//    return idsToLeaderBoardEvents.get(id);
//  }
//
////  decided to get the active tournament events in MiscMethods.currentTournamentEventProtos()
////  public static List<TournamentEvent> getActiveTournamentEvents() {
////    long curTime = (new Date()).getTime();
////    String now = "\"" + new Timestamp(curTime) + "\"";
////
////    List<TournamentEvent> toReturn = new ArrayList<TournamentEvent>();
////    
////    if(null != idsToLeaderBoardEvents) {
////      //go through local copy of db, instead of going to db
////      for(TournamentEvent e : idsToLeaderBoardEvents.values()) {
////        if(e.getEndDate().getTime() > curTime && curTime >= e.getStartDate().getTime()) {
////          toReturn.add(e);
////        }
////      }
////    } else {
////      //initialization crap
////      Connection conn = DBConnection.get().getConnection();
////      ResultSet rs = null;
////      List<String> columns = null;
////      Map<String, Object> absoluteConditionParams = null;
////      Map<String, Object> relativeGreaterThanConditionParams = new HashMap<String, Object>();
////      Map<String, Object> relativeLessThanConditionParams = new HashMap<String, Object>();
////      Map<String, Object> likeCondParams = null;
////      String conddelim = ",";
////      String orderByColumn = "";
////      boolean orderByAsc = false;
////      int limit = -1; //SELECT_LIMIT_NOT_SET;
////      boolean random = false;
////      //end initialization
////      
////      //event should have end time after now
////      relativeGreaterThanConditionParams.put(DBConstants.LEADERBOARD_EVENTS__END_TIME, now);
////      //event should have start time before now
////      relativeLessThanConditionParams.put(DBConstants.LEADERBOARD_EVENTS__START_TIME, now);
////      if (null != conn) {
////        rs = DBConnection.get().selectRows(conn, columns, absoluteConditionParams, 
////            relativeGreaterThanConditionParams, relativeLessThanConditionParams, likeCondParams, 
////            TABLE_NAME, conddelim, orderByColumn, orderByAsc, limit, random);
////        if (null != rs) {
////          try {
////            rs.last();
////            rs.beforeFirst();
////            while(rs.next()) {
////              TournamentEvent le = convertRSRowToTournamentEvent(rs);
////              if(null != le) {
////                toReturn.add(le);
////              }
////            }
////          } catch (SQLException e) {
////            log.error("problem with tournament event db call.", e);
////          }
////        }
////      } catch (Exception e) {
////  				log.error("tournament event retrieve db error.", e);
////				} finally {
////					DBConnection.get().close(rs, null, conn);
////				}
////    }
////    
////    return toReturn;
////  }
//  
//  private static void setStaticIdsToTournamentEvents() {
//    log.debug("setting static map of upgrade struct job id to upgrade struct job");
//
//    Connection conn = DBConnection.get().getConnection();
//    ResultSet rs = null;
//    try {
//			if (conn != null) {
//			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
//			  if (rs != null) {
//			    try {
//			      rs.last();
//			      rs.beforeFirst();
//			      Map <Integer, TournamentEvent> idsToTournamentEventTemp = new HashMap<Integer, TournamentEvent>();
//			      while(rs.next()) {  //should only be one
//			        TournamentEvent le = convertRSRowToTournamentEvent(rs);
//			        if (le != null)
//			          idsToTournamentEventTemp.put(le.getId(), le);
//			      }
//			      idsToLeaderBoardEvents = idsToTournamentEventTemp;
//			    } catch (SQLException e) {
//			      log.error("problem with database call.", e);
//			      
//			    }
//			  }    
//			}
//		} catch (Exception e) {
//    	log.error("tournament event retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//  }
//
//  public static void reload() {
//    setStaticIdsToTournamentEvents();
//  }
//
//  /*
//   * assumes the resultset is apprpriately set up. traverses the row it's on.
//   */
//  private static TournamentEvent convertRSRowToTournamentEvent(ResultSet rs) throws SQLException {
//    int i = 1;
//    int id = rs.getInt(i++);
//    Date startDate = new Date(rs.getTimestamp(i++).getTime());
//    Date endDate = new Date(rs.getTimestamp(i++).getTime());
//    String eventName = rs.getString(i++);
//    boolean rewardsGivenOut = rs.getBoolean(i++);
//    return new TournamentEvent(id, startDate, endDate, eventName, rewardsGivenOut);
//  }
//}
