package com.lvl6.test.ControllerTests;

import static org.junit.Assert.*;

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
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
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
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.InAppPurchaseController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class SalesTest {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private JdbcTemplate jdbcTemplate;
	private boolean endOfTesting;
	private User user;

	private MinimumUserProto mup;
	private String userId;
	private String userResearchUuid;

	@Autowired
	InsertUtil insertUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	InAppPurchaseController inAppPurchaseController;

	@Autowired
	MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;
	
	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;



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

		//		if (null == userId) {
		//			throw new RuntimeException("no user was created!");
		//		}

		user = userRetrieveUtil.getUserById(userId);

	}

	@After
	public void tearDown() {
		if (null == user) {
			log.info("no user to delete");
			return;
		}

//		deleteSalesPurchases(userId);

		String query2 = String.format("DELETE FROM %s where %s=?",
				DBConstants.TABLE_USER, DBConstants.USER__ID);
		Object[] values2 = new Object[] { user.getId() };
		int[] types2 = new int[] { java.sql.Types.VARCHAR };

		int numDeleted2 = jdbcTemplate.update(query2, values2, types2);
		if (numDeleted2 != 1) {
			log.error("did not delete test user when cleaning up");
		}

	}

	//buy money tree twice, then destroy it
	@Test
	public void testBuyingSalesPackage() {
		InAppPurchaseRequestProto.Builder iaprpb = InAppPurchaseRequestProto
				.newBuilder();
		iaprpb.setSender(CreateInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(user, null));
		
		int userGems = user.getGems();
		
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
		iaprpb.setUuid("test");

		InAppPurchaseRequestEvent iapre = new InAppPurchaseRequestEvent();
		iapre.setTag(1);
		iapre.setInAppPurchaseRequestProto(iaprpb.build());
		inAppPurchaseController.handleEvent(iapre);

		List<MonsterForUser> userMonsterList = monsterForUserRetrieveUtil.getMonstersForUser(userId);
		
		assertTrue(userMonsterList.size() == 4);
		
		for(MonsterForUser mfu : userMonsterList) {
			if(mfu.getMonsterId() == 2) {
				assertTrue(mfu.getNumPieces() == 1);
				assertFalse(mfu.isComplete());
			}
			else if(mfu.getMonsterId() == 1) {
				assertTrue(mfu.getCurrentLvl() == 5);
				assertTrue(mfu.isComplete());
			}
		}
		
		List<ItemForUser> userItemList = itemForUserRetrieveUtil.getSpecificOrAllItemForUser(userId, null);
		assertTrue(userItemList.size() == 2);
		
		for(ItemForUser ifu : userItemList) {
			if(ifu.getItemId() == 1) {
				assertTrue(ifu.getQuantity() == 5);
			}
			else if(ifu.getItemId() == 2) {
				assertTrue(ifu.getQuantity() == 10);
			}
		}
		
		User user2 = userRetrieveUtil.getUserById(userId);
		assertEquals(userGems + 100, user2.getGems());
	}
		

