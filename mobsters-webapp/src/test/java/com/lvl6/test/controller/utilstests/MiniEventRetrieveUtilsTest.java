package com.lvl6.test.controller.utilstests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfig;
import com.lvl6.retrieveutils.rarechange.MiniEventTimetableRetrieveUtils;
import com.lvl6.utils.TimeUtils;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
public class MiniEventRetrieveUtilsTest {

	private static Logger log = LoggerFactory.getLogger(MiniEventRetrieveUtilsTest.class);

//	@Autowired
	protected static TimeUtils timeUtil;

//	@Autowired
	protected static MiniEventTimetableRetrieveUtils miniEventTimetableRetrieveUtil;

	private static NavigableSet<MiniEventTimetableConfig> started;
	private static NavigableSet<MiniEventTimetableConfig> notYetEnded;

	@BeforeClass
	public static void setUp() {
		timeUtil = new TimeUtils();
		miniEventTimetableRetrieveUtil = new MiniEventTimetableRetrieveUtils();

		started = new TreeSet<MiniEventTimetableConfig>(new Comparator<MiniEventTimetableConfig>() {
			@Override
			public int compare(MiniEventTimetableConfig o1, MiniEventTimetableConfig o2) {
				long o1Time = o1.getStartTime().getTime();
				long o2Time = o2.getStartTime().getTime();
				if ( o1Time < o2Time ) {
					return -1;
				} else if ( o1Time > o2Time ) {
					return 1;
				} else if (o1.getId() < o2.getId()) {
					return -1;
				} else if (o1.getId() > o2.getId()) {
					return 1;
				} else {
					return 0;
				}

			}
		});

		notYetEnded = new TreeSet<MiniEventTimetableConfig>(new Comparator<MiniEventTimetableConfig>() {
			@Override
			public int compare(MiniEventTimetableConfig o1, MiniEventTimetableConfig o2) {
				long o1Time = o1.getEndTime().getTime();
				long o2Time = o2.getEndTime().getTime();
				if ( o1Time < o2Time ) {
					return -1;
				} else if ( o1Time > o2Time ) {
					return 1;
				} else if (o1.getId() < o2.getId()) {
					return -1;
				} else if (o1.getId() > o2.getId()) {
					return 1;
				} else {
					return 0;
				}

			}
		});
	}

	@After
	public void tearDown() {
		started.clear();
		notYetEnded.clear();
	}

