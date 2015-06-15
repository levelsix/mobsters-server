package com.lvl6.test.controller.integrationtests;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.info.StrengthLeaderBoard;
import com.lvl6.info.User;
import com.lvl6.leaderboards.LeaderBoardImpl;
import com.lvl6.proto.UserProto.MinimumUserProto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class LeaderboardTest {

	private JdbcTemplate jdbcTemplate;

	private User user;
	private MinimumUserProto mup;
	private String userId;

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	protected LeaderBoardImpl leaderBoardImpl;
	
	
	@Test
	public void testLeaderboard() {
		int sizeOfLeaderBoard = leaderBoardImpl.getSize();
		List<StrengthLeaderBoard> top50List = leaderBoardImpl.getTopNStrengths(50);
		assertTrue(!top50List.isEmpty());
		assertTrue(top50List.size() == 50);
		long strength50 = 0;
		
		for(StrengthLeaderBoard slb : top50List) {
			if(slb.getRank() == 50) {
				strength50 = slb.getStrength();
			}
		}
		
		leaderBoardImpl.addToLeaderboard("test", (long)(strength50 + 1));
		
		assertTrue(leaderBoardImpl.getUserRank("test") == 50);
		assertTrue(sizeOfLeaderBoard + 1 == leaderBoardImpl.getSize());
	}
		
	
	
}
