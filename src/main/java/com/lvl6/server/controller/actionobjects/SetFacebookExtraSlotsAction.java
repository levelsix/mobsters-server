package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetFacebookExtraSlotsAction implements StartUpAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final String userId;
	private final UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils;

	public SetFacebookExtraSlotsAction(
			StartupResponseProto.Builder resBuilder,
			User user,
			String userId,
			UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils) {
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.userFacebookInviteForSlotRetrieveUtils = userFacebookInviteForSlotRetrieveUtils;
	}

	//derived state
	private Map<String, UserFacebookInviteForSlot> idsToInvitesToMe;
	private Map<String, UserFacebookInviteForSlot> idsToInvitesFromMe;
	private Set<String> recipientFacebookIds;
	private Map<String, UserFacebookInviteForSlot> inviterIdsToInvites;
	private Set<String> inviterUserIds;
	private List<User> recipients;
	private List<User> inviters;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		getAllFbInvites();

		getRecipientFbIds();
		fillMe.addFacebookId(recipientFacebookIds);

		//to make it easier later on, get the inviter ids for these invites and
		//map inviter id to an invite
		//		Map<Integer, UserFacebookInviteForSlot> inviterIdsToInvites =
		//			new HashMap<Integer, UserFacebookInviteForSlot>();
		//inviterIdsToInvites will be populated by getInviterIds(...)
		getInviterIds();
		fillMe.addUserId(inviterUserIds);
		//execute is next();
	}

	@Override
	public void execute(StartUpResource useMe) {
		if (idsToInvitesToMe.isEmpty() && idsToInvitesFromMe.isEmpty()) {
			return;
		}

		if (recipientFacebookIds.isEmpty() && inviterUserIds.isEmpty()) {
			return;
		}

		Map<String, User> userIdsToUsers = useMe.getUserIdsToUsers();
		Map<String, Clan> clanIdsToClans = useMe.getClanIdsToClans();

		separateUsersIntoRecipientsAndInviters(userIdsToUsers);

		//send all the invites where this user is the one being invited
		for (String inviterId : inviterUserIds) {
			UserFacebookInviteForSlot invite = inviterIdsToInvites
					.get(inviterId);
			User inviter = userIdsToUsers.get(inviterId);
			Clan inviterClan = null;
			MinimumUserProtoWithFacebookId inviterProto = null;

			String clanId = inviter.getClanId();
			if (clanIdsToClans.containsKey(clanId)) {
				inviterClan = clanIdsToClans.get(clanId);
			}

			UserFacebookInviteForSlotProto inviteProto = CreateInfoProtoUtils
					.createUserFacebookInviteForSlotProtoFromInvite(invite,
							inviter, inviterClan, inviterProto);

			resBuilder.addInvitesToMeForSlots(inviteProto);
		}

		Clan userClan = null;
		String clanId = user.getClanId();
		if (clanIdsToClans.containsKey(clanId)) {
			userClan = clanIdsToClans.get(clanId);
		}

		//send all the invites where this user is the one inviting
		MinimumUserProtoWithFacebookId thisUserProto = CreateInfoProtoUtils
				.createMinimumUserProtoWithFacebookId(user, userClan);
		for (UserFacebookInviteForSlot invite : idsToInvitesFromMe.values()) {
			UserFacebookInviteForSlotProto inviteProto = CreateInfoProtoUtils
					.createUserFacebookInviteForSlotProtoFromInvite(invite,
							user, userClan, thisUserProto);
			resBuilder.addInvitesFromMeForSlots(inviteProto);
		}

	}

	private void getAllFbInvites() {
		//get the invites where this user is the recipient, get unaccepted, hence, unredeemed invites
		String fbId = user.getFacebookId();
		List<String> specificInviteIds = null;
		boolean filterByAccepted = true;
		boolean isAccepted = false;
		boolean filterByRedeemed = false;
		boolean isRedeemed = false; //doesn't matter

		//base case where user does not have facebook id
		if (null != fbId && !fbId.isEmpty()) {
			idsToInvitesToMe = userFacebookInviteForSlotRetrieveUtils
					.getSpecificOrAllInvitesForRecipient(fbId,
							specificInviteIds, filterByAccepted, isAccepted,
							filterByRedeemed, isRedeemed);
		}

		if (null == idsToInvitesToMe) {
			idsToInvitesToMe = new HashMap<String, UserFacebookInviteForSlot>();
		}

		//get the invites where this user is the inviter: get accepted, unredeemed/redeemed does not matter 
		isAccepted = true;
		idsToInvitesFromMe = userFacebookInviteForSlotRetrieveUtils
				.getSpecificOrAllInvitesForInviter(userId, specificInviteIds,
						filterByAccepted, isAccepted, filterByRedeemed,
						isRedeemed);

		if (null == idsToInvitesFromMe) {
			idsToInvitesFromMe = new HashMap<String, UserFacebookInviteForSlot>();
		}
	}

	private void getRecipientFbIds() {
		recipientFacebookIds = new HashSet<String>();
		for (UserFacebookInviteForSlot invite : idsToInvitesFromMe.values()) {
			recipientFacebookIds.add(invite.getRecipientFacebookId());
		}

	}

	private void getInviterIds() {
		inviterUserIds = new HashSet<String>();
		inviterIdsToInvites = new HashMap<String, UserFacebookInviteForSlot>();
		for (UserFacebookInviteForSlot invite : idsToInvitesToMe.values()) {
			String userId = invite.getInviterUserId();
			inviterUserIds.add(userId);

			inviterIdsToInvites.put(userId, invite);
		}

	}

	//given map of userIds to users, list of recipient facebook ids and list of inviter
	//user ids, separate the map of users into recipient and inviter
	private void separateUsersIntoRecipientsAndInviters(
			Map<String, User> idsToUsers) {

		recipients = new ArrayList<User>();
		inviters = new ArrayList<User>();

		Set<String> recipientFacebookIdsSet = new HashSet<String>(
				recipientFacebookIds);

		//set the recipients
		for (String userId : idsToUsers.keySet()) {
			User u = idsToUsers.get(userId);
			String facebookId = u.getFacebookId();

			if (null != facebookId
					&& recipientFacebookIdsSet.contains(facebookId)) {
				//this is a recipient
				recipients.add(u);
			}
		}

		//set the inviters
		for (String inviterId : inviterUserIds) {
			if (idsToUsers.containsKey(inviterId)) {
				User u = idsToUsers.get(inviterId);
				inviters.add(u);
			}
		}

	}
}
