package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class UserFacebookInviteForSlotRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
  
  public static UserFacebookInviteForSlot getInviteForId(int inviteId) {
    Connection conn = DBConnection.get().getConnection();
    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    absoluteConditionParams.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID, inviteId);
    
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
    UserFacebookInviteForSlot invite = convertRSToInvite(rs);
    return invite;
  }
  
  public static Map<Integer, UserFacebookInviteForSlot> getInviteIdsToInvitesForUserId(int userId) {
  	TreeMap<String, Object> paramsToVals = new TreeMap<String, Object>();
  	paramsToVals.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID, userId);
  	
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
    Map<Integer, UserFacebookInviteForSlot> idsToInvites = convertRSToInviteIdsToInvites(rs);
    return idsToInvites;
  }
  
  public static List<String> getUniqueRecipientFacebookIdsForInviterId(int userId) {
  	StringBuffer querySb = new StringBuffer();
  	querySb.append("SELECT DISTINCT(");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID);
  	querySb.append(") FROM ");
  	querySb.append(TABLE_NAME);
  	querySb.append(" WHERE ");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
  	querySb.append("=?");
  	String query = querySb.toString();
  	
  	log.info("query=" + query);
  	List<Object> values = new ArrayList<Object>();
  	values.add(userId);
  	Connection conn = DBConnection.get().getConnection();
  	ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
  	List<String> recipientIds = convertRSToStrings(rs);
  	return recipientIds;
  }
  
  private static UserFacebookInviteForSlot convertRSToInvite(ResultSet rs) {
    List<UserFacebookInviteForSlot> utList = new ArrayList<UserFacebookInviteForSlot>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
        	UserFacebookInviteForSlot invite = convertRSRowToInvite(rs);
          utList.add(invite);
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    
    //error checking. There should only be one row in user_task table for any user
    if (utList.isEmpty()) {
      return null;
    } else {
      if (utList.size() > 1) {
        log.error("unexpected error: user has more than one user_task. userTasks=" +
            utList);
      }
      return utList.get(0);
    }
  }
  
  private static Map<Integer, UserFacebookInviteForSlot> convertRSToInviteIdsToInvites(ResultSet rs) {
  	Map<Integer, UserFacebookInviteForSlot> idsToInvites =
  			new HashMap<Integer, UserFacebookInviteForSlot>();
  	
  	if (null != rs) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				UserFacebookInviteForSlot invite = convertRSRowToInvite(rs); 
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
  
  private static UserFacebookInviteForSlot convertRSRowToInvite(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int inviterUserId = rs.getInt(i++);
    String recipientFacebookId = rs.getString(i++);
    
    Date timeOfInvite = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		timeOfInvite = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: start_date is null. id=" + id + " inviterId=" +
    			inviterUserId + " recipientFacebookId=" + recipientFacebookId, e);
    }
    boolean accepted = rs.getBoolean(i++);
    
    UserFacebookInviteForSlot invite = new UserFacebookInviteForSlot(id,
    		inviterUserId, recipientFacebookId, timeOfInvite, accepted); 
    return invite;
  }
}
