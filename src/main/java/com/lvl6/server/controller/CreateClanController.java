package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CreateClanRequestEvent;
import com.lvl6.events.response.CreateClanResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.misc.Notification;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.CreateClanRequestProto;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.CreateClanStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class CreateClanController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected ClanSearch clanSearch;

	public CreateClanController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CreateClanRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_CREATE_CLAN_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		CreateClanRequestProto reqProto = ((CreateClanRequestEvent)event).getCreateClanRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String clanName = reqProto.getName();
		String tag = reqProto.getTag();
		boolean requestToJoinRequired = reqProto.getRequestToJoinClanRequired();
		String description = reqProto.getDescription();
		int clanIconId = reqProto.getClanIconId();
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		//most likely never positive
		int cashChange = reqProto.getCashChange();

		CreateClanResponseProto.Builder resBuilder = CreateClanResponseProto.newBuilder();
		resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s",
				userId), e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			CreateClanResponseEvent resEvent = new CreateClanResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setCreateClanResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());
			Timestamp createTime = new Timestamp(new Date().getTime());

			boolean legitCreate = checkLegitCreate(resBuilder, user, clanName, tag, gemsSpent,
				cashChange);

			boolean success = false;
			Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			Clan createdClan = new Clan();
			if (legitCreate) {
				previousCurrency.put(MiscMethods.gems, user.getGems());
				previousCurrency.put(MiscMethods.cash, user.getCash());
				success = writeChangesToDB(user, clanName, tag, requestToJoinRequired, description,
					clanIconId, createTime, createdClan, gemsSpent, cashChange, currencyChange);
			}

			if (success) {
				resBuilder.setClanInfo(CreateInfoProtoUtils.createMinimumClanProtoFromClan(createdClan));
				resBuilder.setStatus(CreateClanStatus.SUCCESS);
				updateClanCache(createdClan);
			}

			CreateClanResponseEvent resEvent = new CreateClanResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setCreateClanResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
					.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, createdClan);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				sendGeneralNotification(user.getName(), clanName);

				writeToUserCurrencyHistory(user, createdClan, createTime,
					currencyChange, previousCurrency);
			}
		} catch (Exception e) {
			log.error("exception in CreateClan processEvent", e);
			try {
				resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
				CreateClanResponseEvent resEvent = new CreateClanResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setCreateClanResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in CreateClan processEvent", e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegitCreate(Builder resBuilder, User user, String clanName,
		String tag, int gemsSpent, int cashChange) {
		if (user == null || clanName == null || clanName.isEmpty() ||
			tag == null || tag.isEmpty()) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			log.error("user is null");
			return false;      
		}
		//    if (user.getCash() < ControllerConstants.CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN) {
		//      resBuilder.setStatus(CreateClanStatus.FAIL_NOT_ENOUGH_CASH);
		//      log.error("user only has " + user.getCash() + ", needs " + ControllerConstants.CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN);
		//      return false;
		//    }
		if (clanName.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			log.error(String.format(
				"clan name %s is more than %s characters",
				clanName, ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME));
			return false;
		}

		if (tag.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG) {
			resBuilder.setStatus(CreateClanStatus.FAIL_INVALID_TAG_LENGTH);
			log.error(String.format(
				"clan tag %s is more than %s characters.",
				tag, ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG));
			return false;
		}

		if (null != user.getClanId() && !user.getClanId().isEmpty()) {
			resBuilder.setStatus(CreateClanStatus.FAIL_ALREADY_IN_CLAN);
			log.error(String.format(
				"user already in clan with id %s", user.getClanId()));
			return false;
		}
		Clan clan = clanRetrieveUtil.getClanWithNameOrTag(clanName, tag);
		if (clan != null) {
			if (clan.getName().equalsIgnoreCase(clanName)) {
				resBuilder.setStatus(CreateClanStatus.FAIL_NAME_TAKEN);
				log.error("clan name already taken with name " + clanName);
				return false;
			}
			if (clan.getTag().equalsIgnoreCase(tag)) {
				resBuilder.setStatus(CreateClanStatus.FAIL_TAG_TAKEN);
				log.error("clan tag already taken with tag " + tag);
				return false;
			}
		}

		//CHECK MONEY
		if (0 == gemsSpent) {
			if (!hasEnoughCash(resBuilder, user, cashChange)) {
				return false;
			}
		}

		if (!hasEnoughGems(resBuilder, user, gemsSpent)) {
			return false;
		}

		resBuilder.setStatus(CreateClanStatus.SUCCESS);
		return true;
	}

	private boolean hasEnoughCash(Builder resBuilder, User u, int cashSpent) {
		//if user's aggregate cash is < cost, don't allow transaction
		int userCash = u.getCash();

		//since negative resourceChange means charge, then negative of that is
		//the cost. If resourceChange is positive, meaning refund, user will always
		//have more than a negative amount
		int cashRequired = -1 * cashSpent;
		if (userCash < cashRequired) {
			log.error("user error: user does not have enough cash. userCash=" + userCash +
				"\t cashSpent=" + cashSpent);
			resBuilder.setStatus(CreateClanStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
				"\t gemsSpent=" + gemsSpent);
			resBuilder.setStatus(CreateClanStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		return true;
	}

	private void sendGeneralNotification (String userName, String clanName) {
		Notification createClanNotification = new Notification ();
		createClanNotification.setAsClanCreated(userName, clanName);

		MiscMethods.writeGlobalNotification(createClanNotification, server);
	}

	private boolean writeChangesToDB(User user, String name, String tag,
		boolean requestToJoinRequired, String description, int clanIconId,
		Timestamp createTime, Clan createdClan, int gemsSpent, int cashChange,
		Map<String, Integer> money) {

		//just in case user doesn't input one, set default description
		if (null == description || description.isEmpty()) {
			description = String.format( "Welcome to %s!", name );
		}

		String clanId = InsertUtils.get().insertClan(name, createTime, description,
			tag, requestToJoinRequired, clanIconId);
		if (null == clanId || clanId.isEmpty()) {
			return false;
		} else {
			setClan(createdClan, clanId, name, createTime, description, tag,
				requestToJoinRequired, clanIconId);
			log.info(String.format("clan=%s", createdClan));
		}

		int gemChange = -1 * Math.abs(gemsSpent);
		cashChange = -1 * Math.abs(cashChange);
		if (!user.updateGemsCashClan(gemChange, cashChange, clanId)) {
			log.error(String.format(
				"problem decreasing user gems, cash for creating clan. gemChange=%s, cashChange=%s",
				gemChange, cashChange));
		} else {
			if (0 != gemsSpent) {
				money.put(MiscMethods.gems, gemsSpent);
			}
			if (0 != cashChange) {
				money.put(MiscMethods.cash, cashChange);
			}
		}

		if (!InsertUtils.get().insertUserClan(user.getId(), clanId,
			UserClanStatus.LEADER.name(), createTime)) {
			log.error(String.format(
				"problem with inserting user clan data for user %s, and clan id %s",
				user, clanId));
		}
		DeleteUtils.get().deleteUserClansForUserExceptSpecificClan(user.getId(), clanId);

		return true;
	}

	private void setClan(Clan createdClan, String clanId, String name,
		Timestamp createTime, String description, String tag,
		boolean requestToJoinRequired, int clanIconId)
	{
		createdClan.setId(clanId);
		createdClan.setName(name);
		createdClan.setCreateTime(createTime);
		createdClan.setDescription(description);
		createdClan.setTag(tag);
		createdClan.setRequestToJoinRequired(requestToJoinRequired);
		createdClan.setClanIconId(clanIconId);
	}

	private void updateClanCache(Clan clan)
	{
		String clanId = clan.getId();
		//need to account for this user creating clan
		int clanSize = 1;
		Date lastChatTime = ControllerConstants.INCEPTION_DATE;

		clanSearch.updateClanSearchRank(clanId, clanSize, lastChatTime);
	}
	
	private void writeToUserCurrencyHistory(User aUser, Clan clan, Timestamp createTime,
		Map<String, Integer> currencyChange, Map<String, Integer> previousCurrency) {
		if (currencyChange.isEmpty()) {
			return;
		}

		String reason = ControllerConstants.UCHRFC__CREATE_CLAN;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("clanId=");
		detailsSb.append(clan.getId());
		detailsSb.append(" clanName=");
		detailsSb.append(clan.getName());
		String details = detailsSb.toString();

		String userId = aUser.getId();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MiscMethods.gems;
		String cash = MiscMethods.cash;

		currentCurrency.put(gems, aUser.getGems());
		currentCurrency.put(cash, aUser.getCash());
		reasonsForChanges.put(gems, reason);
		reasonsForChanges.put(cash, reason);
		detailsMap.put(gems, details);
		detailsMap.put(cash, details);

		MiscMethods.writeToUserCurrencyOneUser(userId, createTime, currencyChange, 
			previousCurrency, currentCurrency, reasonsForChanges, detailsMap);

	}

	public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil()
	{
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil( ClanRetrieveUtils2 clanRetrieveUtil )
	{
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public ClanSearch getClanSearch()
	{
		return clanSearch;
	}

	public void setClanSearch( ClanSearch clanSearch )
	{
		this.clanSearch = clanSearch;
	}

}
