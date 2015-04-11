package com.lvl6.mobsters.services

import com.lvl6.proto.EventStartupProto.StartupRequestProto
import com.lvl6.proto.EventStartupProto.StartupRequestProto.VersionNumberProto
import com.lvl6.proto.EventStartupProto.StartupResponseProto
import com.lvl6.proto.EventStartupProto.StartupResponseProto.Builder
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupStatus
import com.lvl6.proto.EventStartupProto.StartupResponseProto.TutorialConstants
import com.lvl6.proto.EventStartupProto.StartupResponseProto.UpdateStatus
import java.sql.Timestamp
import java.util.Date
import com.lvl6.info.User
import scala.concurrent._
import ExecutionContext.Implicits.global
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils
import com.lvl6.retrieveutils.UserRetrieveUtils2
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils2
import com.lvl6.server.controller.utils.TimeUtils
import com.lvl6.retrieveutils.ClanEventPersistentUserRewardRetrieveUtils2
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils2
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils2
import com.lvl6.retrieveutils.QuestForUserRetrieveUtils2
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2
import com.lvl6.retrieveutils.UserClanRetrieveUtils2
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils2
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentForUserRetrieveUtils2
import org.springframework.beans.factory.annotation.Autowired
import com.lvl6.retrieveutils.CepfuRaidStageHistoryRetrieveUtils2
import com.lvl6.retrieveutils.PrivateChatPostRetrieveUtils2
import com.lvl6.retrieveutils.ClanEventPersistentForClanRetrieveUtils2
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils2
import com.lvl6.retrieveutils.ClanRetrieveUtils2
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2
import com.lvl6.retrieveutils.EventPersistentForUserRetrieveUtils2
import java.util.ArrayList
import com.lvl6.info.QuestForUser
import java.util.HashSet
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils
import scala.collection.JavaConversions._
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil
import com.lvl6.retrieveutils.ItemForUserUsageRetrieveUtil
import com.lvl6.retrieveutils.QuestJobForUserRetrieveUtil
import com.lvl6.retrieveutils.PvpBattleHistoryRetrieveUtil2
import com.lvl6.pvp.HazelcastPvpUtil
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2
import com.lvl6.retrieveutils.TaskForUserClientStateRetrieveUtil
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil
import com.lvl6.retrieveutils.ItemSecretGiftForUserRetrieveUtil
import com.lvl6.retrieveutils.MonsterSnapshotForUserRetrieveUtil
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil
import com.lvl6.utils.CreateInfoProtoUtils
import com.lvl6.info.UserClan
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils
import com.lvl6.info.MonsterForUser
import com.lvl6.info.MonsterHealingForUser
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto
import com.typesafe.scalalogging.Logging
import com.lvl6.utils.utilmethods.DeleteUtil
import com.hazelcast.core.IList
import javax.annotation.Resource
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto
import scala.util.Sorting
import com.lvl6.info.TaskForUserOngoing
import com.lvl6.info.TaskForUserClientState
import java.util.HashMap
import com.lvl6.info.TaskStageForUser
import com.lvl6.retrieveutils.PvpBoardObstacleForUserRetrieveUtil
import com.typesafe.scalalogging.slf4j.LazyLogging
import com.lvl6.proto.EventStartupProto.ForceLogoutResponseProto
import com.lvl6.events.response.ForceLogoutResponseEvent
import com.lvl6.server.EventWriter
import com.lvl6.pvp.PvpUser
import com.lvl6.info.PvpLeagueForUser
import com.lvl6.info.PvpBattleForUser
import java.util.UUID
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils
import com.lvl6.utils.utilmethods.UpdateUtil
import com.lvl6.utils.utilmethods.InsertUtil
import com.lvl6.server.Locker
import com.lvl6.misc.MiscMethods
import com.lvl6.info.MiniJobForUser


class StartupService extends LazyLogging{
    
