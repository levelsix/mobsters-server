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
import com.lvl6.mobsters.db.jooq.generated.tables.daos.StructureForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureForUserPojo;

@Component
public class StructureForUserDao2 extends StructureForUserDao {

	public StructureForUserDao2() {
		super();
	}
	
	@Autowired
	public StructureForUserDao2(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}


	public Map<String, List<StructureForUserPojo>> fetchByUserIds(List<String> userIds) {
		List<StructureForUserPojo> list = using(configuration())
				.selectFrom(Tables.STRUCTURE_FOR_USER)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.StructureForUser.STRUCTURE_FOR_USER.USER_ID.in(userIds))
				.fetch()
				.map(mapper());
		Map<String, List<StructureForUserPojo>> returnMap = new HashMap<String, List<StructureForUserPojo>>();
		for(StructureForUserPojo sfu : list) {
			String userId = sfu.getUserId();
			if(!returnMap.containsKey(userId)) {
				List<StructureForUserPojo> innerList = new ArrayList<StructureForUserPojo>();
				innerList.add(sfu);
				returnMap.put(userId, innerList);
			}
			else {
				List<StructureForUserPojo> innerList = returnMap.get(userId);
				innerList.add(sfu);
				returnMap.put(userId, innerList);
			}
		}
		return returnMap;
	}
	
	
	
}
