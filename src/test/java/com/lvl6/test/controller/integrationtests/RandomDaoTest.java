package com.lvl6.test.controller.integrationtests;

import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.info.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureForUser;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.daos.StructureForUserDao2;
import com.lvl6.server.controller.InAppPurchaseController;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.InsertUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class RandomDaoTest {

	private static Logger log = LoggerFactory.getLogger(SalesTest.class);

	private JdbcTemplate jdbcTemplate;
	private boolean endOfTesting;
	private User user;

	private MinimumUserProto mup;
	private String userId;
	private String userResearchUuid;

	@Autowired
	InsertUtil insertUtil;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	InAppPurchaseController inAppPurchaseController;

	@Autowired
	MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;
	
	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	CreateInfoProtoUtils createInfoProtoUtils;
	
	@Autowired
	protected StructureForUserDao2 sfuDao;


	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}



	@Test
	public void testUpdatingDao() {
//		StructureForUserDao2 sfuDao = AppContext.getApplicationContext().getBean(StructureForUserDao2.class);//TODO: These actions should be created in spring
//		Configuration config = new DefaultConfiguration().set(DBConnection.get().getConnection()).set(SQLDialect.MYSQL);
//
//		sfuDao.setConfiguration(config);
	
		StructureForUser sfu = new StructureForUser();
		sfu.setFbInviteStructLvl((byte)0);
		sfu.setId("id");
		sfu.setIsComplete((byte)1);
		sfu.setLastRetrieved(new Timestamp(new Date().getTime()));
		sfu.setOrientation("orientation");
		sfu.setPurchaseTime(new Timestamp(new Date().getTime()));
		sfu.setStructId(1);
		sfu.setUserId("test user id");
		sfu.setXcoord(1);
		sfu.setYcoord(1);
		sfuDao.insert(sfu);
		
		StructureForUser sfu2 = new StructureForUser();
		sfu2.setId("id");
		sfu2.setXcoord(3);
		
		sfuDao.update(sfu2);
		
		List<StructureForUser> sfulist = sfuDao.fetchById("id");
		for(StructureForUser sfu3 : sfulist) {
			log.info("sfu details:");
			log.info("sfu id: {}", sfu3.getId());
			log.info("sfu x coord: {}", sfu3.getXcoord());
			log.info("sfu y coord: {}", sfu3.getYcoord());
			log.info("sfu user id", sfu3.getUserId());
		}
		
	}
}
	
	
	
	
	
	
	
	
	
	
	
	