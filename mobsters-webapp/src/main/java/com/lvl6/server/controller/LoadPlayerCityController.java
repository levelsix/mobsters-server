package com.lvl6.server.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LoadPlayerCityRequestEvent;
import com.lvl6.events.response.LoadPlayerCityResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventCityProto.LoadPlayerCityRequestProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto;
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.UserObstacleProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.ObstacleForUserRetrieveUtil2;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component

public class LoadPlayerCityController extends EventController {

	private static Logger log = LoggerFactory.getLogger(LoadPlayerCityController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected StructureForUserRetrieveUtils2 userStructRetrieveUtils;

	@Autowired
	protected ObstacleForUserRetrieveUtil2 obstacleForUserRetrieveUtil;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	public LoadPlayerCityController() {
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new LoadPlayerCityRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_LOAD_PLAYER_CITY_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		LoadPlayerCityRequestProto reqProto = ((LoadPlayerCityRequestEvent) event)
				.getLoadPlayerCityRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String cityOwnerId = reqProto.getCityOwnerUuid();

		LoadPlayerCityResponseProto.Builder resBuilder = LoadPlayerCityResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

		resBuilder.setStatus(ResponseStatus.SUCCESS);

		UUID userUuid = null;
		UUID cityOwnerUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			cityOwnerUuid = UUID.fromString(cityOwnerId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, cityOwnerId=%s", userId,
					cityOwnerId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			LoadPlayerCityResponseEvent resEvent = new LoadPlayerCityResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		//I guess in case someone attacks this guy while loading the city, want
		//both people to have one consistent view
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			User owner = getUserRetrieveUtils().getUserById(cityOwnerId);

			List<StructureForUser> userStructs = getUserStructRetrieveUtils()
					.getUserStructsForUser(cityOwnerId);
			setResponseUserStructs(resBuilder, userStructs);
			setObstacleStuff(resBuilder, cityOwnerId);

			//      List<ExpansionPurchaseForUser> userCityExpansionDataList = geExpansionPurchaseForUserRetrieveUtils.getUserCityExpansionDatasForUserId(senderProto.getUserUuid());
			//      List<UserCityExpansionDataProto> userCityExpansionDataProtoList = new ArrayList<UserCityExpansionDataProto>();
			//      if (userCityExpansionDataList != null) {
			//    	for(ExpansionPurchaseForUser uced : userCityExpansionDataList) {
			//    		userCityExpansionDataProtoList.add(CreateInfoProtoUtils.createUserCityExpansionDataProtoFromUserCityExpansionData(uced));
			//    	}
			//        resBuilder.addAllUserCityExpansionDataProtoList(userCityExpansionDataProtoList);
			//      }

			if (owner == null) {
				log.error(String.format("owner is null for ownerId=%s",
						cityOwnerId));
			} else if (cityOwnerId.equals(userId)) {
				resBuilder.setCityOwner(senderProto);
			} else {
				String clanId = owner.getClanId();
				Clan clan = null;
				if (clanId != null) {
					clan = getClanRetrieveUtils().getClanWithId(clanId);
				}
				resBuilder.setCityOwner(createInfoProtoUtils
						.createMinimumUserProtoFromUserAndClan(owner, clan));
			}

			LoadPlayerCityResponseEvent resEvent = new LoadPlayerCityResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in LoadPlayerCity processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				LoadPlayerCityResponseEvent resEvent = new LoadPlayerCityResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SubmitMonsterEnhancementController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private void setResponseUserStructs(Builder resBuilder,
			List<StructureForUser> userStructs) {
		if (userStructs != null) {
			for (StructureForUser userStruct : userStructs) {
				resBuilder
						.addOwnerNormStructs(createInfoProtoUtils
								.createFullUserStructureProtoFromUserstruct(userStruct));
			}
		} else {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			log.error("user structs found for user is null");
		}
	}

	private void setObstacleStuff(Builder resBuilder, String userId) {
		List<ObstacleForUser> ofuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);

		if (null == ofuList) {
			return;
		}

		for (ObstacleForUser ofu : ofuList) {
			UserObstacleProto uop = createInfoProtoUtils
					.createUserObstacleProto(ofu);
			resBuilder.addObstacles(uop);
		}

	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public StructureForUserRetrieveUtils2 getUserStructRetrieveUtils() {
		return userStructRetrieveUtils;
	}

	public void setUserStructRetrieveUtils(
			StructureForUserRetrieveUtils2 userStructRetrieveUtils) {
		this.userStructRetrieveUtils = userStructRetrieveUtils;
	}

	public ObstacleForUserRetrieveUtil2 getObstacleForUserRetrieveUtil() {
		return obstacleForUserRetrieveUtil;
	}

	public void setObstacleForUserRetrieveUtil(
			ObstacleForUserRetrieveUtil2 obstacleForUserRetrieveUtil) {
		this.obstacleForUserRetrieveUtil = obstacleForUserRetrieveUtil;
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

}
