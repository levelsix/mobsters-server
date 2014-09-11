package com.lvl6.server.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.DevRequestEvent;
import com.lvl6.events.response.DevResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.Globals;
import com.lvl6.proto.DevProto.DevRequest;
import com.lvl6.proto.EventDevProto.DevRequestProto;
import com.lvl6.proto.EventDevProto.DevResponseProto;
import com.lvl6.proto.EventDevProto.DevResponseProto.DevStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;

@Component @DependsOn("gameServer") public class DevController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public DevController() {
		numAllocatedThreads = 1;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new DevRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_DEV_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		DevRequestProto reqProto = ((DevRequestEvent)event).getDevRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		DevRequest request = reqProto.getDevRequest();
		int num = reqProto.getNum();

		DevResponseProto.Builder resBuilder = DevResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(DevStatus.SUCCESS);

		//    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
		try {
			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
			//TODO: Consider writing currency history and other history
			
			log.info(String.format(
				"CHEATER DETECTED!!!! %s", aUser));
			if (aUser.isAdmin() && Globals.IS_SANDBOX()) {
				cheat(userId, request, num, resBuilder, aUser);
			} else {
				log.error(String.format(
					"azzhole tried cheating: user=",
					aUser));
				resBuilder.setStatus(DevStatus.FAIL_OTHER);
			}


			DevResponseProto resProto = resBuilder.build();
			DevResponseEvent resEvent = new DevResponseEvent(senderProto.getUserId());
			resEvent.setDevResponseProto(resProto);
			server.writeEvent(resEvent);

			UpdateClientUserResponseEvent resEventUpdate = MiscMethods
				.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null);
			resEventUpdate.setTag(event.getTag());
			server.writeEvent(resEventUpdate);

		} catch (Exception e) {
			log.error("exception in DevController processEvent", e);
			try {
				resBuilder.setStatus(DevStatus.FAIL_OTHER);
				DevResponseEvent resEvent = new DevResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setDevResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in DevController processEvent", e);
			}

		} finally {
			//      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName()); 
		}
	}

	private void cheat(
		int userId,
		DevRequest request,
		int num,
		DevResponseProto.Builder resBuilder,
		User aUser )
	{
		switch (request) {
			case RESET_ACCOUNT:
				log.info(String.format(
					"resetting user=%s", aUser));
				aUser.updateResetAccount();
				break;

			case GET_MONZTER:
				log.info(String.format(
					"giving user=%s monsterId=%d", 
					aUser, num));
//				Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(num);
//				Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
//				monsterIdToNumPieces.put(num, monzter.getNumPuzzlePieces());

				Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity =
					new HashMap<Integer, Map<Integer, Integer>>();
					monsterIdToLvlToQuantity.put(num, Collections.singletonMap(1, 1));
					
				
				String mfusop = "cheater, cheater, pumpkin eater";
				List<FullUserMonsterProto> reward = MonsterStuffUtils
					.updateUserMonsters(userId, null, monsterIdToLvlToQuantity, mfusop, new Date());
				resBuilder.setFump(reward.get(0));
				break;

			case F_B_GET_CASH:
				log.info(String.format(
					"giving user=%s cash=%d", 
					aUser, num));
				aUser.updateRelativeCashAndOilAndGems(num, 0, 0);
				break;

			case F_B_GET_OIL:
				log.info(String.format(
					"giving user=%s oil=%d", 
					aUser, num));
				aUser.updateRelativeCashAndOilAndGems(0, num, 0);
				break;

			case F_B_GET_GEMS:
				log.info(String.format(
					"giving user=%s gems=%d", 
					aUser, num));
				aUser.updateRelativeCashAndOilAndGems(0, 0, num);
				break;

			case F_B_GET_CASH_OIL_GEMS:
				log.info(String.format(
					"giving user=%s cash, gems, oil=%d", 
					aUser, num));
				aUser.updateRelativeCashAndOilAndGems(num, num, num);
				break;
		}
	}

}
