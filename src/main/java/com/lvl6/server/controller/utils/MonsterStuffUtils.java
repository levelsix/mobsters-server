package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Monster;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;


public class MonsterStuffUtils {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	//extract and return the ids from the UserMonsterCurrentHealthProtos, also
	//return mapping of userMonsterIdToExpectedHealth
  public static List<Long> getUserMonsterIds(List<UserMonsterCurrentHealthProto> umchpList,
  		Map<Long, Integer> userMonsterIdToExpectedHealth) {
  	List<Long> idList = new ArrayList<Long>();
  	
  	if (null == umchpList) {
  		return idList;
  	}
  	
  	for(UserMonsterCurrentHealthProto umchp : umchpList) {
  		long id = umchp.getUserMonsterId();
  		idList.add(id);
  		int health = umchp.getCurrentHealth();
  		userMonsterIdToExpectedHealth.put(id, health);
  	}
  	return idList;
  }
  
  public static List<Long> getUserMonsterIds(List<FullUserMonsterProto> mfuList) {
  	List<Long> idList = new ArrayList<Long>();
  	
  	if (null == mfuList) {
  		return idList;
  	}
  	
  	for (FullUserMonsterProto fump : mfuList) {
  		long id = fump.getUserMonsterId();
  		idList.add(id);
  	}
  	return idList;
  }
  
  //transforming list to map with key = monsterForUserId.
  public static Map<Long, UserMonsterHealingProto> convertIntoUserMonsterIdToUmhpProtoMap(
  		List<UserMonsterHealingProto> umhpList) {
  	Map<Long, UserMonsterHealingProto> returnMap = new HashMap<Long, UserMonsterHealingProto>();
  	if (null == umhpList) {
  		return returnMap;
  	}
  	for (UserMonsterHealingProto umhp : umhpList) {
  		long id = umhp.getUserMonsterId();
  		returnMap.put(id, umhp);
  	}
  	
  	return returnMap;
  }
  
  public static Map<Long, UserEnhancementItemProto> convertIntoUserMonsterIdToUeipProtoMap(
  		List<UserEnhancementItemProto> ueipList) {
  	Map<Long, UserEnhancementItemProto> returnMap = new HashMap<Long, UserEnhancementItemProto>();
  	if(null == ueipList) {
  		return returnMap;
  	}
  	for (UserEnhancementItemProto ueip : ueipList) {
  		long id = ueip.getUserMonsterId();
  		returnMap.put(id, ueip);
  	}
  	
  	return returnMap;
  }
  
  /*
   * selected monsters (the second argument) might be modified, nothing is done if
   * selected monsters is empty.
   */
  public static void retainValidMonsters(Set<Long> domain,  Map<Long, ?> selectedMonsters,
  		boolean keepThingsInDomain, boolean keepThingsNotInDomain) {
  	Set<Long> selectedIds = selectedMonsters.keySet();
  	selectedIds = new HashSet<Long>(selectedIds);
  	
  	log.info("domain=" + domain + "\t selectedMonsters=" + selectedMonsters);
  	for (Long selectedId : selectedIds) {
  		if (domain.contains(selectedId) && keepThingsInDomain) {
  			continue;
  		}
  		if (!domain.contains(selectedId) && keepThingsNotInDomain) {
  			continue;
  		}
  		//since selectedId isn't in the domain and want to keep things in domain
  		//or is in the domain and want to keep things not in domain, remove it
  		Object umhp = selectedMonsters.remove(selectedId);
  		log.warn("Not retaining. object=" + umhp + ", keepThingsInDomain=" + keepThingsInDomain +
  				"; keepThingsNotInDomain=" + keepThingsNotInDomain);
  	}
  }
  
  /*
   * selected monsters (the second argument) might be modified
   */
  public static void retainValidMonsterIds(Set<Long> existing, List<Long> ids) {
//  	ids.add(123456789L);
//  	log.info("existing=" + existing + "\t ids=" + ids);
  	
  	List<Long> copyIds = new ArrayList<Long>(ids);
  	// remove the invalid ids from ids client sent 
  	// (modifying argument so calling function doesn't have to do it)
  	ids.retainAll(existing);
  	
  	if (copyIds.size() != ids.size()) {
  		//client asked for invalid ids
  		log.warn("client asked for some invalid ids. asked for ids=" + copyIds + 
  				"\t existingIds=" + existing + "\t remainingIds after purge =" + ids);
  	}
  }

