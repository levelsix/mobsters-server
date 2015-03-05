package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CollectMonsterEnhancementRequestEvent;
import com.lvl6.events.response.CollectMonsterEnhancementResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementRequestProto;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto.CollectMonsterEnhancementStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentExpProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class CollectMonsterEnhancementController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;
	
	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;
	
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
		String userId = senderProto.getUserUuid();
		UserMonsterCurrentExpProto umcep = reqProto.getUmcep();
		//user monster ids that will be deleted from monster enhancing for user table
		List<String> userMonsterIdsThatFinished = reqProto.getUserMonsterUuidsList();
		userMonsterIdsThatFinished = new ArrayList<String>(userMonsterIdsThatFinished);

		//set some values to send to the client (the response proto)
		CollectMonsterEnhancementResponseProto.Builder resBuilder = CollectMonsterEnhancementResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(CollectMonsterEnhancementStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		boolean invalidUuids = true;
		
		try {
			userUuid = UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s",
				userId), e);
		}
		
		//UUID checks
	    if (invalidUuids) {
	    	resBuilder.setStatus(CollectMonsterEnhancementStatus.FAIL_OTHER);
			CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setCollectMonsterEnhancementResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
	    	return;
	    }
		
		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			List<String> userMonsterIds = new ArrayList<String>();
			if (null != umcep && !userMonsterIdsThatFinished.isEmpty()) {
				userMonsterIds.add(umcep.getUserMonsterUuid()); //monster being enhanced
				userMonsterIds.addAll(userMonsterIdsThatFinished);
			}

			MonsterForUser mfu = monsterForUserRetrieveUtil
					.getSpecificUserMonster(umcep.getUserMonsterUuid());
			
			//get whatever we need from the database
			Map<String, MonsterEnhancingForUser> inEnhancing =
				monsterEnhancingForUserRetrieveUtil.getMonstersForUser(userId);

            User aUser = userRetrieveUtil.getUserById(userId);

			boolean legit = checkLegit(resBuilder, userId,
				userMonsterIds, inEnhancing, umcep, userMonsterIdsThatFinished);

			boolean successful = false;
			if(legit) {
				successful = writeChangesToDb( aUser, umcep, mfu );
			}
			if (successful) {
				resBuilder.setStatus(CollectMonsterEnhancementStatus.SUCCESS);
			}

			CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setCollectMonsterEnhancementResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

            UpdateClientUserResponseEvent resEventUpdate = MiscMethods
                .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null, null);
            resEventUpdate.setTag(event.getTag());
            server.writeEvent(resEventUpdate);

			if (successful) {
				int currExp = umcep.getExpectedExperience();
				writeChangesToHistory(userId, userMonsterIds, inEnhancing, userMonsterIdsThatFinished, mfu, currExp);
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
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
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
	private boolean checkLegit(Builder resBuilder, String userId,
		List<String> userMonsterIds,
		Map<String, MonsterEnhancingForUser> inEnhancing,
		UserMonsterCurrentExpProto umcep, List<String> usedUpMonsterIds)
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
		Set<String> inEnhancingIds = inEnhancing.keySet();
		
		if (inEnhancingIds.size() != userMonsterIds.size() ||
			!inEnhancingIds.containsAll(userMonsterIds))
		{
			log.error(String.format(
				"inconsistent enhancing data. umcep=%s, usedUpMonsterIds=%s, inEnhancing=%s",
				umcep, usedUpMonsterIds, inEnhancing));
			return false;
		}

		//check to make sure the base is complete
		MonsterEnhancingForUser mefu = inEnhancing.get(umcep.getUserMonsterUuid()); 
		if (!mefu.isEnhancingComplete()) {
			log.error(String.format(
				"base monster being enhanced is incomplete: %s", mefu));
			return false;
		}

		return true;
	}

	private boolean writeChangesToDb(User u,
		UserMonsterCurrentExpProto umcep, MonsterForUser mfu)
	{

		String userMonsterIdBeingEnhanced = umcep.getUserMonsterUuid();
		int newExp = umcep.getExpectedExperience();
		int newLvl = umcep.getExpectedLevel();
		int newHp = umcep.getExpectedHp();
		
		//reward the user with exp

		awardUserExp(u, userMonsterIdBeingEnhanced, newExp, mfu);

		//GIVE THE MONSTER EXP
		int num = UpdateUtils.get().updateUserMonsterExpAndLvl(userMonsterIdBeingEnhanced,
			newExp, newLvl, newHp);
		log.info(String.format(
			"numUpdated (monster being enhanced, expected 1)=%s", num));

		
		return true;
	}

	private void awardUserExp( User u, String userMonsterIdBeingEnhanced, int newExp, MonsterForUser mfu)
	{
		try {			
			float expReward = (float)newExp - (float)mfu.getCurrentExp();
			expReward *= ControllerConstants.MONSTER_ENHANCING__PLAYER_EXP_CONVERTER;
			log.info(String.format(
				"expReward for enhancing=%s, userBefore=%s", expReward, u));
			
			if (expReward > 0) {
				u.updateRelativeGemsNaive(0, (int)expReward);
				log.info(String.format(
					"expReward for userAfter=%s", u));
			}

		} catch (Exception e) {
			log.error(String.format(
				"can't reward user exp for enhancing. mfu=%s, u=%s",
				mfu, u), e);
		}
	}

	private void writeChangesToHistory(String uId, List<String> allEnhancingMfuIds,
		Map<String, MonsterEnhancingForUser> inEnhancing, List<String> usedUpMfuIds, MonsterForUser mfu, int currExp)
	{
		//get the monster being enhanced
		MonsterEnhancingForUser monsterBeingEnhanced = null;
		for(String id : inEnhancing.keySet()) {
			monsterBeingEnhanced = inEnhancing.get(id);
		}
		
		MonsterForUser enhancedMonster = monsterForUserRetrieveUtil.getSpecificUserMonster(monsterBeingEnhanced.getMonsterForUserId());
		Date now = new Date();
		Timestamp timeOfEntry = new Timestamp(now.getTime());
		Timestamp enhanceStartTime = new Timestamp(monsterBeingEnhanced.getExpectedStartTime().getTime());
		//TODO: keep track of the monsters that were enhancing
		
		for(String feederId : usedUpMfuIds) {
			InsertUtils.get().insertMonsterEnhanceHistory(uId, enhancedMonster.getId(),
					feederId, currExp, mfu.getCurrentExp(), enhanceStartTime, 
					timeOfEntry, monsterBeingEnhanced.getEnhancingCost());

		}

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
		
		writeToMonsterDeleteHistory(usedUpMfuIds);
		log.info("added deleted monsters to monster delete table");

	}
	
	private void writeToMonsterDeleteHistory(List<String> deletedMonsterForUserIds) {
		String deletedReason = "enhancing";
		String details = "feeder monsters";
		Date now = new Date();
		Timestamp deletedTime = new Timestamp(now.getTime());
		for(String userMonsterId : deletedMonsterForUserIds) {
			MonsterForUser mfu = monsterForUserRetrieveUtil.getSpecificUserMonster(userMonsterId);
			InsertUtils.get().insertMonsterDeleteHistory(mfu, deletedReason, details, null, deletedTime);
		}
	}
	
	

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterEnhancingForUserRetrieveUtils2 getMonsterEnhancingForUserRetrieveUtil()
	{
		return monsterEnhancingForUserRetrieveUtil;
	}

	public void setMonsterEnhancingForUserRetrieveUtil(
		MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtil )
	{
		this.monsterEnhancingForUserRetrieveUtil = monsterEnhancingForUserRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil()
	{
		return monsterForUserRetrieveUtil;
	}

	public void setMonsterForUserRetrieveUtil(
		MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil )
	{
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil()
	{
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil( UserRetrieveUtils2 userRetrieveUtil )
	{
		this.userRetrieveUtil = userRetrieveUtil;
	}

}
