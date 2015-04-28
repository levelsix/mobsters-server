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

import com.lvl6.events.request.RetrieveMiniEventRequestEvent;
import com.lvl6.info.MiniEventForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventRequestProto;
import com.lvl6.proto.ItemsProto.ItemType;
import com.lvl6.proto.MiniEventProtos.MiniEventGoalProto.MiniEventGoalType;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.RetrieveMiniEventController;
import com.lvl6.server.controller.UpdateMiniEventController;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class MiniEventTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected TimeUtils timeUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	private InsertUtil insertUtil;

	@Autowired
	private DeleteUtil deleteUtil;

	@Autowired
	protected MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil;

	@Autowired
	private RetrieveMiniEventController retrieveMiniEventController;

	@Autowired
	private UpdateMiniEventController updateMiniEventController;

	private JdbcTemplate jdbcTemplate;

	private String userId;
	private User user;
	private MinimumUserProto mup;
	private int miniEventId;
	private int miniEventForPlayerLvlId;
	private int itemId;
	private int rewardId;
	private int miniEventTierRewardId;
	private int rewardTier;
	private int miniEventGoalId;
	private int miniEventLeaderboardRewardId;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	@Before
	public void setUp() {
		log.info("setUp");
		Date now = new Date();
		Timestamp createTime = new Timestamp(now.getTime());

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

		userId = insertUtil.insertUser(name, udid, lvl, playerExp, cash,
				oil, gems, false, deviceToken, createTime, facebookId,
				avatarMonsterId, email, fbData);

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			throw new RuntimeException("no user was created!");
		}

		mup = CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user,
				null);

		createMiniEvent(now);

		createMiniEventForPlayerLvl();

		createItem();

		createReward();

		createMiniEventTierReward();

		createMiniEventGoal();

		createMiniEventLeaderBoardReward();

//		log.info("{}", MiniEventRetrieveUtils.getAllIdsToMiniEvents());
		MiscMethods.reloadAllRareChangeStaticData();