  public static List<MonsterHealingForUser> convertToMonsterHealingForUser(
  		int userId, Map<Long, UserMonsterHealingProto> protos) {
  	
  	List<MonsterHealingForUser> nonProtos = new ArrayList<MonsterHealingForUser>();
  	
  	for(UserMonsterHealingProto umhp: protos.values()) {
  		Long monsterForUserId = umhp.getUserMonsterId();
  		
  		//maybe client not supposed to always set this?
  		Date queuedTime = null;
  		if (umhp.hasQueuedTimeMillis() && umhp.getQueuedTimeMillis() > 0) {
  			queuedTime = new Date(umhp.getQueuedTimeMillis());
  		}
  		
//  		int userStructHospitalId = umhp.getUserHospitalStructId();
  		float healthProgress = umhp.getHealthProgress();
  		int priority = umhp.getPriority();
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		MonsterHealingForUser mhfu = new MonsterHealingForUser(userId, monsterForUserId,
  				queuedTime, healthProgress, priority);
  		nonProtos.add(mhfu);
  	}
  	
  	return nonProtos;
  }
  
  public static List<MonsterEnhancingForUser> convertToMonsterEnhancingForUser(
  		int userId, Map<Long, UserEnhancementItemProto> protos) {
  	
  	List<MonsterEnhancingForUser> nonProtos = new ArrayList<MonsterEnhancingForUser>();
  	
  	for(UserEnhancementItemProto ueip: protos.values()) {
  		Long monsterForUserId = ueip.getUserMonsterId();
  		long startTimeMillis = ueip.getExpectedStartTimeMillis();
  		Date expectedStartTime;
  		
  		if (!ueip.hasExpectedStartTimeMillis() || startTimeMillis <= 0) {
  			expectedStartTime = null;
  		} else {
  			expectedStartTime = new Date(startTimeMillis);
  		}
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		int enhancingCost = ueip.getEnhancingCost();
  		
  		MonsterEnhancingForUser mefu = new MonsterEnhancingForUser(userId,
  				monsterForUserId, expectedStartTime, enhancingCost);//, queuedTime);
  		nonProtos.add(mefu);
  	}
  	
  	return nonProtos;
  }
  
  // ASSUMPTION: WHATEVER MONSTER ID EXISTS IN  monsterIdToIncompleteUserMonster
  // THERE IS A CORRESPONDING ENTRY IN monsterIdToQuantity
  // returns the remaining quantities for each monster id after "completing"
  // a user_monster 
  // ALSO MODIFIES the monsters in monsterIdToIncompleteUserMonster
  //monsterIdToQuantity will not be modified
  public static Map<Integer, Integer> completeMonsterForUserFromMonsterIdsAndQuantities(
  		Map<Integer, MonsterForUser> monsterIdToIncompleteUserMonster,
  		Map<Integer, Integer> monsterIdToQuantity) {
  	
  	Map<Integer, Integer> monsterIdToNewQuantity = new HashMap<Integer, Integer>();
  	//IF NO INCOMLETE MONSTERFORUSER EXISTS, THEN METHOD JUST
  	//RETURNS A COPY OF THE MAP monsterIdToQuantity
  	if (monsterIdToIncompleteUserMonster.isEmpty()) {
  		monsterIdToNewQuantity.putAll(monsterIdToQuantity);
  		return monsterIdToNewQuantity;
  	}
  	
  	//retrieve the monsters so we know how much is needed to complete it
  	Set<Integer> incompleteMonsterIds = monsterIdToIncompleteUserMonster.keySet();
  	Map<Integer, Monster> monsterIdsToMonsters =  MonsterRetrieveUtils
  			.getMonstersForMonsterIds(incompleteMonsterIds);
  	

  	//for each incomplete user monster, try to complete it with the 
  	//available quantity in the monsterIdToQuantity map,
  	//monsterIdToIncompleteUserMonster will be modified
  	for (int monsterId : incompleteMonsterIds) {
  		MonsterForUser mfu = monsterIdToIncompleteUserMonster.get(monsterId);
  		int numPiecesAvailable = monsterIdToQuantity.get(monsterId);
  		Monster monzter = monsterIdsToMonsters.get(monsterId);
  		
  		int newPiecesAvailable = completeMonsterForUserFromQuantity(
  				mfu, numPiecesAvailable, monzter);
  		
  		if (newPiecesAvailable > 0) {
  			monsterIdToNewQuantity.put(monsterId, newPiecesAvailable);
  		}
  	}
  	
  	return monsterIdToNewQuantity;
  }
  
