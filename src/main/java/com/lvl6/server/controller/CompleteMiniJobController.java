package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CompleteMiniJobRequestEvent;
import com.lvl6.events.response.CompleteMiniJobResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobResponseProto;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobResponseProto.Builder;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobResponseProto.CompleteMiniJobStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;


@Component
public class CompleteMiniJobController extends EventController{

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MonsterForUserRetrieveUtils monsterForUserRetrieveUtils;
	
	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;

	public CompleteMiniJobController() {
		numAllocatedThreads = 4;
	}

	
	@Override
	public RequestEvent createRequestEvent() {
		return new CompleteMiniJobRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COMPLETE_MINI_JOB_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		CompleteMiniJobRequestProto reqProto = ((CompleteMiniJobRequestEvent)event).getCompleteMiniJobRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		long userMiniJobId = reqProto.getUserMiniJobId();
		
		boolean isSpeedUp = reqProto.getIsSpeedUp();
		int gemCost = reqProto.getGemCost();
		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();
		
		
		CompleteMiniJobResponseProto.Builder resBuilder = CompleteMiniJobResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(CompleteMiniJobStatus.FAIL_OTHER);

		getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
		try {
			//retrieve whatever is necessary from the db
			//TODO: consider only retrieving user if the request is valid
			User user = RetrieveUtils.userRetrieveUtils()
					.getUserById(senderProto.getUserId());
			int previousGems = 0;
			
			Map<Long, Integer> userMonsterIdToExpectedHealth =
					new HashMap<Long, Integer>();
	    	
			boolean legit = checkLegit(resBuilder, userId, user,
					userMiniJobId, isSpeedUp, gemCost, umchpList,
					userMonsterIdToExpectedHealth);
			
			boolean success = false;
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			
			if (legit) {
				previousGems = user.getGems();
				success = writeChangesToDB(userId, user, userMiniJobId,
						isSpeedUp, gemCost, clientTime,
						userMonsterIdToExpectedHealth, currencyChange);
			}
			
			if (success) {
				resBuilder.setStatus(CompleteMiniJobStatus.SUCCESS);
			}
			
			CompleteMiniJobResponseEvent resEvent = new CompleteMiniJobResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setCompleteMiniJobResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(user, userMiniJobId,
						currencyChange, clientTime, previousGems);
			}
			
		} catch (Exception e) {
			log.error("exception in CompleteMiniJobController processEvent", e);
			//don't let the client hang
      try {
      	resBuilder.setStatus(CompleteMiniJobStatus.FAIL_OTHER);
      	CompleteMiniJobResponseEvent resEvent = new CompleteMiniJobResponseEvent(userId);
      	resEvent.setTag(event.getTag());
      	resEvent.setCompleteMiniJobResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in CompleteMiniJobController processEvent", e);
      }
		} finally {
			getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}

