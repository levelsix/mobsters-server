package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ChangeClanSettingsRequestEvent;
import com.lvl6.events.response.ChangeClanSettingsResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanIcon;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsRequestProto;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto.Builder;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto.ChangeClanSettingsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ClanIconRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.ChangeClanSettingsAction;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
@DependsOn("gameServer")
public class ChangeClanSettingsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtil;

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtil;

	@Autowired
	protected ClanIconRetrieveUtils clanIconRetrieveUtils;

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;

	@Autowired
	protected ClanSearch clanSearch;
	
	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected UpdateUtil updateUtil;
	
	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils;
	
	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtils;
		

	public ChangeClanSettingsController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ChangeClanSettingsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_CHANGE_CLAN_SETTINGS_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		ChangeClanSettingsRequestProto reqProto = ((ChangeClanSettingsRequestEvent) event)
				.getChangeClanSettingsRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		boolean isChangeDescription = reqProto.getIsChangeDescription();
		String description = reqProto.getDescriptionNow();
		boolean isChangeJoinType = reqProto.getIsChangeJoinType();
		boolean requestToJoinRequired = reqProto.getRequestToJoinRequired();
		boolean isChangeIcon = reqProto.getIsChangeIcon();
		int iconId = reqProto.getIconId();

		ChangeClanSettingsResponseProto.Builder resBuilder = ChangeClanSettingsResponseProto
				.newBuilder();
		resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
		resBuilder.setSender(senderProto);

		String clanId = "";
		if (senderProto.hasClan() && null != senderProto.getClan()) {
			clanId = senderProto.getClan().getClanUuid();
		}
		UUID userUuid = null;
		UUID clanUuid = null;

		boolean invalidUuids = true;
		if (!clanId.isEmpty()) {
			try {
				userUuid = UUID.fromString(userId);
				clanUuid = UUID.fromString(clanId);
				invalidUuids = false;
			} catch (Exception e) {
				log.error(String.format(
						"UUID error. incorrect userId=%s, clanId=%s", userId,
						clanId), e);
			}
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
			ChangeClanSettingsResponseEvent resEvent = new ChangeClanSettingsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setChangeClanSettingsResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		boolean lockedClan = getLocker().lockClan(clanUuid);

		try {
//			User user = userRetrieveUtil.getUserById(senderProto.getUserUuid());
//			Clan clan = clanRetrieveUtil.getClanWithId(user.getClanId());
//
//			log.info("about to check legit");
//			boolean legitChange = checkLegitChange(resBuilder, lockedClan,
//					userId, user, clanId, clan);
//
//			if (legitChange) {
//				//clan will be modified
//				writeChangesToDB(resBuilder, clanId, clan, isChangeDescription,
//						description, isChangeJoinType, requestToJoinRequired,
//						isChangeIcon, iconId);
//				
//			}
			
			ChangeClanSettingsAction ccsa = new ChangeClanSettingsAction(userId, isChangeDescription,
					description, isChangeJoinType, requestToJoinRequired, isChangeIcon, iconId, lockedClan,
					userRetrieveUtils, updateUtil, miscMethods, clanRetrieveUtils, userClanRetrieveUtils,
					clanIconRetrieveUtils);
			ccsa.execute(resBuilder);
			
			List<Integer> clanSizeContainer = new ArrayList<Integer>();

			if(ChangeClanSettingsStatus.SUCCESS.equals(resBuilder.getStatus())) {
				setResponseBuilderStuff(resBuilder, clanId, ccsa.getClan(),
						clanSizeContainer);
			}

			ChangeClanSettingsResponseEvent resEvent = new ChangeClanSettingsResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setChangeClanSettingsResponseProto(resBuilder.build());

			//if not successful only write to user
			if (!ChangeClanSettingsStatus.SUCCESS
					.equals(resBuilder.getStatus())) {
				server.writeEvent(resEvent);

			} else {
				//only write to clan if successful 
				server.writeClanEvent(resEvent, ccsa.getClan().getId());

				updateClanCache(clanId, clanSizeContainer, isChangeJoinType,
						requestToJoinRequired);
			}

		} catch (Exception e) {
			log.error("exception in ChangeClanSettings processEvent", e);
			try {
				resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
				ChangeClanSettingsResponseEvent resEvent = new ChangeClanSettingsResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setChangeClanSettingsResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in ChangeClanSettings processEvent", e);
			}
		} finally {
			if (null != clanUuid && lockedClan) {
				getLocker().unlockClan(clanUuid);
			}
		}
	}

	private void setResponseBuilderStuff(Builder resBuilder, String clanId,
			Clan clan, List<Integer> clanSizeContainer) {
		List<String> clanIdList = Collections.singletonList(clanId);

		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		Map<String, Integer> clanIdToSize = userClanRetrieveUtil
				.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);

		resBuilder.setMinClan(createInfoProtoUtils
				.createMinimumClanProtoFromClan(clan));

		int size = clanIdToSize.get(clanId);
		resBuilder.setFullClan(createInfoProtoUtils
				.createFullClanProtoWithClanSize(clan, size));

		clanSizeContainer.add(size);
	}

	private void updateClanCache(String clanId,
			List<Integer> clanSizeContainer, boolean isChangeJoinType,
			boolean requestToJoinRequired) {
		if (!isChangeJoinType) {
			return;
		}

		Date lastChatTime = null;
		int clanSize = clanSizeContainer.get(0);
		if (requestToJoinRequired) {
			//since people can't join freely, this clan has a low rank 
			lastChatTime = ControllerConstants.INCEPTION_DATE;
			clanSize = ClanSearch.penalizedClanSize;
		} else {
			lastChatTime = clanChatPostRetrieveUtil.getLastChatPost(clanId);
		}

		clanSearch.updateClanSearchRank(clanId, clanSize, lastChatTime);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtil() {
		return clanRetrieveUtil;
	}

	public void setClanRetrieveUtil(ClanRetrieveUtils2 clanRetrieveUtil) {
		this.clanRetrieveUtil = clanRetrieveUtil;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtil() {
		return userClanRetrieveUtil;
	}

	public void setUserClanRetrieveUtil(
			UserClanRetrieveUtils2 userClanRetrieveUtil) {
		this.userClanRetrieveUtil = userClanRetrieveUtil;
	}

	public ClanIconRetrieveUtils getClanIconRetrieveUtils() {
		return clanIconRetrieveUtils;
	}

	public void setClanIconRetrieveUtils(
			ClanIconRetrieveUtils clanIconRetrieveUtils) {
		this.clanIconRetrieveUtils = clanIconRetrieveUtils;
	}

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtil() {
		return clanChatPostRetrieveUtil;
	}

	public void setClanChatPostRetrieveUtil(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil) {
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
	}

	public ClanSearch getClanSearch() {
		return clanSearch;
	}

	public void setClanSearch(ClanSearch clanSearch) {
		this.clanSearch = clanSearch;
	}

}
