package com.lvl6.test.controller;
//package com.lvl6.test.ControllerTests;
//
//import static org.junit.Assert.*;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.lvl6.events.request.SendGroupChatRequestEvent;
//import com.lvl6.info.ChatTranslations;
//import com.lvl6.info.Clan;
//import com.lvl6.info.ResearchForUser;
//import com.lvl6.info.User;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.proto.ChatProto.GroupChatScope;
//import com.lvl6.proto.EventChatProto.SendGroupChatRequestProto;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.UserRetrieveUtils2;
//import com.lvl6.retrieveutils.rarechange.ChatTranslationsRetrieveUtils;
//import com.lvl6.server.controller.SendGroupChatController;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
//public class ClanChatTest {
//
//	private JdbcTemplate jdbcTemplate;
//
//	private User user;
//	private MinimumUserProto mup;
//	private String userId;
//
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	@Autowired
//	UserRetrieveUtils2 userRetrieveUtil;
//
//	@Autowired
//	SendGroupChatController sendGroupChatController;
//
//	@Autowired
//	InsertUtil insertUtil;
//
//
//
//	@Resource
//	public void setDataSource(DataSource dataSource) {
//		log.info("Setting datasource and creating jdbcTemplate");
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//
//	@Before
//	public void setUp() {
//		log.info("setUp");
//		Timestamp createTime = new Timestamp((new Date()).getTime());
//
//		String name = "bobUnitTest";
//		String udid = "bobUdid";
//		int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
//		int playerExp = 10;
//		int cash = 10000;
//		int oil = 10000;
//		int gems = 10000;
//		String deviceToken = "bobToken";
//		String facebookId = null;
//		int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
//		String email = null;
//		String fbData = null;
//
//		userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash, oil,
//				gems, false, deviceToken, createTime, facebookId,
//				avatarMonsterId, email, fbData);
//		
//		Timestamp now = new Timestamp(new Date().getTime());
//	
//		//random clan id from db
//		insertUtil.insertUserClan(userId, "41578749-673b-4701-8e63-866d506976b2",
//				"MEMBER", now);
//
//		user = userRetrieveUtil.getUserById(userId);
//		
//		insertClan();
//
//		if (null == user) {
//			throw new RuntimeException("no user was created!");
//		}
//
//		mup = CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user,
//				null);
//
//	}
//
//	private void insertClan() {
//		log.debug("retrieving researchForUser for userId {}", userId);
//
//		//			Object[] values = { "41578749-673b-4701-8e63-866d506976b2", userId };
//		//			String query = String.format("update %s set %s=? where %s=?", DBConstants.TABLE_USER,
//		//					DBConstants.USER__CLAN_ID, DBConstants.USER__ID);
//
//		try {
//			this.jdbcTemplate.update("update user set clan_id=? where id=?", "41578749-673b-4701-8e63-866d506976b2", userId);
//		} catch (Exception e) {
//			log.error("ResearchForUser retrieve db error.", e);
// 
//			//		} finally {
//			//			DBConnection.get().close(rs, null, conn);
//		}
//	}
//
//	@After
//	public void tearDown() {
//		if (null == user) {
//			log.info("no user to delete");
//			return;
//		}
//
//		String query2 = String.format("DELETE FROM %s where %s=?",
//				DBConstants.TABLE_USER, DBConstants.USER__ID);
//		Object[] values2 = new Object[] { user.getId() };
//		int[] types2 = new int[] { java.sql.Types.VARCHAR };
//
//		int numDeleted = jdbcTemplate.update(query2, values2, types2);
//		if (numDeleted != 1) {
//			log.error("did not delete test user when cleaning up");
//		}
//
//	}
//
//	@Test
//	public void testClanChats() {
//		User user1 = userRetrieveUtil.getUserById(userId);
//		Map<String, ChatTranslations> chatTranslationsMap = ChatTranslationsRetrieveUtils.getChatTranslationsIdsToChatTranslationss();
//		int prevSize = chatTranslationsMap.size();
//		
//		SendGroupChatRequestProto.Builder sgcrpb = SendGroupChatRequestProto
//				.newBuilder();
//		
//		sgcrpb.setSender(mup);
//		GroupChatScope gcs = GroupChatScope.CLAN;
//		sgcrpb.setScope(gcs);
//		sgcrpb.setChatMessage("your dick exploded");
//		sgcrpb.setClientTime(new Date().getTime());
//
//		SendGroupChatRequestEvent sgcre = new SendGroupChatRequestEvent();
//		sgcre.setTag(1);
//		sgcre.setSendGroupChatRequestProto(sgcrpb.build());
//		sendGroupChatController.handleEvent(sgcre);
//		
//		Map<String, ChatTranslations> chatTranslationsMap2 = ChatTranslationsRetrieveUtils.getChatTranslationsIdsToChatTranslationss();
//		int newSize = chatTranslationsMap2.size();
//		
//		assertTrue(prevSize + 7 == newSize);
//		
//	}
//
//	
//}
