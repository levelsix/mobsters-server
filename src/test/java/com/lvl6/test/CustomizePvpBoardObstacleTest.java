package com.lvl6.test;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import junit.framework.TestCase;

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

import com.lvl6.events.request.CustomizePvpBoardObstacleRequestEvent;
import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleRequestProto;
import com.lvl6.proto.StructureProto.UserPvpBoardObstacleProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.CustomizePvpBoardObstacleController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class CustomizePvpBoardObstacleTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	private InsertUtil insertUtil;

	@Autowired
	private DeleteUtil deleteUtil;

	@Autowired
	private CustomizePvpBoardObstacleController customizePvpBoardObstacleController;
	
	private JdbcTemplate jdbcTemplate;

	private User user;
	private MinimumUserProto mup;

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
		int cash = 0;
		int oil = 0;
		int gems = 0;
		String deviceToken = "bobToken";
		String facebookId = null;
		int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
		String email = null;
		String fbData = null;

		String userId = insertUtil.insertUser(name, udid, lvl,  playerExp, cash, oil,
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
	//	@Rollback(true) //doesn't roll back transaction >:C
	//	@Transactional //just manually undo...
	public void testCreateDestroyBoardObstacle() {
		String userId = user.getId(); 
		PvpBoardObstacleForUser pbofu = createPvpBoardObstacle(userId);
		UserPvpBoardObstacleProto upbop = CreateInfoProtoUtils
				.createUserPvpBoardObstacleProto(pbofu);

		//construct the request proto
		CustomizePvpBoardObstacleRequestProto.Builder cpborpb =
				CustomizePvpBoardObstacleRequestProto.newBuilder();
		cpborpb.setSender(mup);
		cpborpb.addNuOrUpdatedObstacles(upbop);
		
		//construct the request event to create obstacle
		CustomizePvpBoardObstacleRequestEvent cpbore =
				new CustomizePvpBoardObstacleRequestEvent();
		cpbore.setCustomizePvpBoardObstacleRequestProto(cpborpb.build());
		cpbore.setTag(1);
		
		//process the request event
		customizePvpBoardObstacleController.handleEvent(cpbore);
		
		
		
		//construct the request proto
		cpborpb = CustomizePvpBoardObstacleRequestProto.newBuilder();
		cpborpb.setSender(mup);
		cpborpb.addRemoveUpboIds(pbofu.getId());
		
		//construct the request event to delete obstacle
		cpbore.setCustomizePvpBoardObstacleRequestProto(cpborpb.build());
		cpbore.setTag(2); //just so it's different, not necessary I think
		
		//process the request event
		customizePvpBoardObstacleController.handleEvent(cpbore);
		
	}

	private PvpBoardObstacleForUser createPvpBoardObstacle(String userId) {
		PvpBoardObstacleForUser pbofu = new PvpBoardObstacleForUser();
		int id = 1;
		pbofu.setId(id);
		pbofu.setUserId(userId);
		pbofu.setObstacleId(1);
		pbofu.setPosX(1);
		pbofu.setPosY(1);
		return pbofu;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil()
	{
		return userRetrieveUtil;
	}


	public void setUserRetrieveUtil( UserRetrieveUtils2 userRetrieveUtil )
	{
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public InsertUtil getInsertUtil()
	{
		return insertUtil;
	}

	public void setInsertUtil( InsertUtil insertUtil )
	{
		this.insertUtil = insertUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

}
