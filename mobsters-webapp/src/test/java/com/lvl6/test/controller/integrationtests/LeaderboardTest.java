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
		List<StrengthLeaderBoard> slbList = leaderBoardImpl.getTopNStrengths(3);
		assertTrue(!slbList.isEmpty());
		log.info("slbList: {}", slbList);
		
		leaderBoardImpl.addToLeaderboard("hi1", (long)280072663);
		log.info("user rank {}", leaderBoardImpl.getUserRank("hi1"));
		List<StrengthLeaderBoard> slbList3 = leaderBoardImpl.getTopNStrengths(3);
		log.info("slbList3: {}", slbList3);
		
		List<StrengthLeaderBoard> slbList4 = leaderBoardImpl.getStrengths(25, 30);
		log.info("slbList4 {}", slbList4);
	}
	
	
}
