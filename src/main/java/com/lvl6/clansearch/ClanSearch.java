package com.lvl6.clansearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.lvl6.datastructures.DistributedZSet;
import com.lvl6.datastructures.DistributedZSetHazelcast;
import com.lvl6.datastructures.ZSetMember;


@Component
public class ClanSearch {
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	
	private static Long DAY = 24L*60L*60L*1000L;
	private static Long HOUR = 60L * 60L * 1000L;
	private static Integer idealNumberOfMembers = 80;
	
	protected HazelcastInstance hz;
	protected DistributedZSet rankedClans;
	

	/**
	 * Calculates and saves a clans rank for clan search 
	 * @param clanID
	 * @param numberOfMembers
	 * @param lastChat
	 */
	public void updateClanSearchRank(String clanID, Integer numberOfMembers, Date lastChat) {
		//Integer members = numberOfMembers > idealNumberOfMembers ? idealNumberOfMembers - (numberOfMembers - idealNumberOfMembers) : numberOfMembers;
		long timeToLastChat = System.currentTimeMillis() - lastChat.getTime();
		//Long rawScore = (members * DAY) - timeToLastChat;
		//if(timeToLastChat > DAY) {
		//	rawScore = rawScore / 2;
		//}
		
		long rawScore = 0;
		
		long firstMemberPart = ((long) numberOfMembers) * DAY;
		long timePart = HOUR * timeToLastChat;
		long memberPenalty = (long) Math.pow(
			Math.max(0, 40 - numberOfMembers),
			7);
		
		if (timeToLastChat <= DAY) {
			timePart =  timePart * 10;
		} else {
			timePart = timePart* 240;
		}
		
		rawScore = firstMemberPart - (timePart + memberPenalty);
		rankedClans.add(clanID, rawScore);
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
		for(ZSetMember m : rankedClans.range(0, numberOfClansToRetrieve)) {
			clans.add(m.getKey());
		}
		return clans;
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
