package com.lvl6.server.controller;

import com.lvl6.proto.RewardsProto.UserSecretGiftProto;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.SecretGiftForUserRetrieveUtil;
//import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;

//@Component

public class StartupControllerOld {//extends EventController {
	//  private static String nameRulesFile = "namerulesElven.txt";
	//  private static int syllablesInName1 = 2;
	//  private static int syllablesInName2 = 3;
/*
	private static Logger log = LoggerFactory.getLogger(StartupControllerOld.class);

	@Resource(name = "goodEquipsRecievedFromBoosterPacks")
	protected IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks;

	public IList<RareBoosterPurchaseProto> getGoodEquipsRecievedFromBoosterPacks() {
		return goodEquipsRecievedFromBoosterPacks;
	}

	public void setGoodEquipsRecievedFromBoosterPacks(
			IList<RareBoosterPurchaseProto> goodEquipsRecievedFromBoosterPacks) {
		this.goodEquipsRecievedFromBoosterPacks = goodEquipsRecievedFromBoosterPacks;
	}

	@Resource(name = "globalChat")
	protected IList<GroupChatMessageProto> chatMessages;

	public IList<GroupChatMessageProto> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(IList<GroupChatMessageProto> chatMessages) {
		this.chatMessages = chatMessages;
	}

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected StartupStuffRetrieveUtils startupStuffRetrieveUtils;

	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils;

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils;

	@Autowired
	protected Locker locker;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected InAppPurchaseUtils inAppPurchaseUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected Globals globals;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected QuestForUserRetrieveUtils2 questForUserRetrieveUtils;

	@Autowired
	protected QuestJobForUserRetrieveUtil questJobForUserRetrieveUtil;

	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil;

	@Autowired
	protected AchievementForUserRetrieveUtil achievementForUserRetrieveUtil;

	@Autowired
	protected TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil;

	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected ItemForUserUsageRetrieveUtil itemForUserUsageRetrieveUtil;

	@Autowired
	protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;

	@Autowired
	protected ClanAvengeRetrieveUtil clanAvengeRetrieveUtil;

	@Autowired
	protected ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil;

	@Autowired
	protected BattleItemQueueForUserRetrieveUtil battleItemQueueForUserRetrieveUtil;

	@Autowired
	protected BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;

	@Autowired
	protected SecretGiftUtils secretGiftUtils;

	@Autowired
	protected MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtils;

	@Autowired
	protected MonsterHealingForUserRetrieveUtils2 monsterHealingForUserRetrieveUtils;

	@Autowired
	protected MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtils;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	@Autowired
	protected TaskForUserCompletedRetrieveUtils taskForUserCompletedRetrieveUtils;

	@Autowired
	protected TaskForUserOngoingRetrieveUtils2 taskForUserOngoingRetrieveUtils;

	@Autowired
	protected TaskForUserClientStateRetrieveUtil taskForUserClientStateRetrieveUtil;

	@Autowired
	protected TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtils;

	@Autowired
	protected EventPersistentForUserRetrieveUtils2 eventPersistentForUserRetrieveUtils;

	@Autowired
	protected PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil;

	@Autowired
	protected PvpBattleForUserRetrieveUtils2 pvpBattleForUserRetrieveUtils;

	@Autowired
	protected IAPHistoryRetrieveUtils iapHistoryRetrieveUtils;

	@Autowired
	protected ClanEventPersistentForClanRetrieveUtils2 clanEventPersistentForClanRetrieveUtils;

	@Autowired
	protected ClanEventPersistentForUserRetrieveUtils2 clanEventPersistentForUserRetrieveUtils;

	@Autowired
	protected CepfuRaidStageHistoryRetrieveUtils2 cepfuRaidStageHistoryRetrieveUtils;

	@Autowired
	protected ClanEventPersistentUserRewardRetrieveUtils2 clanEventPersistentUserRewardRetrieveUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils;

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils;

	@Autowired
	protected PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils;

	@Autowired
	protected SecretGiftForUserRetrieveUtil secretGiftForUserRetrieveUtil;

	@Autowired
	protected ResearchForUserRetrieveUtils researchForUserRetrieveUtil;

	@Autowired
	protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil;

	@Autowired
	protected MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil;

	@Autowired
	protected MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil;

	@Autowired
	protected GiftForUserRetrieveUtils giftForUserRetrieveUtil;

	@Autowired
	protected GiftForTangoUserRetrieveUtil giftForTangoUserRetrieveUtil;

	@Autowired
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Autowired
	protected MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils;

	@Autowired
	protected MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils;

	@Autowired
	protected MiniEventRetrieveUtils miniEventRetrieveUtils;

	@Autowired
	protected MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils;

	@Autowired
	protected MiniEventLeaderboardRewardRetrieveUtils miniEventLeaderboardRewardRetrieveUtils;

	@Autowired
	protected SalesPackageRetrieveUtils salesPackageRetrieveUtils;

	@Autowired
	protected SalesItemRetrieveUtils salesItemRetrieveUtils;

    @Autowired
    protected SalesDisplayItemRetrieveUtils salesDisplayItemRetrieveUtils;

    @Autowired
    protected CustomMenuRetrieveUtils customMenuRetrieveUtils;

    @Autowired
    protected RewardRetrieveUtils rewardRetrieveUtil;

    @Autowired
    protected BattleReplayForUserRetrieveUtil battleReplayForUserRetrieveUtil;

	public StartupControllerOld() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new StartupRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_STARTUP_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		StartupRequestProto reqProto = ((StartupRequestEvent) event)
				.getStartupRequestProto();
		log.info("Processing startup request reqProto:{}",
				reqProto);
		UpdateStatus updateStatus;
		String udid = reqProto.getUdid();
		String apsalarId = reqProto.hasApsalarId() ? reqProto.getApsalarId()
				: null;

		String playerId = null;
		miscMethods.setMDCProperties(udid, null,
				miscMethods.getIPOfPlayer(server, null, udid));
		log.info("{}ms at getIpOfPlayer", stopWatch.getTime());

		VersionNumberProto version = null;

		if (reqProto.hasVersionNumberProto()) {
			version = reqProto.getVersionNumberProto();
		}

		if (null != version) {
			updateStatus = checkVersion(version);

		} else {
			updateStatus = checkVersionNoVersionProto(reqProto);

		}

		Builder resBuilder = StartupResponseProto.newBuilder();
		resBuilder.setUpdateStatus(updateStatus);
		resBuilder.setAppStoreURL(Globals.APP_STORE_URL());
		resBuilder.setReviewPageURL(Globals.REVIEW_PAGE_URL());
		resBuilder
				.setReviewPageConfirmationMessage(Globals.REVIEW_PAGE_CONFIRMATION_MESSAGE);
		StartupResponseEvent resEvent = new StartupResponseEvent(udid);
		resEvent.setTag(event.getTag());

		// Don't fill in other fields if it is a major update
		StartupStatus startupStatus = StartupStatus.USER_NOT_IN_DB;

		Date nowDate = new Date();
		nowDate = getTimeUtils().createDateTruncateMillis(nowDate);
		Timestamp now = new Timestamp(nowDate.getTime());
		User user = null;
		String fbId = reqProto.getFbId();
		boolean freshRestart = reqProto.getIsFreshRestart();

		int newNumConsecutiveDaysLoggedIn = 0;
		log.info("{}ms at start of logic", stopWatch.getTime());
		try {
			//TODO: GET RID OF "updateStatus != UpdateStatus.MINOR_UPDATE".
			//Only here because of bug with client's need to not force update
			if (updateStatus != UpdateStatus.MAJOR_UPDATE) {
				List<User> users = getUserRetrieveUtils().getUserByUDIDorFbId(
						udid, fbId);
				user = selectUser(users, udid, fbId);//RetrieveUtils.userRetrieveUtils().getUserByUDID(udid);

				boolean isLogin = true;
				boolean goingThroughTutorial = false;
				boolean userIdSet = true;

				if (user != null) {
					playerId = user.getId();
					//if can't lock player, exception will be thrown
					getLocker().lockPlayer(UUID.fromString(playerId),
							this.getClass().getSimpleName());
					log.info("{}ms at got lock", stopWatch.getTime());
					startupStatus = StartupStatus.USER_IN_DB;
					log.info("No major update... getting user info");

					if(user.getSegmentationGroup() == 0) {
						UserSegmentationGroupAction usga = new UserSegmentationGroupAction(playerId, user);
						usga.convertUserIdIntoInt();
						int segmentationGroup = usga.getSegmentationGroup();
						if(!user.updateUserSegmentationGroup(segmentationGroup)) {
							log.error("something wrong with updating user's segmentation group value:{}",segmentationGroup);
						}
					}

					loginExistingUser(stopWatch, udid, playerId, resBuilder,
							nowDate, now, user, fbId, freshRestart);

				} else {
					log.info(String
							.format("tutorial player with udid=%s", udid));

					goingThroughTutorial = true;
					userIdSet = false;
					tutorialUserAccounting(reqProto, udid, now);
				}

				InsertUtils.get().insertIntoLoginHistory(udid, playerId, now,
						isLogin, goingThroughTutorial);
				log.info("{}ms at InsertIntoLoginHistory", stopWatch.getTime());

				setAllStaticData(resBuilder, playerId, userIdSet);
				log.info("{}ms at static data", stopWatch.getTime());

				resBuilder.setStartupStatus(startupStatus);
				setConstants(resBuilder, startupStatus);
			}
		} catch (Exception e) {
			log.error("exception in StartupController processEvent", e);
			//don't let the client hang
			try {
				resBuilder
						.setStartupStatus(StartupStatus.SERVER_IN_MAINTENANCE); //DO NOT allow user to play
				//				resEvent = new StartupResponseEvent(udid);
				//				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				getEventWriter().processPreDBResponseEvent(resEvent, udid);
			} catch (Exception e2) {
				log.error("exception2 in StartupController processEvent", e);
			}
		}


		if (Globals.KABAM_ENABLED()) {
		String naid = retrieveKabamNaid(user, udid, reqProto.getMacAddress(),
		  reqProto.getAdvertiserId());
		resBuilder.setKabamNaid(naid);
		}

		//startup time
		resBuilder.setServerTimeMillis((new Date()).getTime());
		//		resEvent = new StartupResponseEvent(udid);
		//		resEvent.setTag(event.getTag());

		resEvent.setResponseProto(resBuilder.build());

		log.debug("Writing event response: " + resEvent);
		server.writePreDBEvent(resEvent, udid);
		log.debug("Wrote response event: " + resEvent);
		// for things that client doesn't need
		log.debug("After response tasks");

		// if app is not in force tutorial execute this function,
		// regardless of whether the user is new or restarting from an account
		// reset
		//UPDATE USER's LAST LOGIN
		updateLeaderboard(apsalarId, user, now, newNumConsecutiveDaysLoggedIn);
		//log.info("user after change user login via db. user=" + user);
	}

	protected UpdateStatus checkVersionNoVersionProto(StartupRequestProto reqProto) {
		UpdateStatus updateStatus;
		double tempClientVersionNum = reqProto.getVersionNum() * 10D;
		double tempLatestVersionNum = GameServer.clientVersionNumber * 10D;
		// Check version number
		if ((int) tempClientVersionNum < (int) tempLatestVersionNum) {
			updateStatus = UpdateStatus.MAJOR_UPDATE;
			log.info("player has been notified of forced update");
		} else if (tempClientVersionNum < tempLatestVersionNum) {
			updateStatus = UpdateStatus.MINOR_UPDATE;
		} else {
			updateStatus = UpdateStatus.NO_UPDATE;
		}
		return updateStatus;
	}

	protected UpdateStatus checkVersion(VersionNumberProto version) {
		UpdateStatus updateStatus;
		int superNum = version.getSuperNum();
		int majorNum = version.getMajorNum();
		int minorNum = version.getMinorNum();

		int serverSuperNum = Globals.VERSION_SUPER_NUMBER();
		int serverMajorNum = Globals.VERSION_MAJOR_NUMBER();
		int serverMinorNum = Globals.VERSION_MINOR_NUMBER();

		boolean clientSuperGreater = superNum > serverSuperNum;
		boolean clientSuperEqual = superNum == serverSuperNum;

		boolean clientMajorGreater = majorNum > serverMajorNum;
		boolean clientMajorEqual = majorNum == serverMajorNum;

		boolean clientMinorGreater = minorNum > serverMinorNum;

		if (clientSuperGreater
				|| (clientSuperEqual && clientMajorGreater)
				|| (clientSuperEqual && clientMajorEqual && clientMinorGreater)) {
			String preface = "CLIENT AND SERVER VERSION'S ARE OFF.";
			log.error(
					"{} clientVersion={}.{}.{} \t serverVersion={}.{}.{}",
					new Object[] { preface, superNum, majorNum, minorNum,
							serverSuperNum, serverMajorNum, serverMinorNum });

			updateStatus = UpdateStatus.NO_UPDATE;
		}

		if (superNum < serverSuperNum || majorNum < serverMajorNum) {
			updateStatus = UpdateStatus.MAJOR_UPDATE;
			log.info("player has been notified of forced update");

		} else if (minorNum < serverMinorNum) {
			updateStatus = UpdateStatus.MINOR_UPDATE;
			log.info("player has been notified of minor update");
		} else {
			updateStatus = UpdateStatus.NO_UPDATE;
		}
		return updateStatus;
	}

	//priority of user returned is
	//user with specified fbId
	//user with specified udid
	//null
	private User selectUser(List<User> users, String udid, String fbId) {
		int numUsers = users.size();
		if (numUsers > 2) {
			log.error(String
					.format("more than 2 users with same udid, fbId. udid=%s, fbId=%s, users=%s",
							udid, fbId, users));
		}
		if (1 == numUsers) {
			return users.get(0);
		}

		User udidUser = null;

		for (User u : users) {
			String userFbId = u.getFacebookId();
			String userUdid = u.getUdid();

			if (fbId != null && fbId.equals(userFbId)) {
				return u;
			} else if (null == udidUser && udid != null
					&& udid.equals(userUdid)) {
				//so this is the first user with specified udid, don't change reference
				//to this user once set
				udidUser = u;
			}
		}

		//didn't find user with specified fbId
		return udidUser;
	}

	private void tutorialUserAccounting(StartupRequestProto reqProto,
			String udid, Timestamp now) {
		boolean userLoggedIn = LoginHistoryRetrieveUtils
				.userLoggedInByUDID(udid);
		int numOldAccounts = getUserRetrieveUtils().numAccountsForUDID(udid);
		boolean alreadyInFirstTimeUsers = FirstTimeUsersRetrieveUtils
				.userExistsWithUDID(udid);
		boolean isFirstTimeUser = false;
		if (!userLoggedIn && 0 >= numOldAccounts && !alreadyInFirstTimeUsers) {
			isFirstTimeUser = true;
		}

		log.info("\n userLoggedIn=" + userLoggedIn + "\t numOldAccounts="
				+ numOldAccounts + "\t alreadyInFirstTimeUsers="
				+ alreadyInFirstTimeUsers + "\t isFirstTimeUser="
				+ isFirstTimeUser);

		if (isFirstTimeUser) {
			log.info("new player with udid {}", udid);
			InsertUtils.get().insertIntoFirstTimeUsers(udid, null,
					reqProto.getMacAddress(), reqProto.getAdvertiserId(), now);
		}

		if (Globals.OFFERCHART_ENABLED() && isFirstTimeUser) {
			sendOfferChartInstall(now, reqProto.getAdvertiserId());
		}
	}

	private void loginExistingUser(StopWatch stopWatch, String udid,
			String playerId, Builder resBuilder, Date nowDate, Timestamp now,
			User user, String fbId, boolean freshRestart) {
		try {
			//force other devices on this account to logout
			forceLogOutOthers(stopWatch, udid, playerId, user, fbId);

			//Doesn't send a read request to user table
			log.info("No major update... getting user info");
			//          newNumConsecutiveDaysLoggedIn = setDailyBonusInfo(resBuilder, user, now);
			setInProgressAndAvailableQuests(resBuilder, playerId);
			log.info("{}ms at setInProgressAndAvailableQuests",
					stopWatch.getTime());
			setUserClanInfos(resBuilder, playerId);
			log.info("{}ms at setUserClanInfos", stopWatch.getTime());
			setNoticesToPlayers(resBuilder);
			log.info("{}ms at setNoticesToPlayers", stopWatch.getTime());
			setUserMonsterStuff(resBuilder, playerId);
			log.info("{}ms at setUserMonsterStuff", stopWatch.getTime());
			setBoosterPurchases(resBuilder);
			log.info("{}ms at boosterPurchases", stopWatch.getTime());
			setTaskStuff(resBuilder, playerId);
			log.info("{}ms at task stuff", stopWatch.getTime());
			setEventStuff(resBuilder, playerId);
			log.info("{}ms at eventStuff", stopWatch.getTime());
			//if server sees that the user is in a pvp battle, decrement user's elo
			setPvpBoardObstacles(resBuilder, playerId);
			PvpLeagueForUser plfu = pvpBattleStuff(resBuilder, user, playerId,
					freshRestart, now);
			log.info("{}ms at pvpBattleStuff", stopWatch.getTime());
			setAchievementStuff(resBuilder, playerId, user, now, stopWatch);
			log.info("{}ms at achivementStuff", stopWatch.getTime());
			setMiniJob(resBuilder, playerId);
			log.info("{}ms at miniJobStuff", stopWatch.getTime());
			setUserItems(stopWatch, resBuilder, user, playerId);
			log.info("{}ms at setUserItems", stopWatch.getTime());
			setWhetherPlayerCompletedInAppPurchase(resBuilder, user);
			log.info("{}ms at whetherCompletedInAppPurchase",
					stopWatch.getTime());
			setSecretGifts(resBuilder, playerId, now.getTime());
			log.info("{}ms at setSecretGifts", stopWatch.getTime());
			setResearch(resBuilder, playerId);
			log.info("{}ms at setResearch", stopWatch.getTime());
			setBattleItemForUser(resBuilder, playerId);
			log.info("{}ms at setBattleItemForUser", stopWatch.getTime());
			setBattleItemQueueForUser(resBuilder, playerId);
			log.info("{}ms at setBattleItemQueueForUser", stopWatch.getTime());
			setSalesForUser(resBuilder, user);
			log.info("{}ms at setSalesForuser", stopWatch.getTime());
			setMiniEventForUser(resBuilder, user, playerId, nowDate);
			log.info("{}ms at setMiniEventForUser", stopWatch.getTime());


			//db request for user monsters
			setClanRaidStuff(resBuilder, user, playerId, now); //NOTE: This sends a read query to monster_for_user table
			log.info("{}ms at clanRaidStuff", stopWatch.getTime());

			SetGlobalChatMessageAction sgcma = new SetGlobalChatMessageAction(
					resBuilder, user, chatMessages);
			sgcma.execute();

			//fill up with userIds, and other ids to fetch from tables
			StartUpResource fillMe = new StartUpResource(
					getUserRetrieveUtils(), getClanRetrieveUtils());

			// For creating the full user
			fillMe.addUserId(user.getId());

			//get translationsettingforuser list of the player to check for defaults
			List<TranslationSettingsForUser> tsfuList = translationSettingsForUserRetrieveUtil.
					getUserTranslationSettingsForUser(playerId);
			boolean tsfuListIsNull = false;

			if(tsfuList == null || tsfuList.isEmpty()) {
				insertUtil.insertTranslateSettings(playerId, null,
						ControllerConstants.TRANSLATION_SETTINGS__DEFAULT_LANGUAGE,
						ChatScope.GLOBAL.toString(),
						ControllerConstants.TRANSLATION_SETTINGS__DEFAULT_TRANSLATION_ON);
				tsfuListIsNull = true;
			}

			List<TranslationSettingsForUser> updatedTsfuList = translationSettingsForUserRetrieveUtil.
					getUserTranslationSettingsForUser(playerId);

			SetPrivateChatMessageAction spcma = new SetPrivateChatMessageAction(
					resBuilder, user, playerId,
					privateChatPostRetrieveUtils, tsfuListIsNull, insertUtil,
					createInfoProtoUtils, translationSettingsForUserRetrieveUtil,
					updatedTsfuList);
			spcma.setUp(fillMe);
			log.info("{}ms at privateChatPosts", stopWatch.getTime());

			SetFacebookExtraSlotsAction sfesa = new SetFacebookExtraSlotsAction(
					resBuilder, user, playerId,
					userFacebookInviteForSlotRetrieveUtils,
					createInfoProtoUtils);
			sfesa.setUp(fillMe);
			log.info("{}ms at facebookAndExtraSlotsStuff", stopWatch.getTime());

			SetPvpBattleHistoryAction spbha = new SetPvpBattleHistoryAction(
					resBuilder, user, playerId, pvpBattleHistoryRetrieveUtil,
					monsterForUserRetrieveUtils, clanRetrieveUtils,
					hazelcastPvpUtil, monsterStuffUtils, createInfoProtoUtils,
					serverToggleRetrieveUtil, monsterLevelInfoRetrieveUtils,
					battleReplayForUserRetrieveUtil);
			spbha.setUp(fillMe);
			log.info("{}ms at pvpBattleHistoryStuff", stopWatch.getTime());

			//CLAN DATA
			ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();
			SetClanChatMessageAction sccma = new SetClanChatMessageAction(cdpb,
					user, clanChatPostRetrieveUtils,
					createInfoProtoUtils);
			sccma.setUp(fillMe);
			log.info("{}ms at setClanChatMessages", stopWatch.getTime());

			SetClanHelpingsAction scha = new SetClanHelpingsAction(cdpb, user,
					playerId, clanHelpRetrieveUtil, createInfoProtoUtils);
			scha.setUp(fillMe);
			log.info("{}ms at setClanHelpings", stopWatch.getTime());

			SetClanRetaliationsAction scra = new SetClanRetaliationsAction(
					cdpb, user, playerId, clanAvengeRetrieveUtil,
					clanAvengeUserRetrieveUtil, createInfoProtoUtils);
			scra.setUp(fillMe);
			log.info("{}ms at setClanRetaliations", stopWatch.getTime());

			SetClanMemberTeamDonationAction scmtda = new SetClanMemberTeamDonationAction(
					cdpb, user, playerId, clanMemberTeamDonationRetrieveUtil,
					monsterSnapshotForUserRetrieveUtil, createInfoProtoUtils);
			scmtda.setUp(fillMe);
			log.info("{}ms at setClanMemberTeamDonation", stopWatch.getTime());

			//not sure if need clan so putting here for now
			SetGiftsAction sga = new SetGiftsAction(resBuilder, user, playerId,
					giftForUserRetrieveUtil, giftForTangoUserRetrieveUtil,
					giftRetrieveUtil, rewardRetrieveUtil, createInfoProtoUtils);
			sga.setUp(fillMe);
			log.info("{}ms at SetGiftsAction", stopWatch.getTime());


			//Now since all the ids of resources are known, get them from db
			fillMe.fetch();
			log.info("{}ms at fillMe.fetch()", stopWatch.getTime());

			spcma.execute(fillMe);
			log.info("{}ms at privateChatPosts", stopWatch.getTime());

			//set this proto after executing privatechatprotos
			setDefaultLanguagesForUser(resBuilder, playerId);
			log.info("{}ms at setDefaultLanguagesForUser", stopWatch.getTime());


			sfesa.execute(fillMe);
			log.info("{}ms at facebookAndExtraSlotsStuff", stopWatch.getTime());
			spbha.execute(fillMe);
			log.info("{}ms at pvpBattleHistoryStuff", stopWatch.getTime());

			sccma.execute(fillMe);
			log.info("{}ms at setClanChatMessages", stopWatch.getTime());
			scha.execute(fillMe);
			log.info("{}ms at setClanHelpings", stopWatch.getTime());
			scra.execute(fillMe);
			log.info("{}ms at setClanRetaliations", stopWatch.getTime());
			scmtda.execute(fillMe);
			log.info("{}ms at setClanMemberTeamDonation", stopWatch.getTime());
			sga.execute(fillMe);;
			log.info("{}ms at setGifts", stopWatch.getTime());

			resBuilder.setClanData(cdpb.build());
			//TODO: DELETE IN FUTURE. This is for legacy client
			resBuilder.addAllClanChats(cdpb.getClanChatsList());
			resBuilder.addAllClanHelpings(cdpb.getClanHelpingsList());

			//          setLeaderboardEventStuff(resBuilder);

			//OVERWRITE THE LASTLOGINTIME TO THE CURRENT TIME
			//log.info("before last login change, user=" + user);
			user.setLastLogin(nowDate);
			//log.info("after last login change, user=" + user);

			Clan clan = null;
			if (user.getClanId() != null) {
				clan = fillMe.getClanIdsToClans().get(user.getClanId());
			}
			FullUserProto fup = createInfoProtoUtils
					.createFullUserProtoFromUser(user, plfu, clan);
			//log.info("fup=" + fup);
			resBuilder.setSender(fup);


		} catch (Exception e) {
			log.error("exception in StartupController processEvent", e);
		} finally {
			getLocker().unlockPlayer(UUID.fromString(playerId),
					this.getClass().getSimpleName());
			log.info("{}ms at unlock", stopWatch.getTime());
		}
	}

	private void forceLogOutOthers(StopWatch stopWatch, String udid,
			String playerId, User user, String fbId) {
		ForceLogoutResponseProto.Builder logoutResponse = ForceLogoutResponseProto
				.newBuilder();
		//login value before it is overwritten with current time
		logoutResponse.setPreviousLoginTime(user.getLastLogin().getTime());
		logoutResponse.setUdid(udid);
		ForceLogoutResponseEvent logoutEvent = new ForceLogoutResponseEvent(
				playerId);
		logoutEvent.setForceLogoutResponseProto(logoutResponse.build());
		//to take care of two devices using the same udid amqp queue (not very common)
		//only if a device is already on and then another one comes on and somehow
		//switches to the existing user account, no fbId though
		getEventWriter().processPreDBResponseEvent(logoutEvent, udid);
		log.info("{}ms at processPreDBResponseEvent", stopWatch.getTime());
		//to take care of one device already logged on (lot more common than above)
		getEventWriter().handleEvent(logoutEvent);
		//to take care of both the above, but when user is logged in via facebook id
		if (null != fbId && !fbId.isEmpty()) {
			getEventWriter().processPreDBFacebookEvent(logoutEvent, fbId);
		}
	}

	private void setInProgressAndAvailableQuests(Builder resBuilder,
			String userId) {
		NOTE: DB CALL
		List<QuestForUser> inProgressAndRedeemedUserQuests = getQuestForUserRetrieveUtils()
				.getUserQuestsForUser(userId);

		if (null == inProgressAndRedeemedUserQuests
				|| inProgressAndRedeemedUserQuests.isEmpty()) {
			return;
		}

		//  	  log.info("user quests: " + inProgressAndRedeemedUserQuests);

		List<QuestForUser> inProgressQuests = new ArrayList<QuestForUser>();
		Set<Integer> inProgressQuestsIds = new HashSet<Integer>();
		List<Integer> redeemedQuestIds = new ArrayList<Integer>();

		Map<Integer, Quest> questIdToQuests = questRetrieveUtils
				.getQuestIdsToQuests();
		for (QuestForUser uq : inProgressAndRedeemedUserQuests) {
			int questId = uq.getQuestId();

			if (!uq.isRedeemed()) {
				//unredeemed quest section
				inProgressQuests.add(uq);
				inProgressQuestsIds.add(questId);

			} else {
				redeemedQuestIds.add(questId);
			}
		}

		// get the QuestJobForUser for ONLY the inProgressQuests
		NOTE: DB CALL
		Map<Integer, Collection<QuestJobForUser>> questIdToUserQuestJobs = getQuestJobForUserRetrieveUtil()
				.getSpecificOrAllQuestIdToQuestJobsForUserId(userId, inProgressQuestsIds);

		//generate the user quests
		List<FullUserQuestProto> currentUserQuests = createInfoProtoUtils
				.createFullUserQuestDataLarges(inProgressQuests,
						questIdToQuests, questIdToUserQuestJobs);
		resBuilder.addAllUserQuests(currentUserQuests);

		//generate the redeemed quest ids
		resBuilder.addAllRedeemedQuestIds(redeemedQuestIds);
	}

	private void setUserClanInfos(StartupResponseProto.Builder resBuilder,
			String userId) {
		NOTE: DB CALL
		List<UserClan> userClans = getUserClanRetrieveUtils()
				.getUserClansRelatedToUser(userId);
		for (UserClan uc : userClans) {
			resBuilder.addUserClanInfo(createInfoProtoUtils
					.createFullUserClanProtoFromUserClan(uc));
		}
	}

	private void setNoticesToPlayers(Builder resBuilder) {
		NOTE: DB CALL
		List<String> notices = startupStuffRetrieveUtils.getAllActiveAlerts();
		if (null != notices) {
			for (String notice : notices) {
				resBuilder.addNoticesToPlayers(notice);
			}
		}

	}

	private void setUserMonsterStuff(Builder resBuilder, String userId) {
		NOTE: DB CALL
		List<MonsterForUser> userMonsters = monsterForUserRetrieveUtils
				.getMonstersForUser(userId);

		if (null != userMonsters && !userMonsters.isEmpty()) {
			for (MonsterForUser mfu : userMonsters) {
				FullUserMonsterProto fump = createInfoProtoUtils
						.createFullUserMonsterProtoFromUserMonster(mfu);
				resBuilder.addUsersMonsters(fump);
			}
		}

		NOTE: DB CALL
		//monsters in healing
		Map<String, MonsterHealingForUser> userMonstersHealing = monsterHealingForUserRetrieveUtils
				.getMonstersForUser(userId);
		if (null != userMonstersHealing && !userMonstersHealing.isEmpty()) {

			Collection<MonsterHealingForUser> healingMonsters = userMonstersHealing
					.values();
			for (MonsterHealingForUser mhfu : healingMonsters) {
				UserMonsterHealingProto umhp = createInfoProtoUtils
						.createUserMonsterHealingProtoFromObj(mhfu);
				resBuilder.addMonstersHealing(umhp);
			}
		}

		NOTE: DB CALL
		//enhancing monsters
		Map<String, MonsterEnhancingForUser> userMonstersEnhancing = monsterEnhancingForUserRetrieveUtils
				.getMonstersForUser(userId);
		if (null != userMonstersEnhancing && !userMonstersEnhancing.isEmpty()) {
			//find the monster that is being enhanced
			Collection<MonsterEnhancingForUser> enhancingMonsters = userMonstersEnhancing
					.values();
			UserEnhancementItemProto baseMonster = null;

			List<String> feederUserMonsterIds = new ArrayList<String>();
			List<UserEnhancementItemProto> feederProtos = new ArrayList<UserEnhancementItemProto>();
			for (MonsterEnhancingForUser mefu : enhancingMonsters) {
				UserEnhancementItemProto ueip = createInfoProtoUtils
						.createUserEnhancementItemProtoFromObj(mefu);

				//TODO: if user has no monsters with null start time
				//(if user has all monsters with a start time), or if user has more than one
				//monster with a null start time
				//STORE THEM AND DELETE THEM OR SOMETHING

				//search for the monster that is being enhanced, the one with null start time
				Date startTime = mefu.getExpectedStartTime();
				if (null == startTime) {
					//found him
					baseMonster = ueip;
				} else {
					//just a feeder, add him to the list
					feederProtos.add(ueip);
					feederUserMonsterIds.add(mefu.getMonsterForUserId());
				}
			}

			if (null == baseMonster) {
				log.error(
						"no base monster enhancement. deleting inEnhancing={}",
						enhancingMonsters);
				deleteOrphanedEnhancements(userId, feederUserMonsterIds);
			} else {
				UserEnhancementProto uep = createInfoProtoUtils
						.createUserEnhancementProtoFromObj(userId, baseMonster,
								feederProtos);

				resBuilder.setEnhancements(uep);
			}
		}

		NOTE: DB CALL
		//evolving monsters
		Map<String, MonsterEvolvingForUser> userMonsterEvolving = getMonsterEvolvingForUserRetrieveUtils()
				.getCatalystIdsToEvolutionsForUser(userId);
		if (null != userMonsterEvolving && !userMonsterEvolving.isEmpty()) {

			for (MonsterEvolvingForUser mefu : userMonsterEvolving.values()) {
				UserMonsterEvolutionProto uep = createInfoProtoUtils
						.createUserEvolutionProtoFromEvolution(mefu);

				//TODO: NOTE THAT IF MORE THAN ONE EVOLUTION IS ALLLOWED AT A TIME, THIS METHOD
				//CALL NEEDS TO CHANGE
				resBuilder.setEvolution(uep);
			}
		}
	}

	private void deleteOrphanedEnhancements(String userId,
			List<String> feederUserMonsterIds) {
		try {
			int numDeleted = DeleteUtils.get().deleteMonsterEnhancingForUser(
					userId, feederUserMonsterIds);
			log.info("numDeleted enhancements: {}", numDeleted);
		} catch (Exception e) {
			log.error("unable to delete orphaned enhancements", e);
		}
	}

	private void setBoosterPurchases(StartupResponseProto.Builder resBuilder) {
		Iterator<RareBoosterPurchaseProto> it = goodEquipsRecievedFromBoosterPacks
				.iterator();
		List<RareBoosterPurchaseProto> boosterPurchases = new ArrayList<RareBoosterPurchaseProto>();
		while (it.hasNext()) {
			boosterPurchases.add(it.next());
		}

		Comparator<RareBoosterPurchaseProto> c = new Comparator<RareBoosterPurchaseProto>() {
			@Override
			public int compare(RareBoosterPurchaseProto o1,
					RareBoosterPurchaseProto o2) {
				if (o1.getTimeOfPurchase() < o2.getTimeOfPurchase()) {
					return -1;
				} else if (o1.getTimeOfPurchase() > o2.getTimeOfPurchase()) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		Collections.sort(boosterPurchases, c);
		// Need to add them in reverse order
		for (int i = 0; i < boosterPurchases.size(); i++) {
			resBuilder.addRareBoosterPurchases(boosterPurchases.get(i));
		}
	}

	private void setTaskStuff(Builder resBuilder, String userId) {
		NOTE: DB CALL
		List<UserTaskCompleted> utcList = taskForUserCompletedRetrieveUtils
				.getAllCompletedTasksForUser(userId);
		List<UserTaskCompletedProto> utcpList = createInfoProtoUtils
				.createUserTaskCompletedProto(utcList);
		resBuilder.addAllCompletedTasks(utcpList);

		List<Integer> taskIds = taskForUserCompletedRetrieveUtils
				.getTaskIds(utcList);
		resBuilder.addAllCompletedTaskIds(taskIds);

		NOTE: DB CALL
		TaskForUserOngoing aTaskForUser = getTaskForUserOngoingRetrieveUtils()
				.getUserTaskForUserId(userId);
		if (null != aTaskForUser) {
			TaskForUserClientState tfucs = taskForUserClientStateRetrieveUtil
					.getTaskForUserClientState(userId);
			log.warn(String.format("user has incompleted task userTask=%s",
					aTaskForUser));
			setOngoingTask(resBuilder, userId, aTaskForUser, tfucs);
		}
	}

	private void setOngoingTask(Builder resBuilder, String userId,
			TaskForUserOngoing aTaskForUser, TaskForUserClientState tfucs) {
		try {
			MinimumUserTaskProto mutp = createInfoProtoUtils
					.createMinimumUserTaskProto(userId, aTaskForUser, tfucs);
			resBuilder.setCurTask(mutp);

			//create protos for stages
			String userTaskId = aTaskForUser.getId();
			NOTE: DB CALL
			List<TaskStageForUser> taskStages = getTaskStageForUserRetrieveUtils()
					.getTaskStagesForUserWithTaskForUserId(userTaskId);

			//group taskStageForUsers by stage nums because more than one
			//taskStageForUser with the same stage num means this stage
			//has more than one monster
			Map<Integer, List<TaskStageForUser>> stageNumToTsfu = new HashMap<Integer, List<TaskStageForUser>>();
			for (TaskStageForUser tsfu : taskStages) {
				int stageNum = tsfu.getStageNum();

				if (!stageNumToTsfu.containsKey(stageNum)) {
					List<TaskStageForUser> a = new ArrayList<TaskStageForUser>();
					stageNumToTsfu.put(stageNum, a);
				}

				List<TaskStageForUser> tsfuList = stageNumToTsfu.get(stageNum);
				tsfuList.add(tsfu);
			}

			//now that we have grouped all the monsters in their corresponding
			//task stages, protofy them
			int taskId = aTaskForUser.getTaskId();
			for (Integer stageNum : stageNumToTsfu.keySet()) {
				List<TaskStageForUser> monsters = stageNumToTsfu.get(stageNum);

				TaskStageProto tsp = createInfoProtoUtils.createTaskStageProto(
						taskId, stageNum, monsters);
				resBuilder.addCurTaskStages(tsp);
			}

		} catch (Exception e) {
			log.error(
					"could not create existing user task, letting it get deleted when user starts another task.",
					e);
		}
	}

	private void setEventStuff(Builder resBuilder, String userId) {
		NOTE: DB CALL
		List<EventPersistentForUser> events = getEventPersistentForUserRetrieveUtils()
				.getUserPersistentEventForUserId(userId);

		for (EventPersistentForUser epfu : events) {
			UserPersistentEventProto upep = createInfoProtoUtils
					.createUserPersistentEventProto(epfu);
			resBuilder.addUserEvents(upep);
		}

	}

	private void setPvpBoardObstacles(Builder resBuilder, String userId) {
		List<PvpBoardObstacleForUser> boList = pvpBoardObstacleForUserRetrieveUtil
				.getPvpBoardObstacleForUserId(userId);
		for (PvpBoardObstacleForUser pbofu : boList) {
			UserPvpBoardObstacleProto upbop = createInfoProtoUtils
					.createUserPvpBoardObstacleProto(pbofu);
			resBuilder.addUserPvpBoardObstacles(upbop);
		}
	}

	private PvpLeagueForUser pvpBattleStuff(Builder resBuilder, User user,
			String userId, boolean isFreshRestart, Timestamp battleEndTime) {

		//	  PvpLeagueForUser plfu = setPvpLeagueInfo(resBuilder, userId);
		PvpLeagueForUser plfu = getPvpLeagueForUserRetrieveUtil()
				.getUserPvpLeagueForId(userId);

		PvpUser pu = new PvpUser(plfu);
		getHazelcastPvpUtil().replacePvpUser(pu, userId);

		if (!isFreshRestart) {
			log.info("not fresh restart, so not deleting pvp battle stuff");
			return plfu;
		}

		//if bool isFreshRestart is true, then deduct user's elo by amount specified in
		//the table (pvp_battle_for_user), since user auto loses
		PvpBattleForUser battle = getPvpBattleForUserRetrieveUtils()
				.getPvpBattleForUserForAttacker(userId);

		if (null == battle) {
			return plfu;
		}
		Timestamp battleStartTime = new Timestamp(battle.getBattleStartTime()
				.getTime());
		//capping max elo attacker loses
		int eloAttackerLoses = battle.getAttackerLoseEloChange();
		if (plfu.getElo() + eloAttackerLoses < ControllerConstants.PVP__DEFAULT_MIN_ELO) {
			eloAttackerLoses = plfu.getElo() - ControllerConstants.PVP__DEFAULT_MIN_ELO;
		}

		String defenderId = battle.getDefenderId();
		int eloDefenderWins = battle.getDefenderWinEloChange();

		//user has unfinished battle, reward defender and penalize attacker
		penalizeUserForLeavingGameWhileInPvp(userId, user, plfu, defenderId,
				eloAttackerLoses, eloDefenderWins, battleEndTime,
				battleStartTime, battle);
		return plfu;
	}

	private void penalizeUserForLeavingGameWhileInPvp(String userId, User user,
			PvpLeagueForUser attackerPlfu, String defenderId,
			int eloAttackerLoses, int eloDefenderWins, Timestamp battleEndTime,
			Timestamp battleStartTime, PvpBattleForUser battle) {
		//NOTE: this lock ordering might result in a temp deadlock
		//doesn't reeeally matter if can't penalize defender...

		UUID defenderUuid = null;
		boolean invalidUuids = true;
		try {
			if(defenderId != null && !defenderId.isEmpty()) {
				defenderUuid = UUID.fromString(defenderId);
			}
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect defenderId=%s",
					defenderId), e);
			invalidUuids = true;
		}
		if (invalidUuids) {
			return;
		}
		//only lock real users
		if (null != defenderUuid) {
			getLocker().lockPlayer(defenderUuid,
					this.getClass().getSimpleName());
		}
		try {
			int attackerEloBefore = attackerPlfu.getElo();
			int defenderEloBefore = 0;
			int attackerPrevLeague = attackerPlfu.getPvpLeagueId();
			int attackerCurLeague = 0;
			int defenderPrevLeague = 0;
			int defenderCurLeague = 0;
			int attackerPrevRank = attackerPlfu.getRank();
			int attackerCurRank = 0;
			int defenderPrevRank = 0;
			int defenderCurRank = 0;

			//update hazelcast map and ready arguments for pvp battle history
			int attackerCurElo = attackerPlfu.getElo() + eloAttackerLoses;
			attackerCurLeague = pvpLeagueRetrieveUtils.getLeagueIdForElo(
					attackerCurElo, attackerPrevLeague);
			attackerCurRank = pvpLeagueRetrieveUtils.getRankForElo(
					attackerCurElo, attackerCurLeague);

			int attacksLost = attackerPlfu.getAttacksLost() + 1;

			//update attacker
			//don't update his shields, just elo
			int numUpdated = UpdateUtils.get().updatePvpLeagueForUser(userId,
					attackerCurLeague, attackerCurRank, eloAttackerLoses, null,
					null, 0, 0, 1, 0, -1);

			log.info("num updated when changing attacker's elo because of reset={}",
					numUpdated);
			attackerPlfu.setElo(attackerCurElo);
			attackerPlfu.setPvpLeagueId(attackerCurLeague);
			attackerPlfu.setRank(attackerCurRank);
			attackerPlfu.setAttacksLost(attacksLost);
			PvpUser attackerPu = new PvpUser(attackerPlfu);
			getHazelcastPvpUtil().replacePvpUser(attackerPu, userId);

			//update defender if real, TODO: might need to cap defenderElo
			if (null != defenderUuid) {//defenderId) {
				PvpLeagueForUser defenderPlfu = getPvpLeagueForUserRetrieveUtil()
						.getUserPvpLeagueForId(defenderId);

				defenderEloBefore = defenderPlfu.getElo();
				defenderPrevLeague = defenderPlfu.getPvpLeagueId();
				defenderPrevRank = defenderPlfu.getRank();
				//update hazelcast map and ready arguments for pvp battle history
				int defenderCurElo = defenderEloBefore + eloDefenderWins;
				defenderCurLeague = pvpLeagueRetrieveUtils.getLeagueIdForElo(
						defenderCurElo, defenderPrevLeague);
				defenderCurRank = pvpLeagueRetrieveUtils.getRankForElo(
						defenderCurElo, defenderCurLeague);

				int defensesWon = defenderPlfu.getDefensesWon() + 1;

				numUpdated = UpdateUtils.get().updatePvpLeagueForUser(
						defenderId, defenderCurLeague, defenderCurRank,
						eloDefenderWins, null, null, 0, 1, 0, 0, -1);
				log.info("num updated when changing defender's elo because of reset={}",
						numUpdated);

				defenderPlfu.setElo(defenderCurElo);
				defenderPlfu.setPvpLeagueId(defenderCurLeague);
				defenderPlfu.setRank(defenderCurRank);
				defenderPlfu.setDefensesWon(defensesWon);
				PvpUser defenderPu = new PvpUser(defenderPlfu);
				getHazelcastPvpUtil().replacePvpUser(defenderPu, defenderId);
			}

			boolean attackerWon = false;
			boolean cancelled = false;
			boolean defenderGotRevenge = false;
			boolean displayToDefender = true;
			int numInserted = InsertUtils.get().insertIntoPvpBattleHistory(
					userId, defenderId, battleEndTime, battleStartTime,
					eloAttackerLoses, attackerEloBefore, eloDefenderWins,
					defenderEloBefore, attackerPrevLeague, attackerCurLeague,
					defenderPrevLeague, defenderCurLeague, attackerPrevRank,
					attackerCurRank, defenderPrevRank, defenderCurRank, 0, 0,
					0, 0, -1, attackerWon, cancelled, defenderGotRevenge,
					displayToDefender);

			log.info("numInserted into battle history={}",
					numInserted);
			//delete that this battle occurred
			int numDeleted = DeleteUtils.get().deletePvpBattleForUser(userId);
			log.info("successfully penalized, rewarded attacker and defender respectively. battle={}, numDeleted={}",
					battle, numDeleted);

		} catch (Exception e) {
			log.error(
					String.format(
							"tried to penalize, reward attacker and defender respectively. battle=%s",
							battle), e);
		} finally {
			if (null != defenderUuid) {
				getLocker().unlockPlayer(defenderUuid,
						this.getClass().getSimpleName());
			}
		}
	}

	private void setAllStaticData(Builder resBuilder, String userId,
			boolean userIdSet) {
		StaticDataProto sdp = miscMethods.getAllStaticData(userId, userIdSet,
				getQuestForUserRetrieveUtils());

		resBuilder.setStaticDataStuffProto(sdp);
	}


	private void pvpBattleHistoryStuff(Builder resBuilder, User user, int userId) {
		int n = ControllerConstants.PVP_HISTORY__NUM_RECENT_BATTLES;

		//NOTE: AN ATTACKER MIGHT SHOW UP MORE THAN ONCE DUE TO REVENGE
		List<PvpBattleHistory> historyList = getPvpBattleHistoryRetrieveUtil()
			.getRecentNBattlesForDefenderId(userId, n);
		log.info(String.format(
			"historyList=%s", historyList));

		Set<Integer> attackerIds = getPvpBattleHistoryRetrieveUtil()
			.getAttackerIdsFromHistory(historyList);
		log.info(String.format(
			"attackerIds=%s", attackerIds));

		if (null == attackerIds || attackerIds.isEmpty()) {
			log.info("no valid 10 pvp battles for user. ");
			return;
		}
		//!!!!!!!!!!!RETRIEVE BUNCH OF USERS REQUEST
		Map<Integer, User> idsToAttackers = RetrieveUtils.userRetrieveUtils()
			.getUsersByIds(attackerIds);
		log.info(String.format(
			"idsToAttackers=%s", idsToAttackers));

		List<Integer> attackerIdsList = new ArrayList<Integer>(idsToAttackers.keySet());
		Map<Integer, List<MonsterForUser>> attackerIdToCurTeam = selectMonstersForUsers(
			attackerIdsList);
		log.info(String.format(
			"history monster teams=%s", attackerIdToCurTeam));

		Map<Integer, Integer> attackerIdsToProspectiveCashWinnings =
			new HashMap<Integer, Integer>();
		Map<Integer, Integer> attackerIdsToProspectiveOilWinnings =
			new HashMap<Integer, Integer>();
		PvpUser attackerPu = getHazelcastPvpUtil().getPvpUser(userId);
		calculateCashOilRewardFromPvpUsers(userId, attackerPu.getElo(),
			idsToAttackers, attackerIdsToProspectiveCashWinnings,
			attackerIdsToProspectiveOilWinnings);

		List<PvpHistoryProto> historyProtoList = createInfoProtoUtils
			.createPvpHistoryProto(historyList, idsToAttackers, attackerIdToCurTeam,
				attackerIdsToProspectiveCashWinnings, attackerIdsToProspectiveOilWinnings);

		//  	log.info("historyProtoList=" + historyProtoList);
		resBuilder.addAllRecentNBattles(historyProtoList);
	}

	//Similar logic to calculateCashOilRewards in QueueUpController
	private void calculateCashOilRewardFromPvpUsers( int attackerId,
		int attackerElo, Map<Integer, User> userIdsToUsers,
		Map<Integer, Integer> userIdToCashStolen,
		Map<Integer, Integer> userIdToOilStolen )
	{
		Collection<Integer> userIdz = userIdsToUsers.keySet() ;
		Map<String, PvpUser> idsToPvpUsers = getHazelcastPvpUtil()
			.getPvpUsers(userIdz);

		for (Integer defenderId : userIdz) {
			String defenderEyed = defenderId.toString();

			User defender = userIdsToUsers.get(defenderId);
			PvpUser defenderPu = idsToPvpUsers.get(defenderEyed);

			PvpBattleOutcome potentialResult = new PvpBattleOutcome(
				attackerId, attackerElo, defenderId, defenderPu.getElo(),
				defender.getCash(), defender.getOil());

			userIdToCashStolen.put(defenderId,
				potentialResult.getUnsignedCashAttackerWins());
			userIdToOilStolen.put(defenderId,
				potentialResult.getUnsignedOilAttackerWins());
		}
	}

	//SOOOOOO DISGUSTING.............ALL THIS FUCKING CODE. SO GROSS.
	//COPIED FROM QueueUpController;
	//given users, get the 3 monsters for each user
	private Map<Integer, List<MonsterForUser>> selectMonstersForUsers(
		List<Integer> userIdList) {

		//return value
		Map<Integer, List<MonsterForUser>> userIdsToUserMonsters =
			new HashMap<Integer, List<MonsterForUser>>();

		//for all these users, get all their complete monsters
		Map<Integer, Map<Long, MonsterForUser>> userIdsToMfuIdsToMonsters = RetrieveUtils
			.monsterForUserRetrieveUtils().getCompleteMonstersForUser(userIdList);


		for (int index = 0; index < userIdList.size(); index++) {
			//extract a user's monsters
			int defenderId = userIdList.get(index);
			Map<Long, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters.get(defenderId);

			if (null == mfuIdsToMonsters || mfuIdsToMonsters.isEmpty()) {
				log.error("WTF!!!!!!!! user has no monsters!!!!! userId=" + defenderId +
					"\t will move on to next guy.");
				continue;
			}
			//try to select at most 3 monsters for this user
			List<MonsterForUser> defenderMonsters = selectMonstersForUser(mfuIdsToMonsters);

			//if the user still doesn't have 3 monsters, then too bad
			userIdsToUserMonsters.put(defenderId, defenderMonsters);
		}

		return userIdsToUserMonsters;
	}
	private List<MonsterForUser> selectMonstersForUser(Map<Long, MonsterForUser> mfuIdsToMonsters) {

		//get all the monsters the user has on a team (at the moment, max is 3)
		List<MonsterForUser> defenderMonsters = getEquippedMonsters(mfuIdsToMonsters);

		//so users can have no monsters equipped, so just choose one fucking monster for him
		if (defenderMonsters.isEmpty()) {
			List<MonsterForUser> randMonsters = new ArrayList<MonsterForUser>(
				mfuIdsToMonsters.values());
			defenderMonsters.add(randMonsters.get(0));
		}

		return defenderMonsters;
	}
	private List<MonsterForUser> getEquippedMonsters(Map<Long, MonsterForUser> userMonsters) {
		List<MonsterForUser> equipped = new ArrayList<MonsterForUser>();

		for (MonsterForUser mfu : userMonsters.values()) {
			if (mfu.getTeamSlotNum() <= 0) {
				//only want equipped monsters
				continue;
			}
			equipped.add(mfu);

		}
		return equipped;
	}


	private void setAchievementStuff(Builder resBuilder, String userId, User user, Date nowDate, StopWatch stopWatch) {
		NOTE: DB CALL
		Map<Integer, AchievementForUser> achievementIdToUserAchievements = getAchievementForUserRetrieveUtil()
				.getSpecificOrAllAchievementIdToAchievementForUserId(userId,
						null);

		for (AchievementForUser afu : achievementIdToUserAchievements.values()) {
			UserAchievementProto uap = createInfoProtoUtils
					.createUserAchievementProto(afu);
			resBuilder.addUserAchievements(uap);

		}
		boolean calculateMiniEvent = true;
		log.info("clanAchievementIds={}", ControllerConstants.CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS);
		for (int i = 0; i < ControllerConstants.CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS.length; i++) {
			int achievementId = ControllerConstants.CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS[i];
			if (!achievementIdToUserAchievements.containsKey(achievementId))
			{
				calculateMiniEvent = false;
				break;
			}

			AchievementForUser afu = achievementIdToUserAchievements.get(achievementId);
			log.info("afu={}", afu);
			if (!afu.isRedeemed())
			{
				calculateMiniEvent = false;
				break;
			}
		}

		if (calculateMiniEvent) {
			setMiniEventForUser(resBuilder, user, userId, nowDate);
			log.info("{}ms at setMiniEventForUser", stopWatch.getTime());
		}
	}

	private void setMiniEventForUser(
			Builder resBuilder, User u, String userId, Date now)
	{
		RetrieveMiniEventResponseProto.Builder rmeaResBuilder =
				RetrieveMiniEventResponseProto.newBuilder();

		RetrieveMiniEventAction rmea = new RetrieveMiniEventAction(
				userId,
				now,
				false,
				userRetrieveUtils,
				achievementForUserRetrieveUtil,
				miniEventForUserRetrieveUtil,
				miniEventGoalForUserRetrieveUtil,
				insertUtil,
				deleteUtil,
				miniEventGoalRetrieveUtils,
				miniEventForPlayerLvlRetrieveUtils,
				miniEventRetrieveUtils,
				miniEventTierRewardRetrieveUtils,
				miniEventLeaderboardRewardRetrieveUtils,
				timeUtils);


		rmea.execute(rmeaResBuilder);
//		log.info("{}, {}", MiniEventRetrieveUtils.getAllIdsToMiniEvents(),
//				MiniEventRetrieveUtils.getCurrentlyActiveMiniEvent(now));
//		log.info("resProto for MiniEvent={}", rmeaResBuilder.build());

		if (rmeaResBuilder.getStatus().equals(RetrieveMiniEventStatus.SUCCESS) &&
				null != rmea.getCurActiveMiniEvent())
		{
			//get UserMiniEvent info and create the proto to set into resBuilder
			UserMiniEventProto umep = createInfoProtoUtils
					.createUserMiniEventProto(
							rmea.getMefu(), rmea.getCurActiveMiniEvent(),
							rmea.getMegfus(),
							rmea.getLvlEntered(), rmea.getRewards(),
							rmea.getGoals(), rmea.getLeaderboardRewards(),
							rewardRetrieveUtil);
			resBuilder.setUserMiniEvent(umep);
		}

	}

	private void setMiniJob(Builder resBuilder, String userId) {
		Map<String, MiniJobForUser> miniJobIdToUserMiniJobs = getMiniJobForUserRetrieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(userId, null);

		if (miniJobIdToUserMiniJobs.isEmpty()) {
			return;
		}

		List<MiniJobForUser> mjfuList = new ArrayList<MiniJobForUser>(
				miniJobIdToUserMiniJobs.values());
		List<UserMiniJobProto> umjpList = createInfoProtoUtils
				.createUserMiniJobProtos(mjfuList, null, rewardRetrieveUtil);

		resBuilder.addAllUserMiniJobProtos(umjpList);
	}

	private void setUserItems(StopWatch stopWatch, Builder resBuilder,
			User user, String userId)
	{
		Map<Integer, ItemForUser> itemIdToUserItems = itemForUserRetrieveUtil
				.getSpecificOrAllItemForUserMap(userId, null);

		if (!itemIdToUserItems.isEmpty()) {
			List<UserItemProto> uipList = createInfoProtoUtils
					.createUserItemProtosFromUserItems(new ArrayList<ItemForUser>(
							itemIdToUserItems.values()));

			resBuilder.addAllUserItems(uipList);
		}

		List<ItemForUserUsage> itemsUsed = itemForUserUsageRetrieveUtil
				.getItemForUserUsage(userId, null);

		for (ItemForUserUsage ifuu : itemsUsed) {
			UserItemUsageProto uiup = createInfoProtoUtils
					.createUserItemUsageProto(ifuu);
			resBuilder.addItemsInUse(uiup);
		}

		Set<Integer> userItemIds = itemIdToUserItems.keySet();
		setSalePack(stopWatch, resBuilder, user, userItemIds);
	}

	private void setSalePack(StopWatch stopWatch, Builder resBuilder,
			User user, Set<Integer> userItemIds) {
		if(serviceCombinedStarterAndBuilderPack(user)) {
			setStarterBuilderPackForUser(resBuilder, user);
			log.info("{}ms at setStarterBuilderPackForUser", stopWatch.getTime());
		}
		else {
			setStarterPackForUser(resBuilder, user);
			log.info("{}ms at setStarterPackForUser", stopWatch.getTime());
			setBuilderPackForUser(resBuilder, user, userItemIds);
			log.info("{}ms at setBuilderPackForUser", stopWatch.getTime());
		}
	}

	public boolean serviceCombinedStarterAndBuilderPack(User user) {
		Object[] objArray = new Object[2];
		objArray[0] = "COOPER";
		objArray[1] = "ALEX";

		Float[] floatArray = new Float[2];
		floatArray[0] = (float)0.5;
		floatArray[1] = (float)0.5;

		UserSegmentationGroupAction usga = new UserSegmentationGroupAction(objArray, floatArray, user.getId(), user);

		if(usga.returnAppropriateObjectGroup().equals("COOPER")) {
			log.info("sending starterbuilderpack");
			return true;
		}
		else {
			log.info("sending starter and builder pack");
			return false;
		}
	}
	//TODO: Get rid of this copy pasted code
	public void setStarterBuilderPackForUser(Builder resBuilder, User user) {
		int numBeginnerSalesPurchased = user.getNumBeginnerSalesPurchased();

		if(numBeginnerSalesPurchased == 0) {
			Map<Integer, SalesPackage> idsToSalesPackages = salesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages();
			for(SalesPackage sp : idsToSalesPackages.values()) {
				if(sp.getProductId().equalsIgnoreCase(IAPValues.STARTERBUILDERPACK)) {
					SalesPackageProto spProto = inAppPurchaseUtils.createSalesPackageProto(sp, salesItemRetrieveUtils,
							salesDisplayItemRetrieveUtils, customMenuRetrieveUtils);
					resBuilder.addSalesPackages(spProto);
				}
			}
		}
	}
	//TODO: Get rid of this copy pasted code
	public void setStarterPackForUser(Builder resBuilder, User user) {
		int numBeginnerSalesPurchased = user.getNumBeginnerSalesPurchased();

		if(numBeginnerSalesPurchased == 0) {
			Map<Integer, SalesPackage> idsToSalesPackages = salesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages();
			for(SalesPackage sp : idsToSalesPackages.values()) {
				if(sp.getProductId().equalsIgnoreCase(IAPValues.STARTERPACK)) {
					SalesPackageProto spProto = inAppPurchaseUtils.createSalesPackageProto(sp, salesItemRetrieveUtils,
							salesDisplayItemRetrieveUtils, customMenuRetrieveUtils);
					resBuilder.addSalesPackages(spProto);
				}
			}
		}
	}

	//TODO: Get rid of this copy pasted code
	public void setBuilderPackForUser(Builder resBuilder, User user, Set<Integer> userItemIds) {
		boolean hasExtraBuilder = false;
		for(Integer itemId : userItemIds) {
			//TODO: Make a constant out of this number for builder's id
			if(itemId == 10000) { //builder's id
				hasExtraBuilder = true;
			}
		}

		if(!hasExtraBuilder) {
			Map<Integer, SalesPackage> idsToSalesPackages = salesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages();
			for(SalesPackage sp : idsToSalesPackages.values()) {
				if(sp.getProductId().equalsIgnoreCase(IAPValues.BUILDERPACK)) {
					SalesPackageProto spProto = inAppPurchaseUtils.createSalesPackageProto(sp, salesItemRetrieveUtils,
							salesDisplayItemRetrieveUtils, customMenuRetrieveUtils);
					resBuilder.addSalesPackages(spProto);
				}
			}
		}
	}

	private void setWhetherPlayerCompletedInAppPurchase(Builder resBuilder,
			User user) {
		boolean hasPurchased = getIapHistoryRetrieveUtils()
				.checkIfUserHasPurchased(user.getId());
		resBuilder.setPlayerHasBoughtInAppPurchase(hasPurchased);
	}

	private void setSecretGifts(Builder resBuilder, String userId, long now) {
		Collection<SecretGiftForUser> gifts = secretGiftForUserRetrieveUtil
				.getSpecificOrAllSecretGiftForUser(userId, null);

		//need to enforce 2 gift minimum
		int numGifts = 0;
		if (null == gifts || gifts.isEmpty()) {
			gifts = new ArrayList<SecretGiftForUser>();
			numGifts = 2;

		} else if (null != gifts && gifts.size() == 1) {
			numGifts = 1;
		}

		if (numGifts > 0) {
			giveGifts(userId, now, gifts, numGifts);
		}

		Collection<UserSecretGiftProto> nuGiftsProtos = createInfoProtoUtils
				.createUserSecretGiftProto(gifts);
		resBuilder.addAllGifts(nuGiftsProtos);
	}

	//need to enforce 2 gift minimum
	private void giveGifts(String userId, long now,
			Collection<SecretGiftForUser> gifts, int numGifts) {
		List<SecretGiftForUser> giftList = secretGiftUtils
				.calculateGiftsForUser(userId, numGifts, now);

		List<String> ids = insertUtil
				.insertIntoSecretGiftForUserGetId(giftList);

		//need to set the ids
		if (null != ids && ids.size() == giftList.size()) {

			for (int index = 0; index < ids.size(); index++) {
				String id = ids.get(index);
				SecretGiftForUser isgfu = giftList.get(index);

				isgfu.setId(id);
			}

			gifts.addAll(giftList);

		} else {
			log.error("Error calculating the new SecretGifts. nuGifts={}, ids={}",
					giftList, ids);
		}

	}

	private void setResearch(Builder resBuilder, String userId) {
		List<ResearchForUser> userResearchs = researchForUserRetrieveUtil
				.getAllResearchForUser(userId);

		if (null != userResearchs && !userResearchs.isEmpty()) {
			Collection<UserResearchProto> urpList = createInfoProtoUtils
					.createUserResearchProto(userResearchs);

			resBuilder.addAllUserResearchs(urpList);
		}
	}

	private void setBattleItemQueueForUser(Builder resBuilder, String userId) {
		List<BattleItemQueueForUser> biqfuList = battleItemQueueForUserRetrieveUtil
				.getUserBattleItemQueuesForUser(userId);
		if (null != biqfuList && !biqfuList.isEmpty()) {
			Collection<BattleItemQueueForUserProto> biqfupList = createInfoProtoUtils
					.createBattleItemQueueForUserProtoList(biqfuList);

			resBuilder.addAllBattleItemQueue(biqfupList);
		}
	}

	public void setSalesForUser(Builder resBuilder, User user) {

		Map<Integer, SalesPackage> idsToSalesPackages = salesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages();

		//		boolean salesJumpTwoTiers = updateUserSalesJumpTwoTiers(user);
		int userSalesValue = user.getSalesValue();
		int newMinPrice = priceForSalesPackToBeShown(userSalesValue);
		Date now = new Date();

		for(SalesPackage sp : idsToSalesPackages.values()) {
			if(!sp.getProductId().equalsIgnoreCase(IAPValues.STARTERPACK) &&
					!sp.getProductId().equalsIgnoreCase(IAPValues.BUILDERPACK) &&
					!sp.getProductId().equalsIgnoreCase(IAPValues.STARTERBUILDERPACK)) { //make sure it's not starter pack
				if(sp.getPrice() == newMinPrice && timeUtils.isFirstEarlierThanSecond(sp.getTimeStart(), now) &&
						timeUtils.isFirstEarlierThanSecond(now, sp.getTimeEnd())) {
					SalesPackageProto spProto = inAppPurchaseUtils
							.createSalesPackageProto(sp, salesItemRetrieveUtils, salesDisplayItemRetrieveUtils, customMenuRetrieveUtils);
					resBuilder.addSalesPackages(spProto);
				}
			}
		}
	}

	public int priceForSalesPackToBeShown(int userSalesValue) {

		int newMinPrice = 0;

		if(userSalesValue == 0) {
			newMinPrice = 5;
		}
		else if(userSalesValue == 1) {
			newMinPrice = 10;
		}
		else if(userSalesValue == 2) {
			newMinPrice = 20;
		}
		else if(userSalesValue == 3) {
			newMinPrice = 50;
		}
		else if(userSalesValue > 3) {
			newMinPrice = 100;
		}

		return newMinPrice;
	}

//	public boolean updateUserSalesJumpTwoTiers(User user) {
//		//update user jump two tier's value
//		boolean salesJumpTwoTiers = user.isSalesJumpTwoTiers();
//		if(salesJumpTwoTiers) {
//			Date lastPurchaseTime = user.getLastPurchaseTime();
//			if(lastPurchaseTime == null) {
//				lastPurchaseTime = new Date();
//				Timestamp ts = new Timestamp(lastPurchaseTime.getTime());
//				updateUtil.updateUserSalesLastPurchaseTime(user.getId(), ts);
//			}
//			Date now = new Date();
//			int diffInDays = (int)(now.getTime() - lastPurchaseTime.getTime())/(24*60*60*1000);
//			if(diffInDays > 5) {
//				updateUtil.updateUserSalesJumpTwoTiers(user.getId(), false);
//				salesJumpTwoTiers = false;
//			}
//		}
//		return salesJumpTwoTiers;
//	}


	private void setBattleItemForUser(Builder resBuilder, String userId) {
		List<BattleItemForUser> bifuList = battleItemForUserRetrieveUtil
				.getUserBattleItemsForUser(userId);
		if (null != bifuList && !bifuList.isEmpty()) {
			Collection<UserBattleItemProto> biqfupList = createInfoProtoUtils
					.convertBattleItemForUserListToBattleItemForUserProtoList(bifuList);

			resBuilder.addAllBattleItem(biqfupList);
		}
	}


	private void setDefaultLanguagesForUser(Builder resBuilder, String userId) {

		//		TranslationSettingsForUser tsfu = translationSettingsForUserRetrieveUtil.
		//				getSpecificUserGlobalTranslationSettings(userId, ChatType.GLOBAL_CHAT);

		List<TranslationSettingsForUser> tsfuList = translationSettingsForUserRetrieveUtil.
				getUserTranslationSettingsForUser(userId);

		log.info("tsfuList: " + tsfuList);

		DefaultLanguagesProto dlp = null;

		if(tsfuList != null && !tsfuList.isEmpty()) {
			dlp = createInfoProtoUtils.createDefaultLanguagesProto(tsfuList);
		}

		//if there's no default languages, they havent ever been set
		if (null != dlp) {
			resBuilder.setUserDefaultLanguages(dlp);
		}
	}

	private void setClanRaidStuff(Builder resBuilder, User user, String userId,
			Timestamp now) {
		Date nowDate = new Date(now.getTime());
		String clanId = user.getClanId();

		if (clanId == null) {
			return;
		}
		//get the clan raid information for the clan
		ClanEventPersistentForClan cepfc = getClanEventPersistentForClanRetrieveUtils()
				.getPersistentEventForClanId(clanId);

		if (null == cepfc) {
			log.info("no clan raid stuff existing for clanId={}, user={}",
					clanId, user);
			return;
		}

		PersistentClanEventClanInfoProto pcecip = createInfoProtoUtils
				.createPersistentClanEventClanInfoProto(cepfc);
		resBuilder.setCurRaidClanInfo(pcecip);

		//get the clan raid information for all the clan users
		//shouldn't be null (per the retrieveUtils)
		Map<String, ClanEventPersistentForUser> userIdToCepfu = getClanEventPersistentForUserRetrieveUtils()
				.getPersistentEventUserInfoForClanId(clanId);
		log.info("the users involved in clan raid:{}",
				userIdToCepfu);

		if (null == userIdToCepfu || userIdToCepfu.isEmpty()) {
			log.info( "no users involved in clan raid. clanRaid={}",
					cepfc );
			return;
		}

		List<String> userMonsterIds = monsterStuffUtils
				.getUserMonsterIdsInClanRaid(userIdToCepfu);

		//TODO: when retrieving clan info, and user's current teams, maybe query for
		//these monsters as well
		Map<String, MonsterForUser> idsToUserMonsters = getMonsterForUserRetrieveUtils()
				.getSpecificUserMonsters(userMonsterIds);

		for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
			PersistentClanEventUserInfoProto pceuip = createInfoProtoUtils
					.createPersistentClanEventUserInfoProto(cepfu,
							idsToUserMonsters, null);
			resBuilder.addCurRaidClanUserInfo(pceuip);
		}

		setClanRaidHistoryStuff(resBuilder, userId, nowDate);

	}

	private void setClanRaidHistoryStuff(Builder resBuilder, String userId,
			Date nowDate) {

		//the raid stage and reward history for past 7 days
		int nDays = ControllerConstants.CLAN_EVENT_PERSISTENT__NUM_DAYS_FOR_RAID_STAGE_HISTORY;
		Map<Date, CepfuRaidStageHistory> timesToRaidStageHistory = getCepfuRaidStageHistoryRetrieveUtils()
				.getRaidStageHistoryForPastNDaysForUserId(userId, nDays,
						nowDate, timeUtils);

		Map<Date, List<ClanEventPersistentUserReward>> timesToUserRewards = getClanEventPersistentUserRewardRetrieveUtils()
				.getCepUserRewardForPastNDaysForUserId(userId, nDays, nowDate,
						timeUtils);

		//possible for ClanRaidStageHistory to have no rewards if clan didn't beat stage
		for (Date aDate : timesToRaidStageHistory.keySet()) {
			CepfuRaidStageHistory cepfursh = timesToRaidStageHistory.get(aDate);
			List<ClanEventPersistentUserReward> rewards = null;

			if (timesToUserRewards.containsKey(aDate)) {
				rewards = timesToUserRewards.get(aDate);
			}

			PersistentClanEventRaidStageHistoryProto stageProto = createInfoProtoUtils
					.createPersistentClanEventRaidStageHistoryProto(cepfursh,
							rewards);

			resBuilder.addRaidStageHistory(stageProto);
		}
	}

	//  private void setAllBosses(Builder resBuilder, UserType type) {
	//    Map<Integer, Monster> bossIdsToBosses =
	//        MonsterRetrieveUtils.getBossIdsToBosses();
	//
	//    for (Monster b : bossIdsToBosses.values()) {
	//      FullBossProto fbp =
	//          createInfoProtoUtils.createFullBossProtoFromBoss(type, b);
	//      resBuilder.addBosses(fbp);
	//    }
	//  }

	// retrieve's the active leaderboard event prizes and rewards for the events
	//  private void setLeaderboardEventStuff(StartupResponseProto.Builder resBuilder) {
	//    resBuilder.addAllLeaderboardEvents(miscMethods.currentTournamentEventProtos());
	//  }

	private void sendOfferChartInstall(Date installTime, String advertiserId) {
		String clientId = "15";
		String appId = "648221050";
		String geo = "N/A";
		String installTimeStr = "" + installTime.getTime();
		String devicePlatform = "iphone";
		String deviceType = "iphone";

		String urlString = "http://offerchart.com/mobileapp/api/send_install_ping?"
				+ "client_id="
				+ clientId
				+ "&app_id="
				+ appId
				+ "&device_id="
				+ advertiserId
				+ "&device_type="
				+ deviceType
				+ "&geo="
				+ geo
				+ "&install_time="
				+ installTimeStr
				+ "&device_platform="
				+ devicePlatform;

		log.info("Sending offerchart request:\n" + urlString);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urlString);

		try {
			HttpResponse response1 = httpclient.execute(httpGet);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response1.getEntity().getContent()));
			String responseString = "";
			String line;
			while ((line = rd.readLine()) != null) {
				responseString += line;
			}
			log.info("Received response: " + responseString);
		} catch (Exception e) {
			log.error("failed to make offer chart call", e);
		}
	}

	private String retrieveKabamNaid(User user, String openUdid, String mac, String advertiserId) {
	String host;
	int port = 443;
	int clientId;
	String secret;
	if (Globals.IS_SANDBOX()) {
	  host = KabamProperties.SANDBOX_API_URL;
	  clientId = KabamProperties.SANDBOX_CLIENT_ID;
	  secret = KabamProperties.SANDBOX_SECRET;
	} else {
	  host = KabamProperties.PRODUCTION_API_URL;
	  clientId = KabamProperties.PRODUCTION_CLIENT_ID;
	  secret = KabamProperties.PRODUCTION_SECRET;
	}

	KabamApi kabamApi = new KabamApi(host, port, secret);
	String userId = openUdid;
	String platform = "iphone";

	String biParams = "{\"open_udid\":\"" + userId + "\",\"mac\":\"" + mac
	    + "\",\"mac_hash\":\"" + DigestUtils.md5Hex(mac) + "\",\"advertiser_id\":\"" + advertiserId
	    + "\"}";

	MobileNaidResponse naidResponse;
	try {
	  naidResponse = kabamApi.mobileGetNaid(userId, clientId, platform, biParams,
	      new Date().getTime() / 1000);
	} catch (Exception e) {
	  e.printStackTrace();
	  return "";
	}

	if (naidResponse.getReturnCode() == ResponseCode.Success) {
	  if (user != null) {
	    user.updateSetKabamNaid(naidResponse.getNaid());
	  }
	  log.info("Successfully got kabam naid.");
	  return naidResponse.getNaid()+"";
	} else {
	  log.error("Error retrieving kabam naid: " + naidResponse.getReturnCode());
	}
	return "";
	}

	protected void updateLeaderboard(String apsalarId, User user,
			Timestamp now, int newNumConsecutiveDaysLoggedIn) {
		if (user != null) {
			log.info("Updating leaderboard for user " + user.getId());
			syncApsalaridLastloginConsecutivedaysloggedinResetBadges(user,
					apsalarId, now, newNumConsecutiveDaysLoggedIn);
			//LeaderBoardUtil leaderboard = AppContext.getApplicationContext().getBean(LeaderBoardUtil.class);
			//null PvpLeagueFromUser means will pull from hazelcast instead
			//leaderboard.updateLeaderboardForUser(user, null);
		}
	}

	// returns the total number of consecutive days the user logged in,
	// awards user if user logged in for an additional consecutive day
	//  private int setDailyBonusInfo(Builder resBuilder, User user, Timestamp now) {
	//    // will keep track of total consecutive days user has logged in, just
	//    // for funzies
	//    List<Integer> numConsecDaysList = new ArrayList<Integer>();
	//    int totalConsecutiveDaysPlayed = 1;
	//    List<Boolean> rewardUserList = new ArrayList<Boolean>();
	//    boolean rewardUser = false;
	//
	//    int consecutiveDaysPlayed = determineCurrentConsecutiveDay(user, now, numConsecDaysList,
	//        rewardUserList);
	//    if (!numConsecDaysList.isEmpty()) {
	//      totalConsecutiveDaysPlayed = numConsecDaysList.get(0);
	//      rewardUser = rewardUserList.get(0);
	//    }
	//
	//    DailyBonusReward rewardForUser = determineRewardForUser(user);
	//    // function does nothing if null reward was returned from
	//    // determineRewardForUser
	//    Map<String, Integer> currentDayReward = selectRewardFromDailyBonusReward(rewardForUser,
	//        consecutiveDaysPlayed);
	//
	//    List<Integer> equipIdRewardedList = new ArrayList<Integer>();
	//    // function does nothing if previous function returned null, or
	//    // either updates user's money or "purchases" booster pack for user
	//    boolean successful = writeDailyBonusRewardToDB(user, currentDayReward, rewardUser, now,
	//        equipIdRewardedList);
	//    if (successful) {
	//      int equipIdRewarded = equipIdRewardedList.get(0);
	//      writeToUserDailyBonusRewardHistory(user, currentDayReward, consecutiveDaysPlayed, now,
	//          equipIdRewarded);
	//    }
	//    setDailyBonusStuff(resBuilder, user, rewardUser, rewardForUser);
	//    return totalConsecutiveDaysPlayed;
	//  }

	// totalConsecutiveDaysList will contain one element the actual number of
	// consecutive
	// days the user has logged into our game, not really necessary to keep
	// track...
	//  private int determineCurrentConsecutiveDay(User user, Timestamp now,
	//      List<Integer> totalConsecutiveDaysList, List<Boolean> rewardUserList) {
	//    // SETTING STUFF UP
	//    int userId = user.getId();
	//    UserDailyBonusRewardHistory lastReward = UserDailyBonusRewardHistoryRetrieveUtils
	//        .getLastDailyRewardAwardedForUserId(userId);
	//    Date nowDate = new Date(now.getTime());
	//    long nowDateMillis = nowDate.getTime();
	//
	//    if (null == lastReward) {
	//      log.info("user has never received a daily bonus reward. Setting consecutive days played to 1.");
	//      totalConsecutiveDaysList.add(1);
	//      rewardUserList.add(true);
	//      return 1;
	//    }
	//    // let days = consecutive day amount corresponding to the reward user
	//    // was given
	//    // if reward was more than one day ago (in past), return 1
	//    // else if user was rewarded yesterday return the (1 + days)
	//    // else reward was today return days
	//    int nthConsecutiveDay = lastReward.getNthConsecutiveDay();
	//    Date dateLastAwarded = lastReward.getDateAwarded();
	//    long dateLastMillis = dateLastAwarded.getTime();
	//    boolean awardedInThePast = nowDateMillis > dateLastMillis;
	//
	//    int dayDiff = miscMethods.dateDifferenceInDays(dateLastAwarded, nowDate);
	//    // log.info("dateLastAwarded=" + dateLastAwarded + ", nowDate=" +
	//    // nowDate + ", day difference=" + dayDiff);
	//    if (1 < dayDiff && awardedInThePast) {
	//      // log.info("user broke their logging in streak. previous daily bonus reward: "
	//      // + lastReward
	//      // + ", now=" + now);
	//      // been a while since user last logged in
	//      totalConsecutiveDaysList.add(1);
	//      rewardUserList.add(true);
	//      return 1;
	//    } else if (1 == dayDiff && awardedInThePast) {
	//      // log.info("awarding user. previous daily bonus reward: " +
	//      // lastReward + ", now=" + now);
	//      // user logged in yesterday
	//      totalConsecutiveDaysList.add(user.getNumConsecutiveDaysPlayed() + 1);
	//      rewardUserList.add(true);
	//      return nthConsecutiveDay % ControllerConstants.STARTUP__DAILY_BONUS_MAX_CONSECUTIVE_DAYS + 1;
	//    } else {
	//      // either user logged in today or user tried faking time, but who
	//      // cares...
	//      totalConsecutiveDaysList.add(user.getNumConsecutiveDaysPlayed());
	//      rewardUserList.add(false);
	//      // log.info("user already collected his daily reward. previous daily bonus reward: "
	//      // + lastReward + ", now=" + now);
	//      return nthConsecutiveDay;
	//    }
	//  }

	//  private DailyBonusReward determineRewardForUser(User aUser) {
	//    Map<Integer, DailyBonusReward> allDailyRewards = DailyBonusRewardRetrieveUtils
	//        .getDailyBonusRewardIdsToDailyBonusRewards();
	//    // sanity check, exit if it fails
	//    if (null == allDailyRewards || allDailyRewards.isEmpty()) {
	//      log.error("unexpected error: There are no daily bonus rewards set up in the daily_bonus_reward table");
	//      return null;
	//    }
	//
	//    int level = aUser.getLevel();
	//    // determine daily bonus reward for this user's level, exit if there it
	//    // doesn't exist
	//    DailyBonusReward reward = selectDailyBonusRewardForLevel(allDailyRewards, level);
	//    if (null == reward) {
	//      log.error("unexpected error: no daily bonus rewards available for level=" + level);
	//    }
	//    return reward;
	//  }

	//  private DailyBonusReward selectDailyBonusRewardForLevel(Map<Integer, DailyBonusReward> allRewards,
	//      int userLevel) {
	//    DailyBonusReward returnValue = null;
	//    for (int id : allRewards.keySet()) {
	//      DailyBonusReward dbr = allRewards.get(id);
	//      int minLevel = dbr.getMinLevel();
	//      int maxLevel = dbr.getMaxLevel();
	//      if (minLevel <= userLevel && userLevel <= maxLevel) {
	//        // we found the reward to return
	//        returnValue = dbr;
	//        break;
	//      }
	//    }
	//    return returnValue;
	//  }
	//
	//  private Map<String, Integer> selectRewardFromDailyBonusReward(DailyBonusReward rewardForUser,
	//      int numConsecutiveDaysPlayed) {
	//    if (null == rewardForUser) {
	//      return null;
	//    }
	//    if (5 < numConsecutiveDaysPlayed || 0 >= numConsecutiveDaysPlayed) {
	//      log.error("unexpected error: number of consecutive days played is not in the range [1,5]. "
	//          + "numConsecutiveDaysPlayed=" + numConsecutiveDaysPlayed);
	//      return null;
	//    }
	//    Map<String, Integer> reward = getCurrentDailyReward(rewardForUser, numConsecutiveDaysPlayed);
	//    return reward;
	//  }

	// sets the rewards the user gets/ will get in the daily bonus info builder
	//  private Map<String, Integer> getCurrentDailyReward(DailyBonusReward reward, int numConsecutiveDaysPlayed) {
	//    Map<String, Integer> returnValue = new HashMap<String, Integer>();
	//    String key = "";
	//    int value = ControllerConstants.NOT_SET;
	//
	//    String silver = miscMethods.silver;
	//    String gold = miscMethods.gold;
	//    String boosterPackIdString = miscMethods.boosterPackId;
	//
	//    // mimicking fall through in switch statement, setting reward user just
	//    // got
	//    // today and will get in future logins in 5 consecutive day spans
	//    if (5 == numConsecutiveDaysPlayed) {
	//      // can't set reward in the builder; currently have booster pack id
	//      // need equip id
	//      key = boosterPackIdString;
	//      List<Integer> boosterPackIds = reward.getDayFiveBoosterPackIds();
	//      value = miscMethods.getRandomIntFromList(boosterPackIds);
	//    }
	//    if (4 == numConsecutiveDaysPlayed) {
	//      key = silver;
	//      value = reward.getDayFourCoins();
	//    }
	//    if (3 == numConsecutiveDaysPlayed) {
	//      key = gold;
	//      value = reward.getDayThreeDiamonds();
	//    }
	//    if (2 == numConsecutiveDaysPlayed) {
	//      key = silver;
	//      value = reward.getDayTwoCoins();
	//    }
	//    if (1 == numConsecutiveDaysPlayed) {
	//      key = silver;
	//      value = reward.getDayOneCoins();
	//    }
	//    returnValue.put(key, value);
	//    return returnValue;
	//  }

	// returns the equip id user "purchased" by logging in
	// mimics purchase booster pack controller except the argument checking and
	// dealing with money
	private int purchaseBoosterPack(int boosterPackId, User aUser,
			int numBoosterItemsUserWants, Timestamp now) {
		int equipId = ControllerConstants.NOT_SET;
		//    try {
		//      // local vars
		//      int userId = aUser.getId();
		//      BoosterPack aPack = BoosterPackRetrieveUtils.getBoosterPackForBoosterPackId(boosterPackId);
		//      Map<Integer, BoosterItem> boosterItemIdsToBoosterItems = BoosterItemRetrieveUtils
		//          .getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);
		//      Map<Integer, Integer> boosterItemIdsToNumCollected = UserBoosterItemRetrieveUtils
		//          .getBoosterItemIdsToQuantityForUser(userId);
		//      Map<Integer, Integer> newBoosterItemIdsToNumCollected = new HashMap<Integer, Integer>();
		//      List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
		//      List<Boolean> collectedBeforeReset = new ArrayList<Boolean>();
		//      List<Long> userEquipIds = new ArrayList<Long>();
		//
		//      // actually selecting equips
		//      boolean resetOccurred = miscMethods.getAllBoosterItemsForUser(boosterItemIdsToBoosterItems,
		//          boosterItemIdsToNumCollected, numBoosterItemsUserWants, aUser, aPack, itemsUserReceives,
		//          collectedBeforeReset);
		//      newBoosterItemIdsToNumCollected = new HashMap<Integer, Integer>(boosterItemIdsToNumCollected);
		//      boolean successful = writeBoosterStuffToDB(aUser, boosterItemIdsToNumCollected,
		//          newBoosterItemIdsToNumCollected, itemsUserReceives, collectedBeforeReset,
		//          resetOccurred, now, userEquipIds);
		//      if (successful) {
		//        //exclude from daily limit check in PurchaseBoosterPackController
		//        boolean excludeFromLimitCheck = true;
		//        miscMethods.writeToUserBoosterPackHistoryOneUser(userId, boosterPackId,
		//            numBoosterItemsUserWants, now, itemsUserReceives, excludeFromLimitCheck,
		//            userEquipIds);
		//        equipId = getEquipId(numBoosterItemsUserWants, itemsUserReceives);
		//      }
		//
		//    } catch (Exception e) {
		//      log.error("unexpected error: ", e);
		//    }
		return equipId;
	}

	//  private int getEquipId(int numBoosterItemsUserWants, List<BoosterItem> itemsUserReceives) {
	//    if (1 != numBoosterItemsUserWants) {
	//      log.error("unexpected error: trying to buy more than one equip from booster pack. boosterItems="
	//          + miscMethods.shallowListToString(itemsUserReceives));
	//      return ControllerConstants.NOT_SET;
	//    }
	//    BoosterItem bi = itemsUserReceives.get(0);
	//    return bi.getEquipId();
	//  }

	private boolean writeBoosterStuffToDB(User aUser,
			Map<Integer, Integer> boosterItemIdsToNumCollected,
			Map<Integer, Integer> newBoosterItemIdsToNumCollected,
			List<BoosterItem> itemsUserReceives,
			List<Boolean> collectedBeforeReset, boolean resetOccurred,
			Timestamp now, List<Long> userEquipIdsForHistoryTable) {
		//    int userId = aUser.getId();
		//    List<Long> userEquipIds = miscMethods.insertNewUserEquips(userId,
		//        itemsUserReceives, now, ControllerConstants.UER__DAILY_BONUS_REWARD);
		//    if (null == userEquipIds || userEquipIds.isEmpty() || userEquipIds.size() != itemsUserReceives.size()) {
		//      log.error("unexpected error: failed to insert equip for user. boosteritems="
		//          + miscMethods.shallowListToString(itemsUserReceives) + "\t userEquipIds="+ userEquipIds);
		//      return false;
		//    }
		//
		//    if (!miscMethods.updateUserBoosterItems(itemsUserReceives, collectedBeforeReset,
		//        boosterItemIdsToNumCollected, newBoosterItemIdsToNumCollected, userId, resetOccurred)) {
		//      // failed to update user_booster_items
		//      log.error("unexpected error: failed to update user_booster_items for userId: " + userId
		//          + " attempting to delete equips given: " + miscMethods.shallowListToString(userEquipIds));
		//      DeleteUtils.get().deleteUserEquips(userEquipIds);
		//      return false;
		//    }
		//    userEquipIdsForHistoryTable.addAll(userEquipIds);
		return true;
	}

	private boolean writeDailyBonusRewardToDB(User aUser,
			Map<String, Integer> currentDayReward, boolean giveToUser,
			Timestamp now, List<Integer> equipIdRewardedList) {
		int equipId = ControllerConstants.NOT_SET;
		if (!giveToUser || null == currentDayReward
				|| 0 == currentDayReward.size()) {
			return false;
		}
		String key = "";
		int value = ControllerConstants.NOT_SET;
		// sanity check, should only be one reward: gold, silver, equipId
		if (1 == currentDayReward.size()) {
			String[] keys = new String[1];
			currentDayReward.keySet().toArray(keys);
			key = keys[0];
			value = currentDayReward.get(key);
		} else {
			log.error("unexpected error: current day's reward for a user is more than one. rewards="
					+ currentDayReward);
			return false;
		}

		int previousSilver = aUser.getCash();
		int previousGold = aUser.getGems();
		if (key.equals(miscMethods.boosterPackId)) {
			// since user got a booster pack id as reward, need to "buy it" for
			// him
			int numBoosterItemsUserWants = 1;
			// calling this will already give the user an equip
			equipId = purchaseBoosterPack(value, aUser,
					numBoosterItemsUserWants, now);
			if (ControllerConstants.NOT_SET == equipId) {
				log.error("unexpected error: failed to 'buy' booster pack for user. packId="
						+ value + ", user=" + aUser);
				return false;
			}
		}
		if (key.equals(miscMethods.cash)) {
			if (!aUser.updateRelativeCashNaive(value)) {
				log.error("unexpected error: could not give silver bonus of "
						+ value + " to user " + aUser);
				return false;
			} else {// gave user silver
				writeToUserCurrencyHistory(aUser, key, previousSilver,
						currentDayReward);
			}
		}
		if (key.equals(miscMethods.gems)) {
			if (!aUser.updateRelativeGemsNaive(value, 0)) {
				log.error("unexpected error: could not give silver bonus of "
						+ value + " to user " + aUser);
				return false;
			} else {// gave user gold
				writeToUserCurrencyHistory(aUser, key, previousGold,
						currentDayReward);
			}
		}
		equipIdRewardedList.add(equipId);
		return true;
	}

	private void writeToUserDailyBonusRewardHistory(User aUser,
			Map<String, Integer> rewardForUser, int nthConsecutiveDay,
			Timestamp now, int equipIdRewarded) {
		//    int userId = aUser.getId();
		//    int currencyRewarded = ControllerConstants.NOT_SET;
		//    boolean isCoins = false;
		//    int boosterPackIdRewarded = ControllerConstants.NOT_SET;
		//
		//    String boosterPackId = miscMethods.boosterPackId;
		//    String silver = miscMethods.silver;
		//    String gold = miscMethods.gold;
		//    if (rewardForUser.containsKey(boosterPackId)) {
		//      boosterPackIdRewarded = rewardForUser.get(boosterPackId);
		//    }
		//    if (rewardForUser.containsKey(silver)) {
		//      currencyRewarded = rewardForUser.get(silver);
		//      isCoins = true;
		//    }
		//    if (rewardForUser.containsKey(gold)) {
		//      currencyRewarded = rewardForUser.get(gold);
		//    }
		//    int numInserted = InsertUtils.get().insertIntoUserDailyRewardHistory(userId, currencyRewarded,
		//        isCoins, boosterPackIdRewarded, equipIdRewarded, nthConsecutiveDay, now);
		//    if (1 != numInserted) {
		//      log.error("unexpected error: could not record that user got a reward for this day: " + now);
		//    }
	}

	//  private void setDailyBonusStuff(Builder resBuilder, User aUser, boolean rewardUser,
	//      DailyBonusReward rewardForUser) {
	//    // log.info("rewardUser=" + rewardUser + "rewardForUser=" +
	//    // rewardForUser + "user=" + aUser);
	//
	//    int userId = aUser.getId();
	//    // there should be a reward inserted if things saved sans a hitch
	//    UserDailyBonusRewardHistory udbrh = UserDailyBonusRewardHistoryRetrieveUtils
	//        .getLastDailyRewardAwardedForUserId(userId);
	//
	//    if (null == udbrh || null == rewardForUser) {
	//      log.error("unexpected error: no daily bonus reward history exists for user=" + aUser);
	//      return;
	//    }
	//    int consecutiveDaysPlayed = udbrh.getNthConsecutiveDay();
	//
	//    DailyBonusInfo.Builder dbib = DailyBonusInfo.newBuilder();
	//    if (5 == consecutiveDaysPlayed) {
	//      // user just got an equip
	//      int boosterPackId = udbrh.getBoosterPackIdRewarded();
	//      BoosterPack bp = BoosterPackRetrieveUtils.getBoosterPackForBoosterPackId(boosterPackId);
	//      Map<Integer, BoosterItem> biMap = BoosterItemRetrieveUtils
	//          .getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);
	//      Collection<BoosterItem> biList = biMap.values();
	//      BoosterPackProto aBoosterPackProto = createInfoProtoUtils.createBoosterPackProto(bp, biList);
	//      dbib.setBoosterPack(aBoosterPackProto);
	//
	//      // log.info("setting 5th consecutive day reward");
	//      int equipId = udbrh.getEquipIdRewarded();
	//      dbib.setEquipId(equipId);
	//    }
	//    if (4 >= consecutiveDaysPlayed) {
	//      // log.info("setting 4th consecutive day reward");
	//      dbib.setDayFourCoins(rewardForUser.getDayFourCoins());
	//    }
	//    if (3 >= consecutiveDaysPlayed) {
	//      // log.info("setting 3rd consecutive day reward");
	//      dbib.setDayThreeDiamonds(rewardForUser.getDayThreeDiamonds());
	//    }
	//    if (2 >= consecutiveDaysPlayed) {
	//      // log.info("setting 2nd consecutive day reward");
	//      dbib.setDayTwoCoins(rewardForUser.getDayTwoCoins());
	//    }
	//    if (1 == consecutiveDaysPlayed) {
	//      // log.info("setting first consecutive day reward");
	//      dbib.setDayOneCoins(rewardForUser.getDayOneCoins());
	//    }
	//    // log.info("nth consecutive day=" + consecutiveDaysPlayed);
	//    Date dateAwarded = udbrh.getDateAwarded();
	//    long dateAwardedMillis = dateAwarded.getTime();
	//    dbib.setTimeAwarded(dateAwardedMillis);
	//    dbib.setNumConsecutiveDaysPlayed(consecutiveDaysPlayed);
	//    resBuilder.setDailyBonusInfo(dbib.build());
	//  }

	private void syncApsalaridLastloginConsecutivedaysloggedinResetBadges(
			User user, String apsalarId, Timestamp loginTime,
			int newNumConsecutiveDaysLoggedIn) {
		if (user.getApsalarId() != null && apsalarId == null) {
			apsalarId = user.getApsalarId();
		}
		if (!user
				.updateAbsoluteApsalaridLastloginBadgesNumConsecutiveDaysLoggedIn(
						apsalarId, loginTime, 0, newNumConsecutiveDaysLoggedIn)) {
			log.error("problem with updating apsalar id to " + apsalarId
					+ ", last login to " + loginTime
					+ ", and badge count to 0 for " + user
					+ " and newNumConsecutiveDaysLoggedIn is "
					+ newNumConsecutiveDaysLoggedIn);
		}
		if (!InsertUtils.get().insertLastLoginLastLogoutToUserSessions(
				user.getId(), loginTime, null)) {
			log.error("problem with inserting last login time for user " + user
					+ ", loginTime=" + loginTime);
		}

		if (user.getNumBadges() != 0) {
			if (user.getDeviceToken() != null) {

				 * handled locally?

				// ApnsServiceBuilder builder =
				// APNS.newService().withCert(APNSProperties.PATH_TO_CERT,
				// APNSProperties.CERT_PASSWORD);
				// if (Globals.IS_SANDBOX()) {
				// builder.withSandboxDestination();
				// }
				// ApnsService service = builder.build();
				// service.push(newDeviceToken,
				// APNS.newPayload().badge(0).build());
				// service.stop();
			}
		}
	}

	private void setConstants(Builder startupBuilder,
			StartupStatus startupStatus) {
		startupBuilder.setStartupConstants(miscMethods
				.createStartupConstantsProto(globals));
		if (startupStatus == StartupStatus.USER_NOT_IN_DB) {
			TutorialConstants tc = miscMethods.createTutorialConstantsProto();
			startupBuilder.setTutorialConstants(tc);
		}
	}

	//TODO: FIX THIS
	public void writeToUserCurrencyHistory(User aUser, String goldSilver,
			int previousMoney, Map<String, Integer> goldSilverChange) {
		//String cash = miscMethods.cash;
		//String gems = miscMethods.gems;

		//    Timestamp date = new Timestamp((new Date()).getTime());
		//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
		//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
		//    String reasonForChange = ControllerConstants.UCHRFC__STARTUP_DAILY_BONUS;
		//
		//    if (goldSilver.equals(cash)) {
		//      previousGoldSilver.put(cash, previousMoney);
		//      reasonsForChanges.put(cash, reasonForChange);
		//    } else {
		//      previousGoldSilver.put(gems, previousMoney);
		//      reasonsForChanges.put(gems, reasonForChange);
		//    }
		//
		//    miscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, goldSilverChange,
		//        previousGoldSilver, reasonsForChanges);
	}

	//for the setter dependency injection or something****************************************************************
	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public Globals getGlobals() {
		return globals;
	}

	public void setGlobals(Globals globals) {
		this.globals = globals;
	}

	public QuestJobForUserRetrieveUtil getQuestJobForUserRetrieveUtil() {
		return questJobForUserRetrieveUtil;
	}

	public void setQuestJobForUserRetrieveUtil(
			QuestJobForUserRetrieveUtil questJobForUserRetrieveUtil) {
		this.questJobForUserRetrieveUtil = questJobForUserRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

	public PvpBattleHistoryRetrieveUtil2 getPvpBattleHistoryRetrieveUtil() {
		return pvpBattleHistoryRetrieveUtil;
	}

	public void setPvpBattleHistoryRetrieveUtil(
			PvpBattleHistoryRetrieveUtil2 pvpBattleHistoryRetrieveUtil) {
		this.pvpBattleHistoryRetrieveUtil = pvpBattleHistoryRetrieveUtil;
	}

	public AchievementForUserRetrieveUtil getAchievementForUserRetrieveUtil() {
		return achievementForUserRetrieveUtil;
	}

	public void setAchievementForUserRetrieveUtil(
			AchievementForUserRetrieveUtil achievementForUserRetrieveUtil) {
		this.achievementForUserRetrieveUtil = achievementForUserRetrieveUtil;
	}

	public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
		return miniJobForUserRetrieveUtil;
	}

	public void setMiniJobForUserRetrieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public ItemForUserUsageRetrieveUtil getItemForUserUsageRetrieveUtil() {
		return itemForUserUsageRetrieveUtil;
	}

	public void setItemForUserUsageRetrieveUtil(
			ItemForUserUsageRetrieveUtil itemForUserUsageRetrieveUtil) {
		this.itemForUserUsageRetrieveUtil = itemForUserUsageRetrieveUtil;
	}

	public ClanHelpRetrieveUtil getClanHelpRetrieveUtil() {
		return clanHelpRetrieveUtil;
	}

	public void setClanHelpRetrieveUtil(
			ClanHelpRetrieveUtil clanHelpRetrieveUtil) {
		this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
	}

	public ClanAvengeRetrieveUtil getClanAvengeRetrieveUtil() {
		return clanAvengeRetrieveUtil;
	}

	public void setClanAvengeRetrieveUtil(
			ClanAvengeRetrieveUtil clanAvengeRetrieveUtil) {
		this.clanAvengeRetrieveUtil = clanAvengeRetrieveUtil;
	}

	public ClanAvengeUserRetrieveUtil getClanAvengeUserRetrieveUtil() {
		return clanAvengeUserRetrieveUtil;
	}

	public void setClanAvengeUserRetrieveUtil(
			ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil) {
		this.clanAvengeUserRetrieveUtil = clanAvengeUserRetrieveUtil;
	}

	public BattleItemQueueForUserRetrieveUtil getBattleItemQueueForUserRetrieveUtil() {
		return battleItemQueueForUserRetrieveUtil;
	}

	public void setBattleItemQueueForUserRetrieveUtil(
			BattleItemQueueForUserRetrieveUtil battleItemQueueForUserRetrieveUtil) {
		this.battleItemQueueForUserRetrieveUtil = battleItemQueueForUserRetrieveUtil;
	}

	public BattleItemForUserRetrieveUtil getBattleItemForUserRetrieveUtil() {
		return battleItemForUserRetrieveUtil;
	}

	public void setBattleItemForUserRetrieveUtil(
			BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil) {
		this.battleItemForUserRetrieveUtil = battleItemForUserRetrieveUtil;
	}

	public QuestForUserRetrieveUtils2 getQuestForUserRetrieveUtils() {
		return questForUserRetrieveUtils;
	}

	public void setQuestForUserRetrieveUtils(
			QuestForUserRetrieveUtils2 questForUserRetrieveUtils) {
		this.questForUserRetrieveUtils = questForUserRetrieveUtils;
	}

	public MonsterEnhancingForUserRetrieveUtils2 getMonsterEnhancingForUserRetrieveUtils() {
		return monsterEnhancingForUserRetrieveUtils;
	}

	public void setMonsterEnhancingForUserRetrieveUtils(
			MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtils) {
		this.monsterEnhancingForUserRetrieveUtils = monsterEnhancingForUserRetrieveUtils;
	}

	public MonsterHealingForUserRetrieveUtils2 getMonsterHealingForUserRetrieveUtils() {
		return monsterHealingForUserRetrieveUtils;
	}

	public void setMonsterHealingForUserRetrieveUtils(
			MonsterHealingForUserRetrieveUtils2 monsterHealingForUserRetrieveUtils) {
		this.monsterHealingForUserRetrieveUtils = monsterHealingForUserRetrieveUtils;
	}

	public MonsterEvolvingForUserRetrieveUtils2 getMonsterEvolvingForUserRetrieveUtils() {
		return monsterEvolvingForUserRetrieveUtils;
	}

	public void setMonsterEvolvingForUserRetrieveUtils(
			MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtils) {
		this.monsterEvolvingForUserRetrieveUtils = monsterEvolvingForUserRetrieveUtils;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public TaskForUserCompletedRetrieveUtils getTaskForUserCompletedRetrieveUtils() {
		return taskForUserCompletedRetrieveUtils;
	}

	public void setTaskForUserCompletedRetrieveUtils(
			TaskForUserCompletedRetrieveUtils taskForUserCompletedRetrieveUtils) {
		this.taskForUserCompletedRetrieveUtils = taskForUserCompletedRetrieveUtils;
	}

	public TaskForUserOngoingRetrieveUtils2 getTaskForUserOngoingRetrieveUtils() {
		return taskForUserOngoingRetrieveUtils;
	}

	public void setTaskForUserOngoingRetrieveUtils(
			TaskForUserOngoingRetrieveUtils2 taskForUserOngoingRetrieveUtils) {
		this.taskForUserOngoingRetrieveUtils = taskForUserOngoingRetrieveUtils;
	}

	public TaskForUserClientStateRetrieveUtil getTaskForUserClientStateRetrieveUtil() {
		return taskForUserClientStateRetrieveUtil;
	}

	public void setTaskForUserClientStateRetrieveUtil(
			TaskForUserClientStateRetrieveUtil taskForUserClientStateRetrieveUtil) {
		this.taskForUserClientStateRetrieveUtil = taskForUserClientStateRetrieveUtil;
	}

	public TaskStageForUserRetrieveUtils2 getTaskStageForUserRetrieveUtils() {
		return taskStageForUserRetrieveUtils;
	}

	public void setTaskStageForUserRetrieveUtils(
			TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtils) {
		this.taskStageForUserRetrieveUtils = taskStageForUserRetrieveUtils;
	}

	public PvpBoardObstacleForUserRetrieveUtil getPvpBoardObstacleForUserRetrieveUtil() {
		return pvpBoardObstacleForUserRetrieveUtil;
	}

	public void setPvpBoardObstacleForUserRetrieveUtil(
			PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil) {
		this.pvpBoardObstacleForUserRetrieveUtil = pvpBoardObstacleForUserRetrieveUtil;
	}

	public EventPersistentForUserRetrieveUtils2 getEventPersistentForUserRetrieveUtils() {
		return eventPersistentForUserRetrieveUtils;
	}

	public void setEventPersistentForUserRetrieveUtils(
			EventPersistentForUserRetrieveUtils2 eventPersistentForUserRetrieveUtils) {
		this.eventPersistentForUserRetrieveUtils = eventPersistentForUserRetrieveUtils;
	}

	public PvpBattleForUserRetrieveUtils2 getPvpBattleForUserRetrieveUtils() {
		return pvpBattleForUserRetrieveUtils;
	}

	public void setPvpBattleForUserRetrieveUtils(
			PvpBattleForUserRetrieveUtils2 pvpBattleForUserRetrieveUtils) {
		this.pvpBattleForUserRetrieveUtils = pvpBattleForUserRetrieveUtils;
	}

	public IAPHistoryRetrieveUtils getIapHistoryRetrieveUtils() {
		return iapHistoryRetrieveUtils;
	}

	public void setIapHistoryRetrieveUtils(
			IAPHistoryRetrieveUtils iapHistoryRetrieveUtils) {
		this.iapHistoryRetrieveUtils = iapHistoryRetrieveUtils;
	}

	public ClanEventPersistentForClanRetrieveUtils2 getClanEventPersistentForClanRetrieveUtils() {
		return clanEventPersistentForClanRetrieveUtils;
	}

	public void setClanEventPersistentForClanRetrieveUtils(
			ClanEventPersistentForClanRetrieveUtils2 clanEventPersistentForClanRetrieveUtils) {
		this.clanEventPersistentForClanRetrieveUtils = clanEventPersistentForClanRetrieveUtils;
	}

	public ClanEventPersistentForUserRetrieveUtils2 getClanEventPersistentForUserRetrieveUtils() {
		return clanEventPersistentForUserRetrieveUtils;
	}

	public void setClanEventPersistentForUserRetrieveUtils(
			ClanEventPersistentForUserRetrieveUtils2 clanEventPersistentForUserRetrieveUtils) {
		this.clanEventPersistentForUserRetrieveUtils = clanEventPersistentForUserRetrieveUtils;
	}

	public CepfuRaidStageHistoryRetrieveUtils2 getCepfuRaidStageHistoryRetrieveUtils() {
		return cepfuRaidStageHistoryRetrieveUtils;
	}

	public void setCepfuRaidStageHistoryRetrieveUtils(
			CepfuRaidStageHistoryRetrieveUtils2 cepfuRaidStageHistoryRetrieveUtils) {
		this.cepfuRaidStageHistoryRetrieveUtils = cepfuRaidStageHistoryRetrieveUtils;
	}

	public ClanEventPersistentUserRewardRetrieveUtils2 getClanEventPersistentUserRewardRetrieveUtils() {
		return clanEventPersistentUserRewardRetrieveUtils;
	}

	public void setClanEventPersistentUserRewardRetrieveUtils(
			ClanEventPersistentUserRewardRetrieveUtils2 clanEventPersistentUserRewardRetrieveUtils) {
		this.clanEventPersistentUserRewardRetrieveUtils = clanEventPersistentUserRewardRetrieveUtils;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public UserFacebookInviteForSlotRetrieveUtils2 getUserFacebookInviteForSlotRetrieveUtils() {
		return userFacebookInviteForSlotRetrieveUtils;
	}

	public void setUserFacebookInviteForSlotRetrieveUtils(
			UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils) {
		this.userFacebookInviteForSlotRetrieveUtils = userFacebookInviteForSlotRetrieveUtils;
	}

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtils() {
		return clanChatPostRetrieveUtils;
	}

	public void setClanChatPostRetrieveUtils(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtils) {
		this.clanChatPostRetrieveUtils = clanChatPostRetrieveUtils;
	}

	public PrivateChatPostRetrieveUtils2 getPrivateChatPostRetrieveUtils() {
		return privateChatPostRetrieveUtils;
	}

	public void setPrivateChatPostRetrieveUtils(
			PrivateChatPostRetrieveUtils2 privateChatPostRetrieveUtils) {
		this.privateChatPostRetrieveUtils = privateChatPostRetrieveUtils;
	}

	public SecretGiftForUserRetrieveUtil getSecretGiftForUserRetrieveUtil() {
		return secretGiftForUserRetrieveUtil;
	}

	public void setSecretGiftForUserRetrieveUtil(
			SecretGiftForUserRetrieveUtil secretGiftForUserRetrieveUtil) {
		this.secretGiftForUserRetrieveUtil = secretGiftForUserRetrieveUtil;
	}

	public ResearchForUserRetrieveUtils getResearchForUserRetrieveUtil() {
		return researchForUserRetrieveUtil;
	}

	public void setResearchForUserRetrieveUtil(
			ResearchForUserRetrieveUtils researchForUserRetrieveUtil) {
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}

	public ClanMemberTeamDonationRetrieveUtil getClanMemberTeamDonationRetrieveUtil() {
		return clanMemberTeamDonationRetrieveUtil;
	}

	public void setClanMemberTeamDonationRetrieveUtil(
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil) {
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
	}

	public MonsterSnapshotForUserRetrieveUtil getMonsterSnapshotForUserRetrieveUtil() {
		return monsterSnapshotForUserRetrieveUtil;
	}

	public void setMonsterSnapshotForUserRetrieveUtil(
			MonsterSnapshotForUserRetrieveUtil monsterSnapshotForUserRetrieveUtil) {
		this.monsterSnapshotForUserRetrieveUtil = monsterSnapshotForUserRetrieveUtil;
	}

	public MiniEventForUserRetrieveUtil getMiniEventForUserRetrieveUtil() {
		return miniEventForUserRetrieveUtil;
	}

	public void setMiniEventForUserRetrieveUtil(
			MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil) {
		this.miniEventForUserRetrieveUtil = miniEventForUserRetrieveUtil;
	}

	public MiniEventGoalForUserRetrieveUtil getMiniEventGoalForUserRetrieveUtil() {
		return miniEventGoalForUserRetrieveUtil;
	}

	public void setMiniEventGoalForUserRetrieveUtil(
			MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil) {
		this.miniEventGoalForUserRetrieveUtil = miniEventGoalForUserRetrieveUtil;
	}

	public TranslationSettingsForUserRetrieveUtil getTranslationSettingsForUserRetrieveUtil() {
		return translationSettingsForUserRetrieveUtil;
	}

	public void setTranslationSettingsForUserRetrieveUtil(
			TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil) {
		this.translationSettingsForUserRetrieveUtil = translationSettingsForUserRetrieveUtil;
	}

	public ServerToggleRetrieveUtils getServerToggleRetrieveUtil() {
		return serverToggleRetrieveUtil;
	}

	public void setServerToggleRetrieveUtil(
			ServerToggleRetrieveUtils serverToggleRetrieveUtil) {
		this.serverToggleRetrieveUtil = serverToggleRetrieveUtil;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	public StartupStuffRetrieveUtils getStartupStuffRetrieveUtils() {
		return startupStuffRetrieveUtils;
	}

	public void setStartupStuffRetrieveUtils(
			StartupStuffRetrieveUtils startupStuffRetrieveUtils) {
		this.startupStuffRetrieveUtils = startupStuffRetrieveUtils;
	}

	public QuestRetrieveUtils getQuestRetrieveUtils() {
		return questRetrieveUtils;
	}

	public void setQuestRetrieveUtils(QuestRetrieveUtils questRetrieveUtils) {
		this.questRetrieveUtils = questRetrieveUtils;
	}

	public PvpLeagueRetrieveUtils getPvpLeagueRetrieveUtils() {
		return pvpLeagueRetrieveUtils;
	}

	public void setPvpLeagueRetrieveUtils(
			PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils) {
		this.pvpLeagueRetrieveUtils = pvpLeagueRetrieveUtils;
	}

	public MiscMethods getMiscMethods() {
		return miscMethods;
	}

	public void setMiscMethods(MiscMethods miscMethods) {
		this.miscMethods = miscMethods;
	}

	public SecretGiftUtils getSecretGiftUtils() {
		return secretGiftUtils;
	}

	public void setSecretGiftUtils(SecretGiftUtils secretGiftUtils) {
		this.secretGiftUtils = secretGiftUtils;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public MiniEventGoalRetrieveUtils getMiniEventGoalRetrieveUtils() {
		return miniEventGoalRetrieveUtils;
	}

	public void setMiniEventGoalRetrieveUtils(
			MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils) {
		this.miniEventGoalRetrieveUtils = miniEventGoalRetrieveUtils;
	}

	public MiniEventForPlayerLvlRetrieveUtils getMiniEventForPlayerLvlRetrieveUtils() {
		return miniEventForPlayerLvlRetrieveUtils;
	}

	public void setMiniEventForPlayerLvlRetrieveUtils(
			MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils) {
		this.miniEventForPlayerLvlRetrieveUtils = miniEventForPlayerLvlRetrieveUtils;
	}

	public MiniEventRetrieveUtils getMiniEventRetrieveUtils() {
		return miniEventRetrieveUtils;
	}

	public void setMiniEventRetrieveUtils(
			MiniEventRetrieveUtils miniEventRetrieveUtils) {
		this.miniEventRetrieveUtils = miniEventRetrieveUtils;
	}

	public MiniEventTierRewardRetrieveUtils getMiniEventTierRewardRetrieveUtils() {
		return miniEventTierRewardRetrieveUtils;
	}

	public void setMiniEventTierRewardRetrieveUtils(
			MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils) {
		this.miniEventTierRewardRetrieveUtils = miniEventTierRewardRetrieveUtils;
	}

	public MiniEventLeaderboardRewardRetrieveUtils getMiniEventLeaderboardRewardRetrieveUtils() {
		return miniEventLeaderboardRewardRetrieveUtils;
	}

	public void setMiniEventLeaderboardRewardRetrieveUtils(
			MiniEventLeaderboardRewardRetrieveUtils miniEventLeaderboardRewardRetrieveUtils) {
		this.miniEventLeaderboardRewardRetrieveUtils = miniEventLeaderboardRewardRetrieveUtils;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

//	public SalesPackageRetrieveUtils getSalesPackageRetrieveUtils() {
//		return salesPackageRetrieveUtils;
//	}
//
//	public void setSalesPackageRetrieveUtils(
//			SalesPackageRetrieveUtils salesPackageRetrieveUtils) {
//		this.salesPackageRetrieveUtils = salesPackageRetrieveUtils;
//	}

	public SalesItemRetrieveUtils getSalesItemRetrieveUtils() {
		return salesItemRetrieveUtils;
	}

	public void setSalesItemRetrieveUtils(
			SalesItemRetrieveUtils salesItemRetrieveUtils) {
		this.salesItemRetrieveUtils = salesItemRetrieveUtils;
	}

	public SalesDisplayItemRetrieveUtils getSalesDisplayItemRetrieveUtils() {
		return salesDisplayItemRetrieveUtils;
	}

	public void setSalesDisplayItemRetrieveUtils(
			SalesDisplayItemRetrieveUtils salesDisplayItemRetrieveUtils) {
		this.salesDisplayItemRetrieveUtils = salesDisplayItemRetrieveUtils;
	}
*/
}
