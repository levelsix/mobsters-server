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

import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventDungeonProto.BeginDungeonRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class DungeonTest {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private JdbcTemplate jdbcTemplate;
	private static boolean endOfTesting;
	private static User user;
	private static MinimumUserProto mup;
	private static String userId;
	
	@Autowired
	InsertUtil insertUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	CreateInfoProtoUtils createInfoProtoUtils;


	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Before
	public void setUp() {
		if (userId == null) {
			endOfTesting = false;
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

			userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
					oil, gems, false, deviceToken, createTime, facebookId,
					avatarMonsterId, email, fbData, 0);
			user = userRetrieveUtil.getUserById(userId);
		}
	}
	
	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}

		String query2 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values2 = new Object[] { userId };
		int[] types2 = new int[] { java.sql.Types.VARCHAR };

		int numDeleted2 = jdbcTemplate.update(query2, values2, types2);
		if (numDeleted2 != 1) {
			log.error("did not delete test user when cleaning up");
		}
	}
	
	@Test
	public void testDungeon() {
		BeginDungeonRequestProto.Builder bdrpb = BeginDungeonRequestProto.newBuilder();
		bdrpb.setSender(createInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		bdrpb.setClientTime(new Date().getTime());
		bdrpb.setTaskId(2);
		bdrpb.setIsEvent(false);
		bdrpb.setGemsSpent(0);
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
