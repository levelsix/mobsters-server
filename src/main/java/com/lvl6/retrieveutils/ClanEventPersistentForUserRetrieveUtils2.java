package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class ClanEventPersistentForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
	private static final UserClientEventPersistentForClientMapper rowMapper = new UserClientEventPersistentForClientMapper();
	private static final CrsmDmgMapper dmgMapper = new CrsmDmgMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<String, ClanEventPersistentForUser> getPersistentEventUserInfoForClanId(
		String clanId)
	{

		Object[] values = { clanId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);

		log.info(String.format(
			"getting ClanEventPersistentForUser for clanId=%s, query=%s, values=%s",
			clanId, query, values));
		Map<String, ClanEventPersistentForUser> userIdToClanPersistentEventUserInfo =
			new HashMap<String, ClanEventPersistentForUser>();
		try {
			List<ClanEventPersistentForUser> events = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			for (ClanEventPersistentForUser cepfu : events) {
				String userId = cepfu.getUserId();
				userIdToClanPersistentEventUserInfo.put(userId, cepfu);
			}
			
		} catch (Exception e) {
			log.error("clan event persistent for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userIdToClanPersistentEventUserInfo;
	}

	public ClanEventPersistentForUser getPersistentEventUserInfoForUserIdClanId(
		String userId, String clanId)
	{
		Object[] values = { clanId, userId };
		String query = String.format(
			"select * from %s where %s=? and %s=?",
			TABLE_NAME, DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID,
			DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID);

		log.info(String.format(
			"getting ClanEventPersistentForUser for clanId=%s, query=%s, values=%s",
			clanId, query, values));
		ClanEventPersistentForUser clanPersistentEventUserInfo = null;
		try {
			List<ClanEventPersistentForUser> events = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			if (null != events && !events.isEmpty()) {
				clanPersistentEventUserInfo = events.get(0);
			}
			
		} catch (Exception e) {
			log.error("clan event persistent for user retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return clanPersistentEventUserInfo;
	}

	public Map<String, Integer> getTotalCrsmDmgForClanIds(List<String> clanIds) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT(");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID);
		sb.append("), SUM(");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
		sb.append(") FROM ");
		sb.append(DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER);
		sb.append(" WHERE ");

		List<Object> values = new ArrayList<Object>();

		if (null != clanIds && !clanIds.isEmpty()) {
			sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);
			sb.append(" IN (");

			int amount = clanIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionStr = StringUtils.csvList(questions);
			sb.append(questionStr);
			sb.append(") AND ");

			values.addAll(clanIds);
		}

		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
		sb.append(" IS NOT null AND 1=?  GROUP BY ");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);

		values.add(1);

		String query = sb.toString();

		if (null != clanIds && !clanIds.isEmpty()) {
			log.info(String.format(
				"retrieving crsm damage for clans. query=%s, values=%s",
				query, values));
		} else {
			log.info(String.format(
				"retrieving crsm damage for all clans. query=%s, values=%s",
				query, values));
		}

		Map<String, Integer> clanIdToCrsmDmg = new HashMap<String, Integer>();
		try {
			List<ClanEventPersistentForUser> dmgs = this.jdbcTemplate
				.query(query, values.toArray(), dmgMapper);
			
			for (ClanEventPersistentForUser dmg : dmgs) {
				String clanId = dmg.getClanId();
				int crsmDmg = dmg.getCrsmDmgDone();
				
				int sumCrsmDmg = 0;
				
				if (clanIdToCrsmDmg.containsKey(clanId)) {
					sumCrsmDmg = clanIdToCrsmDmg.get(clanId);
				}
				
				sumCrsmDmg += crsmDmg;
				clanIdToCrsmDmg.put(clanId, sumCrsmDmg);
			}
		} catch (Exception e) {
			log.error("could not retrieve crsm damage.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}

		return clanIdToCrsmDmg;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserClientEventPersistentForClientMapper implements RowMapper<ClanEventPersistentForUser> {

		private static List<String> columnsSelected;

		public ClanEventPersistentForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanEventPersistentForUser cepfu = new ClanEventPersistentForUser();
			cepfu.setUserId(rs.getString(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID));
			cepfu.setClanId(rs.getString(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID));
			cepfu.setCrId(rs.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_ID));
			cepfu.setCrDmgDone(rs.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_DMG_DONE));
			cepfu.setCrsId(rs.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_ID));
			cepfu.setCrsmDmgDone(rs.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_DMG_DONE));
			cepfu.setCrsmId(rs.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_ID));
			cepfu.setCrsmDmgDone(rs.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE));
			cepfu.setUserMonsterIdOne(rs.getString(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_ONE));
			cepfu.setUserMonsterIdTwo(rs.getString(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_TWO));
			cepfu.setUserMonsterIdThree(rs.getString(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_THREE));
			return cepfu;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_ID);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_DMG_DONE);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_ID);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_DMG_DONE);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_ID);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_ONE);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_TWO);
				columnsSelected.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_THREE);
			}
			return columnsSelected;
		}
	} 	

	private static final class CrsmDmgMapper implements RowMapper<ClanEventPersistentForUser> {

		private static List<String> columnsSelected;

		public ClanEventPersistentForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanEventPersistentForUser cepfu = new ClanEventPersistentForUser();
			cepfu.setClanId(rs.getString(1));
			cepfu.setCrsmDmgDone(rs.getInt(2));
			
			return cepfu;
		}
	}
}
