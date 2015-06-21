package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.clansearch.ClanSearch;
import com.lvl6.clansearch.HazelcastClanSearchImpl;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto.LeaveClanStatus;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtils;

public class LeaveClanAction {
	private static Logger log = LoggerFactory.getLogger( LeaveClanAction.class);

	private String userId;
	private String clanId;
	private boolean lockedClan;
	private UserRetrieveUtils2 userRetrieveUtils;
	protected InsertUtil insertUtil;
	protected DeleteUtil deleteUtil;
	private ClanRetrieveUtils2 clanRetrieveUtils;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private HazelcastClanSearchImpl hzClanSearch;
	private ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;
	private TimeUtils timeUtils;
	private ClanSearch clanSearch;
	private ServerToggleRetrieveUtils toggle;
	
	public LeaveClanAction(
			String userId, String clanId,
			boolean lockedClan, UserRetrieveUtils2 userRetrieveUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil,
			ClanRetrieveUtils2 clanRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			HazelcastClanSearchImpl hzClanSearch,
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil,
			TimeUtils timeUtils, ClanSearch clanSearch,
			ServerToggleRetrieveUtils toggle) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
		this.clanRetrieveUtils = clanRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.hzClanSearch = hzClanSearch;
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
		this.timeUtils = timeUtils;
		this.clanSearch = clanSearch;
		this.toggle = toggle;
	}

	private User user;
	private Clan clan;
	private List<String> clanOwnerIdList;
	private List<Integer> clanSizeContainer;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(LeaveClanStatus.FAIL_OTHER);

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

		resBuilder.setStatus(LeaveClanStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		user = userRetrieveUtils.getUserById(userId);
		clan = clanRetrieveUtils.getClanWithId(clanId);

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}
		if (user == null || clan == null) {
			log.error("user is null");
			return false;
		}
		if (user.getClanId() == null || !user.getClanId().equals(clan.getId())) {
			resBuilder.setStatus(LeaveClanStatus.FAIL_NOT_IN_CLAN);
			log.error(String.format("user's clan id={}, clan id={}",
					user.getClanId(), clan.getId()));
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		//one query to get the leader and the members in a clan
		String clanId = user.getClanId();
		String leaderStatus = UserClanStatus.LEADER.name();
		List<String> statuses = new ArrayList<String>();
		statuses.add(leaderStatus);
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		Map<String, String> userIdsAndStatuses = userClanRetrieveUtils
				.getUserIdsToStatuses(clanId, statuses);

		String clanOwnerId = getClanOwnerId(leaderStatus, userIdsAndStatuses);

		int userClanMembersInClan = userIdsAndStatuses.size();
		if (clanOwnerId.equals(user.getId())) {
			if (userClanMembersInClan > 1) {
				resBuilder
				.setStatus(LeaveClanStatus.FAIL_OWNER_OF_CLAN_WITH_OTHERS_STILL_IN);
				String preface = "user is owner and he's not alone in clan,";
				String preface2 = "can't leave without switching ownership.";
				log.error("{} {} user clan members are {}",
						new Object[] {preface, preface2, userClanMembersInClan});
				return false;
			}
		}
		clanOwnerIdList = new ArrayList<String>();
		clanSizeContainer = new ArrayList<Integer>();
		clanOwnerIdList.add(clanOwnerId);
		clanSizeContainer.add(userClanMembersInClan);
		return true;
	}
	
	private String getClanOwnerId(String leaderStatus,
			Map<String, String> userIdsAndStatuses) {
		String clanOwnerId = null;
		if (null != userIdsAndStatuses && !userIdsAndStatuses.isEmpty()) {

			//find clanOwnerId
			for (String userId : userIdsAndStatuses.keySet()) {
				String status = userIdsAndStatuses.get(userId);

				if (leaderStatus.equals(status)) {
					clanOwnerId = userId;
				}
			}
		}
		return clanOwnerId;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		String clanId = clan.getId();
		
		String clanOwnerId = clanOwnerIdList.get(0);
		int clanSize = clanSizeContainer.get(0);

		if (userId.equals(clanOwnerId)) {
			List<String> userIds = getUserClanRetrieveUtils()
					.getUserIdsRelatedToClan(clanId);
			deleteClan(clanId, clan, userIds, user);
		} else {
			if (!DeleteUtils.get().deleteUserClan(userId, clanId)) {
				log.error(String.format(
						"problem deleting UserClan. user=%s, clan=%s", user,
						clan));
				return false;
			}
			if (!user.updateRelativeCoinsAbsoluteClan(0, null)) {
				log.error("problem with making clanid for user null");
				return false;
			}
		}
		Date lastChatPost = clanChatPostRetrieveUtil.getLastChatPost(clanId);

		if (null == lastChatPost) {
			//for the clans that have not chatted at all
			lastChatPost = ControllerConstants.INCEPTION_DATE;
		}

		//need to account for this user leaving clan
		ExitClanAction eca = new ExitClanAction(userId, clanId, clanSize - 1,
				lastChatPost, timeUtils, UpdateUtils.get(), hzClanSearch, clanSearch, toggle);
		eca.execute();

		return true;
	}
	
	private void deleteClan(String clanId, Clan clan, List<String> userIds,
			User user) {
		if (!user.updateRelativeCoinsAbsoluteClan(0, null)) {
			log.error("problem marking clan id null for users with ids in {}",
					userIds);
			return;
		}

		if (!deleteUtil.deleteUserClanDataRelatedToClanId(clan.getId(),
				userIds.size())) {
			log.error("problem with deleting user clan data for clan with id {}",
					clan.getId());
		} else {
			if (!deleteUtil.deleteClanWithClanId(clan.getId())) {
				log.error("problem with deleting clan with id {}", clan.getId());
				return;
			}
			clanSearch.removeClanId(clanId);
		}

	}


	public User getUser() {
		return user;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isLockedClan() {
		return lockedClan;
	}

	public void setLockedClan(boolean lockedClan) {
		this.lockedClan = lockedClan;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public Clan getClan() {
		return clan;
	}

	public void setClan(Clan clan) {
		this.clan = clan;
	}

	public HazelcastClanSearchImpl getHzClanSearch() {
		return hzClanSearch;
	}

	public void setHzClanSearch(HazelcastClanSearchImpl hzClanSearch) {
		this.hzClanSearch = hzClanSearch;
	}

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtil() {
		return clanChatPostRetrieveUtil;
	}

	public void setClanChatPostRetrieveUtil(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil) {
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public List<String> getClanOwnerIdList() {
		return clanOwnerIdList;
	}

	public void setClanOwnerIdList(List<String> clanOwnerIdList) {
		this.clanOwnerIdList = clanOwnerIdList;
	}

	public List<Integer> getClanSizeContainer() {
		return clanSizeContainer;
	}

	public void setClanSizeContainer(List<Integer> clanSizeContainer) {
		this.clanSizeContainer = clanSizeContainer;
	}
	
	

}
