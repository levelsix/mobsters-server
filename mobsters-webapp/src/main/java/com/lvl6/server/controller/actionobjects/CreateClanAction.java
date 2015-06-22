package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.Builder;
import com.lvl6.proto.EventClanProto.CreateClanResponseProto.CreateClanStatus;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.utils.ResourceUtil;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component@Scope("prototype")public class CreateClanAction {
	private static Logger log = LoggerFactory.getLogger( CreateClanAction.class);

	private String userId;
	private int cashChange;
	private int gemsSpent;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtils; 
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected DeleteUtil deleteUtil;
	private MiscMethods miscMethods;
	private String clanName;
	private String tag;
	private boolean requestToJoinRequired;
	private String description;
	private int clanIconId;
	@Autowired protected ClanRetrieveUtils2 clanRetrieveUtils; 
	@Autowired protected ResourceUtil resourceUtil; 


	public CreateClanAction(
			String userId,
			int cashChange, int gemsSpent, UserRetrieveUtils2 userRetrieveUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil,
			MiscMethods miscMethods, String clanName, String tag,
			boolean requestToJoinRequired, String description, int clanIconId,
			ClanRetrieveUtils2 clanRetrieveUtils,
			ResourceUtil resourceUtil) {
		super();
		this.userId = userId;
		this.cashChange = cashChange;
		this.gemsSpent = gemsSpent;
		this.userRetrieveUtils = userRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
		this.miscMethods = miscMethods;
		this.clanName = clanName;
		this.tag = tag;
		this.requestToJoinRequired = requestToJoinRequired;
		this.description = description;
		this.clanIconId = clanIconId;
		this.clanRetrieveUtils = clanRetrieveUtils;
		this.resourceUtil = resourceUtil;

	}

	private User user;
	private int gemsChange;
	private String clanId;
	private Clan createdClan;
	private Timestamp createTime;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);

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

		resBuilder.setStatus(CreateClanStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		user = userRetrieveUtils.getUserById(userId);
		if (user == null || clanName == null || clanName.isEmpty() ||
				tag == null || tag.isEmpty()) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			log.error("user is null");
			return false;
		}

		if (clanName.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME) {
			resBuilder.setStatus(CreateClanStatus.FAIL_OTHER);
			log.error(String.format(
					"clan name %s is more than %s characters",
					clanName, ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME));
			return false;
		}

		if (tag.length() > ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG) {
			resBuilder.setStatus(CreateClanStatus.FAIL_INVALID_TAG_LENGTH);
			log.error(String.format(
					"clan tag %s is more than %s characters.",
					tag, ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG));
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		if (null != user.getClanId() && !user.getClanId().isEmpty()) {
			resBuilder.setStatus(CreateClanStatus.FAIL_ALREADY_IN_CLAN);
			log.error(String.format(
					"user already in clan with id %s", user.getClanId()));
			return false;
		}
		Clan clan = clanRetrieveUtils.getClanWithNameOrTag(clanName, tag);
		if (clan != null) {
			if (clan.getName().equalsIgnoreCase(clanName)) {
				resBuilder.setStatus(CreateClanStatus.FAIL_NAME_TAKEN);
				log.error("clan name already taken with name " + clanName);
				return false;
			}
			if (clan.getTag().equalsIgnoreCase(tag)) {
				resBuilder.setStatus(CreateClanStatus.FAIL_TAG_TAKEN);
				log.error("clan tag already taken with tag " + tag);
				return false;
			}
		}

		//CHECK MONEY
		if (0 == gemsSpent) {
			if (!resourceUtil.hasEnoughCash(user, cashChange)) {
				resBuilder.setStatus(CreateClanStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}

		if (!resourceUtil.hasEnoughGems(user, gemsSpent)) {
			resBuilder.setStatus(CreateClanStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		resBuilder.setStatus(CreateClanStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		//update currency
		if (gemsSpent > 0) {
			prevCurrencies.put(miscMethods.gems, user.getGems());
			gemsChange = -1 * Math.abs(gemsSpent);
		}
		if (cashChange != 0) {
			prevCurrencies.put(miscMethods.cash, user.getCash());
		}

		//just in case user doesn't input one, set default description
		if (null == description || description.isEmpty()) {
			description = String.format("Welcome to %s!", clanName);
		}

		createdClan = new Clan();
		createTime = new Timestamp(new Date().getTime());
		clanId = insertUtil.insertClan(clanName, createTime,
				description, tag, requestToJoinRequired, clanIconId);
		if (null == clanId || clanId.isEmpty()) {
			return false;
		} else {
			setClan(createdClan, clanId, clanName, createTime, description, tag,
					requestToJoinRequired, clanIconId);
			log.info("clan={}", createdClan);
		}

		if (!insertUtil.insertUserClan(user.getId(), clanId,
				UserClanStatus.LEADER.name(), createTime)) {
			log.error("problem with inserting user clan data for user {}, and clan id {}",
							user, clanId);
		}
		deleteUtil.deleteUserClansForUserExceptSpecificClan(
				user.getId(), clanId);
		
		updateUserCurrency();

		prepCurrencyHistory();

		return true;
	}

	private void setClan(Clan createdClan, String clanId, String name,
			Timestamp createTime, String description, String tag,
			boolean requestToJoinRequired, int clanIconId) {
		createdClan.setId(clanId);
		createdClan.setName(name);
		createdClan.setCreateTime(createTime);
		createdClan.setDescription(description);
		createdClan.setTag(tag);
		createdClan.setRequestToJoinRequired(requestToJoinRequired);
		createdClan.setClanIconId(clanIconId);
	}

	private void updateUserCurrency() {
		boolean updated = user.updateGemsCashClan(gemsChange, cashChange, clanId);
		if(!updated) {
			log.error("can't decrease user gems/cash for creating clan. gemChange={}, cashChange={}, user={}",
					new Object[] { gemsChange, cashChange, user } );
		}
	}

	private void prepCurrencyHistory() {
		String gems = miscMethods.gems;
		String cash = miscMethods.cash;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		StringBuilder detailSb1 = new StringBuilder();
		StringBuilder detailSb2 = new StringBuilder();
		details = new HashMap<String, String>();
		String reason = ControllerConstants.UCHRFC__CREATE_CLAN;

		if (0 != gemsChange) {
			currencyDeltas.put(gems, gemsChange);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges.put(gems, reason);
			detailSb1.append("clanId=");
			detailSb1.append(createdClan.getId());
			detailSb1.append(" clanName=");
			detailSb1.append(createdClan.getName());
			detailSb1.append(" gems spent buying=");
			detailSb1.append(gemsSpent);
			details.put(gems, detailSb1.toString());
		}

		if (0 != cashChange) {
			currencyDeltas.put(cash, cashChange);
			curCurrencies.put(cash, user.getCash());
			reasonsForChanges.put(cash, reason);
			detailSb2.append("clanId=");
			detailSb2.append(createdClan.getId());
			detailSb2.append(" clanName=");
			detailSb2.append(createdClan.getName());
			detailSb2.append(" cash spent or refunded=");
			detailSb2.append(cashChange);
			details.put(cash, detailSb2.toString());
		}
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

	public int getCashChange() {
		return cashChange;
	}

	public void setCashChange(int cashChange) {
		this.cashChange = cashChange;
	}

	public int getGemsSpent() {
		return gemsSpent;
	}

	public void setGemsSpent(int gemsSpent) {
		this.gemsSpent = gemsSpent;
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

	public MiscMethods getMiscMethods() {
		return miscMethods;
	}

	public void setMiscMethods(MiscMethods miscMethods) {
		this.miscMethods = miscMethods;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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

	public int getClanIconId() {
		return clanIconId;
	}

	public void setClanIconId(int clanIconId) {
		this.clanIconId = clanIconId;
	}

	public ClanRetrieveUtils2 getClanRetrieveUtils() {
		return clanRetrieveUtils;
	}

	public void setClanRetrieveUtils(ClanRetrieveUtils2 clanRetrieveUtils) {
		this.clanRetrieveUtils = clanRetrieveUtils;
	}

	public int getGemsChange() {
		return gemsChange;
	}

	public void setGemsChange(int gemsChange) {
		this.gemsChange = gemsChange;
	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
	}

	public Clan getCreatedClan() {
		return createdClan;
	}

	public void setCreatedClan(Clan createdClan) {
		this.createdClan = createdClan;
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



}
