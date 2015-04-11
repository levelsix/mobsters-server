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


class StartupService {
    
  @Autowired var  hazelcastPvpUtil : HazelcastPvpUtil  = null
  @Autowired var  timeUtils : TimeUtils  = null
  @Autowired var  userRetrieveUtils : UserRetrieveUtils2  = null
  @Autowired var  questForUserRetrieveUtils : QuestForUserRetrieveUtils2  = null
  @Autowired var  questJobForUserRetrieveUtil : QuestJobForUserRetrieveUtil  = null
  @Autowired var  pvpLeagueForUserRetrieveUtil : PvpLeagueForUserRetrieveUtil2  = null
  @Autowired var  pvpBattleHistoryRetrieveUtil : PvpBattleHistoryRetrieveUtil2  = null
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

  
  
  def loginExistingUser(
      udid:String, 
      playerId:String, 
      resBuilder:Builder, 
      nowDate:Date, 
      now:Timestamp,
      user:User,
      fbid:String,
      freshRestart:Boolean)={
      
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
      
    }
  }
  
  def setUserMonstersEvolving(resBuilder:Builder, userId:String):Future[Unit]= {
    future{
      
    }
  }
  
}