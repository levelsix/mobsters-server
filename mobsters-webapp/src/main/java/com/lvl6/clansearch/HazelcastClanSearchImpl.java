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
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.lvl6.datastructures.DistributedZSet;
import com.lvl6.datastructures.DistributedZSetHazelcast;
import com.lvl6.datastructures.ZSetMember;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ClanForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanChatPostPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanHelpCountForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ClanMemberTeamDonationPojo;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.utils.TimeUtils;

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
	
	protected ILock clanSearchReloadLock;

	
	public HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}
	
	@Autowired
	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
		clanSearchRanking = new DistributedZSetHazelcast("clanSearch",
				hazelcastInstance);
		clanSearchReloadLock = hazelcastInstance.getLock("clanSearchLock");
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
		
		double members = (double) clanMemberCountMap.get(clanId);
		
		if(members == 0) {
			clanSearchRanking.remove(clanId);
			clanMemberCountMap.remove(clanId);
			dailyHelpsMap.remove(clanId);
			dailyDonateRequestsMap.remove(clanId);
			dailyDonateCompletesMap.remove(clanId);
			chatsPastHourMap.remove(clanId);
			return;
		}
		
		double chatsPastHour = (double) retrieveFromHazelCast(chatsPastHourMap, clanId,
				now, minutesPastHour);
		double chatsPastDay = (double) retrieveFromHazelCast(chatsPastHourMap, clanId,
				now, minutesPastDay);
		double filledDonations = (double) retrieveFromHazelCast(dailyDonateCompletesMap, 
				clanId, now, minutesPastDay);
		double helpsPastDay = (double) retrieveFromHazelCast(dailyHelpsMap, clanId, 
				now, minutesPastDay);
		
		double clanStrength = members/(double)10 * (chatsPastHour + 1) * (chatsPastDay/(double)12) *
				filledDonations * (helpsPastDay/(double)100);
		long clanStrengthLong = (long) clanStrength;
		clanSearchRanking.add(clanId, clanStrengthLong);
		log.info("updating clan search rank, clanId {}, clanStrength{}", clanId, clanStrength);
		log.info("CLAN MEMBER MAP: {}", clanMemberCountMap);
		log.info("DAILY HELPS MAP: {}", dailyHelpsMap);
		log.info("DAILY DONATE REQUEST MAP: {}", dailyDonateRequestsMap);
		log.info("DAILY DONATE COMPLETES MAP: {}", dailyDonateCompletesMap);
		log.info("CHATS PAST HR MAP: {}", chatsPastHourMap);
	}
	
