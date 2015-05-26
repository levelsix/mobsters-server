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

import com.lvl6.info.BattleReplayForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class BattleReplayForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_BATTLE_REPLAY_FOR_USER;
	private static final UserBattleReplayForClientMapper rowMapper = new UserBattleReplayForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	public List<BattleReplayForUser> getUserBattleReplaysForUser(String userId) {
		log.debug(String.format("retrieving user battle items for userId %s",
				userId));

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.BATTLE_REPLAY_FOR_USER__CREATOR_ID);

		List<BattleReplayForUser> userBattleReplays = null;
		try {
			userBattleReplays = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("battle item for user retrieve db error.", e);
			userBattleReplays = new ArrayList<BattleReplayForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userBattleReplays;
	}

	////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
	public Map<String, BattleReplayForUser> getBattleReplayIdsToUserBattleReplayForUser(
			String userId) {
		log.debug("retrieving map of battle item id to userbattleReplays for userId "
				+ userId);

		Map<String, BattleReplayForUser> battleReplayIdToBattleReplayForUser = new HashMap<String, BattleReplayForUser>();
		try {

			List<BattleReplayForUser> bifuList = getUserBattleReplaysForUser(userId);

			for (BattleReplayForUser bifu : bifuList) {
				String battleReplayId = bifu.getId();
				battleReplayIdToBattleReplayForUser.put(battleReplayId, bifu);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"battle item for user retrieve db error. userId=%s",
							userId), e);
		}

		return battleReplayIdToBattleReplayForUser;
	}

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public BattleReplayForUser getSpecificUserBattleReplay(String userId,
			int battleReplayId) {
		log.debug(
				"retrieving user battle item with userId={}, battleReplayId={}",
				userId, battleReplayId);

		Object[] values = { userId, battleReplayId };
		String query = String.format("select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.BATTLE_REPLAY_FOR_USER__CREATOR_ID,
				DBConstants.BATTLE_REPLAY_FOR_USER__ID);

		BattleReplayForUser userBattleReplay = null;
		try {
			List<BattleReplayForUser> bifuList = this.jdbcTemplate.query(query,
					values, rowMapper);

			if (null != bifuList && !bifuList.isEmpty()) {
				userBattleReplay = bifuList.get(0);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"battle item for user retrieve db error. userId=%s, battleReplayId=%s",
							userId, battleReplayId), e);
		}

		return userBattleReplay;
	}

	public List<BattleReplayForUser> getSpecificOrAllUserBattleReplaysForUser(
			String userId, List<String> userBattleReplayIds) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.BATTLE_REPLAY_FOR_USER__CREATOR_ID);
		querySb.append("=?");
		List<Object> values = new ArrayList<Object>();
		values.add(userId);

		if (userBattleReplayIds != null
				&& !userBattleReplayIds.isEmpty()) {
			log.debug("retrieving userBattleReplays with battleReplayIds {}",
					userBattleReplayIds);
			querySb.append(" AND ");
			querySb.append(DBConstants.BATTLE_REPLAY_FOR_USER__ID);
			querySb.append(" IN (");

			int amount = userBattleReplayIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userBattleReplayIds);
		}

		String query = querySb.toString();
		log.info("query={}, values={}", query, values);

		List<BattleReplayForUser> userBattleReplays = null;
		try {
			userBattleReplays = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userBattleReplays = new ArrayList<BattleReplayForUser>();

		}

		return userBattleReplays;
	}
*/

	public BattleReplayForUser getUserBattleReplay( String replayId )
	{
		log.debug("retrieving UserBattleReplay for id {}", replayId);
		Object[] val = new Object[] { replayId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.BATTLE_REPLAY_FOR_USER__ID);

		BattleReplayForUser brfu = null;
		try {
			List<BattleReplayForUser> userBattleReplays =
					this.jdbcTemplate.query(query, val, rowMapper);

			if (null != userBattleReplays && !userBattleReplays.isEmpty()) {
				brfu = userBattleReplays.get(0);
			}
		} catch (Exception e) {
			log.error("UserBattleReplay retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return brfu;
	}

	public List<BattleReplayForUser> getUserBattleReplays(
			Collection<String> replayIds)
	{
		log.debug("retrieving UserBattleReplay for ids {}", replayIds);
		int amount = replayIds.size();
		List<String> questions = Collections.nCopies(amount, "?");
		String questionMarkStr = StringUtils.csvList(questions);

		String query = String.format("select * from %s where %s in (%s)", TABLE_NAME,
				DBConstants.BATTLE_REPLAY_FOR_USER__ID, questionMarkStr);

		List<BattleReplayForUser> userBattleReplays = null;
		try {
			userBattleReplays = this.jdbcTemplate.query(query, replayIds.toArray(), rowMapper);

		} catch (Exception e) {
			log.error("battle item for user retrieve db error.", e);
			userBattleReplays = new ArrayList<BattleReplayForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userBattleReplays;
	}

	public Map<String, BattleReplayForUser> getBattleReplayIdsToReplays(
			Collection<String> replayIds) {
		log.debug("retrieving map of UserBattleReplay id to UserbattleReplays for ids {}",
				replayIds);

		Map<String, BattleReplayForUser> battleReplayIdToBattleReplayForUser = new HashMap<String, BattleReplayForUser>();
		try {

			List<BattleReplayForUser> bifuList = getUserBattleReplays(replayIds);

			for (BattleReplayForUser bifu : bifuList) {
				String battleReplayId = bifu.getId();
				battleReplayIdToBattleReplayForUser.put(battleReplayId, bifu);
			}
		} catch (Exception e) {
			log.error(
					String.format(
							"battle item for user retrieve db error. ids=%s",
							replayIds), e);
		}

		return battleReplayIdToBattleReplayForUser;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserBattleReplayForClientMapper implements
			RowMapper<BattleReplayForUser> {

		private static List<String> columnsSelected;

		@Override
		public BattleReplayForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			BattleReplayForUser bifu = new BattleReplayForUser();
			bifu.setId(rs.getString(DBConstants.BATTLE_REPLAY_FOR_USER__ID));
			bifu.setCreatorId(rs
					.getString(DBConstants.BATTLE_REPLAY_FOR_USER__CREATOR_ID));
			bifu.setReplay(rs
					.getBytes(DBConstants.BATTLE_REPLAY_FOR_USER__REPLAY));

			Timestamp ts = rs.getTimestamp(DBConstants.BATTLE_REPLAY_FOR_USER__CREATE_TIME);
			Date createTime = null;
			if (null != ts && !rs.wasNull()) {
				createTime = new Date(ts.getTime());
			}

			bifu.setTimeCreated(createTime);

			return bifu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.BATTLE_REPLAY_FOR_USER__ID);
				columnsSelected.add(DBConstants.BATTLE_REPLAY_FOR_USER__CREATOR_ID);
				columnsSelected
						.add(DBConstants.BATTLE_REPLAY_FOR_USER__REPLAY);
				columnsSelected.add(DBConstants.BATTLE_REPLAY_FOR_USER__CREATE_TIME);
			}
			return columnsSelected;
		}
	}

}
