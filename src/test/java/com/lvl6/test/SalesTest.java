
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONObject;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Matchers;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.powermock.modules.junit4.rule.PowerMockRule;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.TestingAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.lvl6.events.request.InAppPurchaseRequestEvent;
//import com.lvl6.info.ItemForUser;
//import com.lvl6.info.SalesItem;
//import com.lvl6.info.SalesPackage;
//import com.lvl6.info.User;
//import com.lvl6.misc.MiscMethods;
//import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;
//import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
//import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
//import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
//import com.lvl6.retrieveutils.UserRetrieveUtils2;
//import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
//import com.lvl6.server.controller.InAppPurchaseController;
//import com.lvl6.server.controller.utils.MonsterStuffUtils;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//import com.lvl6.utils.utilmethods.UpdateUtil;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@PrepareForTest({SalesPackageRetrieveUtils.class, SalesItemRetrieveUtils.class, MiscMethods.class,
//	MonsterStuffUtils.class})
//@ContextConfiguration("/test-spring-application-context.xml")
//public class SalesTest {
//
//	private static User mockedUser;
//	private static InsertUtil mockedInsertUtil;
//	private static UpdateUtil mockedUpdateUtil;
//	private static ItemForUserRetrieveUtil mockedItemForUserRetrieveUtil;
//	private static UserRetrieveUtils2 mockedUserRetrieveUtil;
//	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;
//
//	@Rule
//	public PowerMockRule rule = new PowerMockRule();
//
//	@Autowired
//	InAppPurchaseController inAppPurchaseController;
//	
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	@Before
//	public void setUp() {
//		log.info("/////////////////////////SALES TEST////////////////////////////////////");
//		
//		TestingAuthenticationToken testToken = new TestingAuthenticationToken("testUser", "");
//	    SecurityContextHolder.getContext().setAuthentication(testToken);
//		
//		mockedUser = mock(User.class);
//		when(mockedUser.getId()).thenReturn("user id");
//		when(mockedUser.getSalesValue()).thenReturn(1);
//		when(mockedUser.getName()).thenReturn("name");
//		Date now = new Date();
//		when(mockedUser.getLastPurchaseTime()).thenReturn(now);
//		when(mockedUser.isSalesJumpTwoTiers()).thenReturn(true);
//		
//		mockedInsertUtil = PowerMockito.mock(InsertUtil.class);
//		when(mockedInsertUtil.insertIAPHistoryElem(any(JSONObject.class), 
//				any(Integer.class), any(User.class), any(Double.class))).thenReturn(true);
//		List<String> returnList = new ArrayList<String>();
//		returnList.add("list");
//		when(mockedInsertUtil.insertIntoMonsterForUserReturnIds(any(String.class), any(List.class), 
//				any(String.class), any(Date.class))).thenReturn(returnList);
//		
//		mockedItemForUserRetrieveUtil = PowerMockito.mock(ItemForUserRetrieveUtil.class);
//		Map<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
//		itemMap.put(1, 5);
//		ItemForUser ifu = new ItemForUser();
//		ifu.setItemId(1);
//		ifu.setQuantity(2);
//		ifu.setUserId("user id");
//		Map<Integer, ItemForUser> ifuMap = new HashMap<Integer, ItemForUser>();
//		ifuMap.put(1, ifu);
//		when(mockedItemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(any(String.class), Matchers.eq(itemMap.keySet()))).thenReturn(ifuMap);
//		
//		mockedUpdateUtil = PowerMockito.mock(UpdateUtil.class);
//		when(mockedUpdateUtil.updateItemForUser(any(List.class))).thenReturn(1);
//		
//		mockedUserRetrieveUtil = PowerMockito.mock(UserRetrieveUtils2.class);
//		when(mockedUserRetrieveUtil.getUserById(any(String.class))).thenReturn(mockedUser);
//		
//		mockedIAPHistoryRetrieveUtil = PowerMockito.mock(IAPHistoryRetrieveUtils.class);
//		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(any(Long.class))).thenReturn(false);
//		
//		////////////////////////////////mock retrieveutils with static methods/////////////////////
//        PowerMockito.mockStatic(SalesPackageRetrieveUtils.class);
//        
//        SalesPackage sp = new SalesPackage();
//        sp.setId(1);
//        sp.setName("name");
//        sp.setPrice(4.99);
//        sp.setUuid("uuid");
//        Map<Integer, SalesPackage> salesPackageMap = new HashMap<Integer, SalesPackage>();
//        salesPackageMap.put(1, sp);
//        
//        when(SalesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages()).thenReturn(salesPackageMap);
//		
//        PowerMockito.mockStatic(SalesItemRetrieveUtils.class);
//        Map<Integer, SalesItem> salesItemMap = new HashMap<Integer, SalesItem>();
//        
//        SalesItem si1 = new SalesItem();
//        si1.setId(1);
//        si1.setSalesPackageId(1);
//        si1.setMonsterId(1);
//        si1.setMonsterLevel(5);
//        si1.setMonsterQuantity(1);
//        si1.setGemReward(0);
//        salesItemMap.put(1, si1);
//        
//        SalesItem si2 = new SalesItem();
//        si2.setId(2);
//        si2.setSalesPackageId(1);
//        si2.setItemId(1);
//        si2.setItemQuantity(5);
//        si2.setGemReward(0);
//        salesItemMap.put(2, si2);
//        
//        SalesItem si3 = new SalesItem();
//        si3.setId(3);
//        si3.setSalesPackageId(1);
//        si3.setGemReward(100);
//        salesItemMap.put(3, si3);
//        
//        when(SalesItemRetrieveUtils.getSalesItemIdsToSalesItemsForSalesPackageId(any(Integer.class)))
//        	.thenReturn(salesItemMap);
//        
//        PowerMockito.mockStatic(MonsterStuffUtils.class);
//        List<FullUserMonsterProto> fumpList = new ArrayList<FullUserMonsterProto>();
//        FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
//        fumpList.add(fumpb.build());
//
//        when(MonsterStuffUtils.updateUserMonsters(any(String.class), any(Map.class), any(Map.class), any(String.class), any(Date.class))).thenReturn(fumpList);
//               
//		////////////////////////////////miscmethods////////////////////////////////
//        PowerMockito.mockStatic(MiscMethods.class);
//        
//        when(MiscMethods.checkIfMonstersExistInSalesItem(any(List.class))).thenReturn(true);
//        when(MiscMethods.determineGemRewardForSale(any(List.class))).thenReturn(100);
//        when(MiscMethods.createUpdateUserMonsterArgumentsForSales(any(String.class), Matchers.eq(1), any(List.class),
//        		any(Map.class), any(List.class), any(Date.class))).thenReturn("string");
//        when(MiscMethods.createFullUserMonsterProtos(any(List.class), any(List.class))).thenReturn(fumpList);
//	}
//	
//	@Test
//	public void testSales() {
//		InAppPurchaseRequestProto.Builder iaprpb = InAppPurchaseRequestProto
//				.newBuilder();
//		iaprpb.setSender(CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(mockedUser, null));
//		
//		String receipt1 = "ewoJInNpZ25hdHVyZSIgPSAiQXJCRkpXdkttVytTMHlRNlU5cmh5azVnWE93ajgwZCtnNm"
//				+ "NDRjdxOFNnbjU4OEhZY2VVR3h1aE9QKzV3NERKVDAvdjBwQ3VTbFArV3JvOWV0YmRRZFZOcE1YdWVDc"
//				+ "GNvdEVpc2FRbzVuUE1rTFdkQUhHSVFtbFBVbnVzVEhZUmh2djZhTC9ITVc3ZXNDL2d4Ti92dkRCWWxu"
//				+ "bXYvNWdPb3lLbjJzekFXVG5keUFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BME"
//				+ "dDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pT"
//				+ "QkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVU"
//				+ "RXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1F"
//				+ "YVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWERURTJNRFV4T0RFNE16RXpNRm93WkRFak1"
//				+ "DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTU"
//				+ "VrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR"
//				+ "0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNbVRFdUxnamlt"
//				+ "THdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclBTdXg5RDU"
//				+ "1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STS"
//				+ "tRTFJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQUFHamNqQndNQjBHQ"
//				+ "TFVZERnUVdCQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhH"
//				+ "QTFVZEl3UVlNQmFBRkRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0"
//				+ "lIZ0RBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVT"
//				+ "UxcnhmY3FBQWU1QzIvZkVXOEtVbDRpTzRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllIS"
//				+ "ytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU"
//				+ "1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQ"
//				+ "cFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGRxRnd4VmdtNTJoM29lSk9PdC92WTRF"
//				+ "Y1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU0dZejErVTNzRHhKemViU3Bi"
//				+ "YUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9"
//				+ "ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREUxTFRBeUx"
//				+ "USXpJREUyT2pBek9qRXdJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluVnVhWEYxWlMxcFp"
//				+ "HVnVkR2xtYVdWeUlpQTlJQ0k0TlRGaFlUQmpNR0kwTWpjMVpESTFaakl4TVRBeU4yVTRNbUV3WVdSaU1"
//				+ "qWmpNR0U0TXpoa0lqc0tDU0p2Y21sbmFXNWhiQzEwY21GdWMyRmpkR2x2YmkxcFpDSWdQU0FpTVRBd01"
//				+ "EQXdNREUwTkRZd01UUTVNaUk3Q2draVluWnljeUlnUFNBaU1TNHhMakV3TGpFeU5TSTdDZ2tpZEhKaGJ"
//				+ "uTmhZM1JwYjI0dGFXUWlJRDBnSWpFd01EQXdNREF4TkRRMk1ERTBPVElpT3dvSkluRjFZVzUwYVhSNUl"
//				+ "pQTlJQ0l4SWpzS0NTSnZjbWxuYVc1aGJDMXdkWEpqYUdGelpTMWtZWFJsTFcxeklpQTlJQ0l4TkRJME5"
//				+ "6TTJNVGt3T1RJeUlqc0tDU0oxYm1seGRXVXRkbVZ1Wkc5eUxXbGtaVzUwYVdacFpYSWlJRDBnSWtJM1J"
//				+ "VWkdNamcwTFVJeE5Ea3ROREJGUmkxQ056STBMVEV5UXpRek1qVTJRMEkyTVNJN0Nna2ljSEp2WkhWamR"
//				+ "DMXBaQ0lnUFNBaVkyOXRMbk5qYjNCbGJIa3ViVzlpYzNGMVlXUXVjM1JoY25SbGNuQmhZMnNpT3dvSkl"
//				+ "tbDBaVzB0YVdRaUlEMGdJamsyTlRrNU16a3hNaUk3Q2draVltbGtJaUE5SUNKamIyMHViSFpzTmk1dGI"
//				+ "ySnpkR1Z5Y3lJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxdGN5SWdQU0FpTVRReU5EY3pOakU1TURreU1"
//				+ "pSTdDZ2tpY0hWeVkyaGhjMlV0WkdGMFpTSWdQU0FpTWpBeE5TMHdNaTB5TkNBd01Eb3dNem94TUNCRm"
//				+ "RHTXZSMDFVSWpzS0NTSndkWEpqYUdGelpTMWtZWFJsTFhCemRDSWdQU0FpTWpBeE5TMHdNaTB5TXlBeE"
//				+ "5qb3dNem94TUNCQmJXVnlhV05oTDB4dmMxOUJibWRsYkdWeklqc0tDU0p2Y21sbmFXNWhiQzF3ZFhKam"
//				+ "FHRnpaUzFrWVhSbElpQTlJQ0l5TURFMUxUQXlMVEkwSURBd09qQXpPakV3SUVWMFl5OUhUVlFpT3dwOS"
//				+ "I7CgkiZW52aXJvbm1lbnQiID0gIlNhbmRib3giOwoJInBvZCIgPSAiMTAwIjsKCSJzaWduaW5nLXN0YX"
//				+ "R1cyIgPSAiMCI7Cn0=";
//
//		iaprpb.setReceipt(receipt1);
//		iaprpb.setUuid("uuid");
//		
//		InAppPurchaseRequestEvent iapre = new InAppPurchaseRequestEvent();
//		iapre.setTag(1);
//		iapre.setInAppPurchaseRequestProto(iaprpb.build());
//		
//		InAppPurchaseController inAppPurchaseController = new InAppPurchaseController();
//		inAppPurchaseController.setInsertUtil(mockedInsertUtil);
//		inAppPurchaseController.setItemForUserRetrieveUtil(mockedItemForUserRetrieveUtil);
//		inAppPurchaseController.setUpdateUtil(mockedUpdateUtil);
//		inAppPurchaseController.setUserRetrieveUtil(mockedUserRetrieveUtil);
//		inAppPurchaseController.setIapHistoryRetrieveUtil(mockedIAPHistoryRetrieveUtil);
//		
//		inAppPurchaseController.handleEvent(iapre);
//		
//		SecurityContextHolder.clearContext();
//	}
//	
//	
//}
package com.lvl6.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.events.request.InAppPurchaseRequestEvent;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.SalesItem;
import com.lvl6.info.SalesPackage;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
import com.lvl6.server.controller.InAppPurchaseController;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@PrepareForTest({SalesPackageRetrieveUtils.class, SalesItemRetrieveUtils.class, MiscMethods.class,
	MonsterStuffUtils.class})
