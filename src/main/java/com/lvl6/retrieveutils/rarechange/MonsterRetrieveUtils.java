package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.Monster;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
public class MonsterRetrieveUtils {

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
    
    if (monsterIdsToMonsters.containsKey(monsterId)) {
    	return monsterIdsToMonsters.get(monsterId); 
    } else {
    	log.error("db error: no monster exists with id=" + monsterId);
    	return null; 
    }
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

  public static Monster getEvolvedFormForMonster(int monsterId) {
  	if (null == monsterIdsToMonsters) {
  		setStaticMonsterIdsToMonsters();
  	}
  	Monster m = monsterIdsToMonsters.get(monsterId);
  	
  	int evolvedId = m.getEvolutionMonsterId();
  	
  	return monsterIdsToMonsters.get(evolvedId);
  }

  private static void setStaticMonsterIdsToMonsters() {
    log.debug("setting static map of monsterIds to monsters");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
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
    } catch (Exception e) {
    	log.error("monster retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticMonsterIdsToMonsters();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Monster convertRSRowToMonster(ResultSet rs) throws SQLException {
    int id = rs.getInt(DBConstants.MONSTER__ID);
    String evolutionGroup = rs.getString(DBConstants.MONSTER__EVOLUTION_GROUP);
    String monsterGroup = rs.getString(DBConstants.MONSTER__MONSTER_GROUP);
    String quality = rs.getString(DBConstants.MONSTER__QUALITY);
    int evolutionLevel = rs.getInt(DBConstants.MONSTER__EVOLUTION_LEVEL);
    String displayName = rs.getString(DBConstants.MONSTER__DISPLAY_NAME);
    String element = rs.getString(DBConstants.MONSTER__ELEMENT);
    String imagePrefix = rs.getString(DBConstants.MONSTER__IMAGE_PREFIX);
    int numPuzzlePieces = rs.getInt(DBConstants.MONSTER__NUM_PUZZLE_PIECES);
    int minutesToCombinePieces = rs.getInt(DBConstants.MONSTER__MINUTES_TO_COMBINE_PIECES);
    int maxLevel = rs.getInt(DBConstants.MONSTER__MAX_LEVEL);

    int evolutionMonsterId = rs.getInt(DBConstants.MONSTER__EVOLUTION_MONSTER_ID);
    int evolutionCatalystMonsterId = rs.getInt(DBConstants.MONSTER__EVOLUTION_CATALYST_MONSTER_ID);
    int minutesToEvolve = rs.getInt(DBConstants.MONSTER__MINUTES_TO_EVOLVE);
    int numCatalystsRequired = rs.getInt(DBConstants.MONSTER__NUM_EVOLUTION_CATALYSTS);

    String carrotRecruited = rs.getString(DBConstants.MONSTER__CARROT_RECRUITED);
    String carrotDefeated = rs.getString(DBConstants.MONSTER__CARROT_DEFEATED);
    String carrotEvolved = rs.getString(DBConstants.MONSTER__CARROT_EVOLVED);
    String description = rs.getString(DBConstants.MONSTER__DESCRIPTION);
    
    int evolutionCost = rs.getInt(DBConstants.MONSTER__EVOLUTION_COST);
    
    String animationType = rs.getString(DBConstants.MONSTER__ANIMATION_TYPE);
    int verticalPixelOffset = rs.getInt(DBConstants.MONSTER__VERTICAL_PIXEL_OFFSET);
    String atkSoundFile = rs.getString(DBConstants.MONSTER__ATK_SOUND_FILE);
    int atkSoundAnimationFrame = rs.getInt(DBConstants.MONSTER__ATK_SOUND_ANIMATION_FRAME);
    int atkAnimationRepeatedFramesStart = rs.getInt(DBConstants.MONSTER__ATK_ANIMATION_REPEATED_FRAMES_START);
    int atkAnimationRepeatedFramesEnd = rs.getInt(DBConstants.MONSTER__ATK_ANIMATION_REPEATED_FRAMES_END);
    String shorterName = rs.getString(DBConstants.MONSTER__SHORT_NAME);
    float shadowScaleFactor = rs.getFloat(DBConstants.MONSTER__SHADOW_SCALE_FACTOR);
    int baseOffensiveSkillId = rs.getInt(DBConstants.MONSTER__BASE_OFFENSIVE_SKILL_ID);
    int baseDefensiveSkillId = rs.getInt(DBConstants.MONSTER__BASE_DEFENSIVE_SKILL_ID);
    
    Monster monster = new Monster(id, evolutionGroup, monsterGroup, quality, evolutionLevel,
    		displayName, element, imagePrefix, numPuzzlePieces, minutesToCombinePieces,
    		maxLevel, evolutionMonsterId, evolutionCatalystMonsterId, minutesToEvolve,
    		numCatalystsRequired, carrotRecruited, carrotDefeated, carrotEvolved,
    		description, evolutionCost, animationType, verticalPixelOffset, atkSoundFile,
    		atkSoundAnimationFrame, atkAnimationRepeatedFramesStart,
    		atkAnimationRepeatedFramesEnd, shorterName, shadowScaleFactor,
    		baseOffensiveSkillId, baseDefensiveSkillId);
    
    if (null != animationType) {
    	String newAnimationType = animationType.trim().toUpperCase();
    	if (!animationType.equals(newAnimationType)) {
    		log.error("monster's animation type has whitespace or is uncapitalized. actual=" +
    				animationType + "\t expected=" + newAnimationType);
    		monster.setAnimationType(newAnimationType);
    	}
    }
    
    return monster;
  }
}
