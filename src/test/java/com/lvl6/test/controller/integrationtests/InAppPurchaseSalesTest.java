package com.lvl6.test.controller.integrationtests;

import static org.junit.Assert.assertEquals;
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
				avatarMonsterId, email, fbData);

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
	public void testBattleItems() {
		User user1 = userRetrieveUtil.getUserById(userId);
		int userGems1 = user1.getGems();
		InAppPurchaseRequestProto.Builder iaprpb = InAppPurchaseRequestProto
				.newBuilder();

		iaprpb.setSender(mup);

		String receipt1 = "ewoJInNpZ25hdHVyZSIgPSAiQXJCRkpXdkttVytTMHlRNlU5cmh5azVnWE93ajgwZCtnNm"
				+ "NDRjdxOFNnbjU4OEhZY2VVR3h1aE9QKzV3NERKVDAvdjBwQ3VTbFArV3JvOWV0YmRRZFZOcE1YdWVDc"
				+ "GNvdEVpc2FRbzVuUE1rTFdkQUhHSVFtbFBVbnVzVEhZUmh2djZhTC9ITVc3ZXNDL2d4Ti92dkRCWWxu"
				+ "bXYvNWdPb3lLbjJzekFXVG5keUFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BME"
				+ "dDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pT"
				+ "QkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVU"
				+ "RXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1F"
				+ "YVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWERURTJNRFV4T0RFNE16RXpNRm93WkRFak1"
				+ "DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTU"
				+ "VrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR"
				+ "0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNbVRFdUxnamlt"
				+ "THdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclBTdXg5RDU"
				+ "1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STS"
				+ "tRTFJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQUFHamNqQndNQjBHQ"
				+ "TFVZERnUVdCQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhH"
				+ "QTFVZEl3UVlNQmFBRkRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0"
				+ "lIZ0RBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVT"
				+ "UxcnhmY3FBQWU1QzIvZkVXOEtVbDRpTzRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllIS"
				+ "ytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU"
				+ "1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQ"
				+ "cFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGRxRnd4VmdtNTJoM29lSk9PdC92WTRF"
				+ "Y1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU0dZejErVTNzRHhKemViU3Bi"
				+ "YUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9"
				+ "ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREUxTFRBeUx"
				+ "USXpJREUyT2pBek9qRXdJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluVnVhWEYxWlMxcFp"
				+ "HVnVkR2xtYVdWeUlpQTlJQ0k0TlRGaFlUQmpNR0kwTWpjMVpESTFaakl4TVRBeU4yVTRNbUV3WVdSaU1"
				+ "qWmpNR0U0TXpoa0lqc0tDU0p2Y21sbmFXNWhiQzEwY21GdWMyRmpkR2x2YmkxcFpDSWdQU0FpTVRBd01"
				+ "EQXdNREUwTkRZd01UUTVNaUk3Q2draVluWnljeUlnUFNBaU1TNHhMakV3TGpFeU5TSTdDZ2tpZEhKaGJ"
				+ "uTmhZM1JwYjI0dGFXUWlJRDBnSWpFd01EQXdNREF4TkRRMk1ERTBPVElpT3dvSkluRjFZVzUwYVhSNUl"
				+ "pQTlJQ0l4SWpzS0NTSnZjbWxuYVc1aGJDMXdkWEpqYUdGelpTMWtZWFJsTFcxeklpQTlJQ0l4TkRJME5"
				+ "6TTJNVGt3T1RJeUlqc0tDU0oxYm1seGRXVXRkbVZ1Wkc5eUxXbGtaVzUwYVdacFpYSWlJRDBnSWtJM1J"
				+ "VWkdNamcwTFVJeE5Ea3ROREJGUmkxQ056STBMVEV5UXpRek1qVTJRMEkyTVNJN0Nna2ljSEp2WkhWamR"
				+ "DMXBaQ0lnUFNBaVkyOXRMbk5qYjNCbGJIa3ViVzlpYzNGMVlXUXVjM1JoY25SbGNuQmhZMnNpT3dvSkl"
				+ "tbDBaVzB0YVdRaUlEMGdJamsyTlRrNU16a3hNaUk3Q2draVltbGtJaUE5SUNKamIyMHViSFpzTmk1dGI"
				+ "ySnpkR1Z5Y3lJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxdGN5SWdQU0FpTVRReU5EY3pOakU1TURreU1"
				+ "pSTdDZ2tpY0hWeVkyaGhjMlV0WkdGMFpTSWdQU0FpTWpBeE5TMHdNaTB5TkNBd01Eb3dNem94TUNCRm"
				+ "RHTXZSMDFVSWpzS0NTSndkWEpqYUdGelpTMWtZWFJsTFhCemRDSWdQU0FpTWpBeE5TMHdNaTB5TXlBeE"
				+ "5qb3dNem94TUNCQmJXVnlhV05oTDB4dmMxOUJibWRsYkdWeklqc0tDU0p2Y21sbmFXNWhiQzF3ZFhKam"
				+ "FHRnpaUzFrWVhSbElpQTlJQ0l5TURFMUxUQXlMVEkwSURBd09qQXpPakV3SUVWMFl5OUhUVlFpT3dwOS"
				+ "I7CgkiZW52aXJvbm1lbnQiID0gIlNhbmRib3giOwoJInBvZCIgPSAiMTAwIjsKCSJzaWduaW5nLXN0YX"
				+ "R1cyIgPSAiMCI7Cn0=";

		iaprpb.setReceipt(receipt1);
		iaprpb.setUuid("12581D5C-D488-4E2B-B8B4-642D012855C6");

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
		assertTrue(user.getSalesValue() == 1);
		assertTrue(user.isSalesJumpTwoTiers());
	}

}
