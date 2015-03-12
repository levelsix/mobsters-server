package com.lvl6.test.ControllerTests;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@ContextConfiguration("/test-spring-application-context.xml")
public class BeforeClassSetUp extends AbstractTestExecutionListener {
	
	public BeforeClassSetUp() {}
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static JdbcTemplate jdbcTemplate;
	
//	@Autowired
//	InsertUtil insertUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;
	
	
	@Override
	public void beforeTestClass(TestContext testContext) {
		log.info("setUp");
		Timestamp createTime = new Timestamp((new Date()).getTime());

		String name = "bobUnitTest";
		String udid = "bobUdid";
		int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
		int playerExp = 10;
		int cash = 10000;
		int oil = 10000;
		int gems = 10000;
		String deviceToken = "bobToken";
		String facebookId = null;
		int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
		String email = null;
		String fbData = null;
		
		InsertUtil insertUtil = testContext.getApplicationContext().getBean(InsertUtil.class);

		String userId = insertUtil.insertUser(name, udid, lvl,  playerExp, cash, oil,
				gems, false, deviceToken, createTime, facebookId, avatarMonsterId,
				email, fbData);


		if (null == userId) {
			throw new RuntimeException("no user was created!");
		}

//		mup = CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null);
	}
	
}