//		log.info("{}", MiniEventRetrieveUtils.getAllIdsToMiniEvents());
	}

	private void createMiniEvent(Date now) {
		miniEventId = 9999;
		String query = String.format(
				"INSERT INTO %s.%s (%s,%s,%s,%s,%s,%s) VALUE (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE %s=values(%s), %s=values(%s)",
				"mobsters", DBConstants.TABLE_MINI_EVENT_CONFIG,
				DBConstants.MINI_EVENT__ID,
				DBConstants.MINI_EVENT__START_TIME,
				DBConstants.MINI_EVENT__END_TIME,
				DBConstants.MINI_EVENT__NAME,
				DBConstants.MINI_EVENT__DESCRIPTION,
				DBConstants.MINI_EVENT__IMG,
				DBConstants.MINI_EVENT__START_TIME,
				DBConstants.MINI_EVENT__START_TIME,
				DBConstants.MINI_EVENT__END_TIME,
				DBConstants.MINI_EVENT__END_TIME);
		Date startTime = timeUtil.createDateAddDays(now, -1);
		Date endTime = timeUtil.createDateAddDays(now, 1);
		Object[] values = new Object[] {
				miniEventId,
				new Timestamp(startTime.getTime()),
				new Timestamp(endTime.getTime()),
				"foo",
				"bar",
				"baz"
		};
		int[] types = new int[] {
				java.sql.Types.INTEGER,
				java.sql.Types.TIMESTAMP,
				java.sql.Types.TIMESTAMP,
				java.sql.Types.VARCHAR,
				java.sql.Types.VARCHAR,
				java.sql.Types.VARCHAR
		};

		log.info("{} {} {}",
				new Object[] { query, values, types });

		int numInserted = jdbcTemplate.update(query, values, types);
		log.info("numInserted={}", numInserted);
	}

	private void createMiniEventForPlayerLvl() {
		miniEventForPlayerLvlId = 9999;
		String query = String.format(
				"INSERT INTO %s.%s (%s,%s,%s,%s,%s,%s,%s) VALUE (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE %s=values(%s), %s=values(%s)",
				"mobsters", DBConstants.TABLE_MINI_EVENT_FOR_PLAYER_LVL_CONFIG,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__ID,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__MINI_EVENT_ID,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MIN,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MAX,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__TIER_ONE_MIN_PTS,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__TIER_TWO_MIN_PTS,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__TIER_THREE_MIN_PTS,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MIN,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MIN,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MAX,
				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MAX);

		int userLvl = user.getLevel();
		Object[] values = new Object[] {
				miniEventForPlayerLvlId,
				miniEventId,
				userLvl,
				userLvl + 1,
				1,
				2,
				3
		};
		int[] types = new int[] {
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER
		};

		int numInserted = jdbcTemplate.update(query, values, types);
		log.info("numInserted={}", numInserted);
	}

	private void createItem() {
		itemId = 0;
		String query = String.format(
				"INSERT IGNORE INTO %s.%s (%s,%s,%s,%s,%s,%s,%s,%s) VALUE (?,?,?,?,?,?,?,?)",
				"mobsters", DBConstants.TABLE_ITEM_CONFIG,
				DBConstants.ITEM__ID,
				DBConstants.ITEM__NAME,
				DBConstants.ITEM__IMG_NAME,
				DBConstants.ITEM__ITEM_TYPE,
				DBConstants.ITEM__STATIC_DATA_ID,
				DBConstants.ITEM__AMOUNT,
				DBConstants.ITEM__SECRET_GIFT_CHANCE,
				DBConstants.ITEM__ALWAYS_DISPLAY_TO_USER);

		Object[] values = new Object[] {
				itemId,
				"foo",
				"bar",
				ItemType.ITEM_CASH,
				0,
				50,
				0.5,
				0
		};
		int[] types = new int[] {
				java.sql.Types.INTEGER,
				java.sql.Types.VARCHAR,
				java.sql.Types.VARCHAR,
				java.sql.Types.VARCHAR,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.FLOAT,
				java.sql.Types.BOOLEAN
		};

		int numInserted = jdbcTemplate.update(query, values, types);
		log.info("numInserted={}", numInserted);
	}

	private void createReward() {
		rewardId = 9999;
		String query = String.format(
				"INSERT IGNORE INTO %s.%s (%s,%s,%s,%s) VALUE (?,?,?,?)",
				"mobsters", DBConstants.TABLE_REWARD_CONFIG,
				DBConstants.REWARD__ID,
				DBConstants.REWARD__STATIC_DATA_ID,
				DBConstants.REWARD__TYPE,
				DBConstants.REWARD__AMT);

		Object[] values = new Object[] {
				rewardId,
				itemId,
				RewardType.ITEM.name(),
				50
		};
		int[] types = new int[] {
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.VARCHAR,
				java.sql.Types.INTEGER
		};

		int numInserted = jdbcTemplate.update(query, values, types);
		log.info("numInserted={}", numInserted);
	}

	private void createMiniEventTierReward() {
		miniEventTierRewardId = 9999;
		rewardTier = 1;
		String query = String.format(
				"INSERT IGNORE INTO %s.%s (%s,%s,%s,%s) VALUE (?,?,?,?)",
				"mobsters", DBConstants.TABLE_MINI_EVENT_TIER_REWARD_CONFIG,
				DBConstants.MINI_EVENT_TIER_REWARD__ID,
				DBConstants.MINI_EVENT_TIER_REWARD__MINI_EVENT_FOR_PLAYER_LVL_ID,
				DBConstants.MINI_EVENT_TIER_REWARD__REWARD_ID,
				DBConstants.MINI_EVENT_TIER_REWARD__REWARD_TIER);

		Object[] values = new Object[] {
				miniEventTierRewardId,
				miniEventForPlayerLvlId,
				rewardId,
				rewardTier
		};
		int[] types = new int[] {
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER
		};

		int numInserted = jdbcTemplate.update(query, values, types);
		log.info("numInserted={}", numInserted);
	}

	private void createMiniEventGoal() {
		miniEventGoalId = 0;
		String query = String.format(
				"INSERT IGNORE INTO %s.%s (%s,%s,%s,%s,%s,%s) VALUE (?,?,?,?,?,?)",
				"mobsters", DBConstants.TABLE_MINI_EVENT_GOAL_CONFIG,
				DBConstants.MINI_EVENT_GOAL__ID,
				DBConstants.MINI_EVENT_GOAL__MINI_EVENT_ID,
				DBConstants.MINI_EVENT_GOAL__TYPE,
				DBConstants.MINI_EVENT_GOAL__AMT,
				DBConstants.MINI_EVENT_GOAL__DESCRIPTION,
				DBConstants.MINI_EVENT_GOAL__PTS_REWARD);

		Object[] values = new Object[] {
				miniEventGoalId,
				miniEventId,
				MiniEventGoalType.NO_GOAL.name(),
				0,
				"foo",
				50
		};
		int[] types = new int[] {
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.VARCHAR,
				java.sql.Types.INTEGER,
				java.sql.Types.VARCHAR,
				java.sql.Types.INTEGER
		};

		int numInserted = jdbcTemplate.update(query, values, types);
		log.info("numInserted={}", numInserted);
	}

	private void createMiniEventLeaderBoardReward() {
		miniEventLeaderboardRewardId = 9999;
		String query = String.format(
				"INSERT IGNORE INTO %s.%s (%s,%s,%s,%s) VALUE (?,?,?,?)",
				"mobsters", DBConstants.TABLE_MINI_EVENT_LEADERBOARD_REWARD_CONFIG,
				DBConstants.MINI_EVENT_LEADERBOARD_REWARD__ID,
				DBConstants.MINI_EVENT_LEADERBOARD_REWARD__MINI_EVENT_ID,
				DBConstants.MINI_EVENT_LEADERBOARD_REWARD__REWARD_ID,
				DBConstants.MINI_EVENT_LEADERBOARD_REWARD__LEADERBOARD_POS);

		Object[] values = new Object[] {
				miniEventLeaderboardRewardId,
				miniEventId,
				rewardId,
				50
		};
		int[] types = new int[] {
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER,
				java.sql.Types.INTEGER
		};

		int numInserted = jdbcTemplate.update(query, values, types);
		log.info("numInserted={}", numInserted);
	}

	@Override
	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}

		String query = String.format("DELETE FROM %s WHERE %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values = new Object[] { userId };
		int[] types = new int[] { java.sql.Types.VARCHAR };
		int numDeleted = jdbcTemplate.update(query, values, types);

		deleteFromTable(DBConstants.TABLE_MINI_EVENT_FOR_USER,
				DBConstants.MINI_EVENT_FOR_USER__USER_ID,
				userId,
				java.sql.Types.VARCHAR,
				values, types);

//		deleteFromTable(DBConstants.TABLE_MINI_EVENT_CONFIG,
//				DBConstants.MINI_EVENT__ID,
//				miniEventId,
//				java.sql.Types.INTEGER,
//				values, types);
//
//		deleteFromTable(DBConstants.TABLE_MINI_EVENT_FOR_PLAYER_LVL_CONFIG,
//				DBConstants.MINI_EVENT_FOR_PLAYER_LVL__ID,
//				miniEventForPlayerLvlId,
//				java.sql.Types.INTEGER,
//				values, types);
//
//		deleteFromTable(DBConstants.TABLE_ITEM_CONFIG,
//				DBConstants.ITEM__ID,
//				itemId,
//				java.sql.Types.INTEGER,
//				values, types);
//
//		deleteFromTable(DBConstants.TABLE_REWARD_CONFIG,
//				DBConstants.REWARD__ID,
//				rewardId,
//				java.sql.Types.INTEGER,
//				values, types);
//
//		deleteFromTable(DBConstants.TABLE_MINI_EVENT_TIER_REWARD_CONFIG,
//				DBConstants.MINI_EVENT_TIER_REWARD__ID,
//				miniEventTierRewardId,
//				java.sql.Types.INTEGER,
//				values, types);
//
//		deleteFromTable(DBConstants.TABLE_MINI_EVENT_GOAL_CONFIG,
//				DBConstants.MINI_EVENT_GOAL__ID,
//				miniEventGoalId,
//				java.sql.Types.INTEGER,
//				values, types);
//
//		deleteFromTable(DBConstants.TABLE_MINI_EVENT_LEADERBOARD_REWARD_CONFIG,
//				DBConstants.MINI_EVENT_LEADERBOARD_REWARD__ID,
//				miniEventLeaderboardRewardId,
//				java.sql.Types.INTEGER,
//				values, types);
	}

	private void deleteFromTable(String tableName, String primaryKey,
			Object value, int type, Object[] values, int[] types)
	{
		String query = String.format("DELETE FROM %s WHERE %s=?",
				tableName, primaryKey);
		values[0] = value;
		types[0] = type;
		int numDeleted = jdbcTemplate.update(query, values, types);

	}

	@Test
	//	@Rollback(true) //doesn't roll back transaction >:C
	//	@Transactional //just manually undo...
	public void testRetrieveMiniEvent() {
		String userId = user.getId();

		//make sure no MiniEvent exists
		MiniEventForUser mefu = miniEventForUserRetrieveUtil
				.getSpecificUserMiniEvent(userId);
		assertNull(mefu);

		//construct the request proto
		RetrieveMiniEventRequestProto.Builder rmerpb = RetrieveMiniEventRequestProto
				.newBuilder();
		rmerpb.setSender(mup);

		//construct the request event
		RetrieveMiniEventRequestEvent rmere = new RetrieveMiniEventRequestEvent();
		rmere.setRetrieveMiniEventRequestProto(rmerpb.build());
		rmere.setTag(1);

		//process the request event
		retrieveMiniEventController.handleEvent(rmere);

		//make sure a MiniEventForUser now exists
		mefu = miniEventForUserRetrieveUtil
				.getSpecificUserMiniEvent(userId);
		assertNotNull(mefu);

		//process the request event again to make sure that no errors occur
		//when existing MiniEventForUser exists
		retrieveMiniEventController.handleEvent(rmere);

	}