	private boolean checkLegit(Builder resBuilder, int userId, User user,
			long userMiniJobId, boolean isSpeedUp, int gemCost,
			List<UserMonsterCurrentHealthProto> umchpList,
			Map<Long, Integer> userMonsterIdToExpectedHealth) {
		
		//sanity check
		if (umchpList.isEmpty() || 0 == userMiniJobId) {
			log.error("invalid userMonsterIds or userMiniJobId." +
					" userMonsters=" + umchpList +
					"\t userMiniJobId=" + userMiniJobId);
			return false;
		}
		
		List<Long> userMonsterIds = MonsterStuffUtils.getUserMonsterIds(
				umchpList, userMonsterIdToExpectedHealth);
		Map<Long, MonsterForUser> mfuIdsToUserMonsters = 
				getMonsterForUserRetrieveUtils()
				.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
		
		//keep only valid userMonsterIds another sanity check
		if (userMonsterIds.size() != mfuIdsToUserMonsters.size()) {
			log.warn("some userMonsterIds client sent are invalid." +
					" Keeping valid ones. userMonsterIds=" + userMonsterIds +
					" mfuIdsToUserMonsters=" + mfuIdsToUserMonsters);
			
			Set<Long> existing = mfuIdsToUserMonsters.keySet();
			MonsterStuffUtils.retainValidMonsterIds(existing, userMonsterIds);
		}
		
		if (userMonsterIds.isEmpty()) {
			log.error("no valid user monster ids sent by client");
			return false;
		}
		
		Collection<Long> userMiniJobIds = Collections.singleton(userMiniJobId);
		Map<Long, MiniJobForUser> idToUserMiniJob =
				getMiniJobForUserRetrieveUtil()
				.getSpecificOrAllIdToMiniJobForUserId(
						userId, userMiniJobIds);
	
		if (idToUserMiniJob.isEmpty()) {
			log.error("no UserMiniJob exists with id=" + userMiniJobId);
			resBuilder.setStatus(CompleteMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
			return false;
		}
		
		if (isSpeedUp && !hasEnoughGems(resBuilder, user, gemCost)) {
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
	  		resBuilder.setStatus(CompleteMiniJobStatus.FAIL_INSUFFICIENT_GEMS);
	  		return false;
	  	}
	  	
	  	return true;
	  }
	
	private boolean writeChangesToDB(int userId, User user,
			long userMiniJobId, boolean isSpeedUp, int gemCost,
			Timestamp clientTime, 
	  		Map<Long, Integer> userMonsterIdToExpectedHealth,
			Map<String, Integer> currencyChange) {

		log.info("updating user's monsters' healths");
		int numUpdated = UpdateUtils.get()
				.updateUserMonstersHealth(userMonsterIdToExpectedHealth);
		log.info("numUpdated=" + numUpdated);

		//number updated is based on INSERT ... ON DUPLICATE KEY UPDATE
		//so returns 2 if one row was updated, 1 if inserted
		if (numUpdated > 2 * userMonsterIdToExpectedHealth.size()) {
			log.warn("unexpected error: more than user monsters were" +
					" updated. actual numUpdated=" + numUpdated +
					"expected: userMonsterIdToExpectedHealth=" +
					userMonsterIdToExpectedHealth);
		}
		
		//update user currency
		int gemsChange = -1 * Math.abs(gemCost);
		int cashChange = 0;
		int oilChange = 0;

		if (isSpeedUp &&
				!updateUser(user, gemsChange, cashChange, oilChange)) {
			log.error("unexpected error: could not decrement user gems by " +
					gemsChange + ", cash by " + cashChange + ", and oil by " +
					oilChange);
			return false;
		} else {
			if (0 != gemsChange) {
				currencyChange.put(MiscMethods.gems, gemsChange);
			}
		}
		
		//update complete time for MiniJobForUser
		numUpdated = UpdateUtils.get().updateMiniJobForUserCompleteTime(userId,
				userMiniJobId, clientTime);
		
		log.info("writeChangesToDB() numUpdated=" + numUpdated);
		
		return true;
	}
	

	private boolean updateUser(User u, int gemsChange, int cashChange,
			int oilChange) {
		int numChange = u.updateRelativeCashAndOilAndGems(cashChange,
				oilChange, gemsChange);

		if (numChange <= 0) {
			log.error("unexpected error: problem with updating user gems," +
					" cash, and oil. gemChange=" + gemsChange + ", cash= " +
					cashChange + ", oil=" + oilChange + " user=" + u);
			return false;
		}
		return true;
	}
	

	private void writeToUserCurrencyHistory(User aUser, long userMiniJobId,
			Map<String, Integer> currencyChange, Timestamp curTime,
			int previousGems) {
		int userId = aUser.getId();
		String reason = ControllerConstants.UCHRFC__SPED_UP_COMPLETE_MINI_JOB;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("userMiniJobId=");
		detailsSb.append(userMiniJobId);
		
		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MiscMethods.gems;

		previousCurrency.put(gems, previousGems);
		currentCurrency.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, detailsSb.toString());

		MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
				previousCurrency, currentCurrency, reasonsForChanges, detailsMap);

	}
	
	public Locker getLocker() {
		return locker;
	}
	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterForUserRetrieveUtils getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}


	public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
		return miniJobForUserRetrieveUtil;
	}


	public void setMiniJobForUserRetrieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
	}
  
}
