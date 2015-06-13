//package com.lvl6.server.controller;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import redis.clients.jedis.Tuple;
//
//import com.lvl6.events.RequestEvent;
//import com.lvl6.events.request.RetrieveTournamentRankingsRequestEvent;
//import com.lvl6.info.User;
//import com.lvl6.leaderboards.LeaderBoardUtilImpl;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsRequestProto;
//import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsResponseProto;
//import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsResponseProto.Builder;
//import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsResponseProto.RetrieveTournamentStatus;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.server.Locker;
//
//@Component
//
//public class RetrieveTournamentRankingsController extends EventController {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() {
//  }.getClass().getEnclosingClass());
//
//  @Autowired
//  public LeaderBoardUtilImpl leader;
//
//  @Autowired
//  protected Locker locker;
//
//  public RetrieveTournamentRankingsController() {
//    
//  }
//
//  @Override
//  public RequestEvent createRequestEvent() {
//    return new RetrieveTournamentRankingsRequestEvent();
//  }
//
//  @Override
//  public EventProtocolRequest getEventType() {
//    return EventProtocolRequest.C_RETRIEVE_TOURNAMENT_RANKINGS_EVENT;
//  }
//
//  @Override
//  public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
//    RetrieveTournamentRankingsRequestProto reqProto = ((RetrieveTournamentRankingsRequestEvent) event)
//        .getRetrieveTournamentRankingsRequestProto();
//
//    MinimumUserProto senderProto = reqProto.getSender();
//
//    int eventId = reqProto.getEventId();
//    int afterThisRank = reqProto.getAfterThisRank();
//
//    RetrieveTournamentRankingsResponseProto.Builder resBuilder = RetrieveTournamentRankingsResponseProto
//        .newBuilder();
//    resBuilder.setSender(senderProto);
//    resBuilder.setEventId(eventId);
//    resBuilder.setAfterThisRank(afterThisRank);
//    resBuilder.setStatus(RetrieveTournamentStatus.OTHER_FAIL);
//
////    getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
//    try {
////      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());
////      int userId = user.getId();
////      boolean legitRetrieval = checkLegitRetrieval(resBuilder, user,	eventId);
////      Map<Integer, UserRankScore> lurs = null;
////      if (legitRetrieval) {
////        int rank = (int) leader.getRankForEventAndUser(eventId, userId);
////        double score = leader.getScoreForEventAndUser(eventId, userId);
////
////        resBuilder.setRetriever(CreateInfoProtoUtils.createMinimumUserProtoWithLevelForTournament(
////            user,  rank, score));
////
////        //TODO: FIX THIS IMPLEMENTATION
////        lurs = getUsersAfterThisRank(eventId, afterThisRank);
////
////        if (lurs != null) {
////          List<User> resultUsers = new ArrayList<User>(RetrieveUtils.userRetrieveUtils().getUsersByIds(new ArrayList<Integer>(lurs.keySet())).values());
////          log.debug("Populating leaderboard results for event: "+eventId+" after this rank: "+afterThisRank+" found results: "+resultUsers.size());
////          for (User u : resultUsers) {
////            UserRankScore urs = lurs.get(u.getId());
////            resBuilder.addResultPlayers(CreateInfoProtoUtils.createMinimumUserProtoWithLevelForTournament(u, urs.rank, urs.score));
////            //null PvpLeagueFromUser means will pull from hazelcast instead
////            resBuilder.addFullUsers(CreateInfoProtoUtils.createFullUserProtoFromUser(u, null));
////          }
////        }
////      }
//
//      RetrieveTournamentRankingsResponseProto resProto = resBuilder.build();
//      RetrieveTournamentRankingsResponseEvent resEvent = new RetrieveTournamentRankingsResponseEvent(senderProto.getUserUuid());
//      resEvent.setTag(event.getTag());
//      resEvent.setResponseProto(resProto);
//
//      responses.normalResponseEvents().add(resEvent);
//    } catch (Exception e) {
//      log.error(
//          "exception in RetrieveTournamentController processEvent",
//          e);
////    } finally {
////      getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
//    }
//
//  }
//
//  private Map<Integer, UserRankScore> getUsersAfterThisRank(int eventId,	int afterThisRank) {
//    Set<Tuple> usrs = new HashSet<Tuple>();
//
//    usrs = leader.getEventTopN(eventId, afterThisRank, afterThisRank+ControllerConstants.TOURNAMENT_EVENT__MAX_PLAYERS_SENT_AT_ONCE);
//
//    Map<Integer, UserRankScore> lurs = new LinkedHashMap<Integer, UserRankScore>();
//    Iterator<Tuple> it = usrs.iterator();
//    int counter = 1;
//    while(it.hasNext()) {
//      Tuple t = it.next();
//      Integer userId = Integer.valueOf(t.getElement());
//      UserRankScore urs = new UserRankScore(userId, t.getScore(), counter+afterThisRank);
//      lurs.put(userId, urs);
//      log.debug(urs.toString());
//      counter++;
//    }
//    return lurs;
//  }
//
//  private boolean checkLegitRetrieval(Builder resBuilder, User user,
//      int eventId) {
//    if (user == null || 0 >= eventId) {
//      resBuilder.setStatus(RetrieveTournamentStatus.OTHER_FAIL);
//      log.error("user is " + user + ", event id="
//          + eventId);
//      return false;
//    }
//    resBuilder.setStatus(RetrieveTournamentStatus.SUCCESS);
//    return true;
//  }
//
//  public class UserRankScore{
//    public UserRankScore(Integer userId, Double score, Integer rank) {
//      super();
//      this.userId = userId;
//      this.score = score;
//      this.rank = rank;
//    }
//    Integer userId;
//    Double score;
//    Integer rank;
//    @Override
//    public String toString() {
//      return "UserRankScore [userId=" + userId + ", rank=" + rank + ", score=" + score + "]";
//    }
//
//  }
//
//  public Locker getLocker() {
//	  return locker;
//  }
//
//  public void setLocker(Locker locker) {
//	  this.locker = locker;
//  }
//
//}