//	@Test
//	public void testUpdateMiniEvent() {
//		String userId = user.getId();
//
//		//hopefully this creates an existing mini_event_for_user
//		testRetrieveMiniEvent();
//
//		MiniEventForUser mefu = miniEventForUserRetrieveUtil
//				.getSpecificUserMiniEvent(userId);
//		assertNotNull(mefu);
//
//		MiniEventForUser updatedMefu = new MiniEventForUser(mefu);
//		updatedMefu.setPtsEarned(mefu.getPtsEarned() + 1);
//
//		//construct the request proto
//		UpdateMiniEventRequestProto.Builder umerpb = UpdateMiniEventRequestProto
//				.newBuilder();
//		umerpb.setSender(mup);
//
//		UserMiniEventProto updatedUmep = CreateInfoProtoUtils
//				.createUserMiniEventProto(updatedMefu)
//				.build();
//		umerpb.setUpdatedUserMiniEvent(updatedUmep);
//
//		//construct the request event
//		UpdateMiniEventRequestEvent umere = new UpdateMiniEventRequestEvent();
//		umere.setUpdateMiniEventRequestProto(umerpb.build());
//		umere.setTag(1);
//
//		//process the request event
//		updateMiniEventController.handleEvent(umere);
//
//		//check to make sure that the MiniEventForUser is updated
//		MiniEventForUser upToDateMefu = miniEventForUserRetrieveUtil
//				.getSpecificUserMiniEvent(userId);
//		assertEquals(updatedMefu.getPtsEarned(), upToDateMefu.getPtsEarned());
//
//	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

}
