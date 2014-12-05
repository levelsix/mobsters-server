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

import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class MonsterLevelInfoRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Map<Integer, MonsterLevelInfo>> monsterIdToLevelToInfo;
  
  private static Map<Integer, Map<Integer, MonsterLevelInfo>> enumeratedPartialMonsterLevelInfo;

  private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_LEVEL_INFO_CONFIG;

  public static Map<Integer, Map<Integer, MonsterLevelInfo>> getMonsterIdToLevelToInfo() {
    log.debug("retrieving all monster lvl info data");
    if (monsterIdToLevelToInfo == null) {
      setStaticMonsterIdToLevelToInfo();
    }
    return monsterIdToLevelToInfo;
  }
  
  //TODO: return enumeratedCompleteMonsterLevelInfo for a monster,
  //similar to getAllPartialMonsterLevelInfo() but complete data
  public static Map<Integer, MonsterLevelInfo> getMonsterLevelInfoForMonsterId(int id) {
  	log.debug(String.format(
  		"retrieving MonsterLevelInfo for monster id=%s",
  		id));
  	if (null == monsterIdToLevelToInfo) {
  		setStaticMonsterIdToLevelToInfo();
  	}
  	
  	if (!monsterIdToLevelToInfo.containsKey(id)) {
  		log.error(String.format(
  			"no MonsterLevelInfo for monsterId=%s",
  			id));
  	}
  	
  	return monsterIdToLevelToInfo.get(id);
  }
  
  public static Map<Integer, MonsterLevelInfo> getAllPartialMonsterLevelInfo(int id) {
  	log.debug(String.format(
  		"retrieving MonsterLevelInfo for monster id=%s",
  		id));
  	if (null == enumeratedPartialMonsterLevelInfo) {
  		setStaticMonsterIdToLevelToInfo();
  	}
  	
  	if (!enumeratedPartialMonsterLevelInfo.containsKey(id)) {
  		log.error(String.format(
  			"no MonsterLevelInfo for monsterId=%s",
  			id));
  	}
  	
  	return enumeratedPartialMonsterLevelInfo.get(id);
  }

  private static void setStaticMonsterIdToLevelToInfo() {
    log.debug("setting static map of monster id to level to info");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Map<Integer, MonsterLevelInfo>> monsterIdToLevelToTemp =
			      		new HashMap<Integer, Map<Integer, MonsterLevelInfo>>();
			      while(rs.next()) {
			        MonsterLevelInfo mefe = convertRSRowToMonsterLevelInfo(rs);
			        if (mefe == null) {
			        	continue;
			        }
			        
			        int monsterId = mefe.getMonsterId();
			        if (!monsterIdToLevelToTemp.containsKey(monsterId)) {
			        	//base case where have not encountered this monster id before
			        	Map<Integer, MonsterLevelInfo> levelToFeederExpTemp =
			        			new HashMap<Integer, MonsterLevelInfo>();
			        	monsterIdToLevelToTemp.put(monsterId, levelToFeederExpTemp);
			        }
			        
			        Map<Integer, MonsterLevelInfo> levelToFeederExpTemp =
			        		monsterIdToLevelToTemp.get(monsterId);
			        
			        int level = mefe.getLevel();
			        levelToFeederExpTemp.put(level, mefe);

			      }
			      monsterIdToLevelToInfo = monsterIdToLevelToTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
			
			computePartials();
		} catch (Exception e) {
    	log.error("MonsterLevelInfo retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }
  
  //since only given first and last monsterLevelInfo, need to compute
  //inbetween values
  private static void computePartials() {
	  
	  Map<Integer, Map<Integer, MonsterLevelInfo>> allPartialMonsterLevelInfo =
		  new HashMap<Integer, Map<Integer, MonsterLevelInfo>>();
	  
	  for (Integer monsterId : monsterIdToLevelToInfo.keySet()) {
		  Map<Integer, MonsterLevelInfo> lvlToInfo =
			  monsterIdToLevelToInfo.get(monsterId);
		  
		  Map<Integer, MonsterLevelInfo> allLvlToPartialInfo =
			  new HashMap<Integer, MonsterLevelInfo>();
			  
		  List<Integer> orderedLvls = new ArrayList<Integer>(
			  lvlToInfo.keySet());
		  
		  if (orderedLvls.size() < 2) {
			  allLvlToPartialInfo.putAll(lvlToInfo);
			  log.warn(String.format(
				  "monsterId=%s has only one specified lvl=%s",
				  monsterId, orderedLvls));
			  continue;
		  } else if (orderedLvls.size() > 2) {
			  log.warn(String.format(
				  "monsterId=%s has more than one specified lvl=%s",
				  monsterId, orderedLvls));
			  continue;
		  }
		  
		  
		  int lvl1 = orderedLvls.get(0);
		  int lvl2 = orderedLvls.get(1);
		  int minLvl = Math.min(lvl1, lvl2);
		  int maxLvl = Math.max(lvl1, lvl2);
		  
		  MonsterLevelInfo minLvlInfo = lvlToInfo.get(minLvl); 
		  MonsterLevelInfo maxLvlInfo = lvlToInfo.get(maxLvl);
		  
		  //given min and max range, generate MonsterLevelInfo data inbetween
		  allLvlToPartialInfo.put(minLvl, minLvlInfo);
		  
		  //due to hackery the max lvl info needs to be calculated as well,
		  //specifically exp. The exp value is the max value
		  //corresponding to the max lvl of 99, not max.getLevel()
		  //allLvlToPartialInfo.put(maxLvl, maxLvlInfo);
		  
		  for (int curLvl = minLvl + 1; curLvl <= maxLvl; curLvl++) {
			  MonsterLevelInfo nextLvlInfo = new MonsterLevelInfo();
			  int hp = calculateHp(minLvlInfo, maxLvlInfo, curLvl);
			  nextLvlInfo.setHp(hp);
			  
			  int exp = calculateExp(maxLvlInfo, curLvl);
			  nextLvlInfo.setCurLvlRequiredExp(exp);
			  
//			  if (ControllerConstants.TUTORIAL__MARK_Z_MONSTER_ID == monsterId) {
//				  log.info(String.format(
//					  "hp=%s, exp=%s, currentLvl=%s",
//					  hp, exp, curLvl));
//			  }
			  
			  allLvlToPartialInfo.put(curLvl, nextLvlInfo);
		  }
		  
		  allPartialMonsterLevelInfo.put(monsterId, allLvlToPartialInfo);
	  }
	  enumeratedPartialMonsterLevelInfo = allPartialMonsterLevelInfo;
  }

  private static int calculateHp(MonsterLevelInfo min, MonsterLevelInfo max, int currentLvl) {
	  
	  double base = ((double)(currentLvl-1))/(double)(max.getLevel()-1);
	  double hpDiff = (max.getHp()-min.getHp());
	  int hpOffset = (int) (hpDiff * Math.pow(base, max.getHpExponentBase()));
	  /*if (ControllerConstants.TUTORIAL__MARK_Z_MONSTER_ID == min.getMonsterId()) {
		  log.info(String.format(
			  "minInfo=%s, maxInfo=%s, curLvl=%s, base=%s, hpDiff=%s, hpOffset=%s, minHp=%s",
			  min, max, currentLvl, base, hpDiff, hpOffset, min.getHp()));
	  }*/
	  return (min.getHp()+hpOffset);
  }
  
  private static int calculateExp(MonsterLevelInfo max, int currentLvl) {
	  double base = ((double) (currentLvl-1))/((double) (max.getExpLvlDivisor()-1));
	  double multiplicand = Math.pow(base, max.getExpLvlExponent()); 
	  /*if (ControllerConstants.TUTORIAL__MARK_Z_MONSTER_ID == max.getMonsterId()) {
		  log.info(String.format(
			  "base=%s, resultMultiplicand=%s, currentLvl=%s",
			  base, multiplicand, currentLvl));
	  }*/
	  return  (int) Math.ceil(multiplicand * max.getCurLvlRequiredExp());
  }
  
  public static void reload() {
    setStaticMonsterIdToLevelToInfo();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static MonsterLevelInfo convertRSRowToMonsterLevelInfo(ResultSet rs) throws SQLException {
    int monsterId = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__MONSTER_ID);
    int level = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__LEVEL);
    int hp = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__HP);
//    int attack = rs.getInt(DBConstants.);
    int curLvlRequiredExp = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__CUR_LVL_REQUIRED_EXP);
    int feederExp = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__FEEDER_EXP);
    int fireDmg = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__FIRE_DMG);
    int grassDmg = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__GRASS_DMG);
    int waterDmg = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__WATER_DMG);
    int lightningDmg = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__LIGHTNING_DMG);
    int darknessDmg = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__DARKNESS_DMG);
    int rockDmg = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__ROCK_DMG);
    int speed = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__SPEED);
    float hpExponentBase = rs.getFloat(DBConstants.MONSTER_LEVEL_INFO__HP_EXPONENT_BASE);
    float dmgExponentBase = rs.getFloat(DBConstants.MONSTER_LEVEL_INFO__DMG_EXPONENT_BASE);
    float expLvlDivisor = rs.getFloat(DBConstants.MONSTER_LEVEL_INFO__EXP_LVL_DIVISOR);
    float expLvlExponent = rs.getFloat(DBConstants.MONSTER_LEVEL_INFO__EXP_LVL_EXPONENT);
    int sellAmount = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__SELL_AMOUNT);
    int teamCost = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__TEAM_COST);
    int costToFullyHeal = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__COST_TO_FULLY_HEAL);
    int secsToFullyHeal = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__SECS_TO_FULLY_HEAL);
    
    int enhanceCostPerFeeder = rs.getInt(DBConstants.MONSTER_LEVEL_INFO__ENHANCE_COST_PER_FEEDER);
    float enhanceCostExponent = rs.getFloat(DBConstants.MONSTER_LEVEL_INFO__ENHANCE_COST_EXPONENT);
    float enhanceExpPerSec = rs.getFloat(DBConstants.MONSTER_LEVEL_INFO__ENHANCE_EXP_PER_SEC);
    float enhanceExpPerSecExponent = rs.getFloat(DBConstants.MONSTER_LEVEL_INFO__ENHANCE_EXP_PER_SEC_EXPONENT);
    
    MonsterLevelInfo srs = new MonsterLevelInfo(monsterId, level, hp,
    		curLvlRequiredExp, feederExp, fireDmg, grassDmg, waterDmg,
    		lightningDmg, darknessDmg, rockDmg, speed, hpExponentBase,
    		dmgExponentBase, expLvlDivisor, expLvlExponent, sellAmount,
    		teamCost, costToFullyHeal, secsToFullyHeal,enhanceCostPerFeeder,
    		enhanceCostExponent, enhanceExpPerSec, enhanceExpPerSecExponent);
    
    return srs;
  }
}
