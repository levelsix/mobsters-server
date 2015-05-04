package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanIcon;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto.Builder;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto.ChangeClanSettingsStatus;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ClanIconRetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class ChangeClanSettingsAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private boolean isChangeDescription;
	private String description;
	private boolean isChangeJoinType;
	private boolean requestToJoinRequired;
	private boolean isChangeIcon;
	private int iconId;
	private boolean lockedClan;
	private UserRetrieveUtils2 userRetrieveUtils;
	protected UpdateUtil updateUtil;
	private MiscMethods miscMethods;
	private ClanRetrieveUtils2 clanRetrieveUtils;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private ClanIconRetrieveUtils clanIconRetrieveUtils;


	public ChangeClanSettingsAction(String userId, boolean isChangeDescription,
			String description, boolean isChangeJoinType,
			boolean requestToJoinRequired, boolean isChangeIcon, int iconId, 
			boolean lockedClan, UserRetrieveUtils2 userRetrieveUtils, 
			UpdateUtil updateUtil, MiscMethods miscMethods, 
			ClanRetrieveUtils2 clanRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			ClanIconRetrieveUtils clanIconRetrieveUtils) {
		super();
		this.userId = userId;
		this.isChangeDescription = isChangeDescription;
		this.description = description;
		this.isChangeJoinType = isChangeJoinType;
		this.requestToJoinRequired = requestToJoinRequired;
		this.isChangeIcon = isChangeIcon;
		this.iconId = iconId;
		this.lockedClan = lockedClan;
		this.userRetrieveUtils = userRetrieveUtils;
		this.updateUtil = updateUtil;
		this.miscMethods = miscMethods;
		this.clanRetrieveUtils = clanRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.clanIconRetrieveUtils = clanIconRetrieveUtils;
	}

	private User user;
	private Clan clan;
	private Timestamp createTime;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);

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

		resBuilder.setStatus(ChangeClanSettingsStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		user = userRetrieveUtils.getUserById(userId);

		if (!lockedClan) {
			log.error("couldn't obtain clan lock");
			return false;
		}

		String clanIdUser = user.getClanId();
		if (null == clanIdUser || clanIdUser.isEmpty()) {
			resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_NOT_IN_CLAN);
			log.error("user not in clan");
			return false;
		}

		clan = clanRetrieveUtils.getClanWithId(clanIdUser);

		if (user == null || clan == null) {
			resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
			log.error("userId is {}, user is {}, clanId is {}, clan is {}",
					new Object[] {userId, user, clan.getId(), clan});
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		List<String> userIds = userClanRetrieveUtils.getUserIdsWithStatuses(
				clan.getId(), statuses);

		Set<String> uniqUserIds = new HashSet<String>();
		if (null != userIds && !userIds.isEmpty()) {
			uniqUserIds.addAll(userIds);
		}

		if (!uniqUserIds.contains(userId)) {
			resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_NOT_AUTHORIZED);
			log.error("clan member can't change clan description member= {}", user);
			return false;
		}
		resBuilder.setStatus(ChangeClanSettingsStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		if (isChangeDescription) {
			if (description.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_DESCRIPTION) {
				resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
				log.warn("description is {}, and length of that is {}, max size is {}", 
						new Object[] {description, description.length(), ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_DESCRIPTION});
			}
			return false;
		} else {
			clan.setDescription(description);
		}

		if (isChangeJoinType) {
			clan.setRequestToJoinRequired(requestToJoinRequired);
		}

		if (isChangeIcon) {
			ClanIcon ci = clanIconRetrieveUtils.getClanIconForId(iconId);
			if (null == ci) {
				resBuilder.setStatus(ChangeClanSettingsStatus.FAIL_OTHER);
				log.warn("no clan icon with id={}", iconId);
			} else {
				clan.setClanIconId(iconId);
			}
		}

		log.info("about to update");
		int numUpdated = updateUtil.updateClan(clan.getId(),
				isChangeDescription, description, isChangeJoinType,
				requestToJoinRequired, isChangeIcon, iconId);

		log.info("numUpdated (should be 1)={}", numUpdated);
		return true;
	}



	public User getUser() {
		return user;
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

	public MiscMethods getMiscMethods() {
		return miscMethods;
	}

	public void setMiscMethods(MiscMethods miscMethods) {
		this.miscMethods = miscMethods;
	}

	public boolean isRequestToJoinRequired() {
		return requestToJoinRequired;
	}

	public void setRequestToJoinRequired(boolean requestToJoinRequired) {
		this.requestToJoinRequired = requestToJoinRequired;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public Map<String, Integer> getPrevCurrencies() {
		return prevCurrencies;
	}

	public void setPrevCurrencies(Map<String, Integer> prevCurrencies) {
		this.prevCurrencies = prevCurrencies;
	}

	public Map<String, Integer> getCurCurrencies() {
		return curCurrencies;
	}

	public void setCurCurrencies(Map<String, Integer> curCurrencies) {
		this.curCurrencies = curCurrencies;
	}

	public Map<String, String> getReasonsForChanges() {
		return reasonsForChanges;
	}

	public void setReasonsForChanges(Map<String, String> reasonsForChanges) {
		this.reasonsForChanges = reasonsForChanges;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setCurrencyDeltas(Map<String, Integer> currencyDeltas) {
		this.currencyDeltas = currencyDeltas;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public boolean isChangeDescription() {
		return isChangeDescription;
	}

	public void setChangeDescription(boolean isChangeDescription) {
		this.isChangeDescription = isChangeDescription;
	}

	public boolean isChangeJoinType() {
		return isChangeJoinType;
	}

	public void setChangeJoinType(boolean isChangeJoinType) {
		this.isChangeJoinType = isChangeJoinType;
	}

	public boolean isChangeIcon() {
		return isChangeIcon;
	}

	public void setChangeIcon(boolean isChangeIcon) {
		this.isChangeIcon = isChangeIcon;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public boolean isLockedClan() {
		return lockedClan;
	}

	public void setLockedClan(boolean lockedClan) {
		this.lockedClan = lockedClan;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public UserClanRetrieveUtils2 getUserClanRetrieveUtils() {
		return userClanRetrieveUtils;
	}

	public void setUserClanRetrieveUtils(
			UserClanRetrieveUtils2 userClanRetrieveUtils) {
		this.userClanRetrieveUtils = userClanRetrieveUtils;
	}

	public ClanIconRetrieveUtils getClanIconRetrieveUtils() {
		return clanIconRetrieveUtils;
	}

	public void setClanIconRetrieveUtils(ClanIconRetrieveUtils clanIconRetrieveUtils) {
		this.clanIconRetrieveUtils = clanIconRetrieveUtils;
	}

	public Clan getClan() {
		return clan;
	}

	public void setClan(Clan clan) {
		this.clan = clan;
	}



}
