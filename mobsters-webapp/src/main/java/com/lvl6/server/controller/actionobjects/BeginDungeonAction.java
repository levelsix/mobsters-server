package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Item;
import com.lvl6.info.Monster;
import com.lvl6.info.QuestJobMonsterItem;
import com.lvl6.info.Task;
import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.info.TaskStage;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserCurrencyHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserCurrencyHistoryPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.retrieveutils.TaskForUserOngoingRetrieveUtils2;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobMonsterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@Scope("prototype")
public class BeginDungeonAction {
	private static Logger log = LoggerFactory.getLogger( BeginDungeonAction.class);

	@Autowired protected ItemRetrieveUtils itemRetrieveUtil;
	@Autowired protected MonsterRetrieveUtils monsterRetrieveUtil;
	@Autowired protected TaskForUserOngoingRetrieveUtils2 taskForUserOngoingRetrieveUtil;
	@Autowired protected TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtil;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired protected QuestRetrieveUtils questRetrieveUtil;
	@Autowired protected QuestJobRetrieveUtils questJobRetrieveUtil;
	@Autowired protected QuestJobMonsterItemRetrieveUtils questJobMonsterItemRetrieveUtil;
	@Autowired protected TaskRetrieveUtils taskRetrieveUtil;
	@Autowired protected TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtil;
	@Autowired protected TaskStageRetrieveUtils taskStageRetrieveUtil;

	@Autowired protected InsertUtil insertUtil;
	@Autowired protected HistoryUtils historyUtil;
	@Autowired protected UserCurrencyHistoryDao userCurrencyHistoryDao;

	@Autowired protected CreateInfoProtoUtils createInfoProtoUtil;
	@Autowired protected MonsterStuffUtils monsterStuffUtil;

	private String userId;
	private Timestamp clientTime;
	private int taskId;
	private boolean isEvent;
	private int eventId;
	private int gemsSpent;
	private List<Integer> questIds;

	public void wire(String userId, Timestamp clientTime, int taskId,
			boolean isEvent, int eventId, int gemsSpent, List<Integer> questIds) {
		this.userId = userId;
		this.clientTime = clientTime;
		this.taskId = taskId;
		this.isEvent = isEvent;
		this.eventId = eventId;
		this.gemsSpent = gemsSpent;
		this.questIds = questIds;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RemoveUserItemUsedResource {
	//
	//
	//		public RemoveUserItemUsedResource() {
	//
	//		}
	//	}
	//
	//	public RemoveUserItemUsedResource execute() {
	//
	//	}


	//derived state
	protected User user;
	protected Map<Integer, TaskStage> tsMap;
	protected int expGained;
	protected int cashGained;
	protected int oilGained;
	protected String userTaskId;
	protected Map<Integer, TaskStageProto> stageNumsToProtos;

	private UserCurrencyHistoryPojo gemsUserCurrencyHistory;
	private List<UserCurrencyHistoryPojo> uchList;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}
//
		boolean valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);

	}

