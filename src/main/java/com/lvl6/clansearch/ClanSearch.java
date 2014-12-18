package com.lvl6.clansearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.lvl6.datastructures.DistributedZSet;
import com.lvl6.datastructures.DistributedZSetHazelcast;
import com.lvl6.datastructures.ZSetMember;


@Component
public class ClanSearch {
	
	
	private static Long DAY = 24*60*60*1000l;
	private static Integer idealNumberOfMembers = 80;
	
	@Autowired
	protected HazelcastInstance hz;
	protected DistributedZSet rankedClans;
	

	/**
	 * Calculates and saves a clans rank for clan search 
	 * @param clanID
	 * @param numberOfMembers
	 * @param lastChat
	 */
	public void updateClanSearchRank(String clanID, Integer numberOfMembers, Date lastChat) {
		Integer members = numberOfMembers > idealNumberOfMembers ? idealNumberOfMembers - (numberOfMembers - idealNumberOfMembers) : numberOfMembers;
		long timeToLastChat = System.currentTimeMillis() - lastChat.getTime();
		Long rawScore = (members * DAY) - timeToLastChat;
		if(timeToLastChat > DAY) {
			rawScore = rawScore / 2;
		}
		rankedClans.add(clanID, rawScore);
	}
	
	
	public void getTopNClans(int numberOfClansToRetrieve) {
		List<String> clans = new ArrayList<String>();
		for(ZSetMember m : rankedClans.range(0, numberOfClansToRetrieve)) {
			clans.add(m.getKey());
		}
	}
	
	
	
	
	
	public HazelcastInstance getHz() {
		return hz;
	}


	public void setHz(HazelcastInstance hz) {
		this.hz = hz;
		rankedClans = new DistributedZSetHazelcast("clanSearch", hz);
	}


}
