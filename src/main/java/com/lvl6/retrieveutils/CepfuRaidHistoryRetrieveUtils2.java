package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.CepfuRaidHistory;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;

@Component @DependsOn("gameServer") public class CepfuRaidHistoryRetrieveUtils2 {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_CEPFU_RAID_HISTORY;
  private static final CepfuRaidHistoryForClientMapper rowMapper = new CepfuRaidHistoryForClientMapper();

  private JdbcTemplate jdbcTemplate;

  @Resource
  public void setDataSource(DataSource dataSource) {
    log.info("Setting datasource and creating jdbcTemplate");
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public Map<Date, Map<String, CepfuRaidHistory>> getRaidHistoryForPastNDaysForClan(
      String clanId, int nDays, Date curDate, TimeUtils timeUtils) {

    curDate = timeUtils.createPstDate(curDate, 0, 0, 0);
    Date pastDate = timeUtils.createPstDate(curDate, -1* nDays, 0, 0);
    Timestamp pastTime = new Timestamp(pastDate.getTime());

    StringBuilder querySb = new StringBuilder();
    querySb.append("SELECT ");
    querySb.append(DBConstants.CEPFU_RAID_HISTORY__USER_ID);
    querySb.append(",");
    querySb.append(DBConstants.CEPFU_RAID_HISTORY__TIME_OF_ENTRY);
    querySb.append(",");
    querySb.append(DBConstants.CEPFU_RAID_HISTORY__CR_DMG_DONE);
    querySb.append(",");
    querySb.append(DBConstants.CEPFU_RAID_HISTORY__CLAN_CR_DMG);
    querySb.append(" FROM ");
    querySb.append(TABLE_NAME);
    querySb.append(" WHERE ");
    //		querySb.append(DBConstants.CEPFU_RAID_HISTORY__USER_ID);
    //		querySb.append("=? AND ");
    querySb.append(DBConstants.CEPFU_RAID_HISTORY__TIME_OF_ENTRY);
    querySb.append(">? AND ");
    querySb.append(DBConstants.CEPFU_RAID_HISTORY__CLAN_ID);
    querySb.append("=?;");

    List<Object> values = new ArrayList<Object>();
    //		values.add(userId);
    values.add(pastTime);
    values.add(clanId);

    String query = querySb.toString();

    Map<Date, Map<String, CepfuRaidHistory>> timesToUserIdsToRaidHistory = null;
    try {
      List<CepfuRaidHistory> history = this.jdbcTemplate
          .query(query, values.toArray(), rowMapper);

      timesToUserIdsToRaidHistory =
          new HashMap<Date, Map<String, CepfuRaidHistory>>();
      for (CepfuRaidHistory cepfurh : history) {
        Date aDate = cepfurh.getTimeOfEntry();

        if (!timesToUserIdsToRaidHistory.containsKey(aDate)) {
          timesToUserIdsToRaidHistory.put(aDate, new HashMap<String, CepfuRaidHistory>());
        }
        Map<String, CepfuRaidHistory> userIdToRaidHistory =
            timesToUserIdsToRaidHistory.get(aDate);

        String userId = cepfurh.getUserId();
        userIdToRaidHistory.put(userId, cepfurh);
      }

      log.info(String.format(
          "query=%s, values=%s", query, values));


      log.info(String.format(
          "getting raid history for past days. clanId=%s, pastDate=%s",
          clanId, pastDate));

    } catch (Exception e) {
      log.error("clan event persistent for user raid history retrieve db error.", e);
      timesToUserIdsToRaidHistory = new HashMap<Date, Map<String, CepfuRaidHistory>>();
      //		} finally {
      //			DBConnection.get().close(rs, null, conn);
    }
    return timesToUserIdsToRaidHistory;
  }

  //Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
  //mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
  //made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
  //says so (search for "private static final")
  private static final class CepfuRaidHistoryForClientMapper implements RowMapper<CepfuRaidHistory> {

    private static List<String> columnsSelected;

    public CepfuRaidHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
      CepfuRaidHistory crh = new CepfuRaidHistory();
      crh.setUserId(rs.getString(DBConstants.CEPFU_RAID_HISTORY__USER_ID));
      Timestamp time = rs.getTimestamp(DBConstants.CEPFU_RAID_HISTORY__TIME_OF_ENTRY);
      if (null != time) {
        crh.setTimeOfEntry(new Date(time.getTime()));
      }

      //			crh.setClanId(rs.getString(DBConstants.CEPFU_RAID_HISTORY__CLAN_ID));
      //			crh.setClanEventPersistentId(rs.getInt(DBConstants.CEPFU_RAID_HISTORY__CLAN_EVENT_PERSISTENT_ID));
      //			crh.setCrId(rs.getInt(DBConstants.CEPFU_RAID_HISTORY__CR_ID));
      crh.setCrDmgDone(rs.getInt(DBConstants.CEPFU_RAID_HISTORY__CR_DMG_DONE));
      crh.setClanCrDmg(rs.getInt(DBConstants.CEPFU_RAID_HISTORY__CLAN_CR_DMG));
      //			crh.setUserMonsterIdOne(rs.getString(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_ONE));
      //			crh.setUserMonsterIdTwo(rs.getString(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_TWO));
      //			crh.setUserMonsterIdThree(rs.getString(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_THREE));

      return crh;
    }        

    public static List<String> getColumnsSelected() {
      if (null == columnsSelected) {
        columnsSelected = new ArrayList<String>();
        columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__USER_ID);
        columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__TIME_OF_ENTRY);
        //				columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__CLAN_ID);
        //				columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__CLAN_EVENT_PERSISTENT_ID);
        //				columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__CR_ID);
        columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__CR_DMG_DONE);
        columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__CLAN_CR_DMG);
        //				columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_ONE);
        //				columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_TWO);
        //				columnsSelected.add(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_THREE);
      }
      return columnsSelected;
    }
  } 	
}
