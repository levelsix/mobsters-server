package com.lvl6.server.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.GiveClanHelpRequestEvent;
import com.lvl6.events.response.GiveClanHelpResponseEvent;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanHelpProto;
import com.lvl6.proto.EventClanProto.GiveClanHelpRequestProto;
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto;
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto.Builder;
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto.GiveClanHelpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class GiveClanHelpController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;
  
  @Autowired
  protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;
   

  public GiveClanHelpController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new GiveClanHelpRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_GIVE_CLAN_HELP_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    GiveClanHelpRequestProto reqProto = ((GiveClanHelpRequestEvent)event)
    	.getGiveClanHelpRequestProto();
    
    log.info(String.format("reqProto=%s", reqProto));

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserUuid();
    int clanId = 0;
    
    if (null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    
    //NOTE: For all these ids, append userId to helperIds property
    List<Long> clanHelpIds = reqProto.getClanHelpIdsList();

    GiveClanHelpResponseProto.Builder resBuilder = GiveClanHelpResponseProto.newBuilder();
    resBuilder.setStatus(GiveClanHelpStatus.FAIL_OTHER);
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
    	server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
    }*/
    try {
//      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      
      boolean legitLeave = checkLegitLeave(resBuilder, userId, clanId);
      
      boolean success = false;
      if (legitLeave) {
      	success = writeChangesToDB(userId, clanId, clanHelpIds);
      }

      GiveClanHelpResponseEvent resEvent = new GiveClanHelpResponseEvent(userId);
      resEvent.setTag(event.getTag());
      if (!success) {
    	  resEvent.setGiveClanHelpResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	  
      } else {
    	  //only write to clan if success
    	  //send back most up to date ClanHelps that changed
    	  //NOTE: Sending most up to date ClanHelps incurs a db read
    	  setClanHelpings(resBuilder, null, senderProto, clanHelpIds);
    	  resBuilder.setStatus(GiveClanHelpStatus.SUCCESS);
    	  resEvent.setGiveClanHelpResponseProto(resBuilder.build());
    	  server.writeClanEvent(resEvent, clanId);
      }
      
    } catch (Exception e) {
      log.error("exception in GiveClanHelp processEvent", e);
      try {
    	  resBuilder.setStatus(GiveClanHelpStatus.FAIL_OTHER);
    	  GiveClanHelpResponseEvent resEvent = new GiveClanHelpResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setGiveClanHelpResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in GiveClanHelp processEvent", e);
    	}
    } /*finally {
    	if (0 != clanId && lockedClan) {
    		getLocker().unlockClan(clanId);
    	} else {
    		server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
    	}
    }*/
  }

  private boolean checkLegitLeave(Builder resBuilder, int userId, int clanId) {

    if (userId <= 0 || clanId <= 0) {
      log.error("invalid: userId=%s, clanId=%s", userId, clanId );
      return false;
    }

    return true;
  }

  private boolean writeChangesToDB(int userId, int clanId,
	  List<Long> clanHelpIds)
  {
    int numUpdated = UpdateUtils.get().updateClanHelp(userId, clanId,
    	clanHelpIds);
    
    log.info(String.format(
    	"numUpdated=%s", numUpdated));
    
    User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
    boolean updated = user.updateClanHelps(clanHelpIds.size());
    log.info(String.format( "updated=%s", updated ));
    
    
    return true;
  }
  
  private void setClanHelpings(Builder resBuilder, User solicitor,
	  MinimumUserProto mup, List<Long> clanHelpIds)
  {
	  List<ClanHelp> modifiedSolicitations = clanHelpRetrieveUtil
		  .getClanHelpsForIds( clanHelpIds );
	  
	  for (ClanHelp aid : modifiedSolicitations) {
		  //only need the name
		  MinimumUserProto mup2 = MinimumUserProto.newBuilder().setUserId(aid.getUserId()).build();

		  //only need name not clan
		  ClanHelpProto chp = CreateInfoProtoUtils
			  .createClanHelpProtoFromClanHelp(aid, solicitor, null, mup2);

		  resBuilder.addClanHelps(chp);
	  }
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

  public ClanHelpRetrieveUtil getClanHelpRetrieveUtil()
  {
	  return clanHelpRetrieveUtil;
  }

  public void setClanHelpRetrieveUtil( ClanHelpRetrieveUtil clanHelpRetrieveUtil )
  {
	  this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
  }
  
}
