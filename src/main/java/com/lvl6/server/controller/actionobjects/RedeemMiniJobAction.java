package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.MiniJob;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto.Builder;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto.RedeemMiniJobStatus;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class RedeemMiniJobAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private String userMiniJobId;
	private Timestamp clientTime;
	private UserRetrieveUtils2 userRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private DeleteUtil deleteUtil;
	private UpdateUtil updateUtil;
	private InsertUtil insertUtil;
	private MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;
	private MiniJobRetrieveUtils miniJobRetrieveUtils;
	private MonsterStuffUtils monsterStuffUtils;
	private MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	private List<UserMonsterCurrentHealthProto> umchpList;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private RewardRetrieveUtils rewardRetrieveUtils;
	

	public RedeemMiniJobAction(
			String userId,
			String userMiniJobId,
			Timestamp clientTime,
			UserRetrieveUtils2 userRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			DeleteUtil deleteUtil, UpdateUtil updateUtil, InsertUtil insertUtil,
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil,
			MiniJobRetrieveUtils miniJobRetrieveUtils,
			MonsterStuffUtils monsterStuffUtils,
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils,
			List<UserMonsterCurrentHealthProto> umchpList,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			RewardRetrieveUtils rewardRetrieveUtils) {
		super();
		this.userId = userId;
		this.userMiniJobId = userMiniJobId;
		this.clientTime = clientTime;
		this.userRetrieveUtil = userRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.deleteUtil = deleteUtil;
		this.updateUtil = updateUtil;
		this.insertUtil = insertUtil;
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
		this.miniJobRetrieveUtils = miniJobRetrieveUtils;
		this.monsterStuffUtils = monsterStuffUtils;
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
		this.umchpList = umchpList;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.rewardRetrieveUtils = rewardRetrieveUtils;
	}

	//derived state
	private User user;
	private Collection<String> userMiniJobIds;
	private Map<String, MiniJobForUser> idToUserMiniJob;
	private Map<String, Integer> userMonsterIdToExpectedHealth;
	private MiniJobForUser mjfu;
	private MiniJob mj;
	private AwardRewardAction ara;
	private List<Reward> listOfRewards;
	

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(RedeemMiniJobStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		user = userRetrieveUtil.getUserById(userId);
		
		if(user == null) {
			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);
			log.error("user is null, userId = {}", userId);
			return false;
		}
		
		
		userMiniJobIds = Collections
				.singleton(userMiniJobId);
		idToUserMiniJob = miniJobForUserRetrieveUtil
				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);

		if (idToUserMiniJob.isEmpty() || umchpList.isEmpty()) {
			log.error("no UserMiniJob exists with id="
					+ userMiniJobId
					+ "or invalid userMonsterIds (monsters need to be damaged). "
					+ " userMonsters=" + umchpList);
			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		
		mjfu = idToUserMiniJob.get(userMiniJobId);
		if (null == mjfu.getTimeCompleted()) {
			//sanity check
			log.error("MiniJobForUser incomplete: " + mjfu);
			return false;
		}

		//sanity check
		int miniJobId = mjfu.getMiniJobId();
		mj = miniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
		if (null == mj) {
			log.error("no MiniJob exists with id=" + miniJobId
					+ "\t invalid MiniJobForUser=" + mjfu);
			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
			return false;
		}
		
		if(!verifyListOfRewards()) {
			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_OTHER);
			return false;
		}
		
		userMonsterIdToExpectedHealth = new HashMap<String, Integer>();
		
		List<String> userMonsterIds = monsterStuffUtils.getUserMonsterIds(
				umchpList, userMonsterIdToExpectedHealth);

		Map<String, MonsterForUser> mfuIdsToUserMonsters = monsterForUserRetrieveUtils
				.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
		
		//keep only valid userMonsterIds another sanity check
		if (userMonsterIds.size() != mfuIdsToUserMonsters.size()) {
			log.warn("some userMonsterIds client sent are invalid."
					+ " Keeping valid ones. userMonsterIds=" + userMonsterIds
					+ " mfuIdsToUserMonsters=" + mfuIdsToUserMonsters);

			//since client sent some invalid monsters, keep only the valid
			//mappings from userMonsterId to health
			Set<String> existing = mfuIdsToUserMonsters.keySet();
			userMonsterIdToExpectedHealth.keySet().retainAll(existing);
		}
		
		if (userMonsterIds.isEmpty()) {
			log.error("no valid user monster ids sent by client");
			return false;
		}
		return true;
	}
	
	public boolean verifyListOfRewards() {
		listOfRewards = new ArrayList<Reward>();

		if(mj.getRewardIdOne() > 0) {
			Reward rewardOne = rewardRetrieveUtils.getRewardById(mj.getRewardIdOne());
			if(rewardOne == null) {
				log.error("there is no reward for this id {}", mj.getRewardIdOne());
				return false;
			}
			else listOfRewards.add(rewardOne);
		}
		if(mj.getRewardIdTwo() > 0) {
			Reward rewardTwo = rewardRetrieveUtils.getRewardById(mj.getRewardIdTwo());
			if(rewardTwo == null) {
				log.error("there is no reward for this id {}", mj.getRewardIdTwo());
				return false;
			}
			else listOfRewards.add(rewardTwo);
		}
		if(mj.getRewardIdThree() > 0) {
			Reward rewardThree = rewardRetrieveUtils.getRewardById(mj.getRewardIdThree());
			if(rewardThree == null) {
				log.error("there is no reward for this id {}", mj.getRewardIdThree());
				return false;
			}
			else listOfRewards.add(rewardThree);
		}
		return true;
	}
	
	private boolean writeChangesToDB(Builder resBuilder) {

		ara = new AwardRewardAction(userId, user, 0, 0, clientTime, "userminijob with id " + userMiniJobId,
				listOfRewards, userRetrieveUtil, itemForUserRetrieveUtil, insertUtil, updateUtil,
				monsterStuffUtils, monsterLevelInfoRetrieveUtils);
		
		ara.execute();
		
		//delete the user mini job
		int numDeleted = DeleteUtils.get().deleteMiniJobForUser(userMiniJobId);
		log.info("userMiniJob numDeleted=" + numDeleted);

		log.info("updating user's monsters' healths");
		int numUpdated = updateUtil
				.updateUserMonstersHealth(userMonsterIdToExpectedHealth);
		log.info("numUpdated=" + numUpdated);

		//number updated is based on INSERT ... ON DUPLICATE KEY UPDATE
		//so returns 2 if one row was updated, 1 if inserted
		if (numUpdated > 2 * userMonsterIdToExpectedHealth.size()) {
			log.warn("unexpected error: more than user monsters were"
					+ " updated. actual numUpdated=" + numUpdated
					+ "expected: userMonsterIdToExpectedHealth="
					+ userMonsterIdToExpectedHealth);
		}
		return true;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserMiniJobId() {
		return userMiniJobId;
	}

	public void setUserMiniJobId(String userMiniJobId) {
		this.userMiniJobId = userMiniJobId;
	}

	public Timestamp getClientTime() {
		return clientTime;
	}

	public void setClientTime(Timestamp clientTime) {
		this.clientTime = clientTime;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
		return miniJobForUserRetrieveUtil;
	}

	public void setMiniJobForUserRetrieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
	}

	public MiniJobRetrieveUtils getMiniJobRetrieveUtils() {
		return miniJobRetrieveUtils;
	}

	public void setMiniJobRetrieveUtils(MiniJobRetrieveUtils miniJobRetrieveUtils) {
		this.miniJobRetrieveUtils = miniJobRetrieveUtils;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public List<UserMonsterCurrentHealthProto> getUmchpList() {
		return umchpList;
	}

	public void setUmchpList(List<UserMonsterCurrentHealthProto> umchpList) {
		this.umchpList = umchpList;
	}

	public Map<String, Integer> getUserMonsterIdToExpectedHealth() {
		return userMonsterIdToExpectedHealth;
	}

	public void setUserMonsterIdToExpectedHealth(
			Map<String, Integer> userMonsterIdToExpectedHealth) {
		this.userMonsterIdToExpectedHealth = userMonsterIdToExpectedHealth;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

	public RewardRetrieveUtils getRewardRetrieveUtils() {
		return rewardRetrieveUtils;
	}

	public void setRewardRetrieveUtils(RewardRetrieveUtils rewardRetrieveUtils) {
		this.rewardRetrieveUtils = rewardRetrieveUtils;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Collection<String> getUserMiniJobIds() {
		return userMiniJobIds;
	}

	public void setUserMiniJobIds(Collection<String> userMiniJobIds) {
		this.userMiniJobIds = userMiniJobIds;
	}

	public Map<String, MiniJobForUser> getIdToUserMiniJob() {
		return idToUserMiniJob;
	}

	public void setIdToUserMiniJob(Map<String, MiniJobForUser> idToUserMiniJob) {
		this.idToUserMiniJob = idToUserMiniJob;
	}

	public MiniJobForUser getMjfu() {
		return mjfu;
	}

	public void setMjfu(MiniJobForUser mjfu) {
		this.mjfu = mjfu;
	}

	public MiniJob getMj() {
		return mj;
	}

	public void setMj(MiniJob mj) {
		this.mj = mj;
	}

	public AwardRewardAction getAra() {
		return ara;
	}

	public void setAra(AwardRewardAction ara) {
		this.ara = ara;
	}

	public List<Reward> getListOfRewards() {
		return listOfRewards;
	}

	public void setListOfRewards(List<Reward> listOfRewards) {
		this.listOfRewards = listOfRewards;
	}

	
}
