package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.TaskStageHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageHistoryPojo;
import com.lvl6.proto.EventUserProto.SetTangoIdResponseProto.Builder;
import com.lvl6.retrieveutils.TaskStageForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@Scope("prototype")
public class DeleteExistingUserTaskAction {
	private static Logger log = LoggerFactory.getLogger( DeleteExistingUserTaskAction.class);

	@Autowired protected UserRetrieveUtils2 userRetrieveUtil;
	@Autowired protected TaskStageForUserRetrieveUtils2 taskStageForUserRetrieveUtil;
	@Autowired protected DeleteUtil deleteUtil;
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtil;
	@Autowired protected HistoryUtils historyUtil;
	@Autowired
	protected TaskStageHistoryDao tshDao;

	private String userTaskId;
	private TaskForUserOngoing aTaskForUser;

	public void wire(
			String userTaskId,
			TaskForUserOngoing aTaskForUser)
	{
		this.userTaskId = userTaskId;
		this.aTaskForUser = aTaskForUser;
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
//	protected User user;

	public void execute(){//Builder resBuilder) {
//		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}
//
//		boolean valid = verifySemantics(resBuilder);
//
//		if (!valid) {
//			return;
//		}

		boolean success = writeChangesToDB();//resBuilder);
		if (!success) {
			return;
		}

//		resBuilder.setStatus(ResponseStatus.SUCCESS);

	}

//	private boolean verifySyntax(Builder resBuilder) {
//		return true;
//	}
//
	private boolean verifySemantics(Builder resBuilder) {
		return true;
	}

	private boolean writeChangesToDB(){//Builder resBuilder) {
		deleteExistingTaskForUser();
		deleteExistingTaskStagesForUser();

		return false;
	}

	private void deleteExistingTaskForUser() {
		try {
			//  	DeleteUtils.get().deleteTaskForUserOngoingWithTaskForUserId(taskForUserId);
			String userId = aTaskForUser.getUserId();
			int taskId = aTaskForUser.getTaskId();
			int expGained = aTaskForUser.getExpGained();
			int cashGained = aTaskForUser.getCashGained();
			int oilGained = aTaskForUser.getOilGained();
			int numRevives = aTaskForUser.getNumRevives();
			Date aDate = aTaskForUser.getStartDate(); //shouldn't null
			Timestamp startTime = null;
			if (null != aDate) {
				startTime = new Timestamp(aDate.getTime());
			}
			Timestamp endTime = null;
			boolean userWon = false;
			boolean cancelled = true;
			int taskStageId = aTaskForUser.getTaskStageId();

			int num = deleteUtil.deleteTaskForUserOngoingWithTaskForUserId(
					userTaskId);
			log.warn("deleted existing task_for_user. taskForUser={}, (should be 1) numDeleted={}",
					aTaskForUser, num);

			num = insertUtil.insertIntoTaskHistory(userTaskId, userId,
					taskId, expGained, cashGained, oilGained, numRevives,
					startTime, endTime, userWon, cancelled, taskStageId);
			log.warn("inserted into task_history. taskForUser={}, (should be 1) numInserted={}",
					aTaskForUser, num);
		} catch (Exception e) {
			log.error("error saving task info", e);
		}
	}

	private void deleteExistingTaskStagesForUser() {
		List<TaskStageForUserPojo> taskStages = taskStageForUserRetrieveUtil
				.getTaskStagesForUserWithTaskForUserId(userTaskId);

		try {
			List<String> userTaskStageId = new ArrayList<String>();
//			List<String> userTaskId = new ArrayList<String>();
//			List<Integer> stageNum = new ArrayList<Integer>();
//			List<Integer> tsmIdList = new ArrayList<Integer>();
//			List<String> monsterTypes = new ArrayList<String>();
//			List<Integer> expGained = new ArrayList<Integer>();
//			List<Integer> cashGained = new ArrayList<Integer>();
//			List<Integer> oilGained = new ArrayList<Integer>();
//			List<Boolean> monsterPieceDropped = new ArrayList<Boolean>();
//			List<Integer> itemIdDropped = new ArrayList<Integer>();
//			List<Integer> monsterIdDrops = new ArrayList<Integer>();
//			List<Integer> monsterDropLvls = new ArrayList<Integer>();
//			List<Boolean> attackedFirstList = new ArrayList<Boolean>();

			List<TaskStageHistoryPojo> historyList = new ArrayList<TaskStageHistoryPojo>();

			for (TaskStageForUserPojo tsfu : taskStages) {
				TaskStageHistoryPojo tsh = new TaskStageHistoryPojo();

				tsh.setTaskStageForUserId(tsfu.getId());
				tsh.setTaskForUserId(tsfu.getTaskForUserId());
				tsh.setStageNum(tsfu.getStageNum());

				int tsmId = tsfu.getTaskStageMonsterId();
				tsh.setTaskStageMonsterId(tsmId);
				tsh.setMonsterType(tsfu.getMonsterType());
				tsh.setExpGained(tsfu.getExpGained());
				tsh.setCashGained(tsfu.getCashGained());
				tsh.setOilGained(tsfu.getOilGained());
				boolean dropped = tsfu.getMonsterPieceDropped();
				tsh.setMonsterPieceDropped(dropped);
				tsh.setItemIdDropped(tsfu.getItemIdDropped());

				TaskStageMonster tsm = taskStageMonsterRetrieveUtil
						.getTaskStageMonsterForId(tsmId);
				tsh.setMonsterIdDropped(tsm.getMonsterIdDrop());
				tsh.setMonsterDroppedLvl(tsm.getMonsterDropLvl());
				tsh.setAttackedFirst(tsfu.getAttackedFirst());
				tsh.setMonsterLvl(tsfu.getMonsterLvl());

				historyList.add(tsh);
			}

			int num = DeleteUtils.get().deleteTaskStagesForUserWithIds(
					userTaskStageId);

			historyUtil.insertTaskStageHistory(historyList, tshDao);
		} catch (Exception e) {
			log.error("error saving task stage info", e);
		}


//		log.warn(String.format(
//				"num task stage history rows deleted: %s, taskStageForUser=%s",
//				num, taskStages));

//		num = InsertUtils.get().insertIntoTaskStageHistory(userTaskStageId,
//				userTaskId, stageNum, tsmIdList, monsterTypes, expGained,
//				cashGained, oilGained, monsterPieceDropped, itemIdDropped,
//				monsterIdDrops, monsterDropLvls, attackedFirstList);
//		log.warn(String
//				.format("num task stage history rows inserted: %s, taskStageForUser=%s",
//						num, taskStages));
	}

}
