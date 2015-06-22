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

import com.lvl6.info.PrivateChatPost;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class PrivateChatPostRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(PrivateChatPostRetrieveUtils2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_USER_PRIVATE_CHAT_POST;
	private static final PrivatChatPostForClientMapper rowMapper = new PrivatChatPostForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//the ones where user is recipient
	public List<PrivateChatPost> getUserPrivateChatPost(String userId) {
		log.debug(String.format("retrieving user private chat posts for userId %s",
				userId));

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID);

		List<PrivateChatPost> userPrivateChatPosts = null;
		try {
			userPrivateChatPosts = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("battle item for user retrieve db error.", e);
			userPrivateChatPosts = new ArrayList<PrivateChatPost>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userPrivateChatPosts;
	}
	
	public List<PrivateChatPost> getPrivateChatPostsBetweenUsersBeforePostId(
			int limit, String userOne, String userTwo) {
		log.info(String
				.format("retrieving %s private chat posts for userOne %s and userTwo %s",
						limit, userOne, userTwo));

		String query = "";
		List<Object> values = new ArrayList<Object>();
		query += "SELECT * " + "FROM " + TABLE_NAME + " " + "WHERE "
				+ DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID + " IN (?,?) ";
		values.add(userOne);
		values.add(userTwo);
		query += "AND " + DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID
				+ " IN (?,?) ";
		values.add(userOne);
		values.add(userTwo);

		query += "ORDER BY "
				+ DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST
				+ " DESC  LIMIT ?";
		values.add(limit);

		List<PrivateChatPost> privateChatPosts = null;
		try {
			privateChatPosts = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);
		} catch (Exception e) {
			log.error("private chat post retrieve db error.", e);
		}
		return privateChatPosts;
	}

	public Map<String, PrivateChatPost> getMostRecentPrivateChatPostsByOrToUser(
			String userId, boolean isRecipient, int limit) {
		log.debug(String
				.format("retrieving most recent private chat posts. userId %s isRecipient=%s",
						userId, isRecipient));

		String otherPersonColumn = null;
		String column = null;

		if (isRecipient) {
			otherPersonColumn = DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID;
			column = DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID;
		} else {
			otherPersonColumn = DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID;
			column = DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID;
		}
		List<Object> values = new ArrayList<Object>();

		//TODO: use duple (timestamp, otherPersonColumn)
		//get last post id between specified user and person said user chatted with
		String subquery = String.format(
				"(SELECT max(%s) as %s, %s FROM %s WHERE %s=? GROUP BY %s)",
				DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST,
				DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST,
				otherPersonColumn, TABLE_NAME, column, otherPersonColumn);
		//		subquery +=
		//			"(SELECT max(" + DBConstants.USER_PRIVATE_CHAT_POSTS__ID + ") as id " + 
		//				"FROM " + TABLE_NAME + " " +
		//				"WHERE " + column + "=? " +
		//				"GROUP BY " + otherPersonColumn + ")";
		values.add(userId);

		String query = String
				.format("SELECT pcp.* FROM %s as timeList LEFT JOIN %s as pcp ON timeList.%s=pcp.%s AND timeList.%s=pcp.%s AND pcp.%s=? ORDER BY pcp.%s DESC LIMIT ?",
						subquery, TABLE_NAME,
						DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST,
						DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST,
						otherPersonColumn, otherPersonColumn, column,
						DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST);
		//get the actual posts to those ids
		//		query +=
		//			"SELECT pcp.* " +
		//				"FROM " + subquery + " as idList " +
		//				"LEFT JOIN " +
		//				TABLE_NAME + " as pcp " +
		//				"ON idList.id=pcp.id " +
		//				"ORDER BY pcp.time_of_post DESC " +
		//				"LIMIT ?";
		values.add(userId);
		values.add(limit);

		log.info(String.format("query=%s, values=%s", query, values));

		Map<String, PrivateChatPost> idsToPrivateChatPosts = new HashMap<String, PrivateChatPost>();
		try {
			List<PrivateChatPost> chats = this.jdbcTemplate.query(query,
					values.toArray(), rowMapper);

			for (PrivateChatPost pcp : chats) {
				String id = pcp.getId();
				idsToPrivateChatPosts.put(id, pcp);
			}

		} catch (Exception e) {
			log.error("private chat post retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return idsToPrivateChatPosts;
	}

	//
	//	private static List<PrivateChatPost> convertRSToPrivateChatPosts(ResultSet rs) {
	//		if (rs != null) {
	//			try {
	//				rs.last();
	//				rs.beforeFirst();
	//				List<PrivateChatPost> wallPosts = new ArrayList<PrivateChatPost>();
	//				while(rs.next()) {
	//					PrivateChatPost pwp = convertRSRowToPrivateChatPost(rs);
	//					if (pwp != null) wallPosts.add(pwp);
	//				}
	//				return wallPosts;
	//			} catch (SQLException e) {
	//				log.error("problem with database call.", e);
	//
	//			}
	//		}
	//		return null;
	//	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class PrivatChatPostForClientMapper implements
			RowMapper<PrivateChatPost> {

		private static List<String> columnsSelected;

		@Override
		public PrivateChatPost mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PrivateChatPost pcp = new PrivateChatPost();

			pcp.setId(rs.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__ID));
			pcp.setPosterId(rs
					.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID));
			pcp.setRecipientId(rs
					.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID));
			try {
				Timestamp time = rs
						.getTimestamp(DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					pcp.setTimeOfPost(date);
				}
			} catch (Exception e) {
				log.error(String.format(
						"maybe time of post is invalid, pcp=%s", pcp), e);
			}
			pcp.setContent(rs
					.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__CONTENT));
			
			pcp.setContentLanguage(rs.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__CONTENT_LANGUAGE));

			return pcp;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.USER_PRIVATE_CHAT_POSTS__ID);
				columnsSelected
						.add(DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID);
				columnsSelected
						.add(DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID);
				columnsSelected
						.add(DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID);
				columnsSelected
						.add(DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST);
			}
			return columnsSelected;
		}
	}
}
