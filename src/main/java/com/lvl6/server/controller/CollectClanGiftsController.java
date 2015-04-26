
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
import com.lvl6.events.request.CollectClanGiftsRequestEvent;
import com.lvl6.events.response.CollectClanGiftsResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ClanGiftForUser;
import com.lvl6.proto.RewardsProto.ClanGiftProto;
import com.lvl6.proto.RewardsProto.UserClanGiftProto;
import com.lvl6.proto.EventClanProto.CollectClanGiftsRequestProto;
import com.lvl6.proto.EventClanProto.CollectClanGiftsResponseProto;
import com.lvl6.proto.EventClanProto.CollectClanGiftsResponseProto.CollectClanGiftsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanGiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.CollectClanGiftsAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class CollectClanGiftsController extends EventController {

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

	public CollectClanGiftsController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CollectClanGiftsRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COLLECT_CLAN_GIFTS_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event, ToClientEvents responses) throws Exception {
		CollectClanGiftsRequestProto reqProto = ((CollectClanGiftsRequestEvent) event)
				.getCollectClanGiftsRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<UserClanGiftProto> listOfClanGiftProtos = reqProto.getUserClanGiftList();
		List<ClanGiftForUser> listOfClanGIfts = convertProtos(listOfClanGiftProtos);

		//all positive numbers, server will change to negative

		//set some values to send to the client (the response proto)
		CollectClanGiftsResponseProto.Builder resBuilder = CollectClanGiftsResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(CollectClanGiftsStatus.FAIL_OTHER); //default

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
			resBuilder.setStatus(CollectClanGiftsStatus.FAIL_OTHER);
			CollectClanGiftsResponseEvent resEvent = new CollectClanGiftsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setCollectClanGiftsResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			CollectClanGiftsAction uusa = new CollectClanGiftsAction(userId, userRetrieveUtils,
					clanGiftForUserRetrieveUtils, rewardRetrieveUtils, itemForUserRetrieveUtil,
					monsterStuffUtils, monsterLevelInfoRetrieveUtils, insertUtil, updateUtil,
					deleteUtil, listOfClanGIfts, createInfoProtoUtils);

			uusa.execute(resBuilder);

			CollectClanGiftsResponseEvent resEvent = new CollectClanGiftsResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resBuilder.setReward(uusa.getUrp());

			resEvent.setCollectClanGiftsResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (CollectClanGiftsStatus.SUCCESS.equals(resBuilder.getStatus())) {
				
				log.info("reward proto for collect: " + uusa.getUrp());

				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								uusa.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

			}

		} catch (Exception e) {
			log.error("exception in CollectClanGiftsController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(CollectClanGiftsStatus.FAIL_OTHER);
				CollectClanGiftsResponseEvent resEvent = new CollectClanGiftsResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setCollectClanGiftsResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in CollectClanGiftsController processEvent",
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
