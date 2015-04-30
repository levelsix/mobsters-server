//package com.lvl6.server.controller;
//
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.events.RequestEvent;
//import com.lvl6.events.request.InviteToClanRequestEvent;
//import com.lvl6.events.response.InviteToClanResponseEvent;
//import com.lvl6.proto.EventClanProto.InviteToClanRequestProto;
//import com.lvl6.proto.EventClanProto.InviteToClanResponseProto;
//import com.lvl6.proto.EventClanProto.InviteToClanResponseProto.InviteToClanStatus;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.ClanInviteRetrieveUtil;
//import com.lvl6.server.Locker;
//import com.lvl6.server.controller.actionobjects.InviteToClanAction;
//import com.lvl6.utils.RetrieveUtils;
//import com.lvl6.utils.utilmethods.InsertUtils;
//
//@Component  public class InviteToClanController extends EventController {
//
//	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//	@Autowired
//	protected Locker locker;
//
//	@Autowired
//	protected ClanInviteRetrieveUtil clanInviteRetrieveUtil;
//	
//	public InviteToClanController() {
//		
//	}
//
//	@Override
//	public RequestEvent createRequestEvent() {
//		return new InviteToClanRequestEvent();
//	}
//
//	@Override
//	public EventProtocolRequest getEventType() {
//		return EventProtocolRequest.C_INVITE_TO_CLAN_EVENT;
//	}
//
//	@Override
//	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
//		InviteToClanRequestProto reqProto = ((InviteToClanRequestEvent)event).getInviteToClanRequestProto();
//		log.info(String.format("reqProto=%s", reqProto));
//
//		MinimumUserProto senderProto = reqProto.getSender();
//		int inviterId = senderProto.getUserUuid();
//		int prospectiveMember = reqProto.getProspectiveMember();
//		Date inviteTime = new Date(reqProto.getClientTime());
//
//		InviteToClanResponseProto.Builder resBuilder = InviteToClanResponseProto.newBuilder();
//		resBuilder.setStatus(InviteToClanStatus.SUCCESS);
//		resBuilder.setSender(senderProto);
//
//		int clanId = 0;
//		if (senderProto.hasClan() && null != senderProto.getClan()) {
//			clanId = senderProto.getClan().getClanId();
//		}
//
//		//    //maybe should get clan lock instead of locking person
//		//    //but going to modify user, so lock user. however maybe locking is not necessary
//		//    boolean lockedClan = false;
//		//    if (0 != clanId) {
//		//    	lockedClan = getLocker().lockClan(clanId);
//		//    }/* else {
//		//    	locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
//		//    }*/
//		try {
//
//			InviteToClanAction itca = new InviteToClanAction(inviterId,
//				prospectiveMember, clanId, inviteTime,
//				RetrieveUtils.userClanRetrieveUtils(), InsertUtils.get(),
//				clanInviteRetrieveUtil);
//			itca.execute(resBuilder);
//			
//			InviteToClanResponseEvent resEvent = new InviteToClanResponseEvent(inviterId);
//			resEvent.setTag(event.getTag());
//			resEvent.setInviteToClanResponseProto(resBuilder.build());
//			
//			//only write to user if failed
//			if (resBuilder.getStatus().equals(InviteToClanStatus.FAIL_OTHER)) {
//				responses.normalResponseEvents().add(resEvent);
//
//			} else {
//				responses.clanResponseEvents().add(new ClanResponseEvent(resEvent, clanId, false));
//			}
//		} catch (Exception e) {
//			log.error("exception in InviteToClan processEvent", e);
//			try {
//				resBuilder.setStatus(InviteToClanStatus.FAIL_OTHER);
//				InviteToClanResponseEvent resEvent = new InviteToClanResponseEvent(inviterId);
//				resEvent.setTag(event.getTag());
//				resEvent.setInviteToClanResponseProto(resBuilder.build());
//				responses.normalResponseEvents().add(resEvent);
//			} catch (Exception e2) {
//				log.error("exception2 in InviteToClan processEvent", e);
//			}
//			//    } finally {
//			//    	if (0 != clanId && lockedClan) {
//			//    		getLocker().unlockClan(clanId);
//			//    	}/* else {
//			//    		locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
//			//    	}*/
//		}
//	}
//
//	public Locker getLocker()
//	{
//		return locker;
//	}
//
//	public void setLocker( Locker locker )
//	{
//		this.locker = locker;
//	}
//
//	public ClanInviteRetrieveUtil getClanInviteRetrieveUtil()
//	{
//		return clanInviteRetrieveUtil;
//	}
//
//	public void setClanInviteRetrieveUtil( ClanInviteRetrieveUtil clanInviteRetrieveUtil )
//	{
//		this.clanInviteRetrieveUtil = clanInviteRetrieveUtil;
//	}
//
//}
