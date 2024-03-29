//package com.lvl6.retrieveutils;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.PrivateChatPost;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//
//@Component @DependsOn("gameServer") public class PrivateChatPostRetrieveUtils {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  private static final String TABLE_NAME = DBConstants.TABLE_USER_PRIVATE_CHAT_POST;
//
//  
//  public static List<PrivateChatPost> getPrivateChatPostsBetweenUsersBeforePostId(
//      int limit, int postId, int userOne, int userTwo) {
//    log.info("retrieving " + limit + " private chat posts before certain postId "
//      + postId + " for userOne " + userOne + " and userTwo" + userTwo);
//    
//    String query = "";
//    List<Object> values = new ArrayList<Object>();
//    query += 
//        "SELECT * " +
//    		"FROM " + TABLE_NAME + " " +
//    		"WHERE " + DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID + " IN (?,?) ";
//    values.add(userOne);
//    values.add(userTwo);
//    query +=
//        "AND " + DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID + " IN (?,?) ";
//    values.add(userOne);
//    values.add(userTwo);
//    
//    //in case no before post id is specified
//    if (ControllerConstants.NOT_SET != postId) {
//      query += "AND " + DBConstants.USER_PRIVATE_CHAT_POSTS__ID + " < ? ";
//      values.add(postId);
//    }
//    
//    query += "ORDER BY " + DBConstants.USER_PRIVATE_CHAT_POSTS__ID + " DESC  LIMIT ?";
//    values.add(limit);
//    
//    Connection conn = null;
//		ResultSet rs = null;
//		List<PrivateChatPost> privateChatPosts = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			privateChatPosts = convertRSToPrivateChatPosts(rs);
//		} catch (Exception e) {
//    	log.error("private chat post retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return privateChatPosts;
//  }
//  
//  public static Map<Integer, PrivateChatPost> getMostRecentPrivateChatPostsByOrToUser(
//      int userId, boolean isRecipient, int limit) {
//    log.debug("retrieving most recent private chat posts. userId "
//      + userId + " isRecipient=" + isRecipient);
//    
//    String otherPersonColumn = null;
//    String column = null;
//    
//    if (isRecipient) {
//      otherPersonColumn = DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID;
//      column = DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID;
//    } else {
//      otherPersonColumn = DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID;
//      column = DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID;
//    }
//    List<Object> values = new ArrayList<Object>();
//    String query = "";
//    String subquery = "";
//
//    //get last post id between specified user and person said user chatted with
//    subquery +=
//        "(SELECT max(" + DBConstants.USER_PRIVATE_CHAT_POSTS__ID + ") as id " + 
//        "FROM " + TABLE_NAME + " " +
//        "WHERE " + column + "=? " +
//        "GROUP BY " + otherPersonColumn + ")";
//    values.add(userId);
//    
//    //get the actual posts to those ids
//    query +=
//        "SELECT pcp.* " +
//        "FROM " + subquery + " as idList " +
//        "LEFT JOIN " +
//                  TABLE_NAME + " as pcp " +
//            "ON idList.id=pcp.id " +
//        "ORDER BY pcp.time_of_post DESC " +
//        "LIMIT ?";
//    values.add(limit);
//    
//    
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Integer, PrivateChatPost> privateChatPosts = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			privateChatPosts = convertRSToMapIdToPrivateChatPost(rs);
//		} catch (Exception e) {
//    	log.error("private chat post retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return privateChatPosts;
//  }
//  
//  
//  private static List<PrivateChatPost> convertRSToPrivateChatPosts(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        List<PrivateChatPost> wallPosts = new ArrayList<PrivateChatPost>();
//        while(rs.next()) {
//          PrivateChatPost pwp = convertRSRowToPrivateChatPost(rs);
//          if (pwp != null) wallPosts.add(pwp);
//        }
//        return wallPosts;
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//
//  private static Map<Integer, PrivateChatPost> convertRSToMapIdToPrivateChatPost(ResultSet rs) {
//    if (null != rs) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        Map<Integer, PrivateChatPost> idsToPrivateChatPosts =
//            new HashMap<Integer, PrivateChatPost>();
//        
//        while(rs.next()) {
//          PrivateChatPost pcp = convertRSRowToPrivateChatPost(rs);
//          if (null != pcp) {
//            int id = pcp.getId();
//            idsToPrivateChatPosts.put(id, pcp);
//          }
//        }
//        
//        return idsToPrivateChatPosts;
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//      }
//    }
//    return null;
//  }
//
//  private static PrivateChatPost convertRSRowToPrivateChatPost(ResultSet rs)
//      throws SQLException {
//    int i = 1;
//    int id = rs.getInt(DBConstants.USER_PRIVATE_CHAT_POSTS__ID);
//    int posterId = rs.getInt(DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID);
//    int recipientId = rs.getInt(DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID);
//    Date timeOfPost = new Date(rs.getTimestamp(DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST).getTime());
//    String content = rs.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__CONTENT);
//
//    PrivateChatPost pwp = new PrivateChatPost(id, posterId, recipientId,
//        timeOfPost, content);
//  
//    return pwp;
//  }
//  
//}
