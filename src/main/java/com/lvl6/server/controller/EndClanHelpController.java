package com.lvl6.server.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EndClanHelpRequestEvent;
import com.lvl6.events.response.EndClanHelpResponseEvent;
import com.lvl6.proto.EventClanProto.EndClanHelpRequestProto;
import com.lvl6.proto.EventClanProto.EndClanHelpResponseProto;
import com.lvl6.proto.EventClanProto.EndClanHelpResponseProto.Builder;
import com.lvl6.proto.EventClanProto.EndClanHelpResponseProto.EndClanHelpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component @DependsOn("gameServer") public class EndClanHelpController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;

  public EndClanHelpController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new EndClanHelpRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_END_CLAN_HELP_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    EndClanHelpRequestProto reqProto = ((EndClanHelpRequestEvent)event)
    	.getEndClanHelpRequestProto();

    log.info(String.format("reqProto=%s", reqProto));
    
    MinimumUserProto senderProto = reqProto.getSender();
    List<Long> clanHelpIdList = reqProto.getClanHelpIdsList();
    int userId = senderProto.getUserId();

    EndClanHelpResponseProto.Builder resBuilder = EndClanHelpResponseProto.newBuilder();
    resBuilder.setStatus(EndClanHelpStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);
    resBuilder.addAllClanHelpIds(clanHelpIdList);

    int clanId = 0;

    if (null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }

    try {
      boolean legitLeave = checkLegitLeave(resBuilder, userId, clanHelpIdList);
      
      boolean success = false;
      if (legitLeave) {
      	success = writeChangesToDB(userId, clanId, clanHelpIdList);
      }

      EndClanHelpResponseEvent resEvent = new EndClanHelpResponseEvent(userId);
      resEvent.setTag(event.getTag());
      //only write to user if failed
      if (!success) {
      	resEvent.setEndClanHelpResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      	
      } else {
    	  //only write to clan if success

    	  resBuilder.setStatus(EndClanHelpStatus.SUCCESS);
    	  resEvent.setEndClanHelpResponseProto(resBuilder.build());
    	  server.writeClanEvent(resEvent, clanId);
    	  //this works for other clan members, but not for the person 
    	  //who left (they see the message when they join a clan, reenter clan house
    	  //notifyClan(user, clan);
      }
    } catch (Exception e) {
      log.error("exception in EndClanHelp processEvent", e);
      try {
    	  resBuilder.setStatus(EndClanHelpStatus.FAIL_OTHER);
    	  EndClanHelpResponseEvent resEvent = new EndClanHelpResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setEndClanHelpResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in EndClanHelp processEvent", e);
    	}
    } /*finally {
    	if (0 != clanId && lockedClan) {
    		getLocker().unlockClan(clanId);
    	} else {
    		server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    	}
    }*/
  }

  private boolean checkLegitLeave(Builder resBuilder, int userId,
	  List<Long> clanHelpIds) {

    if (userId <= 0) {
      log.error(String.format("user is null. id=%s", userId));
      return false;      
    }
    
    if (null == clanHelpIds || clanHelpIds.isEmpty()) {
    	log.error("no clan help ids sent");
    	return false;
    }
    
    return true;
  }

  private boolean writeChangesToDB(int userId, int clanId,
	  List<Long> clanHelpIdList)
  {
	  int numDeleted = DeleteUtils.get().deleteClanHelp(userId, clanHelpIdList);
	  log.info(String.format("numDeleted: %s", numDeleted));

	  return true;
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