//	private boolean verifySyntax(Builder resBuilder) {
//		return true;
//	}
//
	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error("no user with id={}", userId);
			return false;
		}

		Task aTask = taskRetrieveUtil.getTaskForTaskId(taskId);
		if (null == aTask) {
			log.error("no task with id={}", taskId);
			return false;
		}

		tsMap = taskStageRetrieveUtil
				.getTaskStagesForTaskId(taskId);
		if (null == tsMap) {
			log.error("task has no taskStages. task={}", aTask);
			return false;
		}

		TaskForUserOngoing aTaskForUser = taskForUserOngoingRetrieveUtil
				.getUserTaskForUserId(userId);
		if (null != aTaskForUser) {
			log.warn(String.format(
					"%s %s. user=%s\t task=%s\t userTask=%s",
					"(will continue processing, but) user has existing task when",
					" beginning another. No task should exist.",
					user,
					aTask,
					aTaskForUser));
			//DELETE TASK AND PUT IT INTO TASK HISTORY
			String userTaskId = aTaskForUser.getId();
			DeleteExistingUserTaskAction deuta =
					AppContext.getBean(DeleteExistingUserTaskAction.class);
			deuta.wire(userTaskId, aTaskForUser);
			deuta.execute();
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		try {
			expGained = 0;
			cashGained = 0;
			oilGained = 0;

			Map<Integer, List<TaskStageForUserPojo>> stageNumToStages =
					generateUserTaskStages();

			//record into user_task table
			int tsId = taskStageRetrieveUtil.getFirstTaskStageIdForTaskId(taskId);
			userTaskId = insertUtil.insertIntoUserTaskReturnId(userId,
					taskId, expGained, cashGained, oilGained, clientTime, tsId);

			//record into user_task stage table
			stageNumsToProtos =
					new HashMap<Integer, TaskStageProto>();
			recordStages(stageNumToStages, stageNumsToProtos);

			log.debug("stageNumsToProtosTemp={}", stageNumsToProtos);

			boolean success = true;
			//start the cool down timer if for event
			if (isEvent) {
				int numInserted = insertUtil
						.insertIntoUpdateEventPersistentForUser(
								userId, eventId, clientTime);

				log.info("started cool down timer for (eventId, userId): ({},{}), numInserted={}",
						new Object[] { userId, eventId, numInserted } );

				prepCurrencyHistory();
				if (0 != gemsSpent) {
					success = updateUser();
				}

				if (success) {
					insertCurrencyHistory();
				}
			}

			return true;
		} catch (Exception e) {
			log.error("", e);
		}

		return false;
	}

	private Map<Integer, List<TaskStageForUserPojo>> generateUserTaskStages() {
		//return value
//		int expSum = 0;
//		int cashSum = 0;
//		int oilSum = 0;

		Map<Integer, List<TaskStageForUserPojo>> stageNumToUserTaskStages =
				new HashMap<Integer, List<TaskStageForUserPojo>>();

		Random rand = ControllerConstants.RAND;
		//quest monster items are dropped based on QUEST JOB IDS not quest ids
		List<Integer> questJobIds = questJobRetrieveUtil
				.getQuestJobIdsForQuestIds(questIds);

		//for each stage, calculate the monster(s) the user will face and
		//reward(s) that might be given if the user wins
		for (int tsId : tsMap.keySet()) {

			//calculate the monster(s) the user will face for this stage
			//at the moment only one monster will be generated
			List<TaskStageMonster> spawnedTaskStageMonsters = new ArrayList<TaskStageMonster>();
			generateSpawnedMonsters(tsId, 1, rand, spawnedTaskStageMonsters);

			log.debug("monster(s) spawned={}",
					spawnedTaskStageMonsters);
			/*Code below is done such that if more than one monster is generated
			  above, then user has potential to get the cash and exp from all
			  the monsters including the one above.*/

			TaskStage ts = tsMap.get(tsId);
			int stageNum = ts.getStageNum();
			boolean userFirst = ts.isAttackerAlwaysHitsFirst();
			List<Integer> stageExpReward = new ArrayList<Integer>();
			List<Integer> stageCashReward = new ArrayList<Integer>();
			List<Integer> stageOilReward = new ArrayList<Integer>();
			List<TaskStageForUserPojo> userTaskStages = createUserTaskStagesFromMonsters(
					stageNum, userFirst, spawnedTaskStageMonsters, questJobIds,
					stageExpReward, stageCashReward, stageOilReward);
			stageNumToUserTaskStages.put(stageNum, userTaskStages);

			//sum up stageExpReward with expSum
			//sum up stageCashReward with cashSum
			//sum up stageOilReward with oilSum
		}

//		expGained.add(expSum);
//		cashGained.add(cashSum);
//		oilGained.add(oilSum);

		return stageNumToUserTaskStages;
	}

	public void generateSpawnedMonsters(int tsId, int quantity, Random rand,
			List<TaskStageMonster> spawnedTaskStageMonsters) {

		//select one monster, at random. This is the ONE monster for this stage
		List<TaskStageMonster> possibleMonsters = taskStageMonsterRetrieveUtil
				.getMonstersForTaskStageId(tsId);
		List<TaskStageMonster> copyTaskStageMonsters = new ArrayList<TaskStageMonster>(
				possibleMonsters);

		int size = copyTaskStageMonsters.size();
		int quantityWanted = quantity;
		//sum up chance to appear, and need to normalize all the probabilities
		float sumOfProbabilities = monsterStuffUtil
				.sumProbabilities(copyTaskStageMonsters);

		for (int i = 0; i < size; i++) {
			if (quantityWanted == 0) {
				//since we selected all the monsters we wanted, exit
				break;
			}
			if (quantityWanted < 0) {
				log.error("selecting some amount of monsters out of n possible monsters failed.");
				break;
			}
			//since we want k more monsters and we have k left, take them all
			int numLeft = size - i;
			if (quantityWanted == numLeft) {
				List<TaskStageMonster> leftOvers = copyTaskStageMonsters
						.subList(i, size);
				spawnedTaskStageMonsters.addAll(leftOvers);
				break;
			}

			//seeing if current monster is selected
			TaskStageMonster tsmSoFar = copyTaskStageMonsters.get(i);
			float chanceToAppear = tsmSoFar.getChanceToAppear();

			if (monsterSelected(rand, tsmSoFar, chanceToAppear,
					sumOfProbabilities)) {
				spawnedTaskStageMonsters.add(tsmSoFar);
				quantityWanted -= 1;
			}
			//selecting without replacement so this guy's probability needs to go
			sumOfProbabilities -= chanceToAppear;
		}
	}

	private boolean monsterSelected(Random rand, TaskStageMonster tsm,
			float chanceToAppear, float sumOfProbabilities) {
		float randFloat = rand.nextFloat();

		float normalizedProb = chanceToAppear / sumOfProbabilities;
		if (normalizedProb > randFloat) {
			//random number generated falls within this monster's probability window
			return true;
		}
		return false;
	}

	private List<TaskStageForUserPojo> createUserTaskStagesFromMonsters(
			int stageNum, boolean attackedFirst,
			List<TaskStageMonster> spawnedTaskStageMonsters,
			List<Integer> questJobIds, List<Integer> stageExpReward,
			List<Integer> stageCashReward, List<Integer> stageOilReward) {
		//return values
		List<TaskStageForUserPojo> tsfuList = new ArrayList<TaskStageForUserPojo>();
		int expReward = 0;
		int cashReward = 0;
		int oilReward = 0;

		//create as many TaskStageForUsers as there are TaskStageMonsters and
		//aggregate the exp and cash
		for (TaskStageMonster tsm : spawnedTaskStageMonsters) {
			TaskStageForUserPojo tsfu = createTaskStageFromMonster(stageNum, tsm,
					questJobIds);
			tsfu.setAttackedFirst(attackedFirst);

			expReward += tsfu.getExpGained();
			cashReward += tsfu.getCashGained();
			oilReward += tsfu.getOilGained();
			tsfuList.add(tsfu);
		}

		stageExpReward.add(expReward);
		stageCashReward.add(cashReward);
		stageOilReward.add(oilReward);

		return tsfuList;
	}

	private TaskStageForUserPojo createTaskStageFromMonster(int stageNum,
			TaskStageMonster tsm, List<Integer> questJobIds) {
		TaskStageForUserPojo tsfu = new TaskStageForUserPojo();

		//sanity checks
		int monsterId = tsm.getMonsterId();
		Monster mon = monsterRetrieveUtil.getMonsterForMonsterId(monsterId);
		if (null == mon) {
			throw new RuntimeException(
					"Non existent monsterId for TaskStageMonster=" + tsm);
		}
		boolean puzzlePieceDropped = tsm.didPuzzlePieceDrop();
		if (puzzlePieceDropped) {
			mon = monsterRetrieveUtil.getMonsterForMonsterId(tsm.getMonsterIdDrop());
			if (null == mon) {
				throw new RuntimeException(
						"Non existent monsterIdDrop for TaskStageMonster=" + tsm);
			}
		}
		//determine the item that this monster drops, if any, (-1 means no item drop)
		int itemId = generateQuestJobMonsterItem(questJobIds, tsm);
		if (itemId > 0) {
			//check if item exists
			Item item = itemRetrieveUtil.getItemForId(itemId);
			if (null == item) {
				throw new RuntimeException(String.format(
						"nonexistent itemId for userTask=%s", tsfu));
			}
		}

		tsfu.setStageNum(stageNum);
		tsfu.setTaskStageMonsterId(tsm.getId());
		tsfu.setMonsterType(tsm.getMonsterType());
		tsfu.setExpGained(tsm.getExpReward());
		tsfu.setCashGained(tsm.getCashDrop());
		tsfu.setOilGained(tsm.getOilDrop());
		tsfu.setItemIdDropped(itemId);

		if (itemId > 0) {
			log.info("not generating monster piece since monster dropped item={}, tsm={}",
					itemId, tsm);
			tsfu.setMonsterPieceDropped(false);
		} else {
			tsfu.setMonsterPieceDropped(puzzlePieceDropped);
		}

		//UGH, this is just to make all cake kids' hp scale
		int monsterLvl = tsm.getLevel();

		float hpScale = tsm.getUserToonHpScale();
		float atkScale = tsm.getUserToonAtkScale();

		log.info("tsm={}", tsm);
		if (hpScale > 0F || atkScale > 0F) {
			float newMonsterLvlFactor1 = hpScale * (user.getHighestToonHp());
			float newMonsterLvlFactor2 = atkScale * (user.getHighestToonAtk());

			log.info("monsterLvl={}, newMonsterLvlFactor1={}, newMonsterLvlFactor2={}",
					new Object[] { monsterLvl, newMonsterLvlFactor1, newMonsterLvlFactor2 } );
			monsterLvl = Math.round(newMonsterLvlFactor1 + newMonsterLvlFactor2);
			monsterLvl = Math.max(monsterLvl, 1);
		}

		tsfu.setMonsterLvl(monsterLvl);

		return tsfu;
	}

	//see if quest job id and monster id have an item. if yes, see if it drops.
	//If it drops return the item id.
	//default return -1
	private int generateQuestJobMonsterItem(List<Integer> questJobIds,
			TaskStageMonster tsm) {

		int monsterId = tsm.getMonsterId();
		for (int questJobId : questJobIds) {

			QuestJobMonsterItem qjmi = questJobMonsterItemRetrieveUtil
					.getItemForQuestJobAndMonsterId(questJobId, monsterId);
			log.info(String.format("QuestJobMonsterItem=%s", qjmi));

			if (null == qjmi) {
				continue;
			}
			log.info("item might drop");

			//roll to see if item should drop
			if (!qjmi.didItemDrop()) {
				log.info(String.format(
						"task stage monster didn't drop item. tsm=%s", tsm));
				continue;
			}
			//since quest job and monster have item associated with it and the
			//item "dropped"
			//return item dropped

			int itemId = qjmi.getItemId();
			log.info(String.format("item dropped=%s", itemId));
			return itemId;
		}

		log.debug( "no quest job ids sent by client. questJobIds={}",
				questJobIds);
		//no item
		return -1;
	}

	//save these task stage for user objects; convert into protos and store into
	//stageNumsToProtos. one TaskStageForUser represents one monster in the current stage
	private void recordStages(
			Map<Integer, List<TaskStageForUserPojo>> stageNumToStages,
			Map<Integer, TaskStageProto> stageNumsToProtos)
	{
		List<Integer> stageNumList = new ArrayList<Integer>(
				stageNumToStages.keySet());
		Collections.sort(stageNumList);

		//loop through the individual stages, aggregate them
		//to later be saved to the db
		List<TaskStageForUserPojo> allTsfu = new ArrayList<TaskStageForUserPojo>();
		for (Integer stageNum : stageNumList) {

			//aggregate to later be saved to db
			List<TaskStageForUserPojo> tsfuList = stageNumToStages.get(stageNum);

			//don't forget to set the taskForUserId
			for (TaskStageForUserPojo tsfu : tsfuList) {
				tsfu.setTaskForUserId(userTaskId);
			}

			allTsfu.addAll(tsfuList);
		}

		log.debug("userTaskStages={}", allTsfu);

		//save to db
		List<String> tsfuIds = insertUtil.insertIntoUserTaskStage(
				allTsfu);

		if (null == tsfuIds || tsfuIds.size() != allTsfu.size()) {
			log.error("wtf, dbconnections failed to return ids when creating TaskStageForUsers");
			allTsfu = taskStageForUserRetrieveUtil
					.getTaskStagesForUserWithTaskForUserId(userTaskId);

			stageNumToStages.clear();
			for (TaskStageForUserPojo tsfu : allTsfu) {
				int stageNum = tsfu.getStageNum();

				if (!stageNumToStages.containsKey(stageNum)) {
					stageNumToStages.put(stageNum,
							new ArrayList<TaskStageForUserPojo>());
				}
				List<TaskStageForUserPojo> tsfuList = stageNumToStages
						.get(stageNum);
				tsfuList.add(tsfu);
			}

		} else {
			//set the tsfuIds
			for (int index = 0; index < tsfuIds.size(); index++) {
				TaskStageForUserPojo tsfu = allTsfu.get(index);
				String tsfuId = tsfuIds.get(index);
				tsfu.setId(tsfuId);
			}
		}

		//create the proto
		for (Integer stageNum : stageNumList) {
			List<TaskStageForUserPojo> tsfu = stageNumToStages.get(stageNum);
			TaskStageProto tsp = createInfoProtoUtil.createTaskStageProto(
					taskId, stageNum, tsfu);

			//return to sender
			stageNumsToProtos.put(stageNum, tsp);
		}

	}

	public void prepCurrencyHistory() {
		uchList = new ArrayList<UserCurrencyHistoryPojo>();

		gemsUserCurrencyHistory = new UserCurrencyHistoryPojo();
		gemsUserCurrencyHistory.setResourceType(MiscMethods.gems);
		gemsUserCurrencyHistory.setCurrencyBeforeChange(user.getGems());
		uchList.add(gemsUserCurrencyHistory);

	}

	private boolean updateUser() {
		log.info("user before: {} \t\t", user);
		int gemChange = -1 * gemsSpent;
		if (!user.updateRelativeGemsNaive(gemChange, 0)) {
			log.error("problem updating user gems to delete event cool down timer. gemChange=%s, user=%s",
					gemChange, user);
			return false;
		}
		log.info("user after: {}", user);


		return true;
	}

	private void insertCurrencyHistory() {
		Date now = new Date();
		gemsUserCurrencyHistory.setCurrencyChange(-1 * gemsSpent);
		gemsUserCurrencyHistory.setCurrencyAfterChange(user.getGems());

		String reasonForChange = ControllerConstants.UCHRFC__END_PERSISTENT_EVENT_COOLDOWN;
		String details = String.format("eventId=%s, taskId=%s",
				eventId, taskId);
		historyUtil.insertUserCurrencyHistory(userId, uchList, now, reasonForChange, details, userCurrencyHistoryDao);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserTaskId() {
		return userTaskId;
	}

	public Map<Integer, TaskStageProto> getStageNumsToProtos() {
		return stageNumsToProtos;
	}

}
