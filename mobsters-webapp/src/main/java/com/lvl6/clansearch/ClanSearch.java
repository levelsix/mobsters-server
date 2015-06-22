package com.lvl6.clansearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.lvl6.datastructures.DistributedZSet;
import com.lvl6.datastructures.DistributedZSetHazelcast;
import com.lvl6.datastructures.ZSetMember;
import com.lvl6.properties.ControllerConstants;

@Component
public class ClanSearch {
	private static Logger log = LoggerFactory.getLogger( ClanSearch.class);

	//	private static Long DAY = 24L*60L*60L*1000L;
	private static Double ONE_DAY_IN_HOURS = 24D;
	private static Double ONE_HOUR_IN_MILLIS = 60D * 60D * 1000D;
	//	private static Integer idealNumberOfMembers = 80;
	private static int maxCutOffClanSize = (int) (0.98F * ControllerConstants.CLAN__MAX_NUM_MEMBERS);
	public static int penalizedClanSize = 2;
	private static String penalizingStr = "clan hit limit. Changing numMembers, lastChat.";

	protected HazelcastInstance hz;
	protected DistributedZSet rankedClans;

	/**
	 * Calculates and saves a clans rank for clan search
	 * 
	 * @param clanID
	 * @param numberOfMembers
	 * @param lastChat
	 */
	public void updateClanSearchRank(String clanID, Integer numberOfMembers,
			Date lastChat) {
		//Integer members = numberOfMembers > idealNumberOfMembers ? idealNumberOfMembers - (numberOfMembers - idealNumberOfMembers) : numberOfMembers;
		double timeToLastChat = System.currentTimeMillis() - lastChat.getTime();
		//Long rawScore = (members * DAY) - timeToLastChat;
		//if(timeToLastChat > DAY) {
		//	rawScore = rawScore / 2;
		//}

		if (numberOfMembers > maxCutOffClanSize) {

			log.warn("{} lastChat={}, numMembers={}, maxCutOffClanSize={}.",
					new Object[] { penalizingStr, lastChat, numberOfMembers,
							maxCutOffClanSize });
			numberOfMembers = penalizedClanSize;
			lastChat = ControllerConstants.INCEPTION_DATE;
		}

		long rawScore = 0;

		double firstMemberPart = ((double) numberOfMembers) * ONE_DAY_IN_HOURS;
		//		long timePart = HOUR * timeToLastChat;
		double timePart = timeToLastChat / ONE_HOUR_IN_MILLIS;
		double memberPenalty = Math.pow(Math.max(0, 40 - numberOfMembers), 5);

		if (timeToLastChat <= ONE_DAY_IN_HOURS) {
			timePart = timePart * 10D;
		} else {
			timePart = timePart * 240D;
		}

		rawScore = Math.round(firstMemberPart - (timePart + memberPenalty));
		rankedClans.add(clanID, rawScore);

		log.info("clanId={} clansize={} lastChatTime={} rawScore={}",
				new Object[] { clanID, numberOfMembers, lastChat, rawScore });
	}

	/*
	 if(time_to_last_chat <= 24){
		first_member_part = 24 * 60 * 60 * 1000 * num_members
		time_part = 60 * 60 * 1000 * time_to_last_chat * 10
		member_penalty = max(40-num_members, 0)^7
		final = first_member_part - (time_part + member_penalty)
	}
	else{
		first_member_part = 24 * 60 * 60 * 1000 * num_members
		time_part = 60 * 60 * 1000 * time_to_last_chat * 240
		member_penalty = max(40 - num_members, 0)^7
		final = first_member_part - (time_part + member_penalty)
	}
	return(final)
	 
	 */

	public List<String> getTopNClans(int numberOfClansToRetrieve) {
		List<String> clans = new ArrayList<String>();
		for (ZSetMember m : rankedClans.range(0, numberOfClansToRetrieve)) {
			log.info("{}", m);
			clans.add(m.getKey());
		}
		int size = rankedClans.size();
		log.info("rankedClans.size={}", size);

		Set<String> clanIds = rankedClans.getAllIds();
		log.info("clanIds.size={}", clanIds.size());

		for (String clanId : clanIds) {
			log.info("clanId={}, index={}", clanId, rankedClans.get(clanId));
		}

		return clans;
	}

	public void removeClanId(String clanId) {
		rankedClans.remove(clanId);
	}

	public HazelcastInstance getHz() {
		return hz;
	}

	@Autowired
	public void setHz(HazelcastInstance hz) {
		log.info("!!!!!!SETTING rankedClans FOR ClanSearch!!!!!!");
		this.hz = hz;
		rankedClans = new DistributedZSetHazelcast("clanSearch", hz);
	}

}
