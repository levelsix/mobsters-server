
package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ClearExpiredClanGiftsRequestEvent;
import com.lvl6.events.response.ClearExpiredClanGiftsResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ClanGiftForUser;
import com.lvl6.proto.RewardsProto.ClanGiftProto;
import com.lvl6.proto.RewardsProto.UserClanGiftProto;
import com.lvl6.proto.EventClanProto.ClearExpiredClanGiftsRequestProto;
import com.lvl6.proto.EventClanProto.ClearExpiredClanGiftsResponseProto;
import com.lvl6.proto.EventClanProto.ClearExpiredClanGiftsResponseProto.ClearExpiredClanGiftsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanGiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.ClearExpiredClanGiftsAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class ClearExpiredClanGiftsController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	public ClearExpiredClanGiftsController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ClearExpiredClanGiftsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_CLEAR_EXPIRED_CLAN_GIFTS_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		ClearExpiredClanGiftsRequestProto reqProto = ((ClearExpiredClanGiftsRequestEvent) event)
				.getClearExpiredClanGiftsRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<UserClanGiftProto> listOfClanGiftProtos = reqProto.getExpiredGiftsList();
		List<ClanGiftForUser> listOfClanGifts = convertProtos(listOfClanGiftProtos);

		//all positive numbers, server will change to negative

		//set some values to send to the client (the response proto)
		ClearExpiredClanGiftsResponseProto.Builder resBuilder = ClearExpiredClanGiftsResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ClearExpiredClanGiftsStatus.FAIL_OTHER); //default

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
			resBuilder.setStatus(ClearExpiredClanGiftsStatus.FAIL_OTHER);
			ClearExpiredClanGiftsResponseEvent resEvent = new ClearExpiredClanGiftsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setClearExpiredClanGiftsResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			ClearExpiredClanGiftsAction uusa = new ClearExpiredClanGiftsAction(userId, userRetrieveUtils,
					deleteUtil, listOfClanGifts);

			uusa.execute(resBuilder);

			ClearExpiredClanGiftsResponseEvent resEvent = new ClearExpiredClanGiftsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setClearExpiredClanGiftsResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (ClearExpiredClanGiftsStatus.SUCCESS.equals(resBuilder.getStatus())) {

				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								uusa.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

			}

		} catch (Exception e) {
			log.error("exception in ClearExpiredClanGiftsController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ClearExpiredClanGiftsStatus.FAIL_OTHER);
				ClearExpiredClanGiftsResponseEvent resEvent = new ClearExpiredClanGiftsResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setClearExpiredClanGiftsResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in ClearExpiredClanGiftsController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	public List<ClanGiftForUser> convertProtos(List<UserClanGiftProto> protoList) {
		List<ClanGiftForUser> returnList = new ArrayList<ClanGiftForUser>();
		for(UserClanGiftProto ucgp : protoList) {
			ClanGiftForUser cgfu = new ClanGiftForUser();
			cgfu.setId(ucgp.getUserClanGiftId());

			if(ucgp.getGifterUser() != null) {
				cgfu.setGifterUserId(ucgp.getGifterUser().getUserUuid());
			}

			ClanGiftProto cgp = ucgp.getClanGift();
			cgfu.setClanGiftId(cgp.getClanGiftId());
			cgfu.setReceiverUserId(ucgp.getReceiverUserId());
			cgfu.setRewardId(ucgp.getReward().getRewardId());
			cgfu.setTimeReceived(new Date(ucgp.getTimeReceived()));

			returnList.add(cgfu);
		}

		return returnList;
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

}
