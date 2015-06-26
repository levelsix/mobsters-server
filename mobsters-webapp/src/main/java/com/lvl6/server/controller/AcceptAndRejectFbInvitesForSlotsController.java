package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AcceptAndRejectFbInviteForSlotsRequestEvent;
import com.lvl6.events.response.AcceptAndRejectFbInviteForSlotsResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsRequestProto;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class AcceptAndRejectFbInvitesForSlotsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(AcceptAndRejectFbInvitesForSlotsController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	public AcceptAndRejectFbInvitesForSlotsController() {
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new AcceptAndRejectFbInviteForSlotsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses){
		AcceptAndRejectFbInviteForSlotsRequestProto reqProto = ((AcceptAndRejectFbInviteForSlotsRequestEvent) event)
				.getAcceptAndRejectFbInviteForSlotsRequestProto();

		log.info(String.format("reqProto=%s", reqProto));
		//get values sent from the client (the request proto)
		MinimumUserProtoWithFacebookId senderProto = reqProto.getSender();
		MinimumUserProto sender = senderProto.getMinUserProto();
		String userId = sender.getUserUuid();
		String userFacebookId = senderProto.getFacebookId();

		//just accept these
		List<String> acceptedInviteIds = reqProto.getAcceptedInviteUuidsList();
		if (null == acceptedInviteIds) {
			acceptedInviteIds = new ArrayList<String>();
		} else {
			acceptedInviteIds = new ArrayList<String>(acceptedInviteIds);
		}

		//delete these from the table
		List<String> rejectedInviteIds = reqProto.getRejectedInviteUuidsList();
		if (null == rejectedInviteIds) {
			rejectedInviteIds = new ArrayList<String>();
		} else {
			rejectedInviteIds = new ArrayList<String>(rejectedInviteIds);
		}
		Timestamp acceptTime = new Timestamp((new Date()).getTime());

		//set some values to send to the client (the response proto)
		AcceptAndRejectFbInviteForSlotsResponseProto.Builder resBuilder = AcceptAndRejectFbInviteForSlotsResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		boolean invalidUuids = false;
		try {
			userUuid = UUID.fromString(userId);
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_OTHER);
			AcceptAndRejectFbInviteForSlotsResponseEvent resEvent = new AcceptAndRejectFbInviteForSlotsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			//these will be populated. by checkLegit()
			Map<String, UserFacebookInviteForSlot> idsToInvitesInDb = new HashMap<String, UserFacebookInviteForSlot>();

			boolean legit = checkLegit(resBuilder, userId, userFacebookId,
					acceptedInviteIds, rejectedInviteIds, idsToInvitesInDb);

			boolean successful = false;
			if (legit) {
				successful = writeChangesToDb(userId, userFacebookId,
						acceptedInviteIds, rejectedInviteIds, idsToInvitesInDb,
						acceptTime);
			}

			if (successful) {
				//need to retrieve all the inviters from the db, set the accepted time for
				//accepted invites
				Collection<UserFacebookInviteForSlot> invites = idsToInvitesInDb
						.values();
				List<String> userIds = getInviterIds(invites);
				Map<String, User> idsToInviters = RetrieveUtils
						.userRetrieveUtils().getUsersByIds(userIds);
				Map<String, Clan> clanIdsToClans = getClans(idsToInviters);

				for (UserFacebookInviteForSlot invite : invites) {
					invite.setTimeAccepted(acceptTime);

					String inviterId = invite.getInviterUserId();
					User inviter = idsToInviters.get(inviterId);
					String clanId = inviter.getClanId();
					Clan clan = null;
					if (clanIdsToClans.containsKey(clanId)) {
						clan = clanIdsToClans.get(clanId);
					}
					MinimumUserProtoWithFacebookId inviterProto = null;

					//create the proto for the invites

					UserFacebookInviteForSlotProto inviteProto = createInfoProtoUtils
							.createUserFacebookInviteForSlotProtoFromInvite(
									invite, inviter, clan, inviterProto);

					resBuilder.addAcceptedInvites(inviteProto);
				}
				resBuilder
						.setStatus(ResponseStatus.SUCCESS);
			}

			AcceptAndRejectFbInviteForSlotsResponseEvent resEvent = new AcceptAndRejectFbInviteForSlotsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//write to the inviters this user accepted
				AcceptAndRejectFbInviteForSlotsResponseProto responseProto = resBuilder
						.build();
				for (String inviteId : acceptedInviteIds) {
					UserFacebookInviteForSlot invite = idsToInvitesInDb
							.get(inviteId);
					String inviterId = invite.getInviterUserId();

					AcceptAndRejectFbInviteForSlotsResponseEvent newResEvent = new AcceptAndRejectFbInviteForSlotsResponseEvent(
							inviterId);
					newResEvent.setTag(0);
					newResEvent
							.setResponseProto(responseProto);
					responses.normalResponseEvents().add(newResEvent);

				}
			}

		} catch (Exception e) {
			log.error(
					"exception in AcceptAndRejectFbInviteForSlotsController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder
						.setStatus(ResponseStatus.FAIL_OTHER);
				AcceptAndRejectFbInviteForSlotsResponseEvent resEvent = new AcceptAndRejectFbInviteForSlotsResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in AcceptAndRejectFbInviteForSlotsController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value. accepetedInviteIds,
	 * rejectedInviteIds, and idsToAcceptedInvites might be modified
	 */
	private boolean checkLegit(Builder resBuilder, String userId,
			String userFacebookId, List<String> acceptedInviteIds,
			List<String> rejectedInviteIds,
			Map<String, UserFacebookInviteForSlot> idsToInvites) {

		if (null == userFacebookId || userFacebookId.isEmpty()) {
			log.error(String
					.format("facebookId is null. id=%s, acceptedInvitesIds=%s, rejectedInviteIds=%s",
							userFacebookId, acceptedInviteIds,
							rejectedInviteIds));
			return false;
		}
		//search for these invites accepted and rejected
		List<String> inviteIds = new ArrayList<String>(acceptedInviteIds);
		inviteIds.addAll(rejectedInviteIds);

		//retrieve the invites for this recipient that haven't been accepted nor redeemed
		boolean filterByAccepted = true;
		boolean isAccepted = false;
		boolean filterByRedeemed = true;
		boolean isRedeemed = false;
		Map<String, UserFacebookInviteForSlot> idsToInvitesInDb = userFacebookInviteForSlotRetrieveUtils
				.getSpecificOrAllInvitesForRecipient(userFacebookId, inviteIds,
						filterByAccepted, isAccepted, filterByRedeemed,
						isRedeemed);

		log.info("idsToInvitesInDb={}", idsToInvitesInDb);
		Set<String> validIds = idsToInvitesInDb.keySet();

		//only want the acceptedInvite ids that aren't yet accepted nor redeemed
		log.info(String.format("acceptedInviteIds before filter: %s",
				acceptedInviteIds));
		retainIfInExisting(validIds, acceptedInviteIds);
		log.info(String.format("acceptedInviteIds after filter: %s",
				acceptedInviteIds));

		//only want the rejectedInvite ids that aren't yet accepted nor redeemed
		retainIfInExisting(validIds, rejectedInviteIds);

		//check to make sure this user is not accepting any invites from an inviter
		//this user has already accepted, or in other words
		//check to make sure this user has not previously accepted any invites from
		//any of the inviters of the acceptedInviteIds

		//pair up inviterUserIds with the acceptedInviteIds
		Map<String, String> acceptedInviterIdsToInviteIds = getInviterUserIds(
				acceptedInviteIds, idsToInvitesInDb);

		//look in the invite table for accepted invites (includes redeemed),
		//select the inviter user ids that have recipientFacebookId = userFacebookId
		isAccepted = true;
		Set<String> redeemedInviterIds = userFacebookInviteForSlotRetrieveUtils
				.getUniqueInviterUserIdsForRequesterId(userFacebookId,
						filterByAccepted, isAccepted);

		//if any of the acceptedInviteIds contains an inviterId this user has already accepted
		//an invite from,
		//delete inviteId from the acceptedInviteIds list and put the
		//inviteId into the rejectedInviteIds list,
		//done so because the db probably has recorded that the inviter used up this user
		//and is trying to use this user again
		log.info(String.format(
				"acceptedInviteIds before inviter used check: %s",
				acceptedInviteIds));
		retainInvitesFromUnusedInviters(redeemedInviterIds,
				acceptedInviterIdsToInviteIds, acceptedInviteIds,
				rejectedInviteIds);
		log.info(String.format(
				"acceptedInviteIds after inviter used check: %s",
				acceptedInviteIds));

		idsToInvites.putAll(idsToInvitesInDb);

		return true;
	}

	private void retainIfInExisting(Set<String> existingStr,
			List<String> someStr) {
		int lastIndex = someStr.size() - 1;
		for (int index = lastIndex; index >= 0; index--) {
			String someInt = someStr.get(index);

			if (!existingStr.contains(someInt)) {
				//since the Str is not in existingStr, remove it.
				someStr.remove(index);
			}
		}
	}

	private Map<String, String> getInviterUserIds(List<String> inviteIds,
			Map<String, UserFacebookInviteForSlot> idsToInvites) {
		Map<String, String> inviterUserIdsToInviteIds = new HashMap<String, String>();

		for (String inviteId : inviteIds) {
			UserFacebookInviteForSlot invite = idsToInvites.get(inviteId);
			String inviterUserId = invite.getInviterUserId();
			//what if this guy is used more than once? meh, fuck it
			inviterUserIdsToInviteIds.put(inviterUserId, inviteId);
		}

		return inviterUserIdsToInviteIds;
	}

	//recordedInviterIds are the inviterIds in the invite table that belong to invites
	//accepted by a user
	private void retainInvitesFromUnusedInviters(
			Set<String> recordedInviterIds,
			Map<String, String> acceptedInviterIdsToInviteIds,
			List<String> acceptedInviteIds, List<String> rejectedInviteIds) {
		//if any of the inviter ids in acceptedInviterIdsToInviteIds are in
		//recordedInviterIds, delete inviteId from the
		//acceptedInviteIds list and put the inviteId into the rejectedInviteIds list

		//keep track of the inviterIds that this user has previously already accepted
		Map<String, String> invalidInviteIdsToUserIds = new HashMap<String, String>();
		for (String potentialNewInviterId : acceptedInviterIdsToInviteIds
				.keySet()) {
			if (recordedInviterIds.contains(potentialNewInviterId)) {
				//userA trying to accept an invite from a person userA has already
				//accepted an invite from
				String inviteId = acceptedInviterIdsToInviteIds
						.get(potentialNewInviterId);

				invalidInviteIdsToUserIds.put(inviteId, potentialNewInviterId);
			}
		}

		Set<String> invalidInviteIds = invalidInviteIdsToUserIds.keySet();
		if (invalidInviteIds.isEmpty()) {
			return;
		}
		log.warn(String
				.format("accepting invites from users user already accepted. invalidInviteIdsToUserIds=%s",
						invalidInviteIdsToUserIds));

		log.warn(String.format("before: rejectedInviteIds=%s",
				acceptedInviteIds));
		log.warn(String.format("before: acceptedInviteIds=%s",
				acceptedInviteIds));
		//go through acceptedInviteIds and remove invalid inviteIds
		int lastIndex = acceptedInviteIds.size() - 1;
		for (int index = lastIndex; index >= 0; index--) {
			String acceptedInviteId = acceptedInviteIds.get(index);

			if (invalidInviteIds.contains(acceptedInviteId)) {
				acceptedInviteIds.remove(index);

				//after removing it put it into rejectedInviteIds
				rejectedInviteIds.add(acceptedInviteId);
			}
		}
		log.warn(String
				.format("after: acceptedInviteIds=%s", acceptedInviteIds));
		log.warn(String
				.format("after: rejectedInviteIds=%s", rejectedInviteIds));
	}

	private boolean writeChangesToDb(String userId, String userFacebookId,
			List<String> acceptedInviteIds, List<String> rejectedInviteIds,
			Map<String, UserFacebookInviteForSlot> idsToInvitesInDb,
			Timestamp acceptTime) {
		log.info(String
				.format("idsToInvitesInDb=%s, acceptedInviteIds=%s, rejectedInviteIds=%s ",
						idsToInvitesInDb, acceptedInviteIds, rejectedInviteIds));

		//update the acceptTimes for the acceptedInviteIds
		//these acceptedInviteIds are for unaccepted, unredeemed invites
		if (!acceptedInviteIds.isEmpty()) {
			int num = UpdateUtils.get()
					.updateUserFacebookInviteForSlotAcceptTime(userFacebookId,
							acceptedInviteIds, acceptTime);
			log.info(String.format(
					"\t\t\t num acceptedInviteIds updated: %s \t invites=%s",
					num, acceptedInviteIds));
		}

		//DELETE THE rejectedInviteIds THAT ARE ALREADY IN DB
		//these deleted invites are for unaccepted, unredeemed invites
		if (!rejectedInviteIds.isEmpty()) {
			int num = DeleteUtils.get().deleteUserFacebookInvitesForSlots(
					rejectedInviteIds);
			log.info("num rejectedInviteIds deleted: " + num + "\t invites="
					+ rejectedInviteIds);
		}

		return true;
	}

	private List<String> getInviterIds(
			Collection<UserFacebookInviteForSlot> invites) {
		List<String> inviterIds = new ArrayList<String>();

		for (UserFacebookInviteForSlot invite : invites) {
			String inviterId = invite.getInviterUserId();
			inviterIds.add(inviterId);
		}
		return inviterIds;
	}

	private Map<String, Clan> getClans(Map<String, User> idsToInviters) {
		Set<String> clanIds = new HashSet<String>();

		for (User u : idsToInviters.values()) {
			String clanId = u.getClanId();
			if (null != clanId && !clanId.isEmpty()) {
				clanIds.add(clanId);
			}
		}

		if (!clanIds.isEmpty()) {
			return clanRetrieveUtils.getClansByIds(new ArrayList<String>(
					clanIds));
		}
		return new HashMap<String, Clan>();
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserFacebookInviteForSlotRetrieveUtils2 getUserFacebookInviteForSlotRetrieveUtils() {
		return userFacebookInviteForSlotRetrieveUtils;
	}

	public void setUserFacebookInviteForSlotRetrieveUtils(
			UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils) {
		this.userFacebookInviteForSlotRetrieveUtils = userFacebookInviteForSlotRetrieveUtils;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

}
