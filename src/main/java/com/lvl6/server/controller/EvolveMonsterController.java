package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
import com.lvl6.events.request.EvolveMonsterRequestEvent;
import com.lvl6.events.response.EvolveMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.EvolveMonsterRequestProto;
import com.lvl6.proto.EventMonsterProto.EvolveMonsterResponseProto;
import com.lvl6.proto.EventMonsterProto.EvolveMonsterResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.EvolveMonsterResponseProto.EvolveMonsterStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterEvolutionProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.StringUtils;

@Component

public class EvolveMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected InsertUtil insertUtil;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtil;

	@Autowired
	protected MonsterHealingForUserRetrieveUtils2 monsterHealingForUserRetrieveUtil;

	@Autowired
	protected MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;

	public EvolveMonsterController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EvolveMonsterRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_EVOLVE_MONSTER_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		EvolveMonsterRequestProto reqProto = ((EvolveMonsterRequestEvent) event)
				.getEvolveMonsterRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		//get data client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();

		UserMonsterEvolutionProto uep = reqProto.getEvolution();
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int oilChange = reqProto.getOilChange();

		String catalystUserMonsterId = "";
		List<String> evolvingUserMonsterIds = new ArrayList<String>();
		Timestamp clientTime = null;

		if (null != uep && reqProto.hasEvolution()) {
			log.info("uep is not null");
			catalystUserMonsterId = uep.getCatalystUserMonsterUuid();
			evolvingUserMonsterIds = new ArrayList<String>(
					uep.getUserMonsterUuidsList());
			clientTime = new Timestamp(uep.getStartTime());
		}

		//set some values to send to the client (the response proto)
		EvolveMonsterResponseProto.Builder resBuilder = EvolveMonsterResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(EvolveMonsterStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			//just seeing if ids are valid
			UUID.fromString(catalystUserMonsterId);
			StringUtils.convertToUUID(evolvingUserMonsterIds);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_OTHER);
			EvolveMonsterResponseEvent resEvent = new EvolveMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, getClass().getSimpleName());
		try {
			int previousOil = 0;
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = userRetrieveUtil.getUserById(userId);
			Map<String, MonsterEnhancingForUser> alreadyEnhancing = monsterEnhancingForUserRetrieveUtil
					.getMonstersForUser(userId);

			Map<String, MonsterHealingForUser> alreadyHealing = monsterHealingForUserRetrieveUtil
					.getMonstersForUser(userId);

			Map<String, MonsterEvolvingForUser> alreadyEvolving = monsterEvolvingForUserRetrieveUtil
					.getCatalystIdsToEvolutionsForUser(userId);

			//retrieve all the new monsters
			Map<String, MonsterForUser> existingUserMonsters = new HashMap<String, MonsterForUser>();

			//just in case uep is null, but most likely not. retrieve all the monsters used
			//in evolution, just to make sure they exist
			if (null != uep && reqProto.hasEvolution()) {
				Set<String> newIds = new HashSet<String>();
				newIds.add(catalystUserMonsterId);
				newIds.addAll(evolvingUserMonsterIds);
				existingUserMonsters = monsterForUserRetrieveUtil
						.getSpecificOrAllUserMonstersForUser(userId, newIds);
				log.info(String.format(
						"retrieved user monsters. existingUserMonsters=%s",
						existingUserMonsters));
			}
			boolean legitMonster = checkLegit(resBuilder, aUser, userId,
					existingUserMonsters, alreadyEnhancing, alreadyHealing,
					alreadyEvolving, catalystUserMonsterId,
					evolvingUserMonsterIds, gemsSpent, oilChange);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();

			if (legitMonster) {
				previousOil = aUser.getOil();
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, gemsSpent,
						oilChange, catalystUserMonsterId,
						evolvingUserMonsterIds, clientTime, money);
			}

			if (successful) {
				resBuilder.setStatus(EvolveMonsterStatus.SUCCESS);
			}

			EvolveMonsterResponseEvent resEvent = new EvolveMonsterResponseEvent(
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

				writeToUserCurrencyHistory(aUser, clientTime, money,
						previousOil, previousGems, catalystUserMonsterId,
						evolvingUserMonsterIds);

				String userMonsterId1 = evolvingUserMonsterIds.get(0);
				String userMonsterId2 = evolvingUserMonsterIds.get(1);
				Date now = new Date();
				Timestamp currentTime = new Timestamp(now.getTime());

				insertUtil.insertMonsterEvolveHistory(userId, userMonsterId1,
						userMonsterId2, catalystUserMonsterId, clientTime,
						currentTime);
			}

		} catch (Exception e) {
			log.error("exception in EvolveMonster processEvent", e);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_OTHER);
			EvolveMonsterResponseEvent resEvent = new EvolveMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
		} finally {
			getLocker().unlockPlayer(userUuid, getClass().getSimpleName());
		}
	}

	private boolean checkLegit(Builder resBuilder, User u, String userId,
			Map<String, MonsterForUser> existingUserMonsters,
			Map<String, MonsterEnhancingForUser> alreadyEnhancing,
			Map<String, MonsterHealingForUser> alreadyHealing,
			Map<String, MonsterEvolvingForUser> alreadyEvolving,
			String catalystUserMonsterId, List<String> userMonsterIds,
			int gemsSpent, int oilChange) {
		if (null == u) {
			log.error(String
					.format("user is null. user=%s, catalystUserMonsterId=%s, userMonsterIds=%s",
							u, catalystUserMonsterId, userMonsterIds));
			return false;
		}

		//at the moment only 3 are required to evolve a monster
		if (null == existingUserMonsters || existingUserMonsters.isEmpty()
				|| 3 != existingUserMonsters.size()) {
			log.error(String
					.format("using nonexistent monster in evolution. existing=%s, catalyst=%s, others=%s",
							existingUserMonsters, catalystUserMonsterId,
							userMonsterIds));
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_NONEXISTENT_MONSTERS);
			return false;
		}

		//at the moment only one evolution is allowed going on at any one time
		if (null != alreadyEvolving && !alreadyEvolving.isEmpty()) {
			log.error(String.format(
					"user already evolving monsters. monsters=%s",
					alreadyEvolving));
			resBuilder
					.setStatus(EvolveMonsterStatus.FAIL_MAX_NUM_EVOLUTIONS_REACHED);
			return false;
		}

		// TODO: Check minijob
		Set<String> prospectiveEvolutionMfuIds = new HashSet<String>(
				userMonsterIds);
		prospectiveEvolutionMfuIds.add(catalystUserMonsterId);

		Set<String> unavailableMfuIds = new HashSet<String>(
				alreadyEnhancing.keySet());
		unavailableMfuIds.addAll(alreadyHealing.keySet());

		//don't allow this transaction through because at least one of these monsters is
		//used in enhancing or is being healed
		if (!Collections
				.disjoint(prospectiveEvolutionMfuIds, unavailableMfuIds)) {
			log.error(String
					.format("at least one of the monsters is in healing or enhancing. enhancing=%s, healing=%s, catalyst=%s, others=%s",
							alreadyEnhancing, alreadyHealing,
							catalystUserMonsterId, userMonsterIds));
			return false;
		}

		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, oilChange,
				catalystUserMonsterId, userMonsterIds)) {
			return false;
		}

		if (!hasEnoughOil(resBuilder, u, gemsSpent, oilChange,
				catalystUserMonsterId, userMonsterIds)) {
			return false;
		}

		if (0 == gemsSpent && 0 == oilChange) {
			log.error(String.format(
					"gemsSpent=%s, oilChange=%s. Not evolving.", gemsSpent,
					oilChange));
			return false;
		}

		return true;
	}

	//if gem cost is 0 and user gems is 0, then 0 !< 0 so no error issued
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			int oilChange, String catalyst, List<String> userMonsterIds) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error(String
					.format("not enough gems. userGems=%s, gemsSpent=%s, oilChange=%s, catalyst=%s, userMonsterIds=%s",
							userGems, gemsSpent, oilChange, catalyst,
							userMonsterIds));
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder, User u, int gemsSpent,
			int oilChange, String catalyst, List<String> userMonsterIds) {
		int userOil = u.getOil();
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * oilChange;

		//if user not spending gems check if user has enough oil
		if (0 == gemsSpent && userOil < cost) {
			log.error(String
					.format("not enough oil. cost=%s, oilChange=%s, catalyst=%s, userMonsterIds=%s",
							cost, oilChange, catalyst, userMonsterIds));
			resBuilder
					.setStatus(EvolveMonsterStatus.FAIL_INSUFFICIENT_RESOURCES);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(User user, String uId, int gemsSpent,
			int oilChange, String catalystUserMonsterId,
			List<String> userMonsterIds, Timestamp clientTime,
			Map<String, Integer> money) {

		//CHARGE THE USER
		int cashChange = 0;
		int gemChange = -1 * gemsSpent;

		int numChange = user.updateRelativeCashAndOilAndGems(cashChange,
				oilChange, gemChange, 0);
		if (1 != numChange) {
			log.error(String
					.format("problem updating user stats: gemChange=%s, oilChange=%s, user=%s",
							gemChange, oilChange, user));
			return false;

		} else {
			//everything went well
			if (0 != oilChange) {
				money.put(miscMethods.oil, oilChange);
			}
			if (0 != gemsSpent) {
				money.put(miscMethods.gems, gemChange);
			}
		}

		//insert into monster_evolving_for_user table
		int numInserted = InsertUtils.get().insertIntoMonsterEvolvingForUser(
				uId, catalystUserMonsterId, userMonsterIds, clientTime);

		log.info(String.format("for monster_evolving table, numInserted=%s",
				numInserted));

		return true;
	}

	public void writeToUserCurrencyHistory(User aUser, Timestamp date,
			Map<String, Integer> moneyChange, int previousOil,
			int previousGems, String catalystUserMonsterId,
			List<String> userMonsterIds) {
		if (moneyChange.isEmpty()) {
			return;
		}
		String oil = miscMethods.oil;
		String gems = miscMethods.gems;

		String reasonForChange = ControllerConstants.UCHRFC__EVOLVING;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("(catalystId, userMonsterId1, userMonsterId2) ");
		if (moneyChange.containsKey(gems)) {
			reasonForChange = ControllerConstants.UCHRFC__SPED_UP_EVOLUTION;
		}
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		detailSb.append("(");
		detailSb.append(catalystUserMonsterId);
		detailSb.append(",");
		String one = userMonsterIds.get(0);
		detailSb.append(one);
		String two = userMonsterIds.get(1);
		detailSb.append(two);
		detailSb.append(")");

		String userId = aUser.getId();
		Map<String, Integer> previousCurrencyMap = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
		Map<String, String> changeReasonsMap = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();

		previousCurrencyMap.put(oil, previousOil);
		previousCurrencyMap.put(gems, previousGems);
		currentCurrencyMap.put(oil, aUser.getOil());
		currentCurrencyMap.put(gems, aUser.getGems());
		changeReasonsMap.put(oil, reasonForChange);
		changeReasonsMap.put(gems, reasonForChange);
		detailsMap.put(oil, detailSb.toString());
		detailsMap.put(gems, detailSb.toString());

		miscMethods.writeToUserCurrencyOneUser(userId, date, moneyChange,
				previousCurrencyMap, currentCurrencyMap, changeReasonsMap,
				detailsMap);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public MonsterEnhancingForUserRetrieveUtils2 getMonsterEnhancingForUserRetrieveUtil() {
		return monsterEnhancingForUserRetrieveUtil;
	}

	public void setMonsterEnhancingForUserRetrieveUtil(
			MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtil) {
		this.monsterEnhancingForUserRetrieveUtil = monsterEnhancingForUserRetrieveUtil;
	}

	public MonsterHealingForUserRetrieveUtils2 getMonsterHealingForUserRetrieveUtil() {
		return monsterHealingForUserRetrieveUtil;
	}

	public void setMonsterHealingForUserRetrieveUtil(
			MonsterHealingForUserRetrieveUtils2 monsterHealingForUserRetrieveUtil) {
		this.monsterHealingForUserRetrieveUtil = monsterHealingForUserRetrieveUtil;
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
