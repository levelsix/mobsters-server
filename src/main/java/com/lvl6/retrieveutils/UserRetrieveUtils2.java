package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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

import com.lvl6.info.User;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class UserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_USER;
	private static final UserForClientMapper rowMapper = new UserForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<String> getUserIdsForFacebookIds(List<String> facebookIds) {
		int amount = facebookIds.size();
		List<String> questionMarkList = Collections.nCopies(amount, "?");
		String questionMarks = StringUtils.csvList(questionMarkList);

		List<Object> params = new ArrayList<Object>();
		params.addAll(facebookIds);

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT ");
		querySb.append(DBConstants.USER__ID);
		querySb.append(" FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER__FACEBOOK_ID);
		querySb.append(" IN (");
		querySb.append(questionMarks);
		querySb.append(");");

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, params));
		List<String> userIdList = null;
		try {
			userIdList = this.jdbcTemplate.queryForList(query,
					params.toArray(), String.class);
		} catch (Exception e) {
			log.error(String.format(
					"getUserIdsForFacebookIds user db error, fbIds=%s",
					facebookIds), e);
			userIdList = new ArrayList<String>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userIdList;
	}

	public Map<String, User> getUsersForFacebookIdsOrUserIds(
			List<String> facebookIds, List<String> userIds) {

		List<Object> params = new ArrayList<Object>();

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		if (null != facebookIds && !facebookIds.isEmpty()) {
			int amount = facebookIds.size();
			List<String> questionMarkList = Collections.nCopies(amount, "?");
			String questionMarks = StringUtils.csvList(questionMarkList);

			querySb.append(DBConstants.USER__FACEBOOK_ID);
			querySb.append(" IN (");
			querySb.append(questionMarks);
			querySb.append(")");

			if (null != userIds && !userIds.isEmpty()) {
				querySb.append(" OR ");
			}

			params.addAll(facebookIds);
		}

		if (null != userIds && !userIds.isEmpty()) {
			int amount = userIds.size();
			List<String> questionMarkList = Collections.nCopies(amount, "?");
			String questionMarks = StringUtils.csvList(questionMarkList);

			querySb.append(DBConstants.USER__ID);
			querySb.append(" IN (");
			querySb.append(questionMarks);
			querySb.append(")");

			params.addAll(userIds);
		}

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, params));
		Map<String, User> userMap = new HashMap<String, User>();
		try {
			List<User> usersList = this.jdbcTemplate.query(query,
					params.toArray(), rowMapper);

			for (User user : usersList) {
				userMap.put(user.getId(), user);
			}
		} catch (Exception e) {
			log.error(
					"getUsersForFacebookIdsOrUserIds user retrieve db error.",
					e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userMap;
	}

	public int numAccountsForUDID(String udid) {
		String query = String.format(
				"select count(*) from %s where %s like concat(?, \"%%\");",
				TABLE_NAME, DBConstants.USER__UDID_FOR_HISTORY, udid);
		try {
			return this.jdbcTemplate.queryForInt(query, udid);

		} catch (Exception e) {
			log.error("numAccountsForUDID error", e);
		}
		log.warn(String.format(
				"No accounts found when counting accounts for udid=%s", udid));
		return 0;
	}

	public Integer countUsers(Boolean isFake) {
		String query = String.format("select count(*) from %s where %s=?",
				TABLE_NAME, DBConstants.USER__IS_FAKE);

		try {
			return this.jdbcTemplate.queryForInt(query, isFake);

		} catch (Exception e) {
			log.error("countUsers error", e);
		}
		log.warn(String.format(
				"No users found when counting users for isFake=%s", isFake));
		return 0;
	}

	////@Cacheable(value="usersCache")
	public User getUserById(String userId) {
		log.debug(String.format("retrieving user with userId %s", userId));

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.USER__ID);

		User user = null;
		try {
			user = this.jdbcTemplate.queryForObject(query, values, rowMapper);
		} catch (Exception e) {
			log.error(String.format(
					"getUserById user retrieve db error. userId=%s", userId), e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return user;
	}

	public Map<String, User> getUsersByIds(Collection<String> userIds) {
		log.debug("retrieving users with userIds " + userIds);

		if (userIds == null || userIds.size() <= 0) {
			return new HashMap<String, User>();
		}

		int amount = userIds.size();
		List<String> questions = Collections.nCopies(amount, "?");
		String qStr = StringUtils.csvList(questions);

		String query = String.format("select * from %s where %s in (%s)",
				TABLE_NAME, DBConstants.USER__ID, qStr);

		List<Object> values = new ArrayList<Object>();
		values.addAll(userIds);

		Map<String, User> userIdToUserMap = new HashMap<String, User>();
		try {
			List<User> usersList = this.jdbcTemplate.query(query,
					values.toArray(), rowMapper);

			for (User user : usersList) {
				userIdToUserMap.put(user.getId(), user);
			}
		} catch (Exception e) {
			log.error("user retrieve db error.", e);

			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userIdToUserMap;
	}

	public List<User> getUsersByClanId(String clanId) {
		log.debug(String.format("retrieving users with clanId %s", clanId));

		Object[] values = { clanId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.USER__CLAN_ID);

		List<User> usersList = null;
		try {
			usersList = this.jdbcTemplate.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error(String.format(
					"getUsersByClanId user retrieve db error. clanId=%s",
					clanId), e);
			usersList = new ArrayList<User>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return usersList;
	}

	//	public List<User> getUsersByReferralCodeOrName(String queryString) {
	//		log.debug("retrieving user with queryString " + queryString);
	//
	//		Map <String, Object> paramsToVals = new HashMap<String, Object>();
	//		paramsToVals.put(DBConstants.USER__REFERRAL_CODE, queryString);
	//		paramsToVals.put(DBConstants.USER__NAME, queryString);
	//
	//		Connection conn = null;
	//		ResultSet rs = null;
	//		List<User> users = null;
	//		try {
	//			conn = DBConnection.get().getConnection();
	//			rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
	//
	//			users = convertRSToUsers(rs);
	//			if (users == null) users = new ArrayList<User>();
	//		} catch (Exception e) {
	//			log.error("user retrieve db error.", e);
	//		} finally {
	//			DBConnection.get().close(rs, null, conn);
	//		}
	//		return users;
	//	}

	//	public User getUserByUDID(String UDID) {
	//		log.debug(String.format(
	//			"retrieving user with udid %s",
	//			UDID));
	//
	//		Object[] values = { UDID };
	//		String query = String.format(
	//			"select * from %s where %s=?",
	//			TABLE_NAME, DBConstants.USER__UDID);
	//
	//		User user = null;
	//		try {
	//			user = this.jdbcTemplate
	//				.queryForObject(query, values, rowMapper);
	//		} catch (Exception e) {
	//			log.error("user retrieve db error.", e);
	////		} finally {
	////			DBConnection.get().close(rs, null, conn);
	//		}
	//		return user;
	//	}

	public List<User> getUserByUDIDorFbId(String UDID, String fbId) {
		log.debug(String.format("retrieving user with udid=%s, fbId=%s", UDID,
				fbId));

		Object[] values = { UDID, fbId };
		String query = String.format("select * from %s where %s=? or %s=?",
				TABLE_NAME, DBConstants.USER__UDID,
				DBConstants.USER__FACEBOOK_ID);

		List<User> user = null;
		try {
			user = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error(String.format("user retrieve db error. UDID=%s, fbId=%s",
					UDID, fbId), e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return user;
	}

	//	public User getUserByReferralCode(String referralCode) {
	//		log.debug("retrieving user with referral code " + referralCode);
	//		Map <String, Object> paramsToVals = new HashMap<String, Object>();
	//		paramsToVals.put(DBConstants.USER__REFERRAL_CODE, referralCode);
	//
	//		User user = null;
	//		Connection conn = DBConnection.get().getConnection();
	//		ResultSet rs = null;
	//		try {
	//			rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
	//			user = convertRSToUser(rs);
	//			DBConnection.get().close(rs, null, conn);
	//		} catch (Exception e) {
	//			log.error("user  retrieve db error.", e);
	//		} finally {
	//			DBConnection.get().close(rs, null, conn);
	//		}
	//		return user;
	//	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserForClientMapper implements RowMapper<User> {

		private static List<String> columnsSelected;

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			String id = rs.getString(DBConstants.USER__ID);
			String name = rs.getString(DBConstants.USER__NAME);
			int level = rs.getInt(DBConstants.USER__LEVEL);
			int gems = rs.getInt(DBConstants.USER__GEMS);
			int cash = rs.getInt(DBConstants.USER__CASH);
			int oil = rs.getInt(DBConstants.USER__OIL);
			int experience = rs.getInt(DBConstants.USER__EXPERIENCE);
			int tasksCompleted = rs.getInt(DBConstants.USER__TASKS_COMPLETED);
			String referralCode = rs.getString(DBConstants.USER__REFERRAL_CODE);
			int numReferrals = rs.getInt(DBConstants.USER__NUM_REFERRALS);
			String udidForHistory = rs
					.getString(DBConstants.USER__UDID_FOR_HISTORY);

			Timestamp ts;
			Date lastLogin = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_LOGIN);
				if (!rs.wasNull()) {
					lastLogin = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("db error: last_login not set. user_id=" + id);
			}

			Date lastLogout = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_LOGOUT);
				if (!rs.wasNull()) {
					lastLogout = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("db error: last_logout not set. user_id=" + id);
			}

			String deviceToken = rs.getString(DBConstants.USER__DEVICE_TOKEN);
			int numBadges = rs.getInt(DBConstants.USER__NUM_BADGES);
			boolean isFake = rs.getBoolean(DBConstants.USER__IS_FAKE);

			Date createTime = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__CREATE_TIME);
				if (!rs.wasNull()) {
					createTime = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("db error: create_time not set. user_id=" + id);
			}

			boolean isAdmin = rs.getBoolean(DBConstants.USER__IS_ADMIN);
			String apsalarId = rs.getString(DBConstants.USER__APSALAR_ID);
			int numCoinsRetrievedFromStructs = rs
					.getInt(DBConstants.USER__NUM_COINS_RETRIEVED_FROM_STRUCTS);
			int numOilRetrievedFromStructs = rs
					.getInt(DBConstants.USER__NUM_OIL_RETRIEVED_FROM_STRUCTS);
			int numConsecutiveDaysPlayed = rs
					.getInt(DBConstants.USER__NUM_CONSECUTIVE_DAYS_PLAYED);

			String clanId = rs.getString(DBConstants.USER__CLAN_ID);
			//			if (rs.wasNull()) {
			//				clanId = ControllerConstants.NOT_SET;
			//			}

			Date lastWallPostNotificationTime = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_WALL_POST_NOTIFICATION_TIME);
				if (!rs.wasNull()) {
					lastWallPostNotificationTime = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("db error: last_wall_post_notification_time not set. user_id="
						+ id);
			}

			//    int kabamNaid = rs.getInt(DBConstants.USER__KABAM_NAID);

			boolean hasReceivedfbReward = rs
					.getBoolean(DBConstants.USER__HAS_RECEIVED_FB_REWARD);
			int numBeginnerSalesPurchased = rs
					.getInt(DBConstants.USER__NUM_BEGINNER_SALES_PURCHASED);
			String facebookId = rs.getString(DBConstants.USER__FACEBOOK_ID);
			boolean fbIdSetOnUserCreate = rs
					.getBoolean(DBConstants.USER__FB_ID_SET_ON_USER_CREATE);
			String gameCenterId = rs
					.getString(DBConstants.USER__GAME_CENTER_ID);
			String udid = rs.getString(DBConstants.USER__UDID);
			String pvpDefendingMessage = rs
					.getString(DBConstants.USER__PVP_DEFENDING_MESSAGE);
			Date lastObstacleSpawnedTime = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_OBSTACLE_SPAWNED_TIME);
				if (!rs.wasNull()) {
					lastObstacleSpawnedTime = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("db error: last_obstacle_spawned_time"
						+ " was null...?");
			}

			int numObstaclesRemoved = rs
					.getInt(DBConstants.USER__NUM_OBSTACLES_REMOVED);

			Date lastMiniJobGeneratedTime = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_MINI_JOB_GENERATED_TIME);
				if (!rs.wasNull()) {
					lastMiniJobGeneratedTime = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("lastMiniJobGeneratedTime null...?", e);
			}

			int avatarMonsterId = rs
					.getInt(DBConstants.USER__AVATAR_MONSTER_ID);

			Date lastFreeBoosterPackTime = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_FREE_BOOSTER_PACK_TIME);
				if (!rs.wasNull()) {
					lastFreeBoosterPackTime = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("last_free_booster_pack_time null...?", e);
			}

			int numClanHelps = rs.getInt(DBConstants.USER__CLAN_HELPS);

			Date lastSecretGiftCollectTime = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_SECRET_GIFT_COLLECT_TIME);
				if (!rs.wasNull()) {
					lastSecretGiftCollectTime = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("last_secret_gift_collect_time null...?", e);
			}

			Date lastTeamDonateSolicitation = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__LAST_TEAM_DONATE_SOLICITATION);
				if (!rs.wasNull()) {
					lastTeamDonateSolicitation = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("last_team_donate_solicitation null...?", e);
			}

			boolean boughtRiggedBoosterPack = rs
					.getBoolean(DBConstants.USER__BOUGHT_RIGGED_BOOSTER_PACK);

			int salesValue = rs.getInt(DBConstants.USER__SALES_VALUE);

			Date lastPurchaseTime = null;
			try {
				ts = rs.getTimestamp(DBConstants.USER__SALES_LAST_PURCHASE_TIME);
				if (!rs.wasNull()) {
					lastPurchaseTime = new Date(ts.getTime());
				}
			} catch (Exception e) {
				log.error("last_purchase_time null...?", e);
			}

			boolean salesJumpTwoTiers = rs.getBoolean(DBConstants.USER__SALES_JUMP_TWO_TIERS);

			long totalStrength = rs.getLong(DBConstants.USER__TOTAL_STRENGTH);

			int segmentationGroup = rs.getInt(DBConstants.USER__SEGMENTATION_GROUP);

			User user = new User(id, name, level, gems, cash, oil, experience,
					tasksCompleted, referralCode, numReferrals, udidForHistory,
					lastLogin, lastLogout, deviceToken, numBadges, isFake,
					createTime, isAdmin, apsalarId,
					numCoinsRetrievedFromStructs, numOilRetrievedFromStructs,
					numConsecutiveDaysPlayed, clanId,
					lastWallPostNotificationTime, hasReceivedfbReward,
					numBeginnerSalesPurchased, facebookId, fbIdSetOnUserCreate,
					gameCenterId, udid, lastObstacleSpawnedTime,
					numObstaclesRemoved, lastMiniJobGeneratedTime,
					avatarMonsterId, lastFreeBoosterPackTime, numClanHelps,
					lastSecretGiftCollectTime, pvpDefendingMessage,
					lastTeamDonateSolicitation, boughtRiggedBoosterPack,
					salesValue, lastPurchaseTime, salesJumpTwoTiers, totalStrength,
					segmentationGroup);

			return user;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.USER__ID);
				columnsSelected.add(DBConstants.USER__NAME);
				columnsSelected.add(DBConstants.USER__LEVEL);
				columnsSelected.add(DBConstants.USER__GEMS);
				columnsSelected.add(DBConstants.USER__CASH);
				columnsSelected.add(DBConstants.USER__OIL);
				columnsSelected.add(DBConstants.USER__EXPERIENCE);

				columnsSelected.add(DBConstants.USER__TASKS_COMPLETED);
				columnsSelected.add(DBConstants.USER__REFERRAL_CODE);
				columnsSelected.add(DBConstants.USER__NUM_REFERRALS);
				columnsSelected.add(DBConstants.USER__UDID_FOR_HISTORY);
				columnsSelected.add(DBConstants.USER__LAST_LOGIN);
				columnsSelected.add(DBConstants.USER__LAST_LOGOUT);
				columnsSelected.add(DBConstants.USER__DEVICE_TOKEN);

				columnsSelected.add(DBConstants.USER__NUM_BADGES);
				columnsSelected.add(DBConstants.USER__IS_FAKE);
				columnsSelected.add(DBConstants.USER__CREATE_TIME);
				columnsSelected.add(DBConstants.USER__IS_ADMIN);
				columnsSelected.add(DBConstants.USER__APSALAR_ID);
				columnsSelected
						.add(DBConstants.USER__NUM_COINS_RETRIEVED_FROM_STRUCTS);
				columnsSelected
						.add(DBConstants.USER__NUM_OIL_RETRIEVED_FROM_STRUCTS);

				columnsSelected
						.add(DBConstants.USER__NUM_CONSECUTIVE_DAYS_PLAYED);
				columnsSelected.add(DBConstants.USER__CLAN_ID);
				columnsSelected
						.add(DBConstants.USER__LAST_WALL_POST_NOTIFICATION_TIME);
				columnsSelected.add(DBConstants.USER__HAS_RECEIVED_FB_REWARD);
				columnsSelected
						.add(DBConstants.USER__NUM_BEGINNER_SALES_PURCHASED);
				columnsSelected.add(DBConstants.USER__FACEBOOK_ID);
				columnsSelected.add(DBConstants.USER__FB_ID_SET_ON_USER_CREATE);

				columnsSelected.add(DBConstants.USER__GAME_CENTER_ID);
				columnsSelected.add(DBConstants.USER__UDID);
				columnsSelected
						.add(DBConstants.USER__LAST_OBSTACLE_SPAWNED_TIME);
				columnsSelected.add(DBConstants.USER__NUM_OBSTACLES_REMOVED);
				columnsSelected
						.add(DBConstants.USER__LAST_MINI_JOB_GENERATED_TIME);
				columnsSelected.add(DBConstants.USER__AVATAR_MONSTER_ID);
				columnsSelected.add(DBConstants.USER__CLAN_HELPS);

				columnsSelected.add(DBConstants.USER__PVP_DEFENDING_MESSAGE);
				columnsSelected
						.add(DBConstants.USER__LAST_TEAM_DONATE_SOLICITATION);
				columnsSelected
						.add(DBConstants.USER__BOUGHT_RIGGED_BOOSTER_PACK);
				columnsSelected
						.add(DBConstants.USER__SALES_VALUE);
				columnsSelected
						.add(DBConstants.USER__SALES_LAST_PURCHASE_TIME);

			}
			return columnsSelected;
		}
	}

}
