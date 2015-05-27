package com.lvl6.test.controller.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.lvl6.events.request.InAppPurchaseRequestEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.InAppPurchaseController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class InAppPurchaseSalesTest {

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
	InAppPurchaseController inAppPurchaseController;

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

		deleteSalesPurchase(userId);

		String query2 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values2 = new Object[] { user.getId() };
		int[] types2 = new int[] { java.sql.Types.VARCHAR };

		int numDeleted = jdbcTemplate.update(query2, values2, types2);
		if (numDeleted != 1) {
			log.error("did not delete test user when cleaning up");
		}

	}

	private int deleteSalesPurchase(String userId) {
		String tableName = DBConstants.TABLE_IAP_HISTORY;
		String condDelim = "and";
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		int totalDeleted = 0;

		conditionParams.put(DBConstants.IAP_HISTORY__USER_ID,
				userId);
		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, condDelim);
		totalDeleted += numDeleted;

		return totalDeleted;
	}

	@Test
	public void testStarterPack() {
		User user1 = userRetrieveUtil.getUserById(userId);
		int userGems1 = user1.getGems();
		InAppPurchaseRequestProto.Builder iaprpb = InAppPurchaseRequestProto
				.newBuilder();

		iaprpb.setSender(mup);

		String receipt1 = "ewoJInNpZ25hdHVyZSIgPSAiQXJwditEcWhFUVNsVlo0K3pBd2I5bU1o"
				+ "WUdQT3F3dHdsTU9tK3kzY0Q0T3JZTlJPZ0V2WXNLdUIrYnFsWDAyMEFCWHpuQVFPMTRPbyttSj"
				+ "JxQk5GVFhETHlEaUtpRE9lSWdTb2JteExEMnA1K1JsR2JvTm1aNGFnNHBpT3hWTnQwN09GRUN0"
				+ "RXJaMDVVZGROZjFtSkdQd2t0ZWVxZmxFVHQvdTFZZzVHUnNNR0FBQURWekNDQTFNd2dnSTdvQ"
				+ "U1DQVFJQ0NCdXA0K1BBaG0vTE1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZV"
				+ "EFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU"
				+ "0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2"
				+ "FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEU"
				+ "wTURZd056QXdNREl5TVZvWERURTJNRFV4T0RFNE16RXpNRm93WkRFak1DRUdBMVVFQXd3YVVIV"
				+ "nlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsS"
				+ "UdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVU"
				+ "VCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNbVRFdUxnaml"
				+ "tTHdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclB"
				+ "TdXg5RDU1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGp"
				+ "FMU93QytvVG5STStRTFJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzd"
				+ "BZ01CQUFHamNqQndNQjBHQTFVZERnUVdCQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0R"
				+ "BTUJnTlZIUk1CQWY4RUFqQUFNQjhHQTFVZEl3UVlNQmFBRkRZZDZPS2RndElCR0xVeWF3N1h"
				+ "Rd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0lIZ0RBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQUR"
				+ "BTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVTUxcnhmY3FBQWU1QzIvZkVXOEtVbDR"
				+ "pTzRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllISytIalJLU1U5UkxndU5sMG5"
				+ "rZnhxT2JpTWNrd1J1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU1lsc0t0OG9OdGx"
				+ "oZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQcFNOMDR"
				+ "rYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGRxRnd4VmdtNTJoM29lSk9PdC92WT"
				+ "RFY1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU0dZejErVTNzR"
				+ "HhKemViU3BiYUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwd"
				+ "XJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjS"
				+ "E4wSWlBOUlDSXlNREUxTFRBMExURTNJREF5T2pJd09qSXdJRUZ0WlhKcFkyRXZURzl6WDBGd"
				+ "VoyVnNaWE1pT3dvSkluVnVhWEYxWlMxcFpHVnVkR2xtYVdWeUlpQTlJQ0k0TlRGaFlUQmpNR0"
				+ "kwTWpjMVpESTFaakl4TVRBeU4yVTRNbUV3WVdSaU1qWmpNR0U0TXpoa0lqc0tDU0p2Y21sbm"
				+ "FXNWhiQzEwY21GdWMyRmpkR2x2YmkxcFpDSWdQU0FpTVRBd01EQXdNREUxTWpFeE5qSXlNU0"
				+ "k3Q2draVluWnljeUlnUFNBaU1TNDBMak11TVRJMUlqc0tDU0owY21GdWMyRmpkR2x2YmkxcF"
				+ "pDSWdQU0FpTVRBd01EQXdNREUxTWpFeE5qSXlNU0k3Q2draWNYVmhiblJwZEhraUlEMGdJa"
				+ "kVpT3dvSkltOXlhV2RwYm1Gc0xYQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakUwTWpre"
				+ "U5qSTBNakExTURZaU93b0pJblZ1YVhGMVpTMTJaVzVrYjNJdGFXUmxiblJwWm1sbGNpSWdQU"
				+ "0FpUWpkRlJrWXlPRFF0UWpFME9TMDBNRVZHTFVJM01qUXRNVEpETkRNeU5UWkRRall4SWpzS"
				+ "0NTSndjbTlrZFdOMExXbGtJaUE5SUNKamIyMHVjMk52Y0dWc2VTNXRiMkp6Y1hWaFpDNXpk"
				+ "R0Z5ZEdWeWNHRmpheUk3Q2draWFYUmxiUzFwWkNJZ1BTQWlPVFkxT1Rrek9URXlJanNLQ1N"
				+ "KaWFXUWlJRDBnSW1OdmJTNXNkbXcyTG0xdlluTjBaWEp6SWpzS0NTSndkWEpqYUdGelpTMWt"
				+ "ZWFJsTFcxeklpQTlJQ0l4TkRJNU1qWXlOREl3TlRBMklqc0tDU0p3ZFhKamFHRnpaUzFrWVh"
				+ "SbElpQTlJQ0l5TURFMUxUQTBMVEUzSURBNU9qSXdPakl3SUVWMFl5OUhUVlFpT3dvSkluQjFj"
				+ "bU5vWVhObExXUmhkR1V0Y0hOMElpQTlJQ0l5TURFMUxUQTBMVEUzSURBeU9qSXdPakl3SUVG"
				+ "dFpYSnBZMkV2VEc5elgwRnVaMlZzWlhNaU93b0pJbTl5YVdkcGJtRnNMWEIxY21Ob1lYTmxM"
				+ "V1JoZEdVaUlEMGdJakl3TVRVdE1EUXRNVGNnTURrNk1qQTZNakFnUlhSakwwZE5WQ0k3Q24w"
				+ "PSI7CgkiZW52aXJvbm1lbnQiID0gIlNhbmRib3giOwoJInBvZCIgPSAiMTAwIjsKCSJzaWdu"
				+ "aW5nLXN0YXR1cyIgPSAiMCI7Cn0=";


		iaprpb.setReceipt(receipt1);
		iaprpb.setUuid("6552136A-1AF1-4A98-966C-FC030DE1F032");

		InAppPurchaseRequestEvent iapre = new InAppPurchaseRequestEvent();
		iapre.setTag(1);
		iapre.setInAppPurchaseRequestProto(iaprpb.build());
		inAppPurchaseController.handleEvent(iapre);

		User user2 = userRetrieveUtil.getUserById(user.getId());

		int userGems2 = user2.getGems();

		List<MonsterForUser> mfuList = monsterForUserRetrieveUtils.getMonstersForUser(user.getId());
		List<Integer> itemIds = new ArrayList<Integer>();
		itemIds.add(1);
		itemIds.add(2);
		List<ItemForUser> ifuList = itemForUserRetrieveUtil.getSpecificOrAllItemForUser(user.getId(), itemIds);

		assertTrue(mfuList.size() == 4);
		assertEquals(userGems1 + 100, userGems2);
		assertTrue(ifuList.size() == 2);
		assertTrue(user2.getSalesValue() == 1);
		assertNotNull(user2.getSegmentationGroup());
		assertNotNull(user2.getLastPurchaseTime());

	}

}
