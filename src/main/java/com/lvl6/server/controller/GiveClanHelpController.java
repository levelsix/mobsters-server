package com.lvl6.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    int userId = senderProto.getUserId();
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
    	server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
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
    	  //send back most up to date ClanHelps
    	  //NOTE: Sending most up to date ClanHelps incurs a db read
    	  setClanHelpings(resBuilder, clanId, userId);
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
    		server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
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
    
    return true;
  }
  
  //Copy pasted from StartupController
  private void setClanHelpings(Builder resBuilder, int clanId, int userId) {
	  Map<Integer, List<ClanHelp>> allSolicitations = clanHelpRetrieveUtil
		  .getUserIdToClanHelp( clanId, userId );
	  
	  Map<Integer, User> solicitors = RetrieveUtils.userRetrieveUtils()
		  .getUsersByIds(allSolicitations.keySet());                                
	  
	  //convert all solicitors into MinimumUserProtos
	  Map<Integer, MinimumUserProto> mupSolicitors = new HashMap<Integer, MinimumUserProto>();
	  for (Integer solicitorId : solicitors.keySet()) {
		  User moocher = solicitors.get(solicitorId);
		  MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(moocher);
		  mupSolicitors.put(solicitorId, mup);
	  }
	  
	  
	  for (Integer solicitorId : allSolicitations.keySet()) {
		  List<ClanHelp> solicitations = allSolicitations.get(solicitorId);
			  
		  User solicitor = solicitors.get( solicitorId );
		  MinimumUserProto mup = mupSolicitors.get( solicitorId );
		  
		  for (ClanHelp aid : solicitations) {
			  
			  ClanHelpProto chp = CreateInfoProtoUtils
				  .createClanHelpProtoFromClanHelp(aid, solicitor, mup);
			  
			  resBuilder.addClanHelps(chp);
		  }
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
