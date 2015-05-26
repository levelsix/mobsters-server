package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanAvenge;
import com.lvl6.info.ClanAvengeUser;
import com.lvl6.info.User;
import com.lvl6.proto.BattleProto.PvpClanAvengeProto;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.retrieveutils.ClanAvengeRetrieveUtil;
import com.lvl6.retrieveutils.ClanAvengeUserRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetClanRetaliationsAction implements StartUpAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final ClanDataProto.Builder cdpBuilder;
	private final User user;
	private final String userId;
	private final ClanAvengeRetrieveUtil clanAvengeRetrieveUtil;
	private final ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil;
	private final CreateInfoProtoUtils createInfoProtoUtils;

	public SetClanRetaliationsAction(ClanDataProto.Builder cdpBuilder,
			User user, String userId,
			ClanAvengeRetrieveUtil clanAvengeRetrieveUtil,
			ClanAvengeUserRetrieveUtil clanAvengeUserRetrieveUtil,
			CreateInfoProtoUtils createInfoProtoUtils) {
		this.cdpBuilder = cdpBuilder;
		this.user = user;
		this.userId = userId;
		this.clanAvengeRetrieveUtil = clanAvengeRetrieveUtil;
		this.clanAvengeUserRetrieveUtil = clanAvengeUserRetrieveUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	//derived state
	private String clanId;
	private List<ClanAvenge> clanAvenges;
	private Set<String> prospectiveVictimUserIdSet;
	private Set<String> retaliationInstigatorIdSet;
	private Map<String, List<ClanAvengeUser>> clanAvengeIdToClanAvenge;
	private Set<String> participantIdSet;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		clanId = user.getClanId();

		if (null == clanId || clanId.isEmpty()) {
			return;
		}

		clanAvenges = clanAvengeRetrieveUtil.getClanAvenge(clanId);

		prospectiveVictimUserIdSet = new HashSet<String>();
		retaliationInstigatorIdSet = new HashSet<String>();
		//get the open avenges
		for (ClanAvenge ca : clanAvenges) {
			String victimId = ca.getAttackerId();
			prospectiveVictimUserIdSet.add(victimId);

			String instigatorId = ca.getDefenderId();
			retaliationInstigatorIdSet.add(instigatorId);
		}

		//get the participants in the avenges
		clanAvengeIdToClanAvenge = clanAvengeUserRetrieveUtil
				.getClanAvengeUserMap(clanId);
		participantIdSet = new HashSet<String>();
		for (ClanAvenge clanAvengeId : clanAvenges) {
			//there could be no clan_avenge_user
			if (!clanAvengeIdToClanAvenge.containsKey(clanAvengeId)) {
				continue;
			}

			List<ClanAvengeUser> cauList = clanAvengeIdToClanAvenge
					.get(clanAvengeId);

			for (ClanAvengeUser cau : cauList) {
				String participantId = cau.getUserId();
				participantIdSet.add(participantId);
			}
		}

		fillMe.addUserId(prospectiveVictimUserIdSet);
		fillMe.addUserId(retaliationInstigatorIdSet);
		fillMe.addUserId(participantIdSet);
	}

	@Override
	public void execute(StartUpResource useMe) {
		if (null == clanId || clanId.isEmpty() || null == clanAvenges
				|| clanAvenges.isEmpty()) {
			return;
		}

		Map<String, User> victims = useMe
				.getUserIdsToUsers(prospectiveVictimUserIdSet);
		Map<String, User> instigators = useMe
				.getUserIdsToUsers(retaliationInstigatorIdSet);
		Map<String, User> participants = useMe
				.getUserIdsToUsers(participantIdSet);

		Map<String, User> userIdsToUsers = new HashMap<String, User>();
		userIdsToUsers.putAll(victims);
		userIdsToUsers.putAll(instigators);
		userIdsToUsers.putAll(participants);

		if (userIdsToUsers.isEmpty()) {
			log.info("no ClanAvenge");
			return;
		}

		Map<String, Clan> userIdsToClans = useMe
				.getUserIdsToClans(userIdsToUsers.keySet());

		List<PvpClanAvengeProto> pcapList = createInfoProtoUtils
				.createPvpClanAvengeProto(clanAvenges,
						clanAvengeIdToClanAvenge, userIdsToUsers,
						userIdsToClans);
		cdpBuilder.addAllClanAvengings(pcapList);
	}

}
