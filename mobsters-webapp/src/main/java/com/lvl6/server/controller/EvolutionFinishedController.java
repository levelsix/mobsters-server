package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EvolutionFinishedRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.EvolutionFinishedResponseEvent;
import com.lvl6.events.response.EvolveMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterDeleteHistory;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedRequestProto;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component

public class EvolutionFinishedController extends EventController {

	private static Logger log = LoggerFactory.getLogger(EvolutionFinishedController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	
	@Autowired
	protected TimeUtils timeUtils;


	public EvolutionFinishedController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EvolutionFinishedRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_EVOLUTION_FINISHED_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		EvolutionFinishedRequestProto reqProto = ((EvolutionFinishedRequestEvent) event)
				.getEvolutionFinishedRequestProto();

		log.info("reqProto={}", reqProto);

		//get data client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		//(positive number, server will convert it to negative)
		int gemsSpent = reqProto.getGemsSpent();
		Date now = new Date();
		
		Date clientTime = new Date(reqProto.getClientTime());

		//set some values to send to the client (the response proto)
		EvolutionFinishedResponseProto.Builder resBuilder = EvolutionFinishedResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(clientTime, new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, getClass().getSimpleName());

			int previousGems = 0;
			//get whatever we need from the database
			User aUser = userRetrieveUtil.getUserById(userId);
			MonsterEvolvingForUser evolution = monsterEvolvingForUserRetrieveUtil
					.getEvolutionForUser(userId);

			String catalystUserMonsterId = evolution
					.getCatalystMonsterForUserId();
			String uMonsterIdOne = evolution.getMonsterForUserIdOne();
			String uMonsterIdTwo = evolution.getMonsterForUserIdTwo();
			Timestamp combineStartTime = new Timestamp(evolution.getStartTime()
					.getTime());

			//retrieve all the monsters used in evolution
			Map<String, MonsterForUser> existingUserMonsters = getMonstersUsedInEvolution(
					userId, evolution);

			log.debug("evolution={}", evolution);
			log.debug("existingUserMonsters={}",
					existingUserMonsters);

			boolean legitMonster = checkLegit(resBuilder, aUser, userId,
					evolution, existingUserMonsters, gemsSpent);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			List<MonsterForUser> evolvedUserMonster = new ArrayList<MonsterForUser>();
			if (legitMonster) {
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, now, gemsSpent,
						catalystUserMonsterId, uMonsterIdOne, uMonsterIdTwo,
						combineStartTime, money, existingUserMonsters,
						evolvedUserMonster);
			}

