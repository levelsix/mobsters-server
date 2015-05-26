package com.lvl6.test.controller.unittests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.info.User;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.server.controller.actionobjects.InAppPurchaseMoneyTreeAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class InAppPurchaseMoneyTreeTest {

	private static User mockedUser;
	private static UpdateUtil mockedUpdateUtil;
	private static JSONObject mockedReceiptFromApple1;
	private static JSONObject mockedReceiptFromApple2;
	private static IAPHistoryRetrieveUtils mockedIAPHistoryRetrieveUtil;
	private static MonsterRetrieveUtils mockedMonsterRetrieveUtils;
	private static MonsterStuffUtils mockedMonsterStuffUtils;
	private static InAppPurchaseMoneyTreeAction iapmta;

	@BeforeClass
	public static void setUp() {
		mockedUser = mock(User.class);
		when(mockedUser.getId()).thenReturn("userId");

		mockedReceiptFromApple1 = mock(JSONObject.class);
		mockedReceiptFromApple2 = mock(JSONObject.class);
		try {
			when(mockedReceiptFromApple1.getString(any(String.class))).thenReturn("123");
			when(mockedReceiptFromApple2.getString(any(String.class))).thenReturn("456");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mockedIAPHistoryRetrieveUtil = mock(IAPHistoryRetrieveUtils.class);
		long uniqueLong = 123;
		long duplicateLong = 456;
		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(uniqueLong)).thenReturn(false);
		when(mockedIAPHistoryRetrieveUtil.checkIfDuplicateTransaction(duplicateLong)).thenReturn(true);

		mockedMonsterRetrieveUtils = mock(MonsterRetrieveUtils.class);

		mockedMonsterStuffUtils = mock(MonsterStuffUtils.class);

		mockedUpdateUtil = mock(UpdateUtil.class);
		when(mockedUpdateUtil.updateUserSalesValue(any(String.class), any(Integer.class),
				any(Date.class))).thenReturn(true);

		iapmta = new InAppPurchaseMoneyTreeAction();
		iapmta.setUser(mockedUser);
		iapmta.setUpdateUtil(mockedUpdateUtil);
	}

	@Test
	public void testUserOwnsOneMoneyTreeMax() {
		StructureMoneyTreeRetrieveUtils mockStructureMoneyTreeRetrieveUtils =
				mock(StructureMoneyTreeRetrieveUtils.class);
		Map<Integer, StructureMoneyTree> structIdToMoneyTree = new HashMap<Integer, StructureMoneyTree>();
		structIdToMoneyTree.put(10000, new StructureMoneyTree());

		when(mockStructureMoneyTreeRetrieveUtils.getStructIdsToMoneyTrees()).thenReturn(structIdToMoneyTree);

		StructureForUserRetrieveUtils2 mockedStructureForUserRetrieveUtils =
				mock(StructureForUserRetrieveUtils2.class);

		iapmta.setStructureMoneyTreeRetrieveUtils(mockStructureMoneyTreeRetrieveUtils);
		iapmta.setStructureForUserRetrieveUtils(mockedStructureForUserRetrieveUtils);
		iapmta.setUserId(mockedUser.getId());

		List<StructureForUser> sfuList = new ArrayList<StructureForUser>();

		when(mockedStructureForUserRetrieveUtils.getUserStructsForUser(mockedUser.getId())).thenReturn(sfuList);

		StructureForUser sfu = new StructureForUser();
		sfu.setStructId(1000);
		sfuList.add(sfu);

		assertTrue(iapmta.userOwnsOneMoneyTreeMax());


		StructureForUser sfu2 = new StructureForUser();
		sfu2.setStructId(10000);
		sfuList.add(sfu2);

		assertTrue(iapmta.userOwnsOneMoneyTreeMax());

		StructureForUser sfu3 = new StructureForUser();
		sfu3.setStructId(10000);
		sfuList.add(sfu3);

		assertFalse(iapmta.userOwnsOneMoneyTreeMax());
	}


}
