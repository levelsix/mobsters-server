package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ItemForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class ItemForUserRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_ITEM_FOR_USER;
  
  
  public static Map<Integer, ItemForUser> getSpecificOrAllUserItems(int userId,
  		Collection<Integer> itemIds) {
  	StringBuilder querySb = new StringBuilder();
  	querySb.append("SELECT * FROM ");
  	querySb.append(TABLE_NAME);
  	querySb.append(" WHERE ");
  	querySb.append(DBConstants.ITEM_FOR_USER__USER_ID);
  	querySb.append("=?");
  	List <Object> values = new ArrayList<Object>();
    values.add(userId);
    
    if (null != itemIds && !itemIds.isEmpty()) {
    	querySb.append(" AND ");
    	querySb.append(DBConstants.ITEM_FOR_USER__ITEM_ID);
    	querySb.append(" IN (");

    	int amount = itemIds.size();
    	List<String> questions = Collections.nCopies(amount, "?");
    	String questionMarkStr = StringUtils.csvList(questions);

    	querySb.append(questionMarkStr);
    	querySb.append(");");
    	values.addAll(itemIds);
    }
    
    String query = querySb.toString();
    log.info("query=" + query + "\t values=" + values);
    
  	
    Connection conn = null;
    ResultSet rs = null;
    Map<Integer, ItemForUser> ifuIdsToUserItems= new HashMap<Integer, ItemForUser>();
    try {
  		
  		conn = DBConnection.get().getConnection();
  		rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				ItemForUser ifu = convertRSRowToItemForUser(rs);
  				
  				if (null != ifu) {
  					int ifuId = ifu.getItemId();
  					ifuIdsToUserItems.put(ifuId, ifu);
  				}
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	} catch (Exception e) {
  		log.error("sql query wrong 2", e);
  	} finally {
  		DBConnection.get().close(rs, null, conn);
  	}
    return ifuIdsToUserItems;
  }
  
  private static ItemForUser convertRSRowToItemForUser(ResultSet rs) throws SQLException {
    int i = 1;
    int userId = rs.getInt(DBConstants.ITEM_FOR_USER__USER_ID);
    int itemId = rs.getInt(DBConstants.ITEM_FOR_USER__ITEM_ID);
    int quantity = rs.getInt(DBConstants.ITEM_FOR_USER__QUANTITY);
    
    return new ItemForUser(userId, itemId, quantity);
  }
}
