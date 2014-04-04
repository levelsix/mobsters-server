package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Item;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ItemRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static Map<Integer, Item> itemIdsToItems;

  private static final String TABLE_NAME = DBConstants.TABLE_ITEM;
  
  public static Map<Integer, Item> getItemIdsToItems() {
  	if (null == itemIdsToItems) {
  		setStaticItemIdsToItems();
  	}
  	return itemIdsToItems;
  }

  public static Map<Integer, Item> getItemsForIds(Collection<Integer> ids) {
  	if (null == itemIdsToItems) {
  		setStaticItemIdsToItems();
  	}
  	Map<Integer, Item> returnMap = new HashMap<Integer, Item>();
  	
  	for (int id : ids) {
  		Item tsm = itemIdsToItems.get(id);
  		returnMap.put(id, tsm);
  	}
  	return returnMap;
  }

  private static void setStaticItemIdsToItems() {
    log.debug("setting static map of item ids to items");

    Random rand = new Random();
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, Item> itemIdsToItemsTemp =
			      		new HashMap<Integer, Item>();
			      
			      //loop through each row and convert it into a java object
			      while(rs.next()) {  
			        Item item = convertRSRowToItem(rs, rand);
			        
			        int itemId = item.getId();
			        itemIdsToItemsTemp.put(itemId, item);
			      }
			      
			      itemIdsToItems = itemIdsToItemsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("item retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
    setStaticItemIdsToItems();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static Item convertRSRowToItem(ResultSet rs, Random rand) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String name = rs.getString(i++);
    String imgName = rs.getString(i++);
    String borderImgName = rs.getString(i++);
    int blue = rs.getInt(i++);
    int green = rs.getInt(i++);
    int red = rs.getInt(i++);
    
    Item item = new Item(id, name, imgName, borderImgName, blue, green, red);
    return item;
  }
}
