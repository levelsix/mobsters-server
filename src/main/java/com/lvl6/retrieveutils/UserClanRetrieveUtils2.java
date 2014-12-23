package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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

import com.lvl6.info.UserClan;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class UserClanRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_CLAN_FOR_USER;
	private static final UserClanForClientMapper rowMapper = new UserClanForClientMapper();
	private static final ClanSizeMapper clanSizeMapper = new ClanSizeMapper();
	private static final UserIdAndStatusMapper userIdAndStatusMapper = new UserIdAndStatusMapper();
	private static final UserIdMapper userIdMapper = new UserIdMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<String, UserClan> getUserClanForUsers(String clanId, List<String> userIds) {
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
		log.info(String.format(
			"user clan query=%s, values=%s",
			query, values));

		Map<String, UserClan> userIdsToStatuses = new HashMap<String, UserClan>();
		try {
			List<UserClan> userClans = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);
			for (UserClan uc : userClans) {
				String userId = uc.getUserId();
				userIdsToStatuses.put(userId, uc);
			}
			
		} catch (Exception e) {
			log.error("user clan retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		//should not be null
		return userIdsToStatuses;
	}
	
	public List<UserClan> getUserClansRelatedToUser(String userId) {
		Object[] values = { userId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.CLAN_FOR_USER__USER_ID);
		
		List<UserClan> userClans = null;
		try {
			userClans = this.jdbcTemplate
				.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error("user clan retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userClans;
	}

	public List<UserClan> getUserClansRelatedToClan(String clanId) {
		Object[] values = { clanId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.CLAN_FOR_USER__CLAN_ID);
		
		List<UserClan> userClans = null;
		try {
			userClans = this.jdbcTemplate
				.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error(String.format(
				"getUserClansRelatedToClan user clan retrieve db error. clanId=%s",
				clanId), e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userClans;
	}

	////@Cacheable(value="specificUserClan")
	public UserClan getSpecificUserClan(String userId, String clanId) {
		Object[] values = { clanId, userId };
		String query = String.format(
			"select * from %s where %s=? and %s=?",
			TABLE_NAME, DBConstants.CLAN_FOR_USER__CLAN_ID,
			DBConstants.CLAN_FOR_USER__USER_ID);
		
		UserClan userClan = null;
		try {
			List<UserClan> userClans = this.jdbcTemplate
				.query(query, values, rowMapper);
			if (null != userClans && !userClans.isEmpty()) {
				userClan = userClans.get(0);
			}
		} catch (Exception e) {
			log.error(String.format(
				"user clan retrieve db error. userId=%s, clanId=%s",
				userId, clanId), e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return userClan;
	}


	public List<UserClan> getUserUserClansWithStatuses(String clanId, List<String> statuses) {
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

		List<UserClan> userClans = null;
		try {
			userClans = this.jdbcTemplate
				.query(query, values.toArray(), rowMapper);
				
		} catch (Exception e) {
			log.error("user clan retrieve db error.", e);
			userClans = new ArrayList<UserClan>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		//should not be null and should be a list object
		return userClans;
	}

	public Map<String, String> getUserIdsToStatuses(String clanId, List<String> statuses)
	{
		int numQuestionMarks = statuses.size();
		List<String> questionMarks = Collections.nCopies(numQuestionMarks, "?");
		String questionMarkStr = com.lvl6.utils.utilmethods.StringUtils.csvList(questionMarks);
		
		String query = String.format(
			"SELECT %s, %s FROM %s WHERE %s=? AND %s in (%s);",
			DBConstants.CLAN_FOR_USER__USER_ID,
			DBConstants.CLAN_FOR_USER__STATUS,
			TABLE_NAME,
			DBConstants.CLAN_FOR_USER__CLAN_ID,
			DBConstants.CLAN_FOR_USER__STATUS,
			questionMarkStr);
		
		List<Object> values = new ArrayList<Object>();
		values.add(clanId);
		values.addAll(statuses);

		log.info("user clan query={} \t values={}", query, values);

		Map<String, String> userIdsToStatuses = new HashMap<String, String>();
		try {
			List<UserIdAndStatus> userIdsAndStatuses = this.jdbcTemplate
				.query(query, values.toArray(), userIdAndStatusMapper);
			
			for (UserIdAndStatus uias : userIdsAndStatuses) {
				userIdsToStatuses.put(uias.getUserId(), uias.getStatus());
			}
			
		} catch (Exception e) {
			log.error("getUserIdsToStatuses() retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		
		return userIdsToStatuses;
	}
	
	public List<String> getUserIdsWithStatuses(String clanId, List<String> statuses) {
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

		List<String> userIds = null;
		try {
			userIds = this.jdbcTemplate.query(query, values.toArray(),
				userIdMapper);
		} catch (Exception e) {
			log.error("user clan retrieve db error.", e);
			userIds = new ArrayList<String>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		//should not be null and should be a list object
		return userIds;
	}

	public Map<String, Integer> getClanSizeForClanIdsAndStatuses(List<String> clanIds,
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
		log.info(String.format(
			"user clan size query=%s, values=%s",
			query, values));

		Map<String, Integer> clanIdsToSize = new HashMap<String, Integer>();
		try {
			List<ClanSize> sizes = this.jdbcTemplate
				.query(query, values.toArray(), clanSizeMapper);
			
			for (ClanSize size : sizes) {
				clanIdsToSize.put(size.getClanId(), size.getSize());
			}
		} catch (Exception e) {
			log.error("user clan retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		//should not be null and should be a list object
		return clanIdsToSize;
	}

	public List<String> getUserIdsRelatedToClan(String clanId) {
		List<UserClan> userClans = getUserClansRelatedToClan(clanId);
		List<String> userIds = new ArrayList<String>();
		for (UserClan userClan : userClans) {
			userIds.add(userClan.getUserId());
		}
		return userIds;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserClanForClientMapper implements RowMapper<UserClan> {

		private static List<String> columnsSelected;

		public UserClan mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserClan uc = new UserClan();
			uc.setUserId(rs.getString(DBConstants.CLAN_FOR_USER__USER_ID));
			uc.setClanId(rs.getString(DBConstants.CLAN_FOR_USER__CLAN_ID));
			
			String status = rs.getString(DBConstants.CLAN_FOR_USER__STATUS);
			if (null != status) {
				String newStatus = status.trim().toUpperCase();
				if (!status.equals(newStatus)) {
					log.error(String.format(
		    			"UserClanStatus incorrect: %s, userId=%s, clanId=%s",
		    			status, uc.getUserId(), uc.getClanId()));
		    		status = newStatus;
				}
				uc.setStatus(status);
			}
			
			try {
				Timestamp time = rs.getTimestamp(DBConstants.CLAN_FOR_USER__REQUEST_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					uc.setRequestTime(date);
				}
			} catch (Exception e) {
				log.error(String.format(
					"maybe requestTime is invalid, uc=%s", uc), e);
			}

			return uc;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.CLAN_FOR_USER__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_FOR_USER__STATUS);
				columnsSelected.add(DBConstants.CLAN_FOR_USER__REQUEST_TIME);
			}
			return columnsSelected;
		}
	} 	

	protected static class ClanSize {
		private String clanId;
		private int size;

		public ClanSize()
		{
			super();
		}

		public String getClanId()
		{
			return clanId;
		}

		public void setClanId( String clanId )
		{
			this.clanId = clanId;
		}

		public int getSize()
		{
			return size;
		}

		public void setSize( int size )
		{
			this.size = size;
		}
		
	}
	
	private static final class ClanSizeMapper implements RowMapper<ClanSize> {
		
		public ClanSize mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanSize cs = new ClanSize();
			cs.setClanId(rs.getString(DBConstants.CLAN_FOR_USER__CLAN_ID));
			cs.setSize(rs.getInt(2));
			return cs;
		}	
	}

	protected static class UserIdAndStatus {
		private String userId;
		private String status;
		
		public UserIdAndStatus( String userId, String status )
		{
			super();
			this.userId = userId;
			this.status = status;
		}
		public String getUserId()
		{
			return userId;
		}
		public void setUserId( String userId )
		{
			this.userId = userId;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus( String status )
		{
			this.status = status;
		}

	}
	
	private static final class UserIdAndStatusMapper implements RowMapper<UserIdAndStatus> {

		public UserIdAndStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			String userId = rs.getString(DBConstants.CLAN_FOR_USER__USER_ID);
			String status = rs.getString(DBConstants.CLAN_FOR_USER__STATUS);
			return new UserIdAndStatus(userId, status);
		}	
	}

	
	private static final class UserIdMapper implements RowMapper<String> {

		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString(DBConstants.CLAN_FOR_USER__USER_ID);
		}	
	}
	
}
