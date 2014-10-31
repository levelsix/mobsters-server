package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.FullClanProtoWithClanSize;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto.AcceptOrRejectClanInviteStatus;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto.Builder;
import com.lvl6.retrieveutils.ClanInviteRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.UserClanRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

public class AcceptOrRejectClanInviteAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private int acceptedInviteId;
	private int prospectiveMemberId;
	private int inviterId;
	private int clanId;
	private Date clientTime;
	private List<Integer> rejectedInviteIds;
	private UserClanRetrieveUtils userClanRetrieveUtils;
	private InsertUtil insertUtil;
	private DeleteUtil deleteUtil;
	private ClanInviteRetrieveUtil clanInviteRetrieveUtil;
	
	public AcceptOrRejectClanInviteAction(
		int acceptedInviteId,
		int prospectiveMemberId,
		int inviterId,
		int clanId,
		Date clientTime,
		List<Integer> rejectedInviteIds,
		UserClanRetrieveUtils userClanRetrieveUtils,
		InsertUtil insertUtil,
		DeleteUtil deleteUtil,
		ClanInviteRetrieveUtil clanInviteRetrieveUtil )
	{
		super();
		this.acceptedInviteId = acceptedInviteId;
		this.prospectiveMemberId = prospectiveMemberId;
		this.inviterId = inviterId;
		this.clanId = clanId;
		this.clientTime = clientTime;
		this.rejectedInviteIds = rejectedInviteIds;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
		this.clanInviteRetrieveUtil = clanInviteRetrieveUtil;
	}
	
//	//encapsulates the return value from this Action Object
//	static class AcceptOrRejectClanInviteResource {
//		
//		
//		public AcceptOrRejectClanInviteResource() {
//			
//		}
//	}
//
//	public AcceptOrRejectClanInviteResource execute() {
//		
//	}
	
	//derived state
	boolean rejectOnly;
	private User prospectiveMember;
	private Clan prospectiveClan;
	private int clanSize;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(AcceptOrRejectClanInviteStatus.FAIL_OTHER);
		
		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);
		
		if (!valid) {
			return;
		}
		
		valid = verifySemantics(resBuilder);
		
		if (!valid) {
			return;
		}
		
		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}
		
		resBuilder.setStatus(AcceptOrRejectClanInviteStatus.SUCCESS);
		if (!rejectOnly) {
			//meaning user accepted an invite
			FullClanProtoWithClanSize fcpwcs = CreateInfoProtoUtils
				.createFullClanProtoWithClanSize(prospectiveClan, clanSize);

			resBuilder.setFullClan( fcpwcs );
		}
		
	}
	
	private boolean verifySyntax(Builder resBuilder) {
		
		if (acceptedInviteId <= 0  && rejectedInviteIds.isEmpty()) {
			log.error(String.format(
				"invalid inviterId=%s or clanId=%s", inviterId, clanId));
			return false;
		}
		
		return true;
	}
	
	private boolean verifySemantics(Builder resBuilder) {
		rejectOnly = false;
		if (acceptedInviteId <= 0 && rejectedInviteIds.isEmpty()) {
			log.error("invalid request: did not accept or reject invites.");
			return false;
		}
		//user is just rejecting invites.
		if (acceptedInviteId <= 0 && !rejectedInviteIds.isEmpty()) {
			rejectOnly = true;
			log.info("user is just rejecting invites.");
			
			
			return true;
		}
		
		log.info("user is accepting an invite");
		
		prospectiveMember = RetrieveUtils.userRetrieveUtils().getUserById(inviterId);
		if (null == prospectiveMember) {
			log.error(String.format(
				"No user. invalid prospectiveMemberId=%s", prospectiveMemberId));
			return false;
		}
		
		//check if user already in a clan
		if (prospectiveMember.getClanId() > 0) {
			log.error(String.format(
				"prospectiveMember already in clan. %s", prospectiveMember));
			return false;
		}
		
		//check if clan full
		prospectiveClan = ClanRetrieveUtils.getClanWithId(clanId);
		if (null == prospectiveClan) {
			log.error(String.format(
				"No clan. invalid clanId=%s", clanId));
			return false;
		}
		
		//check out the size of the clan
	    List<Integer> clanIdList = Collections.singletonList(clanId);
		List<String> statuses = new ArrayList<String>();
	    statuses.add(UserClanStatus.LEADER.name());
	    statuses.add(UserClanStatus.JUNIOR_LEADER.name());
	    statuses.add(UserClanStatus.CAPTAIN.name());
	  	statuses.add(UserClanStatus.MEMBER.name());
	  	Map<Integer, Integer> clanIdToSize = userClanRetrieveUtils
  			.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);
		
	  	clanSize = clanIdToSize.get(clanId);
	  	
	  	if (clanSize >= ControllerConstants.CLAN__MAX_NUM_MEMBERS) {
	  		log.error(String.format(
	  			"clan is full. %s, size=%s",
	  			prospectiveClan, clanSize));
	  		resBuilder.setStatus(AcceptOrRejectClanInviteStatus.FAIL_CLAN_IS_FULL);
	  		return false;
	  	}
	  	
	  	//still room in clan
		return true;
	}
	
	private boolean writeChangesToDB(Builder resBuilder) {
		
		if (rejectOnly) {
			//deleting invites only
			int numDeleted = deleteUtil
				.deleteClanInvite(prospectiveMemberId, rejectedInviteIds);
			log.info(String.format("deleting invites. numDeleted=%s", numDeleted));
			
		} else {
			
			if (!prospectiveMember.updateRelativeCoinsAbsoluteClan(0, clanId)) {
				log.error(String.format(
					"problem updating user to new clanId. user=%s, clanId=%s",
					prospectiveMember, clanId));
				return false;
			}
			
			//delete all invites for this person
			int numDeleted = deleteUtil
				.deleteClanInvite(prospectiveMemberId, null);
			log.info(String.format("deleting invites. numDeleted=%s", numDeleted));
			
		
			//make person into a clan member
			String userClanStatus = UserClanStatus.MEMBER.name();
			if (!insertUtil.insertUserClan(prospectiveMemberId, clanId, userClanStatus, new Timestamp(clientTime.getTime()))) {
				log.error(String.format(
					"problem inserting user clan data. user=%s, clanId=%s",
					prospectiveMember, clanId));
				return false;
			} 

			
		}
		
		return true;
	}

	public Clan getProspectiveClan()
	{
		return prospectiveClan;
	}

	public User getProspectiveMember()
	{
		return prospectiveMember;
	}

}
