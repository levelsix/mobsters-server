//package com.lvl6.retrieveutils;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.TreeMap;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.ClanChatPost;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//
//@Component @DependsOn("gameServer") public class ClanChatPostRetrieveUtils {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  private static final String TABLE_NAME = DBConstants.TABLE_CLAN_CHAT_POST;
//
//  /*
//  public static ClanChatPost getSpecificActiveClanChatPost(int wallPostId) {
//    log.debug("retrieving clan chat post with id " + wallPostId);
//    
//    ClanChatPost clanChatPost = null;
//    Connection conn = null;
//    ResultSet rs = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsById(conn, wallPostId, TABLE_NAME);
//			clanChatPost = convertRSToSingleClanChatPost(rs);
//			DBConnection.get().close(rs, null, conn);
//		} catch (Exception e) {
//    	log.error("clan chat post retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return clanChatPost;
//  }*/
//
//  /*
//  public static List<ClanChatPost> getMostRecentActiveClanChatPostsForClanBeforePostId(int limit, int postId, int clanId) {
//    log.debug("retrieving " + limit + " player wall posts before certain postId " + postId + " for clan " + clanId);
//    TreeMap <String, Object> lessThanParamsToVals = new TreeMap<String, Object>();
//    lessThanParamsToVals.put(DBConstants.CLAN_CHAT_POST__ID, postId);
//    
//    TreeMap <String, Object> absoluteParams = new TreeMap<String, Object>();
//    absoluteParams.put(DBConstants.CLAN_CHAT_POST__CLAN_ID, clanId);
//    
//    List<ClanChatPost> clanChatPosts = new ArrayList<ClanChatPost>();
//    Connection conn = null;
//    ResultSet rs = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsAbsoluteAndOrderbydescLimitLessthan(conn, absoluteParams, TABLE_NAME, DBConstants.CLAN_CHAT_POST__ID, limit, lessThanParamsToVals);
//			clanChatPosts = convertRSToClanChatPosts(rs);
//		} catch (Exception e) {
//    	log.error("clan chat post retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return clanChatPosts;
//  }*/
//  
//  public static List<ClanChatPost> getMostRecentClanChatPostsForClan(int limit, int clanId) {
//    log.debug("retrieving " + limit + " clan wall posts for clan " + clanId);
//    
//    TreeMap <String, Object> absoluteParams = new TreeMap<String, Object>();
//    absoluteParams.put(DBConstants.CLAN_CHAT_POST__CLAN_ID, clanId);
//    
//    List<ClanChatPost> clanChatPosts = new ArrayList<ClanChatPost>();
//    Connection conn = null;
//    ResultSet rs = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsAbsoluteAndOrderbydescLimit(conn, absoluteParams, TABLE_NAME, DBConstants.CLAN_CHAT_POST__ID, limit);
//			clanChatPosts = convertRSToClanChatPosts(rs);
//		} catch (Exception e) {
//    	log.error("clan chat post retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return clanChatPosts;
//  }
//  
//  private static List<ClanChatPost> convertRSToClanChatPosts(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        List<ClanChatPost> wallPosts = new ArrayList<ClanChatPost>();
//        while(rs.next()) {
//          ClanChatPost pwp = convertRSRowToClanChatPost(rs);
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
//  private static ClanChatPost convertRSToSingleClanChatPost(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        while(rs.next()) {
//          ClanChatPost pwp = convertRSRowToClanChatPost(rs);
//          return pwp;
//        }
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//
//  private static ClanChatPost convertRSRowToClanChatPost(ResultSet rs) throws SQLException {
//    int i = 1;
//    int id = rs.getInt(DBConstants.CLAN_CHAT_POST__ID);
//    int posterId = rs.getInt(DBConstants.CLAN_CHAT_POST__POSTER_ID);
//    int clanId = rs.getInt(DBConstants.CLAN_CHAT_POST__CLAN_ID);
//    Date timeOfPost = new Date(rs.getTimestamp(DBConstants.CLAN_CHAT_POST__TIME_OF_POST).getTime());
//    String content = rs.getString(DBConstants.CLAN_CHAT_POST__CONTENT);
//
//    ClanChatPost pwp = new ClanChatPost(id, posterId, clanId, timeOfPost, content);
//  
//    return pwp;
//  }
//}
