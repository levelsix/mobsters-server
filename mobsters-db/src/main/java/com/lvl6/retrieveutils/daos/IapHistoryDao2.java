package com.lvl6.retrieveutils.daos;

import static org.jooq.impl.DSL.using;

import java.util.List;

import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.IapHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.IapHistoryPojo;
import com.lvl6.properties.IAPValues;

@Component
public class IapHistoryDao2 extends IapHistoryDao {

	public IapHistoryDao2() {
		super();
	}
	
	@Autowired
	public IapHistoryDao2(Configuration configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * select * from pvp_battle_history where (defender_id in (?) or attacker_id in (?)) and cancelled=false order by 
	 * battle_end_time desc limit n
	*/
	public int getUserPurchasesAtTier(String userId, int salesValue) {
		String productId = "";
		if(salesValue == 2) {
			productId = IAPValues.SALE10;
		}
		if(salesValue == 3) {
			productId = IAPValues.SALE20;
		}
		if(salesValue >= 4) {
			productId = IAPValues.SALE50;
		}
		
		return using(configuration())
				.selectFrom(Tables.IAP_HISTORY)
				.where(com.lvl6.mobsters.db.jooq.generated.tables.IapHistory.IAP_HISTORY.USER_ID.eq(userId)
						.and(com.lvl6.mobsters.db.jooq.generated.tables.IapHistory.IAP_HISTORY.PRODUCT_ID.eq(productId)))
				.fetch()
				.map(mapper()).size();
	}
	
}
