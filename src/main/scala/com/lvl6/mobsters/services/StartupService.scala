package com.lvl6.mobsters.services

import java.io.BufferedReader
import java.io.InputStreamReader
import java.sql.Timestamp
import java.util.ArrayList
import java.util.Collection
import java.util.Date
import java.util.HashMap
import java.util.HashSet
import java.util.UUID

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet
import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.concurrent.Future

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.hazelcast.core.IList
import com.lvl6.events.RequestEvent
import com.lvl6.events.request.StartupRequestEvent
import com.lvl6.events.response.ForceLogoutResponseEvent
import com.lvl6.events.response.StartupResponseEvent
import com.lvl6.info.AchievementForUser
import com.lvl6.info.Clan
import com.lvl6.info.ClanEventPersistentUserReward
import com.lvl6.info.ItemForUser
import com.lvl6.info.ItemSecretGiftForUser
import com.lvl6.info.MiniJobForUser
import com.lvl6.info.MonsterForUser
import com.lvl6.info.MonsterHealingForUser
import com.lvl6.info.PvpBattleForUser
import com.lvl6.info.PvpLeagueForUser
import com.lvl6.info.QuestForUser
import com.lvl6.info.SalesPackage
import com.lvl6.info.TaskForUserClientState
import com.lvl6.info.TaskForUserOngoing
import com.lvl6.info.TaskStageForUser
import com.lvl6.info.User
import com.lvl6.info.UserClan
import com.lvl6.leaderboards.LeaderBoardImpl
import com.lvl6.misc.MiscMethods
import com.lvl6.properties.ControllerConstants
import com.lvl6.properties.Globals
import com.lvl6.properties.IAPValues
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto
import com.lvl6.proto.ChatProto.ChatScope
import com.lvl6.proto.ChatProto.DefaultLanguagesProto
import com.lvl6.proto.ChatProto.GroupChatMessageProto
import com.lvl6.proto.ClanProto.ClanDataProto
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.RetrieveMiniEventStatus
import com.lvl6.proto.EventStartupProto.ForceLogoutResponseProto
import com.lvl6.proto.EventStartupProto.StartupRequestProto
import com.lvl6.proto.EventStartupProto.StartupRequestProto.VersionNumberProto
import com.lvl6.proto.EventStartupProto.StartupResponseProto
import com.lvl6.proto.EventStartupProto.StartupResponseProto.Builder
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupStatus
import com.lvl6.proto.EventStartupProto.StartupResponseProto.UpdateStatus
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto
import com.lvl6.proto.SalesProto.SalesPackageProto
import com.lvl6.pvp.HazelcastPvpUtil
import com.lvl6.pvp.PvpUser
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil
import com.lvl6.retrieveutils.BattleReplayForUserRetrieveUtil
import com.lvl6.retrieveutils.CepfuRaidStageHistoryRetrieveUtils2
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentUserRewardRetrieveUtils2
import com.lvl6.retrieveutils.ClanGiftForUserRetrieveUtils
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil
import com.lvl6.retrieveutils.ClanRetrieveUtils2
import com.lvl6.retrieveutils.EventPersistentForUserRetrieveUtils2
import com.lvl6.retrieveutils.FirstTimeUsersRetrieveUtils
import com.lvl6.retrieveutils.GiftForTangoUserRetrieveUtil
import com.lvl6.retrieveutils.GiftForUserRetrieveUtils
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil
import com.lvl6.retrieveutils.ItemForUserUsageRetrieveUtil
import com.lvl6.retrieveutils.ItemSecretGiftForUserRetrieveUtil
import com.lvl6.retrieveutils.LoginHistoryRetrieveUtils
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil
import com.lvl6.retrieveutils.MiniEventGoalForUserRetrieveUtil
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils2
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils2
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil
import com.lvl6.retrieveutils.PrivateChatPostRetrieveUtils2
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils2
import com.lvl6.retrieveutils.PvpBattleHistoryRetrieveUtil2
import com.lvl6.retrieveutils.PvpBoardObstacleForUserRetrieveUtil
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2
import com.lvl6.retrieveutils.QuestForUserRetrieveUtils2
import com.lvl6.retrieveutils.QuestJobForUserRetrieveUtil
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils
import com.lvl6.retrieveutils.TaskForUserClientStateRetrieveUtil
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils2
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils2
import com.lvl6.retrieveutils.TranslationSettingsForUserRetrieveUtil
import com.lvl6.retrieveutils.UserClanRetrieveUtils2
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils2
import com.lvl6.retrieveutils.UserRetrieveUtils2
import com.lvl6.retrieveutils.rarechange.CustomMenuRetrieveUtils
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils
import com.lvl6.retrieveutils.rarechange.MiniEventLeaderboardRewardRetrieveUtils
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils
import com.lvl6.retrieveutils.rarechange.SalesDisplayItemRetrieveUtils
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils
import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils
import com.lvl6.retrieveutils.rarechange.TangoGiftRetrieveUtils
import com.lvl6.server.EventWriter
import com.lvl6.server.GameServer
import com.lvl6.server.Locker
import com.lvl6.server.concurrent.FutureThreadPool.ec
import com.lvl6.server.controller.actionobjects.RetrieveMiniEventAction
import com.lvl6.server.controller.actionobjects.SetClanChatMessageAction
import com.lvl6.server.controller.actionobjects.SetClanGiftsAction
import com.lvl6.server.controller.actionobjects.SetClanHelpingsAction
import com.lvl6.server.controller.actionobjects.SetClanMemberTeamDonationAction
import com.lvl6.server.controller.actionobjects.SetClanRetaliationsAction
import com.lvl6.server.controller.actionobjects.SetFacebookExtraSlotsAction
import com.lvl6.server.controller.actionobjects.SetGiftsAction
import com.lvl6.server.controller.actionobjects.SetGlobalChatMessageAction
import com.lvl6.server.controller.actionobjects.SetPrivateChatMessageAction
import com.lvl6.server.controller.actionobjects.SetPvpBattleHistoryAction
import com.lvl6.server.controller.actionobjects.StartUpResource
import com.lvl6.server.controller.actionobjects.UserSegmentationGroupAction
import com.lvl6.server.controller.utils.InAppPurchaseUtils
import com.lvl6.server.controller.utils.MonsterStuffUtils
import com.lvl6.server.controller.utils.SecretGiftUtils
import com.lvl6.server.controller.utils.TimeUtils
import com.lvl6.server.metrics.Metrics.timed
import com.lvl6.utils.CreateInfoProtoUtils
import com.lvl6.utils.utilmethods.DeleteUtil
import com.lvl6.utils.utilmethods.InsertUtil
import com.lvl6.utils.utilmethods.UpdateUtil
import com.typesafe.scalalogging.slf4j.LazyLogging

import javax.annotation.Resource

case class StartupData(
		resBuilder:Builder, 
		udid:String,
		fbId:String,
		playerId:String, 
		now:Timestamp,
		nowDate:Date,
		isLogin:Boolean, 
		goingThroughTutorial:Boolean,
		userIdSet:Boolean,
		startupStatus:StartupStatus,
		resEvent:StartupResponseEvent,
		user:User,
		apsalarId:String,
		newNumConsecutiveDaysLoggedIn:Int,
		freshRestart:Boolean)
@Component
class StartupService extends LazyLogging{

