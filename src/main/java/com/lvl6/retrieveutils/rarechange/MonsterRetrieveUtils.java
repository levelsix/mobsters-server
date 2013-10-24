package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Monster;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterQuality;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class MonsterRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Monster> monsterIdsToMonsters;

  private static final String TABLE_NAME = DBConstants.TABLE_MONSTER;

  public static Map<Integer, Monster> getMonsterIdsToMonsters() {
    log.debug("retrieving all monsteres data map");
    if (monsterIdsToMonsters == null) {
      setStaticMonsterIdsToMonsters();
    }
    return monsterIdsToMonsters;
  }

  public static Monster getMonsterForMonsterId(int monsterId) {
    log.debug("retrieve monster data for monster " + monsterId);
    if (monsterIdsToMonsters == null) {
      setStaticMonsterIdsToMonsters();      
    }
    return monsterIdsToMonsters.get(monsterId);
  }

  public static Map<Integer, Monster> getMonstersForMonsterIds(Collection<Integer> ids) {
    log.debug("retrieve monster data for monsterids " + ids);
    if (monsterIdsToMonsters == null) {
      setStaticMonsterIdsToMonsters();      
    }
    Map<Integer, Monster> toreturn = new HashMap<Integer, Monster>();
    for (Integer id : ids) {
    	if (monsterIdsToMonsters.containsKey(id)) {
    		toreturn.put(id,  monsterIdsToMonsters.get(id));
    	} else {
    		log.error("db error: no monster exists with id=" +id);
    	}
    }
    return toreturn;
  }


  private static void setStaticMonsterIdsToMonsters() {
    log.debug("setting static map of monsterIds to monsters");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          HashMap<Integer, Monster> monsterIdsToMonstersTemp = new HashMap<Integer, Monster>();
          while(rs.next()) {  //should only be one
            Monster monster = convertRSRowToMonster(rs);
            if (monster != null)
              monsterIdsToMonstersTemp.put(monster.getId(), monster);
          }
          monsterIdsToMonsters = monsterIdsToMonstersTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs, null, conn);
  }

  public static void reload() {
    setStaticMonsterIdsToMonsters();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Monster convertRSRowToMonster(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String name = rs.getString(i++);
    String monsterGroup = rs.getString(i++);
    MonsterQuality quality = MonsterQuality.valueOf(rs.getInt(i++));
    int evolutionLevel = rs.getInt(i++);
    String displayName = rs.getString(i++);
    MonsterElement element = MonsterElement.valueOf(rs.getInt(i++));
    int baseHp = rs.getInt(i++);
    String imageName = rs.getString(i++);
    int numPuzzlePieces = rs.getInt(i++);
    int elementOneDmg = rs.getInt(i++);
    int elementTwoDmg = rs.getInt(i++);
    int elementThreeDmg = rs.getInt(i++);
    int elementFourDmg = rs.getInt(i++);
    int elementFiveDmg = rs.getInt(i++);
    float hpLevelMultiplier = rs.getFloat(i++);
    float attackLevelMultiplier = rs.getInt(i++);
    int maxLevel = rs.getInt(i++);
    int evolutionMonsterId = rs.getInt(i++);

    String carrotRecruited = rs.getString(i++);
    String carrotDefeated = rs.getString(i++);
    String carrotEvolved = rs.getString(i++);
    String description = rs.getString(i++);
    
    Monster monster = new Monster(id, name, monsterGroup, quality,
    		evolutionLevel, displayName, element, baseHp, imageName,
    		numPuzzlePieces, elementOneDmg, elementTwoDmg, elementThreeDmg,
    		elementFourDmg, elementFiveDmg, hpLevelMultiplier,
    		attackLevelMultiplier, maxLevel, evolutionMonsterId,
    		carrotRecruited, carrotDefeated, carrotEvolved, description);

    return monster;
  }
}
