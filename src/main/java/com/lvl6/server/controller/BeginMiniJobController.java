package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginMiniJobRequestEvent;
import com.lvl6.events.response.BeginMiniJobResponseEvent;
import com.lvl6.events.response.BeginObstacleRemovalResponseEvent;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobResponseProto;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobResponseProto.BeginMiniJobStatus;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto.BeginObstacleRemovalStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;


@Component
public class BeginMiniJobController extends EventController{

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
//	@Autowired
//	protected Locker locker;
	
	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	
	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	public BeginMiniJobController() {
		numAllocatedThreads = 4;
	}

	
	@Override
	public RequestEvent createRequestEvent() {
		return new BeginMiniJobRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BEGIN_MINI_JOB_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		BeginMiniJobRequestProto reqProto = ((BeginMiniJobRequestEvent)event).getBeginMiniJobRequestProto();
		
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		List<String> userMonsterIds = reqProto.getUserMonsterUuidsList();
		userMonsterIds = new ArrayList<String>(userMonsterIds); //gonna modify it
		
		String userMiniJobId = reqProto.getUserMiniJobUuid();
		
		BeginMiniJobResponseProto.Builder resBuilder = BeginMiniJobResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(BeginMiniJobStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = false;
		try {
			userUuid = UUID.fromString(userId);
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s",
				userId), e);
			invalidUuids = true;
		}
		
		//UUID checks
	    if (invalidUuids) {
	    	resBuilder.setStatus(BeginMiniJobStatus.FAIL_OTHER);
	      	BeginMiniJobResponseEvent resEvent = new BeginMiniJobResponseEvent(userId);
	      	resEvent.setTag(event.getTag());
	      	resEvent.setBeginMiniJobResponseProto(resBuilder.build());
	      	server.writeEvent(resEvent);
	    	return;
	    }
		
		//TODO: figure out if locking is needed
//		getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			
			boolean legit = checkLegit(resBuilder, userId, userMonsterIds,
					userMiniJobId);
			
			boolean success = false;
			
			if (legit) {
				success = writeChangesToDB(userId, userMiniJobId,
						userMonsterIds, clientTime);
			}
			
			if (success) {
				resBuilder.setStatus(BeginMiniJobStatus.SUCCESS);
			}
			
			BeginMiniJobResponseEvent resEvent = new BeginMiniJobResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setBeginMiniJobResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			
		} catch (Exception e) {
			log.error("exception in BeginMiniJobController processEvent", e);
			//don't let the client hang
      try {
      	resBuilder.setStatus(BeginMiniJobStatus.FAIL_OTHER);
      	BeginMiniJobResponseEvent resEvent = new BeginMiniJobResponseEvent(userId);
      	resEvent.setTag(event.getTag());
      	resEvent.setBeginMiniJobResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in BeginMiniJobController processEvent", e);
      }
//		} finally {
//			getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());      
		}
	}

	private boolean checkLegit(Builder resBuilder, String userId,
			List<String> userMonsterIds, String userMiniJobId) {
		
		//sanity check
		if (userMonsterIds.isEmpty() || null == userMiniJobId ||
			userMiniJobId.isEmpty())
		{
			log.error(String.format(
				"invalid userMonsterIds or userMiniJobId. userMonsterIds=%s userMiniJobId=%s",
				userMonsterIds, userMiniJobId));
			return false;
		}
		
		//keep only valid userMonsterIds
		Map<String, MonsterForUser> mfuIdsToUserMonsters = 
				monsterForUserRetrieveUtils
				.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
		//another sanity check
		if (userMonsterIds.size() != mfuIdsToUserMonsters.size()) {
			log.warn("some userMonsterIds client sent are invalid." +
					" Keeping valid ones. userMonsterIds=" + userMonsterIds +
					" mfuIdsToUserMonsters=" + mfuIdsToUserMonsters);
			
			Set<String> existing = mfuIdsToUserMonsters.keySet();
			MonsterStuffUtils.retainValidMonsterIds(existing, userMonsterIds);
		}
		
		if (userMonsterIds.isEmpty()) {
			log.error("no valid user monster ids sent by client");
			return false;
		}
		
		Collection<Long> userMiniJobIds = Collections.singleton(userMiniJobId);
		Map<Long, MiniJobForUser> idToUserMiniJob =
				getMiniJobForUserRetrieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(
						userId, userMiniJobIds);
	
		if (idToUserMiniJob.isEmpty()) {
			log.error("no UserMiniJob exists with id=" + userMiniJobId);
			resBuilder.setStatus(BeginMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
			return false;
		}
		
		return true;
	}
	
	private boolean writeChangesToDB(int userId, long userMiniJobId,
			List<Long> userMonsterIds, Timestamp clientTime) {
		String userMonsterIdStr = StringUtils.implode(userMonsterIds, ",");
		
		int numUpdated = UpdateUtils.get().updateMiniJobForUser(userId,
				userMiniJobId, userMonsterIdStr, clientTime);
		
		log.info("writeChangesToDB() numUpdated=" + numUpdated);
		
		return true;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}


	public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
		return miniJobForUserRetrieveUtil;
	}


	public void setMiniJobForUserRetrieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
	}


	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}


	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

//  public Locker getLocker() {
//	  return locker;
//  }
//  public void setLocker(Locker locker) {
//	  this.locker = locker;
//  }
  
}