	@Autowired var  achievementForUserRetrieveUtil : AchievementForUserRetrieveUtil  = null
	@Autowired var  battleItemQueueForUserRetrieveUtil : BattleItemQueueForUserRetrieveUtil = null
	@Autowired var  battleItemForUserRetrieveUtil : BattleItemForUserRetrieveUtil = null
    @Autowired var  battleReplayForUserRetrieveUtil : BattleReplayForUserRetrieveUtil = null 
	@Autowired var  cepfuRaidStageHistoryRetrieveUtils : CepfuRaidStageHistoryRetrieveUtils2  = null
	@Autowired var  clanAvengeRetrieveUtil : ClanAvengeRetrieveUtil  = null
	@Autowired var  clanAvengeUserRetrieveUtil : ClanAvengeUserRetrieveUtil  = null
	@Autowired var  clanChatPostRetrieveUtils : ClanChatPostRetrieveUtils2  = null
	@Autowired var  clanEventPersistentForClanRetrieveUtils : ClanEventPersistentForClanRetrieveUtils2  = null
	@Autowired var  clanEventPersistentForUserRetrieveUtils : ClanEventPersistentForUserRetrieveUtils2  = null
	@Autowired var  clanEventPersistentUserRewardRetrieveUtils : ClanEventPersistentUserRewardRetrieveUtils2  = null
	@Autowired var  clanGiftForUserRetrieveUtil : ClanGiftForUserRetrieveUtils = null
	@Autowired var  clanHelpRetrieveUtil : ClanHelpRetrieveUtil  = null
	@Autowired var  clanMemberTeamDonationRetrieveUtil : ClanMemberTeamDonationRetrieveUtil  = null
	@Autowired var  clanRetrieveUtils : ClanRetrieveUtils2  = null
	@Autowired var  eventPersistentForUserRetrieveUtils : EventPersistentForUserRetrieveUtils2  = null
    @Autowired var  giftForUserRetrieveUtil : GiftForUserRetrieveUtils  = null
    @Autowired var  giftForTangoUserRetrieveUtil : GiftForTangoUserRetrieveUtil = null;
	@Autowired var  itemForUserRetrieveUtil : ItemForUserRetrieveUtil  = null
	@Autowired var  itemForUserUsageRetrieveUtil : ItemForUserUsageRetrieveUtil  = null
	@Autowired var  iapHistoryRetrieveUtils : IAPHistoryRetrieveUtils  = null
	@Autowired var  itemSecretGiftForUserRetrieveUtil : ItemSecretGiftForUserRetrieveUtil  = null
	@Autowired var  miniEventForUserRetrieveUtil : MiniEventForUserRetrieveUtil = null
	@Autowired var  miniEventGoalForUserRetrieveUtil : MiniEventGoalForUserRetrieveUtil = null
	@Autowired var  miniJobForUserRetrieveUtil : MiniJobForUserRetrieveUtil  = null
	@Autowired var  monsterEnhancingForUserRetrieveUtils : MonsterEnhancingForUserRetrieveUtils2  = null
	@Autowired var  monsterEvolvingForUserRetrieveUtils : MonsterEvolvingForUserRetrieveUtils2  = null
	@Autowired var  monsterForUserRetrieveUtils : MonsterForUserRetrieveUtils2  = null
	@Autowired var  monsterHealingForUserRetrieveUtils : MonsterHealingForUserRetrieveUtils2  = null
	@Autowired var  monsterSnapshotForUserRetrieveUtil : MonsterSnapshotForUserRetrieveUtil  = null
	@Autowired var  privateChatPostRetrieveUtils : PrivateChatPostRetrieveUtils2  = null
	@Autowired var  pvpBattleForUserRetrieveUtils : PvpBattleForUserRetrieveUtils2  = null
	@Autowired var  pvpLeagueForUserRetrieveUtil : PvpLeagueForUserRetrieveUtil2  = null
	@Autowired var  pvpBattleHistoryRetrieveUtil : PvpBattleHistoryRetrieveUtil2  = null
	@Autowired var  pvpBoardObstacleForUserRetrieveUtil : PvpBoardObstacleForUserRetrieveUtil = null 
	@Autowired var  questForUserRetrieveUtils : QuestForUserRetrieveUtils2  = null
	@Autowired var  questJobForUserRetrieveUtil : QuestJobForUserRetrieveUtil  = null
	@Autowired var  researchForUserRetrieveUtil : ResearchForUserRetrieveUtils = null
	@Autowired var  taskForUserCompletedRetrieveUtils : TaskForUserCompletedRetrieveUtils  = null
	@Autowired var  taskForUserClientStateRetrieveUtil : TaskForUserClientStateRetrieveUtil  = null
	@Autowired var  taskForUserOngoingRetrieveUtils : TaskForUserOngoingRetrieveUtils2  = null
	@Autowired var  taskStageForUserRetrieveUtils : TaskStageForUserRetrieveUtils2  = null
	@Autowired var  translationSettingsForUserRetrieveUtil : TranslationSettingsForUserRetrieveUtil = null
	@Autowired var  userClanRetrieveUtils : UserClanRetrieveUtils2  = null
	@Autowired var  userFacebookInviteForSlotRetrieveUtils : UserFacebookInviteForSlotRetrieveUtils2  = null
	@Autowired var  userRetrieveUtils : UserRetrieveUtils2  = null

	@Autowired var  customMenuRetrieveUtil : CustomMenuRetrieveUtils = null
	@Autowired var  inAppPurchaseUtil : InAppPurchaseUtils = null
	@Autowired var  miniEventRetrieveUtil : MiniEventRetrieveUtils = null
	@Autowired var  miniEventForPlayerLvlRetrieveUtil : MiniEventForPlayerLvlRetrieveUtils = null
	@Autowired var  miniEventGoalRetrieveUtil : MiniEventGoalRetrieveUtils = null
	@Autowired var  miniEventLeaderboardRewardRetrieveUtil : MiniEventLeaderboardRewardRetrieveUtils = null
	@Autowired var  miniEventTierRewardRetrieveUtil : MiniEventTierRewardRetrieveUtils = null
	@Autowired var  monsterLevelInfoRetrieveUtil : MonsterLevelInfoRetrieveUtils = null
	@Autowired var  pvpLeagueRetrieveUtil : PvpLeagueRetrieveUtils  = null
	@Autowired var  questRetrieveUtil : QuestRetrieveUtils  = null
    @Autowired var  rewardRetrieveUtil : RewardRetrieveUtils = null
	@Autowired var  salesDisplayItemRetrieveUtil : SalesDisplayItemRetrieveUtils = null
	@Autowired var  salesItemRetrieveUtil : SalesItemRetrieveUtils = null
	@Autowired var  salesPackageRetrieveUtil : SalesPackageRetrieveUtils = null
	@Autowired var  serverToggleRetrieveUtil : ServerToggleRetrieveUtils = null
	@Autowired var  startupStuffRetrieveUtil : StartupStuffRetrieveUtils = null
    @Autowired var  tangoGiftRetrieveUtil : TangoGiftRetrieveUtils = null;

			@Autowired var  createInfoProtoUtils : CreateInfoProtoUtils = null
			@Autowired var  deleteUtil : DeleteUtil = null
			@Autowired var  insertUtil : InsertUtil = null
			@Autowired var  updateUtil : UpdateUtil = null
			@Autowired var  hazelcastPvpUtil : HazelcastPvpUtil  = null
			@Autowired var  monsterStuffUtil : MonsterStuffUtils = null
			@Autowired var  secretGiftUtil : SecretGiftUtils = null
			@Autowired var  timeUtils : TimeUtils  = null
			@Autowired var  miscMethods: MiscMethods = null
			@Autowired var  locker :  Locker = null
			@Autowired var  eventWriter:EventWriter = null
      @Autowired var  leaderBoard: LeaderBoardImpl = null

	@Autowired var globals:Globals = null
	@Resource(name = "globalChat") var chatMessages : IList[GroupChatMessageProto] = null
	@Resource(name = "goodEquipsRecievedFromBoosterPacks") var goodEquipsRecievedFromBoosterPacks: IList[RareBoosterPurchaseProto] = null 

    //TODO: Refactor GameServer class
    @Autowired var  server:GameServer = null

	def startup(event:RequestEvent)={
		val reqProto = (event.asInstanceOf[StartupRequestEvent]).getStartupRequestProto();
		logger.info(s"Processing startup request reqProto:$reqProto")
		val udid = reqProto.getUdid();
		var apsalarId:String = null
		if(reqProto.hasApsalarId()) {
			apsalarId = reqProto.getApsalarId()
		}
		var playerId:String = null;
		miscMethods.setMDCProperties(udid, null, miscMethods.getIPOfPlayer(server, null, udid));
		var version:VersionNumberProto  = null;
		if (reqProto.hasVersionNumberProto()) {
			version = reqProto.getVersionNumberProto();
		}
		var updateStatus:UpdateStatus  = getUpdateStatus(version, reqProto.getVersionNum)
		val resBuilder = StartupResponseProto.newBuilder();
		resBuilder.setUpdateStatus(updateStatus);
		resBuilder.setAppStoreURL(Globals.APP_STORE_URL());
		resBuilder.setReviewPageURL(Globals.REVIEW_PAGE_URL());
		resBuilder.setReviewPageConfirmationMessage(Globals.REVIEW_PAGE_CONFIRMATION_MESSAGE);
		val resEvent = new StartupResponseEvent(udid);
		resEvent.setTag(event.getTag());

		// Don't fill in other fields if it is a major update
		var startupStatus = StartupStatus.USER_NOT_IN_DB;

		var nowDate = new Date();
		nowDate = timeUtils.createDateTruncateMillis(nowDate);
		var now = new Timestamp(nowDate.getTime());
		var user:User = null;
		val fbId = reqProto.getFbId();
		val freshRestart = reqProto.getIsFreshRestart();
		var newNumConsecutiveDaysLoggedIn = 0;
		if (updateStatus != UpdateStatus.MAJOR_UPDATE) {
			val users = userRetrieveUtils.getUserByUDIDorFbId(udid, fbId);
			user = selectUser(users, udid, fbId);

			val isLogin = true;
			var goingThroughTutorial = false;
			var userIdSet = true;

			if (user != null) {
				playerId = user.getId();
				//if can't lock player, exception will be thrown
				locker.lockPlayer(UUID.fromString(playerId), this.getClass().getSimpleName());
				startupStatus = StartupStatus.USER_IN_DB;
				logger.info("No major update... getting user info");
				val sd = StartupData(resBuilder, udid, fbId, playerId, now, nowDate, isLogin, goingThroughTutorial, userIdSet, startupStatus, resEvent, user, apsalarId, newNumConsecutiveDaysLoggedIn, freshRestart)
						loginExistingUser(sd);
			} else {
				logger.info(s"tutorial player with udid=$udid");
				goingThroughTutorial = true;
				userIdSet = false;
				tutorialUserAccounting(reqProto, udid, now);
				val sd = StartupData(resBuilder, udid, fbId, playerId, now, nowDate, isLogin, goingThroughTutorial, userIdSet, startupStatus, resEvent, user, apsalarId, newNumConsecutiveDaysLoggedIn, freshRestart)
				finishStartup(sd)
			}
		} else {
            resBuilder.setServerTimeMillis((new Date()).getTime())
            resEvent.setStartupResponseProto(resBuilder.build())
            logger.debug(s"Update available. Writing event response: $resEvent")
            server.writePreDBEvent(resEvent, udid)
        }
	}


