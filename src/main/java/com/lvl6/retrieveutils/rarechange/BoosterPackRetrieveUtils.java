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

import com.lvl6.info.BoosterPack;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class BoosterPackRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, BoosterPack> boosterPackIdsToBoosterPacks;

  private static final String TABLE_NAME = DBConstants.TABLE_BOOSTER_PACK_CONFIG;

  public static Map<Integer, BoosterPack> getBoosterPackIdsToBoosterPacks() {
    log.debug("retrieving all booster packs data map");
    if (boosterPackIdsToBoosterPacks == null) {
      setStaticBoosterPackIdsToBoosterPacks();
    }
    return boosterPackIdsToBoosterPacks;
  }
  
//  public static Map<Integer, BoosterPack> getBoosterPacksForBoosterPackIds(
//      List<Integer> boosterPackIds) {
//    if (boosterPackIdsToBoosterPacks == null) {
//      setStaticBoosterPackIdsToBoosterPacks();
//    }
//    Map<Integer, BoosterPack> returnValue = new HashMap<Integer, BoosterPack>();
//    for (int id : boosterPackIds) {
//      BoosterPack aPack = boosterPackIdsToBoosterPacks.get(id);
//      returnValue.put(id, aPack);
//    }
//    
//    return returnValue;
//  }

  public static BoosterPack getBoosterPackForBoosterPackId(int boosterPackId) {
    log.debug("retrieve booster pack data for booster pack " + boosterPackId);
    if (boosterPackIdsToBoosterPacks == null) {
      setStaticBoosterPackIdsToBoosterPacks();      
    }
    return boosterPackIdsToBoosterPacks.get(boosterPackId);
  }

  private static void setStaticBoosterPackIdsToBoosterPacks() {
    log.debug("setting static map of boosterPackIds to boosterPacks");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
    	if (conn != null) {
    		rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

    		if (rs != null) {
    			try {
    				rs.last();
    				rs.beforeFirst();
    				HashMap<Integer, BoosterPack> boosterPackIdsToBoosterPacksTemp = new HashMap<Integer, BoosterPack>();
    				while(rs.next()) {  //should only be one
    					BoosterPack boosterPack = convertRSRowToBoosterPack(rs);
    					if (boosterPack != null)
    						boosterPackIdsToBoosterPacksTemp.put(boosterPack.getId(), boosterPack);
    				}
    				boosterPackIdsToBoosterPacks = boosterPackIdsToBoosterPacksTemp;
    			} catch (SQLException e) {
    				log.error("problem with database call.", e);

    			}
    		}    
    	}
    } catch (Exception e) {
    	log.error("booster pack retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticBoosterPackIdsToBoosterPacks();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static BoosterPack convertRSRowToBoosterPack(ResultSet rs) throws SQLException {
    int id = rs.getInt(DBConstants.BOOSTER_PACK__ID);
    String name = rs.getString(DBConstants.BOOSTER_PACK__NAME);
    int gemPrice = rs.getInt(DBConstants.BOOSTER_PACK__GEM_PRICE);
    String listBackgroundImgName = rs.getString(DBConstants.BOOSTER_PACK__LIST_BACKGROUND_IMG_NAME);
    String listDescription = rs.getString(DBConstants.BOOSTER_PACK__LIST_DESCRIPTION);
    String navBarImgName = rs.getString(DBConstants.BOOSTER_PACK__NAV_BAR_IMG_NAME);
    String navTitleImgName = rs.getString(DBConstants.BOOSTER_PACK__NAV_TITLE_IMG_NAME);
    String machineImgName = rs.getString(DBConstants.BOOSTER_PACK__MACHINE_IMG_NAME);
    int expPerItem = 0;
    try {
		expPerItem = rs.getInt(DBConstants.BOOSTER_PACK__EXP_PER_ITEM);
	} catch (Exception e) {
		log.error("most likely db does not have column EXP_PER_ITEM.", e);
	}
    boolean displayToUser = rs.getBoolean(DBConstants.BOOSTER_PACK__DISPLAY_TO_USER);
    
    BoosterPack boosterPack = new BoosterPack(id, name, gemPrice,
    		listBackgroundImgName, listDescription, navBarImgName,
    		navTitleImgName, machineImgName, expPerItem, displayToUser);
    return boosterPack; 
  }
}
