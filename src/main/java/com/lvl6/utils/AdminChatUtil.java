package com.lvl6.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.lvl6.events.response.PrivateChatPostResponseEvent;
import com.lvl6.info.AdminChatPost;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.EventChatProto.PrivateChatPostResponseProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.GameServer;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
public class AdminChatUtil {

	private static Logger log = LoggerFactory.getLogger(AdminChatUtil.class);

	@Autowired
	protected InsertUtil insertUtils;

	public void setInsertUtils(InsertUtil insertUtils) {
		this.insertUtils = insertUtils;
	}

	@Autowired
	protected GameServer server;

	public GameServer getServer() {
		return server;
	}

	public void setServer(GameServer server) {
		this.server = server;
	}

	/*	protected SimpleMailMessage getEmailTemplate() {
			return (SimpleMailMessage) AppContext.getApplicationContext().getBean("adminChatEmailTemplate");
		}*/

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	//	protected String iap = DBConstants.TABLE_IAP_HISTORY;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	public List<AdminChatPost> getMessagesToAndFromAdmin(int offset, int limit) {
		String query = "SELECT * FROM "
				+ DBConstants.TABLE_USER_PRIVATE_CHAT_POST
				+ " as chat where (chat."
				+ DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID + "="
				+ ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID + " or chat."
				+ DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID + "="
				+ ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID
				+ ") order by chat."
				+ DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST
				+ " DESC LIMIT " + limit + " OFFSET " + offset;
		List<AdminChatPost> msgs = jdbcTemplate.query(query,
				new RowMapper<AdminChatPost>() {
					@Override
					public AdminChatPost mapRow(ResultSet rs, int index)
							throws SQLException {
						return new AdminChatPost(
								rs.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__ID),
								rs.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID),
								rs.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID),
								rs.getDate(DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST),
								rs.getString(DBConstants.USER_PRIVATE_CHAT_POSTS__CONTENT));
					}
				});
		return addUsernames(msgs);
	}

	protected List<AdminChatPost> addUsernames(List<AdminChatPost> msgs) {
		if (msgs.size() == 0) {
			log.info("No messages for admins");
			return msgs;
		}
		List<String> users = new ArrayList<String>();
		for (AdminChatPost msg : msgs) {
			if (msg.getPosterId() != ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID) {
				users.add(msg.getPosterId());
			} else {
				users.add(msg.getRecipientId());
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", users);
		String query = "select " + DBConstants.USER__ID + ", "
				+ DBConstants.USER__NAME + " from " + DBConstants.TABLE_USER
				+ " where " + DBConstants.USER__ID + " in (:ids)";
		Map<Integer, String> usernames = namedParameterJdbcTemplate.query(
				query, params, new ResultSetExtractor<Map<Integer, String>>() {
					@Override
					public Map<Integer, String> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<Integer, String> usernames = new LinkedHashMap<Integer, String>();
						//log.info("Mapping usernames from ids");
						while (rs.next()) {

							int userId = rs.getInt(DBConstants.USER__ID);
							String userName = rs
									.getString(DBConstants.USER__NAME);
							log.info("id: " + userId + " name: " + userName);
							usernames.put(userId, userName);
						}
						return usernames;
					}
				});
		for (AdminChatPost msg : msgs) {
			if (msg.getPosterId() != ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID) {
				msg.setUsername(usernames.get(msg.getPosterId()));
			} else {
				msg.setUsername(usernames.get(msg.getRecipientId()));
			}
		}
		return msgs;
	}

	public void sendAdminChatMessage(AdminChatPost msg) {
		log.info("Sending admin chat message to user: " + msg.getUsername()
				+ " : " + msg.getRecipientId() + " content: "
				+ msg.getContent());
		String posterId = msg.getPosterId();
		String recipientId = msg.getRecipientId();
		String censoredContent = MiscMethods.censorUserInput(msg.getContent());
		String privateChatPostId = insertUtils.insertIntoPrivateChatPosts(
				posterId, recipientId, censoredContent, new Timestamp(msg
						.getTimeOfPost().getTime()));
		List<String> userIds = new ArrayList<String>();
		userIds.add(posterId);
		userIds.add(recipientId);
		Map<String, User> users = RetrieveUtils.userRetrieveUtils()
				.getUsersByIds(userIds);

		MinimumUserProto.Builder admin = MinimumUserProto.newBuilder()
				.setUserUuid(ControllerConstants.STARTUP__ADMIN_CHAT_USER_ID);
		admin.setName(users.get(posterId).getName());
		PrivateChatPostResponseProto.Builder resBuilder = PrivateChatPostResponseProto
				.newBuilder();
		resBuilder.setSender(admin.build());

		PrivateChatPostResponseEvent resEvent = new PrivateChatPostResponseEvent(
				posterId);
		resEvent.setTag(0);
		Timestamp timeOfPost = new Timestamp(new Date().getTime());
		//		if (privateChatPostId <= 0) {
		//			resBuilder.setStatus(PrivateChatPostStatus.OTHER_FAIL);
		//			log.error("problem with inserting private chat post into db. posterId=" + posterId
		//					+ ", recipientId=" + recipientId + ", content=" + msg.getContent() + ", censoredContent="
		//					+ censoredContent + ", timeOfPost=" + timeOfPost);
		//		} else {

		PrivateChatPost pwp = new PrivateChatPost(privateChatPostId, posterId,
				recipientId, timeOfPost, censoredContent);
		User poster = users.get(posterId);
		User recipient = users.get(recipientId);
		PrivateChatPostProto pcpp = CreateInfoProtoUtils
				.createPrivateChatPostProtoFromPrivateChatPost(pwp, poster,
						null, recipient, null, null);
		resBuilder.setPost(pcpp);

		// send to recipient of the private chat post
		PrivateChatPostResponseEvent resEvent2 = new PrivateChatPostResponseEvent(
				recipientId);
		resEvent2.setPrivateChatPostResponseProto(resBuilder.build());
		log.info("player " + resEvent2.getPlayerId() + " "
				+ server.getPlayerById(resEvent2.getPlayerId()));
		server.writeAPNSNotificationOrEvent(resEvent2);
		//		}
	}

	public void sendAdminChatEmail(AdminChatPost msg) {
		/*		SimpleMailMessage tmp = getEmailTemplate();
				tmp.setTo(new String[] {"4086555751@vtext.com","6509963609@mms.att.net"});
				tmp.setSubject("Admin Chat from "+msg.getPosterId()+": "+msg.getUsername());
				tmp.setText(msg.getContent());*/
	}

}
