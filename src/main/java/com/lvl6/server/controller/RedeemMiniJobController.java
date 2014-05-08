package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemMiniJobRequestEvent;
import com.lvl6.events.response.RedeemMiniJobResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MiniJob;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto.Builder;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto.RedeemMiniJobStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;


@Component
public class RedeemMiniJobController extends EventController{

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MonsterForUserRetrieveUtils monsterForUserRetrieveUtils;
	
	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;

	public RedeemMiniJobController() {
		numAllocatedThreads = 4;
	}

	
	@Override
	public RequestEvent createRequestEvent() {
		return new RedeemMiniJobRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COMPLETE_MINI_JOB_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		RedeemMiniJobRequestProto reqProto = ((RedeemMiniJobRequestEvent)event).getRedeemMiniJobRequestProto();
		log.info("reqProto=" + reqProto);

		MinimumUserProtoWithMaxResources senderResourcesProto =
				reqProto.getSender();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();
		
		int userId = senderProto.getUserId();
		Date now = new Date(reqProto.getClientTime());
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		long userMiniJobId = reqProto.getUserMiniJobId();
		
		
		RedeemMiniJobResponseProto.Builder resBuilder = RedeemMiniJobResponseProto.newBuilder();
		resBuilder.setSender(senderResourcesProto);
		resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);

		getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
		try {
			//retrieve whatever is necessary from the db
			//TODO: consider only retrieving user if the request is valid
			User user = RetrieveUtils.userRetrieveUtils()
					.getUserById(senderProto.getUserId());
			List<MiniJobForUser> mjfuList = new ArrayList<MiniJobForUser>();
			
			
			boolean legit = checkLegit(resBuilder, userId, user,
					userMiniJobId, mjfuList);
			
			boolean success = false;
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
			if (legit) {
				MiniJobForUser mjfu = mjfuList.get(0);
				success = writeChangesToDB(resBuilder, userId, user,
						userMiniJobId, mjfu, now, clientTime, currencyChange,
						previousCurrency);
			}
			
			if (success) {
				resBuilder.setStatus(RedeemMiniJobStatus.SUCCESS);
			}
			
			RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setRedeemMiniJobResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);
				
				//TODO: track the MiniJobForUser history
				writeToUserCurrencyHistory(user, userMiniJobId,
						currencyChange, clientTime, previousCurrency);
			}
			
		} catch (Exception e) {
			log.error("exception in RedeemMiniJobController processEvent", e);
			//don't let the client hang
      try {
      	resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);
      	RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(userId);
      	resEvent.setTag(event.getTag());
      	resEvent.setRedeemMiniJobResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in RedeemMiniJobController processEvent", e);
      }
		} finally {
			getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}

	private boolean checkLegit(Builder resBuilder, int userId, User user,
			long userMiniJobId, List<MiniJobForUser> mjfuList) {
		
		Collection<Long> userMiniJobIds = Collections.singleton(userMiniJobId);
		Map<Long, MiniJobForUser> idToUserMiniJob =
				getMiniJobForUserRetrieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(
						userId, userMiniJobIds);
	
		if (idToUserMiniJob.isEmpty()) {
			log.error("no UserMiniJob exists with id=" + userMiniJobId);
			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
			return false;
		}
		
		MiniJobForUser mjfu = idToUserMiniJob.get(userMiniJobId);
		if (null == mjfu.getTimeCompleted()) {
			//sanity check
			log.error("MiniJobForUser incomplete: " + mjfu);
			return false;
		}
		
		//sanity check
		int miniJobId = mjfu.getMiniJobId();
		MiniJob mj = MiniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
		if (null == mj) {
			log.error("no MiniJob exists with id=" + miniJobId +
					"\t invalid MiniJobForUser=" + mjfu);
			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
			return false;
		}
		
		mjfuList.add(mjfu);
		return true;
	}
	
	
	private boolean writeChangesToDB(Builder resBuilder, int userId,
			User user, long userMiniJobId, MiniJobForUser mjfu, Date now,
			Timestamp clientTime, Map<String, Integer> currencyChange,
			Map<String, Integer> previousCurrency) {
		int miniJobId = mjfu.getMiniJobId();
		MiniJob mj = MiniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
		
		int prevGems = user.getGems();
		int prevCash = user.getCash();
		int prevOil = user.getOil();

		//update user currency
		int gemsChange = mj.getGemReward();
		int cashChange = mj.getCashReward();
		int oilChange = mj.getOilReward();
		int monsterIdReward = mj.getMonsterIdReward();

		if (!updateUser(user, gemsChange, cashChange, oilChange)) {
			log.error("unexpected error: could not decrement user gems by " +
					gemsChange + ", cash by " + cashChange + ", and oil by " +
					oilChange);
			return false;
		} else {
			if (0 != gemsChange) {
				currencyChange.put(MiscMethods.gems, gemsChange);
				previousCurrency.put(MiscMethods.gems, prevGems);
			}
			if (0 != cashChange) {
				currencyChange.put(MiscMethods.cash, cashChange);
				previousCurrency.put(MiscMethods.cash, prevCash);
			}
			if (0 != oilChange) {
				currencyChange.put(MiscMethods.oil, oilChange);
				previousCurrency.put(MiscMethods.oil, prevOil);
			}
		}
		
		//give the user the monster if he got one
		if (0 != monsterIdReward) {
			StringBuilder mfusopB = new StringBuilder();
      		mfusopB.append(ControllerConstants.MFUSOP__MINI_JOB);
      		mfusopB.append(" ");
      		mfusopB.append(miniJobId);
      		String mfusop = mfusopB.toString();
			Map<Integer, Integer> monsterIdToNumPieces =
					new HashMap<Integer, Integer>();
			monsterIdToNumPieces.put(monsterIdReward, 1);
			
			List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils.
      				updateUserMonsters(userId, monsterIdToNumPieces, mfusop,
      						now);
			FullUserMonsterProto fump = newOrUpdated.get(0);
			resBuilder.setFump(fump);
		}
		
		//delete the user mini job
		int numDeleted = DeleteUtils.get().deleteMiniJobForUser(userMiniJobId);
		log.info("userMiniJob numDeleted=" + numDeleted);
		
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
			Map<String, Integer> previousCurrency) {
		int userId = aUser.getId();
		String reason = ControllerConstants.UCHRFC__SPED_UP_COMPLETE_MINI_JOB;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("userMiniJobId=");
		detailsSb.append(userMiniJobId);
		
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MiscMethods.gems;

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
