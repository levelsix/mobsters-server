package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.CoordinatePair;
import com.lvl6.info.StructureForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.StructureProto.StructOrientation;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class StructureForUserRetrieveUtils {

  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_FOR_USER;

  public List<StructureForUser> getUserStructsForUser(int userId) {
    log.debug("retrieving user structs for userId " + userId);
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
    List<StructureForUser> userStructs = convertRSToUserStructs(rs);
    DBConnection.get().close(rs, null, conn);
    return userStructs;
  }

  
  ////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
  public Map<Integer, List<StructureForUser>> getStructIdsToUserStructsForUser(int userId) {
    log.debug("retrieving map of struct id to userstructs for userId " + userId);
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
    Map<Integer, List<StructureForUser>> structIdToUserStructs = convertRSToStructIdsToUserStructs(rs);
    DBConnection.get().close(rs, null, conn);
    return structIdToUserStructs;
  }

  ////@Cacheable(value="specificUserStruct", key="#userStructId")
  public StructureForUser getSpecificUserStruct(int userStructId) {
    log.debug("retrieving user struct with id " + userStructId);
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsById(conn, userStructId, TABLE_NAME);
    StructureForUser userStruct = convertRSSingleToUserStructs(rs);
    DBConnection.get().close(rs, null, conn);
    return userStruct;
  }

  
  public List<StructureForUser> getSpecificOrAllUserStructsForUser(int userId,
  		List<Integer> userStructIds) {
    
  	StringBuffer querySb = new StringBuffer();
    querySb.append("SELECT * FROM ");
    querySb.append(TABLE_NAME); 
    querySb.append(" WHERE ");
    querySb.append(DBConstants.STRUCTURE_FOR_USER__USER_ID);
    querySb.append("=?");
    List <Object> values = new ArrayList<Object>();
    values.add(userId);
    
    //if user didn't give any userStructIds then get all the user's structs
    //else get the specific ids
    if (userStructIds != null && !userStructIds.isEmpty() ) {
    	log.debug("retrieving userStructs with ids " + userStructIds);
    	querySb.append(" AND ");
    	querySb.append(DBConstants.STRUCTURE_FOR_USER__ID);
    	querySb.append(" IN (");
    	
    	int amount = userStructIds.size();
    	List<String> questions = Collections.nCopies(amount, "?");
    	String questionMarkStr = StringUtils.csvList(questions);
    	
    	querySb.append(questionMarkStr);
    	querySb.append(");");
    	values.addAll(userStructIds);
    }

    String query = querySb.toString();
    log.info("query=" + query + "\t values=" + values);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    List<StructureForUser> userStructs = convertRSToUserStructs(rs);
    DBConnection.get().close(rs, null, conn);
    return userStructs;
  }

  private List<StructureForUser> convertRSToUserStructs(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        List<StructureForUser> userStructs = new ArrayList<StructureForUser>();
        while(rs.next()) {
          userStructs.add(convertRSRowToUserStruct(rs));
        }
        return userStructs;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }


  private Map<Integer, List<StructureForUser>> convertRSToStructIdsToUserStructs(
      ResultSet rs) {
  	Map<Integer, List<StructureForUser>> structIdsToUserStructs = new HashMap<Integer, List<StructureForUser>>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
          StructureForUser userStruct = convertRSRowToUserStruct(rs);
          List<StructureForUser> userStructsForStructId = structIdsToUserStructs.get(userStruct.getStructId());
          if (userStructsForStructId != null) {
            userStructsForStructId.add(userStruct);
          } else {
            List<StructureForUser> userStructs = new ArrayList<StructureForUser>();
            userStructs.add(userStruct);
            structIdsToUserStructs.put(userStruct.getStructId(), userStructs);
          }
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return structIdsToUserStructs;
  }

  private StructureForUser convertRSSingleToUserStructs(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
          return convertRSRowToUserStruct(rs);
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private StructureForUser convertRSRowToUserStruct(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int userId = rs.getInt(i++);
    int structId = rs.getInt(i++);
    
    Date lastRetrieved = null;
    Timestamp ts = rs.getTimestamp(i++);
    if (!rs.wasNull()) {
      lastRetrieved = new Date(ts.getTime());
    }
    
    CoordinatePair coordinates = new CoordinatePair(rs.getInt(i++), rs.getInt(i++));
//    int level = rs.getInt(i++);
    Date purchaseTime = new Date(rs.getTimestamp(i++).getTime());
    
    boolean isComplete = rs.getBoolean(i++);
    StructOrientation orientation = StructOrientation.valueOf(rs.getInt(i++));
    
    Date upgradeTime = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		upgradeTime = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: maybe upgradeTime is null. id=" + id + " userId=" + userId);
		}

    return new StructureForUser(id, userId, structId, lastRetrieved, coordinates,
    		purchaseTime, isComplete, orientation, upgradeTime);
  }

}
