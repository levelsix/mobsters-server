package com.lvl6.server.controller;

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
import com.lvl6.info.User;
import com.lvl6.proto.EventCityProto.LoadCityRequestProto;
import com.lvl6.proto.EventCityProto.LoadCityResponseProto;
import com.lvl6.proto.EventCityProto.LoadCityResponseProto.Builder;
import com.lvl6.proto.EventCityProto.LoadCityResponseProto.LoadCityStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.CityElementsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityRetrieveUtils;
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

      boolean legitCityLoad = checkLegitCityLoad(resBuilder, user, city);//, currentCityRankForUser);

      if (legitCityLoad) {
        List<CityElement> neutralCityElements = CityElementsRetrieveUtils.getCityElementsForCity(cityId);
        if (neutralCityElements != null) {
          for (CityElement nce : neutralCityElements) {
            resBuilder.addCityElements(CreateInfoProtoUtils.createCityElementProtoFromCityElement(nce));
          }
        }

//        List<Monster> bosses = MonsterRetrieveUtils.getBossesForCityId(cityId);
//        if (bosses != null && bosses.size() > 0) {
//          List<Integer> bossIds = new ArrayList<Integer>();
//          for (Monster b : bosses) {
//            bossIds.add(b.getId());
//          }
//          setResponseUserBossInfos(resBuilder, bossIds, user.getId());
//        }

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
