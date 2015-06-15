package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.GiftForTangoUser;
import com.lvl6.info.GiftForUser;
import com.lvl6.info.TangoGift;
import com.lvl6.info.TangoGiftReward;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventRewardProto.SendTangoGiftResponseProto.Builder;
import com.lvl6.proto.EventRewardProto.SendTangoGiftResponseProto.SendTangoGiftStatus;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.TangoGiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TangoGiftRewardRetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

public class SendTangoGiftAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String gifterUserId;
	private String gifterTangoUserId;
	private int gemReward;
	private Date clientTime;
	private Set<String> tangoUserIds;
	private UserRetrieveUtils2 userRetrieveUtil;
	private TangoGiftRetrieveUtils tangoGiftRetrieveUtil;
	private TangoGiftRewardRetrieveUtils tangoGiftRewardRetrieveUtil;
	private InsertUtil insertUtil;

	public SendTangoGiftAction(String userId, String senderTangoUserId,
			int gemReward, Date clientTime, Set<String> tangoIds,
			UserRetrieveUtils2 userRetrieveUtil,
			TangoGiftRetrieveUtils tangoGiftRetrieveUtil,
			TangoGiftRewardRetrieveUtils tangoGiftRewardRetrieveUtil,
			InsertUtil insertUtil)
	{
		super();
		this.gifterUserId = userId;
		this.gemReward = gemReward;
		this.gifterTangoUserId = senderTangoUserId;
		this.clientTime = clientTime;
		this.tangoUserIds = tangoIds;
		this.userRetrieveUtil = userRetrieveUtil;
		this.tangoGiftRetrieveUtil = tangoGiftRetrieveUtil;
		this.tangoGiftRewardRetrieveUtil = tangoGiftRewardRetrieveUtil;
		this.insertUtil = insertUtil;
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
	protected boolean emptyTangoUsers;
	protected int tangoGiftId;
	protected TangoGift tangoGift;
	protected User gifter;
	protected Map<String, User> userIdToReceiver;
	protected Collection<String> nonToonSquadTangoUserIds;
	protected Collection<String> toonSquadTangoUserIds;
	protected List<GiftForUser> receiverGifts;
	protected Map<String, GiftForTangoUser> giftForUserIdToGiftForTangoUser;
	protected Random rand;

	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(SendTangoGiftStatus.FAIL_OTHER);

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

		resBuilder.setStatus(SendTangoGiftStatus.SUCCESS);

	}

