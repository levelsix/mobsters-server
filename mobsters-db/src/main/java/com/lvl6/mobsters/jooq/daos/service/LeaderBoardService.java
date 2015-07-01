package com.lvl6.mobsters.jooq.daos.service;

import static org.jooq.impl.DSL.using;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.Record2;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StrengthLeaderboardRecord;
import com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;

@Component
public class LeaderBoardService extends UserDao {

	private static final Logger log = LoggerFactory
			.getLogger(LeaderBoardService.class);
	
	public LeaderBoardService() {
		super();
	}
	
	@Autowired
	public LeaderBoardService(DefaultConfiguration configuration) {
		super(configuration);
	}


	public LeaderBoardService(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}
	
	
	public List<StrengthLeaderBoardProto> getStrengths(int minRank, int maxRank) {
		List<StrengthLeaderboardRecord> userStrengths = using(configuration())
				.selectFrom(Tables.STRENGTH_LEADERBOARD)
				.orderBy(com.lvl6.mobsters.db.jooq.generated.tables.StrengthLeaderboard.STRENGTH_LEADERBOARD.STRENGTH.desc())
				.fetch();
				
		int rank = 1;
		List<StrengthLeaderBoardProto> returnList = new ArrayList<StrengthLeaderBoardProto>();
		for(StrengthLeaderboardRecord slbr : userStrengths) {
			if(rank >= minRank && rank<= maxRank) {
				StrengthLeaderBoardProto.Builder b = StrengthLeaderBoardProto.newBuilder();
				b.setRank(rank);
				b.setStrength(slbr.getStrength());
				MinimumUserProto.Builder mupb = MinimumUserProto.newBuilder();
				if(slbr.getClanTag() != null) {
					MinimumClanProto.Builder mcpb = MinimumClanProto.newBuilder();
					mcpb.setTag(slbr.getClanTag());
					mupb.setClan(mcpb.build());
				}
				mupb.setName(slbr.getName());
				b.setMup(mupb.build());
				returnList.add(b.build());
			}
			rank++;
		}
		return returnList;
	}
	
	public int getUserRank(String userId) {
		List<Record2<String, Integer>> userStrengths = using(configuration())
				.select(com.lvl6.mobsters.db.jooq.generated.tables.StrengthLeaderboard.STRENGTH_LEADERBOARD.USER_ID, 
						com.lvl6.mobsters.db.jooq.generated.tables.StrengthLeaderboard.STRENGTH_LEADERBOARD.STRENGTH)
				.from(Tables.STRENGTH_LEADERBOARD)
				.orderBy(com.lvl6.mobsters.db.jooq.generated.tables.StrengthLeaderboard.STRENGTH_LEADERBOARD.STRENGTH.desc())
				.fetch();
		
		int rank = 1;
		for(Record2<String, Integer> record : userStrengths) {
			if(record.value1().equals(userId)) {
				return rank;
			}
			else rank++;
		}
		return 0;
	}

}
