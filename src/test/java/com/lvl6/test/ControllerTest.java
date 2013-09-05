package com.lvl6.test;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.events.request.StartupRequestEvent;
import com.lvl6.events.request.UserCreateRequestEvent;
import com.lvl6.info.User;
import com.lvl6.proto.EventProto.StartupRequestProto;
import com.lvl6.proto.EventProto.UserCreateRequestProto;
import com.lvl6.proto.InfoProto.CoordinateProto;
import com.lvl6.proto.InfoProto.LocationProto;
import com.lvl6.proto.InfoProto.UserType;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.server.controller.StartupController;
import com.lvl6.server.controller.UserCreateController;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ControllerTest extends TestCase {
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
	
	@Autowired
	StartupController startupController;
	
	@Autowired
	UserRetrieveUtils userRetrieveUtils;
	
	@Autowired
	UserCreateController userCreateController;

	public void createUser(String udid) {
		String name = "bob";
		UserType ut = UserType.BAD_ARCHER;
		
		LocationProto.Builder lpb = LocationProto.newBuilder();
		lpb.setLatitude(1.1D);
		lpb.setLongitude(1.1D);
		LocationProto lp = lpb.build();
		
		String deviceToken = "deviceToken";
		int attack = 12;
		int defense = 12;
		int energy = 50;
		int stamina = 3;
		
		long timeOfStructPurchase = (new Date()).getTime();
		long timeOfStructBuild = timeOfStructPurchase;
		
		CoordinateProto.Builder cpb = CoordinateProto.newBuilder();
		cpb.setX(1.1F);
		cpb.setY(1.1F);
		CoordinateProto cp = cpb.build();
		boolean usedDiamondsToBuilt = true;
		
		UserCreateRequestProto.Builder ucrpb = UserCreateRequestProto.newBuilder();
		ucrpb.setUdid(udid);
		ucrpb.setName(name);
		ucrpb.setType(ut);
		ucrpb.setUserLocation(lp);
		ucrpb.setDeviceToken(deviceToken);
		ucrpb.setAttack(attack);
		ucrpb.setDefense(defense);
		ucrpb.setEnergy(energy);
		ucrpb.setStamina(stamina);
		ucrpb.setTimeOfStructPurchase(timeOfStructPurchase);
		ucrpb.setTimeOfStructBuild(timeOfStructBuild);
		ucrpb.setStructCoords(cp);
		ucrpb.setUsedDiamondsToBuilt(usedDiamondsToBuilt);
		
		UserCreateRequestEvent ucre = new UserCreateRequestEvent();
		ucre.setTag(1);
		ucre.setUdid(udid);
		ucre.setUserCreateRequestProto(ucrpb.build());
		
		getUserCreateController().handleEvent(ucre);
	}
	
	public void loginUser(String udid) {
		StartupRequestProto.Builder srpb = StartupRequestProto.newBuilder();
		srpb.setUdid(udid);
		srpb.setVersionNum(3.6f);
		srpb.setIsForceTutorial(false);
		
		StartupRequestEvent sre = new StartupRequestEvent();
		sre.setStartupRequestProto(srpb.build());
		
		//have the controller process this event
		getStartupController().handleEvent(sre);
	}
	
	public void deleteUser(String udid) {
		
	}

	@Test
	public void testStartup() {
//		String udid = "blah";
//		log.info("\n\n\n\n\n\n\n\n\n\n\n CREATING USER");
//		createUser(udid);
//		log.info("CREATED USER\n\n\n\n\n\n\n\n\n\n\n");
//		
//		User aUser = getUserRetrieveUtils().getUserByUDID(udid);
//		assertTrue("Expected: not null. Actual=" + aUser, null != aUser);
//		log.info("user=" + aUser);
//		log.info("\n\n\n\n\n\n");
		
		//TODO: MAKE CLEAN UP FUNCTIONS TO DELETE EVERYTHING THIS METHOD DID
	}
	
//	@Test
//	public void testPurchaseCityExpansion() {
//		
//	}
	
	

	public StartupController getStartupController() {
		return startupController;
	}

	public void setStartupController(StartupController startupController) {
		this.startupController = startupController;
	}

	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UserCreateController getUserCreateController() {
		return userCreateController;
	}

	public void setUserCreateController(UserCreateController userCreateController) {
		this.userCreateController = userCreateController;
	}
	
}