//	private boolean verifySyntax(Builder resBuilder) {
//		return true;
//	}
//
	private boolean verifySemantics(Builder resBuilder) {
		Map<Integer, TangoGift> tangoGifts =
				tangoGiftRetrieveUtil.getTangoGiftIdsToTangoGifts();
		if (null == tangoGifts || tangoGifts.isEmpty()) {
			log.error("no TangoGifts available");
			return false;
		}
		//only one element so...
		for (Integer id : tangoGifts.keySet())
		{
			tangoGiftId = id;
			tangoGift = tangoGifts.get(id);
		}

		if (null == tangoUserIds || tangoUserIds.isEmpty()) {
			log.info("no tango users sent, but awarding user gems");
			emptyTangoUsers = true;
			gifter = userRetrieveUtil.getUserById(gifterUserId);
			return true;
		}

		//to get rid of another db call just to get gifter...
		tangoUserIds.add(gifterTangoUserId);
		Map<String, User> tangoUsers = userRetrieveUtil
				.getUsersForTangoIdsMap(tangoUserIds);
		if (!tangoUsers.containsKey(gifterUserId))
		{
			log.error("no user with id={}, tangoUsers={}",
					gifterUserId, tangoUsers);
			return false;
		}

		gifter = tangoUsers.remove(gifterUserId);
		tangoUserIds.remove(gifterTangoUserId);
		userIdToReceiver = tangoUsers;
		rand = ControllerConstants.RAND;

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();
		prevCurrencies.put(MiscMethods.gems, gifter.getGems());

		//update the user's last_tango_gift_sent_time
		if (!gifter.updateLastTangoGiftSentTime(clientTime, gemReward)) {
			log.error("unable to update lastTangoGiftSentTime to {}, gems={}",
					clientTime, gemReward);
			return false;
		}

		prepCurrencyHistory();

		if (emptyTangoUsers) {
			log.info("only updating the tango_gift_time and gems");
			return true;
		}

		boolean success = processTangoUsersInToonSquad();
		return success;
	}

	private boolean processTangoUsersInToonSquad() {
		nonToonSquadTangoUserIds = new HashSet<String>(tangoUserIds);
		toonSquadTangoUserIds = new HashSet<String>();
		receiverGifts = new ArrayList<GiftForUser>();
		//filter out those tango ids in and not in toon squad
		for (User receiver : userIdToReceiver.values())
		{
			String tangoId = receiver.getTangoId();
			nonToonSquadTangoUserIds.remove(tangoId);
			toonSquadTangoUserIds.add(tangoId);

			GiftForUser gfu = createGiftForUser(receiver);
			receiverGifts.add(gfu);

		}

		boolean success = insertUtil.insertGiftForUser(receiverGifts);

		List<GiftForTangoUser> gftuList = null;
		if (success) {
			giftForUserIdToGiftForTangoUser = new HashMap<String, GiftForTangoUser>();
			gftuList = new ArrayList<GiftForTangoUser>();
			for (GiftForUser gfu : receiverGifts) {
				GiftForTangoUser gftu = new GiftForTangoUser();
				String id = gfu.getId();
				gftu.setGiftForUserId(id);
				gftu.setGifterUserId(gifterUserId);
				gftu.setGifterTangoUserId(gifterTangoUserId);
				gftuList.add(gftu);

				giftForUserIdToGiftForTangoUser.put(id, gftu);
			}
		} else {
			log.error("unable to insert GiftForUser data {}", receiverGifts);
			return false;
		}

		if (null != gftuList && !gftuList.isEmpty()) {
			success = insertUtil.insertGiftForTangoUser(gftuList);
			if (!success) {
				log.error("unable to insert GiftForTangoUser data {}", gftuList);
				//TODO: CONSIDER DELETING THE INSERTED GiftForUser data.
				return false;
			}
		}
		return true;
	}

	private GiftForUser createGiftForUser(User receiver) {
		GiftForUser gfu = new GiftForUser();
		gfu.setGifterUserId(gifterUserId);
		gfu.setReceiverUserId(receiver.getId());
		gfu.setGiftType(RewardType.TANGO_GIFT.name());
		gfu.setStaticDataId(tangoGiftId);
		gfu.setTimeOfEntry(clientTime);

		int rewardId = getReward();
		gfu.setRewardId(rewardId);
		gfu.setCollected(false);
		gfu.setMinutesTillExpiration(tangoGift.getHoursUntilExpiration() * 60);
		gfu.setReasonForGift(ControllerConstants.REWARD_REASON__TANGO_GIFT);

		return gfu;
	}

	private int getReward()
	{
		float prob = rand.nextFloat();

		TangoGiftReward tgr = tangoGiftRewardRetrieveUtil
				.nextTangoGiftReward(tangoGiftId, prob);

		return tgr.getRewardId();
	}

	private void prepCurrencyHistory() {
		String gems = MiscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		currencyDeltas.put(gems, gemReward);

		curCurrencies = new HashMap<String, Integer>();
		curCurrencies.put(gems, gifter.getGems());

		reasonsForChanges = new HashMap<String, String>();
		reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__TANGO_GIFT);


		details = new HashMap<String, String>();
		details.put(gems, "");
	}

	public User getGifter() {
		return gifter;
	}

	public Collection<String> getNonToonSquadTangoUserIds() {
		if (null == nonToonSquadTangoUserIds) {
			nonToonSquadTangoUserIds = new HashSet<String>();
		}
		return nonToonSquadTangoUserIds;
	}

	public void setNonToonSquadTangoUserIds(
			Collection<String> nonToonSquadTangoUserIds) {
		this.nonToonSquadTangoUserIds = nonToonSquadTangoUserIds;
	}

	public Collection<String> getToonSquadTangoUserIds() {
		if (null == toonSquadTangoUserIds) {
			toonSquadTangoUserIds = new HashSet<String>();
		}
		return toonSquadTangoUserIds;
	}

	public void setToonSquadTangoUserIds(Collection<String> toonSquadTangoUserIds) {
		this.toonSquadTangoUserIds = toonSquadTangoUserIds;
	}

	public TangoGift getTangoGift() {
		return tangoGift;
	}

	public void setTangoGift(TangoGift tangoGift) {
		this.tangoGift = tangoGift;
	}

	public List<GiftForUser> getReceiverGifts() {
		return receiverGifts;
	}

	public void setReceiverGifts(List<GiftForUser> receiverGifts) {
		this.receiverGifts = receiverGifts;
	}

	public Map<String, GiftForTangoUser> getGiftForUserIdToGiftForTangoUser() {
		return giftForUserIdToGiftForTangoUser;
	}

	public void setGiftForUserIdToGiftForTangoUser(
			Map<String, GiftForTangoUser> giftForUserIdToGiftForTangoUser) {
		this.giftForUserIdToGiftForTangoUser = giftForUserIdToGiftForTangoUser;
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