  //(breaking the abstraction) MonsterForUser mfu will be modified.
  //returns the number of pieces remaining after using up the pieces
  //available in order to try completing the monster_for_user
  public static int completeMonsterForUserFromQuantity(MonsterForUser mfu,
  		int numPiecesAvailable, Monster monzter) {
  	int numPiecesRemaining = 0;
  	
  	int numPiecesForCompletion = monzter.getNumPuzzlePieces();
  	int existingNumPieces = mfu.getNumPieces();
  	
  	int updatedExistingPieces = existingNumPieces + numPiecesAvailable;

  	//two scenarios: 
  	//1) there are pieces remaining after trying to complete monsterForUser
  	//2) no pieces remaining after trying to complete monsterForUser
  	if (updatedExistingPieces > numPiecesForCompletion) {
  		//there are more than enough pieces to complete this monster
  		//"complete" the monsterForUser
  		mfu.setNumPieces(numPiecesForCompletion);

  		//calculate the remaining pieces remaining
  		numPiecesRemaining = updatedExistingPieces - numPiecesForCompletion;
  		
  	} else {
  		//no extra pieces remaining after trying to complete monsterForUser
  		mfu.setNumPieces(updatedExistingPieces); 
  	}
  	
  	//if monster for user has max number of pieces, and minutes to combine is 0
  	//it should be marked as complete
  	int mfuNewNumPieces = mfu.getNumPieces();
  	int numMinutesForCompletion = monzter.getMinutesToCombinePieces();
  	if (mfuNewNumPieces == numPiecesForCompletion && 0 == numMinutesForCompletion) {
  		mfu.setComplete(true);
  	}
  	
  	return numPiecesRemaining;
  }
  
  
  public static List<MonsterForUser> createMonstersForUserFromQuantities(
  		int userId, Map<Integer, Integer> monsterIdsToQuantities, Date combineStartTime) {
  	List<MonsterForUser> returnList = new ArrayList<MonsterForUser>();
  	
  	if(monsterIdsToQuantities.isEmpty()) {
  		return returnList;
  	}
  	
  	Set<Integer> monsterIds = monsterIdsToQuantities.keySet();
  	Map<Integer, Monster> monsterIdsToMonsters =  MonsterRetrieveUtils
  			.getMonstersForMonsterIds(monsterIds);
  	
  	//for each monster and quantity, create as many complete and incomplete
  	//user monsters
  	for (int monsterId : monsterIds) {
  		Monster monzter = monsterIdsToMonsters.get(monsterId);
  		int quantity = monsterIdsToQuantities.get(monsterId);
  		
  		log.info("for monsterId=" + monsterId + "\t and for quantity=" + quantity +
  				"\t creating some number of a specific monster for a user. monster=" +
  				monzter);
  		
  		List<MonsterForUser> newUserMonsters = createMonsterForUserFromQuantity(
  				userId, monzter, quantity, combineStartTime);
  		log.info("some amount of a certain monster created. monster(s)=" +
  				newUserMonsters);
  		
  		returnList.addAll(newUserMonsters);
  	}
  	
  	return returnList;
  }
  
  //for A GIVEN MONSTER and QUANTITY of pieces, create as many of this monster as possible
  //THE ID PROPERTY FOR ALL these monsterForUser will be a useless value, say 0
  public static List<MonsterForUser> createMonsterForUserFromQuantity(int userId,
  		Monster monzter, int quantity, Date combineStartTime) {
  	List<MonsterForUser> returnList = new ArrayList<MonsterForUser>();
  	
  	Map<Integer, MonsterLevelInfo> levelToInfo = MonsterLevelInfoRetrieveUtils
  			.getMonsterLevelInfoForMonsterId(monzter.getId());
  	MonsterLevelInfo info = levelToInfo.get(1); //not sure if this is right
  	
  	
  	int numPiecesForCompletion = monzter.getNumPuzzlePieces();
  	
  	//TODO: FIGURE OUT IF THESE ARE TEH CORRECT DEFAULT VALUES
  	//default values for creating a monster for user
  	int id = 0;
  	int monsterId = monzter.getId();
  	int currentExp = 0; //not sure if this is right
  	int currentLvl = 1; //not sure if this is right
  	int currentHealth = info.getHp();
  	
  	int teamSlotNum = 0;
  	String sourceOfPieces = "";
  	
  	//decrement quantity by number_of_pieces_to_create_a_monster and
  	//this represents one monster_for_user
  	for (; quantity > 0; quantity -= numPiecesForCompletion) {
  		boolean isComplete = false;
  		int numPieces = 0;
  		
  		if (quantity >= numPiecesForCompletion) {
  			numPieces = numPiecesForCompletion;
  			
  			//since there's enough pieces to create a whole monster, if the time
  			//it takes to combine a monster is 0 then the monster is complete
  			if (monzter.getMinutesToCombinePieces() == 0) {
  				isComplete = true;
  			}
  			
  		} else {
  			//this happens only when there isn't enough pieces left to make a whole
  			//monster 
  			numPieces = quantity; 
  		}
  		
  		
  		MonsterForUser mfu = new MonsterForUser(id, userId, monsterId,
  				currentExp, currentLvl, currentHealth, numPieces, isComplete,
  				combineStartTime, teamSlotNum, sourceOfPieces);
  		returnList.add(mfu);
  	}
  	return returnList;
  }
  
  
  
