package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserAndEquipFail;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class UserAndEquipFailRetrieveUtils {

  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private final String TABLE_NAME = DBConstants.TABLE_MONSTER_EVOLVING_FAIL_FOR_USER;


  ////@Cacheable(value="specificUserAndEquipFail")
  public UserAndEquipFail getSpecificFail(int userId, int equipId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__MONSTER_ID, equipId);
    paramsToVals.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__USER_ID, userId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
    UserAndEquipFail userClan = grabUserAndEquipFailFromRS(rs);
    DBConnection.get().close(rs, null, conn);
    return userClan;
  }

  private UserAndEquipFail grabUserAndEquipFailFromRS(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
          UserAndEquipFail uc = convertRSRowToUserAndEquipFail(rs);
          return uc;
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  public List<UserAndEquipFail> getUserAndEquipFailsRelatedToUser(int userId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__USER_ID, userId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
    List<UserAndEquipFail> userClans = grabUserAndEquipFailsFromRS(rs);
    DBConnection.get().close(rs, null, conn);
    return userClans;
  }

  private List<UserAndEquipFail> grabUserAndEquipFailsFromRS(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        List<UserAndEquipFail> userAndEquipFailList = new ArrayList<UserAndEquipFail>();
        while(rs.next()) {
          UserAndEquipFail uc = convertRSRowToUserAndEquipFail(rs);
          userAndEquipFailList.add(uc);
        }
        return userAndEquipFailList;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  
  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private UserAndEquipFail convertRSRowToUserAndEquipFail(ResultSet rs) throws SQLException {
    int i = 1;
    int userId = rs.getInt(i++);
    int equipId = rs.getInt(i++);
    int numFails = rs.getInt(i++);

    return new UserAndEquipFail(userId, equipId, numFails);
  }

}
