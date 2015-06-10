package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.IncreaseMonsterInventorySlotRequestEvent;
import com.lvl6.events.response.IncreaseMonsterInventorySlotResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureResidence;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto.IncreaseSlotType;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.IncreaseMonsterInventorySlotStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.StructureInfoProto.StructType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureResidenceRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class IncreaseMonsterInventorySlotController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UserFacebookInviteForSlotRetrieveUtils2 userFacebookInviteForSlotRetrieveUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected StructureForUserRetrieveUtils2 userStructRetrieveUtils;

	@Autowired
	protected StructureResidenceRetrieveUtils structureResidenceRetrieveUtils;

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	public IncreaseMonsterInventorySlotController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new IncreaseMonsterInventorySlotRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_INCREASE_MONSTER_INVENTORY_SLOT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		IncreaseMonsterInventorySlotRequestProto reqProto = ((IncreaseMonsterInventorySlotRequestEvent) event)
				.getIncreaseMonsterInventorySlotRequestProto();

		log.info("reqProto={}", reqProto);
		//EVERY TIME USER BUYS SLOTS RESET user_facebook_invite_for_slot table

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		IncreaseSlotType increaseType = reqProto.getIncreaseSlotType();
		String userStructId = reqProto.getUserStructUuid();
		//the invites to redeem
		List<String> userFbInviteIds = reqProto
				.getUserFbInviteForSlotUuidsList();
		Timestamp curTime = new Timestamp((new Date()).getTime());

		//set some values to send to the client (the response proto)
		IncreaseMonsterInventorySlotResponseProto.Builder resBuilder = IncreaseMonsterInventorySlotResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		UUID inviteUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (userFbInviteIds != null) {
				for (String userFbInviteId : userFbInviteIds) {
					inviteUuid = UUID.fromString(userFbInviteId);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userFbInviteIds=%s",
					userId, userFbInviteIds), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER);
			IncreaseMonsterInventorySlotResponseEvent resEvent = new IncreaseMonsterInventorySlotResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setIncreaseMonsterInventorySlotResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			int previousGems = 0;
			//get stuff from the db
			User aUser = getUserRetrieveUtils().getUserById(userId);
			StructureForUser sfu = getUserStructRetrieveUtils()
					.getSpecificUserStruct(userStructId);

			//will be populated if user is successfully redeeming fb invites
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStruct = new HashMap<String, UserFacebookInviteForSlot>();

			boolean legit = checkLegit(resBuilder, userId, aUser, userStructId,
					sfu, increaseType, userFbInviteIds,
					idsToInvitesForUserStruct);

			int gemCost = 0;
			boolean successful = false;
			Map<String, Integer> changeMap = new HashMap<String, Integer>();
			if (legit) {
				previousGems = aUser.getGems();
				gemCost = getGemPriceFromStruct(sfu);
				successful = writeChangesToDb(aUser, sfu, increaseType,
						gemCost, curTime, idsToInvitesForUserStruct, changeMap);
			}

			if (successful) {
				resBuilder
						.setStatus(IncreaseMonsterInventorySlotStatus.SUCCESS);
			}

			IncreaseMonsterInventorySlotResponseEvent resEvent = new IncreaseMonsterInventorySlotResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setIncreaseMonsterInventorySlotResponseProto(resBuilder
					.build());
			server.writeEvent(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				if (increaseType == IncreaseSlotType.PURCHASE) {
					writeToUserCurrencyHistory(aUser, sfu, increaseType,
							curTime, changeMap, previousGems);
				}
				//delete the user's facebook invites for slots
				deleteInvitesForSlotsAfterPurchase(userId, changeMap);
			}
		} catch (Exception e) {
			log.error(
					"exception in IncreaseMonsterInventorySlotController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder
						.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER);
				IncreaseMonsterInventorySlotResponseEvent resEvent = new IncreaseMonsterInventorySlotResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setIncreaseMonsterInventorySlotResponseProto(resBuilder
						.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in IncreaseMonsterInventorySlotController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 */
	private boolean checkLegit(Builder resBuilder, String userId, User u,
			String userStructId, StructureForUser sfu, IncreaseSlotType aType,
			List<String> userFbInviteIds,
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStruct) {
		if (null == u) {
			log.error("user is null. no user exists with id=" + userId);
			return false;
		}
		if (null == sfu) {
			log.error("doesn't exist, user struct with id=" + userStructId);
			return false;
		}

		//THE CHECK IF USER IS REDEEMING FACEBOOK INVITES
		if (IncreaseSlotType.REDEEM_FACEBOOK_INVITES == aType) {
			//get accepted and unredeemed invites
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStructTemp = getUserFacebookInviteForSlotRetrieveUtils()
					.getInvitesForUserStructMap(userId, userStructId);
			//check if requested invites even exist
			if (null == idsToInvitesForUserStructTemp
					|| idsToInvitesForUserStructTemp.isEmpty()) {
				log.error("no invites exist for userId={} \t userStructId={}",
						userId, userStructId);
				return false;
			}

			//should get rid of the invites that have fbLvl before sfu.getFbInviteStructLvl()
			//THE INVITE IN THE DB TABLE SPECIFIES THE GOAL fb_struct_lvl
			//so need to keep the invites with the same goal fb_struct_lvl
			filterOutInvites(sfu.getFbInviteStructLvl() + 1,
					idsToInvitesForUserStructTemp);

			//check if requested invites even exist
			boolean allRequestedIdsExist = idsToInvitesForUserStructTemp
					.keySet().containsAll(userFbInviteIds);
			if (!allRequestedIdsExist) {
				log.error("not all invites exist. ids={} \t inDb={}",
						userFbInviteIds, idsToInvitesForUserStructTemp);
				return false;
			}

			String userStructIdFromInvites = getUserStructId(userFbInviteIds,
					idsToInvitesForUserStructTemp);
			if (!userStructId.equals(userStructIdFromInvites)) {
				resBuilder
						.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_INCONSISTENT_INVITE_DATA);
				String preface = "data across invites inconsistent:";
				log.error(
						"{} user struct id/fb lvl. invites={}, \t expectedUserStructId={}",
						new Object[] { preface, idsToInvitesForUserStructTemp,
								userStructId });
				return false;
			}

			//check if user struct is already at its max fb invite lvl,
			int structId = sfu.getStructId();
			Structure struct = structureRetrieveUtils
					.getStructForStructId(structId);
			int structLvl = struct.getLevel();
			int nextUserStructFbInviteLvl = sfu.getFbInviteStructLvl() + 1;

			if (nextUserStructFbInviteLvl > structLvl) {
				resBuilder
						.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_STRUCTURE_AT_MAX_FB_INVITE_LVL);
				log.error(
						"user struct maxed fb invite lvl. userStruct={} \t struct={}",
						sfu, struct);
				return false;
			}

			//required min num invites depends on the structure and the UserStructure's fb lvl
			int minNumInvites = getMinNumInvitesFromStruct(sfu, structId,
					nextUserStructFbInviteLvl);

			//check if user has enough invites to gain a slot
			int acceptedAmount = calcNumAcceptedInvites(idsToInvitesForUserStructTemp);

			if (acceptedAmount < minNumInvites) {
				resBuilder
						.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_INSUFFICIENT_FACEBOOK_INVITES);
				String preface = "insufficient accepted fb invites to increase slots.";
				log.error("{} \t minRequired={} \t has={}", new Object[] {
						preface, minNumInvites, acceptedAmount });
				return false;
			}

			//give the caller the invites, at this point, the number of invites is at least
			//equal to minNumInvites and could be more
			idsToInvitesForUserStruct.putAll(idsToInvitesForUserStructTemp);

			//THE CHECK IF USER IS BUYING SLOTS
		} else if (IncreaseSlotType.PURCHASE == aType) {
			//gemprice depends on the structure;
			int gemPrice = getGemPriceFromStruct(sfu);

			//check if user has enough money
			int userGems = u.getGems();
			if (userGems < gemPrice) {
				resBuilder
						.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_INSUFFICIENT_FUNDS);
				log.error("user does not have enough gems to buy more monster inventory slots. userGems="
						+ userGems + "\t gemPrice=" + gemPrice);
				return false;
			}

		} else {
			return false;
		}

		return true;
	}

	private void filterOutInvites(int sfuFbLvl,
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStruct) {
		List<String> removeIds = new ArrayList<String>();

		//filtering out invites with userStructFbLvl that is below sfuFbLvl
		//keep only the invites with sfuFbLvl
		for (UserFacebookInviteForSlot invite : idsToInvitesForUserStruct
				.values()) {
			int inviteFbStructLvl = invite.getUserStructFbLvl();

			if (sfuFbLvl != inviteFbStructLvl) {
				removeIds.add(invite.getId());
			}
		}

		log.info("removing irrelevant invites={}, inDb={}", removeIds,
				idsToInvitesForUserStruct);
		for (String inviteId : removeIds) {
			idsToInvitesForUserStruct.remove(inviteId);
		}
	}

	private int calcNumAcceptedInvites(
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStruct) {
		int numAccepted = 0;
		for (UserFacebookInviteForSlot ufifs : idsToInvitesForUserStruct
				.values()) {
			if (null != ufifs.getTimeAccepted()) {
				numAccepted += 1;
			}
		}

		log.info("numAccepted={}, invites={}", numAccepted,
				idsToInvitesForUserStruct);

		return numAccepted;
	}

	//  private Map<String, UserFacebookInviteForSlot> getInvites(String userId,
	//  		List<String> userFbInviteIds) {
	//  	//get accepted and unredeemed invites
	//		boolean filterByAccepted = true;
	//		boolean isAccepted = true;
	//		boolean filterByRedeemed = true;
	//		boolean isRedeemed = false;
	//		Map<String, UserFacebookInviteForSlot> idsToAcceptedTemp =
	//				getUserFacebookInviteForSlotRetrieveUtils().getSpecificOrAllInvitesForInviter(
	//						userId, userFbInviteIds, filterByAccepted, isAccepted, filterByRedeemed,
	//						isRedeemed);
	//		return idsToAcceptedTemp;
	//  }

	//if user struct ids and user struct fb lvls are inconsistent, return non-positive value;
	private String getUserStructId(List<String> userFbInviteIds,
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStructTemp) {
		String prevUserStructId = null;
		int prevUserStructFbLvl = -1;

		for (String inviteId : userFbInviteIds) {
			UserFacebookInviteForSlot invite = idsToInvitesForUserStructTemp
					.get(inviteId);

			String tempUserStructId = invite.getUserStructId();
			int tempUserStructFbLvl = invite.getUserStructFbLvl();
			if (null == prevUserStructId) {
				prevUserStructId = tempUserStructId;
				prevUserStructFbLvl = tempUserStructFbLvl;

			} else if (!prevUserStructId.equals(tempUserStructId)
					|| prevUserStructFbLvl != tempUserStructFbLvl) {
				//since the userStructIds or userStructFbLvl's are inconsistent, return failure
				log.error(
						"inconsistent invites (userStructId, fbLvl). requested={} \t invitesFromDb={}",
						userFbInviteIds, idsToInvitesForUserStructTemp);
				return null;
			}

		}
		return prevUserStructId;
	}

	private int getMinNumInvitesFromStruct(StructureForUser sfu, int structId,
			int userStructFbInviteLvl) {
		//since userStructFbInviteLvl and structure level are one to one, essentially
		//they are one and the same
		//e.g. userStructFbInviteLvl = 1, also means 1 = structure level
		//get the structure with the struct lvl= userStructFbInviteLvl
		Structure structForFbInviteLvl = structureRetrieveUtils
				.getPredecessorStructForStructIdAndLvl(structId,
						userStructFbInviteLvl);
		String structType = structForFbInviteLvl.getStructType();

		log.info("StructureForUser=" + sfu);
		log.info("structId=" + structForFbInviteLvl);
		log.info("userStructFbInviteLvl=" + userStructFbInviteLvl);
		log.info("resulting structure for structId and level: "
				+ structForFbInviteLvl);

		int minNumInvites = -1;
		//at the moment, invites are only for residences
		if (StructType.valueOf(structType) == StructType.RESIDENCE) {
			int structIdForUserStructFbInviteLvl = structForFbInviteLvl.getId();
			StructureResidence residence = structureResidenceRetrieveUtils
					.getResidenceForStructId(structIdForUserStructFbInviteLvl);
			minNumInvites = residence.getNumAcceptedFbInvites();
		} else {
			log.error("invalid struct type for increasing monster slots. structType="
					+ structType);
		}

		log.info("getMinNumInvitesFromStruct returns minNumInvites="
				+ minNumInvites);
		return minNumInvites;
	}

	private int getGemPriceFromStruct(StructureForUser sfu) {
		//get the structure, based off of fbInviteStructLvl
		//fbInviteStructLvl starts off at 0. If sfu is the very
		//first level then need to find gem price of structure at lvl =
		//fbInviteStructLvl + 1
		int structId = sfu.getStructId();
		Structure struct = structureRetrieveUtils
				.getPredecessorStructForStructIdAndLvl(
						structId, sfu.getFbInviteStructLvl() + 1);
		String structType = struct.getStructType();

		int gemPrice = Integer.MAX_VALUE;
		//at the moment, invites are only for residences
		if (StructType.valueOf(structType) == StructType.RESIDENCE) {
			structId = struct.getId();
			StructureResidence residence = structureResidenceRetrieveUtils
					.getResidenceForStructId(structId);
			gemPrice = residence.getNumGemsRequired();
		}

		return gemPrice;
	}

	private boolean writeChangesToDb(User aUser, StructureForUser sfu,
			IncreaseSlotType increaseType, int gemCost, Timestamp curTime,
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStruct,
			Map<String, Integer> changeMap) {
		boolean success = false;

		//NOTE: if for some reason user manages to send two of these events
		//and both are for the same userStruct and the second event
		//is a duplicate of the first, need to only increases
		//the userStruct's fbLvl only once
		int nuFbInviteLevel = sfu.getFbInviteStructLvl();

		if (IncreaseSlotType.REDEEM_FACEBOOK_INVITES == increaseType) {
			int structId = sfu.getStructId();
			int nextUserStructFbInviteLvl = sfu.getFbInviteStructLvl() + 1;
			int minNumInvites = getMinNumInvitesFromStruct(sfu, structId,
					nextUserStructFbInviteLvl);

			//if num accepted invites more than min required, just take the earliest ones

			List<String> inviteIdsTheRest = new ArrayList<String>();
			log.info("idsToInvitesForUserStruct={}", idsToInvitesForUserStruct);
			List<UserFacebookInviteForSlot> nEarliestInvites = nEarliestAcceptedInvites(
					idsToInvitesForUserStruct, minNumInvites, inviteIdsTheRest);

			//redeem the nEarliestInvites
			int num = UpdateUtils.get().updateRedeemUserFacebookInviteForSlot(
					curTime, nEarliestInvites);
			log.info("num saved: {}", num);

			//using goalLvl stored in the invite, in the case of
			//duplicate events being sent as mentioned above
			nuFbInviteLevel = nEarliestInvites.get(0).getUserStructFbLvl();

			if (num != minNumInvites) {
				log.error("expected updated={} \t actual updated={}",
						minNumInvites, num);
				return false;
			}

			log.info("inviteIdsTheRest: {}", inviteIdsTheRest);
			//delete all the remaining invites
			int numExtraInvites = inviteIdsTheRest.size();
			if (numExtraInvites > 0) {
				log.info("numExtraInvites={} \t invitesToDelete={}",
						numExtraInvites, inviteIdsTheRest);
				num = DeleteUtils.get().deleteUserFacebookInvitesForSlots(
						inviteIdsTheRest);
				log.info("num deleted={}", num);
			}
			success = true;
		}

		if (IncreaseSlotType.PURCHASE == increaseType) {
			int cost = -1 * gemCost;
			success = aUser.updateRelativeGemsNaive(cost, 0);

			if (!success) {
				log.error("problem with updating user monster inventory slots and diamonds");
				return false;
			}
			if (success && 0 != cost) {
				changeMap.put(miscMethods.gems, cost);
			}

			nuFbInviteLevel = sfu.getFbInviteStructLvl() + 1;
		}

		//increase the user structs fb invite lvl
		String userStructId = sfu.getId();
		//	  int fbInviteLevelChange = 1;

		if (!UpdateUtils.get().updateUserStructFbLevel(userStructId,
				nuFbInviteLevel)) {
			log.error("(won't continue processing) couldn't update fbInviteLevel for user struct="
					+ sfu);
			return false;
		}

		return success;
	}

	private List<UserFacebookInviteForSlot> nEarliestAcceptedInvites(
			Map<String, UserFacebookInviteForSlot> idsToInvitesForUserStruct,
			int n, List<String> inviteIdsTheRest) {
		//need to calculate based on only the accepted invites
		List<UserFacebookInviteForSlot> acceptedInvites = new ArrayList<UserFacebookInviteForSlot>();

		//idsToInvitesForUserStruct contains all the invites for this
		//userStruct (unredeemed, redeemed), so need to keep track
		//of the unaccepted invites to delete as well as extraneous
		//accepted invites. If for some reason, invites are redeemed,
		//no processing should really be done, but userStruct fb_lvl
		//is the only thing user gets, so absolute fb_lvl update is the solution
		//(use the goal fb_lvl in the invite as the value)
		for (UserFacebookInviteForSlot ufifs : idsToInvitesForUserStruct
				.values()) {
			if (null != ufifs.getTimeAccepted()) {
				acceptedInvites.add(ufifs);
			} else if (null != ufifs.getTimeRedeemed()) {
				log.error("user redeeming already redeemed invite. invites={}",
						idsToInvitesForUserStruct);

			} else {
				log.info(
						"curInvite has no accept time {}. will be deleted after redeeming invites",
						ufifs);
				inviteIdsTheRest.add(ufifs.getId());
			}
		}

		log.info("acceptedInvites={} \t allInvites={}", acceptedInvites,
				idsToInvitesForUserStruct);

		List<UserFacebookInviteForSlot> earliestAcceptedInvites = new ArrayList<UserFacebookInviteForSlot>(
				acceptedInvites);
		orderUserFacebookAcceptedInvitesForSlots(earliestAcceptedInvites);

		if (earliestAcceptedInvites.size() > n) {
			int amount = earliestAcceptedInvites.size();

			//want to keep track of the remaining accepted invites after the first n
			for (UserFacebookInviteForSlot invite : earliestAcceptedInvites
					.subList(n, amount)) {
				String id = invite.getId();
				inviteIdsTheRest.add(id);
			}

			//get first n invites
			return earliestAcceptedInvites.subList(0, n);
		} else {
			//num invites guaranteed to not be less than n because of checkLegit()
			return earliestAcceptedInvites;
		}
	}

	private void orderUserFacebookAcceptedInvitesForSlots(
			List<UserFacebookInviteForSlot> invites) {

		Collections.sort(invites, new Comparator<UserFacebookInviteForSlot>() {
			@Override
			public int compare(UserFacebookInviteForSlot lhs,
					UserFacebookInviteForSlot rhs) {
				//sorting by accept time, which should not be null
				Date lhsDate = lhs.getTimeAccepted();
				Date rhsDate = rhs.getTimeAccepted();

				if (null == lhsDate && null == rhsDate)
					return 0;
				else if (null == lhsDate)
					return -1;
				else if (null == rhsDate)
					return 1;
				else if (lhsDate.getTime() < rhsDate.getTime())
					return -1;
				else if (lhsDate.getTime() == rhsDate.getTime())
					return 0;
				else
					return 1;
			}
		});
	}

	private void writeToUserCurrencyHistory(User aUser, StructureForUser sfu,
			IncreaseSlotType increaseType, Timestamp curTime,
			Map<String, Integer> changeMap, int previousGems) {
		if (changeMap.isEmpty()) {
			return;
		}

		String userId = aUser.getId();
		Map<String, Integer> previousCurrencyMap = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
		Map<String, String> changeReasonsMap = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = miscMethods.gems;
		String reasonForChange = ControllerConstants.UCHRFC__INCREASE_MONSTER_INVENTORY;

		StringBuilder sb = new StringBuilder();
		sb.append("increaseType=");
		sb.append(increaseType.name());
		sb.append(" prevFbInviteStructLvl=");
		sb.append(sfu.getFbInviteStructLvl());
		String details = sb.toString();

		previousCurrencyMap.put(gems, previousGems);
		currentCurrencyMap.put(gems, aUser.getGems());
		changeReasonsMap.put(gems, reasonForChange);
		detailsMap.put(gems, details);

		miscMethods.writeToUserCurrencyOneUser(userId, curTime, changeMap,
				previousCurrencyMap, currentCurrencyMap, changeReasonsMap,
				detailsMap);

	}

	//after user buys slots, delete all accepted and unaccepted invites for slots
	private void deleteInvitesForSlotsAfterPurchase(String userId,
			Map<String, Integer> money) {
		if (money.isEmpty()) {
			return;
		}

		int num = DeleteUtils.get().deleteUnredeemedUserFacebookInvitesForUser(
				userId);
		log.info("num invites deleted after buying slot. userId=" + userId
				+ " numDeleted=" + num);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
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

	public StructureForUserRetrieveUtils2 getUserStructRetrieveUtils() {
		return userStructRetrieveUtils;
	}

	public void setUserStructRetrieveUtils(
			StructureForUserRetrieveUtils2 userStructRetrieveUtils) {
		this.userStructRetrieveUtils = userStructRetrieveUtils;
	}

}
