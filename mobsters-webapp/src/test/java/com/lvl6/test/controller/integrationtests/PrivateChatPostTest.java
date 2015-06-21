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

import com.lvl6.events.request.PrivateChatPostRequestEvent;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.EventChatProto.PrivateChatPostRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.PrivateChatPostController;
import com.lvl6.server.eventsender.EventsUtil;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class PrivateChatPostTest {

	private static Logger log = LoggerFactory.getLogger(PrivateChatPostTest.class);

	
	private JdbcTemplate jdbcTemplate;

	private User user;
	private MinimumUserProto mup;
	private String userId;

	private User user2;
	private MinimumUserProto mup2;
	private String userId2;


	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	BattleItemQueueForUserRetrieveUtil battleItemQueueForUserRetrieveUtil;

	@Autowired
	BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;

	@Autowired
	InsertUtil insertUtil;

	@Autowired
	PrivateChatPostController privateChatPostController;

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

		String name2 = "bobUnitTest2";
		String udid2 = "bobUdid2";
		int lvl2 = ControllerConstants.USER_CREATE__START_LEVEL;
		int playerExp2 = 10;
		int cash2 = 10000;
		int oil2 = 10000;
		int gems2 = 10000;
		String deviceToken2 = "bobToken2";
		String facebookId2 = null;
		int avatarMonsterId2 = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
		String email2 = null;
		String fbData2 = null;

		userId2 = insertUtil.insertUser(name2, udid2, lvl2, playerExp2, cash2, oil2,
				gems2, false, deviceToken2, createTime, facebookId2,
				avatarMonsterId2, email2, fbData2, 0);

		user2 = userRetrieveUtil.getUserById(userId2);

		if (null == user2) {
			throw new RuntimeException("no user was created!");
		}

		mup2 = createInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user2,
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

	@Test
	public void testPrivateChatPost() {
		User user1 = userRetrieveUtil.getUserById(userId);
		int userGems1 = user1.getGems();
		PrivateChatPostRequestProto.Builder pcprpb = PrivateChatPostRequestProto
				.newBuilder();

		pcprpb.setSender(mup);
		pcprpb.setRecipientUuid(userId2);
		pcprpb.setContent("this is  test content");
		pcprpb.setContentLanguage(TranslateLanguages.ENGLISH);

		PrivateChatPostRequestEvent pcpre = new PrivateChatPostRequestEvent();
		pcpre.setTag(1);
		pcpre.setPrivateChatPostRequestProto(pcprpb.build());
		privateChatPostController.processRequestEvent(pcpre, EventsUtil.getToClientEventsForUnitTest());

		log.info("private chat post responseproto : {}", privateChatPostController.getPcprp());
	}

}
