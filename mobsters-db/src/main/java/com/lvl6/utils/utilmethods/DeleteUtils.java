package com.lvl6.utils.utilmethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;

import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.DBConnection;

public class DeleteUtils implements DeleteUtil {

	private static final Logger log = LoggerFactory
			.getLogger(DeleteUtils.class);

	public static DeleteUtil get() {
		return (DeleteUtil) AppContext.get().getBean(
				"deleteUtils");
	}

	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.DeleteUtil#deleteUserStruct(int)
	 */
	@Override
	/*@Caching(evict= {
	    //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
	    //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
	public boolean deleteUserStruct(String userStructId) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);
		int numDeleted = DBConnection.get().deleteRows(
				DBConstants.TABLE_STRUCTURE_FOR_USER, conditionParams, "and");
		if (numDeleted == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteUserClanDataRelatedToClanId(String clanId,
			int numRowsToDelete) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
		int numDeleted = DBConnection.get().deleteRows(
				DBConstants.TABLE_CLAN_FOR_USER, conditionParams, "and");
		if (numDeleted == numRowsToDelete) {
			return true;
		}
		return false;
	}

	@Override
	public void deleteUserClansForUserExceptSpecificClan(String userId,
			String clanId) {
		String query = "delete from " + DBConstants.TABLE_CLAN_FOR_USER
				+ " where " + DBConstants.CLAN_FOR_USER__USER_ID + "=? and "
				+ DBConstants.CLAN_FOR_USER__CLAN_ID + "!=?";

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(clanId);

		DBConnection.get().deleteDirectQueryNaive(query, values);
	}

	@Override
	public boolean deleteUserClan(String userId, String clanId) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
		conditionParams.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);

		int numDeleted = DBConnection.get().deleteRows(
				DBConstants.TABLE_CLAN_FOR_USER, conditionParams, "and");
		if (numDeleted == 1) {
			return true;
		}
		return false;
	}

	//@CacheEvict(value="clanById", key="#clanId")
	@Override
	@CacheEvict(value = "clanWithId", key = "#clanId")
	public boolean deleteClanWithClanId(String clanId) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLANS__ID, clanId);
		int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_CLANS,
				conditionParams, "and");
		if (numDeleted == 1) {
			return true;
		}
		return false;
	}

	@Override
	public int deleteAllUserQuestsForUser(String userId) {
		String tableName = DBConstants.TABLE_QUEST_FOR_USER;
		String condDelim = "and";
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, condDelim);

		return numDeleted;
	}

	@Override
	public int deleteTaskForUserOngoingWithTaskForUserId(String taskForUserId) {
		String tableName = DBConstants.TABLE_TASK_FOR_USER_ONGOING;
		String condDelim = "and";
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.TASK_FOR_USER_ONGOING__ID,
				taskForUserId);
		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, condDelim);

		return numDeleted;
	}

	@Override
	public int deleteTaskStagesForUserWithIds(List<String> taskStageForUserIds) {
		String tableName = DBConstants.TABLE_TASK_STAGE_FOR_USER;
		int size = taskStageForUserIds.size();
		List<String> questions = Collections.nCopies(size, "?");

		String delimiter = ",";
		String query = " DELETE FROM " + tableName + " WHERE "
				+ DBConstants.TASK_STAGE_FOR_USER__ID + " IN ("
				+ StringUtils.getListInString(questions, delimiter) + ");";

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				taskStageForUserIds);
		return numDeleted;
	}

	@Override
	public int deleteMonsterHealingForUser(String userId,
			List<String> userMonsterIds) {
		String tableName = DBConstants.TABLE_MONSTER_HEALING_FOR_USER;
		int size = userMonsterIds.size();

		List<String> questions = Collections.nCopies(size, "?");

		String delimiter = ",";
		String query = " DELETE FROM " + tableName + " WHERE "
				+ DBConstants.MONSTER_HEALING_FOR_USER__USER_ID + "=?"
				+ " and "
				+ DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID
				+ " IN (" + StringUtils.getListInString(questions, delimiter)
				+ ");";

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.addAll(userMonsterIds);

		log.info("userMonsterIds=" + userMonsterIds + "\t values sent to db: "
				+ values);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteMonsterEnhancingForUser(String userId,
			List<String> userMonsterIds) {
		String tableName = DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER;
		int size = userMonsterIds.size();

		List<String> questions = Collections.nCopies(size, "?");

		String delimiter = ",";
		String query = " DELETE FROM " + tableName + " WHERE "
				+ DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID + "=?"
				+ " and "
				+ DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID
				+ " IN (" + StringUtils.getListInString(questions, delimiter)
				+ ");";

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.addAll(userMonsterIds);

		log.info("userMonsterIds=" + userMonsterIds + "\t values sent to db: "
				+ values);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	////@CacheEvict(value ="specificUserEquip", key="#userEquipId")
	@Override
	public int deleteMonsterForUser(String userMonsterId) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userMonsterId);

		int numDeleted = DBConnection.get().deleteRows(
				DBConstants.TABLE_MONSTER_FOR_USER, conditionParams, "and");
		return numDeleted;
	}

	@Override
	public int deleteMonstersForUser(List<String> userMonsterIds) {
		String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
		int size = userMonsterIds.size();
		List<String> questions = Collections.nCopies(size, "?");

		String delimiter = ",";
		//    String query = " DELETE FROM " + tableName + " WHERE " + DBConstants.MONSTER_FOR_USER__ID
		//    + " IN (" + StringUtils.getListInString(questions, delimiter) + ")";

		String query = String.format(" DELETE FROM %s WHERE %s IN(%s)",
				tableName, DBConstants.MONSTER_FOR_USER__ID,
				StringUtils.getListInString(questions, delimiter));

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				userMonsterIds);
		return numDeleted;
	}

	@Override
	public int deleteUserFacebookInvitesForSlots(List<String> idsOfInvites) {
		String tableName = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
		int size = idsOfInvites.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		StringBuilder querySb = new StringBuilder();
		querySb.append("DELETE FROM ");
		querySb.append(tableName);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
		querySb.append(" IN (");
		querySb.append(questionMarks);
		querySb.append(")");
		String query = querySb.toString();

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				idsOfInvites);
		return numDeleted;
	}

	@Override
	public int deleteUnredeemedUserFacebookInvitesForUser(String userId) {
		String tableName = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;

		StringBuilder querySb = new StringBuilder();
		querySb.append("DELETE FROM ");
		querySb.append(tableName);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
		querySb.append(" > 0 AND ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
		querySb.append("=?");
		querySb.append(" AND ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED);
		querySb.append(" IS NULL");

		String query = querySb.toString();
		List<String> values = new ArrayList<String>();
		values.add(userId);
		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteMonsterEvolvingForUser(String catalystUserMonsterId,
			String userMonsterIdOne, String userMonsterIdTwo, String userId) {
		String tableName = DBConstants.TABLE_MONSTER_EVOLVING_FOR_USER;

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams
				.put(DBConstants.MONSTER_EVOLVING_FOR_USER__CATALYST_USER_MONSTER_ID,
						catalystUserMonsterId);
		conditionParams.put(
				DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_ONE,
				userMonsterIdOne);
		conditionParams.put(
				DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_TWO,
				userMonsterIdTwo);
		conditionParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_ID,
				userId);

		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, "and");
		return numDeleted;
	}

	@Override
	public int deleteEventPersistentForUser(String userId, int eventId) {
		String tableName = DBConstants.TABLE_EVENT_PERSISTENT_FOR_USER;

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.EVENT_PERSISTENT_FOR_USER__USER_ID,
				userId);
		conditionParams.put(
				DBConstants.EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID,
				eventId);

		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, "and");
		return numDeleted;
	}

	@Override
	public int deletePvpBattleForUser(String attackerId) {
		String tableName = DBConstants.TABLE_PVP_BATTLE_FOR_USER;

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_ID,
				attackerId);

		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, "and");
		return numDeleted;
	}

	@Override
	public int deleteClanEventPersistentForClan(String clanId) {
		String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(
				DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID, clanId);

		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, "and");
		return numDeleted;
	}

	@Override
	public int deleteClanEventPersistentForUsers(List<String> userIdList) {
		String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
		int size = userIdList.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		StringBuilder querySb = new StringBuilder();
		querySb.append("DELETE FROM ");
		querySb.append(tableName);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID);
		querySb.append(" IN (");
		querySb.append(questionMarks);
		querySb.append(")");
		String query = querySb.toString();

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				userIdList);
		return numDeleted;
	}

	@Override
	public int deleteObstacleForUser(String userObstacleId) {
		String tableName = DBConstants.TABLE_OBSTACLE_FOR_USER;

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.OBSTACLE_FOR_USER__ID, userObstacleId);

		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, "and");
		return numDeleted;
	}

	@Override
	public int deleteMiniJobForUser(String userMiniJobId) {
		String tableName = DBConstants.TABLE_MINI_JOB_FOR_USER;

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MINI_JOB_FOR_USER__ID, userMiniJobId);

		int numDeleted = DBConnection.get().deleteRows(tableName,
				conditionParams, "and");
		return numDeleted;
	}

	@Override
	public int deleteMiniJobForUser(
			String userId,
			Collection<String> userMiniJobIds )
	{
		String tableName = DBConstants.TABLE_MINI_JOB_FOR_USER;

		int size = userMiniJobIds.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionsStr = StringUtils.getListInString(questions, ",");

		String query = String.format(
				"DELETE FROM %s WHERE %s=? AND %s IN (%s);",
				tableName,
				DBConstants.MINI_JOB_FOR_USER__USER_ID,
				DBConstants.MINI_JOB_FOR_USER__ID,
				questionsStr);

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.addAll(userMiniJobIds);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteClanHelp(String userId, List<String> clanHelpIdList) {
		String tableName = DBConstants.TABLE_CLAN_HELP;

		int size = clanHelpIdList.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format(
				"DELETE FROM %s WHERE %s IN (%s) AND %s=?", tableName,
				DBConstants.CLAN_HELP__ID, questionMarks,
				DBConstants.CLAN_HELP__USER_ID);

		List<Object> values = new ArrayList<Object>();
		values.addAll(clanHelpIdList);
		values.add(userId);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteClanInvite(String userId, List<String> clanInviteIdList) {
		String tableName = DBConstants.TABLE_CLAN_INVITE;

		String query = null;
		List<Object> values = new ArrayList<Object>();
		if (null == clanInviteIdList || clanInviteIdList.isEmpty()) {
			query = String.format("DELETE FROM %s WHERE %s=?", tableName,
					DBConstants.CLAN_INVITE__USER_ID);
			values.add(userId);

		} else {
			int size = clanInviteIdList.size();
			List<String> questions = Collections.nCopies(size, "?");
			String questionMarks = StringUtils.csvList(questions);

			query = String.format("DELETE FROM %s WHERE %s IN (%s) AND %s=?",
					tableName, DBConstants.CLAN_INVITE__ID, questionMarks,
					DBConstants.CLAN_INVITE__USER_ID);

			values.addAll(clanInviteIdList);
			values.add(userId);

		}
		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteItemUsed(String userId, List<String> itemForUserUsageIds) {
		String tableName = DBConstants.TABLE_ITEM_FOR_USER_USAGE;

		int size = itemForUserUsageIds.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format(
				"DELETE FROM %s WHERE %s IN (%s) AND %s=?", tableName,
				DBConstants.ITEM_FOR_USER_USAGE__ID, questionMarks,
				DBConstants.ITEM_FOR_USER_USAGE__USER_ID);

		List<Object> values = new ArrayList<Object>();
		values.addAll(itemForUserUsageIds);
		values.add(userId);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteSecretGifts(String userId, List<String> ids) {
		String tableName = DBConstants.TABLE_SECRET_GIFT_FOR_USER;

		int size = ids.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format(
				"DELETE FROM %s WHERE %s IN (%s) AND %s=?", tableName,
				DBConstants.SECRET_GIFT_FOR_USER__ID, questionMarks,
				DBConstants.SECRET_GIFT_FOR_USER__USER_ID);

		List<Object> values = new ArrayList<Object>();
		values.addAll(ids);
		values.add(userId);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteClanAvenge(String clanId, List<String> ids) {
		String tableName = DBConstants.TABLE_CLAN_AVENGE;

		int size = ids.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format(
				"DELETE FROM %s WHERE %s IN (%s) AND %s=?", tableName,
				DBConstants.CLAN_AVENGE__ID, questionMarks,
				DBConstants.CLAN_AVENGE__CLAN_ID);

		List<Object> values = new ArrayList<Object>();
		values.addAll(ids);
		values.add(clanId);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteClanAvengeUser(String clanId, List<String> ids) {
		String tableName = DBConstants.TABLE_CLAN_AVENGE_USER;

		int size = ids.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format(
				"DELETE FROM %s WHERE %s IN (%s) AND %s=?", tableName,
				DBConstants.CLAN_AVENGE_USER__CLAN_AVENGE_ID, questionMarks,
				DBConstants.CLAN_AVENGE_USER__CLAN_ID);

		List<Object> values = new ArrayList<Object>();
		values.addAll(ids);
		values.add(clanId);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteClanMemberTeamDonationSolicitation(List<String> ids) {
		String tableName = DBConstants.TABLE_CLAN_MEMBER_TEAM_DONATION;

		int size = ids.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format("DELETE FROM %s WHERE %s IN (%s)",
				tableName, DBConstants.CLAN_MEMBER_TEAM_DONATION__ID,
				questionMarks);

		List<Object> values = new ArrayList<Object>();
		values.addAll(ids);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteMonsterSnapshotForUser(
			List<MonsterSnapshotForUser> snapshots) {
		String tableName = DBConstants.TABLE_MONSTER_SNAPSHOT_FOR_USER;

		int size = snapshots.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format("DELETE FROM %s WHERE %s IN (%s)",
				tableName, DBConstants.MONSTER_SNAPSHOT_FOR_USER__ID,
				questionMarks);

		List<String> ids = new ArrayList<String>();
		for (MonsterSnapshotForUser msfu : snapshots) {
			ids.add(msfu.getId());
		}

		List<Object> values = new ArrayList<Object>();
		values.addAll(ids);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteMonsterSnapshotsFromUser(
			String type, List<String> idInTable) {
		String tableName = DBConstants.TABLE_MONSTER_SNAPSHOT_FOR_USER;

		int size = idInTable.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format("DELETE FROM %s WHERE %s=? and %s IN (%s)",
				tableName,
				DBConstants.MONSTER_SNAPSHOT_FOR_USER__TYPE,
				DBConstants.MONSTER_SNAPSHOT_FOR_USER__ID_IN_TABLE,
				questionMarks);

		List<Object> values = new ArrayList<Object>();
		values.add(type);
		values.addAll(idInTable);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteFromBattleItemQueueForUser(String userId,
			List<BattleItemQueueForUser> biqfuList) {
		String tableName = DBConstants.TABLE_BATTLE_ITEM_QUEUE_FOR_USER;

		int size = biqfuList.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String
				.format("DELETE FROM %s WHERE %s=? AND %s IN (%s)", tableName,
						DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__USER_ID,
						DBConstants.BATTLE_ITEM_QUEUE_FOR_USER__PRIORITY,
						questionMarks);

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		for (BattleItemQueueForUser biqfu : biqfuList) {
			values.add(biqfu.getPriority());
		}

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deletePvpBoardObstacleForUser(Collection<Integer> ids,
			String userId) {
		String tableName = DBConstants.TABLE_PVP_BOARD_OBSTACLE_FOR_USER;
		int size = ids.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String.format(
				"DELETE FROM %s WHERE %s=? and %s IN (%s)", tableName,
				DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__USER_ID,
				DBConstants.PVP_BOARD_OBSTACLE_FOR_USER__ID, questionMarks);

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.addAll(ids);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteMiniEventGoalForUser(String userId)
	{
		String tableName = DBConstants.TABLE_MINI_EVENT_GOAL_FOR_USER;

		String query = String.format(
				"DELETE FROM %s WHERE %s=?",
				tableName, DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID);

		List<Object> values = new ArrayList<Object>();
		values.add(userId);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, values);
		return numDeleted;
	}

	@Override
	public int deleteGiftForUser(Collection<String> gfuIds) {
		String tableName = DBConstants.TABLE_GIFT_FOR_USER;

		int size = gfuIds.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String
				.format("DELETE FROM %s WHERE %s IN (%s)", tableName,
						DBConstants.GIFT_FOR_USER__ID,
						questionMarks);

		List<Object> values = new ArrayList<Object>();
		values.addAll(gfuIds);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

	@Override
	public int deleteGiftForTangoUser(Collection<String> gfuIds) {
		String tableName = DBConstants.TABLE_GIFT_FOR_TANGO_USER;

		int size = gfuIds.size();
		List<String> questions = Collections.nCopies(size, "?");
		String questionMarks = StringUtils.csvList(questions);

		String query = String
				.format("DELETE FROM %s WHERE %s IN (%s)", tableName,
						DBConstants.GIFT_FOR_TANGO_USER__GIFT_FOR_USER_ID,
						questionMarks);

		List<Object> values = new ArrayList<Object>();
		values.addAll(gfuIds);

		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
				values);
		return numDeleted;
	}

}
