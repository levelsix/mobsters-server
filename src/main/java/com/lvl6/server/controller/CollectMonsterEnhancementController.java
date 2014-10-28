package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CollectMonsterEnhancementRequestEvent;
import com.lvl6.events.response.CollectMonsterEnhancementResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementRequestProto;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto.CollectMonsterEnhancementStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentExpProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class CollectMonsterEnhancementController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	public CollectMonsterEnhancementController() {
		numAllocatedThreads = 4;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CollectMonsterEnhancementRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COLLECT_MONSTER_ENHANCEMENT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		CollectMonsterEnhancementRequestProto reqProto = ((CollectMonsterEnhancementRequestEvent)event).getCollectMonsterEnhancementRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		UserMonsterCurrentExpProto umcep = reqProto.getUmcep();
		//user monster ids that will be deleted from monster enhancing for user table
		List<Long> userMonsterIdsThatFinished = reqProto.getUserMonsterIdsList();
		userMonsterIdsThatFinished = new ArrayList<Long>(userMonsterIdsThatFinished);

		//set some values to send to the client (the response proto)
		CollectMonsterEnhancementResponseProto.Builder resBuilder = CollectMonsterEnhancementResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(CollectMonsterEnhancementStatus.FAIL_OTHER); //default

		getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
		try {
			List<Long> userMonsterIds = new ArrayList<Long>();
			if (null != umcep && !userMonsterIds.isEmpty()) {
				userMonsterIds.add(umcep.getUserMonsterId()); //monster being enhanced
				userMonsterIds.addAll(userMonsterIdsThatFinished);
			}

			//get whatever we need from the database
			Map<Long, MonsterEnhancingForUser> inEnhancing =
				MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);

			boolean legit = checkLegit(resBuilder, userId,
				userMonsterIds, inEnhancing, umcep, userMonsterIdsThatFinished);

			boolean successful = false;
			if(legit) {
				successful = writeChangesToDb( userId, umcep );
			}
			if (successful) {
				resBuilder.setStatus(CollectMonsterEnhancementStatus.SUCCESS);
			}

			CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setCollectMonsterEnhancementResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (successful) {
				writeChangesToHistory(userId, userMonsterIds, inEnhancing, userMonsterIdsThatFinished);
			}
		} catch (Exception e) {
			log.error("exception in CollectMonsterEnhancementController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(CollectMonsterEnhancementStatus.FAIL_OTHER);
				CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setCollectMonsterEnhancementResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in CollectMonsterEnhancementController processEvent", e);
			}
		} finally {
			getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
		}
	}

	/**
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 *
	 * True if monster being enhanced has completed enhancing and all
	 * the monsters involved in enhancing is accounted for by what client sent 
	 * 
	 * @param resBuilder
	 * @param u
	 * @param userId
	 * @param userMonsterIds - monster ids client sent that are in enhancing
	 * @param inEnhancing - the monsters that are in the enhancing queue
	 * @param umcep - the base monster that is updated from using up some of the feeders
	 * @param usedUpUserMonsterIds - userMonsterIds the user thinks has finished being enhanced
	 * @return
	 */
	private boolean checkLegit(Builder resBuilder, int userId,
		List<Long> userMonsterIds,
		Map<Long, MonsterEnhancingForUser> inEnhancing,
		UserMonsterCurrentExpProto umcep, List<Long> usedUpMonsterIds)
	{

		if ( null == umcep || usedUpMonsterIds.isEmpty()) {
			log.error(String.format(
				"deficient enhancing data. umcep=%s, usedUpMonsterIds=%s, inEnhancing=%s",
				umcep, usedUpMonsterIds, inEnhancing));
			return false;
		}
		log.info(String.format("inEnhancing=%s", inEnhancing));
//		long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();

		//make sure that all the monsters involved in enhancing is accounted for
		Set<Long> inEnhancingIds = inEnhancing.keySet();
		
		if (inEnhancingIds.size() != userMonsterIds.size() ||
			!inEnhancingIds.containsAll(userMonsterIds))
		{
			log.error(String.format(
				"inconsistent enhancing data. umcep=%s, usedUpMonsterIds=%s, inEnhancing=%s",
				umcep, usedUpMonsterIds, inEnhancing));
			return false;
		}

		//check to make sure the base is complete
		MonsterEnhancingForUser mefu = inEnhancing.get(umcep.getUserMonsterId()); 
		if (!mefu.isEnhancingComplete()) {
			log.error(String.format(
				"base monster being enhanced is incomplete: %s", mefu));
			return false;
		}

		return true;
	}

	private boolean writeChangesToDb(int uId,
		UserMonsterCurrentExpProto umcep)
	{

		long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();
		int newExp = umcep.getExpectedExperience();
		int newLvl = umcep.getExpectedLevel();
		int newHp = umcep.getExpectedHp();

		//GIVE THE MONSTER EXP
		int num = UpdateUtils.get().updateUserMonsterExpAndLvl(userMonsterIdBeingEnhanced,
			newExp, newLvl, newHp);
		log.info(String.format(
			"numUpdated (monster being enhanced, expected 1)=%s", num));

		return true;
	}

	private void writeChangesToHistory(int uId, List<Long> allEnhancingMfuIds,
		Map<Long, MonsterEnhancingForUser> inEnhancing, List<Long> usedUpMfuIds)
	{

		//TODO: keep track of the userMonsters that are deleted


		//TODO: keep track of the monsters that were enhancing

		//delete the selected monsters from  the enhancing table
		int num = DeleteUtils.get().deleteMonsterEnhancingForUser(
			uId, allEnhancingMfuIds);
		log.info(String.format(
			"deleted monster enhancing rows. numDeleted=%s, userMonsterIds=%s, inEnhancing=%s",
			num, allEnhancingMfuIds, inEnhancing));


		//delete the userMonsterIds from the monster_for_user table, but don't delete
		//the monster user is enhancing
		num = DeleteUtils.get().deleteMonstersForUser(usedUpMfuIds);
		log.info(String.format(
			"deleted monster_for_user rows. numDeleted=%s, inEnhancing=%s, deletedIds=%s",
			num, inEnhancing, usedUpMfuIds));

	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
