
package com.lvl6.server.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.DeleteGiftRequestEvent;
import com.lvl6.events.response.DeleteGiftResponseEvent;
import com.lvl6.proto.EventRewardProto.DeleteGiftRequestProto;
import com.lvl6.proto.EventRewardProto.DeleteGiftResponseProto;
import com.lvl6.proto.EventRewardProto.DeleteGiftResponseProto.DeleteGiftStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.DeleteGiftAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component
@DependsOn("gameServer")
public class DeleteGiftController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

//	@Autowired
//	protected UserRetrieveUtils2 userRetrieveUtils;

//	@Autowired
//	protected GiftForUserRetrieveUtils clanGiftForUserRetrieveUtils;

//	@Autowired
//	protected RewardRetrieveUtils rewardRetrieveUtils;

//	@Autowired
//	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

//	@Autowired
//	protected MonsterStuffUtils monsterStuffUtils;

//	@Autowired
//	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

//	@Autowired
//	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;

//	@Autowired
//	protected UpdateUtil updateUtil;

	public DeleteGiftController() {
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new DeleteGiftRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_DELETE_GIFT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses){
		DeleteGiftRequestProto reqProto = ((DeleteGiftRequestEvent) event)
				.getDeleteGiftRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<UserGiftProto> listOfGiftProtos = reqProto.getExpiredGiftsList();

		//all positive numbers, server will change to negative

		//set some values to send to the client (the response proto)
		DeleteGiftResponseProto.Builder resBuilder = DeleteGiftResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(DeleteGiftStatus.FAIL_OTHER); //default

		Set<String> giftForUserIds = new HashSet<String>();
		Set<String> giftForTangoUserGfuIds = new HashSet<String>();
		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			for (UserGiftProto ugp : listOfGiftProtos) {
				String gfuId = ugp.getUgId();
				UUID.fromString(gfuId);

				giftForUserIds.add(gfuId);

				switch (ugp.getGiftType()) {
				case TANGO_GIFT:
					giftForTangoUserGfuIds.add(gfuId);
					break;
				default:
					log.error("GiftType unsupported: {}", ugp.getGiftType());
					break;
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s or userGiftData=%s",
					userId, listOfGiftProtos),
				e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(DeleteGiftStatus.FAIL_OTHER);
			DeleteGiftResponseEvent resEvent = new DeleteGiftResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setDeleteGiftResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			DeleteGiftAction dga = new DeleteGiftAction(userId, giftForUserIds,
					giftForTangoUserGfuIds, deleteUtil);

			dga.execute(resBuilder);

			DeleteGiftResponseEvent resEvent = new DeleteGiftResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setDeleteGiftResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in DeleteGiftController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(DeleteGiftStatus.FAIL_OTHER);
				DeleteGiftResponseEvent resEvent = new DeleteGiftResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setDeleteGiftResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in DeleteGiftController processEvent",
						e);
			}
		} finally {
			locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
