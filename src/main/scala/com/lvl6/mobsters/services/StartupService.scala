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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.hazelcast.core.IList
import com.lvl6.events.RequestEvent
import com.lvl6.events.request.StartupRequestEvent
import com.lvl6.events.response.ForceLogoutResponseEvent
import com.lvl6.events.response.StartupResponseEvent
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
import com.lvl6.info.TaskForUserClientState
import com.lvl6.info.TaskForUserOngoing
import com.lvl6.info.TaskStageForUser
import com.lvl6.info.User
import com.lvl6.info.UserClan
import com.lvl6.misc.MiscMethods
import com.lvl6.properties.ControllerConstants
import com.lvl6.properties.Globals
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto
import com.lvl6.proto.ChatProto.ChatType
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
import com.lvl6.pvp.HazelcastPvpUtil
import com.lvl6.pvp.PvpUser
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil
import com.lvl6.retrieveutils.CepfuRaidStageHistoryRetrieveUtils2
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentUserRewardRetrieveUtils2
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil
import com.lvl6.retrieveutils.ClanRetrieveUtils2
import com.lvl6.retrieveutils.EventPersistentForUserRetrieveUtils2
import com.lvl6.retrieveutils.FirstTimeUsersRetrieveUtils
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
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils
import com.lvl6.server.EventWriter
import com.lvl6.server.GameServer
import com.lvl6.server.Locker
import com.lvl6.server.controller.actionobjects.RedeemSecretGiftAction
import com.lvl6.server.controller.actionobjects.RetrieveMiniEventAction
import com.lvl6.server.controller.actionobjects.SetClanChatMessageAction
import com.lvl6.server.controller.actionobjects.SetClanHelpingsAction
import com.lvl6.server.controller.actionobjects.SetClanMemberTeamDonationAction
import com.lvl6.server.controller.actionobjects.SetClanRetaliationsAction
import com.lvl6.server.controller.actionobjects.SetFacebookExtraSlotsAction
import com.lvl6.server.controller.actionobjects.SetGlobalChatMessageAction
import com.lvl6.server.controller.actionobjects.SetPrivateChatMessageAction
import com.lvl6.server.controller.actionobjects.SetPvpBattleHistoryAction
import com.lvl6.server.controller.actionobjects.StartUpResource
import com.lvl6.server.controller.utils.MonsterStuffUtils
import com.lvl6.server.controller.utils.TimeUtils
import com.lvl6.server.metrics.Metrics._
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
	@Autowired var  cepfuRaidStageHistoryRetrieveUtils : CepfuRaidStageHistoryRetrieveUtils2  = null
	@Autowired var  clanAvengeRetrieveUtil : ClanAvengeRetrieveUtil  = null
	@Autowired var  clanAvengeUserRetrieveUtil : ClanAvengeUserRetrieveUtil  = null
	@Autowired var  clanChatPostRetrieveUtils : ClanChatPostRetrieveUtils2  = null
	@Autowired var  clanEventPersistentForClanRetrieveUtils : ClanEventPersistentForClanRetrieveUtils2  = null
	@Autowired var  clanEventPersistentForUserRetrieveUtils : ClanEventPersistentForUserRetrieveUtils2  = null
	@Autowired var  clanEventPersistentUserRewardRetrieveUtils : ClanEventPersistentUserRewardRetrieveUtils2  = null
	@Autowired var  clanHelpRetrieveUtil : ClanHelpRetrieveUtil  = null
	@Autowired var  clanMemberTeamDonationRetrieveUtil : ClanMemberTeamDonationRetrieveUtil  = null
	@Autowired var  clanRetrieveUtils : ClanRetrieveUtils2  = null
	@Autowired var  eventPersistentForUserRetrieveUtils : EventPersistentForUserRetrieveUtils2  = null
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
  
  @Autowired var  createInfoProtoUtils : CreateInfoProtoUtils =null
  @Autowired var  deleteUtil : DeleteUtil = null
  @Autowired var  insertUtil : InsertUtil = null
  @Autowired var  updateUtil : UpdateUtil = null
  @Autowired var  hazelcastPvpUtil : HazelcastPvpUtil  = null
  @Autowired var  timeUtils : TimeUtils  = null
  @Autowired var  locker :  Locker = null
  @Autowired var  eventWriter:EventWriter = null
  
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
    MiscMethods.setMDCProperties(udid, null, MiscMethods.getIPOfPlayer(server, null, udid));
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
    timed("startupFinished"){
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
          || (clientSuperEqual && clientMajorEqual && clientMinorGreater)) {
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
      val tempClientVersionNum = clientVersionNum * 10;
      val tempLatestVersionNum = GameServer.clientVersionNumber * 10;
      // Check version number
      if (tempClientVersionNum < tempLatestVersionNum) {
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
    timed("tutorialUserAccounting"){
      val userLoggedIn = LoginHistoryRetrieveUtils.userLoggedInByUDID(udid);
      //TODO: Retrieve from user table
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
        logger.info("new player with udid " + udid);
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
        //sipaaq <-  setInProgressAndAvailableQuests(resBuilder, userId)
        suci <-    setUserClanInfos(resBuilder, userId)
        sntp <-    setNoticesToPlayers(resBuilder)
        sums <-    setUserMonsterStuff(resBuilder, userId)
        sbp  <-    setBoosterPurchases(resBuilder)
        sts  <-    setTaskStuff(resBuilder, userId)
        ses  <-    setEventStuff(resBuilder, userId)
        spbo <-    setPvpBoardObstacles(resBuilder, userId)
        sas  <-    setAchievementStuff(resBuilder, playerId)
        smj  <-    setMiniJob(resBuilder, playerId)
        sui  <-    setUserItems(resBuilder, playerId);
        swpciap <- setWhetherPlayerCompletedInAppPurchase(resBuilder, user)
        ssg  <-    setSecretGifts(resBuilder, playerId, now.getTime())
        sr   <-    setResearch(resBuilder, playerId)
        sbifu <-   setBattleItemForUser(resBuilder, playerId)
        sbiqfu <-  setBattleItemQueueForUser(resBuilder, playerId)
        smefu <-   setMiniEventForUser(resBuilder, user, playerId, nowDate)
        scrs  <-   setClanRaidStuff(resBuilder, user, playerId, now)
        plfu  <-   pvpBattleStuff(resBuilder, user, playerId, freshRestart, now)
      } yield plfu
      
      userInfo onSuccess {
        case plfu:PvpLeagueForUser =>  finishLoginExisting(resBuilder, user, playerId, nowDate, plfu, sd) 
      }
      
      userInfo onFailure {
        case t:Throwable => {
          logger.error("Error running login futures", t)
          loginFinished(playerId)
          exceptionInStartup(sd)
        }
      }
      
    }catch{
      case t:Throwable => logger.error(s"", t)
      loginFinished(playerId)
    }
  }
  
  def loginFinished(playerId:String)= {
    locker.unlockPlayer(UUID.fromString(playerId), this.getClass().getSimpleName());
  }
  
  
  def finishLoginExisting(resBuilder:Builder, user:User, playerId:String, nowDate:Date, plfu:PvpLeagueForUser, sd:StartupData) = {
    timed("finishExsitingLogin"){
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
              ChatType.GLOBAL_CHAT.toString(),
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
            userFacebookInviteForSlotRetrieveUtils);
        sfesa.setUp(fillMe);
    
        val spbha = new SetPvpBattleHistoryAction(
            resBuilder, 
            user, 
            playerId, 
            pvpBattleHistoryRetrieveUtil,
            monsterForUserRetrieveUtils, 
            clanRetrieveUtils,
            hazelcastPvpUtil);
        spbha.setUp(fillMe);
    
        //CLAN DATA
        val cdpb = ClanDataProto.newBuilder();
        val sccma = new SetClanChatMessageAction(cdpb, user, clanChatPostRetrieveUtils);
        sccma.setUp(fillMe);
    
        val scha = new SetClanHelpingsAction(cdpb, user, playerId, clanHelpRetrieveUtil);
        scha.setUp(fillMe);
    
        val scra = new SetClanRetaliationsAction(cdpb, user, playerId, clanAvengeRetrieveUtil, clanAvengeUserRetrieveUtil);
        scra.setUp(fillMe);
    
        val scmtda = new SetClanMemberTeamDonationAction(
            cdpb, 
            user, 
            playerId, 
            clanMemberTeamDonationRetrieveUtil,
            monsterSnapshotForUserRetrieveUtil);
        scmtda.setUp(fillMe);
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
        val fup = CreateInfoProtoUtils.createFullUserProtoFromUser(user, plfu, clan);
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
  
  def setInProgressAndAvailableQuests(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setInProgressAndAvailableQuests"){
        val inProgressAndredeemedUserQuests = questForUserRetrieveUtils.getUserQuestsForUser(userId)
        if(inProgressAndredeemedUserQuests != null) {
          val inProgressQuests = new ArrayList[QuestForUser]()
          val questIds = new HashSet[Integer]()
          val redeemedQuestIds = new ArrayList[Integer]()
          val questIdtoQuests = QuestRetrieveUtils.getQuestIdsToQuests
          inProgressAndredeemedUserQuests.foreach{ uq:QuestForUser  =>
            if(!uq.isRedeemed()) {
              inProgressQuests.add(uq)
              questIds.add(uq.getQuestId)
            }else {
              redeemedQuestIds.add(uq.getQuestId)
            }
          }
          val questIdtoUserQuestJobs = questJobForUserRetrieveUtil.getSpecificOrAllQuestIdToQuestJobsForUserId(userId, questIds)
          val currentUserQuests = CreateInfoProtoUtils.createFullUserQuestDataLarges(inProgressQuests, questIdtoQuests, questIdtoUserQuestJobs)
          resBuilder.addAllUserQuests(currentUserQuests)
          resBuilder.addAllRedeemedQuestIds(redeemedQuestIds)
        }
      }
    }
  }
  
  
  def setUserClanInfos(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setUserClanInfos"){
        userClanRetrieveUtils.getUserClansRelatedToUser(userId).foreach{ uc:UserClan =>
          resBuilder.addUserClanInfo(CreateInfoProtoUtils.createFullUserClanProtoFromUserClan(uc))
        }
      }
    }
  }
  
  
  def setNoticesToPlayers(resBuilder:Builder):Future[Unit]= {
    Future{
      val notices = StartupStuffRetrieveUtils.getAllActiveAlerts
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
      userMonstersEvolving <- setUserMonsters(resBuilder,userId)
    } yield Unit
  }
  
  def setUserMonsters(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setUserMonsters"){
        val userMonsters = monsterForUserRetrieveUtils.getMonstersForUser(userId)
        if(userMonsters != null) {
          userMonsters.foreach{ mfu:MonsterForUser => resBuilder.addUsersMonsters(CreateInfoProtoUtils.createFullUserMonsterProtoFromUserMonster(mfu))}
        }
      }
    }
  }
  
  def setUserMonstersInHealing(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setUsersMonstersInHealing"){
        val userMonstersHealing = monsterHealingForUserRetrieveUtils.getMonstersForUser(userId)
        if(userMonstersHealing != null) {
          userMonstersHealing.values.foreach{ mhfu:MonsterHealingForUser =>
              resBuilder.addMonstersHealing(CreateInfoProtoUtils.createUserMonsterHealingProtoFromObj(mhfu))
          }
        }
      }
    }
  }
  
  def setUserMonstersEnhancing(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setUsersMonstersEnhancing"){
        val userMonstersEnhancing = monsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId)
        if(userMonstersEnhancing != null) {
          var baseMonster:UserEnhancementItemProto = null;
          val feederUserMonsterIds = new ArrayList[String]();
          val feederProtos = new ArrayList[UserEnhancementItemProto]();
          userMonstersEnhancing.values().foreach{mefu =>
            val ueip = CreateInfoProtoUtils.createUserEnhancementItemProtoFromObj(mefu)
            val startTime = mefu.getExpectedStartTime;
            if(startTime == null) {
              baseMonster = ueip
            }else {
              feederProtos.add(ueip)
              feederUserMonsterIds.add(mefu.getMonsterForUserId)
            }
          }
          if(baseMonster == null) {
            logger.error(s"no base monster enhancement. deleting inEnhancing=$userMonstersEnhancing.values()")
            try {
              val numDeleted = deleteUtil.deleteMonsterEnhancingForUser(userId, feederUserMonsterIds)
              logger.info(s"numDeleted enhancements: $numDeleted")
            }catch{
              case t:Throwable => logger.error(s"unable to delete orphaned enhancements", t)
            }
          }else {
            val uep = CreateInfoProtoUtils.createUserEnhancementProtoFromObj(userId, baseMonster, feederProtos)
            resBuilder.setEnhancements(uep)
          }
        }
      }
    }
  }
  
  def setUserMonstersEvolving(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setUserMonstersEvolving"){
        val userMonstersEvolving = monsterEvolvingForUserRetrieveUtils.getCatalystIdsToEvolutionsForUser(userId)
        if(userMonstersEvolving != null) {
          userMonstersEvolving.values.foreach{ mefu =>
            val eup = CreateInfoProtoUtils.createUserEvolutionProtoFromEvolution(mefu)
            resBuilder.setEvolution(eup)
          }
        }
      }
    }
  }
  
  
  
  def setBoosterPurchases(resBuilder : Builder):Future[Unit]= {
    Future{
      timed("setBoosterPurchases"){
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
      timed("setTaskStuff_completedTasks"){
        val utcList = taskForUserCompletedRetrieveUtils.getAllCompletedTasksForUser(userId)
        resBuilder.addAllCompletedTasks(CreateInfoProtoUtils.createUserTaskCompletedProto(utcList))
        val taskIds = taskForUserCompletedRetrieveUtils.getTaskIds(utcList)
        resBuilder.addAllCompletedTaskIds(taskIds)
      }
    }
    def ongoingTaskFuture = Future{
      timed("setTaskStuff_ongingTasks"){
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
      val mutp = CreateInfoProtoUtils.createMinimumUserTaskProto(userId, aTaskForUser, tfucs)
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
        val tsp = CreateInfoProtoUtils.createTaskStageProto(taskId, stageNum, stageNumToTsfu.get(stageNum))
        resBuilder.addCurTaskStages(tsp)
      }
    }catch{
      case t:Throwable => logger.error(s"could not create existing task, letting it get deleted when user starts another task", t)
    }
  }
  
  
  def setEventStuff(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setEventStuff"){
        val events = eventPersistentForUserRetrieveUtils.getUserPersistentEventForUserId(userId)
        events.foreach{ epfu =>
          resBuilder.addUserEvents(CreateInfoProtoUtils.createUserPersistentEventProto(epfu))  
        }
      }
    }
  }
  
  
  def setPvpBoardObstacles(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      val boList = pvpBoardObstacleForUserRetrieveUtil.getPvpBoardObstacleForUserId(userId)
      boList.foreach{ pbofu => 
        resBuilder.addUserPvpBoardObstacles(CreateInfoProtoUtils.createUserPvpBoardObstacleProto(pbofu))  
      }
    }
  }
  
  def pvpBattleStuff(resBuilder:Builder, user:User, userId:String, isFreshRestart:Boolean, battleEndTime:Timestamp):Future[PvpLeagueForUser] ={
    Future{
      timed("pvpBattleStuff"){
        val plfu = pvpLeagueForUserRetrieveUtil.getUserPvpLeagueForId(userId)
        val pu = new PvpUser(plfu)
        hazelcastPvpUtil.replacePvpUser(pu, userId)
        if(isFreshRestart) {
          val battle = pvpBattleForUserRetrieveUtils.getPvpBattleForUserForAttacker(userId);
          if(battle != null) {
          	val battleStartTime = new Timestamp(battle.getBattleStartTime.getTime)
            var eloAttackerLoses = battle.getAttackerLoseEloChange
            if(plfu.getElo+eloAttackerLoses < 0) {
              eloAttackerLoses = -1*plfu.getElo
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
      timed("penalizeUserForLeavingGameWhileInPvp") {
        if(defenderId == null || defenderId.isEmpty()) {
          try {
            defenderUuid = UUID.fromString(defenderId)
            invalidUuids = false
          }catch{
            case t:Throwable => {
              logger.error(s"UUID error. Incorrect defenderId=$defenderId", t)
            }
          }
          if (invalidUuids) return
          //only lock real users
          if (null != defenderUuid)  locker.lockPlayer(defenderUuid, this.getClass().getSimpleName())
        }
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
          attackerCurLeague = PvpLeagueRetrieveUtils.getLeagueIdForElo(attackerCurElo, attackerPrevLeague)
          attackerCurRank = PvpLeagueRetrieveUtils.getRankForElo(attackerCurElo, attackerCurLeague);
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
            defenderCurLeague = PvpLeagueRetrieveUtils.getLeagueIdForElo(defenderCurElo, defenderPrevLeague);
            defenderCurRank = PvpLeagueRetrieveUtils.getRankForElo(defenderCurElo, defenderCurLeague);
    
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
  
  
  def setAllStaticData(resBuilder:Builder, userId:String, userIdSet:Boolean):Future[Unit]= {
    Future{
      val sdp = MiscMethods.getAllStaticData(userId, userIdSet, questForUserRetrieveUtils)
      resBuilder.setStaticDataStuffProto(sdp)
    }
  }
  
  def setAchievementStuff(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setAchievementStuff"){
        val achievementsIdToUserAchievements = achievementForUserRetrieveUtil.getSpecificOrAllAchievementIdToAchievementForUserId(userId, null)
        achievementsIdToUserAchievements.values.foreach{ afu =>
          resBuilder.addUserAchievements(CreateInfoProtoUtils.createUserAchievementProto(afu))  
        }
      }
    }
  }
  
  def setMiniJob(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      timed("setMiniJob"){
        val miniJobIdtoUserMiniJobs = miniJobForUserRetrieveUtil.getSpecificOrAllIdToMiniJobForUser(userId, null)
        if(!miniJobIdtoUserMiniJobs.isEmpty()) {
          val mjfuList = new ArrayList[MiniJobForUser](miniJobIdtoUserMiniJobs.values)
          resBuilder.addAllUserMiniJobProtos(CreateInfoProtoUtils.createUserMiniJobProtos(mjfuList, null))
        }
      }
    }
  }
  
  def setUserItems(resBuilder:Builder, userId:String):Future[Unit]= {
    Future{
      /*NOTE: DB CALL*/
      timed("setUserItems"){
        val itemIdToUserItems = itemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(userId, null);
        if (!itemIdToUserItems.isEmpty()) {
          val uipList = CreateInfoProtoUtils.createUserItemProtosFromUserItems(new ArrayList[ItemForUser](itemIdToUserItems.values()));
          resBuilder.addAllUserItems(uipList);
        }
        /*NOTE: DB CALL*/
        val itemsUsed = itemForUserUsageRetrieveUtil.getItemForUserUsage(userId, null);
        itemsUsed.foreach{ ifuu =>
          val uiup = CreateInfoProtoUtils.createUserItemUsageProto(ifuu);
          resBuilder.addItemsInUse(uiup);
        }
      }
    }
  }
  
  def setWhetherPlayerCompletedInAppPurchase(resBuilder:Builder, user:User):Future[Unit]= {
    Future{
      /*NOTE: DB CALL*/
      timed("setWhetherPlayerCompletedInAppPurchase"){
        val hasPurchased = iapHistoryRetrieveUtils.checkIfUserHasPurchased(user.getId());
        resBuilder.setPlayerHasBoughtInAppPurchase(hasPurchased);
      }
    }
  }

  def setSecretGifts(resBuilder:Builder, userId:String , now:Long):Future[Unit]= {
    Future{
      timed("setSecretGifts"){
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
        val nuGiftsProtos = CreateInfoProtoUtils.createUserItemSecretGiftProto(gifts);
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
    timed("giveGifts"){
      var giftList = RedeemSecretGiftAction.calculateGiftsForUser(userId, numGifts, now);
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
        logger.error("Error calculating the new SecretGifts. nuGifts=$giftList, ids=$ids");
      }
    }
  }

  def setResearch(resBuilder:Builder , userId:String ):Future[Unit]= {
    Future{
      timed("setResearch"){
        val userResearchs = researchForUserRetrieveUtil.getAllResearchForUser(userId);
        if(null != userResearchs && !userResearchs.isEmpty()) {
          val urpList = CreateInfoProtoUtils.createUserResearchProto(userResearchs);
          resBuilder.addAllUserResearchs(urpList);
        }
      }
    }
  }

  def setBattleItemQueueForUser(resBuilder:Builder , userId:String ):Future[Unit]= {
    Future{
      timed("setBattleItemQueueForUser"){
        var biqfuList = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(userId)
        if (null != biqfuList && !biqfuList.isEmpty()) {
          val biqfupList = CreateInfoProtoUtils.createBattleItemQueueForUserProtoList(biqfuList);
          resBuilder.addAllBattleItemQueue(biqfupList);
        }
      }
    }
  }
  
  def setBattleItemForUser(resBuilder:Builder , userId:String ):Future[Unit]= {
    Future{
      timed("setBattleItemForUser"){
        val bifuList = battleItemForUserRetrieveUtil.getUserBattleItemsForUser(userId);
        if (null != bifuList && !bifuList.isEmpty()) {
          val biqfupList = CreateInfoProtoUtils.convertBattleItemForUserListToBattleItemForUserProtoList(bifuList);
          resBuilder.addAllBattleItem(biqfupList);
        }
      }
    }
  }

  def setDefaultLanguagesForUser(resBuilder:Builder , userId:String ):Future[Unit]= {
    Future{
      timed("setDefaultLanguagesForUser"){
      val tsfuList = translationSettingsForUserRetrieveUtil.getUserTranslationSettingsForUser(userId);
        logger.info("tsfuList: $tsfuList");
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
  
  def setMiniEventForUser(resBuilder:Builder, u:User, userId:String, now:Date):Future[Unit]={
    Future{
      timed("setMiniEventForUser"){
        val rmeaResBuilder =  RetrieveMiniEventResponseProto.newBuilder();
        val rmea = new RetrieveMiniEventAction(
            userId, 
            now, 
            userRetrieveUtils,
            miniEventForUserRetrieveUtil,
            miniEventGoalForUserRetrieveUtil, 
            insertUtil, 
            deleteUtil);
        rmea.execute(rmeaResBuilder);
        if (rmeaResBuilder.getStatus().equals(RetrieveMiniEventStatus.SUCCESS) &&  null != rmea.getCurActiveMiniEvent()){
          //get UserMiniEvent info and create the proto to set into resBuilder
          //TODO: Consider protofying MiniEvent stuff
          val  umep = CreateInfoProtoUtils.createUserMiniEventProto(
                  rmea.getMefu(), 
                  rmea.getCurActiveMiniEvent(),
                  rmea.getMegfus(),
                  rmea.getLvlEntered(), 
                  rmea.getRewards(),
                  rmea.getGoals(), 
                  rmea.getLeaderboardRewards());
          resBuilder.setUserMiniEvent(umep);
        }
      }
    }
  }

  def setClanRaidStuff(resBuilder:Builder, user:User, userId:String, now:Timestamp):Future[Unit] ={
    Future{
      timed("setClanRaidStuff"){
        val nowDate = new Date(now.getTime());
        val clanId = user.getClanId();
        if (clanId != null) {
          /*NOTE: DB CALL*/
          //get the clan raid information for the clan
          val cepfc = clanEventPersistentForClanRetrieveUtils.getPersistentEventForClanId(clanId);
          if (null != cepfc) {
            val pcecip = CreateInfoProtoUtils.createPersistentClanEventClanInfoProto(cepfc);
            resBuilder.setCurRaidClanInfo(pcecip);
            /*NOTE: DB CALL*/
            //get the clan raid information for all the clan users
            //shouldn't be null (per the retrieveUtils)
            val userIdToCepfu = clanEventPersistentForUserRetrieveUtils.getPersistentEventUserInfoForClanId(clanId);
            logger.info("the users involved in clan raid:$userIdToCepfu");
            if (null == userIdToCepfu || userIdToCepfu.isEmpty()) {
              logger.info("no users involved in clan raid. clanRaid=$cepfc");
            }else {
              val userMonsterIds = MonsterStuffUtils.getUserMonsterIdsInClanRaid(userIdToCepfu);
              /*NOTE: DB CALL*/
              //TODO: when retrieving clan info, and user's current teams, maybe query for
              //these monsters as well
              val idsToUserMonsters = monsterForUserRetrieveUtils.getSpecificUserMonsters(userMonsterIds);
              userIdToCepfu.values.foreach { cepfu =>
                val pceuip = CreateInfoProtoUtils.createPersistentClanEventUserInfoProto(cepfu, idsToUserMonsters, null);
                resBuilder.addCurRaidClanUserInfo(pceuip);
              }
              setClanRaidHistoryStuff(resBuilder, userId, nowDate);
            }
          }else {
            logger.info("no clan raid stuff existing for clan=$clanId, user=$user");
          }
        }
      }
    }
  }
  
  def setClanRaidHistoryStuff(resBuilder:Builder, userId:String, nowDate:Date)= {
    timed("setClanRaidHistoryStuff"){
      /*NOTE: DB CALL*/
      //the raid stage and reward history for past 7 days
      val nDays = ControllerConstants.CLAN_EVENT_PERSISTENT__NUM_DAYS_FOR_RAID_STAGE_HISTORY;
      val timesToRaidStageHistory = cepfuRaidStageHistoryRetrieveUtils
        .getRaidStageHistoryForPastNDaysForUserId(userId, nDays, nowDate, timeUtils);
      /*NOTE: DB CALL*/
      val timesToUserRewards = clanEventPersistentUserRewardRetrieveUtils
        .getCepUserRewardForPastNDaysForUserId(userId, nDays, nowDate, timeUtils);
      //possible for ClanRaidStageHistory to have no rewards if clan didn't beat stage
      timesToRaidStageHistory.keySet.foreach{ aDate =>
        val cepfursh = timesToRaidStageHistory.get(aDate);
        var rewards:java.util.List[ClanEventPersistentUserReward]  = null;
        if (timesToUserRewards.containsKey(aDate)) {
          rewards = timesToUserRewards.get(aDate);
        }
        val stageProto = CreateInfoProtoUtils.createPersistentClanEventRaidStageHistoryProto(cepfursh, rewards);
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
  
      logger.info("Sending offerchart request:\n" + urlString);
      val httpclient = new DefaultHttpClient();
      val httpGet = new HttpGet(urlString);
  
      try {
        val response1 = httpclient.execute(httpGet);
        val rd = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
        var responseString = Stream.continually(rd.readLine()).takeWhile(_ != null).mkString("\n")
        logger.info("Received response: " + responseString);
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
    startupBuilder.setStartupConstants(MiscMethods.createStartupConstantsProto(globals));
    if (startupStatus == StartupStatus.USER_NOT_IN_DB) {
      val tc = MiscMethods.createTutorialConstantsProto();
      startupBuilder.setTutorialConstants(tc);
    }
  }  
  
  
  def updateLeaderboard(apsalarId:String , user:User , now:Timestamp , newNumConsecutiveDaysLoggedIn:Int)= {
    if (user != null) {
      val userId = user.getId()
      logger.info(s"Updating leaderboard for user $userId");
      syncApsalaridLastloginConsecutivedaysloggedinResetBadges(user, apsalarId, now, newNumConsecutiveDaysLoggedIn)
    }
  }
}