package com.lvl6.mobsters.jooq.daos.service;

import static org.jooq.impl.DSL.using;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MiniEventLeaderboardRecord;
import com.lvl6.proto.LeaderBoardProto.MiniEventLeaderBoardProto;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;

@Component
public class MiniEventLeaderBoardService extends UserDao {

	private static final Logger log = LoggerFactory
			.getLogger(MiniEventLeaderBoardService.class);
	
	public MiniEventLeaderBoardService() {
		super();
	}
	
	@Autowired
	public MiniEventLeaderBoardService(DefaultConfiguration configuration) {
		super(configuration);
	}

	public MiniEventLeaderBoardService(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}
	
	
	public List<MiniEventLeaderBoardProto> getStrengths(int miniEventTimetableId, int minRank, int maxRank) {
		List<MiniEventLeaderboardRecord> userLeaderBoardPoints = using(configuration())
				.selectFrom(Tables.MINI_EVENT_LEADERBOARD)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.MiniEventLeaderboard.MINI_EVENT_LEADERBOARD.MINI_EVENT_TIMETABLE_ID
						.eq(miniEventTimetableId))
				.fetch();
		HashMap<String, MiniEventLeaderboardRecord> userLeaderBoardMap = new HashMap<String, MiniEventLeaderboardRecord>();
		HashMap<String, Long> userIdToMiniEventPoints = new HashMap<String, Long>();
		for(MiniEventLeaderboardRecord melbr : userLeaderBoardPoints) {
			userLeaderBoardMap.put(melbr.getUserId(), melbr);
			if(userIdToMiniEventPoints.containsKey(melbr.getUserId())) {
				userIdToMiniEventPoints.put(melbr.getUserId(), 
						userIdToMiniEventPoints.get(melbr.getUserId() + melbr.getPtsForMiniEvent()));
			}
			else userIdToMiniEventPoints.put(melbr.getUserId(), melbr.getPtsForMiniEvent());
		}
		
		int rank = 1;
		List<MiniEventLeaderBoardProto> returnList = new ArrayList<MiniEventLeaderBoardProto>();
		LinkedHashMap<String, Long> sortedMap = sortHashMapByValues(userIdToMiniEventPoints);
		for(String userId : sortedMap.keySet()) {
			MiniEventLeaderboardRecord melbr = userLeaderBoardMap.get(userId);
			if(rank >= minRank && rank <= maxRank) {
				MiniEventLeaderBoardProto.Builder b = MiniEventLeaderBoardProto.newBuilder();
				b.setRank(rank);
				b.setTotalPoints(sortedMap.get(userId));
				MinimumUserProto.Builder mupb = MinimumUserProto.newBuilder();
				if(melbr.getClanTag() != null) {
					MinimumClanProto.Builder mcpb = MinimumClanProto.newBuilder();
					mcpb.setTag(melbr.getClanTag());
					mupb.setClan(mcpb.build());
				}
				mupb.setName(melbr.getName());
				mupb.setUserUuid(userId);
				mupb.setAvatarMonsterId(melbr.getAvatarId());
				b.setMup(mupb.build());
				returnList.add(b.build());
			}
			rank++;
		}
		return returnList;
	}
	
	public int getUserRank(String userId, int miniEventTimetableId) {
		List<MiniEventLeaderboardRecord> userLeaderBoardPoints = using(configuration())
				.selectFrom(Tables.MINI_EVENT_LEADERBOARD)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.MiniEventLeaderboard.MINI_EVENT_LEADERBOARD.MINI_EVENT_TIMETABLE_ID
						.eq(miniEventTimetableId))
				.fetch();
		HashMap<String, MiniEventLeaderboardRecord> userLeaderBoardMap = new HashMap<String, MiniEventLeaderboardRecord>();
		HashMap<String, Long> userIdToMiniEventPoints = new HashMap<String, Long>();
		for(MiniEventLeaderboardRecord melbr : userLeaderBoardPoints) {
			userLeaderBoardMap.put(melbr.getUserId(), melbr);
			if(userIdToMiniEventPoints.containsKey(melbr.getUserId())) {
				userIdToMiniEventPoints.put(melbr.getUserId(), 
						userIdToMiniEventPoints.get(melbr.getUserId() + melbr.getPtsForMiniEvent()));
			}
			else userIdToMiniEventPoints.put(melbr.getUserId(), melbr.getPtsForMiniEvent());
		}
		
		int rank = 1;
		LinkedHashMap<String, Long> sortedMap = sortHashMapByValues(userIdToMiniEventPoints);
		for(String mapUserId : sortedMap.keySet()) {
			if(mapUserId.equals(userId)) {
				return rank;
			}
			else rank++;
		}
		log.info("user with id {} didnt particpate in minievent timetable id {}", userId, miniEventTimetableId);
		return 0;
	}

	public LinkedHashMap<String, Long> sortHashMapByValues(HashMap<String, Long> passedMap) {
		List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		List<Long> mapValues = new ArrayList<Long>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<String, Long>();

		Iterator<Long> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Long val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)){
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String)key, val);
					break;
				}
			}
		}
		return sortedMap;
	}

}
