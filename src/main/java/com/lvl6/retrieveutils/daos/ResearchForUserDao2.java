package com.lvl6.retrieveutils.daos;

import static org.jooq.impl.DSL.using;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ResearchForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ResearchForUser;

@Component
public class ResearchForUserDao2 extends ResearchForUserDao{

	public ResearchForUserDao2() {
		super();
	}

	@Autowired
	public ResearchForUserDao2(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}


	public Map<String, List<ResearchForUser>> fetchByUserIds(List<String> userIds) {
		List<ResearchForUser> list = using(configuration())
				.selectFrom(Tables.RESEARCH_FOR_USER)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.ResearchForUser.RESEARCH_FOR_USER.USER_ID.in(userIds))
				.fetch()
				.map(mapper());
		Map<String, List<ResearchForUser>> returnMap = new HashMap<String, List<ResearchForUser>>();
		for(ResearchForUser rfu : list) {
			String userId = rfu.getUserId();
			if(!returnMap.containsKey(userId)) {
				List<ResearchForUser> rfuList = new ArrayList<ResearchForUser>();
				returnMap.put(userId, rfuList);
			}
			List<ResearchForUser> innerList = returnMap.get(userId);
			innerList.add(rfu);
			returnMap.put(userId, innerList);
		}
		return returnMap;		
	}




}
