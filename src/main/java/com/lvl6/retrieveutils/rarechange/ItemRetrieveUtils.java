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
  
  public static Item getItemForId(int itemId) {
	  if (null == itemIdsToItems) {
		  setStaticItemIdsToItems();
	  }
	  
	  if (!itemIdsToItems.containsKey(itemId)) {
		  log.error("no item for id=" + itemId);
		  return null;
	  }
	  return itemIdsToItems.get(itemId);
  }

  public static Map<Integer, Item> getItemsForIds(Collection<Integer> ids) {
  	if (null == itemIdsToItems) {
  		setStaticItemIdsToItems();
  	}
  	Map<Integer, Item> returnMap = new HashMap<Integer, Item>();
  	
  	for (int id : ids) {
  		Item tsm = getItemForId(id);
  		returnMap.put(id, tsm);
  	}
  	return returnMap;
  }

  private static void setStaticItemIdsToItems() {
    log.debug("setting static map of item ids to items");

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
			        Item item = convertRSRowToItem(rs);
			        
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
  private static Item convertRSRowToItem(ResultSet rs) throws SQLException {
    int id = rs.getInt(DBConstants.ITEM__ID);
    String name = rs.getString(DBConstants.ITEM__NAME);
    String imgName = rs.getString(DBConstants.ITEM__IMG_NAME);
    String itemType = rs.getString(DBConstants.ITEM__ITEM_TYPE);
    
    if (null != itemType) {
    	String newItemType = itemType.trim().toUpperCase();
    	if (!itemType.equals(newItemType)) {
    		log.error(String.format(
    			"itemType incorrect: %s, id=%s",
    			itemType, id));
    		itemType = newItemType;
    	}
    }
    int staticDataId = rs.getInt(DBConstants.ITEM__STATIC_DATA_ID);
    
    Item item = new Item(id, name, imgName, itemType, staticDataId);
    return item;
  }
}
