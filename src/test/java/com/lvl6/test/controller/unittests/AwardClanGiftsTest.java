package com.lvl6.test.controller.unittests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lvl6.info.ClanGiftRewards;
import com.lvl6.server.controller.actionobjects.AwardClanGiftsAction;


public class AwardClanGiftsTest {


	@Test
	public void testDetermineReward() {
		AwardClanGiftsAction acga = new AwardClanGiftsAction();
		List<ClanGiftRewards> listOfRewards = new ArrayList<ClanGiftRewards>();
		ClanGiftRewards cgr = new ClanGiftRewards();
		cgr.setRewardId(1);
		cgr.setChanceOfDrop((float)0.01);

		ClanGiftRewards cgr2 = new ClanGiftRewards();
		cgr2.setRewardId(2);
		cgr2.setChanceOfDrop((float)0.01);

		ClanGiftRewards cgr3 = new ClanGiftRewards();
		cgr3.setRewardId(3);
		cgr3.setChanceOfDrop((float)0.01);

		ClanGiftRewards cgr4 = new ClanGiftRewards();
		cgr4.setRewardId(4);
		cgr4.setChanceOfDrop((float)0.96);

		ClanGiftRewards cgr5 = new ClanGiftRewards();
		cgr5.setRewardId(5);
		cgr5.setChanceOfDrop((float)0.11);

		listOfRewards.add(cgr);
		listOfRewards.add(cgr2);
		listOfRewards.add(cgr3);
		listOfRewards.add(cgr4);
		listOfRewards.add(cgr5);

		acga.setRewardsForClanGift(listOfRewards);

		assertEquals(acga.determineReward(), 4);
	}


}
