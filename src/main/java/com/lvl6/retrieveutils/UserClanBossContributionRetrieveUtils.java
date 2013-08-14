package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserClanBossContribution;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class UserClanBossContributionRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  /*
  private static final String TABLE_NAME = DBConstants.TABLE_USER_CLAN_BOSS_CONTRIBUTION;

  public static List<UserClanBossContribution> getUserClanBossContributions(int userId, int clanId, int bossId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.USER_CLAN_BOSS_CONTRIBUTION__USER_ID, userId);
    paramsToVals.put(DBConstants.USER_CLAN_BOSS_CONTRIBUTION__CLAN_ID, clanId);
    paramsToVals.put(DBConstants.USER_CLAN_BOSS_CONTRIBUTION__BOSS_ID, bossId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
    List<UserClanBossContribution> ucbc = grabUserClanBossContributionFromRS(rs);
    DBConnection.get().close(rs, null, conn);
    return ucbc;
  }

  private static List<UserClanBossContribution> grabUserClanBossContributionFromRS(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        List<UserClanBossContribution> userClanBossContribution = new ArrayList<UserClanBossContribution>();
        while(rs.next()) {
          UserClanBossContribution uc = convertRSRowToUserClanBossContribution(rs);
          userClanBossContribution.add(uc);
        }
        return userClanBossContribution;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  */
  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static UserClanBossContribution convertRSRowToUserClanBossContribution(ResultSet rs) 
      throws SQLException {
    int i = 1;
    int userId = rs.getInt(i++);
    int clanId = rs.getInt(i++);
    int bossId = rs.getInt(i++);
    int totalDamageDone = rs.getInt(i++);
    int totalEnergyUsed = rs.getInt(i++);
    int numRunesOne = rs.getInt(i++);
    int numRunesTwo = rs.getInt(i++);
    int numRunesThree = rs.getInt(i++);
    int numRunesFour = rs.getInt(i++);
    int numRunesFive = rs.getInt(i++);
    
    return new UserClanBossContribution(userId, clanId, bossId, totalDamageDone, 
        totalEnergyUsed, numRunesOne, numRunesTwo, numRunesThree, numRunesFour, numRunesFive);
  }

}
