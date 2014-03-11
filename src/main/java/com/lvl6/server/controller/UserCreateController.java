package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UserCreateRequestEvent;
import com.lvl6.events.response.ReferralCodeUsedResponseEvent;
import com.lvl6.events.response.UserCreateResponseEvent;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.User;
import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventReferralProto.ReferralCodeUsedResponseProto;
import com.lvl6.proto.EventUserProto.UserCreateRequestProto;
import com.lvl6.proto.EventUserProto.UserCreateResponseProto;
import com.lvl6.proto.EventUserProto.UserCreateResponseProto.Builder;
import com.lvl6.proto.EventUserProto.UserCreateResponseProto.UserCreateStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.TutorialStructProto;
import com.lvl6.proto.UserProto.FullUserProto;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.EventWriter;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class UserCreateController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Resource
  protected EventWriter eventWriter;

  public EventWriter getEventWriter() {
    return eventWriter;
  }

  public void setEventWriter(EventWriter eventWriter) {
    this.eventWriter = eventWriter;
  }
  
  @Autowired
  protected LeaderBoardUtil leaderboard;

  public LeaderBoardUtil getLeaderboard() {
	return leaderboard;
	}
	
	public void setLeaderboard(LeaderBoardUtil leaderboard) {
		this.leaderboard = leaderboard;
	}
  
  @Autowired
  protected InsertUtil insertUtils;

  public void setInsertUtils(InsertUtil insertUtils) {
    this.insertUtils = insertUtils;
  }


  @Autowired
  protected Locker locker;
  
  
  protected Locker getLocker() {
		return locker;
	}

	protected void setLocker(Locker locker) {
		this.locker = locker;
	}
	
	@Autowired
	protected TimeUtils timeUtils;
	

  public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public UserCreateController() {
    numAllocatedThreads = 3;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new UserCreateRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_USER_CREATE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    UserCreateRequestProto reqProto = ((UserCreateRequestEvent)event).getUserCreateRequestProto();
    String udid = reqProto.getUdid();
    String name = reqProto.getName();
    String deviceToken = reqProto.getDeviceToken();
    String fbId = reqProto.getFacebookId();
    Timestamp createTime = new Timestamp((new Date()).getTime());
    List<TutorialStructProto> structsJustBuilt = reqProto.getStructsJustBuiltList();
    String facebookId = reqProto.getFacebookId();
    
    //in case user tries hacking, don't let the amount go over tutorial default values
    int cash = Math.min(reqProto.getCash(), ControllerConstants.TUTORIAL__INIT_CASH);
    int oil = Math.min(reqProto.getOil(), ControllerConstants.TUTORIAL__INIT_OIL);
    int gems = Math.min(reqProto.getGems(), ControllerConstants.TUTORIAL__INIT_GEMS);

    UserCreateResponseProto.Builder resBuilder = UserCreateResponseProto.newBuilder();
    resBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
    
    boolean gotLock = true;
    if (null != fbId && !fbId.isEmpty()) {
    	gotLock = getLocker().lockFbId(fbId);
    }
    try {
			boolean legitUserCreate = checkLegitUserCreate(gotLock, resBuilder, udid,
					facebookId, name);

			User user = null;
			int userId = ControllerConstants.NOT_SET;

			if (legitUserCreate) {
//			  String newReferCode = grabNewReferCode();
//			  taskCompleted = TaskRetrieveUtils.getTaskForTaskId(ControllerConstants.TUTORIAL__FIRST_TASK_ID);
//			  questTaskCompleted = TaskRetrieveUtils.getTaskForTaskId(ControllerConstants.TUTORIAL__FAKE_QUEST_TASK_ID);

			  user = writeChangeToDb(resBuilder, name, udid, cash, oil, gems, deviceToken,
			  		createTime, facebookId);
			}

			UserCreateResponseProto resProto = resBuilder.build();
			UserCreateResponseEvent resEvent = new UserCreateResponseEvent(udid);
			resEvent.setTag(event.getTag());
			resEvent.setUserCreateResponseProto(resProto);
			log.info("Writing event: " + resEvent);
			server.writePreDBEvent(resEvent, udid);

			if (user != null) {
				//recording that player is online I guess
				userId = user.getId();
			  ConnectedPlayer player = server.getPlayerByUdId(udid);
			  player.setPlayerId(userId);
			  server.getPlayersByPlayerId().put(userId, player);
			  server.getPlayersPreDatabaseByUDID().remove(udid);
			}

			if (legitUserCreate && userId > 0) {
			  /*server.lockPlayer(userId, this.getClass().getSimpleName());*/
			  try {
			  	//TAKE INTO ACCOUNT THE PROPERTIES SENT IN BY CLIENT
			  	log.info("writing user structs");
			  	writeStructs(userId, createTime, structsJustBuilt);
			  	log.info("writing tasks");
			    writeTaskCompleted(userId, createTime);
			    writeMonsters(userId, createTime);
//			    LeaderBoardUtil leaderboard = AppContext.getApplicationContext().getBean(LeaderBoardUtil.class);
//			    leaderboard.updateLeaderboardForUser(user);
			    
			    //CURRENCY CHANGE HISTORY
			    writeToUserCurrencyHistory(userId, cash, oil, gems, createTime);
			    
			  } catch (Exception e) {
			    log.error("exception in UserCreateController processEvent", e);
			  } /*finally {
			    server.unlockPlayer(userId, this.getClass().getSimpleName()); 
			  }*/
			}
		} catch (Exception e) {
			log.error("exception in UserCreateController processEvent", e);
			try {
    		resBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
    		UserCreateResponseEvent resEvent = new UserCreateResponseEvent(udid);
    		resEvent.setTag(event.getTag());
    		resEvent.setUserCreateResponseProto(resBuilder.build());
    		server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in UserCreateController processEvent", e);
    	}
		}
  }
  
  private boolean checkLegitUserCreate(boolean gotLock, Builder resBuilder, String udid,
  		String facebookId, String name) {
  	List<User> users = RetrieveUtils.userRetrieveUtils().getUserByUDIDorFbId(udid, facebookId);
  	
  	if (!gotLock) {
  		log.error("did not get fb lock. fbId=" + facebookId + ", udid=" + udid + ", name="
  				+ name);
  		return false;
  	}
  	
  	if (null == users || users.isEmpty()) {
  		return true;
  	}
    
    if (null != facebookId && !facebookId.isEmpty() &&
    		facebookIdExists(facebookId, users)) {
    	//check if facebookId is tied to an account
    	resBuilder.setStatus(UserCreateStatus.FAIL_USER_WITH_FACEBOOK_ID_EXISTS);
    	log.error("user with facebookId " + facebookId + " already exists. users=" + users);
    	return false;
    }
    if (null != udid && !udid.isEmpty() && udidExists(udid, users)) {
    	//udid shouldn't be empty
    	//check if udid is tied to an account
    	resBuilder.setStatus(UserCreateStatus.FAIL_USER_WITH_UDID_ALREADY_EXISTS);
    	log.error("user with udid " + udid + " already exists");
    	return false;
    }
    
//    resBuilder.setStatus(UserCreateStatus.SUCCESS);
    return true;
  }
  
  private boolean facebookIdExists(String facebookId, List<User> users) {
    for(User u : users) {
    	String userFacebookId = u.getFacebookId();
    	
    	if (null != userFacebookId && userFacebookId.equals(facebookId)) {
    		return true;
    	}
    }
    return false;
  }
  
  private boolean udidExists(String udid, List<User> users) {
    for(User u : users) {
    	String userUdid = u.getUdid();
    	
    	if (null != userUdid && userUdid.equals(udid)) {
    		return true;
    	}
    }
    return false;
  }
  
  private User writeChangeToDb(Builder resBuilder, String name, String udid, int cash,
  		int oil, int gems, String deviceToken, Timestamp createTime, String facebookId) {
  	User user = null;
  //TODO: FIX THESE NUMBERS
		int lvl = ControllerConstants.USER_CREATE__START_LEVEL;  
	  int playerExp = 69;
	  
	  //newbie protection
	  boolean activateShield = true;
	  String rank = ControllerConstants.TUTORIAL__INIT_RANK;
	  
	  Date createDate = new Date(createTime.getTime());
	  Date shieldEndDate = getTimeUtils().createDateAddDays(createDate,
	  		ControllerConstants.PVP__SHIELD_DURATION_DAYS);
	  Timestamp shieldEndTime = new Timestamp(shieldEndDate.getTime());
	  
	  int userId = insertUtils.insertUser(name, udid, lvl,  playerExp, cash, oil,
	      gems, false, deviceToken, activateShield, createTime, rank, facebookId,
	      shieldEndTime);
	        
	  if (userId > 0) {
	    /*server.lockPlayer(userId, this.getClass().getSimpleName());*/
	    try {
	      user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
	      FullUserProto userProto = CreateInfoProtoUtils.createFullUserProtoFromUser(user);
	      resBuilder.setSender(userProto);
	    } catch (Exception e) {
	      log.error("exception in UserCreateController processEvent", e);
	    } /*finally {
	      server.unlockPlayer(userId, this.getClass().getSimpleName()); 
	    }*/
	  } else {
	    resBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
	    log.error("problem with trying to create user. udid=" + udid + ", name=" +
	    		name + ", deviceToken=" + deviceToken + ", playerExp=" + playerExp +
	    		", cash=" + cash+ ", oil=" + oil + ", gems=" + gems); 
	  }
	  
	  log.info("created new user=" + user);
	  resBuilder.setStatus(UserCreateStatus.SUCCESS);
	  return user;
  }
  
  
  //THE VALUES AND STRUCTS TO GIVE THE USER
  private void writeStructs(int userId, Timestamp purchaseTime,
  		List<TutorialStructProto> structsJustBuilt) {
  	Date purchaseDate = new Date(purchaseTime.getTime());
  	Date purchaseDateOneWeekAgo = getTimeUtils().createDateAddDays(purchaseDate, -7);
  	Timestamp purchaseTimeOneWeekAgo = new Timestamp(purchaseDateOneWeekAgo.getTime());
  	
  	int[] buildingIds = ControllerConstants.TUTORIAL__EXISTING_BUILDING_IDS;
  	float[] xPositions = ControllerConstants.TUTORIAL__EXISTING_BUILDING_X_POS;
  	float[] yPositions = ControllerConstants.TUTORIAL__EXISTING_BUILDING_Y_POS;
  	log.info("giving user buildings");
  	int quantity = buildingIds.length;
  	List<Integer> userIdList = new ArrayList<Integer>(
  			Collections.nCopies(quantity, userId));
  	List<Integer> structIdList = new ArrayList<Integer>();
  	List<Float> xCoordList = new ArrayList<Float>();
  	List<Float> yCoordList = new ArrayList<Float>();
  	List<Timestamp> purchaseTimeList = new ArrayList<Timestamp>(
  			Collections.nCopies(quantity, purchaseTime));
  	List<Timestamp> retrievedTimeList = new ArrayList<Timestamp>(
  			Collections.nCopies(quantity, purchaseTimeOneWeekAgo));
  	List<Boolean> isComplete = new ArrayList<Boolean>(
  			Collections.nCopies(quantity, true));
  	
  	for (int i = 0; i < buildingIds.length; i++) {
  		int structId = buildingIds[i];
  		float xCoord = xPositions[i];
  		float yCoord = yPositions[i];
  		
  		structIdList.add(structId);
  		xCoordList.add(xCoord);
  		yCoordList.add(yCoord);
  	}
  	
  	//giving user the buildings he just created
  	for (TutorialStructProto tsp : structsJustBuilt) {
  		int structId = tsp.getStructId();
  		CoordinateProto cp = tsp.getCoordinate();
  		float xCoord = cp.getX();
  		float yCoord = cp.getY();
  		
  		userIdList.add(userId);
  		structIdList.add(structId);
  		xCoordList.add(xCoord);
  		yCoordList.add(yCoord);
  		purchaseTimeList.add(purchaseTime);
  		retrievedTimeList.add(purchaseTime);
  		isComplete.add(true);
  	}
  	
  	log.info("userIdList=" + userIdList);
  	log.info("structIdList=" + structIdList);
  	log.info("xCoordList=" + xCoordList);
  	log.info("yCoordList=" + yCoordList);
  	log.info("purchaseTimeList=" + purchaseTimeList);
  	log.info("retrievedTimeList=" + retrievedTimeList);
  	log.info("isComplete=" + isComplete);
  	
  	
  	int numInserted = InsertUtils.get().insertUserStructs(userIdList, structIdList,
  			xCoordList, yCoordList, purchaseTimeList, retrievedTimeList, isComplete);
  	log.info("num buildings given to user: " + numInserted);
  }

  private void writeTaskCompleted(int userId, Timestamp createTime) {
  	List<Integer> taskIdList = new ArrayList<Integer>();
  	taskIdList.add(ControllerConstants.TUTORIAL__CITY_ELEMENT_ID_FOR_FIRST_DUNGEON);
  	taskIdList.add(ControllerConstants.TUTORIAL__CITY_ELEMENT_ID_FOR_SECOND_DUNGEON);
  	
  	int size = taskIdList.size();
  	List<Integer> userIdList = Collections.nCopies(size, userId);
  	List<Timestamp> createTimeList = Collections.nCopies(size, createTime);
  	
  	int numInserted = InsertUtils.get().insertIntoTaskForUserCompleted(userIdList,
  			taskIdList, createTimeList);
  	log.info("num tasks inserted:" + numInserted + ", should be " + size);
  }
  
  private void writeMonsters(int userId, Timestamp createTime) {
  	String sourceOfPieces = ControllerConstants.MFUSOP__USER_CREATE;
  	Date createDate = new Date(createTime.getTime());
  	Date combineStartDate = getTimeUtils().createDateAddDays(createDate, -7);
  	
  	List<Integer> monsterIds = new ArrayList<Integer>();
  	monsterIds.add(ControllerConstants.TUTORIAL__STARTING_MONSTER_ID);
  	monsterIds.add(ControllerConstants.TUTORIAL__MARK_Z_MONSTER_ID);
  	
  	List<MonsterForUser> userMonsters = new ArrayList<MonsterForUser>();
  	for (int i = 0; i < monsterIds.size(); i++) {
  		int monsterId = monsterIds.get(i);
  		int teamSlotNum = i + 1;
  		
  		Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(monsterId);
  		Map<Integer, MonsterLevelInfo> info = MonsterLevelInfoRetrieveUtils
  				.getMonsterLevelInfoForMonsterId(monsterId);
  		
  		List<Integer> lvls = new ArrayList<Integer>(info.keySet());
  		Collections.sort(lvls);
  		int firstOne = lvls.get(0);
  		MonsterLevelInfo mli = info.get(firstOne);
  		
  		
  		MonsterForUser mfu = new MonsterForUser(0, userId, monsterId, mli.getCurLvlRequiredExp(),
  				mli.getLevel(), mli.getHp(), monzter.getNumPuzzlePieces(), true, combineStartDate,
  				teamSlotNum, sourceOfPieces);
  		userMonsters.add(mfu);
  	}
  	List<Long> ids = InsertUtils.get().insertIntoMonsterForUserReturnIds(userId,
  			userMonsters, sourceOfPieces, combineStartDate);
  	
  	log.info("monsters inserted for userId=" + userId + ", ids=" + ids);
  }


