package com.lvl6.server.controller.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.info.Reward;
import com.lvl6.info.SalesDisplayItem;
import com.lvl6.info.SalesItem;
import com.lvl6.info.SalesPackage;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfigPojo;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.SalesProto.SalesDisplayItemProto;
import com.lvl6.proto.SalesProto.SalesItemProto;
import com.lvl6.proto.SalesProto.SalesPackageProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.daos.IapHistoryDao2;
import com.lvl6.retrieveutils.rarechange.CustomMenuRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesDisplayItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.TimeUtils;

@Component
public class InAppPurchaseUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());


    @Autowired
    protected CreateInfoProtoUtils createInfoProtoUtils;

    @Autowired
    protected CustomMenuRetrieveUtils customMenuRetrieveUtils;

    @Autowired
    protected RewardRetrieveUtils rewardRetrieveUtils;
    
    @Autowired
    protected TimeUtils timeUtils;
    
    @Autowired
    protected IapHistoryDao2 iapHistoryDao;


	public boolean checkIfDuplicateReceipt(JSONObject receiptFromApple,
			IAPHistoryRetrieveUtils iapHistoryRetrieveUtil) { //returns true if duplicate
		String transactionId = null;
		try {
			transactionId = receiptFromApple
					.getString(IAPValues.TRANSACTION_ID);
		} catch (JSONException e) {
			log.error(String.format("error verifying InAppPurchase request. "
					+ "receiptFromApple={}", receiptFromApple), e);
			e.printStackTrace();
		}

		long transactionIdLong = Long.parseLong(transactionId);
		if (iapHistoryRetrieveUtil
				.checkIfDuplicateTransaction(transactionIdLong)) {
			return true;
		}
		else return false;
	}
	
	public boolean checkWhetherToTierDownSalesValue(String userId, int userSalesValue,
			Date lastPurchaseTime) {
		int requirementInDays = 0;
		int numPurchases = iapHistoryDao.getUserPurchasesAtTier(userId, userSalesValue);
		double constantInFormula = Math.pow(1.3, Math.min(numPurchases-1, 4));
		
		log.info("numPurchases {}, constantInFormula {}", numPurchases, constantInFormula);
		log.info("userSalesValue {}, lastPurchaseTime {}", userSalesValue, lastPurchaseTime);
		
		if(userSalesValue == 2) {
			requirementInDays = (int)(5 * constantInFormula);
		}
		if(userSalesValue == 3) {
			requirementInDays = (int)(10 * constantInFormula);
		}
		if(userSalesValue >= 4) {
			requirementInDays = (int)(25 * constantInFormula);
		}
		
		if(requirementInDays == 0) {
			return false;
		}
		
		log.info("lastPurchaseTime {}, now {}", lastPurchaseTime, new Date());
		int numDaysDiff = Math.abs(timeUtils.numDaysDifference(new Date(), lastPurchaseTime));
		log.info("num days diff: {}", numDaysDiff);
		if(numDaysDiff >= requirementInDays) {
			log.info("decayed user's sales value");
			return true;
		}
		else return false;
	}
	

	/////////////////////////////////CREATING PROTOS/////////////////////////////////

	public SalesPackageProto createSalesPackageProto(SalesPackage sp,
			SalesItemRetrieveUtils salesItemRetrieveUtils,
			SalesDisplayItemRetrieveUtils salesDisplayItemRetrieveUtils,
			CustomMenuRetrieveUtils customMenuRetrieveUtils,
			Timestamp startTime, Timestamp endTime) {
		SalesPackageProto.Builder b = SalesPackageProto.newBuilder();
		b.setSalesPackageId((int)Math.random()*2000000000);

		String str = sp.getProductId();
		if (null != str && !str.isEmpty()) {
			b.setSalesProductId(str);
		}

		b.setPrice((long)sp.getPrice());

		str = sp.getUuid();
		if (null != str && !str.isEmpty()) {
			b.setUuid(str);
		}

		b.setSuccId(sp.getSuccId());

		b.setPriority(sp.getPriority());
		b.setAnimatingIcon(sp.getAnimatingIcon());
		b.setSlamIcon(sp.getSlamIcon());
		b.setTitleColor(sp.getTitleColor());

		if(startTime != null) {
			b.setTimeStart(startTime.getTime());
		}
		if(endTime != null) {
			b.setTimeEnd(endTime.getTime());
		}
		Map<Integer, List<SalesItem>> salesPackageIdToSalesItems = salesItemRetrieveUtils
				.getSalesItemIdsToSalesItemsForSalesPackIds();
		Map<Integer, Map<Integer, SalesDisplayItem>> salesPackageIdToDisplayIdsToDisplayItems = salesDisplayItemRetrieveUtils
				.getSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds();

		//get the sales items associated with this booster pack
		List<SalesItem> salesItemsList = salesPackageIdToSalesItems
				.get(sp.getId());

		//get the booster display items for this booster pack
		Map<Integer, SalesDisplayItem> displayIdsToDisplayItems = salesPackageIdToDisplayIdsToDisplayItems
				.get(sp.getId());
		Collection<SalesDisplayItem> displayItems = null;
		if (null != displayIdsToDisplayItems) {
			ArrayList<Integer> displayItemIds = new ArrayList<Integer>();
			displayItemIds.addAll(displayIdsToDisplayItems.keySet());
			Collections.sort(displayItemIds);

			displayItems = new ArrayList<SalesDisplayItem>();

			for (Integer displayItemId : displayItemIds) {
				displayItems.add(displayIdsToDisplayItems
						.get(displayItemId));
			}
		}

		if (salesItemsList != null) {
			for (SalesItem si : salesItemsList) {
				SalesItemProto sip = createSalesItemProtoFromSalesItem(si);
				b.addSip(sip);
			}
		}

		if (null != displayItems) {
			for (SalesDisplayItem sdi : displayItems) {
				SalesDisplayItemProto sdip = createSalesDisplayItemProtoFromSalesDisplayItem(sdi);
				b.addSdip(sdip);
			}
		}

		List<CustomMenuConfigPojo> cms = customMenuRetrieveUtils.getCustomMenuForId(sp.getCustomMenuId());

		if (cms != null && !cms.isEmpty()) {
		    for (CustomMenuConfigPojo cm : cms) {
		        b.addCmp(createInfoProtoUtils.createCustomMenuProto(cm));
		    }
		}

		return b.build();
	}

	public SalesItemProto createSalesItemProtoFromSalesItem(SalesItem si) {
		SalesItemProto.Builder sipb = SalesItemProto.newBuilder();
		sipb.setSalesItemId(si.getId());
		sipb.setSalesPackageId(si.getSalesPackageId());
		Reward r = rewardRetrieveUtils.getRewardById(si.getRewardId());
		sipb.setReward(createInfoProtoUtils.createRewardProto(r));

		return sipb.build();
	}

	public SalesDisplayItemProto createSalesDisplayItemProtoFromSalesDisplayItem(SalesDisplayItem sdi) {
		SalesDisplayItemProto.Builder sdipb = SalesDisplayItemProto.newBuilder();
		sdipb.setSalesItemId(sdi.getId());
		sdipb.setSalesPackageId(sdi.getSalesPackageId());
		Reward r = rewardRetrieveUtils.getRewardById(sdi.getRewardId());
		sdipb.setReward(createInfoProtoUtils.createRewardProto(r));

		return sdipb.build();
	}
	
	public void sortSalesPackageProtoList(List<SalesPackageProto> sppList) {
		Collections.sort(sppList, new Comparator<SalesPackageProto>() {
			@Override
			public int compare(SalesPackageProto spp1, SalesPackageProto spp2) {
				return spp2.getPriority() - spp1.getPriority();
			}
		});
	}


}