  @Autowired var  hazelcastPvpUtil : HazelcastPvpUtil  = null
  @Autowired var  timeUtils : TimeUtils  = null
  @Autowired var  userRetrieveUtils : UserRetrieveUtils2  = null
  @Autowired var  questForUserRetrieveUtils : QuestForUserRetrieveUtils2  = null
  @Autowired var  questJobForUserRetrieveUtil : QuestJobForUserRetrieveUtil  = null
  @Autowired var  pvpLeagueForUserRetrieveUtil : PvpLeagueForUserRetrieveUtil2  = null
  @Autowired var  pvpBattleHistoryRetrieveUtil : PvpBattleHistoryRetrieveUtil2  = null
  @Autowired var  pvpBoardObstacleForUserRetrieveUtil : PvpBoardObstacleForUserRetrieveUtil = null 
  @Autowired var  achievementForUserRetrieveUtil : AchievementForUserRetrieveUtil  = null
  @Autowired var  miniJobForUserRetrieveUtil : MiniJobForUserRetrieveUtil  = null
  @Autowired var  itemForUserRetrieveUtil : ItemForUserRetrieveUtil  = null
  @Autowired var  itemForUserUsageRetrieveUtil : ItemForUserUsageRetrieveUtil  = null
  @Autowired var  clanHelpRetrieveUtil : ClanHelpRetrieveUtil  = null
  @Autowired var  clanAvengeRetrieveUtil : ClanAvengeRetrieveUtil  = null
  @Autowired var  clanAvengeUserRetrieveUtil : ClanAvengeUserRetrieveUtil  = null
  @Autowired var  monsterEnhancingForUserRetrieveUtils : MonsterEnhancingForUserRetrieveUtils2  = null
  @Autowired var  monsterHealingForUserRetrieveUtils : MonsterHealingForUserRetrieveUtils2  = null
  @Autowired var  monsterEvolvingForUserRetrieveUtils : MonsterEvolvingForUserRetrieveUtils2  = null
  @Autowired var  monsterForUserRetrieveUtils : MonsterForUserRetrieveUtils2  = null
  @Autowired var  taskForUserCompletedRetrieveUtils : TaskForUserCompletedRetrieveUtils  = null
  @Autowired var  taskForUserOngoingRetrieveUtils : TaskForUserOngoingRetrieveUtils2  = null
  @Autowired var  taskForUserClientStateRetrieveUtil : TaskForUserClientStateRetrieveUtil  = null
  @Autowired var  taskStageForUserRetrieveUtils : TaskStageForUserRetrieveUtils2  = null
  @Autowired var  eventPersistentForUserRetrieveUtils : EventPersistentForUserRetrieveUtils2  = null
  @Autowired var  pvpBattleForUserRetrieveUtils : PvpBattleForUserRetrieveUtils2  = null
  @Autowired var  iapHistoryRetrieveUtils : IAPHistoryRetrieveUtils  = null
  @Autowired var  clanEventPersistentForClanRetrieveUtils : ClanEventPersistentForClanRetrieveUtils2  = null
  @Autowired var  clanEventPersistentForUserRetrieveUtils : ClanEventPersistentForUserRetrieveUtils2  = null
  @Autowired var  cepfuRaidStageHistoryRetrieveUtils : CepfuRaidStageHistoryRetrieveUtils2  = null
  @Autowired var  clanEventPersistentUserRewardRetrieveUtils : ClanEventPersistentUserRewardRetrieveUtils2  = null
  @Autowired var  clanRetrieveUtils : ClanRetrieveUtils2  = null
  @Autowired var  userClanRetrieveUtils : UserClanRetrieveUtils2  = null
  @Autowired var  userFacebookInviteForSlotRetrieveUtils : UserFacebookInviteForSlotRetrieveUtils2  = null
  @Autowired var  clanChatPostRetrieveUtils : ClanChatPostRetrieveUtils2  = null
  @Autowired var  privateChatPostRetrieveUtils : PrivateChatPostRetrieveUtils2  = null
  @Autowired var  itemSecretGiftForUserRetrieveUtil : ItemSecretGiftForUserRetrieveUtil  = null
  @Autowired var  clanMemberTeamDonationRetrieveUtil : ClanMemberTeamDonationRetrieveUtil  = null
  @Autowired var  monsterSnapshotForUserRetrieveUtil : MonsterSnapshotForUserRetrieveUtil  = null
  @Autowired var  deleteUtil : DeleteUtil = null
  @Autowired var  updateUtil : UpdateUtil = null
  @Autowired var  insertUtil : InsertUtil = null
  @Autowired var  locker :  Locker = null
  @Resource(name = "goodEquipsRecievedFromBoosterPacks") var goodEquipsRecievedFromBoosterPacks: IList[RareBoosterPurchaseProto] = null 
  @Autowired var  eventWriter:EventWriter = null
  
