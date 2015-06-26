package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RedeemMiniJobRequestEvent;
import com.lvl6.events.response.ReceivedGiftResponseEvent;
import com.lvl6.events.response.RedeemMiniJobResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.AwardRewardAction;
import com.lvl6.server.controller.actionobjects.RedeemMiniJobAction;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ClanResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
public class RedeemMiniJobController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemMiniJobController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	protected GiftRewardRetrieveUtils giftRewardRetrieveUtils;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil;

	@Autowired
	private UserClanRetrieveUtils2 userClanRetrieveUtils;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;

	@Autowired
	protected MiniJobRetrieveUtils miniJobRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;

	@Autowired
	protected InsertUtil insertUtil;

	@Autowired
	protected DeleteUtil deleteUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;


	public RedeemMiniJobController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RedeemMiniJobRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REDEEM_MINI_JOB_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RedeemMiniJobRequestProto reqProto = ((RedeemMiniJobRequestEvent) event)
				.getRedeemMiniJobRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProtoWithMaxResources senderResourcesProto = reqProto
				.getSender();
		int maxCash = senderResourcesProto.getMaxCash();
		int maxOil = senderResourcesProto.getMaxOil();
		MinimumUserProto senderProto = senderResourcesProto.getMinUserProto();

		String userId = senderProto.getUserUuid();
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		String userMiniJobId = reqProto.getUserMiniJobUuid();

		List<UserMonsterCurrentHealthProto> umchpList = reqProto.getUmchpList();

		RedeemMiniJobResponseProto.Builder resBuilder = RedeemMiniJobResponseProto
				.newBuilder();
		resBuilder.setSender(senderResourcesProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			UUID.fromString(userMiniJobId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userMiniJobId=%s",
					userId, userMiniJobId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			RedeemMiniJobAction rmja = new RedeemMiniJobAction(userId, userMiniJobId, clientTime,
					giftRetrieveUtil, giftRewardRetrieveUtils, userClanRetrieveUtils,
					userRetrieveUtils, itemForUserRetrieveUtil,
					deleteUtil, updateUtil, insertUtil, miniJobForUserRetrieveUtil,
					miniJobRetrieveUtils, monsterStuffUtils, monsterForUserRetrieveUtils, umchpList,
					monsterLevelInfoRetrieveUtils, rewardRetrieveUtils,
					createInfoProtoUtils);

			rmja.execute(resBuilder);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {
				UserRewardProto urp = createInfoProtoUtils.createUserRewardProto(rmja.getAra().getNuOrUpdatedItems(),
						rmja.getAra().getNuOrUpdatedMonsters(), rmja.getAra().getGemsGained(),
						rmja.getAra().getCashGained(), rmja.getAra().getOilGained(), rmja.getAra().getGachaCreditsGained(), null);
				resBuilder.setRewards(urp);
				log.info("rewards: {}", urp);
			}

			RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (resBuilder.getStatus().equals(ResponseStatus.SUCCESS)) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								rmja.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				sendClanGiftIfExists(responses, userId, rmja);

				writeToUserCurrencyHistory(rmja.getUser(), userMiniJobId, rmja.getAra().getCurrencyDeltas(),
						clientTime, rmja.getAra().getPreviousCurrencies());
			}

		} catch (Exception e) {
			log.error("exception in RedeemMiniJobController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				RedeemMiniJobResponseEvent resEvent = new RedeemMiniJobResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in RedeemMiniJobController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

//	//userMonsterIdToExpectedHealth  may be modified
//	private boolean checkLegit(Builder resBuilder, String userId, User user,
//			String userMiniJobId, List<MiniJobForUser> mjfuList,
//			List<UserMonsterCurrentHealthProto> umchpList,
//			Map<String, Integer> userMonsterIdToExpectedHealth) {
//
//		Collection<String> userMiniJobIds = Collections
//				.singleton(userMiniJobId);
//		Map<String, MiniJobForUser> idToUserMiniJob = getMiniJobForUserRetrieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);
//
//		if (idToUserMiniJob.isEmpty() || umchpList.isEmpty()) {
//			log.error("no UserMiniJob exists with id="
//					+ userMiniJobId
//					+ "or invalid userMonsterIds (monsters need to be damaged). "
//					+ " userMonsters=" + umchpList);
//			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
//			return false;
//		}
//
//		MiniJobForUser mjfu = idToUserMiniJob.get(userMiniJobId);
//		if (null == mjfu.getTimeCompleted()) {
//			//sanity check
//			log.error("MiniJobForUser incomplete: " + mjfu);
//			return false;
//		}
//
//		//sanity check
//		int miniJobId = mjfu.getMiniJobId();
//		MiniJob mj = miniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
//		if (null == mj) {
//			log.error("no MiniJob exists with id=" + miniJobId
//					+ "\t invalid MiniJobForUser=" + mjfu);
//			resBuilder.setStatus(RedeemMiniJobStatus.FAIL_NO_MINI_JOB_EXISTS);
//			return false;
//		}
//
//		List<String> userMonsterIds = monsterStuffUtils.getUserMonsterIds(
//				umchpList, userMonsterIdToExpectedHealth);
//
//		Map<String, MonsterForUser> mfuIdsToUserMonsters = getMonsterForUserRetrieveUtils()
//				.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);
//
//		//keep only valid userMonsterIds another sanity check
//		if (userMonsterIds.size() != mfuIdsToUserMonsters.size()) {
//			log.warn("some userMonsterIds client sent are invalid."
//					+ " Keeping valid ones. userMonsterIds=" + userMonsterIds
//					+ " mfuIdsToUserMonsters=" + mfuIdsToUserMonsters);
//
//			//since client sent some invalid monsters, keep only the valid
//			//mappings from userMonsterId to health
//			Set<String> existing = mfuIdsToUserMonsters.keySet();
//			userMonsterIdToExpectedHealth.keySet().retainAll(existing);
//		}
//
//		if (userMonsterIds.isEmpty()) {
//			log.error("no valid user monster ids sent by client");
//			return false;
//		}
//
//		mjfuList.add(mjfu);
//		return true;
//	}
//
//	private boolean writeChangesToDB(Builder resBuilder, String userId,
//			User user, String userMiniJobId, MiniJobForUser mjfu, Date now,
//			Timestamp clientTime, int maxCash, int maxOil,
//			Map<String, Integer> userMonsterIdToExpectedHealth,
//			Map<String, Integer> currencyChange,
//			Map<String, Integer> previousCurrency) {
//		int miniJobId = mjfu.getMiniJobId();
//		MiniJob mj = miniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
//
//		int prevGems = user.getGems();
//		int prevCash = user.getCash();
//		int prevOil = user.getOil();
//
//		//update user currency
//		int gemsChange = mj.getGemReward();
//		int cashChange = mj.getCashReward();
//		int oilChange = mj.getOilReward();
//		int expChange = mj.getExpReward();
//
//		if (!updateUser(user, gemsChange, cashChange, maxCash, oilChange,
//				maxOil, expChange)) {
//			log.error(String
//					.format("could not decrement user gems by %s, cash by %s, and oil by %s",
//							gemsChange, cashChange, oilChange));
//			return false;
//		} else {
//			if (0 != gemsChange) {
//				currencyChange.put(miscMethods.gems, gemsChange);
//				previousCurrency.put(miscMethods.gems, prevGems);
//			}
//			if (0 != cashChange) {
//				currencyChange.put(miscMethods.cash, cashChange);
//				previousCurrency.put(miscMethods.cash, prevCash);
//			}
//			if (0 != oilChange) {
//				currencyChange.put(miscMethods.oil, oilChange);
//				previousCurrency.put(miscMethods.oil, prevOil);
//			}
//		}
//
//		int monsterIdReward = mj.getMonsterIdReward();
//		//give the user the monster if he got one
//		if (0 != monsterIdReward) {
//			StringBuilder mfusopB = new StringBuilder();
//			mfusopB.append(ControllerConstants.MFUSOP__MINI_JOB);
//			mfusopB.append(" ");
//			mfusopB.append(miniJobId);
//			String mfusop = mfusopB.toString();
//			Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
//			monsterIdToNumPieces.put(monsterIdReward, 1);
//
//			log.info("rewarding user with {monsterId->amount}: {}",
//					monsterIdToNumPieces);
//			List<FullUserMonsterProto> newOrUpdated = monsterStuffUtils
//					.updateUserMonsters(userId, monsterIdToNumPieces, null,
//							mfusop, now, monsterLevelInfoRetrieveUtils);
//			FullUserMonsterProto fump = newOrUpdated.get(0);
//			resBuilder.setFump(fump);
//		}
//
//		List<ItemForUser> ifuList = calculateItemRewards(userId, mj);
//		log.info("ifuList={}", ifuList);
//		if (null != ifuList && !ifuList.isEmpty()) {
//			updateUtil.updateItemForUser(ifuList);
//		}
//
//		//delete the user mini job
//		int numDeleted = DeleteUtils.get().deleteMiniJobForUser(userMiniJobId);
//		log.info("userMiniJob numDeleted=" + numDeleted);
//
//		log.info("updating user's monsters' healths");
//		int numUpdated = updateUtil
//				.updateUserMonstersHealth(userMonsterIdToExpectedHealth);
//		log.info("numUpdated=" + numUpdated);
//
//		//number updated is based on INSERT ... ON DUPLICATE KEY UPDATE
//		//so returns 2 if one row was updated, 1 if inserted
//		if (numUpdated > 2 * userMonsterIdToExpectedHealth.size()) {
//			log.warn("unexpected error: more than user monsters were"
//					+ " updated. actual numUpdated=" + numUpdated
//					+ "expected: userMonsterIdToExpectedHealth="
//					+ userMonsterIdToExpectedHealth);
//		}
//
//		return true;
//	}
//
//	private List<ItemForUser> calculateItemRewards(String userId, MiniJob mj) {
//		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();
//
//		int itemIdReward = mj.getItemIdReward();
//		int itemRewardQuantity = mj.getItemRewardQuantity();
//		int secondItemIdReward = mj.getSecondItemIdReward();
//		int secondItemRewardQuantity = mj.getSecondItemRewardQuantity();
//		if (itemIdReward > 0 && itemRewardQuantity > 0) {
//			itemIdToQuantity.put(itemIdReward, itemRewardQuantity);
//
//			//    	ItemForUser ifuOne = new ItemForUser(userId,
//			//    		itemIdReward, itemRewardQuantity);
//			//    	int numUpdated = updateUtil.updateItemForUser(
//			//    		userId, itemIdReward, itemRewardQuantity);
//			//
//			//
//			//    	String preface = "rewarding user with more items.";
//			//    	log.info(
//			//    		"%s itemId=%s, \t amount=%s, numUpdated=%s",
//			//    		new Object[] { preface, itemIdReward, itemRewardQuantity,
//			//    			numUpdated});
//		}
//
//		if (secondItemIdReward > 0 && secondItemRewardQuantity > 0) {
//			int newQuantity = secondItemRewardQuantity;
//			if (itemIdToQuantity.containsKey(secondItemIdReward)) {
//				newQuantity += itemIdToQuantity.get(secondItemIdReward);
//			}
//			itemIdToQuantity.put(secondItemIdReward, newQuantity);
//		}
//
//		List<ItemForUser> ifuList = BoosterItemUtils
//				.calculateItemRewards(userId, itemForUserRetrieveUtil,
//						itemIdToQuantity);
//		return ifuList;
//	}
//
//	private boolean updateUser(User u, int gemsChange, int cashChange,
//			int maxCash, int oilChange, int maxOil, int expChange) {
//		//capping how much the user can gain of a certain resource
//		int curCash = Math.min(u.getCash(), maxCash); //in case user's cash is more than maxCash
//		int maxCashUserCanGain = maxCash - curCash; //this is the max cash the user can gain
//		cashChange = Math.min(maxCashUserCanGain, cashChange);
//
//		int curOil = Math.min(u.getOil(), maxOil); //in case user's oil is more than maxOil
//		int maxOilUserCanGain = maxOil - curOil;
//		oilChange = Math.min(maxOilUserCanGain, oilChange);
//
//		if (0 == cashChange && 0 == oilChange && 0 == gemsChange
//				&& 0 == expChange) {
//			log.info("after caping rewards to max, user gets no resources");
//			return true;
//		}
//
//		//    int numChange = u.updateRelativeCashAndOilAndGems(cashChange,
//		//        oilChange, gemsChange);
//
//		//    if (numChange <= 0) {
//		if (!u.updateRelativeGemsCashOilExperienceNaive(gemsChange, cashChange,
//				oilChange, expChange)) {
//			String preface = "could not update user gems, cash, oil, exp.";
//			log.error(String.format(
//					"%s gemChange=%s, cash=%s, oil=%s, exp=%s, user=%s",
//					preface, gemsChange, cashChange, oilChange, expChange, u));
//			return false;
//		}
//		return true;
//	}

	private void sendClanGiftIfExists(
			ToClientEvents responses,
			String userId,
			RedeemMiniJobAction rmja) {
		try {
			AwardRewardAction ara = rmja.getAra();
			if (null != ara && ara.existsClanGift()) {
				ReceivedGiftResponseProto rgrp = ara.getClanGift();
				ReceivedGiftResponseEvent rgre = new ReceivedGiftResponseEvent(userId);
				rgre.setResponseProto(rgrp);
				String clanId = rmja.getUser().getClanId();

				responses.clanResponseEvents().add(new ClanResponseEvent(rgre, clanId, false));
			}
		} catch (Exception e) {
			log.error("failed to send ClanGift notification", e);
		}
	}

	private void writeToUserCurrencyHistory(User aUser, String userMiniJobId,
			Map<String, Integer> currencyChange, Timestamp curTime,
			Map<String, Integer> previousCurrency) {
		String userId = aUser.getId();
		String reason = ControllerConstants.UCHRFC__SPED_UP_COMPLETE_MINI_JOB;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("userMiniJobId=");
		detailsSb.append(userMiniJobId);

		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = miscMethods.gems;

		currentCurrency.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, detailsSb.toString());

		miscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public MiniJobForUserRetrieveUtil getMiniJobForUserRetrieveUtil() {
		return miniJobForUserRetrieveUtil;
	}

	public void setMiniJobForUserRetrieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetrieveUtil) {
		this.miniJobForUserRetrieveUtil = miniJobForUserRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

}
