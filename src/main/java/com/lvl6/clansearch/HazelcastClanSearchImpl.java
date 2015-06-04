package com.lvl6.clansearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.lvl6.datastructures.DistributedZSet;
import com.lvl6.datastructures.DistributedZSetHazelcast;
import com.lvl6.datastructures.ZSetMember;
import com.lvl6.info.Clan;
import com.lvl6.server.controller.utils.TimeUtils;

public class HazelcastClanSearchImpl {
	
	private static final Logger log = LoggerFactory
			.getLogger(HazelcastClanSearchImpl.class);
	
	@Autowired
	protected TimeUtils timeUtils;

	@Resource(name = "clanMemberCountMap")
	IMap<String, Integer> clanMemberCountMap;
	
	public IMap<String, Integer> getclanMemberCountMap() {
		return clanMemberCountMap;
	}

	public void setclanMemberCountMap(
			IMap<String, Integer> clanMemberCountMap) {
		this.clanMemberCountMap = clanMemberCountMap;
	}
	
	@Resource(name = "dailyHelpsMap")
	IMap<String, Map<Date, Integer>> dailyHelpsMap;
	
	public IMap<String, Map<Date, Integer>> getDailyHelpsMap() {
		return dailyHelpsMap;
	}

	public void setDailyHelpsMap(
			IMap<String, Map<Date, Integer>> dailyHelpsMap) {
		this.dailyHelpsMap = dailyHelpsMap;
	}

	@Resource(name = "dailyDonateRequestsMap")
	IMap<String, Map<Date, Integer>> dailyDonateRequestsMap;
	
	public IMap<String, Map<Date, Integer>> getdailyDonateRequestsMap() {
		return dailyDonateRequestsMap;
	}

	public void setdailyDonateRequestsMap(
			IMap<String, Map<Date, Integer>> dailyDonateRequestsMap) {
		this.dailyDonateRequestsMap = dailyDonateRequestsMap;
	}
	
	@Resource(name = "dailyDonateCompletesMap")
	IMap<String, Map<Date, Integer>> dailyDonateCompletesMap;
	
	public IMap<String, Map<Date, Integer>> getdailyDonateCompletesMap() {
		return dailyDonateCompletesMap;
	}

	public void setdailyDonateCompletesMap(
			IMap<String, Map<Date, Integer>> dailyDonateCompletesMap) {
		this.dailyDonateCompletesMap = dailyDonateCompletesMap;
	}
	
	@Resource(name = "chatsPastHourMap")
	IMap<String, Map<Date, Integer>> chatsPastHourMap;
	
	public IMap<String, Map<Date, Integer>> getchatsPastHourMap() {
		return chatsPastHourMap;
	}

	public void setchatsPastHourMap(
			IMap<String, Map<Date, Integer>> chatsPastHourMap) {
		this.chatsPastHourMap = chatsPastHourMap;
	}
	
	protected DistributedZSet clanSearchRanking;
	
	@Autowired
	protected HazelcastInstance hazelcastInstance;
	
	public HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}
	
	@Autowired
	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
		clanSearchRanking = new DistributedZSetHazelcast("clan search ranking",
				hazelcastInstance);
	}
	
	private static int minuteIntervals = 5;
	private static int minutesPastHour = 60;
	private static int minutesPastDay = 1440;
	
	public void updateRankForClanSearch(Clan clan, Date now, int numHelps, 
			int numDonateRequest, int numDonateComplete, int numChats) {
		if(numHelps != 0) {
			saveToHazelCast(dailyHelpsMap, clan.getId(), now, numHelps);
		}
		if(numDonateRequest != 0) {
			saveToHazelCast(dailyDonateRequestsMap, clan.getId(), now, numDonateRequest);
		}
		if(numDonateComplete != 0) {
			saveToHazelCast(dailyDonateCompletesMap, clan.getId(), now, numDonateComplete);
		}
		if(numChats != 0) {
			saveToHazelCast(chatsPastHourMap, clan.getId(), now, numChats);
		}
		//calculate the clan's current rank w/ arin's formula
		//(members / 10) * (chats_in_past_hour+1) * (chats_in_past_24_hrs / 12) 
		//* filled_donations_in_24hrs  * (helps_in_24_hrs/100)
		
		long members = (long) clanMemberCountMap.get(clan.getId());
		long chatsPastHour = (long) retrieveFromHazelCast(chatsPastHourMap, clan.getId(),
				now, minutesPastHour);
		long chatsPastDay = (long) retrieveFromHazelCast(chatsPastHourMap, clan.getId(),
				now, minutesPastDay);
		long filledDonations = (long) retrieveFromHazelCast(dailyDonateCompletesMap, 
				clan.getId(), now, minutesPastDay);
		long helpsPastDay = (long) retrieveFromHazelCast(dailyHelpsMap, clan.getId(), 
				now, minutesPastDay);
		
		long clanStrength = members/10 * (chatsPastHour + 1) * (chatsPastDay/12) *
				filledDonations * (helpsPastDay/100);
		clanSearchRanking.add(clan.getId(), clanStrength);
	}
	
//	public List<String> getTopClanRanksForSearch() {
//		List<String> returnList = new ArrayList<String>();
//		for(ZSetMember m : clanSearchRanking.range(0, clanSearchRanking.size())) {
//			
//		}
//	}
	
	public void saveToHazelCast(IMap<String, Map<Date, Integer>> map, 
			String clanId, Date now, int amount) {
		if(!map.containsKey(clanId)) {
			HashMap<Date, Integer> newInnerMap = new HashMap<Date, Integer>();
			newInnerMap.put(now, amount);
			map.put(clanId, newInnerMap);
		}
		else {
			boolean updated = false;
			Map<Date, Integer> innerMap = map.get(clanId);
			for(Date d : innerMap.keySet()) {
				if(timeUtils.numMinutesDifference(d, now) <= minuteIntervals) {
					innerMap.put(d, innerMap.get(d) + amount);
					updated = true;
				}
			}
			if(!updated) {
				innerMap.put(now, amount);
			}
		}
	}
	
	public int retrieveFromHazelCast(IMap<String, Map<Date, Integer>> map, String clanId,
			Date now, int pastMinutes) {
		int total = 0;
		if(!map.containsKey(clanId)) {
			return total;
		}
		Map<Date, Integer> innerMap = map.get(clanId);
		for(Date d : innerMap.keySet()) {
			if(timeUtils.numMinutesDifference(d, now) <= pastMinutes) {
				total += innerMap.get(d);
			}
		}
		return total;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
