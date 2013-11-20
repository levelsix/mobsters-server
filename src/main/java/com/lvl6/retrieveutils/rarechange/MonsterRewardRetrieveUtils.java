package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MonsterReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class MonsterRewardRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, List<MonsterReward>> monsterIdsToMonsterReward;
  private static Map<Integer, MonsterReward> idsToMonsterReward;

  private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_REWARD;

  public static Map<Integer, MonsterReward> getIdsToMonsterReward() {
    log.debug("retrieving all monster rewards data map");
    if (idsToMonsterReward == null) {
      setStaticIdsToMonsterReward();
    }
    return idsToMonsterReward;
  }

  public static MonsterReward getMonsterRewardForId(int id) {
    log.debug("retrieve monster reward data for id " + id);
    if (idsToMonsterReward == null) {
      setStaticIdsToMonsterReward();      
    }
    return idsToMonsterReward.get(id);
  }

  public static Map<Integer, MonsterReward> getMonsterRewardForIds(
		  Collection<Integer> ids) {
    log.debug("retrieve monster rewards data for ids " + ids);
    if (idsToMonsterReward == null) {
      setStaticIdsToMonsterReward();      
    }
    Map<Integer, MonsterReward> toreturn = new HashMap<Integer, MonsterReward>();
    for (Integer id : ids) {
      toreturn.put(id,  idsToMonsterReward.get(id));
    }
    return toreturn;
  }

  public static List<MonsterReward> getMonsterRewardForMonsterId(int monsterId) {
    log.debug("retrieving all monsteres for monsterId " + monsterId);
    if (monsterIdsToMonsterReward == null) {
      setStaticMonsterIdsToMonsterReward();
    }
    return monsterIdsToMonsterReward.get(monsterId);
  }

  private static void setStaticMonsterIdsToMonsterReward() {
    log.debug("setting static map of monsterId to monsterRewards");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
    	if (conn != null) {
    		rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
    		if (rs != null) {
    			try {
    				rs.last();
    				rs.beforeFirst();
    				Map<Integer, List<MonsterReward>> monsterIdsToMonsterRewardTemp = new HashMap<Integer, List<MonsterReward>>();
    				while(rs.next()) {  
    					MonsterReward br = convertRSRowToMonsterReward(rs);
    					if (br != null) {
    						int bid = br.getMonsterId();
    						if (monsterIdsToMonsterRewardTemp.get(bid) == null) {
    							monsterIdsToMonsterRewardTemp.put(bid, new ArrayList<MonsterReward>());
    						}
    						monsterIdsToMonsterRewardTemp.get(bid).add(br);
    					}
    				}
    				monsterIdsToMonsterReward = monsterIdsToMonsterRewardTemp;
    			} catch (SQLException e) {
    				log.error("problem with database call.", e);

    			}
    		}    
    	}
    } catch (Exception e) {
    	log.error("monster reward retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  private static void setStaticIdsToMonsterReward() {
    log.debug("setting static map of monsterIds to monsteres");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
    	if (conn != null) {
    		rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

    		if (rs != null) {
    			try {
    				rs.last();
    				rs.beforeFirst();
    				HashMap<Integer, MonsterReward> idsToMonsterRewardTemp = new HashMap<Integer, MonsterReward>();
    				while(rs.next()) {  //should only be one
    					MonsterReward mr = convertRSRowToMonsterReward(rs);
    					if (mr != null)
    						idsToMonsterRewardTemp.put(mr.getId(), mr);
    				}
    				idsToMonsterReward = idsToMonsterRewardTemp;
    			} catch (SQLException e) {
    				log.error("problem with database call.", e);

    			}
    		}    
    	}
    } catch (Exception e) {
    	log.error("monster reward retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticIdsToMonsterReward();
    setStaticMonsterIdsToMonsterReward();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static MonsterReward convertRSRowToMonsterReward(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    int equipId = rs.getInt(i++);
    float dropRate = rs.getFloat(i++);
    
    MonsterReward br = new MonsterReward(id, monsterId, equipId, dropRate);
    return br;
  }
}
