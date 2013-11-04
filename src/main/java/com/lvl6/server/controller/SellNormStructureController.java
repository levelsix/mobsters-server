package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SellNormStructureRequestEvent;
import com.lvl6.events.response.SellNormStructureResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.SellNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.SellNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.SellNormStructureResponseProto.SellNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;

 @Component @DependsOn("gameServer") public class SellNormStructureController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public SellNormStructureController() {
    numAllocatedThreads = 3;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new SellNormStructureRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_SELL_NORM_STRUCTURE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    SellNormStructureRequestProto reqProto = ((SellNormStructureRequestEvent)event).getSellNormStructureRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userStructId = reqProto.getUserStructId();

    SellNormStructureResponseProto.Builder resBuilder = SellNormStructureResponseProto.newBuilder();
    resBuilder.setSender(senderProto);

    StructureForUser userStruct = RetrieveUtils.userStructRetrieveUtils().getSpecificUserStruct(userStructId);
    Structure struct = null;

    if (userStruct != null) {
      struct = StructureRetrieveUtils.getStructForStructId(userStruct.getStructId());
    }

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

    try {
      User user = null;
      if (userStruct != null) {
        user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
        int previousSilver = 0;
        int previousGold = 0;
        if (user != null && struct != null && user.getId() == userStruct.getUserId()) {
          previousSilver = user.getCash();
          previousGold = user.getGems();
          
          int diamondChange = Math.max(0,  (int)Math.ceil(struct.getGemPrice()*ControllerConstants.SELL_NORM_STRUCTURE__PERCENT_RETURNED_TO_USER));
          int coinChange = Math.max(0,  (int)Math.ceil(struct.getCashPrice()*ControllerConstants.SELL_NORM_STRUCTURE__PERCENT_RETURNED_TO_USER));
          
          if (!user.updateRelativeDiamondsCoinsExperienceNaive(diamondChange, coinChange, 0)) {
            resBuilder.setStatus(SellNormStructureStatus.FAIL);
            log.error("problem with giving user " + diamondChange + " diamonds and " + coinChange + " coins");
          } else {
            if (!DeleteUtils.get().deleteUserStruct(userStructId)) {
              resBuilder.setStatus(SellNormStructureStatus.FAIL);
              log.error("problem with deleting user struct with user struct id " + userStructId);
            } else {
              resBuilder.setStatus(SellNormStructureStatus.SUCCESS);                      
            }
            writeToUserCurrencyHistory(user, userStruct, diamondChange, coinChange, previousSilver, previousGold);
          }
        } else {
          resBuilder.setStatus(SellNormStructureStatus.FAIL);
          log.error("parameter null, struct doesn't belong to user, or struct is not complete. user="
              + user + ", struct=" + struct + ", userStruct=" + userStruct);
        }
      } else {
        resBuilder.setStatus(SellNormStructureStatus.FAIL);       
        log.error("no user struct with id " + userStructId);
      }

      SellNormStructureResponseEvent resEvent = new SellNormStructureResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setSellNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

      if (user != null) {
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
      }

    } catch (Exception e) {
      log.error("exception in SellNormStructure processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }
  
  public void writeToUserCurrencyHistory(User aUser, StructureForUser userStruct, int diamondChange, 
      int coinChange, int previousSilver, int previousGold) {
//    int userStructId = userStruct.getId();
//    int structId = userStruct.getStructId();
//    int prevLevel = userStruct.getLevel();
//    String structDetails = "uStructId:" + userStructId + " structId:" + structId
//        + " prevLevel:" + prevLevel;
//    
//    Timestamp date = new Timestamp((new Date()).getTime());
//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String gems = MiscMethods.gems;
//    String cash = MiscMethods.cash;
//    String reasonForChange = ControllerConstants.UCHRFC__SELL_NORM_STRUCT + " "
//        + structDetails;
//
//    Map<String, Integer> money = new HashMap<String, Integer>();
//    if (0 != diamondChange) {
//      money.put(gems, diamondChange);
//      previousGoldSilver.put(gems, previousGold);
//      reasonsForChanges.put(gems, reasonForChange);
//    }
//    if (0 != coinChange) {
//      money.put(cash, coinChange);
//      previousGoldSilver.put(cash, previousSilver);
//      reasonsForChanges.put(cash, reasonForChange);
//    }
//    
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, money,
//        previousGoldSilver, reasonsForChanges);
//    
  }
}
