package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.RetrieveUtils;

@Component @DependsOn("gameServer") public class StartupStuffRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static List<String> notices;

  private static final String TABLE_NAME = DBConstants.TABLE_ALERT_ON_STARTUP;
  
  private static User adminChatUser;
  
  public static User getAdminChatUser() {
  	log.debug("retrieving adminChatUserProto");
  	if (null == adminChatUser) {
  		setStaticAdminChatUser();
  	}
  	
  	return adminChatUser;
  }
  
  private static void setStaticAdminChatUser() {
  	User adminChatUserTemp = RetrieveUtils.userRetrieveUtils().getUserById(
  			ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID);
  	adminChatUser = adminChatUserTemp;
  }

  
  
  public static List<String> getAllActiveAlerts() {
    log.debug("retrieving all alerts placed in a set");
    if (null == notices) {
      setStaticActiveAlerts();
    }
    return notices;
  }

  private static void setStaticActiveAlerts() {
    log.debug("setting static Set of notices");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
			  absoluteConditionParams.put(DBConstants.ALERT_ON_STARTUP__IS_ACTIVE, true);
			  rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
			  //rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      List<String> noticesTemp = new ArrayList<String>();
			      while(rs.next()) { 
			        String noticesTerm = convertRSRowToAlerts(rs);
			        if (null != noticesTerm)
			          noticesTemp.add(noticesTerm);
			      }
			      notices = noticesTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("startup stuff retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }

  public static void reload() {
  	setStaticAdminChatUser();
    setStaticActiveAlerts();
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static String convertRSRowToAlerts(ResultSet rs) throws SQLException {
    String noticesTerm = rs.getString(DBConstants.ALERT_ON_STARTUP__MESSAGE);
    
    return noticesTerm;
  }

}
