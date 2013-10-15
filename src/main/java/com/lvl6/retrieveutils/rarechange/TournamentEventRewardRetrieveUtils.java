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

import com.lvl6.info.TournamentEventReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TournamentEventRewardRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, List<TournamentEventReward>> tournamentEventIdsToLeaderboardEventRewards;

  private static final String TABLE_NAME = DBConstants.TABLE_TOURNAMENT_REWARD;

  public static Map<Integer, List<TournamentEventReward>> gettournamentEventIdsToLeaderboardEventRewards() {
    log.debug("retrieving tournament event data");
    if (tournamentEventIdsToLeaderboardEventRewards == null) {
      setStaticLeaderboardEventIdsToLeaderboardEventRewards();
    }
    return tournamentEventIdsToLeaderboardEventRewards;
  }

  public static Map<Integer, List<TournamentEventReward>> getLeaderboardEventRewardsForIds(List<Integer> ids) {
    log.debug("retrieving LeaderboardEventRewards with ids " + ids);
    if (tournamentEventIdsToLeaderboardEventRewards == null) {
      setStaticLeaderboardEventIdsToLeaderboardEventRewards();
    }
    Map<Integer, List<TournamentEventReward>> toReturn = new HashMap<Integer, List<TournamentEventReward>>();
    for (Integer id : ids) {
      toReturn.put(id,  tournamentEventIdsToLeaderboardEventRewards.get(id));
    }
    return toReturn;
  }

  public static List<TournamentEventReward> getLeaderboardEventRewardsForId(int id) {
    log.debug("retrieving LeaderboardEventReward for id " + id);
    if (tournamentEventIdsToLeaderboardEventRewards == null) {
      setStaticLeaderboardEventIdsToLeaderboardEventRewards();
    }
    return tournamentEventIdsToLeaderboardEventRewards.get(id);
  }

  private static void setStaticLeaderboardEventIdsToLeaderboardEventRewards() {
    log.debug("setting static map of leader board event id to leader board reward");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map <Integer, List<TournamentEventReward>> idsToLeaderboardEventRewardTemp = 
              new HashMap<Integer, List<TournamentEventReward>>();
          while(rs.next()) {  
            TournamentEventReward le = convertRSRowToLeaderboardEventReward(rs);
            
            if (le != null) {
              int tournamentEventId = le.getTournamentEventId();
              List<TournamentEventReward> existingRewards = 
                  idsToLeaderboardEventRewardTemp.get(tournamentEventId);
              
              if (null != existingRewards) {
                //map already has rewards pertaining to this event, so add to it
                existingRewards.add(le);
              } else {
                //le is a reward for a new event, create a new list for it
                List<TournamentEventReward> newEventRewards = new ArrayList<TournamentEventReward>();
                newEventRewards.add(le);
                
                idsToLeaderboardEventRewardTemp.put(tournamentEventId, newEventRewards);  
              }
            }
          }
          tournamentEventIdsToLeaderboardEventRewards = idsToLeaderboardEventRewardTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs,  null, conn);
  }

  public static void reload() {
    setStaticLeaderboardEventIdsToLeaderboardEventRewards();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static TournamentEventReward convertRSRowToLeaderboardEventReward(ResultSet rs) throws SQLException {
    int i = 1;
    int tournamentEventId = rs.getInt(i++);
    int minRank = rs.getInt(i++);
    int maxRank = rs.getInt(i++);
    int goldRewarded = rs.getInt(i++);
    String backgroundImageName = rs.getString(i++);
    String prizeImageName = rs.getString(i++);
    int blue = rs.getInt(i++);
    int green = rs.getInt(i++);
    int red = rs.getInt(i++);
    
    return new TournamentEventReward(tournamentEventId, minRank, maxRank, goldRewarded, 
        backgroundImageName, prizeImageName, blue, green, red);
  }
}
