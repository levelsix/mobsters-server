package com.lvl6.test.controller.utilstests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Comparator;
import java.util.Date;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.MiniEvent;
import com.lvl6.info.User;
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils;
import com.lvl6.server.controller.utils.ResourceUtil;
import com.lvl6.server.controller.utils.TimeUtils;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
public class ResourceUtilTest {

	private static Logger log = LoggerFactory.getLogger(ResourceUtilTest.class);


//	@Autowired
	protected static ResourceUtil resourceUtil;

	@BeforeClass
	public static void setUp() {
		resourceUtil = new ResourceUtil();

	}

	@Test
	public void testABNowBA() {
		//scenario 1: aStarts  bStarts now  bEnds  aEnds

//		User mockedUser = mock(User.class);
//		when(mockedUser.getCash()).thenReturn(started);
//		when(mockedTreeSet.tailSet(any(MiniEvent.class), any(Boolean.class))).thenReturn(notYetEnded);
//
//		miniEventRetrieveUtil.setMeStartTimeTree(mockedTreeSet);
//		miniEventRetrieveUtil.setMeEndTimeTree(mockedTreeSet);
//
//		Assert.assertEquals(firstMe, miniEventRetrieveUtil
//				.getCurrentlyActiveMiniEvent(now));
//
//		Assert.assertEquals(firstMe, miniEventRetrieveUtil
//				.getCurrentlyActiveMiniEvent(new Date()));


	}

}
