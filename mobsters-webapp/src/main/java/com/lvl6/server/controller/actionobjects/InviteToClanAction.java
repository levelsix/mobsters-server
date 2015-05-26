//package com.lvl6.server.controller.actionobjects;
//
//import java.sql.Timestamp;
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.lvl6.info.Clan;
//import com.lvl6.info.ClanInvite;
//import com.lvl6.info.User;
//import com.lvl6.info.UserClan;
//import com.lvl6.proto.ClanProto.ClanInviteProto;
//import com.lvl6.proto.ClanProto.UserClanStatus;
//import com.lvl6.proto.EventClanProto.InviteToClanResponseProto.Builder;
//import com.lvl6.proto.EventClanProto.InviteToClanResponseProto.InviteToClanStatus;
//import com.lvl6.retrieveutils.ClanInviteRetrieveUtil;
//import com.lvl6.retrieveutils.ClanRetrieveUtils;
//import com.lvl6.retrieveutils.UserClanRetrieveUtils;
//import com.lvl6.server.controller.utils.ClanStuffUtils;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.RetrieveUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//
//public class InviteToClanAction
//{
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//
//	private int inviterId;
//	private int prospectiveMemberId;
//	private int clanId;
//	private Date inviteTime;
//	private UserClanRetrieveUtils userClanRetrieveUtils;
//	private InsertUtil insertUtil;
//	private ClanInviteRetrieveUtil clanInviteRetrieveUtil;
//	
//	public InviteToClanAction(int inviterId, int prospectiveMemberId,
//		int clanId, Date inviteTime,
//		UserClanRetrieveUtils userClanRetrieveUtils, 
//		InsertUtil insertUtil,
//		ClanInviteRetrieveUtil clanInviteRetrieveUtil)
//	{
//		this.inviterId = inviterId;
//		this.prospectiveMemberId = prospectiveMemberId;
//		this.clanId = clanId;
//		this.inviteTime = inviteTime;
//		this.userClanRetrieveUtils = userClanRetrieveUtils;
//		this.insertUtil = insertUtil;
//		this.clanInviteRetrieveUtil = clanInviteRetrieveUtil;
//	}
//	
////	//encapsulates the return value from this Action Object
////	static class InviteToClanResource {
////		
////		
////		public InviteToClanResource() {
////			
////		}
////	}
////
////	public InviteToClanResource execute() {
////		
////	}
//	
//	//derived state
//	private User u;
//	private Clan c;
//	
//	public void execute(Builder resBuilder) {
//		resBuilder.setStatus(InviteToClanStatus.FAIL_OTHER);
//		
//		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//		
//		if (!valid) {
//			return;
//		}
//		
//		valid = verifySemantics(resBuilder);
//		
//		if (!valid) {
//			return;
//		}
//		
//		boolean success = writeChangesToDB(resBuilder);
//		if (success) {
//			resBuilder.setStatus(InviteToClanStatus.SUCCESS);
//		}
//	}
//	
//	private boolean verifySyntax(Builder resBuilder) {
//		
//		if (inviterId < 0  || clanId < 0) {
//			log.error(String.format(
//				"invalid inviterId=%s or clanId=%s", inviterId, clanId));
//			return false;
//		}
//		
//		return true;
//	}
//	
//	private boolean verifySemantics(Builder resBuilder) {
//		u = RetrieveUtils.userRetrieveUtils().getUserById(inviterId);
//		
//		if (null == u) {
//			log.error(String.format(
//				"No user. invalid inviterId=%s", inviterId));
//			return false;
//		}
//		
//		c = ClanRetrieveUtils.getClanWithId(clanId);
//		if (null == c) {
//			log.error(String.format(
//				"No clan. invalid clanId=%s", clanId));
//			return false;
//		}
//		
//		//make sure user is authorized to send invite
//		UserClan uc = userClanRetrieveUtils.getSpecificUserClan(inviterId, clanId);
//		UserClanStatus status = UserClanStatus.MEMBER; 
//		
//		String strStatus = uc.getStatus();
//		try {
//			status = UserClanStatus.valueOf(strStatus);
//		} catch(Exception e) {
//			log.error(
//				String.format(
//					"invalid UserClan: %s, user=%s. default to MEMBER status",
//					uc, u),
//				e);
//		}
//			
//		boolean authorized = ClanStuffUtils.firstUserClanStatusAboveSecond(
//			status, UserClanStatus.CAPTAIN);
//		
//		if (!authorized) {
//			log.error(String.format(
//				"clan member not authorized to send invite: %s, status=%s",
//				u, uc));
//			return false;
//		}
//		
//		return true;
//	}
//	
//	//NOTE: One write one read db call.
//	private boolean writeChangesToDB(Builder resBuilder) {
//		//most common case is insert new clan invite
//		//(user_id, inviter_id) duple is a new pairing
//		//but incase there is an old invite, replace it and then retrieve it
//		Timestamp timeOfInvite = new Timestamp(inviteTime.getTime());
//		
//		int numInserted = insertUtil.insertIntoUpdateClanInvite(
//			prospectiveMemberId, inviterId, clanId, timeOfInvite);
//		log.info(String.format(
//			"num clanInvites inserted:%s", numInserted));
//		
//		//get it from db to get the id
//		ClanInvite ci = clanInviteRetrieveUtil.getClanInvite(
//			prospectiveMemberId, inviterId);
//		if (null != ci) {
//			ClanInviteProto cip = CreateInfoProtoUtils.createClanInviteProto(ci);
//			resBuilder.setInvite(cip);
//			return true;
//		} else {
//			return false;
//		}
//	}
//}
