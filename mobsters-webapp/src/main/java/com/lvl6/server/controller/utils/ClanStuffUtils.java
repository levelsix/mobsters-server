package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanAvenge;
import com.lvl6.info.ClanAvengeUser;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.User;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.BattleProto.PvpUserClanAvengeProto;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.MonsterStuffProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.UserProto.FullUserProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component
public class ClanStuffUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	public ClanEventPersistentForClan createClanEventPersistentForClan(
			PersistentClanEventClanInfoProto pceip) {
		log.info("creating ClanEventPersistentForClan");

		String clanId = pceip.getClanUuid();
		int clanEventPersistentId = pceip.getClanEventId();
		int crId = pceip.getClanRaidId();
		int crsId = pceip.getClanRaidStageId();
		Date stageStartTime = null;
		long stageStartTimeMillis = pceip.getStageStartTime();
		if (stageStartTimeMillis > 0) {
			stageStartTime = new Date(stageStartTimeMillis);
		}
		int crsmId = pceip.getCrsmId();

		Date stageMonsterStartTime = null;
		long stageMonsterStartTimeMillis = pceip.getStageMonsterStartTime();
		if (stageMonsterStartTimeMillis > 0) {
			stageMonsterStartTime = new Date(stageMonsterStartTimeMillis);
		}

		return new ClanEventPersistentForClan(clanId, clanEventPersistentId,
				crId, crsId, stageStartTime, crsmId, stageMonsterStartTime);
	}

	public boolean firstUserClanStatusAboveSecond(UserClanStatus first,
			UserClanStatus second) {

		if (first.equals(second)) {
			return false;
		}
		if (UserClanStatus.LEADER.equals(second)) {
			return false;
		}
		if (UserClanStatus.MEMBER.equals(first)) {
			return false;
		}
		if (UserClanStatus.CAPTAIN.equals(first)
				&& UserClanStatus.JUNIOR_LEADER.equals(second)) {
			return false;
		}

		return true;

	}

	public List<ClanAvenge> javafyPvpHistoryProto(String defenderUuid,
			String clanUuid, List<PvpHistoryProto> phpList, Date clientTime) {
		List<ClanAvenge> caList = new ArrayList<ClanAvenge>();

		if (null == phpList || phpList.isEmpty()) {
			return caList;
		}

		for (PvpHistoryProto php : phpList) {
			String attackerUuid = php.getAttacker().getUserUuid();

			ClanAvenge ca = new ClanAvenge();
			ca.setAttackerId(attackerUuid);
			ca.setDefenderId(defenderUuid);

			Date battleEndTime = new Date(php.getBattleEndTime());
			ca.setBattleEndTime(battleEndTime);
			ca.setClanId(clanUuid);
			ca.setAvengeRequestTime(clientTime);

			caList.add(ca);
		}

		return caList;
	}

	public Map<String, MinimumUserProto> extractAttackerFullUserProto(
			List<PvpHistoryProto> phpList) {
		Map<String, MinimumUserProto> idToMup = new HashMap<String, MinimumUserProto>();

		for (PvpHistoryProto php : phpList) {
			FullUserProto attacker = php.getAttacker();
			String attackerId = attacker.getUserUuid();
			MinimumUserProto mup = createInfoProtoUtils
					.createMinimumUserProto(attacker);

			idToMup.put(attackerId, mup);
		}
		return idToMup;
	}

	public List<ClanAvengeUser> extractClanAvengeUser(
			List<PvpUserClanAvengeProto> pucapList) {
		List<ClanAvengeUser> cauList = new ArrayList<ClanAvengeUser>();

		if (null == pucapList || pucapList.isEmpty()) {
			return cauList;
		}

		for (PvpUserClanAvengeProto pucap : pucapList) {
			ClanAvengeUser cau = new ClanAvengeUser();
			cau.setClanId(pucap.getClanUuid());
			cau.setClanAvengeId(pucap.getClanAvengeUuid());
			cau.setUserId(pucap.getUserUuid());

			Date avengeTime = new Date(pucap.getAvengeTime());
			cau.setAvengeTime(avengeTime);

			cauList.add(cau);
		}

		return cauList;
	}

	public ClanMemberTeamDonation javafyClanMemberTeamDonationProto(
			ClanMemberTeamDonationProto cmtdp) {
		ClanMemberTeamDonation cmtd = new ClanMemberTeamDonation();

		if (null == cmtdp) {
			log.info("null ClanMemberTeamDonationProto.");
			return cmtd;
		}

		cmtd.setId(cmtdp.getDonationUuid());
		cmtd.setUserId(cmtdp.getSolicitor().getUserUuid());
		cmtd.setClanId(cmtdp.getClanUuid());
		cmtd.setPowerLimit(cmtdp.getPowerAvailability());
		cmtd.setFulfilled(cmtdp.getIsFulfilled());
		cmtd.setMsg(cmtdp.getMsg());

		if (cmtdp.hasTimeOfSolicitation() && cmtdp.getTimeOfSolicitation() > 0) {
			cmtd.setTimeOfSolicitation(new Date(cmtdp.getTimeOfSolicitation()));
		}
		return cmtd;
	}

	public Set<String> getAuthorizedClanMembers(User user,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			Map<String, String> userIdsToStatuses,
			String leaderStatus, String jrLeaderStatus) {
		Set<String> uniqUserIds = new HashSet<String>();
		if (null != userIdsToStatuses && !userIdsToStatuses.isEmpty()) {

			//gather up only the leader or jr leader userIds
			for (String userId : userIdsToStatuses.keySet()) {
				String status = userIdsToStatuses.get(userId);
				if (leaderStatus.equals(status)
						|| jrLeaderStatus.equals(status)) {
					uniqUserIds.add(userId);
				}
			}
		}
		return uniqUserIds;
	}

}
