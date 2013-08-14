package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanBoss;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanBossRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  /*
  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_BOSS;

  private static Map<Integer, ClanBoss> clanBossIdToClanBoss;
  
  public static ClanBoss getClanBossWithId(int id) {
    if (null == clanBossIdToClanBoss) {
      setStaticClanBossIdToClanBoss();
    }
    return clanBossIdToClanBoss.get(id);
  }

  private static void setStaticClanBossIdToClanBoss() {
    log.debug("setting static map of clan boss ids to clan bosses");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map<Integer, ClanBoss> tempClanBossIdToClanBoss = new HashMap<Integer, ClanBoss>();
          while(rs.next()) {  
            ClanBoss clanBoss = convertRSRowToClanBoss(rs);
            if (clanBoss != null)
              clanBossIdToClanBoss.put(clanBoss.getId(), clanBoss);
          }
          clanBossIdToClanBoss = tempClanBossIdToClanBoss;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs, null, conn);
  }
  */
  public static void reload() {
    //setStaticClanBossIdToClanBoss();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static ClanBoss convertRSRowToClanBoss(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String name = rs.getString(i++);
    int hp = rs.getInt(i++);
    int energyCost = rs.getInt(i++);
    int numMinutesToKill = rs.getInt(i++);
    int numMinutesToRespawn = rs.getInt(i++);
    int numRunesOne = rs.getInt(i++);
    int numRunesTwo = rs.getInt(i++);
    int numRunesThree = rs.getInt(i++);
    int numRunesFour = rs.getInt(i++);
    int numRunesFive = rs.getInt(i++);;
        
    ClanBoss aClanBoss = new ClanBoss(id, name, hp, energyCost, numMinutesToKill, 
        numMinutesToRespawn, numRunesOne, numRunesTwo, numRunesThree, numRunesFour,
        numRunesFive);
    return aClanBoss;
  }
}
