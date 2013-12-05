package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserFacebookInviteForSlotAccepted;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class UserFacebookInviteForSlotAcceptedRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED;
  
  public static UserFacebookInviteForSlotAccepted getInviteForId(int inviteId) {
    /*Connection conn = DBConnection.get().getConnection();
    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    absoluteConditionParams.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__ID, inviteId);
    
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
    UserFacebookInviteForSlotAccepted invite = convertRSToInvite(rs);
    DBConnection.get().close(rs, null, conn);
    return invite;*/
  	return null;
  }
  
  public static Map<Integer, UserFacebookInviteForSlotAccepted> getInviteIdsToInvitesForInviterUserId(int userId) {
  	/*TreeMap<String, Object> paramsToVals = new TreeMap<String, Object>();
  	paramsToVals.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__INVITER_USER_ID, userId);
  	
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
    Map<Integer, UserFacebookInviteForSlotAccepted> idsToInvites = convertRSToInviteIdsToInvites(rs);
    DBConnection.get().close(rs, null, conn);
    return idsToInvites;*/
  	return new HashMap<Integer, UserFacebookInviteForSlotAccepted>();
  }
  
  //recipientFacebookId assumed to be not null
  public static Map<Integer, UserFacebookInviteForSlotAccepted> getSpecificOrAllInvitesForRecipient(
  		String recipientFacebookId, List<Integer> specificInviteIds) {
    /*
    StringBuilder querySb = new StringBuilder();
    querySb.append("SELECT * FROM ");
    querySb.append(TABLE_NAME); 
    querySb.append(" WHERE ");
    querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__RECIPIENT_FACEBOOK_ID);
    querySb.append("=?");
    List <Object> values = new ArrayList<Object>();
    values.add(recipientFacebookId);
    
    //if user didn't give any userStructIds then get all the user's structs
    if (null != specificInviteIds && !specificInviteIds.isEmpty()) {
    	log.debug("retrieving UserFacebookInviteForSlotAccepted with ids " + specificInviteIds);
    	querySb.append(" AND ");
    	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__ID);
    	querySb.append(" IN (");

    	int amount = specificInviteIds.size();
    	List<String> questionMarkList = Collections.nCopies(amount, "?");
    	String questionMarkStr = StringUtils.csvList(questionMarkList);
    	
    	querySb.append(questionMarkStr);
    	querySb.append(");");
    	values.addAll(specificInviteIds);
    }
    
    String query = querySb.toString();
    log.info("query=" + query + "\t values=" + values);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    Map<Integer, UserFacebookInviteForSlotAccepted> idsToInvites = convertRSToInviteIdsToInvites(rs);
    DBConnection.get().close(rs, null, conn);
    return idsToInvites;
    */
  	return new HashMap<Integer, UserFacebookInviteForSlotAccepted>();
  }
  
  public static List<String> getUniqueRecipientFacebookIdsForInviterId(int userId) {
  	/*StringBuilder querySb = new StringBuilder();
  	querySb.append("SELECT DISTINCT(");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__RECIPIENT_FACEBOOK_ID);
  	querySb.append(") FROM ");
  	querySb.append(TABLE_NAME);
  	querySb.append(" WHERE ");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__INVITER_USER_ID);
  	querySb.append("=?");
  	String query = querySb.toString();
  	
  	log.info("query=" + query);
  	List<Object> values = new ArrayList<Object>();
  	values.add(userId);
  	Connection conn = DBConnection.get().getConnection();
  	ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
  	List<String> recipientIds = convertRSToStrings(rs);
    DBConnection.get().close(rs, null, conn);
  	return recipientIds;*/
  	return new ArrayList<String>();
  }
  
  public static Set<Integer> getUniqueInviterUserIdsForRequesterId(String facebookId) {
  	/*StringBuilder querySb = new StringBuilder();
  	querySb.append("SELECT DISTINCT(");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__INVITER_USER_ID);
  	querySb.append(") FROM ");
  	querySb.append(TABLE_NAME);
  	querySb.append(" WHERE ");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT_ACCEPTED__RECIPIENT_FACEBOOK_ID);
  	querySb.append("=?");
  	String query = querySb.toString();
  	
  	log.info("query=" + query);
  	List<Object> values = new ArrayList<Object>();
  	values.add(facebookId);
  	Connection conn = DBConnection.get().getConnection();
  	ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
  	Set<Integer> userIds = convertRSToInts(rs);
    DBConnection.get().close(rs, null, conn);
  	return userIds;*/
  	return new HashSet<Integer>();
  }
  
  private static UserFacebookInviteForSlotAccepted convertRSToInvite(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        if (rs.next()) {  //should only be one
        	UserFacebookInviteForSlotAccepted invite = convertRSRowToInvite(rs);
        	return invite;
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  
  private static Map<Integer, UserFacebookInviteForSlotAccepted> convertRSToInviteIdsToInvites(ResultSet rs) {
  	Map<Integer, UserFacebookInviteForSlotAccepted> idsToInvites =
  			new HashMap<Integer, UserFacebookInviteForSlotAccepted>();
  	
  	if (null != rs) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				UserFacebookInviteForSlotAccepted invite = convertRSRowToInvite(rs); 
  				int id = invite.getId();
  				idsToInvites.put(id, invite);
  			}
  		} catch(SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return idsToInvites;
  }
  
  private static List<String> convertRSToStrings(ResultSet rs) {
  	List<String> stringList = new ArrayList<String>();
  	if (null != rs) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				int indexOfFirstAndOnlyColumn = 1;
  				String aString = rs.getString(indexOfFirstAndOnlyColumn); 
  				stringList.add(aString);
  			}
  		} catch(SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return stringList;
  }
  private static Set<Integer> convertRSToInts(ResultSet rs) {
  	Set<Integer> intSet = new HashSet<Integer>();
  	if (null != rs) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				int indexOfFirstAndOnlyColumn = 1;
  				int anInt = rs.getInt(indexOfFirstAndOnlyColumn); 
  				intSet.add(anInt);
  			}
  		} catch(SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return intSet;
  }
  
  private static UserFacebookInviteForSlotAccepted convertRSRowToInvite(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int inviterUserId = rs.getInt(i++);
    String recipientFacebookId = rs.getString(i++);
    
    Timestamp ts = null;
    Date timeOfInvite = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		timeOfInvite = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: maybe timeOfInvite is null. id=" + id + " inviterId=" +
    			inviterUserId + " recipientFacebookId=" + recipientFacebookId, e);
    }
    
    Date timeAccepted = null; 
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		timeAccepted = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: maybe timeAccepted is null. id=" + id + " inviterId=" +
    			inviterUserId + " recipientFacebookId=" + recipientFacebookId, e);
    }
    
    int nthExtraSlotsViaFb = rs.getInt(i++);
    Date timeOfEntry = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		timeOfEntry = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: maybe timeAccepted is null. id=" + id + " inviterId=" +
    			inviterUserId + " recipientFacebookId=" + recipientFacebookId, e);
    }
    
    
    UserFacebookInviteForSlotAccepted invite = new UserFacebookInviteForSlotAccepted(
    		id, inviterUserId, recipientFacebookId, timeOfInvite, timeAccepted,
    		nthExtraSlotsViaFb, timeOfEntry);
    return invite;
  }
}
