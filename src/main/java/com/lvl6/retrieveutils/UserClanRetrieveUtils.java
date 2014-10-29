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
import java.util.TreeMap;

import com.lvl6.utils.utilmethods.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserClan;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class UserClanRetrieveUtils {

  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private final String TABLE_NAME = DBConstants.TABLE_CLAN_FOR_USER;

  public Map<Integer, UserClan> getUserClanForUsers(int clanId, List<Integer> userIds) {
  	StringBuilder querySb = new StringBuilder();
    querySb.append("SELECT * FROM ");
    querySb.append(TABLE_NAME);
    querySb.append(" WHERE ");
    querySb.append(DBConstants.CLAN_FOR_USER__CLAN_ID);
    querySb.append("=? AND ");
    querySb.append(DBConstants.CLAN_FOR_USER__USER_ID);
    querySb.append(" in (");
    
    int numQuestionMarks = userIds.size();
    List<String> questionMarks = Collections.nCopies(numQuestionMarks, "?");
    String questionMarkStr = com.lvl6.utils.utilmethods.StringUtils.csvList(questionMarks);
    querySb.append(questionMarkStr);
    querySb.append(");");
    
    List<Object> values = new ArrayList<Object>();
    values.add(clanId);
    values.addAll(userIds);
    
    String query = querySb.toString();
    log.info("user clan query=" + query + "\t values=" + values);
    
    Connection conn = null;
		ResultSet rs = null;
		Map<Integer, UserClan> userIdsToStatuses = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			userIdsToStatuses = convertRSToUserIdsToUserClans(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    	userIdsToStatuses = new HashMap<Integer, UserClan>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		//should not be null
    return userIdsToStatuses;
  }
  /*
  public List<UserClan> getUserClanMembersInClan(int clanId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
    paramsToVals.put(DBConstants.CLAN_FOR_USER__STATUS, UserClanStatus.MEMBER.getNumber());

    Connection conn = null;
		ResultSet rs = null;
		List<UserClan> userClans = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userClans = grabUserClansFromRS(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userClans;
  }

  public List<UserClan> getUserClanMembersInClanOr(int clanId1, int clanId2) {
    String query = "select * from " + TABLE_NAME + " where (" + DBConstants.CLAN_FOR_USER__CLAN_ID +
        " = ? or " + DBConstants.CLAN_FOR_USER__CLAN_ID + " = ? ) and " + DBConstants.CLAN_FOR_USER__STATUS +
        " = ?";
    
    List<Object> values = new ArrayList<Object>();
    values.add(clanId1);
    values.add(clanId2);
    values.add(UserClanStatus.MEMBER.getNumber());

    Connection conn = null;
		ResultSet rs = null;
		List<UserClan> userClans = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			userClans = grabUserClansFromRS(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userClans;
  }
*/
  public List<UserClan> getUserClansRelatedToUser(int userId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);

    Connection conn = null;
		ResultSet rs = null;
		List<UserClan> userClans = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userClans = grabUserClansFromRS(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userClans;
  }

  public List<UserClan> getUserClansRelatedToClan(int clanId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);

    Connection conn = null;
		ResultSet rs = null;
		List<UserClan> userClans = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = null;
			if (ControllerConstants.CLAN__ALLIANCE_CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT == clanId ||
			    ControllerConstants.CLAN__LEGION_CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT == clanId) {
			  //some clans will have a shit ton of people.
			  //Getting 1k+ users generates buffer overflow exception
			  rs = DBConnection.get().selectRowsAbsoluteAndLimit(conn, paramsToVals, TABLE_NAME,
			      ControllerConstants.CLAN__ALLIANCE_LEGION_LIMIT_TO_RETRIEVE_FROM_DB);
			} else {
			  rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			}
			userClans = grabUserClansFromRS(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userClans;
  }

  ////@Cacheable(value="specificUserClan")
  public UserClan getSpecificUserClan(int userId, int clanId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
    paramsToVals.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);

    Connection conn = null;
		ResultSet rs = null;
		UserClan userClan = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userClan = grabUserClanFromRS(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userClan;
  }
  

  public List<UserClan> getUserUserClansWithStatuses(int clanId, List<String> statuses) {
    StringBuilder querySb = new StringBuilder();
    querySb.append("SELECT *");
    querySb.append(" FROM ");
    querySb.append(TABLE_NAME);
    querySb.append(" WHERE ");
    querySb.append(DBConstants.CLAN_FOR_USER__CLAN_ID);
    querySb.append("=? AND ");
    querySb.append(DBConstants.CLAN_FOR_USER__STATUS);
    querySb.append(" in (");
    
    int numQuestionMarks = statuses.size();
    List<String> questionMarks = Collections.nCopies(numQuestionMarks, "?");
    String questionMarkStr = com.lvl6.utils.utilmethods.StringUtils.csvList(questionMarks);
    querySb.append(questionMarkStr);
    querySb.append(");");

    List<Object> values = new ArrayList<Object>();
    values.add(clanId);
    values.addAll(statuses);

    String query = querySb.toString();
    log.info(String.format(
    	"user clan query=%s, values=%s", query, values));

    Connection conn = null;
    ResultSet rs = null;
    List<UserClan> userClans = null;
    try {
    	conn = DBConnection.get().getConnection();
    	rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    	userClans = grabUserClansFromRS(rs);
    } catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    	userClans = new ArrayList<UserClan>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    //should not be null and should be a list object
    return userClans;
  }
  
  public List<Integer> getUserIdsWithStatuses(int clanId, List<String> statuses) {
    StringBuilder querySb = new StringBuilder();
    querySb.append("SELECT ");
    querySb.append(DBConstants.CLAN_FOR_USER__USER_ID);
    querySb.append(" FROM ");
    querySb.append(TABLE_NAME);
    querySb.append(" WHERE ");
    querySb.append(DBConstants.CLAN_FOR_USER__CLAN_ID);
    querySb.append("=? AND ");
    querySb.append(DBConstants.CLAN_FOR_USER__STATUS);
    querySb.append(" in (");
    
    int numQuestionMarks = statuses.size();
    List<String> questionMarks = Collections.nCopies(numQuestionMarks, "?");
    String questionMarkStr = com.lvl6.utils.utilmethods.StringUtils.csvList(questionMarks);
    querySb.append(questionMarkStr);
    querySb.append(");");
    
    List<Object> values = new ArrayList<Object>();
    values.add(clanId);
    values.addAll(statuses);
    
    String query = querySb.toString();
    log.info("user clan query=" + query + "\t values=" + values);
    
    Connection conn = null;
		ResultSet rs = null;
		List<Integer> userIds = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			userIds = getUserIdsFromRS(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    	userIds = new ArrayList<Integer>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		//should not be null and should be a list object
    return userIds;
  }
  
  public Map<Integer, Integer> getClanSizeForClanIdsAndStatuses(List<Integer> clanIds,
  		List<String> statuses) {
  	StringBuilder querySb = new StringBuilder();
  	querySb.append("SELECT ");
    querySb.append(DBConstants.CLAN_FOR_USER__CLAN_ID);
    querySb.append(", count(*) FROM ");
    querySb.append(TABLE_NAME);
    querySb.append(" WHERE ");
    querySb.append(DBConstants.CLAN_FOR_USER__CLAN_ID);
    querySb.append(" in (");
    int numQuestionMarks = clanIds.size();
    List<String> questionMarks = Collections.nCopies(numQuestionMarks, "?");
    String questionMarkStr = StringUtils.csvList(questionMarks);
    querySb.append(questionMarkStr);
    querySb.append(") and ");
    
    querySb.append(DBConstants.CLAN_FOR_USER__STATUS);
    querySb.append(" in (");
    
    numQuestionMarks = statuses.size();
    questionMarks = Collections.nCopies(numQuestionMarks, "?");
    questionMarkStr = StringUtils.csvList(questionMarks);
    querySb.append(questionMarkStr);
    querySb.append(") group by ");
    querySb.append(DBConstants.CLAN_FOR_USER__CLAN_ID);
    
    List<Object> values = new ArrayList<Object>();
    values.addAll(clanIds);
    values.addAll(statuses);
    
    String query = querySb.toString();
    log.info("user clan size query=" + query + "\t values=" + values);
    
    Connection conn = null;
		ResultSet rs = null;
		Map<Integer, Integer> clanIdsToSize = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			clanIdsToSize = convertRSToClanIdToSize(rs);
		} catch (Exception e) {
    	log.error("user clan retrieve db error.", e);
    	clanIdsToSize = new HashMap<Integer, Integer>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		//should not be null and should be a list object
    return clanIdsToSize;
  }

  private UserClan grabUserClanFromRS(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
          UserClan uc = convertRSRowToUserClan(rs);
          return uc;
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  public List<Integer> getUserIdsRelatedToClan(int clanId) {
    List<UserClan> userClans = getUserClansRelatedToClan(clanId);
    List<Integer> userIds = new ArrayList<Integer>();
    for (UserClan userClan : userClans) {
      userIds.add(userClan.getUserId());
    }
    return userIds;
  }

  private List<UserClan> grabUserClansFromRS(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        List<UserClan> userClans = new ArrayList<UserClan>();
        while(rs.next()) {
          UserClan uc = convertRSRowToUserClan(rs);
          userClans.add(uc);
        }
        return userClans;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  
  private List<Integer> getUserIdsFromRS(ResultSet rs) {
  	if (null != rs) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			List<Integer> userIds = new ArrayList<Integer>();
  			while (rs.next()) {
//  				int userId = rs.getInt(DBConstants.CLAN_FOR_USER__USER_ID); 
  				int userId = rs.getInt(1);//get the first and only column, same thing as above
  				userIds.add(userId);
  			}
  			return userIds;
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return new ArrayList<Integer>();
  }

  private Map<Integer, UserClan> convertRSToUserIdsToUserClans(ResultSet rs) {
  	Map<Integer, UserClan> userIdsToStatuses = new HashMap<Integer, UserClan>();
  	if (rs != null) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			
  			while(rs.next()) {
  				UserClan uc = convertRSRowToUserClan(rs);
  				if (null == uc) {
  					continue;
  				}
  				int userId = uc.getUserId();
  				userIdsToStatuses.put(userId, uc);
  				
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);

  		}
  	}
  	return userIdsToStatuses;
  }
  
  private Map<Integer, Integer> convertRSToClanIdToSize(ResultSet rs) {
  	Map<Integer, Integer> clanIdToSize = new HashMap<Integer, Integer>();
  	if (rs != null) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			
  			while(rs.next()) {
  				int i = 1;
  				int clanId = rs.getInt(i++);
  				int size = rs.getInt(i++);
  				clanIdToSize.put(clanId, size);
  				
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);

  		}
  	}
  	return clanIdToSize;
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private UserClan convertRSRowToUserClan(ResultSet rs) throws SQLException {
    int userId = rs.getInt(DBConstants.CLAN_FOR_USER__USER_ID);
    int clanId = rs.getInt(DBConstants.CLAN_FOR_USER__CLAN_ID);
    String status = rs.getString(DBConstants.CLAN_FOR_USER__STATUS);

    Date requestTime = null;
    try {
    	Timestamp ts = rs.getTimestamp(DBConstants.CLAN_FOR_USER__REQUEST_TIME);
    	if (!rs.wasNull()) {
    		requestTime = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error(String.format(
    		"maybe user clan request time was null. userId=%s, clanId=%s",
    		userId, clanId), e);
    }


    return new UserClan(userId, clanId, status, requestTime);
  }

}