@ContextConfiguration("/test-spring-application-context.xml")
public class SalesTest {

	private static User mockedUser;
	private static InsertUtil mockedInsertUtil;
	private static UpdateUtil mockedUpdateUtil;
	private static ItemForUserRetrieveUtil mockedItemForUserRetrieveUtil;
	private static UserRetrieveUtils2 mockedUserRetrieveUtil;
	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Autowired
	InAppPurchaseController inAppPurchaseController;
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Before
	public void setUp() {
		log.info("/////////////////////////SALES TEST////////////////////////////////////");
		
		TestingAuthenticationToken testToken = new TestingAuthenticationToken("testUser", "");
	    SecurityContextHolder.getContext().setAuthentication(testToken);
		
		mockedUser = mock(User.class);
		when(mockedUser.getId()).thenReturn("user id");
		when(mockedUser.getSalesValue()).thenReturn(1);
		when(mockedUser.getName()).thenReturn("name");
		Date now = new Date();
		when(mockedUser.getLastPurchaseTime()).thenReturn(now);
		when(mockedUser.isSalesJumpTwoTiers()).thenReturn(true);
		
		mockedInsertUtil = PowerMockito.mock(InsertUtil.class);
		when(mockedInsertUtil.insertIAPHistoryElem(any(JSONObject.class), 
				any(Integer.class), any(User.class), any(Double.class))).thenReturn(true);
		List<String> returnList = new ArrayList<String>();
		returnList.add("list");
		when(mockedInsertUtil.insertIntoMonsterForUserReturnIds(any(String.class), any(List.class), 
				any(String.class), any(Date.class))).thenReturn(returnList);
		
		mockedItemForUserRetrieveUtil = PowerMockito.mock(ItemForUserRetrieveUtil.class);
		Map<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
		itemMap.put(1, 5);
		ItemForUser ifu = new ItemForUser();
		ifu.setItemId(1);
		ifu.setQuantity(2);
		ifu.setUserId("user id");
		Map<Integer, ItemForUser> ifuMap = new HashMap<Integer, ItemForUser>();
		ifuMap.put(1, ifu);
		when(mockedItemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(any(String.class), Matchers.eq(itemMap.keySet()))).thenReturn(ifuMap);
		
		mockedUpdateUtil = PowerMockito.mock(UpdateUtil.class);
		when(mockedUpdateUtil.updateItemForUser(any(List.class))).thenReturn(1);
		
		mockedUserRetrieveUtil = PowerMockito.mock(UserRetrieveUtils2.class);
		when(mockedUserRetrieveUtil.getUserById(any(String.class))).thenReturn(mockedUser);
		
		mockedIAPHistoryRetrieveUtil = PowerMockito.mock(IAPHistoryRetrieveUtils.class);
		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(any(Long.class))).thenReturn(false);
		