			if (successful) {
				writeToUserMonsterDeleteHistory(uMonsterIdOne, uMonsterIdTwo,
						catalystUserMonsterId, userId, combineStartTime,
						existingUserMonsters);

				MonsterForUser evolvedMfu = evolvedUserMonster.get(0);
				FullUserMonsterProto fump = createInfoProtoUtils
						.createFullUserMonsterProtoFromUserMonster(evolvedMfu);
				resBuilder.setEvolvedMonster(fump);
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(aUser, now, money, previousGems,
						evolution, evolvedUserMonster);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceMonster processEvent", e);
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, getClass().getSimpleName());
			}
		}
	}

	private Map<String, MonsterForUser> getMonstersUsedInEvolution(
			String userId, MonsterEvolvingForUser evolution) {
		Map<String, MonsterForUser> existingUserMonsters = new HashMap<String, MonsterForUser>();
		//just  in case evolution is null, but most likely not.
		//retrieve all the monsters used in evolution, so they can be deleted
		if (null != evolution) {
			Set<String> newIds = new HashSet<String>();
			String catalystUserMonsterId = evolution
					.getCatalystMonsterForUserId();
			String userMonsterIdOne = evolution.getMonsterForUserIdOne();
			String userMonsterIdTwo = evolution.getMonsterForUserIdTwo();
			newIds.add(catalystUserMonsterId);
			newIds.add(userMonsterIdOne);
			newIds.add(userMonsterIdTwo);
			existingUserMonsters = monsterForUserRetrieveUtil
					.getSpecificOrAllUserMonstersForUser(userId, newIds);
		}

		return existingUserMonsters;
	}

	private boolean checkLegit(Builder resBuilder, User u, String userId,
			MonsterEvolvingForUser evolution,
			Map<String, MonsterForUser> existingUserMonsters, int gemsSpent) {
		if (null == u || null == evolution || null == existingUserMonsters
				|| existingUserMonsters.isEmpty()) {
			log.error(String
					.format("user, evolution, or existingMonsters is null. user=%s, evolution=%s, existingMonsters=%s",
							u, evolution, existingUserMonsters));
			return false;
		}

		//check to make sure these monsters still exist
		String catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
		String one = evolution.getMonsterForUserIdOne();
		String two = evolution.getMonsterForUserIdTwo();
		if (!existingUserMonsters.containsKey(catalystUserMonsterId)
				|| !existingUserMonsters.containsKey(one)
				|| !existingUserMonsters.containsKey(two)) {
			log.error(String
					.format("one of the monsters in an evolution is missing. evolution=%s, existingUserMonsters=%s",
							evolution, existingUserMonsters));
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			return false;
		}

		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, evolution)) {
			return false;
		}

		return true;
	}

	//if gem cost is 0 and user gems is 0, then 0 !< 0 so no error issued
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			MonsterEvolvingForUser evolution) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < Math.abs(gemsSpent)) {
			log.error(String.format(
					"not enough gems. userGems=%s, gemsSpent=%s, evolution=%s",
					userGems, gemsSpent, evolution));
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	//List<MonsterForUser> userMonsters will be populated with the evolved monster
	private boolean writeChangesToDB(User user, String uId, Date now,
			int gemsSpent, String catalystUserMonsterId, String uMonsterIdOne,
			String uMonsterIdTwo, Timestamp combineStartTime,
			Map<String, Integer> money,
			Map<String, MonsterForUser> idsToUserMonsters,
			List<MonsterForUser> userMonsters) {

		//CHARGE THE USER IF HE SPED UP THE EVOLUTION
		if (0 != gemsSpent) {
			int oilChange = 0;
			int cashChange = 0;
			int gemChange = -1 * gemsSpent;
			int numChange = user.updateRelativeCashAndOilAndGems(cashChange,
					oilChange, gemChange, 0);
			if (1 != numChange) {
				log.warn(String
						.format("problem with updating user stats: gemChange=%s, cashChange=%s, user is %s",
								gemChange, oilChange, user));
			} else {
				//everything went well
				if (0 != oilChange) {
					money.put(miscMethods.oil, oilChange);
				}
				if (0 != gemsSpent) {
					money.put(miscMethods.gems, gemChange);
				}
			}
		}
		//delete the monsters used in the evolution
		List<String> userMonsterIds = new ArrayList<String>();

		userMonsterIds.add(catalystUserMonsterId);
		userMonsterIds.add(uMonsterIdOne);
		userMonsterIds.add(uMonsterIdTwo);
		int num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
		log.info(String.format("num monsterForUser deleted: %s", num));

		//delete the evolution
		num = DeleteUtils.get().deleteMonsterEvolvingForUser(
				catalystUserMonsterId, uMonsterIdOne, uMonsterIdTwo, uId);
		log.info(String.format("num evolutions deleted: %s", num));

		//insert the COMPLETE evolved monster into monster for user
		//get evolved version of monster
		MonsterForUser evolvedUserMonster = createEvolvedMonster(uId, now,
				uMonsterIdOne, idsToUserMonsters);
		userMonsters.add(evolvedUserMonster);

		String sourceOfPieces = createSourceOfPieces(catalystUserMonsterId,
				uMonsterIdOne, uMonsterIdTwo);
		List<String> evovlvedMfuId = InsertUtils.get()
				.insertIntoMonsterForUserReturnIds(uId, userMonsters,
						sourceOfPieces, now);

		//set the evolved monster for user's id
		if (null != evovlvedMfuId && !evovlvedMfuId.isEmpty()) {
			String mfuId = evovlvedMfuId.get(0);
			evolvedUserMonster.setId(mfuId);
		}

		log.info(String.format("evolvedUserMonster=%s", evolvedUserMonster));
		log.info(String.format("userMonsters=%s", userMonsters));

		return true;
	}

	private MonsterForUser createEvolvedMonster(String uId, Date now,
			String uMonsterIdOne, Map<String, MonsterForUser> idsToUserMonsters) {
		MonsterForUser unevolvedMonster = idsToUserMonsters.get(uMonsterIdOne);
		int monsterId = unevolvedMonster.getMonsterId();
		Monster evolvedMonster = monsterRetrieveUtils
				.getEvolvedFormForMonster(monsterId);
		int numPieces = evolvedMonster.getNumPuzzlePieces();
		boolean hasAllPieces = true;
		boolean isComplete = true;
		MonsterForUser mfu = monsterStuffUtils.createNewUserMonster(uId,
				numPieces, evolvedMonster, now, hasAllPieces, isComplete,
				monsterLevelInfoRetrieveUtils);

		return mfu;
	}

	private String createSourceOfPieces(String catalystUserMonsterId,
			String uMonsterIdOne, String uMonsterIdTwo) {
		StringBuilder sourceOfPiecesSb = new StringBuilder();
		sourceOfPiecesSb.append("evolved from (catalystId,idOne,idTwo): (");
		sourceOfPiecesSb.append(catalystUserMonsterId);
		sourceOfPiecesSb.append(",");
		sourceOfPiecesSb.append(uMonsterIdOne);
		sourceOfPiecesSb.append(",");
		sourceOfPiecesSb.append(uMonsterIdTwo);
		sourceOfPiecesSb.append(")");

		return sourceOfPiecesSb.toString();
	}

	public void writeToUserCurrencyHistory(User aUser, Date now,
			Map<String, Integer> moneyChange, int previousGems,
			MonsterEvolvingForUser evolution,
			List<MonsterForUser> evolvedUserMonsterList) {
		if (moneyChange.isEmpty()) {
			return;
		}
		String gems = miscMethods.gems;

		Timestamp date = new Timestamp((now.getTime()));
		String catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
		String one = evolution.getMonsterForUserIdOne();
		String two = evolution.getMonsterForUserIdTwo();
		MonsterForUser evolved = evolvedUserMonsterList.get(0);
		String evolvedId = evolved.getId();

		String reasonForChange = ControllerConstants.UCHRFC__SPED_UP_EVOLUTION;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("(catalystId, userMonsterId, userMonsterId, evolvedMonsterId)");
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		detailSb.append("(");
		detailSb.append(catalystUserMonsterId);
		detailSb.append(",");
		detailSb.append(one);
		detailSb.append(",");
		detailSb.append(two);
		detailSb.append(",");
		detailSb.append(evolvedId);
		detailSb.append(")");

		String userId = aUser.getId();
		Map<String, Integer> previousCurrencyMap = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
		Map<String, String> changeReasonsMap = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();

		previousCurrencyMap.put(gems, previousGems);
		currentCurrencyMap.put(gems, aUser.getGems());
		changeReasonsMap.put(gems, reasonForChange);
		detailsMap.put(gems, detailSb.toString());

		miscMethods.writeToUserCurrencyOneUser(userId, date, moneyChange,
				previousCurrencyMap, currentCurrencyMap, changeReasonsMap,
				detailsMap);
	}

	public void writeToUserMonsterDeleteHistory(String uMonsterIdOne,
			String uMonsterIdTwo, String catalystUserMonsterId, String userId,
			Timestamp combineStartTime,
			Map<String, MonsterForUser> idsToUserMonsters) {
		String deletedReason = "monster evolution";
		Date d = new Date();
		Timestamp deletedTime = new Timestamp(d.getTime());
		List<MonsterDeleteHistory> monsterDeleteHistoryList = new ArrayList<MonsterDeleteHistory>();

		for (String id : idsToUserMonsters.keySet()) {
			MonsterForUser mfu = idsToUserMonsters.get(id);
			mfu.setCombineStartTime(combineStartTime);
			String details = "";
			if (id.equals(uMonsterIdOne)) {
				details = "monster one of evolution";
			} else if (id.equals(uMonsterIdTwo)) {
				details = "monster two of evolution";
			} else if (id.equals(catalystUserMonsterId)) {
				details = "catalystUserMonsterId";
			}
			MonsterDeleteHistory mdh = new MonsterDeleteHistory(mfu,
					deletedReason, details, deletedTime);
			monsterDeleteHistoryList.add(mdh);
		}

		InsertUtils.get().insertMonsterDeleteHistory(monsterDeleteHistoryList);

	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterEvolvingForUserRetrieveUtils2 getMonsterEvolvingForUserRetrieveUtil() {
		return monsterEvolvingForUserRetrieveUtil;
	}

	public void setMonsterEvolvingForUserRetrieveUtil(
			MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtil) {
		this.monsterEvolvingForUserRetrieveUtil = monsterEvolvingForUserRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil() {
		return monsterForUserRetrieveUtil;
	}

	public void setMonsterForUserRetrieveUtil(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil) {
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
	}

}
