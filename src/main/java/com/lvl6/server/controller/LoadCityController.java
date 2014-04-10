package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LoadCityRequestEvent;
import com.lvl6.events.response.LoadCityResponseEvent;
import com.lvl6.info.City;
import com.lvl6.info.CityElement;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventCityProto.LoadCityRequestProto;
import com.lvl6.proto.EventCityProto.LoadCityResponseProto;
import com.lvl6.proto.EventCityProto.LoadCityResponseProto.Builder;
import com.lvl6.proto.EventCityProto.LoadCityResponseProto.LoadCityStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.CityElementsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class LoadCityController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  public LoadCityController() {
    numAllocatedThreads = 3;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new LoadCityRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_LOAD_CITY_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    LoadCityRequestProto reqProto = ((LoadCityRequestEvent)event).getLoadCityRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int cityId = reqProto.getCityId();
    City city = CityRetrieveUtils.getCityForCityId(cityId);

    LoadCityResponseProto.Builder resBuilder = LoadCityResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setCityId(cityId);

    resBuilder.setStatus(LoadCityStatus.SUCCESS);

//    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
//      int currentCityRankForUser = RetrieveUtils.userCityRetrieveUtils().getCurrentCityRankForUser(senderProto.getUserId(), cityId);

      boolean legitCityLoad = checkLegitCityLoad(resBuilder, user, city);//, currentCityRankForUser);

      if (legitCityLoad) {
        List<CityElement> neutralCityElements = CityElementsRetrieveUtils.getCityElementsForCity(cityId);
        if (neutralCityElements != null) {
          for (CityElement nce : neutralCityElements) {
            resBuilder.addCityElements(CreateInfoProtoUtils.createCityElementProtoFromCityElement(nce));
          }
        }

//        List<Task> tasks = TaskRetrieveUtils.getAllTasksForCityId(cityId);
//        if (tasks != null && tasks.size() > 0) {
//          setResponseUserTaskInfos(resBuilder, tasks, user.getId(), senderProto.getUserType());
//        }
        
//        List<Monster> bosses = MonsterRetrieveUtils.getBossesForCityId(cityId);
//        if (bosses != null && bosses.size() > 0) {
//          List<Integer> bossIds = new ArrayList<Integer>();
//          for (Monster b : bosses) {
//            bossIds.add(b.getId());
//          }
//          setResponseUserBossInfos(resBuilder, bossIds, user.getId());
//        }

//        List<Quest> questsInCity = QuestRetrieveUtils.getQuestsInCity(cityId);
//        if (questsInCity != null && questsInCity.size() > 0) {
//          setResponseDefeatTypeJobEnemies(resBuilder, questsInCity, user, cityId);
//        }
        
        List<QuestForUser> allUnredeemedUserQuests = RetrieveUtils.questForUserRetrieveUtils().getUnredeemedUserQuestsForUser(senderProto.getUserId());
        List<QuestForUser> userQuestsInCity = new ArrayList<QuestForUser>();
        if (allUnredeemedUserQuests != null && allUnredeemedUserQuests.size() > 0) {
          for (QuestForUser uq : allUnredeemedUserQuests) {
            Quest q = QuestRetrieveUtils.getQuestForQuestId(uq.getQuestId());
            if (q.getCityId() == cityId) {
              userQuestsInCity.add(uq);
            }
          }
//          addFullUserQuestDataLarges(resBuilder, userQuestsInCity, senderProto.getUserType());
        }
      }

      LoadCityResponseEvent resEvent = new LoadCityResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setLoadCityResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

    } catch (Exception e) {
      log.error("exception in LoadCity processEvent", e);
    } finally {
//      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }
  
//  private void addFullUserQuestDataLarges(Builder resBuilder, List<UserQuest> inProgressUserQuests, UserType userType) {
//    Map<Integer, Quest> questIdsToQuests = QuestRetrieveUtils.getQuestIdsToQuests();
//    resBuilder.addAllInProgressUserQuestDataInCity(CreateInfoProtoUtils.createFullUserQuestDataLarges(inProgressUserQuests, questIdsToQuests, userType));
//  }

  /*private void setResponseDefeatTypeJobEnemies(Builder resBuilder, List<Quest> questsInCity, User user, int cityId) {
    Map<UserType, Integer> numToGenerate = new HashMap<UserType, Integer>();

    for (Quest quest : questsInCity) {
      List<Integer> defeatTypeJobIds = quest.getDefeatGoodGuysJobsRequired();
      if (defeatTypeJobIds != null && defeatTypeJobIds.size() > 0) {
        Map<Integer, DefeatTypeJob> defeatTypeJobIdsToDefeatTypeJobs = DefeatTypeJobRetrieveUtils.getDefeatTypeJobsForDefeatTypeJobIds(defeatTypeJobIds);
        if (defeatTypeJobIdsToDefeatTypeJobs != null) {
          for (DefeatTypeJob dtj : defeatTypeJobIdsToDefeatTypeJobs.values()) {
            if (dtj.getEnemyType() == DefeatTypeJobEnemyType.ALL_TYPES_FROM_OPPOSING_SIDE) {
              Random r = new Random();
              for (int i = 0; i < dtj.getNumEnemiesToDefeat(); i++) {
                int rand = r.nextInt(UserType.values().length / 2);
                if (MiscMethods.checkIfGoodSide(user.getType())) {
                  switch (rand) {
                  case 0:
                    numToGenerate.put(UserType.BAD_ARCHER, 
                        1 + ((numToGenerate.containsKey(UserType.BAD_ARCHER)) ? numToGenerate.get(UserType.BAD_ARCHER): 0));
                    break;
                  case 1:
                    numToGenerate.put(UserType.BAD_MAGE, 
                        1 + ((numToGenerate.containsKey(UserType.BAD_MAGE)) ? numToGenerate.get(UserType.BAD_MAGE): 0));
                    break;
                  case 2:
                    numToGenerate.put(UserType.BAD_WARRIOR, 
                        1 + ((numToGenerate.containsKey(UserType.BAD_WARRIOR)) ? numToGenerate.get(UserType.BAD_WARRIOR): 0));
                    break;
                  default:
                    log.error("invalid random int chosen for generating enemies, chose " + rand);
                  }
                } else {
                  switch (rand) {
                  case 0:
                    numToGenerate.put(UserType.GOOD_ARCHER, 
                        1 + ((numToGenerate.containsKey(UserType.GOOD_ARCHER)) ? numToGenerate.get(UserType.GOOD_ARCHER): 0));
                    break;
                  case 1:
                    numToGenerate.put(UserType.GOOD_MAGE, 
                        1 + ((numToGenerate.containsKey(UserType.GOOD_MAGE)) ? numToGenerate.get(UserType.GOOD_MAGE): 0));
                    break;
                  case 2:
                    numToGenerate.put(UserType.GOOD_WARRIOR, 
                        1 + ((numToGenerate.containsKey(UserType.GOOD_WARRIOR)) ? numToGenerate.get(UserType.GOOD_WARRIOR): 0));
                    break;
                  default:
                    log.error("invalid random int chosen for generating enemies, chose " + rand);
                  }
                }
              }
            } else {
              UserType userType = MiscMethods.getUserTypeFromDefeatTypeJobUserType(dtj.getEnemyType());
              if (userType != null) {
                numToGenerate.put(userType, dtj.getNumEnemiesToDefeat()
                  + ((numToGenerate.containsKey(userType)) ? numToGenerate.get(userType): 0));
              }
            }
          }
        }
      }
    }
    for (UserType type : numToGenerate.keySet()) {
      List<UserType> temp = new ArrayList<UserType>();
      temp.add(type);
      boolean realPlayersOnly = false;
      boolean fakePlayersOnly = true;
      boolean offlinePlayersOnly = false; //does not include fake players
      boolean prestigePlayersOnly = false;
      boolean inactiveShield = true;
      List<User> users = RetrieveUtils.userRetrieveUtils().getUsers(temp,
          numToGenerate.get(type), user.getLevel(), user.getId(), true, null,
          null, null, null, true, realPlayersOnly, fakePlayersOnly,
          offlinePlayersOnly, prestigePlayersOnly, inactiveShield, null);
      if (users != null) {
        for (User u : users) {
          resBuilder.addDefeatTypeJobEnemies(CreateInfoProtoUtils.createFullUserProtoFromUser(u));
        }
      }
    }
  }*/

//  private void setResponseUserTaskInfos(Builder resBuilder, List<Task> tasks, int userId, UserType userType) {
//    Map<Integer, Integer> taskIdToNumTimesActedInRankForUser = UserTaskRetrieveUtils.getTaskIdToNumTimesActedInRankForUser(userId);
//    for (Task t : tasks) {
//      int numTimesUserActed = (taskIdToNumTimesActedInRankForUser.containsKey(t.getId())) ? taskIdToNumTimesActedInRankForUser.get(t.getId()) : 0;
//      resBuilder.addUserTasksInfo(CreateInfoProtoUtils.createMinimumUserTaskProto(userType, userId, t.getId(), numTimesUserActed));
//    }
//  }

  //for each of this city's bosses send the corresponding user_bosses
//  private void setResponseUserBossInfos(Builder resBuilder, List<Integer> bossIds, int userId) {
//    boolean livingBossesOnly = false;
//    List<UserBoss> userBosses = UserBossRetrieveUtils
//        .getUserBossesForUserId(userId, livingBossesOnly);
//    for (UserBoss b : userBosses) {
//      if (bossIds.contains(b.getBossId())) {
//        resBuilder.addUserBosses(CreateInfoProtoUtils.createFullUserBossProtoFromUserBoss(b));
//      }
//    }
//  }

  private boolean checkLegitCityLoad(Builder resBuilder, User user, City city) {//, int currentCityRankForUser) {
    if (city == null || user == null) {
      resBuilder.setStatus(LoadCityStatus.OTHER_FAIL);
      log.error("city or user is null. city=" + city + ", user=" + user);
      return false;
    }
//    if (currentCityRankForUser < 1) {
//      resBuilder.setStatus(LoadCityStatus.NOT_ACCESSIBLE_TO_USER);
//      log.error("city " + city + "is not unlocked for user");
//      return false;
//    }
    resBuilder.setStatus(LoadCityStatus.SUCCESS);
    return true;
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }
  
}
