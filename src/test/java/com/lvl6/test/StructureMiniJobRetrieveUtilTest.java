//package com.lvl6.test;
//
//import java.util.Map;
//
//import junit.framework.TestCase;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.lvl6.info.Structure;
//import com.lvl6.info.StructureMiniJob;
//import com.lvl6.retrieveutils.rarechange.StructureMiniJobRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
//public class StructureMiniJobRetrieveUtilTest extends TestCase {
//
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	@Test
//	public void testGetStructureMiniJobs() {
//		log.info("testing retrieving structure mini jobs");
//
//		Map<Integer, StructureMiniJob> structIdToStructureMiniJob = StructureMiniJobRetrieveUtils
//				.getStructIdsToMiniJobs();
//
//		for (Integer structId : structIdToStructureMiniJob.keySet()) {
//			Structure struct = StructureRetrieveUtils
//					.getStructForStructId(structId);
//			assertTrue("Expected: not null. Actual: " + struct, null != struct);
//		}
//
//		log.info("structIdToStructureMiniJob=" + structIdToStructureMiniJob);
//
//		assertTrue("Expected: some miniJobStructs. Actual: "
//				+ structIdToStructureMiniJob,
//				!structIdToStructureMiniJob.isEmpty());
//
//	}
//
//}
