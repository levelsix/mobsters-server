package com.lvl6.test.ControllerTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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

import com.lvl6.events.request.CreateClanRequestEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventClanProto.CreateClanRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.CreateClanController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ClanTest {


	private JdbcTemplate jdbcTemplate;

	private User user;
	private MinimumUserProto mup;
	private String userId;


	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	ClanRetrieveUtils2 clanRetrieveUtil;
	
	@Autowired
	InsertUtil insertUtil;

	@Autowired
	CreateClanController createClanController;

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

		userId = insertUtil.insertUser(name, udid, lvl,  playerExp, cash, oil,
				gems, false, deviceToken, createTime, facebookId, avatarMonsterId,
				email, fbData);

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			throw new RuntimeException("no user was created!");
		}

		mup = CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null);

	}
	
	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}

		String query = String.format(
				"DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER,
				DBConstants.USER__ID);
		Object[] values = new Object[] {
				user.getId()
		};
		int[] types = new int[] {
				java.sql.Types.VARCHAR
		};

		int numDeleted = jdbcTemplate.update( query, values, types );

	}
	
	
	@Test
	public void testCreatingClan() {
		CreateClanRequestProto.Builder ccrpb = CreateClanRequestProto.newBuilder();
		User user = userRetrieveUtil.getUserById(userId);
		int userGems = user.getGems();
		int userCash = user.getCash();

		ccrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		ccrpb.setName("test clan");
		ccrpb.setTag("tes");
		ccrpb.setRequestToJoinClanRequired(true);
		ccrpb.setDescription("description");
		ccrpb.setClanIconId(1);
		ccrpb.setGemsSpent(100);
		ccrpb.setCashChange(100);
		
		CreateClanRequestEvent ccre = new CreateClanRequestEvent();
		ccre.setTag(1);
		ccre.setCreateClanRequestProto(ccrpb.build());
		createClanController.handleEvent(ccre);
		
		User user2 = userRetrieveUtil.getUserById(userId);
		assertEquals(userGems - 100, user2.getGems());
		assertEquals(userCash - 100, user2.getCash());
		
		List<Clan> clanList = clanRetrieveUtil.getClansWithSimilarNameOrTag("test clan", "tes");
		assertTrue(!clanList.isEmpty());

		
		
	}
	
	
	
	
	
	
	

}
