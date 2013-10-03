package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Equipment;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.InfoProto.FullEquipProto.EquipType;
import com.lvl6.proto.InfoProto.FullEquipProto.Rarity;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class EquipmentRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Equipment> equipIdToEquipment;

  private static final String TABLE_NAME = DBConstants.TABLE_EQUIPMENT;

  public static Map<Integer, Equipment> getEquipmentIdsToEquipment() {
    log.debug("retrieving all equipment data");
    if (equipIdToEquipment == null) {
      setStaticEquipIdsToEquipment();
    }
    return equipIdToEquipment;
  }

//  public static List<Equipment> getAllEquipmentForClassType(EquipClassType classtype) {
//    log.debug("retrieving all armory equipment for class type " + classtype);
//    if (equipIdToEquipment == null) {
//      setStaticEquipIdsToEquipment();
//    }
//    List <Equipment> equips = new ArrayList<Equipment>();
//    for (Integer equipId : equipIdToEquipment.keySet()) {
//      Equipment equip = equipIdToEquipment.get(equipId);
//      if (equip.getClassType() == classtype || equip.getClassType() == EquipClassType.ALL_AMULET) {
//        equips.add(equip);
//      }
//    }
//    return equips;
//  }

  public static Map<Integer, Equipment> getEquipmentIdsToEquipment(List<Integer> equipIds) {
    log.debug("retrieving equipment with ids " + equipIds);
    if (equipIdToEquipment == null) {
      setStaticEquipIdsToEquipment();
    }
    log.debug("equipIdToEquipment is " + equipIdToEquipment);
    Map<Integer, Equipment> toreturn = new HashMap<Integer, Equipment>();
    for (Integer equipId : equipIds) {
      toreturn.put(equipId,  equipIdToEquipment.get(equipId));
    }
    return toreturn;
  }

  private static void setStaticEquipIdsToEquipment() {
    log.debug("setting static map of equipIds to equipment");
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
      if (rs != null) {
        try {
          rs.last();
          rs.beforeFirst();
          Map <Integer, Equipment> equipIdToEquipmentTemp = new HashMap<Integer, Equipment>();
          while(rs.next()) {  //should only be one
            Equipment equip = convertRSRowToEquipment(rs);
            if (equip != null)
              equipIdToEquipmentTemp.put(equip.getId(), equip);
          }
          equipIdToEquipment = equipIdToEquipmentTemp;
        } catch (SQLException e) {
          log.error("problem with database call.", e);
          
        }
      }    
    }
    DBConnection.get().close(rs, null, conn);
  }
  
  public static void reload() {
    setStaticEquipIdsToEquipment();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Equipment convertRSRowToEquipment(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String name = rs.getString(i++);
    EquipType type = EquipType.valueOf(rs.getInt(i++));
    String description = rs.getString(i++);
    int attackBoost = rs.getInt(i++);
    int defenseBoost = rs.getInt(i++);
    int minLevel = rs.getInt(i++);
    Rarity rarity = Rarity.valueOf(rs.getInt(i++));
    float chanceOfForgeFailureBase = rs.getFloat(i++);
    int minutesToAttemptForgeBase = rs.getInt(i++);
    int maxDurability = rs.getInt(i++);
    int constantOne = rs.getInt(i++);
    int constantTwo = rs.getInt(i++);
    int constantThree = rs.getInt(i++);
    int constantFour = rs.getInt(i++);
    int constantFive = rs.getInt(i++);
    int constantSix = rs.getInt(i++);
    int constantSeven = rs.getInt(i++);
    int constantEight = rs.getInt(i++);
    int constantNine =rs.getInt(i++);
    
    
    Equipment equip = new Equipment(id, name, type, description,
    		attackBoost, defenseBoost, minLevel, rarity,
    		chanceOfForgeFailureBase, minutesToAttemptForgeBase,
    		maxDurability, constantOne, constantTwo, constantThree,
    		constantFour, constantFive, constantSix, constantSeven,
    		constantEight, constantNine);

    return equip;
  }
}
