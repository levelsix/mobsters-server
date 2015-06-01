package com.lvl6.test.controller.integrationtests;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.events.request.SendGroupChatRequestEvent;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.EventChatProto.SendGroupChatRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.SendGroupChatController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class SendGroupChatTest {

	private JdbcTemplate jdbcTemplate;

	private User user;
	private MinimumUserProto mup;
	private String userId;

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	BattleItemQueueForUserRetrieveUtil battleItemQueueForUserRetrieveUtil;

	@Autowired
	BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;

	@Autowired
	InsertUtil insertUtil;

	@Autowired
	SendGroupChatController sendGroupChatController;

	@Autowired
	MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	CreateInfoProtoUtils createInfoProtoUtils;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Before
	public void setUp() {
		log.info("setUp");
		Timestamp createTime = new Timestamp((new Date()).getTime());

		String name = "bobUnitTest";
		String udid = "bobUdid";
		int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
		int playerExp = 10;
		int cash = 10000;
		int oil = 10000;
		int gems = 10000;
		String deviceToken = "bobToken";
		String facebookId = null;
		int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
		String email = null;
		String fbData = null;

		userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash, oil,
				gems, false, deviceToken, createTime, facebookId,
				avatarMonsterId, email, fbData, 0);

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			throw new RuntimeException("no user was created!");
		}

		mup = createInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user,
				null);

	}

	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}

//		deleteSalesPurchase(userId);

		String query2 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values2 = new Object[] { user.getId() };
		int[] types2 = new int[] { java.sql.Types.VARCHAR };

		int numDeleted = jdbcTemplate.update(query2, values2, types2);
		if (numDeleted != 1) {
			log.error("did not delete test user when cleaning up");
		}

	}

//	private int deleteSalesPurchase(String userId) {
//		String tableName = DBConstants.TABLE_IAP_HISTORY;
//		String condDelim = "and";
//		Map<String, Object> conditionParams = new HashMap<String, Object>();
//		int totalDeleted = 0;
//
//		conditionParams.put(DBConstants.IAP_HISTORY__USER_ID,
//				userId);
//		int numDeleted = DBConnection.get().deleteRows(tableName,
//				conditionParams, condDelim);
//		totalDeleted += numDeleted;
//
//		return totalDeleted;
//	}

//	@Test
//	public void testSendGroupChat() {
//		User user1 = userRetrieveUtil.getUserById(userId);
//		int userGems1 = user1.getGems();
//		SendGroupChatRequestProto.Builder sgcrpb = SendGroupChatRequestProto
//				.newBuilder();
//
//		sgcrpb.setSender(mup);
//		sgcrpb.setScope(ChatScope.GLOBAL);
//		sgcrpb.setChatMessage("this is a test");
//		sgcrpb.setClientTime(new Date().getTime());
//		sgcrpb.setGlobalLanguage(TranslateLanguages.ENGLISH);
//
//		SendGroupChatRequestEvent sgcre = new SendGroupChatRequestEvent();
//		sgcre.setTag(1);
//		sgcre.setSendGroupChatRequestProto(sgcrpb.build());
//		sendGroupChatController.handleEvent(sgcre);
//
//		log.info(" receive group chat response proto: {} ", sendGroupChatController.getRgcrp());
//
//	}

}
