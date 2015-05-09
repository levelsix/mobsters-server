package com.lvl6.test.controller.utilstests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.MiniEvent;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils;
import com.lvl6.server.controller.utils.TimeUtils;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
public class MiniEventRetrieveUtilsTest {

	private static Logger log = LoggerFactory.getLogger(MiniEventRetrieveUtilsTest.class);

//	@Autowired
	protected static TimeUtils timeUtil;

//	@Autowired
	protected static MiniEventRetrieveUtils miniEventRetrieveUtil;

	private static NavigableSet<MiniEvent> started;
	private static NavigableSet<MiniEvent> notYetEnded;

	@BeforeClass
	public static void setUp() {
		timeUtil = new TimeUtils();
		miniEventRetrieveUtil = new MiniEventRetrieveUtils();

		started = new TreeSet<MiniEvent>(new Comparator<MiniEvent>() {
			@Override
			public int compare(MiniEvent o1, MiniEvent o2) {
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

		notYetEnded = new TreeSet<MiniEvent>(new Comparator<MiniEvent>() {
			@Override
			public int compare(MiniEvent o1, MiniEvent o2) {
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

	@Test
	public void testABNowBA() {
		//scenario 1: aStarts  bStarts now  bEnds  aEnds
		Date now = new Date();
		Date me1St = timeUtil.createDateAddMinutes(now, -10);
		Date me2St = timeUtil.createDateAddMinutes(now, -5);
		Date me2Et = timeUtil.createDateAddMinutes(now, 5);
		Date me1Et = timeUtil.createDateAddMinutes(now, 10);

		MiniEvent firstMe = new MiniEvent();
		firstMe.setId(1);
		firstMe.setStartTime(me1St);
		firstMe.setEndTime(me1Et);
		started.add(firstMe);
		notYetEnded.add(firstMe);

		MiniEvent secondMe = new MiniEvent();
		secondMe.setId(2);
		secondMe.setStartTime(me2St);
		secondMe.setEndTime(me2Et);
		started.add(secondMe);
		notYetEnded.add(secondMe);

		TreeSet<MiniEvent> mockedTreeSet = mock(TreeSet.class);
		when(mockedTreeSet.headSet(any(MiniEvent.class), any(Boolean.class))).thenReturn(started);
		when(mockedTreeSet.tailSet(any(MiniEvent.class), any(Boolean.class))).thenReturn(notYetEnded);

		miniEventRetrieveUtil.setMeStartTimeTree(mockedTreeSet);
		miniEventRetrieveUtil.setMeEndTimeTree(mockedTreeSet);

		Assert.assertEquals(firstMe, miniEventRetrieveUtil
				.getCurrentlyActiveMiniEvent(now));

		Assert.assertEquals(firstMe, miniEventRetrieveUtil
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

		MiniEvent firstMe = new MiniEvent();
		firstMe.setId(1);
		firstMe.setStartTime(me1St);
		firstMe.setEndTime(me1Et);
		started.add(firstMe);
		notYetEnded.add(firstMe);

		MiniEvent secondMe = new MiniEvent();
		secondMe.setId(2);
		secondMe.setStartTime(me2St);
		secondMe.setEndTime(me2Et);
		started.add(secondMe);
		notYetEnded.add(secondMe);

		TreeSet<MiniEvent> mockedTreeSet = mock(TreeSet.class);
		when(mockedTreeSet.headSet(any(MiniEvent.class), any(Boolean.class))).thenReturn(started);
		when(mockedTreeSet.tailSet(any(MiniEvent.class), any(Boolean.class))).thenReturn(notYetEnded);

		miniEventRetrieveUtil.setMeStartTimeTree(mockedTreeSet);
		miniEventRetrieveUtil.setMeEndTimeTree(mockedTreeSet);

		Assert.assertEquals(firstMe, miniEventRetrieveUtil
				.getCurrentlyActiveMiniEvent(now));

		Assert.assertEquals(firstMe, miniEventRetrieveUtil
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

		MiniEvent firstMe = new MiniEvent();
		firstMe.setId(1);
		firstMe.setStartTime(me1St);
		firstMe.setEndTime(me1Et);
		started.add(firstMe);
		notYetEnded.add(firstMe);

		MiniEvent secondMe = new MiniEvent();
		secondMe.setId(2);
		secondMe.setStartTime(me2St);
		secondMe.setEndTime(me2Et);
		started.add(secondMe);
		notYetEnded.add(secondMe);

		TreeSet<MiniEvent> mockedTreeSet = mock(TreeSet.class);
		when(mockedTreeSet.headSet(any(MiniEvent.class), any(Boolean.class))).thenReturn(started);
		when(mockedTreeSet.tailSet(any(MiniEvent.class), any(Boolean.class))).thenReturn(notYetEnded);

		miniEventRetrieveUtil.setMeStartTimeTree(mockedTreeSet);
		miniEventRetrieveUtil.setMeEndTimeTree(mockedTreeSet);

		Assert.assertEquals(firstMe, miniEventRetrieveUtil
				.getCurrentlyActiveMiniEvent(now));

		Assert.assertEquals(firstMe, miniEventRetrieveUtil
				.getCurrentlyActiveMiniEvent(new Date()));
	}
}
