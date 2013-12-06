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

import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class UserFacebookInviteForSlotRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
  
  public static UserFacebookInviteForSlot getInviteForId(int inviteId) {
    Connection conn = DBConnection.get().getConnection();
    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    absoluteConditionParams.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID, inviteId);
    
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
    UserFacebookInviteForSlot invite = convertRSToInvite(rs);
    DBConnection.get().close(rs, null, conn);
    return invite;
  }
  
  public static Map<Integer, UserFacebookInviteForSlot> getInviteIdsToInvitesForInviterUserId(
  		int userId, boolean acceptedInvitesOnly, boolean filterByRedeemed, boolean isRedeemed) {
  	StringBuilder querySb = new StringBuilder();
  	querySb.append("SELECT * FROM ");
  	querySb.append(TABLE_NAME);
  	querySb.append(" WHERE ");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
  	querySb.append("=?");
  	List<Object> values = new ArrayList<Object>();
  	values.add(userId);
  	
  	if (acceptedInvitesOnly) {
  		querySb.append(" AND ");
  		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED);
  		querySb.append(" IS NOT NULL");
  	}
  	if (filterByRedeemed) {
  		querySb.append(" AND ");
  		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__IS_REDEEMED);
  		querySb.append("=?");
  		values.add(isRedeemed);
  	}
  	String query = querySb.toString();
  	log.info("query=" + query + "\t values=" + values);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    Map<Integer, UserFacebookInviteForSlot> idsToInvites = convertRSToInviteIdsToInvites(rs);
    DBConnection.get().close(rs, null, conn);
    return idsToInvites;
  }
  
  //recipientFacebookId assumed to be not null
  public static Map<Integer, UserFacebookInviteForSlot> getSpecificOrAllInvitesForRecipient(
  		String recipientFacebookId, List<Integer> specificInviteIds) {
    
    StringBuilder querySb = new StringBuilder();
    querySb.append("SELECT * FROM ");
    querySb.append(TABLE_NAME); 
    querySb.append(" WHERE ");
    querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID);
    querySb.append("=?");
    List <Object> values = new ArrayList<Object>();
    values.add(recipientFacebookId);
    
    //if user didn't give any userStructIds then get all the user's structs
    if (null != specificInviteIds && !specificInviteIds.isEmpty()) {
    	log.debug("retrieving UserFacebookInviteForSlot with ids " + specificInviteIds);
    	querySb.append(" AND ");
    	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
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
    Map<Integer, UserFacebookInviteForSlot> idsToInvites = convertRSToInviteIdsToInvites(rs);
    DBConnection.get().close(rs, null, conn);
    return idsToInvites;
  }
  
  public static List<String> getUniqueRecipientFacebookIdsForInviterId(int userId) {
  	StringBuilder querySb = new StringBuilder();
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
    DBConnection.get().close(rs, null, conn);
  	return recipientIds;
  }
  
  public static List<Integer> getUniqueInviterUserIdsForRequesterId(String facebookId) {
  	StringBuilder querySb = new StringBuilder();
  	querySb.append("SELECT DISTINCT(");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
  	querySb.append(") FROM ");
  	querySb.append(TABLE_NAME);
  	querySb.append(" WHERE ");
  	querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID);
  	querySb.append("=?");
  	String query = querySb.toString();
  	
  	log.info("query=" + query);
  	List<Object> values = new ArrayList<Object>();
  	values.add(facebookId);
  	Connection conn = DBConnection.get().getConnection();
  	ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
  	List<Integer> userIds = convertRSToInts(rs);
    DBConnection.get().close(rs, null, conn);
  	return userIds;
  }
  
  private static UserFacebookInviteForSlot convertRSToInvite(ResultSet rs) {
  	if (rs != null) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			if (rs.next()) {  //should only be one
  				UserFacebookInviteForSlot invite = convertRSRowToInvite(rs);
  				return invite;
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);

  		}
  	}
  	return null;
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
  private static List<Integer> convertRSToInts(ResultSet rs) {
  	List<Integer> intList = new ArrayList<Integer>();
  	if (null != rs) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				int indexOfFirstAndOnlyColumn = 1;
  				int anInt = rs.getInt(indexOfFirstAndOnlyColumn); 
  				intList.add(anInt);
  			}
  		} catch(SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return intList;
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
    	log.error("db error: maybe timeOfInvite is null. id=" + id + " inviterId=" +
    			inviterUserId + " recipientFacebookId=" + recipientFacebookId, e);
    }
    
    Date timeAccepted = null; 
    try {
    		Timestamp ts = rs.getTimestamp(i++);
    		if (!rs.wasNull()) {
    			timeAccepted = new Date(ts.getTime());
    		}
    		
    } catch (Exception e) {
    	log.error("db error: maybe timeAccepted is null. id=" + id + " inviterId=" +
    			inviterUserId + " recipientFacebookId=" + recipientFacebookId, e);
    }
    
    int userStructId = rs.getInt(i++);
    int structFbLvl = rs.getInt(i++);
    boolean isRedeemed = rs.getBoolean(i++);
    
    Date timeRedeemed = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		timeRedeemed = new Date(ts.getTime());
    	}
    	
    } catch (Exception e) {
    	log.error("db error: maybe timeRedeemed is null. id=" + id + " inviterId=" +
    			inviterUserId + " recipientFacebookId=" + recipientFacebookId, e);
    }
    
    UserFacebookInviteForSlot invite = new UserFacebookInviteForSlot(id, inviterUserId,
    		recipientFacebookId, timeOfInvite, timeAccepted, userStructId, structFbLvl,
    		isRedeemed, timeRedeemed); 
    return invite;
  }
}
