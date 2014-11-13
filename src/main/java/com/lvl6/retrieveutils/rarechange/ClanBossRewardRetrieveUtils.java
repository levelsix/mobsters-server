//package com.lvl6.retrieveutils.rarechange;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.ClanBossReward;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//
//@Component @DependsOn("gameServer") public class ClanBossRewardRetrieveUtils {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//  /*
//  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_BOSS_REWARD;
//
//  private static Map<Integer, List<ClanBossReward>> clanBossIdToClanBossRewards;
//  
//  public static List<ClanBossReward> getClanBossRewardsWithClanBossId(int id) {
//    if (null == clanBossIdToClanBossRewards) {
//      setStaticClanBossIdToClanBossRewards();
//    }
//    return clanBossIdToClanBossRewards.get(id);
//  }
//
//  private static void setStaticClanBossIdToClanBossRewards() {
//    log.debug("setting static map of clan boss ids to clan boss reward");
//
//    Connection conn = DBConnection.get().getConnection();
//    ResultSet rs = null;
//    if (conn != null) {
//      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
//
//      if (rs != null) {
//        try {
//          rs.last();
//          rs.beforeFirst();
//          Map<Integer, List<ClanBossReward>> tempClanBossIdToClanBossRewards =
//              new HashMap<Integer, List<ClanBossReward>>();
//          while(rs.next()) {  
//            ClanBossReward aClanBossReward = convertRSRowToClanBossReward(rs);
//            if (aClanBossReward != null) {
//              int clanBossId = aClanBossReward.getClanBossId();
//              if (!tempClanBossIdToClanBossRewards.containsKey(clanBossId)) {
//                List<ClanBossReward> tempList = new ArrayList<ClanBossReward>();
//                tempList.add(aClanBossReward);
//              }
//              List<ClanBossReward> preexistingList = tempClanBossIdToClanBossRewards.get(clanBossId);
//              preexistingList.add(aClanBossReward);
//            }
//          }
//            clanBossIdToClanBossRewards = tempClanBossIdToClanBossRewards;
//        } catch (SQLException e) {
//          log.error("problem with database call.", e);
//          
//        }
//      }    
//    } catch (Exception e) {
//    	log.error("clan boss reward retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//  }
//   */
//  public static void reload() {
//    //setStaticClanBossIdToClanBossRewards();
//  }
//
//  /*
//   * assumes the resultset is apprpriately set up. traverses the row it's on.
//   */
//  private static ClanBossReward convertRSRowToClanBossReward(ResultSet rs) throws SQLException {
//    int i = 1;
//    int id = rs.getInt(i++);
//    int clanBossId = rs.getInt(i++);
//    int equipId = rs.getInt(i++);;
//        
//    ClanBossReward aClanBossReward = new ClanBossReward(id, clanBossId, equipId);
//    return aClanBossReward;
//  }
//}
