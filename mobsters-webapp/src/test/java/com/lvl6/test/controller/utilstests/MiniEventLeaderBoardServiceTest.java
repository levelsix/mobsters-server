package com.lvl6.test.controller.utilstests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lvl6.mobsters.jooq.daos.service.MiniEventLeaderBoardService;

public class MiniEventLeaderBoardServiceTest {

	private static MiniEventLeaderBoardService melbs;
	
	@BeforeClass
	public static void setUp() {
		melbs = new MiniEventLeaderBoardService();
	}
	
	@Test
	public void testSortingHashMap() {
		HashMap<String, Long> testMap = new HashMap<String, Long>();
		testMap.put("four", (long)4);
		testMap.put("two", (long)2);
		testMap.put("one", (long)1);
		testMap.put("three", (long)3);
		LinkedHashMap<String, Long> sortedMap = melbs.sortHashMapByValues(testMap);
		for(String string : sortedMap.keySet()) {
			assertTrue(sortedMap.get(string) == (long)1);
			break;
		}
		sortedMap.remove("one");
		for(String string : sortedMap.keySet()) {
			assertTrue(sortedMap.get(string) == (long)2);
			break;
		}
		sortedMap.remove("two");
		for(String string : sortedMap.keySet()) {
			assertTrue(sortedMap.get(string) == (long)3);
			break;
		}
		sortedMap.remove("three");
		for(String string : sortedMap.keySet()) {
			assertTrue(sortedMap.get(string) == (long)4);
			break;
		}
	}
	
	
	
	
}
