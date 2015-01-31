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

import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.proto.SharedEnumConfigProto.Element;
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
  public static List<String> getUserMonsterIds(List<UserMonsterCurrentHealthProto> umchpList,
  		Map<String, Integer> userMonsterIdToExpectedHealth) {
  	List<String> idList = new ArrayList<String>();
  	
  	if (null == umchpList) {
  		return idList;
  	}
  	
  	for(UserMonsterCurrentHealthProto umchp : umchpList) {
  		String id = umchp.getUserMonsterUuid();
  		idList.add(id);
  		int health = umchp.getCurrentHealth();
  		userMonsterIdToExpectedHealth.put(id, health);
  	}
  	return idList;
  }
  
  public static List<String> getUserMonsterIds(List<FullUserMonsterProto> mfuList) {
  	List<String> idList = new ArrayList<String>();
  	
  	if (null == mfuList) {
  		return idList;
  	}
  	
  	for (FullUserMonsterProto fump : mfuList) {
  		String id = fump.getUserMonsterUuid();
  		idList.add(id);
  	}
  	return idList;
  }
  
  //transforming list to map with key = monsterForUserId.
  public static Map<String, UserMonsterHealingProto> convertIntoUserMonsterIdToUmhpProtoMap(
  		List<UserMonsterHealingProto> umhpList) {
  	Map<String, UserMonsterHealingProto> returnMap = new HashMap<String, UserMonsterHealingProto>();
  	if (null == umhpList) {
  		return returnMap;
  	}
  	for (UserMonsterHealingProto umhp : umhpList) {
  		String id = umhp.getUserMonsterUuid();
  		returnMap.put(id, umhp);
  	}
  	
  	return returnMap;
  }
  
  public static Map<String, UserEnhancementItemProto> convertIntoUserMonsterIdToUeipProtoMap(
  		List<UserEnhancementItemProto> ueipList) {
  	Map<String, UserEnhancementItemProto> returnMap = new HashMap<String, UserEnhancementItemProto>();
  	if(null == ueipList) {
  		return returnMap;
  	}
  	for (UserEnhancementItemProto ueip : ueipList) {
  		String id = ueip.getUserMonsterUuid();
  		returnMap.put(id, ueip);
  	}
  	
  	return returnMap;
  }
  
  /*
   * selected monsters (the second argument) might be modified, nothing is done if
   * selected monsters is empty.
   */
  public static void retainValidMonsters(Set<String> domain,  Map<String, ?> selectedMonsters,
  		boolean keepThingsInDomain, boolean keepThingsNotInDomain) {
  	Set<String> selectedIds = selectedMonsters.keySet();
  	selectedIds = new HashSet<String>(selectedIds);
  	
  	log.info("domain=" + domain + "\t selectedMonsters=" + selectedMonsters);
  	for (String selectedId : selectedIds) {
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
   * the second argument might be modified
   */
  public static void retainValidMonsterIds(Set<String> existing, List<String> ids) {
//  	ids.add(123456789L);
//  	log.info("existing=" + existing + "\t ids=" + ids);
  	
  	List<String> copyIds = new ArrayList<String>(ids);
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
	  String userId, Map<String, UserMonsterHealingProto> protos) {
  	
  	List<MonsterHealingForUser> nonProtos = new ArrayList<MonsterHealingForUser>();
  	
  	for(UserMonsterHealingProto umhp: protos.values()) {
  		String monsterForUserId = umhp.getUserMonsterUuid();
  		
  		//maybe client not supposed to always set this?
  		Date queuedTime = null;
  		if (umhp.hasQueuedTimeMillis() && umhp.getQueuedTimeMillis() > 0) {
  			queuedTime = new Date(umhp.getQueuedTimeMillis());
  		}
  		
  		String userStructHospitalId = umhp.getUserHospitalStructUuid();
  		float healthProgress = umhp.getHealthProgress();
  		int priority = umhp.getPriority();
  		float elapsedSeconds = umhp.getElapsedSeconds();
  		
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		MonsterHealingForUser mhfu = new MonsterHealingForUser(userId,
  			monsterForUserId, queuedTime, userStructHospitalId,
  			healthProgress, priority, elapsedSeconds);
  		nonProtos.add(mhfu);
  	}
  	
  	return nonProtos;
  }
  
  public static List<MonsterEnhancingForUser> convertToMonsterEnhancingForUser(
	  String userId, Map<String, UserEnhancementItemProto> protos) {
  	
  	List<MonsterEnhancingForUser> nonProtos = new ArrayList<MonsterEnhancingForUser>();
  	
  	for(UserEnhancementItemProto ueip: protos.values()) {
  		String monsterForUserId = ueip.getUserMonsterUuid();
  		long startTimeMillis = ueip.getExpectedStartTimeMillis();
  		Date expectedStartTime;
  		
  		if (!ueip.hasExpectedStartTimeMillis() || startTimeMillis <= 0) {
  			expectedStartTime = null;
  		} else {
  			expectedStartTime = new Date(startTimeMillis);
  		}
//  		Date queuedTime = new Date(umhp.getQueuedTimeMillis());
  		int enhancingCost = ueip.getEnhancingCost();
  		boolean enhancingFinished = ueip.getEnhancingComplete();
  		
  		MonsterEnhancingForUser mefu = new MonsterEnhancingForUser(userId,
  				monsterForUserId, expectedStartTime, enhancingCost, enhancingFinished);//, queuedTime);
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
  public static Map<Integer, Integer> completeMonstersFromQuantities(
  		Map<Integer, MonsterForUser> monsterIdToIncompleteUserMonster,
  		Map<Integer, Integer> monsterIdToQuantity) {
  	
  	Map<Integer, Integer> monsterIdToNewQuantity = new HashMap<Integer, Integer>();
  	//IF NO INCOMLETE MONSTERFORUSER EXISTS, THEN METHOD JUST
  	//RETURNS A COPY OF THE MAP monsterIdToQuantity
  	if (monsterIdToIncompleteUserMonster.isEmpty()) {
  		monsterIdToNewQuantity.putAll(monsterIdToQuantity);
  		return monsterIdToNewQuantity;
  	}
  	
  	//retrieve from the monster_config table so we know how much is
  	//needed to complete the Monster
  	Set<Integer> incompleteMonsterIds =
  		new HashSet<Integer>(monsterIdToIncompleteUserMonster.keySet());
  	Map<Integer, Monster> monsterIdsToMonsters =  MonsterRetrieveUtils
  			.getMonstersForMonsterIds(incompleteMonsterIds);
  	

  	//for each incomplete user monster, try to complete it with the 
  	//available quantity in the monsterIdToQuantity map,
  	//monsterIdToIncompleteUserMonster will be modified
  	for (int monsterId : incompleteMonsterIds) {
  		MonsterForUser mfu = monsterIdToIncompleteUserMonster.get(monsterId);
  		int numPiecesAvailable = monsterIdToQuantity.get(monsterId);
  		Monster monzter = monsterIdsToMonsters.get(monsterId);
  		
  		int newPiecesAvailable = numPiecesAvailable;
  		//sanity check
  		if (mfu.getNumPieces() >= monzter.getNumPuzzlePieces()) {
  			log.warn("(will not process this one) somehow retrieved monster"
  				+ " with max pieces, when it shouldn't be at max pieces. mfu=" + mfu);
  			monsterIdToIncompleteUserMonster.remove(monsterId);
  			
  		} else {
  			newPiecesAvailable = completePieceDeficientMonster(
  				mfu, numPiecesAvailable, monzter);
  		}
  		
  		if (newPiecesAvailable > 0) {
  			monsterIdToNewQuantity.put(monsterId, newPiecesAvailable);
  		}
  	}
  	
  	//add in all the new pieces that do not add to an incomplete MonsterForUser
  	//account for the new rows in monster_for_user table
  	for (Integer monsterId : monsterIdToQuantity.keySet())
  	{
  		//already accounted for the pieces with this monster id
  		if (incompleteMonsterIds.contains(monsterId)) {
  			continue;
  		}
  		int quantity = monsterIdToQuantity.get(monsterId);
  		monsterIdToNewQuantity.put(monsterId, quantity);
  	}
  	
  	return monsterIdToNewQuantity;
  }
  
  //(breaking the abstraction) MonsterForUser mfu will be modified.
  //returns the number of pieces remaining after using up the pieces
  //available in order to try completing the monster_for_user
  public static int completePieceDeficientMonster(MonsterForUser mfu,
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
  	if (mfuNewNumPieces >= numPiecesForCompletion) { //shouldn't really be >= but == seems not safe enough
  		mfu.setHasAllPieces(true);
  		
  		if (0 >= numMinutesForCompletion) {
  			mfu.setComplete(true);
  		}
  	}
  	
  	return numPiecesRemaining;
  }
  
  
  public static List<MonsterForUser> createMonstersForUserFromQuantities(
  		String userId, Map<Integer, Integer> monsterIdsToQuantities,
  		Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity,
  		Date combineStartTime) {
  	List<MonsterForUser> returnList = new ArrayList<MonsterForUser>();

  	Set<Integer> monsterIds = new HashSet<Integer>();
  	if (!monsterIdsToQuantities.isEmpty()) {
  		monsterIds.addAll(monsterIdsToQuantities.keySet());
  	}
  	
  	if (null != monsterIdToLvlToQuantity && !monsterIdToLvlToQuantity.isEmpty()) {
  		monsterIds.addAll(monsterIdToLvlToQuantity.keySet());
  	}
  	
  	Map<Integer, Monster> monsterIdsToMonsters =  MonsterRetrieveUtils
  		.getMonstersForMonsterIds(monsterIds);
  	
  	if(!monsterIdsToQuantities.isEmpty()) {
  		//for each monster and quantity, create as many complete and incomplete
  		//user monsters
  		for (int monsterId : monsterIds) {
  			Monster monzter = monsterIdsToMonsters.get(monsterId);
  			int quantity = monsterIdsToQuantities.get(monsterId);

  			log.info(String.format(
  				"for monsterId=%s and for quantity=%s, creating some number of a specific monster for a user. monster=%s",
  				monsterId, quantity, monzter));

  			List<MonsterForUser> newUserMonsters = createMonsterForUserFromQuantity(
  				userId, monzter, quantity, combineStartTime);
  			log.info(String.format(
  				"some amount of a certain monster created. monster(s)=%s",
  				newUserMonsters));

  			returnList.addAll(newUserMonsters);
  		}
  	}
  	
  	if (null != monsterIdToLvlToQuantity && !monsterIdToLvlToQuantity.isEmpty()) {
  		for (int monsterId : monsterIdToLvlToQuantity.keySet()) {
  			Monster monzter = monsterIdsToMonsters.get(monsterId);

  			Map<Integer, Integer> lvlToQuantity = monsterIdToLvlToQuantity.get(monsterId);
  			for (int lvl : lvlToQuantity.keySet()) {

  				List<MonsterForUser> newUserMonsters =
  					createLeveledMonsterForUserFromQuantity(userId, monzter,
  						lvlToQuantity.get(lvl), combineStartTime, lvl);

  				log.info(String.format(
  					"some amount of a certain leveled monster created. monster(s)=%s",
  					newUserMonsters));

  				returnList.addAll(newUserMonsters);
  			}
  		}
  	}
  	
  	return returnList;
  }
  
  //for A GIVEN MONSTER and QUANTITY of pieces, create as many of this monster as possible
  //THE ID PROPERTY FOR ALL these monsterForUser will be a useless value, say 0
  public static List<MonsterForUser> createMonsterForUserFromQuantity(String userId,
  		Monster monzter, int quantity, Date combineStartTime) {
  	List<MonsterForUser> returnList = new ArrayList<MonsterForUser>();
  	
  	Map<Integer, MonsterLevelInfo> levelToInfo = MonsterLevelInfoRetrieveUtils
  			.getMonsterLevelInfoForMonsterId(monzter.getId());
  	MonsterLevelInfo info = levelToInfo.get(1); //not sure if this is right
  	
  	
  	int numPiecesForCompletion = monzter.getNumPuzzlePieces();
  	
  	//TODO: FIGURE OUT IF THESE ARE TEH CORRECT DEFAULT VALUES
  	//default values for creating a monster for user
  	String id = "";//0;
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
  		boolean hasAllPieces = false;
  		
  		if (quantity >= numPiecesForCompletion) {
  			numPieces = numPiecesForCompletion;
  			hasAllPieces = true;
  			
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
  				currentExp, currentLvl, currentHealth, numPieces, hasAllPieces,
  				isComplete, combineStartTime, teamSlotNum, sourceOfPieces, false,
  				monzter.getBaseOffensiveSkillId(), monzter.getBaseDefensiveSkillId());
  		returnList.add(mfu);
  	}
  	
  	return returnList;
  }
  
  //for A GIVEN MONSTER and QUANTITY of them, create as many of this monster as possible
  //THE ID PROPERTY FOR ALL these monsterForUser will be a useless value, say 0
  public static List<MonsterForUser> createLeveledMonsterForUserFromQuantity(
	  String userId, Monster monzter, int quantity, Date combineStartTime, int lvl) {
	  List<MonsterForUser> returnList = new ArrayList<MonsterForUser>();

	  Map<Integer, MonsterLevelInfo> levelToInfo = MonsterLevelInfoRetrieveUtils
		  .getAllPartialMonsterLevelInfo(monzter.getId());
	  MonsterLevelInfo info = levelToInfo.get(lvl); //not sure if this is right

	  String id = "";//0;
	  int monsterId = monzter.getId();
	  int currentExp = info.getCurLvlRequiredExp();
	  int currentLvl = lvl;
	  int currentHealth = info.getHp();

	  int teamSlotNum = 0;
	  String sourceOfPieces = "";
	  for (; quantity > 0; quantity -= 1) {
		  boolean isComplete = true;
		  int numPieces = monzter.getNumPuzzlePieces();
		  boolean hasAllPieces = true;
		  boolean restricted = false;

		  MonsterForUser mfu = new MonsterForUser(id, userId, monsterId,
			  currentExp, currentLvl, currentHealth, numPieces, hasAllPieces,
			  isComplete, combineStartTime, teamSlotNum, sourceOfPieces, restricted,
			  monzter.getBaseOffensiveSkillId(), monzter.getBaseDefensiveSkillId());
		  returnList.add(mfu);
	  }

	  return returnList;
  }


  
  //METHOD TO REWARD A USER WITH SOME MONSTERS
  public static List<FullUserMonsterProto> updateUserMonsters(String userId,
  		Map<Integer, Integer> monsterIdToNumPieces, 
  		Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity,
  		String sourceOfPieces, Date combineStartDate) {
	  log.info(String.format("the monster pieces the user gets:%s", monsterIdToNumPieces));
	  log.info(String.format("the monsters the user gets:%s", monsterIdToLvlToQuantity));
  	
  	if ((null == monsterIdToNumPieces || monsterIdToNumPieces.isEmpty())
  		&& (null == monsterIdToLvlToQuantity || monsterIdToLvlToQuantity.isEmpty())) {
  		return new ArrayList<FullUserMonsterProto>();
  	}
  	
  	//holds return values
  	Map<Integer, MonsterForUser> monsterIdsToIncompletes =
  		new HashMap<Integer, MonsterForUser>();
  	Map<Integer, Integer> monsterIdToRemainingPieces =
  		new HashMap<Integer, Integer>();
  	awardPieces(userId, monsterIdToNumPieces, monsterIdsToIncompletes,
  		monsterIdToRemainingPieces);
  	
  	//UPDATE THESE INCOMPLETE MONSTERS, IF ANY. SINCE UPDATING, UPDATE THE
  	//combineStartTime
  	List<MonsterForUser> dirtyMonsterForUserList = 
  			new ArrayList<MonsterForUser>(monsterIdsToIncompletes.values());
  	if (!dirtyMonsterForUserList.isEmpty()) {
  		log.info("the monsters that are updated: " + dirtyMonsterForUserList);
  		UpdateUtils.get().updateUserMonsterNumPieces(userId, dirtyMonsterForUserList,
  				sourceOfPieces, combineStartDate);
  	}
  	log.info("the new monsters (in pieces) the user gets {}", monsterIdToRemainingPieces); 
  	
  	//monsterIdToRemainingPieces now contains all the new monsters
  	//the user will get. SET THE combineStartTime
  	List<MonsterForUser> newMonsters = createMonstersForUserFromQuantities(
  			userId, monsterIdToRemainingPieces, monsterIdToLvlToQuantity, combineStartDate);
  	if (!newMonsters.isEmpty()) {
  		log.info("the monsters that are new: " + newMonsters);
  		List<String> monsterForUserIds = InsertUtils.get().insertIntoMonsterForUserReturnIds(
  				userId, newMonsters, sourceOfPieces, combineStartDate);
  		
  		//set these ids into the list "newMonsters"
  		for (int i = 0; i < monsterForUserIds.size(); i++) {
  			MonsterForUser newMonster = newMonsters.get(i);
  			String monsterForUserId = monsterForUserIds.get(i);
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

  private static void awardPieces(
	  String userId,
	  Map<Integer, Integer> monsterIdToNumPieces,
	  Map<Integer, MonsterForUser> monsterIdsToIncompletes,
	  Map<Integer, Integer> monsterIdToRemainingPieces)
  {
	  if (null == monsterIdToNumPieces || monsterIdToNumPieces.isEmpty()) {
		  //no pieces to award;
		  return;
	  }
	  
	  //for all the monster pieces the user will receive, see if he already has any
	  //retrieve all of user's incomplete monsters that have these monster ids 
	  monsterIdsToIncompletes.putAll(
		  RetrieveUtils.monsterForUserRetrieveUtils()
		  .getPieceDeficientIncompleteMonstersWithUserAndMonsterIds(
			  userId,
			  monsterIdToNumPieces.keySet()));

	  //take however many pieces necessary from monsterIdToNumPieces to
	  //complete these incomplete monsterForUsers
	  //monsterIdsToIncompletes will be modified
	  monsterIdToRemainingPieces.putAll(
		  completeMonstersFromQuantities(
			  monsterIdsToIncompletes, monsterIdToNumPieces));
  }
  
  //returns user monster ids
  public static List<String> getWholeButNotCombinedUserMonsters(
  		Map<String, MonsterForUser> idsToUserMonsters) {
  	List<String> wholeUserMonsterIds = new ArrayList<String>();
  	
  	Set<Integer> uniqMonsterIds = new HashSet<Integer>();
  	for (MonsterForUser mfu : idsToUserMonsters.values()) {
  		uniqMonsterIds.add(mfu.getMonsterId());
  	}
  	//get the monsters in order to determine num pieces to be considered whole
  	List<Integer> monsterIds = new ArrayList<Integer>(uniqMonsterIds);
  	Map<Integer, Monster> idsToMonsters = 
  			MonsterRetrieveUtils.getMonstersForMonsterIds(monsterIds);
  	
  	//loop through user monsters and monsters and see if user monster is whole
  	for (String userMonsterId : idsToUserMonsters.keySet()) {
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
  		} 

  		if (userMonsterPieces >= numPiecesToBeWhole) {
  			wholeUserMonsterIds.add(userMonsterId);
  		}
  	}
  	
  	return wholeUserMonsterIds;
  }
  
  public static Map<String, Integer> convertToMonsterForUserIdToCashAmount(
  		List<MinimumUserMonsterSellProto> userMonsters) {
  	Map<String, Integer> idToCashAmount = new HashMap<String, Integer>();
  	
  	for (MinimumUserMonsterSellProto mumsp : userMonsters) {
  		String userMonsterId = mumsp.getUserMonsterUuid();
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
  
  public static MonsterForUser createNewUserMonster(String userId, int numPieces,
  		Monster monzter, Date now, boolean hasAllPieces, boolean isComplete) {
  	
  	int monsterId = monzter.getId();
  	
  	Map<Integer, MonsterLevelInfo> levelToInfo = MonsterLevelInfoRetrieveUtils
  			.getMonsterLevelInfoForMonsterId(monsterId);
  	MonsterLevelInfo info = levelToInfo.get(1); //not sure if this is right
  	
  	
  	String id = "";//0;
  	int currentExp = 0;
  	int currentLvl = 1;
  	int currentHealth = info.getHp();
  	int teamSlotNum = 0;
  	String sourceOfPieces = "";
  	MonsterForUser mfu = new MonsterForUser(id, userId, monsterId, currentExp,
  			currentLvl, currentHealth, numPieces, hasAllPieces, isComplete, now,
  			teamSlotNum, sourceOfPieces, false, monzter.getBaseOffensiveSkillId(),
  			monzter.getBaseDefensiveSkillId());
  	
  	return mfu;
  }
  
  //gather up the user monster ids in the evolution(s)
  public static Set<String> getUserMonsterIdsUsedInEvolution(MonsterEvolvingForUser mefu,
  		Map<Long, MonsterEvolvingForUser> catalystIdToMonsterEvolutionForUser) {
  	Set<String> userMonsterIds = new HashSet<String>();

  	if (null != mefu) {
  		String id = mefu.getCatalystMonsterForUserId();
  		userMonsterIds.add(id);
  		id = mefu.getMonsterForUserIdOne();
  		userMonsterIds.add(id);
  		id = mefu.getMonsterForUserIdTwo();
  		userMonsterIds.add(id);

  	} else if (null != catalystIdToMonsterEvolutionForUser &&
  			!catalystIdToMonsterEvolutionForUser.isEmpty()){
  		//loop through each MonsterEvolvingForUser gathering up the MonsterForUserIds
  		for (MonsterEvolvingForUser mefyou : catalystIdToMonsterEvolutionForUser.values()) {
  			String id = mefyou.getCatalystMonsterForUserId();
  			userMonsterIds.add(id);
  			id = mefyou.getMonsterForUserIdOne();
  			userMonsterIds.add(id);
  			id = mefyou.getMonsterForUserIdTwo();
  			userMonsterIds.add(id);
  		}
  	}

  	return userMonsterIds;
  }
  
  public static List<String> getUserMonsterIdsInClanRaid(
  		Map<String, ClanEventPersistentForUser> userIdToCepfu) {
  	List<String> userMonsterIds = new ArrayList<String>();
  	
  	for (ClanEventPersistentForUser cepfu : userIdToCepfu.values()) {
  		List<String> someUserMonsterIds = cepfu.getUserMonsterIds();
  		userMonsterIds.addAll(someUserMonsterIds);
  	}
  	
  	return userMonsterIds;
  }
  
  public static Element getMonsterElementForMonsterId(int monsterId) {
	  	Monster mon = MonsterRetrieveUtils.getMonsterForMonsterId(monsterId);
	  	
	  	if (null != mon) {
	  		Element me = null;
	  		try {
	  			me = Element.valueOf(mon.getElement());
	  			return me;
	  		} catch (Exception e) {
	  			log.error("invalid monsterElement. id=" + monsterId + " monster=" + mon);
	  		}
	  	}
	  	return null;
	  }
  
  public static boolean checkAllUserMonstersAreComplete(
		  List<Long> deleteUserMonsterIds,
		  Map<Long, MonsterForUser> deletedUserMonsters) {
	  //make sure the monsters are all complete
	  for (long deleteId : deleteUserMonsterIds) {
		  //this assumes all the deleted user monster ids are retrieved from db
		  MonsterForUser mfu = deletedUserMonsters.get(deleteId);
		  if (mfu.isComplete()) {
			  continue;
		  }
		  
		  log.info("monsterForUser incomplete: " + mfu);
		  return false;
	  }
	  return true;
  }
  
  public static Map<String, Map<String, Integer>> calculatePvpDrops(
		Map<String, List<MonsterForUser>> userIdToUserMonsters)
	{
		Map<String, Map<String, Integer>> userIdToUserMonsterIdToDroppedId =
			new HashMap<String, Map<String, Integer>>();
		
		for (String userId : userIdToUserMonsters.keySet())
		{
			List<MonsterForUser> userMonsters = userIdToUserMonsters.get(userId);
			Map<String, Integer> monsterDropIds = MonsterStuffUtils
				.calculatePvpDropIds(userMonsters); 
			
			userIdToUserMonsterIdToDroppedId.put(userId, monsterDropIds);
		}
		
		return userIdToUserMonsterIdToDroppedId;
	}
  
  /**
   * 
   * @param userMonsters
   * @return map(userMonsterId -> monsterId or -1 if no drop)
   */
  public static Map<String, Integer> calculatePvpDropIds(
	  List<MonsterForUser> userMonsters)
  {
	  Map<String, Integer> userMonsterIdToDroppedId =
		  new HashMap<String, Integer>();

	  for (MonsterForUser userMonster : userMonsters)
	  {
		  String mfuId = userMonster.getId();
		  int monsterId = userMonster.getMonsterId();
		  int lvl = userMonster.getCurrentLvl();
		  
		  //calculate if dropped
		  boolean dropped = MonsterLevelInfoRetrieveUtils
			  .didPvpMonsterDrop(monsterId, lvl);

		  //if dropped set monster
		  Monster mon = null;
		  if (dropped) {
			  mon = MonsterRetrieveUtils
				  .getMonsterForMonsterId(monsterId);
		  }
		  
		  //get the pvpMonsterDropId if monster not null.
		  int pvpMonsterDropId = ControllerConstants.NOT_SET;
		  if (null != mon) {
			  pvpMonsterDropId = mon.getPvpMonsterDropId();
		  }
//		  log.info("for mfu {}, set pvpMonsterDropId={}",
//				userMonster, pvpMonsterDropId);
		  //if cases not nested in order to decrease nesting
		  userMonsterIdToDroppedId.put(mfuId, pvpMonsterDropId);
	  }
	  
	  return userMonsterIdToDroppedId;
  }

  public static MonsterSnapshotForUser javafyFullUserMonsterProto(
	  FullUserMonsterProto fump)
  {
	  MonsterSnapshotForUser msfu = new MonsterSnapshotForUser();
	  msfu.setUserId(fump.getUserUuid());
	  msfu.setMonsterForUserId(fump.getUserMonsterUuid());
	  msfu.setMonsterId(fump.getMonsterId());
	  msfu.setCurrentExp(fump.getCurrentExp());
	  msfu.setCurrentLvl(fump.getCurrentLvl());
	  msfu.setCurrentHp(fump.getCurrentHealth());
	  msfu.setTeamSlotNum(fump.getTeamSlotNum());
	  msfu.setOffSkillId(fump.getOffensiveSkillId());
	  msfu.setDefSkillId(fump.getDefensiveSkillId());
	  
	  return msfu;
  }
  
}
