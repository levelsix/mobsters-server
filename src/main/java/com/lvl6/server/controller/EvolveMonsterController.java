package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
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
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils;
import com.lvl6.retrieveutils.MonsterHealingForUserRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class EvolveMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public EvolveMonsterController() {
		numAllocatedThreads = 3;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
		EvolveMonsterRequestProto reqProto = ((EvolveMonsterRequestEvent)event)
				.getEvolveMonsterRequestProto();

		log.info("reqProto=" + reqProto); 
		
		//get data client sent
		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		
		UserMonsterEvolutionProto uep = reqProto.getEvolution();
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int oilChange = reqProto.getOilChange();
		
		long catalystUserMonsterId = 0;
		List<Long> evolvingUserMonsterIds = new ArrayList<Long>();
		Timestamp clientTime = null;
		
		if (null != uep && reqProto.hasEvolution()) {
			log.info("uep is not null");
			catalystUserMonsterId = uep.getCatalystUserMonsterId();
			evolvingUserMonsterIds = new ArrayList<Long>(uep.getUserMonsterIdsList());
			clientTime = new Timestamp(uep.getStartTime());
		}

		//set some values to send to the client (the response proto)
		EvolveMonsterResponseProto.Builder resBuilder = EvolveMonsterResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(EvolveMonsterStatus.FAIL_OTHER);

		server.lockPlayer(senderProto.getUserId(), getClass().getSimpleName());
		try {
			int previousOil = 0;
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
			Map<Long, MonsterEnhancingForUser> alreadyEnhancing =
						MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
			
			Map<Long, MonsterHealingForUser> alreadyHealing =
    			MonsterHealingForUserRetrieveUtils.getMonstersForUser(userId);
			
			Map<Long, MonsterEvolvingForUser> alreadyEvolving =
					MonsterEvolvingForUserRetrieveUtils.getCatalystIdsToEvolutionsForUser(userId);
			
			//retrieve all the new monsters
    	Map<Long, MonsterForUser> existingUserMonsters = new HashMap<Long, MonsterForUser>();

    	//just in case uep is null, but most likely not. retrieve all the monsters used
    	//in evolution, just to make sure they exist
    	if (null != uep && reqProto.hasEvolution()) {
    		Set<Long> newIds = new HashSet<Long>();
    		newIds.add(catalystUserMonsterId);
    		newIds.addAll(evolvingUserMonsterIds);
    		existingUserMonsters = RetrieveUtils.monsterForUserRetrieveUtils().getSpecificOrAllUserMonstersForUser(userId, newIds);
    		log.info("retrieved user monsters. existingUserMonsters=" + existingUserMonsters);
    	}
			boolean legitMonster = checkLegit(resBuilder, aUser, userId, existingUserMonsters, 
					alreadyEnhancing, alreadyHealing, alreadyEvolving, catalystUserMonsterId,
					evolvingUserMonsterIds, gemsSpent, oilChange);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			
			if (legitMonster) {
				previousOil = aUser.getOil();
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, gemsSpent, oilChange,
						catalystUserMonsterId, evolvingUserMonsterIds, clientTime, money);
			}
		
			if (successful) {
				resBuilder.setStatus(EvolveMonsterStatus.SUCCESS);
			}

			EvolveMonsterResponseEvent resEvent = new EvolveMonsterResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setEvolveMonsterResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (successful) {
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(aUser, clientTime, money, previousOil, previousGems,
						catalystUserMonsterId, evolvingUserMonsterIds);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceMonster processEvent", e);
		} finally {
			server.unlockPlayer(senderProto.getUserId(), getClass().getSimpleName());   
		}
	}

	private boolean checkLegit(Builder resBuilder, User u, int userId,
			Map<Long, MonsterForUser> existingUserMonsters,
			Map<Long, MonsterEnhancingForUser> alreadyEnhancing,
			Map<Long, MonsterHealingForUser> alreadyHealing,
			Map<Long, MonsterEvolvingForUser> alreadyEvolving, long catalystUserMonsterId,
			List<Long> userMonsterIds, int gemsSpent, int oilChange) {
		if (null == u ) {
			log.error("unexpected error: user is null. user=" + u + "\t catalystUserMonsterId="+
					catalystUserMonsterId + "\t userMonsterIds=" + userMonsterIds);
			return false;
		}
		
		//at the moment only 3 are required to evolve a monster
		if (null == existingUserMonsters || existingUserMonsters.isEmpty() ||
				3 != existingUserMonsters.size()) {
			log.error("user trying to user nonexistent monster in evolution. existing=" +
					existingUserMonsters + "\t catalyst=" + catalystUserMonsterId + "\t others=" +
					userMonsterIds);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_NONEXISTENT_MONSTERS);
			return false;
		}
		
		//at the moment only one evolution is allowed going on at any one time
		if (null != alreadyEvolving && !alreadyEvolving.isEmpty()) {
			log.error("user already evolving monsters. monsters=" + alreadyEvolving);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_MAX_NUM_EVOLUTIONS_REACHED);
			return false;
		}
		
		//don't allow this transaction through because at least one of these monsters is
		//used in enhancing or is being healed
		if ((null != alreadyEnhancing && !alreadyEnhancing.isEmpty()) ||
				(null != alreadyHealing && !alreadyHealing.isEmpty())) {
			log.error("the monsters provided are in healing or enhancing. enhancing=" +
				alreadyEnhancing + "\t healing=" + alreadyHealing + "\t catalyst=" +
					catalystUserMonsterId + "\t others=" + userMonsterIds);
			return false;
		}
		
		//CHECK MONEY
		if (!hasEnoughGems(resBuilder, u, gemsSpent, oilChange, catalystUserMonsterId, userMonsterIds)) {
			return false;
		}

		if (!hasEnoughOil(resBuilder, u, gemsSpent, oilChange, catalystUserMonsterId, userMonsterIds)) {
			return false;
		}

		return true;
	}
 
	//if gem cost is 0 and user gems is 0, then 0 !< 0 so no error issued
	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent,
			int oilChange, long catalyst, List<Long> userMonsterIds) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent + "\t oilChange=" + oilChange + "\t catalyst=" +
					catalyst + "\t userMonsterIds=" + userMonsterIds);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder, User u, int gemsSpent,
			int oilChange, long catalyst, List<Long> userMonsterIds) {
		int userOil = u.getOil(); 
		//positive 'cashChange' means refund, negative means charge user
		int cost = -1 * oilChange;
		
		//if user not spending gems check if user has enough oil
		if (0 == gemsSpent && userOil < cost) {
			log.error("user error: user does not have enough cash. cost=" + cost +
					"\t oilChange=" + oilChange + "\t catalyst=" + catalyst + 
					"\t userMonsterIds=" + userMonsterIds);
			resBuilder.setStatus(EvolveMonsterStatus.FAIL_INSUFFICIENT_RESOURCES);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(User user, int uId, int gemsSpent,
			int oilChange, long catalystUserMonsterId, List<Long> userMonsterIds,
		  Timestamp clientTime, Map<String, Integer> money) {

		//CHARGE THE USER
		int cashChange = 0;
		int gemChange = -1 * gemsSpent;
		
		if (0 == gemChange && 0 == oilChange) {
			log.error("gemchange=" + gemChange + "\t oilChange=" + oilChange + "\t Not evolving.");
			return false;
		}
		
		int numChange = user.updateRelativeCashAndOilAndGems(cashChange, oilChange, gemChange); 
		if (1 != numChange) {
			log.warn("problem with updating user stats: gemChange=" + gemChange
					+ ", cashChange=" + oilChange + ", user is " + user);
		} else {
			//everything went well
			if (0 != oilChange) {
				money.put(MiscMethods.oil, oilChange);
			}
			if (0 != gemsSpent) {
				money.put(MiscMethods.gems, gemChange);
			}
		}
		
		//insert into monster_evolving_for_user table
		int numInserted = InsertUtils.get().insertIntoMonsterEvolvingForUser(uId,
				catalystUserMonsterId, userMonsterIds, clientTime);
		
		log.info("for monster_evolving table, numInserted=" + numInserted);
		
		return true;
	}

  
	public void writeToUserCurrencyHistory(User aUser, Timestamp date,
			Map<String, Integer> moneyChange, int previousOil, int previousGems,
			long catalystUserMonsterId, List<Long> userMonsterIds) {
		int userId = aUser.getId();
		
		Map<String, Integer> previousCurrencyMap = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
		Map<String, String> changeReasonsMap = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String reasonForChange = ControllerConstants.UCHRFC__ENHANCING;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("(catalystId, userMonsterId, userMonsterId");
		
		String oil = MiscMethods.oil;
		String gems = MiscMethods.gems;
		//maybe shouldn't keep track...oh well, more info hopefully is better than none
		detailSb.append("(");
		detailSb.append(catalystUserMonsterId);
		detailSb.append(",");
		long one = userMonsterIds.get(0);
		detailSb.append(one);
		long two = userMonsterIds.get(1);
		detailSb.append(two);
		
		previousCurrencyMap.put(oil, previousOil);
		previousCurrencyMap.put(gems, previousGems);
		changeReasonsMap.put(oil, reasonForChange);
		changeReasonsMap.put(gems, reasonForChange);
		detailsMap.put(oil, detailSb.toString());
		detailsMap.put(gems, detailSb.toString());
		
		MiscMethods.writeToUserCurrencyOneUser(userId, date, moneyChange, previousCurrencyMap,
				currentCurrencyMap, changeReasonsMap, detailsMap);
	}

}
