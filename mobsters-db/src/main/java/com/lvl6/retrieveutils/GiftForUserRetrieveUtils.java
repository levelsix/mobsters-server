package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUserPojo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class GiftForUserRetrieveUtils {


	private static final Logger log = LoggerFactory
			.getLogger(GiftForUserRetrieveUtils.class);

	private final String TABLE_NAME = DBConstants.TABLE_GIFT_FOR_USER;
	private static final UserGiftForClientMapper rowMapper = new UserGiftForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<String, GiftForUserPojo> getUserGiftsForUserMap(Collection<String> gfuIds)
	{
		log.debug("retrieving GiftForUser map for ids {}",
				gfuIds);
		if (null == gfuIds || gfuIds.isEmpty()) {
			return new HashMap<String, GiftForUserPojo>();
		}

		Map<String, GiftForUserPojo> idToGfu =
				new HashMap<String, GiftForUserPojo>();
		List<GiftForUserPojo> gfuList = getUserGiftsForUser(gfuIds);
		for (GiftForUserPojo gfu : gfuList) {
			String id = gfu.getId();
			idToGfu.put(id, gfu);
		}

		return idToGfu;
	}

	public List<GiftForUserPojo> getUserGiftsForUser(Collection<String> gfuIds)
	{
		if (null == gfuIds || gfuIds.isEmpty()) {
			return new ArrayList<GiftForUserPojo>();
		}

		log.debug("retrieving GiftForUser for ids {}",
				gfuIds);
		List<GiftForUserPojo> gfuList = null;
		try {
			int amount = gfuIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);
			String query = String.format("select * from %s where %s in (%s)",
					TABLE_NAME, DBConstants.GIFT_FOR_USER__ID, questionMarkStr);

			gfuList = this.jdbcTemplate.query(query, gfuIds.toArray(), rowMapper);

		} catch (Exception e) {
			log.error(String.format(
					"GiftForUser retrieve db error, ids=%s", gfuIds), e);
			gfuList = new ArrayList<GiftForUserPojo>();
		}

		return gfuList;
	}

	public List<GiftForUserPojo> getUserGiftsForUser(String userId) {
		log.debug("retrieving GiftForUser for userId {}",
				userId);

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.GIFT_FOR_USER__RECEIVER_USER_ID);

		List<GiftForUserPojo> userGifts = null;
		try {
			userGifts = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error(" GiftForUser retrieve db error.", e);
			userGifts = new ArrayList<GiftForUserPojo>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userGifts;
	}

	////@Cacheable(value="specificUserStruct", key="#userStructId")
//	public GiftForUser getSpecificUserGift(String userId,
//			int id) {
//		log.debug(
//				"retrieving user  gift with userId={}, GiftId={}",
//				userId, id);
//
//		Object[] values = { userId, id };
//		String query = String.format("select * from %s where %s=? and %s=?",
//				TABLE_NAME, DBConstants.GIFT_FOR_USER__RECEIVER_USER_ID,
//				DBConstants.GIFT_FOR_USER__ID);
//
//		GiftForUser userGift = null;
//		try {
//			List<GiftForUser> bifuList = this.jdbcTemplate.query(query,
//					values, rowMapper);
//
//			if (null != bifuList && !bifuList.isEmpty()) {
//				userGift = bifuList.get(0);
//			}
//
//		} catch (Exception e) {
//			log.error(
//					String.format(
//							"battle item for user retrieve db error. userId=%s, GiftId=%s",
//							userId, id), e);
//		}
//
//		return userGift;
//	}

//	public List<GiftForUser> getSpecificOrAllUserGiftsForUser(
//			String userId, List<String> ids) {
//
//		StringBuilder querySb = new StringBuilder();
//		querySb.append("SELECT * FROM ");
//		querySb.append(TABLE_NAME);
//		querySb.append(" WHERE ");
//		querySb.append(DBConstants.GIFT_FOR_USER__RECEIVER_USER_ID);
//		querySb.append("=?");
//		List<Object> values = new ArrayList<Object>();
//		values.add(userId);
//
//		//if user didn't give any userStructIds then get all the user's structs
//		//else get the specific ids
//		if (ids != null
//				&& !ids.isEmpty()) {
//			log.debug("retrieving userGifts with GiftIds {}",
//					ids);
//			querySb.append(" AND ");
//			querySb.append(DBConstants.GIFT_FOR_USER__ID);
//			querySb.append(" IN (");
//
//			int amount = ids.size();
//			List<String> questions = Collections.nCopies(amount, "?");
//			String questionMarkStr = StringUtils.csvList(questions);
//
//			querySb.append(questionMarkStr);
//			querySb.append(");");
//			values.addAll(ids);
//		}
//
//		String query = querySb.toString();
//		log.info("query={}, values={}", query, values);
//
//		List<GiftForUser> userGifts = null;
//		try {
//			userGifts = this.jdbcTemplate.query(query, values.toArray(),
//					rowMapper);
//
//		} catch (Exception e) {
//			log.error("structure for user retrieve db error.", e);
//			userGifts = new ArrayList<GiftForUser>();
//
//		}
//
//		return userGifts;
//	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserGiftForClientMapper implements
			RowMapper<GiftForUserPojo> {

//		private static List<String> columnsSelected;

		@Override
		public GiftForUserPojo mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			String id = rs.getString(DBConstants.GIFT_FOR_USER__ID);
			String gifterUserId = rs.getString(DBConstants.GIFT_FOR_USER__GIFTER_USER_ID);
			String receiverUserId = rs.getString(DBConstants.GIFT_FOR_USER__RECEIVER_USER_ID);
			int giftId = rs.getInt(DBConstants.GIFT_FOR_USER__GIFT_ID);
			Timestamp ts = rs.getTimestamp(DBConstants.GIFT_FOR_USER__TIME_OF_ENTRY);

			int rewardId = rs.getInt(DBConstants.GIFT_FOR_USER__REWARD_ID);
			boolean collected = rs.getBoolean(DBConstants.GIFT_FOR_USER__COLLECTED);
			int minTillExp = rs.getInt(DBConstants.GIFT_FOR_USER__MINUTES_TILL_EXPIRATION);
			String reasonForGift = rs.getString(DBConstants.GIFT_FOR_USER__REASON_FOR_GIFT);

			return new GiftForUserPojo(id, gifterUserId, receiverUserId, giftId,
					ts, rewardId, collected, minTillExp, reasonForGift);
		}

//		public static List<String> getColumnsSelected() {
//			if (null == columnsSelected) {
//				columnsSelected = new ArrayList<String>();
//				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__ID);
//				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__USER_ID);
//				columnsSelected
//						.add(DBConstants.BATTLE_ITEM_FOR_USER__BATTLE_ITEM_ID);
//				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__QUANTITY);
//			}
//			return columnsSelected;
//		}
	}

}