//		String receipt2 = "ewoJInNpZ25hdHVyZSIgPSAiQW5JVHhmUkJFaEc5dldtQmZzTU1QTHpPc0xKSFgvZGpGMURBTFhSWTB"
//				+ "MMTZ5QWZpb3NLM2VCbGQ1dmhsTjBxNHNjdGNmdEdmdUllSTFSaVpaRjVTSUp2cGUrN3g2N3dNUFNKWjhIczlwSU"
//				+ "5hTGVUdjZCVVFrdDMrK2lhYVVLWVU0QVd1QW1mMjB6cWY3dElJVnFaK0h4SHFZdDdPRGlDd0JqMCtRbE93Vj"
//				+ "lFYkFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN"
//				+ "6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNa"
//				+ "U0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6S"
//				+ "UZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWER"
//				+ "URTJNRFV4T0RFNE16RXpNRm93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV"
//				+ "05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3Ykd"
//				+ "VZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNb"
//				+ "VRFdUxnamltTHdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclBTdXg"
//				+ "5RDU1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STStRT"
//				+ "FJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQUFHamNqQndNQjBHQTFVZERnUVd"
//				+ "CQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhHQTFVZEl3UVlNQmFBR"
//				+ "kRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0lIZ0RBUUJnb3Foa2lHOTJOa0J"
//				+ "nVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVTUxcnhmY3FBQWU1QzIvZkVXOEtVbDRpT"
//				+ "zRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllISytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J"
//				+ "1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS"
//				+ "01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQcFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGR"
//				+ "xRnd4VmdtNTJoM29lSk9PdC92WTRFY1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU"
//				+ "0dZejErVTNzRHhKemViU3BiYUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJ"
//				+ "jaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNR"
//				+ "EUxTFRBeUxUSTBJREUwT2pBeU9qRTJJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluVnVhWEYxWlMx"
//				+ "cFpHVnVkR2xtYVdWeUlpQTlJQ0k0TlRGaFlUQmpNR0kwTWpjMVpESTFaakl4TVRBeU4yVTRNbUV3WVdSaU1qWmp"
//				+ "NR0U0TXpoa0lqc0tDU0p2Y21sbmFXNWhiQzEwY21GdWMyRmpkR2x2YmkxcFpDSWdQU0FpTVRBd01EQXdNREUwT"
//				+ "kRjM01URTBNaUk3Q2draVluWnljeUlnUFNBaU1TNHhMakV3TGpFeU5TSTdDZ2tpZEhKaGJuTmhZM1JwYjI0dGF"
//				+ "XUWlJRDBnSWpFd01EQXdNREF4TkRRM056RXhORElpT3dvSkluRjFZVzUwYVhSNUlpQTlJQ0l4SWpzS0NTSnZjb"
//				+ "WxuYVc1aGJDMXdkWEpqYUdGelpTMWtZWFJsTFcxeklpQTlJQ0l4TkRJME9ERTFNek0yTkRNeElqc0tDU0oxYm1s"
//				+ "eGRXVXRkbVZ1Wkc5eUxXbGtaVzUwYVdacFpYSWlJRDBnSWtJM1JVWkdNamcwTFVJeE5Ea3ROREJGUmkxQ056ST"
//				+ "BMVEV5UXpRek1qVTJRMEkyTVNJN0Nna2ljSEp2WkhWamRDMXBaQ0lnUFNBaVkyOXRMbk5qYjNCbGJIa3ViVzlp"
//				+ "YzNGMVlXUXVjM1JoY25SbGNuQmhZMnNpT3dvSkltbDBaVzB0YVdRaUlEMGdJamsyTlRrNU16a3hNaUk3Q2draV"
//				+ "ltbGtJaUE5SUNKamIyMHViSFpzTmk1dGIySnpkR1Z5Y3lJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxdGN5SWdQ"
//				+ "U0FpTVRReU5EZ3hOVE16TmpRek1TSTdDZ2tpY0hWeVkyaGhjMlV0WkdGMFpTSWdQU0FpTWpBeE5TMHdNaTB5Tk"
//				+ "NBeU1qb3dNam94TmlCRmRHTXZSMDFVSWpzS0NTSndkWEpqYUdGelpTMWtZWFJsTFhCemRDSWdQU0FpTWpBeE5T"
//				+ "MHdNaTB5TkNBeE5Eb3dNam94TmlCQmJXVnlhV05oTDB4dmMxOUJibWRsYkdWeklqc0tDU0p2Y21sbmFXNWhiQz"
//				+ "F3ZFhKamFHRnpaUzFrWVhSbElpQTlJQ0l5TURFMUxUQXlMVEkwSURJeU9qQXlPakUySUVWMFl5OUhUVlFpT3dw"
//				+ "OSI7CgkiZW52aXJvbm1lbnQiID0gIlNhbmRib3giOwoJInBvZCIgPSAiMTAwIjsKCSJzaWduaW5nLXN0YXR1cy"
//				+ "IgPSAiMCI7Cn0";
//
//		User user2 = userRetrieveUtil.getUserById(user.getId());
//		InAppPurchaseRequestProto.Builder iaprpb2 = InAppPurchaseRequestProto
//				.newBuilder();
//		iaprpb2.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user2, null));
//
//		iaprpb2.setReceipt(receipt2);
//
//		InAppPurchaseRequestEvent iapre2 = new InAppPurchaseRequestEvent();
//		iapre2.setTag(1);
//		iapre2.setInAppPurchaseRequestProto(iaprpb2.build());
//		inAppPurchaseController.handleEvent(iapre2);
//		String userStructId = "";
//
//		List<StructureForUser> sfuList2 = structureForUserRetrieveUtils2
//				.getUserStructsForUser(user2.getId());
//		int salesCounter = 0;
//		for (StructureForUser sfu : sfuList2) {
//			if (sfu.getStructId() >= 10000) {
//				salesCounter++;
//				userStructId = sfu.getId();
//			}
//		}
//		assertTrue(salesCounter == 1);
//
//		//destroy money tree
//		User user3 = userRetrieveUtil.getUserById(user.getId());
//		DestroySalesStructureRequestProto.Builder dmtsrpb = DestroySalesStructureRequestProto
//				.newBuilder();
//		dmtsrpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user3, null));
//
//		dmtsrpb.addUserStructUuid(userStructId);
//
//		DestroySalesStructureRequestEvent dmtsre = new DestroySalesStructureRequestEvent();
//		dmtsre.setTag(1);
//		dmtsre.setDestroySalesStructureRequestProto(dmtsrpb.build());
//		destroySalesStructureController.handleEvent(dmtsre);
//
//		User user4 = userRetrieveUtil.getUserById(user.getId());
//		List<StructureForUser> sfuList3 = structureForUserRetrieveUtils2
//				.getUserStructsForUser(user4.getId());
//		int salesCounter2 = 0;
//		for (StructureForUser sfu : sfuList3) {
//			if (sfu.getStructId() >= 10000) {
//				salesCounter2++;
//
//			}
//		}
//		assertTrue(salesCounter2 == 0);
//
//	}
//
//	private int deleteSalesPurchases(String userId) {
//		String tableName = DBConstants.TABLE_IAP_HISTORY;
//		String condDelim = "and";
//		Map<String, Object> conditionParams = new HashMap<String, Object>();
//		int totalDeleted = 0;
//
//		conditionParams.put(DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__USER_ID,
//				userId);
//		int numDeleted = DBConnection.get().deleteRows(tableName,
//				conditionParams, condDelim);
//		totalDeleted += numDeleted;
//
//		return totalDeleted;
//	}

}
