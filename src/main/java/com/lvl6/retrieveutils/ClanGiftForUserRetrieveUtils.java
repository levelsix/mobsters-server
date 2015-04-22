package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanGiftForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class ClanGiftForUserRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_CLAN_GIFT_FOR_USER;
	private static final UserClanGiftForClientMapper rowMapper = new UserClanGiftForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<ClanGiftForUser> getUserClanGiftsForUser(String userId) {
		log.debug(String.format("retrieving user clan gifts for userId %s",
				userId));

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.CLAN_GIFT_FOR_USER__RECEIVER_USER_ID);

		List<ClanGiftForUser> userClanGifts = null;
		try {
			userClanGifts = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("clan gift for user retrieve db error.", e);
			userClanGifts = new ArrayList<ClanGiftForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userClanGifts;
	}

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public ClanGiftForUser getSpecificUserClanGift(String userId,
			int clanGiftId) {
		log.debug(
				"retrieving user clan gift with userId={}, clanGiftId={}",
				userId, clanGiftId);

		Object[] values = { userId, clanGiftId };
		String query = String.format("select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.CLAN_GIFT_FOR_USER__RECEIVER_USER_ID,
				DBConstants.CLAN_GIFT_FOR_USER__CLAN_GIFT_ID);

		ClanGiftForUser userClanGift = null;
		try {
			List<ClanGiftForUser> bifuList = this.jdbcTemplate.query(query,
					values, rowMapper);

			if (null != bifuList && !bifuList.isEmpty()) {
				userClanGift = bifuList.get(0);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"battle item for user retrieve db error. userId=%s, clanGiftId=%s",
							userId, clanGiftId), e);
		}

		return userClanGift;
	}

	public List<ClanGiftForUser> getSpecificOrAllUserClanGiftsForUser(
			String userId, List<Integer> userClanGiftClanGiftIds) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.CLAN_GIFT_FOR_USER__RECEIVER_USER_ID);
		querySb.append("=?");
		List<Object> values = new ArrayList<Object>();
		values.add(userId);

		//if user didn't give any userStructIds then get all the user's structs
		//else get the specific ids
		if (userClanGiftClanGiftIds != null
				&& !userClanGiftClanGiftIds.isEmpty()) {
			log.debug("retrieving userClanGifts with clanGiftIds {}",
					userClanGiftClanGiftIds);
			querySb.append(" AND ");
			querySb.append(DBConstants.CLAN_GIFT_FOR_USER__CLAN_GIFT_ID);
			querySb.append(" IN (");

			int amount = userClanGiftClanGiftIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userClanGiftClanGiftIds);
		}

		String query = querySb.toString();
		log.info("query={}, values={}", query, values);

		List<ClanGiftForUser> userClanGifts = null;
		try {
			userClanGifts = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userClanGifts = new ArrayList<ClanGiftForUser>();

		}

		return userClanGifts;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserClanGiftForClientMapper implements
			RowMapper<ClanGiftForUser> {

		private static List<String> columnsSelected;

		@Override
		public ClanGiftForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			String id = rs.getString(DBConstants.CLAN_GIFT_FOR_USER__ID);
			String receiverUserId = rs.getString(DBConstants.CLAN_GIFT_FOR_USER__RECEIVER_USER_ID);
			String gifterUserId = rs.getString(DBConstants.CLAN_GIFT_FOR_USER__GIFTER_USER_ID);
			int clanGiftId = rs.getInt(DBConstants.CLAN_GIFT_FOR_USER__CLAN_GIFT_ID);
			int rewardId = rs.getInt(DBConstants.CLAN_GIFT_FOR_USER__REWARD_ID);
			Date timeReceived = new Date(rs.getTimestamp(DBConstants.CLAN_GIFT_FOR_USER__TIME_RECEIVED).getTime());
			String reasonForGift = rs.getString(DBConstants.CLAN_GIFT_FOR_USER__REASON_FOR_GIFT);
			boolean hasBeenCollected = rs.getBoolean(DBConstants.CLAN_GIFT_FOR_USER__HAS_BEEN_COLLECTED);

			return new ClanGiftForUser(id, receiverUserId, gifterUserId, clanGiftId, rewardId, timeReceived,
					reasonForGift, hasBeenCollected);
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__USER_ID);
				columnsSelected
						.add(DBConstants.BATTLE_ITEM_FOR_USER__BATTLE_ITEM_ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__QUANTITY);
			}
			return columnsSelected;
		}
	}

}
