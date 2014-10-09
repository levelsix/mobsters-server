package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SolicitClanHelpRequestEvent;
import com.lvl6.events.response.SolicitClanHelpResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanHelpNoticeProto;
import com.lvl6.proto.ClanProto.ClanHelpProto;
import com.lvl6.proto.EventClanProto.SolicitClanHelpRequestProto;
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto;
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto.Builder;
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto.SolicitClanHelpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class SolicitClanHelpController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;

  public SolicitClanHelpController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new SolicitClanHelpRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_SOLICIT_CAN_HELP_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    SolicitClanHelpRequestProto reqProto = ((SolicitClanHelpRequestEvent)event)
    	.getSolicitClanHelpRequestProto();

    log.info(String.format("reqProto=%s", reqProto));
    
    MinimumUserProto senderProto = reqProto.getSender();
    ClanHelpNoticeProto chnp = reqProto.getNotice();
    int userId = senderProto.getUserId();
    Timestamp clientDate = new Timestamp(reqProto.getClientTime());
    int maxHelpers = reqProto.getMaxHelpers();

    SolicitClanHelpResponseProto.Builder resBuilder = SolicitClanHelpResponseProto.newBuilder();
    resBuilder.setStatus(SolicitClanHelpStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    /*int clanId = 0;
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    
    //maybe should get clan lock instead of locking person
    //but going to modify user, so lock user. however maybe locking is not necessary
    boolean lockedClan = false;
    if (0 != clanId) {
    	lockedClan = getLocker().lockClan(clanId);
    } else {
    	server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }*/
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      
      boolean legitLeave = checkLegitLeave(resBuilder, user);
      
      boolean success = false;
      List<Long> clanHelpIdStore = new ArrayList<Long>();
      if (legitLeave) {
      	success = writeChangesToDB(userId, user, chnp, clientDate,
      		maxHelpers, clanHelpIdStore);
      }

      SolicitClanHelpResponseEvent resEvent = new SolicitClanHelpResponseEvent(userId);
      resEvent.setTag(event.getTag());
      //only write to user if failed
      if (!success) {
      	resEvent.setSolicitClanHelpResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      	
      } else {
      	//only write to clan if success
    	int clanId = user.getClanId();
    	long clanHelpId = clanHelpIdStore.get(0);
    	ClanHelpProto chp = CreateInfoProtoUtils.createClanHelpProtoFromClanHelp(
    		clanHelpId, clanId, userId, chnp.getUserDataId(), chnp.getHelpType(),
    		maxHelpers, true);
    	resBuilder.setHelpProto(chp);
        resBuilder.setStatus(SolicitClanHelpStatus.SUCCESS);
        resEvent.setSolicitClanHelpResponseProto(resBuilder.build());
      	server.writeClanEvent(resEvent, clanId);
        //this works for other clan members, but not for the person 
        //who left (they see the message when they join a clan, reenter clan house
        //notifyClan(user, clan);
      }
    } catch (Exception e) {
      log.error("exception in SolicitClanHelp processEvent", e);
      try {
    	  resBuilder.setStatus(SolicitClanHelpStatus.FAIL_OTHER);
    	  SolicitClanHelpResponseEvent resEvent = new SolicitClanHelpResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setSolicitClanHelpResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in SolicitClanHelp processEvent", e);
    	}
    } /*finally {
    	if (0 != clanId && lockedClan) {
    		getLocker().unlockClan(clanId);
    	} else {
    		server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}
    }*/
  }

  private boolean checkLegitLeave(Builder resBuilder, User user) {

    if (null == user) {
      log.error("user is null");
      return false;      
    }
    if (user.getClanId() <= 0) {
      resBuilder.setStatus(SolicitClanHelpStatus.FAIL_NOT_IN_CLAN);
      log.error(String.format(
    	  "user's clanId=%s",
    	  user.getClanId()));
      return false;
    }

    Clan clan = ClanRetrieveUtils.getClanWithId(user.getClanId());
    if (null == clan) {
    	resBuilder.setStatus(SolicitClanHelpStatus.FAIL_NOT_IN_CLAN);
        log.error("user not in clan=%s", user.getClanId());
        return false;
    }
    
    return true;
  }

  private boolean writeChangesToDB(int userId, User user,
	  ClanHelpNoticeProto chnp, Timestamp clientDate, int maxHelpers,
	  List<Long> clanHelpIdStore)
  {
    int clanId = user.getId();
    
    long clanHelpId = InsertUtils.get().insertIntoClanHelpGetId(clanId,
    	userId, chnp.getUserDataId(), chnp.getHelpType().name(), clientDate,
    	maxHelpers);
    
    if ( clanHelpId > 0 ) {
    	clanHelpIdStore.add(clanHelpId);
    	return true;
    }
    return false;
  }

  /*
  private void notifyClan(User aUser, Clan aClan) {
    int clanId = aClan.getId();
    
    int level = aUser.getLevel();
    String deserter = aUser.getName();
    Notification aNote = new Notification();
    
    aNote.setAsUserLeftClan(level, deserter);
    MiscMethods.writeClanApnsNotification(aNote, server, clanId);
  }*/
  
  public Locker getLocker() {
	  return locker;
  }
  public void setLocker(Locker locker) {
	  this.locker = locker;
  }
  
}
