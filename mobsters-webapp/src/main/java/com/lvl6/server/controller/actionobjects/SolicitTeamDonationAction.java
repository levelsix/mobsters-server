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

import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventClanProto.SolicitTeamDonationResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.ClanMemberTeamDonationRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class SolicitTeamDonationAction {
	private static Logger log = LoggerFactory.getLogger( SolicitTeamDonationAction.class);

	private String userId;
	private String clanId;
	private String msg;
	private int powerLimit;
	private Date clientTime;
	private int gemsSpent;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 
	@Autowired protected ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil; 
	@Autowired protected InsertUtil insertUtil; 
	@Autowired protected UpdateUtil updateUtil; 
	private MiscMethods miscMethods;

	public SolicitTeamDonationAction(
			String userId,
			String clanId,
			String msg,
			int powerLimit,
			Date clientDate,
			int gemsSpent,
			UserRetrieveUtils2 userRetrieveUtil,
			ClanMemberTeamDonationRetrieveUtil clanMemberTeamDonationRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil, MiscMethods miscMethods) {
		super();
		this.userId = userId;
		this.clanId = clanId;
		this.msg = msg;
		this.powerLimit = powerLimit;
		this.clientTime = clientDate;
		this.gemsSpent = gemsSpent;
		this.userRetrieveUtil = userRetrieveUtil;
		this.clanMemberTeamDonationRetrieveUtil = clanMemberTeamDonationRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.miscMethods = miscMethods;
	}

	//	//encapsulates the return value from this Action Object
	//	static class SolicitTeamDonationResource {
	//		
	//		
	//		public SolicitTeamDonationResource() {
	//			
	//		}
	//	}
	//
	//	public SolicitTeamDonationResource execute() {
	//		
	//	}

	//derived state
	private User user;
	private ClanMemberTeamDonation solicitation;
	private boolean insert;

	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

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

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		//TODO: consider moving this out to controller
		//ClanMemberTeamDonationProto cmtdp = CreateInfoProtoUtils
		//	.createClanMemberTeamDonationProto(donation, null);
		//resBuilder.setSolicitation(cmtdp);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (gemsSpent < 0) {
			log.error("gemsSpent can't be negative. {}", gemsSpent);
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error("no user for id {}", userId);
			return false;
		}

		if (gemsSpent > 0 && !hasEnoughGems(resBuilder, user, gemsSpent)) {
			return false;
		}

		//if user has a fulfilled request, he can't ask again 
		solicitation = clanMemberTeamDonationRetrieveUtil
				.getClanMemberTeamDonationForUserIdClanId(userId, clanId);

		if (null != solicitation && solicitation.isFulfilled()) {
			log.error("fulflled solicitation exists {}", solicitation);
			return false;
		}

		insert = (null == solicitation);

		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error("not enough gems. userGems={}, gemsSpent={}", userGems,
					gemsSpent);
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		if (0 != gemsSpent) {
			prevCurrencies.put(miscMethods.gems, user.getGems());
		}

		int gemsDelta = gemsSpent * -1;
		boolean success = user.updateGemsLastTeamDonateSolicitation(gemsDelta,
				new Timestamp(clientTime.getTime()));

		if (!success) {
			log.error(
					"can't update teamDonateSolicitation time {} and gems. {} ",
					clientTime, gemsDelta);
			return false;
		}

		if (insert) {
			solicitation = new ClanMemberTeamDonation();
			solicitation.setUserId(userId);
			solicitation.setClanId(clanId);
			solicitation.setFulfilled(false);
		}
		String censoredMsg = miscMethods.censorUserInput(msg);
		solicitation.setMsg(censoredMsg);
		solicitation.setPowerLimit(powerLimit);
		solicitation.setTimeOfSolicitation(clientTime);

		if (insert) {
			String id = insertUtil
					.insertIntoClanMemberTeamDonateGetId(solicitation);
			solicitation.setId(id);
			log.info("new donation: {}", solicitation);
		} else {
			int numUpdated = updateUtil
					.updateClanMemberTeamDonation(solicitation);
			log.info("numUpdated donations: {}", numUpdated);
		}

		prepCurrencyHistory();

		return true;
	}

	private void prepCurrencyHistory() {
		String gems = miscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		if (0 != gemsSpent) {
			currencyDeltas.put(gems, -1 * gemsSpent);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges
					.put(gems,
							ControllerConstants.UCHRFC__SOLICIT_CLAN_MEMBER_TEAM_DONATION);
		}

		details = new HashMap<String, String>();
		details.put(gems,
				String.format("powerLimit=%s, msg=%s", powerLimit, msg));
	}

	public User getUser() {
		return user;
	}

	public ClanMemberTeamDonation getSolicitation() {
		return solicitation;
	}

	public void setSolicitation(ClanMemberTeamDonation solicitation) {
		this.solicitation = solicitation;
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