	def finishStartup(sd:StartupData)={
		setAllStaticData(sd.resBuilder, sd.playerId, sd.userIdSet);
		sd.resBuilder.setStartupStatus(sd.startupStatus);
		setConstants(sd.resBuilder, sd.startupStatus);
		sd.resBuilder.setServerTimeMillis((new Date()).getTime())
		sd.resEvent.setStartupResponseProto(sd.resBuilder.build())
		val resEvent = sd.resEvent
		logger.debug(s"Writing event response: $resEvent")
		server.writePreDBEvent(resEvent, sd.udid);
		timed("StartupService.startupFinished"){
			insertUtil.insertIntoLoginHistory(sd.udid, sd.playerId, sd.now, sd.isLogin, sd.goingThroughTutorial);
			updateLeaderboard(sd.apsalarId, sd.user, sd.now, sd.newNumConsecutiveDaysLoggedIn);
		}
	}

	def getUpdateStatus(version:VersionNumberProto, clientVersionNum:Float):UpdateStatus ={
		var updateStatus:UpdateStatus  = null
		if (null != version) {
			val superNum = version.getSuperNum();
			val majorNum = version.getMajorNum();
			val minorNum = version.getMinorNum();
			val serverSuperNum = Globals.VERSION_SUPER_NUMBER();
			val serverMajorNum = Globals.VERSION_MAJOR_NUMBER();
			val serverMinorNum = Globals.VERSION_MINOR_NUMBER();
			val clientSuperGreater = superNum > serverSuperNum;
			val clientSuperEqual = superNum == serverSuperNum;
			val clientMajorGreater = majorNum > serverMajorNum;
			val clientMajorEqual = majorNum == serverMajorNum;
			val clientMinorGreater = minorNum > serverMinorNum;
			if (clientSuperGreater
					|| (clientSuperEqual && clientMajorGreater)
					|| (clientSuperEqual && clientMajorEqual && clientMinorGreater))
            {
				val preface = "CLIENT AND SERVER VERSION'S ARE OFF.";
				logger.error(s"$preface clientVersion=$superNum.$majorNum.$minorNum \t serverVersion=$serverSuperNum.$serverMajorNum.$serverMinorNum")
				updateStatus = UpdateStatus.NO_UPDATE;
			}

			if (superNum < serverSuperNum || majorNum < serverMajorNum) {
				updateStatus = UpdateStatus.MAJOR_UPDATE;
				logger.info("player has been notified of forced update");

			} else if (minorNum < serverMinorNum) {
				updateStatus = UpdateStatus.MINOR_UPDATE;
				logger.info("player has been notified of minor update");
			} else {
				updateStatus = UpdateStatus.NO_UPDATE;
			}

		} else {
			val tempClientVersionNum : Float = clientVersionNum * 10F;
        	val tempLatestVersionNum : Float = GameServer.clientVersionNumber * 10F;
        	// Check version number
        	if (tempClientVersionNum.asInstanceOf[Int] < tempLatestVersionNum.asInstanceOf[Int]) {
        		updateStatus = UpdateStatus.MAJOR_UPDATE;
        		logger.info("player has been notified of forced update");
        	} else if (tempClientVersionNum < tempLatestVersionNum) {
        		updateStatus = UpdateStatus.MINOR_UPDATE;
        	} else {
        		updateStatus = UpdateStatus.NO_UPDATE;
        	}
		}
	    return updateStatus
	}


	def selectUser(users:java.util.List[User], udid: String , fbId: String ):User = {
		var numUsers = users.size();
		if (numUsers > 2)   logger.error(s"more than 2 users with same udid, fbId. udid=$udid, fbId=$fbId, users=$users")
		if (1 == numUsers) return users.get(0)
				var udidUser:User = null;
		users.foreach{ u =>
		val userFbId = u.getFacebookId();
		val userUdid = u.getUdid();
		if (fbId != null && fbId.equals(userFbId)) {
			return u;
		} else if (null == udidUser && udid != null  && udid.equals(userUdid)) {
			//so this is the first user with specified udid, don't change reference
			//to this user once set
			udidUser = u;
		}
		}
		//didn't find user with specified fbId
		return udidUser;
	}

	def tutorialUserAccounting(reqProto:StartupRequestProto , udid:String , now:Timestamp )= {
		timed("StartupService.tutorialUserAccounting"){
			val userLoggedIn = LoginHistoryRetrieveUtils.userLoggedInByUDID(udid);
			val numOldAccounts = userRetrieveUtils.numAccountsForUDID(udid);
			val alreadyInFirstTimeUsers = FirstTimeUsersRetrieveUtils.userExistsWithUDID(udid);
			var isFirstTimeUser = false;
			if (!userLoggedIn && 0 >= numOldAccounts && !alreadyInFirstTimeUsers) {
				isFirstTimeUser = true;
			}

			logger.info("\n userLoggedIn=" + userLoggedIn + "\t numOldAccounts="
					+ numOldAccounts + "\t alreadyInFirstTimeUsers="
					+ alreadyInFirstTimeUsers + "\t isFirstTimeUser="
					+ isFirstTimeUser);

			if (isFirstTimeUser) {
				logger.info("new player with udid {}", udid);
				insertUtil.insertIntoFirstTimeUsers(udid, null, reqProto.getMacAddress(), reqProto.getAdvertiserId(), now);
			}

			if (Globals.OFFERCHART_ENABLED() && isFirstTimeUser) {
				sendOfferChartInstall(now, reqProto.getAdvertiserId());
			}
		}
	}

	def loginExistingUser(sd:StartupData)={
		val udid:String = sd.udid 
		val playerId:String = sd.playerId 
		val resBuilder:Builder = sd.resBuilder 
		val nowDate:Date = sd.nowDate 
		val now:Timestamp = sd.now
		val user:User = sd.user
		val fbId:String = sd.fbId
		val freshRestart:Boolean = sd.freshRestart
		try {
			//force other devices on this account to logout
			forceLogoutOthers(udid, playerId, user, fbId)
			logger.info(s"no major update... getting user info")
			val userId = playerId;

							val userInfo:Future[PvpLeagueForUser] = for{
								sipaaq <-  setInProgressAndAvailableQuests(resBuilder, userId)
								suci <-    setUserClanInfos(resBuilder, userId)
								sntp <-    setNoticesToPlayers(resBuilder)
								sums <-    setUserMonsterStuff(resBuilder, userId)
								sbp  <-    setBoosterPurchases(resBuilder)
								sts  <-    setTaskStuff(resBuilder, userId)
								ses  <-    setEventStuff(resBuilder, userId)
								spbo <-    setPvpBoardObstacles(resBuilder, userId)
								sas  <-    setAchievementStuff(resBuilder, user, userId, now)
								smj  <-    setMiniJob(resBuilder, userId)
								sui  <-    setUserItems(resBuilder, user, userId);
								swpciap <- setWhetherPlayerCompletedInAppPurchase(resBuilder, user)
								ssg  <-    setSecretGifts(resBuilder, userId, now.getTime())
								sr   <-    setResearch(resBuilder, userId)
								sbifu <-   setBattleItemForUser(resBuilder, userId)
								sbiqfu <-  setBattleItemQueueForUser(resBuilder, userId)
								ssfu  <-   setSalesForUser(resBuilder, user)
								scrs  <-   setClanRaidStuff(resBuilder, user, userId, now)
								plfu  <-   pvpBattleStuff(resBuilder, user, userId, freshRestart, now)
                sttslb<-   setTopThreeStrengthLeaderBoard(resBuilder)
							} yield plfu

			userInfo onSuccess {
			    case plfu:PvpLeagueForUser =>  finishLoginExisting(resBuilder, user, userId, nowDate, plfu, sd) 
			}

			userInfo onFailure {
    			case t:Throwable => {
    				logger.error("Error running login futures", t)
    				loginFinished(playerId)
    				exceptionInStartup(sd)
    			}
			}

		} catch{
    		case t:Throwable => logger.error(s"", t)
    			loginFinished(playerId)
		}
	}

	def loginFinished(playerId:String)= {
		locker.unlockPlayer(UUID.fromString(playerId), this.getClass().getSimpleName());
	}