	@Test
	public void testABNowBA() {
		//scenario 1: aStarts  bStarts now  bEnds  aEnds
		Date now = new Date();
		Date me1St = timeUtil.createDateAddMinutes(now, -10);
		Date me2St = timeUtil.createDateAddMinutes(now, -5);
		Date me2Et = timeUtil.createDateAddMinutes(now, 5);
		Date me1Et = timeUtil.createDateAddMinutes(now, 10);

		MiniEventTimetableConfig firstMe = new MiniEventTimetableConfig();
		firstMe.setId(1);
		firstMe.setMiniEventId(1);
		firstMe.setStartTime(new Timestamp(me1St.getTime()));
		firstMe.setEndTime(new Timestamp(me1Et.getTime()));
		started.add(firstMe);
		notYetEnded.add(firstMe);

		MiniEventTimetableConfig secondMe = new MiniEventTimetableConfig();
		secondMe.setId(2);
		secondMe.setStartTime(new Timestamp(me2St.getTime()));
		secondMe.setEndTime(new Timestamp(me2Et.getTime()));
		started.add(secondMe);
		notYetEnded.add(secondMe);

		TreeSet<MiniEventTimetableConfig> mockedTreeSet = mock(TreeSet.class);
		when(mockedTreeSet.headSet(any(MiniEventTimetableConfig.class), any(Boolean.class))).thenReturn(started);
		when(mockedTreeSet.tailSet(any(MiniEventTimetableConfig.class), any(Boolean.class))).thenReturn(notYetEnded);

		miniEventTimetableRetrieveUtil.setMeStartTimeTree(mockedTreeSet);
		miniEventTimetableRetrieveUtil.setMeEndTimeTree(mockedTreeSet);

		Assert.assertEquals(firstMe, miniEventTimetableRetrieveUtil
				.getCurrentlyActiveMiniEvent(now));

		Assert.assertEquals(firstMe, miniEventTimetableRetrieveUtil
				.getCurrentlyActiveMiniEvent(new Date()));


	}
	@Test
	public void testABNowAB() {
		//scenario 1: aStarts  bStarts now  bEnds  aEnds
		Date now = new Date();
		Date me1St = timeUtil.createDateAddMinutes(now, -10);
		Date me2St = timeUtil.createDateAddMinutes(now, -5);
		Date me1Et = timeUtil.createDateAddMinutes(now, 5);
		Date me2Et = timeUtil.createDateAddMinutes(now, 10);

		MiniEventTimetableConfig firstMe = new MiniEventTimetableConfig();
		firstMe.setId(1);
		firstMe.setStartTime(new Timestamp(me1St.getTime()));
		firstMe.setEndTime(new Timestamp(me1Et.getTime()));
		started.add(firstMe);
		notYetEnded.add(firstMe);

		MiniEventTimetableConfig secondMe = new MiniEventTimetableConfig();
		secondMe.setId(2);
		secondMe.setStartTime(new Timestamp(me2St.getTime()));
		secondMe.setEndTime(new Timestamp(me2Et.getTime()));
		started.add(secondMe);
		notYetEnded.add(secondMe);

		TreeSet<MiniEventTimetableConfig> mockedTreeSet = mock(TreeSet.class);
		when(mockedTreeSet.headSet(any(MiniEventTimetableConfig.class), any(Boolean.class))).thenReturn(started);
		when(mockedTreeSet.tailSet(any(MiniEventTimetableConfig.class), any(Boolean.class))).thenReturn(notYetEnded);

		miniEventTimetableRetrieveUtil.setMeStartTimeTree(mockedTreeSet);
		miniEventTimetableRetrieveUtil.setMeEndTimeTree(mockedTreeSet);

		Assert.assertEquals(firstMe, miniEventTimetableRetrieveUtil
				.getCurrentlyActiveMiniEvent(now));

		Assert.assertEquals(firstMe, miniEventTimetableRetrieveUtil
				.getCurrentlyActiveMiniEvent(new Date()));
	}

	@Test
	public void testABBNowA() {
		//scenario 1: aStarts  bStarts now  bEnds  aEnds
		Date now = new Date();
		Date me1St = timeUtil.createDateAddMinutes(now, -10);
		Date me2St = timeUtil.createDateAddMinutes(now, -5);
		Date me1Et = timeUtil.createDateAddMinutes(now, 5);
		Date me2Et = timeUtil.createDateAddMinutes(now, 10);

		MiniEventTimetableConfig firstMe = new MiniEventTimetableConfig();
		firstMe.setId(1);
		firstMe.setStartTime(new Timestamp(me1St.getTime()));
		firstMe.setEndTime(new Timestamp(me1Et.getTime()));
		started.add(firstMe);
		notYetEnded.add(firstMe);

		MiniEventTimetableConfig secondMe = new MiniEventTimetableConfig();
		secondMe.setId(2);
		secondMe.setStartTime(new Timestamp(me2St.getTime()));
		secondMe.setEndTime(new Timestamp(me2Et.getTime()));
		started.add(secondMe);
		notYetEnded.add(secondMe);

		TreeSet<MiniEventTimetableConfig> mockedTreeSet = mock(TreeSet.class);
		when(mockedTreeSet.headSet(any(MiniEventTimetableConfig.class), any(Boolean.class))).thenReturn(started);
		when(mockedTreeSet.tailSet(any(MiniEventTimetableConfig.class), any(Boolean.class))).thenReturn(notYetEnded);

		miniEventTimetableRetrieveUtil.setMeStartTimeTree(mockedTreeSet);
		miniEventTimetableRetrieveUtil.setMeEndTimeTree(mockedTreeSet);

		Assert.assertEquals(firstMe, miniEventTimetableRetrieveUtil
				.getCurrentlyActiveMiniEvent(now));

		Assert.assertEquals(firstMe, miniEventTimetableRetrieveUtil
				.getCurrentlyActiveMiniEvent(new Date()));
	}
}