//  private String grabNewReferCode() {
//    String newReferCode = AvailableReferralCodeRetrieveUtils.getAvailableReferralCode();
//    if (newReferCode != null && newReferCode.length() > 0) {
//      while (!DeleteUtils.get().deleteAvailableReferralCode(newReferCode)) {
//        newReferCode = AvailableReferralCodeRetrieveUtils.getAvailableReferralCode();
//      }
//    } else {
//      log.error("no refer codes left");
//    }
//    return newReferCode;
//  }

  private void rewardReferrer(User referrer, User user) {
    if (!referrer.isFake()) {
      server.lockPlayer(referrer.getId(), this.getClass().getSimpleName());
      try {
        int previousSilver = referrer.getCash();
        
        int coinsGivenToReferrer = MiscMethods.calculateCoinsGivenToReferrer(referrer);
        if (!referrer.updateRelativeCoinsNumreferrals(coinsGivenToReferrer, 1)) {
          log.error("problem with rewarding the referrer " + referrer + " with this many coins: " + coinsGivenToReferrer);
        } else {
          if (!insertUtils.insertReferral(referrer.getId(), user.getId(), coinsGivenToReferrer)) {
            log.error("problem with inserting referral into db. referrer is " + referrer.getId() + ", user=" + user.getId()
                + ", coins given to referrer=" + coinsGivenToReferrer);
          }
          ReferralCodeUsedResponseEvent resEvent = new ReferralCodeUsedResponseEvent(referrer.getId());
          ReferralCodeUsedResponseProto resProto = ReferralCodeUsedResponseProto.newBuilder()
              .setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUser(referrer))
              .setReferredPlayer(CreateInfoProtoUtils.createMinimumUserProtoFromUser(user))
              .setCoinsGivenToReferrer(coinsGivenToReferrer).build();
          resEvent.setReferralCodeUsedResponseProto(resProto);
          server.writeAPNSNotificationOrEvent(resEvent);
          
          writeToUserCurrencyHistoryTwo(referrer, coinsGivenToReferrer, previousSilver);
        }
      } catch (Exception e) {
        log.error("exception in UserCreateController processEvent", e);
      } finally {
        server.unlockPlayer(referrer.getId(), this.getClass().getSimpleName()); 
      }
    }
  }


  private void writeToUserCurrencyHistory(int userId, int cash, int oil, int gems,
  		Timestamp createTime) {
  	String reasonForChange = ControllerConstants.UCHRFC__USER_CREATED;

    Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> detailsMap = new HashMap<String, String>();
    String gemsStr = MiscMethods.gems;
    String cashStr = MiscMethods.cash;
    String oilStr = MiscMethods.oil;

    previousCurrency.put(gemsStr, 0);
    previousCurrency.put(cashStr, 0);
    previousCurrency.put(oilStr, 0);
    currentCurrency.put(gemsStr, gems);
    currentCurrency.put(cashStr, cash);
    currentCurrency.put(oilStr, oil);
    reasonsForChanges.put(gemsStr, reasonForChange);
    reasonsForChanges.put(cashStr, reasonForChange);
    reasonsForChanges.put(oilStr, reasonForChange);
    detailsMap.put(gemsStr, "");
    detailsMap.put(cashStr, "");
    detailsMap.put(oilStr, "");

    MiscMethods.writeToUserCurrencyOneUser(userId, createTime, currentCurrency, 
        previousCurrency, currentCurrency, reasonsForChanges, detailsMap);

  }
  //TODO: FIX THIS
  public void writeToUserCurrencyHistoryTwo(User aUser, int coinChange, int previousSilver) {
//    Timestamp date = new Timestamp((new Date()).getTime());
//
//    Map<String, Integer> goldSilverChange = new HashMap<String, Integer>();
//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String silver = MiscMethods.cash;
//    String reasonForChange = ControllerConstants.UCHRFC__USER_CREATE_REFERRED_A_USER;
//    
//    goldSilverChange.put(silver, coinChange);
//    previousGoldSilver.put(silver, previousSilver);
//    reasonsForChanges.put(silver, reasonForChange);
//    
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, goldSilverChange,
//        previousGoldSilver, reasonsForChanges);
  }
  
}
