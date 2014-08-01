package com.lvl6.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class MonsterForUserTest extends TestCase {
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
	@Autowired
	protected MonsterForUserRetrieveUtils mfuRetrieveUtil;
	
	private int TESTER_ID = 11;
	private int PIECE_DEFICIENT_MONSTER_ID = 140;
	private long PIECE_SUFFICIENT_USER_MONSTER_ID = 36849;
	private int PIECE_SUFFICIENT_MONSTER_ID = 38;
	
	@Test
	public void testCompletingMonster() {
		//get current monsters
		//give user a full monster via dev controller
		//check that current monsters is + 1
		Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(PIECE_DEFICIENT_MONSTER_ID);
		
		Map<Integer, MonsterForUser> monsterIdToMfu = mfuRetrieveUtil
			.getPieceDeficientIncompleteMonstersWithUserAndMonsterIds(
				TESTER_ID, Collections.singletonList(PIECE_DEFICIENT_MONSTER_ID));
		
		assertTrue("Expected user to have one piece deficient monster, but does not",
			!monsterIdToMfu.isEmpty());
		MonsterForUser mfu = monsterIdToMfu.get(PIECE_DEFICIENT_MONSTER_ID);
		int currentNumPieces = mfu.getNumPieces();
		
		int numPuzzlePieces = monzter.getNumPuzzlePieces();
		int numPiecesRemaining = MonsterStuffUtils.completePieceDeficientMonster(
			mfu, numPuzzlePieces, monzter);
			
		//The number of pieces left should still be the same amount the monster had
		//pieces remaining = numPuzzlePieces - (numPuzzlePieces - currentNumPieces)
		//				   = currentNumPieces
		assertEquals(currentNumPieces, numPiecesRemaining);

		assertEquals(numPuzzlePieces, mfu.getNumPieces());
	}

	@Test
	public void testCompletingPieceSufficientMonster() {
		MonsterForUser mfu = mfuRetrieveUtil.getSpecificUserMonster(PIECE_SUFFICIENT_USER_MONSTER_ID);
		Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(
			PIECE_SUFFICIENT_MONSTER_ID);
		
		mfu.setNumPieces(monzter.getNumPuzzlePieces());
		mfu.setComplete(false); //these don't really matter
		mfu.setHasAllPieces(true); //these don't really matter
		
		
		Map<Integer, MonsterForUser> monsterIdToMfu = new HashMap<Integer, MonsterForUser>();
		monsterIdToMfu.put(PIECE_SUFFICIENT_MONSTER_ID, mfu);
		
		Map<Integer, Integer> monsterIdToQuantity = Collections
			.singletonMap(PIECE_SUFFICIENT_MONSTER_ID, monzter.getNumPuzzlePieces());

		//test out that an impossibility does not change anything:
		//impossibility referring to that a piece sufficient monster
		//would be sent in as an argument to 
		// MonsterStuffUtils.completeMonstersFromQuantities()
		Map<Integer, Integer> newMonsterIdToQuantity = MonsterStuffUtils
			.completeMonstersFromQuantities(monsterIdToMfu, monsterIdToQuantity);
			
		assertEquals(monsterIdToQuantity.get(PIECE_SUFFICIENT_MONSTER_ID),
			newMonsterIdToQuantity.get(PIECE_SUFFICIENT_MONSTER_ID));
		assertTrue(monsterIdToMfu.isEmpty());
		
	}
	
	public MonsterForUserRetrieveUtils getMfuRetrieveUtil()
	{
		return mfuRetrieveUtil;
	}

	public void setMfuRetrieveUtil( MonsterForUserRetrieveUtils mfuRetrieveUtil )
	{
		this.mfuRetrieveUtil = mfuRetrieveUtil;
	}
	
}