		////////////////////////////////mock retrieveutils with static methods/////////////////////
        PowerMockito.mockStatic(SalesPackageRetrieveUtils.class);
        
        SalesPackage sp = new SalesPackage();
        sp.setId(1);
        sp.setName("name");
        sp.setPrice(4.99);
        sp.setUuid("uuid");
        Map<Integer, SalesPackage> salesPackageMap = new HashMap<Integer, SalesPackage>();
        salesPackageMap.put(1, sp);
        
        when(SalesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages()).thenReturn(salesPackageMap);
		
        PowerMockito.mockStatic(SalesItemRetrieveUtils.class);
        Map<Integer, SalesItem> salesItemMap = new HashMap<Integer, SalesItem>();
        
        SalesItem si1 = new SalesItem();
        si1.setId(1);
        si1.setSalesPackageId(1);
        si1.setMonsterId(1);
        si1.setMonsterLevel(5);
        si1.setMonsterQuantity(1);
        si1.setGemReward(0);
        salesItemMap.put(1, si1);
        
        SalesItem si2 = new SalesItem();
        si2.setId(2);
        si2.setSalesPackageId(1);
        si2.setItemId(1);
        si2.setItemQuantity(5);
        si2.setGemReward(0);
        salesItemMap.put(2, si2);
        