	def finishLoginExisting(resBuilder:Builder, user:User, playerId:String, nowDate:Date, plfu:PvpLeagueForUser, sd:StartupData) = {
		timed("StartupService.finishExistingLogin"){
			try {
				val sgcma = new SetGlobalChatMessageAction(resBuilder, user, chatMessages);
				sgcma.execute();
				//fill up with userIds, and other ids to fetch from tables
				val fillMe = new StartUpResource(userRetrieveUtils, clanRetrieveUtils);
				// For creating the full user
				fillMe.addUserId(user.getId());

				//get translationsettingforuser list of the player to check for defaults
				val tsfuList = translationSettingsForUserRetrieveUtil.getUserTranslationSettingsForUser(playerId);
				var tsfuListIsNull = false;

				if(tsfuList == null || tsfuList.isEmpty()) {
					insertUtil.insertTranslateSettings(
							playerId, 
							null,
							ControllerConstants.TRANSLATION_SETTINGS__DEFAULT_LANGUAGE,
							ChatScope.GLOBAL.toString(),
							ControllerConstants.TRANSLATION_SETTINGS__DEFAULT_TRANSLATION_ON);
					tsfuListIsNull = true;
				}

				val updatedTsfuList = translationSettingsForUserRetrieveUtil.getUserTranslationSettingsForUser(playerId);

				val spcma = new SetPrivateChatMessageAction(
						resBuilder, 
						user, 
						playerId,
						privateChatPostRetrieveUtils, 
						tsfuListIsNull, 
						insertUtil,
						createInfoProtoUtils, 
						translationSettingsForUserRetrieveUtil,
						updatedTsfuList);
				spcma.setUp(fillMe);

				val sfesa = new SetFacebookExtraSlotsAction(
						resBuilder, 
						user, 
						playerId,
						userFacebookInviteForSlotRetrieveUtils,
						createInfoProtoUtils);
				sfesa.setUp(fillMe);

				val spbha = new SetPvpBattleHistoryAction(
						resBuilder, 
						user, 
						playerId, 
						pvpBattleHistoryRetrieveUtil,
						monsterForUserRetrieveUtils, 
						clanRetrieveUtils,
						hazelcastPvpUtil,
						monsterStuffUtil,
						createInfoProtoUtils,
						serverToggleRetrieveUtil,
            			monsterLevelInfoRetrieveUtil,
			            battleReplayForUserRetrieveUtil);
				spbha.setUp(fillMe);

				//CLAN DATA
				val cdpb = ClanDataProto.newBuilder();
				val sccma = new SetClanChatMessageAction(
						cdpb,
						user,
						clanChatPostRetrieveUtils,
						createInfoProtoUtils);
				sccma.setUp(fillMe);

				val scha = new SetClanHelpingsAction(
						cdpb,
						user,
						playerId,
						clanHelpRetrieveUtil,
						createInfoProtoUtils);
				scha.setUp(fillMe);

				val scra = new SetClanRetaliationsAction(
						cdpb,
						user,
						playerId,
						clanAvengeRetrieveUtil,
						clanAvengeUserRetrieveUtil,
						createInfoProtoUtils);
				scra.setUp(fillMe);

				val scmtda = new SetClanMemberTeamDonationAction(
						cdpb, 
						user, 
						playerId, 
						clanMemberTeamDonationRetrieveUtil,
						monsterSnapshotForUserRetrieveUtil,
						createInfoProtoUtils);
				scmtda.setUp(fillMe);

				//SETTING CLAN GIFTS, it adds protos straight to resbuilder
				val scga = new SetClanGiftsAction(
						resBuilder,
						user,
						playerId,
						clanGiftForUserRetrieveUtil,
						createInfoProtoUtils);
				scga.setUp(fillMe);
                
                val sga = new SetGiftsAction(
                        resBuilder,
                        user,
                        playerId,
                        giftForUserRetrieveUtil,
                        giftForTangoUserRetrieveUtil,
                        tangoGiftRetrieveUtil,
                        rewardRetrieveUtil,
                        createInfoProtoUtils)
                sga.setUp(fillMe);

				//Now since all the ids of resources are known, get them from db
				fillMe.fetch();
				spcma.execute(fillMe);
				//set this proto after executing privatechatprotos
				setDefaultLanguagesForUser(resBuilder, playerId);
				sfesa.execute(fillMe);
				spbha.execute(fillMe);
				sccma.execute(fillMe);
				scha.execute(fillMe);
				scra.execute(fillMe);
				scmtda.execute(fillMe);
				scga.execute(fillMe);
                sga.execute(fillMe);
				resBuilder.setClanData(cdpb.build());
				//TODO: DELETE IN FUTURE. This is for legacy client
				resBuilder.addAllClanChats(cdpb.getClanChatsList());
				resBuilder.addAllClanHelpings(cdpb.getClanHelpingsList());

				//OVERWRITE THE LASTLOGINTIME TO THE CURRENT TIME
				//log.info("before last login change, user=" + user);
				user.setLastLogin(nowDate);
				//log.info("after last login change, user=" + user);

				var clan:Clan = null;
				if (user.getClanId() != null) {
					clan = fillMe.getClanIdsToClans().get(user.getClanId());
				}
				val fup = createInfoProtoUtils.createFullUserProtoFromUser(user, plfu, clan);
				resBuilder.setSender(fup);
				finishStartup(sd)
			}catch{
			    case t:Throwable => logger.error("Error finishing login for user: $playerId", t)
			}finally {
				loginFinished(playerId)
			}
		}
	}


	//TODO: figure out when to call this
	def exceptionInStartup(sd:StartupData)={
		try {
			sd.resBuilder.setStartupStatus(StartupStatus.SERVER_IN_MAINTENANCE); //DO NOT allow user to play
			sd.resEvent.setStartupResponseProto(sd.resBuilder.build());
			eventWriter.processPreDBResponseEvent(sd.resEvent, sd.udid);
		} catch{
		    case t:Throwable => logger.error("exception2 in StartupController processEvent", t);
		}
	}


	def forceLogoutOthers(udid:String, playerId:String, user:User, fbId:String)={
		val logoutResponse = ForceLogoutResponseProto.newBuilder()
		logoutResponse.setPreviousLoginTime(user.getLastLogin.getTime)
		logoutResponse.setUdid(udid)
		val logoutEvent = new ForceLogoutResponseEvent(playerId)
		logoutEvent.setForceLogoutResponseProto(logoutResponse.build())
		eventWriter.processPreDBResponseEvent(logoutEvent, udid)
		eventWriter.handleEvent(logoutEvent)
		if(fbId!=null && !fbId.isEmpty()) {
			eventWriter.processPreDBFacebookEvent(logoutEvent, fbId)
		}
	}

	//NOTE: will only ever be executed once for each user,
	def setUserSegmentationGroup(resBuilder:Builder, user:User, userId:String)= {
		timed("StartupService.setUserSegmentationGroup"){
			if(user.getSegmentationGroup() == 0) {
				val usga: UserSegmentationGroupAction = new UserSegmentationGroupAction(userId, user)
			    usga.convertUserIdIntoInt();
			    val segmentationGroup: Int = usga.getSegmentationGroup();
			    if(!user.updateUserSegmentationGroup(segmentationGroup)) {
				    logger.error(s"something wrong with updating user's segmentation group value:$segmentationGroup");
			    }
			}
		}
	}

