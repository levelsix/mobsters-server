package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UserCreateRequestEvent;
import com.lvl6.events.response.UserCreateResponseEvent;
import com.lvl6.info.ItemSecretGiftForUser;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.PvpLeague;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventUserProto.UserCreateRequestProto;
import com.lvl6.proto.EventUserProto.UserCreateResponseProto;
import com.lvl6.proto.EventUserProto.UserCreateResponseProto.Builder;
import com.lvl6.proto.EventUserProto.UserCreateResponseProto.UserCreateStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.TutorialStructProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.StructureStuffUtil;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
@DependsOn("gameServer")
public class UserCreateController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected InsertUtil insertUtils;

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected StructureStuffUtil structureStuffUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	
	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;
	
	@Autowired
	protected PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils;
	
	@Autowired
	protected MiscMethods miscMethods;
	
	@Autowired
	protected TaskRetrieveUtils taskRetrieveUtils;

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
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		UserCreateRequestProto reqProto = ((UserCreateRequestEvent) event)
				.getUserCreateRequestProto();
		String udid = reqProto.getUdid();
		String name = reqProto.getName();
		String deviceToken = reqProto.getDeviceToken();
		String fbId = reqProto.getFacebookId();
		Timestamp createTime = new Timestamp((new Date()).getTime());
		List<TutorialStructProto> structsJustBuilt = reqProto
				.getStructsJustBuiltList();
		String facebookId = reqProto.getFacebookId();

		//in case user tries hacking, don't let the amount go over tutorial default values
		int cash = Math.max(reqProto.getCash(), 0);
		int oil = Math.max(reqProto.getOil(), 0);
		int gems = Math.max(reqProto.getGems(), 0);

		cash = Math.min(cash, ControllerConstants.TUTORIAL__INIT_CASH);
		oil = Math.min(oil, ControllerConstants.TUTORIAL__INIT_OIL);
		gems = Math.min(gems, ControllerConstants.TUTORIAL__INIT_GEMS);

		String email = reqProto.getEmail();
		String fbData = reqProto.getFbData();

		UserCreateResponseProto.Builder resBuilder = UserCreateResponseProto
				.newBuilder();
		resBuilder.setStatus(UserCreateStatus.FAIL_OTHER);

		boolean gotLock = true;
		if (null != fbId && !fbId.isEmpty()) {
			//this is to prevent one user on two devices creating account with one fbId
			gotLock = getLocker().lockFbId(fbId);
		}
		try {
			boolean legitUserCreate = checkLegitUserCreate(gotLock, resBuilder,
					udid, facebookId, name);

			String userId = null;

			if (legitUserCreate) {
				//			  String newReferCode = grabNewReferCode();
				userId = writeChangeToDb(resBuilder, name, udid, cash, oil,
						gems, deviceToken, createTime, facebookId, email,
						fbData);
			}

			UserCreateResponseProto resProto = resBuilder.build();
			UserCreateResponseEvent resEvent = new UserCreateResponseEvent(udid);
			resEvent.setTag(event.getTag());
			resEvent.setUserCreateResponseProto(resProto);
			log.info(String.format("Writing event: %s", resEvent));
			server.writePreDBEvent(resEvent, udid);

			if (userId != null) {
				//recording that player is online I guess
				ConnectedPlayer player = server.getPlayerByUdId(udid);
				player.setPlayerId(userId);
				server.getPlayersByPlayerId().put(userId, player);
				server.getPlayersPreDatabaseByUDID().remove(udid);
			}

			if (legitUserCreate && userId != null) {
				/*server.lockPlayer(userId, this.getClass().getSimpleName());*/
				try {
					//TAKE INTO ACCOUNT THE PROPERTIES SENT IN BY CLIENT
					log.info("writing user structs");
					writeStructs(userId, createTime, structsJustBuilt);
					writeObstacles(userId);
					log.info("writing tasks");
					//			    writeTaskCompleted(userId, createTime);
					writeMonsters(userId, createTime, facebookId);
					writePvpStuff(userId, createTime);
					//			    LeaderBoardUtil leaderboard = AppContext.getApplicationContext().getBean(LeaderBoardUtil.class);
					//			    leaderboard.updateLeaderboardForUser(user);
					writeSecretGifts(userId, createTime);

					//CURRENCY CHANGE HISTORY
					writeToUserCurrencyHistory(userId, cash, oil, gems,
							createTime);

				} catch (Exception e) {
					log.error("exception in UserCreateController processEvent",
							e);
				} /*finally {
					server.unlockPlayer(userId, this.getClass().getSimpleName()); 
					}*/
			}
		} catch (Exception e) {
			log.error("exception in UserCreateController processEvent", e);
			try {
				resBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
				UserCreateResponseEvent resEvent = new UserCreateResponseEvent(
						udid);
				resEvent.setTag(event.getTag());
				resEvent.setUserCreateResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in UserCreateController processEvent", e);
			}
		} finally {
			if (gotLock) {
				getLocker().unlockFbId(fbId);
			}
		}
	}

	private boolean checkLegitUserCreate(boolean gotLock, Builder resBuilder,
			String udid, String facebookId, String name) {
		List<User> users = getUserRetrieveUtils().getUserByUDIDorFbId(udid,
				facebookId);

		if (!gotLock) {
			log.error("did not get fb lock. fbId=" + facebookId + ", udid="
					+ udid + ", name=" + name);
			return false;
		}

		if (null == users || users.isEmpty()) {
			return true;
		}

		if (null != facebookId && !facebookId.isEmpty()
				&& facebookIdExists(facebookId, users)) {
			//check if facebookId is tied to an account
			resBuilder
					.setStatus(UserCreateStatus.FAIL_USER_WITH_FACEBOOK_ID_EXISTS);
			log.error("user with facebookId " + facebookId
					+ " already exists. users=" + users);
			return false;
		}
		if (null != udid && !udid.isEmpty() && udidExists(udid, users)) {
			//udid shouldn't be empty
			//check if udid is tied to an account
			resBuilder
					.setStatus(UserCreateStatus.FAIL_USER_WITH_UDID_ALREADY_EXISTS);
			log.error("user with udid " + udid + " already exists");
			return false;
		}

		return true;
	}

	private boolean facebookIdExists(String facebookId, List<User> users) {
		for (User u : users) {
			String userFacebookId = u.getFacebookId();

			if (null != userFacebookId && userFacebookId.equals(facebookId)) {
				return true;
			}
		}
		return false;
	}

	private boolean udidExists(String udid, List<User> users) {
		for (User u : users) {
			String userUdid = u.getUdid();

			if (null != userUdid && userUdid.equals(udid)) {
				return true;
			}
		}
		return false;
	}

	private String writeChangeToDb(Builder resBuilder, String name,
			String udid, int cash, int oil, int gems, String deviceToken,
			Timestamp createTime, String facebookId, String email, String fbData) {
		//TODO: FIX THESE NUMBERS
		int lvl = ControllerConstants.USER_CREATE__START_LEVEL;
		int playerExp = 10;
		int avatarMonsterId = ControllerConstants.TUTORIAL__USER_STARTING_MONSTER_ID;

		String userId = insertUtils.insertUser(name, udid, lvl, playerExp,
				cash, oil, gems, false, deviceToken, createTime, facebookId,
				avatarMonsterId, email, fbData);

		if (userId != null) {
			/*server.lockPlayer(userId, this.getClass().getSimpleName());*//*
																			try {
																			user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
																			FullUserProto userProto = CreateInfoProtoUtils.createFullUserProtoFromUser(user);
																			resBuilder.setSender(userProto);
																			} catch (Exception e) {
																			log.error("exception in UserCreateController processEvent", e);
																			}*//*finally {
																				server.unlockPlayer(userId, this.getClass().getSimpleName()); 
																				}*/
		} else {
			resBuilder.setStatus(UserCreateStatus.FAIL_OTHER);
			log.error("problem with trying to create user. udid=" + udid
					+ ", name=" + name + ", deviceToken=" + deviceToken
					+ ", playerExp=" + playerExp + ", cash=" + cash + ", oil="
					+ oil + ", gems=" + gems);
		}

		log.info(String.format("created new userId=%s", userId));
		resBuilder.setStatus(UserCreateStatus.SUCCESS);
		return userId;
	}

	//THE VALUES AND STRUCTS TO GIVE THE USER
	private void writeStructs(String userId, Timestamp purchaseTime,
			List<TutorialStructProto> structsJustBuilt) {
		Date purchaseDate = new Date(purchaseTime.getTime());
		Date purchaseDateOneWeekAgo = getTimeUtils().createDateAddDays(
				purchaseDate, -7);
		Timestamp purchaseTimeOneWeekAgo = new Timestamp(
				purchaseDateOneWeekAgo.getTime());

		int[] buildingIds = ControllerConstants.TUTORIAL__EXISTING_BUILDING_IDS;
		float[] xPositions = ControllerConstants.TUTORIAL__EXISTING_BUILDING_X_POS;
		float[] yPositions = ControllerConstants.TUTORIAL__EXISTING_BUILDING_Y_POS;
		log.info("giving user buildings");
		int quantity = buildingIds.length;
		List<String> userIdList = new ArrayList<String>(Collections.nCopies(
				quantity, userId));
		List<Integer> structIdList = new ArrayList<Integer>();
		List<Float> xCoordList = new ArrayList<Float>();
		List<Float> yCoordList = new ArrayList<Float>();
		List<Timestamp> purchaseTimeList = new ArrayList<Timestamp>(
				Collections.nCopies(quantity, purchaseTime));
		List<Timestamp> retrievedTimeList = new ArrayList<Timestamp>(
				Collections.nCopies(quantity, purchaseTimeOneWeekAgo));
		List<Boolean> isComplete = new ArrayList<Boolean>(Collections.nCopies(
				quantity, true));

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

		int numInserted = InsertUtils.get().insertUserStructs(userIdList,
				structIdList, xCoordList, yCoordList, purchaseTimeList,
				retrievedTimeList, isComplete);
		log.info("num buildings given to user: " + numInserted);
	}

	private void writeObstacles(String userId) {
		List<ObstacleForUser> ofuList = getStructureStuffUtil()
				.createTutorialObstacleForUser(userId);
		log.info("inserting tutorial obstacles into obstacle_for_user");
		List<String> ofuIdList = InsertUtils.get()
				.insertIntoObstaclesForUserGetIds(userId, ofuList);

		log.info("tutorial ofuIdList=" + ofuIdList);
	}

	private void writeTaskCompleted(String userId, Timestamp createTime) {
		List<Integer> taskIdList = new ArrayList<Integer>();

		int cityId = ControllerConstants.TUTORIAL__CITY_ONE_ID;
		int assetIdOne = ControllerConstants.TUTORIAL__CITY_ONE_ASSET_NUM_FOR_FIRST_DUNGEON;
		int taskIdOne = taskRetrieveUtils.getTaskIdForCityElement(cityId,
				assetIdOne);
		taskIdList.add(taskIdOne);

		int assetIdTwo = ControllerConstants.TUTORIAL__CITY_ONE_ASSET_NUM_FOR_SECOND_DUNGEON;
		int taskIdTwo = taskRetrieveUtils.getTaskIdForCityElement(cityId,
				assetIdTwo);
		taskIdList.add(taskIdTwo);

		log.info("taskIdList: " + taskIdList);

		int size = taskIdList.size();
		List<String> userIdList = Collections.nCopies(size, userId);
		List<Timestamp> createTimeList = Collections.nCopies(size, createTime);

		int numInserted = InsertUtils.get().insertIntoTaskForUserCompleted(
				userIdList, taskIdList, createTimeList);
		log.info("num tasks inserted:" + numInserted + ", should be " + size);
	}

	private void writeMonsters(String userId, Timestamp createTime, String fbId) {
		String sourceOfPieces = ControllerConstants.MFUSOP__USER_CREATE;
		Date createDate = new Date(createTime.getTime());
		Date combineStartDate = getTimeUtils()
				.createDateAddDays(createDate, -7);

		List<Integer> monsterIds = new ArrayList<Integer>();
		monsterIds.add(ControllerConstants.TUTORIAL__USER_STARTING_MONSTER_ID);

		if (null != fbId && !fbId.isEmpty()) {
			log.info("awarding facebook zucker mucker burger.");
			monsterIds.add(ControllerConstants.TUTORIAL__MARK_Z_MONSTER_ID);
		}

		List<MonsterForUser> userMonsters = new ArrayList<MonsterForUser>();
		for (int i = 0; i < monsterIds.size(); i++) {
			int monsterId = monsterIds.get(i);
			int teamSlotNum = i + 1;

			Monster monzter = monsterRetrieveUtils
					.getMonsterForMonsterId(monsterId);
			Map<Integer, MonsterLevelInfo> info = monsterLevelInfoRetrieveUtils
					.getMonsterLevelInfoForMonsterId(monsterId);

			List<Integer> lvls = new ArrayList<Integer>(info.keySet());
			Collections.sort(lvls);
			int firstOne = lvls.get(0);
			MonsterLevelInfo mli = info.get(firstOne);

			MonsterForUser mfu = new MonsterForUser(null, userId,
					monsterId,
					0,//mli.getCurLvlRequiredExp(),
					mli.getLevel(), mli.getHp(), monzter.getNumPuzzlePieces(),
					true, true, combineStartDate, teamSlotNum, sourceOfPieces,
					false, monzter.getBaseOffensiveSkillId(),
					monzter.getBaseDefensiveSkillId());
			userMonsters.add(mfu);
		}
		List<String> ids = InsertUtils.get().insertIntoMonsterForUserReturnIds(
				userId, userMonsters, sourceOfPieces, combineStartDate);

		log.info("monsters inserted for userId=" + userId + ", ids=" + ids);
	}

	private void writePvpStuff(String userId, Timestamp createTime) {
		int elo = ControllerConstants.PVP__DEFAULT_MIN_ELO;
		int pvpLeagueId = ControllerConstants.PVP__INITIAL_LEAGUE_ID;
		List<PvpLeague> pvpLeagueList = pvpLeagueRetrieveUtils
				.getLeaguesForElo(elo);
		if (pvpLeagueList.size() > 1) {
			log.error("there are multiple leagues for initial elo: " + elo
					+ "\t leagues=" + pvpLeagueList + "\t choosing first one.");
		} else if (pvpLeagueList.isEmpty()) {
			log.error("no pvp league id for elo: " + elo);
		} else { //size is one
			pvpLeagueId = pvpLeagueList.get(0).getId();
		}

		int rank = pvpLeagueRetrieveUtils.getRankForElo(elo, pvpLeagueId);

		Date createDate = new Date(createTime.getTime());
		Date shieldEndDate = getTimeUtils().createDateAddDays(createDate,
				ControllerConstants.PVP__SHIELD_DURATION_DAYS);
		Timestamp shieldEndTime = new Timestamp(shieldEndDate.getTime());

		int numInsertedIntoPvp = InsertUtils.get().insertPvpLeagueForUser(
				userId, pvpLeagueId, rank, elo, shieldEndTime, shieldEndTime);
		log.info("numInsertedIntoPvp=" + numInsertedIntoPvp);

		//user put into hazelcast on server restart or he queue's up ppl
	}

	private void writeSecretGifts(String userId, Date createTime) {
		List<ItemSecretGiftForUser> gifts = new ArrayList<ItemSecretGiftForUser>();

		int[] itemIds = ControllerConstants.ITEM_SECRET_GIFT_FOR_USER__ITEM_IDS;
		int[] waitTimeSeconds = ControllerConstants.ITEM_SECRET_GIFT_FOR_USER__WAIT_TIMES_SECONDS;

		int len = itemIds.length;
		for (int index = 0; index < len; index++) {
			ItemSecretGiftForUser isgfu = new ItemSecretGiftForUser();
			isgfu.setUserId(userId);
			isgfu.setItemId(itemIds[index]);

			Date newTime = new Date(createTime.getTime() + index * 1000);
			isgfu.setSecsTillCollection(waitTimeSeconds[index]);
			isgfu.setCreateTime(newTime);

			gifts.add(isgfu);
		}

		insertUtils.insertIntoItemSecretGiftForUserGetId(gifts);
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

	//  private void rewardReferrer(User referrer, User user) {
	//    if (!referrer.isFake()) {
	//      getLocker().lockPlayer(referrer.getId(), this.getClass().getSimpleName());
	//      try {
	//        int previousSilver = referrer.getCash();
	//        
	//        int coinsGivenToReferrer = miscMethods.calculateCoinsGivenToReferrer(referrer);
	//        if (!referrer.updateRelativeCoinsNumreferrals(coinsGivenToReferrer, 1)) {
	//          log.error("problem with rewarding the referrer " + referrer + " with this many coins: " + coinsGivenToReferrer);
	//        } else {
	//          if (!insertUtils.insertReferral(referrer.getId(), user.getId(), coinsGivenToReferrer)) {
	//            log.error("problem with inserting referral into db. referrer is " + referrer.getId() + ", user=" + user.getId()
	//                + ", coins given to referrer=" + coinsGivenToReferrer);
	//          }
	//          ReferralCodeUsedResponseEvent resEvent = new ReferralCodeUsedResponseEvent(referrer.getId());
	//          ReferralCodeUsedResponseProto resProto = ReferralCodeUsedResponseProto.newBuilder()
	//              .setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(referrer, null))
	//              .setReferredPlayer(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null))
	//              .setCoinsGivenToReferrer(coinsGivenToReferrer).build();
	//          resEvent.setReferralCodeUsedResponseProto(resProto);
	//          server.writeAPNSNotificationOrEvent(resEvent);
	//          
	//          writeToUserCurrencyHistoryTwo(referrer, coinsGivenToReferrer, previousSilver);
	//        }
	//      } catch (Exception e) {
	//        log.error("exception in UserCreateController processEvent", e);
	//      } finally {
	//        getLocker().unlockPlayer(referrer.getId(), this.getClass().getSimpleName()); 
	//      }
	//    }
	//  }

	private void writeToUserCurrencyHistory(String userId, int cash, int oil,
			int gems, Timestamp createTime) {
		String reasonForChange = ControllerConstants.UCHRFC__USER_CREATED;

		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gemsStr = miscMethods.gems;
		String cashStr = miscMethods.cash;
		String oilStr = miscMethods.oil;

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

		miscMethods.writeToUserCurrencyOneUser(userId, createTime,
				currentCurrency, previousCurrency, currentCurrency,
				reasonsForChanges, detailsMap);

	}

	//TODO: FIX THIS
	public void writeToUserCurrencyHistoryTwo(User aUser, int coinChange,
			int previousSilver) {
		//    Timestamp date = new Timestamp((new Date()).getTime());
		//
		//    Map<String, Integer> goldSilverChange = new HashMap<String, Integer>();
		//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
		//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
		//    String silver = miscMethods.cash;
		//    String reasonForChange = ControllerConstants.UCHRFC__USER_CREATE_REFERRED_A_USER;
		//    
		//    goldSilverChange.put(silver, coinChange);
		//    previousGoldSilver.put(silver, previousSilver);
		//    reasonsForChanges.put(silver, reasonForChange);
		//    
		//    miscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, goldSilverChange,
		//        previousGoldSilver, reasonsForChanges);
	}

	public void setInsertUtils(InsertUtil insertUtils) {
		this.insertUtils = insertUtils;
	}

	protected Locker getLocker() {
		return locker;
	}

	protected void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public StructureStuffUtil getStructureStuffUtil() {
		return structureStuffUtil;
	}

	public void setStructureStuffUtil(StructureStuffUtil structureStuffUtil) {
		this.structureStuffUtil = structureStuffUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}
}