        SalesItem si3 = new SalesItem();
        si3.setId(3);
        si3.setSalesPackageId(1);
        si3.setGemReward(100);
        salesItemMap.put(3, si3);
        
        when(SalesItemRetrieveUtils.getSalesItemIdsToSalesItemsForSalesPackageId(any(Integer.class)))
        	.thenReturn(salesItemMap);
        
        PowerMockito.mockStatic(MonsterStuffUtils.class);
        List<FullUserMonsterProto> fumpList = new ArrayList<FullUserMonsterProto>();
        FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
        fumpList.add(fumpb.build());

        when(MonsterStuffUtils.updateUserMonsters(any(String.class), any(Map.class), any(Map.class), any(String.class), any(Date.class))).thenReturn(fumpList);
               
		////////////////////////////////miscmethods////////////////////////////////
        PowerMockito.mockStatic(MiscMethods.class);
        
        when(MiscMethods.checkIfMonstersExistInSalesItem(any(List.class))).thenReturn(true);
        when(MiscMethods.determineGemRewardForSale(any(List.class))).thenReturn(100);
        when(MiscMethods.createUpdateUserMonsterArgumentsForSales(any(String.class), Matchers.eq(1), any(List.class),
        		any(Map.class), any(List.class), any(Date.class))).thenReturn("string");
        when(MiscMethods.createFullUserMonsterProtos(any(List.class), any(List.class))).thenReturn(fumpList);
	}
	
	@Test
	public void testSales() {
		InAppPurchaseRequestProto.Builder iaprpb = InAppPurchaseRequestProto
				.newBuilder();
		iaprpb.setSender(CreateInfoProtoUtils
				.createMinimumUserProtoFromUserAndClan(mockedUser, null));
		
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
		iaprpb.setUuid("uuid");
		
		InAppPurchaseRequestEvent iapre = new InAppPurchaseRequestEvent();
		iapre.setTag(1);
		iapre.setInAppPurchaseRequestProto(iaprpb.build());
		
		InAppPurchaseController inAppPurchaseController = new InAppPurchaseController();
		inAppPurchaseController.setInsertUtil(mockedInsertUtil);
		inAppPurchaseController.setItemForUserRetrieveUtil(mockedItemForUserRetrieveUtil);
		inAppPurchaseController.setUpdateUtil(mockedUpdateUtil);
		inAppPurchaseController.setUserRetrieveUtil(mockedUserRetrieveUtil);
		inAppPurchaseController.setIapHistoryRetrieveUtil(mockedIAPHistoryRetrieveUtil);
		
		inAppPurchaseController.handleEvent(iapre);
		
		SecurityContextHolder.clearContext();
	}
	
	
}
