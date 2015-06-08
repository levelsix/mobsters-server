package com.lvl6.clansearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.lvl6.datastructures.DistributedZSet;
import com.lvl6.datastructures.DistributedZSetHazelcast;
import com.lvl6.datastructures.ZSetMember;
import com.lvl6.info.StrengthLeaderBoard;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ClanForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanChatPost;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanHelpCountForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanMemberTeamDonation;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.server.controller.utils.TimeUtils;

@Component
public class HazelcastClanSearchImpl {
	
	private static final Logger log = LoggerFactory
			.getLogger(HazelcastClanSearchImpl.class);
	
	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected ClanChatPostDao2 clanChatPostDao;
	
	@Autowired
	protected ClanHelpCountForUserDao2 clanHelpCountForUserDao;
	
	@Autowired
	protected ClanMemberTeamDonationDao2 clanMemberTeamDonationDao;
	
	@Autowired
	protected ClanForUserDao clanForUserDao;
	
	@Autowired
	protected ClanRetrieveUtils2 clanRetrieveUtils; 
	

	@Resource(name = "clanMemberCountMap")
	IMap<String, Integer> clanMemberCountMap;
	
	public IMap<String, Integer> getClanMemberCountMap() {
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
	
	public void updateRankForClanSearch(String clanId, Date now, int numHelps, 
			int numDonateRequest, int numDonateComplete, int numChats,
			int numNewMembers) {
		if(numHelps != 0) {
			saveToHazelCast(dailyHelpsMap, clanId, now, numHelps);
		}
		if(numDonateRequest != 0) {
			saveToHazelCast(dailyDonateRequestsMap, clanId, now, numDonateRequest);
		}
		if(numDonateComplete != 0) {
			saveToHazelCast(dailyDonateCompletesMap, clanId, now, numDonateComplete);
		}
		if(numChats != 0) {
			saveToHazelCast(chatsPastHourMap, clanId, now, numChats);
		}
		if(numNewMembers != 0) {
			saveNumberOfMembers(clanMemberCountMap, clanId, numNewMembers);
		}
		//calculate the clan's current rank w/ arin's formula
//		(members / 10) * (chats_in_past_hour+1) * (chats_in_past_24_hrs / 12) 
//		* filled_donations_in_24hrs  * (helps_in_24_hrs/100)
		
		long members = (long) clanMemberCountMap.get(clanId);
		
		if(members == 0) {
			clanSearchRanking.remove(clanId);
			clanMemberCountMap.remove(clanId);
			dailyHelpsMap.remove(clanId);
			dailyDonateRequestsMap.remove(clanId);
			dailyDonateCompletesMap.remove(clanId);
			chatsPastHourMap.remove(clanId);
			return;
		}
		
		long chatsPastHour = (long) retrieveFromHazelCast(chatsPastHourMap, clanId,
				now, minutesPastHour);
		long chatsPastDay = (long) retrieveFromHazelCast(chatsPastHourMap, clanId,
				now, minutesPastDay);
		long filledDonations = (long) retrieveFromHazelCast(dailyDonateCompletesMap, 
				clanId, now, minutesPastDay);
		long helpsPastDay = (long) retrieveFromHazelCast(dailyHelpsMap, clanId, 
				now, minutesPastDay);
		
		long clanStrength = members/10 * (chatsPastHour + 1) * (chatsPastDay/12) *
				filledDonations * (helpsPastDay/100);
		clanSearchRanking.add(clanId, clanStrength);
		log.info("updating clan search rank, clanId {}, clanStrength{}", clanId, clanStrength);
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
			map.put(clanId, newInnerMap, 7, TimeUnit.DAYS);
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
	
	public void saveNumberOfMembers(IMap<String, Integer> map, String clanId, int numNewMembers) {
		if(!map.containsKey(clanId)) {
			map.put(clanId, 1);
		}
		else {
			map.put(clanId, map.get(clanId) + numNewMembers);
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
	
	public List<String> getTopNClans(int numOfClans) {
		List<String> returnList = new ArrayList<String>();
		for(ZSetMember m : clanSearchRanking.range(0, numOfClans)) {
			String clanId = m.getKey();
			returnList.add(clanId);
		}
		return returnList;
	}
	
	public void reload() {
		//retrieve all the data relevant (past hour or 24 hrs)
		Date hourAgo = timeUtils.createDateAddHours(new Date(), -1);
		Date dayAgo = timeUtils.createDateAddHours(new Date(), -24);
		List<ClanChatPost> chatsPastDay = clanChatPostDao.fetchForClanSearch(dayAgo);
		List<ClanHelpCountForUser> helpsPastDay = clanHelpCountForUserDao.fetchForClanSearch(dayAgo);
		List<ClanMemberTeamDonation> donatesPastDay = clanMemberTeamDonationDao.fetchForClanSearch(dayAgo);
		List<ClanForUser> usersInClans = clanForUserDao.fetchByStatus(UserClanStatus.LEADER.toString(),
				UserClanStatus.JUNIOR_LEADER.toString(), UserClanStatus.CAPTAIN.toString(),
				UserClanStatus.MEMBER.toString());
		
		//reorganize data retrieved
		for(ClanChatPost ccp : chatsPastDay) {
			String clanId = ccp.getClanId();
			saveToHazelCast(chatsPastHourMap, clanId, ccp.getTimeOfPost(), 1);
		}
		
		for(ClanMemberTeamDonation cmtd : donatesPastDay) {
			String clanId = cmtd.getClanId();
			saveToHazelCast(dailyDonateRequestsMap, clanId, cmtd.getTimeOfSolicitation(), 1);
			if(cmtd.getFulfilled()) {
				saveToHazelCast(dailyDonateCompletesMap, clanId, cmtd.getTimeOfSolicitation(), 1);
			}
		}
		
		for(ClanHelpCountForUser chcfu : helpsPastDay) {
			String clanId = chcfu.getClanId();
			if(chcfu.getGiven() > 0)
				saveToHazelCast(dailyDonateCompletesMap, clanId, chcfu.getDate(), 1);
		}
		
		for(ClanForUser cfu : usersInClans) {
			String clanId = cfu.getClanId();
			saveNumberOfMembers(clanMemberCountMap, clanId, 1);
		}
		
		//calculate each clan's strength value and save to hz
		Date now = new Date();
		for(String clanId : clanMemberCountMap.keySet()) {
			long members2 = clanMemberCountMap.get(clanId);
			long chatsPastHour2 = (long) retrieveFromHazelCast(chatsPastHourMap, clanId,
					now, minutesPastHour);
			long chatsPastDay2 = (long) retrieveFromHazelCast(chatsPastHourMap, clanId,
					now, minutesPastDay);
			long filledDonations2 = (long) retrieveFromHazelCast(dailyDonateCompletesMap, 
					clanId, now, minutesPastDay);
			long helpsPastDay2 = (long) retrieveFromHazelCast(dailyHelpsMap, clanId, 
					now, minutesPastDay);
			
			long clanStrength2 = members2/10 * (chatsPastHour2 + 1) * (chatsPastDay2/12) *
					filledDonations2 * (helpsPastDay2/100);
			clanSearchRanking.add(clanId, clanStrength2);
			log.info("added to clan search ranking, clanId {}, clanStrength{}", clanId, clanStrength2);
		}
	}	
}