  def loginExistingUser(
      udid:String, 
      playerId:String, 
      resBuilder:Builder, 
      nowDate:Date, 
      now:Timestamp,
      user:User,
      fbId:String,
      freshRestart:Boolean)={
      try {
        //force other devices on this account to logout
        forceLogoutOthers(udid, playerId, user, fbId)
        logger.info(s"no major update... getting user info")
        val userId = playerId;
        val userInfo:Future[Unit] = for{
          sipaaq <-  setInProgressAndAvailableQuests(resBuilder, userId)
          suci <-    setUserClanInfos(resBuilder, userId)
          sntp <-    setNoticesToPlayers(resBuilder)
          sums <-    setUserMonsterStuff(resBuilder, userId)
          sbp  <-    setBoosterPurchases(resBuilder)
          sts  <-    setTaskStuff(resBuilder, userId)
          ses  <-    setEventStuff(resBuilder, userId)
          spbo <-    setPvpBoardObstacles(resBuilder, userId)
        } yield Unit
        
        
        
      }catch{
        case t:Throwable => logger.error(s"", t)
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
    future{
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
  
  
  def setUserClanInfos(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
      userClanRetrieveUtils.getUserClansRelatedToUser(userId).foreach{ uc:UserClan =>
        resBuilder.addUserClanInfo(CreateInfoProtoUtils.createFullUserClanProtoFromUserClan(uc))
      }
    }
  }
  
  
  def setNoticesToPlayers(resBuilder:Builder):Future[Unit]= {
    future{
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
    future{
      val userMonsters = monsterForUserRetrieveUtils.getMonstersForUser(userId)
      if(userMonsters != null) {
        userMonsters.foreach{ mfu:MonsterForUser => resBuilder.addUsersMonsters(CreateInfoProtoUtils.createFullUserMonsterProtoFromUserMonster(mfu))}
      }
    }
  }
  
  def setUserMonstersInHealing(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
      val userMonstersHealing = monsterHealingForUserRetrieveUtils.getMonstersForUser(userId)
      if(userMonstersHealing != null) {
        userMonstersHealing.values.foreach{ mhfu:MonsterHealingForUser =>
            resBuilder.addMonstersHealing(CreateInfoProtoUtils.createUserMonsterHealingProtoFromObj(mhfu))
        }
      }
    }
  }
  
  def setUserMonstersEnhancing(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
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
  
  def setUserMonstersEvolving(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
      val userMonstersEvolving = monsterEvolvingForUserRetrieveUtils.getCatalystIdsToEvolutionsForUser(userId)
      if(userMonstersEvolving != null) {
        userMonstersEvolving.values.foreach{ mefu =>
          val eup = CreateInfoProtoUtils.createUserEvolutionProtoFromEvolution(mefu)
          resBuilder.setEvolution(eup)
        }
      }
    }
  }
  
  
  
  def setBoosterPurchases(resBuilder : Builder):Future[Unit]= {
    future{
      val it = goodEquipsRecievedFromBoosterPacks.iterator()
      val boosterPurchases = scala.collection.mutable.ArrayBuffer.empty[RareBoosterPurchaseProto]
      while(it.hasNext()) {
        boosterPurchases += it.next()
      }
      boosterPurchases.sortBy(x => x.getTimeOfPurchase).foreach(resBuilder.addRareBoosterPurchases(_))
    }
  }
  
  
  def setTaskStuff(resBuilder:Builder, userId:String):Future[Unit]= {
    def completedTasksFuture = future{
      val utcList = taskForUserCompletedRetrieveUtils.getAllCompletedTasksForUser(userId)
      resBuilder.addAllCompletedTasks(CreateInfoProtoUtils.createUserTaskCompletedProto(utcList))
      val taskIds = taskForUserCompletedRetrieveUtils.getTaskIds(utcList)
      resBuilder.addAllCompletedTaskIds(taskIds)
    }
    def ongoingTaskFuture = future{
      val aTaskForUser = taskForUserOngoingRetrieveUtils.getUserTaskForUserId(userId)
      if(aTaskForUser != null) {
        val tfucs = taskForUserClientStateRetrieveUtil.getTaskForUserClientState(userId)
        logger.warn(s"user has incompleted task userTask=$aTaskForUser")
        setOngoingTask(resBuilder, userId, aTaskForUser, tfucs)
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
    future{
      val events = eventPersistentForUserRetrieveUtils.getUserPersistentEventForUserId(userId)
      events.foreach{ epfu =>
        resBuilder.addUserEvents(CreateInfoProtoUtils.createUserPersistentEventProto(epfu))  
      }
    }
  }
  
  
  def setPvpBoardObstacles(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
      val boList = pvpBoardObstacleForUserRetrieveUtil.getPvpBoardObstacleForUserId(userId)
      boList.foreach{ pbofu => 
        resBuilder.addUserPvpBoardObstacles(CreateInfoProtoUtils.createUserPvpBoardObstacleProto(pbofu))  
      }
    }
  }
  
  def pvpBattleStuff(resBuilder:Builder, user:User, userId:String, isFreshRestart:Boolean, battleEndTime:Timestamp):Future[PvpLeagueForUser] ={
    future{
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
  
  
  def setAllStaticData(resBuilder:Builder, userId:String, userIdSet:Boolean):Future[Unit]= {
    future{
      val sdp = MiscMethods.getAllStaticData(userId, userIdSet, questForUserRetrieveUtils)
      resBuilder.setStaticDataStuffProto(sdp)
    }
  }
  
  def setAchievementStuff(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
      val achievementsIdToUserAchievements = achievementForUserRetrieveUtil.getSpecificOrAllAchievementIdToAchievementForUserId(userId, null)
      achievementsIdToUserAchievements.values.foreach{ afu =>
        resBuilder.addUserAchievements(CreateInfoProtoUtils.createUserAchievementProto(afu))  
      }
    }
  }
  
  def setMiniJob(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
      val miniJobIdtoUserMiniJobs = miniJobForUserRetrieveUtil.getSpecificOrAllIdToMiniJobForUser(userId, null)
      if(!miniJobIdtoUserMiniJobs.isEmpty()) {
        val mjfuList = new ArrayList[MiniJobForUser](miniJobIdtoUserMiniJobs.values)
        resBuilder.addAllUserMiniJobProtos(CreateInfoProtoUtils.createUserMiniJobProtos(mjfuList, null))
      }
    }
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}