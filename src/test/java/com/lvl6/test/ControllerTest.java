package com.lvl6.test;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.lvl6.events.request.StartupRequestEvent;
import com.lvl6.info.User;
import com.lvl6.proto.EventProto.StartupRequestProto;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.server.controller.StartupController;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ControllerTest extends TestCase {
	
	private static Logger log = LoggerFactory.getLogger(StatsTest.class);
	
	
	@Autowired
	StartupController startupController;
	
	@Autowired
	UserRetrieveUtils userRetrieveUtils;

	public void createUser(String udid) {
		StartupRequestProto.Builder srpb = StartupRequestProto.newBuilder();
		srpb.setUdid(udid);
		srpb.setVersionNum(3.6f);
		srpb.setIsForceTutorial(false);
		
		StartupRequestEvent sre = new StartupRequestEvent();
		sre.setStartupRequestProto(srpb.build());
		
		//have the controller process this event
		getStartupController().handleEvent(sre);
	}

	@Transactional
	@Rollback(true)
	@Test
	public void testStartup() {
//		String udid = "blah";
//		createUser(udid);
//		
//		User aUser = getUserRetrieveUtils().getUserByUDID(udid);
//		assertTrue("Expected: not null. Actual=" + aUser, null != aUser);
//		
	}

	public StartupController getStartupController() {
		return startupController;
	}

	public void setStartupController(StartupController startupController) {
		this.startupController = startupController;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		ControllerTest.log = log;
	}

	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}
	
}
