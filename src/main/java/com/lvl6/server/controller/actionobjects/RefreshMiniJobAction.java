package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Item;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MiniJob;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniJobProto.RefreshMiniJobResponseProto.Builder;
import com.lvl6.proto.EventMiniJobProto.RefreshMiniJobResponseProto.RefreshMiniJobStatus;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class RefreshMiniJobAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<String> deleteUserMiniJobIds;
	private int itemIdUsed;
	private int numToSpawn;
	private int gemsSpent;
	private String minQualitySpawned;
	private Date clientTime;
	private int structId;
	private Random randGen;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private ItemRetrieveUtils itemRetrieveUtil;
	private UserRetrieveUtils2 userRetrieveUtil;
	private MiniJobRetrieveUtils miniJobRetrieveUtil;

	private UpdateUtil updateUtil;
	private InsertUtil insertUtil;
	private DeleteUtil deleteUtil;

	public RefreshMiniJobAction(String userId,
			List<String> deleteUserMiniJobIds, int itemIdUsed, int numToSpawn,
			int gemsSpent, String minQualitySpawned, Date clientTime,
			int structId, Random randGen,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			ItemRetrieveUtils itemRetrieveUtil,
			UserRetrieveUtils2 userRetrieveUtil,
			MiniJobRetrieveUtils miniJobRetrieveUtil, UpdateUtil updateUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.deleteUserMiniJobIds = deleteUserMiniJobIds;
		this.itemIdUsed = itemIdUsed;
		this.numToSpawn = numToSpawn;
		this.gemsSpent = gemsSpent;
		this.minQualitySpawned = minQualitySpawned;
		this.clientTime = clientTime;
		this.structId = structId;
		this.randGen = randGen;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.itemRetrieveUtil = itemRetrieveUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.miniJobRetrieveUtil = miniJobRetrieveUtil;
		this.updateUtil = updateUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RefreshMiniJobResource {
	//
	//
	//		public RefreshMiniJobResource() {
	//
	//		}
	//	}
	//
	//	public RefreshMiniJobResource execute() {
	//
	//	}

	//derived state
	private User user;
	private boolean usedGems;
	private Item item;
	private List<ItemForUser> ifus;
	private ItemForUser ifu;
	private List<MiniJob> spawnedMiniJobs;
	private Map<Integer, MiniJob> miniJobIdToMj;
	private List<MiniJobForUser> userMiniJobs;

	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RefreshMiniJobStatus.FAIL_OTHER);

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

		resBuilder.setStatus(RefreshMiniJobStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (numToSpawn <= 0)
		{
			log.error("numToSpawn invalid: {}", numToSpawn);
			return false;
		}

		boolean setQuality = (null != minQualitySpawned && !minQualitySpawned.isEmpty());
		// if user spending gems, make sure quality is set
		if (gemsSpent > 0 && setQuality) {
			return true;
		}

		if (itemIdUsed > 0)
		{
			return true;
		}


		log.error(
				"invalid itemIdUsed={}, or not positive gemsSpent={} or not set minQualityToSpawn={}",
				new Object[] { itemIdUsed, gemsSpent, minQualitySpawned } );

		return false;
	}

	private boolean verifySemantics(Builder resBuilder) {

		if (gemsSpent > 0)
		{
			user = userRetrieveUtil.getUserById(userId);
			if (null == user)
			{
				log.error(String.format("no user with id=%s", userId));
				return false;
			}

			//check gems
			if (!hasEnoughGems(resBuilder, gemsSpent))
			{
				resBuilder
				.setStatus(RefreshMiniJobStatus.FAIL_INSUFFICIENT_GEMS);
				return false;
			}

			usedGems = true;
		}

		if (itemIdUsed > 0)
		{

			if (!validItem()) {
				return false;
			}

			//verify user has sufficient items
			if (!hasEnoughItems(resBuilder))
			{
				resBuilder.setStatus(RefreshMiniJobStatus.FAIL_INSUFFICIENT_ITEMS);
				return false;
			}
		}

		//could check miniJobs

		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder, int gemsSpent) {
		int userGems = user.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("insufficient gems. userGems={}, gemsSpent={}",
					userGems, gemsSpent);
			return false;
		}
		return true;
	}

	private boolean validItem() {
		item = itemRetrieveUtil.getItemForId(itemIdUsed);
		if (null == item) {
			log.error("invalid itemId={}", itemIdUsed);
			return false;
		}
		return true;
	}


	private boolean hasEnoughItems(Builder resBuilder) {
		ifus = itemForUserRetrieveUtil.getSpecificOrAllItemForUser(
				userId, Collections.singleton(itemIdUsed));

		if (null == ifus || ifus.isEmpty()) {
			log.error("user does not have item={}", itemIdUsed);
			return false;
		}

		ifu = ifus.get(0);
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		int numUpdated = 0;
		if (usedGems) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());

			//charge the user
			log.info("user before: {}", user);
			int gemChange = -1 * Math.abs(gemsSpent);
			numUpdated = user.updateRelativeCashAndOilAndGems(0, 0, gemChange);
			log.info("user after: {}", user);

			prepCurrencyHistory();

		} else {
			int nuQuantity = ifu.getQuantity() - 1;
			ifu.setQuantity(nuQuantity);

			//update items to reflect being used
			numUpdated = updateUtil.updateItemForUser(ifus);
			log.info("itemForUser numUpdated: {}, updated={}",
					numUpdated, ifus);
		}

		if (numUpdated <= 0)
		{
			return false;
		}

		//delete the user's mini jobs
		int deleted = deleteUtil.deleteMiniJobForUser(userId, deleteUserMiniJobIds);
		log.info("num deleted={}", deleted);

		//update the user's mini jobs
		//TODO: COPY PASTED FROM SpawnMiniJobController.
		spawnMiniJobs();
		if (null == spawnedMiniJobs || spawnedMiniJobs.isEmpty())
		{
			log.error("unable to spawn mini jobs, gems={}, itemId={}",
					gemsSpent, itemIdUsed);
			return false;
		}
		convertIntoUserMiniJobs();
		List<String> userMiniJobIds = insertUtil.
				insertIntoMiniJobForUserGetIds(userId, userMiniJobs);
		log.info("inserted MiniJobForUser into mini_job_for_user: {}, \t ids={}",
				userMiniJobs, userMiniJobIds);

		for (int index = 0; index < userMiniJobIds.size(); index++) {
			String userMiniJobId = userMiniJobIds.get(index);
			MiniJobForUser mjfu = userMiniJobs.get(index);
			mjfu.setId(userMiniJobId);
		}

		return true;
	}

	private void prepCurrencyHistory() {
		String gems = MiscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		currencyDeltas.put(gems, -1 * gemsSpent);
		curCurrencies.put(gems, user.getGems());
		reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__RESET_MINI_JOB);

		details = new HashMap<String, String>();
		details.put(gems,
				String.format("minQualitySpawned=%s", minQualitySpawned) );
	}

	//TODO: CONSIDER REFACTORING SpawnMiniJobController
	private void spawnMiniJobs() {
		if (!usedGems)
		{
			//also means that user didn't use gems
			minQualitySpawned = item.getQuality();
		}

		spawnedMiniJobs = new ArrayList<MiniJob>();
		miniJobIdToMj = new HashMap<Integer, MiniJob>();
		for (int i = 0; i < numToSpawn; i++)
		{
			float randFloat = randGen.nextFloat();
			MiniJob mj = miniJobRetrieveUtil.nextMiniJob(
					structId, minQualitySpawned, randFloat);

			spawnedMiniJobs.add(mj);

			miniJobIdToMj.put(mj.getId(), mj);
		}
		log.info("spawned MiniJobs {}", spawnedMiniJobs);
	}

	//TODO: COPY PASTED FROM SpawnMiniJobController
	private List<MiniJobForUser> convertIntoUserMiniJobs() {
		userMiniJobs = new ArrayList<MiniJobForUser>();

		for (MiniJob mj : spawnedMiniJobs) {
			MiniJobForUser mjfu = new MiniJobForUser();

			int miniJobId = mj.getId();
			int dmgDealt = mj.getDmgDealt();
			int durationSeconds = mj.getDurationSeconds();

			mjfu.setUserId(userId);
			mjfu.setMiniJobId(miniJobId);
			mjfu.setBaseDmgReceived(dmgDealt);
			mjfu.setDurationSeconds(durationSeconds);

			userMiniJobs.add(mjfu);
		}

		return userMiniJobs;
	}

	public boolean isUsedGems() {
		return usedGems;
	}

	public void setUsedGems(boolean usedGems) {
		this.usedGems = usedGems;
	}

	public User getUser() {
		return user;
	}

	public List<MiniJobForUser> getUserMiniJobs() {
		return userMiniJobs;
	}

	public Map<Integer, MiniJob> getMiniJobIdToMj() {
		return miniJobIdToMj;
	}

	public void setMiniJobIdToMj(Map<Integer, MiniJob> miniJobIdToMj) {
		this.miniJobIdToMj = miniJobIdToMj;
	}

	public void setUserMiniJobs(List<MiniJobForUser> userMiniJobs) {
		this.userMiniJobs = userMiniJobs;
	}

	public Map<String, Integer> getCurrencyDeltas() {
		return currencyDeltas;
	}

	public Map<String, Integer> getPreviousCurrencies() {
		return prevCurrencies;
	}

	public Map<String, Integer> getCurrentCurrencies() {
		return curCurrencies;
	}

	public Map<String, String> getReasons() {
		return reasonsForChanges;
	}

	public Map<String, String> getDetails() {
		return details;
	}
}