//	public List<String> getTopClanRanksForSearch() {
//		List<String> returnList = new ArrayList<String>();
//		for(ZSetMember m : clanSearchRanking.range(0, clanSearchRanking.size())) {
//			
//		}
//	}
	
	public void saveToHazelCast(IMap<String, Map<Date, Integer>> map, 
			String clanId, Date now, int amount) {
		log.info("attemping to save to hazelcast for map {}", map);
		if(!map.containsKey(clanId)) {
			HashMap<Date, Integer> newInnerMap = new HashMap<Date, Integer>();
			newInnerMap.put(now, amount);
			map.put(clanId, newInnerMap, 7, TimeUnit.DAYS);
			log.info("successful insert into map");
		}
		else {
			boolean updated = false;
			Map<Date, Integer> innerMap = map.get(clanId);
			for(Date d : innerMap.keySet()) {
				if(timeUtils.numMinutesDifference(d, now) <= minuteIntervals) {
					innerMap.put(d, innerMap.get(d) + amount);
					updated = true;
					log.info("successful insert into map2");
				}
			}
			if(!updated) {
				innerMap.put(now, amount);
				log.info("successful insert into map3");
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
		log.info("successful insert into clan members count map");
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
		log.info("RETRIEVE amount = {}", total);
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean gotLock = false;
				try {
					if(clanSearchReloadLock.tryLock(1, TimeUnit.SECONDS)) {
						log.info("got the clan search reload lock");
						gotLock = true;
						reloadClans();
					}
				}
				catch (Throwable e) {
					log.error("Error processing str leaderboard reload", e);
				}
				finally {
					if(gotLock) {
						clanSearchReloadLock.forceUnlock();
					}
				}
			}
		}).start();
	}
	
	public void reloadClans() {
		//retrieve all the data relevant (past hour or 24 hrs)
		Date hourAgo = timeUtils.createDateAddHours(new Date(), -1);
		Date dayAgo = timeUtils.createDateAddHours(new Date(), -24);
		List<ClanChatPostPojo> chatsPastDay = clanChatPostDao.fetchForClanSearch(dayAgo);
		List<ClanHelpCountForUserPojo> helpsPastDay = clanHelpCountForUserDao.fetchForClanSearch(dayAgo);
		List<ClanMemberTeamDonationPojo> donatesPastDay = clanMemberTeamDonationDao.fetchForClanSearch(dayAgo);
		List<ClanForUserPojo> usersInClans = clanForUserDao.fetchByStatus(UserClanStatus.LEADER.toString(),
				UserClanStatus.JUNIOR_LEADER.toString(), UserClanStatus.CAPTAIN.toString(),
				UserClanStatus.MEMBER.toString());
		
		//reorganize data retrieved
		for(ClanChatPostPojo ccp : chatsPastDay) {
			String clanId = ccp.getClanId();
			saveToHazelCast(chatsPastHourMap, clanId, ccp.getTimeOfPost(), 1);
		}
		
		for(ClanMemberTeamDonationPojo cmtd : donatesPastDay) {
			String clanId = cmtd.getClanId();
			saveToHazelCast(dailyDonateRequestsMap, clanId, cmtd.getTimeOfSolicitation(), 1);
			if(cmtd.getFulfilled()) {
				saveToHazelCast(dailyDonateCompletesMap, clanId, cmtd.getTimeOfSolicitation(), 1);
			}
		}
		
		for(ClanHelpCountForUserPojo chcfu : helpsPastDay) {
			String clanId = chcfu.getClanId();
			if(chcfu.getGiven() > 0)
				saveToHazelCast(dailyHelpsMap, clanId, chcfu.getDate(), chcfu.getGiven());
		}
		
		for(ClanForUserPojo cfu : usersInClans) {
			String clanId = cfu.getClanId();
			saveNumberOfMembers(clanMemberCountMap, clanId, 1);
		}
		
		//calculate each clan's strength value and save to hz
		Date now = new Date();
		for(String clanId : clanMemberCountMap.keySet()) {
			log.info("clanId {}", clanId);
			double members2 = clanMemberCountMap.get(clanId);
			log.info("members {}", members2);

			double chatsPastHour2 = (double) retrieveFromHazelCast(chatsPastHourMap, clanId,
					now, minutesPastHour);
			log.info("chatsPastHour {}", chatsPastHour2);

			double chatsPastDay2 = (double) retrieveFromHazelCast(chatsPastHourMap, clanId,
					now, minutesPastDay);
			log.info("chats past day {}", chatsPastDay2);
			
			double filledDonations2 = (double) retrieveFromHazelCast(dailyDonateCompletesMap, 
					clanId, now, minutesPastDay);
			log.info("filled donations {}", filledDonations2);

			double helpsPastDay2 = (double) retrieveFromHazelCast(dailyHelpsMap, clanId, 
					now, minutesPastDay);
			log.info("helps past day {}", helpsPastDay2);

			
			double clanStrength2 = members2/(double)10 * (chatsPastHour2 + 1) * (chatsPastDay2/(double)12) *
					filledDonations2 * (helpsPastDay2/(double)100);
			long clanStrength2Long = (long) clanStrength2;
			clanSearchRanking.add(clanId, clanStrength2Long);
			log.info("added to clan search ranking, clanId {}, clanStrength{}", clanId, clanStrength2);
		}
	}	
}
