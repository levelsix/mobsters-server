package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.InviteFbFriendsForSlotsRequestEvent;
import com.lvl6.events.response.InviteFbFriendsForSlotsResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsRequestProto;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsRequestProto.FacebookInviteStructure;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto.InviteFbFriendsForSlotsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
@DependsOn("gameServer")
public class InviteFbFriendsForSlotsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils;

	public InviteFbFriendsForSlotsController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new InviteFbFriendsForSlotsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		InviteFbFriendsForSlotsRequestProto reqProto = ((InviteFbFriendsForSlotsRequestEvent) event)
				.getInviteFbFriendsForSlotsRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		//get values sent from the client (the request proto)
		MinimumUserProtoWithFacebookId senderProto = reqProto.getSender();
		String userId = senderProto.getMinUserProto().getUserUuid();
		List<FacebookInviteStructure> invites = reqProto.getInvitesList();

		Map<String, String> fbIdsToUserStructIds = new HashMap<String, String>();
		Map<String, Integer> fbIdsToUserStructFbLvl = new HashMap<String, Integer>();
		List<String> fbIdsOfFriends = demultiplexFacebookInviteStructure(
				invites, fbIdsToUserStructIds, fbIdsToUserStructFbLvl);

		Timestamp curTime = new Timestamp((new Date()).getTime());

		//set some values to send to the client (the response proto)
		InviteFbFriendsForSlotsResponseProto.Builder resBuilder = InviteFbFriendsForSlotsResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(InviteFbFriendsForSlotsStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(InviteFbFriendsForSlotsStatus.FAIL_OTHER);
			InviteFbFriendsForSlotsResponseEvent resEvent = new InviteFbFriendsForSlotsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setInviteFbFriendsForSlotsResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User aUser = getUserRetrieveUtils().getUserById(userId);
			//get all the invites the user sent
			List<String> specificIds = null;
			boolean filterByAccepted = false;
			boolean isAccepted = false;
			boolean filterByRedeemed = false;
			boolean isRedeemed = false;
			Map<String, UserFacebookInviteForSlot> idsToInvites = getUserFacebookInviteForSlotRetrieveUtils()
					.getSpecificOrAllInvitesForInviter(userId, specificIds,
							filterByAccepted, isAccepted, filterByRedeemed,
							isRedeemed);

			//will contain the facebook ids of new users the user can invite
			//new is defined as: for each facebookId the tuple
			//(inviterId, recipientId)=(userId, facebookId) 
			//doesn't already exist in the table 
			List<String> newFacebookIdsToInvite = new ArrayList<String>();
			boolean legit = checkLegit(resBuilder, userId, aUser,
					fbIdsOfFriends, idsToInvites, newFacebookIdsToInvite);

			boolean successful = false;
			List<String> inviteIds = new ArrayList<String>();
			if (legit) {
				//will populate inviteIds
				successful = writeChangesToDb(aUser, newFacebookIdsToInvite,
						curTime, fbIdsToUserStructIds, fbIdsToUserStructFbLvl,
						inviteIds);
			}

			if (successful) {
				Clan inviterClan = null;

				//inviteIds can be empty when user invites some facebook friends
				//he's already invited
				//NOTE: Could construct the Map<String, UserFacebookInviteForSlot> newIdsToInvites
				//instead of retrieveing from db
				if (!inviteIds.isEmpty()) {
					Map<String, UserFacebookInviteForSlot> newIdsToInvites = getUserFacebookInviteForSlotRetrieveUtils()
							.getInviteForId(inviteIds);
					//client needs to know what the new invites are;
					for (String id : newIdsToInvites.keySet()) {
						UserFacebookInviteForSlot invite = newIdsToInvites
								.get(id);
						UserFacebookInviteForSlotProto inviteProto = createInfoProtoUtils
								.createUserFacebookInviteForSlotProtoFromInvite(
										invite, aUser, inviterClan, senderProto);
						resBuilder.addInvitesNew(inviteProto);
					}
				}
				resBuilder.setStatus(InviteFbFriendsForSlotsStatus.SUCCESS);
			}

			InviteFbFriendsForSlotsResponseEvent resEvent = new InviteFbFriendsForSlotsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setInviteFbFriendsForSlotsResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			//send this to all the recipients in fbIdsOfFriends that have a user id
			//if want to send to the new ones only use newFacebookIdsToInvite
			//if want to send to old and new ones use fbIdsOfFriends
			//old one means a User who has already been invited by senderProto
			if (successful && !newFacebookIdsToInvite.isEmpty()) {
				List<String> recipientUserIds = getUserRetrieveUtils()
						.getUserIdsForFacebookIds(newFacebookIdsToInvite);

				InviteFbFriendsForSlotsResponseProto responseProto = resBuilder
						.build();
				for (String recipientUserId : recipientUserIds) {
					InviteFbFriendsForSlotsResponseEvent newResEvent = new InviteFbFriendsForSlotsResponseEvent(
							recipientUserId);
					newResEvent.setTag(0);
					newResEvent
							.setInviteFbFriendsForSlotsResponseProto(responseProto);
					responses.normalResponseEvents().add(newResEvent);
				}
			}
		} catch (Exception e) {
			log.error(
					"exception in InviteFbFriendsForSlotsController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(InviteFbFriendsForSlotsStatus.FAIL_OTHER);
				InviteFbFriendsForSlotsResponseEvent resEvent = new InviteFbFriendsForSlotsResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setInviteFbFriendsForSlotsResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in InviteFbFriendsForSlotsController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private List<String> demultiplexFacebookInviteStructure(
			List<FacebookInviteStructure> invites,
			Map<String, String> fbIdsToUserStructIds,
			Map<String, Integer> fbIdsToUserStructFbLvl) {

		List<String> retVal = new ArrayList<String>();
		for (FacebookInviteStructure fis : invites) {
			String fbId = fis.getFbFriendId();

			String userStructId = fis.getUserStructUuid();
			int userStructFbLvl = fis.getUserStructFbLvl();

			retVal.add(fbId);
			fbIdsToUserStructIds.put(fbId, userStructId);
			fbIdsToUserStructFbLvl.put(fbId, userStructFbLvl);
		}
		return retVal;
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value. newUserIdsToInvite will be 
	 * modified
	 */
	private boolean checkLegit(Builder resBuilder, String userId, User u,
			List<String> fbIdsOfFriends,
			Map<String, UserFacebookInviteForSlot> idsToInvites,
			List<String> newFacebookIdsToInvite) {

		if (null == u) {
			log.error("user is null. no user exists with id=" + userId);
			return false;
		}

		//if the user already invited some friends, don't invite again, keep
		//only new ones
		List<String> newFacebookIdsToInviteTemp = getNewInvites(fbIdsOfFriends,
				idsToInvites);
		newFacebookIdsToInvite.addAll(newFacebookIdsToInviteTemp);
		return true;
	}

	//keeps and returns the facebook ids that have not been invited yet
	private List<String> getNewInvites(List<String> fbIdsOfFriends,
			Map<String, UserFacebookInviteForSlot> idsToInvites) {
		//CONTAINS THE DUPLICATE INVITES, THAT NEED TO BE DELETED
		//E.G. TWO INVITES EXIST WITH SAME INVITERID AND RECIPIENTID
		List<String> inviteIdsOfDuplicateInvites = new ArrayList<String>();

		//running collection of recipient ids already seen
		Set<String> processedRecipientIds = new HashSet<String>();

		//for each recipientId separate the unique ones from the duplicates
		for (String inviteId : idsToInvites.keySet()) {
			UserFacebookInviteForSlot invite = idsToInvites.get(inviteId);
			String recipientId = invite.getRecipientFacebookId();

			//if seen this recipientId place it in the duplicates list
			if (processedRecipientIds.contains(recipientId)) {
				//done to ensure a user does not invite another user more than once
				//i.e. tuple (inviterId, recipientId) is unique
				inviteIdsOfDuplicateInvites.add(inviteId);
			} else {
				//keep track of the recipientIds seen so far (the unique ones)
				processedRecipientIds.add(recipientId);
			}
		}

		//DELETE THE DUPLICATE INVITES THAT ARE ALREADY IN DB
		//maybe need to determine which invites should be deleted, as in most recent or somethings
		//because right now, any of the nonunique invites could be deleted
		if (!inviteIdsOfDuplicateInvites.isEmpty()) {
			int num = DeleteUtils.get().deleteUserFacebookInvitesForSlots(
					inviteIdsOfDuplicateInvites);
			log.warn("num duplicate invites deleted: {} \t allInvites={}", num,
					idsToInvites);
		}

		List<String> newFacebookIdsToInvite = new ArrayList<String>();
		//don't want to generate an invite to a recipient the user has already invited
		//going through the facebook ids client sends and select only the new ones
		for (String prospectiveRecipientId : fbIdsOfFriends) {

			//keep only the recipient ids that have not been seen/invited
			if (!processedRecipientIds.contains(prospectiveRecipientId)) {
				newFacebookIdsToInvite.add(prospectiveRecipientId);
			}
		}
		return newFacebookIdsToInvite;
	}

	private boolean writeChangesToDb(User aUser,
			List<String> newFacebookIdsToInvite, Timestamp curTime,
			Map<String, String> fbIdsToUserStructIds,
			Map<String, Integer> fbIdsToUserStructsFbLvl, List<String> inviteIds) {
		if (newFacebookIdsToInvite.isEmpty()) {
			return true;
		}
		String userId = aUser.getId();
		List<String> inviteIdsTemp = InsertUtils.get()
				.insertIntoUserFbInviteForSlot(userId, newFacebookIdsToInvite,
						curTime, fbIdsToUserStructIds, fbIdsToUserStructsFbLvl);
		int numInserted = inviteIdsTemp.size();

		int expectedNum = newFacebookIdsToInvite.size();
		if (numInserted != expectedNum) {
			log.error("problem with updating user monster inventory slots and diamonds."
					+ " num inserted: "
					+ numInserted
					+ "\t should have been: "
					+ expectedNum);
		}
		log.info("num inserted: " + numInserted + "\t ids=" + inviteIdsTemp);

		inviteIds.addAll(inviteIdsTemp);
		return true;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public UserFacebookInviteForSlotRetrieveUtils2 getUserFacebookInviteForSlotRetrieveUtils() {
		return userFacebookInviteForSlotRetrieveUtils;
	}

	public void setUserFacebookInviteForSlotRetrieveUtils(
			UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils) {
		this.userFacebookInviteForSlotRetrieveUtils = userFacebookInviteForSlotRetrieveUtils;
	}

}
