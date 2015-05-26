package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class UserFacebookInviteForSlotRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
	private static final UserFbInviteForClientMapper rowMapper = new UserFbInviteForClientMapper();
	public static final UserIdMapper userIdMapper = new UserIdMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public UserFacebookInviteForSlot getInviteForId(String inviteId) {
		Object[] values = { inviteId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);

		UserFacebookInviteForSlot invite = null;
		try {
			List<UserFacebookInviteForSlot> inv = this.jdbcTemplate.query(
					query, values, rowMapper);

			if (null != inv && !inv.isEmpty()) {
				invite = inv.get(0);
			}

		} catch (Exception e) {
			log.error("getInviteForId retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}

		return invite;
	}

	public Map<String, UserFacebookInviteForSlot> getInviteForId(
			List<String> inviteIds) {
		int amount = inviteIds.size();
		List<String> questionMarkList = Collections.nCopies(amount, "?");
		String questionMarks = StringUtils.csvList(questionMarkList);

		List<Object> params = new ArrayList<Object>();
		params.addAll(inviteIds);

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
		querySb.append(" IN (");
		querySb.append(questionMarks);
		querySb.append(");");

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, params));

		Map<String, UserFacebookInviteForSlot> idsToInvites = new HashMap<String, UserFacebookInviteForSlot>();
		try {
			List<UserFacebookInviteForSlot> invites = this.jdbcTemplate.query(
					query, params.toArray(), rowMapper);

			for (UserFacebookInviteForSlot invite : invites) {
				String id = invite.getId();
				idsToInvites.put(id, invite);
			}
		} catch (Exception e) {
			log.error("getInviteForId(collection) retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}

		return idsToInvites;
	}

	public Map<String, UserFacebookInviteForSlot> getSpecificOrAllInvitesForInviter(
			String userId, List<String> specificInviteIds,
			boolean filterByAccepted, boolean isAccepted,
			boolean filterByRedeemed, boolean isRedeemed) {
		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
		querySb.append("=?");
		List<Object> values = new ArrayList<Object>();
		values.add(userId);

		//if user didn't give any userStructIds then get all the user's structs
		if (null != specificInviteIds && !specificInviteIds.isEmpty()) {
			log.debug(String.format(
					"retrieving UserFacebookInviteForSlot with ids %s",
					specificInviteIds));
			querySb.append(" AND ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
			querySb.append(" IN (");

			int amount = specificInviteIds.size();
			List<String> questionMarkList = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questionMarkList);

			querySb.append(questionMarkStr);
			querySb.append(")");
			values.addAll(specificInviteIds);
		}

		if (filterByAccepted) {
			querySb.append(" AND ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED);
			querySb.append(" IS ");
			if (isAccepted) {
				querySb.append("NOT ");
			}
			querySb.append("NULL");
		}
		if (filterByRedeemed) {
			querySb.append(" AND ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED);
			querySb.append(" IS ");
			if (isRedeemed) {
				querySb.append("NOT ");
			}
			querySb.append("NULL");
		}
		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, values));

		Map<String, UserFacebookInviteForSlot> idsToInvites = new HashMap<String, UserFacebookInviteForSlot>();
		try {
			List<UserFacebookInviteForSlot> invites = this.jdbcTemplate.query(
					query, values.toArray(), rowMapper);

			for (UserFacebookInviteForSlot invite : invites) {
				String id = invite.getId();
				idsToInvites.put(id, invite);
			}

		} catch (Exception e) {
			log.error("getSpecificOrAllInvitesForInviter retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}

		return idsToInvites;
	}

	//recipientFacebookId assumed to be not null
	public Map<String, UserFacebookInviteForSlot> getSpecificOrAllInvitesForRecipient(
			String recipientFacebookId, List<String> specificInviteIds,
			boolean filterByAccepted, boolean isAccepted,
			boolean filterByRedeemed, boolean isRedeemed) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID);
		querySb.append("=?");
		List<Object> values = new ArrayList<Object>();
		values.add(recipientFacebookId);

		//if user didn't give any userStructIds then get all the user's structs
		if (null != specificInviteIds && !specificInviteIds.isEmpty()) {
			log.debug(String.format(
					"retrieving UserFacebookInviteForSlot with ids %s",
					specificInviteIds));
			querySb.append(" AND ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
			querySb.append(" IN (");

			int amount = specificInviteIds.size();
			List<String> questionMarkList = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questionMarkList);

			querySb.append(questionMarkStr);
			querySb.append(")");
			values.addAll(specificInviteIds);
		}

		if (filterByAccepted) {
			querySb.append(" AND ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED);
			querySb.append(" IS ");
			if (isAccepted) {
				querySb.append("NOT ");
			}
			querySb.append("NULL");
		}

		if (filterByRedeemed) {
			querySb.append(" AND ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED);
			querySb.append(" IS ");
			if (isRedeemed) {
				querySb.append("NOT ");
			}
			querySb.append("NULL");
		}

		String query = querySb.toString();
		log.info(String.format("query=%s, values=", query, values));

		Map<String, UserFacebookInviteForSlot> idsToInvites = new HashMap<String, UserFacebookInviteForSlot>();
		try {
			List<UserFacebookInviteForSlot> invites = this.jdbcTemplate.query(
					query, values.toArray(), rowMapper);

			for (UserFacebookInviteForSlot invite : invites) {
				String id = invite.getId();
				idsToInvites.put(id, invite);
			}

		} catch (Exception e) {
			log.error("getSpecificOrAllInvitesForRecipient retrieve db error.",
					e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}

		return idsToInvites;
	}

	public Set<String> getUniqueInviterUserIdsForRequesterId(String facebookId,
			boolean filterByAccepted, boolean isAccepted) {
		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT DISTINCT(");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
		querySb.append(") FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID);
		querySb.append("=?");
		List<Object> values = new ArrayList<Object>();
		values.add(facebookId);

		if (filterByAccepted) {
			querySb.append(" AND ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED);
			querySb.append(" IS ");
			if (isAccepted) {
				querySb.append("NOT ");
			}
			querySb.append("NULL");
		}

		String query = querySb.toString();

		log.info(String.format("query=%s, values=%s", query, values));

		List<String> userIds = null;
		try {
			userIds = this.jdbcTemplate.query(query, values.toArray(),
					userIdMapper);

		} catch (Exception e) {
			log.error(
					"getUniqueInviterUserIdsForRequesterId retrieve db error.",
					e);
			userIds = new ArrayList<String>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}

		Set<String> uniqUserIds = new HashSet<String>(userIds);
		return uniqUserIds;
	}

	public List<UserFacebookInviteForSlot> getInvitesForUserStruct(
			String userId, String userStructId) {
		List<UserFacebookInviteForSlot> invites = null;

		List<Object> params = new ArrayList<Object>();
		params.add(userId);
		params.add(userStructId);

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
		querySb.append("=? AND ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_ID);
		querySb.append("=?");

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, params));

		try {
			invites = this.jdbcTemplate.query(query, params.toArray(),
					rowMapper);

		} catch (Exception e) {
			invites = new ArrayList<UserFacebookInviteForSlot>();
			log.error("getInviteForId(collection) retrieve db error.", e);
		}

		return invites;
	}

	public Map<String, UserFacebookInviteForSlot> getInvitesForUserStructMap(
			String userId, String userStructId) {
		List<UserFacebookInviteForSlot> invites = getInvitesForUserStruct(
				userId, userStructId);

		Map<String, UserFacebookInviteForSlot> idsToInvites = new HashMap<String, UserFacebookInviteForSlot>();
		for (UserFacebookInviteForSlot invite : invites) {
			String id = invite.getId();
			idsToInvites.put(id, invite);
		}

		return idsToInvites;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserFbInviteForClientMapper implements
			RowMapper<UserFacebookInviteForSlot> {

		private static List<String> columnsSelected;

		@Override
		public UserFacebookInviteForSlot mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			UserFacebookInviteForSlot invite = new UserFacebookInviteForSlot();
			invite.setId(rs
					.getString(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID));
			invite.setInviterUserId(rs
					.getString(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID));
			invite.setRecipientFacebookId(rs
					.getString(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID));

			try {
				Timestamp time = rs
						.getTimestamp(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_OF_INVITE);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					invite.setTimeOfInvite(date);
				}
			} catch (Exception e) {
				log.error(String.format(
						"maybe timeOfInvite is invalid, invite=%s", invite), e);
			}

			try {
				Timestamp time = rs
						.getTimestamp(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					invite.setTimeAccepted(date);
				}
			} catch (Exception e) {
				log.error(String.format(
						"maybe timeAccepted is invalid, invite=%s", invite), e);
			}

			invite.setUserStructId(rs
					.getString(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_ID));
			invite.setUserStructFbLvl(rs
					.getInt(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_FB_LVL));

			try {
				Timestamp time = rs
						.getTimestamp(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					invite.setTimeRedeemed(date);
				}
			} catch (Exception e) {
				log.error(String.format(
						"maybe timeRedeemed is invalid, invite=%s", invite), e);
			}

			return invite;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID);
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_OF_INVITE);
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED);
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_ID);
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_FB_LVL);
				columnsSelected
						.add(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED);
			}
			return columnsSelected;
		}
	}

	private static final class UserIdMapper implements RowMapper<String> {
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs
					.getString(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
		}

	}

}