  //METHOD TO REWARD A USER WITH SOME MONSTERS
  public static List<FullUserMonsterProto> updateUserMonsters(int userId,
  		Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
  		Date combineStartDate) {
  	log.info("the monster pieces the user gets: " + monsterIdToNumPieces);
  	
  	if (monsterIdToNumPieces.isEmpty()) {
  		return new ArrayList<FullUserMonsterProto>();
  	}
  	
  	//for all the monster pieces the user will receive, see if he already has any
  	//retrieve all of user's incomplete monsters that have these monster ids 
  	Set<Integer> droppedMonsterIds = monsterIdToNumPieces.keySet();
  	
  	Map<Integer, MonsterForUser> monsterIdsToIncompletes =  RetrieveUtils
  			.monsterForUserRetrieveUtils()
  			.getIncompleteMonstersWithUserAndMonsterIds(userId, droppedMonsterIds);
  	
  	//take however many pieces necessary from monsterIdToNumPieces to
  	//complete these incomplete monsterForUsers
  	//monsterIdsToIncompletes will be modified
  	Map<Integer, Integer> monsterIdToRemainingPieces = 
  			completeMonsterForUserFromMonsterIdsAndQuantities(
  					monsterIdsToIncompletes, monsterIdToNumPieces);
  	
  	//UPDATE THESE INCOMPLETE MONSTERS, IF ANY. SINCE UPDATING, UPDATE THE
  	//combineStartTime
  	List<MonsterForUser> dirtyMonsterForUserList = 
  			new ArrayList<MonsterForUser>(monsterIdsToIncompletes.values());
  	if (!dirtyMonsterForUserList.isEmpty()) {
  		log.info("the monsters that are updated: " + dirtyMonsterForUserList);
  		UpdateUtils.get().updateUserMonsterNumPieces(userId, dirtyMonsterForUserList,
  				sourceOfPieces, combineStartDate);
  	}
  	
  	//monsterIdToRemainingPieces now contains all the new monsters
  	//the user will get. SET THE combineStartTime
  	List<MonsterForUser> newMonsters = createMonstersForUserFromQuantities(
  			userId, monsterIdToRemainingPieces, combineStartDate);
  	if (!newMonsters.isEmpty()) {
  		log.info("the monsters that are new: " + newMonsters);
  		List<Long> monsterForUserIds = InsertUtils.get().insertIntoMonsterForUserReturnIds(
  				userId, newMonsters, sourceOfPieces, combineStartDate);
  		
  		//set these ids into the list "newMonsters"
  		for (int i = 0; i < monsterForUserIds.size(); i++) {
  			MonsterForUser newMonster = newMonsters.get(i);
  			long monsterForUserId = monsterForUserIds.get(i);
  			newMonster.setId(monsterForUserId);
  		}
  	}
  	
  	//create the return value
  	List<MonsterForUser> newOrUpdated = new ArrayList<MonsterForUser>();
  	newOrUpdated.addAll(dirtyMonsterForUserList);
  	newOrUpdated.addAll(newMonsters);
  	List<FullUserMonsterProto> protos = CreateInfoProtoUtils
  			.createFullUserMonsterProtoList(newOrUpdated);
  	
  	return protos;
  }
  
