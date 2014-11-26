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
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEvolvingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class EvolutionFinishedController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;
	
	@Autowired
	protected MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;
	public EvolutionFinishedController() {
		numAllocatedThreads = 3;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
		EvolutionFinishedRequestProto reqProto = ((EvolutionFinishedRequestEvent)event)
				.getEvolutionFinishedRequestProto();
		
		log.info(String.format("reqProto=%s", reqProto));

		//get data client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		//(positive number, server will convert it to negative)
		int gemsSpent = reqProto.getGemsSpent();
		Date now = new Date();
		
		//set some values to send to the client (the response proto)
		EvolutionFinishedResponseProto.Builder resBuilder = EvolutionFinishedResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);

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
	    	resBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);
			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setEvolutionFinishedResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
	    	return;
	    }
		
		getLocker().lockPlayer(userUuid, getClass().getSimpleName());
		try {
			int previousGems = 0;
			//get whatever we need from the database
			User aUser = userRetrieveUtil.getUserById(userId);
			MonsterEvolvingForUser evolution = monsterEvolvingForUserRetrieveUtil
					.getEvolutionForUser(userId);
			
			//retrieve all the monsters used in evolution
    	Map<String, MonsterForUser> existingUserMonsters = getMonstersUsedInEvolution(userId,
    			evolution);
    	
    	log.info(String.format("evolution=%s", evolution));
    	log.info(String.format("existingUserMonsters=%s", existingUserMonsters));

			boolean legitMonster = checkLegit(resBuilder, aUser, userId, evolution,
					existingUserMonsters, gemsSpent);

			boolean successful = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			List<MonsterForUser> evolvedUserMonster = new ArrayList<MonsterForUser>();
			if (legitMonster) {
				previousGems = aUser.getGems();
				successful = writeChangesToDB(aUser, userId, now, gemsSpent,
						evolution, money, existingUserMonsters,
						evolvedUserMonster);
			}
		
			if (successful) {
				MonsterForUser evolvedMfu = evolvedUserMonster.get(0);
				FullUserMonsterProto fump = CreateInfoProtoUtils
						.createFullUserMonsterProtoFromUserMonster(evolvedMfu);
				resBuilder.setEvolvedMonster(fump);
				resBuilder.setStatus(EvolutionFinishedStatus.SUCCESS);
			}

			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setEvolutionFinishedResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(aUser, now, money, previousGems, evolution,
						evolvedUserMonster);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceMonster processEvent", e);
			resBuilder.setStatus(EvolutionFinishedStatus.FAIL_OTHER);
			EvolutionFinishedResponseEvent resEvent = new EvolutionFinishedResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setEvolutionFinishedResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
		} finally {
			getLocker().unlockPlayer(userUuid, getClass().getSimpleName());   
		}
	}
	
	private Map<String, MonsterForUser> getMonstersUsedInEvolution(String userId,
			MonsterEvolvingForUser evolution) {
		Map<String, MonsterForUser> existingUserMonsters = new HashMap<String, MonsterForUser>();
		//just  in case evolution is null, but most likely not.
		//retrieve all the monsters used in evolution, so they can be deleted
		if (null != evolution) {
			Set<String> newIds = new HashSet<String>();
			String catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
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
			MonsterEvolvingForUser evolution, Map<String, MonsterForUser> existingUserMonsters,
			int gemsSpent) {
		if (null == u || null == evolution || null == existingUserMonsters ||
				existingUserMonsters.isEmpty()) {
			log.error(String.format(
				"user, evolution, or existingMonsters is null. user=%s, evolution=%s, existingMonsters=%s",
					u, evolution, existingUserMonsters));
			return false;
		}
		
		//check to make sure these monsters still exist
		String catalystUserMonsterId = evolution.getCatalystMonsterForUserId();
		String one = evolution.getMonsterForUserIdOne();
		String two = evolution.getMonsterForUserIdTwo();
		if (!existingUserMonsters.containsKey(catalystUserMonsterId) ||
				!existingUserMonsters.containsKey(one) || !existingUserMonsters.containsKey(two)) {
			log.error(String.format(
				"one of the monsters in an evolution is missing. evolution=%s, existingUserMonsters=%s",
				evolution, existingUserMonsters));
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
		if (userGems < Math.abs(gemsSpent)) {
			log.error(String.format(
				"not enough gems. userGems=%s, gemsSpent=%s, evolution=%s",
				userGems, gemsSpent, evolution));
			resBuilder.setStatus(EvolutionFinishedStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}
		return true;
	}


	//List<MonsterForUser> userMonsters will be populated with the evolved monster
	private boolean writeChangesToDB(User user, String uId, Date now, int gemsSpent,
			MonsterEvolvingForUser mefu, Map<String, Integer> money,
			Map<String, MonsterForUser> idsToUserMonsters, List<MonsterForUser> userMonsters) {

		//CHARGE THE USER IF HE SPED UP THE EVOLUTION
		if (0 != gemsSpent) {
			int oilChange = 0;
			int cashChange = 0;
			int gemChange = -1 * gemsSpent;
			int numChange = user.updateRelativeCashAndOilAndGems(cashChange, oilChange, gemChange); 
			if (1 != numChange) {
				log.warn(String.format(
					"problem with updating user stats: gemChange=%s, cashChange=%s, user is %s",
					gemChange, oilChange, user));
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
		List<String> userMonsterIds = new ArrayList<String>();
		String catalystUserMonsterId = mefu.getCatalystMonsterForUserId();
		userMonsterIds.add(catalystUserMonsterId);
		String uMonsterIdOne = mefu.getMonsterForUserIdOne();
		userMonsterIds.add(uMonsterIdOne);
		String uMonsterIdTwo = mefu.getMonsterForUserIdTwo();
		userMonsterIds.add(uMonsterIdTwo);
		int num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
		log.info(String.format("num monsterForUser deleted: %s", num));
		
		//delete the evolution
		num = DeleteUtils.get().deleteMonsterEvolvingForUser(catalystUserMonsterId,
				uMonsterIdOne, uMonsterIdTwo, uId);
		log.info(String.format("num evolutions deleted: %s", num));
		
		//insert the COMPLETE evolved monster into monster for user
		//get evolved version of monster
		MonsterForUser evolvedUserMonster = createEvolvedMonster(uId, now, uMonsterIdOne,
				idsToUserMonsters);
		userMonsters.add(evolvedUserMonster);

		String sourceOfPieces = createSourceOfPieces(catalystUserMonsterId, uMonsterIdOne, uMonsterIdTwo);
		List<String> evovlvedMfuId = InsertUtils.get().insertIntoMonsterForUserReturnIds(uId,
				userMonsters, sourceOfPieces, now); 
		
		//set the evolved monster for user's id
		if (null != evovlvedMfuId && !evovlvedMfuId.isEmpty()) {
			String mfuId = evovlvedMfuId.get(0);
			evolvedUserMonster.setId(mfuId);
		}
		
		log.info(String.format("evolvedUserMonster=%s", evolvedUserMonster));
		log.info(String.format("userMonsters=%s", userMonsters));
		
		return true;
	}
	
	private MonsterForUser createEvolvedMonster(String uId, Date now, String uMonsterIdOne,
			Map<String, MonsterForUser> idsToUserMonsters)
	{
		MonsterForUser unevolvedMonster = idsToUserMonsters.get(uMonsterIdOne);
		int monsterId = unevolvedMonster.getMonsterId();
		Monster evolvedMonster = MonsterRetrieveUtils.getEvolvedFormForMonster(monsterId);
		int numPieces = evolvedMonster.getNumPuzzlePieces();
		boolean hasAllPieces = true;
		boolean isComplete = true;
		MonsterForUser mfu = MonsterStuffUtils.createNewUserMonster(uId, numPieces,
				evolvedMonster, now, hasAllPieces, isComplete);
		
		return mfu;
	}
	
	private String createSourceOfPieces(String catalystUserMonsterId,
		String uMonsterIdOne, String uMonsterIdTwo)
	{
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
		String gems = MiscMethods.gems;
		
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
		
		MiscMethods.writeToUserCurrencyOneUser(userId, date, moneyChange, previousCurrencyMap,
				currentCurrencyMap, changeReasonsMap, detailsMap);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterEvolvingForUserRetrieveUtils2 getMonsterEvolvingForUserRetrieveUtil()
	{
		return monsterEvolvingForUserRetrieveUtil;
	}

	public void setMonsterEvolvingForUserRetrieveUtil(
		MonsterEvolvingForUserRetrieveUtils2 monsterEvolvingForUserRetrieveUtil )
	{
		this.monsterEvolvingForUserRetrieveUtil = monsterEvolvingForUserRetrieveUtil;
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

}
