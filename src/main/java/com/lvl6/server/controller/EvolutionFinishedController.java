package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import com.lvl6.events.request.EvolutionFinishedRequestEvent;
import com.lvl6.events.response.EvolutionFinishedResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedRequestProto;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto.EvolutionFinishedStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class EvolutionFinishedController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public EvolutionFinishedController() {
		numAllocatedThreads = 3;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EvolutionFinishedRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_EVOLVE_MONSTER_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		EvolutionFinishedRequestProto reqProto = ((EvolutionFinishedRequestEvent)event)
				.getEvolutionFinishedRequestProto();

		//get data client sent
		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		//(positive number, server will convert it to negative)
		int gemsSpent = reqProto.getGemsSpent();
		Date now = new Date();
		

		//set some values to send to the client (the response proto)
		EvolutionFinishedResponseProto.Builder resBuilder = EvolutionFinishedResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);

		server.lockPlayer(senderProto.getUserId(), getClass().getSimpleName());
		try {
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
			MonsterEvolvingForUser evolution = MonsterEvolvingForUserRetrieveUtils
					.getEvolutionForUser(userId);
			
			//retrieve all the monsters used in evolution
    	Map<Long, MonsterForUser> existingUserMonsters = getMonstersUsedInEvolution(userId,
    			evolution);

			boolean legitMonster = checkLegit(resBuilder, aUser, userId, evolution,
					existingUserMonsters, gemsSpent);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			List<MonsterForUser> evolvedUserMonster = new ArrayList<MonsterForUser>();
			if (legitMonster) {
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, now, gemsSpent, evolution, money,
						existingUserMonsters, evolvedUserMonster);
			}
		
			if (successful) {
				resBuilder.setStatus(EvolutionFinishedStatus.SUCCESS);
			}

			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setEvolutionFinishedResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (successful) {
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(aUser, now, money, previousGems, evolution,
						evolvedUserMonster);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceMonster processEvent", e);
		} finally {
			server.unlockPlayer(senderProto.getUserId(), getClass().getSimpleName());   
		}
	}
	
	private Map<Long, MonsterForUser> getMonstersUsedInEvolution(int userId,
			MonsterEvolvingForUser evolution) {
		Map<Long, MonsterForUser> existingUserMonsters = new HashMap<Long, MonsterForUser>();
		//just  in case evolution is null, but most likely not.
		//retrieve all the monsters used in evolution, so they can be deleted
		if (null != evolution) {
			Set<Long> newIds = new HashSet<Long>();
			long catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
			long userMonsterIdOne = evolution.getMonsterForUserIdOne();
			long userMonsterIdTwo = evolution.getMonsterForUserIdTwo();
			newIds.add(catalystUserMonsterId);
			newIds.add(userMonsterIdOne);
			newIds.add(userMonsterIdTwo);
			existingUserMonsters = RetrieveUtils.monsterForUserRetrieveUtils()
					.getSpecificOrAllUserMonstersForUser(userId, newIds);
		}
		
		return existingUserMonsters;
	}

	private boolean checkLegit(Builder resBuilder, User u, int userId,
			MonsterEvolvingForUser evolution, Map<Long, MonsterForUser> existingUserMonsters,
			int gemsSpent) {
		if (null == u || null == evolution || null == existingUserMonsters ||
				existingUserMonsters.isEmpty()) {
			log.error("unexpected error: user, evolution, or existingMonsters is null. user=" +
					u + ",\t evolution="+ evolution + "\t existingMonsters=" + existingUserMonsters);
			return false;
		}
		
		//check to make sure these monsters still exist
		long catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
		long one = evolution.getMonsterForUserIdOne();
		long two = evolution.getMonsterForUserIdTwo();
		if (!existingUserMonsters.containsKey(catalystUserMonsterId) ||
				!existingUserMonsters.containsKey(one) || !existingUserMonsters.containsKey(two)) {
			log.error("one of the monsters in an evolution is missing. evolution=" + evolution +
					"\t existingUserMonsters=" + existingUserMonsters);
			resBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);
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
		if (userGems < gemsSpent) {
			log.error("user error: user does not have enough gems. userGems=" + userGems +
					"\t gemsSpent=" + gemsSpent + "\t evolution=" + evolution);
			resBuilder.setStatus(EvolutionFinishedStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}


	//List<MonsterForUser> userMonsters will be populated with the evolved monster
	private boolean writeChangesToDB(User user, int uId, Date now, int gemsSpent,
			MonsterEvolvingForUser mefu, Map<String, Integer> money,
			Map<Long, MonsterForUser> idsToUserMonsters, List<MonsterForUser> userMonsters) {

		//CHARGE THE USER
		if (0 != gemsSpent) {
			int oilChange = 0;
			int cashChange = 0;
			int gemChange = -1 * gemsSpent;
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
		}
		//delete the monsters used in the evolution
		List<Long> userMonsterIds = new ArrayList<Long>();
		long catalystUserMonsterId = mefu.getCatalystMonsterForUserId();
		userMonsterIds.add(catalystUserMonsterId);
		long uMonsterIdOne = mefu.getMonsterForUserIdOne();
		userMonsterIds.add(uMonsterIdOne);
		long uMonsterIdTwo = mefu.getMonsterForUserIdTwo();
		userMonsterIds.add(uMonsterIdTwo);
		int num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
		log.info("num monsterForUser deleted: " + num);
		
		//delete the evolution
		num = DeleteUtils.get().deleteMonsterEvolvingForUser(catalystUserMonsterId,
				uMonsterIdOne, uMonsterIdOne, uId);
		log.info("num evolutions deleted: " + num);
		
		//insert the COMPLETE evolved monster into monster for user
		//get evolved version of monster
		MonsterForUser evolvedUserMonster = createEvolvedMonster(uId, now, uMonsterIdOne,
				idsToUserMonsters);
		userMonsters.add(evolvedUserMonster);

		String sourceOfPieces = createSourceOfPieces(catalystUserMonsterId, uMonsterIdOne, uMonsterIdTwo);
		List<Long> evovlvedMfuId = InsertUtils.get().insertIntoMonsterForUserReturnIds(uId,
				userMonsters, sourceOfPieces, now); 
		
		//set the evolved monster for user's id
		if (null != evovlvedMfuId && !evovlvedMfuId.isEmpty()) {
			long mfuId = evovlvedMfuId.get(0);
			evolvedUserMonster.setId(mfuId);
		}
		
		return true;
	}
	
	private MonsterForUser createEvolvedMonster(int uId, Date now, long uMonsterIdOne,
			Map<Long, MonsterForUser> idsToUserMonsters) {
		MonsterForUser unevolvedMonster = idsToUserMonsters.get(uMonsterIdOne);
		int monsterId = unevolvedMonster.getMonsterId();
		Monster evolvedMonster = MonsterRetrieveUtils.getEvolvedFormForMonster(monsterId);
		int numPieces = evolvedMonster.getNumPuzzlePieces();
		boolean isComplete = true;
		MonsterForUser mfu = MonsterStuffUtils.createNewUserMonster(uId, numPieces,
				evolvedMonster, now, isComplete);
		
		return mfu;
	}
	
	private String createSourceOfPieces(long catalystUserMonsterId, long uMonsterIdOne,
			long uMonsterIdTwo) {
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
			MonsterEvolvingForUser evolution, List<MonsterForUser> evolvedUserMonsterList) {
		if (moneyChange.isEmpty()) {
			return;
		}
		
		int userId = aUser.getId();
		Timestamp date = new Timestamp((now.getTime()));
		long catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
		long one = evolution.getMonsterForUserIdOne();
		long two = evolution.getMonsterForUserIdTwo();
		MonsterForUser evolved = evolvedUserMonsterList.get(0);
		long evolvedId = evolved.getId();
		
		Map<String, Integer> previousCurrencyMap = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencyMap = new HashMap<String, Integer>();
		Map<String, String> changeReasonsMap = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String reasonForChange = ControllerConstants.UCHRFC__SPED_UP_EVOLUTION;
		StringBuilder detailSb = new StringBuilder();
		detailSb.append("(catalystId, userMonsterId, userMonsterId, evolvedMonsterId)");
		
		String gems = MiscMethods.gems;
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
		
		previousCurrencyMap.put(gems, previousGems);
		changeReasonsMap.put(gems, reasonForChange);
		detailsMap.put(gems, detailSb.toString());
		
		MiscMethods.writeToUserCurrencyOneUser(userId, date, moneyChange, previousCurrencyMap,
				currentCurrencyMap, changeReasonsMap, detailsMap);
	}

}