	def setInProgressAndAvailableQuests(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setInProgressAndAvailableQuests"){
				val inProgressAndredeemedUserQuests = questForUserRetrieveUtils.getUserQuestsForUser(userId)
				if(inProgressAndredeemedUserQuests != null) {
					val inProgressQuests = new ArrayList[QuestForUser]()
					val inProgressQuestsIds = new HashSet[Integer]()
					val redeemedQuestIds = new ArrayList[Integer]()
					val questIdtoQuests = questRetrieveUtil.getQuestIdsToQuests
					inProgressAndredeemedUserQuests.foreach{ uq:QuestForUser  =>
    					if(!uq.isRedeemed()) {
    						inProgressQuests.add(uq)
    						inProgressQuestsIds.add(uq.getQuestId)
    					}else {
    						redeemedQuestIds.add(uq.getQuestId)
    					}
				    }
					val questIdtoUserQuestJobs = questJobForUserRetrieveUtil.getSpecificOrAllQuestIdToQuestJobsForUserId(userId, inProgressQuestsIds)
					val currentUserQuests = createInfoProtoUtils.createFullUserQuestDataLarges(inProgressQuests, questIdtoQuests, questIdtoUserQuestJobs)
					resBuilder.addAllUserQuests(currentUserQuests)
					resBuilder.addAllRedeemedQuestIds(redeemedQuestIds)
				}
			}
		}
	}


	def setUserClanInfos(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setUserClanInfos"){
				userClanRetrieveUtils.getUserClansRelatedToUser(userId).foreach{ uc:UserClan =>
				    resBuilder.addUserClanInfo(createInfoProtoUtils.createFullUserClanProtoFromUserClan(uc))
				}
			}
		}
	}


	def setNoticesToPlayers(resBuilder:Builder):Future[Unit]= {
		Future{
			val notices = startupStuffRetrieveUtil.getAllActiveAlerts
			if(notices != null) {
				notices.foreach(resBuilder.addNoticesToPlayers(_))
			}
		}
	}


	def setUserMonsterStuff(resBuilder:Builder, userId:String):Future[Unit]= {
		for{
			userMonsters <- setUserMonsters(resBuilder,userId)
			userMonstersInHealing <- setUserMonstersInHealing(resBuilder,userId)
			userMonstersEnhancing <- setUserMonstersEnhancing(resBuilder,userId)
			userMonstersEvolving <- setUserMonstersEvolving(resBuilder,userId)
		} yield Unit
	}

	def setUserMonsters(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setUserMonsters"){
				val userMonsters = monsterForUserRetrieveUtils.getMonstersForUser(userId)
				if(userMonsters != null) {
					userMonsters.foreach{ mfu:MonsterForUser => resBuilder.addUsersMonsters(createInfoProtoUtils.createFullUserMonsterProtoFromUserMonster(mfu))}
				}
			}
		}
	}

	def setUserMonstersInHealing(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setUsersMonstersInHealing"){
				val userMonstersHealing = monsterHealingForUserRetrieveUtils.getMonstersForUser(userId)
				if(userMonstersHealing != null) {
					userMonstersHealing.values.foreach{ mhfu:MonsterHealingForUser =>
					    resBuilder.addMonstersHealing(createInfoProtoUtils.createUserMonsterHealingProtoFromObj(mhfu))
					}
				}
			}
		}
	}

	def setUserMonstersEnhancing(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setUsersMonstersEnhancing"){
				val userMonstersEnhancing = monsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId)
				if(userMonstersEnhancing != null && !userMonstersEnhancing.isEmpty()) {
					var baseMonster:UserEnhancementItemProto = null;
    				val feederUserMonsterIds = new ArrayList[String]();
    				val feederProtos = new ArrayList[UserEnhancementItemProto]();
    				userMonstersEnhancing.values().foreach{mefu =>
        				val ueip = createInfoProtoUtils.createUserEnhancementItemProtoFromObj(mefu)
        				val startTime = mefu.getExpectedStartTime;
        				if(startTime == null) {
        					baseMonster = ueip
        				}else {
        					feederProtos.add(ueip)
        					feederUserMonsterIds.add(mefu.getMonsterForUserId)
        				}
    				}
    				if(baseMonster == null ) {
    					logger.error(s"no base monster enhancement. deleting inEnhancing=$userMonstersEnhancing.values()")
    					try {
    						if(!feederUserMonsterIds.isEmpty()) {
    							val numDeleted = deleteUtil.deleteMonsterEnhancingForUser(userId, feederUserMonsterIds)
								logger.info(s"numDeleted enhancements: $numDeleted")
    						}
    					}catch{
              				case t:Throwable => logger.error(s"unable to delete orphaned enhancements", t)
    					}
    				}else {
    					val uep = createInfoProtoUtils.createUserEnhancementProtoFromObj(userId, baseMonster, feederProtos)
						resBuilder.setEnhancements(uep)
    				}
				}
			}
		}
	}

	def setUserMonstersEvolving(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setUserMonstersEvolving"){
				val userMonstersEvolving = monsterEvolvingForUserRetrieveUtils.getCatalystIdsToEvolutionsForUser(userId)
				if(userMonstersEvolving != null) {
					userMonstersEvolving.values.foreach{ mefu =>
						val eup = createInfoProtoUtils.createUserEvolutionProtoFromEvolution(mefu)
						resBuilder.setEvolution(eup)
					}
				}
			}
		}
	}



	def setBoosterPurchases(resBuilder : Builder):Future[Unit]= {
		Future{
			timed("StartupService.setBoosterPurchases"){
				val it = goodEquipsRecievedFromBoosterPacks.iterator()
				val boosterPurchases = scala.collection.mutable.ArrayBuffer.empty[RareBoosterPurchaseProto]
				while(it.hasNext()) {
					boosterPurchases += it.next()
				}
				boosterPurchases.sortBy(x => x.getTimeOfPurchase).foreach(resBuilder.addRareBoosterPurchases(_))
			}
		}
	}


	def setTaskStuff(resBuilder:Builder, userId:String):Future[Unit]= {
		def completedTasksFuture = Future{
			timed("StartupService.setTaskStuff_completedTasks"){
				val utcList = taskForUserCompletedRetrieveUtils.getAllCompletedTasksForUser(userId)
				resBuilder.addAllCompletedTasks(createInfoProtoUtils.createUserTaskCompletedProto(utcList))
				val taskIds = taskForUserCompletedRetrieveUtils.getTaskIds(utcList)
				resBuilder.addAllCompletedTaskIds(taskIds)
			}
		}
		def ongoingTaskFuture = Future{
			timed("StartupService.setTaskStuff_ongingTasks"){
				val aTaskForUser = taskForUserOngoingRetrieveUtils.getUserTaskForUserId(userId)
				if(aTaskForUser != null) {
					val tfucs = taskForUserClientStateRetrieveUtil.getTaskForUserClientState(userId)
					logger.warn(s"user has incompleted task userTask=$aTaskForUser")
					setOngoingTask(resBuilder, userId, aTaskForUser, tfucs)
				}
			}
		}
		for{
			ctf <- completedTasksFuture
			otf <- ongoingTaskFuture
		}yield Unit
	}


	def setOngoingTask(resBuilder:Builder, userId:String, aTaskForUser:TaskForUserOngoing, tfucs:TaskForUserClientState):Unit= {
		try {
			val mutp = createInfoProtoUtils.createMinimumUserTaskProto(userId, aTaskForUser, tfucs)
			resBuilder.setCurTask(mutp)
			val userTaskId = aTaskForUser.getId
			val taskStages = taskStageForUserRetrieveUtils.getTaskStagesForUserWithTaskForUserId(userTaskId)
			val stageNumToTsfu = new HashMap[Integer, java.util.List[TaskStageForUser]]()
			taskStages.foreach{ tsfu  =>
    			val stageNum = tsfu.getStageNum
    			var tsfuList = stageNumToTsfu.get(stageNum)
    			if(tsfuList == null) {
    				tsfuList = new ArrayList[TaskStageForUser]()
					stageNumToTsfu.put(stageNum, tsfuList)
    			}
    			tsfuList.add(tsfu)
			}
			val taskId = aTaskForUser.getTaskId
			stageNumToTsfu.keySet.foreach{ stageNum =>
    			val tsp = createInfoProtoUtils.createTaskStageProto(taskId, stageNum, stageNumToTsfu.get(stageNum))
    			resBuilder.addCurTaskStages(tsp)
			}
		}catch{
		    case t:Throwable => logger.error(s"could not create existing task, letting it get deleted when user starts another task", t)
		}
	}


	def setEventStuff(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setEventStuff"){
				val events = eventPersistentForUserRetrieveUtils.getUserPersistentEventForUserId(userId)
				events.foreach{ epfu =>
					resBuilder.addUserEvents(createInfoProtoUtils.createUserPersistentEventProto(epfu))  
				}
			}
		}
	}


	def setPvpBoardObstacles(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setPvpBoardObstacles"){
				val boList = pvpBoardObstacleForUserRetrieveUtil.getPvpBoardObstacleForUserId(userId)
				boList.foreach{ pbofu => 
				    resBuilder.addUserPvpBoardObstacles(createInfoProtoUtils.createUserPvpBoardObstacleProto(pbofu))  
				}
			}
		}
	}

	def pvpBattleStuff(resBuilder:Builder, user:User, userId:String, isFreshRestart:Boolean, battleEndTime:Timestamp):Future[PvpLeagueForUser] ={
		Future{
			timed("StartupService.pvpBattleStuff"){
				val plfu = pvpLeagueForUserRetrieveUtil.getUserPvpLeagueForId(userId)
				val pu = new PvpUser(plfu)
				hazelcastPvpUtil.replacePvpUser(pu, userId)
				if(isFreshRestart) {
					val battle = pvpBattleForUserRetrieveUtils.getPvpBattleForUserForAttacker(userId);
					if(battle != null) {
						val battleStartTime = new Timestamp(battle.getBattleStartTime.getTime)
						var eloAttackerLoses = battle.getAttackerLoseEloChange
						if(plfu.getElo+eloAttackerLoses < ControllerConstants.PVP__DEFAULT_MIN_ELO) {
							eloAttackerLoses = plfu.getElo - ControllerConstants.PVP__DEFAULT_MIN_ELO;
						}
						val defenderId = battle.getDefenderId
						var eloDefenderWins = battle.getDefenderWinEloChange
						penalizeUserForLeavingGameWhileInPvp(
							userId, 
							user, 
							plfu, 
							defenderId, 
							eloAttackerLoses, 
							eloDefenderWins, 
							battleEndTime, 
							battleStartTime, 
							battle)
					}
				}
				plfu
			}
		}
	}


	def penalizeUserForLeavingGameWhileInPvp(
			userId:String, 
			user:User, 
			attackerPlfu:PvpLeagueForUser, 
			defenderId:String,
			eloAttackerLoses:Int, 
			eloDefenderWins:Int,
			battleEndTime:Timestamp,
			battleStartTime:Timestamp,
			battle:PvpBattleForUser):Unit={
		var defenderUuid:UUID = null
		var invalidUuids = true
		timed("StartupService.penalizeUserForLeavingGameWhileInPvp") {
		    try {
				if(defenderId != null && !defenderId.isEmpty()) {
					defenderUuid = UUID.fromString(defenderId)
				}
				invalidUuids = false
		    }catch{
		        case t:Throwable => {
			        logger.error(s"UUID error. Incorrect defenderId=$defenderId", t)
		        }
			    invalidUuids = true
			}
		    if (invalidUuids) return
				//only lock real users
				if (null != defenderUuid)  locker.lockPlayer(defenderUuid, this.getClass().getSimpleName())
				try {
					var attackerEloBefore = attackerPlfu.getElo();
					var defenderEloBefore = 0;
					var attackerPrevLeague = attackerPlfu.getPvpLeagueId();
					var attackerCurLeague = 0;
					var defenderPrevLeague = 0;
					var defenderCurLeague = 0;
					var attackerPrevRank = attackerPlfu.getRank();
					var attackerCurRank = 0;
					var defenderPrevRank = 0;
					var defenderCurRank = 0;

					var attackerCurElo = attackerPlfu.getElo + eloAttackerLoses
							attackerCurLeague = pvpLeagueRetrieveUtil.getLeagueIdForElo(attackerCurElo, attackerPrevLeague)
							attackerCurRank = pvpLeagueRetrieveUtil.getRankForElo(attackerCurElo, attackerCurLeague);
					var attacksLost = attackerPlfu.getAttacksLost+1

					var numUpdated = updateUtil.updatePvpLeagueForUser(userId,
							attackerCurLeague, attackerCurRank, eloAttackerLoses, null, null, 0, 0, 1, 0, -1);
					logger.info(s"num updated when changing attackers elo because of reset=$numUpdated")

					attackerPlfu.setElo(attackerCurElo);
					attackerPlfu.setPvpLeagueId(attackerCurLeague);
					attackerPlfu.setRank(attackerCurRank);
					attackerPlfu.setAttacksLost(attacksLost);
					val attackerPu = new PvpUser(attackerPlfu);
					hazelcastPvpUtil.replacePvpUser(attackerPu, userId);

					if(defenderId != null) {
						val defenderPlfu = pvpLeagueForUserRetrieveUtil.getUserPvpLeagueForId(defenderId);

						defenderEloBefore = defenderPlfu.getElo();
						defenderPrevLeague = defenderPlfu.getPvpLeagueId();
						defenderPrevRank = defenderPlfu.getRank();
						//update hazelcast map and ready arguments for pvp battle history
						var defenderCurElo = defenderEloBefore + eloDefenderWins;
						defenderCurLeague = pvpLeagueRetrieveUtil.getLeagueIdForElo(defenderCurElo, defenderPrevLeague);
						defenderCurRank = pvpLeagueRetrieveUtil.getRankForElo(defenderCurElo, defenderCurLeague);

						var defensesWon = defenderPlfu.getDefensesWon() + 1;

						numUpdated = updateUtil.updatePvpLeagueForUser(defenderId, defenderCurLeague, defenderCurRank, eloDefenderWins, null, null, 0, 1, 0, 0, -1);
						logger.info(s"num updated when changing defender's elo because of reset=$numUpdated");

						defenderPlfu.setElo(defenderCurElo);
						defenderPlfu.setPvpLeagueId(defenderCurLeague);
						defenderPlfu.setRank(defenderCurRank);
						defenderPlfu.setDefensesWon(defensesWon);
						var defenderPu = new PvpUser(defenderPlfu);
						hazelcastPvpUtil.replacePvpUser(defenderPu, defenderId);
					}
					var attackerWon = false;
					var cancelled = false;
					var defenderGotRevenge = false;
					var displayToDefender = true;
					var numInserted = insertUtil.insertIntoPvpBattleHistory(
						userId, defenderId, battleEndTime, battleStartTime,
						eloAttackerLoses, attackerEloBefore, eloDefenderWins,
						defenderEloBefore, attackerPrevLeague, attackerCurLeague,
						defenderPrevLeague, defenderCurLeague, attackerPrevRank,
						attackerCurRank, defenderPrevRank, defenderCurRank, 0, 0,
						0, 0, -1, attackerWon, cancelled, defenderGotRevenge,
						displayToDefender);

					logger.info(s"numInserted into battle history=$numInserted");
					//delete that this battle occurred
					val numDeleted = deleteUtil.deletePvpBattleForUser(userId);
					logger.info(s"successfully penalized, rewarded attacker and defender respectively. battle=$battle, numDeleted=$numDeleted");
				}catch{
				    case t:Throwable => logger.error(s"tried to penalize, reward attacker and defender respectively. battle=$battle", t);
				} finally {
					if (null != defenderUuid) locker.unlockPlayer(defenderUuid, this.getClass().getSimpleName())
				}
	    }
	}


	def setAllStaticData(resBuilder:Builder, userId:String, userIdSet:Boolean) {
		val sdp = miscMethods.getAllStaticData(userId, userIdSet, questForUserRetrieveUtils)
		resBuilder.setStaticDataStuffProto(sdp)
	}

	def setAchievementStuff(resBuilder:Builder, user:User, userId:String, now:Date):Future[Unit]= {
		Future{
			var achievementsIdToUserAchievements: java.util.Map[Integer, AchievementForUser] = null
			timed("StartupService.setAchievementStuff"){
				achievementsIdToUserAchievements = achievementForUserRetrieveUtil.getSpecificOrAllAchievementIdToAchievementForUserId(userId, null)
				achievementsIdToUserAchievements.values.foreach{ afu =>
		    		resBuilder.addUserAchievements(createInfoProtoUtils.createUserAchievementProto(afu))  
				}
			}

			var calculateMiniEvent: Boolean = true
			for (achievementId <- ControllerConstants.CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS) {
				if (!achievementsIdToUserAchievements.containsKey(achievementId)) {
					calculateMiniEvent = false
					logger.info(s"not retrieving MiniEvent1, user does not have achievementId $achievementId");
				} else {
					val afu = achievementsIdToUserAchievements.get(achievementId)
					if (!afu.isRedeemed()) {
						calculateMiniEvent = false
						logger.info(s"not retrieving MiniEvent1, achievement not redeemed $afu");
					}
				}
            }
			logger.info(s"calculateMiniEvent=$calculateMiniEvent");
			if (calculateMiniEvent) {
				//calculate only if user finished all clan achievements
				setMiniEventForUser(resBuilder, user, userId, now)
			}
		}
	}
	def setMiniEventForUser(resBuilder:Builder, u:User, userId:String, now:Date):Future[Unit]={
		Future{
			timed("StartupService.setMiniEventForUser"){
				val rmeaResBuilder =  RetrieveMiniEventResponseProto.newBuilder();
				val rmea = new RetrieveMiniEventAction(
					userId,
					now,
					false,
					userRetrieveUtils,
					achievementForUserRetrieveUtil,
					miniEventForUserRetrieveUtil,
					miniEventGoalForUserRetrieveUtil,
					insertUtil,
					deleteUtil,
					miniEventGoalRetrieveUtil,
					miniEventForPlayerLvlRetrieveUtil,
					miniEventRetrieveUtil,
					miniEventTierRewardRetrieveUtil,
					miniEventLeaderboardRewardRetrieveUtil,
					timeUtils);
				rmea.execute(rmeaResBuilder);
				if (rmeaResBuilder.getStatus().equals(RetrieveMiniEventStatus.SUCCESS) &&  null != rmea.getCurActiveMiniEvent()){
					//get UserMiniEvent info and create the proto to set into resBuilder
					val  umep = createInfoProtoUtils.createUserMiniEventProto(
						rmea.getMefu(), 
						rmea.getCurActiveMiniEvent(),
						rmea.getMegfus(),
						rmea.getLvlEntered(), 
						rmea.getRewards(),
						rmea.getGoals(), 
						rmea.getLeaderboardRewards(),
                        rewardRetrieveUtil);
					resBuilder.setUserMiniEvent(umep);
				}
            }
		}
	}

	def setMiniJob(resBuilder:Builder, userId:String):Future[Unit]= {
		Future{
			timed("StartupService.setMiniJob"){
				val miniJobIdtoUserMiniJobs = miniJobForUserRetrieveUtil.getSpecificOrAllIdToMiniJobForUser(userId, null)
				if(!miniJobIdtoUserMiniJobs.isEmpty()) {
					val mjfuList = new ArrayList[MiniJobForUser](miniJobIdtoUserMiniJobs.values)
					resBuilder.addAllUserMiniJobProtos(
                            createInfoProtoUtils.createUserMiniJobProtos(
                                    mjfuList, null, rewardRetrieveUtil))
				}
			}
		}
	}
	def setUserItems(resBuilder:Builder, user:User, userId:String):Future[Unit]= {
		Future{
			var userItemIds: java.util.Set[Integer] = null
			timed("StartupService.setUserItems"){
				val itemIdToUserItems = itemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(userId, null);
				if (!itemIdToUserItems.isEmpty()) {
					val uipList = createInfoProtoUtils.createUserItemProtosFromUserItems(new ArrayList[ItemForUser](itemIdToUserItems.values()));
					resBuilder.addAllUserItems(uipList);
				}
			    //TODO: could be run in parallel with above code
				val itemsUsed = itemForUserUsageRetrieveUtil.getItemForUserUsage(userId, null);
				itemsUsed.foreach{ ifuu =>
					val uiup = createInfoProtoUtils.createUserItemUsageProto(ifuu);
					resBuilder.addItemsInUse(uiup);
				}
				userItemIds = itemIdToUserItems.keySet()
    		if (userItemIds != null) {
    			setSalePack(resBuilder, user, userItemIds);
    		}
      }
		}
  }

	def setSalePack(resBuilder:Builder, user:User, userItemIds:java.util.Set[Integer])= {
		timed("StartupService.setSalePack"){
			setStarterBuilderPackForUser(resBuilder, user);
		}
	}

	def serviceCombinedStarterAndBuilderPack(user:User):Boolean= {
		timed("StartupService.serviceCombinedStarterAndBuilderPack"){
			val objArray: Array[Object] = Array("COOPER", "ALEX")
			val floatArray: Array[java.lang.Float] = Array(0.5F, 0.5F)

			val usga = new UserSegmentationGroupAction(objArray, floatArray, user.getId(), user);
			if(usga.returnAppropriateObjectGroup().equals("COOPER")){
				logger.info("sending starterbuilderpack");
				return true;
			} else {
				logger.info("sending starter and builder pack");
				return false;
			}    
		}
	}

	//TODO: Get rid of this copy pasted code
	def setStarterBuilderPackForUser(resBuilder:Builder, user:User)= {
		timed("StartupService.serviceCombinedStarterAndBuilderPack"){
			val numBeginnerSalesPurchased = user.getNumBeginnerSalesPurchased();
			if(numBeginnerSalesPurchased == 0) {
				val idsToSalesPackages = salesPackageRetrieveUtil.getSalesPackageIdsToSalesPackages() 
				idsToSalesPackages.values().foreach{ sp:SalesPackage =>
					if(sp.getProductId().equalsIgnoreCase(IAPValues.STARTERBUILDERPACK)) {
						val spProto = inAppPurchaseUtil.createSalesPackageProto(
							sp,
							salesItemRetrieveUtil,
							salesDisplayItemRetrieveUtil,
							customMenuRetrieveUtil);
						resBuilder.addSalesPackages(spProto);
                    }
				}
			}
		}
	}
	//TODO: Get rid of this copy pasted code
	def setStarterPackForUser(resBuilder: Builder, user:User)= {
		timed("StartupService.setStarterPackForUser"){
			val numBeginnerSalesPurchased = user.getNumBeginnerSalesPurchased();
			if(numBeginnerSalesPurchased == 0) {
				val idsToSalesPackages = salesPackageRetrieveUtil.getSalesPackageIdsToSalesPackages() 
				idsToSalesPackages.values().foreach{ sp:SalesPackage =>
					if(sp.getProductId().equalsIgnoreCase(IAPValues.STARTERPACK)) {
						val spProto = inAppPurchaseUtil.createSalesPackageProto(
						sp,
						salesItemRetrieveUtil,
						salesDisplayItemRetrieveUtil,
						customMenuRetrieveUtil);
						resBuilder.addSalesPackages(spProto);
					}
			    }
			}
		}
	}
	//TODO: Get rid of this copy pasted code
	def setBuilderPackForUser(resBuilder: Builder, user:User, userItemIds:java.util.Set[Integer])= {
		timed("StartupService.setBuilderPackForUser"){
			var hasExtraBuilder = false
			userItemIds.foreach { itemId =>
				//TODO: Make a constant out of this number for builder's id
				if (itemId == 10000) {
					hasExtraBuilder = true
				}
			}

			if(!hasExtraBuilder) {
				val idsToSalesPackages = salesPackageRetrieveUtil.getSalesPackageIdsToSalesPackages() 
				idsToSalesPackages.values().foreach{ sp:SalesPackage =>
					if(sp.getProductId().equalsIgnoreCase(IAPValues.BUILDERPACK)) {
						val spProto = inAppPurchaseUtil.createSalesPackageProto(
							sp,
							salesItemRetrieveUtil,
							salesDisplayItemRetrieveUtil,
							customMenuRetrieveUtil);
						resBuilder.addSalesPackages(spProto);
				    }
				}
			}
		}
	}

	def setWhetherPlayerCompletedInAppPurchase(resBuilder:Builder, user:User):Future[Unit]= {
		Future{
			timed("StartupService.setWhetherPlayerCompletedInAppPurchase"){
				val hasPurchased = iapHistoryRetrieveUtils.checkIfUserHasPurchased(user.getId());
				resBuilder.setPlayerHasBoughtInAppPurchase(hasPurchased);
			}
		}
	}

	def setSecretGifts(resBuilder:Builder, userId:String , now:Long):Future[Unit]= {
		Future{
			timed("StartupService.setSecretGifts"){
				var gifts = itemSecretGiftForUserRetrieveUtil.getSpecificOrAllItemSecretGiftForUser(userId, null);
				//need to enforce 2 gift minimum
				var numGifts = 0;
				if (null == gifts || gifts.isEmpty()) {
					gifts = new ArrayList[ItemSecretGiftForUser]();
					numGifts = 2;
				} else if (null != gifts && gifts.size() == 1) {
					numGifts = 1;
				}
				if (numGifts > 0) {
					giveGifts(userId, now, gifts, numGifts);
				}
				val nuGiftsProtos = createInfoProtoUtils.createUserItemSecretGiftProto(gifts);
				resBuilder.addAllGifts(nuGiftsProtos);
			}
		}
	}

	//need to enforce 2 gift minimum
	def giveGifts(
			userId:String, 
			now:Long,
			gifts:Collection[ItemSecretGiftForUser], 
			numGifts:Int) {
		timed("StartupService.giveGifts"){
			var giftList = secretGiftUtil.calculateGiftsForUser(userId, numGifts, now);
			var ids = insertUtil.insertIntoItemSecretGiftForUserGetId(giftList);
			//need to set the ids
			if (null != ids && ids.size() == giftList.size()) {
				for (index:Int <- 0 to ids.size - 1) {
					val id = ids.get(index);
					val isgfu = giftList.get(index);
					isgfu.setId(id);
				}
				gifts.addAll(giftList);
			} else {
				logger.error(s"Error calculating the new SecretGifts. nuGifts=$giftList, ids=$ids");
			}
		}
	}

	def setResearch(resBuilder:Builder , userId:String ):Future[Unit]= {
		Future{
			timed("StartupService.setResearch"){
				val userResearchs = researchForUserRetrieveUtil.getAllResearchForUser(userId);
				if(null != userResearchs && !userResearchs.isEmpty()) {
					val urpList = createInfoProtoUtils.createUserResearchProto(userResearchs);
					resBuilder.addAllUserResearchs(urpList);
				}
			}
		}
	}

	def setBattleItemForUser(resBuilder:Builder , userId:String ):Future[Unit]= {
		Future{
			timed("StartupService.setBattleItemForUser"){
				val bifuList = battleItemForUserRetrieveUtil.getUserBattleItemsForUser(userId);
				if (null != bifuList && !bifuList.isEmpty()) {
					val biqfupList = createInfoProtoUtils.convertBattleItemForUserListToBattleItemForUserProtoList(bifuList);
					resBuilder.addAllBattleItem(biqfupList);
				}
			}
		}
	}

	def setBattleItemQueueForUser(resBuilder:Builder , userId:String ):Future[Unit]= {
		Future{
			timed("StartupService.setBattleItemQueueForUser"){
				var biqfuList = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(userId)
				if (null != biqfuList && !biqfuList.isEmpty()) {
					val biqfupList = createInfoProtoUtils.createBattleItemQueueForUserProtoList(biqfuList);
					resBuilder.addAllBattleItemQueue(biqfupList);
				}
			}
		}
	}

	def setSalesForUser(resBuilder:Builder ,  user:User):Future[Unit]= {
		Future{
			timed("StartupService.setSalesForUser"){
				var userSalesValue = user.getSalesValue()
				val salesLastPurchaseTime = user.getLastPurchaseTime();
				val now = new Date
                logger.info("setting regular sales for user");
				if(salesLastPurchaseTime == null) {
					val ts = new Timestamp(now.getTime());
					updateUtil.updateUserSalesLastPurchaseTime(user.getId(), ts);
				}
				else {
					if(userSalesValue == 0) {
                        logger.info("checking if longer than 5 days");
						if(Math.abs(timeUtils.numDaysDifference(salesLastPurchaseTime, now)) > 5) {
							logger.info("updating user sales value, been longer than 5 days");
							updateUtil.updateUserSalesValue(user.getId(), 1, null);
							userSalesValue = 1;
						}
					}
				}
				val newMinPrice = priceForSalesPackToBeShown(userSalesValue);

        val salesProtoList = new ArrayList[SalesPackageProto]()
				val idsToSalesPackages = salesPackageRetrieveUtil.getSalesPackageIdsToSalesPackages()
				idsToSalesPackages.values().foreach { sp:SalesPackage =>
					if(!sp.getProductId().equalsIgnoreCase(IAPValues.STARTERPACK)
							&& !sp.getProductId().equalsIgnoreCase(IAPValues.BUILDERPACK)
							&& !sp.getProductId().equalsIgnoreCase(IAPValues.STARTERBUILDERPACK))
					{ //make sure it's not starter pack
						if(sp.getPrice() == newMinPrice && timeUtils.isFirstEarlierThanSecond(sp.getTimeStart(), now) &&
								timeUtils.isFirstEarlierThanSecond(now, sp.getTimeEnd())) {
							  val spProto = inAppPurchaseUtil.createSalesPackageProto(
								  sp, salesItemRetrieveUtil, salesDisplayItemRetrieveUtil,
								  customMenuRetrieveUtil);
                salesProtoList.add(spProto)
            }
          }
				}
        inAppPurchaseUtil.sortSalesPackageProtoList(salesProtoList);
        resBuilder.addAllSalesPackages(salesProtoList);
      }
		}
	}

	def priceForSalesPackToBeShown(userSalesValue:Int):Int= {
		//TODO: Possible to rewrite it more succinctly in scala?
		var newMinPrice = 0;
		if(userSalesValue == 0) {
			newMinPrice = 5;
		} else if(userSalesValue == 1) {
			newMinPrice = 10;
		} else if(userSalesValue == 2) {
			newMinPrice = 20;
		} else if(userSalesValue == 3) {
			newMinPrice = 50;
		} else if(userSalesValue > 3) {
			newMinPrice = 100;
		}
		return newMinPrice
	}
  
	def setDefaultLanguagesForUser(resBuilder:Builder , userId:String ):Future[Unit]= {
		Future{
			timed("StartupService.setDefaultLanguagesForUser"){
				val tsfuList = translationSettingsForUserRetrieveUtil.getUserTranslationSettingsForUser(userId);
				logger.info(s"tsfuList: $tsfuList");
				var dlp:DefaultLanguagesProto  = null;
				if(tsfuList != null && !tsfuList.isEmpty()) {
					dlp = createInfoProtoUtils.createDefaultLanguagesProto(tsfuList);
				}
				//if there's no default languages, they havent ever been set
			  if (null != dlp) {
					resBuilder.setUserDefaultLanguages(dlp);
				}
			}
		}
  }
      
  def setTopThreeStrengthLeaderBoard(resBuilder:Builder):Future[Unit]= {
    Future{
      timed("StartupServer.setTopThreeStrengthLeaderBoard") {
        val leaderBoardList = leaderBoard.getTopNStrengths(2);
        resBuilder.addAllTopStrengthLeaderBoards(createInfoProtoUtils.
            createStrengthLeaderBoardProtosWithMonsterId(leaderBoardList, userRetrieveUtils, monsterForUserRetrieveUtils));
      }
    }
  }
      
	def setClanRaidStuff(resBuilder:Builder, user:User, userId:String, now:Timestamp):Future[Unit] ={
		Future{
			timed("StartupService.setClanRaidStuff"){
				val nowDate = new Date(now.getTime());
				val clanId = user.getClanId();
				if (clanId != null) {
  				//get the clan raid information for the clan
					val cepfc = clanEventPersistentForClanRetrieveUtils.getPersistentEventForClanId(clanId);
					if (null != cepfc) {
						val pcecip = createInfoProtoUtils.createPersistentClanEventClanInfoProto(cepfc);
						resBuilder.setCurRaidClanInfo(pcecip);
						//get the clan raid information for all the clan users
						//shouldn't be null (per the retrieveUtils)
						val userIdToCepfu = clanEventPersistentForUserRetrieveUtils.getPersistentEventUserInfoForClanId(clanId);
						logger.info("the users involved in clan raid:$userIdToCepfu");
						if (null == userIdToCepfu || userIdToCepfu.isEmpty()) {
							logger.info("no users involved in clan raid. clanRaid=$cepfc");
						}else {
							val userMonsterIds = monsterStuffUtil.getUserMonsterIdsInClanRaid(userIdToCepfu);
							//TODO: when retrieving clan info, and user's current teams, maybe query for
							//these monsters as well
							val idsToUserMonsters = monsterForUserRetrieveUtils.getSpecificUserMonsters(userMonsterIds);
							userIdToCepfu.values.foreach { cepfu =>
							val pceuip = createInfoProtoUtils.createPersistentClanEventUserInfoProto(cepfu, idsToUserMonsters, null);
							resBuilder.addCurRaidClanUserInfo(pceuip);
							}
							setClanRaidHistoryStuff(resBuilder, userId, nowDate);
						}
					}else {
						logger.info("no clan raid stuff existing for clan=$clanId, user=$user");
					}
				}
				setClanRaidHistoryStuff(resBuilder, userId, nowDate);
			}
		}
	}

	def setClanRaidHistoryStuff(resBuilder:Builder, userId:String, nowDate:Date)= {
		timed("StartupService.setClanRaidHistoryStuff"){
			//the raid stage and reward history for past 7 days
			val nDays = ControllerConstants.CLAN_EVENT_PERSISTENT__NUM_DAYS_FOR_RAID_STAGE_HISTORY;
			val timesToRaidStageHistory = cepfuRaidStageHistoryRetrieveUtils
					.getRaidStageHistoryForPastNDaysForUserId(userId, nDays, nowDate, timeUtils);
			val timesToUserRewards = clanEventPersistentUserRewardRetrieveUtils
					.getCepUserRewardForPastNDaysForUserId(userId, nDays, nowDate, timeUtils);
			//possible for ClanRaidStageHistory to have no rewards if clan didn't beat stage
			timesToRaidStageHistory.keySet.foreach{ aDate =>
				val cepfursh = timesToRaidStageHistory.get(aDate);
				var rewards:java.util.List[ClanEventPersistentUserReward]  = null;
				if (timesToUserRewards.containsKey(aDate)) {
					rewards = timesToUserRewards.get(aDate);
				}
				val stageProto = createInfoProtoUtils.createPersistentClanEventRaidStageHistoryProto(cepfursh, rewards);
				resBuilder.addRaidStageHistory(stageProto);
			}
		}
	}


	def sendOfferChartInstall(installTime:Date , advertiserId:String):Future[Unit] ={
		Future{
			val clientId = "15";
			val appId = "648221050";
			val geo = "N/A";
			val installTimeStr = "" + installTime.getTime();
			val devicePlatform = "iphone";
			val deviceType = "iphone";

			var urlString = new StringBuilder()
			.append("http://offerchart.com/mobileapp/api/send_install_ping?")
			.append("client_id=")
			.append(clientId)
			.append("&app_id=")
			.append(appId)
			.append("&device_id=")
			.append(advertiserId)
			.append("&device_type=")
			.append(deviceType)
			.append("&geo=")
			.append(geo)
			.append("&install_time=")
			.append(installTimeStr)
			.append("&device_platform=")
			.append(devicePlatform)
			.toString();

			logger.info(s"Sending offerchart request:\n $urlString");
			val httpclient = new DefaultHttpClient();
			val httpGet = new HttpGet(urlString);

			try {
				val response1 = httpclient.execute(httpGet);
				val rd = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
				var responseString = Stream.continually(rd.readLine()).takeWhile(_ != null).mkString("\n")
				logger.info(s"Received response: $responseString");
			} catch {
				case t:Throwable => logger.error("failed to make offer chart call", t);
			}
		}
	}


	def syncApsalaridLastloginConsecutivedaysloggedinResetBadges(
			user:User,
			apsalarID:String, 
			loginTime:Timestamp,
			newNumConsecutiveDaysLoggedIn:Int) {
		var apsalarId:String = null
		if (user.getApsalarId() != null && apsalarID == null) {
			apsalarId = user.getApsalarId();
		}else {
			apsalarId = apsalarID
		}
		if (!user.updateAbsoluteApsalaridLastloginBadgesNumConsecutiveDaysLoggedIn(
				apsalarId, loginTime, 0, newNumConsecutiveDaysLoggedIn)) {
			logger.error("problem with updating apsalar id to " + apsalarId
					+ ", last login to " + loginTime
					+ ", and badge count to 0 for " + user
					+ " and newNumConsecutiveDaysLoggedIn is "
					+ newNumConsecutiveDaysLoggedIn);
		}
		if (!insertUtil.insertLastLoginLastLogoutToUserSessions(user.getId(), loginTime, null)) {
			logger.error("problem with inserting last login time for user " + user
					+ ", loginTime=" + loginTime);
		}
	}

	def setConstants(startupBuilder:Builder ,  startupStatus:StartupStatus )= {
		startupBuilder.setStartupConstants(miscMethods.createStartupConstantsProto(globals));
		if (startupStatus == StartupStatus.USER_NOT_IN_DB) {
			val tc = miscMethods.createTutorialConstantsProto();
			startupBuilder.setTutorialConstants(tc);
		}
	}  


	def updateLeaderboard(apsalarId:String , user:User , now:Timestamp , newNumConsecutiveDaysLoggedIn:Int)= {
		if (user != null) {
			val userId = user.getId()
			logger.info(s"Updating leaderboard for user $userId");
			syncApsalaridLastloginConsecutivedaysloggedinResetBadges(user, apsalarId, now, newNumConsecutiveDaysLoggedIn);
		}
	}
}
