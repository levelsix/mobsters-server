package com.lvl6.test.mocktests;
//package com.lvl6.test.MockTests;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.lvl6.events.request.PerformResearchRequestEvent;
//import com.lvl6.info.Research;
//import com.lvl6.info.ResearchForUser;
//import com.lvl6.info.User;
//import com.lvl6.proto.EventResearchProto.PerformResearchRequestProto;
//import com.lvl6.proto.StructureProto.ResourceType;
//import com.lvl6.server.controller.PerformResearchController;
//import com.lvl6.server.controller.actionobjects.PerformResearchAction;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//import com.lvl6.utils.utilmethods.UpdateUtil;
//
//
//public class PerformResearchTest {
//
//	private static PerformResearchController performResearchController;
//	private static User user;
//	private static Research mockResearch;
//	private static ResearchForUser mockResearchForUser;
//
//	private static InsertUtil mockInsertUtil;
//	private static UpdateUtil mockUpdateUtil;
//
//	@Before
//	public void setUp() throws Exception {
//		Date now = new Date();
//		Timestamp nowTimestamp = new Timestamp(now.getTime());
//		ResourceType resourceType = ResourceType.CASH;
//
//		user = new User("user id", "tester", 10, 100, 100, 100, 100, 10, "referralCode", 0, 
//				"udidForHistory", now, now, "deviceToken", 5, true, now, false, "apsalarId", 100, 
//				100, 5, "clanId", now, false, 0, "facebookId", false, "gameCenterId", "udid", now, 
//				0, now, 0, now, 0, now, "pvpDefendingMessage", now, false);
//
//		performResearchController = new PerformResearchController();
//	}
//
//	@Test
//	public void testGemsRemoved() {
//		PerformResearchRequestProto.Builder prrpb = PerformResearchRequestProto.newBuilder();
//
//		prrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
//		prrpb.setResearchId(10000);
//		Date date = new Date();
//		prrpb.setClientTime(date.getTime());
//		prrpb.setGemsSpent(25);
//
//
//		PerformResearchRequestEvent prre = new PerformResearchRequestEvent();
//		prre.setTag(1);
//		prre.setPerformResearchRequestProto(prrpb.build());
//		performResearchController.handleEvent(prre);
//
//		List<Integer> researchIdList = new ArrayList<Integer>();
//		researchIdList.add(10000);
//		List<ResearchForUser> rfuList = researchForUserRetrieveUtil.getAllResearchForUser(user.getId());
//		//assertNotNull(rfuList);
//		String userResearchUuid = null;
//		for(ResearchForUser rfu : rfuList) {
//			assertEquals(rfu.getUserId(), user.getId());
//			assertEquals(rfu.getResearchId(), 10000);
//			userResearchUuid = rfu.getId();
//		}
//		assertTrue(!rfuList.isEmpty());
//		User user2 = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
//
//		assertEquals(userGems-25, user2.getGems());
//
//	}
//
//
//
//
//
//
//
//
//
//}