  //returns user monster ids
  public static List<Long> getWholeButNotCombinedUserMonsters(
  		Map<Long, MonsterForUser> idsToUserMonsters) {
  	List<Long> wholeUserMonsterIds = new ArrayList<Long>();
  	
  	Set<Integer> uniqMonsterIds = new HashSet<Integer>();
  	for (MonsterForUser mfu : idsToUserMonsters.values()) {
  		uniqMonsterIds.add(mfu.getMonsterId());
  	}
  	//get the monsters in order to determine num pieces to be considered whole
  	List<Integer> monsterIds = new ArrayList<Integer>(uniqMonsterIds);
  	Map<Integer, Monster> idsToMonsters = 
  			MonsterRetrieveUtils.getMonstersForMonsterIds(monsterIds);
  	
  	//loop through user monsters and monsters and see if user monster is whole
  	for (long userMonsterId : idsToUserMonsters.keySet()) {
  		MonsterForUser mfu = idsToUserMonsters.get(userMonsterId);
  		int monsterId = mfu.getMonsterId();
  		Monster monzter = idsToMonsters.get(monsterId);

  		if (mfu.isComplete()) {
  			//want only incomplete monsters that are whole
  			//(all pieces haven't been combined yet)
  			continue;
  		}
  		
  		int numPiecesToBeWhole = monzter.getNumPuzzlePieces();
  		int userMonsterPieces = mfu.getNumPieces();
  		if (userMonsterPieces > numPiecesToBeWhole) {
  			log.warn("userMonster has more than the max num pieces. userMonster=" +
  					mfu + "\t monster=" + monzter);
  		} else if (userMonsterPieces == numPiecesToBeWhole) {
  			wholeUserMonsterIds.add(userMonsterId);
  		}
  	}
  	
  	return wholeUserMonsterIds;
  }
  
  public static Map<Long, Integer> convertToMonsterForUserIdToCashAmount(
  		List<MinimumUserMonsterSellProto> userMonsters) {
  	Map<Long, Integer> idToCashAmount = new HashMap<Long, Integer>();
  	
  	for (MinimumUserMonsterSellProto mumsp : userMonsters) {
  		long userMonsterId = mumsp.getUserMonsterId();
  		int cashAmount = mumsp.getCashAmount();
  		
  		idToCashAmount.put(userMonsterId, cashAmount);
  	}
  	
  	return idToCashAmount;
  }
  
  public static float sumProbabilities(List<TaskStageMonster> taskStageMonsters) {
  	float sumProbabilities = 0.0f;
  	
  	for (TaskStageMonster tsm : taskStageMonsters) {
  		sumProbabilities += tsm.getChanceToAppear();
  	}
  	
  	return sumProbabilities;
  }
  
  public static MonsterForUser createNewUserMonster(int userId, int numPieces,
  		Monster monster, Date now, boolean isComplete) {
  	
  	int monsterId = monster.getId();
  	
  	Map<Integer, MonsterLevelInfo> levelToInfo = MonsterLevelInfoRetrieveUtils
  			.getMonsterLevelInfoForMonsterId(monsterId);
  	MonsterLevelInfo info = levelToInfo.get(1); //not sure if this is right
  	
  	
  	int id = 0;
  	int currentExp = 0;
  	int currentLvl = 1;
  	int currentHealth = info.getHp();
  	int teamSlotNum = 0;
  	String sourceOfPieces = "";
  	MonsterForUser mfu = new MonsterForUser(id, userId, monsterId, currentExp,
  			currentLvl, currentHealth, numPieces, isComplete, now,
  			teamSlotNum, sourceOfPieces);
  	
  	return mfu;
  }
  
  //gather up the user monster ids in the evolution(s)
  public static Set<Long> getUserMonsterIdsUsedInEvolution(MonsterEvolvingForUser mefu,
  		Map<Long, MonsterEvolvingForUser> catalystIdToMonsterEvolutionForUser) {
  	Set<Long> userMonsterIds = new HashSet<Long>();

  	if (null != mefu) {
  		long id = mefu.getCatalystMonsterForUserId();
  		userMonsterIds.add(id);
  		id = mefu.getMonsterForUserIdOne();
  		userMonsterIds.add(id);
  		id = mefu.getMonsterForUserIdTwo();
  		userMonsterIds.add(id);

  	} else if (null != catalystIdToMonsterEvolutionForUser &&
  			!catalystIdToMonsterEvolutionForUser.isEmpty()){
  		//loop through each MonsterEvolvingForUser gathering up the MonsterForUserIds
  		for (MonsterEvolvingForUser mefyou : catalystIdToMonsterEvolutionForUser.values()) {
  			long id = mefyou.getCatalystMonsterForUserId();
  			userMonsterIds.add(id);
  			id = mefyou.getMonsterForUserIdOne();
  			userMonsterIds.add(id);
  			id = mefyou.getMonsterForUserIdTwo();
  			userMonsterIds.add(id);
  		}
  	}

  	return userMonsterIds;
  }
  
}
