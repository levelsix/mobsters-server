package com.lvl6.server.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveMiniEventRequestEvent;
import com.lvl6.events.response.RetrieveMiniEventResponseEvent;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventRequestProto;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.RetrieveMiniEventStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.server.controller.actionobjects.RetrieveMiniEventAction;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class RetrieveMiniEventController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public RetrieveMiniEventController() {
		numAllocatedThreads = 1;
	}

	@Autowired
	ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new RetrieveMiniEventRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REMOVE_USER_ITEM_USED_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RetrieveMiniEventRequestProto reqProto = ((RetrieveMiniEventRequestEvent) event)
				.getRetrieveMiniEventRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userItemUsedIdList = reqProto.getUserItemUsedUuidList();

		RetrieveMiniEventResponseProto.Builder resBuilder = RetrieveMiniEventResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(RetrieveMiniEventStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			StringUtils.convertToUUID(userItemUsedIdList);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userItemUsedIdList=%s",
					userId, userItemUsedIdList), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(RetrieveMiniEventStatus.FAIL_OTHER);
			RetrieveMiniEventResponseEvent resEvent = new RetrieveMiniEventResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveMiniEventResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		server.lockPlayer(senderProto.getUserUuid(), this.getClass()
				.getSimpleName());
		try {

			RetrieveMiniEventAction tifsua = new RetrieveMiniEventAction(
					userId, userItemUsedIdList, DeleteUtils.get());

			tifsua.execute(resBuilder);

			RetrieveMiniEventResponseProto resProto = resBuilder.build();
			RetrieveMiniEventResponseEvent resEvent = new RetrieveMiniEventResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setRetrieveMiniEventResponseProto(resProto);
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in RetrieveMiniEventController processEvent",
					e);
			try {
				resBuilder.setStatus(RetrieveMiniEventStatus.FAIL_OTHER);
				RetrieveMiniEventResponseEvent resEvent = new RetrieveMiniEventResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setRetrieveMiniEventResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RetrieveMiniEventController processEvent",
						e);
			}

		} finally {
			server.unlockPlayer(senderProto.getUserUuid(), this.getClass()
					.getSimpleName());
		}
	}

}
